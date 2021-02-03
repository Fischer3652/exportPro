package com.example.demo.controller;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.UUID;

/**
 * base64图片上传
 */
@RestController
public class Base64Controller {

    @RequestMapping(value = "/upload")
    public void upload(HttpServletRequest request) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        String base = sb.toString();
        if (!StringUtils.isEmpty(base)) {
            OutputStream out = null;
            try {
                String base64 = base.split(",")[1];
                byte[] bytes = new BASE64Decoder().decodeBuffer(base64);
                for (int i = 0; i < bytes.length; ++i) {
                    if (bytes[i] < 0) {
                        bytes[i] += 256;
                    }
                }
                String fileName = UUID.randomUUID().toString().replaceAll("-", "")+".jpg";
                String realPath = request.getSession().getServletContext().getRealPath("")+ "/normalFiles/workOrderAttach/";
                File file = new File(realPath);
                if (!file.exists() || !file.isDirectory()) {
                    file.mkdirs();
                }
                out = new FileOutputStream(realPath + "/" + fileName);
                out.write(bytes);
                out.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                if (out != null) {
                    out.close();
                }
            }
        }
    }

}
