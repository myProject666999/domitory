package com.example.domitory.config;

import com.example.domitory.entity.Role;
import com.example.domitory.entity.User;
import com.example.domitory.repository.RoleRepository;
import com.example.domitory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private static final String DEFAULT_PASSWORD = "123456";
    private static final String ADMIN_USERNAME = "admin";
    
    @Override
    public void run(String... args) {
        initRoles();
        initOrUpdateAdmin();
    }
    
    private void initRoles() {
        if (roleRepository.count() == 0) {
            Role student = new Role();
            student.setRoleName("学生");
            student.setRoleCode("STUDENT");
            student.setDescription("普通学生用户");
            roleRepository.save(student);
            
            Role manager = new Role();
            manager.setRoleName("宿管");
            manager.setRoleCode("DORM_MANAGER");
            manager.setDescription("宿舍管理员");
            roleRepository.save(manager);
            
            Role logistics = new Role();
            logistics.setRoleName("后勤");
            logistics.setRoleCode("LOGISTICS");
            logistics.setDescription("后勤管理人员");
            roleRepository.save(logistics);
        }
    }
    
    private void initOrUpdateAdmin() {
        Optional<User> adminOpt = userRepository.findByUsername(ADMIN_USERNAME);
        
        if (adminOpt.isPresent()) {
            User admin = adminOpt.get();
            String storedPassword = admin.getPassword();
            
            boolean isValidBcrypt = isBcryptHash(storedPassword);
            boolean matchesDefault = false;
            
            if (isValidBcrypt) {
                try {
                    matchesDefault = passwordEncoder.matches(DEFAULT_PASSWORD, storedPassword);
                } catch (Exception e) {
                    isValidBcrypt = false;
                }
            }
            
            if (!isValidBcrypt || !matchesDefault) {
                admin.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
                ensureAdminRoles(admin);
                userRepository.save(admin);
            }
        } else {
            createAdmin();
        }
    }
    
    private boolean isBcryptHash(String password) {
        if (password == null || password.length() != 60) {
            return false;
        }
        return password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$");
    }
    
    private void createAdmin() {
        User admin = new User();
        admin.setUsername(ADMIN_USERNAME);
        admin.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        admin.setRealName("系统管理员");
        admin.setPhone("13800138000");
        admin.setStatus(1);
        ensureAdminRoles(admin);
        userRepository.save(admin);
    }
    
    private void ensureAdminRoles(User admin) {
        Set<Role> roles = new HashSet<>();
        roleRepository.findByRoleCode("STUDENT").ifPresent(roles::add);
        roleRepository.findByRoleCode("DORM_MANAGER").ifPresent(roles::add);
        roleRepository.findByRoleCode("LOGISTICS").ifPresent(roles::add);
        admin.setRoles(roles);
    }
}
