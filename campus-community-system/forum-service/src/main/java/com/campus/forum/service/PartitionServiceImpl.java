package com.campus.forum.service;

import com.campus.forum.dal.domain.Partition;
import com.campus.forum.dal.mapper.PartitionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Bugar
 * @date 2024/5/16
 */
@Service
public class PartitionServiceImpl implements PartitionService {
    @Autowired
    private PartitionMapper partitionMapper;

    @Override
    public List<Partition> findPartitions() {
        return partitionMapper.selectAll();
    }
}
