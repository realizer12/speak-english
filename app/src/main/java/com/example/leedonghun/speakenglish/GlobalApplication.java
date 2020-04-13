package com.example.leedonghun.speakenglish;


import android.app.Application;
import android.content.Context;

import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthType;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;
/**
 * speakenglish
 * Class: GlobalApplication.
 * Created by leedonghun.
 * Created On 2019-07-24.
 * Description:
 * 카카오톡  로그인에서  로그인  인증방법  등등 세션 확인할때
 * 진행  프로세스를  설정하는  클래스이다.
 *
 */

public class GlobalApplication extends Application {

    //선생님일때  자기 uid저장
    private String teacheruid;

    public String getTeacheruid() {
        return teacheruid;
    }

    public void setTeacheruid(String teacheruid) {
        this.teacheruid = teacheruid;
    }



    //학생 이름이랑  프로필 url을  video chatting 할때  사용하기 위해서
    private String student_name;//학생 이름
    private String studnet_profile_url;//학생 프로필 url
    private String studnet_uid;//학생 uid

    public String getStudent_name() {//학생 이름 가져오기
        return student_name;
    }

    public void setStudent_name(String student_name) {//학생 이름 set
        this.student_name = student_name;
    }

    public String getStudnet_profile_url() {//학생 프로필 url get
        return studnet_profile_url;
    }

    public void setStudnet_profile_url(String studnet_profile_url) {//학생 프로필 url set
        this.studnet_profile_url = studnet_profile_url;
    }

    public String getStudnet_uid() {
        return studnet_uid;
    }

    public void setStudnet_uid(String studnet_uid) {
        this.studnet_uid = studnet_uid;
    }


    private class KakaoSDKAdapter extends KakaoAdapter{


       public ISessionConfig getSessionConfig(){
           return new ISessionConfig() {
               @Override
               public AuthType[] getAuthTypes() {

                   //로그인시 인증받을 타입이다.
                   //모든 로그인 방식을 선택함.
                   return new AuthType[]{AuthType.KAKAO_LOGIN_ALL};
               }



               //카카오톡  로그인관련 웹뷰가  나왔으럐때,  pause resume시에 timer를
               //설정하여  웹뷰를  시간이 지나면  없애 cpu 소모를 절약하는내용이ㅏㄷ.
               //웹뷰가나오는 엑티비티에
               //true일경우에  onpaue와  onresume에  timer를 설정해줘야한다.
               //지금은  false
               @Override
               public boolean isUsingWebviewTimer() {
                   return false;
               }



               //로그인 할떄 acesstoken과 refresh token을 지정할때  암호화여부를 결정한다.
               @Override
               public boolean isSecureMode() {
                   return false;
               }



               //일반 사용자가 아니고  kakao와 제휴된 앱에서 사용되는 값일떄  값을 지정해준다.
               //아니면  indivial 값을 사용됨.

               @Override
               public ApprovalType getApprovalType() {

                   //일반사용자
                   return ApprovalType.INDIVIDUAL;
               }


               //kakao sdk에서 사용된  webview에서 email입력폼에 data를  save할지 여부임.
               @Override
               public boolean isSaveFormData() {
                   //세이브 한다로 지정.
                   return true;
               }
           };//session값 리턴
       }//getSessionconfig 메소드 끝.



       @Override
       public IApplicationConfig getApplicationConfig() {
           return new IApplicationConfig(){


               //어플리케이션의 context
               @Override
               public Context getApplicationContext() {
                   return GlobalApplication.this.getApplicationContext();
               }
           };
       }



   }//KakaoSDKAdapter  끝

  
    public  void onCreate(){

        super.onCreate();
        KakaoSDK.init(new KakaoSDKAdapter());
//     Log.e("Key Hash : ", getKeyHash(this));
    }//oncreate 종료




}//globalapplciation 끝
