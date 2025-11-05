package com.campus.forum.dal.domain;

import lombok.Data;
import java.util.Date;

/**
 * 点赞记录实体
 * @author Bugar
 * @date 2024/5/16
 */
@Data
public class LikeLog {
    private Integer id;
    private Integer userId;
    private Integer entityType;
    private Integer entityId;
    private Integer entityUserId;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}

