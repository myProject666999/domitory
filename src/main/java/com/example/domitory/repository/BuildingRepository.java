package com.example.domitory.repository;

import com.example.domitory.entity.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long>, JpaSpecificationExecutor<Building> {
    
    Optional<Building> findByBuildingCode(String buildingCode);
    
    boolean existsByBuildingCode(String buildingCode);
}
