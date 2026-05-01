package com.example.domitory.entity;

import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "building")
public class Building {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    
    @Column(name = "building_name", nullable = false, length = 50)
    private String buildingName;
    
    @Column(name = "building_code", unique = true, nullable = false, length = 20)
    private String buildingCode;
    
    private Integer floors;
    
    @Column(name = "rooms_per_floor")
    private Integer roomsPerFloor;
    
    @Column(name = "gender_type")
    private Integer genderType;
    
    @ManyToOne
    @JoinColumn(name = "manager_id")
    @ToString.Exclude
    private User manager;
    
    @Column(length = 500)
    private String description;
    
    private Integer status = 1;
    
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
