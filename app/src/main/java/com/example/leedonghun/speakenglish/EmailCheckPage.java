package com.example.leedonghun.speakenglish;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
//import android.support.annotation.Nullable;
//import android.support.annotation.RequiresApi;
//import android.support.v4.app.NotificationCompat;
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
 * Class: EmailCheckPage.
 * Created by leedonghun.
 * Created On 2018-12-26.
 * Description:
 */
public class EmailCheckPage extends AppCompatActivity {
    private static final int MILLISINFUTURE = 180*1000;//현재 10초로 설정  전체  타이머 시간이다.
    private static final int COUNT_DOWN_INTERVAL = 1000;//카운트다운  간격이다. 1000-> 1초
    private int count = 180;//전체 카운트 다운 하는 카운트?
    private CountDownTimer countDownTimer;
    private Button  emailcheckokbtn;
    private EditText emailchecknumber;
    private TextView emailcount;
    private TextView emailment1;
    private TextView emailment2;
    private TextView titlepage;

    int buttoncheck=0;//버튼 체크를 위한  변수이다.
    //ok버튼이 눌리면  버튼 체크는 1이되고
    //그외의 상황에는 0이되어 진행된다.

    private Toastcustomer toastcustomer;//커스톰 토스트


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_emailcheck);

        emailcheckokbtn=(Button)findViewById(R.id.emailAuth_btn);//이메일 인증 확인 버튼이다.
        emailchecknumber=(EditText)findViewById(R.id.emailAuth_number);//이메일 인증확인 보안 코드를 적는 란이다.
        emailcount=(TextView)findViewById(R.id.emailAuth_time_counter);//이메일 카운터가 보이는 란이다.
        emailment1=(TextView)findViewById(R.id.emailment1);//이메일인증 페이지 관련 설명글--> 나중에  선생용으로 사용할때 영어로 바꿀려고 선언함  아래 타이틀 부분까지
        emailment2=(TextView)findViewById(R.id.emailment2);//이메일 인증 페이지 관련 설명글
        titlepage=(TextView)findViewById(R.id.titleemailcheckpage);//이메일 이증페이지  타이틀

        toastcustomer=new Toastcustomer(EmailCheckPage.this);//커스톰 토스트 선언

        NetworkUtil.setNetworkPolicy();//네트워크 정책 세팅;
        String toastment = null;

        Intent intent = getIntent();//Makeidforstudent엑티비티에서 보낸 인텐트 값을 받는다.
        String email = intent.getStringExtra("emailforcheck");//이메일  변수로  이메일 값을 받는다.
        String tors=intent.getStringExtra("teacherofstudent");//선생님인지  학생인지 여부를  체크 하기위한  값이다.

        //선생님일때 -> 영어로 보여줌
        if(tors.equals("teacher")){
            titlepage.setText("Email Check");
            emailchecknumber.setHint("insert your secure key!!");
            emailment1.setText("check your secure-key from your email");
            emailment2.setText("and insert it on this box in 3minuets");
            toastment="incorrect secure-key";

        //학생일때   -> 한국어로 보여줌
        }else if(tors.equals("student")){
            titlepage.setText("이메일 인증하기");
            emailchecknumber.setHint("인증 암호를 등록해 주세요");
            emailment1.setText("3분안에 해당 이메일로 발송 된 인증 암호를 ");
            emailment2.setText("확인 후 아래칸에 적어주세요.");
            toastment="제대로된 인증번호가  아닙니다!!";
        }


        countDownTimer();//카운트 다운 타이머가 선언됨.
        countDownTimer.start();//카운트 다운 타이머 시작함.


        final String finalToastment = toastment;
        emailcheckokbtn.setOnClickListener(new View.OnClickListener() {//이메일 인증번호 입력완료  버튼을 눌렀을경우 실행되는  코드
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();//Makeidforstudent엑티비티에서 보낸 인텐트 값을 받는다.
                String email = intent.getStringExtra("emailforcheck");//이메일  변수로  이메일 값을 받는다.
                try {
                    PHPRequest request = new PHPRequest("http://13.209.249.1/Emailscurekeycheck.php");//임시 이메일 디비를 지워주는 파일  부르기
                    String result = request.PhPtest(email,emailchecknumber.getText().toString(),"","");

                    if (result.equals("1")){
                        //성공적으로 인증
                        ;
                        buttoncheck=1;//이메일 확인 버튼이 눌리면  버튼 체크값이 1로 되어서  인증버튼을 누름으로 진행된다.
                        countDownTimer.onFinish();//인증버튼이 눌리면  카운트 다운  피니쉬 부분의  코드가  진행된다.

                    }else if(result.equals("2")){

                        //인증에  실패함
                        buttoncheck=0;
                        toastcustomer.showcustomtaost(emailchecknumber, finalToastment);

                    }


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }




            }
        });

    }

    private void countDownTimer() {//카운트 다운 하는 메소드
        countDownTimer = new CountDownTimer(MILLISINFUTURE, COUNT_DOWN_INTERVAL) {
            public void onTick(long millisUntilFinished) {

                count --;//카운트가  낮아짐
                //아래는 카운트 다운 을 할때  분 초 단위로 나눠서 보여주기 위해 나눠주는 코드이다.
                if ((count - ((count / 60) * 60)) >= 10) { //초가 10보다 크면 그냥 출력
                    emailcount.setText((count / 60) + " : " + (count - ((count / 60) * 60)));
                } else { //초가 10보다 작으면 앞에 '0' 붙여서 같이 출력. ex) 02,03,04...
                    emailcount.setText((count / 60) + " : 0" + (count - ((count / 60) * 60)));
                }
            }

            public void onFinish() {// 시간이 다되면  아래와 같이  피니쉬 울림

                if(buttoncheck==0) {
                    Intent emailcheckresult = new Intent();
                    emailcheckresult.putExtra("check", "0");// 앞선 엑티비티에서 emailcheck여부를 위해 요청한 값을 0  즉  체크안됨으로 돌려준다.
                    setResult(133, emailcheckresult);
                    finish();//현재창 종료되면서  destroy함수  진행됨.
                }else if(buttoncheck==1){
                    Intent emailcheckresult = new Intent();
                    emailcheckresult.putExtra("check", "1");// 앞선 엑티비티에서 emailcheck여부를 위해 요청한 값을 1  즉  체크가됨으로 돌려준다.
                    setResult(133, emailcheckresult);
                    finish();//현재창 종료되면서  destroy함수  진행됨.
                }
            }
        };

    }//카운트다운 하는 메소드 끝




    @Override
    protected void onDestroy() {//엑티비티 디스트로이
        super.onDestroy();

       // if(buttoncheck==1) {//이메일 인증 확인 버튼을 누르고 보안코드가 맞게  들어갔을때 버튼체크값이 1 이되어서 아래와 같이 진행된다.



            //Toast.makeText(getApplicationContext(), email, Toast.LENGTH_SHORT).show();//이메일값왔는지 확인용으로 써놓음.
            //이경우에는 이메일 체크 확인용 디비에서 값을 지우지 않는다. 해당 디비의 값은 회원가입이 완전히 끝나는 시점에 사라진다.
            //그 이유는 회원가입이 끝나기도 전에  인증후 디비의 값이 지워진다면  동시에 다른  신규회원이 메일 인증을 해도 인증가능  메일로 떠서  중복되기 때문이다.



           // try {
           //     countDownTimer.cancel();//카운트 다운 타이머가 취소되고
          //  } catch (Exception e) {
          //  }
         //   countDownTimer = null;//타이머는 null값으로 초기화된다.



  //////////////////////아래는 이메일 인증확인 버튼이 눌리지않고  엑티비티가 destroy될 경우이다.   ///////////////////////////////////////////////////////////////////////////////////


       // }else{//버튼 체크값이 0일 경우임.

        // 이경우에는 디비에 들어간 값들을 다시 사라지게 해준다.
            Intent intent = getIntent();//Makeidforstudent엑티비티에서 보낸 인텐트 값을 받는다.
            String email = intent.getStringExtra("emailforcheck");//이메일  변수로  이메일 값을 받는다.
            try {
                PHPRequest request = new PHPRequest("http://13.209.249.1/DeleteEmailCheck.php");//임시 이메일 디비를 지워주는 파일  부르기
                String result = request.PhPtest(email,email,email,"");

                if (result.equals("1")) {

                    //성공적으로 지움
                    try {
                        countDownTimer.cancel();// 카운트 다운 타이머 가 취소된다.

                    } catch (Exception e) {
                    }
                    countDownTimer = null;//카운트 다운 타이머가 null값으로 바뀐다.

                   } else if(result.equals("2")) {


                    //성공적으로 못지움
                    toastcustomer.showcustomtaost(null,"임시 저장소에서 해당 이메일이 지워지지 않았습니다.");
                    try {
                        countDownTimer.cancel();// 카운트 다운 타이머 가 취소된다.
                    } catch (Exception e) {
                    }
                    countDownTimer = null;//카운트 다운 타이머가 null값으로 바뀐다.
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            }



    }
}



