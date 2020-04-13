package com.example.leedonghun.speakenglish;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * speakenglish
 * Class: teacherinforesult.
 * Created by leedonghun.
 * Created On 2019-01-17.
 * Description: 리트로핏에서
 * 해당 선생님  정보를  수정하는 엑티비티에  선생님 정보를 가지고 오기위한  코드이다.
 */
public class teacherinforesult {


    @SerializedName("uid")
    @Expose
    private  String teacheruid;

    @SerializedName("career")
    @Expose
    private  String career;

    @SerializedName("shortsentence")
    @Expose
    private String shortsentence;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("profilepath")
    @Expose
    private String profilepath;

    @SerializedName("hellowtostudent")
    @Expose
    private  String hellowtostudent;

    @SerializedName("teachercountry")
    @Expose
    private  String teachercountry;


    public String getName() {// 해당 강사의 이름을 리턴한다.
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {//ㅎ해당 강사의 이메일을 리턴한다.
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getProfilepath() {//해당 강사의 프로필 이미지를 리턴한다.
        return profilepath;
    }


    public void setProfilepath(String profilepath) {
        this.profilepath = profilepath;
    }


    public String getCareer() {// 해당강사의 커리어 글을 리턴한다.
        return career;
    }

    public void setCareer(String career) {
        this.career = career;
    }

    public String getShortsentence() {//해당 강사의  내 강의는 한문장을 리턴한다.
        return shortsentence;
    }

    public void setShortsentence(String shortsentence) {
        this.shortsentence = shortsentence;
    }

    public String getHellowtostudent() {//해당강사가 학생들에게 하는 인사말을 리턴한다.
        return hellowtostudent;
    }

    public void setHellowtostudent(String hellowtostudent) {
        this.hellowtostudent = hellowtostudent;
    }

    public String getTeachercountry() {
        return teachercountry;
    }

    public void setTeachercountry(String teachercountry) {
        this.teachercountry = teachercountry;
    }

    public String getTeacheruid() {
        return teacheruid;
    }

    public void setTeacheruid(String teacheruid) {
        this.teacheruid = teacheruid;
    }
}
