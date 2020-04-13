package com.example.leedonghun.speakenglish;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * speakenglish
 * Class: GetRoomInfoForChattingRoomList.
 * Created by leedonghun.
 * Created On 2019-11-29.
 * Description:해당 유저가 가져온  리스트의 방에서  각 방의  정보를 room디비에서  가져와서  필요정보로 나눠주는  클래스
 */
public class GetRoomInfoForChattingRoomList {

    @SerializedName("teacheruid")
    @Expose
    private String teacheruid;//해당방의 참여  클라이언트 수   //1-1


    @SerializedName("chatclientcount")
    @Expose
    private String chatclientcount;//해당방의 참여  클라이언트 수   //1-1


    @SerializedName("roomimage")
    @Expose
    private String room_profile_image;//해당방의  룸 이미지  오픈 채팅방-> 선생님 지정,  일대일 채팅방 -> 대화 상대의  프로필 이미지//1-2


    @SerializedName("teacherinfo")
    @Expose
    private String room_teacherinfo;//오픈채팅방 -> 선생님  이메일   또는  일대일 채팅방->  선생님 uid//1-3


    @SerializedName("roomname")
    @Expose
    private String room_name;//해당방의 이름  오픈 채팅방 ->  oo's opencahttingroom   일대일 채팅방 ->  상대의  name //1-4


    public String getChatclientcount() {
        return chatclientcount;
    }//1-1

    public void setChatclientcount(String chatclientcount) {
        this.chatclientcount = chatclientcount;
    }//1-1


    public String getRoom_teacherinfo() {
        return room_teacherinfo;
    }//1-3

    public void setRoom_teacherinfo(String room_teacherinfo) {
        this.room_teacherinfo = room_teacherinfo;
    }//1-3

    public String getRoom_name() {
        return room_name;
    }//1-4

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }//1-4


    public String getRoom_profile_image() {//1-2
        return room_profile_image;
    }

    public void setRoom_profile_image(String room_profile_image) {//1-2
        this.room_profile_image = room_profile_image;
    }

    public String getTeacheruid() {
        return teacheruid;
    }

    public void setTeacheruid(String teacheruid) {
        this.teacheruid = teacheruid;
    }
}//클래스 끝.
