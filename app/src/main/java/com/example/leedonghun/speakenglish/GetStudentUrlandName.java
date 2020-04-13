package com.example.leedonghun.speakenglish;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * speakenglish
 * Class: GetStudentUrlandName.
 * Created by leedonghun.
 * Created On 2020-02-26.
 * Description:선생님이 받은 피드백 뿌려줄때 학생의  프로필이랑  이름이  필요했음.
 */
public class GetStudentUrlandName {

    @SerializedName("std_name")
    @Expose
    private String std_name;

    @SerializedName("std_profile")
    @Expose
    private String std_profile;


    public String getStd_profile() {
        return std_profile;
    }

    public void setStd_profile(String std_profile) {
        this.std_profile = std_profile;
    }

    public String getStd_name() {
        return std_name;
    }

    public void setStd_name(String std_name) {
        this.std_name = std_name;
    }
}
