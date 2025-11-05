package com.campus.forum.service;

import com.campus.forum.dal.domain.Partition;

import java.util.List;

/**
 * @author Bugar
 * @date 2024/5/16
 */
public interface PartitionService {

    List<Partition> findPartitions();

}
