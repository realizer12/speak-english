package com.example.leedonghun.speakenglish;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
//import android.support.annotation.DrawableRes;
//import android.support.annotation.Nullable;
//import android.support.annotation.RequiresApi;
//import android.support.design.widget.TextInputLayout;
//import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.Calendar;
import java.util.regex.Pattern;

/**
 * speakenglish
 * Class: MakeidForStudent.
 * Created by leedonghun.
 * Created On 2018-12-21.
 * Description:
 * 학생의 회원 가입을 위한  정보를  받아 들이는 부분이다.
 * 회원가입창으로 보면된다.
 * 이메일 인증 등이 들어간다.
 *
 */

public class MakeidForStudent extends AppCompatActivity {


    final int[] emailcheck = {0};// 이메일  인증 확인 했는지 여부 체크  0일 경우에는 체크가 안된 defult부분이고, 체크가 되면 1로 변하도록 한다.

   private Toastcustomer toastcustomer;




    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.makeidforstudent);



        Button cancelmakestudent=(Button)findViewById(R.id.makestudentidcancel);//회원가입 취소 버튼
        Button finishtomakestudentid=(Button)findViewById(R.id.finalmakestudentidbtn);//회원가입 완료 버튼
        final Button chekemailbtn=(Button)findViewById(R.id.checkemail);
        final EditText studentpasswd=(EditText)findViewById(R.id.studentpasswd);//회원가입때 들어가는 학생 비밀번호
        final EditText studentpasswdcheck=(EditText)findViewById(R.id.studentpasswdcheck);//학생비밀번호 한번더 확인
        final EditText studentemail=(EditText)findViewById(R.id.studentemail);//회원가입때 들어가는 학생 이메일
        final EditText studentenglishname=(EditText)findViewById(R.id.enlgishname);//회원가입때 들어가는 학생 영어이름

         toastcustomer=new Toastcustomer(MakeidForStudent.this);//토스트 커스토머 선언


        NetworkUtil.setNetworkPolicy();//서버아 네트워크연결하기위한 정책 설정.



         //회원가입 완료 창을 눌렀을때 필요한 정보들이  edittext들 안에 들어있는지 여부를 확인하고 들어있지 않으면 토스트를 보내고 ui색을 바꿔준다.
         //그리고  정보들이 모두 있으면 값을 db에   저장한다.
        finishtomakestudentid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (studentemail.getText().toString().replace(" ", "").equals("")) {
                    //이메일 쓰는 곳에 아무것도 없이 공백이 있을경우 " "이런경우는 ""으로 변환해서
                    //공백으로 처줌
                    toastcustomer.showcustomtaost(studentemail, "사용할 이메일을 써주세요!");//토스트 나옴


                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(studentemail.getText().toString()).matches()) {//이메일  형식이 틀릴때
                    toastcustomer.showcustomtaost(studentemail, "이메일 형식을 맞추세요!");
                    studentemail.setText(null);//형식이 틀렸으므로  다시 쓰라고  에딧 텍스트의 값을 null로 바꾼다.


                } else if (studentpasswd.getText().toString().replace(" ", "").equals("")) {//패스워드  부분이  공백일때
                    toastcustomer.showcustomtaost(studentpasswd, "사용할  비밀번호를 쓰세요!");


                } else if (!Pattern.matches("^(?=.*[a-zA-Z]+)(?=.*[!@#$%^*+=-]|.*[0-9]+).{2,16}$", studentpasswd.getText().toString())) {//패스워드 부분이  정규식으로  맞지 않을때 (영어, 숫자외 다른 언어일때)

                    toastcustomer.showcustomtaost(studentpasswd, "비밀번호 형식을 확인해주세요!");
                    studentpasswd.setText(null);//형식이 틀렸으므로  다시 쓰라고  에딧 텍스트의 값을 null로 바꾼다.

                } else if (studentpasswdcheck.getText().toString().replace(" ", "").equals("")) {//패스워드 체크 부분이 공백일때
                    toastcustomer.showcustomtaost(studentpasswdcheck, "pw체크란에  비밀번호를 한번더 써주세요!");

                }else if(!(studentpasswdcheck.getText().toString().equals(studentpasswd.getText().toString()))){//비밀번호와 비밀번호 체크란의 값이 다를때
                    toastcustomer.showcustomtaost(studentpasswdcheck, "체크한 비밀번호가 다릅니다!");
                    studentpasswdcheck.setText(null);

                 }else if(studentenglishname.getText().toString().replace(" ", "").equals("")){//영어이름이 아무것도 안써져있을때
                    toastcustomer.showcustomtaost(studentenglishname, "사용할 영어이름을 써주세요");

                }else if(!Pattern.matches("^[a-zA-Z]*$", studentenglishname.getText().toString())){//영어 이름이 영어만이 아닌 다른언어로 써졌을때
                    toastcustomer.showcustomtaost(studentenglishname, "영어이름은  영어로만!");
                    studentenglishname.setText(null);

                //모든 값이 다 써져있지만  이메일 체크가 안되어서  0이 되어있을경우 진행되는 이벤트이다.
                }else if(emailcheck[0] ==0){

                    Animation wrongshake= AnimationUtils.loadAnimation(MakeidForStudent.this,R.anim.shakeedittext);//쉐이크하는 애니메이션 선언
                    chekemailbtn.startAnimation(wrongshake);
                    toastcustomer.showcustomtaost(null,"이메일 인증을 진행하세요!");

                }else if(emailcheck[0] ==1){//이메일 체크도 되어있는 상태이다
                    //모든 것이 완벽할때  디비에  정보를 넣는 진행
                    try {
                        PHPRequest request = new PHPRequest("http://13.209.249.1/checkemail.php");//checkmail-> 학생 회원가입용  페이지이다

                        String api="0";//이부분은  api로그인인 경우에도  아이디가 db에 올라가는데  그경우를 위해 api 로그인 된 회원인지 여부를 구별하기 위함이다.

                        String result = request.PhPtest(studentemail.getText().toString(), studentpasswd.getText().toString(),studentenglishname.getText().toString(),api);
                        //위와같이  checkemail서버 페이지에   학생 이메일  학생 비밀번호 학생  영어이름 api로그인이 아니라는 뜻의 0을 보낸다.

                        if(result.equals("1")){

                            toastcustomer.showcustomtaost(null,"가입이 완료되었습니다.!!");
                            Intent intent=new Intent();
                            intent.putExtra("useremail",studentemail.getText().toString());//이메일을  맨처음  로그인 화면으로 보내기 위한  인텐트
                            setResult(120,intent);//결과값을 120으로 해서  가입완료된 이메일을 맨앞으로 보낸다.

                            MakeidActivity makeidActivity=(MakeidActivity) MakeidActivity.MakeidActivity;//중간 엑티비인 makeidactivity를  선언하고  종료시켜줘서 현재 엑티비티가 종료되면 바로
                            //맨앞  엑티비티가 나오도록 해준다.
                            makeidActivity.finish();
                            MakeidForStudent.this.finish();// 맨앞으로 가야됨.

                        }else if(result.equals("2")){
                            toastcustomer.showcustomtaost(null,"가입중 에러가 생겼습니다!");
                        }else if(result.equals("3")) {
                            toastcustomer.showcustomtaost(null, "로그인 api로 사용중인 메일이네요!");
                        }else if(result.equals("4")){
                            toastcustomer.showcustomtaost(null,"누군가 사용중인 이메일이네요!");
                        }


                    }catch (MalformedURLException e){
                        e.printStackTrace();
                    }

                }
            }
        });//가입완료 버튼 눌렀을경우 끝


        //메일 인증 버튼 눌렀을때
        chekemailbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
              //버튼 눌렀을떄 진행되는 곳

                if (studentemail.getText().toString().replace(" ", "").equals("")) {
                    //이메일 쓰는 곳에 아무것도 없이 공백이 있을경우 " "이런경우는 ""으로 변환해서
                    //공백으로 처줌
                    toastcustomer.showcustomtaost(studentemail, "사용할 이메일을 써주세요!");//토스트 나옴


                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(studentemail.getText().toString()).matches()) {//이메일  형식이 틀릴때
                    toastcustomer.showcustomtaost(studentemail, "이메일 형식을 맞추세요!");
                    studentemail.setText(null);//형식이 틀렸으므로  다시 쓰라고  에딧 텍스트의 값을 null로 바꾼다.


                    //이메일이 써졌을 경우 진행된다.
                }else {

                    try {
                         PHPRequest request = new PHPRequest("http://13.209.249.1/realemailcheckforteacher.php");//이메일이  중복되어있는지 안되어있는지 여부를 판단
                         String result = request.PhPtest(studentemail.getText().toString(), "", "","");

                         if (result.equals("1")) {
                            //toastcustomer(null, "사용 가능한 이메일입니다!");

                             try {
                                 PHPRequest request1 = new PHPRequest("http://13.209.249.1/PHPMailer/testemail.php");
                                 String result1 = request1.PhPtest(studentemail.getText().toString(), "", "","");

                                 if (result1.equals("1")) {
                                     String tors="student";// 이메일 체크칸을  선생부분과 학생부분이  공유해서 쓰는 데  이때 해당 메일이  선생부분에서 온건지  학생부분에서 온건지  구별하기위한 값이다.
                                     //메일 보내는데  성공 하였을때 나오는 칸입니다.
                                     toastcustomer.showcustomtaost(null,"메일을 보냈습니다!!");//메일을 보내는데 성공 메세지를 보냄.
                                     Intent emailcheckresult=new Intent(MakeidForStudent.this, EmailCheckPage.class);//이메일 체크 페이지로 넘어간다.
                                     emailcheckresult.putExtra("emailforcheck",studentemail.getText().toString());//이메일 체크 페이지로  학생 이메일 보냄.
                                     emailcheckresult.putExtra("teacherofstudent",tors);//학생 이메일인 것을 알려줌.
                                     // 이메일 체크하는곳에서 이메일 인증번호가 알맞게 들어가야지 emailcheck값을1로
                                     //받을수 있으므로  사용가능한 메일일때 바로 emailcheck값을 바꾸지 않고  startactivyforresult로  값의  진위여부를 확인후  맞을때 체크를 1로 바꿔준다.
                                     startActivityForResult(emailcheckresult,3);


                                 } else {
                                     toastcustomer.showcustomtaost(null,"메일을 보내는데  오류가 생겼습니다.!!");//메일을 보내는데 있어서  오류가 생김.

                                 }


                             } catch (MalformedURLException e) {
                                 e.printStackTrace();
                             }




                         } else if (result.equals("2")) {//임시용 디비 또는회원 용 디비에  해당 메일이 있어서 중복되는경우이다.

                            toastcustomer.showcustomtaost(null, "누군가 사용중이네요ㅠㅠ");
                            emailcheck[0] = 0;
                         }

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            }    //버튼 눌렀을떄 진행되는 곳끝
        });//메일 인증 버튼 눌렀을때 끝

        final TextView mentforcheckedemail=(TextView) findViewById(R.id.mentforcheckedemail);//메일 인증되었을때 보이는 인증 멘트
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {//텍스트 변화 전

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {//텍스트가 변화되는 중일때
                if(emailcheck[0]==1) {
                    chekemailbtn.setText("메일인증");//메일 버튼의  글
                    mentforcheckedemail.setVisibility(View.INVISIBLE);//메일 인증되었을때 보이는 인증 멘트
                    emailcheck[0]=0;//이메일 체크 0
                    studentemail.setBackground(getDrawable(R.drawable.border));//학생 이메일 백그라운드
                }

            }

            @Override
            public void afterTextChanged(Editable edit) {//텍스트가 변화가 되었을때
                if(emailcheck[0]==1) {
                    chekemailbtn.setText("메일인증");
                    mentforcheckedemail.setVisibility(View.INVISIBLE);
                    emailcheck[0]=0;
                    studentemail.setBackground(getDrawable(R.drawable.border));
                }
            }
        };//텍스트 리스너  종료

        studentemail.addTextChangedListener(textWatcher);//학생 이메일 란에  인증이 된후 에  다시  이메일을 수정할때 이메일인증이 풀리는 효과를 줌.



        //회원가입창을  취소를 눌러 현재 엑티비틀  피니쉬 해버린다.
        //이렇게 하여  스택이 쌓이지 않고 바로 전단계 엑티비티가 보이도록 진행하였다.
        cancelmakestudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MakeidForStudent.this.finish();


            }
        });//회원가입 취소버튼을 눌렀을때 끝

    }//on create 끝

    //이메일체크를 해서  해당 시간안에   맞는  보안코드를 넣었을때
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        EditText emailtext=(EditText)findViewById(R.id.studentemail);
        final Button chekemailbtn=(Button)findViewById(R.id.checkemail);
        TextView mentforcheckedemail=(TextView) findViewById(R.id.mentforcheckedemail);
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==3){
            if(resultCode==133){
                String emailcheckresult=data.getStringExtra("check");

                  if(emailcheckresult.equals("0")){// 이메일 체크가 안되었을때

                  }else if(emailcheckresult.equals("1")){//이메일 체크가 되었을때
                      emailcheck[0]=1;//이메일 체크 변수도 1로 체크되어 가입완료 버튼을 눌렀을때  가입이 완료 될수 있는 환경으로 만들어진다.
                      //emailtext.setFocusable(false);//아래는 이메일 체크란이  더이상 수정할수 없도록 바뀌는  코드이다.
                      //emailtext.setClickable(false);
                      chekemailbtn.setText("인증됨");//  인증되었다는 글씨를 알려준다.
                      mentforcheckedemail.setVisibility(View.VISIBLE);
                      //chekemailbtn.setFocusable(false);
                      //chekemailbtn.setClickable(false);
                      //정신 차려라  이동훈 이 바보 같은 놈아 ㅡㅡ,
                      //임시디비에 너놓고  중복처리로  수정 처리 막아 놓으면  나주엥 문제 되는거 확인했으니까  그냥  인증만 완료 하는걸로 하자 ㅡㅡ,
                      emailtext.setBackgroundColor(Color.parseColor("#53f0b4"));
                  }

            }
        }
    }

    @Override
    protected void onDestroy() {
       //이부분은  가입시 갑작 스러운 취소를 할경우 가입도중 중복 가입을 막기위해 임시  이메일 체크 디비에 남겨둔  디비를  지워주는  구간이다.
        super.onDestroy();

        NetworkUtil.setNetworkPolicy();//서버아 네트워크연결하기위한 정책 설정.
      if(emailcheck[0]==1){//버튼 체크값이 1일 경우임.
          EditText emailtext=(EditText)findViewById(R.id.studentemail);
            // 이경우에는 디비에 들어간 값들을 다시 사라지게 해준다.

            try {
                PHPRequest request = new PHPRequest("http://13.209.249.1/DeleteEmailCheck.php");//임시 이메일 디비를 지워주는 파일  부르기
                String result = request.PhPtest(emailtext.getText().toString(),"","","");

                if (result.equals("1")) {

                    //성공적으로 지움


                } else if(result.equals("2")) {


                    //성공적으로 못지움
                    toastcustomer.showcustomtaost(null,"임시 저장소에서 해당 이메일이 지워지지 않았습니다.");

                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        }


    }//destroy 끝
}
