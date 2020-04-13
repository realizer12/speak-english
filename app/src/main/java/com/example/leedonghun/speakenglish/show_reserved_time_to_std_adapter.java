package com.example.leedonghun.speakenglish;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * speakenglish
 * Class: show_reserved_time_to_std_adapter.
 * Created by leedonghun.
 * Created On 2020-03-13.
 * Description: 해당 학생이 예약한   수업 날짜  리스트를  teacherporfile reserve 섹션에  보여지게  만들어주는
 * adpater이다.
 */
public class show_reserved_time_to_std_adapter extends RecyclerView.Adapter {


    private Context context;
    private LayoutInflater layoutInflater;//레이아웃 인플레이터
    private ArrayList<JsonObject> reserved_list;//예약한  수업 리스트
    private TextView textView_for_ment;//리스트 0 초과일때 나오는 텍스트뷰
    private String teacher_uid;//선생님 uid
    private RecyclerView recyclerView;//리사이클러뷰

    //어뎁터  생성자
    show_reserved_time_to_std_adapter(Context context, ArrayList<JsonObject> reserved_list, TextView txt_for_ment,String teacher_uid,RecyclerView thisrecyclerview){

      this.context=context;
      this.reserved_list=reserved_list;
      this.textView_for_ment=txt_for_ment;
      this.recyclerView=thisrecyclerview;
      this.teacher_uid=teacher_uid;

    }//생성자 끝

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        layoutInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view=layoutInflater.inflate(R.layout.show_reserved_class_time_item, parent,false);

        return new viewholder_for_show_reserved_time_to_std(view);

    }//onCreateViewholder 끝

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        String  reservetime=reserved_list.get(position).get("availabledate").getAsString();

        //해당 포지션에  예약한   시간 데이터 연결해줌.
        ((viewholder_for_show_reserved_time_to_std)holder).textView_for_show_reserved_time.setText(reservetime+"  예약됨");


    }//onBindViewHolder 끝

    @Override
    public int getItemCount() {

        return reserved_list.size();

    }//getitemcount 끝


    class viewholder_for_show_reserved_time_to_std extends RecyclerView.ViewHolder{

         LinearLayout show_reserved_time_container;//전체 레이아웃 1-3
         TextView textView_for_show_reserved_time;//예약된 시간  보여주는 텍스트뷰 1-1
         Button btn_for_cancel_reserved_class;//예약된 시간  다시 취소하는  버튼 1-2

        public viewholder_for_show_reserved_time_to_std(@NonNull View itemView) {
            super(itemView);

            textView_for_show_reserved_time=itemView.findViewById(R.id.txt_for_show_reserved_time);//1-1
            btn_for_cancel_reserved_class=itemView.findViewById(R.id.btn_for_cancel_reserved_time);//1-2
            show_reserved_time_container=itemView.findViewById(R.id.show_reserved_time_container);//1-3

            btn_for_cancel_reserved_class.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder alertDialog_builder=new AlertDialog.Builder(context);

                    //제목 설정
                    alertDialog_builder.setTitle("예약 취소");

                    //취소 의사 물어봄.
                    alertDialog_builder.setMessage("정말"+reserved_list.get(getAdapterPosition()).get("availabledate").getAsString()+"에 예약됨\n"+"수업을 취소하시겠습니까??");

                    //취소 확인 버튼
                    alertDialog_builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //해당  포지션 uid 클래스  학생 등록 취소 진행.
                            cancel_std_reserved_class(reserved_list.get(getAdapterPosition()).get("uid").getAsString(),show_reserved_time_container);
                        }
                    });

                    alertDialog_builder.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();//다이얼로그 취소
                        }
                    });

                    AlertDialog alertDialog=alertDialog_builder.create();
                    alertDialog.show();
                }
            });

        }//viewholder_for_show_reserved_time_to_std()끝

    }//뷰홀더 끝끝


    //해당  학생이 등록한  예약 날짜  취소 시켜주는 메소드
    private void cancel_std_reserved_class(String reserved_class_uid, LinearLayout reserved_class_conatiner){

        Log.v("checkddddddddddddddddddddd", reserved_class_uid);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiService.baseurl).build();//리트로핏 뷸딩
        ApiService apiService = retrofit.create(ApiService.class);//api인터페이스 크리에이트
        Call<ResponseBody> cancel_reserve_classtime = apiService.delete_or_edit_available_class_time(reserved_class_uid, 4, 0,null,null);//edit_or_delete 가  0이므로  수정를 뜻함.

        cancel_reserve_classtime.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String cancel_result=response.body().string();
                    Log.v("check", "학생이  예약한  수업  취소 서버 처리 결과->"+cancel_result);

                    if(cancel_result.equals("1")){//해당  취소 처리 성공시

                        //취소 했으니 새롭게  리스트 가져옴.
                        get_student_reserved_time_list(recyclerView,teacher_uid);

                        //학생 정보를 가지고 오기위한  applicaiton 객체
                        GlobalApplication globalApplication=(GlobalApplication)context.getApplicationContext();

                        //취소 했으니 선생님께  취소했다는 fcm보냄
                        send_fcm_to_alert_std_cancel_reservation(globalApplication.getStudent_name(),globalApplication.getStudnet_profile_url(),teacher_uid,reserved_class_uid);

                        cancelAlarmManger(Integer.parseInt(reserved_class_uid));



                    }else{

                        new Toastcustomer(context).showcustomtaost(null, "취소 과정에서 문제생김");
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }


            }//onResponse()끝

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Log.v("check", "학생이  예약한  수업  취소 할때 에러남->"+t.getMessage());

            }//onFailure()끝
        });

    }//cancel_std_reserved_class() 끝

    //예약 수업 취소시 등록되었던  알람도 취소 시켜준다.
    private void cancelAlarmManger(int canceled_reserved_class_uid){


             AlarmManager  mAlarmMgr = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context.getApplicationContext(), Broadcast_for_alarm_reservation_class.class);
            PendingIntent mAlarmIntent = PendingIntent.getBroadcast(context.getApplicationContext(), canceled_reserved_class_uid, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mAlarmMgr.cancel(mAlarmIntent);
            mAlarmIntent.cancel();


    }//cancelAlarmManger()끝


    //학생이  해당 수업을  취소 했다는 것을  선생님에게 알리기 위한  fcm
    private void send_fcm_to_alert_std_cancel_reservation(String student_name,String student_profile,String teacher_uid,String canceled_class_uid){

        //rerofit 준비
        Retrofit retrofit=new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create()).build();
        ApiService apiServie=retrofit.create(ApiService.class);

        //fcm서버로  현재 로그인  선생님  이메일  보내기
        Call<ResponseBody> send_fcm_to_teacher_for_alert_cancel_reservation=apiServie.send_cancel_reservation_info_to_teacher_fcm(student_name,student_profile,teacher_uid,canceled_class_uid);

        send_fcm_to_teacher_for_alert_cancel_reservation.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    String result=response.body().string();
                    Log.v("check", "show_reserved_time_to_std_adapter의  학생  예약 시간 취소시 fcm 보내기 결과->"+result);

                    if(result.equals("-1")){
                        Log.v("check", "show_reserved_time_to_std_adapter의  학생  예약 시간 취소시 fcm 보내기 결과 선생님 토큰 가져오는데서  에러남"+result);

                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }//onResponse() 끝

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Log.v("check", "show_reserved_time_to_std_adapter의  학생  예약 시간 취소시 fcm관련 서버 에러->"+t.getMessage());

            }//onFailure() 끝
        });


    }//send_fcm_to_alert_std_cancel_reservation() 끝



    //학생이  예약한  수업 리스트 가져오기  -> 이때는 해당 학생의 uid까지 같이 가져가서  가져온다.
    private  void get_student_reserved_time_list(RecyclerView recyclerView_for_show_reservedtime_list,String teacheruid){

        GlobalApplication globalApplication=(GlobalApplication)context.getApplicationContext();

        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit=new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create(gson))//json으로 받고 싶으면 쓰면안됨..
                .build();//리트로핏 뷸딩
        ApiService apiService=retrofit.create(ApiService.class);//api인터페이스 크리에이트
        Call<GetStudentReservedClassTime>get_reserved_class_time=apiService.get_student_available_class_time(globalApplication.getStudnet_uid(),teacheruid);


        get_reserved_class_time.enqueue(new Callback<GetStudentReservedClassTime>() {
            @Override
            public void onResponse(Call<GetStudentReservedClassTime> call, Response<GetStudentReservedClassTime> response) {

                ArrayList<JsonObject> reserved_class_list=response.body().getGet_reserved_class_time();


                if(reserved_class_list.size()>0){//예약한 수업 리스트가 있을때  예약한 수업 리스트 들어가는 리사이클러뷰 visible 처리

                    Log.v("check", "show_reserved_time_to_std_adapter 의 예약한  수업 리스트가 1이상일때 해당 리사이클러뷰-> visible");

                    reserved_list=reserved_class_list;//새롭게 리스트 값 받아와서 notifydatasetchanged() 실행해줌.
                    notifyDataSetChanged();

                    //리사이클러뷰 보여주고,  밑에  취소 멘트 날림.
                    recyclerView_for_show_reservedtime_list.setVisibility(View.VISIBLE);
                    textView_for_ment.setVisibility(View.VISIBLE);

                }else{//예약한 수업리스트가 없을때 -> 예약한 수업 리스트 들어가는 리사이클러뷰 GONE처리

                    Log.v("check", "show_reserved_time_to_std_adapter 의  예약한  수업리스트가  0이하일때 해당 리사이클러뷰 ->Gone");

                    recyclerView_for_show_reservedtime_list.setVisibility(View.GONE);
                    textView_for_ment.setVisibility(View.GONE);
                }


            }

            @Override
            public void onFailure(Call<GetStudentReservedClassTime> call, Throwable t) {
                Log.v("check", t.getMessage());
            }
        });

    }//get_student_reserved_time_list() 끝



}//show_reserved_time_to_st_adapter  어뎁터 끝
