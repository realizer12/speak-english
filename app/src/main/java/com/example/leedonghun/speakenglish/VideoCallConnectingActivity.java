package com.example.leedonghun.speakenglish;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Manager;
import com.github.nkzawa.socketio.client.Socket;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * speakenglish
 * Class: VideoCallConnectingActivity.
 * Created by leedonghun.
 * Created On 2020-02-18.
 * Description:선생님이  학생에게  콜을 받거나,
 * 학생이 선생님으로부터  콜을  보내고,   해당 콜이  서로 응답되기전까지   보여지는 activity이다.
 *연결중 임을 보여주는 엑티비티
 * 여기서  학생은 해당방 선생님에게   소켓 통신으로  연결 요청을 알리고,  선생님에게  해당  accpet / refuse를 할수있는  엑티비티를  띄어지도록  한다.
 */
public class VideoCallConnectingActivity extends AppCompatActivity {


    private ImageView imgView_for_other_party;//상대방  프로필 사진 들어가는 이미지뷰 1-1
    private TextView txt_for_show_other_party_name;//상대방 이름을 보여줌. 1-2

    private TextView txt_for_show_explanation;//현재 상황의 설명을 보여주는 엑티비티 1-3

    private Button btn_for_cancel_class;// 수업 취소하기 버튼 -1-4
    private Button btn_for_accept;//수업 accept 버튼 1-5
    private Button btn_for_refuse;//수업 cancel 버튼 1-6

    private int teachorstd=-1;//현재 유저가  선생님인지  학생인지 구별하는 int값.  0=학생,  선생님=1 이다.  현재 default -1 : 1-7

    private LinearLayout btn_container_for_teacher;//선생님 수업 허락  cancel 버튼 담긴 리니어 1-8;

    private String other_party_name;//상대방 이름  1-9
    private String other_party_profileimg_url;//상대방  프로필 이미지 url  1-10
    private String room_number;//webrtc join할때 사용할  방번호.(선생님 uid)  1-11



    private Socket socket;//자바  서버 연결 위한 소켓   2-1
    private final String iport="http://13.209.249.1:1794";//ip 연결  2-2

    //학생  취소시  선생님 VideoCallconnecting 엑티비티  취소하기위한  엑티비티 객체
    public static Activity videocallconnectingactivity;//2-3
    private String teacherroomnumber;

    private String teacheruid;
    private String student_uid;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_call_connecting_activity);
        Log.v("check", getLocalClassName()+"의 onCreate() 실행됨");

        imgView_for_other_party=findViewById(R.id.imgview_for_other_party);//1-1
        txt_for_show_other_party_name=findViewById(R.id.other_party_name_txt);//1-2
        txt_for_show_explanation=findViewById(R.id.video_call_explain_txt);//1-3

        btn_for_cancel_class=findViewById(R.id.btn_for_cancel_class);//1-4
        btn_for_accept=findViewById(R.id.btn_for_accept);//1-5
        btn_for_refuse=findViewById(R.id.btn_for_refuse);//1-6

        btn_container_for_teacher=findViewById(R.id.container_for_teacher_callee);//1-8

        videocallconnectingactivity=VideoCallConnectingActivity.this;//2-3

        GlobalBusForVideoClass.getBus_data().register(this);


        //선생님 학생 구별  1-7
        teachorstd=user_position_check();

         if(teachorstd==0){//학생일때

             btn_container_for_teacher.setVisibility(View.GONE);//선생님용 컨테이너 GONE 1-8
             btn_for_cancel_class.setVisibility(View.VISIBLE); //현재 커넥팅  취소 버튼 VISIBLE 1-4

             Intent intent_for_get_teacherinfo=getIntent();

             teacheruid=intent_for_get_teacherinfo.getStringExtra("teacheruid");//선생님 uid
             room_number=teacheruid+"speakenglish";//해당 비디오 채팅  방 번호 1-11
             other_party_name=intent_for_get_teacherinfo.getStringExtra("teachername");//선생님 이름. 1-9
             other_party_profileimg_url=intent_for_get_teacherinfo.getStringExtra("profileimg");//선생님 프로필 이미지  1-10

             //학생일때 connecting activity에 나오는 설명  1-3
             txt_for_show_explanation.setText(other_party_name+" 선생님이 \n수업 준비 중입니다 \n\n   조금만 기다려주세요 ! ");

             //이름 넣어줌. 1-2
             txt_for_show_other_party_name.setText(other_party_name+" teacher");




         }else if(teachorstd==1){//선생일때

             btn_container_for_teacher.setVisibility(View.VISIBLE);// 1-8
             btn_for_cancel_class.setVisibility(View.GONE);//1-4

             //선생님이 가져갈  roomnumber이다.
             Intent get_teacher_roomnumber=getIntent();
             teacherroomnumber=get_teacher_roomnumber.getStringExtra("teacherroomnumber");
             String stundent_name=get_teacher_roomnumber.getStringExtra("name");
             String profile_url=get_teacher_roomnumber.getStringExtra("profile");
             student_uid=get_teacher_roomnumber.getStringExtra("uid");

             other_party_name=stundent_name;
             other_party_profileimg_url="http://13.209.249.1/"+profile_url;

             //선생일때 connecting activity에 나오는 설명  1-3
             txt_for_show_explanation.setText("student "+other_party_name+"\nrequest Video Class\n\nPlz Press\n'Accept'or'Refuse'");

             //이름 넣어줌. 1-2
             txt_for_show_other_party_name.setText(other_party_name+" student");

         }else{//선생님 학생 값 모두 해당 안될때 activity 종료

             finish();
         }

        try {
            URL url=new URL(other_party_profileimg_url);
            //상대방  프로필 사진 넣어줌.
            Glide.with(VideoCallConnectingActivity.this).load(url).into(imgView_for_other_party);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {

            Manager manager = new Manager(new URI(iport));
            socket=manager.socket("/videoCall");

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

      if(teachorstd==0) {//기기 로그인한 유저가  학생일때  소켓을  connecting 엑티비티에서 연결 시켜준다.

          socket_connection(socket);

      }


        //학생이 수업 취소를  눌렀을때.,1-4
        btn_for_cancel_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.v("check", getLocalClassName()+"의 수업취소버튼 클릭됨");
                socket.emit("studnet_refuse");
                socket.disconnect();//socket 연결 종료
                finish();
            }
        });//1-4 버튼 클릭이벤트 끝.




        //선생님이  수업 ACCEPT 버튼을 누렀을 경우. 1-5
        btn_for_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.v("check",getLocalClassName()+"의  수업 accept 버튼 눌림");

                final JSONObject teacher_accept_video_class =new JSONObject();

                try {

                    //1은  선생님이  수락한 내용임.
                    teacher_accept_video_class.put("ress", "1");



                    //이벤트 버스로  서비스에  선생님  accept 값을 보냄.
                    GlobalBusForVideoClass.getBus_data().post(teacher_accept_video_class);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent intent=new Intent(VideoCallConnectingActivity.this,VideoCallActivity.class);
                intent.putExtra("roomnumber", teacherroomnumber);//비디오 채팅 방 번호
                intent.putExtra("student_name", other_party_name);// 학생 이름
                intent.putExtra("student_uid", student_uid);//학생 uid

                intent.putExtra("student_profileurl", other_party_profileimg_url);//학생 프로필 url 넘김.

                startActivity(intent);
                finish();
            }

        });//1-5 버튼 클릭이벤트 끝

        //선생님 수업 REFUSE 버튼을 눌렀을 경우 -> 이벤트 버스로  서비스에  해당  이벤트를 알려
        //서비스에서 서버로 alert_refuse를 emit 한다.  -> 서버에서  aler_refuse를 확인하고,
        //학생기기기  (현재 엑티비티를  종료 시켜준다. )   1-6
        btn_for_refuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.v("check", getLocalClassName()+"의  수업 REFUSE 버튼 눌리");

                final JSONObject teacher_refuse_alert =new JSONObject();//해당 refuse 값  담을  JSONObject

                try {

                    //0을 담아줌. -> 0은 refuse
                    teacher_refuse_alert.put("ress", "0");

                    //이벤트 버스로 값 서비스로  보냄.
                    GlobalBusForVideoClass.getBus_data().post(teacher_refuse_alert);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                finish();
            }
        });//1-6 버튼 클릭 이벤트 끝


    }//onCreate()끝



    //쉐어드에  저장된 값으로  현재 유저가 선생님인지 학생인지를 구별한다.
    public int user_position_check(){

        Log.v("check", getLocalClassName()+"의 user_position_check()실행됨");

        int result=-1;//학생 또는 선생님 결과

        SharedPreferences getstudentemail = getSharedPreferences("loginstudentid", MODE_PRIVATE);//로그인 아이디  쉐어드에 담긴거 가져옴
        final String studentemail = getstudentemail.getString("loginid", "");//학생 로그인 이메일 가져옴.


        //선생님 이메일 쉐어드에  넣기.
        final SharedPreferences getteacheremail = getBaseContext().getSharedPreferences("loginteacherid",MODE_PRIVATE);//선생님 로그인 아이디  쉐어드에 담긴거 가져옴
        String teacheremail= getteacheremail.getString("loginidteacher","");//선생님 로그인 이메일 가져옴

        if(studentemail.equals("")){//선생님 로그인 일때

            result=1;//선생님일때 1-7

        }else if(teacheremail.equals("")){//학생 로그인 일때

            result=0;//학생일때 1-7
        }

        return result;

    }//user_position_check()끝




  //학생이 소켓 연결을 할수 있도록 해주는 메소드
  //연결 및  소켓 관련 처리를 진행한다.
  public void socket_connection(Socket socket){


          socket.connect();//socket연결
          socket.once(Socket.EVENT_CONNECT, onConnect);//10-1

          socket.on("full_alarm", onFull_alarm); //10-2

          socket.on("no_room", no_room);//10-3

          socket.on("teacher_refuse", teacher_refuse);//10-4

          socket.on("teacher_accpet", teacher_accept);//10-5

  }//socket_connection() 끝


    //10-5
    //선생님이 수업을  수락했을 때  진행되는 이벤트이다.
    //이렇게 되면  학생은  apprtc연결로 넘어가게된다.
    private  Emitter.Listener teacher_accept=new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            socket.disconnect();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Log.v("check", getLocalClassName()+"의  선생님이 수업 허락함..");
                    new Toastcustomer(VideoCallConnectingActivity.this).showcustomtaost(null, "선생님이  수업을 허락했어요!!");

                }
            });

            //String data = (String) args[0];
            Intent intent=new Intent(VideoCallConnectingActivity.this,VideoCallActivity.class);
            intent.putExtra("roomnumber", room_number);
            intent.putExtra("teacheruid", teacheruid);
            startActivity(intent);

            finish();
            //Intent intent= new Intent(); -> 이걸 통해서  화상통화 엑티비티로 넘어가야됨.

        }
    };


    //10-4
    //선생님이 수업을 취소했을때  진행되는  이벤트이다.
    //학생 화면에서  진행되며 진행되면, 학생 connecting 화면은 finish  선생님이 수업 거절했다는
    //토스트와 함께 해당방에서 socket disconnect된다.
    private Emitter.Listener teacher_refuse=new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            socket.disconnect();//socket 연결 종료
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Log.v("check", getLocalClassName()+"의  선생님이 수업 거절함.");
                    new Toastcustomer(VideoCallConnectingActivity.this).showcustomtaost(null, "선생님이  수업을 거절했어요!!");

                }
            });

            finish();
        }
    };

    //10-3
    //학생이  선생님  영상 수업을 눌렀는데,  선생님  비디오 채팅 방이 안만들어져 있어서
    //서로  매칭 정보를  못받을때를  대비해서 만들어놓은 이벤트이다.
    private Emitter.Listener no_room=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
           runOnUiThread(new Runnable() {
               @Override
               public void run() {

                   Log.v("check", getLocalClassName()+"의 해당  영상채팅 방이 없음. ");
                   new Toastcustomer(VideoCallConnectingActivity.this).showcustomtaost(null, "해당 선생님과  수업을 할수  없습니다. ");
               }
           });

           //영상 채팅방이 없으므로 다시 돌아간다.
           finish();
        }
    };

    //10-2
    //소켓 연결시  해당방이 2명으로  사람이 꽉찬경우 알림 받음.-> 수업 시작하면, 선생님  수업으로 상태 바뀌는데,
    //혹시  들어가게되면  2명이 꽉찬경우에,  다시  이전 엑티비티로 돌아가게한다.
    private Emitter.Listener onFull_alarm=new Emitter.Listener() {

        @Override
        public void call(Object... args) {

          //토스트는  UI쓰레드에서  작동하므로 ,
          //아래왁 같이 runOnUiThread를 사용하였다.
          runOnUiThread(new Runnable() {
              @Override
              public void run() {
                  Log.v("check", getLocalClassName()+"의 해당 영상 수업 방  꽉참");
                  new Toastcustomer(VideoCallConnectingActivity.this).showcustomtaost(null, "해당 수업은 이미 진행중입니다.");
              }
          });

          //해당방  인원이  꽉찼으므로, 연결 엑티비티 종료
          finish();
        }
    };//socket connection 이벤트 끝.



    //10-1
    //처음에  소켓  connection이 되었을때.
    //학생 이름이랑 ,  프로필 url을 보냄.
    private Emitter.Listener onConnect=new Emitter.Listener() {

        @Override
        public void call(Object... args) {

            //처음에  소켓이 연결될때 ->   유저의 uid,  접속하려는  채팅방 룸 넘버 / user의 포지션 (학생 or 선생)/노티에서 사용될  선생님 이름.
            socket.emit("join",room_number,"s");

            //방 연결과 동시에 -> 학생 이름 과  학생 프로필 주소를 서버로 보내준다
            GlobalApplication globalApplication=(GlobalApplication)getApplicationContext();

            socket.emit("student_info",globalApplication.getStudent_name(),globalApplication.getStudnet_profile_url(),globalApplication.getStudnet_uid());//학생 이름


        }//call() 끝

    };//socket connection 이벤트 끝.


    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalBusForVideoClass.getBus_data().unregister(this);
    }
}//VideoCallConnectingActivity 클래스 끝.
