package com.example.leedonghun.speakenglish;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * speakenglish
 * Class: GetStudentReservedClassTime.
 * Created by leedonghun.
 * Created On 2020-03-13.
 * Description:학생이  자기가  에약한   수업 리스트를  받기 위해  만들어짐.
 */
public class GetStudentReservedClassTime {


    @SerializedName("reserved_classtime_list")
    @Expose
    private ArrayList<JsonObject> get_reserved_class_time;


    public ArrayList<JsonObject> getGet_reserved_class_time() {
        return get_reserved_class_time;
    }

    public void setGet_reserved_class_time(ArrayList<JsonObject> get_reserved_class_time) {
        this.get_reserved_class_time = get_reserved_class_time;
    }
}
