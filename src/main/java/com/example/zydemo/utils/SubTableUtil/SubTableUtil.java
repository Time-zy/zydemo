package com.example.zydemo.utils.SubTableUtil;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: weiruyue
 * @date: 2022/9/2 16:27
 * @className: SqlUtil
 * @description: 分表创建工具
 */
@Slf4j
@Component
public class SubTableUtil {

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    /**
     * @auther weiruyue
     * @date 2022/9/2 16:35
     * @description 根据表名创建某年的分表，格式 tablename_year_month
     **/
    @Transactional(rollbackFor = Exception.class)
    public <T> void createSubTable(Class<T> classs, Map<String,Integer> data, String timeFeild) {
        String month = String.format("%02d", data.get("month"));
        int year = data.get("year");

        StringBuilder sql = new StringBuilder();
        SqlSession session = getSqlSession();
        PreparedStatement pst = null;
        String tableName;
        TableName tableNameAnnotation = AnnotationUtils.findAnnotation(classs, TableName.class);
        if (tableNameAnnotation == null) {
            throw new BusinessException(ErrorCodeEnum.NO_ANNOTATION_ERROR.getCode(), ErrorCodeEnum.NO_ANNOTATION_ERROR.getMsg());
        }
        tableName = tableNameAnnotation.value();
        sql.append("select relname from pg_class where relname = '").append(tableName).append("_").append(data.get("year")).append("_").append(month).append("';");
        try {
            pst = session.getConnection().prepareStatement(sql.toString());
            ResultSet resultSet = pst.executeQuery();
            if (resultSet.next()) {
                log.error("{} 已存在分表 {}_{}_{} 创建失败!", tableName, tableName, year, month);
                return;
            }
        } catch (Exception e) {
            log.error("执行创建分表语句失败 create tables failed, sql:{}", sql, e);
        } finally {
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    log.error("关闭 PreparedStatement 失败 ", e);
                }
            }
            closeSqlSession(session);
        }
        createTables(classs, data, timeFeild);
        createIndexs(classs, data);
        createFunction(classs, data, timeFeild);
        createTrigger(classs, year);
    }

    /**
     * @auther weiruyue
     * @date 2022/9/3 15:51
     * @description 创建分表
     **/
    public <T> void createTables(Class<T> classs, Map<String,Integer> data, String timeFeild) {

        int monthInt = data.get("month");
        String month = String.format("%02d", monthInt);
        int year = data.get("year");

        StringBuilder sql = new StringBuilder();
        SqlSession session = getSqlSession();
        PreparedStatement pst = null;
        String tableName;
        TableName tableNameAnnotation = AnnotationUtils.findAnnotation(classs, TableName.class);
        if (tableNameAnnotation == null) {
            throw new BusinessException(ErrorCodeEnum.NO_ANNOTATION_ERROR.getCode(), ErrorCodeEnum.NO_ANNOTATION_ERROR.getMsg());
        }
        tableName = tableNameAnnotation.value();
        sql.append("create table ").append(tableName).append("_").append(year).append("_").append(month);
        sql.append("(check (").append(timeFeild).append(">= '").append(year).append("-").append(month).append("-01 00:00:00' and ");
        sql.append(timeFeild).append("< '").append(monthInt == 12 ? year + 1 : year).append("-").append(String.format("%02d", monthInt == 12 ? 1 : monthInt + 1)).append("-01 00:00:00' )) ");
        sql.append("inherits(").append(tableName).append(");");
        try {
            pst = session.getConnection().prepareStatement(sql.toString());
            pst.executeUpdate();
            log.info("create tables success,table name:{},year:{},sql:{}", tableName, year, sql);
        } catch (Exception e) {
            log.error("执行创建分表语句失败 create tables failed, sql:{}", sql, e);
        } finally {
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    log.error("关闭 PreparedStatement 失败 ", e);
                }
            }
            closeSqlSession(session);
        }
    }

    /**
     * @auther weiruyue
     * @date 2022/9/3 15:51
     * @description 创建索引
     **/
    public <T> void createIndexs(Class<T> classs, Map<String,Integer> data) {

        int monthInt = data.get("month");
        String month = String.format("%02d", monthInt);
        int year = data.get("year");

        StringBuilder sql = new StringBuilder();
        SqlSession session = getSqlSession();
        PreparedStatement pst = null;
        List<Field> fields = Arrays.asList(classs.getDeclaredFields());
        Map<String, Integer> fieldMap = new HashMap<>();
        fields.forEach(field -> {
            if (field.isAnnotationPresent(IndexDesc.class)) {
                field.setAccessible(true);
                IndexDesc indexDesc = AnnotationUtils.findAnnotation(field, IndexDesc.class);
                String fieldName = StringUtils.isNotBlank(indexDesc.fieldName()) ? indexDesc.fieldName() : field.getName();
                fieldMap.put(fieldName, indexDesc.value().getType());
            }
        });
        String tableName;
        TableName tableNameAnnotation = AnnotationUtils.findAnnotation(classs, TableName.class);
        if (tableNameAnnotation == null) {
            throw new BusinessException(ErrorCodeEnum.NO_ANNOTATION_ERROR.getCode(), ErrorCodeEnum.NO_ANNOTATION_ERROR.getMsg());
        }
        tableName = tableNameAnnotation.value();
        if (CollectionUtils.isEmpty(fieldMap)) {
            log.info("{} 分表没有索引需要创建!", tableName);
            return;
        }

        fieldMap.forEach((key, value) -> {
            sql.append("create index ").append(tableName).append("_").append(year).append("_").append(month).append("_index_").append(key);
            sql.append(" on ").append(tableName).append("_").append(year).append("_").append(month);
            if (value == 0) {
                sql.append(" using btree (").append(key).append(");");
            } else if (value == 1) {
                sql.append(" using gin (").append(key)
                        .append(" COLLATE pg_catalog.\"default\" gin_trgm_ops").append(")").append(";");
            }
        });

        try {
            pst = session.getConnection().prepareStatement(sql.toString());
            pst.executeUpdate();
            log.info("create indexs success,table name:{},year:{},indexs:{},sql:{}", tableName, year, JSON.toJSONString(fieldMap.keySet()), sql);
        } catch (Exception e) {
            log.error("执行创建索引语句失败 create indexs failed, sql:{}", sql, e);
        } finally {
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    log.error("关闭 PreparedStatement 失败 ", e);
                }
            }
            closeSqlSession(session);
        }
    }

    /**
     * @auther weiruyue
     * @date 2022/9/5 8:48
     * @description 创建funtion
     **/
    public <T> void createFunction(Class<T> classs, Map<String,Integer> data, String timeFeild) {

        int monthInt = data.get("month");
        String month = String.format("%02d", monthInt);
        int year = data.get("year");

        StringBuilder sql = new StringBuilder();
        SqlSession session = getSqlSession();
        PreparedStatement pst = null;
        String tableName;
        TableName tableNameAnnotation = AnnotationUtils.findAnnotation(classs, TableName.class);
        if (tableNameAnnotation == null) {
            throw new BusinessException(ErrorCodeEnum.NO_ANNOTATION_ERROR.getCode(), ErrorCodeEnum.NO_ANNOTATION_ERROR.getMsg());
        }
        tableName = tableNameAnnotation.value();
        sql.append("create or replace function ").append(tableName).append("_insert_trigger() \n");
        sql.append("returns trigger \n language plpgsql \n as $function$ \n begin \n");

        sql.append("if (NEW.").append(timeFeild).append("<'").append(year).append("-").append(month).append("-01 00:00:00') then \n");
        sql.append("insert into ").append(tableName).append("_").append(monthInt == 1 ? year - 1 : year).append("_").append(String.format("%02d", monthInt == 1 ? 12 : monthInt - 1)).append(" values (NEW.*); \n");
        sql.append("elsif (NEW.").append(timeFeild).append(">='").append(year).append("-").append(month).append("-01 00:00:00' and ");
        sql.append("NEW.").append(timeFeild).append("<'").append(monthInt == 12 ? year + 1 : year).append("-").append(String.format("%02d", monthInt == 12 ? 1 : monthInt + 1)).append("-01 00:00:00') then \n");
        sql.append("insert into ").append(tableName).append("_").append(year).append("_").append(month).append(" values (NEW.*); \n");

        sql.append("else \n raise exception '").append(timeFeild).append(" out of range!!!';\n");
        sql.append("end if; \n return NULL; \n END; \n $function$;");
        try {
            pst = session.getConnection().prepareStatement(sql.toString());
            pst.executeUpdate();
            log.info("create function success,table name:{},year:{},sql:{}", tableName, year, sql);
        } catch (Exception e) {
            log.error("执行创建funtion语句失败 create function failed, sql:{}", sql, e);
        } finally {
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    log.error("关闭 PreparedStatement 失败 ", e);
                }
            }
            closeSqlSession(session);
        }
    }

    /**
     * @auther weiruyue
     * @date 2022/9/5 9:29
     * @description 创建触发器
     **/
    public <T> void createTrigger(Class<T> classs, int year) {
        StringBuilder sqlSelect = new StringBuilder();
        StringBuilder sql = new StringBuilder();
        SqlSession session = getSqlSession();
        PreparedStatement pst = null;
        String tableName;
        TableName tableNameAnnotation = AnnotationUtils.findAnnotation(classs, TableName.class);
        if (tableNameAnnotation == null) {
            throw new BusinessException(ErrorCodeEnum.NO_ANNOTATION_ERROR.getCode(), ErrorCodeEnum.NO_ANNOTATION_ERROR.getMsg());
        }
        tableName = tableNameAnnotation.value();
        sqlSelect.append("select t.tgname from pg_trigger t where t.tgname = '").append(tableName).append("_insert_trigger';");
        try {
            Connection connection = session.getConnection();
            pst = connection.prepareStatement(sqlSelect.toString());
            ResultSet resultSet = pst.executeQuery();
            if (!resultSet.next()) {
                sql.append("create trigger ").append(tableName).append("_insert_trigger before insert on ").append(tableName).append(" for each row ");
                sql.append("execute procedure ").append(tableName).append("_insert_trigger();");
                try {
                    pst = connection.prepareStatement(sql.toString());
                    pst.executeUpdate();
                    log.info("create trigger success,trigger name:{},year:{},sql:{}", tableName + "_insert_trigger", year, sql);
                } catch (Exception e) {
                    log.error("执行创建触发器语句失败 create trigger failed, sql:{}", sqlSelect, e);
                }
            }
        } catch (Exception e) {
            log.error("执行查询触发器语句失败 select trigger failed, sql:{}", sqlSelect, e);
        } finally {
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    log.error("关闭 PreparedStatement 失败 ", e);
                }
            }
            closeSqlSession(session);
        }
    }

    /**
     * @auther weiruyue
     * @date 2022/9/2 16:36
     * @description 获取sqlSession
     **/
    public SqlSession getSqlSession() {
        return SqlSessionUtils.getSqlSession(sqlSessionTemplate.getSqlSessionFactory(),
                sqlSessionTemplate.getExecutorType(), sqlSessionTemplate.getPersistenceExceptionTranslator());
    }

    /**
     * @auther weiruyue
     * @date 2022/9/2 16:36
     * @description 关闭sqlSession
     **/
    public void closeSqlSession(SqlSession session) {
        SqlSessionUtils.closeSqlSession(session, sqlSessionTemplate.getSqlSessionFactory());
    }
}
