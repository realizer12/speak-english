package com.example.leedonghun.speakenglish;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
//import android.support.v7.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * speakenglish
 * Class: TeacherProfile.
 * Created by leedonghun.
 * Created On 2019-09-16.
 * Description: 학생이  선생님 리스트에서 선생님을 눌렀을때  가지는  선생님 프로필 엑티비티이다.
 * 이곳에서  선생님이  작성한  자세한  선생님 이력을 볼수 있으며,  선생님과  수업하기.  채팅하기.  선생님의  오프채팅방에 들어갈수가 있다.
 * 또한 선생님의 평점을 볼수 있으며, 선생님과의 수업을 예약하고,  선생님을  My Class에 등록이 가능하다.
 * 또한  선생님 로그인시  알람 받기 설정도 가능하다.
 */
public class TeacherProfile extends AppCompatActivity {


    //현재 엑티비티 스크롤뷰
    ScrollView TeacherProfileScroll;

    //이름이랑  글로벌여부  들어가는 텍스트뷰
    TextView showteachernameandglobalornot;

    //선생님 프로필 들어가는  이미지뷰
    ImageView teacherprofilephoto;

    //수업 시작 버튼
    Button startclassbtn;

    //선생님과 1:1 채팅
    Button onetoonechattinbtn;

    //openchatting 버튼
    Button openchattingbtn;

    //선생님 짧은 한마디 들어가는 텍스트뷰
    TextView teachershortsentence;

    //선생님  알람 받기 -> 체크 효과 이미지
    ImageView checkedalarm;//체크됨
    ImageView uncheckedalarm;//체크안됨

    //선생님  프로필 tabhos
    TabHost teacherprofiletabhost;

    //선생님 career 텍스트
    TextView teachercareertextview;

    //선생님  학생들에게 하고 싶은말 텍스트
    TextView teachersayhellowtostudent;

    //선생님  프로필 엑티비티 toolbar
    Toolbar teacherprofiletoolbar;

    //선생님  myclass에 넣기 하트 버튼
    ImageView checkedhearbtnwhite;//체크됨
    ImageView uncheckedhearbtnwhite;//체크안됨.


    //선생님  설명칸 에서  제목 부분 who is ooo tutor? ooo부분  바꾸기 위해서
    TextView mentforintroduceteacher;

    //선생님  데이터 정보 받아온거  뿌리기위해  데이터 담을 변수들
    String teachernameglobalchecktext;//선생님 글로벌 네이티브 체크
    URL teacherprofileurl;//선생님 프로필 이미지
    String teachernamement;//선생님  이름  들어가는 who is ooo teacher? 멘트
    String teachername;//선생님 이름
    String teachershorsentencephrase;//선생님  shortsentence
    String teachercareercontent;//선생님 경력 사항
    String teachersayhellowcontent;//선생님  하고 싶은말  한생들에게


    //커스톰 토스트
    Toastcustomer toastcustomer=new Toastcustomer(TeacherProfile.this);

    //현재 로그인된  학생  아이디 받기 위한 스트링 변수
    String loginedid;
    String teacherid;

    String teacheremail;

    private String profileurl;//videocall connecting 엑티비티에 보낼 string url

    //선생님 mytutor등록용
    private Retrofit retrofit;//리트로핏 선언
    private ApiService apiService;//api service 인터페이스

    //선생님  로그인 알람 등록용
    private Retrofit retrofitforgetteacheralarm;
    private ApiService apiServiceforgetteacheralarm;


    //선생님 피드백 관련 뷰-------------

    //선생님 전체 별점
    private RatingBar total_rating_bar;

    //선생님 전체 평균 점수 텍스트뷰
    private TextView total_rating_score_txt;

    //선생님  피드백 리사이클러뷰 관련 객체들
    private teacherfeedbackAdapter teacherfeedbackAdapter;
    private LinearLayoutManager teacherfeedbackLayoytManager;
    private RecyclerView recyclerView_for_feedback;//선생님 피드백  뿌려질  리사이클러뷰 1-4


    //선생님 정보  어디서 클릭해서 왔는지  확인 하는 변수.
    int teacherinfofromcheck;

    //선생님 수업 예약하기 관련 뷰----------------------------------

    //학생이 선생님 수업을  하나 이상  예약했을때,  해당 예약한  시간을 다시  취소 하는 방법을  예약 버튼 밑에  띄어줌.  5-1
    private TextView txt_for_ment_for_explain_cancel_reservation_time;

    //선생님이 지정한  수업 가능 시간 리스트를 보여주기 위한  버튼  뷰 - 누르면  가능 시간들이 리스트로  다이얼로그에 나옴. 5-2
    private Button btn_for_show_available_time_list;

    //학생이 예약한  수업 리스트 담길 리사클러뷰.  5-3
    private RecyclerView recyclerview_for_reservation_time_list;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacherprofile);


        //안드로이드  strict 모드  적챙   허용해준다.
        // strinct 모드는  메인 쓰레드에서   네트워크  작업등이 실행될때,  이를 감지하여,  강제종료등을 진행해준다
        //그런데 현재 엑티비티에서 -> 수업 시작 버튼 누를때,
        // 선생님 로그인 onoff확인해서  들어가는  CheckTeacherStatusForViedoClass에서 -> 해당  네트워크를  동기화로 처리해놔서
        //현재 teacherprofile엑티비티 메인 쓰레드에서 네트워크작업이 진행된걸로  감지되나봄
        //그래서 아래와 같이  strict모드를  허용하는  코드를 넣어줌.
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }



        Log.v("check", getLocalClassName()+"의  onCreate 실행됨");

        TeacherProfileScroll=findViewById(R.id.realscrollView3);//엑티비티 스크롤뷰
        showteachernameandglobalornot=findViewById(R.id.realnameandglobalcheck);//선생님 이름과 global 여부 텍스트뷰
        teacherprofilephoto=findViewById(R.id.realprofileimage);//선생님  프로필 사진 이미지뷰
        startclassbtn=findViewById(R.id.realstartclassbutton);//수업 시작 버튼
        openchattingbtn=findViewById(R.id.realopenchattingbutton);//오픈 채팅 버튼
        onetoonechattinbtn=findViewById(R.id.realmessengerbutton);//1대1 채팅 버튼
        teachershortsentence=findViewById(R.id.realonesentenceforstudent);//선생님 shortsentence 텍스트뷰;

        checkedalarm=findViewById(R.id.realcheckedalarmlogin);//알람이  체크됨
        uncheckedalarm=findViewById(R.id.realuncheckedalarmlogin);//알람이 체크되지 않음.

        teacherprofiletabhost=findViewById(R.id.realtabhostforpreview);//teacherprofile tabhost부분
        teachercareertextview=findViewById(R.id.realcareertextbox);//선생님 커리어 텍스브뷰
        teachersayhellowtostudent=findViewById(R.id.realdearstudenttextbox);//선생님  학생들에게 하고 싶은말  텍스트뷰

        teacherprofiletoolbar=(Toolbar) findViewById(R.id.realtoolbarteacherprofile);//선생님  프로필 툴바

        checkedhearbtnwhite=findViewById(R.id.realwhiteheartentire);//선생님  하트하얀색  전체
        uncheckedhearbtnwhite=findViewById(R.id.realwhiteheart);//선생님 하트 테두리 전체

        mentforintroduceteacher=findViewById(R.id.realwhoisteachermenttext);//선생님  소개 멘트 텍스트뷰

        recyclerView_for_feedback=findViewById(R.id.list_for_class_feedback_tab);//선생님 받은 피드백들 담길 리사이클러뷰
        total_rating_bar=findViewById(R.id.total_rating_bar_tab2);//전체  rating 담길  rating bar
        total_rating_score_txt=findViewById(R.id.totalrating_score_tab2);//전체 rating 담길  텍스트뷰


        //수업 예약 관련 뷰
        txt_for_ment_for_explain_cancel_reservation_time=findViewById(R.id.txt_for_show_ment_for_explain_cancel_reserved_time);//5-1
        txt_for_ment_for_explain_cancel_reservation_time.setVisibility(View.GONE);//처음에 시작할때  GONE처리 해놓고,  해당  리스트 가져올때  리스트 수가 1이상이면  VISIBLE 처리해줌.

        btn_for_show_available_time_list=findViewById(R.id.btn_availabletime_list);//5-2

        recyclerview_for_reservation_time_list=findViewById(R.id.recyclerview_for_show_reserved_time_list);//5-3
        recyclerview_for_reservation_time_list.setVisibility(View.GONE);//처음에 시작할때 GONE처리 해놓고,  해당  리스트 가져올때 리스트 수가  1이상이면 VISIBLE처리





        SharedPreferences getid = getSharedPreferences("loginstudentid",MODE_PRIVATE);
        loginedid= getid.getString("loginid","");

        Intent getteacherinfo=getIntent();//인텐트 받기
        String teacherinfojsonstring=getteacherinfo.getStringExtra("teacherinfo");//스트링으로 넘겼어서 스트링으로 받음.

        //데이터 출처  리사이클러뷰  리스트 체크 0-> 전체 선생,  1-> 로그인한 선생
        teacherinfofromcheck=getteacherinfo.getIntExtra("teacherinfocheck", -1);


        //선생님  데이터 온 출처 체크  0->  전체 선생님 리스트에서 왔을때
        if(teacherinfofromcheck==0) {
            try {
                JSONObject jsonObject = new JSONObject(teacherinfojsonstring);//스트링 json다시  Json으로  변환
                Log.v("check", "json으로 변환됨 -> " + jsonObject);


                //내튜터 등록시 서버로 보낼  선생님 uid.
                teacherid=jsonObject.get("teacherUID").toString().replaceAll("\"","");

                //선생님 이름.
                teachername=jsonObject.get("teacherNAME").toString().replaceAll("\"","");

                //선생님 이메일
                teacheremail=jsonObject.get("teacherID").toString().replaceAll("\"","");

                //네이티브 =1,  gloabal=0이므로 그에 맞춰서  나눠줌.
                if (jsonObject.get("teacherNativeOrNot").equals("1")) {//native경우
                    teachernameglobalchecktext = jsonObject.get("teacherNAME") + "/native";//선생님 이름 및 글로벌 여부 들어가는 텍스트뷰에 쓰일거임.
                    teachernamement = "Who is '" + jsonObject.get("teacherNAME") + "' tutor??";//선생님   who is alen teacher? 부분에 들어갈멘트

                } else if (jsonObject.get("teacherNativeOrNot").equals("0")) {//글로벌 경우
                    teachernameglobalchecktext = jsonObject.get("teacherNAME") + "/global";
                    teachernamement = "Who is '" + jsonObject.get("teacherNAME") + "' tutor??";
                }
                profileurl="http://13.209.249.1/" + jsonObject.get("teacherPHOTOpath");//url스트링으로 받음.
                teacherprofileurl = new URL("http://13.209.249.1/" + jsonObject.get("teacherPHOTOpath"));//해당 이미지 url 받아옴
                teachershorsentencephrase = jsonObject.get("teachershorsentence").toString();
                teachercareercontent = jsonObject.get("teachercareer").toString();
                teachersayhellowcontent = jsonObject.get("teachersayhellow").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }else if(teacherinfofromcheck==1){ //선생님  데이터 온 출처 체크  1->  로그인한 선생님 리스트에서 왔을때

            try {
            JSONObject jsonObject = new JSONObject(teacherinfojsonstring);//스트링 json다시  Json으로  변환
            Log.v("check", "json으로 변환됨 -> " + jsonObject);


            //내튜터 등록시 서버로 보낼  선생님 uid.
            teacherid=jsonObject.get("id").toString().replaceAll("\"","");

            //선생님 이름.
            teachername=jsonObject.get("name").toString().replaceAll("\"","");

            //선생님 이메일
            teacheremail= jsonObject.get("email").toString().replaceAll("\"","");

            //네이티브 =1,  gloabal=0이므로 그에 맞춰서  나눠줌.
            if (jsonObject.get("nativeornot").equals("1")) {//native경우

                teachernameglobalchecktext = jsonObject.get("name") + "/native";//선생님 이름 및 글로벌 여부 들어가는 텍스트뷰에 쓰일거임.
                teachernamement = "Who is '" + jsonObject.get("name") + "' tutor??";//선생님   who is alen teacher? 부분에 들어갈멘트

            }else if (jsonObject.get("nativeornot").equals("0")) {//글로벌 경우

                teachernameglobalchecktext = jsonObject.get("name") + "/global";
                teachernamement = "Who is '" + jsonObject.get("name") + "' tutor??";
            }

            profileurl="http://13.209.249.1/" + jsonObject.get("profilepath");//url스트링으로 받음.
            teacherprofileurl = new URL("http://13.209.249.1/" + jsonObject.get("profilepath"));//해당 이미지 url 받아옴
            teachershorsentencephrase = jsonObject.get("shortsentence").toString();
            teachercareercontent = jsonObject.get("teachercareer").toString();
            teachersayhellowcontent = jsonObject.get("teachersayhellow").toString();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
                e.printStackTrace();

        }

       }else if(teacherinfofromcheck==3){//선생님 데이터 온 출처 값  3일때 ->  선생님 로그인 노티피케이션에서 옴.
                                         // 또는 채팅방에서 선생님 프로필 클릭하고,  프로필 홈 버튼 눌렀을때  3값으로 와짐.

            try {
                JSONObject jsonObject = new JSONObject(teacherinfojsonstring);//스트링 json다시  Json으로  변환
                Log.v("check", "json으로 변환됨 -> " + jsonObject);

                //내튜터 등록시 서버로 보낼  선생님 uid.
                teacherid=jsonObject.get("teacheruid").toString().replaceAll("\"","");

                //선생님 이름.
                teachername=jsonObject.get("teachername").toString().replaceAll("\"","");

                //3번으로 프로필 접근할때는  이메일 안가지고 오게 해놓음 이거 처리해야됨.
                //teacheremail

                //네이티브 =1,  gloabal=0이므로 그에 맞춰서  나눠줌.
                if (jsonObject.get("teachernativeornot").equals("1")) {//native경우

                    teachernameglobalchecktext = jsonObject.get("teachername") + "/native";//선생님 이름 및 글로벌 여부 들어가는 텍스트뷰에 쓰일거임.
                    teachernamement = "Who is '" + jsonObject.get("teachername") + "' tutor??";//선생님   who is alen teacher? 부분에 들어갈멘트

                }else if (jsonObject.get("teachernativeornot").equals("0")) {//글로벌 경우

                    teachernameglobalchecktext = jsonObject.get("teachername") + "/global";
                    teachernamement = "Who is '" + jsonObject.get("teachername") + "' tutor??";
                }

                teacherprofileurl = new URL("http://13.209.249.1/" + jsonObject.get("profilepath"));//해당 이미지 url 받아옴
                teachershorsentencephrase = jsonObject.get("shortsentence").toString();
                teachercareercontent = jsonObject.get("career").toString();
                teachersayhellowcontent = jsonObject.get("sayhellow").toString();


            } catch (JSONException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }


        }
        //선생님  데이터 온 출처 체크  1->  로그인한 선생님 리스트에서 왔을때 끝


        Glide.with(TeacherProfile.this).load(teacherprofileurl).into(teacherprofilephoto);//선생님  프로필 사진  -> 글라이드로 넣어줌,


        showteachernameandglobalornot.setText(teachernameglobalchecktext);//선생님 이름이랑  native여부
        mentforintroduceteacher.setText(teachernamement);//선생님  who is alen teacher textview에  이름으로 넣어줌.

        //선생님 간단소개 문장 보이기
        //한줄 소개가 null일경우  지정된  멘트 날림.  ('한줄소개가 없어요')
        if(teachershorsentencephrase.equals("null")||teachershorsentencephrase.equals("")){
            teachershortsentence.setText("한줄 소개가 없어요 ㅠ");
        }else{
            teachershortsentence.setText(teachershorsentencephrase);
        }
        //간단소개 문장 보이기 끝.

        //선생님 인사말
        if(teachersayhellowcontent.equals("null")||teachersayhellowcontent.equals("")){

            teachersayhellowtostudent.setText("선생님이 아직 글을 안올리셨어요 ㅠ");
        }else {
            teachersayhellowtostudent.setText(teachersayhellowcontent);
        }


        //선생님 경력
        if(teachercareercontent.equals("null")||teachercareercontent.equals("")){
            teachercareertextview.setText("선생님이 아직 글을 안올리셨어요 ㅠ");
        }else{

            teachercareertextview.setText(teachercareercontent);
        }




        buttonclicklistener();//버튼들  클릭리스너 따로 모아둔  메소드

        tabhost();//탭호스트


        //선생님 수업 시작  버튼
        startclassbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //해당 선생님  로그인 여부  알아봄.
                boolean result=new CheckTeacherStatusForViedoClass(teacherid,TeacherProfile.this).check();

                if(result){//선생님  로그인 했을때,
                    Log.v("check", getLocalClassName()+"의 선생님 수업 연결 엑티비티로 감. ");

                    //비디오 연결전  연결 중임을 나타내는  엑티비티로 감.
                    Intent intent=new Intent(TeacherProfile.this,VideoCallConnectingActivity.class);
                    intent.putExtra("teacheruid", teacherid);//선생님 uid
                    intent.putExtra("teachername", teachername);//선생님 이름.
                    intent.putExtra("profileimg", profileurl);//선생님 사진 url
                    
                    startActivity(intent);


                }else{//선생님 로그인 안했을떄

                    Log.v("check", getLocalClassName()+"의 선생님 수업 연결 실패함.. ");
                    new Toastcustomer(TeacherProfile.this).showcustomtaost(null, "선생님이 OffLine 상태입니다.!");

                }

            }//onClick() 끝
        });//startclassbtn 버튼 끝.




        //선생님 프로필 사진 눌렀을때  클릭리스너
        //누르면  사진만 보이는 엑티비티들어가서 사진확대 가능하게 만들기.
        teacherprofilephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              //Teacher_Profile_Photo_magnify.class로 ㄱㄱ
              Intent gotomagnifiedteacherprofileactivity=new Intent(TeacherProfile.this,Teacher_Profile_Photo_magnify.class);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Bitmap bitmap=((BitmapDrawable)teacherprofilephoto.getDrawable()).getBitmap();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                gotomagnifiedteacherprofileactivity.putExtra("image",byteArray);//비트맵 바이트 어레이로 compress하여  보내줌.  ->


                startActivity(gotomagnifiedteacherprofileactivity);//해당 엑티비티 시작.
            }
        });
        //선생님 프로필 사진 클릭 리스너 끝

       //현재 구현해야되는 부분은  스크롤뷰에서 스크롤이  맨위로가면  툴바가  투명해지고
       //다른 위치로 가게되면 툴바가  다시  파란색으로 돌아오는 것을 구현해야한다.
       TeacherProfileScroll.post(new Runnable() {
            @Override
            public void run() {

               TeacherProfileScroll.scrollTo(0,0);//맨위로 위치 설정
                Log.v("check",getLocalClassName()+"의  처음시작 스크롤 위치 ->0,0");
            }
        });




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//api 23 이상인데  현재  gradle에  최소 버전  16으로 해놔서  이러게  조건문 걸어줘야됨.
            TeacherProfileScroll.setOnScrollChangeListener(new ScrollView.OnScrollChangeListener() {//스크롤 리스너 이다.
                @Override
                public void onScrollChange(View view, int scrollX, int scrollY, int oldScrollx, int oldscrolly) {
                    if (scrollY <10) {//스크롤의 위치 범위가 10이하이면//투명도가 적용됨.

                        teacherprofiletoolbar.setBackgroundColor(Color.parseColor("#001863C4"));
                        Log.v("check",getLocalClassName()+"의 스크롤위치가 맨위입니다.");


                    }else if(scrollY>10){//스크롤의 범위가 10이상으로 올라가면  투명도가 풀림

                        teacherprofiletoolbar.setBackgroundColor(Color.parseColor("#1863c4"));
                        Log.v("check",getLocalClassName()+"의  스크롤위치가 맨위가 아닙니다.");
                    }


                }
            });//스크롤 리스너 끝
        }//버전  조건문  끝





        //선생님 오픈채팅 버튼 눌렀을때 이벤트
        openchattingbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("check", getLocalClassName()+"의  오픈채팅방 버튼  눌림");

                //오픈채팅방 엑티비티로  넘어간다.
                Intent gotoopenchattingroomintent=new Intent(TeacherProfile.this,ChattingRoomActivityForStudent.class);
                gotoopenchattingroomintent.putExtra("teacheruid", teacherid);//오픈채팅방에  선생님 아이디보냄
                gotoopenchattingroomintent.putExtra("studentemail", loginedid);//오픈 채팅방에 학생 본인 이메일 보냄.
                gotoopenchattingroomintent.putExtra("chattingroomtype", 0);  //오픈 채팅  인지  1대일  채팅인지 여부를  채팅방으로 보내준다.   /1이면  일대일 채팅방,   0이면  오픈 채팅방


                //이걸 넣은 이유는 ->  만약에  채팅  프로필에서  프로필  엑티비티로 넘어와서
                //해당  채팅방을  들어가면,  기존에  채팅방 스택이 2번 쌓이는 결과 가 나옴.
                //하지만 아래 플래그를 쓰게 되면, 기존 스택에 있는 같은  엑티비티로 가지고 그 위  엑티비티들을 지워준다.
                //이부분에서 ->  채팅방이  여러개 겹치는 것을  막을 수 있음
                gotoopenchattingroomintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


                startActivity(gotoopenchattingroomintent);//엑티비티 실행.


            }//onClick끝
        });//선생님 오픈 채팅 버튼 이벤트 끝.

        //선생님 1대1 채팅방 버튼 눌렀을때 이벤트
        onetoonechattinbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //1대 1 채팅방 엑티비티로  넘어간다.
                Intent gotoopenchattingroomintent=new Intent(TeacherProfile.this,ChattingRoomActivityForStudent.class);
                gotoopenchattingroomintent.putExtra("teacheruid", teacherid);//1대1 채팅 방에  선생님 아이디보냄
                gotoopenchattingroomintent.putExtra("teachername", teachername);//1대1 채팅방에  채팅방 제목으로 쓰일  선생님 이름을 보냄.
                gotoopenchattingroomintent.putExtra("studentemail", loginedid);//1대1 채팅방에 학생 본인 이메일 보냄.
                gotoopenchattingroomintent.putExtra("chattingroomtype", 1);  //오픈 채팅  인지  1대일  채팅인지 여부를  채팅방으로 보내준다.   /1이면  일대일 채팅방,   0이면  오픈 채팅방


                //이걸 넣은 이유는 ->  만약에  채팅  프로필에서  프로필  엑티비티로 넘어와서
                //해당  채팅방을  들어가면,  기존에  채팅방 스택이 2번 쌓이는 결과 가 나옴.
                //하지만 아래 플래그를 쓰게 되면, 기존 스택에 있는 같은  엑티비티로 가지고 그 위  엑티비티들을 지워준다.
                //이부분에서 ->  채팅방이  여러개 겹치는 것을  막을 수 있음
                gotoopenchattingroomintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(gotoopenchattingroomintent);//엑티비티 실행.


            }//onClick끝
        });//선생님 1대1 채팅 버튼 눌렀을때 이벤트 끝




        //선생님 프로필 엑티비티 진행할때  -1값을 넣으면,  해당  데이터를 읽어와  뷰를  데이터에 따라 바꿔준다.
        //선생님 등록 여부
        registermyteacher("-1");
        //선생님  알람 여부
        registerteachertogetloginalarm("-1");


        //toolbar 관련 코드
        setSupportActionBar(teacherprofiletoolbar);//툴바에  액션바  서포트 연결
        getSupportActionBar().setDisplayShowTitleEnabled(false);//엑션바에서 타이틀 안보이게함
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//액션바에서 Homeashup 버튼 가능하게함
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.whiteback);//해당 버튼  뒤로가기 버튼모양 으로  이미지 지정




        //5-2 해당  선생님 이름 넣어줌.
        btn_for_show_available_time_list.setText(teachername+" 선생님 수업 예약하기");

        //5-2 클릭이벤트
        btn_for_show_available_time_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.v("check", getLocalClassName()+"의 btn_for_show_available_time_list버튼 클릭됨");

                //수업 가능 시간 리스트  다이얼로그 로 보여줌 - 파라미터로  선생님 이름 가지고감.
                show_availabletimelist_dialog(teachername,teacherid);

            }
        });//5-2 클릭이벤트 끝.


        //해당 학생이  예약한  수업리스트를 가지고와  teacherprofile -> reservation부분에  뿌려준다.
        get_student_reserved_time_list(recyclerview_for_reservation_time_list,teacherid);

    }//oncreate 끝


    //학생이  예약한  수업 리스트 가져오기  -> 이때는 해당 학생의 uid까지 같이 가져가서  가져온다.
    private  void get_student_reserved_time_list(RecyclerView recyclerView_for_show_reservedtime_list,String teacheruid){

        GlobalApplication globalApplication=(GlobalApplication)getApplicationContext();

        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit=new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create(gson))//json으로 받고 싶으면 쓰면안됨..
                .build();//리트로핏 뷸딩
        ApiService apiService=retrofit.create(ApiService.class);//api인터페이스 크리에이트
        Call<GetStudentReservedClassTime>get_reserved_class_time=apiService.get_student_available_class_time(globalApplication.getStudnet_uid(),teacheruid);


        get_reserved_class_time.enqueue(new Callback<GetStudentReservedClassTime>() {
            @Override
            public void onResponse(Call<GetStudentReservedClassTime> call, Response<GetStudentReservedClassTime> response) {

                ArrayList<JsonObject> reserved_class_list=response.body().getGet_reserved_class_time();
                Log.v("CHECKSGGSDF", String.valueOf(reserved_class_list));

                if(reserved_class_list.size()>0){//예약한 수업 리스트가 있을때  예약한 수업 리스트 들어가는 리사이클러뷰 visible 처리

                     Log.v("check", getLocalClassName()+"의 예약한  수업 리스트가 1이상일때 해당 리사이클러뷰-> visible");


                    LinearLayoutManager linearLayoutManager_for_reserved_class_time_list;//다이얼로그에 뿌려질 수업 가능한  시간 리스트 리사이클러뷰 담당할  레이아웃 메니져

                    show_reserved_time_to_std_adapter show_reserved_time_to_std_adapter;//수업 가능한  시간 리스트 리사이클러뷰에 뿌려줄  어뎁터

                    //선생님이  지정한  available 클래스 시간 리스트로 보기 위한 리사이클러뷰 관련 코드.
                    show_reserved_time_to_std_adapter=new show_reserved_time_to_std_adapter(TeacherProfile.this,reserved_class_list,txt_for_ment_for_explain_cancel_reservation_time,teacheruid,recyclerView_for_show_reservedtime_list);
                    linearLayoutManager_for_reserved_class_time_list=new LinearLayoutManager(TeacherProfile.this,RecyclerView.VERTICAL,false);
                    linearLayoutManager_for_reserved_class_time_list.setReverseLayout(false);
                    linearLayoutManager_for_reserved_class_time_list.setStackFromEnd(false);

                    recyclerView_for_show_reservedtime_list.setLayoutManager(linearLayoutManager_for_reserved_class_time_list);
                    recyclerView_for_show_reservedtime_list.setAdapter(show_reserved_time_to_std_adapter);

                    recyclerView_for_show_reservedtime_list.setNestedScrollingEnabled(false);//스크롤 부드럽게


                    //리사이클러뷰 보여주고,  밑에  취소 멘트 날림.
                    recyclerView_for_show_reservedtime_list.setVisibility(View.VISIBLE);
                    txt_for_ment_for_explain_cancel_reservation_time.setVisibility(View.VISIBLE);

                }else{//예약한 수업리스트가 없을때 -> 예약한 수업 리스트 들어가는 리사이클러뷰 GONE처리

                     Log.v("check", getLocalClassName()+"의 예약한  수업리스트가  0이하일때 해당 리사이클러뷰 ->Gone");

                    recyclerView_for_show_reservedtime_list.setVisibility(View.GONE);
                    txt_for_ment_for_explain_cancel_reservation_time.setVisibility(View.GONE);
                }


            }

            @Override
            public void onFailure(Call<GetStudentReservedClassTime> call, Throwable t) {
                Log.v("check", t.getMessage());
            }
        });

    }//get_student_reserved_time_list() 끝



    //해당 선생님이  만들어놓은  수업 가능  리스트를  보여주는 다이얼로그 show하기 위한 메소드
    private  void show_availabletimelist_dialog(String teachername,String teacheruid){

        Log.v("check", getLocalClassName()+"의 show_availabletimelist_dialog() 실행됨");

        final Dialog dialog_for_show_availabletime = new Dialog(TeacherProfile.this);

        // 액티비티의 타이틀바를 숨긴다.
        dialog_for_show_availabletime.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog_for_show_availabletime.setCancelable(false);//취소 가능여부 false

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dialog_for_show_availabletime.setContentView(R.layout.show_avaialble_reserve_time_dialog_for_student);

        WindowManager.LayoutParams params = dialog_for_show_availabletime.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;//dialog width 늘려줌.
        dialog_for_show_availabletime.getWindow().setAttributes((WindowManager.LayoutParams) params);

         Button btn_for_cancel_dialog=dialog_for_show_availabletime.findViewById(R.id.cancel_btn_for_std_reserve_time_dialog);//다이얼로그 취소 버튼14-1
         Button btn_for_ok_dailog=dialog_for_show_availabletime.findViewById(R.id.ok_btn_for_std_reserve_time_dialog);//다이얼로그 ok버튼  14-2

         RecyclerView recyclerView_for_show_available_time_list=dialog_for_show_availabletime.findViewById(R.id.recycler_view_for_available_time_list_for_student);//해당 선생님 예약 가능 시간 리스트가 담길 리사이 클러뷰 14-3

         TextView txt_for_show_dialog_title=dialog_for_show_availabletime.findViewById(R.id.txt_for_dialog_title_in_std_available_time_dialog);//해당 다이얼로그 제목14-4

         TextView txt_for_ment_for_dialog=dialog_for_show_availabletime.findViewById(R.id.ment_for_no_available_time_txt);//해당 선생님  예약 가능 리스트 없을때 나오는 멘트 14-5





         //해당 다이얼로그  제목  넣어줌.
         txt_for_show_dialog_title.setText(teachername+"쌤  예약 가능한 수업 리스트");//14-4

         //해당 리스트 보여주기
        get_available_reserve_time_list(teacheruid,recyclerView_for_show_available_time_list,txt_for_ment_for_dialog,btn_for_ok_dailog,dialog_for_show_availabletime);


        //다이얼로그 보여주기
         dialog_for_show_availabletime.show();

         //14-1 취소 버튼 클릭 리스너
          btn_for_cancel_dialog.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  Log.v("check", getLocalClassName()+"의  선생님 예약 가능 리스트 다이얼로그 취소 버튼 눌림");


                  //다이얼로그 닫아줌.
                  dialog_for_show_availabletime.dismiss();

              }
          });//14-1 끝


    }//show_availabletimelist_dialog()끝끝



    //서버에서 해당 선생님의  예약 가능 리스트를  가지고온다.
    private void get_available_reserve_time_list(String teacheruid,RecyclerView recyclerView_for_show_available_time_list,TextView txt_view_for_ment_no_time,Button btn_for_okbtn,Dialog dialog_for_show_availabletime){



        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit=new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create(gson))//json으로 받고 싶으면 쓰면안됨..
                .build();//리트로핏 뷸딩
        ApiService apiService=retrofit.create(ApiService.class);//api인터페이스 크리에이트
        Call<GetTeacherAvailableClassTime>get_available_class_time=apiService.get_teacher_available_class_time(teacheruid);

        get_available_class_time.enqueue(new Callback<GetTeacherAvailableClassTime>() {
            int count=0;
            @Override
            public void onResponse(Call<GetTeacherAvailableClassTime> call, Response<GetTeacherAvailableClassTime> response) {
                ArrayList<JsonObject> list_for_available_clas=response.body().getGet_available_class_time();
                Log.v("check",getLocalClassName()+"의 선생님이 준비한 리스트 가져오기 성공  가져온 리스트->"+list_for_available_clas);

                 int count_available_class_size=0;//수업가능 시간 카운트

                for(int i=0; i<list_for_available_clas.size(); i++) {

                  int status = list_for_available_clas.get(i).get("reserve_status").getAsInt();//해당  예약시간 상태 값

                  if (status == 0) {//0이면  예약 한 사람이 없는 시간이므로  카운트가 세어진다.

                       count_available_class_size=count_available_class_size+1;
                   }
                }

        if(count_available_class_size>0){//해당  선생님 수업 가능 시간 있을때 -> 리사이클러뷰에 뿌려준다.

            recyclerView_for_show_available_time_list.setVisibility(View.VISIBLE);
            txt_view_for_ment_no_time.setVisibility(View.GONE);

            LinearLayoutManager linearLayoutManager_for_available_class_time_list;//다이얼로그에 뿌려질 수업 가능한  시간 리스트 리사이클러뷰 담당할  레이아웃 메니져

            Show_available_reservation_time_to_std_adapter show_available_reservation_time_to_std_adapter;//수업 가능한  시간 리스트 리사이클러뷰에 뿌려줄  어뎁터

            //선생님이  지정한  available 클래스 시간 리스트로 보기 위한 리사이클러뷰 관련 코드.
            show_available_reservation_time_to_std_adapter=new Show_available_reservation_time_to_std_adapter(TeacherProfile.this,list_for_available_clas);
            linearLayoutManager_for_available_class_time_list=new LinearLayoutManager(TeacherProfile.this,RecyclerView.VERTICAL,false);
            linearLayoutManager_for_available_class_time_list.setReverseLayout(false);
            linearLayoutManager_for_available_class_time_list.setStackFromEnd(false);

            recyclerView_for_show_available_time_list.setLayoutManager(linearLayoutManager_for_available_class_time_list);
            recyclerView_for_show_available_time_list.setAdapter(show_available_reservation_time_to_std_adapter);

            recyclerView_for_show_available_time_list.setNestedScrollingEnabled(false);//스크롤 부드럽게


            //14-2 OK버튼 클릭 리스너 -> 이때  선택한  날짜들이 해당 학생이 예약한걸로 서버에 업데이트 된다.
            btn_for_okbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (show_available_reservation_time_to_std_adapter.show_selected_count() == 0) {//선택한  수업이  0일때

                        new Toastcustomer(TeacherProfile.this).showcustomtaost(null, "수업을  선택하세요!");

                    }else{//선택한 수업이 있을때

                         Log.v("check", getLocalClassName()+"의 예약 가능 수업  다이얼로그 ok버튼 눌림-> 서버에  선택한  수업들 해당 학생 예약으로  업데이트 진행");

                         GlobalApplication globalApplication=(GlobalApplication)getApplicationContext();

                         std_reserve_class_time(globalApplication.getStudnet_uid(),show_available_reservation_time_to_std_adapter.show_selected_class_list());


                        //다이얼로그 닫아줌.
                        dialog_for_show_availabletime.dismiss();
                    }



                }//onClic() 끝
            });

        }else{//해당 선생님 수업 가능 시간이 없을때


            recyclerView_for_show_available_time_list.setVisibility(View.GONE);
            txt_view_for_ment_no_time.setVisibility(View.VISIBLE);

            //14-2 OK버튼 클릭 리스너
            btn_for_okbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Toastcustomer(TeacherProfile.this).showcustomtaost(null, "선택된 수업이 없습니다.");

                }//onClic() 끝
            });
        }

       }//onResponse() 끝

            @Override
            public void onFailure(Call<GetTeacherAvailableClassTime> call, Throwable t) {
                Log.v("check",get_available_class_time+"의 선생님이 준비한 리스트 가져오기 실패  실패내용->"+t.getMessage());

            }//onFailure() 끝
        });

    }//get_available_reserve_time_list() 끝

     //해당 선생님 수업  예약 해서   서버 업데이트 해주는   메소드
    private void std_reserve_class_time(String student_uid,ArrayList<Integer> selected_class_uid) {

        JSONArray jsonArray=new JSONArray();//arraylist <integer>제너릭으로 받은  클래스 UID 값들  서버에서 json으로 쓰게  JSONARRY로  다시  넣어줌.
        for(int i=0; i<selected_class_uid.size(); i++) {

            jsonArray.put(selected_class_uid.get(i));

        }


        Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiService.baseurl).build();//리트로핏 뷸딩
        ApiService apiService = retrofit.create(ApiService.class);//api인터페이스 크리에이트
        Call<ResponseBody> reserve_classtime = apiService.delete_or_edit_available_class_time(null, 3, 0,student_uid,jsonArray);//edit_or_delete 가  0이므로  수정를 뜻함.


        reserve_classtime.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {

                    //해당 클래스 예약  결과값
                    String result_reserve_class=response.body().string();
                    Log.v("check", "show_available_reservation_time_to_std_adapter 의 reserve_classtime()끝  서버 응답 edit->" + result_reserve_class);

                    //예약한  수업 업데이트 성공했을때,
                    if(result_reserve_class.equals("1")){

                        new Toastcustomer(TeacherProfile.this).showcustomtaost(null, "수업 예약이 완료 되었습니다.");

                        //선생님 한테  FCM 보내는  코드도 실행한다.
                        //이렇게 나눈 이유는  업데이트 성공을 해도  FCM 보내기 서버에서 실패시  업데이트를  포기해야할수 있기때문에
                        //일단 나눔
                        GlobalApplication globalApplication=(GlobalApplication)getApplicationContext();


                        //선생님에게  예약했다는  fcm을 보내기 위한  메소드
                        send_fcm_to_teacher_for_reservation_complete(student_uid,globalApplication.getStudnet_profile_url(),globalApplication.getStudent_name(),teacherid,jsonArray);


                        //해당 시간 알람 등록하기
                        get_reserved_class_list_For_register_alarm(teacherid);//33-1


                        //수업 예약이 완료되었으므로,  다시  예약  한  클래스 리스트를 업데이트 해줌.
                        //해당 학생이  예약한  수업리스트를 가지고와  teacherprofile -> reservation부분에  뿌려준다.
                        get_student_reserved_time_list(recyclerview_for_reservation_time_list,teacherid);

                    }else{//예약한 수업 업데이트 실패 했을때

                        Log.v("check", getLocalClassName()+"의 예약 수업 등록  실패 ->"+result_reserve_class);
                        new Toastcustomer(TeacherProfile.this).showcustomtaost(null, "수업 예약 도중에 문제가 생겼습니다.");
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }//onRespse() 끝

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.v("check", "show_available_reservation_time_to_std_adapter 의 reserve_classtime()끝  서버 응답 실페00 edit->" + t.getMessage());

            }//onFailure() 끝
        });


    }//std_reserve_class_time 끝

    //알람등록을 위해  우선  예약한  수업의 시간들을  리스트로 가지고 온다.
    private void get_reserved_class_list_For_register_alarm(String teacheruid){

        GlobalApplication globalApplication=(GlobalApplication)getApplicationContext();

        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit=new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create(gson))//json으로 받고 싶으면 쓰면안됨..
                .build();//리트로핏 뷸딩
        ApiService apiService=retrofit.create(ApiService.class);//api인터페이스 크리에이트
        Call<GetStudentReservedClassTime>get_reserved_class_time=apiService.get_student_available_class_time(globalApplication.getStudnet_uid(),teacheruid);


        get_reserved_class_time.enqueue(new Callback<GetStudentReservedClassTime>() {
            @Override
            public void onResponse(Call<GetStudentReservedClassTime> call, Response<GetStudentReservedClassTime> response) {

                //예약한  수업 시간  받아옴.
                ArrayList<JsonObject> reserved_class_list=response.body().getGet_reserved_class_time();

                //for문 돌려서  수업시간들  알람 등록 시킴
                for(int i=0; i<reserved_class_list.size(); i++){

                    int reserved__date_uid_for_reqeuestcode_alarm=reserved_class_list.get(i).get("uid").getAsInt();
                    String reserved_date = reserved_class_list.get(i).get("availabledate").getAsString();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = null;
                    try {
                        date = sdf.parse(reserved_date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    //예약한  시간  timemills
                    long reserved_date_timemillis = date.getTime();

                     register_alarm(reserved_date_timemillis,profileurl,teachername,teacheruid,reserved__date_uid_for_reqeuestcode_alarm);

                }//for문 끝

            }//onResponse() 끝

            @Override
            public void onFailure(Call<GetStudentReservedClassTime> call, Throwable t) {

            }//onFailure() 끝
        });


    }//register_reserved_class_alarm() 끝

    //예약한  시간의  알람을 등록한다.
    private void register_alarm(long alarm_time,String teacher_profile,String teacher_name,String teacher_uid,int alarm_request_code){


        AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(TeacherProfile.this, Broadcast_for_alarm_reservation_class.class);
        intent.putExtra("student_or_teacher", 1);//1이면 학생에 알람 등록  0이면 선생님에 알람 등록
        intent.putExtra("teacher_profile", teacher_profile);//선생님 프로필 url 보냄
        intent.putExtra("teacher_name", teacher_name);//선생님 이름 보냄
        intent.putExtra("teacher_uid", teacher_uid);//선생님 uid보냄

        PendingIntent sender = PendingIntent.getBroadcast(TeacherProfile.this, alarm_request_code, intent, 0);

        Date date = new Date(alarm_time);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MINUTE, -10);//알람지정시간의 10분전으로
        date.setTime( c.getTime().getTime() );
        alarm_time = date.getTime();


        //알람 예약
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//도즈 모드 관련
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarm_time, sender);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            am.setExact(AlarmManager.RTC_WAKEUP, alarm_time, sender);
        } else {

            am.set(AlarmManager.RTC_WAKEUP, alarm_time, sender);
        }

    }//register_alarm() 끝


   //해당 선생님 한테  fcm을 보내기 위한 메소드이다. -> 서버로  선생님  uid와  학생 uid와 해당  예약 시간을 보낸다.
   //해당  uid의 학생이  선생님의 수업을  몇시에  예약 했다는걸  알려준다.
   private void send_fcm_to_teacher_for_reservation_complete(String studentuid, String student_profile,String student_name,String teacheruid,  JSONArray jsonArray_for_reservation_dates){

           //rerofit 준비
           Retrofit retrofit=new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create()).build();
           ApiService apiServie=retrofit.create(ApiService.class);

           //fcm서버로  현재 로그인  선생님  이메일  보내기
           Call<ResponseBody> send_fcm_to_teacher_for_reservation_complete=apiServie.send_reservation_info_to_send_fcm_teacher(studentuid,student_profile,student_name,teacheruid,jsonArray_for_reservation_dates);

           send_fcm_to_teacher_for_reservation_complete.enqueue(new Callback<ResponseBody>() {
               @Override
               public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                   try {
                      String result_forsend_fcm=response.body().string();
                       Log.v("check", getLocalClassName()+"의 send_fcm_to_teacher_for_reservation_complete() 실행 됨 결과 ->"+result_forsend_fcm);


                       if(result_forsend_fcm.equals("-1")){
                           Log.v("check", getLocalClassName()+"의 send_fcm_to_teacher_for_reservation_completet실행중  서버에서  해당 선생님 fcmtoken가져오기 실패함");
                       }



                   } catch (IOException e) {
                       e.printStackTrace();
                   }


               }//onResponse()끝

               @Override
               public void onFailure(Call<ResponseBody> call, Throwable t) {

                     Log.v("check", getLocalClassName()+"의 send_fcm_to_teacher_for_reservation_complete() 실행 서버 에러남 ->"+t.getMessage());


               }//onFailure() 끝
           });


   }//send_fcm_to_teacher_for_reservation_complete() 끝




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
                total_rating_score_txt.setText(total_rating);//전체 rating평균 점수 넣어줌.
                total_rating_bar.setRating(total_rating_average);//ratingbar에  해당 점수로  별점  setting



                //선생님 피드백 보여주는  리사이클러뷰 관련 코드
                teacherfeedbackAdapter=new teacherfeedbackAdapter(TeacherProfile.this,teach_feedbacks);
                teacherfeedbackLayoytManager=new LinearLayoutManager(TeacherProfile.this, RecyclerView.VERTICAL,false);
                teacherfeedbackLayoytManager.setReverseLayout(false);
                teacherfeedbackLayoytManager.setStackFromEnd(false);
                recyclerView_for_feedback.setLayoutManager(teacherfeedbackLayoytManager);
                recyclerView_for_feedback.setAdapter(teacherfeedbackAdapter);
                recyclerView_for_feedback.setNestedScrollingEnabled(false);//스크롤 부드럽게

            }//onResponse()끝

            @Override
            public void onFailure(Call<GetTeacherFeedback> call, Throwable t) {
                Log.v("check", "FragmentForTeacherClass 의 선생님 feedback들 가져오는데서 에러 나옴. 에러내용-> "+String.valueOf(t.getMessage())+teacheremail);

            }//onFailure()끝
        });


    }




    @Override
    protected void onResume() {
        super.onResume();

        Log.v("check", getLocalClassName()+"의  onResume() 실행됨");

        //ServiceForGetChattingData 서비스가 실행중일때이다.
        if(isMyServiceRunning(ServiceForGetChattingData.class)){

            Log.v("check", getLocalClassName()+"에서 ServiceForGetChattingData 서비스가  실행중이어서 ->해당  서비스  실행  조건  실행안함.");

        }else{ //ServiceForGetChattingData 서비스가 실행중이지 않을대이다.

            Log.v("check", getLocalClassName()+"에서 ServiceForGetChattingData 서비스가  멈춰있어서 ->해당 서비스  실행  조건  됨.");
            //채팅 백그라운드 받는  서비스(ServiceForGetChattingData) 를 실행시켜준다.
            Intent start_chatting_background_service=new Intent(TeacherProfile.this,ServiceForGetChattingData.class);
            startService(start_chatting_background_service);//서비스 실행시킴.

        }// //ServiceForGetChattingData 서비스가 실행중일때 조건 끝

        //선생님 피드백  가지고옴.
        get_teacher_feedback(teacheremail,recyclerView_for_feedback);


    }//onResume() 끝


    //서비스가 실행중인지  판단하기 위한 메소드
    private boolean isMyServiceRunning(Class<?> serviceClass) {

        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);//엑티ㅣ비티 매니져

        //엑티비티 매니저에서  실행중인 서비스 정보  객체 안에  -> 실행중인  서비스들  하나씩  넣어서 -> 해당 서비스와 -> 이름이 같은지  체크하기.
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {

            if (serviceClass.getName().equals(service.service.getClassName())) {//해당 서비스가  실행중인 서비스 중 하나와  이름이 같다면
                return true;//true로  -> 실행중임을 알린다.

            }
        }
        return false;//그외 경우 ->  false로 실행주이지 않을을 알린다.

    }//isMyServiceRunning() 메소드 끝,





private void tabhost(){
    //탭 호스트 vs  탭 레이아웃 차이점 알아보기

     Log.v("checksds", "tab눌림");

    teacherprofiletabhost.setup();


     //Tab 1
     TabHost.TabSpec spec1 = teacherprofiletabhost.newTabSpec("Tab One");
     spec1.setContent(R.id.realtab1);
     spec1.setIndicator("Profile");
     teacherprofiletabhost.addTab(spec1);

     //Tab 2
     TabHost.TabSpec spec2 = teacherprofiletabhost.newTabSpec("Tab Two");
     spec2.setContent(R.id.realtab2);
     spec2.setIndicator("Review");
     teacherprofiletabhost.addTab(spec2);


    //Tab3
    TabHost.TabSpec spec3 = teacherprofiletabhost.newTabSpec("Tab Three");
    spec3.setContent(R.id.realtab3);
    spec3.setIndicator("Reserve");
    teacherprofiletabhost.addTab(spec3);


    //tab의  최소  길이 가  너무 크므로  높이를 조절하기 위해  아래와 같은 코드를 사용함.
    for(int tab=0; tab<teacherprofiletabhost.getTabWidget().getChildCount(); ++tab){
        teacherprofiletabhost.getTabWidget().getChildAt(tab).getLayoutParams().height=160;
    }



    teacherprofiletabhost.setCurrentTab(0);  //해당 엑티비티  create될떄 진행되는 탭 번호이다.


}




//버튼 클릭리스너들 모아둔 메소드
private void buttonclicklistener(){

         View.OnClickListener onClickListener=new View.OnClickListener() {
             @Override
             public void onClick(View view) {
               switch (view.getId()){

                   //선생님 로그인 알람  -> 체크된 상태 -> 체크안된 상태로
                   case R.id.realcheckedalarmlogin:
                       Log.v("check", "선생님  로그인 알람  취소");

                       checkedalarm.setVisibility(View.INVISIBLE);//체크된 알람 이미지 안보여짐
                       uncheckedalarm.setVisibility(View.VISIBLE);//체크안됨 알람 이미지 보임.

                       //토스트
                       toastcustomer.showcustomtaost(null, teachername+" 선생님 알람 취소");

                       //선생님 알람  등록 여부  0을 보내  등록  취소를 알림.
                       registerteachertogetloginalarm("0");

                    break;

                    //선생님 로그인 알람 -> 체크안된 상태. -> 체크된 상태로
                   case R.id.realuncheckedalarmlogin:
                       Log.v("check", "선생님  로그인 알람  등록");

                       uncheckedalarm.setVisibility(View.INVISIBLE);//체크안됨 알람  이미지 안보임
                       checkedalarm.setVisibility(View.VISIBLE);//체크된 알람 이미지 보임.

                       //토스트
                       toastcustomer.showcustomtaost(null, teachername+" 선생님 알람 등록");

                       //선생님  알람  등록 여부  1을 보내  알람 등록을 알림.
                       registerteachertogetloginalarm("1");

                    break;

                    //선생님  하트  체크되어있는 상태   -> 체크되어있는걸 누르는 건니까  등록취소
                   case R.id.realwhiteheartentire:

                       Log.v("check", "선생님  내튜터로 등록 취소");

                       checkedhearbtnwhite.setVisibility(View.INVISIBLE);//체크된  하얀색 하트-> 안보임
                       uncheckedhearbtnwhite.setVisibility(View.VISIBLE);//체크안된 하얀색 하트->보임

                       //토스트로  내튜터 등록취소 알림.
                       toastcustomer.showcustomtaost(null, teachername+"선생님 내 튜터 등록 취소");

                       //서버로 체크값보내는 메소드 -> 0이면  내 튜터로  등록 취소 한거임.
                       registermyteacher("0");



                    break;

                   //선생님  하트  체크안되어있음.   -> 체크안되어있는걸  누르는 거니까 등록.
                   case R.id.realwhiteheart:

                       Log.v("check", "선생님 내튜터로  등록함");

                       checkedhearbtnwhite.setVisibility(View.VISIBLE);//체크된 하얀색 하트 -> 보임.
                       uncheckedhearbtnwhite.setVisibility(View.INVISIBLE);//체크안된 하얀색 하트 -> 안보임.

                       //토스트로  내튜터 등록알림
                       toastcustomer.showcustomtaost(null, teachername+"선생님 내 튜터 등록");

                       //서버로 체크값보내는 메소드 -> 1이면  내 튜터로  등록한거임.
                       registermyteacher("1");

                     break;

                    //수업 시작  버튼
                   case R.id.realstartclassbutton:

                       Log.v("check",getLocalClassName()+"에서  수업 시작 버튼 눌림.");

                       break;


                   //선생님과 1대1 채팅  버튼
                   case R.id.realmessengerbutton:

                       Log.v("check",getLocalClassName()+"에서  1대1 채팅 버튼 눌림.");

                       break;

                    //선생님 오픈 채팅방 버튼
                   case R.id.realopenchattingbutton:
                       Log.v("check",getLocalClassName()+"에서  오픈채팅 입장 눌림.");

                       break;


               }
             }
         };

         //클릭리스너  뷰들과 연결시켜즘
         checkedhearbtnwhite.setOnClickListener(onClickListener);
         uncheckedhearbtnwhite.setOnClickListener(onClickListener);
         checkedalarm.setOnClickListener(onClickListener);
         uncheckedalarm.setOnClickListener(onClickListener);


}//버튼  클릭리스너 메소드 끝

    //toolbar 아이템  셀렉트
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//옵션아이템들 클릭시 진행되는 코드
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작



                    finish();//현재 엑티비티 끝냄.
                    Log.v("check", getLocalClassName() + "의 툴바 뒤로가기 눌림.");
                    return true;

            }
        }
        return super.onOptionsItemSelected(item);
    }//옵션 아이템 클릭시 진행 끝


    //내 튜터로 등록하기
    private void registermyteacher(String checkmytutorornot){

      Log.v("check", "내튜터 등록 하기  서버 처리 메소드  실행됨.-> 등록 여부는 (1->등록, 0->취소) =>  "+checkmytutorornot);

      RequestBody studentemail = RequestBody.create(MediaType.parse("text/plain"),loginedid);// 서버에서 구별하기위한 현재 학생 이메일
      RequestBody teacheremail = RequestBody.create(MediaType.parse("text/plain"),teacherid);
      RequestBody checkemytutorornot=RequestBody.create(MediaType.parse("text/plain"), checkmytutorornot);

      retrofit=new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create()).build();//리트로핏 뷸딩
      apiService=retrofit.create(ApiService.class);//api인터페이스 크리에이트

      Call<ResponseBody>registermytutor =apiService.registerteacherasmytutor(studentemail,teacheremail,checkemytutorornot);

      //서버값  콜백 받아옴.
      registermytutor.enqueue(new Callback<ResponseBody>() {

          @Override
          public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

              try {
                  if (response.body() != null) {
                      String a=response.body().string();
                      Log.v("checkteacherprofile", "선생님 등록 결과는 ? 학생uid->"+loginedid+"의 선생님 uid"+teacherid+"를  등록 여부-> "+a);


                      if(a.equals("3-1")){//3-1일경우에는 체크 값이  등록이 안되어있는 경우이므로,   빈 하트로 뷰를 바꿔준다.

                          Log.v("checkteacher", "등록안되어 있음.");
                         checkedhearbtnwhite.setVisibility(View.INVISIBLE);//체크된  하얀색 하트-> 안보임
                         uncheckedhearbtnwhite.setVisibility(View.VISIBLE);//체크안된 하얀색 하트->보임

                      }else if(a.equals("3-2")){//3-2의 경우는  체크값이 등록 되어있는 경우이므로  꽉찬 하트를 보여준다.

                         Log.v("checkteacher", "등록되어 있음");
                          checkedhearbtnwhite.setVisibility(View.VISIBLE);//체크된  하얀색 하트-> 안보임
                          uncheckedhearbtnwhite.setVisibility(View.INVISIBLE);//체크안된 하얀색 하트->보임

                     }//선생님 mytutor 등록여부 조건문  끝.

                  }//responsebody  null값이 아닐때.

              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
          //onResponse끝

          @Override
          public void onFailure(Call<ResponseBody> call, Throwable t) {
              Log.v("checkteacherprofile", "결과는 ? "+t);
          }
          //onFailure 끝


      });//콜백 받아오기 끝

    }//registermyteacher메소드 끝


    //선생님 로그인시  fcm으로  알림 받기  등록하기
    private void registerteachertogetloginalarm(String checkregister){


        //현재 로그인한 학생 아이디
        RequestBody studentemail=RequestBody.create(MediaType.parse("text/plain"),loginedid);

        //해당 프로필  선생님의 uid
        RequestBody teacheruid=RequestBody.create(MediaType.parse("text/plain"),teacherid);

        //선생님 로그인시  알람  여부  0->  알람 해제,   1-> 알람  등록
        RequestBody checkregisterornot=RequestBody.create(MediaType.parse("text/plain"),checkregister);

        //로그인 알람 등록 여부 통신을 위한 retrofit 선언
        retrofitforgetteacheralarm=new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create()).build();
        apiServiceforgetteacheralarm=retrofitforgetteacheralarm.create(ApiService.class);

        //서버로  필요  파라미터들  보냄.
        Call<ResponseBody>registerteachertogetloginalarm=apiServiceforgetteacheralarm.registerteachertogetloginalarm(studentemail, teacheruid, checkregisterornot);

        //선생님 로그인 알람 등록  Callback 메세지
        registerteachertogetloginalarm.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {


                    if (response.body() != null) {

                        //선생님 로그인 알람 등록 콜백메세지 받는 스트링 변수.
                        String result = response.body().string();
                        Log.v("check", getLocalClassName()+"의 선생님 로그인 알람등록  응답내용->"+result);

                        if(result.equals("1-1")){//1-1일 경우는  알람이 등록되어있지 않는 경우이다.

                            checkedalarm.setVisibility(View.INVISIBLE);//체크된 알람 이미지 안보여짐
                            uncheckedalarm.setVisibility(View.VISIBLE);//체크안됨 알람 이미지 보임.


                        }else if(result.equals("1-2")){//1-2일 경우는 알람이  등록되어있는 경우이다.

                            checkedalarm.setVisibility(View.VISIBLE);//체크된 알람 이미지 보여짐
                            uncheckedalarm.setVisibility(View.INVISIBLE);//체크안됨 알람 이미지 안보임.


                        }//선생님 등록 여부 체크 조건문 끝.



                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }


            }//onResponse끝

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Log.v("check", getLocalClassName()+"의  선생님 로그인 알람등록  에러 생김->"+t);


            }//onFailure끝
        });//선생님 로그인 알람 등록 callback메세지 끝.

    }//registerteachergetloginalarm()끝

}//TeacherProfile 엑티비티 끝
