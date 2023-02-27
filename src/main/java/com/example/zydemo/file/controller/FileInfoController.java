package com.example.zydemo.file.controller;

import com.example.zydemo.file.repository.entity.FileInfoEntity;
import com.example.zydemo.file.service.FileInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @Description:
 * @Author: zhaoyi18
 * @Date: 2022/09/28 10:04
 * @Since jdk8+
 **/
@Log4j2
@Api("文件")
@RestController
public class FileInfoController {
    @Autowired
    private FileInfoService fileInfoService;
    @Value("${file.upload.path}")
    private String filePathCofig;

    @ApiOperation(value = "上传")
    @RequestMapping(value = "/uploadFile", method = {RequestMethod.GET,RequestMethod.POST})
    public FileInfoEntity uploadFile(MultipartFile multipartFile){
        String filePath = filePathCofig;
        FileInfoEntity fileInfoEntity = fileInfoService.uploadFile(multipartFile,filePath);
        return fileInfoEntity;
    }

    @ApiOperation(value = "下载")
    @RequestMapping(value = "/downloadFile", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> downloadFile(@RequestParam String id)
            throws IOException {
        return fileInfoService.downloadFile(id);
    }


    @ApiOperation(value = "通过responset下载")
    @RequestMapping(value = "/downloadFileByResponse", method = RequestMethod.GET)
    public void downloadFileByResponse(@RequestParam String id, HttpServletResponse response) throws IOException {
        fileInfoService.downloadFileByResponse(id,response);
    }

    @ApiOperation(value = "word转pdf")
    @RequestMapping(value = "/wordTurnBytePdf", method = RequestMethod.GET)
    public String wordTurnBytePdf() throws IOException {
        String filePath = "E:\\xiazai\\f24744c1-576e-4830-8866-faff4467f863.docx";
        return fileInfoService.wordTurnBytePdf(filePath);
    }

    @ApiOperation(value = "删除")
    @RequestMapping(value = "/dropFile", method = RequestMethod.POST)
    public void dropFile(@RequestBody List<String> filePathList) {
        fileInfoService.dropFile(filePathList);
    }

    @ApiOperation(value = "excel导出")
    @RequestMapping(value = "/exportExcel", method = RequestMethod.POST)
    public void exportExcel(HttpServletResponse response) {
        fileInfoService.exportExcel(response);
    }

    @ApiOperation(value = "excel导入")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public void importExcel(MultipartFile file) {
        fileInfoService.importExcel(file);
    }


}
