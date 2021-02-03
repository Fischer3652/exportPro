package com.example.demo.util;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xwpf.usermodel.*;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordUtil {

    /**
     * 替换段落里面的变量 
     *
     * @param doc    要替换的文档 
     * @param params 参数 
     * @throws FileNotFoundException
     * @throws InvalidFormatException
     */
    public void replaceInPara(CustomXWPFDocument doc, Map<String, Object> params) throws InvalidFormatException, FileNotFoundException {
        Iterator<XWPFParagraph> iterator = doc.getParagraphsIterator();
        XWPFParagraph para;
        while (iterator.hasNext()) {
            para = iterator.next();
            this.replaceInPara(para, params,doc);
        }
    }

    /**
     * 替换段落里面的变量 
     *
     * @param para   要替换的段落 
     * @param params 参数 
     * @throws FileNotFoundException
     * @throws InvalidFormatException
     */
    public void replaceInPara(XWPFParagraph para, Map<String, Object> params, CustomXWPFDocument doc) throws InvalidFormatException, FileNotFoundException {
        List<XWPFRun> runs;
        Matcher matcher;
        if (this.matcher(para.getParagraphText()).find()) {
            runs = para.getRuns();

            int start = -1;
            int end = -1;
            String str = "";
            String text= "";
            for (int i = 0; i < runs.size(); i++) {
                text += runs.get(i).toString();
            }
            for (int i = 0; i < runs.size(); i++) {
                XWPFRun run = runs.get(i);
                System.out.println("------>>>>>>>>>" + text);
                if (text.contains("$")) {
                    start = text.indexOf("$");
                }
                if ((start != -1)) {
                    str += text.substring(text.indexOf("$"), text.length()).trim();
                    String paraList=runs.toString();
                    System.out.println("未删除前"+paraList);
                    Object[] runArr = runs.toArray();
                    int size=runs.size();
                    int $Index=0;
                    for (int j = 0; j < runArr.length; j++) {
                        if (runArr[j].toString().contains("$")) {
                            $Index=j;
                            break;
                        }
                    }
                    int startIn=$Index;
                    while (startIn<runs.size()) {
                        para.removeRun(startIn);
                        System.out.println("删除中"+para.getRuns());
                    }
                    System.out.println("删除后"+para.getRuns());
                }
                if ('}' == text.charAt(text.length() - 1)) {
                    if (start != -1) {
                        end = text.length() - 1;
                        break;
                    }
                }
            }
            System.out.println("start--->"+start);
            System.out.println("end--->"+end);
            System.out.println("str---->>>" + str);

            for (String key : params.keySet()) {
                if (str.equals(key)) {
                    if(str.indexOf("@")==-1){
                        String value= params.get(key).toString();
                        para.createRun().setText(value);
                        break;
                    }else{
                        String value= params.get(key).toString();
                        int length = para.getRuns().size();
                        if (length > 0) {
                            for (int i = (length - 1); i >= 0; i--) {
                                para.removeRun(i);
                            }
                        }
                        String blipId = doc.addPictureData(new FileInputStream(new File(value)), CustomXWPFDocument.PICTURE_TYPE_PNG);
                        doc.createPicture(blipId,doc.getNextPicNameNumber(CustomXWPFDocument.PICTURE_TYPE_PNG), 550, 250,para);
                        break;
                    }
                }
            }


        }
    }

    /**
     * 替换表格里面的变量 
     *
     * @param doc    要替换的文档 
     * @param params 参数 
     * @throws FileNotFoundException
     * @throws InvalidFormatException
     */
    public void replaceInTable(CustomXWPFDocument doc, Map<String, Object> params) throws InvalidFormatException, FileNotFoundException {
        Iterator<XWPFTable> iterator = doc.getTablesIterator();
        XWPFTable table;
        List<XWPFTableRow> rows;
        List<XWPFTableCell> cells;
        List<XWPFParagraph> paras;
        while (iterator.hasNext()) {
            table = iterator.next();
            rows = table.getRows();
            for (XWPFTableRow row : rows) {
                cells = row.getTableCells();
                for (XWPFTableCell cell : cells) {
                    paras = cell.getParagraphs();
                    for (XWPFParagraph para : paras) {
                        this.replaceInPara(para, params,doc);
                    }
                }
            }
        }
    }

    /**
     * 正则匹配字符串 
     *
     * @param str
     * @return
     */
    private Matcher matcher(String str) {
        Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        return matcher;
    }

    /**
     * 关闭输入流 
     *
     * @param is
     */
    public void close(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭输出流 
     *
     * @param os
     */
    public void close(OutputStream os) {
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
