package com.example.domitory.repository;

import com.example.domitory.entity.AccountRecord;
import com.example.domitory.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRecordRepository extends JpaRepository<AccountRecord, Long>, JpaSpecificationExecutor<AccountRecord> {
    
    Optional<AccountRecord> findByRecordNo(String recordNo);
    
    List<AccountRecord> findByOperator(User operator);
    
    List<AccountRecord> findByRecordType(Integer recordType);
    
    List<AccountRecord> findByRecordDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<AccountRecord> findByCategory(String category);
}
