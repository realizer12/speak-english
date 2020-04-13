package com.example.leedonghun.speakenglish;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * speakenglish
 * Class: GetKakaoPayReadyInfo.
 * Created by leedonghun.
 * Created On 2020-03-20.
 * Description:카카오 페이 실행시 ready과정에서 approval로 넘기기위한  app_url_redirect가지고온다
 */
public class GetKakaoPayReadyInfo {


    //앱에서 카카오페이 결제 창에 넘어가기위한 redirecapp_url이다.
    @SerializedName("next_redirect_app_url")
    @Expose
    private String app_redirect;

    //앱에서 사용하는 tid
    @SerializedName("tid")
    @Expose
    private String tid;


    public String getApp_redirect() {
        return app_redirect;
    }

    public void setApp_redirect(String app_redirect) {
        this.app_redirect = app_redirect;
    }


    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }
}
