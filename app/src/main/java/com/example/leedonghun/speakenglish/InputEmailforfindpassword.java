package com.example.leedonghun.speakenglish;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

/**
 * speakenglish
 * Class: InputEmailforfindpassword.
 * Created by leedonghun.
 * Created On 2018-12-29.
 * Description:회원이  비밀번호를 잃어 버렸을때  사용하는
 * 곳이다.  이곳에서 본인의 이메일을 쓰면 해당 이메일로 본인인증암호가 가게 되고
 * 성공적으로가게되면 인증암호를 적는 칸으로 가지게 된다.
 */
public class InputEmailforfindpassword extends AppCompatActivity {
    public static Activity inputEmailforfindpasswd;//현재 엑티비티 엑티비티 변수로 선언.
    private Toastcustomer toastcustomer;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findpassword);


        toastcustomer=new Toastcustomer(InputEmailforfindpassword.this);//커스톰 토스트 선언
        inputEmailforfindpasswd=(com.example.leedonghun.speakenglish.InputEmailforfindpassword.this);
        //이메일 넣는 에딧텍스트
        final EditText inputemail=(EditText)findViewById(R.id.insertemail);//비밀번호를 찾으려는 이메일
        //ok버튼
        Button okbtn=(Button)findViewById(R.id.findpasswordokbtn);
        NetworkUtil.setNetworkPolicy();//서버아 네트워크연결하기위한 정책 설정.

       okbtn.setOnClickListener(new View.OnClickListener() {//OK버튼을 눌렀을때 진행됨.
           @Override
           public void onClick(View v) {
               if(inputemail.getText().toString().equals("")){
                   toastcustomer.showcustomtaost(inputemail, "이메일을 쓰고 누르세요!");
               }else {
                   try {
                       PHPRequest request = new PHPRequest("http://13.209.249.1/emailcheckforfindpw.php");
                       String result = request.PhPtest(inputemail.getText().toString(), "", "", "");

                       if (result.equals("1")) {
                           toastcustomer.showcustomtaost(null, "등록되지 않은 메일입니다!!");


                       } else if (result.equals("3")) {//임시용 디비 또는회원 용 디비에  해당 메일이 있어서 중복되는경우이다.// 선생님 메일인 경우이다.

                           String teacherorstudent1="0";//선생일때 0으로 알려준다.
                          // NetworkTask network= new NetworkTask(inputemail.getText().toString(),teacherorstudent1);
                          // network.execute();
                           try {
                               PHPRequest request1 = new PHPRequest("http://13.209.249.1/sendsecurekeyforchangepassword.php");//메일 보내기 서버 코드로 연결
                               String result1 = request1.PhPtest(inputemail.getText().toString(),teacherorstudent1, "", "");
                               if (result1.equals("1")) {//메일 보내기에 성공 하였음

                                   Intent gotoputsecurekeyforfindpw=new Intent(InputEmailforfindpassword.this,Inputsecurekeyforfindpw.class);
                                   gotoputsecurekeyforfindpw.putExtra("whichmail",inputemail.getText().toString());
                                   gotoputsecurekeyforfindpw.putExtra("studentorteacher",teacherorstudent1);//선생님 인지학생인지 알려줌,
                                   startActivity(gotoputsecurekeyforfindpw);//메일이 성공 적으로 보내지면 시큐어키를  적는 곳으로 들어가진다.
                                   toastcustomer.showcustomtaost(null, "메일 보냈음");
                               } else {
                                   Log.v("check_findpassword", result1);
                                   toastcustomer.showcustomtaost(null, "메일보내는데서 오류가생김");//메일을 보내는데 있어서  오류가 생김.
                               }
                           } catch (MalformedURLException e) {
                               e.printStackTrace();
                           }



                       }//선생님 메일일 경우 끝
                       else if (result.equals("4")){// 학생메일 일 경우이다.

                           String teacherorstudent2="1";//학생일때 1로 알려준다
                           try {
                               PHPRequest request1 = new PHPRequest("http://13.209.249.1/sendsecurekeyforchangepassword.php");//메일 보내기 서버 코드로 연결
                               String result1 = request1.PhPtest(inputemail.getText().toString(),teacherorstudent2, "", "");
                               if (result1.equals("1")) {//메일 보내기에 성공 하였음

                                   Intent gotoputsecurekeyforfindpw=new Intent(InputEmailforfindpassword.this,Inputsecurekeyforfindpw.class);
                                   gotoputsecurekeyforfindpw.putExtra("whichmail",inputemail.getText().toString());//이메일이 어떤건지 알려줌
                                   gotoputsecurekeyforfindpw.putExtra("studentorteacher",teacherorstudent2);//선생님 인지학생인지 알려줌,
                                   startActivity(gotoputsecurekeyforfindpw);//메일이 성공 적으로 보내지면 시큐어키를  적는 곳으로 들어가진다.
                                   toastcustomer.showcustomtaost(null, "메일로 인증암호를 보냈습니다!");
                               }else if(result.equals("2")) {
                                   toastcustomer.showcustomtaost(null, "디비 업데이트하는데서 오류가 생김");//메일을 보내는데 있어서  오류가 생김.
                               }else {

                                   Log.v("check_findpassword", result1);
                                   toastcustomer.showcustomtaost(null, "메일보내는데서 오류가생겼습니다.");//메일을 보내는데 있어서  오류가 생김.
                               }
                           } catch (MalformedURLException e) {
                               e.printStackTrace();
                           }

                       }//학생 메일일 경우 끝

                   } catch (MalformedURLException e) {
                       e.printStackTrace();
                   }
               }
           }
       });
    }//ON CREATE  끝




}//class끝
