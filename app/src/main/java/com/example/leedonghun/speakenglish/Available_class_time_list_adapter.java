package com.example.leedonghun.speakenglish;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * speakenglish
 * Class: Available_class_time_list_adapter.
 * Created by leedonghun.
 * Created On 2020-03-02.
 * Description: 선생님이 지정한  수업 가능 클래스 리스트를 보여주는  어뎁터이다.
 */
public class Available_class_time_list_adapter extends RecyclerView.Adapter {

   private Context context;//context
   private ArrayList<JsonObject> class_list;// 선생님이 설정한 예약 가능한  시간
   private LayoutInflater layoutInflater;//레이아웃 인플레이터
   private String teacher_uid;//선생님 유아이디


    //Available_class_time_list_adapter 생성자
    Available_class_time_list_adapter(Context context,ArrayList<JsonObject> class_list,String teacheruid){
        Log.v("check", "Available_class_time_list_adapter의 생성자실행됨");
        this.context=context;//context 생성자로 받아서 연결
        this.class_list=class_list;//선생님 설정한 예약 가능한 시간 객체에 연결 해줌.
        this.teacher_uid=teacheruid;//선생님 uid 받아옴.


    }//Available_class_time_list_adaptert생성자 끝.


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.v("check", "Available_class_time_list_adapter의 oncreateviewholder실행됨");

        Context context=parent.getContext();//context

        View view;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view=layoutInflater.inflate(R.layout.show_available_class_time_item, parent,false);


        return new available_class_time_list_viewholder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.v("check", "Available_class_time_list_adapter의 onBindViewHolder실행됨");

        String available_time=class_list.get(position).get("availabledate").getAsString();//수업 가능한 시간
        int reserve_status=class_list.get(position).get("reserve_status").getAsInt();//현재 예약 상태

        ((available_class_time_list_viewholder) holder).txt_for_available_time.setText(available_time);//예약 가능하다고 설정한 시간을 넣어준다.

        if(reserve_status==1){//누군가가  예약 해놓은 상태일때.->  해당 예약자의 이름과  프로필 사진이 담기 linear레이아웃을 visible상태로 한다.

            ((available_class_time_list_viewholder) holder).reservation_status.setText("Reserved");//예약해놓은것이므로 reserved라고 멘트 넣음.
            ((available_class_time_list_viewholder) holder).reservation_status.setTextColor(Color.GREEN);//예약 해놓은 것이므로 초록색으로 reserved 지정.

            ((available_class_time_list_viewholder) holder).container_for_student_info.setVisibility(View.VISIBLE);//학생  ,  프로필 보이게

            //해당  학생의  프로필 사진이랑  이름 가져오기
            get_student_info(class_list.get(position).get("studentid").getAsString(),((available_class_time_list_viewholder) holder).img_for_student_profile,((available_class_time_list_viewholder) holder).txt_for_student_name);


        }else if(reserve_status==0){//아무도 예약을 하지 않았을때 -> 예약한 학생의 이름과 프로필이 담긴 linear레이아웃의 visible상태를 gone으로 처리한다.

            ((available_class_time_list_viewholder) holder).container_for_student_info.setVisibility(View.GONE);

            ((available_class_time_list_viewholder) holder).reservation_status.setText("Not reserved  Long Click -> edit or delete");//예약 한 된거이므로  Notreserved+ 빨간색 텍스트
            ((available_class_time_list_viewholder) holder).reservation_status.setTextColor(Color.RED);

        }





    }//onBindViewHolder끝

    //전체 예약 가능한 시간  갯수 리스트
    @Override
    public int getItemCount() {
        return class_list.size();
    }


   //뷰홀더
    class available_class_time_list_viewholder extends RecyclerView.ViewHolder{



        private TextView txt_for_available_time;//1-1 예약 가능한 시간 담길 텍스트뷰

        private TextView reservation_status;//1-2 예약 상태가 담길  텍스트뷰


        private LinearLayout container_for_student_info;//1-3 //수업 예약한 학생 정보가 담길  linea레이아웃(프로필 이름)

        private TextView txt_for_student_name;//1-4 예약한 학생이름 담길  텍스트뷰
        private ImageView img_for_student_profile;//1-5 예약한 학생 프로필 담길  이미지뷰


        public available_class_time_list_viewholder(@NonNull View itemView) {

            super(itemView);

            txt_for_available_time=itemView.findViewById(R.id.show_available_time);//1-1
            reservation_status=itemView.findViewById(R.id.reserve_status);//1-2

            container_for_student_info=itemView.findViewById(R.id.reserved_student_container);//1-3
            txt_for_student_name=itemView.findViewById(R.id.reserved_student_name);//1-4
            img_for_student_profile=itemView.findViewById(R.id.reserved_student_profile);//1-5




                //아이템을  길게 클릭시  편집 또는  삭제 여부를 선택할수 있도록 함
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        if(container_for_student_info.getVisibility()==View.GONE){
                        int clicked_item_position = getAdapterPosition();//클릭된 아이템의 포지션
                        Log.v("check", "pospos" + class_list.get(clicked_item_position).get("availabledate").getAsString());
                        String class_uid = class_list.get(clicked_item_position).get("uid").getAsString();//해당  수업가능 클래스의 디비 uid


                        PopupMenu popupMenu = new PopupMenu(context, v);//팝업 메뉴
                        popupMenu.getMenuInflater().inflate(R.menu.available_class_pop_up, popupMenu.getMenu());//팝업메뉴  menu에 지정해놓은 available_class_pop_up으로 inflate

                        //팝업 메뉴 클릭이벤트
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {

                                switch (item.getItemId()) {

                                    case R.id.delete://삭제 버튼 클릭

                                        Log.v("check", "Available_class_time_list_adapter의 delete 키 눌림");

                                        delete_or_edit_available_class_list(class_uid, teacher_uid, 1, -1);//해당 데이터 uid를 가져가  삭제 시켜주고,  선생님 uid로 다시  새로고침 해준다.

                                        break;


                                    case R.id.edit://편집 버튼 클릭
                                        Log.v("check", "Available_class_time_list_adapter의 edit 키 눌림");


                                        String selected_time = class_list.get(clicked_item_position).get("availabledate").getAsString();

                                        //date 포맷  HH로 시간 가지고옴.
                                        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//전체 날짜
                                        SimpleDateFormat hour = new SimpleDateFormat("HH");//시간
                                        SimpleDateFormat minute = new SimpleDateFormat("mm");//분
                                        SimpleDateFormat year = new SimpleDateFormat("yyyy");//연도
                                        SimpleDateFormat month = new SimpleDateFormat("MM");//월
                                        SimpleDateFormat day = new SimpleDateFormat("dd");//일


                                        try {

                                            //해당 날짜 ->  date 형식으로 파싱
                                            Date datef = date.parse(selected_time);

                                            int datehour = Integer.parseInt(hour.format(datef));//시간
                                            int dateminue = Integer.parseInt(minute.format(datef));//분
                                            int dateyear = Integer.parseInt(year.format(datef));//연도
                                            int datemonth = Integer.parseInt(month.format(datef));//달
                                            int dateday = Integer.parseInt(day.format(datef));//일


                                            show_for_edit_reservation_time_dialog(datehour, dateminue, dateyear, datemonth, dateday, class_uid);//다시  다이얼로그 보여줘서  수정가능하게 한다.

                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                        break;//편집 버튼 클릭  break;

                                }//switch 끝

                                return true;

                            }//onMenuItemClick끝
                        });//popup메뉴 메뉴아이템 클릭 리스너 끝

                        popupMenu.setGravity(Gravity.RIGHT);//맨 오른쪽에 나오게 gravity설정
                        popupMenu.show();//팝업 메뉴 보이기


                      }else{//예약 되어있는 거  롱클릭할때 토스트 로 알림

                            new Toastcustomer(context).showcustomtaost(null, "Cannot Modify Reserved Class!");

                      }
                        return false;
                    }
                });//아이템 롱 클릭 끝.

       }//available_class_time_list_viewholder

    }//available_class_time_list_viewholder 끝


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

    }//get_student_info() 끝



    //해당 사용 가능한 클래스 타임  아이템의 값을  수정해준다.
    //다시 다이얼로그 를  띄어준다.
    private void show_for_edit_reservation_time_dialog(int get_hour, int get_minute,int get_year, int get_month,int get_day,String class_uid){

        final int[] hour = new int[1];//선택한 예약 시간
        final int[] minute = new int[1];//선택한 예약 분

        //예약한 날짜들
        final int[] available_year = new int[1];//년도
        final int[] available_month = new int[1];//월
        final int[] available_day = new int[1];//날

        //지정했던 시간, 분,  연도, 월 , 일 넣어줌.
        hour[0]=get_hour;
        minute[0]=get_minute;

        available_year[0]=get_year;
        available_month[0]=get_month;
        available_day[0]=get_day;


        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dialog_for_set_reservationtime = new Dialog(context);

        // 액티비티의 타이틀바를 숨긴다.
        dialog_for_set_reservationtime.requestWindowFeature(Window.FEATURE_NO_TITLE);


        dialog_for_set_reservationtime.setCancelable(false);//취소 가능여부 false

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dialog_for_set_reservationtime.setContentView(R.layout.dialog_for_setting_teacher_reservation_time);



        WindowManager.LayoutParams params = dialog_for_set_reservationtime.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;//dialog width 늘려줌.
        dialog_for_set_reservationtime.getWindow().setAttributes((WindowManager.LayoutParams) params);

        //ok버튼 3-1
        Button ok_button=dialog_for_set_reservationtime.findViewById(R.id.okbtn_for_available_class);

        //취소버튼 3-2
        Button cancel_buttn=dialog_for_set_reservationtime.findViewById(R.id.cancelbtn_for_available_class);

        //타임 피커 3-3
        TimePicker timePicker=dialog_for_set_reservationtime.findViewById(R.id.time_picker_for_availableclass);
        TextView timepicker_ok=dialog_for_set_reservationtime.findViewById(R.id.timepickerok);
        TextView timepicker_cancel=dialog_for_set_reservationtime.findViewById(R.id.timepickercancel);

        //타임피커랑  멘트 담긴  컨테이너 ->  타임 선택시  이 container를  gone처리 해줘야됨.
        LinearLayout timepicker_container=dialog_for_set_reservationtime.findViewById(R.id.container_for_timepicker);
        timepicker_container.setVisibility(View.GONE);

        //타임선택 완료  CONTAINER보여줌.
        LinearLayout time_picker_done=dialog_for_set_reservationtime.findViewById(R.id.time_pick_done);
        time_picker_done.setVisibility(View.VISIBLE);

        //타임 피커로 고른  시간  들어가는  텍스트뷰
        TextView textView_for_selected_time=dialog_for_set_reservationtime.findViewById(R.id.available_time_txt);

        //데이트 피커
        DatePicker datePicker=dialog_for_set_reservationtime.findViewById(R.id.datepicker_for_availableclass);
        TextView datepicker_ok=dialog_for_set_reservationtime.findViewById(R.id.datepickerok);
        TextView datepicker_cancel=dialog_for_set_reservationtime.findViewById(R.id.datepickercancel);


        //데이트 피커 담긴 컨테이너
        LinearLayout date_picker_container=dialog_for_set_reservationtime.findViewById(R.id.container_for_datepicker);
        date_picker_container.setVisibility(View.GONE);

        //데이트 피커  골랐을떄  컨테이너
        LinearLayout date_picker_done=dialog_for_set_reservationtime.findViewById(R.id.date_pick_done);
        date_picker_done.setVisibility(View.VISIBLE);

        //데이트 피커로 고른 시간 들어가는 텍스트뷰
        TextView txt_view_for_selected_date=dialog_for_set_reservationtime.findViewById(R.id.available_date_txt);

        //date 다시 고르는 버튼
        Button btn_for_date_reset=dialog_for_set_reservationtime.findViewById(R.id.reset_date_btn);

        //time 다시 고르는 버튼
        Button btn_for_time_reset=dialog_for_set_reservationtime.findViewById(R.id.reset_time_btn);

        //다이얼로그 보여주기
        dialog_for_set_reservationtime.show();


        //맨처음 시작할때  지정해놨던 시간을 보여줘야 하므로...
        textView_for_selected_time.setText(hour[0] + "h " + minute[0] + "m");
        txt_view_for_selected_date.setText(available_year[0] + " / " + available_month[0] + " /  " + available_day[0]);

        //타임피커와 데이트 피커 실행시에도 저장된 날짜부터 보여준다.
        timePicker.setHour(hour[0]);
        timePicker.setMinute(minute[0]);
        datePicker.updateDate(available_year[0],available_month[0],available_day[0]);

        //현재 시간 기준으로 데이트 피커 정할수 있는 날짜 지정함.
        datePicker.setMinDate(System.currentTimeMillis());


        //3-3 클릭이벤트
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute_picked) {
                Log.v("timepicker", String.valueOf(hourOfDay)+"시"+ minute_picked+"분");
                if(hourOfDay==0){
                    hourOfDay=24;
                }
                hour[0] =hourOfDay;//timepicker  시간 넣어줌.
                minute[0] =minute_picked;//timpicker 분 넣어줌.

            }
        });//timepicker  change 리스너 끝.


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Log.v("datepicker", String.valueOf(year)+"년"+ monthOfYear+1+"월"+dayOfMonth+"일");
                    available_year[0] =year;//datepicker 년도 넣어줌.
                    available_month[0] =monthOfYear+1;//datepicker 월 넣어줌.
                    available_day[0] =dayOfMonth;//datepicker 날짜 넣어줌.

                }
            });
        }//오레오 버전 이상





        //////////////////////////////////////////////////////////////////////////////////////타임 피커////////////////////////////////////////////////////////////////////////////////

        //timepicker의   ok버튼을 누를때 이벤트
        timepicker_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //시간이 0일때는  맨처음에  시간 셀렉을 하지 않고 눌렀을때인데,
                //이럴경우는  현재 시간이 들어가도록 처리해주기 위해  아래와 같은 조건을 넣음
                //분같은 경우에는 00분 이 있으므로,  빼줌.
                if(hour[0] !=0) {

                    //타임 피커 사라짐.
                    timepicker_container.setVisibility(View.GONE);

                    if(hour[0]==24){//위에서 hour 픽할때 오후12시는 위  조건  넘어가기위해서 24로 바꿔줬었음. 하지만, 해당  문제되는 조건들이 넘어갔으므로, 다시 0으로 바꿔준ㄷ.
                        hour[0]=0;
                    }

                    //타임 픽  던  컨테이너 보여줌.
                    time_picker_done.setVisibility(View.VISIBLE);
                    textView_for_selected_time.setText(hour[0] + "h " + minute[0] + "m");

                    //데이트 피커 보여줌.
                    //데이터피커 PICK된 텍스트뷰가 VISIBLE이라면,  데이트 피커를 따로 VISIBLE처리 하지 않는다.
                    if(date_picker_done.getVisibility() !=View.VISIBLE){
                        //데이트 피커 보여줌.
                        date_picker_container.setVisibility(View.VISIBLE);
                    }
                }//시간이 0이 아닐때
                else{

                    //타임 피커 사라짐.
                    timepicker_container.setVisibility(View.GONE);


                    //타임 픽  끝난  컨테이너 보여줌.
                    time_picker_done.setVisibility(View.VISIBLE);

                    //현재 시간 date로 가져옴
                    Date currentTime = Calendar.getInstance().getTime();

                    //date 포맷  HH로 시간 가지고옴.
                    SimpleDateFormat simple_hour = new SimpleDateFormat("HH", Locale.getDefault());

                    //date 포맷 mm로  분 가지고옴.
                    SimpleDateFormat simple_minute = new SimpleDateFormat("mm", Locale.getDefault());

                    //시간 분  넣어줌.
                    hour[0]= Integer.parseInt(simple_hour.format(currentTime));
                    minute[0]= Integer.parseInt(simple_minute.format(currentTime));

                    textView_for_selected_time.setText(hour[0] + "h " + minute[0] + "m");

                    //데이트 피커 보여줌.
                    //데이터피커 PICK된 텍스트뷰가 VISIBLE이라면,  데이트 피커를 따로 VISIBLE처리 하지 않는다.
                    if(date_picker_done.getVisibility() !=View.VISIBLE){

                        date_picker_container.setVisibility(View.VISIBLE);
                    }


                }


            }
        });


        //타임 피커취소 버튼
        timepicker_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(textView_for_selected_time.getText().toString().length()>0){//time 고른게 있었을때

                    time_picker_done.setVisibility(View.VISIBLE);
                    timepicker_container.setVisibility(View.GONE);

                    //date가 아직  안골라져있을때...이때는  데이트 피커를 다시 보여준다.
                    if(txt_view_for_selected_date.getText().toString().length()<=0){

                        date_picker_container.setVisibility(View.VISIBLE);
                    }

                }else{//time 고른게 없었을때 date를 픽하라고 나옴.

                    new Toastcustomer(context).showcustomtaost(null, "Should  Pick  Time !!");
                }

            }
        });


        btn_for_time_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(txt_view_for_selected_date.getText().toString().length()>0) {//date 고른게 있었을때

                    time_picker_done.setVisibility(View.GONE);
                    timepicker_container.setVisibility(View.VISIBLE);
                    date_picker_container.setVisibility(View.GONE);
                    date_picker_done.setVisibility(View.VISIBLE);

                }else {
                    time_picker_done.setVisibility(View.GONE);
                    timepicker_container.setVisibility(View.VISIBLE);
                    date_picker_container.setVisibility(View.GONE);

                }
            }
        });
        //////////////////////////////////////////////////////////////////////////////////////타임 피커////////////////////////////////////////////////////////////////////////////////





        //////////////////////////////////////////////////////////////////////////////////////데이터 피커////////////////////////////////////////////////////////////////////////////////
        //ok 버튼 눌렀을때
        datepicker_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //해당 날짜가 안 정해졌을때,
                //처음에  ok버튼을 누르면,  0으로  각 년, 월, 일이  처리가 되므로,  아래와 같이  0이 아닐 때와 0일때(맨처음일때)를  나눠서  진행한다.
                if(available_year[0] !=0 && available_month[0]!=0 && available_day[0] !=0) {


                    //date picker 담은 컨테이너  gone
                    date_picker_container.setVisibility(View.GONE);

                    //고른 시간  담길 텍스트뷰 보여줌.
                    date_picker_done.setVisibility(View.VISIBLE);

                    txt_view_for_selected_date.setText(available_year[0] + " / " + available_month[0] + " /  " + available_day[0]);

                }else{//해당 년,월, 일 이  0일때  이때는  맨처음에 ok버튼을 눌렀을때이다.

                    //date picker 담은 컨테이너  gone
                    date_picker_container.setVisibility(View.GONE);

                    ///고른 시간  담길 텍스트뷰 보여줌.
                    date_picker_done.setVisibility(View.VISIBLE);

                    Date currentTime = Calendar.getInstance().getTime();

                    SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
                    SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.getDefault());
                    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());

                    available_year[0]= Integer.parseInt(yearFormat.format(currentTime));
                    available_month[0]= Integer.parseInt(monthFormat.format(currentTime));
                    available_day[0]= Integer.parseInt(dayFormat.format(currentTime));

                    txt_view_for_selected_date.setText(available_year[0] + " / " + available_month[0] + " /  " + available_day[0]);

                }



            }
        });//ok버튼 눌렀을때  클릭이벤트 끝.


        btn_for_date_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(textView_for_selected_time.getText().toString().length()>0) {//date 고른게 있었을때)

                    date_picker_container.setVisibility(View.VISIBLE);
                    timepicker_container.setVisibility(View.GONE);
                    date_picker_done.setVisibility(View.GONE);
                    time_picker_done.setVisibility(View.VISIBLE);



                }else {

                    date_picker_container.setVisibility(View.VISIBLE);
                    timepicker_container.setVisibility(View.GONE);
                    date_picker_done.setVisibility(View.GONE);

                }
            }
        });

        //////////////////////////////////////////////////////////////////////////////////////데이터 피커////////////////////////////////////////////////////////////////////////////////



        //데이트 피커 취소 버튼
        datepicker_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(txt_view_for_selected_date.getText().toString().length()>0){//date 고른게 있었을때

                    date_picker_done.setVisibility(View.VISIBLE);
                    date_picker_container.setVisibility(View.GONE);

                }else{//date 고른게 없었을때 date를 픽하라고 나옴.

                    new Toastcustomer(context).showcustomtaost(null, "Should  Pick  date !!");
                }

            }
        });


        //3-1클릭 이벤트
        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(date_picker_container.getVisibility()==View.VISIBLE){//데이트 피커가  보일때는 -> 아직  선택 완료가 아님.


                    new Toastcustomer(context).showcustomtaost(null, "you should pick the date!");

                }else if(timepicker_container.getVisibility()==View.VISIBLE){//타임 피커가 보일때는 -> 아직 선택 완료가 아님.

                    new Toastcustomer(context).showcustomtaost(null, "you should pick the time!");

                }else {//그외 상황에서는  모든게  선택이 된것이므로,  디비  저장 및  알람 설정  진행.

                    String myDate = available_year[0] + "/" + available_month[0] + "/" + available_day[0] + " " + hour[0] + ":" + minute[0] + ":00";//yyyy/MM/dd HH:mm:ss이 형식으로 선택한  시간 들  모아줌.
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//yyyy/MM/dd HH:mm:ss해당 date 포맷
                    Date date = null;
                    try {
                        date = sdf.parse(myDate);//위 sdg포맷으로 mydate 파싱해서  date에 넣어줌.
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    //선택한  시간  밀리세컨드
                    long selected_time_mills = date.getTime();//date millseconds로 받음 -> 디비에  이 값을 넣을 거임.


                    delete_or_edit_available_class_list(class_uid,teacher_uid,0,selected_time_mills);//해당 데이터 uid를 가져가  삭제 시켜주고,  선생님 uid로 다시  새로고침 해준다.

                    //다이얼로그 닫아줌.
                    dialog_for_set_reservationtime.dismiss();
                }

            }
        });

        //3-2 클릭이벤트
        cancel_buttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //취소 버튼이므로 현재 다이얼로그 취소함.
                dialog_for_set_reservationtime.dismiss();

            }
        });

    }//show_set_reservation_time_dialog끝





    //해당  사용가능한  클래스타임  리스트에서 삭제 시키는  메소드
    public void delete_or_edit_available_class_list(String class_uid,String teacher_uid,int delete_or_edit,long updatedtimemills) {


      if(delete_or_edit==1){//삭제 일때
            Log.v("check", "Available_class_time_list_adapter의 delete__available_class_list() 삭제일떄 경우 실행됨");

        Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiService.baseurl).build();//리트로핏 뷸딩
        ApiService apiService = retrofit.create(ApiService.class);//api인터페이스 크리에이트
        Call<ResponseBody> delete_available_class_time = apiService.delete_or_edit_available_class_time(class_uid, delete_or_edit,0,"",null);//edit_or_delete 가  1이므로  삭제를 뜻함.

        delete_available_class_time.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    String result = response.body().string();//결과 값.
                    Log.v("check", "Available_class_time_list_adapter의 delete__available_class_list()끝  서버 응답 delete->" + result);


                    if (result.equals("1")) {//삭제  성공시

                        //새롭게  값을 받기위해  서버에서  리스트를  다시 받아옴.
                        show_uploaded_available_class_time(teacher_uid);

                    } else {//삭제 실패시->  에러를  토스트로 알림

                        new Toastcustomer(context).showcustomtaost(null, "something error  shown  while deleting");

                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }//onResponse() 끝


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.v("check", "Available_class_time_list_adapter의 delete__available_class_list()끝  서버 응답 실패 내용 delete->" + t.getMessage());

            }//onFailure()끝
        });//enqueue() 끝

      }//삭제 할 경우 끝
      else if(delete_or_edit==0){//편집일때이다.

            Log.v("check", "Available_class_time_list_adapter의 delete__available_class_list() 수정일떄 경우 실행됨");


          Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiService.baseurl).build();//리트로핏 뷸딩
          ApiService apiService = retrofit.create(ApiService.class);//api인터페이스 크리에이트
          Call<ResponseBody> delete_available_class_time = apiService.delete_or_edit_available_class_time(class_uid, delete_or_edit,updatedtimemills,"",null);//edit_or_delete 가  0이므로  수정를 뜻함.

          delete_available_class_time.enqueue(new Callback<ResponseBody>() {
              @Override
              public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                  try {
                      String result = response.body().string();//결과 값.
                      Log.v("check", "Available_class_time_list_adapter의 delete__available_class_list()끝  서버 응답 edit->" + result);


                      if (result.equals("1")) {//수정  성공시

                          //새롭게  값을 받기위해  서버에서  리스트를  다시 받아옴.
                          show_uploaded_available_class_time(teacher_uid);

                      }else{//수정 실패시->  에러를  토스트로 알림

                          new Toastcustomer(context).showcustomtaost(null, "something error  shown  while editing");

                      }


                  }catch(IOException e){

                      e.printStackTrace();
                  }

              }//onResponse() 끝


              @Override
              public void onFailure(Call<ResponseBody> call, Throwable t) {
                  Log.v("check", "Available_class_time_list_adapter의 delete__available_class_list()끝  서버 응답 실패 내용 edit->" + t.getMessage());

              }//onFailure()끝
          });//enqueue() 끝


        }//편집일때 경우 끝.

    }//delete__available_class_list()끝


    //해당 선생님이 지정한  예약 가능  수업 시간  리스트  가져오는  메소드
    public void show_uploaded_available_class_time(String teacheruid){
        Log.v("check","Available_class_time_list_adapter의 show_uploaded_available_class_time()실행됨");

        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit=new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create(gson))//json으로 받고 싶으면 쓰면안됨..
                .build();//리트로핏 뷸딩
        ApiService apiService=retrofit.create(ApiService.class);//api인터페이스 크리에이트
        Call<GetTeacherAvailableClassTime> get_available_class_time=apiService.get_teacher_available_class_time(teacheruid);

        get_available_class_time.enqueue(new Callback<GetTeacherAvailableClassTime>() {
            @Override
            public void onResponse(Call<GetTeacherAvailableClassTime> call, Response<GetTeacherAvailableClassTime> response) {
                ArrayList<JsonObject> available_class_list=response.body().getGet_available_class_time();
                Log.v("check","Available_class_time_list_adapter의 선생님이 준비한 리스트 가져오기 성공  가져온 리스트->"+available_class_list);

                //adapter에서  notifidatasetchanged를 호출하면, 해당 리스트 내용을  다시  서버에서 받아 와야됨.
                //왜냐면, 맨처음  생성자를 통해 받은 값은  처음 호출시 끝이고,  그뒤로는 서버에서  변경된 값을  받는지 모르기 때문이다.
                //그래서 새로 넣어줌.
                class_list=available_class_list;

                //위에서 리스트를  새로  진행했으니까  다시 리사이클러뷰 bind실행.->서버에서 변경된값 그대로 적용됨.
                notifyDataSetChanged();

            }//onResponse() 끝

            @Override
            public void onFailure(Call<GetTeacherAvailableClassTime> call, Throwable t) {
                Log.v("check","Available_class_time_list_adapter의 선생님이 준비한 리스트 가져오기 실패  실패내용->"+t.getMessage());

            }//onFailure() 끝
        });

    }//show_uploaded_available_class_time 끝



}//adapter끝
