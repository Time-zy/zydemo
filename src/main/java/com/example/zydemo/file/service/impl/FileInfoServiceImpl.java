package com.example.zydemo.file.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.StyleSet;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.zydemo.file.mapper.FileInfoMapper;
import com.example.zydemo.file.repository.entity.FileInfoEntity;
import com.example.zydemo.file.service.FileInfoService;
import com.google.code.appengine.awt.Color;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.BaseFont;
import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import fr.opensagres.xdocreport.itext.extension.font.IFontProvider;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.tomcat.util.http.fileupload.FileUpload;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: zhaoyi18
 * @Date: 2022/10/08 11:25
 * @Since jdk8+
 **/
@Log4j2
@Service
public class FileInfoServiceImpl extends ServiceImpl<FileInfoMapper, FileInfoEntity> implements FileInfoService {


    @Override
    public FileInfoEntity uploadFile(MultipartFile multipartFile,String filePath) {
        UUID uuid = UUID.randomUUID();

        File file = new File(filePath);
        if (!file.exists()) {
            Boolean fileBoolean = file.mkdir();
        }

        FileInfoEntity fileInfoEntity = new FileInfoEntity();
        String originalFilename = multipartFile.getOriginalFilename();
        assert originalFilename != null;
        String fileType = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uuidFileName = uuid + fileType;

        File uploadFile = new File(filePath + uuidFileName);
        //将上传文件保存到路径
        try {
            multipartFile.transferTo(uploadFile);
            fileInfoEntity.setId(uuid.toString());
            fileInfoEntity.setFileCode(uuid.toString());
            fileInfoEntity.setFileName(originalFilename);
            fileInfoEntity.setFilePath(filePath + uuidFileName);
            baseMapper.insert(fileInfoEntity);
        } catch (Exception e) {
            log.error("文件上传异常", e);
        }

        return fileInfoEntity;
    }

    @Override
    public ResponseEntity<InputStreamResource> downloadFile(String id) throws IOException {
        //读取文件
        FileInfoEntity fileInfoEntity = baseMapper.selectById(id);
        if (fileInfoEntity == null) {
            return null;
        }
        String filePath = fileInfoEntity.getFilePath();
        FileSystemResource file = new FileSystemResource(filePath);
        //设置响应头
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", new String(fileInfoEntity.getFileName().getBytes("GBK"), StandardCharsets.ISO_8859_1)));
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(file.contentLength())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new InputStreamResource(file.getInputStream()));
    }

    @Override
    public void downloadFileByResponse(String id, HttpServletResponse response) throws IOException {

        //读取文件
        FileInfoEntity fileInfoEntity = baseMapper.selectById(id);
        if (fileInfoEntity == null) {
            return;
        }
        String filePath = fileInfoEntity.getFilePath();

        InputStream inputStream = new FileInputStream(filePath);
        response.reset();
        //下载
        response.setContentType("application/octet-stream");
        response.addHeader("Content-Disposition", "attachment; filename=" + new String(fileInfoEntity.getFileName().getBytes("GBK"), StandardCharsets.ISO_8859_1));

        //预览
        response.addHeader("Content-Disposition", "inline; filename=" + new String(fileInfoEntity.getFileName().getBytes("GBK"), StandardCharsets.ISO_8859_1));

        ServletOutputStream outputStream = response.getOutputStream();
        byte[] b = new byte[1024];
        int len;
        while ((len = inputStream.read(b)) > 0) {
            outputStream.write(b, 0, len);
        }
        inputStream.close();

    }

    @Override
    public String wordTurnBytePdf(String filePath) throws IOException {

        boolean docFileBool = false;

        String pdfPath = "";
        FileInputStream in = null;
        FileOutputStream fileOutputStream = null;
        try {

            String fileType = filePath.substring(filePath.lastIndexOf("."));
            if (".doc".equals(fileType)) {
                docFileBool = true;
                String docFilePath = filePath;
                Document document = new Document();
                document.loadFromFile(docFilePath);
                filePath = filePath + "x";
                document.saveToFile(filePath, FileFormat.Docx);
            }

            XWPFDocument document = null;
            File file = new File(filePath);

            in = new FileInputStream(file);
            //读取word
            document = new XWPFDocument(in);

            PdfOptions pdfOptions = PdfOptions.create();

            //中文字体处理
            pdfOptions.fontProvider(new IFontProvider() {
                @Override
                public Font getFont(String familyName, String encoding, float size, int style, Color color) {
                    try {
                        BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
                        Font fontChinese = new Font(bfChinese, size, style, color);
                        if (familyName != null) {
                            fontChinese.setFamily(familyName);
                        }
                        return fontChinese;
                    } catch (Exception e) {
                        log.error(e);
                        return null;
                    }
                }
            });

            String fileName = file.getName();
            String substring = fileName.substring(0, fileName.lastIndexOf("."));
            String folderPath = filePath.substring(0,filePath.lastIndexOf(substring))+"pdf\\";
            File filePreviewPathExist = new File(folderPath);
            if (!filePreviewPathExist.exists()) {
                Boolean fileBoolean = filePreviewPathExist.mkdir();
            }
            pdfPath = folderPath+substring+".pdf";
            fileOutputStream = new FileOutputStream(pdfPath);
            PdfConverter.getInstance().convert(document, fileOutputStream, pdfOptions);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                in.close();
            }
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        }

        if (docFileBool) {
            FileSystemUtils.deleteRecursively(new File(filePath));
        }

        //检测pdf是否生成
        File fileExist = new File(pdfPath);
        if(fileExist.exists()){
            return pdfPath;
        }else {
            return "";
        }
    }

    @Override
    public void dropFile(List<String> filePathList) {

        for (String filePath : filePathList) {
            FileSystemUtils.deleteRecursively(new File(filePath));
        }

    }

    @Override
    public void exportExcel(HttpServletResponse response) {
//        FileInfoEntity fileInfoEntity = new FileInfoEntity();
//        List<FileInfoEntity> fileInfoEntityList = new ArrayList<>();
//        fileInfoEntityList.add(fileInfoEntity);
//        ExcelWriter writer = ExcelUtil.getWriter(true);
//        //设置单元格格式
//        StyleSet style = writer.getStyleSet();
//        CellStyle cellStyle = style.getCellStyle();
//
//        OutputStream out = null;
//        try {
//            //自定义列名
//            writer.addHeaderAlias("cell", "列名");
//            writer.setOnlyAlias(true);
//            String fileName = "基本信息";
//            //vnd.openxmlformats-officedocument.spreadsheetml.sheet --xlsx ; application/vnd.ms-excel --xls
//            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");
//            //test.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
//            response.setHeader("Content-Disposition",
//                    "attachment;filename=" + new String(fileName.getBytes(), StandardCharsets.ISO_8859_1) + ".xlsx");
//            response.setHeader("Pragma", "no-cache");
//            response.setHeader("Cache-Control", "no-cache");
//            response.setDateHeader("Expires", 0);
//            writer.write(fileInfoEntityList, true);
//            out = response.getOutputStream();
//            writer.flush(out, true);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally {
//            writer.close();
//            IoUtil.close(out);
//        }



        //数据（从数据库查出）
        List<FileInfoEntity> fileInfoEntityList = new ArrayList<>();

        //定义基础数据
        List<String> rowHead = CollUtil.newArrayList("列1","列2");
        //通过ExcelUtil.getBigWriter()创建Writer对象，BigExcelWriter用于大数据量的导出，不会引起溢出；
        ExcelWriter writer = ExcelUtil.getWriter();
        //写入标题
        writer.writeHeadRow(rowHead);
        //命名sheet页
        writer.renameSheet("sheet");

        ServletOutputStream out = null;
        //实现核心逻辑
        try {
            //定义容器保存数据
            List<List<Object>> rows = new LinkedList<>();
            //按照指定属性进行分组
            LinkedHashMap<String, List<FileInfoEntity>> map = fileInfoEntityList.stream().collect(Collectors.groupingBy(FileInfoEntity::getFileName,
                    LinkedHashMap::new, Collectors.toList()));
            //定义起始行（方便分组后合并时从哪一行开始）
            //因为标题已经占了一行，所以数据从第二行开始写（excel第一行索引为0）
            int index = 1;
            int index1 = 1;
            //遍历按属性分组后的list（用entrySet效率比keySet效率高）
            for (Map.Entry<String, List<FileInfoEntity>> listEntry : map.entrySet()) {
                //获取按属性分组后的集合
                List<FileInfoEntity> value = listEntry.getValue();
                //计算此集合的长度
                int size = value.size();
                //如果只有一行数据不能调用merge方法合并数据，否则会报错
                if (size == 1){
                    index += size;
                    index1 += size;
                }else{
                    //根据指定属性进行合并单元格
                    //合并行，第一个参数是合并行的开始行号（行号从0开始），第二个参数是合并行的结束行号，第三个参数是合并的列号开始(列号从0开始)，
                    //第四个参数是合并的列号结束，第五个参数是合并后的内容，null不设置，第六个参数指是否支持设置样式，true指的是。
                    writer.merge(index, index + size - 1, 0, 0, null, true);
                    //合并完后起始索引移到下一个合并点
                    index += size;

                    //按照属性2进行分组
                    LinkedHashMap<String, List<FileInfoEntity>> map1 = fileInfoEntityList.stream().collect(Collectors.groupingBy(FileInfoEntity::getFileCode,
                            LinkedHashMap::new, Collectors.toList()));
                    for (Map.Entry<String, List<FileInfoEntity>> listEntry1 : map1.entrySet()) {
                        List<FileInfoEntity> value1 = listEntry1.getValue();
                        int size1 = value1.size();
                        if (size1 == 1){
                            index1 += size1;
                        }else{
                            //合并小组
                            writer.merge(index1, index1 + size1 - 1, 2, 2, null, true);
                            index1 += size1;

                        }
                    }
                }
                //保存数据
                value.forEach(
                        sList->{
                            List<Object> rowA = null;
                            rowA = CollUtil.newArrayList(
                                    sList.getFileName()
                            );
                            rows.add(rowA);
                        }
                );
            }
            //导出数据
            // 一次性写出内容，使用默认样式，强制输出标题
            writer.write(rows, true);
            //response为HttpServletResponse对象
            // application/vnd.openxmlformats-officedocument.spreadsheetml.sheet --xlsx ; application/vnd.ms-excel --xls
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            //test.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
            response.setHeader("Content-Disposition",
                    "attachment;filename="+ new String("文件名".getBytes(), StandardCharsets.ISO_8859_1) +".xls");
            out = response.getOutputStream();
            writer.flush(out, true);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关闭输出Servlet流
            IoUtil.close(out);
            //关闭writer，释放内存
            writer.close();
        }
    }

    @Override
    public String importExcel(MultipartFile file) {

        String str = "";

        // 获取上传文件输入流
        InputStream inputStream = null;
        Supplier<String> stringSupplier = String::new;
        try {
            inputStream = file.getInputStream();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        //应用HUtool ExcelUtil获取ExcelReader指定输入流和sheet
        ExcelReader excelReader = ExcelUtil.getReader(inputStream, "sheet");
        //可以加上表头验证
        //读取第二行到最后一行数据
        List<List<Object>> read = excelReader.read(2, excelReader.getRowCount());
        for (List<Object> objects : read) {
            str =  ObjectUtils.toString(objects.get(0),stringSupplier);
        }

        return str;
    }


}
