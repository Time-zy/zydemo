package com.example.zydemo.datasource.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.zydemo.datasource.service.OperationalDatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @Author: zhaoyi18
 * @Date: 2023/02/20 10:46
 * @Since jdk8+
 **/
@RestController
public class OperationalDatabaseController {
    @Autowired
    private OperationalDatabaseService operationalDatabaseService;

    @RequestMapping(value = "/query", method = {RequestMethod.GET, RequestMethod.POST})
    public Object query(@RequestBody JSONObject param) {
        String ds = param.getString("ds");
        String sql = param.getString("sql");

        return operationalDatabaseService.query(ds, sql);
    }

}
