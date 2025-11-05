package com.campus.forum.dal.domain;

import lombok.Data;
import java.util.Date;

/**
 * 好友实体
 * @author Bugar
 * @date 2024/5/16
 */
@Data
public class Friend {
    private Integer id;
    private Integer userId;
    private Integer friendId;
    private Integer status;
    private Date createTime;
}

