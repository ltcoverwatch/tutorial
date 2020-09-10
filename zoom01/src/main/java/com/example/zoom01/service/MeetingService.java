package com.example.zoom01.service;

import com.example.zoom01.dao.MeetingDao;
import com.example.zoom01.entity.Meeting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MeetingService {

    @Autowired
    MeetingDao meetingDao;

    public List<Meeting> list(){
        return meetingDao.findAll();
    }

    public List<Meeting> getListByDate(String startDate) {
        return meetingDao.findByStartDateEquals(startDate);
    }
}
