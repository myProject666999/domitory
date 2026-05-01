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
@Table(name = "account_record")
public class AccountRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    
    @Column(name = "record_no", unique = true, nullable = false, length = 50)
    private String recordNo;
    
    @Column(name = "record_type", nullable = false)
    private Integer recordType;
    
    @Column(length = 50)
    private String category;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Column(length = 500)
    private String description;
    
    @ManyToOne
    @JoinColumn(name = "operator_id")
    @ToString.Exclude
    private User operator;
    
    @Column(name = "record_date")
    private LocalDate recordDate;
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    
    @PrePersist
    public void prePersist() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
        if (recordDate == null) {
            recordDate = LocalDate.now();
        }
    }
    
    @PreUpdate
    public void preUpdate() {
        updateTime = LocalDateTime.now();
    }
}
