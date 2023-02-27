package com.example.zydemo.repository.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @Author: zhaoyi18
 * @Date: 2022/09/27 10:42
 * @Since jdk8+
 **/
@Data
@ApiModel(value = "测试表")
public class TestDTO {
    @ApiModelProperty("测试属性")
    private String test;
}
