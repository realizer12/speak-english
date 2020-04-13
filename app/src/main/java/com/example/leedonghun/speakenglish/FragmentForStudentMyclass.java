package com.example.leedonghun.speakenglish;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
//import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * speakenglish
 * Class: FragmentForStudentMyclass.
 * Created by leedonghun.
 * Created On 2019-01-11.
 * Description:학생 화면  내가 받은 피드백  프레그 먼트 이다.
 */
public class FragmentForStudentMyclass extends Fragment {

    //피드백 뿌려줄  리사이클러뷰
   private RecyclerView recyclerView_for_teacher_feedback;//1-1

   //학생 이메일
   private String studentemail;//1-2

    //이미지뷰 rotation 효과
    private Animation rotae_imageview;//1-3

    //학생이 받은 피드백  리스트  새로고침 이미지뷰
    private ImageView refresh_btn_for_feedback;//1-4


    //선생님  피드백 리사이클러뷰 관련 객체들
    private student_feedback_adapter student_feedback_adapter;
    private LinearLayoutManager studenteedbackLayoytManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        ViewGroup rootView=(ViewGroup)inflater.inflate(R.layout.fragment_for_myclasstypeinstudent,container,false);

        recyclerView_for_teacher_feedback=rootView.findViewById(R.id.recycler_view_for_teacher_feedback);//1-1
        refresh_btn_for_feedback=rootView.findViewById(R.id.refresh_student_feedback);//1-4

        SharedPreferences getstudentemail = getActivity().getSharedPreferences("loginstudentid",Context.MODE_PRIVATE);//로그인 아이디  쉐어드에 담긴거 가져옴
        studentemail = getstudentemail.getString("loginid", "");//학생 로그인 이메일 가져옴.


        //1-3
        rotae_imageview = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_image_view);

        //1-4클릭이벤트
        refresh_btn_for_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                refresh_btn_for_feedback.startAnimation(rotae_imageview);
                get_student_feedback_list(studentemail, recyclerView_for_teacher_feedback);
            }
        });

        return rootView;

    }//onCreateView끝


    @Override
    public void onResume() {
        super.onResume();

        get_student_feedback_list(studentemail, recyclerView_for_teacher_feedback);

    }//onResume() 끝

    public void get_student_feedback_list(String studentemail, RecyclerView recyclerView_for_feedback){


       Gson gson = new GsonBuilder().setLenient().create();
       Retrofit retrofit=new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create(gson))//json으로 받고 싶으면 쓰면안됨..
               .build();//리트로핏 뷸딩
       ApiService apiService=retrofit.create(ApiService.class);//api인터페이스 크리에이트
       Call<GetStudentFeedback> getTeacherFeedback =apiService.get_student_feedack(studentemail);

       getTeacherFeedback.enqueue(new Callback<GetStudentFeedback>() {
           @Override
           public void onResponse(Call<GetStudentFeedback> call, Response<GetStudentFeedback> response) {

               ArrayList<JsonObject> studentfeedback=response.body().getGet_student_feedback();
               Log.v("check", "학생 피드백"+String.valueOf(studentfeedback));

               student_feedback_adapter=new student_feedback_adapter(getActivity(),studentfeedback);
               studenteedbackLayoytManager=new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL,false);
               studenteedbackLayoytManager.setReverseLayout(false);
               studenteedbackLayoytManager.setStackFromEnd(false);
               recyclerView_for_feedback.setLayoutManager(studenteedbackLayoytManager);
               recyclerView_for_feedback.setAdapter(student_feedback_adapter);
               recyclerView_for_feedback.setNestedScrollingEnabled(false);//스크롤 부드럽게


           }

           @Override
           public void onFailure(Call<GetStudentFeedback> call, Throwable t) {
               Log.v("check", "학생 피드백 가져오기 에러->"+t);
           }
       });




   }//get_student_feedback_list 메소드 끝.


}//FragmentForStudentMyclass끝
