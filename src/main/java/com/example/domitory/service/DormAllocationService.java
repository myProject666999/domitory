package com.example.domitory.service;

import com.example.domitory.entity.DormAllocation;
import com.example.domitory.entity.Room;
import com.example.domitory.entity.User;
import com.example.domitory.repository.DormAllocationRepository;
import com.example.domitory.repository.RoomRepository;
import com.example.domitory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.*;

@Service
public class DormAllocationService {
    
    @Autowired
    private DormAllocationRepository dormAllocationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private RoomService roomService;
    
    public Page<DormAllocation> findAll(Long studentId, Long roomId, Integer status, Pageable pageable) {
        Specification<DormAllocation> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (studentId != null) {
                predicates.add(cb.equal(root.get("student").get("id"), studentId));
            }
            
            if (roomId != null) {
                predicates.add(cb.equal(root.get("room").get("id"), roomId));
            }
            
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        return dormAllocationRepository.findAll(spec, pageable);
    }
    
    public Optional<DormAllocation> findById(Long id) {
        return dormAllocationRepository.findById(id);
    }
    
    public Optional<DormAllocation> findActiveByStudentId(Long studentId) {
        User student = userRepository.findById(studentId).orElse(null);
        if (student != null) {
            return dormAllocationRepository.findByStudentAndStatus(student, 1);
        }
        return Optional.empty();
    }
    
    public List<DormAllocation> findByRoomId(Long roomId) {
        Room room = roomRepository.findById(roomId).orElse(null);
        if (room != null) {
            return dormAllocationRepository.findByRoomAndStatus(room, 1);
        }
        return new ArrayList<>();
    }
    
    @Transactional
    public DormAllocation allocate(Long studentId, Long roomId, Integer bedNumber, String description) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("学生不存在"));
        
        if (dormAllocationRepository.existsByStudentAndStatus(student, 1)) {
            throw new RuntimeException("该学生已分配宿舍，请先办理退宿");
        }
        
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("房间不存在"));
        
        if (room.getStatus() != 1) {
            throw new RuntimeException("该房间不可用");
        }
        
        List<DormAllocation> currentAllocations = dormAllocationRepository.findByRoomAndStatus(room, 1);
        Set<Integer> usedBeds = new HashSet<>();
        for (DormAllocation allocation : currentAllocations) {
            if (allocation.getBedNumber() != null) {
                usedBeds.add(allocation.getBedNumber());
            }
        }
        
        if (bedNumber == null) {
            for (int i = 1; i <= room.getBedCount(); i++) {
                if (!usedBeds.contains(i)) {
                    bedNumber = i;
                    break;
                }
            }
        } else {
            if (usedBeds.contains(bedNumber)) {
                throw new RuntimeException("该床位已被占用");
            }
            if (bedNumber < 1 || bedNumber > room.getBedCount()) {
                throw new RuntimeException("床位号超出范围");
            }
        }
        
        if (bedNumber == null) {
            throw new RuntimeException("房间已满");
        }
        
        DormAllocation allocation = new DormAllocation();
        allocation.setStudent(student);
        allocation.setRoom(room);
        allocation.setBedNumber(bedNumber);
        allocation.setCheckInDate(LocalDate.now());
        allocation.setStatus(1);
        allocation.setDescription(description);
        
        roomService.incrementOccupiedCount(roomId);
        
        return dormAllocationRepository.save(allocation);
    }
    
    @Transactional
    public DormAllocation smartAllocate(Long studentId, Long buildingId, String description) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("学生不存在"));
        
        if (dormAllocationRepository.existsByStudentAndStatus(student, 1)) {
            throw new RuntimeException("该学生已分配宿舍，请先办理退宿");
        }
        
        List<Room> availableRooms;
        if (buildingId != null) {
            availableRooms = roomService.findAvailableRoomsByBuildingId(buildingId);
        } else {
            availableRooms = roomService.findAvailableRooms();
        }
        
        if (availableRooms.isEmpty()) {
            throw new RuntimeException("没有可用的房间");
        }
        
        availableRooms.sort((r1, r2) -> {
            int diff1 = r1.getBedCount() - r1.getOccupiedCount();
            int diff2 = r2.getBedCount() - r2.getOccupiedCount();
            return Integer.compare(diff2, diff1);
        });
        
        Room selectedRoom = availableRooms.get(0);
        
        List<DormAllocation> currentAllocations = dormAllocationRepository.findByRoomAndStatus(selectedRoom, 1);
        Set<Integer> usedBeds = new HashSet<>();
        for (DormAllocation allocation : currentAllocations) {
            if (allocation.getBedNumber() != null) {
                usedBeds.add(allocation.getBedNumber());
            }
        }
        
        Integer bedNumber = null;
        for (int i = 1; i <= selectedRoom.getBedCount(); i++) {
            if (!usedBeds.contains(i)) {
                bedNumber = i;
                break;
            }
        }
        
        DormAllocation allocation = new DormAllocation();
        allocation.setStudent(student);
        allocation.setRoom(selectedRoom);
        allocation.setBedNumber(bedNumber);
        allocation.setCheckInDate(LocalDate.now());
        allocation.setStatus(1);
        allocation.setDescription(description);
        
        roomService.incrementOccupiedCount(selectedRoom.getId());
        
        return dormAllocationRepository.save(allocation);
    }
    
    @Transactional
    public void checkOut(Long allocationId, String description) {
        DormAllocation allocation = dormAllocationRepository.findById(allocationId)
                .orElseThrow(() -> new RuntimeException("分配记录不存在"));
        
        if (allocation.getStatus() != 1) {
            throw new RuntimeException("该学生已退宿");
        }
        
        allocation.setStatus(0);
        allocation.setCheckOutDate(LocalDate.now());
        if (description != null) {
            allocation.setDescription(description);
        }
        
        roomService.decrementOccupiedCount(allocation.getRoom().getId());
        
        dormAllocationRepository.save(allocation);
    }
    
    @Transactional
    public DormAllocation changeRoom(Long allocationId, Long newRoomId, Integer newBedNumber, String description) {
        DormAllocation currentAllocation = dormAllocationRepository.findById(allocationId)
                .orElseThrow(() -> new RuntimeException("分配记录不存在"));
        
        if (currentAllocation.getStatus() != 1) {
            throw new RuntimeException("该学生当前没有入住记录");
        }
        
        Room currentRoom = currentAllocation.getRoom();
        Room newRoom = roomRepository.findById(newRoomId)
                .orElseThrow(() -> new RuntimeException("新房间不存在"));
        
        if (newRoom.getStatus() != 1) {
            throw new RuntimeException("该房间不可用");
        }
        
        List<DormAllocation> newRoomAllocations = dormAllocationRepository.findByRoomAndStatus(newRoom, 1);
        Set<Integer> usedBeds = new HashSet<>();
        for (DormAllocation alloc : newRoomAllocations) {
            if (alloc.getBedNumber() != null) {
                usedBeds.add(alloc.getBedNumber());
            }
        }
        
        if (newBedNumber == null) {
            for (int i = 1; i <= newRoom.getBedCount(); i++) {
                if (!usedBeds.contains(i)) {
                    newBedNumber = i;
                    break;
                }
            }
        } else {
            if (usedBeds.contains(newBedNumber)) {
                throw new RuntimeException("该床位已被占用");
            }
        }
        
        if (newBedNumber == null) {
            throw new RuntimeException("新房间已满");
        }
        
        roomService.decrementOccupiedCount(currentRoom.getId());
        
        currentAllocation.setStatus(0);
        currentAllocation.setCheckOutDate(LocalDate.now());
        currentAllocation.setDescription("调宿至" + newRoom.getBuilding().getBuildingName() + " " + newRoom.getRoomNumber() + "室");
        dormAllocationRepository.save(currentAllocation);
        
        DormAllocation newAllocation = new DormAllocation();
        newAllocation.setStudent(currentAllocation.getStudent());
        newAllocation.setRoom(newRoom);
        newAllocation.setBedNumber(newBedNumber);
        newAllocation.setCheckInDate(LocalDate.now());
        newAllocation.setStatus(1);
        newAllocation.setDescription(description);
        
        roomService.incrementOccupiedCount(newRoomId);
        
        return dormAllocationRepository.save(newAllocation);
    }
    
    @Transactional
    public void delete(Long id) {
        dormAllocationRepository.deleteById(id);
    }
}
