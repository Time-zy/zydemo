package com.example.zydemo.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.zydemo.file.repository.entity.FileInfoEntity;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @Description:
 * @Author: zhaoyi18
 * @Date: 2022/10/08 11:25
 * @Since jdk8+
 **/
public interface FileInfoService extends IService<FileInfoEntity> {
    FileInfoEntity uploadFile(MultipartFile multipartFile,String filePath);
    ResponseEntity<InputStreamResource> downloadFile(String id) throws IOException;
    void downloadFileByResponse(String id,HttpServletResponse response) throws IOException;
    String wordTurnBytePdf(String filePath) throws IOException;
    void dropFile(List<String> filePathList);
    void exportExcel(HttpServletResponse response);
    String importExcel(MultipartFile file);
}
