package com.example.leedonghun.speakenglish;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.net.MalformedURLException;

/**
 * speakenglish
 * Class: Inputsecurekeyforfindpw.
 * Created by leedonghun.
 * Created On 2018-12-29.
 * Description:
 */
public class Inputsecurekeyforfindpw extends AppCompatActivity {
    public static Activity inputsecurekeyforfindpw;//현재 엑티비티 엑티비티 변수로 선언.

    private Toastcustomer toastcustomer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inputsecurekey);

        toastcustomer=new Toastcustomer(Inputsecurekeyforfindpw.this);//커스톰토스트 선언
        inputsecurekeyforfindpw=(com.example.leedonghun.speakenglish.Inputsecurekeyforfindpw.this);
        final EditText inputsecretkey=(EditText)findViewById(R.id.secretkeyforfindpw);//본인이메일 인증용 암호적는 칸
        Button btnforinputsecretkey=(Button)findViewById(R.id.gotofindpwbtn);// 인증암호 적고 다음으로 고우 하는 버튼

        btnforinputsecretkey.setOnClickListener(new View.OnClickListener() {//ok 버튼을 눌렀을때 진행되는 이벤트이다.
            @Override
            public void onClick(View v) {
                if(inputsecretkey.getText().toString().equals("")){// 인증암호 적는란에 아무것도 없을 경우에 진행되는 코드
                  toastcustomer.showcustomtaost(inputsecretkey,"인증암호를 입력하세요!!");

                }else{//인증암호에  무언가 적혔을때 진행되는 코드


                    try {
                        Intent getemail=getIntent();
                        String email= getemail.getStringExtra("whichmail");
                        String teacherorstudent=getemail.getStringExtra("studentorteacher");
                        PHPRequest request = new PHPRequest("http://13.209.249.1/checksecretkeytofindpw.php");
                        String result = request.PhPtest(inputsecretkey.getText().toString(),email,teacherorstudent, "");


                        if (result.equals("1")) {//secretkey 맞음

                            Intent gotochangepaswd=new Intent(Inputsecurekeyforfindpw.this,Changenewpasswrod.class);
                            gotochangepaswd.putExtra("emailtochangepw",email);
                            gotochangepaswd.putExtra("changewhichtorstudent",teacherorstudent);
                            startActivity(gotochangepaswd);

                        }else if(result.equals("2")){// secretkey가 틀림

                             toastcustomer.showcustomtaost(inputsecretkey,"인증번호가 안맞습니다.!!");
                        }



                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }






                }//인증 암호에 무언가 적혔을때 진행되는 코드 끝
            }
        });





    }//on create 닫힘
}//class 닫힘
