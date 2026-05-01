package com.example.domitory.controller;

import com.example.domitory.common.Result;
import com.example.domitory.entity.Role;
import com.example.domitory.entity.User;
import com.example.domitory.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/list")
    @ResponseBody
    public Result<List<User>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) Integer status) {
        
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("id").descending());
        Page<User> userPage = userService.findAll(username, realName, status, pageable);
        
        return Result.page(userPage.getContent(), userPage.getTotalElements());
    }
    
    @GetMapping("/{id}")
    @ResponseBody
    public Result<User> getById(@PathVariable Long id) {
        return userService.findById(id)
                .map(Result::success)
                .orElse(Result.error("用户不存在"));
    }
    
    @GetMapping("/current")
    @ResponseBody
    public Result<Map<String, Object>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Result.error("用户未登录");
        }
        
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElse(null);
        
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("username", user.getUsername());
        result.put("realName", user.getRealName());
        result.put("phone", user.getPhone());
        result.put("email", user.getEmail());
        result.put("gender", user.getGender());
        
        List<String> roles = user.getRoles().stream()
                .map(Role::getRoleCode)
                .collect(Collectors.toList());
        result.put("roles", roles);
        
        return Result.success(result);
    }
    
    @PostMapping("/save")
    @ResponseBody
    public Result<User> save(@RequestBody Map<String, Object> params) {
        try {
            User user = new User();
            if (params.get("id") != null) {
                user.setId(Long.valueOf(params.get("id").toString()));
            }
            user.setUsername((String) params.get("username"));
            if (params.get("password") != null) {
                user.setPassword((String) params.get("password"));
            }
            user.setRealName((String) params.get("realName"));
            user.setPhone((String) params.get("phone"));
            user.setEmail((String) params.get("email"));
            if (params.get("gender") != null) {
                user.setGender(Integer.valueOf(params.get("gender").toString()));
            }
            if (params.get("status") != null) {
                user.setStatus(Integer.valueOf(params.get("status").toString()));
            }
            
            @SuppressWarnings("unchecked")
            List<String> roleCodes = (List<String>) params.get("roleCodes");
            
            User savedUser = userService.save(user, roleCodes);
            return Result.success(savedUser);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/register")
    @ResponseBody
    public Result<User> register(@RequestBody User user) {
        try {
            User registeredUser = userService.register(user);
            return Result.success(registeredUser);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/delete")
    @ResponseBody
    public Result<Void> delete(@RequestParam Long id) {
        try {
            userService.delete(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/updateStatus")
    @ResponseBody
    public Result<Void> updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        try {
            userService.updateStatus(id, status);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/resetPassword")
    @ResponseBody
    public Result<Void> resetPassword(@RequestParam Long id) {
        try {
            userService.resetPassword(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/roles")
    @ResponseBody
    public Result<List<Role>> getRoles() {
        List<Role> roles = userService.findAllRoles();
        return Result.success(roles);
    }
    
    @GetMapping("/byRole")
    @ResponseBody
    public Result<List<User>> getUsersByRole(@RequestParam String roleCode) {
        List<User> users = userService.findByRoleCode(roleCode);
        return Result.success(users);
    }
}
