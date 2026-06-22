package com.example.backend.entity.vo.user;

import com.example.backend.entity.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author caiya
 * @Description 脱敏的用户信息
 * @Version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserLoginVO extends User {
    private String token;
}
