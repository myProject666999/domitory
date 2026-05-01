package com.example.domitory.controller;

import com.example.domitory.common.Result;
import com.example.domitory.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Controller
@RequestMapping("/api/excel")
public class ExcelController {
    
    @Autowired
    private ExcelService excelService;
    
    @PostMapping("/import/users")
    @ResponseBody
    public Result<Map<String, Object>> importUsers(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择要上传的文件");
        }
        
        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
            return Result.error("请上传Excel文件（.xlsx或.xls格式）");
        }
        
        try {
            Map<String, Object> result = excelService.importUsers(file);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("导入失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/import/buildings")
    @ResponseBody
    public Result<Map<String, Object>> importBuildings(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择要上传的文件");
        }
        
        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
            return Result.error("请上传Excel文件（.xlsx或.xls格式）");
        }
        
        try {
            Map<String, Object> result = excelService.importBuildings(file);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("导入失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/import/rooms")
    @ResponseBody
    public Result<Map<String, Object>> importRooms(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择要上传的文件");
        }
        
        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
            return Result.error("请上传Excel文件（.xlsx或.xls格式）");
        }
        
        try {
            Map<String, Object> result = excelService.importRooms(file);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("导入失败: " + e.getMessage());
        }
    }
}
