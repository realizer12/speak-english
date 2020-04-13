package com.example.leedonghun.speakenglish;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.ActivityCompat;
//import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kakao.auth.AuthType;
import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.request.LogoutRequest;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;

import java.net.MalformedURLException;
import java.security.MessageDigest;

/**앱을 실행시 맨처음으로 나오는 화면이다.
 * 로그인을 할때 사용한다.
 * 로그인 연결 및  각종 sns로그인이 가능하며  회원 정보가 없을 경우에는
 * 회원가입 창으로 보내주는 버튼이있다.
 * 카카오톡 로그인 api가 사용되었다.
 * 각각 로그인  정보를 입력하였다가 서버에 저장된 정보와 맞지 않게 되면,  토스트를 띄어준다.
 * 카카오톡 로그인 api를 사용할때는 따로   기존 학생 회원 가입시에 필요한
 * 이메일,   영어이름 중  -> 영어이름을 받는 dailog를  띄어서  영어이름을 적을때  가입처리가 되도록 진행한다.
 * 이메일의 경우는 이미  카카오톡 로그인 정보로 받을수 있기떄문에 필요 없다.
 * **/

public class LoginActivity extends AppCompatActivity {

    private Button loginbtn;//로그인 버튼
    private Button makeidbtn;//회원가입 버튼
    private ImageView btn_custom_login_kakao;//로그인 카카오  커스톰한 버튼
    private EditText logintext;//로그인 이메일을 적어주는 editext
    private EditText passwdtext;//로그인용 비밀번호 적어주는 editext

    //**
    private LoginButton btn_kakao_login;//카카오톡 로그인버튼 커스톰 전 원래 제공되는 버튼
    //**
    private TextView findpasswordbtn;//비밀번호 찾기용 텍스트이다.(클릭리스너를 넣어서 버튼처럼 쓸 예정)

    private Toastcustomer toastcustomer;//커스톰 토스트

    private Checkpermission checkpermission;



    private SessionCallback callback;//카카오톡 로그인 api 세션  콜백변수  선언




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.v("check",getLocalClassName()+"의 onCreate()가 실행");


        //화면 구성하는 여러 ui객체들 선언
        loginbtn=(Button)findViewById(R.id.loginbtn);//로그인용 버튼 선언
        makeidbtn=(Button)findViewById(R.id.makeidbtn);//회원가입 용 버튼 선언
        btn_custom_login_kakao = (ImageView) findViewById(R.id.kakaologinbtn);//로그인 카카오  커스톰한 버튼 선언
        logintext=(EditText)findViewById(R.id.loginidttext);//로그인 이메일 들어가는 에딧텍스트 선언.
        passwdtext=(EditText)findViewById(R.id.passwedittext);//비밀번호 들어가는 에딧텍스트 선언
        btn_kakao_login =  (LoginButton) findViewById(R.id.btn_kakao_login);//카카오톡 로그인 버튼 선언 ->  커스톰된 카카오톡 로그인 버튼 뒤에 숨겨져 있는 것.
        findpasswordbtn=(TextView)findViewById(R.id.findpassword);//비밀 번호 찾기 버튼 선언

        //커스톰 토스트 선언
        toastcustomer=new Toastcustomer(getApplicationContext());


        //**
        NetworkUtil.setNetworkPolicy();//네트워크 정책 세팅
        //**


        //혹시 로그인을 한적이있으면,  로그아웃 전까지  자동로그인 서비스를 지원하기 위해
        //쉐어드에   아이디를 저장해놓으면,  login activity에서 항상  쉐어드에  저장되어있는지  확인을 하고
        //저장된 아이디가 있을경우  바로 로그인 처리로 넘어가게 만든다.
        //아래  쉐어드 두개는  선생아이디와  학생 아이디관련 쉐어드에 저장된 정보를 string으로 가져오는 과정이다.

        //학생아이디 쉐어드에 있는것 스트링으로 가져오기
        SharedPreferences getid = getSharedPreferences("loginstudentid",MODE_PRIVATE);
        final String studentloginedidcheck= getid.getString("loginid","");


        //선생아디이  쉐어드에 있는것  스트링으로 가져오기
        SharedPreferences getteacherid = getSharedPreferences("loginteacherid",MODE_PRIVATE);
        final String teacherloginedidcheck= getteacherid.getString("loginidteacher","");



        //******************************************************자동로그인 *********************************************************************//
        //맨처음에 login activity가 시작될때
        //shared preference에 로그인  기록이있는 경우  로그인을 하여 바로 다음 activity로 넘어간다.

        //학생 아이디 로그인 기록 아이디 없을때 -> 쉐어드에서 받아온 스트링이 아무것도 없을 경우의 조건
        if(studentloginedidcheck.equals("")){
            Log.v("check","학생 아이디가 쉐어드에 없다.");


            //학생아이디 기록이 없고  강사  아이디 로그인 기록도  없을 경우의 조건
            //아무 변화도 일어나지 않는다.
            if(teacherloginedidcheck.equals("")){

                Log.v("check","학생 아이디가 쉐어드에 없고  선생님 아이디도 쉐어드에 없다.-> 자동로그인 부분은  넘어가고 로그인하는 파트로 가야한다. ");

            } else {  //학생아이디 기록은 없지만,  강사의  아이디 로그인 기록이 있는 경우의 조건

                Log.v("check","강사 로그인 기록있다! -> 강사 메인화면으로 자동로그인한다.");

                //강사 로그인 기록이있고 로그아웃이 없어 쉐어드에 남아있는 것이므로 바로  강사 메인화면으로  띄운다.
                Intent gotomteachermain = new Intent(LoginActivity.this, MainactivityForTeacher.class);
                startActivity(gotomteachermain);

                finish();//현재 LoginActivity 화면 피니쉬됨

             }//학생 아이디 기록 없고, 강사로그인 아이디기록있을때 끝

        }//여기 까지  학생 아이디 쉐어드 기록이 없을때 의 조건 끝.
        else{//쉐어드에  학생 로그인 아이디가 있을 경우 조건

            Log.v("check","학생 로그인 기록있다! -> 학생 메인화면으로 자동로그인 한다.");
            //학생아이디 기록이 있고 ,  로그아웃되지 않아  쉐어드에 기록이 남아있으므로,  바로 학생 메인화면으로 넘겨준다.
            Intent gotomstudentmain = new Intent(LoginActivity.this, MainactiviyForstudent.class);//학생 메인화면으로 가기
            startActivity(gotomstudentmain);//학생화면  시작
            finish();//현재 로그인 화면 피니쉬됨.

        } //학생아이디가 쉐어드 기록에 있을때 조건 끝
        //******************************************************자동로그인 부분 끝*******************************************************************//





        //로그인용 버튼 클릭리스너
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.v("check","로그인버튼 클릭함.");

                //로그인 버튼을 클릭했을때, 로그인이메일 에딧텍스트에 아무것도 써져있지 않을 경우의 조건
                if(logintext.getText().toString().equals("")) {



                    //로그인이메일 칸이 비어져있으므로, 위에  커스톰한 토스트가 진행된다.
                    Log.v("check", "로그인 이메일칸이 비어져있다. ");
                    toastcustomer.showcustomtaost(logintext, "로그인할 이메일을 적으세요!");

                }else{//로그인 버튼 클릭하고 로그인이메일 에딧텍스트에  무언가 써져있을때  진행된다.


                    //로그인용 패스워드 에딧텍스트칸에 아무것도  안써져있을때 경우의 조건
                   if(passwdtext.getText().toString().equals("")) {

                       Log.v("check","패스워드칸이  비워져있다.");
                        //패스워드칸이 비워져있으므로, 위에 커스톰 토스트 진행
                        toastcustomer.showcustomtaost(passwdtext, "비밀번호를 적으세요!");

                    } else {  //이메일칸과  패스워드 칸에  무언가 적혀있을때 경우의 조건

                        Log.v("check","로그인 칸,  패스워드칸 모두 작성되어있음");
                        //로그인과 패스워드 모두  적혀있기 때문에  바로 서버로  요청하여  로그인 이메일과  비밀번호를  확인할수 있다.
                        try {

                                //이부분에서 httrpurlconnection 으로  서버  쪽으로  데이터 보냄.
                                PHPRequest request = new PHPRequest("http://13.209.249.1/logincheck.php");//서버의 로그인 체크 문서로 요청함.
                                String result = request.PhPtest(logintext.getText().toString(), passwdtext.getText().toString(), "","");
                                // request.phptest부분에서  로그인 텍스트와  비밀번호 텍스를 PHPREQUSET  클래스로 보내서
                                //PHPREQUEST부분에서 받은 값들을   서버의  logincehck.php 파일로  보낸다.


                                //로그인 아이디와  비밀번호를 보냄.
                                //result는  그 값을 받아옴.
                                //서버에서 echo로 보낸  값
                                if (result.equals("1")) {

                                    //서버에서  1을  보냈을 경우에는 학생 로그인이 가능한 경우이다.
                                    //쉐어드 프리퍼런스에  학생  로그인 기록을 남겨두어  나중에 로그인 할때도
                                    //바로 자동 로그인이 가능하도록 한다.
                                    SharedPreferences loginid =getSharedPreferences("loginstudentid",MODE_PRIVATE);
                                    SharedPreferences.Editor editor=loginid.edit();
                                    editor.putString("loginid",logintext.getText().toString());
                                    editor.commit();

                                    //학생 로그인 했다는 토스트 날림
                                    toastcustomer.showcustomtaost(null, "로그인 하였습니다!");


                                    //로그인 인증이 되었으니, 학생 메인 화면 엑티비티 불러오고  로그인 엑티비티 종료
                                    Intent gotomstudentmain = new Intent(LoginActivity.this, MainactiviyForstudent.class);
                                    startActivity(gotomstudentmain);
                                    finish();

                                    Log.v("check","학생으로  로그인 함.");

                                }else if (result.equals("2")) {

                                    //서버에서 2를  결과 값으로 보내옴
                                    //학생이든  선생이든  둘중 이메일이 맞기는 한데  비밀번호가 틀렸다는 말임..
                                    toastcustomer.showcustomtaost(null, "비밀번호가 틀립니다 !!");
                                    Log.v("check","로그인 비밀번호 틀림");


                                }else if (result.equals("3")) {

                                    //서버에서  3을  결과 값으로 보내옴.
                                    //선생 학생  table모두에  해당  이메일 이 없음
                                    toastcustomer.showcustomtaost(null, "존재하지 않는 이메일입니다!");
                                    Log.v("check","학생,선생 모두 존재 하지 않는 이메일");

                                }else if(result.equals("4")){

                                    //서버에서 4를 보내옴,
                                    //학생 아이디이긴 한데,  간편로그인으로  로그인 가능한 아이디
                                    toastcustomer.showcustomtaost(null, "본 계정은 간편로그인으로 등록 되어있습니다.");
                                    Log.v("check","로그인api로 등록된  아이디");

                                }else if(result.equals("5")) {

                                    //서버에서  5를 보내옴.
                                    //이경우는 선생님 아이디로 로그인을 한 경우인데,  선생님의 경우  관리자페이지에서 관리자가
                                    //승인을 해줘야 한다.
                                    //승인이 대기 중인 경우에는 서버에서  5를 보내준다.
                                    //이때는  토스트 메세지를 보내기 보단  alertdailog를  보내서  알려준다.
                                    AlertDialog.Builder ad=new AlertDialog.Builder(LoginActivity.this);
                                    ad.setTitle("Decision in process");//다이얼로그 제목
                                    //다이얼로그 메세지
                                    ad.setMessage("please wait! and check youremail we will send you an email \nwhat's more you can use this \n'nadahdl12@gmail.com' to mail us.");
                                    ad.setCancelable(false);// 다이얼로그가  버튼 이외의  다른  공간 또는 back 버튼을 눌렀을때 취소되는것을 방지한다

                                    //알러르 다이얼로그에서  ok버튼을 눌렀을때
                                    ad.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            dialog.dismiss();     //다이얼로그 닫기

                                        }
                                    });//알럴트 다이얼로 ok버튼  눌렀을 때  끝

                                    ad.show();//다이얼로그를 보여준다.

                                    Log.v("check","선생님이  승인안됨.");

                                }else if(result.equals("6")) {


                                    //서버에서  6을 보내옴,
                                    //선생님  아이디로 로그인된것을 알린다.
                                    //선생님 아이디와 비밀번호가 다 맞음으로,  나중에  로그인시  자동로그인이 될수 있도록  쉐어드 프리퍼런스에
                                    //저장 시켜준다.
                                    SharedPreferences loginid =getSharedPreferences("loginteacherid",MODE_PRIVATE);//로그인 아이디 저장
                                    SharedPreferences.Editor editor=loginid.edit();//쉐어드 프리퍼런스 사용
                                    editor.putString("loginidteacher",logintext.getText().toString());//로그인 아이디 저장 함 'loginidteacher'라는 이름에다가
                                    editor.commit();//자료 받음

                                    ///로그인 결과  토스트 로 알림.
                                    toastcustomer.showcustomtaost(null, "welcome to  say hello!!");


                                     //선생님 로그인이므로  선생님 메인 화면으로  넘어가고 현재 로그인 엑티비티는 종료해준다.
                                    Intent gotomteachermain = new Intent(LoginActivity.this, MainactivityForTeacher.class);
                                    //선생님  로그인 창으로 이동한다.
                                    startActivity(gotomteachermain);
                                    finish();

                                    Log.v("check","선생님으로 로그인함.");


                                }

                        }//try문 끝부분
                        catch (MalformedURLException e) {
                                e.printStackTrace();
                        }//catch 문 끝

                    }//로그인 패스워드칸 모두 무언가 적혀있을떄 조건 끝
                }//로그인 이메일  텍스트 칸에  무언가 적혀있을때  조건 끝

            }//onclick끝
        });
        //로그인 버튼 눌렀을때   끝 //////////////////////////////////////////////////////////////////////////




      //비밀번호   찾기용 버튼///////////////////////////////////////////////
      findpasswordbtn.setOnClickListener(new View.OnClickListener() {

          @Override
          public void onClick(View v) {


              Intent gofindpw=new Intent(LoginActivity.this,InputEmailforfindpassword.class);
              startActivity(gofindpw);
            Log.v("check","비밀번호 찾기 버튼 눌림-> 비밀번호 찾기 화면으로~");
          }
      });
      //비밀 번호 찾기용 버튼 눌렀을 경우  끝.




        //카카오톡  로그인  api  버튼  눌렀을때 진행되는 부분.   원래  카카오톡에서  로그인 api를 쓸때 제공되는 버튼 부분을  숨기고 내가 커스톰한  로그인 부분을 보이도록
        //하였다.
        btn_custom_login_kakao.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {

                  //카카오톡  로그인 api에서 쓰이는 버튼이  커스톰버튼이  눌리면 실행됨.

                  //카카오톡 세션  콜백
                  callback = new SessionCallback(LoginActivity.this,LoginActivity.this);
                  //현재  세션의 여부를 알아보기 위해  콜백을 보낸다.
                  Session.getCurrentSession().addCallback(callback);

                   //아래는 세션이 있을때는 true값을  없을때는 false값을 내보낸다.
                   //세션이 없을때만  카카오톡  로그인  관련 선택창을 띄울수 있게  한다.
                   if(!Session.getCurrentSession().checkAndImplicitOpen()){
                      btn_kakao_login.performClick();
                  }else{
                       Log.v("check","카카오톡 로그인 api 실행됨.");
                   }
              }//on click닫힘.
          });
            //카카오톡 로그인 api 버튼 눌렀을 경우 끝.



        //회원 가입 창으로 연결되는 버튼 이벤트 시작이다.
        makeidbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //회원가입  선생 학생  중  고르는  엑티비티로 넘어간다.
                Intent gotomakeidactivity=new Intent(LoginActivity.this, MakeidActivity.class);

                //startactivityforresult를 사용한이유는 나중에 회원가입이 끝나고 바로  가입한  메일주소를  로그인창에  띄어주기 위해서 사용함.
                startActivityForResult(gotomakeidactivity,0);

                Log.v("check","회원가입 버튼  눌림");
               }
            });//회원가입 버트느 눌렀을때  이벤트 끝

    }//on create 끝남.





    @Override
    protected void onResume() {
        super.onResume();

        Log.v("check",getLocalClassName()+"의  onreume() 실행됨.");

        //onresume에  권한 체크를 넣은 이유는  혹시나 안드로이드 시스템 스택에 현재 엑티비티가  실행중인 상태에서  잠시
        //설정창을 들어가서  사용자가  권한 승인 취소를 하는 행위를 막기위해서  다시  background 에서  foreground올라올떄,
        //권한 체크를 한번더 해준다.

        //Checkpermission클래스를 실행해서   권한 들의   현재  상태를  리턴받는다.
        //혹시나  사용중  사용자가  앱 실행시 승인했던 권한을 다시 취소하면,  다시 돌아가서  권한승인을 하도록
        //Requestpermisssion 엑티비티로 넘겨준다.
        //checkpermission 클래스 선언
          checkpermission=new Checkpermission( LoginActivity.this);

          //checkpermission클래스의  checkpermission클래스 함수 리턴값 받기
          boolean permissioncheck=checkpermission.checkPermissions();

          //리턴값이 false이면 승인이 취소된 권한이 있는것 이므로,  requestpermisssion 클래스 로 다시 보내준다.
          if(!permissioncheck){

              Log.v("check","권한 중  승인 안된  권한이 발견되어  requestpermission엑티비티로 다시 넘어간다.");
              Intent intent=new Intent(LoginActivity.this,RequestPermission.class);
              startActivity(intent);

              //현재 activity를 종료시키는 이유는  Requestpermission 엑티비티에서  권한을 다  승인 했을 경우에  다시 로그인 엑티비티로 넘어갈텐데,
              //이때, 현재 activity를  종료시켜놓지 않으면  스택에 남아있어서  로그인 엑티비티가  스택에  두번  올라가지기 때문이다.
              //뒤로가기 누르다가 발견함.
              finish();
          }

    }//onreumes 끝







    //회원가입해서 플래그를 통해  회원 가입 중간 엑티비를 띄어  로그인화면으로
    //넘어오는  가입된 회원 이메일을 가져오기 위한  부분이다.
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        EditText emailtext=(EditText)findViewById(R.id.loginidttext);

       //카카오 톡  로그인 엑티비티가  따로  띄어지고  그 결과 값을 받아옴.
        if(Session.getCurrentSession().handleActivityResult(requestCode,resultCode,data)){

            return;
        }

        if(requestCode==0){//로그인 엑티비에서  회원가입 버튼을 눌렀을경우 들어간는 리퀘스트  코드로 0일 들어가서 0일 들어감.
            if(resultCode==120){//가입이 완료 되었을때  방금전 가입완료된 회원이메일을  이메일  창에 넣는다.

                String message=data.getStringExtra("useremail");//가입창에서 받아온 회원 이메일 -> 가입하고 바로  로그인 하기 쉽게  로그인 이메일 입력 부분에  올려줌.
                emailtext.setText(message);
            }
        }//request 코드  0일 때  끝


        super.onActivityResult(requestCode, resultCode, data);
    }//onactivityresult끝



//앱이  완전히 종료되는 부분이다.
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v("check",getLocalClassName()+"의  destroy 실행됨.");


        //세션 콜백 없애기
        Session.getCurrentSession().removeCallback(callback);
    }


}//login activity 클래스 마지막

