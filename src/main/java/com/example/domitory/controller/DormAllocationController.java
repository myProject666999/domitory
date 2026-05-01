package com.example.domitory.controller;

import com.example.domitory.common.Result;
import com.example.domitory.entity.DormAllocation;
import com.example.domitory.service.DormAllocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/allocation")
public class DormAllocationController {
    
    @Autowired
    private DormAllocationService dormAllocationService;
    
    @GetMapping("/list")
    @ResponseBody
    public Result<List<DormAllocation>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) Integer status) {
        
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("checkInDate").descending());
        Page<DormAllocation> allocationPage = dormAllocationService.findAll(studentId, roomId, status, pageable);
        
        return Result.page(allocationPage.getContent(), allocationPage.getTotalElements());
    }
    
    @GetMapping("/{id}")
    @ResponseBody
    public Result<DormAllocation> getById(@PathVariable Long id) {
        return dormAllocationService.findById(id)
                .map(Result::success)
                .orElse(Result.error("分配记录不存在"));
    }
    
    @GetMapping("/active")
    @ResponseBody
    public Result<DormAllocation> getActiveByStudentId(@RequestParam Long studentId) {
        return dormAllocationService.findActiveByStudentId(studentId)
                .map(Result::success)
                .orElse(Result.success(null));
    }
    
    @GetMapping("/byRoom")
    @ResponseBody
    public Result<List<DormAllocation>> getByRoomId(@RequestParam Long roomId) {
        List<DormAllocation> allocations = dormAllocationService.findByRoomId(roomId);
        return Result.success(allocations);
    }
    
    @PostMapping("/allocate")
    @ResponseBody
    public Result<DormAllocation> allocate(
            @RequestParam Long studentId,
            @RequestParam Long roomId,
            @RequestParam(required = false) Integer bedNumber,
            @RequestParam(required = false) String description) {
        try {
            DormAllocation allocation = dormAllocationService.allocate(studentId, roomId, bedNumber, description);
            return Result.success(allocation);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/smartAllocate")
    @ResponseBody
    public Result<DormAllocation> smartAllocate(
            @RequestParam Long studentId,
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) String description) {
        try {
            DormAllocation allocation = dormAllocationService.smartAllocate(studentId, buildingId, description);
            return Result.success(allocation);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/checkOut")
    @ResponseBody
    public Result<Void> checkOut(
            @RequestParam Long allocationId,
            @RequestParam(required = false) String description) {
        try {
            dormAllocationService.checkOut(allocationId, description);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/changeRoom")
    @ResponseBody
    public Result<DormAllocation> changeRoom(
            @RequestParam Long allocationId,
            @RequestParam Long newRoomId,
            @RequestParam(required = false) Integer newBedNumber,
            @RequestParam(required = false) String description) {
        try {
            DormAllocation allocation = dormAllocationService.changeRoom(allocationId, newRoomId, newBedNumber, description);
            return Result.success(allocation);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/delete")
    @ResponseBody
    public Result<Void> delete(@RequestParam Long id) {
        try {
            dormAllocationService.delete(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
