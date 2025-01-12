package com.example.seckill.service.impl;

import com.example.seckill.entity.User;
import com.example.seckill.repository.UserRepository;
import com.example.seckill.service.UserService;
import com.example.seckill.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Date;

import static com.example.seckill.util.PasswordUtil.encoder;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private UserRepository userRepository;

    @Override
    public User register(User user) {
        // 检查用户名、邮箱、手机号是否已存在
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new RuntimeException("邮箱已被注册");
        }
        if (userRepository.findByMobile(user.getMobile()) != null) {
            throw new RuntimeException("手机号已被注册");
        }

        // 生成盐值
        String salt = PasswordUtil.generateSalt();
        user.setSalt(salt);

        // 密码加密
        String encryptedPassword = PasswordUtil.encryptPassword(user.getPassword(), salt);
        user.setPassword(encryptedPassword);

        // 设置注册日期
        user.setRegisterDate(new Date());

        if (user.getLoginCount() == null) {
            user.setLoginCount(0); // 初始值为 0
        }

        // 保存用户
        return userRepository.save(user);
    }

    @Override
    public User login(String username, String password) {
        logger.info("username = " + username + ", password = " + password);
        User user = userRepository.findByUsername(username);
        if (user == null) {
            if(userRepository.findByEmail(username) != null) {
                user = userRepository.findByEmail(username);
            } else if(userRepository.findByMobile(username) != null) {
                user = userRepository.findByMobile(username);
            } else throw new RuntimeException("用户不存在");
        }
        // 验证密码
        String encryptedPassword = PasswordUtil.encryptPassword(password, user.getSalt());
//        logger.info("Salt: " + user.getSalt() + ", EncryptedPassword: " + encryptedPassword);
//        logger.info("User's password in mysql: " + user.getPassword());
        if (!encoder.matches(password + user.getSalt(), encryptedPassword)) {
            throw new RuntimeException("密码错误");
        }
        // 更新最后登录时间和登录次数
        user.setLastLoginDate(new Date());
        user.setLoginCount(user.getLoginCount() + 1);
        userRepository.save(user);
        return user;
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

}
