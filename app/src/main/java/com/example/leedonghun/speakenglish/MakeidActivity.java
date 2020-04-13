package com.example.leedonghun.speakenglish;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * speakenglish
 * Class: MakeidActivity.
 * Created by leedonghun.
 * Created On 2018-12-20.
 * Description:
 * 학생로그인 부분과 선생 로그인부분을 나누어서  들어가기 위한  회원가입  입문용 엑티비티이다.
 *
 */
public class MakeidActivity extends AppCompatActivity {
    public static Activity MakeidActivity;//현재 엑티비티 엑티비티 변수로 선언.

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activit_makeid);

        Log.v("check",getLocalClassName()+"의 onCreate실행");


        //이렇게  엑티비티값을
        MakeidActivity=(com.example.leedonghun.speakenglish.MakeidActivity.this);
        Button gomakeidforteacher=(Button)findViewById(R.id.teacherbtn);//선생용  회원가입창 가는 버튼
        Button gomakeidforstudent =(Button)findViewById(R.id.studentbtn);// 학생용 회원가입창 가는 버튼

        //학생 회원가입창 가는 버튼 눌렀을때  이벤트 진행
        gomakeidforstudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotommakeidforstudent=new Intent(MakeidActivity.this,MakeidForStudent.class );
                gotommakeidforstudent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);//a, b, c의  엑티비티가 있을경우 a에서  staractivityforresult로
                //b로 갔을때  b에서 c로가서 c의값을 a로 보내기위해서는 위의 인텐트의대한 해당FLAG가 필요함.
                startActivity(gotommakeidforstudent);
            }
        });


        //선생용 회원 가입창 버튼 눌렀을때 이벤트 징행
        gomakeidforteacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotommakeidforteacher=new Intent(MakeidActivity.this,MakeidForteacher.class );
                gotommakeidforteacher.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                startActivity(gotommakeidforteacher);
            }
        });





    }//oncreate 종료





}//현재 클래스의 끝부분
