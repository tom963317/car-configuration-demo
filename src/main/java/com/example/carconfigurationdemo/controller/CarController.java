package com.example.carconfigurationdemo.controller;

import com.example.carconfigurationdemo.vo.ComponentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class CarController {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/getComponent")
    @ResponseBody
    public Map<String, Integer> getComponent(String options) {
        String[] optionArr = options.split(",");
        String placeholders = String.join(",", Collections.nCopies(optionArr.length, "?"));
        String sql = "select * from car_assembly where optional_code in (" + placeholders + ")";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql, optionArr);
        Map<String, Integer> countMap = new HashMap<>();
        List<ComponentVO> resList = new ArrayList<>();
        for (Map<String, Object> map : maps) {
            String componentCode = map.get("component_code").toString();
            String componentName = map.get("component_name").toString();
            int componentQuantity = Integer.parseInt(map.get("component_quantity").toString());
            if (countMap.containsKey(componentCode)) {
                countMap.put(componentCode, countMap.get(componentCode) + componentQuantity);
            } else {
                countMap.put(componentCode, componentQuantity);
            }
            ComponentVO componentVO = new ComponentVO();
            componentVO.setComponentCode(componentCode);
            componentVO.setComponentName(componentName);
            componentVO.setComponentQuantity(componentQuantity);
            resList.add(componentVO);
        }
        System.out.println(countMap.toString());
        return countMap;
    }
}
