package com.example.leedonghun.speakenglish;

import android.app.Service;
import android.content.Intent;

import android.os.IBinder;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Manager;
import com.github.nkzawa.socketio.client.Socket;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * speakenglish
 * Class: ServiceForTeacherGetVideoClassREQUEST.
 * Created by leedonghun.
 * Created On 2020-02-19.
 * Description:해당 선생님 들어왔을때, 자기 상태를 온라인으로 표시하면,
 * 1명의 학생이 video call를  신청할까지  서버에서  대기하고 있는다.
 * 그래서  online시   서버와 연결해서 백그라운드에서  연결을 실행시키다가
 * 학생이 들어왔을떄,서버의 emit을 받아  서비스를 통해 받고  서비스는  connecting activity 를  선생님 기기에 띄어 준다.
*/


public class ServiceForTeacherGetVideoClassREQUEST extends Service{



    //로그용 -> 현재 서비스 클래스 이름.
    private final static String TAG="ServiceForTeacherGetVideoClassREQUEST";//1-0
    private Socket socket_for_video_chat;// 서버 연결 위한 소켓- 오픈 채팅용  /1-1
    private final String iport="http://13.209.249.1:1794";//1-2

    @Override
    public IBinder onBind(Intent intent) { return null; }//bind service 용으로 쓰는 메소드 -> 지금은 null


    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("check", TAG+"의  onCreate() 실행됨");


    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("check", TAG+"의  onCreate() 실행됨");

        GlobalBusForVideoClass.getBus_data().register(this);//선생님이  거절 한 내용을  connecting activity에서  받아서  보내주기 위해  이벤트 버스를  동작 시켜줌.
        String roomnumber=intent.getStringExtra("teacheruid")+"speakenglish";


        try {

            Manager manager=new Manager(new URI(iport));//ip들어감-1-2
            if(socket_for_video_chat == null ) {//소켓이  null값 일때만  연결시켜서  -> 불필요한 소켓 연결을 줄인다.

                socket_for_video_chat=manager.socket("/videoCall");
                socket_connection(socket_for_video_chat,roomnumber);

            }

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


      return START_REDELIVER_INTENT;// 이걸로 하면  강제 종료시 -> 다시 서비스를 시작 할때  INTENT 를 유지하고 실행
    }



    public void socket_connection(Socket socket,String roomnumber){


        socket.connect();//socket연결
        socket.once(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                socket_for_video_chat.emit("join",roomnumber,"t");
            }

        });//10-1


        //선생님이  만든방에  학생이 들어와서
        //방인원이  2명이 되었을때,  선생님 방에  show_conneting으로  알려서
        //connecting activity를  선생님 기기에 띄어준다.
        socket.on("show_connecting", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                Log.v("check", TAG+"의  채팅 수업  매칭 됨");
                Intent intent=new Intent(getApplication(), VideoCallConnectingActivity.class);
                intent.putExtra("teacherroomnumber", roomnumber);
                JSONObject data = (JSONObject) args[0];
                String name = null;
                String profile_url=null;
                String std_uid=null;
                try {
                    name=data.getString("name");
                    profile_url=data.getString("profile");
                    std_uid=data.getString("uid");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.v("FKFKFK", "SDADAS"+data);

                 intent.putExtra("name", name);
                 intent.putExtra("profile", profile_url);
                 intent.putExtra("uid",std_uid);

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });


        //학생이  거절 한 내용을  선생님이 받음.
        //connecting activity 취소함.
        socket.on("studentrefuse", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

              //학생이  거절 했을 경우로  화상 전화 연결  엑티비티를 종료 해준다.
              VideoCallConnectingActivity videocallconnectingactivity=(VideoCallConnectingActivity)VideoCallConnectingActivity.videocallconnectingactivity;
              videocallconnectingactivity.finish();

            }
        });



        //선생님이  방을 만들었는데  해당 방이  이미 존재 하는 경우를 대비해서
        //만들어놓은 이벤트
        socket.on("alert_position", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                socket.disconnect();
                Log.e("check", TAG+"의 해당 방이  이미 있음-> socket disconnet 처리했음");
            }
        });

    }

    //서비스에서 백그라운드로 받은  채팅 데이터를 이벤트버스로 보냈고,  그걸 받기위한  메소드이다.
    @Subscribe
    public  void getMessage(JSONObject receivedata){

        try {
            socket_for_video_chat.emit("alert_refuse", receivedata.getString("ress"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        GlobalBusForVideoClass.getBus_data().unregister(this);
        socket_for_video_chat.disconnect();
    }
}//ServiceForTeacherGetVideoClassREQUEST 서비스 끝.

