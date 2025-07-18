package com.example.carconfigurationdemo.test;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.example.carconfigurationdemo.vo.excel.OptionalData;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class NoModelListener extends AnalysisEventListener<Map<Integer, String>> {

    private Map<Integer, String> headMap = new HashMap<>(); // index -> 列名
    private List<OptionalData> dataList = new ArrayList<>();

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        this.headMap = headMap;
        System.out.println(headMap.toString());
    }

    @Override
    public void invoke(Map<Integer, String> rowData, AnalysisContext context) {

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        System.out.println("✅ 解析完成，共有记录数：" + dataList.size());
    }

    public List<OptionalData> getDataList() {
        return dataList;
    }

    public static void main(String[] args) throws IOException {
        List<List<String>> dataList = List.of(
                List.of("6307300-DE02", "电动尾翼水管总成", "中信戴卡", "LIANGSS", "100.0", "200.0", "300.0", "400.0", "500.0"),
                List.of("6307300-DE02", "电动尾翼水管总成", "中信戴卡", "LIANGSS", "10.0", "20.0", "30.0", "40.0", "50.0"),
                List.of("6308110-DG01", "撑杆总成", "新民康", "WUCS", "100.0", "100.0", "200.0", "400.0", "100.0")
        );
        Map<String, List<String>> mergedMap = new LinkedHashMap<>();

        for (List<String> row : dataList) {
            String key = row.get(0);

            if (!mergedMap.containsKey(key)) {
                mergedMap.put(key, new ArrayList<>(row)); // 初次添加
            } else {
                List<String> existing = mergedMap.get(key);
                for (int i = 4; i < row.size(); i++) {
                    Double oldVal =Double.parseDouble(existing.get(i));
                    Double newVal =Double.parseDouble(row.get(i));
                    existing.set(i, (oldVal + newVal) + "");
                }
            }
        }

        // 输出结果
        List<List<String>> mergedList = new ArrayList<>(mergedMap.values());

        System.out.println(mergedList);
    }
}


