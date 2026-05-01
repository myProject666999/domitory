package com.example.domitory.entity;

import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "room")
public class Room {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    
    @Column(name = "room_number", nullable = false, length = 20)
    private String roomNumber;
    
    @ManyToOne
    @JoinColumn(name = "building_id", nullable = false)
    @ToString.Exclude
    private Building building;
    
    private Integer floor;
    
    @Column(name = "bed_count")
    private Integer bedCount = 4;
    
    @Column(name = "occupied_count")
    private Integer occupiedCount = 0;
    
    @Column(name = "room_type", length = 20)
    private String roomType;
    
    private BigDecimal price;
    
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
    }
    
    @PreUpdate
    public void preUpdate() {
        updateTime = LocalDateTime.now();
    }
}
