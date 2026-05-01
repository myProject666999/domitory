package com.example.domitory.service;

import com.example.domitory.entity.AccountRecord;
import com.example.domitory.entity.User;
import com.example.domitory.repository.AccountRecordRepository;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AccountRecordService {
    
    @Autowired
    private AccountRecordRepository accountRecordRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public Page<AccountRecord> findAll(Integer recordType, String category, 
                                        LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Specification<AccountRecord> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (recordType != null) {
                predicates.add(cb.equal(root.get("recordType"), recordType));
            }
            
            if (StringUtils.hasText(category)) {
                predicates.add(cb.equal(root.get("category"), category));
            }
            
            if (startDate != null && endDate != null) {
                predicates.add(cb.between(root.get("recordDate"), startDate, endDate));
            } else if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("recordDate"), startDate));
            } else if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("recordDate"), endDate));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        return accountRecordRepository.findAll(spec, pageable);
    }
    
    public Optional<AccountRecord> findById(Long id) {
        return accountRecordRepository.findById(id);
    }
    
    public Optional<AccountRecord> findByRecordNo(String recordNo) {
        return accountRecordRepository.findByRecordNo(recordNo);
    }
    
    public List<AccountRecord> findByOperatorId(Long operatorId) {
        User operator = userRepository.findById(operatorId).orElse(null);
        if (operator != null) {
            return accountRecordRepository.findByOperator(operator);
        }
        return new ArrayList<>();
    }
    
    @Transactional
    public AccountRecord create(AccountRecord record, Long operatorId) {
        if (operatorId != null) {
            User operator = userRepository.findById(operatorId).orElse(null);
            record.setOperator(operator);
        }
        
        record.setRecordNo(OrderNoGenerator.generateRecordNo());
        
        return accountRecordRepository.save(record);
    }
    
    @Transactional
    public AccountRecord update(AccountRecord record) {
        AccountRecord existRecord = accountRecordRepository.findById(record.getId())
                .orElseThrow(() -> new RuntimeException("记录不存在"));
        
        if (record.getRecordType() != null) {
            existRecord.setRecordType(record.getRecordType());
        }
        if (record.getCategory() != null) {
            existRecord.setCategory(record.getCategory());
        }
        if (record.getAmount() != null) {
            existRecord.setAmount(record.getAmount());
        }
        if (record.getDescription() != null) {
            existRecord.setDescription(record.getDescription());
        }
        if (record.getRecordDate() != null) {
            existRecord.setRecordDate(record.getRecordDate());
        }
        
        return accountRecordRepository.save(existRecord);
    }
    
    @Transactional
    public void delete(Long id) {
        accountRecordRepository.deleteById(id);
    }
    
    public Map<String, Object> getStatistics(LocalDate startDate, LocalDate endDate) {
        List<AccountRecord> records = accountRecordRepository.findByRecordDateBetween(startDate, endDate);
        
        Map<String, Object> result = new HashMap<>();
        
        double totalIncome = 0.0;
        double totalExpense = 0.0;
        
        Map<String, Double> incomeByCategory = new HashMap<>();
        Map<String, Double> expenseByCategory = new HashMap<>();
        
        for (AccountRecord record : records) {
            if (record.getRecordType() == 1) {
                totalIncome += record.getAmount().doubleValue();
                incomeByCategory.merge(
                    record.getCategory() != null ? record.getCategory() : "其他",
                    record.getAmount().doubleValue(),
                    Double::sum
                );
            } else {
                totalExpense += record.getAmount().doubleValue();
                expenseByCategory.merge(
                    record.getCategory() != null ? record.getCategory() : "其他",
                    record.getAmount().doubleValue(),
                    Double::sum
                );
            }
        }
        
        result.put("totalIncome", totalIncome);
        result.put("totalExpense", totalExpense);
        result.put("balance", totalIncome - totalExpense);
        result.put("incomeByCategory", incomeByCategory);
        result.put("expenseByCategory", expenseByCategory);
        result.put("recordCount", records.size());
        
        return result;
    }
}
