package com.example.zydemo.datasource.service.impl;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.creator.*;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.zydemo.datasource.entity.DatasourceEntity;
import com.example.zydemo.datasource.mapper.DatasourceMapper;
import com.example.zydemo.datasource.service.OperationalDataSourceService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Description:
 * @Author: zhaoyi18
 * @Date: 2023/02/20 13:40
 * @Since jdk8+
 **/
@Log4j2
@Service
public class OperationalDataSourceServiceImpl implements OperationalDataSourceService {
    @Autowired
    private DataSource dataSource;
    // private final DataSourceCreator dataSourceCreator; //3.3.1及以下版本使用这个通用
    @Autowired
    private DefaultDataSourceCreator dataSourceCreator;
    @Autowired
    private BasicDataSourceCreator basicDataSourceCreator;
    @Autowired
    private JndiDataSourceCreator jndiDataSourceCreator;
    @Autowired(required = false)
    private DruidDataSourceCreator druidDataSourceCreator;
    @Autowired(required = false)
    private HikariDataSourceCreator hikariDataSourceCreator;
    @Autowired(required = false)
    private BeeCpDataSourceCreator beeCpDataSourceCreator;
    @Autowired(required = false)
    private Dbcp2DataSourceCreator dbcp2DataSourceCreator;
    @Autowired(required = false)
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Autowired
    private DatasourceMapper datasourceMapper;

    @Override
    public Set<String> now() {
//        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
//        return ds.getDataSources().keySet();
        return dynamicRoutingDataSource.getDataSources().keySet();
    }


    @Override
    public Set<String> add(DatasourceEntity datasourceEntity) {
        DataSourceProperty dataSourceProperty = new DataSourceProperty();
        BeanUtils.copyProperties(datasourceEntity, dataSourceProperty);
//        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        try {
            Class.forName(datasourceEntity.getDriverClassName());
            Connection connection = DriverManager.getConnection(datasourceEntity.getUrl(), datasourceEntity.getUsername(), datasourceEntity.getPassword());
            connection.close();
            dataSource = dataSourceCreator.createDataSource(dataSourceProperty);
//            ds.addDataSource(datasourceEntity.getPoolName(), dataSource);
            dynamicRoutingDataSource.addDataSource(datasourceEntity.getDatasourceName(), dataSource);
        } catch (Exception e) {
            log.error("datasource connection failure : ", e);
        }
//        return ds.getDataSources().keySet();
        return dynamicRoutingDataSource.getDataSources().keySet();
    }

    @Override
    public Set<String> addBasic(DatasourceEntity datasourceEntity) {
        DataSourceProperty dataSourceProperty = new DataSourceProperty();
        BeanUtils.copyProperties(datasourceEntity, dataSourceProperty);
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        DataSource dataSource = basicDataSourceCreator.createDataSource(dataSourceProperty);
        ds.addDataSource(datasourceEntity.getDatasourceName(), dataSource);
        return ds.getDataSources().keySet();
    }

    @Override
    public Set<String> addJndi(String poolName, String jndiName) {
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        DataSource dataSource = jndiDataSourceCreator.createDataSource(jndiName);
        ds.addDataSource(poolName, dataSource);
        return ds.getDataSources().keySet();
    }

    @Override
    public Set<String> addDruid(DatasourceEntity datasourceEntity) {
        DataSourceProperty dataSourceProperty = new DataSourceProperty();
        BeanUtils.copyProperties(datasourceEntity, dataSourceProperty);
        dataSourceProperty.setLazy(true);
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        DataSource dataSource = druidDataSourceCreator.createDataSource(dataSourceProperty);
        ds.addDataSource(datasourceEntity.getDatasourceName(), dataSource);
        return ds.getDataSources().keySet();
    }

    @Override
    public Set<String> addHikariCP(DatasourceEntity datasourceEntity) {
        DataSourceProperty dataSourceProperty = new DataSourceProperty();
        BeanUtils.copyProperties(datasourceEntity, dataSourceProperty);
        dataSourceProperty.setLazy(true);//3.4.0版本以下如果有此属性，需手动设置，不然会空指针。
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        DataSource dataSource = hikariDataSourceCreator.createDataSource(dataSourceProperty);
        ds.addDataSource(datasourceEntity.getDatasourceName(), dataSource);
        return ds.getDataSources().keySet();
    }

    @Override
    public Set<String> addBeeCp(DatasourceEntity datasourceEntity) {
        DataSourceProperty dataSourceProperty = new DataSourceProperty();
        BeanUtils.copyProperties(datasourceEntity, dataSourceProperty);
        dataSourceProperty.setLazy(true);//3.4.0版本以下如果有此属性，需手动设置，不然会空指针。
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        DataSource dataSource = beeCpDataSourceCreator.createDataSource(dataSourceProperty);
        ds.addDataSource(datasourceEntity.getDatasourceName(), dataSource);
        return ds.getDataSources().keySet();
    }

    @Override
    public Set<String> addDbcp(DatasourceEntity datasourceEntity) {
        DataSourceProperty dataSourceProperty = new DataSourceProperty();
        BeanUtils.copyProperties(datasourceEntity, dataSourceProperty);
        dataSourceProperty.setLazy(true);//3.4.0版本以下如果有此属性，需手动设置，不然会空指针。
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        DataSource dataSource = dbcp2DataSourceCreator.createDataSource(dataSourceProperty);
        ds.addDataSource(datasourceEntity.getDatasourceName(), dataSource);
        return ds.getDataSources().keySet();
    }

    @Override
    public String remove(String name) {
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        ds.removeDataSource(name);
        return "删除成功";
    }

    @Override
    public Set<String> init() {
        List<DatasourceEntity> datasourceList = datasourceMapper.selectList(new LambdaQueryWrapper<>());

        if (CollectionUtils.isEmpty(datasourceList)) {
            return new HashSet<>();
        }

        Set<String> result = new HashSet<>();
        for (DatasourceEntity datasource : datasourceList) {
            result = add(datasource);
        }
        return result;
    }

}
