package com.example.zydemo.datasource.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.zydemo.datasource.entity.DatasourceEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description:
 * @Author: zhaoyi18
 * @Date: 2023/02/20 13:56
 * @Since jdk8+
 **/
@Mapper
public interface DatasourceMapper extends BaseMapper<DatasourceEntity> {
}
