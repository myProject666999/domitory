package com.example.domitory.controller;

import com.example.domitory.common.Result;
import com.example.domitory.entity.AccountRecord;
import com.example.domitory.service.AccountRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/account")
public class AccountRecordController {
    
    @Autowired
    private AccountRecordService accountRecordService;
    
    @GetMapping("/list")
    @ResponseBody
    public Result<List<AccountRecord>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) Integer recordType,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("recordDate").descending().and(Sort.by("createTime").descending()));
        Page<AccountRecord> recordPage = accountRecordService.findAll(recordType, category, startDate, endDate, pageable);
        
        return Result.page(recordPage.getContent(), recordPage.getTotalElements());
    }
    
    @GetMapping("/{id}")
    @ResponseBody
    public Result<AccountRecord> getById(@PathVariable Long id) {
        return accountRecordService.findById(id)
                .map(Result::success)
                .orElse(Result.error("记录不存在"));
    }
    
    @GetMapping("/statistics")
    @ResponseBody
    public Result<Map<String, Object>> getStatistics(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        if (startDate == null) {
            startDate = LocalDate.now().withDayOfMonth(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        Map<String, Object> statistics = accountRecordService.getStatistics(startDate, endDate);
        return Result.success(statistics);
    }
    
    @PostMapping("/create")
    @ResponseBody
    public Result<AccountRecord> create(@RequestBody AccountRecord record,
                                          @RequestParam(required = false) Long operatorId) {
        try {
            AccountRecord savedRecord = accountRecordService.create(record, operatorId);
            return Result.success(savedRecord);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/update")
    @ResponseBody
    public Result<AccountRecord> update(@RequestBody AccountRecord record) {
        try {
            AccountRecord updatedRecord = accountRecordService.update(record);
            return Result.success(updatedRecord);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/delete")
    @ResponseBody
    public Result<Void> delete(@RequestParam Long id) {
        try {
            accountRecordService.delete(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
