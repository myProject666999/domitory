package com.example.domitory.repository;

import com.example.domitory.entity.Room;
import com.example.domitory.entity.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long>, JpaSpecificationExecutor<Room> {
    
    List<Room> findByBuilding(Building building);
    
    Optional<Room> findByBuildingAndRoomNumber(Building building, String roomNumber);
    
    List<Room> findByStatus(Integer status);
    
    List<Room> findByBuildingAndStatus(Building building, Integer status);
}
