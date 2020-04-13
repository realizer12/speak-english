package com.example.leedonghun.speakenglish;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * speakenglish
 * Class: GetTeacherFeedback.
 * Created by leedonghun.
 * Created On 2020-02-26.
 * Description:선생님이  받은피드백 리스트를 json으로  정리해서 받아오기위한  클래스
 */
public class GetTeacherFeedback {

    @SerializedName("teacherfeedback")
    @Expose
    private ArrayList<JsonObject> get_teacher_feedback;


    public ArrayList<JsonObject> getGet_teacher_feedback() {
        return get_teacher_feedback;
    }

    public void setGet_teacher_feedback(ArrayList<JsonObject> get_teacher_feedback) {
        this.get_teacher_feedback = get_teacher_feedback;
    }
}
