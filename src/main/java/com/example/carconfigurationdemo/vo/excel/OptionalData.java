package com.example.carconfigurationdemo.vo.excel;

import lombok.Data;

import java.time.LocalDate;

@Data
public class OptionalData {
    private String optionalCode;
    private LocalDate date;
    private Integer value;

    // 构造函数、getter/setter
}