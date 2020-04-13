package com.example.leedonghun.speakenglish;

import android.content.SharedPreferences;
import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
//import android.support.v7.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.IOException;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * speakenglish
 * Class: TeacherChangePassWord.
 * Created by leedonghun.
 * Created On 2019-09-17.
 * Description:선생님 로그인 후에  패스워드를  바꾸고 싶을때  패스워드를  바꿔준다.
 */
public class TeacherChangePassWord extends AppCompatActivity {

    Retrofit retrofit;//리트로핏 선언
    ApiService apiService;//api service 인터페이스


    //새로운  비밀번호 적는 에딧텍스트
    EditText newpasswordedittext;

    //비밀번호 다시 체크하는 에디섹스트
    EditText checkpasswordagainedittext;

    //비밀번호 체크 완료 버튼
    Button buttonforfinishchangepassword;

    //현재 엑티비티  툴바
    Toolbar toolbarforteacherpasswordactivity;

    //커스톰 토스트
    Toastcustomer toastcustomer;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_change_password);
       Log.v("check",getLocalClassName()+"의  onCreate() 실행됨");


       toastcustomer=new Toastcustomer(TeacherChangePassWord.this);//커스톰 토스트

       newpasswordedittext=findViewById(R.id.newpasswordteacher);//새로운 패스워드
       checkpasswordagainedittext=findViewById(R.id.newpasswordteachercheckagain);//패스워드 다시 체크
       buttonforfinishchangepassword=findViewById(R.id.updateteacherchangepasswordbtn);//패스워드 바꾸기 버튼
       toolbarforteacherpasswordactivity=findViewById(R.id.toolbarforteacherchangepassword);//툴바

        SharedPreferences getid = TeacherChangePassWord.this.getSharedPreferences("loginteacherid",MODE_PRIVATE);//선생님 로그인 아이디  쉐어드에 담긴거 가져옴
        final String logineamil= getid.getString("loginidteacher","");//선생님 로그인 아이디 가져옴

        //패스워드 바꾸기  버튼 클릭 리스너
        buttonforfinishchangepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Log.v("check", getLocalClassName()+"의 패스워드 바꾸기 버튼 클릭됨.");

             //비밀번호 변경 눌렀는데,  정규식  영어 숫자 조합  여부 거짓일때 조건 확인
            if(!Pattern.matches("^(?=.*[a-zA-Z]+)(?=.*[!@#$%^*+=-]|.*[0-9]+).{2,16}$",newpasswordedittext.getText().toString())) {

                Log.v("check", getLocalClassName() + "의 패스워드 정규식 안맞음.");
                toastcustomer.showcustomtaost(newpasswordedittext, "PassWord Pattern is not Accurate");

            //확인 패스워드와 새 패스워드  값이 다를떄
            }else if(!newpasswordedittext.getText().toString().equals(checkpasswordagainedittext.getText().toString())){

                Log.v("check", getLocalClassName()+"의 패스워드  확인 패스워드랑  안맞음.");
                toastcustomer.showcustomtaost(checkpasswordagainedittext, "Check PassWord is not match with original",1500,200);

             //위 정규식  다 맞고  체크 패스워드도 값이 맞을떄,
            }else{

                //서버로  새 메세지 보냄.
                changeteacherpassword(logineamil);
            }

            }
        });
        //패스워드 바꾸기 버튼  클릭 리스너 끝.



        //toolbar관련  코드
        setSupportActionBar(toolbarforteacherpasswordactivity);//툴바에  액션바  서포트 연결
        getSupportActionBar().setDisplayShowTitleEnabled(false);//엑션바에서 타이틀 안보이게함
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//액션바에서 Homeashup 버튼 가능하게함
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.whiteback);//해당 버튼  뒤로가기 버튼모양 으로  이미지 지정

    }//oncreate


    //툴바 뒤로가기  기능  적용 하기위한  itemselected()메소드
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//옵션아이템들 클릭시 진행되는 코드
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                Log.v("check", getLocalClassName()+"의  툴바 뒤로가기 눌림.");

                finish();//현재 엑티비티 끝냄.
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


  //비밀번호  변경  패스워드 서버로 보냄.
  private void changeteacherpassword(String logineamil){



        RequestBody email = RequestBody.create(MediaType.parse("text/plain"),logineamil);// 서버에서 구별하기위한 학생 이메일
        RequestBody newpassword = RequestBody.create(MediaType.parse("text/plain"), newpasswordedittext.getText().toString());//새로운 비밀번호

        retrofit = new Retrofit.Builder().baseUrl(ApiService.baseurl).build();
        apiService = retrofit.create(ApiService.class);

        Call<ResponseBody> changeteachernewpassword=apiService.changeteacherpassword(newpassword, email);

        changeteachernewpassword.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    if (response.body() != null) {//response body가   null이 아닐때
                      //  Log.v("check", "서버에서 온값 ->  1이면 성공 2면  실패"+response.body().string());//response body  string 으로 받음.

                        String changepasswordresult=response.body().string();//서버 결과 값 스트링 변수에 넣어줌.

                        Log.v("check", changepasswordresult+"이거되나>");//response body  string 으로 받음
                       if(changepasswordresult.equals("1")){//1값으로 와서 디비 업데이트 성공
                           Log.v("check", "서버에서 온값 1-> 성공의미");
                           toastcustomer.showcustomtaost(null, "Success to change password!");
                            //성공이므로  현재 엑티비티 finish();
                           finish();

                       }else if(changepasswordresult.equals("2")){//2값으로 와서  디비 업데이트 실패
                           Log.v("check", "서버에서 온값 2->  실패의미");

                           toastcustomer.showcustomtaost(null, "something wrong  happened in changing");
                           finish();
                       }//2값으로 와서  디비 업데이트 실패 끝


                    }else{//서버온값이 애초에  null일때

                        Log.v("check", "서버에서 온값 ->  null임");//response body  ->null임,
                        toastcustomer.showcustomtaost(null, "something wrong  happened in changing");
                        finish();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.v("check", "서버에서  온  에러 "+t.toString());
                toastcustomer.showcustomtaost(null, "something wrong  happened in changing");
                finish();
            }
        });

    }//changeteacherpassword()메소드 끝



}//엑티비티 끝
