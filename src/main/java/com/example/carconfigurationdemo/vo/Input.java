package com.example.carconfigurationdemo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Input {
    private String optionalCode;

    private Date date;

    private Double quantity;
}
