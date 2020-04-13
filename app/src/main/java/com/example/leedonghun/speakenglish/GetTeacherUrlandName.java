package com.example.leedonghun.speakenglish;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * speakenglish
 * Class: GetTeacherUrlandName.
 * Created by leedonghun.
 * Created On 2020-02-27.
 * Description: 학생이 받은 피드백 뿌려줄때 선생님의  프로필이랑  이름
 */
public class GetTeacherUrlandName {

    @SerializedName("teacher_name")
    @Expose
    private String teacher_name;

    @SerializedName("teacher_profile")
    @Expose
    private String teacher_profile;


    public String getTeacher_profile() {
        return teacher_profile;
    }

    public void setTeacher_profile(String teacher_profile) {
        this.teacher_profile = teacher_profile;
    }

    public String getTeacher_name() {
        return teacher_name;
    }

    public void setTeacher_name(String teacher_name) {
        this.teacher_name = teacher_name;
    }
}
