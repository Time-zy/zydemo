package com.example.zydemo.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.zydemo.file.repository.entity.FileInfoEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description:
 * @Author: zhaoyi18
 * @Date: 2022/09/19 09:33
 * @Since jdk8+
 **/
@Mapper
public interface FileInfoMapper extends BaseMapper<FileInfoEntity> {
}
