package com.example.zydemo.datasource.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Description:
 * @Author: zhaoyi18
 * @Date: 2023/02/17 13:57
 * @Since jdk8+
 **/
@Mapper
public interface CommonMapper {
    @Select("<script>"+
            "${sqlStr}"+
            "</script>"
    )
    List<String> query(@Param(value = "sqlStr") String sqlStr);

//    @Select("<script>"+
//            "${sqlStr}"+
//            "</script>"
//    )
//    @DS("#database")
//    List<String> query(@Param(value = "sqlStr") String sqlStr,String database);
}
