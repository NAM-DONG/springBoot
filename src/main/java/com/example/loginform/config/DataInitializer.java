package com.example.loginform.config;

import com.example.loginform.model.User;
import com.example.loginform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    @Lazy
    private UserService userService;
    
    @Override
    public void run(String... args) throws Exception {
        // 테스트 사용자가 없을 때만 생성
        if (!userService.existsByUsername("admin")) {
            User admin = new User("admin", "admin@example.com", "password");
            userService.saveUser(admin);
            System.out.println("기본 관리자 계정이 생성되었습니다.");
            System.out.println("사용자명: admin");
            System.out.println("비밀번호: password");
        }
        
        if (!userService.existsByUsername("user")) {
            User user = new User("user", "user@example.com", "password");
            userService.saveUser(user);
            System.out.println("기본 사용자 계정이 생성되었습니다.");
            System.out.println("사용자명: user");
            System.out.println("비밀번호: password");
        }
    }
}