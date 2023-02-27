package com.example.zydemo.datasource.service;

import java.util.List;

/**
 * @Description:
 * @Author: zhaoyi18
 * @Date: 2023/02/20 10:50
 * @Since jdk8+
 **/
public interface OperationalDatabaseService {
    List<String> query(String ds, String sql);
}
