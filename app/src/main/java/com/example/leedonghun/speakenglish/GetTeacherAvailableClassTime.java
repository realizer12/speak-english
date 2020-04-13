package com.example.leedonghun.speakenglish;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * speakenglish
 * Class: GetTeacherAvailableClassTime.
 * Created by leedonghun.
 * Created On 2020-03-02.
 * Description: 선생님이 지정해 놓은  예약 가능한  수업 시간 json 리스트를 받기위한 클래스
 */
public class GetTeacherAvailableClassTime {


    @SerializedName("available_class_time_list")
    @Expose
    private ArrayList<JsonObject> get_available_class_time;


    public ArrayList<JsonObject> getGet_available_class_time() {
        return get_available_class_time;
    }

    public void setGet_available_class_time(ArrayList<JsonObject> get_available_class_time) {
        this.get_available_class_time = get_available_class_time;
    }
}
