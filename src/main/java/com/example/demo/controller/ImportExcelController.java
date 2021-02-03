package com.example.demo.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jxl.Sheet;
import jxl.Workbook;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
public class ImportExcelController {

    @RequestMapping(value = "/importExcel")
    public void importExcel(@RequestParam("file") MultipartFile multipartFile) {

        try {
            Workbook workbook = Workbook.getWorkbook(multipartFile.getInputStream());
            Sheet sheet = workbook.getSheet(0);
            List<List<String>> strList = new ArrayList<>();
            int row = sheet.getRows();
            for (int i = 1; i < row; i++) {
                List<String> list = new ArrayList<>();
                String name = sheet.getCell(0, i).getContents().trim();
                String age = sheet.getCell(1, i).getContents().trim();
                String sex = sheet.getCell(2, i).getContents().trim();
                list.add(name);
                list.add(age);
                list.add(sex);
                strList.add(list);
            }
            System.out.println(strList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/importCaseinfo")
    public void importCaseinfo(@RequestParam("file") MultipartFile multipartFile) {
        try {
            Workbook workbook = Workbook.getWorkbook(multipartFile.getInputStream());
            Sheet sheet = workbook.getSheet(0);
            int row = sheet.getRows();
            System.out.println(row);
            JsonObject jsonObject = new JsonObject();
            JsonArray array = new JsonArray();
            for (int i = 1; i < row; i++) {
                JsonObject object = new JsonObject();
                String caseno = sheet.getCell(0, i).getContents().trim();
                String deptName = sheet.getCell(1, i).getContents().trim();
                String gridName = sheet.getCell(2, i).getContents().trim();
                String origin = sheet.getCell(3, i).getContents().trim();
                String location = sheet.getCell(4, i).getContents().trim();
                String problem = sheet.getCell(5, i).getContents().trim();
                String reportTime = sheet.getCell(6, i).getContents().trim();
                String reportPerson = sheet.getCell(7, i).getContents().trim();
                String reportPersonPhone = sheet.getCell(8, i).getContents().trim();
                String bigClass = sheet.getCell(9, i).getContents().trim();
                String smallClass = sheet.getCell(10, i).getContents().trim();
                String disposeResult = sheet.getCell(11, i).getContents().trim();
                String finishTime = sheet.getCell(12, i).getContents().trim();
                String longitude = sheet.getCell(13, i).getContents().trim();
                String latitude = sheet.getCell(14, i).getContents().trim();
                object.addProperty("caseguid", new Random().nextInt(1000000));
                object.addProperty("caseno", caseno);
                object.addProperty("deptName", deptName);
                object.addProperty("gridName", gridName);
                object.addProperty("originId", "cg");
                object.addProperty("location", location);
                object.addProperty("problem", problem);
                object.addProperty("reportTime", reportTime);
                object.addProperty("reportPersonName", reportPerson);
                object.addProperty("reportPersonPhone", reportPersonPhone);
                object.addProperty("bigClass", bigClass);
                object.addProperty("smallClass", smallClass);
                object.addProperty("disposeResult", disposeResult);
                object.addProperty("finishTime", finishTime);
                object.addProperty("longitude", longitude);
                object.addProperty("latitude", latitude);
                array.add(object);
            }
            jsonObject.addProperty("token", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiLlpKfkuK3ooZfpgZPnrqHnkIblkZgiLCJpZCI6ImZmODA4MDgxNzJkMWUyZWYwMTcyZDJmMjE4MzgwMDAyIiwiZXhwIjoxNjA1MzU5NjU1LCJ1c2VySWQiOiJkemFkbWluIiwiaWF0IjoxNjA1MzE2NDU1LCJkZXB0Q29kZSI6IkQwMDEwMDQwMDMwMDEiLCJqdGkiOiI5NTU0YjYzMy1kODQyLTQ1MDMtOTAyNC00OTE2NWMzYWE4YTkifQ.ghNQTW5gnoekb1gAeJxfHBAuw0VChTAfa9qJJCBcopY");
            jsonObject.add("datas", array);
            String url = "http://117.80.114.201:8087/wgh/app/outOrderSync.action";
            System.out.println(jsonObject.toString());
            System.out.println(postJson(url, jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //post
    public static String postJson(String url, String jsonString) {
        String result = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        CloseableHttpResponse response = null;
        try {
            post.setHeader("Content-Type", "application/json");
            if (null != jsonString) {
                post.setEntity(new ByteArrayEntity(jsonString.getBytes(StandardCharsets.UTF_8)));
            }
            response = httpClient.execute(post);
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                result = entityToString(entity);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String entityToString(HttpEntity entity) throws IOException {
        String result = null;
        if (entity != null) {
            long length = entity.getContentLength();
            if (length != -1 && length < 2048) {
                result = EntityUtils.toString(entity, "UTF-8");
            } else {
                try (InputStreamReader reader1 = new InputStreamReader(entity.getContent(), StandardCharsets.UTF_8)) {
                    CharArrayBuffer buffer = new CharArrayBuffer(2048);
                    char[] tmp = new char[1024];
                    int l;
                    while ((l = reader1.read(tmp)) != -1) {
                        buffer.append(tmp, 0, l);
                    }
                    result = buffer.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

}
