package com.example.zoom01.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "meeting")
@IdClass(MeetingPrimaryKey.class)
public class Meeting {

    @Id
    Long meetingId;

    Integer hostNum;

    @Id
    String startDate;

    String startTime;

    Integer duration;

    String topic;

    Integer rowIndex;

    /**
     * 合并多少个单元格
     */
    Integer rowSpan;
}
