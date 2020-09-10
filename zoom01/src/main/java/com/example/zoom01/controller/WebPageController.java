package com.example.zoom01.controller;

import com.example.zoom01.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class WebPageController {

    @Autowired
    JwtService jwtService;


    @ResponseBody
    @RequestMapping("/get-all-meetings")
    public void meetings(){
        jwtService.getAllMeetings();
    }

    @ResponseBody
    @RequestMapping("/delete-all-meetings")
    public void deleteMeetings(){
        jwtService.deleteAllMeetings();
    }




}
