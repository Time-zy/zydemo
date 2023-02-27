package com.example.zydemo.datasource.service;

import com.example.zydemo.datasource.entity.DatasourceEntity;

import java.util.Set;

/**
 * @Description:
 * @Author: zhaoyi18
 * @Date: 2023/02/20 13:40
 * @Since jdk8+
 **/
public interface OperationalDataSourceService {

    /**获取当前所有数据源*/
    Set<String> now();

    /**通用添加数据源（推荐）*/
    //通用数据源会根据maven中配置的连接池根据顺序依次选择。
    //默认的顺序为druid>hikaricp>beecp>dbcp>spring basic
    Set<String> add(DatasourceEntity datasourceEntity);

    /**添加基础数据源；调用Springboot内置方法创建数据源，兼容1,2(强烈不推荐，除了用了马上移除)*/
    Set<String> addBasic(DatasourceEntity datasourceEntity);

    /**添加JNDI数据源*/
    Set<String> addJndi(String poolName, String jndiName);

    /**基础Druid数据源*/
    Set<String> addDruid(DatasourceEntity datasourceEntity);

    /**基础HikariCP数据源*/
    Set<String> addHikariCP(DatasourceEntity datasourceEntity);

    /**基础BeeCp数据源*/
    Set<String> addBeeCp(DatasourceEntity datasourceEntity);

    /**基础Dbcp数据源*/
    Set<String> addDbcp(DatasourceEntity datasourceEntity);

    /**删除数据源*/
    String remove(String name);

    /**初始化数据源*/
    Set<String> init();
}
