package com.example.demo.controller;

import ch.qos.logback.core.util.FileUtil;
import com.example.demo.util.CustomXWPFDocument;
import com.example.demo.util.WordUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * word导出
 */
@RestController
public class ExportWordController {

    @RequestMapping(value = "/export")
    public void export(HttpServletRequest request,HttpServletResponse response) throws Exception{
        WordUtil xwpfTUtil = null;
        OutputStream os = null;
        InputStream is = null;
        List<String> files = new ArrayList();
        for (int i = 0; i < 2; i++) {
            xwpfTUtil = new WordUtil();
            Map<String, Object> params = new HashMap<>();
            params.put("${Name}", "Fisher3652");
            params.put("${Sex}", "男");
            params.put("${Desc}", "18岁\tJAVA开发\r熟悉JVM基本原理");
            params.put("${@Pic}", "C:\\Users\\111\\Pictures\\123.jpg");
            CustomXWPFDocument doc;
            is = FileUtil.class.getClassLoader().getResourceAsStream("static/Demo.docx");
            doc = new CustomXWPFDocument(is);
            xwpfTUtil.replaceInPara(doc, params);
            xwpfTUtil.replaceInTable(doc, params);
            String realPath = request.getSession().getServletContext().getRealPath("/");
            String parentPath = new File(realPath).getParent() + "/table" ;
            File dir = new File(parentPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String fileName = parentPath + "/" + i + ".docx";
            System.out.println(fileName);
            os = new FileOutputStream(new File(fileName));
            files.add(fileName);
            doc.write(os);
            System.out.println("word成功生成");
        }
        xwpfTUtil.close(os);
        xwpfTUtil.close(is);
        os.flush();
        os.close();
        writeZip(files, "文件汇总");

    }

    private void writeZip(List<String> files, String zipname) throws IOException {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = servletRequestAttributes.getResponse();
        String fileName = zipname + ".zip";
        OutputStream os = response.getOutputStream();
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-type", "text/html;charset=UTF-8");
        response.setHeader("content-disposition", "attachment;filename=" + new String(fileName.getBytes("gb2312"), "ISO8859-1"));

        ZipOutputStream zos = new ZipOutputStream(os);
        byte[] buf = new byte[8192];
        int len;
        for (int i = 0; i < files.size(); i++) {
            File file = new File(files.get(i));
            if (!file.isFile()) continue;
            ZipEntry ze = new ZipEntry(file.getName());
            zos.putNextEntry(ze);
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            while ((len = bis.read(buf)) > 0) {
                zos.write(buf, 0, len);
            }
            zos.closeEntry();
        }
        zos.closeEntry();
        zos.close();
    }


}
