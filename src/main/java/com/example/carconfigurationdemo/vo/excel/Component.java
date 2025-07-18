package com.example.carconfigurationdemo.vo.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Component {
    @ExcelProperty("component_number")
    private String componentNumber;

    @ExcelProperty("component_name")
    private String componentName;

    @ExcelProperty("supplier")
    private String supplier;
}
