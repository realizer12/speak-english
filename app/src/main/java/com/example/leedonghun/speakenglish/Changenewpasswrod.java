package com.example.leedonghun.speakenglish;

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

import com.google.gson.JsonArray;

import java.net.MalformedURLException;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * speakenglish
 * Class: Changenewpasswrod.
 * Created by leedonghun.
 * Created On 2019-01-09.
 * Description:패스워드  찾기로  암호를 보낸 메일을 받고
 * 알맞은 암호를 적어 넣으면
 * 현재  새로운 비밀번호를 정하는  칸이 나온다.
 */
public class Changenewpasswrod extends AppCompatActivity {

     private Toastcustomer toastcustomer;
     private EditText insertnewpasswd;
     private Button changenewpwbutton;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changenewpassword);

        Inputsecurekeyforfindpw inputsecurekeyforfindpw=(Inputsecurekeyforfindpw)Inputsecurekeyforfindpw.inputsecurekeyforfindpw;//보안코드 넣는 엑티비는 지워버림
        inputsecurekeyforfindpw.finish();//보안코드 넣는 엑티비는 지워버림

        insertnewpasswd=(EditText)findViewById(R.id.insertnewpassword);//새 비밀번호 넣는 칸
        changenewpwbutton=(Button)findViewById(R.id.changenewpwbtn);// 비밀번호 변경 확인 버튼
        Intent intent=getIntent();
        final String email=intent.getStringExtra("emailtochangepw");//이메일값
        final String teaorstu=intent.getStringExtra("changewhichtorstudent");//선생인지 학생인지 구별값

        toastcustomer=new Toastcustomer(Changenewpasswrod.this);



      changenewpwbutton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              if(insertnewpasswd.getText().toString().equals("")){
                      toastcustomer.showcustomtaost(insertnewpasswd,"새 비밀번호를 입력하세요!");

              }else if (!Pattern.matches("^(?=.*[a-zA-Z]+)(?=.*[!@#$%^*+=-]|.*[0-9]+).{2,16}$", insertnewpasswd.getText().toString())) {//패스워드 부분이  정규식으로  맞지 않을때 (영어, 숫자외 다른 언어일때)

                  toastcustomer.showcustomtaost(insertnewpasswd, "비밀번호 형식을 확인해주세요!");
                  insertnewpasswd.setText(null);//형식이 틀렸으므로  다시 쓰라고  에딧 텍스트의 값을 null로 바꾼다.


              }else{

                  try {
                      PHPRequest request =  new PHPRequest("http://13.209.249.1/changenewpassword.php");//checkmail-> 학생 회원가입용  페이지이다
                      String result = request.PhPtest(insertnewpasswd.getText().toString(), email,teaorstu,"");

                      if (result.equals("1")) {//성공저긍로 바뀜


                          toastcustomer.showcustomtaost(null, "성공적으로 바뀜");
                          InputEmailforfindpassword inputEmailforfindpassword=(InputEmailforfindpassword) InputEmailforfindpassword.inputEmailforfindpasswd;//보안코드 넣는 엑티비는 지워버림
                          inputEmailforfindpassword.finish();//이메일  넣는 엑티비는 지워버림
                          Changenewpasswrod.this.finish();//현재 끝남.
                          //모든스택이 지워지면 로그인 화면 으로 가짐.
                      }else if(result.equals("2")){//바뀌는데 실패함.



                          toastcustomer.showcustomtaost(null, "문제가 생김.처음부터 다시 하세요!");
                          Changenewpasswrod.this.finish();//현재 끝남.
                          //이렇게 되면 다시 이메일을 넣는 곳으로 돌아가진다.
                      }

                  } catch (MalformedURLException e) {
                      e.printStackTrace();
                  }



              }

      }//onclick 끝

      });//버튼 이벤트 끝

    }

}
