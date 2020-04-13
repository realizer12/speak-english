package com.example.leedonghun.speakenglish;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;


import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * speakenglish
 * Class: SettingReservationTime.
 * Created by leedonghun.
 * Created On 2020-02-28.
 * Description: 선생님이 가능한  수업 예약 시간을  설정하기 위한 엑티비티이디.
 * floating 버튼을 누르면  커스톰 다이얼로그가 나와서  예약 가능한  시간을  세팅할수 있고,
 * 완료 버튼을 누르면,  해당 시간이 에약 가능으로  db에  들어간다.
 * 그다음 teacherpprofile에서  해당 시간이  리스트로 보이고 그 중 학생이 예약을 했으면,  선생님 한테  알람이 오고
 * 서로  15분전에  알람이  울리도록  처리를 해준다. 선생님의 경우는  예약 처리로 다른 수업을 못받게  처리해준다.
 */
public class SettingReservationTime extends AppCompatActivity {

    private final static String log_ment="SettingReservationTime";

    //현재 엑티비티 툴바 1-1
    private Toolbar toolbar;

    //예약  가능 시간을 추가 하기 위한 플로팅 버튼 1-2
    private FloatingActionButton floatingbtn_for_add_time;

    //예약 가능한 시간 리스트 넣어주는 리사이클러뷰  1-3
    private RecyclerView recyclerView_for_avalilble_time_list;

    private Available_class_time_list_adapter available_class_time_list_adapter;

    private LinearLayoutManager linearLayoutManager_for_available_class_time_list;

    private GlobalApplication globalApplication;

    private ImageView refresh_btn_for_reserved_time;//새로고침  이미 버튼


    //이미지뷰 rotation 효과
    private Animation rotae_imageview;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_reservation_time);
        Log.v("check", log_ment+"의 oncreate() 실행됨");

        toolbar=findViewById(R.id.toolbar_for_set_reservation_time);//1-1
        floatingbtn_for_add_time=findViewById(R.id.floating_btn_for_setting_reservation_time);//1-2
        recyclerView_for_avalilble_time_list=findViewById(R.id.recyclerview_for_show_setted_reservation_time);//1-3

        //선생님 uid 가져오기 위한 글로벌 변수
       globalApplication =(GlobalApplication)getApplicationContext();

       //예약한 클래스  새로고침을 위한  이미지 버튼
       refresh_btn_for_reserved_time=findViewById(R.id.refresh_btn_for_reserved_time);

        //1-2 클릭 이벤트 진행.
        floatingbtn_for_add_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                show_set_reservation_time_dialog();//해당 시간 지정  다이얼로그  띄움.
            }
        });



        //toolbar 관련 코드
        setSupportActionBar(toolbar);//툴바에  액션바  서포트 연결
        getSupportActionBar().setDisplayShowTitleEnabled(false);//엑션바에서 타이틀 안보이게함
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//액션바에서 Homeashup 버튼 가능하게함
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.whiteback);//해당 버튼  뒤로가기 버튼모양 으로  이미지 지정


        //이미지 로테이션 효과를 위한 애니메이션
        rotae_imageview = AnimationUtils.loadAnimation(SettingReservationTime.this, R.anim.rotate_image_view);

        //새로고침 버튼  클릭
        refresh_btn_for_reserved_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //이미지뷰 로테이션 효과줌. - 새로고침느낌으로
                refresh_btn_for_reserved_time.startAnimation(rotae_imageview);

                show_uploaded_available_class_time(globalApplication.getTeacheruid());//성공 했으면 다시 보여줘야 하므로, 새롭게 -> 어뎁터를  뿌려줌.

            }
        });//새로고침 버튼 클릭 끝


    }//onCreate() 끝


    private void show_set_reservation_time_dialog(){

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dialog_for_set_reservationtime = new Dialog(SettingReservationTime.this);

        // 액티비티의 타이틀바를 숨긴다.
        dialog_for_set_reservationtime.requestWindowFeature(Window.FEATURE_NO_TITLE);


         dialog_for_set_reservationtime.setCancelable(false);//취소 가능여부 false

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dialog_for_set_reservationtime.setContentView(R.layout.dialog_for_setting_teacher_reservation_time);





        WindowManager.LayoutParams params = dialog_for_set_reservationtime.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;//dialog width 늘려줌.
        dialog_for_set_reservationtime.getWindow().setAttributes((WindowManager.LayoutParams) params);

        //ok버튼 3-1
        Button ok_button=dialog_for_set_reservationtime.findViewById(R.id.okbtn_for_available_class);

        //취소버튼 3-2
        Button cancel_buttn=dialog_for_set_reservationtime.findViewById(R.id.cancelbtn_for_available_class);

        //타임 피커 3-3
        TimePicker timePicker=dialog_for_set_reservationtime.findViewById(R.id.time_picker_for_availableclass);
        TextView timepicker_ok=dialog_for_set_reservationtime.findViewById(R.id.timepickerok);
        TextView timepicker_cancel=dialog_for_set_reservationtime.findViewById(R.id.timepickercancel);

        //타임피커랑  멘트 담긴  컨테이너 ->  타임 선택시  이 container를  gone처리 해줘야됨.
        LinearLayout timepicker_container=dialog_for_set_reservationtime.findViewById(R.id.container_for_timepicker);
        timepicker_container.setVisibility(View.VISIBLE);

        //타임선택 완료  CONTAINER보여줌.
        LinearLayout time_picker_done=dialog_for_set_reservationtime.findViewById(R.id.time_pick_done);
        time_picker_done.setVisibility(View.GONE);//처음엔  GONE

        //타임 피커로 고른  시간  들어가는  텍스트뷰
        TextView textView_for_selected_time=dialog_for_set_reservationtime.findViewById(R.id.available_time_txt);

        //데이트 피커
        DatePicker datePicker=dialog_for_set_reservationtime.findViewById(R.id.datepicker_for_availableclass);
        TextView datepicker_ok=dialog_for_set_reservationtime.findViewById(R.id.datepickerok);
        TextView datepicker_cancel=dialog_for_set_reservationtime.findViewById(R.id.datepickercancel);


        //데이트 피커 담긴 컨테이너
        LinearLayout date_picker_container=dialog_for_set_reservationtime.findViewById(R.id.container_for_datepicker);
        date_picker_container.setVisibility(View.GONE);

        //데이트 피커  골랐을떄  컨테이너
        LinearLayout date_picker_done=dialog_for_set_reservationtime.findViewById(R.id.date_pick_done);
        date_picker_done.setVisibility(View.GONE);

        //데이트 피커로 고른 시간 들어가는 텍스트뷰
        TextView txt_view_for_selected_date=dialog_for_set_reservationtime.findViewById(R.id.available_date_txt);

        //date 다시 고르는 버튼
        Button btn_for_date_reset=dialog_for_set_reservationtime.findViewById(R.id.reset_date_btn);

        //time 다시 고르는 버튼
        Button btn_for_time_reset=dialog_for_set_reservationtime.findViewById(R.id.reset_time_btn);

        //다이얼로그 보여주기
        dialog_for_set_reservationtime.show();

        final int[] hour = new int[1];//선택한 예약 시간
        final int[] minute = new int[1];//선택한 예약 분


        //현재 시간 기준으로 데이트 피커 정할수 있는 날짜 지정함.
        datePicker.setMinDate(System.currentTimeMillis());

        //3-3 클릭이벤트
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute_picked) {
                Log.v("timepicker", String.valueOf(hourOfDay)+"시"+ minute_picked+"분");
             if(hourOfDay==0){
                 hourOfDay=24;
             }
               hour[0] =hourOfDay;//timepicker  시간 넣어줌.
               minute[0] =minute_picked;//timpicker 분 넣어줌.

            }
        });//timepicker  change 리스너 끝.



        //예약한 날짜들
        final int[] available_year = new int[1];//년도
        final int[] available_month = new int[1];//월
        final int[] available_day = new int[1];//날
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Log.v("datepicker", String.valueOf(year)+"년"+ monthOfYear+1+"월"+dayOfMonth+"일");
                    available_year[0] =year;//datepicker 년도 넣어줌.
                    available_month[0] =monthOfYear+1;//datepicker 월 넣어줌.
                    available_day[0] =dayOfMonth;//datepicker 날짜 넣어줌.

                }
            });
        }//오레오 버전 이상





        //////////////////////////////////////////////////////////////////////////////////////타임 피커////////////////////////////////////////////////////////////////////////////////

        //timepicker의   ok버튼을 누를때 이벤트
        timepicker_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //시간이 0일때는  맨처음에  시간 셀렉을 하지 않고 눌렀을때인데,
                //이럴경우는  현재 시간이 들어가도록 처리해주기 위해  아래와 같은 조건을 넣음
                //분같은 경우에는 00분 이 있으므로,  빼줌. 11-1
                if(hour[0] !=0) {

                    //타임 피커 사라짐.
                    timepicker_container.setVisibility(View.GONE);

                    if(hour[0]==24){//위에서 hour 픽할때 오후12시는 위  조건(11-1)  넘어가기위해서 24로 바꿔줬었음. 하지만, 해당  문제되는 조건들이 넘어갔으므로, 다시 0으로 바꿔준ㄷ.
                        hour[0]=0;
                    }

                    //타임 픽  던  컨테이너 보여줌.
                    time_picker_done.setVisibility(View.VISIBLE);
                    textView_for_selected_time.setText(hour[0] + "h " + minute[0] + "m");

                    //데이트 피커 보여줌.
                    //데이터피커 PICK된 텍스트뷰가 VISIBLE이라면,  데이트 피커를 따로 VISIBLE처리 하지 않는다.
                    if(date_picker_done.getVisibility() !=View.VISIBLE){
                        //데이트 피커 보여줌.
                        date_picker_container.setVisibility(View.VISIBLE);
                    }
                }//시간이 0이 아닐때
                else{

                    //타임 피커 사라짐.
                    timepicker_container.setVisibility(View.GONE);


                    //타임 픽  끝난  컨테이너 보여줌.
                    time_picker_done.setVisibility(View.VISIBLE);

                    //현재 시간 date로 가져옴
                    Date currentTime = Calendar.getInstance().getTime();

                    //date 포맷  HH로 시간 가지고옴.
                    SimpleDateFormat simple_hour = new SimpleDateFormat("HH", Locale.getDefault());

                    //date 포맷 mm로  분 가지고옴.
                    SimpleDateFormat simple_minute = new SimpleDateFormat("mm", Locale.getDefault());

                    //시간 분  넣어줌.
                    hour[0]= Integer.parseInt(simple_hour.format(currentTime));
                    minute[0]= Integer.parseInt(simple_minute.format(currentTime));

                    textView_for_selected_time.setText(hour[0] + "h " + minute[0] + "m");

                    //데이트 피커 보여줌.
                    //데이터피커 PICK된 텍스트뷰가 VISIBLE이라면,  데이트 피커를 따로 VISIBLE처리 하지 않는다.
                    if(date_picker_done.getVisibility() !=View.VISIBLE){

                        date_picker_container.setVisibility(View.VISIBLE);
                    }
                 }
             }
         });


        //타임 피커취소 버튼
        timepicker_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(textView_for_selected_time.getText().toString().length()>0){//time 고른게 있었을때

                    time_picker_done.setVisibility(View.VISIBLE);
                    timepicker_container.setVisibility(View.GONE);

                    //date가 아직  안골라져있을때...이때는  데이트 피커를 다시 보여준다.
                    if(txt_view_for_selected_date.getText().toString().length()<=0){

                        date_picker_container.setVisibility(View.VISIBLE);
                    }

                }else{//time 고른게 없었을때 date를 픽하라고 나옴.

                    new Toastcustomer(SettingReservationTime.this).showcustomtaost(null, "Should  Pick  Time !!");
                }

            }
        });

        //시간 리셋 버튼 눌렀을 때  이벤트
        btn_for_time_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(txt_view_for_selected_date.getText().toString().length()>0) {//date 고른게 있었을때

                    time_picker_done.setVisibility(View.GONE);
                    timepicker_container.setVisibility(View.VISIBLE);
                    date_picker_container.setVisibility(View.GONE);
                    date_picker_done.setVisibility(View.VISIBLE);

                }else {
                    time_picker_done.setVisibility(View.GONE);
                    timepicker_container.setVisibility(View.VISIBLE);
                    date_picker_container.setVisibility(View.GONE);

                }
            }
        });
        //////////////////////////////////////////////////////////////////////////////////////타임 피커////////////////////////////////////////////////////////////////////////////////





        //////////////////////////////////////////////////////////////////////////////////////데이터 피커////////////////////////////////////////////////////////////////////////////////
        //ok 버튼 눌렀을때
        datepicker_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //해당 날짜가 안 정해졌을때,
                //처음에  ok버튼을 누르면,  0으로  각 년, 월, 일이  처리가 되므로,  아래와 같이  0이 아닐 때와 0일때(맨처음일때)를  나눠서  진행한다.
                if(available_year[0] != 0 && available_month[0] != 0 && available_day[0] != 0) {


                    //date picker 담은 컨테이너  gone
                    date_picker_container.setVisibility(View.GONE);

                    //고른 시간  담길 텍스트뷰 보여줌.
                    date_picker_done.setVisibility(View.VISIBLE);

                    txt_view_for_selected_date.setText(available_year[0] + " / " + available_month[0] + " /  " + available_day[0]);

                }else{//해당 년,월, 일 이  0일때  이때는  맨처음에 ok버튼을 눌렀을때이다.

                    //date picker 담은 컨테이너  gone
                    date_picker_container.setVisibility(View.GONE);

                    ///고른 시간  담길 텍스트뷰 보여줌.
                    date_picker_done.setVisibility(View.VISIBLE);

                    Date currentTime = Calendar.getInstance().getTime();
                    SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
                    SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.getDefault());
                    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
                    available_year[0]= Integer.parseInt(yearFormat.format(currentTime));
                    available_month[0]= Integer.parseInt(monthFormat.format(currentTime));
                    available_day[0]= Integer.parseInt(dayFormat.format(currentTime));
                    txt_view_for_selected_date.setText(available_year[0] + " / " + available_month[0] + " /  " + available_day[0]);

                }

            }

        });//ok버튼 눌렀을때  클릭이벤트 끝.


        //날짜  리셋 버튼 눌렀을때 이벤트
        btn_for_date_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(textView_for_selected_time.getText().toString().length()>0) {//date 고른게 있었을때)

                    date_picker_container.setVisibility(View.VISIBLE);
                    timepicker_container.setVisibility(View.GONE);
                    date_picker_done.setVisibility(View.GONE);
                    time_picker_done.setVisibility(View.VISIBLE);

                }else {

                    date_picker_container.setVisibility(View.VISIBLE);
                    timepicker_container.setVisibility(View.GONE);
                    date_picker_done.setVisibility(View.GONE);

                }
                }
        });

        //////////////////////////////////////////////////////////////////////////////////////데이터 피커////////////////////////////////////////////////////////////////////////////////



        //데이트 피커 취소 버튼
        datepicker_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               if(txt_view_for_selected_date.getText().toString().length()>0){//date 고른게 있었을때

                   date_picker_done.setVisibility(View.VISIBLE);
                   date_picker_container.setVisibility(View.GONE);

               }else{//date 고른게 없었을때 date를 픽하라고 나옴.

                   new Toastcustomer(SettingReservationTime.this).showcustomtaost(null, "Should  Pick  date !!");
               }

            }
        });


        //3-1클릭 이벤트
        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(date_picker_container.getVisibility()==View.VISIBLE){//데이트 피커가  보일때는 -> 아직  선택 완료가 아님.


                    new Toastcustomer(SettingReservationTime.this).showcustomtaost(null, "you should pick the date!");

                }else if(timepicker_container.getVisibility()==View.VISIBLE){//타임 피커가 보일때는 -> 아직 선택 완료가 아님.

                    new Toastcustomer(SettingReservationTime.this).showcustomtaost(null, "you should pick the time!");

                }else {//그외 상황에서는  모든게  선택이 된것이므로,  디비  저장 및  알람 설정  진행.

                    String myDate = available_year[0] + "/" + available_month[0] + "/" + available_day[0] + " " + hour[0] + ":" + minute[0] + ":00";//yyyy/MM/dd HH:mm:ss이 형식으로 선택한  시간 들  모아줌.
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//yyyy/MM/dd HH:mm:ss해당 date 포맷
                    Date date = null;
                    try {
                        date = sdf.parse(myDate);//위 sdg포맷으로 mydate 파싱해서  date에 넣어줌.
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    //선택한  시간  밀리세컨드
                    long selected_time_mills = date.getTime();//date millseconds로 받음 -> 디비에  이 값을 넣을 거임.

                    GlobalApplication globalApplication=(GlobalApplication)getApplicationContext();
                    upload_available_time(globalApplication.getTeacheruid(),selected_time_mills);
                    //다이얼로그 닫아줌.
                    dialog_for_set_reservationtime.dismiss();
                }

            }
        });

        //3-2 클릭이벤트
        cancel_buttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //취소 버튼이므로 현재 다이얼로그 취소함.
                dialog_for_set_reservationtime.dismiss();

            }
        });

    }//show_set_reservation_time_dialog끝


    //선생님이 지정한  수업 가능한 시간을  서버에 올려준다.
    public void upload_available_time(String teacher_uid,float available_date_mills){


        Retrofit retrofit=new Retrofit.Builder().baseUrl(ApiService.baseurl).build();
        ApiService apiService=retrofit.create(ApiService.class);

        Call<ResponseBody>upload_available_time=apiService.upload_available_class_time(teacher_uid, available_date_mills);

        upload_available_time.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {

                    String result=response.body().string();//결과 값
                    Log.v("check", log_ment+"의 예약 가능한 시간 업로드 관련   서버 응당 -> "+result);

                    if(result.equals("1")){//업로드 결과가 성공일때 ->  해당  리사이 클러뷰  업데이트해


                        show_uploaded_available_class_time(teacher_uid);//성공 했으면 다시 보여줘야 하므로, 새롭게 -> 어뎁터를  뿌려줌.


                    }else if(result.equals("0")){//업로드 결과가  실패일때

                         new Toastcustomer(SettingReservationTime.this).showcustomtaost(null, "there is something error on\n upload avaialble time",1500,300);

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }//onResponse()끝

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.v("check", log_ment+"의 예약 가능한 시간 업로드 관련   서버 응당 실패 메세지 -> "+t.getMessage());


            }//onFailure() 끝
        });


    }//upload_available_time()끝


    //해당 선생님이 지정한  예약 가능  수업 시간  리스트  가져오는  메소드
    public void show_uploaded_available_class_time(String teacheruid){
        Log.v("check",log_ment+"show_uploaded_available_class_time실행됨");

        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit=new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create(gson))//json으로 받고 싶으면 쓰면안됨..
                .build();//리트로핏 뷸딩
        ApiService apiService=retrofit.create(ApiService.class);//api인터페이스 크리에이트
        Call<GetTeacherAvailableClassTime>get_available_class_time=apiService.get_teacher_available_class_time(teacheruid);

        get_available_class_time.enqueue(new Callback<GetTeacherAvailableClassTime>() {
            @Override
            public void onResponse(Call<GetTeacherAvailableClassTime> call, Response<GetTeacherAvailableClassTime> response) {
                ArrayList<JsonObject> available_class_list=response.body().getGet_available_class_time();
                Log.v("check",log_ment+"의 선생님이 준비한 리스트 가져오기 성공  가져온 리스트->"+available_class_list);


                //선생님이  지정한  available 클래스 시간 리스트로 보기 위한 리사이클러뷰 관련 코드.
                available_class_time_list_adapter=new Available_class_time_list_adapter(SettingReservationTime.this,available_class_list,teacheruid);
                linearLayoutManager_for_available_class_time_list=new LinearLayoutManager(SettingReservationTime.this,RecyclerView.VERTICAL,false);
                linearLayoutManager_for_available_class_time_list.setReverseLayout(false);
                linearLayoutManager_for_available_class_time_list.setStackFromEnd(false);

                recyclerView_for_avalilble_time_list.setLayoutManager(linearLayoutManager_for_available_class_time_list);
                recyclerView_for_avalilble_time_list.setAdapter(available_class_time_list_adapter);

                recyclerView_for_avalilble_time_list.setNestedScrollingEnabled(false);//스크롤 부드럽게


            }//onResponse() 끝

            @Override
            public void onFailure(Call<GetTeacherAvailableClassTime> call, Throwable t) {
                Log.v("check",log_ment+"의 선생님이 준비한 리스트 가져오기 실패  실패내용->"+t.getMessage());

            }//onFailure() 끝
        });

    }//show_uploaded_available_class_time 끝



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


    @Override
    protected void onResume() {
        super.onResume();
        Log.v("check", log_ment+"의 onResume() 실행됨");

        //선생님이  학생이 예약 수업을 등록해서 받은  노티를 클릭해서 해당  엑티비티를
        //들어왔을때 를  체크 하기 위한  코드이다.
        Intent intent_for_get_fcm_check=getIntent();
        String teacher_uid=intent_for_get_fcm_check.getStringExtra("fcmornot_teacher_uid");

        if(teacher_uid !=null && !teacher_uid.equals("")){//fcm을 통해  선생님 uid가  온것일때

            //선생님이 등록한  예약 가능  수업 리스트 서버에서  가져오기
            show_uploaded_available_class_time(teacher_uid);

        }else {//선생님  uid가  fcm을 통해서 온게 아닐때 -> global 변수를 통해  uid 받는다.

            //선생님이 등록한  예약 가능  수업 리스트 서버에서  가져오기
            show_uploaded_available_class_time(globalApplication.getTeacheruid());
        }

    }//onResume() 끝

    @Override
    protected void onPause() {
        super.onPause();
        Log.v("check", log_ment+"의 onPause() 실행됨");


    }//onPause() 끝

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v("check", log_ment+"의 onDestroy() 실행됨");

    }//onDestroy() 끝

}//SettingReservationTime 클래스 끝
