package com.example.leedonghun.speakenglish;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * speakenglish
 * Class: GetStudentFeedback.
 * Created by leedonghun.
 * Created On 2020-02-27.
 * Description:학생이 받은  피드백 리스트 json 받아오기 위한 클래스
 */
public class GetStudentFeedback {

    @SerializedName("studentfeedback")
    @Expose
    private ArrayList<JsonObject>get_student_feedback;


    public ArrayList<JsonObject> getGet_student_feedback() {
        return get_student_feedback;
    }

    public void setGet_student_feedback(ArrayList<JsonObject> get_student_feedback) {
        this.get_student_feedback = get_student_feedback;
    }
}
