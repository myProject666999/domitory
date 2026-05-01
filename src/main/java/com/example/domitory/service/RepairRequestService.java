package com.example.domitory.service;

import com.example.domitory.entity.RepairRequest;
import com.example.domitory.entity.Room;
import com.example.domitory.entity.User;
import com.example.domitory.repository.RepairRequestRepository;
import com.example.domitory.repository.RoomRepository;
import com.example.domitory.repository.UserRepository;
import com.example.domitory.util.OrderNoGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RepairRequestService {
    
    @Autowired
    private RepairRequestRepository repairRequestRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoomRepository roomRepository;
    
    public Page<RepairRequest> findAll(Long studentId, Long handlerId, String repairType, 
                                        Integer status, Pageable pageable) {
        Specification<RepairRequest> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (studentId != null) {
                predicates.add(cb.equal(root.get("student").get("id"), studentId));
            }
            
            if (handlerId != null) {
                predicates.add(cb.equal(root.get("handler").get("id"), handlerId));
            }
            
            if (StringUtils.hasText(repairType)) {
                predicates.add(cb.equal(root.get("repairType"), repairType));
            }
            
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        return repairRequestRepository.findAll(spec, pageable);
    }
    
    public Optional<RepairRequest> findById(Long id) {
        return repairRequestRepository.findById(id);
    }
    
    public Optional<RepairRequest> findByRequestNo(String requestNo) {
        return repairRequestRepository.findByRequestNo(requestNo);
    }
    
    public List<RepairRequest> findByStudentId(Long studentId) {
        User student = userRepository.findById(studentId).orElse(null);
        if (student != null) {
            return repairRequestRepository.findByStudent(student);
        }
        return new ArrayList<>();
    }
    
    public List<RepairRequest> findByHandlerId(Long handlerId) {
        User handler = userRepository.findById(handlerId).orElse(null);
        if (handler != null) {
            return repairRequestRepository.findByHandler(handler);
        }
        return new ArrayList<>();
    }
    
    @Transactional
    public RepairRequest create(RepairRequest request, Long studentId, Long roomId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("学生不存在"));
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("房间不存在"));
        
        request.setStudent(student);
        request.setRoom(room);
        request.setRequestNo(OrderNoGenerator.generateRequestNo());
        request.setStatus(0);
        
        return repairRequestRepository.save(request);
    }
    
    @Transactional
    public RepairRequest update(RepairRequest request) {
        RepairRequest existRequest = repairRequestRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("维修申请不存在"));
        
        if (request.getTitle() != null) {
            existRequest.setTitle(request.getTitle());
        }
        if (request.getRepairType() != null) {
            existRequest.setRepairType(request.getRepairType());
        }
        if (request.getContent() != null) {
            existRequest.setContent(request.getContent());
        }
        if (request.getImageUrl() != null) {
            existRequest.setImageUrl(request.getImageUrl());
        }
        
        return repairRequestRepository.save(existRequest);
    }
    
    @Transactional
    public RepairRequest handle(Long id, Long handlerId, String handleContent, Integer targetStatus) {
        RepairRequest request = repairRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("维修申请不存在"));
        
        if (handlerId != null) {
            User handler = userRepository.findById(handlerId)
                    .orElseThrow(() -> new RuntimeException("处理人不存在"));
            request.setHandler(handler);
        }
        
        if (handleContent != null) {
            request.setHandleContent(handleContent);
        }
        
        if (targetStatus != null) {
            request.setStatus(targetStatus);
            if (targetStatus == 2) {
                request.setHandleTime(LocalDateTime.now());
            }
        }
        
        return repairRequestRepository.save(request);
    }
    
    @Transactional
    public void cancel(Long id) {
        RepairRequest request = repairRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("维修申请不存在"));
        
        if (request.getStatus() != 0) {
            throw new RuntimeException("只能取消待处理的申请");
        }
        
        request.setStatus(3);
        repairRequestRepository.save(request);
    }
    
    @Transactional
    public void delete(Long id) {
        repairRequestRepository.deleteById(id);
    }
}
