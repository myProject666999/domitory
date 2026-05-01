package com.example.domitory.controller;

import com.example.domitory.common.Result;
import com.example.domitory.entity.Room;
import com.example.domitory.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/room")
public class RoomController {
    
    @Autowired
    private RoomService roomService;
    
    @GetMapping("/list")
    @ResponseBody
    public Result<List<Room>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) String roomNumber,
            @RequestParam(required = false) Integer floor,
            @RequestParam(required = false) Integer status) {
        
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("building").ascending().and(Sort.by("floor")).ascending().and(Sort.by("roomNumber")).ascending());
        Page<Room> roomPage = roomService.findAll(buildingId, roomNumber, floor, status, pageable);
        
        return Result.page(roomPage.getContent(), roomPage.getTotalElements());
    }
    
    @GetMapping("/byBuilding")
    @ResponseBody
    public Result<List<Room>> getByBuilding(@RequestParam(required = false) Long buildingId) {
        List<Room> rooms = roomService.findByBuildingId(buildingId);
        return Result.success(rooms);
    }
    
    @GetMapping("/available")
    @ResponseBody
    public Result<List<Room>> getAvailableRooms() {
        List<Room> rooms = roomService.findAvailableRooms();
        return Result.success(rooms);
    }
    
    @GetMapping("/availableByBuilding")
    @ResponseBody
    public Result<List<Room>> getAvailableRoomsByBuilding(@RequestParam Long buildingId) {
        List<Room> rooms = roomService.findAvailableRoomsByBuildingId(buildingId);
        return Result.success(rooms);
    }
    
    @GetMapping("/{id}")
    @ResponseBody
    public Result<Room> getById(@PathVariable Long id) {
        return roomService.findById(id)
                .map(Result::success)
                .orElse(Result.error("房间不存在"));
    }
    
    @PostMapping("/save")
    @ResponseBody
    public Result<Room> save(@RequestBody Room room, 
                              @RequestParam Long buildingId) {
        try {
            Room savedRoom = roomService.save(room, buildingId);
            return Result.success(savedRoom);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/delete")
    @ResponseBody
    public Result<Void> delete(@RequestParam Long id) {
        try {
            roomService.delete(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/updateStatus")
    @ResponseBody
    public Result<Void> updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        try {
            roomService.updateStatus(id, status);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
