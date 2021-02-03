package com.example.demo.controller;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * excel导出
 */
@RestController
@RequestMapping(value = "/excel")
public class ExportExcelController {

    @RequestMapping(value = "/export")
    public void export(HttpServletResponse response) {
        List<String> headlist = new ArrayList<>();
        headlist.add("姓名");
        headlist.add("性别");
        headlist.add("年龄");
        List<List<String>> listList = new ArrayList<>();
        List<String> first = new ArrayList<>();
        first.add("fisher");
        first.add("male");
        first.add("18");
        List<String> second = new ArrayList<>();
        second.add("lily");
        second.add("female");
        second.add("18");
        listList.add(first);
        listList.add(second);
        HSSFWorkbook hwb = this.expExcel(headlist, listList);
        this.outFile(hwb, response);
    }

    public HSSFWorkbook expExcel(List<String> head, List<List<String>> body) {
        //创建一个excel工作簿
        HSSFWorkbook workbook = new HSSFWorkbook();
        //创建一个sheet工作表
        HSSFSheet sheet = workbook.createSheet("身份信息");

        //创建第0行表头，再在这行里在创建单元格，并赋值
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell;
        for (int i = 0; i < head.size(); i++) {
            cell = row.createCell(i);
            cell.setCellValue(head.get(i));//设置值
        }
        //将主体数据填入Excel中
        for (int i = 0; i < body.size(); i++) {
            row = sheet.createRow(i + 1);
            for (int j = 0; j < head.size(); j++) {
                cell = row.createCell(j);
                if (body.get(i).size() > j) {
                    cell.setCellValue(body.get(i).get(j));//设置值
                }
            }
        }
        return workbook;
    }

    public void outFile(HSSFWorkbook workbook, HttpServletResponse response) {

        OutputStream os = null;
        try {
            //防止中文乱码
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String title = "表格" + sdf.format(new Date()) + ".xls";
            String headStr = "attachment; filename=\"" + new String(title.getBytes("gb2312"), "ISO8859-1") + "\"";
            response.setContentType("octets/stream");
            response.setContentType("APPLICATION/OCTET-STREAM");
            response.setHeader("Content-Disposition", headStr);
            os = response.getOutputStream();
            workbook.write(os);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
