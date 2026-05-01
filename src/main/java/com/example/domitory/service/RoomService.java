package com.example.domitory.service;

import com.example.domitory.entity.Building;
import com.example.domitory.entity.Room;
import com.example.domitory.repository.BuildingRepository;
import com.example.domitory.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RoomService {
    
    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private BuildingRepository buildingRepository;
    
    public Page<Room> findAll(Long buildingId, String roomNumber, Integer floor, 
                               Integer status, Pageable pageable) {
        Specification<Room> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (buildingId != null) {
                predicates.add(cb.equal(root.get("building").get("id"), buildingId));
            }
            
            if (StringUtils.hasText(roomNumber)) {
                predicates.add(cb.like(root.get("roomNumber"), "%" + roomNumber + "%"));
            }
            
            if (floor != null) {
                predicates.add(cb.equal(root.get("floor"), floor));
            }
            
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        return roomRepository.findAll(spec, pageable);
    }
    
    public List<Room> findByBuildingId(Long buildingId) {
        if (buildingId == null) {
            return roomRepository.findAll();
        }
        Building building = buildingRepository.findById(buildingId).orElse(null);
        if (building != null) {
            return roomRepository.findByBuilding(building);
        }
        return new ArrayList<>();
    }
    
    public List<Room> findAvailableRooms() {
        return roomRepository.findByStatus(1);
    }
    
    public List<Room> findAvailableRoomsByBuildingId(Long buildingId) {
        Building building = buildingRepository.findById(buildingId).orElse(null);
        if (building != null) {
            return roomRepository.findByBuildingAndStatus(building, 1);
        }
        return new ArrayList<>();
    }
    
    public Optional<Room> findById(Long id) {
        return roomRepository.findById(id);
    }
    
    @Transactional
    public Room save(Room room, Long buildingId) {
        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new RuntimeException("楼宇不存在"));
        
        room.setBuilding(building);
        
        if (room.getId() == null) {
            Optional<Room> existRoom = roomRepository.findByBuildingAndRoomNumber(building, room.getRoomNumber());
            if (existRoom.isPresent()) {
                throw new RuntimeException("该楼宇下已存在此房间号");
            }
            if (room.getOccupiedCount() == null) {
                room.setOccupiedCount(0);
            }
        } else {
            Room existRoom = roomRepository.findById(room.getId())
                    .orElseThrow(() -> new RuntimeException("房间不存在"));
            if (!existRoom.getBuilding().getId().equals(buildingId) 
                    || !existRoom.getRoomNumber().equals(room.getRoomNumber())) {
                Optional<Room> duplicateRoom = roomRepository.findByBuildingAndRoomNumber(building, room.getRoomNumber());
                if (duplicateRoom.isPresent() && !duplicateRoom.get().getId().equals(room.getId())) {
                    throw new RuntimeException("该楼宇下已存在此房间号");
                }
            }
        }
        
        return roomRepository.save(room);
    }
    
    @Transactional
    public void delete(Long id) {
        roomRepository.deleteById(id);
    }
    
    @Transactional
    public void updateStatus(Long id, Integer status) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("房间不存在"));
        room.setStatus(status);
        roomRepository.save(room);
    }
    
    @Transactional
    public void incrementOccupiedCount(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("房间不存在"));
        if (room.getOccupiedCount() >= room.getBedCount()) {
            throw new RuntimeException("房间床位已满");
        }
        room.setOccupiedCount(room.getOccupiedCount() + 1);
        if (room.getOccupiedCount() >= room.getBedCount()) {
            room.setStatus(0);
        }
        roomRepository.save(room);
    }
    
    @Transactional
    public void decrementOccupiedCount(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("房间不存在"));
        if (room.getOccupiedCount() > 0) {
            room.setOccupiedCount(room.getOccupiedCount() - 1);
            if (room.getStatus() == 0 && room.getOccupiedCount() < room.getBedCount()) {
                room.setStatus(1);
            }
            roomRepository.save(room);
        }
    }
}
