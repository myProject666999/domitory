package com.example.domitory.controller;

import com.example.domitory.common.Result;
import com.example.domitory.entity.Bill;
import com.example.domitory.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/bill")
public class BillController {
    
    @Autowired
    private BillService billService;
    
    @GetMapping("/list")
    @ResponseBody
    public Result<List<Bill>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) String billType,
            @RequestParam(required = false) String billingMonth,
            @RequestParam(required = false) Integer status) {
        
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createTime").descending());
        Page<Bill> billPage = billService.findAll(studentId, roomId, billType, billingMonth, status, pageable);
        
        return Result.page(billPage.getContent(), billPage.getTotalElements());
    }
    
    @GetMapping("/{id}")
    @ResponseBody
    public Result<Bill> getById(@PathVariable Long id) {
        return billService.findById(id)
                .map(Result::success)
                .orElse(Result.error("账单不存在"));
    }
    
    @GetMapping("/byStudent")
    @ResponseBody
    public Result<List<Bill>> getByStudentId(
            @RequestParam Long studentId,
            @RequestParam(required = false) Integer status) {
        List<Bill> bills;
        if (status != null) {
            bills = billService.findByStudentIdAndStatus(studentId, status);
        } else {
            bills = billService.findByStudentId(studentId);
        }
        return Result.success(bills);
    }
    
    @PostMapping("/create")
    @ResponseBody
    public Result<Bill> create(@RequestBody Bill bill,
                                 @RequestParam Long studentId,
                                 @RequestParam Long roomId) {
        try {
            Bill savedBill = billService.create(bill, studentId, roomId);
            return Result.success(savedBill);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/update")
    @ResponseBody
    public Result<Bill> update(@RequestBody Bill bill) {
        try {
            Bill updatedBill = billService.update(bill);
            return Result.success(updatedBill);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/pay")
    @ResponseBody
    public Result<Bill> pay(@RequestParam Long billId,
                             @RequestParam(required = false) String paymentMethod) {
        try {
            Bill paidBill = billService.pay(billId, paymentMethod);
            return Result.success(paidBill);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/delete")
    @ResponseBody
    public Result<Void> delete(@RequestParam Long id) {
        try {
            billService.delete(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
