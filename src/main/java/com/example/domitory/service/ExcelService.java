package com.example.domitory.service;

import com.example.domitory.entity.Building;
import com.example.domitory.entity.Role;
import com.example.domitory.entity.Room;
import com.example.domitory.entity.User;
import com.example.domitory.repository.BuildingRepository;
import com.example.domitory.repository.RoleRepository;
import com.example.domitory.repository.RoomRepository;
import com.example.domitory.repository.UserRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

@Service
public class ExcelService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BuildingRepository buildingRepository;
    
    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Transactional
    public Map<String, Object> importUsers(MultipartFile file) throws IOException {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, String>> errors = new ArrayList<>();
        int successCount = 0;
        int totalCount = 0;
        
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new RuntimeException("Excel文件中没有工作表");
            }
            
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new RuntimeException("Excel文件没有表头行");
            }
            
            Map<String, Integer> headerMap = new HashMap<>();
            for (Cell cell : headerRow) {
                String header = getCellValueAsString(cell);
                if (header != null && !header.trim().isEmpty()) {
                    headerMap.put(header.trim(), cell.getColumnIndex());
                }
            }
            
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                totalCount++;
                
                try {
                    String username = getCellValue(row, headerMap, "用户名");
                    String password = getCellValue(row, headerMap, "密码");
                    String realName = getCellValue(row, headerMap, "姓名");
                    String phone = getCellValue(row, headerMap, "手机号");
                    String email = getCellValue(row, headerMap, "邮箱");
                    String genderStr = getCellValue(row, headerMap, "性别");
                    String roleStr = getCellValue(row, headerMap, "角色");
                    
                    if (username == null || username.trim().isEmpty()) {
                        addError(errors, i + 1, "用户名不能为空");
                        continue;
                    }
                    
                    if (userRepository.existsByUsername(username.trim())) {
                        addError(errors, i + 1, "用户名 " + username + " 已存在");
                        continue;
                    }
                    
                    User user = new User();
                    user.setUsername(username.trim());
                    user.setPassword(passwordEncoder.encode(password != null && !password.isEmpty() ? password : "123456"));
                    user.setRealName(realName);
                    user.setPhone(phone);
                    user.setEmail(email);
                    user.setStatus(1);
                    
                    if ("男".equals(genderStr)) {
                        user.setGender(1);
                    } else if ("女".equals(genderStr)) {
                        user.setGender(0);
                    }
                    
                    Set<Role> roles = new HashSet<>();
                    if (roleStr != null && !roleStr.isEmpty()) {
                        String[] roleCodes = roleStr.split("[,，]");
                        for (String roleCode : roleCodes) {
                            roleCode = roleCode.trim();
                            Optional<Role> roleOpt = roleRepository.findByRoleCode(roleCode);
                            roleOpt.ifPresent(roles::add);
                        }
                    }
                    if (roles.isEmpty()) {
                        roleRepository.findByRoleCode("STUDENT").ifPresent(roles::add);
                    }
                    user.setRoles(roles);
                    
                    userRepository.save(user);
                    successCount++;
                    
                } catch (Exception e) {
                    addError(errors, i + 1, "导入失败: " + e.getMessage());
                }
            }
        }
        
        result.put("totalCount", totalCount);
        result.put("successCount", successCount);
        result.put("errorCount", errors.size());
        result.put("errors", errors);
        
        return result;
    }
    
    @Transactional
    public Map<String, Object> importBuildings(MultipartFile file) throws IOException {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, String>> errors = new ArrayList<>();
        int successCount = 0;
        int totalCount = 0;
        
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new RuntimeException("Excel文件中没有工作表");
            }
            
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new RuntimeException("Excel文件没有表头行");
            }
            
            Map<String, Integer> headerMap = new HashMap<>();
            for (Cell cell : headerRow) {
                String header = getCellValueAsString(cell);
                if (header != null && !header.trim().isEmpty()) {
                    headerMap.put(header.trim(), cell.getColumnIndex());
                }
            }
            
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                totalCount++;
                
                try {
                    String buildingName = getCellValue(row, headerMap, "楼宇名称");
                    String buildingCode = getCellValue(row, headerMap, "楼宇编号");
                    String floorsStr = getCellValue(row, headerMap, "楼层数");
                    String roomsPerFloorStr = getCellValue(row, headerMap, "每层房间数");
                    String genderTypeStr = getCellValue(row, headerMap, "性别类型");
                    String description = getCellValue(row, headerMap, "描述");
                    
                    if (buildingName == null || buildingName.trim().isEmpty()) {
                        addError(errors, i + 1, "楼宇名称不能为空");
                        continue;
                    }
                    
                    if (buildingCode == null || buildingCode.trim().isEmpty()) {
                        addError(errors, i + 1, "楼宇编号不能为空");
                        continue;
                    }
                    
                    if (buildingRepository.existsByBuildingCode(buildingCode.trim())) {
                        addError(errors, i + 1, "楼宇编号 " + buildingCode + " 已存在");
                        continue;
                    }
                    
                    Building building = new Building();
                    building.setBuildingName(buildingName.trim());
                    building.setBuildingCode(buildingCode.trim());
                    building.setStatus(1);
                    
                    if (floorsStr != null && !floorsStr.isEmpty()) {
                        building.setFloors(Integer.parseInt(floorsStr));
                    }
                    if (roomsPerFloorStr != null && !roomsPerFloorStr.isEmpty()) {
                        building.setRoomsPerFloor(Integer.parseInt(roomsPerFloorStr));
                    }
                    
                    if ("男寝".equals(genderTypeStr)) {
                        building.setGenderType(1);
                    } else if ("女寝".equals(genderTypeStr)) {
                        building.setGenderType(0);
                    } else if ("混合".equals(genderTypeStr)) {
                        building.setGenderType(2);
                    }
                    
                    building.setDescription(description);
                    
                    buildingRepository.save(building);
                    successCount++;
                    
                } catch (Exception e) {
                    addError(errors, i + 1, "导入失败: " + e.getMessage());
                }
            }
        }
        
        result.put("totalCount", totalCount);
        result.put("successCount", successCount);
        result.put("errorCount", errors.size());
        result.put("errors", errors);
        
        return result;
    }
    
    @Transactional
    public Map<String, Object> importRooms(MultipartFile file) throws IOException {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, String>> errors = new ArrayList<>();
        int successCount = 0;
        int totalCount = 0;
        
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new RuntimeException("Excel文件中没有工作表");
            }
            
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new RuntimeException("Excel文件没有表头行");
            }
            
            Map<String, Integer> headerMap = new HashMap<>();
            for (Cell cell : headerRow) {
                String header = getCellValueAsString(cell);
                if (header != null && !header.trim().isEmpty()) {
                    headerMap.put(header.trim(), cell.getColumnIndex());
                }
            }
            
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                totalCount++;
                
                try {
                    String buildingCode = getCellValue(row, headerMap, "楼宇编号");
                    String roomNumber = getCellValue(row, headerMap, "房间号");
                    String floorStr = getCellValue(row, headerMap, "楼层");
                    String bedCountStr = getCellValue(row, headerMap, "床位数量");
                    String roomType = getCellValue(row, headerMap, "房间类型");
                    String priceStr = getCellValue(row, headerMap, "价格");
                    String description = getCellValue(row, headerMap, "描述");
                    
                    if (buildingCode == null || buildingCode.trim().isEmpty()) {
                        addError(errors, i + 1, "楼宇编号不能为空");
                        continue;
                    }
                    
                    if (roomNumber == null || roomNumber.trim().isEmpty()) {
                        addError(errors, i + 1, "房间号不能为空");
                        continue;
                    }
                    
                    Optional<Building> buildingOpt = buildingRepository.findByBuildingCode(buildingCode.trim());
                    if (!buildingOpt.isPresent()) {
                        addError(errors, i + 1, "楼宇编号 " + buildingCode + " 不存在");
                        continue;
                    }
                    
                    Building building = buildingOpt.get();
                    
                    Optional<Room> existRoom = roomRepository.findByBuildingAndRoomNumber(building, roomNumber.trim());
                    if (existRoom.isPresent()) {
                        addError(errors, i + 1, "楼宇 " + building.getBuildingName() + " 中已存在房间号 " + roomNumber);
                        continue;
                    }
                    
                    Room room = new Room();
                    room.setRoomNumber(roomNumber.trim());
                    room.setBuilding(building);
                    room.setStatus(1);
                    room.setOccupiedCount(0);
                    
                    if (floorStr != null && !floorStr.isEmpty()) {
                        room.setFloor(Integer.parseInt(floorStr));
                    }
                    if (bedCountStr != null && !bedCountStr.isEmpty()) {
                        room.setBedCount(Integer.parseInt(bedCountStr));
                    }
                    if (roomType != null) {
                        room.setRoomType(roomType.trim());
                    }
                    if (priceStr != null && !priceStr.isEmpty()) {
                        room.setPrice(new BigDecimal(priceStr));
                    }
                    room.setDescription(description);
                    
                    roomRepository.save(room);
                    successCount++;
                    
                } catch (Exception e) {
                    addError(errors, i + 1, "导入失败: " + e.getMessage());
                }
            }
        }
        
        result.put("totalCount", totalCount);
        result.put("successCount", successCount);
        result.put("errorCount", errors.size());
        result.put("errors", errors);
        
        return result;
    }
    
    private String getCellValue(Row row, Map<String, Integer> headerMap, String headerName) {
        Integer index = headerMap.get(headerName);
        if (index == null) {
            return null;
        }
        Cell cell = row.getCell(index);
        return getCellValueAsString(cell);
    }
    
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }
    
    private void addError(List<Map<String, String>> errors, int rowNum, String message) {
        Map<String, String> error = new HashMap<>();
        error.put("row", String.valueOf(rowNum));
        error.put("message", message);
        errors.add(error);
    }
}
