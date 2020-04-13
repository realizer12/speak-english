package com.example.leedonghun.speakenglish;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * speakenglish
 * Class: SelectChargingPountAmountActivity.
 * Created by leedonghun.
 * Created On 2020-03-20.
 * Description: 학생이 충전할 포인트를 골라  결제 페이지로 넘어가기 위한  중간 역할을 하는 엑티비티이다.
 * 이곳에서  고를수 있는  포인트는  총 3가지이며,  10 포인트,   50포인트 100 포인트이다.
 * 고르고  포인트 구매하기 버튼을 누르면  카카오페이 결제가 진행된다.
 */
public class SelectChargingPointAmountActivity extends AppCompatActivity {

    private Context this_activity_context;//편하게 쓰려고  context 객체 하나 만듬  1-0

    private Toolbar toolbar;//현재 엑티비티에 있는 툴바 1-1

    private TextView txt_for_show_std_present_point;//현재 학생이 가지고 있는 포인트 보여주는 텍스트뷰 1-2
    private TextView txt_for_buy_point;//현재  구매하려는 포인트 보여주는 텍스트뷰 1-3
    private TextView txt_for_show_cash_amount_for_buy_point;//포인트 구매에 사용되는 비용  보여주는  텍스트뷰 1-4

    private Button btn_for_plus;//수량 플러스 버튼 1-5
    private Button btn_for_minus;//수량 마이너스 버튼 1-6
    private EditText edit_txt_for_point_buy_count;//구매 10 포인트 수량 쓸거나 보여주는  에딧텍스트 1-7

    private Button btn_for_goto_pay;//결제하기 버튼  1-8

    private AlertDialog alertDialog_for_ask_pay_or_not;//결제하기 버튼 눌렀을때  다시한번  의사를 물어보는  alertdialog 2-1

    private int buy_count=0;//구매하는  10p 수량

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_charging_point_amount);
        Log.v("check", getLocalClassName()+"의 onCreate() 실행됨");


        this_activity_context=SelectChargingPointAmountActivity.this;//1-0

        txt_for_show_std_present_point=findViewById(R.id.txt_for_std_present_point);//1-2
        txt_for_buy_point=findViewById(R.id.txt_for_buy_point);//1-3
        txt_for_show_cash_amount_for_buy_point=findViewById(R.id.txt_for_show_cash_amount_for_buy_point);//1-4

        btn_for_plus=findViewById(R.id.btn_for_plus);//1-5
        btn_for_minus=findViewById(R.id.btn_for_minus);//1-6

        edit_txt_for_point_buy_count=findViewById(R.id.edittxt_for_point_buy_amount);//1-7

        btn_for_goto_pay=findViewById(R.id.btn_for_buy_point);//1-8


        //1-5 구매수량  플러스 버튼 이벤트
        btn_for_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("check", getLocalClassName()+"의  구매수량  플러스 버튼 눌림");

                //플러스 버튼 눌리면  1씩 올라감
                buy_count=buy_count+1;

                //buy_count가 0보다  작을때  수량이 마이너스로 가면 안되서  warning을 날린다.
                if(buy_count<0){

                    new Toastcustomer(this_activity_context).showcustomtaost(edit_txt_for_point_buy_count, "수량은  0이상  이어야 합니다");
                    buy_count=0;//buy_count는  0으로 다시 맞춰줌.
                }


                //플러스 버튼 눌렸으므로  1올라간  값  다시  edittext에 넣어줌.
                edit_txt_for_point_buy_count.setText(buy_count+"");

                //커서가  계속 앞으로 가져서  -> 커서위치  문자 뒤로 다시 설정
                edit_txt_for_point_buy_count.setSelection(edit_txt_for_point_buy_count.length());

                //다른  포인트 및  가격 텍스트뷰 값 다시 세팅
                change_amount_eache_view_for_point(buy_count);

            }//onClick() 끝
        });//구매수량  플러스  버튼 이벤트 끝




        //1-6  구매수량 마이너스 버튼
        btn_for_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.v("check", getLocalClassName()+"의  구매수량  마이너스 버튼 눌림");

                buy_count=buy_count-1;

                if(buy_count<0){

                    new Toastcustomer(this_activity_context).showcustomtaost(edit_txt_for_point_buy_count, "수량은  0이상  이어야 합니다");

                    buy_count=0;
                }

                edit_txt_for_point_buy_count.setText(buy_count+"");
                edit_txt_for_point_buy_count.setSelection(edit_txt_for_point_buy_count.length());

                change_amount_eache_view_for_point(buy_count);

            }//onClick() 끝
        });//마이너스 버튼 클릭시 이벤트



        //EDIT텍스트 변화 감지  1-7
        edit_txt_for_point_buy_count.addTextChangedListener(new TextWatcher() {

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
                    buy_count=0;
                    change_amount_eache_view_for_point(buy_count);

                }else{//edittext에  숫자가  있을때

                    try{

                        //
                        buy_count = Integer.parseInt(s.toString());

                        //어차피  intputtype을 number로 해놔서  -를 적용못하지만  혹시 몰라서  넣어둠
                        if(buy_count<0){
                            new Toastcustomer(this_activity_context).showcustomtaost(edit_txt_for_point_buy_count, "수량은  0이상  이어야 합니다");
                            buy_count=0;
                        }


                        change_amount_eache_view_for_point(buy_count);

                     }catch(NumberFormatException E) {

                    }
                }

            }//afterTextChanged() 끝
        });//edit_txt_for_point_buy_count() 끝




        //결제하기 버튼 클릭 이벤트 1-8
        btn_for_goto_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.v("check", getLocalClassName()+"의 결제하기 버튼 눌림");

                if(buy_count>0) {//결제는  최소  1개 이상  포인트를  구매할때 가능함

                    //한번더 확인하는 alert띄움
                    make_alert_for_recheck(txt_for_show_cash_amount_for_buy_point.getText().toString(),buy_count);

                }else{

                    new Toastcustomer(this_activity_context).showcustomtaost(edit_txt_for_point_buy_count, "최소 1개 이상의 포인트를 구매 해야합니다",1500,200);

                    buy_count=0;
                    edit_txt_for_point_buy_count.setText(buy_count+"");
                    edit_txt_for_point_buy_count.setSelection(edit_txt_for_point_buy_count.length());
                }


            }
        });


        //1-1
        toolbar = (Toolbar) findViewById(R.id.toolbar_for_select_charging_point_amount);//툴바 선언
        setSupportActionBar(toolbar);//툴바에  액션바  서포트 연결
        getSupportActionBar().setDisplayShowTitleEnabled(false);//엑션바에서 타이틀 안보이게함
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//액션바에서 Homeashup 버튼 가능하게함
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.whiteback);//해당 버튼  뒤로가기 버튼모양 으로  이미지 지정

    }//onCreatea() 끝끝


    private void make_alert_for_recheck(String  cash_amount_for_buy,int quantity){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("결제의사 확인").setMessage("총 "+cash_amount_for_buy+"이  듭니다\n정말 결제 하시겠습니까???");

        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Toastcustomer(this_activity_context).showcustomtaost(null, "결제 하기 눌림");

                //카카오 페이 진행한다.
                Intent intent_show_kakao_payment = new Intent(SelectChargingPointAmountActivity.this,KakaoPayActivity.class);
                intent_show_kakao_payment.putExtra("quantity", quantity);
                startActivity(intent_show_kakao_payment);

                finish();

                alertDialog_for_ask_pay_or_not.dismiss();;
            }//onClick() Rmx
        });// 알럴트 예  누름

        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                alertDialog_for_ask_pay_or_not.dismiss();;
            }
        });


        alertDialog_for_ask_pay_or_not= builder.create();

        alertDialog_for_ask_pay_or_not.show();

    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.v("check", getLocalClassName()+"의  onResume() 실행됨");

        GlobalApplication globalApplication=(GlobalApplication)getApplicationContext();

        //현재 학생 포인트를  가지고오기위한 메소드
        get_student_present_point(globalApplication.getStudnet_uid(),txt_for_show_std_present_point);

    }//onResume()끝

    //현재 보유 포인트를 보여주기 위해
    //서버에서 해당 학생의  포인트를 가지고 온다
    private  void get_student_present_point(String student_uid,TextView txt_for_show_present_point){

        //retrofit 통신 ..
        Retrofit retrofit_for_charging_std_point=new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create()).build();
        ApiService apiService=retrofit_for_charging_std_point.create(ApiService.class);

        //서버로  fcm토큰 보냄
        Call<ResponseBody> get_std_present_point=apiService.get_std_present_point_amount(student_uid);

        get_std_present_point.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    String result=response.body().string();

                    if(result.equals("-1")){
                        Log.v("check", getLocalClassName()+"의 get_student_present_point()에서  학생 포인트 가져오기 실패함 ");


                    }else{//성공시

                         txt_for_show_present_point.setText(result+" p");

                    }



                } catch (IOException e) {
                    e.printStackTrace();
                }


            }//onResponse()끝

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.v("check", getLocalClassName()+"의 get_student_present_point()에서  학생 포인트 가져오기 callback 실패-> "+t.getMessage());
            }//onFailure() 끝끝
        });


   }//get_student_present_point()끝

    //구매 수량  변경시마다  뷰들  바꿔주는 메소드
    private void change_amount_eache_view_for_point(int buy_count){



        //현재 구매하려는 포인트는  지정한 수량의
        txt_for_buy_point.setText((buy_count*10)+" Point");

        txt_for_show_cash_amount_for_buy_point.setText(buy_count*100+" 원");


    }



    //1-1
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//옵션아이템들 클릭시 진행되는 코드
        switch (item.getItemId()) {
            case android.R.id.home: { //toolbar의 back키 눌렀을 때 동작

                finish();//현재 엑티비티 끝냄.
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

}//SelectChargingPountAountActivity 클래스 끝
