package com.example.leedonghun.speakenglish;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * speakenglish
 * Class: GetWholeTeacherInfo.
 * Created by leedonghun.
 * Created On 2019-08-27.
 * Description:선생님  목록을   native , global 로  나눠서 가져외 위해서  만든  클래스이다.
 */
public class GetWholeTeacherInfo {

    @SerializedName("response")//native를  가져오기 위해서
    @Expose
    private ArrayList<JsonObject> wholeteacherinfornative;

    @SerializedName("response1")//global를  가져오이위해서
    @Expose
    private ArrayList<JsonObject> wholeteacherinforglobal;


    @SerializedName("onlineteachercount")
    @Expose
    private int onlineteachercount;



    //native getter
   ArrayList<JsonObject> getWholeteacherinfornative() { return wholeteacherinfornative; }


    //global getter
    ArrayList<JsonObject> getWholeteacherinforglobal() {
        return wholeteacherinforglobal;
    }


    //온라인인  선생님  카운트  getter
    int getOnlineteachercount() { return onlineteachercount; }

    
}
