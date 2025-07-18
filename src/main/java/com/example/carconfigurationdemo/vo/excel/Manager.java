package com.example.carconfigurationdemo.vo.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Manager {
    @ExcelProperty("supplier")
    private String supplier;

    @ExcelProperty("scm")
    private String scm;
}
