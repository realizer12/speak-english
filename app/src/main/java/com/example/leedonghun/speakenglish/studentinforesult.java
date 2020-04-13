package com.example.leedonghun.speakenglish;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * speakenglish
 * Class: studentinforesult.
 * Created by leedonghun.
 * Created On 2019-01-12.
 * Description: 해당  학생 정보를  가지고 올수 있는  클래스이다.
 */
public class studentinforesult {

    @SerializedName("uid")
    @Expose
    private String uid;


    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("profilepath")
    @Expose
    private String profilepath;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getProfilepath() {
        return profilepath;
    }
    public void setProfilepath(String profilepath) {
        this.profilepath = profilepath;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
