package com.example.zydemo.utils;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;

/**
 * @Description:
 * @Author: zhaoyi18
 * @Date: 2022/10/19 18:15
 * @Since jdk8+
 **/
@Log4j2
@Component
public class CommonUtil {

 /*   public List<Tree> transitionTree(List<String> parentIdList, List<Tree> list){
        List<Tree> treeList = list.stream()
                .filter(l -> parentIdList.contains(l.getParentId()))
                .peek(l -> {
                    List<Tree> children = l.getChildren();
                    if (children == null) {
                        children = new ArrayList<>();
                    }
                    children.add(l);
                    l.setChildren(transitionTree(Collections.singletonList(l.getId()), list));
                })
                .collect(Collectors.toList());

        return treeList;
    }*/

    public String encryptToBase64(String urlString) throws IOException {
        // 构造URL
        URL url = new URL(urlString);
        // 打开连接
        URLConnection con = url.openConnection();
        //设置请求超时为5s
        con.setConnectTimeout(10 * 1000);
        // 输入流
        InputStream is = con.getInputStream();
        // 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        byte[] data = null;
        // 读取图片字节数组
        try {
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            byte[] buff = new byte[100];
            int rc = 0;
            while ((rc = is.read(buff, 0, 100)) > 0) {
                swapStream.write(buff, 0, rc);
            }
            data = swapStream.toByteArray();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return Base64.getEncoder().encodeToString(data);
    }
}
