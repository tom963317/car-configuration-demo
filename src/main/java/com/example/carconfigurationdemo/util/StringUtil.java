package com.example.carconfigurationdemo.util;

public class StringUtil {
    public static String getNotNullString(String value) {
        if (value == null) {
            return "";
        }
        return value;
    }
}
