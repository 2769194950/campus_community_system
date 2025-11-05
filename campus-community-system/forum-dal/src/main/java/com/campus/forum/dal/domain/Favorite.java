package com.campus.forum.dal.domain;

import lombok.Data;
import java.util.Date;

/**
 * 收藏实体
 * @author Bugar
 * @date 2024/5/16
 */
@Data
public class Favorite {
    private Integer id;
    private Integer userId;
    private Integer postId;
    private Date createTime;
}

