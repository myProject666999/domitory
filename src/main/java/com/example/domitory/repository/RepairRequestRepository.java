package com.example.domitory.repository;

import com.example.domitory.entity.RepairRequest;
import com.example.domitory.entity.User;
import com.example.domitory.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepairRequestRepository extends JpaRepository<RepairRequest, Long>, JpaSpecificationExecutor<RepairRequest> {
    
    Optional<RepairRequest> findByRequestNo(String requestNo);
    
    List<RepairRequest> findByStudent(User student);
    
    List<RepairRequest> findByRoom(Room room);
    
    List<RepairRequest> findByStatus(Integer status);
    
    List<RepairRequest> findByHandler(User handler);
}
