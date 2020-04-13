package com.example.leedonghun.speakenglish;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * speakenglish
 * Class: GetRoomInfoClass.
 * Created by leedonghun.
 * Created On 2019-11-12.
 * Description:디비에 저장된  방  정보를 서버에서 가져오기 위한  코드이다.
 * 나중에  룸 리스트를 뿌릴때 그리고  방내부에서의  필요한 방정보를 보일때  사용될수 있다.
 */
public class GetRoomInfoClass  {

    @SerializedName("roomnum")
    @Expose
    private String roomnumber;// 방번호

    @SerializedName("namespace")
    @Expose
    private  String namespace;//방 오픈채팅방,  일대일 채팅방 여부


    @SerializedName("chatclientcount")
    @Expose
    private int chatclientcount;//방 참여 인원 수


    @SerializedName("roomimage")
    @Expose
    private  String roomimagepath;//방  이미지  경로


    @SerializedName("roomname")
    @Expose
    private String roomname;//방 이름.

    @SerializedName("teachername")
    @Expose
    private String teachername;//방 참여 선생님 uid

    @SerializedName("studentname")
    @Expose
    private  String studentname;//방 참여 학생  uid

    @SerializedName("hostposition")
    @Expose
    private  String hostposition;//호스트 포지션


    public String getRoomnumber() {
        return roomnumber;
    }

    public void setRoomnumber(String roomnumber) {
        this.roomnumber = roomnumber;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public int getChatclientcount() {
        return chatclientcount;
    }

    public void setChatclientcount(int chatclientcount) {
        this.chatclientcount = chatclientcount;
    }

    public String getRoomimagepath() {
        return roomimagepath;
    }

    public void setRoomimagepath(String roomimagepath) {
        this.roomimagepath = roomimagepath;
    }

    public String getRoomname() {
        return roomname;
    }

    public void setRoomname(String roomname) {
        this.roomname = roomname;
    }



    public String getHostposition() {
        return hostposition;
    }

    public void setHostposition(String hostposition) {
        this.hostposition = hostposition;
    }

    public String getTeachername() {
        return teachername;
    }

    public void setTeachername(String teachername) {
        this.teachername = teachername;
    }

    public String getStudentname() {
        return studentname;
    }

    public void setStudentname(String studentname) {
        this.studentname = studentname;
    }
}
