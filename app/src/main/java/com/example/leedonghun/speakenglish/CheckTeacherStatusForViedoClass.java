package com.example.leedonghun.speakenglish;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
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

/**
 * speakenglish
 * Class: CheckTeacherStatusForViedoClass.
 * Created by leedonghun.
 * Created On 2020-02-18.
 *
 * Description:학생이 선생님에게  수업을 걸었을때  해당 선생님이 수업이 가능한지 여부를  체크해서  결과를 보내주는  클래스이다.
 */
public class CheckTeacherStatusForViedoClass  {

    String Teacheruid;
    Context context;

    /**CheckTeacherStatusForViedoClas 생성자
     * ->  선생님  uid와   context를 받아옴 */
    CheckTeacherStatusForViedoClass(String teacher_uid,Context context){

        this.Teacheruid=teacher_uid;// 선생님  uid
        this.context=context;

    }//CheckTeacherStatusForViedoClass 생성자.


    /** 학생에게 선생님이 비디오 콜이 가능한지  결과를 return해준다.
     *  true 면 ,화상 수업이 가능하고,  false면  화상 수업이 불가능하다.  */
    public boolean check(){

        boolean check_result=false;//기본  체크값은  false이다.

        boolean result_for_check_onoff=check_teacher_on_off(Teacheruid);//선생님 onoff결과

        if(result_for_check_onoff){//위  두 결과가  모두 true 일경우  check결과를 true로 보내준다.

            check_result=true;
        }

        return check_result;

    }//check() 끝



    /**선생님의   on off 상태를  서버에서 받아와  결과를 체크하고 boolean값으로 return 해준다.
    * true 이면,  선생님은 온라인  상태이고,   false이면,  선생님은 오프라인 상태이거나 ,
    * 해당  상태를 가지고 온데서  문제가 생긴 경우우다.  */
    private boolean check_teacher_on_off(String teacheruid){
        Log.v("check", "check_teacher_on_off()  -> teacheruid=>"+teacheruid);

        boolean check_teacher_on_off_result=false;//


        Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create())//json으로 받고 싶으면 쓰면안됨..
                .build();//리트로핏 뷸딩
        ApiService apiService = retrofit.create(ApiService.class);//api인터페이스 크리에이트

        RequestBody teacheruid_toget_loginstatus=RequestBody.create(MediaType.parse("text/plain"), teacheruid);//서버로 보낼 선생님 로그인 status

        Call<ResponseBody> getteacherloginstatus=apiService.getteacherlogin_status(teacheruid_toget_loginstatus);//선생님 로그인 on off 정보 얻기위한  call객체


        //현재  메소드에서  return값으로 내보내기 위해  ->   retrofit을  비동기가(enque) 아닌  동기화 (excute) 로 사용 하였다.
        try {

            //선생님 on / off 결과
            String onoff_result= getteacherloginstatus.execute().body().string();

            if(onoff_result.equals("1")) {// 선생님  online 상태  일때  ->  result값 true로 바꿔줌. /

                check_teacher_on_off_result=true;

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return check_teacher_on_off_result;//onoff결과  리턴

    }//check_teacher_on_off_result() 끝


}//CheckTeacherStatusForViedoClass 끝
