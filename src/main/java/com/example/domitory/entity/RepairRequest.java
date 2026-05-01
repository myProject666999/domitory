package com.example.domitory.entity;

import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "repair_request")
public class RepairRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    
    @Column(name = "request_no", unique = true, nullable = false, length = 50)
    private String requestNo;
    
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    @ToString.Exclude
    private User student;
    
    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    @ToString.Exclude
    private Room room;
    
    @Column(name = "repair_type", length = 50)
    private String repairType;
    
    @Column(length = 100)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "image_url", length = 500)
    private String imageUrl;
    
    private Integer status = 0;
    
    @ManyToOne
    @JoinColumn(name = "handler_id")
    @ToString.Exclude
    private User handler;
    
    @Column(name = "handle_content", columnDefinition = "TEXT")
    private String handleContent;
    
    @Column(name = "handle_time")
    private LocalDateTime handleTime;
    
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
