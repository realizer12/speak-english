package com.example.leedonghun.speakenglish;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * speakenglish
 * Class: GetUserList.
 * Created by leedonghun.
 * Created On 2020-01-19.
 * Description:서버에서  각방의 유저리스트를  json으로 보내준걸 받기위한  클래스
 */
public class GetUserList {


    @SerializedName("totaluserlist")
    @Expose
    private ArrayList<JsonObject> room_userlist;


    public ArrayList<JsonObject> getRoom_userlist() {
        return room_userlist;
    }

    public void setRoom_userlist(ArrayList<JsonObject> room_userlist) {
        this.room_userlist = room_userlist;
    }
}
