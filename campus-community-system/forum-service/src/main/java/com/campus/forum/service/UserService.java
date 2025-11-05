package com.campus.forum.service;

import com.campus.forum.dal.domain.User;

import java.util.Map;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

/**
 * @author Bugar
 * @date 2024/5/16
 */
public interface UserService {
    Map<String, Object> register(User user);
    Map<String, Object> login(String username, String password);
    void logout(String ticket);
    User findUserById(int id);
    int updateAvatar(int userId, String avatarUrl);
    User findUserByName(String username);
    int updateIntroduction(int userId, String introduction);
    int updateEmail(int userId, String email);
    int updateUsername(int userId, String username);
    Collection<? extends GrantedAuthority> getAuthorities(int userId);
    
    // 找回密码相关
    Map<String, Object> sendResetPasswordEmail(String email);
    Map<String, Object> resetPassword(String token, String newPassword);
    
    // 密保问题相关
    int updateSecurityQuestion(int userId, String question, String answer);
    Map<String, Object> getSecurityQuestion(String username);
    Map<String, Object> resetPasswordBySecurityQuestion(String username, String answer, String newPassword);
}
