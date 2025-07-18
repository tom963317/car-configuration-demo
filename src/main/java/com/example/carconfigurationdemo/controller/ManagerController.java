package com.example.carconfigurationdemo.controller;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.example.carconfigurationdemo.vo.excel.Manager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ManagerController {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/manager/list")
    @ResponseBody
    public Map<String, Object> managerList(int page, int limit, String enterUser) {
        String countSql = "SELECT count(*) FROM public.supplier_manage WHERE 1=1";
        if (enterUser != null && !"".equals(enterUser)) {
            countSql += " and enter_user = '" + enterUser + "'";
        }
        Integer count = jdbcTemplate.queryForObject(countSql, Integer.class);

        String sql = "SELECT * FROM public.supplier_manage WHERE 1=1";
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

    @PostMapping("/manager/add")
    @ResponseBody
    public void insertManager(@RequestParam("file") MultipartFile file, @RequestParam String enterUser) throws Exception{
        InputStream inputStream = file.getInputStream();
        List<Manager> managerList = EasyExcel.read(inputStream)
                .excelType(ExcelTypeEnum.XLSX)
                .head(Manager.class)
                .sheet()
                .doReadSync();
        String sql = "INSERT INTO public.supplier_manage (supplier, manager, enter_user, create_time, update_time)" +
                " VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Manager manager = managerList.get(i);
                ps.setString(1, manager.getSupplier());
                ps.setString(2, manager.getScm());
                ps.setString(3, enterUser);
                ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            }
            @Override
            public int getBatchSize() {
                return managerList.size();
            }
        });
    }

}
