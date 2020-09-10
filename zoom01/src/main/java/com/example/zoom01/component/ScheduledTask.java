package com.example.zoom01.component;

import com.example.zoom01.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ScheduledTask {

    @Autowired
    JwtService jwtService;

    @Scheduled(cron = "0 5 4 * * ?")
    public void refreshDatabase(){
        System.out.println("do scheduled tast at : " + new Date());
        jwtService.deleteAllMeetings();
        jwtService.getAllMeetings();
    }
}
