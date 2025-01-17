package com.example.seckill.entity;

import jakarta.persistence.*;
import java.util.Date;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

@Data
@Entity
@Table(name = "t_user")
//@RedisHash("users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    private String salt;

    private String nickname;

    private String email;

    private String mobile;

    @Temporal(TemporalType.TIMESTAMP)
    private Date registerDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLoginDate;

    private Integer loginCount;
}
