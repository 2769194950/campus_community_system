package com.campus.forum.dal.domain;

import lombok.Data;
import java.util.Date;

/**
 * 用户实体
 * @author Bugar
 * @date 2024/5/16
 */
@Data
public class User {
    private Integer id;
    private String username;
    private String password;
    private String salt;
    private String email;
    private Integer type;
    private Integer status;
    private String activationCode;
    private String avatarUrl;
    private String introduction;
    private Date createTime;
    
    // 密保问题
    private String securityQuestion;
    private String securityAnswer;
    
    // 统计字段（用于榜单等功能）
    private Integer postCount;
    private Integer commentCount;
}

