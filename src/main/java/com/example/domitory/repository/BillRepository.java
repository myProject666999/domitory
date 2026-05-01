package com.example.domitory.repository;

import com.example.domitory.entity.Bill;
import com.example.domitory.entity.User;
import com.example.domitory.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long>, JpaSpecificationExecutor<Bill> {
    
    Optional<Bill> findByBillNo(String billNo);
    
    List<Bill> findByStudent(User student);
    
    List<Bill> findByRoom(Room room);
    
    List<Bill> findByStatus(Integer status);
    
    List<Bill> findByBillingMonth(String billingMonth);
    
    List<Bill> findByStudentAndStatus(User student, Integer status);
}
