package com.example.leedonghun.speakenglish;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.auth.ISessionCallback;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;

/**
 * speakenglish
 * Class: SessionCallback.
 * Created by leedonghun.
 * Created On 2019-07-24.
 * Description:
 * 세션 콜백 받는 곳으로  카카오톡이  연결되어  세션이  등록되는지 여부를  결정한다.
 *
 */
public class SessionCallback implements ISessionCallback {

    Toastcustomer toastcustomer;
    Context context;
    Activity activity;
    Retrofit retrofit;
    Retrofit retrofit2;
    ApiService apiService;
    ApiService apiService2;
    int initailcount=1;
    int initoastcount=1;

    SessionCallback(Context context, Activity activity){

        this.activity=activity;
        this.context=context;
        toastcustomer=new Toastcustomer(context);
    }


    //세션연결됨.
    @Override
    public void onSessionOpened() {
        Log.v("check","카카오톡 로그인 api 세션 연결  성공");

       // movetoStudentMainActivity(null);

         requestme();


    }//session open

    @Override
    public void onSessionOpenFailed(KakaoException e) {
        if(e != null) {
            Log.v("check","카카오톡 로그인 api 세션 연결 실패함. 에러내용: "+e);
        }
    }




    ///카카오톡 로그인 api를 사용해서  로그인을 하는 경우  기본 학생회원가입처럼
    //회원 영어이름을  받아서  디비에 저장해줘야됨.
    //그래서 이부분에서 카카오톡 회원 관련 로그인하고 디비 저장  지정해줌
    //그러므로,  따로 영어이름을 입력받는 커스톰 다이얼로그를 띄어줘야한다.
    //그리고 이부분에서  알맞게 절차를 따랐을때,  학생메인화면으로 가지고
    //그렇지않았을때는  세션 연결을  없앤다. -> requestUnlink
    private void movetoStudentMainActivity(final String email){
         initailcount++;
         final String password="ssss";
         final CustomDialog customDialog=new CustomDialog(context);
         final String loginapiornot="1";

        //커스톰다이얼로그 setgetcustomdialogresult 메소드로  getcustomdialogresult ->인터페이스 호출
        customDialog.setGetCustomDailogResult(new GetCustomDailogResult() {

            //확인을  눌렀을때
            //이부분에서  학생 db로  정보  저장 진행됨.
            //checkemail에  저장되는 내용은  학생  이메일, 비밀번호(랜덤 배정), 영어이름,  로그인 api 여부 1만  넘겨주면됨.
            @Override
            public void onPositveClicked(String englishname) {

                //retrofit을 이용한  db에  카카오 로그인  회원 기록  넣기
               retrofit2=new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create()).build();
               apiService2=retrofit2.create(ApiService.class);

                //아래는 requestme 에서  매개 변수로 받아온 카카오톡 로그인 이메일
                RequestBody loginapiemail=RequestBody.create(MediaType.parse("text/plain"),email);

                //아래는 카카오 로그인  패스워드 -> ssss로  지정해둠. -> 만약에 실무라면  좀더  어렵게 암호화해서 넣어둬야 할듯
                RequestBody loginapipasswd=RequestBody.create(MediaType.parse("text/plain"),password);

                //아래는 커스톰 다이얼로그에서  입력받는  영어이름
                RequestBody loginapienglishname=RequestBody.create(MediaType.parse("text/plain"),englishname);

                //아래는 로그인 api db에서 설정할  값-> 카카오톡 로그인 이므로 값은 1로 지정됨.
                RequestBody loginapi=RequestBody.create(MediaType.parse("text/plain"),loginapiornot);

                //db에  해당 정보들을 보내준다.
                Call<ResponseBody>uploadstudentapi=apiService2.uploadloginapistudentinfo(loginapiemail,loginapipasswd,loginapienglishname,loginapi);
                uploadstudentapi.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        try {
                            String  result2 = response.body().string();

                           switch (result2){


                                // 결과가 1일때는  정상적으로 디비에  저장이 된것임
                               case "1":
                                   Log.v("check","카카오 로그인 정보가 정상적으로  저장됨.");


                                   //나중에  자동로그인을 진행할수 있도록  카카오톡 로그인 api의 이메일을  쉐어드에  저장함.
                                   SharedPreferences loginid =activity.getSharedPreferences("loginstudentid",MODE_PRIVATE);
                                   SharedPreferences.Editor editor=loginid.edit();
                                   editor.putString("loginid",email);
                                   editor.commit();
                                   toastcustomer.showcustomtaost(null,"학생 로그인 하였습니다.");
                                   //카카오톡 로그인 정보가  정상적으로  db에 넣어졌으므로, 학생 메인 화면으로 들어간다.
                                   Intent gotomstudentmain = new Intent(activity, MainactiviyForstudent.class);//학생 메인화면으로 가기
                                   activity.startActivity(gotomstudentmain);//학생화면  시작
                                   activity.finish();//현재 로그인 화면 피니쉬됨.


                                   break;

                               // 로그인 정보를  디비에 넣는 상황에서 문제가 생김.
                               case "2":

                                   Log.v("check","카카오 로그인 정보가 정상적으로 저장되지 않음.");



                                   break;


                               //이메일 중복인데 자체 메일과 중복이아니고 기존  api메일과 중복일때다.
                               case "3":

                                   Log.v("check","카카오 로그인 정보가 이미 등록되어있습니다.  오류");

                                   break;


                               //이메일 중복인데,자체 메일과 중복일때이다.
                               case "4":

                                   Log.v("check","카카오 로그인 정보가 기존 이메일 학생 정보와 중복 됩니다. 오류");


                                   break;


                           }


                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.v("check","카카오톡 로그인  새로 db에 넣는 과정에서 요류 생김 /response까지 받음. -> 오류내용:"+e);
                        }


                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                      Log.v("check","카카오톡 로그인  새로 db에 넣는 과정에서 요류 생김 -> 오류내용:"+throwable);
                    }
                });



                customDialog.dismiss();


            }

            //취소를  눌렀을때
            @Override
            public void onNegativeClicked() {

               unlinkSession(customDialog);
               customDialog.dismiss();

            }//취소를 눌렀을떄 끝
        });

      //커스톰 다이얼로그  끝
      customDialog.show();

    }//movetostudentactivity 끝


    //requestme  코드는
    private void requestme() {

        List<String> keys = new ArrayList<>();
        keys.add("kakao_account.email");
        UserManagement.getInstance().me(keys, new MeV2ResponseCallback() {

            //카카오톡 사용자 정보 가져오기
            @Override
            public void onFailure(ErrorResult errorResult) {

                //에러가 생겼으므로 세션 언링크 진행한다.

                    Log.v("check","카카오  정보가져오는 중에  에러 생김.  에러내용: "+errorResult);


            }

            //세션이  이미 닫혀 있음.
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                //에러가 생겼으므로 세션 언링크 진행한다.

                    Log.v("check","카카오톡 로그인 api 세션취소중  세션이 이미 닫혀있음.: "+errorResult);


            }


            //연결된 세션의 사용자의 정보 가져오기 성공
            @Override
            public void onSuccess(final MeV2Response result) {


                Log.v("check", "카카오 로그인 email: " + result.getKakaoAccount().getEmail());

                retrofit=new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create()).build();
                apiService=retrofit.create(ApiService.class);

                RequestBody loginapiemail=RequestBody.create(MediaType.parse("text/plain"),result.getKakaoAccount().getEmail());
                Call<ResponseBody>checkloignapiemailregistered=apiService.checkloginapiregisered(loginapiemail);
                checkloignapiemailregistered.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                        try {

                            String  result1 = response.body().string();
                            Log.v("check", "실헹"+result1);


                            //카카오 로그인 api로 가입하지않고, 일반 이메일로 학생또는 선생에 가입한 경우
                            if(result1.equals("1")){

                                Log.v("check","이미  일반 이메일로 로그인 되어있음.");
                                //이부분은  세션 오픈이  왜 그런지는 추가 확인이 필요하지만,  1번이상 실행되는 경우가 생겨서  아래 토스트가 계속해서  진행되는
                                //상황이 생김.
                                //이를 방지하기위해  세션클래스 가 로그인엑티비티에서 실행될때,
                                //값을 1로 가지는 initoastcount변수를  만들어
                                //서버에  요청을 확인하면,  1을 즈아 시켜 2를 만들고
                                //2일 경우만  토스트를 보냄, 이렇게 되면  세션 오픈이 여러번  실행되어도
                                //2가 계속 1씩 증가되므로  다시 처음  세션 연결 요청 할때가 아니면,  2가 아니게되어  토스트는  나오지 않는다.
                                initoastcount++;

                                unlinkSession(null);

                                if(initoastcount==2){

                                    toastcustomer.showcustomtaost(null,"해당 아이디로 가입한적이 없었나요?\n 이미 사용중인 아이디네요",1500,300);

                                }else{
                                    //세션 오픈이 계속 진행되어도 2가 될수 없음.
                                    initoastcount=-1000;
                                }


                            }else if(result1.equals("2")){//학생, 선생 모두  가입기록이 없을때이다.//가입을  하기위해  영어이름을  묻는  커스톰 다이얼로그를 실행한다.
                                                          //이 커스톰 다이얼로그에서  서버로  로그인 정보를  저장한다. -> 이메일=카카오로그인  , 로그인 api=1,  그리고  패스워드는  ssss

                                Log.v("check","일반 그리고 로그인 api로  가입한 기록이 없어서  새로 가입을 시도해야됨. -> 영어이름 물어보는 다이얼로그 실행" );
                                 //위의  세션 오픈 계속 진행되어  토스트  계속 나오는것과 마찬가지로,
                                 //커스톰 다이얼로그도 똑같은 형태로  계속 진행되는 것을 막는다.
                                if(initailcount==1){

                                    //커스톰 다이얼로그 ->  카카오  톡 로그인 이메일 정보를  넣어서 실행
                                    movetoStudentMainActivity(result.getKakaoAccount().getEmail());

                                }else{
                                    initailcount=-1000;
                                }

                            }else if(result1.equals("3")){//카카오톡 로그인으로 학생 가입한 경우 -> 이경우에는  바로  로그인 처리하여  학생 메인엑티비티로 넘겨준다.
                                                          //shrared로  이메일 저장 하고  메인엑티비티로 넘겨줘야됨.

                                 Log.v("check","카카오톡 로그인 api로 로그인 한 기록이 있어서  정상적으로 로그인함");
                                //나중에  자동로그인을 진행할수 있도록  카카오톡 로그인 api의 이메일을  쉐어드에  저장함.
                                SharedPreferences loginid =activity.getSharedPreferences("loginstudentid",MODE_PRIVATE);
                                SharedPreferences.Editor editor=loginid.edit();
                                editor.putString("loginid",result.getKakaoAccount().getEmail());
                                editor.commit();
                                toastcustomer.showcustomtaost(null,"학생 로그인 하였습니다.");
                                //카카오톡 로그인 정보가  정상적으로  db에 넣어졌으므로, 학생 메인 화면으로 들어간다.
                                Intent gotomstudentmain = new Intent(activity, MainactiviyForstudent.class);//학생 메인화면으로 가기
                                activity.startActivity(gotomstudentmain);//학생화면  시작
                                activity.finish();//현재 로그인 화면 피니쉬됨.

                            }


                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                        Log.v("check", throwable.toString());
                    }
                });


            }

        });


    }


    //카카오세션  unlink하기
   private  void unlinkSession(final CustomDialog customDialog){

       //카카오톡 로그인 api 는  카카오톡 로그인  웹뷰에서 사용자 인증이 되었을떄, acesstoken이 발급되고
       //해당 로그인 아이디가 등록된다. (세션연결)
       //이때  아래  requestunlink를  통해 등록을 취소시킬수 있다.
       UserManagement.getInstance().requestUnlink(new UnLinkResponseCallback() {
           @Override
           public void onFailure(ErrorResult errorResult) {
               Log.v("check","카카오톡 로그인 api 세션취소중  실패하였음 에러내용: "+errorResult);

           }

           @Override
           public void onSessionClosed(ErrorResult errorResult) {
               Log.v("check","카카오톡 로그인 api 세션취소중  세션이 이미 닫혀있음.: "+errorResult);
           }

           @Override
           public void onNotSignedUp() {
               Log.v("check","카카오톡 로그인 api 세션취소중  사용자 확인이 안되어있음");
           }

           @Override
           public void onSuccess(Long userId) {

               if(customDialog !=null){
                   customDialog.dismiss();
               }

               Log.v("check","카카오톡 로그인 api 정상적으로 등록 취소됨.");

           }
       });//requestunlink 끝부분

   }



}
