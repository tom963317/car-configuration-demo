package com.example.carconfigurationdemo.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.example.carconfigurationdemo.vo.excel.Component;
import com.example.carconfigurationdemo.vo.excel.Manager;
import com.example.carconfigurationdemo.vo.excel.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

@RestController
public class InsertController {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/insertManager")
    @ResponseBody
    public void insertManager() throws Exception{
        InputStream inputStream = new ClassPathResource("excel/manager.xlsx").getInputStream();
        List<Manager> managerList = EasyExcel.read(inputStream)
                .excelType(ExcelTypeEnum.XLSX)
                .head(Manager.class)
                .sheet()
                .doReadSync();
        String sql = "INSERT INTO public.supplier_manage (supplier, manager) VALUES (?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Manager manager = managerList.get(i);
                ps.setString(1, manager.getSupplier());
                ps.setString(2, manager.getScm());
            }
            @Override
            public int getBatchSize() {
                return managerList.size();
            }
        });
    }

    @GetMapping("/insertOptional")
    @ResponseBody
    public void insertOptional() throws Exception{
        InputStream inputStream = new ClassPathResource("excel/optional.xlsx").getInputStream();
        List<Optional> optionalList = EasyExcel.read(inputStream)
                .excelType(ExcelTypeEnum.XLSX)
                .head(Optional.class)
                .sheet()
                .doReadSync();
        String sql = "INSERT INTO public.optional (optional_code, component_number, component_quantity, start_time, end_time) VALUES " +
                "(?, ?, ?, ?, ?)";
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
            }
            @Override
            public int getBatchSize() {
                return optionalList.size();
            }
        });
    }

    @GetMapping("/insertComponent")
    @ResponseBody
    public void insertComponent() throws Exception{
        InputStream inputStream = new ClassPathResource("excel/component.xlsx").getInputStream();
        List<Component> componentList = EasyExcel.read(inputStream)
                .excelType(ExcelTypeEnum.XLSX)
                .head(Component.class)
                .sheet()
                .doReadSync();
        String sql = "INSERT INTO public.component" +
                "(component_number, component_name, supplier) VALUES (?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Component component = componentList.get(i);
                ps.setString(1, component.getComponentNumber());
                ps.setString(2, component.getComponentName());
                ps.setString(3, component.getSupplier());
            }
            @Override
            public int getBatchSize() {
                return componentList.size();
            }
        });
    }
}
