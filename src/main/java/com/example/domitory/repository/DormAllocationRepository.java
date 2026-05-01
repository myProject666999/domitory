package com.example.domitory.repository;

import com.example.domitory.entity.DormAllocation;
import com.example.domitory.entity.User;
import com.example.domitory.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DormAllocationRepository extends JpaRepository<DormAllocation, Long>, JpaSpecificationExecutor<DormAllocation> {
    
    Optional<DormAllocation> findByStudentAndStatus(User student, Integer status);
    
    List<DormAllocation> findByRoom(Room room);
    
    List<DormAllocation> findByRoomAndStatus(Room room, Integer status);
    
    List<DormAllocation> findByStudent(User student);
    
    List<DormAllocation> findByStatus(Integer status);
    
    boolean existsByStudentAndStatus(User student, Integer status);
}
