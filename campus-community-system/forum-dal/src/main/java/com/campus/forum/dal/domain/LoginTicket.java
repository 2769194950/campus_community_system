package com.campus.forum.dal.domain;

import lombok.Data;

import java.util.Date;

/**
 * @author Bugar
 * @date 2024/5/16
 */
@Data
public class LoginTicket {
    private int id;
    private int userId;
    private String ticket;
    private int status;
    private Date expired;
}

