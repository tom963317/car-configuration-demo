package com.example.carconfigurationdemo.controller;

import com.example.carconfigurationdemo.util.FileUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

@RestController
public class VersionController {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/version/list")
    @ResponseBody
    public Map<String, Object> getVersionData(@RequestParam(required = false) String version_no,
                                              @RequestParam(required = false) String component_number,
                                              @RequestParam(required = false) String supplier,
                                              @RequestParam(required = false) String manager, int page, int limit) {
        StringBuilder countSql = new StringBuilder("SELECT count(*) FROM public.version WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (version_no != null && !version_no.isEmpty()) {
            countSql.append(" AND version_no LIKE ?");
            params.add("%" + version_no + "%");
        }

        if (component_number != null && !component_number.isEmpty()) {
            countSql.append(" AND component_number LIKE ?");
            params.add("%" + component_number + "%");
        }

        if (supplier != null && !supplier.isEmpty()) {
            countSql.append(" AND supplier LIKE ?");
            params.add("%" + supplier + "%");
        }

        if (manager != null && !manager.isEmpty()) {
            countSql.append(" AND manager LIKE ?");
            params.add("%" + manager + "%");
        }

        Integer count = jdbcTemplate.queryForObject(countSql.toString(), params.toArray(), Integer.class);
        StringBuilder sql = new StringBuilder("SELECT * FROM public.version WHERE 1=1");
        List<Object> dataParams = new ArrayList<>();
        if (version_no != null && !version_no.isEmpty()) {
            sql.append(" AND version_no LIKE ?");
            dataParams.add("%" + version_no + "%");
        }

        if (component_number != null && !component_number.isEmpty()) {
            sql.append(" AND component_number LIKE ?");
            dataParams.add("%" + component_number + "%");
        }

        if (supplier != null && !supplier.isEmpty()) {
            sql.append(" AND supplier LIKE ?");
            dataParams.add("%" + supplier + "%");
        }

        if (manager != null && !manager.isEmpty()) {
            sql.append(" AND manager LIKE ?");
            dataParams.add("%" + manager + "%");
        }
        sql.append(" LIMIT ? OFFSET ?");
        dataParams.add(limit);
        dataParams.add((page - 1) * limit);
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql.toString(), dataParams.toArray());
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "");
        result.put("count", count);
        result.put("data", maps);
        return result;
    }


    @GetMapping("/version/downloadExcel")
    public void downloadExcel(
            @RequestParam(required = false) String version_no,
            @RequestParam(required = false) String component_number,
            @RequestParam(required = false) String supplier,
            @RequestParam(required = false) String manager,
            HttpServletResponse response
    ) throws Exception {
        StringBuilder sql = new StringBuilder("SELECT * FROM public.version WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (version_no != null && !version_no.isEmpty()) {
            sql.append(" AND version_no LIKE ?");
            params.add("%" + version_no + "%");
        }

        if (component_number != null && !component_number.isEmpty()) {
            sql.append(" AND component_number LIKE ?");
            params.add("%" + component_number + "%");
        }

        if (supplier != null && !supplier.isEmpty()) {
            sql.append(" AND supplier LIKE ?");
            params.add("%" + supplier + "%");
        }

        if (manager != null && !manager.isEmpty()) {
            sql.append(" AND manager LIKE ?");
            params.add("%" + manager + "%");
        }

        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql.toString(), params.toArray());
        System.out.println(maps);
        List<List<String>> dataList = new ArrayList<>();
        for (Map<String, Object> map : maps) {
            List<String> list = new ArrayList<>();
            list.add(map.get("version_no").toString());
            list.add(map.get("component_number").toString());
            list.add(map.get("supplier").toString());
            list.add(map.get("manager").toString());

            String allComponentQuantity = map.get("all_component_quantity").toString();
            String[] split = allComponentQuantity.split(",");
            list.addAll(Arrays.asList(split));
            dataList.add(list);
        }

        FileUtil.writeExcel(dataList, response, maps.get(0).get("start_time").toString(), "history.xlsx");
    }

}
