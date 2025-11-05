package com.campus.forum.api.controller;

import com.campus.forum.common.Result;
import com.campus.forum.dal.domain.Partition;
import com.campus.forum.service.PartitionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/partition")
@Tag(name = "分区控制器")
public class PartitionController {
    @Autowired
    private PartitionService partitionService;

    @GetMapping("/list")
    @Operation(summary = "查询分区列表")
    public Result getPartitionList() {
        List<Partition> list = partitionService.findPartitions();
        return Result.success(list);
    }
}
