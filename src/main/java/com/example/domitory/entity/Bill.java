package com.example.domitory.entity;

import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "bill")
public class Bill {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    
    @Column(name = "bill_no", unique = true, nullable = false, length = 50)
    private String billNo;
    
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    @ToString.Exclude
    private User student;
    
    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    @ToString.Exclude
    private Room room;
    
    @Column(name = "bill_type", nullable = false, length = 20)
    private String billType;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "billing_month", length = 20)
    private String billingMonth;
    
    @Column(name = "due_date")
    private LocalDate dueDate;
    
    private Integer status = 0;
    
    @Column(name = "pay_time")
    private LocalDateTime payTime;
    
    @Column(name = "payment_method", length = 20)
    private String paymentMethod;
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    
    @PrePersist
    public void prePersist() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }
    
    @PreUpdate
    public void preUpdate() {
        updateTime = LocalDateTime.now();
    }
}
