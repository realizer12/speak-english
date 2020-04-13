package com.example.leedonghun.speakenglish;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.widget.SwipeRefreshLayout;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static androidx.recyclerview.widget.LinearLayoutManager.VERTICAL;

/**
 * speakenglish
 * Class: Logined_Teacher_List.
 * Created by leedonghun.
 * Created On 2019-09-20.
 * Description: 로그인한  선생님들 보여주는  리스트
 * 전체 리스트와,  내튜터 리스트,   global, native리스트로 나눠서  보여준다.
 * 해당  선생님 리스트별 바로 수업시작 버튼을 클릭할수 있다.
 *
 */
public class Logined_Teacher_List extends AppCompatActivity {


    ////////////////////////////////////////////////선생님  리스트가  없 을때///////////////////////////////////////////////

     //현재 엑티비티 보여진 화면에서  선생님 로그인목록  0이 되었을때,  뒤로 돌아갈건지,
     //새로고침으로 다시  볼건지 결정하는 버튼 들어있는 레이아웃
      LinearLayout refreshlistnotilayout;//1-1

      //선생님 목록 없을때 엑티비티 취소 버튼
      Button nolistteachercancelbtn;//1-2

      //선생님 목록 없을떄  새로고침 버튼
      Button nolisteacherrefreshbtn;//1-3
    ////////////////////////////////////////////////선생님  리스트가  없 을때//////////////////////////////////////////////

    ////////////////////////////////////////////////선생님  리스트가  있을때///////////////////////////////////////////////
    //로그인한 선생님 목록 보여주는  레이아웃
    LinearLayout teacherlistlayout;//2-1
    //선생님  전체  버튼
    Button btnfortotalteacher;//2-2

    //선생님  내 튜터 버튼
    Button btnformyteacher;//2-3

    //선생님  global 버튼
    Button btnforglobalteacher;//2-4

    //선생님 native 버튼
    Button btnfornativeteacher;//2-5

    //선생님  스와이프 새로고침용  레이아웃
    SwipeRefreshLayout swipeRefreshLayout;//2-6
    ////////////////////////////////////////////선생님  리스트가  있을때///////////////////////////////////////////////

    //툴바
    Toolbar toolbar;//2-7

    //선생님 로그인 리스트  데이터가 없을때 나오는 텍스트
    TextView loginteachernodata;//2-8



   ///////////////////////////////////////////// //리사이클러뷰 관련 ///////////////////////////////////////////////////////
    //로그인한 선생님  리스트 들어갈 리사이클러뷰
    RecyclerView loginedteacherlistrecyclerview;

    //recyvler뷰  레이아웃 매니저
    RecyclerView.LayoutManager recyaclerviewlayoutmanagerforloginteacher;

    //recyvlervie adpter
    LoginTeacherInfoRecyvlerviewAdapter loginTeacherInfoRecyvlerviewAdapter;

    private Retrofit retrofit;//리트로핏 선언
    private ApiService apiService;//api service 인터페이스

    //서버로부터 받은 로그인한 선생님들 정보 넣는  어레이리스트
    ArrayList<JsonObject> loginedteacherlist;

    ///////////////////////////////////////////// //리사이클러뷰 관련 ///////////////////////////////////////////////////////

    //필터 용 체크값
    private int filter=0;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_teacher_list);

        refreshlistnotilayout=findViewById(R.id.layoutfor_refresh_noti);//1-1
        nolistteachercancelbtn=findViewById(R.id.noteachercancelactivitybtn);//1-2
        nolisteacherrefreshbtn=findViewById(R.id.noteacherrefreshactivitybtn);//1-3

        teacherlistlayout=findViewById(R.id.layoutfor_teacher_login_list);//2-1
        btnfortotalteacher=findViewById(R.id.btnfortoatal);//2-2
        btnformyteacher=findViewById(R.id.btnformytutor);//2-3
        btnforglobalteacher=findViewById(R.id.btnforglobalteaccher);//2-4
        btnfornativeteacher=findViewById(R.id.btnfornativeteacher);//2-5
        swipeRefreshLayout=findViewById(R.id.loginteacherswuperefresh);//2-6
        toolbar=findViewById(R.id.toolbarfor_login_teacher_lis);//2-7
        loginteachernodata=findViewById(R.id.textnologinteacherdatatxt);//2-8


        //3-1
        //선생님 리스트 없을때 버튼 리스너들모음 메소드와 뷰 연결
        nolistteachercancelbtn.setOnClickListener(nolistteacherbtnlistners());//취소버튼 눌렀을떄
        nolisteacherrefreshbtn.setOnClickListener(nolistteacherbtnlistners());//새로고침 눌렀을때.

        //3-2
        //선생님 리스트 있을때  버튼 리스너들모음 메소드와 뷰 연결
        btnfortotalteacher.setOnClickListener(existteacherlistlisteners());//전체 선생님 버튼 눌렀을때
        btnfornativeteacher.setOnClickListener(existteacherlistlisteners());//native선생님 버튼 눌렀을때
        btnforglobalteacher.setOnClickListener(existteacherlistlisteners());//global선생님 버튼 눌렀을때
        btnformyteacher.setOnClickListener(existteacherlistlisteners());//내 튜터 버튼 눌렸을때

        //맨처음 시작할때  전체 필터링 버튼으로  클릭되어있어야 하므로,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            btnfortotalteacher.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(61, 137, 236)));
        }
        btnfortotalteacher.setTextColor(Color.WHITE);
        ////////////////////////////////////////////////////////



        // 선생님 목록 있을떄 ->  스와이프리프레쉬 리스너
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                loginedteacherlistdataget(filter);//데이터 가져오기 메소드 실행.


                swipeRefreshLayout.setRefreshing(false);//false를 호출해야지  새로고침 아이콘이 사라짐.  -> 안해주면  안사라짐.
            }
        });//스와이프새로고침  리스너 끝.



        //toolbar 관련 코드
        setSupportActionBar(toolbar);//툴바에  액션바  서포트 연결
        getSupportActionBar().setDisplayShowTitleEnabled(false);//엑션바에서 타이틀 안보이게함
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//액션바에서 Homeashup 버튼 가능하게함
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.whiteback);//해당 버튼  뒤로가기 버튼모양 으로  이미지 지정



    }//oncreate 끝

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("check", getLocalClassName()+"의  onResume()실행됨.");

        //filter int값  0으로 줌 -> 전체 리스트 보기로  리스트 새로고침됨.
        loginedteacherlistdataget(filter);//데이터 가져오기 메소드 실행.

    }

    //3-1
  //선생님  리스트 없을때 버튼 리스너들
  private View.OnClickListener nolistteacherbtnlistners(){

      View.OnClickListener nolistteacher=new View.OnClickListener() {
          @Override
          public void onClick(View view) {

              switch (view.getId()){

                  //선생님 리스트 없을때 목록 새로고침 버튼
                  case R.id.noteacherrefreshactivitybtn:

                      //헤당 필터 위치로 다시 데이터 가져오기
                      //프로그래스바 실행하는  asyancktask실행 시켜  프로그래스바 실행과 함께  데이터 가져오기 실행.
                      refreshloginteacherlsit refreshloginteacherlsit=new refreshloginteacherlsit();
                      refreshloginteacherlsit.execute();

                      break;

                  //선생님 리스트 없을때 엑티비티  취소 버튼
                  case R.id.noteachercancelactivitybtn:

                      finish();//현재 엑티비티 끝냄.

                      break;
              }//switch끝

          }//onclick끝
      };//선생님 클릭리스너

      return nolistteacher;//return값  현 view리스너 값으로.

  }//선생님  리스트 없을때 버튼 리스너들 끝


 //필터 버튼 클릭 되었을떄, 색깔-> 파란색
ColorStateList clickedcolorforfilterbtn=ColorStateList.valueOf(Color.rgb(61, 137, 236));

 //필터 버튼 클릭전 색깔 ->  회색
ColorStateList beforclickforfilterbtn=ColorStateList.valueOf(Color.rgb(105, 105, 103)).withAlpha(126);

  //3-2
  //선생님  목록 있을떄 버튼 리스너들 모음 메소드
  private View.OnClickListener existteacherlistlisteners(){

       View.OnClickListener existteacher=new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               switch(view.getId()){

                   //전체 로그인 튜터 목록 보기 버튼
                   case R.id.btnfortoatal:

                       //필터 체크값.
                       filter=0;

                       //데이터 가져와서 -> 리사이클러뷰 어뎁터 연결시켜 화면 뿌리기 메소드
                       loginedteacherlistdataget(0);

                       //버튼이 클릭되었을때,  뷰 효과들
                       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                           btnfortotalteacher.setBackgroundTintList(clickedcolorforfilterbtn);
                           btnformyteacher.setBackgroundTintList(beforclickforfilterbtn);
                           btnforglobalteacher.setBackgroundTintList(beforclickforfilterbtn);
                           btnfornativeteacher.setBackgroundTintList(beforclickforfilterbtn);
                       }
                       btnfortotalteacher.setTextColor(Color.WHITE);
                       btnformyteacher.setTextColor(Color.BLACK);
                       btnforglobalteacher.setTextColor(Color.BLACK);
                       btnfornativeteacher.setTextColor(Color.BLACK);



                       break;

                   //내 튜터 목록 보기 버튼
                   case R.id.btnformytutor:
                       filter=1;
                        loginedteacherlistdataget(1);
                       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                           btnfortotalteacher.setBackgroundTintList(beforclickforfilterbtn);
                           btnformyteacher.setBackgroundTintList(clickedcolorforfilterbtn);
                           btnforglobalteacher.setBackgroundTintList(beforclickforfilterbtn);
                           btnfornativeteacher.setBackgroundTintList(beforclickforfilterbtn);
                       }
                       btnfortotalteacher.setTextColor(Color.BLACK);
                       btnformyteacher.setTextColor(Color.WHITE);
                       btnforglobalteacher.setTextColor(Color.BLACK);
                       btnfornativeteacher.setTextColor(Color.BLACK);

                       break;

                   //global 선생님 목록 보기 버튼
                   case R.id.btnforglobalteaccher:
                        filter=2;
                       loginedteacherlistdataget(2);
                       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                           btnfortotalteacher.setBackgroundTintList(beforclickforfilterbtn);
                           btnformyteacher.setBackgroundTintList(beforclickforfilterbtn);
                           btnforglobalteacher.setBackgroundTintList(clickedcolorforfilterbtn);
                           btnfornativeteacher.setBackgroundTintList(beforclickforfilterbtn);
                       }
                       btnfortotalteacher.setTextColor(Color.BLACK);
                       btnformyteacher.setTextColor(Color.BLACK);
                       btnforglobalteacher.setTextColor(Color.WHITE);
                       btnfornativeteacher.setTextColor(Color.BLACK);

                       break;

                   //native선생님 목록 보기 버튼
                   case R.id.btnfornativeteacher:
                       filter=3;
                       loginedteacherlistdataget(3);
                       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                           btnfortotalteacher.setBackgroundTintList(beforclickforfilterbtn);
                           btnformyteacher.setBackgroundTintList(beforclickforfilterbtn);
                           btnforglobalteacher.setBackgroundTintList(beforclickforfilterbtn);
                           btnfornativeteacher.setBackgroundTintList(clickedcolorforfilterbtn);
                       }
                       btnfortotalteacher.setTextColor(Color.BLACK);
                       btnformyteacher.setTextColor(Color.BLACK);
                       btnforglobalteacher.setTextColor(Color.BLACK);
                       btnfornativeteacher.setTextColor(Color.WHITE);


                       break;
               }//switch 끝
           }//onclick끝
       };// 선생님  목록 존재할떄 리스너


      return existteacher; //return값 현 view리스너 객체 리턴
  }
  //선생님  목록 있을떄 버튼 리스너들 모음 메소드 끝



    //toolbar 아이템  셀렉트
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//옵션아이템들 클릭시 진행되는 코드
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                finish();//현재 엑티비티 끝냄.
                Log.v("check",getLocalClassName()+"의 툴바 뒤로가기 눌림.");
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }//옵션 아이템 클릭시 진행 끝

    ArrayList<JsonObject>arrayList;

    //로그인한  선생님  데이터 가져오는  메소드
    private void loginedteacherlistdataget(final int fileter){

        //로그인한 선생님들 목록 들어갈 리사이클러뷰
        loginedteacherlistrecyclerview=findViewById(R.id.recyvlerviewforloginteacher);



        //gson내용 알아내기.
        GsonBuilder gsonBuilder=new GsonBuilder();
        gsonBuilder.setLenient();
        Gson gson=gsonBuilder.create();


        //리트로핏 빌드
        retrofit = new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create(gson))//json으로 받고 싶으면 쓰면안됨..
                .build();//리트로핏 뷸딩
        apiService = retrofit.create(ApiService.class);//api인터페이스 크리에이트


        //shared에  저장되어있는 현재 로그인한 학생 정보를 넣어서 보내줌.
        SharedPreferences getid = getSharedPreferences("loginstudentid", MODE_PRIVATE);//로그인 아이디  쉐어드에 담긴거 가져옴
        final String studentemeail = getid.getString("loginid", "");//로그인 아이디 가져옴


        //서버로부터  로그인한 선생님 리스트 가져오기.
        Call<GetLoginedTeachersInfo> getLoginedTeachersInfoCall =apiService.getloginedteachersinfolist(studentemeail);
        getLoginedTeachersInfoCall.enqueue(new Callback<GetLoginedTeachersInfo>() {
            @Override
            public void onResponse(Call<GetLoginedTeachersInfo> call, Response<GetLoginedTeachersInfo> response) {

                //responsebody가  null이 아닐때.
                if (response.body() != null) {
                    loginedteacherlist=response.body().getLoginedteacherarraylist();//어레이리스트 json 형식으로 받음.

                    Log.v("checkloginteacherlist", String.valueOf(loginedteacherlist));



                    //로그인 리스트  내용이  0이하일 경우,  로그인 한 상태의 선생님 이 없는 것이므로,  refresh요청  레이아웃을 화면에 띄워준다.
                    //그리고 선생님  리스트 담는 스와이프 레이아웃은  안보이게 한다.
                    if(loginedteacherlist.size()<=0){

                        refreshlistnotilayout.setVisibility(View.VISIBLE);
                        swipeRefreshLayout.setVisibility(View.INVISIBLE);

                    }else{ //이번엔 반대로  선생님 로그인 리스트가 있는 것이므로,  선생님 리스트 스와이프 레이아웃을  보이게하고,  리프레쉬 요청 레이아웃을  안보이게 한다.



                      refreshlistnotilayout.setVisibility(View.INVISIBLE);
                      swipeRefreshLayout.setVisibility(View.VISIBLE);




                      //리사이클러뷰  어뎁터
                      loginTeacherInfoRecyvlerviewAdapter=new LoginTeacherInfoRecyvlerviewAdapter(loginedteacherlist,Logined_Teacher_List.this,fileter,loginteachernodata,btnformyteacher);

                      //리사이클러뷰 레이아웃 매니저
                      recyaclerviewlayoutmanagerforloginteacher=new LinearLayoutManager(Logined_Teacher_List.this,RecyclerView.VERTICAL,false);

                      //레이아웃 순서 뒤집기
                      ((LinearLayoutManager) recyaclerviewlayoutmanagerforloginteacher).setReverseLayout(false);
                      //setstackfrombottm과 같이 사용가능-> 리스트의  마지막  부분 부터 시작됨.
                      ((LinearLayoutManager) recyaclerviewlayoutmanagerforloginteacher).setStackFromEnd(false);

                        //리사이클러뷰에  레이아웃 매니저 연결 시켜줌.
                        loginedteacherlistrecyclerview.setLayoutManager(recyaclerviewlayoutmanagerforloginteacher);

                        //리사이클러뷰에  어뎁터 연결시켜줌.
                        loginedteacherlistrecyclerview.setAdapter(loginTeacherInfoRecyvlerviewAdapter);

                        //리사이클러뷰 스크롤안에  스크롤 허용  false
                        loginedteacherlistrecyclerview.setNestedScrollingEnabled(true);

                        //데이터 체인지  알려줌.
                        loginTeacherInfoRecyvlerviewAdapter.notifyDataSetChanged();

                        //맨처음 시작 포지션은   0->  맨위.
                        loginedteacherlistrecyclerview.scrollToPosition(0);


                    }
                    //로그인 리스트  선생님 수로 나눠지는 조건끝

                }else{
                    //responsebody가  null일 경우
                    Log.v("check", getLocalClassName()+"의  로그인 리스트 가져오는데 null값 발생함.");
                    refreshlistnotilayout.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setVisibility(View.INVISIBLE);
                }
            }//onResponse 끝

            @Override
            public void onFailure(Call<GetLoginedTeachersInfo> call, Throwable t) {
                Log.v("check", getLocalClassName()+"의  로그인 리스트 가져오는데 에러 발생"+t);
            }
        });//retrofit enquequ끝



    }
    //로그인한  선생님 데이터 가져오는 메소드 끝.





    //목록 새로고침 버튼 눌렸을때,  다이얼로그  띄워서  보여주기위한  asynctask
    private class refreshloginteacherlsit extends AsyncTask<Void,Void,Void>{

        ProgressDialog refreshprogressbar=new ProgressDialog(Logined_Teacher_List.this);

        @Override
        protected void onPreExecute() {
            Log.v("check", getLocalClassName()+"의  목록 새로고침  프로그래스 다이얼로그  실행");

            //프로그래스 다이얼로그  스타일 및  멘트 및  도중 취소 금지시킴.
            refreshprogressbar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            refreshprogressbar.setMessage("선생님 목록 새로고침 중....");
            refreshprogressbar.setCancelable(false);

            //프로그래스바  실행.
            refreshprogressbar.show();

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Log.v("check", getLocalClassName()+"의  목록 새로고침  프로그래스 다이얼로그 띄우고  선생님 목록 받아오는 중.");
            try {

               //for문으로  어느정도  프로그래스 바  느낌을 주기 위해 노력함.
                //왜냐면,  선생님 목록만 받아오려고 하니까 너무 빨리 받아와 줘서 다이얼로그가 안보였음.
                for (int i = 0; i < 3; i++) {

                    Thread.sleep(500);
                }

                //선생님 목록 받아오기
                loginedteacherlistdataget(filter);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            Log.v("check", getLocalClassName()+"의  목록 새로고침  프로그레스 다이얼로그  끝남.");

            //백그라운드 행위 다 끝났으니까  -> 프로그레스 다이얼로그 없애주낟.
            refreshprogressbar.dismiss();

            super.onPostExecute(aVoid);
        }//onPostexcute


    }//로그인 선생님  목록 새로고침  ->  프로그레스 다이얼로그  asynck task로  작동 시키는거 끝.

    @Override
    protected void onPause() {
        super.onPause();

        //선생님  쉐어드 프리페런스체크
        //이게 뭐냐면  loginedteacherlist를  벗어날때만,  새로고침으로  선생님 찾기 프래그먼트   새로고침 하도록 만듬.
        //왜냐면  선생님 찾기 프래그먼트에서  -> 선생님  프로필 본다음에  뒤로 돌아갈때도 새로고침이 계속 되니까  이부분에서
        //서버에 무리를 줄수 있겠다고 판단하여, 로그인한 선생님 리스트 보고 나올때만  이렇게 하도록  함.
        SharedPreferences Logined_Teacher_Listcheck =getSharedPreferences("logined_teacher_list_finish_check",MODE_PRIVATE);//로그인 아이디 저장
        SharedPreferences.Editor editor=Logined_Teacher_Listcheck.edit();//쉐어드 프리퍼런스 사용
        editor.putInt("finishcheck",5);//로그인 아이디 저장 함 'loginidteacher'라는 이름에다가
        editor.commit();//자료 받음

    }//onpause끝


}//엑티비티 끝.
