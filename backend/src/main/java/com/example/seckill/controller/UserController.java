package com.example.seckill.controller;

import com.example.seckill.entity.User;
import com.example.seckill.service.UserService;
import com.example.seckill.util.RSAUtil;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.seckill.entity.LoginRequest;
import com.example.seckill.util.JwtUtil;
import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Response register(@RequestBody User user) {
        try {
            // 解密密码
            logger.debug("user.getPassword() = " + user.getPassword());
            String decryptedPassword = RSAUtil.decrypt(user.getPassword());
            user.setPassword(decryptedPassword);

            // 执行注册逻辑，获取注册后的用户信息
            User registeredUser = userService.register(user);
            logger.info("User registered: " + registeredUser + " successfully");

            // 创建数据 Map 并添加注册用户信息
            Map<String, Object> data = new HashMap<>();
            data.put("user", registeredUser);

            // 返回成功响应
            return new Response(200, "注册成功", data);
        } catch (Exception e) {
            // 返回失败响应
            return new Response(400, e.getMessage(), null);
        }
    }

    @PostMapping("/login")
    public Response login(@RequestBody LoginRequest loginRequest) {
        try {
            // 解密密码
            String decryptedPassword = RSAUtil.decrypt(loginRequest.getPassword());

            logger.info("loginRequest.getUsername() = " + loginRequest.getUsername());
            // 执行登录逻辑，获取用户信息
            User user = userService.login(loginRequest.getUsername(), decryptedPassword);

            // 生成 Token
            String token = JwtUtil.generateToken(user);

            // 创建数据 Map 并添加 token 和 user
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", user);

            logger.info("用户登录：{}", loginRequest.getUsername());
            // 返回成功响应
            return new Response(200, "登录成功", data);
        } catch (Exception e) {
            // 返回失败响应
            return new Response(400, e.getMessage(), null);
        }
    }

    @GetMapping("/userInfo")
    public Response getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String username = (String) request.getAttribute("username");
        User user = userService.findById(userId);
        Map<String, Object> data = new HashMap<>();
        data.put("userID", userId);
        data.put("user", user);
        return new Response(200, "获取用户信息成功", data);
    }


}

