package com.example.leedonghun.speakenglish;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * speakenglish
 * Class: GetLoginedTeachersInfo.
 * Created by leedonghun.
 * Created On 2019-09-23.
 * Description: 선생님 목록 중에  로그인된  선생님 목록만 받아오기 위해 만든  클래스
 */
public class GetLoginedTeachersInfo {

    @SerializedName("loginedteacherlist")
    @Expose
    private ArrayList<JsonObject> loginedteacherinfoarraylist;



    public ArrayList<JsonObject> getLoginedteacherarraylist() {
        return loginedteacherinfoarraylist;
    }

}//GetLogiedTeachersInfo 끝
