package com.example.zoom01.controller;

import com.example.zoom01.entity.Meeting;
import com.example.zoom01.service.MeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


import javax.websocket.server.PathParam;
import java.util.List;

/**
 * 与前端UI的请求对应
 */
@CrossOrigin
@RestController
public class MeetingController {

    @Autowired
    MeetingService meetingService;

    @GetMapping("/api/meeting/list")
    public List<Meeting> list(){
        return meetingService.list();
    }

    @GetMapping("/api/meeting/{date}")
    public List<Meeting> listOnDate(@PathVariable("date") String startDate){

        List<Meeting> listOnDate = meetingService.getListByDate(startDate);

        return listOnDate;
    }
}
