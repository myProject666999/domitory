package com.example.domitory.entity;

import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "dorm_allocation")
public class DormAllocation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    @ToString.Exclude
    private User student;
    
    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    @ToString.Exclude
    private Room room;
    
    @Column(name = "bed_number")
    private Integer bedNumber;
    
    @Column(name = "check_in_date")
    private LocalDate checkInDate;
    
    @Column(name = "check_out_date")
    private LocalDate checkOutDate;
    
    private Integer status = 1;
    
    @Column(length = 500)
    private String description;
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    
    @PrePersist
    public void prePersist() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
        if (checkInDate == null) {
            checkInDate = LocalDate.now();
        }
    }
    
    @PreUpdate
    public void preUpdate() {
        updateTime = LocalDateTime.now();
    }
}
