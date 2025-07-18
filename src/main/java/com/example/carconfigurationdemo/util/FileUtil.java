package com.example.carconfigurationdemo.util;

import com.example.carconfigurationdemo.vo.Input;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FileUtil {
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public static List<Input> readExcel(String filePath) throws IOException, ParseException {
//        FileInputStream fis = new FileInputStream(new File(filePath));
        InputStream fis = new ClassPathResource(filePath).getInputStream();

        // 判断是 xls 还是 xlsx
        Workbook workbook;
        if (filePath.endsWith(".xlsx")) {
            workbook = new XSSFWorkbook(fis); // 2007及以后
        } else if (filePath.endsWith(".xls")) {
            workbook = new HSSFWorkbook(fis); // 2003
        } else {
            throw new IllegalArgumentException("不支持的文件类型");
        }

        Sheet sheet = workbook.getSheetAt(0); // 读取第一个 sheet

        List<String> dateList;
        Map<Integer, List<String>> data = new HashMap<>();
        int rowCount = 0;
        for (Row row : sheet) {
            rowCount++;
            List<String> cellList = new ArrayList<>();
            for (Cell cell : row) {
                cellList.add(getCellValue(cell));
            }
            data.put(rowCount, cellList);
        }
        dateList = data.get(1);
        dateList = dateList.subList(1, dateList.size());

        List<Input> inputList = new ArrayList<>();
        for (Integer row : data.keySet()) {
            List<String> value = data.get(row);
            if ("".equals(value.get(0)) || value.get(0).startsWith("optional_code")) {
                continue;
            }
            String optionCode = value.get(0);
            value = value.subList(1, value.size());


            for (int i = 0; i < value.size(); i++) {
                Input input = new Input();
                input.setOptionalCode(optionCode);
                input.setQuantity(Double.parseDouble(value.get(i)));
                input.setDate(sdf.parse(dateList.get(i)));
                inputList.add(input);
            }
        }

        workbook.close();
        fis.close();
        return inputList;
    }

    public static List<List<String>> readExcel1(InputStream fis) throws IOException, ParseException {
//        FileInputStream fis = new FileInputStream(new File(filePath));

        // 判断是 xls 还是 xlsx
        Workbook workbook = new XSSFWorkbook(fis);


        Sheet sheet = workbook.getSheetAt(0); // 读取第一个 sheet

        List<List<String>> data = new ArrayList<>();
        for (Row row : sheet) {
            List<String> cellList = new ArrayList<>();
            for (Cell cell : row) {
                cellList.add(getCellValue(cell));
            }
            data.add(cellList);
        }

        workbook.close();
        fis.close();
        return data;
    }

    private static String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    Date date = cell.getDateCellValue();
                    return sdf.format(date);
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return "";
        }
    }


    public static void writeExcel(List<List<String>> dataList, HttpServletResponse response, String startTime, String excelName) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("输出文件");

        // 表头
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("plan_version");
        header.createCell(1).setCellValue("component_number");
        header.createCell(2).setCellValue("component_name");
        header.createCell(3).setCellValue("supplier");
        header.createCell(4).setCellValue("supplier_chain_managment");
//        header.createCell(5).setCellValue("enter_user");
//        header.createCell(6).setCellValue("enter_time");

//        LocalDate start = LocalDate.of(2025, 8, 1);
        LocalDate start = LocalDate.parse(startTime);

        for (int i = 0; i < 12; i++) {
            LocalDate date = start.plusMonths(i); // 依次往后推
            header.createCell(i + 5).setCellValue(date.format(formatter));

        }
        for (int i = 0; i < 17; i++) {
            sheet.autoSizeColumn(i);
        }

        // 数据行
//        Row row1 = sheet.createRow(1);
//        row1.createCell(0).setCellValue(1);
//        row1.createCell(1).setCellValue("张三");
//        row1.createCell(2).setCellValue(25);
        int rowCount = 0;
        for (List<String> list : dataList) {
            rowCount++;
            Row row = sheet.createRow(rowCount);
            for (int i = 0; i < list.size(); i++) {
                row.createCell(i).setCellValue(list.get(i));
            }
        }


        // 写入文件
//        try (FileOutputStream out = new FileOutputStream("output.xlsx")) {
//            workbook.write(out);
//        }

        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");

        String fileName = URLEncoder.encode(excelName, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);

        // 写入响应流
        workbook.write(response.getOutputStream());
        workbook.close(); // 记得关闭
    }
}
