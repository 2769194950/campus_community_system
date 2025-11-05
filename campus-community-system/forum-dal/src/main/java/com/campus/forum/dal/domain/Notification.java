package com.campus.forum.dal.domain;

import lombok.Data;
import java.util.Date;

/**
 * 通知实体
 * @author Bugar
 * @date 2024/5/16
 */
@Data
public class Notification {
    private Integer id;
    private Integer fromUserId;
    private Integer toUserId;
    private String topic;
    private String content;
    private Integer status;
    private Date createTime;
}

