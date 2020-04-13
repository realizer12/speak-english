package com.example.leedonghun.speakenglish;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * speakenglish
 * Class: GetChattingImagePath.
 * Created by leedonghun.
 * Created On 2019-12-29.
 * Description:
 */
public class GetChattingImagePath {


    @SerializedName("chattingimagepath")
    @Expose
    private ArrayList<JsonObject> chatting_image_path;


    public ArrayList<JsonObject> getChatting_image_path() {
        return chatting_image_path;
    }

    public void setChatting_image_path(ArrayList<JsonObject> chatting_image_path) {
        this.chatting_image_path = chatting_image_path;
    }
}
