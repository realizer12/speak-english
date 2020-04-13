package com.example.leedonghun.speakenglish;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * speakenglish
 * Class: getteacheronoffresult.
 * Created by leedonghun.
 * Created On 2019-09-09.
 * Description:
 */
public class getteacheronoffresult {

    @SerializedName("teacheremail")
    @Expose
    private  String teacheronoff;

    String getTeacheronoff() {
        return teacheronoff;
    }

    public void setTeacheronoff(String teacheronoff) {
        this.teacheronoff = teacheronoff;
    }
}
