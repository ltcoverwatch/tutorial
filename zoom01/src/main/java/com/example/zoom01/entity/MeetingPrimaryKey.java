package com.example.zoom01.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class MeetingPrimaryKey implements Serializable {
    private Long meetingId;

    private String startDate;
}
