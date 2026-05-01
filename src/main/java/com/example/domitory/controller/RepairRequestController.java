package com.example.domitory.controller;

import com.example.domitory.common.Result;
import com.example.domitory.entity.RepairRequest;
import com.example.domitory.service.RepairRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/repair")
public class RepairRequestController {
    
    @Autowired
    private RepairRequestService repairRequestService;
    
    @GetMapping("/list")
    @ResponseBody
    public Result<List<RepairRequest>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long handlerId,
            @RequestParam(required = false) String repairType,
            @RequestParam(required = false) Integer status) {
        
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createTime").descending());
        Page<RepairRequest> requestPage = repairRequestService.findAll(studentId, handlerId, repairType, status, pageable);
        
        return Result.page(requestPage.getContent(), requestPage.getTotalElements());
    }
    
    @GetMapping("/my")
    @ResponseBody
    public Result<List<RepairRequest>> getMyRequests() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Result.error("用户未登录");
        }
        
        String username = authentication.getName();
        // 需要通过用户名获取学生ID，这里简化处理
        // 实际应通过UserService获取用户信息
        return Result.success();
    }
    
    @GetMapping("/{id}")
    @ResponseBody
    public Result<RepairRequest> getById(@PathVariable Long id) {
        return repairRequestService.findById(id)
                .map(Result::success)
                .orElse(Result.error("维修申请不存在"));
    }
    
    @PostMapping("/create")
    @ResponseBody
    public Result<RepairRequest> create(@RequestBody RepairRequest request,
                                          @RequestParam Long studentId,
                                          @RequestParam Long roomId) {
        try {
            RepairRequest savedRequest = repairRequestService.create(request, studentId, roomId);
            return Result.success(savedRequest);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/update")
    @ResponseBody
    public Result<RepairRequest> update(@RequestBody RepairRequest request) {
        try {
            RepairRequest updatedRequest = repairRequestService.update(request);
            return Result.success(updatedRequest);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/handle")
    @ResponseBody
    public Result<RepairRequest> handle(@RequestParam Long id,
                                          @RequestParam(required = false) Long handlerId,
                                          @RequestParam(required = false) String handleContent,
                                          @RequestParam Integer targetStatus) {
        try {
            RepairRequest handledRequest = repairRequestService.handle(id, handlerId, handleContent, targetStatus);
            return Result.success(handledRequest);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/cancel")
    @ResponseBody
    public Result<Void> cancel(@RequestParam Long id) {
        try {
            repairRequestService.cancel(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/delete")
    @ResponseBody
    public Result<Void> delete(@RequestParam Long id) {
        try {
            repairRequestService.delete(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
