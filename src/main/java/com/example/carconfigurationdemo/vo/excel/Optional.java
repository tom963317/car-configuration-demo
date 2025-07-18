package com.example.carconfigurationdemo.vo.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Optional {
    @ExcelProperty("option")
    private String option;

    @ExcelProperty("component_number")
    private String componentNumber;

    @ExcelProperty("component_name")
    private String componentName;

    @ExcelProperty("quantity")
    private Integer quantity;

    @ExcelProperty("PlantEffectivtyOut")
    private Date plantEffectivtyOut;

    @ExcelProperty("EngineeringEffective")
    private Date EngineeringEffective;
}
