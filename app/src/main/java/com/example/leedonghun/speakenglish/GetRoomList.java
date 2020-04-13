package com.example.leedonghun.speakenglish;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * speakenglish
 * Class: GetRoomList.
 * Created by leedonghun.
 * Created On 2019-11-27.
 * Description:해당  유저가  참여한  채팅방  리스트를 가지고 오기 위한 클래스이다.
 */
public class GetRoomList  {

    @SerializedName("roomlist")
    @Expose
    private ArrayList<JsonObject> roomlist_data_for_user;


    public ArrayList<JsonObject> getRoomlist_data_for_user() {

        return roomlist_data_for_user;
    }

    public void setRoomlist_data_for_user(ArrayList<JsonObject> roomlist_data_for_user) {
        this.roomlist_data_for_user = roomlist_data_for_user;
    }
}//GetRoomList 클래스 끝
