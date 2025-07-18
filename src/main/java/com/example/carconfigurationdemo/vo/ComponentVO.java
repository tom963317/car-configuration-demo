package com.example.carconfigurationdemo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComponentVO {
    private String componentCode;
    private String componentName;
    private Integer componentQuantity;
}
