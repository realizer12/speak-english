package com.example.leedonghun.speakenglish;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.media.Image;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * speakenglish
 * Class: TaskToTimer.
 * Created by leedonghun.
 * Created On 2020-02-23.
 * Description: 비디오 채팅에서 지정 시간동안만  수업을 할수 잇도록  하기위해
 * 타이머 역할을 진행할  asyncktask클래스이다.
 * 여기서  settime을 통해  해당 시간을 받아 그 시간 만큼 타이머를 진행해준다.
 */
public class TaskToTimer extends AsyncTask<Void,Void,String> {


    private static final String RESULT_SUCCESS = "1";//결과 성공시
    private static final String RESULT_FAIL = "0";//결과 실패시

    private int user_position;//현재 유저의 포지션을 구분해서 -> 다이얼로그를  다르게  보여준다.
    private Context context;
    private Activity activity;
    private TextView timer;
    private String  studentprofile_url;
    private String student_name;

    private String teacher_uid;
    private String student_uid;

    private Retrofit retrofit;//리트로핏 선언
    private ApiService apiService;//api service 인터페이스

    private Retrofit retrofit_teacher;//리트로핏 선언
    private ApiService apiService_teacher;//api service 인터페이스

    private int time = -1;//혹시ㄴ time을 지정안되어있다면, 실행 한되게  -1 값 넣어줌.



    //학생일때 받는 값
    public void setTextView(int textViewId, Activity activity,int user_position,String teacher_uid,String student_uid){

        timer = (TextView)activity.findViewById(textViewId);
        this.activity=activity;//엑티비티
        this.context=activity;//엑티비티
        this.user_position=user_position;
        this.teacher_uid=teacher_uid;
        this.student_uid=student_uid;
    }

    //선생일때 받는값
    public void setTextView(int textViewId, Activity activity,int user_position,String studentprofile_url, String student_name,String teacher_uid,String student_uid){

        timer = (TextView)activity.findViewById(textViewId);
        this.activity=activity;//엑티비티
        this.context=activity;//oc
        this.user_position=user_position;
        this.studentprofile_url=studentprofile_url;
        this.student_name=student_name;
        this.student_uid=student_uid;
        this.teacher_uid=teacher_uid;
    }

    public void setTime(int time){

        this.time = time;

    }


    @Override
    protected void onPreExecute() {

        timer.setText((time % 3600 / 60)+"m  " + (time % 3600 % 60)+ "s");

    }



    @Override

    protected String doInBackground(Void... params) {
        while(time > 0){
            try{
                Thread.sleep(500);//시연을위해  500(0.5초) 으로  줄임.

                time--;//시간 -1 씩 빼줌.

                publishProgress();//뷰 업데이트

            }catch(InterruptedException e){

                return RESULT_FAIL;
            }
        }
        return RESULT_SUCCESS;
    }


    @Override

    protected void onProgressUpdate(Void... values) {

        timer.setText((time % 3600 / 60)+"m  " + (time % 3600 % 60)+ "s");

    }


    @Override

    protected void onPostExecute(String result) {

        if(user_position==0){//학생일때

            if(result.equals(RESULT_SUCCESS)){//수업 완전히 끝났을때.

                //학생 포인트 차감하고 선생님 현재 수업 끝난 시간 및  포인트  추가 시켜주는  메소드
                pay_std_point(student_uid,teacher_uid,System.currentTimeMillis());

                new Toastcustomer(context).showcustomtaost(null, "수업이 끝났습니다.!");


                // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
                final Dialog dlg = new Dialog(context);

                // 액티비티의 타이틀바를 숨긴다.
                dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);


                dlg.setCancelable(false);

                // 커스텀 다이얼로그의 레이아웃을 설정한다.
                dlg.setContentView(R.layout.give_star_score);

                WindowManager.LayoutParams params = dlg.getWindow().getAttributes();
                params.width = WindowManager.LayoutParams.MATCH_PARENT;//dialog width 늘려줌.
                dlg.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

                dlg.show();

                //다이얼로그 버튼
                Button finish_btn=dlg.findViewById(R.id.givestartsuccess);

                //다이얼로그 별 rating
                RatingBar ratingBar=dlg.findViewById(R.id.ratingBar);

                //다이얼로그 평점 넣는 edittext
                EditText edittxt_for_feedback=dlg.findViewById(R.id.give_feed_back);

                finish_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String getEdit = edittxt_for_feedback.getText().toString();//editext string 가지고옴.
                        getEdit=getEdit.trim();//공백도 취소

                        if(getEdit.getBytes().length<=0){//해당  평가 edittext가  없다면,

                            new Toastcustomer(context).showcustomtaost(edittxt_for_feedback, "수업에 대해 남길 말을 써주세요!");


                        }else{//해당 평가를 적은 경우

                            if(ratingBar.getRating()>0){//별점이  0이상일때 ->  제대로 함

                                //학생이 선생님에게 준 피드백 및  별점 저장하는 메소드
                                save_student_feedback(student_uid,teacher_uid,ratingBar.getRating(),edittxt_for_feedback.getText().toString(),ratingBar,dlg);


                            }else if(ratingBar.getRating()<=0){//해당  별점이  0 또는 그 이하일때

                                new Toastcustomer(context).showcustomtaost(null, "수업 별점을 매겨 주세요!!");

                            }


                        }//해당 평가 적은 경우 끝.

                    }//finishbtn-> onCLick() 끝
                });//finish버튼  클릭 리스너 끝.

            }else{//수업 도중에  꺼졌을때,


                new Toastcustomer(context).showcustomtaost(null, "수업이 중단되었습니다..");
                activity.finish();//현재 videocall 엑티비티 끝냄,

            }

        }else if(user_position==1){//선생일때
            if(result.equals(RESULT_SUCCESS)) {//수업 완전히 끝났을때.

                new Toastcustomer(context).showcustomtaost(null, "Class is finished!!");


                // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
                final Dialog dlgd = new Dialog(context);

                // 액티비티의 타이틀바를 숨긴다.
                dlgd.requestWindowFeature(Window.FEATURE_NO_TITLE);


                dlgd.setCancelable(false);

                // 커스텀 다이얼로그의 레이아웃을 설정한다.
                dlgd.setContentView(R.layout.dialog_for_teacher_give_feedback);


                WindowManager.LayoutParams params = dlgd.getWindow().getAttributes();
                params.width = WindowManager.LayoutParams.MATCH_PARENT;//dialog width 늘려줌.
                dlgd.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);


                dlgd.show();

                //학생 프로필 이미지
                ImageView profile_img=dlgd.findViewById(R.id.std_profile);

                URL url= null;
                try {

                    url = new URL(studentprofile_url);
                    //학생  프로필 사진 넣어줌.
                    Glide.with(context).load(url).into(profile_img);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }


                //학생 이름
                TextView student_name_txt=dlgd.findViewById(R.id.std_name);

                //학생 이름 넣어줌.
                student_name_txt.setText(student_name);

                //피드백 적는  에딧텍스트
                EditText edit_for_feedback=dlgd.findViewById(R.id.editText_for_feedback);

                //완료 버튼
                Button finish_btn=dlgd.findViewById(R.id.button_for_give_feedback);


                finish_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //피드백 edittext에 아무말도 안적혀있을때
                        if(edit_for_feedback.getText().toString().getBytes().length<=0){

                            new Toastcustomer(context).showcustomtaost(edit_for_feedback, "Plz  give any feed back!!");


                        }else{//피드백이 적혀있을때,

                            //선생님이  학생에게 준 피드백을 저장하는  메소드
                            save_teacher_feedback(student_uid,teacher_uid,edit_for_feedback.getText().toString(),dlgd);

                        }


                    }
                });


            }else {

                new Toastcustomer(context).showcustomtaost(null, "Class stoped!!");

                activity.finish();//현재 videocall 엑티비티 끝냄,
            }
        }

    }

    //선생님이  학생에게 준 피드백을 저장하는  메소드
    public void save_teacher_feedback(String suid,String tuid,String teacher_feedback,Dialog dlgd){


        retrofit=new Retrofit.Builder().baseUrl(ApiService.baseurl).build();
        apiService=retrofit.create(ApiService.class);

        Call<ResponseBody> upload_std_feedback = apiService.upload_teacher_feedbackto_s(suid, tuid, teacher_feedback);//studentinforesult 클래스를 return 타입으로

        upload_std_feedback.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {

                    String result=response.body().string();// 왠지 모르겠는데 계속  뒷공간에  공백에 생김.

                    //그래서  결과값에 공백 지워줌.
                    if(result.equals("1")){//성공시

                        new Toastcustomer(context).showcustomtaost(null, "Thank you for your feedback");
                        dlgd.dismiss();//다이얼로그 끝냄.
                        activity.finish();//현재 videocall 엑티비티 끝냄,

                    }else if(result.equals("2")){//실패시

                        new Toastcustomer(context).showcustomtaost(null, "별점 주는데  에러남. ");
                        dlgd.dismiss();//다이얼로그 끝냄.
                        activity.finish();//현재 videocall 엑티비티 끝냄,

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.v("check","학생 피드백 업로드 중  -> 에러남"+t );
            }
        });


    }//save_teacher_feedback() 끝


     //수업이 다 끝나면,  알러트로  수업 평가할때  학생 포인트 10원이  차감 되고  선생님 포인트 추가 및  날짜가  저장되는  작업 진행한다
    public void pay_std_point(String std_uid,String teacher_uid,long finish_time){

        Log.v("check","TaskToTimer클래스에서  학생 포인트 페이및  선생님  포인트 추가 시켜주는 pay_std_point() 실행됨");

        retrofit=new Retrofit.Builder().baseUrl(ApiService.baseurl).build();
        apiService=retrofit.create(ApiService.class);
        Call<ResponseBody>update_std_ad_teacher_point=apiService.chagne_std_and_teacher_point_amount(std_uid, teacher_uid, finish_time);

        update_std_ad_teacher_point.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    Log.v("check", "TaskToTimer의  pay_std_point 실행후  response 값-> "+result);






                } catch (IOException e) {
                    e.printStackTrace();
                }




            }//onResponse() 끝


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.v("check", "TaskToTimer의  pay_std_point 실행후 실패->"+t.getMessage());


            }//onFailure() 끝
        });


    }//pay_std_point()끝



    //학생이 선생님에게 준 피드백 및  별점 저장하는 메소드
   public void save_student_feedback(String suid,String tuid,float rating,String student_feedback,RatingBar ratingBar,Dialog dlg){


        retrofit=new Retrofit.Builder().baseUrl(ApiService.baseurl).build();
        apiService=retrofit.create(ApiService.class);

        Call<ResponseBody> upload_std_feedback = apiService.upload_std_feedbackto_t(suid, tuid, rating,student_feedback);//studentinforesult 클래스를 return 타입으로

        upload_std_feedback.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {

                 String result=response.body().string();

                 Log.v("check", result+"학생");
                  if(result.equals("1")){//성공시

                      new Toastcustomer(context).showcustomtaost(null, "별점 "+ratingBar.getRating()+"점을  주셨습니다!");
                      dlg.dismiss();//다이얼로그 끝냄.
                      activity.finish();//현재 videocall 엑티비티 끝냄,

                  }else if(result.equals("2")){//실패시

                      new Toastcustomer(context).showcustomtaost(null, "별점 주는데  에러남. ");
                      dlg.dismiss();//다이얼로그 끝냄.
                      activity.finish();//현재 videocall 엑티비티 끝냄,

                  }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                  Log.v("check","학생 피드백 업로드 중  -> 에러남"+t );
            }
        });

    }//save_student_feedback() 끝

}
