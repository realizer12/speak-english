package com.example.leedonghun.speakenglish;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;

import org.json.JSONObject;

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
 * Class: teacherfeedbackAdapter.
 * Created by leedonghun.
 * Created On 2020-02-26.
 * Description:선생님  피드백 내용을  받아서 리사이클러뷰에  뿌려줄   어뎁터
 */
public class teacherfeedbackAdapter extends RecyclerView.Adapter {

    //로그용
    private final String mentforlog="teacherfeedbackAdapter";

    //선생님 피드백  들어갈 어레이리스트
    private ArrayList<JsonObject> teacher_feedback;

    private Context context;

    private LayoutInflater layoutInflater;//피드백 내용이 담길 커스텀뷰 인플레이터 하기위한 인플레이터


    teacherfeedbackAdapter(Context context,ArrayList<JsonObject>teacher_feedback){

        Log.v("check", mentforlog+"의 생성자 실행됨");

        this.context=context;
        this.teacher_feedback=teacher_feedback;

    }//teacherfeedbackAdapter 생성자 끝,

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.v("check", mentforlog+"의 onCreateViewHolder 실행됨");

        View view;

        layoutInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view=layoutInflater.inflate(R.layout.teacher_feedback_item, parent,false);

        return new teacherfeedback_viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        String feedback_content=teacher_feedback.get(position).get("feedbackcontent").getAsString();
        float feedback_rating=teacher_feedback.get(position).get("rating").getAsFloat();
        String stduid=teacher_feedback.get(position).get("studentuid").getAsString();

        get_student_info(stduid,((teacherfeedback_viewholder) holder).student_profileimg,((teacherfeedback_viewholder) holder).student_name);

        ((teacherfeedback_viewholder) holder).student_feedback_content.setText(feedback_content);
        ((teacherfeedback_viewholder) holder).stundent_feedback_rating.setRating(feedback_rating);
        ((teacherfeedback_viewholder) holder).student_feedback_rating_number.setText(String.valueOf(feedback_rating));
    }



    @Override
    public int getItemCount() {
        return teacher_feedback.size();
    }


    //뷰홀더
    class teacherfeedback_viewholder extends RecyclerView.ViewHolder{

        //학생 프로필 사진  1-1
        ImageView student_profileimg;

        //학생 이름  1-2
        TextView  student_name;

        //학생이 쓴 피드백 1-3
        TextView  student_feedback_content;

        //학생 rating 점수  1-4
        TextView student_feedback_rating_number;

        //학생이 준 별점 1-5
        RatingBar stundent_feedback_rating;

        int check=1;
        public teacherfeedback_viewholder(@NonNull View itemView) {
            super(itemView);


        student_profileimg=itemView.findViewById(R.id.std_profile_img_for_feed);//1-1
        student_name=itemView.findViewById(R.id.std_name_for_feed);//1-2
        student_feedback_content=itemView.findViewById(R.id.std_feedback_txt);//1-3
        stundent_feedback_rating=itemView.findViewById(R.id.std_rating_for_feed);//1-4
        student_feedback_rating_number=itemView.findViewById(R.id.std_feedback_rating_number);//1-5

        final boolean[] isclickable = {false};//클릭 여부 확인 -> 처음엔 false

        student_feedback_content.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                       if(!isclickable[0]) {//클릭여부가 false일때

                           student_feedback_content.setMaxLines(Integer.MAX_VALUE);
                           isclickable[0] =true;//

                       }else{//클릭여부가 true 일때

                           student_feedback_content.setMaxLines(1);
                           isclickable[0]=false;
                       }

             }
         });



        }
    }//뷰홀더 끝

    //학생이름이랑 프로필 이미지가 필요해서  서버에 요청해서 가지고오는 메소드
    private void get_student_info(String student_uid,ImageView imageView,TextView std_name){

        Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create())//json으로 받고 싶으면 쓰면안됨..
                .build();//리트로핏 뷸딩
        ApiService apiService = retrofit.create(ApiService.class);//api인터페이스 크리에이트

        Call<GetStudentUrlandName> getStudentUrlandNameCall=apiService.get_student_url_and_nmae(student_uid);

        getStudentUrlandNameCall.enqueue(new Callback<GetStudentUrlandName>() {
            @Override
            public void onResponse(Call<GetStudentUrlandName> call, Response<GetStudentUrlandName> response) {
                URL url= null;
                try {
                    url = new URL("http://13.209.249.1/"+response.body().getStd_profile());

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

              //상대방  프로필 사진 넣어줌.
              Glide.with(context).load(url).into(imageView);
              std_name.setText(response.body().getStd_name());


            }

            @Override
            public void onFailure(Call<GetStudentUrlandName> call, Throwable t) {
                Log.v("checkstdinfo", String.valueOf(t));
            }
        });

    }







}//teacherfeedbackAdapter끝
