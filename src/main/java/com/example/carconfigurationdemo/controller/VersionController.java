package com.example.carconfigurationdemo.controller;

import com.example.carconfigurationdemo.util.FileUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

@RestController
public class VersionController {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/version/list")
    @ResponseBody
    public Map<String, Object> getVersionData(@RequestParam(required = false) String version_no,
                                              @RequestParam(required = false) String component_number,
                                              @RequestParam(required = false) String supplier,
                                              @RequestParam(required = false) String manager, int page, int limit) {
        StringBuilder countSql = new StringBuilder("SELECT count(*) FROM public.version WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (version_no != null && !version_no.isEmpty()) {
            countSql.append(" AND version_no LIKE ?");
            params.add("%" + version_no + "%");
        }

        if (component_number != null && !component_number.isEmpty()) {
            countSql.append(" AND component_number LIKE ?");
            params.add("%" + component_number + "%");
        }

        if (supplier != null && !supplier.isEmpty()) {
            countSql.append(" AND supplier LIKE ?");
            params.add("%" + supplier + "%");
        }

        if (manager != null && !manager.isEmpty()) {
            countSql.append(" AND manager LIKE ?");
            params.add("%" + manager + "%");
        }

        Integer count = jdbcTemplate.queryForObject(countSql.toString(), params.toArray(), Integer.class);
        StringBuilder sql = new StringBuilder("SELECT * FROM public.version WHERE 1=1");
        List<Object> dataParams = new ArrayList<>();
        if (version_no != null && !version_no.isEmpty()) {
            sql.append(" AND version_no LIKE ?");
            dataParams.add("%" + version_no + "%");
        }

        if (component_number != null && !component_number.isEmpty()) {
            sql.append(" AND component_number LIKE ?");
            dataParams.add("%" + component_number + "%");
        }

        if (supplier != null && !supplier.isEmpty()) {
            sql.append(" AND supplier LIKE ?");
            dataParams.add("%" + supplier + "%");
        }

        if (manager != null && !manager.isEmpty()) {
            sql.append(" AND manager LIKE ?");
            dataParams.add("%" + manager + "%");
        }
        sql.append(" LIMIT ? OFFSET ?");
        dataParams.add(limit);
        dataParams.add((page - 1) * limit);
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql.toString(), dataParams.toArray());
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "");
        result.put("count", count);
        result.put("data", maps);
        return result;
    }


    @GetMapping("/version/downloadExcel")
    public void downloadExcel(
            @RequestParam(required = false) String version_no,
            @RequestParam(required = false) String component_number,
            @RequestParam(required = false) String supplier,
            @RequestParam(required = false) String manager,
            HttpServletResponse response
    ) throws Exception {
        StringBuilder sql = new StringBuilder("SELECT * FROM public.version WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (version_no != null && !version_no.isEmpty()) {
            sql.append(" AND version_no LIKE ?");
            params.add("%" + version_no + "%");
        }

        if (component_number != null && !component_number.isEmpty()) {
            sql.append(" AND component_number LIKE ?");
            params.add("%" + component_number + "%");
        }

        if (supplier != null && !supplier.isEmpty()) {
            sql.append(" AND supplier LIKE ?");
            params.add("%" + supplier + "%");
        }

        if (manager != null && !manager.isEmpty()) {
            sql.append(" AND manager LIKE ?");
            params.add("%" + manager + "%");
        }

        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql.toString(), params.toArray());
        System.out.println(maps);
        List<List<String>> dataList = new ArrayList<>();
        for (Map<String, Object> map : maps) {
            List<String> list = new ArrayList<>();
            list.add(map.get("version_no").toString());
            list.add(map.get("component_number").toString());
            list.add(map.get("supplier").toString());
            list.add(map.get("manager").toString());

            String allComponentQuantity = map.get("all_component_quantity").toString();
            String[] split = allComponentQuantity.split(",");
            list.addAll(Arrays.asList(split));
            dataList.add(list);
        }

        FileUtil.writeExcel(dataList, response, maps.get(0).get("start_time").toString(), "history.xlsx");
    }

    @PostMapping("/version/add")
    @ResponseBody
    public void addVersion(@RequestParam("file") MultipartFile file, HttpServletResponse response, @RequestParam String enterUser) throws Exception {
        List<List<String>> inputList = FileUtil.readExcel1(file.getInputStream());
        List<String> headList = inputList.get(0);
        String startTime = headList.get(2);
        List<List<String>> dataList = new ArrayList<>();

        for (int i = 1; i < inputList.size(); i++) {
            List<String> rowData = inputList.get(i);
            String sql = "select a.optional_code,a.component_number,b.component_name ,a.component_quantity,b.supplier,c.manager,a.start_time,a.end_time from optional a " +
                    "left join component b on a.component_number = b.component_number " +
                    "left join supplier_manage c on b.supplier = c.supplier " +
                    "where 1 = 1 " +
                    "and a.optional_code = ?";
            List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql, rowData.get(1));
            for (Map<String, Object> map : maps) {
                List<String> list = new ArrayList<>();
                list.add(rowData.get(0));
                list.add(map.get("component_number").toString());
                list.add(map.get("component_name") == null ? "" : map.get("component_name").toString());
                list.add(map.get("supplier") == null ? "" : map.get("supplier").toString());
                list.add(map.get("manager") == null ? "" : map.get("manager").toString());

                String startT = map.get("start_time").toString();
                LocalDate st = LocalDate.parse(startT);

                String endT = map.get("end_time").toString();
                LocalDate en = LocalDate.parse(endT);

                Integer componentQuantity = Integer.parseInt(map.get("component_quantity").toString());
                for (int j = 2; j < rowData.size(); j++) {
                    LocalDate localDate = LocalDate.parse(headList.get(j));
                    int result1 = localDate.compareTo(st);
                    int result2 = localDate.compareTo(en);
                    if (result1 >= 0 && result2 <= 0) {
                        Double aDouble = Double.parseDouble(rowData.get(j));
                        list.add(componentQuantity * aDouble + "");
                    } else {
                        list.add("0");
                    }

                }
                dataList.add(list);
            }
        }

        Map<String, List<String>> mergedMap = new LinkedHashMap<>();

        for (List<String> row : dataList) {
            String key = row.get(1);

            if (!mergedMap.containsKey(key)) {
                mergedMap.put(key, new ArrayList<>(row)); // 初次添加
            } else {
                List<String> existing = mergedMap.get(key);
                for (int i = 5; i < row.size(); i++) {
                    Double oldVal = Double.parseDouble(existing.get(i));
                    Double newVal = Double.parseDouble(row.get(i));
                    existing.set(i, (oldVal + newVal) + "");
                }
            }
        }

        // 输出结果
        List<List<String>> mergedList = new ArrayList<>(mergedMap.values());
        System.out.println(mergedList);
        String versionNo = mergedList.get(0).get(0);
        String deleteSql = "DELETE FROM public.version WHERE version_no=?";
        jdbcTemplate.update(deleteSql, versionNo);

        String sql = "INSERT INTO public.version (version_no, component_number, supplier, manager, all_component_quantity," +
                " start_time, enter_user, create_time, update_time) VALUES " +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                List<String> list = mergedList.get(i);
                ps.setString(1, list.get(0));
                ps.setString(2, list.get(1));
                ps.setString(3, list.get(3));
                ps.setString(4, list.get(4));
                String quantityStr = "";
                for (int j = 5; j < list.size(); j++) {
                    quantityStr = quantityStr + list.get(j) + ",";
                }
                ps.setString(5, quantityStr);
                ps.setDate(6, Date.valueOf(LocalDate.parse(startTime)));
                ps.setString(7, enterUser);
                ps.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
                ps.setTimestamp(9, new Timestamp(System.currentTimeMillis()));
            }

            @Override
            public int getBatchSize() {
                return mergedList.size();
            }
        });
        FileUtil.writeExcel(mergedList, response, startTime, "output.xlsx");
    }

}
