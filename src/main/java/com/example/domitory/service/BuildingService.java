package com.example.domitory.service;

import com.example.domitory.entity.Building;
import com.example.domitory.entity.User;
import com.example.domitory.repository.BuildingRepository;
import com.example.domitory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BuildingService {
    
    @Autowired
    private BuildingRepository buildingRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public Page<Building> findAll(String buildingName, String buildingCode, Integer genderType, 
                                    Integer status, Pageable pageable) {
        Specification<Building> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (StringUtils.hasText(buildingName)) {
                predicates.add(cb.like(root.get("buildingName"), "%" + buildingName + "%"));
            }
            
            if (StringUtils.hasText(buildingCode)) {
                predicates.add(cb.like(root.get("buildingCode"), "%" + buildingCode + "%"));
            }
            
            if (genderType != null) {
                predicates.add(cb.equal(root.get("genderType"), genderType));
            }
            
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        return buildingRepository.findAll(spec, pageable);
    }
    
    public List<Building> findAll() {
        return buildingRepository.findAll();
    }
    
    public Optional<Building> findById(Long id) {
        return buildingRepository.findById(id);
    }
    
    public Optional<Building> findByBuildingCode(String buildingCode) {
        return buildingRepository.findByBuildingCode(buildingCode);
    }
    
    @Transactional
    public Building save(Building building, Long managerId) {
        if (building.getId() == null) {
            if (buildingRepository.existsByBuildingCode(building.getBuildingCode())) {
                throw new RuntimeException("楼宇编号已存在");
            }
        } else {
            Building existBuilding = buildingRepository.findById(building.getId())
                    .orElseThrow(() -> new RuntimeException("楼宇不存在"));
            if (!existBuilding.getBuildingCode().equals(building.getBuildingCode())) {
                if (buildingRepository.existsByBuildingCode(building.getBuildingCode())) {
                    throw new RuntimeException("楼宇编号已存在");
                }
            }
        }
        
        if (managerId != null) {
            User manager = userRepository.findById(managerId).orElse(null);
            building.setManager(manager);
        }
        
        return buildingRepository.save(building);
    }
    
    @Transactional
    public void delete(Long id) {
        buildingRepository.deleteById(id);
    }
    
    @Transactional
    public void updateStatus(Long id, Integer status) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("楼宇不存在"));
        building.setStatus(status);
        buildingRepository.save(building);
    }
}
