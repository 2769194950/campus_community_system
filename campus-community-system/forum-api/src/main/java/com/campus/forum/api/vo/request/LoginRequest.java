package com.campus.forum.api.vo.request;

import lombok.Data;

/**
 * @author Bugar
 * @date 2024/5/16
 */
@Data
public class LoginRequest {
    private String username;
    private String password;
}

