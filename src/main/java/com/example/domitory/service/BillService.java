package com.example.domitory.service;

import com.example.domitory.entity.Bill;
import com.example.domitory.entity.Room;
import com.example.domitory.entity.User;
import com.example.domitory.repository.BillRepository;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BillService {
    
    @Autowired
    private BillRepository billRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoomRepository roomRepository;
    
    public Page<Bill> findAll(Long studentId, Long roomId, String billType, 
                               String billingMonth, Integer status, Pageable pageable) {
        Specification<Bill> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (studentId != null) {
                predicates.add(cb.equal(root.get("student").get("id"), studentId));
            }
            
            if (roomId != null) {
                predicates.add(cb.equal(root.get("room").get("id"), roomId));
            }
            
            if (StringUtils.hasText(billType)) {
                predicates.add(cb.equal(root.get("billType"), billType));
            }
            
            if (StringUtils.hasText(billingMonth)) {
                predicates.add(cb.equal(root.get("billingMonth"), billingMonth));
            }
            
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        return billRepository.findAll(spec, pageable);
    }
    
    public Optional<Bill> findById(Long id) {
        return billRepository.findById(id);
    }
    
    public Optional<Bill> findByBillNo(String billNo) {
        return billRepository.findByBillNo(billNo);
    }
    
    public List<Bill> findByStudentId(Long studentId) {
        User student = userRepository.findById(studentId).orElse(null);
        if (student != null) {
            return billRepository.findByStudent(student);
        }
        return new ArrayList<>();
    }
    
    public List<Bill> findByStudentIdAndStatus(Long studentId, Integer status) {
        User student = userRepository.findById(studentId).orElse(null);
        if (student != null) {
            return billRepository.findByStudentAndStatus(student, status);
        }
        return new ArrayList<>();
    }
    
    @Transactional
    public Bill create(Bill bill, Long studentId, Long roomId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("学生不存在"));
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("房间不存在"));
        
        bill.setStudent(student);
        bill.setRoom(room);
        bill.setBillNo(OrderNoGenerator.generateBillNo());
        bill.setStatus(0);
        
        return billRepository.save(bill);
    }
    
    @Transactional
    public Bill update(Bill bill) {
        Bill existBill = billRepository.findById(bill.getId())
                .orElseThrow(() -> new RuntimeException("账单不存在"));
        
        if (bill.getBillType() != null) {
            existBill.setBillType(bill.getBillType());
        }
        if (bill.getAmount() != null) {
            existBill.setAmount(bill.getAmount());
        }
        if (bill.getBillingMonth() != null) {
            existBill.setBillingMonth(bill.getBillingMonth());
        }
        if (bill.getDueDate() != null) {
            existBill.setDueDate(bill.getDueDate());
        }
        
        return billRepository.save(existBill);
    }
    
    @Transactional
    public Bill pay(Long billId, String paymentMethod) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("账单不存在"));
        
        if (bill.getStatus() == 1) {
            throw new RuntimeException("账单已支付");
        }
        
        bill.setStatus(1);
        bill.setPayTime(LocalDateTime.now());
        bill.setPaymentMethod(paymentMethod);
        
        return billRepository.save(bill);
    }
    
    @Transactional
    public void createBatch(List<Bill> bills, Long studentId, Long roomId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("学生不存在"));
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("房间不存在"));
        
        for (Bill bill : bills) {
            bill.setStudent(student);
            bill.setRoom(room);
            bill.setBillNo(OrderNoGenerator.generateBillNo());
            bill.setStatus(0);
        }
        
        billRepository.saveAll(bills);
    }
    
    @Transactional
    public void generateMonthlyBills(String billingMonth) {
        // 这里可以实现按月生成账单的逻辑
        // 可以遍历所有入住学生，为每个学生生成账单
    }
    
    @Transactional
    public void delete(Long id) {
        billRepository.deleteById(id);
    }
}
