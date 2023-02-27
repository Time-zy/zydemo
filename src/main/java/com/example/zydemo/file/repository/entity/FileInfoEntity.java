package com.example.zydemo.file.repository.entity;


import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@TableName("tb_file_info")
@ApiModel(value = "文件表")
public class FileInfoEntity {

  @TableId(value = "id", type = IdType.ASSIGN_UUID)
  private String id;
  @ApiModelProperty("文件编号")
  private String fileCode;
  @ApiModelProperty("文件名称")
  private String fileName;
  @ApiModelProperty("文件路径")
  private String filePath;
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
  @ApiModelProperty(value = "创建时间",example = "2021-12-12 12:12:12")
  @TableField(value = "create_time", fill = FieldFill.INSERT)
  private java.sql.Timestamp createTime;
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
  @ApiModelProperty(value = "更新时间",example = "2021-12-12 12:12:12")
  @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
  private java.sql.Timestamp updateTime;

}
