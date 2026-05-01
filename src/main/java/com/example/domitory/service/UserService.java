package com.example.domitory.service;

import com.example.domitory.entity.Role;
import com.example.domitory.entity.User;
import com.example.domitory.repository.RoleRepository;
import com.example.domitory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.*;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public Page<User> findAll(String username, String realName, Integer status, Pageable pageable) {
        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (StringUtils.hasText(username)) {
                predicates.add(cb.like(root.get("username"), "%" + username + "%"));
            }
            
            if (StringUtils.hasText(realName)) {
                predicates.add(cb.like(root.get("realName"), "%" + realName + "%"));
            }
            
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        return userRepository.findAll(spec, pageable);
    }
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    @Transactional
    public User save(User user, List<String> roleCodes) {
        if (user.getId() == null) {
            if (userRepository.existsByUsername(user.getUsername())) {
                throw new RuntimeException("用户名已存在");
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            User existUser = userRepository.findById(user.getId())
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
            if (StringUtils.hasText(user.getPassword())) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            } else {
                user.setPassword(existUser.getPassword());
            }
            if (!existUser.getUsername().equals(user.getUsername())) {
                if (userRepository.existsByUsername(user.getUsername())) {
                    throw new RuntimeException("用户名已存在");
                }
            }
        }
        
        if (roleCodes != null && !roleCodes.isEmpty()) {
            Set<Role> roles = new HashSet<>();
            for (String roleCode : roleCodes) {
                roleRepository.findByRoleCode(roleCode).ifPresent(roles::add);
            }
            user.setRoles(roles);
        }
        
        return userRepository.save(user);
    }
    
    @Transactional
    public User register(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(1);
        
        Set<Role> roles = new HashSet<>();
        roleRepository.findByRoleCode("STUDENT").ifPresent(roles::add);
        user.setRoles(roles);
        
        return userRepository.save(user);
    }
    
    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
    
    @Transactional
    public void updateStatus(Long id, Integer status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setStatus(status);
        userRepository.save(user);
    }
    
    @Transactional
    public void resetPassword(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setPassword(passwordEncoder.encode("123456"));
        userRepository.save(user);
    }
    
    public List<Role> findAllRoles() {
        return roleRepository.findAll();
    }
    
    public List<User> findByRoleCode(String roleCode) {
        Optional<Role> roleOpt = roleRepository.findByRoleCode(roleCode);
        if (roleOpt.isPresent()) {
            return userRepository.findAll((root, query, cb) -> {
                return cb.isMember(roleOpt.get(), root.get("roles"));
            });
        }
        return new ArrayList<>();
    }
}
