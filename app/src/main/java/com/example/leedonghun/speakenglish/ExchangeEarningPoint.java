package com.example.leedonghun.speakenglish;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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
 * Class: ExchangeEarningPoint.
 * Created by leedonghun.
 * Created On 2020-03-26.
 * Description:  선생님이 번  point를  관리자에게  환전 신청 해주는  엑티비티이다.
 * 이곳에서  선생님의   은행과  계좌 번호를  쓰고  신청하기 버튼을 누르면,
 * 관리자에게  신청이 가지며,
 * 그동한  환전 신청한  리스트를 볼수 있다  그리고 각 환전 경우 옆에는 해당  환전의 현재상태를 보여주낟.  (진행중, 환전 거절, 환전 완료)
 */
public class ExchangeEarningPoint extends AppCompatActivity {


    private Toolbar toolbar;//툴바 1-1

    private Button btn_for_show_exchange_point_dialog;//포인트 환전  신청 다이얼로그 실행하는 버튼 1-2


    private RecyclerView recyclerView_for_show_past_exchange_point_info;//이전에 신청한  포인트환전 기록 보여주는 리사이클러뷰  1-4
    private GettingExchangePointRecyclerviewAdapter gettingExchangePointRecyclerviewAdapter;//리사이클러뷰 어뎁터
    private LinearLayoutManager layoutManager;//리사이클러뷰 레이아웃 매니저

    private ImageView img_for_refresh_all_data;//엑티비티 리프레쉬 버튼 1-5

    private TextView show_teacher_present_point;//현재  선생님이 가진  포인트 보여주는 텍스트뷰; 1-6


    //exchange alert  다이얼로그   뷰

    Button btn_for_cancel_exchange_alert;//alert 취소  2-1
    Button btn_for_exchange_point_in_alert;//alert exchange 진행  2-2

    Spinner spinner_for_show_bank_list;//뱅크 리스트 보여주는 스피너

    TextView txt_for_show_teacher_present_point_in_alert;//현재 선생님 포인트 량  보여주는  텍스트 2-3
    TextView txt_for_show_teacher_request_exchange_point; //선생님이  exchange  신청하는  푀인트 량 보여주는 텍스트뷰 2-4
    TextView txt_for_show_amount_of_money_for_exchange;//환전 하면 받게 될  금액

    Button btn_for_minus_point;// 환전 포인트  마이너스 하는  버튼
    Button btn_for_plus_point;//환전 포인트  플러스 하는 버튼

    EditText editText_for_point_exchange_amount;//환전할  포인트량 적는 에딧텍스트
    EditText editText_for_write_back_acoount;//환전할  은행  계좌 적는 에딧텍스트

    //exchange alert  다이얼로그 뷰  끝

    //이미지뷰 rotation 효과
    private Animation rotae_imageview;//1-8


    GlobalApplication globalApplication;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_earning_point);

        globalApplication=(GlobalApplication)getApplicationContext();
        toolbar=findViewById(R.id.toolbar_for_exchange_point);//1-1
        btn_for_show_exchange_point_dialog=findViewById(R.id.btn_for_exchange_point);//1-2
        recyclerView_for_show_past_exchange_point_info=findViewById(R.id.show_past_exchange_info);//1-4
        img_for_refresh_all_data=findViewById(R.id.btn_for_refresh_exchange_activity);//-15

        show_teacher_present_point=findViewById(R.id.show_teacher_point_txt);//1-6

        //1-8
        rotae_imageview = AnimationUtils.loadAnimation(ExchangeEarningPoint.this, R.anim.rotate_image_view);





        //엑티비티  새로고침  이미지 뷰를  클릭시  새롭게   전체 포인트량및  포인트  환전신청 리스트를 보여준다.  1-5
        img_for_refresh_all_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.v("check", getLocalClassName()+"의  엑티비티 새로고침 버튼 클릭됨");

                //이미지뷰 로테이션 효과줌. - 새로고침느낌으로
                img_for_refresh_all_data.startAnimation(rotae_imageview);

                //선생님  전체 포인트 값 가지고옴
                get_teacher_entire_point(globalApplication.getTeacheruid(),show_teacher_present_point);

                //선생님 환전 신청 리스트 가지고온다
                get_teacher_request_exchange_point_info(globalApplication.getTeacheruid());
            }
        });//1-5 클릭 이벤트 끝







        //환전 신청  버튼 클릭 이벤트  ->  알럴트가 실행되어서  원하는  보유 포인트 만큼  환전 신청이 가능하다. 1-2
        btn_for_show_exchange_point_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("check", getLocalClassName()+"의  환전 신청 버튼 클릭됨");

                // 환전 신청하는  다이얼로그를  실행하는 메소드이다.
                show_diaog_for_request_exchange_point();


            }
        });//1-2 클릭 이벤트 끝



        //toolbar 관련 코드
        setSupportActionBar(toolbar);//툴바에  액션바  서포트 연결
        getSupportActionBar().setDisplayShowTitleEnabled(false);//엑션바에서 타이틀 안보이게함
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//액션바에서 Homeashup 버튼 가능하게함
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.whiteback);//해당 버튼  뒤로가기 버튼모양 으로  이미지 지정

    }//onCreate()끝


    @Override
    protected void onResume() {
        super.onResume();

        make_teacher_point_refresh(globalApplication.getTeacheruid());

        //선생님  전체 포인트 값 가지고옴
        get_teacher_entire_point(globalApplication.getTeacheruid(),show_teacher_present_point);

        //선생님 환전 신청 리스트 가지고온다
        get_teacher_request_exchange_point_info(globalApplication.getTeacheruid());

    }//onResume() 끝

    int exchange_point_amount;//환전 하려는  포인트  수량
    //은행 선택 상태
    int bank_selection;
    //환전 신청하는 다이얼로그
    private  void show_diaog_for_request_exchange_point(){

        exchange_point_amount=0;//맨 처음에 시작할때  해당  amount를 0으로  설정해준다.

        //은행 선택 상태
        bank_selection=0;

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dialog_for_request_exchange = new Dialog(ExchangeEarningPoint.this);

        // 액티비티의 타이틀바를 숨긴다.
        dialog_for_request_exchange.requestWindowFeature(Window.FEATURE_NO_TITLE);


        dialog_for_request_exchange.setCancelable(false);//취소 가능여부 false

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dialog_for_request_exchange.setContentView(R.layout.alert_activity_for_exchange_earning_point);



        //환전 신청하는 버튼
        btn_for_exchange_point_in_alert=dialog_for_request_exchange.findViewById(R.id.btn_for_exchange);

        //환전 신청  다이얼로그 취소하는 버튼
        btn_for_cancel_exchange_alert=dialog_for_request_exchange.findViewById(R.id.btn_for_cancel_exchange);

        //뱅크 리스트 보여주는  스피너
        spinner_for_show_bank_list=dialog_for_request_exchange.findViewById(R.id.spinner2);

        ArrayAdapter yearAdapter = ArrayAdapter.createFromResource(this, R.array.bank_array, android.R.layout.simple_spinner_item);

        // Spinner 클릭시 DropDown 모양을 설정
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // 스피너에 어답터를 연결
        spinner_for_show_bank_list.setAdapter(yearAdapter);




        //스피너  선택 리스너
        spinner_for_show_bank_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position==0){
                    Log.v("check", getLocalClassName()+"의  스피너 선택안됨");


                }else if(position>0){
                    Log.v("check", getLocalClassName()+"의  스피너 값 선택됨 =>"+yearAdapter.getItem(position).toString());

                }

                //포지션 넣어줌.
                bank_selection=position;


            }//onItemSelected() 끝

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                bank_selection=0;
                Log.v("check", getLocalClassName()+"의  스피너 선택안됨");
            }//onNothingSelected() 끝끝
        });



        //선생님 현재 포인트 량 여주는  텍스트뷰
        txt_for_show_teacher_present_point_in_alert=dialog_for_request_exchange.findViewById(R.id.txt_for_teacher_present_point);

        //선생님이  환전할  포인트를  금액으로 환산해서 보여주는 텍스트뷰
        txt_for_show_amount_of_money_for_exchange=dialog_for_request_exchange.findViewById(R.id.txt_for_show_cash_amount_for_exchange_point);

        //선생님  환전 요청할 포인트가  보여지는  텍스트뷰이다.
        txt_for_show_teacher_request_exchange_point=dialog_for_request_exchange.findViewById(R.id.txt_for_exchange_point);

        //환전 포인트 마이너스 버튼
        btn_for_minus_point=dialog_for_request_exchange.findViewById(R.id.btn_for_minus_exchange);

        //환전 포인트 플러스 버튼
        btn_for_plus_point=dialog_for_request_exchange.findViewById(R.id.btn_for_plus_exchange);

        //은행 계좌 번호 넣는  에딧 텍스트
        editText_for_write_back_acoount=dialog_for_request_exchange.findViewById(R.id.edittext_for_write_account_number);

        // 환전할  포인트 량  적어주는 에딧텍스트
        editText_for_point_exchange_amount=dialog_for_request_exchange.findViewById(R.id.edittxt_for_point_exchange_amount);


        //선생님 현재 보유 포인트  가져와서 보여주기
        get_teacher_entire_point(globalApplication.getTeacheruid(),txt_for_show_teacher_present_point_in_alert);

        dialog_for_request_exchange.show();//다이얼로스 실행



        //환전 포인트  플러스 버튼 클릭이벤트
        btn_for_plus_point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("check", getLocalClassName()+"의  플러스 버튼 눌림");

                //플러스 버튼 눌리면  1씩 올라감
                exchange_point_amount=exchange_point_amount+1;

                //바꾸려는 point량이 0보다  작을때  수량이 마이너스로 가면 안되서  warning을 날린다.
                if(exchange_point_amount<0){

                    new Toastcustomer(ExchangeEarningPoint.this).showcustomtaost(editText_for_point_exchange_amount, "Should be  more than 0");
                    exchange_point_amount=0;//buy_count는  0으로 다시 맞춰줌.
                }

                editText_for_point_exchange_amount.setText(exchange_point_amount+"");
                editText_for_point_exchange_amount.setSelection(editText_for_point_exchange_amount.length());

                change_amount_eache_view_for_point(exchange_point_amount);
            }
        });//플러스 버튼 클릭 이벤트 끝

        //환전 포인트  마이너스 버튼 클릭이벤트
        btn_for_minus_point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("check", getLocalClassName()+"의 마이너스 버튼 눌림");
                exchange_point_amount=exchange_point_amount-1;


                //바꾸려는 point량이 0보다  작을때  수량이 마이너스로 가면 안되서  warning을 날린다.
                if(exchange_point_amount<0){

                    new Toastcustomer(ExchangeEarningPoint.this).showcustomtaost(editText_for_point_exchange_amount, "Should be  more than 0");
                    exchange_point_amount=0;//buy_count는  0으로 다시 맞춰줌.
                }

                editText_for_point_exchange_amount.setText(exchange_point_amount+"");
                editText_for_point_exchange_amount.setSelection(editText_for_point_exchange_amount.length());

                change_amount_eache_view_for_point(exchange_point_amount);
            }
        });//마이너스 버튼 클릭이벤트




        //최 종적으로  환전 신청할때 버튼 눌림
        btn_for_exchange_point_in_alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("check", getLocalClassName()+"의  다이얼로그최종 환전 신청 버튼 눌림");

                if(exchange_point_amount==0){//환전 포인트가  0 이하이면,

                    new Toastcustomer(ExchangeEarningPoint.this).showcustomtaost(editText_for_point_exchange_amount, "Should be  more than 0");

                }else if(editText_for_point_exchange_amount.getText().toString().length()==0){//또는 아무것도 안써져있을때

                    new Toastcustomer(ExchangeEarningPoint.this).showcustomtaost(editText_for_point_exchange_amount, "Should be  more than 0");
                    exchange_point_amount=0;//buy_count는  0으로 다시 맞춰줌.
                    editText_for_point_exchange_amount.setText(exchange_point_amount+"");
                    editText_for_point_exchange_amount.setSelection(editText_for_point_exchange_amount.length());

                    change_amount_eache_view_for_point(exchange_point_amount);


                }//아무것도 안써져 있을 경우 끝.
                else if(bank_selection==0){//은행 선택  아무것도 안했을 경우

                    new Toastcustomer(ExchangeEarningPoint.this).showcustomtaost(null, "Select Your  Bank!");

                }else if(editText_for_write_back_acoount.getText().toString().length()==0){//은행  ACCOUNT를  적지 않았을 경우

                    new Toastcustomer(ExchangeEarningPoint.this).showcustomtaost(null, "Plz Write Your Bank Account!");

                }else{//모든  조건이 마즈므로  서버에  업로드 해준다.

                     //서버에  해당 환전 신청 내용 업로드
                    upload_teacher_exchange_reqeuest(globalApplication.getTeacheruid(),editText_for_write_back_acoount.getText().toString(),exchange_point_amount*10,bank_selection,dialog_for_request_exchange);

                }

            }
        });


        //취소 버튼 클릭 이벤트 -> 다이얼로그  dismiss해줌
        btn_for_cancel_exchange_alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Log.v("check", getLocalClassName()+"의  다이얼로그 취소 버튼 눌림");

                dialog_for_request_exchange.dismiss();

            }
        });//취소버튼 클릭 이벤트 끝


        //EDIT텍스트 변화 감지  1-7
        editText_for_point_exchange_amount.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }//beforeTextChanged() 끝

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }//onTextChnaged() 끝

            @Override
            public void afterTextChanged(Editable s) {

                if(s.toString().equals("")){//edittext에  아무것도 없을때

                    //아무것도 없을때는  buy_count를 0으로..
                    exchange_point_amount=0;
                    change_amount_eache_view_for_point(exchange_point_amount);

                }else{//edittext에  숫자가  있을때

                    try{

                        //
                        exchange_point_amount = Integer.parseInt(s.toString());

                        //어차피  intputtype을 number로 해놔서  -를 적용못하지만  혹시 몰라서  넣어둠
                        if(exchange_point_amount<0){
                            new Toastcustomer(ExchangeEarningPoint.this).showcustomtaost(editText_for_point_exchange_amount, "수량은  0이상  이어야 합니다");
                            exchange_point_amount=0;
                        }


                        change_amount_eache_view_for_point(exchange_point_amount);

                    }catch(NumberFormatException E) {

                    }
                }

            }//afterTextChanged() 끝
        });//edit_txt_for_point_buy_count() 끝

    }//show_diaog_for_request_exchange_point() 끝



     //선생님 환전 신청 정보  업로드 해주는 메소드
     private void upload_teacher_exchange_reqeuest(String teacheruid,String bank_account,int exchange_point_amount,int bank_position,Dialog dialog){

         Retrofit retrofit=new Retrofit.Builder().baseUrl(ApiService.baseurl).build();
         ApiService apiService=retrofit.create(ApiService.class);

         long timemills=System.currentTimeMillis();//현재시간간

        Call<ResponseBody> upload_std_feedback = apiService.upload_teacher_exchange_request_info(teacheruid,bank_position,exchange_point_amount,bank_account,timemills);//

         upload_std_feedback.enqueue(new Callback<ResponseBody>() {
             @Override
             public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                 try {
                     String result=response.body().string();
                     Log.v("check", getLocalClassName()+"의 upload_teacher_exchange_reqeuestd의  response 결과 ->"+result);



                     if(result.equals("1")){//insert성공시

                         new Toastcustomer(ExchangeEarningPoint.this).showcustomtaost(null, "Success To Upload \nExchange Request",1500,200);


                         //insert 성공했으니까 새롭게 환전 신청 리스트 가지고온다
                         get_teacher_request_exchange_point_info(globalApplication.getTeacheruid());


                         //선생님  전체 포인트 값 가지고와서 다시  텍스트에 업데이트 해줌
                         get_teacher_entire_point(globalApplication.getTeacheruid(),show_teacher_present_point);


                         dialog.dismiss();//다이얼로그 꺼준다

                     }else if(result.equals("-1") || result.equals("-2")){//실패시

                         new Toastcustomer(ExchangeEarningPoint.this).showcustomtaost(null, "Fail to apply Excahnge!");
                     }


                 } catch (IOException e) {
                     e.printStackTrace();
                 }

             }//onResponse() 끝

             @Override
             public void onFailure(Call<ResponseBody> call, Throwable t) {
                 Log.v("check", getLocalClassName()+"의 upload_teacher_exchange_reqeuestd의  response 에러 ->"+t.getMessage());

             }//onFailure() 끝
         });

     }//upload_teacher_exchange_reqeuest() 끝


    //선생님이  신청한  환전 신청   포인트  정보를 가지고 온다.
    private  void get_teacher_request_exchange_point_info(String teacher_uid){

        Gson gson = new GsonBuilder().setLenient().create();
        //retrofit 통신 ..
        Retrofit retrofit=new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create(gson)).build();
        ApiService apiService=retrofit.create(ApiService.class);

        //서버로  fcm토큰 보냄
        Call<GetExchangePointInfo> get_teacher_exchange_point_info=apiService.get_teacher_exchange_request_info(teacher_uid);


        get_teacher_exchange_point_info.enqueue(new Callback<GetExchangePointInfo>() {
            @Override
            public void onResponse(Call<GetExchangePointInfo> call, Response<GetExchangePointInfo> response) {

                //선생님 환전 신청 정보 받아서 넣어줌.
                ArrayList<JsonObject>arraylis_for_get_teacher_exchange_point_info =response.body().getGet_teacher_exchange_point_info();
                Log.v("check", getLocalClassName()+"의  arraylis_for_get_teacher_exchange_point_info 임"+arraylis_for_get_teacher_exchange_point_info);

                if(arraylis_for_get_teacher_exchange_point_info.size()>0){


                    //리사이클러뷰 처리
                    gettingExchangePointRecyclerviewAdapter=new GettingExchangePointRecyclerviewAdapter(ExchangeEarningPoint.this,arraylis_for_get_teacher_exchange_point_info);
                    recyclerView_for_show_past_exchange_point_info.setAdapter(gettingExchangePointRecyclerviewAdapter);//리사이클러뷰 어뎁터에 연결시켜줌.
                    ((SimpleItemAnimator) recyclerView_for_show_past_exchange_point_info.getItemAnimator()).setSupportsChangeAnimations(false);//리사이클러뷰에  변경 될때 진행되는 애니메이션  false값 넣어줌.

                    //리사이클러뷰 매니저 로  -> 리니어 형태의  vertical 방향으로  설정해줌.
                    layoutManager=new LinearLayoutManager(ExchangeEarningPoint.this,RecyclerView.VERTICAL,false);
                    ((LinearLayoutManager) layoutManager).setReverseLayout(false);
                    ((LinearLayoutManager) layoutManager).setStackFromEnd(false);

                    //리사이클러뷰 매니저  연결 시켜줌.
                    recyclerView_for_show_past_exchange_point_info.setLayoutManager(layoutManager);
                    recyclerView_for_show_past_exchange_point_info.setNestedScrollingEnabled(false);//부드럽게 -> 스크롤링 하게


                }else{

                    Log.v("check", getLocalClassName()+"의 환전 신청 어레이 내용이 없음");


                }



            }//onResponse() 끝

            @Override
            public void onFailure(Call<GetExchangePointInfo> call, Throwable t) {

                Log.v("check", getLocalClassName()+"의  get_teacher_request_exchange_point_info리스 폰스 에러"+t.getMessage());

            }//onFailure()끝
        });


    }//get_teacher_request_exchange_point_info() 끝





    //구매 수량  변경시마다  뷰들  바꿔주는 메소드
    private void change_amount_eache_view_for_point(int request_count){


        //현재 구매하려는 포인트는  지정한 수량의
        txt_for_show_teacher_request_exchange_point.setText((request_count*10)+" Points");

        txt_for_show_amount_of_money_for_exchange.setText(request_count*100+" won");


    }


    //toolbar 아이템  셀렉트
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//옵션아이템들 클릭시 진행되는 코드
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작



                finish();//현재 엑티비티 끝냄.
                Log.v("check", getLocalClassName() + "의 툴바 뒤로가기 눌림.");
                return true;

            }
        }
        return super.onOptionsItemSelected(item);

    }//옵션 아이템 클릭시 진행 끝



    //서버에서  선생님 현재 선생님  포인트  합  모아서  전체 보유 포인트량 디비에  업데이트 해준다.
    private void get_teacher_entire_point(String teacher_uid,TextView txt_for_show_entire_points){

        Log.v("check", getLocalClassName()+"의 make_teacher_point_refresh() 실행됨" );

        Retrofit retrofit=new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create())//json으로 받고 싶으면 쓰면안됨..
                .build();//리트로핏 뷸딩
        ApiService apiService=retrofit.create(ApiService.class);//api인터페이스 크리에이트
        Call<ResponseBody> make_teacher_entire_point_info=apiService.get_teacher_entrie_point(teacher_uid);

        make_teacher_entire_point_info.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    String result=response.body().string();
                    Log.v("check", getLocalClassName()+"의 make_teacher_point_refresh()  response 값 ->"+result );

                    //선생님 전체 포인트 텍스트뷰에 넣어줌.
                    txt_for_show_entire_points.setText(result+" Points");


                } catch (IOException e) {
                    e.printStackTrace();
                }



            }//onResponse() 끝

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.v("check", getLocalClassName()+"의 make_teacher_point_refresh()  에러 값 ->"+t.getMessage() );




            }//onFailure() 끝
        });



    }//setShow_teacher_present_point() 끝


    //서버에서  선생님 현재 선생님  포인트  합  모아서  전체 보유 포인트량 디비에  업데이트 해준다.
    private void make_teacher_point_refresh(String teacher_uid){

        Log.v("check", getLocalClassName()+"의 make_teacher_point_refresh() 실행됨" );

        Retrofit retrofit=new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create())//json으로 받고 싶으면 쓰면안됨..
                .build();//리트로핏 뷸딩
        ApiService apiService=retrofit.create(ApiService.class);//api인터페이스 크리에이트
        Call<ResponseBody> make_teacher_entire_point_info=apiService.make_teacher_entire_point_info(teacher_uid);

        make_teacher_entire_point_info.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    String result=response.body().string();
                    Log.v("check", getLocalClassName()+"의 make_teacher_point_refresh()  response 값 ->"+result );

                    //resul값  경우
                    switch (result){

                        case "-1":
                            Log.v("check", "전체 포인트 합 쿼리 날리는데 에러남");

                            break;

                        case "-2":
                            Log.v("check", "전체 포인트  총합  값이 없음");

                            break;

                        case "-3":
                            Log.v("check", "선생님 포인트 기록 확인 쿼리중 에러남");

                            break;

                        case "-5":
                            Log.v("check", "teacher_point_origin_업데이트 에러남");

                            break;

                        case "-6":
                            Log.v("check", "teacher_point_origin_insert 에러남");

                            break;
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }



            }//onResponse() 끝

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.v("check", getLocalClassName()+"의 make_teacher_point_refresh()  에러 값 ->"+t.getMessage() );




            }//onFailure() 끝
        });



    }//setShow_teacher_present_point() 끝


}//ExchangeEarningPoint 클래스 끝
