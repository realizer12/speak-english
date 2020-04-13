package com.example.leedonghun.speakenglish;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * speakenglish
 * Class: teacher_feedback_acapter.
 * Created by leedonghun.
 * Created On 2020-02-27.
 * Description:선생님이 받은 피드백들을  서버에서 받아서 뿌려주기 위한  리사이클러뷰 어뎁터이다.
 */
public class student_feedback_adapter extends RecyclerView.Adapter {

    private Context context;
    private ArrayList<JsonObject> student_feedback;
    private LayoutInflater layoutInflater;//피드백 내용이 담길 커스텀뷰 인플레이터 하기위한 인플레이터
    //본 adapter클래스 생성자
    student_feedback_adapter(Context context, ArrayList<JsonObject> student_feedback){

        this.context=context;
        this.student_feedback=student_feedback;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;

        layoutInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view=layoutInflater.inflate(R.layout.student_feedback_item, parent,false);

        return new student_feedback_viewholer(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

       String feedback_content=student_feedback.get(position).get("feedbackcontent").getAsString();
       String teacheruid=student_feedback.get(position).get("teacheruid").getAsString();


        get_teacher_info(teacheruid,  ((student_feedback_viewholer) holder).teacher_profile, ((student_feedback_viewholer) holder).teacher_name );

        ((student_feedback_viewholer) holder).teacher_feedback_content.setText(feedback_content);

    }

    @Override
    public int getItemCount() {
        return student_feedback.size();
    }


    class student_feedback_viewholer extends RecyclerView.ViewHolder{

        ImageView teacher_profile;//선생님 프로필 사진 1-1
        TextView teacher_name;//선생님 이름 1-2
        TextView teacher_feedback_content;//선생님에게 받은 피드백 내용 1-3

        public student_feedback_viewholer(@NonNull View itemView) {
            super(itemView);

            teacher_profile=itemView.findViewById(R.id.teacher_profile_img_for_feed);//1-1
            teacher_name=itemView.findViewById(R.id.teacher_name_for_feed);//1-2

            teacher_feedback_content=itemView.findViewById(R.id.teacher_feedback_txt);//1-3


            final boolean[] isclickable = {false};//클릭 여부 확인 -> 처음엔 false

            teacher_feedback_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(!isclickable[0]) {//클릭여부가 false일때

                        teacher_feedback_content.setMaxLines(Integer.MAX_VALUE);
                        isclickable[0] =true;//

                    }else{//클릭여부가 true 일때

                        teacher_feedback_content.setMaxLines(2);
                        isclickable[0]=false;
                    }

                }
            });

        }

    }//student_feedback_viewholer끝


    private void get_teacher_info(String teacher_uid,ImageView teacher_profile_imgview,TextView teacher_name){

        Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create())//json으로 받고 싶으면 쓰면안됨..
                .build();//리트로핏 뷸딩
        ApiService apiService = retrofit.create(ApiService.class);//api인터페이스 크리에이트

        Call<GetTeacherUrlandName> getTeacherUrlandNameCall=apiService.get_teacher_url_and_nmae(teacher_uid);

         getTeacherUrlandNameCall.enqueue(new Callback<GetTeacherUrlandName>() {
             @Override
             public void onResponse(Call<GetTeacherUrlandName> call, Response<GetTeacherUrlandName> response) {
                 URL url= null;
                 try {
                     url = new URL("http://13.209.249.1/"+response.body().getTeacher_profile());

                 } catch (MalformedURLException e) {
                     e.printStackTrace();
                 }


                 //상대방  프로필 사진 넣어줌.
                 Glide.with(context).load(url).into(teacher_profile_imgview);
                 teacher_name.setText(response.body().getTeacher_name()+" teacher");
             }

             @Override
             public void onFailure(Call<GetTeacherUrlandName> call, Throwable t) {
                 Log.v("checkstdinfo", String.valueOf(t));
             }
         });

    }


}//teacher_feedback_adapter 끝



