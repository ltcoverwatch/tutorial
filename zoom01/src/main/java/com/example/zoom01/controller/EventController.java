package com.example.zoom01.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.zoom01.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 处理zoom 事件监听
 * 用来添加更改单条会议
 */
@Controller
public class EventController {

    @Autowired
    JwtService jwtService;

    @ResponseBody
    @PostMapping(value = "/event", produces = "application/json;charset=UTF-8")
    public void event(@RequestBody JSONObject jsonParam){

        System.out.println(jsonParam.toJSONString());

        jwtService.saveEventInfo(jsonParam);
    }
}
