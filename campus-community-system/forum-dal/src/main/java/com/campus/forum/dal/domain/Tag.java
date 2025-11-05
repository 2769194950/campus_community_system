package com.campus.forum.dal.domain;

import lombok.Data;
import java.util.Date;

/**
 * 标签实体
 * @author Bugar
 * @date 2024/5/16
 */
@Data
public class Tag {
    private Integer id;
    private String name;
    private Date createTime;
}

