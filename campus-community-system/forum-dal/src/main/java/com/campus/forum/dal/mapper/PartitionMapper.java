package com.campus.forum.dal.mapper;

import com.campus.forum.dal.domain.Partition;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Bugar
 * @date 2024/5/16
 */
@Mapper
public interface PartitionMapper {

    List<Partition> selectAll();
    
    Partition selectById(int id);

    Partition selectByName(String name);
    
    int insertPartition(Partition partition);
}

