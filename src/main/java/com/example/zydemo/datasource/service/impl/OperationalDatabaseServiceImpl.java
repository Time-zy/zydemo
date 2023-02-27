package com.example.zydemo.datasource.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.example.zydemo.datasource.mapper.CommonMapper;
import com.example.zydemo.datasource.service.OperationalDataSourceService;
import com.example.zydemo.datasource.service.OperationalDatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: zhaoyi18
 * @Date: 2023/02/20 10:47
 * @Since jdk8+
 **/
@Service
public class OperationalDatabaseServiceImpl implements OperationalDatabaseService {

    @Autowired
    private CommonMapper commonMapper;
    @Autowired(required = false)
    private DynamicRoutingDataSource dynamicRoutingDataSource;
    @Autowired
    private OperationalDataSourceService operationalDataSourceService;

    @Override
    public List<String> query(String ds, String sql) {

        // 核心代码
        if (!operationalDataSourceService.now().contains(ds)) {
            return new ArrayList<>();
        }

        if (!ds.equalsIgnoreCase(DynamicDataSourceContextHolder.peek())) {
            DynamicDataSourceContextHolder.poll();
            DynamicDataSourceContextHolder.push(ds);
        }

        return commonMapper.query(sql);
    }

}
