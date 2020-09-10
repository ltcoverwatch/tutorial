package com.example.zoom01.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.zoom01.dao.MeetingDao;
import com.example.zoom01.entity.Meeting;
import com.example.zoom01.entity.MeetingPrimaryKey;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class JwtService {
    @Value("${jwt.api-key}")
    private String API_Key;

    @Value("${jwt.api-secret}")
    private String API_Secret;

    @Value("${userIds.ids}")
    private List<String> userIds;

    @Autowired
    MeetingDao meetingDao;

    public void getAllMeetings() {

        String generatedToken = getToken();

        for (String userId : userIds) {

            List<Meeting> meetingList = requestMeetingList(userId, generatedToken);

            meetingDao.saveAll(meetingList);
            System.out.println("there are "+ meetingList.size() + " meetings in "+ userId + " list");
        }
    }

    private List<Meeting> requestMeetingList(String userId, String generatedToken) {

        //1. send request
        HttpResponse<JsonNode> response = Unirest.get("https://api.zoom.us/v2/users/"+ userId +"/meetings?page_size=100&type=upcoming")
                .header("authorization", "Bearer " + generatedToken)
                .asJson();
        //2. get response json data
        JSONObject object = response.getBody().getObject();
        JSONArray jsonArray = object.getJSONArray("meetings");

        //3. 解析jsonarray
        List<Meeting> meetingList = new ArrayList<>();

        //对每个会议信息进行操作
        for (int i = 0; i < jsonArray.length(); i++){

            JSONObject jsonObject = jsonArray.getJSONObject(i);

            String dateRaw = jsonObject.getString("start_time");
            String jpDateStr = getJapanDate(dateRaw);
            String startTime = jpDateStr.substring(11, 16);
            if("07:00".compareTo(startTime) > 0 || "22:15".compareTo(startTime) < 0){
                break;
            }
            String startDate = jpDateStr.substring(0, 10);

            String hostId = jsonObject.getString("host_id");
            int hostNum = getHostNum(hostId);

            //解析时间
            String[] split = startTime.split(":");
            int hour = Integer.parseInt(split[0]);
            int minute = Integer.parseInt(split[1]);
            int rowIndex = (hour - 7) * 4 + minute/15;

            int duration = jsonObject.getInt("duration");
            int rowSpan = duration/15;

            //封装实体类
            Meeting meeting = new Meeting();
            meeting.setHostNum(hostNum);
            meeting.setMeetingId(jsonObject.getLong("id"));
            meeting.setTopic(jsonObject.getString("topic"));
            meeting.setDuration(duration);
            meeting.setStartTime(startTime);
            meeting.setStartDate(startDate);
            meeting.setRowIndex(rowIndex);
            meeting.setRowSpan(rowSpan);

            meetingList.add(meeting);
        }

        return meetingList;
    }

    public void saveEventInfo(com.alibaba.fastjson.JSONObject jsonParam) {

        com.alibaba.fastjson.JSONObject payload = jsonParam.getJSONObject("payload");
        String userId = payload.getString("operator");
        System.out.println("operator at : " + userId);
        //判断是否在国内20条线路之中，不在直接返回
        if (!userIds.contains(userId)){
            return;
        }

        String eventType = jsonParam.getString("event");
        System.out.println("eventType : " + eventType);
        switch (eventType){
            case "meeting.created" : {
                doCreate(payload.getJSONObject("object"), userId);
                break;
            }

            case "meeting.updated" : {
                System.out.println("do update");
                doUpdate(payload.getJSONObject("object"), userId);
                break;
            }
            case "meeting.deleted" : {
                doDelete(payload.getJSONObject("object"));
                break;
            }
            default:
        }
    }



    private void doCreate(com.alibaba.fastjson.JSONObject object, String userId) {

        Integer type = object.getInteger("type");
        //判断会议类型
        if (type.equals(1) || type.equals(3)){
            return;
        } else if(type.equals(8)){

            String generatedToken = getToken();
            List<Meeting> meetingList = requestMeetingList(userId, generatedToken);

            meetingDao.saveAll(meetingList);
            return;
        }

        String dateRaw = object.getString("start_time");
        String jpDateStr = getJapanDate(dateRaw);
        String startDate = jpDateStr.substring(0, 10);
        String startTime = jpDateStr.substring(11, 16);
        if("07:00".compareTo(startTime) > 0 || "22:15".compareTo(startTime) < 0){
            return;
        }

        String host_id = object.getString("host_id");
        int hostNum = getHostNum(host_id);

        //解析时间
        String[] split = startTime.split(":");
        int hour = Integer.parseInt(split[0]);
        int minute = Integer.parseInt(split[1]);
        int rowIndex = (hour - 7) * 4 + minute/15;

        Integer duration = object.getInteger("duration");
        int rowSpan = duration/15;

        //封装实体类
        Meeting meeting = new Meeting();
        meeting.setMeetingId(object.getLong("id"));
        meeting.setHostNum(hostNum);
        meeting.setTopic(object.getString("topic"));
        meeting.setDuration(duration);
        meeting.setStartDate(startDate);
        meeting.setStartTime(startTime);
        meeting.setRowIndex(rowIndex);
        meeting.setRowSpan(rowSpan);

        meetingDao.save(meeting);
    }

    private void doUpdate(com.alibaba.fastjson.JSONObject object, String userId) {

        Long id =  object.getLong("id");
        System.out.println("delete id" + id);
        //删除旧数据
        meetingDao.deleteByMeetingId(id);

        //查询并新增更新账户所有会议
        String generatedToken = getToken();
        List<Meeting> meetingList = requestMeetingList(userId, generatedToken);

        meetingDao.saveAll(meetingList);

    }

    private void doDelete(com.alibaba.fastjson.JSONObject object) {
        MeetingPrimaryKey key = new MeetingPrimaryKey();
        String dateRaw = object.getString("start_time");
        String jpDateStr = getJapanDate(dateRaw);
        String date = jpDateStr.substring(0, 10);

        key.setStartDate(date);
        key.setMeetingId(object.getLong("id"));

        meetingDao.deleteById(key);
    }


    private String getToken(){

        long unixTime = System.currentTimeMillis() / 1000L;
        long timestamp = unixTime + 1000L;

        Algorithm algorithmHS = Algorithm.HMAC256(API_Secret);
        String token = JWT.create()
                .withIssuer(API_Key)
                .withClaim("exp", timestamp)
                .sign(algorithmHS);

        return token;
    }

    private int getHostNum(String hostId){

        switch (hostId) {
            case "TJMaMkilS6ub2iOg325Zcg" : return 1;  //五反田1
            case "r7w9tN_FRteTT47mZoiUAQ" : return 2; //五反田2
            case "vc7THdosRaO1QNlVQHtTRA" : return 3; //五反田3
            case "sf2kNp_QSxWfmXH3DDHjgg" : return 4; //五反田4
            case "N6RNesKRTV2m4Ye_AFvhrg" : return 5; //五反田5
            case "74XgaTCGTXmOsNoEp96w_A" : return 6; //高萩工場
            case "5mfpX_UNTt2WDQyaptFEbA" : return 7; //新潟工場
            case "tq0X6ZTwQpuHLCKEt_DclQ" : return 8; //北関東営業
            case "cdW7sb1RSWyEjcwWhlIl5g" : return 9; //名古屋営業
            case "84UhZzBBSu2VQ6t0kmpv8Q" : return 10; //大阪営業
            case "nWy2096IQWKJVupXcmwr5Q" : return 11; //岩手
            case "jt5UM4m_SMi6whKcUI8iQg" : return 12; //宮城
            case "b83tX__PTmuCfxj2o_pLcA" : return 13; //福島
            case "m0RuM0alTomAxJT9JsYOIQ" : return 14; //喜多方
            case "Mu2aDGsWQ2axgu2nvjpqjg" : return 15; //長井
            case "oDocg1XzReKZEpxQul04pg" : return 16; //米沢
            case "QC7F3CmbRHSL5WSOggGvLg" : return 17; //長岡
            case "iEK-B0qZR_azPrCXCqoJog" : return 18; //CSK
            case "CJqXm8IzQVeLpKcQqQFUdw" : return 19; //KDK販売
            case "BkZ0EwzPSBKXcyoDK9KB0A" : return 20; //神奈川研究所

            default: return 0;
        }
    }

    private String getJapanDate(String raw){
        Instant instant = Instant.parse(raw);

        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.of("Asia/Tokyo"));

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        String jpDate = formatter.format(localDateTime);
        return jpDate;
    }

    public void deleteAllMeetings(){
        meetingDao.deleteAllMeetings();
    }

}
