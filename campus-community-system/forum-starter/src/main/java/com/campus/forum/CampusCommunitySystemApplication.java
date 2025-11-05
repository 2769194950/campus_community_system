package com.campus.forum;

import jakarta.annotation.PostConstruct;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan({"com.campus.forum.dal.mapper"})
public class CampusCommunitySystemApplication {

    public void init() {
        // an issue with Elasticsearch
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }

    public static void main(String[] args) {
        SpringApplication.run(CampusCommunitySystemApplication.class, args);
    }

}

