package com.example.domitory.controller;

import com.example.domitory.common.Result;
import com.example.domitory.entity.Building;
import com.example.domitory.service.BuildingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/building")
public class BuildingController {
    
    @Autowired
    private BuildingService buildingService;
    
    @GetMapping("/list")
    @ResponseBody
    public Result<List<Building>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) String buildingName,
            @RequestParam(required = false) String buildingCode,
            @RequestParam(required = false) Integer genderType,
            @RequestParam(required = false) Integer status) {
        
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("id").descending());
        Page<Building> buildingPage = buildingService.findAll(buildingName, buildingCode, genderType, status, pageable);
        
        return Result.page(buildingPage.getContent(), buildingPage.getTotalElements());
    }
    
    @GetMapping("/all")
    @ResponseBody
    public Result<List<Building>> getAll() {
        List<Building> buildings = buildingService.findAll();
        return Result.success(buildings);
    }
    
    @GetMapping("/{id}")
    @ResponseBody
    public Result<Building> getById(@PathVariable Long id) {
        return buildingService.findById(id)
                .map(Result::success)
                .orElse(Result.error("楼宇不存在"));
    }
    
    @PostMapping("/save")
    @ResponseBody
    public Result<Building> save(@RequestBody Building building, 
                                  @RequestParam(required = false) Long managerId) {
        try {
            Building savedBuilding = buildingService.save(building, managerId);
            return Result.success(savedBuilding);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/delete")
    @ResponseBody
    public Result<Void> delete(@RequestParam Long id) {
        try {
            buildingService.delete(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/updateStatus")
    @ResponseBody
    public Result<Void> updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        try {
            buildingService.updateStatus(id, status);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
