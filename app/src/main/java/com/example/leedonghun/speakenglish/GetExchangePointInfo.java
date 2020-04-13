package com.example.leedonghun.speakenglish;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * speakenglish
 * Class: GetExchangePointInfo.
 * Created by leedonghun.
 * Created On 2020-03-29.
 * Description: 선생님  환전 신청한  포인트 정보를 가지고 온다.
 */
public class GetExchangePointInfo {

    @SerializedName("teacher_exchange_info")
    @Expose
    private ArrayList<JsonObject> get_teacher_exchange_point_info;


    public ArrayList<JsonObject> getGet_teacher_exchange_point_info() {
        return get_teacher_exchange_point_info;
    }

    public void setGet_teacher_exchange_point_info(ArrayList<JsonObject> get_teacher_exchange_point_info) {
        this.get_teacher_exchange_point_info = get_teacher_exchange_point_info;
    }
}
