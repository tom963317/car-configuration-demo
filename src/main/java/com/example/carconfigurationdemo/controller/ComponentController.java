package com.example.carconfigurationdemo.controller;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.example.carconfigurationdemo.vo.excel.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

@RestController
public class ComponentController {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/component/list")
    @ResponseBody
    public Map<String, Object> componentList(int page, int limit, String enterUser) {
        String countSql = "SELECT count(*) FROM public.component WHERE 1=1";
        if (enterUser != null && !"".equals(enterUser)) {
            countSql += " and enter_user = '" + enterUser + "'";
        }
        Integer count = jdbcTemplate.queryForObject(countSql, Integer.class);

        String sql = "SELECT * FROM public.component WHERE 1=1";
        if (enterUser != null && !"".equals(enterUser)) {
            sql += " and enter_user = '" + enterUser + "'";
        }

        sql += " LIMIT ? OFFSET ?";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql, limit, (page - 1) * limit);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "");
        result.put("count", count);
        result.put("data", maps);
        return result;
    }

    @PostMapping("/component/add")
    @ResponseBody
    public void insertComponent(@RequestParam("file") MultipartFile file, @RequestParam String enterUser) throws Exception{
        InputStream inputStream = file.getInputStream();
        List<Component> componentList = EasyExcel.read(inputStream)
                .excelType(ExcelTypeEnum.XLSX)
                .head(Component.class)
                .sheet()
                .doReadSync();
        String sql = "INSERT INTO public.component" +
                "(component_number, component_name, supplier, enter_user, create_time, update_time)" +
                " VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Component component = componentList.get(i);
                ps.setString(1, component.getComponentNumber());
                ps.setString(2, component.getComponentName());
                ps.setString(3, component.getSupplier());
                ps.setString(4, enterUser);
                ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
                ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            }
            @Override
            public int getBatchSize() {
                return componentList.size();
            }
        });
    }

}
