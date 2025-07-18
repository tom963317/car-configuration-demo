package com.example.carconfigurationdemo.controller;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.example.carconfigurationdemo.vo.excel.Component;
import com.example.carconfigurationdemo.vo.excel.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class OptionalController {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/optional/list")
    @ResponseBody
    public Map<String, Object> optionalList(int page, int limit, String enterUser) {
        String countSql = "SELECT count(*) FROM public.optional WHERE 1=1";
        if (enterUser != null && !"".equals(enterUser)) {
            countSql += " and enter_user = '" + enterUser + "'";
        }
        Integer count = jdbcTemplate.queryForObject(countSql, Integer.class);

        String sql = "SELECT * FROM public.optional WHERE 1=1";
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

    @PostMapping("/optional/add")
    @ResponseBody
    public void insertOptional(@RequestParam("file") MultipartFile file, @RequestParam String enterUser) throws Exception{
        InputStream inputStream = file.getInputStream();
        List<Optional> optionalList = EasyExcel.read(inputStream)
                .excelType(ExcelTypeEnum.XLSX)
                .head(Optional.class)
                .sheet()
                .doReadSync();
        String sql = "INSERT INTO public.optional (optional_code, component_number, component_quantity, " +
                "start_time, end_time, enter_user, create_time, update_time) VALUES " +
                "(?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Optional optional = optionalList.get(i);
                ps.setString(1, optional.getOption());
                ps.setString(2, optional.getComponentNumber());
                ps.setInt(3, optional.getQuantity());
//                ps.setDate(4, (Date) optional.getEngineeringEffective());
//                ps.setDate(5, (Date) optional.getPlantEffectivtyOut());
                if (optional.getEngineeringEffective() != null) {
                    ps.setDate(4, new java.sql.Date(optional.getEngineeringEffective().getTime()));
                } else {
                    ps.setNull(4, Types.DATE);
                }

                if (optional.getPlantEffectivtyOut() != null) {
                    ps.setDate(5, new java.sql.Date(optional.getPlantEffectivtyOut().getTime()));
                } else {
                    ps.setNull(5, Types.DATE);
                }
                ps.setString(6, enterUser);
                ps.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
                ps.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
            }
            @Override
            public int getBatchSize() {
                return optionalList.size();
            }
        });
    }

}
