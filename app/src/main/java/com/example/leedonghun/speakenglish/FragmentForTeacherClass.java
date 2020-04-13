package com.example.leedonghun.speakenglish;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.NotificationCompat;
//import android.support.v4.app.NotificationManagerCompat;
//import android.support.v4.content.LocalBroadcastManager;
//import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RemoteViews;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.Quota;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import static android.content.Context.MODE_PRIVATE;

/**
 * speakenglish
 * Class: FragmentForTeacherClass.
 * Created by leedonghun.
 * Created On 2019-01-12.
 * Description:
 * 선생님  화면에서  클래스  추가 수정 삭제가 가능 한  리사이클러뷰가 있는 화면이다.
 * 선생님으로 로그인하면  가장 먼저 나오는 default 화면이기도 하다.
 * 그리고 선생님  로그인  상태를  조정할수 있는 화면이기도 하다.
 *
 */
public class FragmentForTeacherClass extends Fragment {


    //선생님 현재 상태 바꿔주는  스위치 뷰
    static Switch onoffswitchButton;//선생님  on/off라인  스위치 버튼
    TextView onofflinetext;//on/ off line  버튼 글 보여주는  텍스트뷰

     //스위치 버튼 누를때 바뀌는 멘트들
     String offline="OffLine";
     String onLine="OnLine";


     String onoffresult="2";//서버로 보낼  선생님 onoff결과 값.
     String loginedid;//현재 로그인한 선생님 아이디 쉐어드로부터 담기위한 string 변수


     //브로드캐스트로  노티  삭제 전달받을때,  -> 현재 myclass프래그먼트가  foreground인지  확인하기 위한  변수
     static Integer i=null;

    //선생님  피드백 리사이클러뷰 관련 객체들
    private teacherfeedbackAdapter teacherfeedbackAdapter;
    private LinearLayoutManager teacherfeedbackLayoytManager;
    private RecyclerView recyclerView_for_feedback;//선생님 피드백  뿌려질  리사이클러뷰 1-4

    private TextView total_rating_score;//전체 별점  평균 점수  2-1
    private RatingBar total_rating_star;//전체 별점 2-2

     //online 노티피케이션  만들때 사용되는 객체들
   private RemoteViews customnotificationview;
   private NotificationManager notificationManager;
   private NotificationCompat.Builder notificaitonbuider;

   //선생님이 받은 피드백  리스트  새로고침 이미지뷰
   private ImageView refresh_btn_for_feedback;//1-1


    //이미지뷰 rotation 효과
   private Animation rotae_imageview;//1-3

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.v("check1", "onattach");


    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("check1", "oncreate");


    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {



        Log.v("check1", "FragmentFOrteacherclass 클래스 onCreateView 실행");

        //프래그먼트  레이아웃  인플레이트해옴.
        ViewGroup rootView=(ViewGroup)inflater.inflate(R.layout.fragment_for_classteacher,container,false);

        //프레그먼트  on/off라인  텍스트 뷰연결
        onofflinetext=rootView.findViewById(R.id.textViewforonofflinement);

        //로그인 on/off 스위치버튼  연결 및 체크 리스너
        onoffswitchButton=rootView.findViewById(R.id.switchforcurrentstate);

        //1-1
        refresh_btn_for_feedback=rootView.findViewById(R.id.refresh_btn1);

        //1-4
        recyclerView_for_feedback=rootView.findViewById(R.id.list_for_class_feedback);

        //2-1
        total_rating_score=rootView.findViewById(R.id.totalrating_score);

        //2-2
        total_rating_star=rootView.findViewById(R.id.total_rating_bar);

        SharedPreferences getid = getActivity().getSharedPreferences("loginteacherid",MODE_PRIVATE);//선생님 로그인 아이디  쉐어드에 담긴거 가져옴
        loginedid= getid.getString("loginidteacher","");//선생님 로그인 아이디 가져옴


        //1-3
        rotae_imageview = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_image_view);

        //1-1 클릭이벤트
         refresh_btn_for_feedback.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                   //이미지뷰 로테이션 효과줌. - 새로고침느낌으로
                   refresh_btn_for_feedback.startAnimation(rotae_imageview);

                   get_teacher_feedback(loginedid,recyclerView_for_feedback);//해당 선생님 피드백들을 가지고 오기위해 선생님 uid 넣어줌.

             }//onClick() 끝

         });//1-1 클릭이벤트 끝


        return rootView;

    }//oncreateview




//해당 선생님  클래스의  피드백을  받아오기 위한 메소드이다.
private  void get_teacher_feedback(String teacheremail,RecyclerView recyclerView_for_feedback){

    Gson gson = new GsonBuilder().setLenient().create();
    Retrofit retrofit=new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create(gson))//json으로 받고 싶으면 쓰면안됨..
            .build();//리트로핏 뷸딩
    ApiService apiService=retrofit.create(ApiService.class);//api인터페이스 크리에이트
    Call<GetTeacherFeedback> getTeacherFeedback =apiService.get_teacher_feedback(teacheremail);

    getTeacherFeedback.enqueue(new Callback<GetTeacherFeedback>() {
        @Override
        public void onResponse(Call<GetTeacherFeedback> call, Response<GetTeacherFeedback> response) {

            ArrayList<JsonObject>teach_feedbacks=response.body().getGet_teacher_feedback();

            float total_rating_average=0.0f;//default 0.0
            for(int i=0; i<teach_feedbacks.size(); i++){//전체  값 돌려서  rating값만  받아서 더함.

                total_rating_average=total_rating_average+teach_feedbacks.get(i).get("rating").getAsFloat();
            }

            total_rating_average=total_rating_average/teach_feedbacks.size();//전체 rating더한거에서  전체 개수 나눠서 평균 rating 점수 가져옴.
            String total_rating=String.format("%.1f", total_rating_average);//소수점 자리수 -> 1자리만 나오게
            total_rating_score.setText(total_rating);//전체 rating평균 점수 넣어줌.
            total_rating_star.setRating(total_rating_average);//ratingbar에  해당 점수로  별점  setting

            //선생님 피드백 보여주는  리사이클러뷰 관련 코드
            teacherfeedbackAdapter=new teacherfeedbackAdapter(getActivity(),teach_feedbacks);
            teacherfeedbackLayoytManager=new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL,false);
            teacherfeedbackLayoytManager.setReverseLayout(false);
            teacherfeedbackLayoytManager.setStackFromEnd(false);
            recyclerView_for_feedback.setLayoutManager(teacherfeedbackLayoytManager);
            recyclerView_for_feedback.setAdapter(teacherfeedbackAdapter);
            recyclerView_for_feedback.setNestedScrollingEnabled(false);//스크롤 부드럽게

        }//onResponse()끝

        @Override
        public void onFailure(Call<GetTeacherFeedback> call, Throwable t) {
            Log.v("check", "FragmentForTeacherClass 의 선생님 feedback들 가져오는데서 에러 나옴. 에러내용-> "+String.valueOf(t));

        }//onFailure()끝
    });


}



//선생님  로그인 상태 디비로부터  현재 상태를 받아오기 위한  메소드이다.
private void status(){

    Retrofit retrofit=new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create())//json으로 받고 싶으면 쓰면안됨..
            .build();//리트로핏 뷸딩
    ApiService apiService=retrofit.create(ApiService.class);//api인터페이스 크리에이트
    Call<getteacheronoffresult> teacheronoff1=apiService.gettheteacheronoffresult(loginedid);

    teacheronoff1.enqueue(new Callback<getteacheronoffresult>() {
        @Override
        public void onResponse(Call<getteacheronoffresult> call, Response<getteacheronoffresult> response) {
            if (response.body() != null) {

                Log.v("check", "선생님 로그인 상태  값 들어옴 =>"+response.body().getTeacheronoff());

                String teacherloginresult=response.body().getTeacheronoff();//서버로부터 받아온  현재 선생님  로그인 상태값

                if(teacherloginresult.equals("0")){//로그인 상태 -> offline
                    Log.v("check", "선생님 로그인 상태 -> offline");
                    onoffswitchButton.setChecked(false);

                }else if(teacherloginresult.equals("1")){//로그인 상태 -> online
                    Log.v("check", "선생님 로그인 상태 -> online");
                    onoffswitchButton.setChecked(true);

                }else if(teacherloginresult.equals("2")){//로그인 상태-> 기존에  아직  데이터가 입력되지 않음 -> 신입 선생일경우 가능함.
                    Log.v("check", "선생님 로그인 상태 -> 아직  onoff상태처리 해보지 않음");
                    onoffswitchButton.setChecked(false);
                }


            }else{

                //reponse.body값이  null값으로 옴.   -> 서버 확인해봐야됨.
                Log.v("check", "선생님 로그인  상태  받아오는 부분에서  서버로부터 null 값이 옴.");
            }
        }

        @Override
        public void onFailure(Call<getteacheronoffresult> call, Throwable t) {

            //선생님 로그인 상태 가져오는 중   trouble생김
            Log.v("check", "선생님 로그인 데이터 가져오는데서  문제 생김 -> "+t);
        }
    });

}//status() 끝

    //현재 선생님 정보를 서버에서 가지고 오기  위한   메소드
    //선생님 uid가 video call 서버에서  필요한  정보 이므로   가져와서 보내준다.
    private void getteacherinfo(String teacheremail){

        Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create())//json으로 받고 싶으면 쓰면안됨..
                .build();//리트로핏 뷸딩
        ApiService apiService = retrofit.create(ApiService.class);//api인터페이스 크리에이트


        Call<teacherinforesult> getteacerinfo=apiService.sendemailtogetteacherprofile(teacheremail);//선생님 정보 요청위한  call 객체 선언

        //선생님 정보  callback 함수 진행.
        getteacerinfo.enqueue(new Callback<teacherinforesult>() {
            @Override
            public void onResponse(Call<teacherinforesult> call, Response<teacherinforesult> response) {
                Log.v("check", "getteacherinfo  response 내용-> "+ response.body().toString());


                if (response.body() != null) {//선생님 정보 가져온  결과값이 null이 아닐겨우.

                    //해당  선생님  uid를
                    String teacheruid=response.body().getTeacheruid();
                    Intent intent=new Intent(getActivity(),ServiceForTeacherGetVideoClassREQUEST.class);
                    intent.putExtra("teacheruid", teacheruid);
                    getActivity().startService(intent);

                }//responsebody값이  null값이 아닌경우.
            }//onResponse 끝

            @Override
            public void onFailure(Call<teacherinforesult> call, Throwable t) {
                Log.v("check", "getteacherinfo onFailure()실행 됨 / failure 내용 -> "+t);

            }//onFailure() 끝
        });

    }//getteacherinfo()끝


    @Override
    public void onResume() {
        super.onResume();
        Log.v("check1", "onresume");

        i=2;//브로드캐스트 리시버 끌때  현재 프래그먼트가  foreground 인지 아닌지 여부 체크하기위해서
        status();//서버로부터 현재 상태 받아오기 위한  메소드

        //스위치 버튼  changelistener->  변경되면 바로  값을 보내  서버 값을 바꾸고  노티를 지우거나 띄운다.
        onoffswitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean ischecked) {

                //선생님 상태 online으로 체크
                if (ischecked) {
                    Log.v("check", "선생님  상태 " + ischecked);
                    onofflinetext.setText(onLine);
                    onoffresult = "1";
                    Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create())//json으로 받고 싶으면 쓰면안됨..
                            .build();//리트로핏 뷸딩
                    ApiService apiService = retrofit.create(ApiService.class);//api인터페이스 크리에이트

                    RequestBody loginemail = RequestBody.create(MediaType.parse("text/plain"), loginedid);
                    RequestBody onoffresultsend = RequestBody.create(MediaType.parse("text/plain"), onoffresult);
                    Call<ResponseBody> teacheronoff = apiService.sendteacherloginresult(onoffresultsend, loginemail);


                    teacheronoff.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Log.v("선생님 상태  online.성공", String.valueOf(response.body()));

                            createnotification();//로그인 상태 알리는 노티피케이션 만들기  메소드
                            notifyteacherlogintofcmserver(loginedid);//fcm서버로   선생님 로그인 사실 알리기위한  메소드
                            getteacherinfo(loginedid);

                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.v("선생님 상태 online.실패", t.toString());

                            Intent intent=new Intent(getActivity(),ServiceForTeacherGetVideoClassREQUEST.class);
                            getActivity().stopService(intent);

                        }
                    });


                    //선생님 상태 offline으로 체크
                } else {
                    Log.v("check", "선생님  상태" + ischecked);
                    onofflinetext.setText(offline);
                    changestatus(getActivity());


                    Intent intent=new Intent(getActivity(),ServiceForTeacherGetVideoClassREQUEST.class);
                    getActivity().stopService(intent);
                }

            }//onCheckedChanged 끝
        });//checkedchangelistener 끝


        //resume에다 놓아서  해당  ->  수업이 끝나고 학생이  resume이 되기전에 먼저 썼다면,
        //피드백이  보일수 있도록  넣음.
        get_teacher_feedback(loginedid, recyclerView_for_feedback);//해당 선생님 피드백들을 가지고 오기위해 선생님 uid 넣어줌.

    }//onResume 끝


    @Override
    public void onPause() {
        super.onPause();
        Log.v("check1", "onpause");
        i=null;//i는  현재  mycalss프래그먼트가  foreground인지 여부를 확인하는 값임.  null이면 forground가 아님.

    }//onpause끝


    //선생님 online상태를  토글버튼으로 눌렀을때  -> 노티를  띄우기 위한 조치이다.
    private void createnotification(){

     customnotificationview=new RemoteViews(getActivity().getPackageName(),R.layout.custom_teacherlogin_notification);

     notificaitonbuider=new NotificationCompat.Builder(getActivity(), "teacheronoffnoti");

     notificaitonbuider
            .setSmallIcon(R.drawable.sayhellologoblack)
            .setCustomContentView(customnotificationview)
            .setOngoing(true);

    notificationManager=(NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
        notificationManager.createNotificationChannel(new NotificationChannel("teacheronoffnoti","loginstatusnotification",NotificationManager.IMPORTANCE_DEFAULT));

    }




    Intent intent=new Intent(getActivity(),MainactivityForTeacher.class);//노티를 누르면 페이지 다시 시작하기 위해서
    Intent intentforchagneloginoff=new Intent(getActivity(),Broadcastreciever.class);//로그인 상태  바꿔주기 위해 버튼 클릭  정보를 받아  해당 receiver로 전해주기위한 intent



    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);

    PendingIntent pendingIntent=PendingIntent.getActivity(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    notificaitonbuider.setContentIntent(pendingIntent);//



    PendingIntent pendingIntent2=PendingIntent.getBroadcast(getActivity(), 0, intentforchagneloginoff, 0);
    customnotificationview.setOnClickPendingIntent(R.id.btnforonoffline, pendingIntent2);



    if (notificationManager != null) {//notificationmanager  null아니라면
        notificationManager.notify(1, notificaitonbuider.build());
    }

}//create notification () 끝


 //선생님  로그인 상태  online으로 변경시에  fcm보내는  서버코드로 메세지  알려야됨.
 private void notifyteacherlogintofcmserver(String teacheremail){

        Log.v("check", "FragmentForTeacherClass 프래그먼트의   notifyteacherlogintofcmserver() 실행됨");


        //retrofit 준비
        Retrofit retrofit=new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create()).build();
        ApiService apiServie=retrofit.create(ApiService.class);

        //현재 로그인한 선생님 이메일
        RequestBody teacheremeail=RequestBody.create(MediaType.parse("text/plain"), teacheremail);

        //fcm서버로  현재 로그인  선생님  이메일  보내기
        Call<ResponseBody> sendteacherlogintofcmserver=apiServie.notifyteacherlogined(teacheremeail);

        //sendteacherloginofcmserver  콜백  메시지
        sendteacherlogintofcmserver.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    Log.v("check", "선생님 로그인 사실  fcm서버로  보낸부분  response 메세제 -> "+result);

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }//onResponse 끝

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

             Log.v("check", "선생님 로그인  사실   fcm 서버로 보내기 실패  에러-> "+t);

            }//onFailure끝

        });//fcm서버에 선생님 로그인 알리기  callback 끝


    }//notifyteacherlogintofcmserver () 끝





 //리시버로  offline 요청을 받을때 서버측  데이터 수정하기 위해 필요한  메소드이다.
private static void changestatus(final Context context){

    String onoffresult="0";
    Retrofit retrofit=new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create())//json으로 받고 싶으면 쓰면안됨..
            .build();//리트로핏 뷸딩
    SharedPreferences getid = context.getSharedPreferences("loginteacherid",MODE_PRIVATE);//선생님 로그인 아이디  쉐어드에 담긴거 가져옴
    String loginedid= getid.getString("loginidteacher","");//선생님 로그인 아이디 가져옴

    ApiService apiService=retrofit.create(ApiService.class);//api인터페이스 크리에이트
    RequestBody loginemail=RequestBody.create(MediaType.parse("text/plain"),loginedid);
    RequestBody onoffresultsend=RequestBody.create(MediaType.parse("text/plain"),onoffresult);
    Call<ResponseBody> teacheronoff1=apiService.sendteacherloginresult(onoffresultsend,loginemail);

    teacheronoff1.enqueue(new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            Log.v("check","선생님 상태 offline.성공"+ response.body());
            NotificationManagerCompat.from(context).cancel(1);//서버값 성공적으로 바꾸면  노티 삭제해줌.
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            Log.v("check","선생님 상태 offline.실패"+ t.toString());
        }
    });



    }//changestatus()끝



    //브로드캐스트 리시버로   노티를   엑티비티가  태스크에 없을때도  사라지게 해준다.
public static class Broadcastreciever extends BroadcastReceiver{


    @Override
    public void onReceive(Context context, Intent intent) {

        if(i==null){//i가  null일경우는 프래그먼트중  myclass프래그먼트가  foreground가 아닐때 진행된다.  -> 이경우는  스위치 버튼 값 변경으로 바꿀수 없기때문에
                    //서버로  값을 바꿔주는  행위를 한다.

            changestatus(context);//changestatus()-> 서버 값을 바꿔줌.

            Log.v("과연", "뭘까1");
        }else if(i==2){
            onoffswitchButton.setChecked(false);
            Log.v("과연", "뭘까2");
        }


    }//onReceive
}//inner broadcasterreciever 끝




}//프레그먼트 끝끝
