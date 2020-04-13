package com.example.leedonghun.speakenglish;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * speakenglish
 * Class: GetTeacherPointInfo.
 * Created by leedonghun.
 * Created On 2020-03-23.
 * Description: 선생님  포인트  정보  받아온다
 */
public class GetTeacherPointInfo {



    @SerializedName("techer_point_info")
    @Expose
    private ArrayList<JsonObject> get_teacher_point_info;


    public ArrayList<JsonObject> getGet_teacher_point_info() {
        return get_teacher_point_info;
    }

    public void setGet_teacher_point_info(ArrayList<JsonObject> get_teacher_point_info) {
        this.get_teacher_point_info = get_teacher_point_info;
    }
}
