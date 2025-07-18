package com.example.carconfigurationdemo.controller;

import com.example.carconfigurationdemo.util.FileUtil;
import com.example.carconfigurationdemo.util.StringUtil;
import com.example.carconfigurationdemo.vo.Input;
import com.example.carconfigurationdemo.vo.excel.Optional;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.*;

@RestController
public class ComponentController {
    @Autowired
    private JdbcTemplate jdbcTemplate;


}
