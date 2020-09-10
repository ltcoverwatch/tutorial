package com.example.zoom01.dao;

import com.example.zoom01.entity.Meeting;
import com.example.zoom01.entity.MeetingPrimaryKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface MeetingDao extends JpaRepository<Meeting, MeetingPrimaryKey> {

    @Transactional
    @Modifying
    @Query(value = "delete from meeting where meeting_id = ?1", nativeQuery = true)
    void deleteByMeetingId(Long id);

    @Transactional
    @Modifying
    @Query(value = "delete from meeting", nativeQuery = true)
    void deleteAllMeetings();

    List<Meeting> findByStartDateEquals(String startDate);
}
