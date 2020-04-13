package com.example.leedonghun.speakenglish;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * speakenglish
 * Class: ShowEarningGraphActivity.
 * Created by leedonghun.
 * Created On 2020-03-23.
 * Description:  선생님이 수업을 통해 얻은  포인트를   하루치 오늘 기준  그다음  오늘 기준 지날  일주일 치를  보여준다
 * 뷰페이져를 이용해서  하루치 와  지난  일주일치를 나눠서 보여준다.
 */
public class ShowEarningGraphActivity extends AppCompatActivity {

    private final String ment_for_log="ShowEarningGraphActivity의 ";

    private Toolbar toolbar;//툴바 1-1
    private TextView txt_for_show_today_week;//오늘인지 지난 주일인지 보여주는 텍스트뷰 1-2
    private ViewPager viewPager_for_show_graph;//오늘 번돈 그래프랑  지난  일주일  번  돈 그래프를 담을 뷰페이져  1-3

    private TextView swipe_ment;//스와이프  방향 알려주는 텍스트뷰 1-4

    private ShowEarningGraphViewPaegerAdapter showEarningGraphViewPaegerAdapter;//그래프를 보여주는 뷰페이져  어뎁터 2-1

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_teacher_earning_graph);
        Log.v("check", ment_for_log+" onCreate() 실행됨");


        toolbar=findViewById(R.id.toolbar_for_earning_graph);//1-1
        txt_for_show_today_week=findViewById(R.id.txt_for_show_today_or_week);//1-2
        viewPager_for_show_graph=findViewById(R.id.viewpager_for_show_graph);//1-3

        swipe_ment=findViewById(R.id.swipe_ment);//1-4

        txt_for_show_today_week.setText("Today");
        swipe_ment.setText("swipe =>");




        //뷰페이져   넘겨기는거 감지해서  위에  텍스트를 알맞게게  바꿔준다.
       viewPager_for_show_graph.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            }//onPageScrolled() 끝

            @Override
            public void onPageSelected(int position) {
                Log.v("check 포지션", "현재 포지샨 -> "+position);

                if(position==0){

                    txt_for_show_today_week.setText("Today");
                    swipe_ment.setText("<= siwpe");


                }else if(position==1){

                    txt_for_show_today_week.setText("Past 7 days");
                    swipe_ment.setText("siwpe =>");
                }

            }//onPageSelected() 끝

            @Override
            public void onPageScrollStateChanged(int state) {

            }//onPageScrollStateChanged() 끝

        });//viewPager_for_chatting_room_list 끝



        //toolbar 관련 코드
        setSupportActionBar(toolbar);//툴바에  액션바  서포트 연결
        getSupportActionBar().setDisplayShowTitleEnabled(false);//엑션바에서 타이틀 안보이게함
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//액션바에서 Homeashup 버튼 가능하게함
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.whiteback);//해당 버튼  뒤로가기 버튼모양 으로  이미지 지정

        get_teacher_point_get_data();

    }//onCreate() 끝


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

    //선생님  포인트 가지고오기
    private void get_teacher_point_get_data(){



        GlobalApplication globalApplication=(GlobalApplication)getApplicationContext();

        //jsonobject의  어레이이므로 -> gson을 사용한다.
        GsonBuilder gsonBuilder=new GsonBuilder();
        gsonBuilder.setLenient();
        Gson gson=gsonBuilder.create();//gson객체 만들어냄.

        Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create(gson))
                .build();//리트로핏 뷸딩
        ApiService apiService = retrofit.create(ApiService.class);//api인터페이스 크리에이트

        Call<GetTeacherPointInfo> getTeacherPointInfoCall =apiService.get_teacher_point_info(globalApplication.getTeacheruid(), System.currentTimeMillis());
        getTeacherPointInfoCall.enqueue(new Callback<GetTeacherPointInfo>() {
            @Override
            public void onResponse(Call<GetTeacherPointInfo> call, Response<GetTeacherPointInfo> response) {
                ArrayList<JsonObject> result_arraylist=response.body().getGet_teacher_point_info();
                Log.v("check","ShowEarninGrapViewpagerAdapter의 선생님 포인트 정보 가지고 오기 성공->"+result_arraylist);

                //뷰페이져 어뎁터
                showEarningGraphViewPaegerAdapter=new ShowEarningGraphViewPaegerAdapter(ShowEarningGraphActivity.this,result_arraylist);


                //뷰페이저에 뷰페이져 어뎁터 연결 시켜줌.
                viewPager_for_show_graph.setAdapter(showEarningGraphViewPaegerAdapter);



            }//onResponse()끝

            @Override
            public void onFailure(Call<GetTeacherPointInfo> call, Throwable t) {
                Log.v("check","ShowEarninGrapViewpagerAdapter의 선생님 포인트 정보  가지고 오기 실패함->"+t.getMessage());



            }//onFailure()끝
        });


    }//get_teacher_point_get_data()끝



}//ShowEarningGraphActivity 클래스 끝
