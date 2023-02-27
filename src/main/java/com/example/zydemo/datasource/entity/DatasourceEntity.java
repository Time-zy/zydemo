package com.example.zydemo.datasource.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_datasource")
public class DatasourceEntity {

  @TableId
  private String datasourceName;
  private String url;
  private String username;
  private String password;
  private String driverClassName;
  private String operationPerson;
  private java.sql.Timestamp createTime;
  private java.sql.Timestamp updateTime;
}
