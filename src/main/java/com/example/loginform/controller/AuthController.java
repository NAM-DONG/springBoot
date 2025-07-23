package com.example.loginform.controller;

import com.example.loginform.model.User;
import com.example.loginform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.security.Principal;

@Controller
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }
    
    @GetMapping("/login")
    public String loginForm(@RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "logout", required = false) String logout,
                           Model model) {
        if (error != null) {
            model.addAttribute("error", "사용자명 또는 비밀번호가 잘못되었습니다.");
        }
        if (logout != null) {
            model.addAttribute("message", "성공적으로 로그아웃되었습니다.");
        }
        return "login";
    }
    
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }
    
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user, 
                          BindingResult result, 
                          Model model) {
        
        if (result.hasErrors()) {
            return "register";
        }
        
        if (userService.existsByUsername(user.getUsername())) {
            model.addAttribute("error", "이미 존재하는 사용자명입니다.");
            return "register";
        }
        
        if (userService.existsByEmail(user.getEmail())) {
            model.addAttribute("error", "이미 존재하는 이메일입니다.");
            return "register";
        }
        
        userService.saveUser(user);
        model.addAttribute("success", "회원가입이 완료되었습니다. 로그인해주세요.");
        return "login";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Principal principal, Model model) {
        if (principal != null) {
            model.addAttribute("username", principal.getName());
        }
        return "dashboard";
    }
}