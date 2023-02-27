package com.example.zydemo.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

/**
 * @Description:
 * @Author: zhaoyi18
 * @Date: 2022/03/24 11:33
 * @Since jdk8+
 **/
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", Timestamp.class, new Timestamp(System.currentTimeMillis()));
        this.strictUpdateFill(metaObject, "updateTime", Timestamp.class, new Timestamp(System.currentTimeMillis()));
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", Timestamp.class, new Timestamp(System.currentTimeMillis()));
    }
}