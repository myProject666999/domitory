package com.example.domitory.entity;

import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "sys_role")
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    
    @Column(name = "role_name", nullable = false, length = 50)
    private String roleName;
    
    @Column(name = "role_code", unique = true, nullable = false, length = 50)
    private String roleCode;
    
    @Column(length = 200)
    private String description;
    
    private Integer status = 1;
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    
    @ManyToMany(mappedBy = "roles")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<User> users = new HashSet<>();
    
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
