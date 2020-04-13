package com.example.leedonghun.speakenglish;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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
 * Class: TeacherChattingProfileActivity.
 * Created by leedonghun.
 * Created On 2020-01-08.
 *
 * Description://채팅 방에서 선생님  프로필  사진을  눌렀을때 학생처럼 바로 프로필 사진이 보이는것이 아니라.
 * 카카오톡 처럼   프로필 관련  매개  엑티비티가 나오게 한다.
 * 이 엑티비티는  밑에서  위로 올라가는 애니메이션이 들어간다.
 * 그리고 여기서  프로필사진을 한번더 누르면 확대되어서 볼수 있고,
 * 프로필  버튼을 누를시 ->  선생님 프로필 엑티비티로 가짐.
 * 수업을  누르면,  수업  고르는  다이얼로그 나오고,  영상 수업, 이랑  화면 공유 수업  선택해서 들어감.
 */
public class TeacherChattingProfileActivity extends AppCompatActivity {

   private ImageView teacher_profile_imageview;//선생님 프로필 사진이 들어가는 이미지뷰 1-1
   private ImageButton btn_for_cancel_finish_activity;//현재 엑티비티 종료하는 이미지 x 버튼  1-2

   private LinearLayout btn_for_goto_profilehome;// 선생님 프로필 엑티비티로 넘어감. 1-3
   private LinearLayout btn_for_goto_class;//선생님 수업을 바로 진행할수 있는 버튼  1-4

   private TextView textView_for_showing_teacherstatus;//현재 선생님 상태를 보여주기위한 텍스트뷰- online or offline 1-5
   private TextView textView_for_showing_teachername;//현재 선생님 이름을  보여준다. ->  현재 선생님 상태와 함께  붙음.  1-6


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_chatting_progfile_show);
        Log.v("check", getLocalClassName()+"의  onCreate() 실행됨");


        teacher_profile_imageview=findViewById(R.id.teacher_chatting_profileimg);//1-1
        btn_for_cancel_finish_activity=findViewById(R.id.button_for_finish_teacher_chatting_profile);//1-2
        btn_for_goto_profilehome=findViewById(R.id.btn_for_profilehome);//1-3
        btn_for_goto_class=findViewById(R.id.btn_for_getclass);//1-4
        textView_for_showing_teacherstatus=findViewById(R.id.textView_for_show_teacher_status);//1-5
        textView_for_showing_teachername=findViewById(R.id.textView100);//1-6

        //recycelrview adapter에서  보낸 프로필 정보들 받는 intent
        Intent get_profile_info_intent=getIntent();
        byte [] profile_image_bytes=get_profile_info_intent.getByteArrayExtra("profileimage");//프로필 이미지 byea array받음.

        Bitmap image = BitmapFactory.decodeByteArray(profile_image_bytes, 0, profile_image_bytes.length);//비트맵 바이트 받은거  비트맵화
        teacher_profile_imageview.setImageBitmap(image);//프로필 이미지 넣어줌,

        //선생님 이름.
        String teachername=get_profile_info_intent.getStringExtra("teachername");
        String teachername_ment=" "+teachername+" teacher Status : ";//받아온 선생님 이름을 가지고  텍스트뷰에 들어갈 멘트로 수정

        textView_for_showing_teachername.setText(teachername_ment);//선생님 이름 보여주는  텍스트뷰에  -> 해당 선생님 이름을 사용해 넣어줌.


        //선생님  uid
        String teacheruid=get_profile_info_intent.getStringExtra("teacheruid");
        getknow_teacher_online_or_not(textView_for_showing_teacherstatus,teacheruid,btn_for_goto_class);//해당 uid 선생님 로그인 상태  알아내기위한 메소드



        //선생님 프로필 이미지 클릭시 -> 1-1
        teacher_profile_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("check", getLocalClassName()+"의 선생님 프로필 이미지 클릭됨");

                 //프로필 이미지 클릭시 -> 해당 프로필 확대되도록 ->  프로필 이미지 확대  엑티비티로 감.
                Intent goto_profile_photo_magnifyactivity=new Intent(TeacherChattingProfileActivity.this,Teacher_Profile_Photo_magnify.class);
                goto_profile_photo_magnifyactivity.putExtra("image", profile_image_bytes);//위 프로필 이미지 byte어레이로 넣은거  intent로 보냄.
                startActivity(goto_profile_photo_magnifyactivity);


            }
        });//1-1끝


         //현재 엑티비티 종료 X 버튼 클릭됨  -1-2
         btn_for_cancel_finish_activity.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Log.v("check", getLocalClassName()+"의 현재 엑티비티 종료  버튼 눌림 -> finish() 호출");

                 finish();//현재엑티비티 종료
                 overridePendingTransition(R.anim.sdlie_in_down_activity,R.anim.slide_out_down_activity);//슬라이드 위에서 아래오 내오는 애니메이션 추가

             }
         });//1-2 끝


        //선생님  프로필 홈으로 가는  -> 버튼 클릭됨  1-3
        btn_for_goto_profilehome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("check", getLocalClassName()+"의 선생미 프로필홈으로 가는 버튼 클릭");

                //선생님  프로필 정보  서버에서 받아서 -> Teacherprofile엑티비티로 감.
                get_teacher_profile_info(teacheruid);

            }
        });//1-3 끝


    }//onCreate() 끝

    //back키 눌렀을때도  -> 애니메이션  적용하기위해 만듬.
    private void pressBackkey() {

        finish();//activity 종료료
       overridePendingTransition(R.anim.sdlie_in_down_activity,R.anim.slide_out_down_activity);//슬라이드 위에서 아래오 내오는 애니메이션 추가

    }



    //백키 눌렀을때  애니메이션 적용위해 ->  activity클래스에 있는 onKeyUp 메소드 사용
    @Override
    public boolean onKeyUp( int keyCode, KeyEvent event )
    {
        if( keyCode == KeyEvent.KEYCODE_BACK ) {//백 키 눌렸을때  조건

            pressBackkey();// 위에서 만든 back키 눌렸을때 애니메이션 적용 메소드  넣어줌.

            return true;
        }

        return super.onKeyUp( keyCode, event );

    }//onKeyUp() 끝


    //선생님  프로필 정보를 가지고 오기 위한  메소드->  여기서 가지고 온  정보를 가지고 teacherprofile엑티비티로 넘어간다.
    private void get_teacher_profile_info(String teacheruid){

        Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create())//json으로 받고 싶으면 쓰면안됨..
                .build();//리트로핏 뷸딩
        ApiService apiService = retrofit.create(ApiService.class);//api인터페이스 크리에이트

        RequestBody teacheruid_toget_teacherprofile=RequestBody.create(MediaType.parse("text/plain"), teacheruid);//서버로 보낼 선생 uid


        Call<ResponseBody> getteacher_profileinfo=apiService.getteacher_profileinfo(teacheruid_toget_teacherprofile);//선생님  profileinto 정보 얻기위한  call객체

        getteacher_profileinfo.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {


                    //서버로부터  선생님 profile json화로 받음.
                    String teacher_profile_json=response.body().string();

                    Log.v("check", getLocalClassName()+"의  get_teacher_profile_info 메소드 실행 받아온 선생님 프로필 정보 값->"+teacher_profile_json);

                    //intent로  선생님 프로필  내용을 담아서  보내준다.
                    Intent intent_to_go_Teacherprofile=new Intent(TeacherChattingProfileActivity.this,TeacherProfile.class);

                    intent_to_go_Teacherprofile.putExtra("teacherinfo", teacher_profile_json);//서버 string 형태의 json 값  보냄.-> Teacherprofile에서 json값으로 parse함. .
                    intent_to_go_Teacherprofile.putExtra("teacherinfocheck", 3);//check값  3으로 보내서 teacherprofile에서  해당  값으로  처리하게 만들어준다.

                     startActivity(intent_to_go_Teacherprofile);//Teacherprofile 실행

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }//onResponse() 끝

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.v("check", getLocalClassName()+"의  get_teacher_profile_info 메소드 실행 받아온 선생님 프로필 정보 값 에러->"+t);


            }//onFailure() 끝
        });





    }//get_teacher_profile_info()끝



   //선생님  온라인 또는  오프라인 여부를 알아내는 메소드
   //textView_for_showing_teacherstatus 이부분에  ->  값을 넣기 위한 메소드
   private void getknow_teacher_online_or_not(TextView show_teacher_status,String teacheruid,LinearLayout btn_for_goto_class){



       Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create())//json으로 받고 싶으면 쓰면안됨..
               .build();//리트로핏 뷸딩
       ApiService apiService = retrofit.create(ApiService.class);//api인터페이스 크리에이트

       RequestBody teacheruid_toget_loginstatus=RequestBody.create(MediaType.parse("text/plain"), teacheruid);//서버로 보낼 선생님 로그인 status

       Call<ResponseBody> getteacherloginstatus=apiService.getteacherlogin_status(teacheruid_toget_loginstatus);//선생님 로그인 on off 정보 얻기위한  call객체

       getteacherloginstatus.enqueue(new Callback<ResponseBody>() {
           @Override
           public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

               try {
                   String teacher_online_status_result=response.body().string();

                   Log.v("check", getLocalClassName()+"에서 선생님 loginstatus 관련해서 값 가지고 온 결과->"+teacher_online_status_result);

                   //선생님 onoffline 상태가  -offline일때
                   if(teacher_online_status_result.equals("0")){

                       show_teacher_status.setTextColor(Color.argb(255, 145, 142, 142));//off라인이므로  회색으로 처리함.
                       show_teacher_status.setText("Off Line");



                       //선생님 수업 바로 진행  버튼 클릭됨  1-4
                       btn_for_goto_class.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               Log.v("check", getLocalClassName()+"의  선생님 수업 하기  버튼 클릭됨");

                                new Toastcustomer(TeacherChattingProfileActivity.this).showcustomtaost(null, "선생님 상태가 offLine 이어서 \n 수업을 할수 없습니다.",1500,300);

                           }

                       });//1-4 끝

                   }else if(teacher_online_status_result.equals("1")){//선생님 onoffline 상태가  online일떄

                       show_teacher_status.setTextColor(Color.argb(255, 255, 87, 34));//onLine이므로 밝은 주황색으로 처리함.
                       show_teacher_status.setText("On Line");


                       //선생님 수업 바로 진행  버튼 클릭됨  1-4
                       btn_for_goto_class.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               Log.v("check", getLocalClassName()+"의  선생님 수업 하기  버튼 클릭됨");

                               new Toastcustomer(TeacherChattingProfileActivity.this).showcustomtaost(null, "선생님 상태가 onLine 이어서 \n 수업 가능",1500,300);


                           }
                       });//1-4 끝

                   }


               } catch (IOException e) {
                   e.printStackTrace();
               }


           }//onResponse()끝

           @Override
           public void onFailure(Call<ResponseBody> call, Throwable t) {

               Log.v("check", getLocalClassName()+"에서 선생님 loginstatus 관련해서 값 가지고 온 결과 에러남->"+t);

           }//onFailure() 끝


       });//getteacherloginstatus.enqueue끝

   }//getknow_teacher_online_or_not() 끝



}//TeacherChattingProfileActivity 클래스 끝

