package com.example.leedonghun.speakenglish;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.bumptech.glide.Glide;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Manager;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.Inflater;

import gun0912.tedbottompicker.TedBottomPicker;
import gun0912.tedbottompicker.TedBottomSheetDialogFragment;
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
 * Class: ChattingRoomActivityForStudent.
 * Created by leedonghun.
 * Created On 2019-11-11.
 * Description: 채팅방  엑티비티 -> 학생용이다.
 * 이곳에서는  서버로 소켓연결이 들어가며, 해당  채팅 내용들은 영어로 제한 당한다.
 * 그리고 채팅내용을   번역기능으로  사전과 연결 시켜주거나  변역 내용을 보여줄수 있는 기능이있고
 * 선생님 채팅방과는 다르게  채팅방 나가기 목록이있다.
 *
 * 영상과  이미지를 올릴수 있으며,  각  채팅 참여자들의 목록을 볼수 있다.
 * 목록에서 선생님은  -> 누르면  프로필 이동이 가능하다.
 */
public class ChattingRoomActivityForStudent extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    int count_check=0;//sqlite로  채팅방 이미지 가지고 올떄,  -> socket connection부분에다가 위치하게 해놔서
    //다른  엑티비티 갔다가 돌아와도  다시 조회해옴 여기서 ->  기존  이미지  4개가  다 안차있으면,  똑같은걸  다음  이미지뷰에다가 넣어주는 현상나와서
    //oncreate에서만  가지고 오게  check 변수를 만듬.  -> checkcount는  oncreate가 실행되었을때만  1로 변하고,pause를 호츨히면   0이다.-> 0때는  다시 sqlite문에서 미디어 파일들을 조회하지 않는다.

    EditText input_chatting_content;//채팅 edittext /1-1
    Button send_chatting_btn;//채팅 보내기 버튼 /1-2
    ImageButton get_chatting_file_btn;//사진 영상 등 파일 가져오기 위한 버튼/1-3
    Toolbar chatting_room_toolbar;//채팅방에서  나가기 밑  뒤로가기 버튼이 들어갈  툴바이다. //1-4

    RecyclerView chatting_recyclerview;//채팅 내용이  보여질  리사이클러뷰 /2-2
    ChattingDataRecyclerViewAdapter chatting_Data_RecyclerView_Adapter;
    RecyclerView.LayoutManager chatting_Recyclerview_manager;

    TextView room_name_textview;//현재 채팅방의 이름이 들어간다.//2-3


    InputMethodManager managerforkeyboardaction;//키보드 관련/2-5


    Socket socket;//자바  서버 연결 위한 소켓
    ArrayList<JSONObject> chattingdata;// json으로 만들어진 chatting 데이터가  담길  어레이 리스트//6-6

    String useremail;//학생 이메일//2-1
    String useruid;//학생 uid//5-5
    String teacheruid;//선생님 uid//오픈 채팅방의 경우 선생님 uid가  선생님 방번호. //일대일 채팅의 경우  선생님 uid +학생 uid

    ConstraintLayout basic_layout;//현재 엑티비티 전체 레이아웃.

    String iport="http://13.209.249.1:8888";//ip 연결

    int chattingroomtype;//현재 채팅방의  타입을  받는 변수 -> 오픈 채팅방인지 일대일  채팅방인지.
    String chattingroomnumber;//연결할  채팅방 번호 들어갈  변수->  오픈채팅일때는 -> 선생님 uid,  일대일 채팅일때는 학생 uid+ 선생님 uid가 들어감.//5-5

    TextView chattingclientcount_tv;// 채팅방 참여 인원이  들어가는  텍스트뷰 / 2명이상일때부터 숫자가 보이도록 설정한다.//5-6
    int chattingclientcount;//채팅방 참여인원 숫자

    DrawerLayout drawer;//드로워 레이아웃

    Softkeyboard mSoftKeyboard;//키보드 변환  감지용  객체

    String messagecontent_from_edittext;// 에딧텍스트에 쓴 채팅내용들어가는 스트링 변수이다. -> 나중에  아무것도 안적혀있거나 한글등이 적혀 있는 경우를  걸러내기 위해서

    String teachername;//선생님 이름.


    SqLiteOpenHelperClass sqLiteOpenHelper;//sqlite 생성하는 클래스
    SQLiteDatabase database;//sqlite 데이터베이스

    String studentloginedid;
    String loginid_for_sql;

    ArrayList<Integer>unread_chatting_order_list;



    /**네비게이션 뷰 안에 들어가는  뷰*/
    TextView textView_for_show_roomfiles_stu;//해당 방의  파일들 목록으로  가는 텍스트뷰 4-0
    TextView textView_for_chattingmembercount;//해당  채팅 멤버들의  총  카운트가 들어가는 텍스트뷰 4-0-1

    //최근 -> 추가된  미디어 파일 (비디오,  이미지) 파일  들어가는  이미지뷰들
    //리사이클러뷰로 추가 하려다가  4개 고장이고 많지 않아서 -> 이미지뷰로 하나씩 넣어주기로함
    ImageView getShow_recent_mediafile1; //4-1
    ImageView getShow_recent_mediafile2; //4-2
    ImageView getShow_recent_mediafile3; //4-3
    ImageView getShow_recent_mediafile4; //4-4

    RecyclerView recyclerView_for_put_room_members;//  룸에  참여중인 멤버들  리스트가 나열되는 리사이클러뷰 3-5
    LinearLayout chatting_room_out_stu_container;// 채팅 나가기 버튼역할을 하는  리니어 레이아웃. 3-6


    ChattingMembersListRecyclerViewadapter chattingMembersListRecyclerViewadapter;//채팅 멤버  리사이클러뷰 어뎁터

    LinearLayoutManager chattingmemberlistmanager;


    Activity activity;//현재 엑티비티 받기 위한 엑티비티 객체

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatting_drawable_navigation_for_student);//현재 엑티비티에서는  드로어 레이아웃을 사용하여야하기 때문에  원래  chatting_room_activity가 include된 chatting drawable_navigation_for_student를 사용.

        Log.v("check", getLocalClassName()+"의  onCreate() 실행됨");

        SharedPreferences getid_student = getSharedPreferences("loginstudentid", MODE_PRIVATE);//로그인 아이디  쉐어드에 담긴거 가져옴
        studentloginedid = getid_student.getString("loginid", "");//로그인 아이디 가져옴



//        //recyclerview -> useruid 필요해서   여기다  넣어줌.
//        chatting_Data_RecyclerView_Adapter=new ChattingDataRecyclerViewAdapter(chattingdata,ChattingRoomActivityForStudent.this,useruid,"t");
//        chatting_Recyclerview_manager=new LinearLayoutManager(ChattingRoomActivityForStudent.this,RecyclerView.VERTICAL,false);
//        ((LinearLayoutManager) chatting_Recyclerview_manager).setReverseLayout(false);
//        ((LinearLayoutManager) chatting_Recyclerview_manager).setStackFromEnd(false);
//
//        chatting_recyclerview.setLayoutManager(chatting_Recyclerview_manager);
//        chatting_recyclerview.setAdapter(chatting_Data_RecyclerView_Adapter);
//
//        ((SimpleItemAnimator) chatting_recyclerview.getItemAnimator()).setSupportsChangeAnimations(false);//리사이클러뷰에  변경 될때 진행되는 애니메이션  false값 넣어줌.
//        chatting_recyclerview.setNestedScrollingEnabled(false);//스크롤 부드럽게


       input_chatting_content=findViewById(R.id.edittextforchat);//1-1
       send_chatting_btn=findViewById(R.id.btnforsendchat);//1-2
       get_chatting_file_btn=findViewById(R.id.btnforsendfile);//1-3
       chatting_room_toolbar=findViewById(R.id.toolbarforchattingroom);//1-4

       chatting_recyclerview=findViewById(R.id.showingchatrecyvlerview);//2-2
       room_name_textview=findViewById(R.id.room_name_text);//2-3

       chattingclientcount_tv=findViewById(R.id.chatclientcounttextview);//5-6

        activity=ChattingRoomActivityForStudent.this;//현재 엑티비티 넣어줌.


        chattingdata=new ArrayList<>();//6-6

        textView_for_show_roomfiles_stu=findViewById(R.id.text_view_for_show_room_files_stu);//4-0
        textView_for_chattingmembercount=findViewById(R.id.textView_for_chattingmembercount);//4-0-1

        getShow_recent_mediafile1=findViewById(R.id.show_recent_file1_stu);//4-1
        getShow_recent_mediafile2=findViewById(R.id.show_recent_file2_stu);//4-2
        getShow_recent_mediafile3=findViewById(R.id.show_recent_file3_stu);//4-3
        getShow_recent_mediafile4=findViewById(R.id.show_recent_file4_stu);//4-4


        recyclerView_for_put_room_members=findViewById(R.id.recyclerview_for_show_room_members_stu);//3-5

        chatting_room_out_stu_container=findViewById(R.id.chatting_room_out_stu_container);//3-6


        //TeacherProfile 엑티비티에서 가져온 intent 데이터 //선생님 uid & 학생 이메일 받기
        Intent getchattinginfo =getIntent();
        teacheruid=getchattinginfo.getStringExtra("teacheruid");//선생님 uid
        useremail=getchattinginfo.getStringExtra("studentemail");//학생 이메일
        teachername=getchattinginfo.getStringExtra("teachername");//선생님이름.
        chattingroomtype=getchattinginfo.getIntExtra("chattingroomtype",-1);//채팅타입  /오픈 채팅방인지 일대일 채팅방인지  인텐트로 넘겨받음.



        loginid_for_sql =studentloginedid.replaceAll("@", "");
        sqLiteOpenHelper=new SqLiteOpenHelperClass(ChattingRoomActivityForStudent.this,loginid_for_sql , null,1 );
        database=sqLiteOpenHelper.getWritableDatabase();

        unread_chatting_order_list=new ArrayList<>();


        //현재 엑티비티 ->  툴바  부분  설정
        setSupportActionBar(chatting_room_toolbar);//액션바를  툴바 설정
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//toolbar에서 home키  부분 활성화
        getSupportActionBar().setDisplayShowTitleEnabled(false);//엑션바에서 타이틀 안보이게함
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.whiteback);//홈키 부분  뒤로가기 모양으로 바꿔줌.




        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_stu_chatting);//드로워 안에  넣어두었던  네비게이션 뷰 선언
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_chatting_for_student);//드로어 레이아웃 선언

        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);//드로어를   스와프 형태로  가지고 오지 못하고  코딩으로 (버튼 클릭)만으로 가져오도록 하기위한 코드

        navigationView.setNavigationItemSelectedListener(this);//네비게이션 리스너 -> 현재 엑티비티  implements 연결

         //드로워 관련  이벤트  리스너
        drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {

            //채팅창 드로워  관련 리스너 이벤트는 -> 카카오톡을  참고 하여서  만들었음.

            @Override
            public void onDrawerOpened(View drawerView) {//드로워가 열릴때
                Log.v("check", getLocalClassName()+"의 드로워 네비게이션 열림.");

                //드로워가 열리게되면
                //키보드가 열려있을때  -> 키보드를  다시  숨겨준다.
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                //드로워가  열렸을때는  처음에  엑티비티 실행시  실행해 두었던 lock 모드를 풀어준다.
                //이렇게 되면 드로워를 스와이프 하면  해당 드로워가  움직인다.
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);//드로어를   스와프 형태로  가지고 오지 못하고  코딩으로 (버튼 클릭)만으로 가져오도록 하기위한 코드

                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {//드로워가  닫힐때
                Log.v("check", getLocalClassName()+"의 드로워 네비게이션 닫힘.");

                //드로워가 닫힐때에는 열렸을때  풀어져있던  락모드를  다시 걸어준다.
                //이렇게 하면 다시  처음처럼  스와이프로  드로워를 open할수 없다.
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);//드로어를   스와프 형태로  가지고 오지 못하고  코딩으로 (버튼 클릭)만으로 가져오도록 하기위한 코드

                super.onDrawerClosed(drawerView);
            }
        });//드로워 리스너  끝




        //채팅 내용들어가는  edittext 부분에서 ->  필터링 효과  적용시킴
        //한글이 내용중에 들어가면  필터링 하게됨.
        input_chatting_content.setFilters(new InputFilter[]{filter});




        //채팅방 나가기 버튼눌림  //3-6
        chatting_room_out_stu_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("check",getLocalClassName()+"의  채팅방 나가기 버튼 눌림");


                //채팅방 나가기 버튼을  잘못 눌렀으을 수도 있으므로
                //alert로  한번 더  의사를  확인한다.
                AlertDialog.Builder alert_builder=new AlertDialog.Builder(ChattingRoomActivityForStudent.this);
                alert_builder.setCancelable(false);//alert e다른 곳  눌렀을때  cancel되는거  막음.

                alert_builder.setTitle("정말  채팅 방을  나가시겠어요??");//alert  제목

                //alert 취소
                alert_builder.setPositiveButton("아니요", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }

                });

                //alert 에  네라고 말함으로써 -> 채팅방  나가기  진행.
                alert_builder.setNegativeButton("네", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //채팅방 나가기   메소드 실행
                        chattingroom_out();
                    }

                });

                alert_builder.show();//채팅방  나가기 의사  alert 보이기

            }//onClick()끝

        });//3-6 이벤트 끝




        //3-1 클릭이벤트 -> 현재 방에서 주고받은 파일들  목록 보기
        textView_for_show_roomfiles_stu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("check", getLocalClassName() + "에서 videos_and_photos_textview 버튼 눌림-> 파일 리스트가 있는 엑티비티로 넘어감.");


                //미디어 파일들 모아둔  엑티비티로 넘어간다.
                Intent goto_show_chatting_files_activity = new Intent(ChattingRoomActivityForStudent.this, ShowChattingFilesActivity.class);
                goto_show_chatting_files_activity.putExtra("databasename", loginid_for_sql);//데이터 베이스 이름
                goto_show_chatting_files_activity.putExtra("roomnumber", chattingroomnumber);//방 번호.


                //showChattingFilesActivity로 넘어간다.
                startActivity(goto_show_chatting_files_activity);

            }//onCLick() 끝
        });//3-1 클릭 끝.


        count_check=1;


    }//onCreate()끝


    //해당 채팅방의 참여한 인원의  리스트를  받아온다.
    private void get_room_joined_users_list(String chattingroomnumber,RecyclerView userlist_recyclerview,String useruid){

        //gson내용 알아내기.
        GsonBuilder gsonBuilder=new GsonBuilder();
        gsonBuilder.setLenient();
        Gson gson=gsonBuilder.create();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create(gson))//json으로 받고 싶으면 쓰면안됨..
                .build();//리트로핏 뷸딩
        ApiService apiService = retrofit.create(ApiService.class);//api인터페이스 크리에이트
       // RequestBody roomnumber =RequestBody.create(MediaType.parse("text/plain"),chattingroomnumber);//서버로 보내  학생이메일

        Call<GetUserList> get_room_joined_users_list=apiService.get_room_joined_users_list(chattingroomnumber);//채팅방에 참여한  유저 리스트 가져오기진행

        get_room_joined_users_list.enqueue(new Callback<GetUserList>() {
            @Override
            public void onResponse(Call<GetUserList> call, Response<GetUserList> response) {

                ArrayList<JsonObject>user_array_list= null;//유저의  어레이 리스트

              //reponse값이 null이 아닐경우.
              if (response.body() != null) {

                    //arraylist에  -> 유저 어레이 리스트 정보 가지고온거 넣어줌.
                    user_array_list = response.body().getRoom_userlist();

                    String totalcount=user_array_list.get(0).get("totalcount").toString().replaceAll("\"", "");

                    textView_for_chattingmembercount.setText("Chatting Members  ("+totalcount+")");


                   Log.v("check", getLocalClassName()+"의  "+chattingroomnumber+"방  user 리스트->"+user_array_list);
                      //recyclerview -> useruid 필요해서   여기다  넣어줌.


               //해당 멤버 리스트 보여주는  리사이클러뷰 관련 코드
               chattingMembersListRecyclerViewadapter=new ChattingMembersListRecyclerViewadapter(ChattingRoomActivityForStudent.this,user_array_list,useruid) ;
               chattingmemberlistmanager=new LinearLayoutManager(ChattingRoomActivityForStudent.this,RecyclerView.VERTICAL,false);
               chattingmemberlistmanager.setReverseLayout(false);
               chattingmemberlistmanager.setStackFromEnd(false);
               userlist_recyclerview.setLayoutManager(chattingmemberlistmanager);
               userlist_recyclerview.setAdapter(chattingMembersListRecyclerViewadapter);
               userlist_recyclerview.setNestedScrollingEnabled(false);//스크롤 부드럽게


              }//reponse값이 null이 아닐경우.


            }//onResponse() 끝

            @Override
            public void onFailure(Call<GetUserList> call, Throwable t) {
                Log.v("check", getLocalClassName()+"의  "+chattingroomnumber+"방  user 리스트-에러>"+t);

            }//onFailuer()끝
        });

    }//get_room_joined_users_list() 끝



    //해당방의 sqlite부분에서  unreadcount를 가지고 온다.
    private void refresh_server_unreadcount(String roomnumber,String databasename){

         ArrayList<JSONObject> arrayList=new ArrayList<>();

        //sqlite에서  읽지 않은 메세지로 분류된  채팅 메세지들을 가지고 온다
        SqLiteOpenHelperClass GetsqLite_chatting_Database = new SqLiteOpenHelperClass (ChattingRoomActivityForStudent.this,databasename , null, 1);
        SQLiteDatabase Get_saved_database = GetsqLite_chatting_Database.getReadableDatabase();//데이터 베이스 읽기로  데이터에 접근.
        String sql = "select * from chatting_data_store where roomnumber='"+roomnumber+"' and readornot=1";//chatting_data_store테이블 에서 해당방  readornot=1 (읽지 않은) 내용 조회

        Cursor cursor=Get_saved_database.rawQuery(sql,null);///위 쿼리문  데이터베이스로 보냄.

       while (cursor.moveToNext()){//가지고온  쿼리 목록을 담은  cursor를  while문으로 하나씩 돌림

           String chat_message_roomnumber=cursor.getString(3);
           String chat_message_order=cursor.getString(10);

           Log.v("check",getLocalClassName()+"에서 sqlite에  readornot이  1인  채팅 메세지의  방번호: "+chat_message_roomnumber);
           Log.v("check", getLocalClassName()+"에서  sqlite에  readornot이  1인  채팅메세지의 순서:"+chat_message_order);

           JSONObject jsonObject=new JSONObject();

           try {
               jsonObject.put("roomnumber", chat_message_roomnumber);
               jsonObject.put("chatorder", chat_message_order);
               arrayList.add(jsonObject);

           } catch (JSONException e) {
               e.printStackTrace();
           }


       }//cursor가 null이 아닐때 ->  cursor  movettonext로 while문 끝.

        Log.v("check", getLocalClassName()+"에서  sqlite에  readornot이  1인  서버로갈  어레이리스트:"+arrayList);
        Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create())//json으로 받고 싶으면 쓰면안됨..
                .build();//리트로핏 뷸딩
        ApiService apiService = retrofit.create(ApiService.class);//api인터페이스 크리에이트
        RequestBody chatting_readornot_array =RequestBody.create(MediaType.parse("application/json"), String.valueOf(arrayList));//서버로 보내  학생이메일

        Call<ResponseBody> update_chatting_data_unreadcount=apiService.update_unread_count_for_chatting_message(chatting_readornot_array);

        update_chatting_data_unreadcount.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    Log.v("check","eadornot이  1인임이걱ㄱㄱㄱㄱㄱㄱ"+ response.body().string());


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });


        //채팅 데이터를 읽음으로 표시하는 -> 메소드
        make_chatting_data_read(roomnumber, databasename);

    }//refresh_server_unreadcount()끝



    //서비스가 실행중인지  판단하기 위한 메소드
    private boolean isMyServiceRunning(Class<?> serviceClass) {

        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);//엑티ㅣ비티 매니져

        //엑티비티 매니저에서  실행중인 서비스 정보  객체 안에  -> 실행중인  서비스들  하나씩  넣어서 -> 해당 서비스와 -> 이름이 같은지  체크하기.
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {

            if (serviceClass.getName().equals(service.service.getClassName())) {//해당 서비스가  실행중인 서비스 중 하나와  이름이 같다면
                return true;//true로  -> 실행중임을 알린다.

            }
        }
        return false;//그외 경우 ->  false로 실행주이지 않을을 알린다.

    }//isMyServiceRunning() 메소드 끝,


    //네비게이션 드로워 에서  아이템  선택될때  리스너
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        int id = menuItem.getItemId();//네비게이션 드로워에서 해당 메뉴의 아이디


        switch (id){


        }

        return false;
    }//onNavigationselected() 끝




    //채팅방을 나가면, 기존 room디비에  해당 방의  client count가  1줄어든다.
    //그리고 chatconinf디비에서  해당  학생의  해당 room연결  데이터를 삭제 시켜준다.
    public void chattingroom_out(){

        Log.v("check", getLocalClassName()+"의 cahttingroom_out  메소드 실행됨.");

        Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create())//json으로 받고 싶으면 쓰면안됨..
                .build();//리트로핏 뷸딩
        ApiService apiService = retrofit.create(ApiService.class);//api인터페이스 크리에이트

        RequestBody studentuid=RequestBody.create(MediaType.parse("text/plain"), useruid);//서버로 보내  학생이메일
        final RequestBody roomnumber=RequestBody.create(MediaType.parse("text/plain"), chattingroomnumber);//서버로 보낼  현재 학생이 들어간  채팅룸 번호
        RequestBody chattingtype=RequestBody.create(MediaType.parse("text/plain"), String.valueOf(chattingroomtype));//서버로 보낼 현재 학생이 나가려고하는  채팅방 타입.

        Call<ResponseBody> chattingroom_out_call =apiService.deleteclientinfoinchattingroom(studentuid,roomnumber,chattingtype);//학생정보 얻기위한  call객체

        //채팅방 나가기 콜백
        chattingroom_out_call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();

                    if (result.equals("7")) {//오픈 채팅방 나갈때
                        Log.v("check", getLocalClassName()+"의 chatting_room() retrofit통신 결과값  7 나옴->  성공적으로 모든 처리가 끝남.");


                        //채팅룸  out에 대한  소식을  다른 다른 클라이언트에게 broadcast하기 위한 코드
                        socket.emit("chattingroomout",useruid,chattingroomnumber);

                        //채팅방 나가기 버튼을 누르면 ->  MainForstudent로 나가진다.
                        Intent intent=new Intent(ChattingRoomActivityForStudent.this,MainactiviyForstudent.class);
                        intent.putExtra("checkbackstack", 2);//해당  checkbackstack에서 2를 보낼경우 -> 프래그먼트가 chattingtype으로  보여준다.

                        socket.disconnect();//socket 연결 종료

                        //나가므로 기존에 해당  룸넘버에 관련된 채팅내용을  모두 지워준다.
                        String sql_fordelete="DELETE FROM chatting_data_store WHERE roomnumber='"+chattingroomnumber+"'";
                        database.execSQL(sql_fordelete);
                        database.close();

                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);//메인으로 가질때 이전에 있던 엑티비티 스택은  모두 정리하기위한  플래그
                        startActivity(intent);//해당 엑티비티 실행


                    }else if(result.equals("8")){
                        Log.v("check", getLocalClassName()+"의 chatting_room() retrofit통신 결과값  8 나옴->  현재 유저와 현재 이방의 연결기록이  디비에 없음..");
                        new Toastcustomer(ChattingRoomActivityForStudent.this).showcustomtaost(null, "채팅방과 사전에  연결 기록이  없습니다.",1500,150);

                    }else if(result.equals("3")){
                        Log.v("check", getLocalClassName()+"의 chatting_room() retrofit통신 결과값  3 나옴->  room 에서  chatclientcount값 -1못함.");
                        new Toastcustomer(ChattingRoomActivityForStudent.this).showcustomtaost(null, "서버에러 남",1500,150);


                    }else if(result.equals("2")){
                        Log.v("check", getLocalClassName()+"의 chatting_room() retrofit통신 결과값  2 나옴->  룸과 학생 연결관계 지우기 실패");
                        new Toastcustomer(ChattingRoomActivityForStudent.this).showcustomtaost(null, "서버 에러남.",1500,150);


                    }else if(result.equals("1")){
                        Log.v("check", getLocalClassName()+"의 chatting_room() retrofit통신 결과값  1 나옴->  학생uid정보를못가지고 옴.");
                        new Toastcustomer(ChattingRoomActivityForStudent.this).showcustomtaost(null, "서버 에러남.",1500,150);

                    }else if(result.equals("9")){//일대일 채팅방 나갈때.
                        Log.v("check", getLocalClassName()+"의 chatting_room() retrofit통신 결과값  9 나옴->  일대일 채팅이어서 해당 room db는 클라이언트 수는  안까고 -chatuserconinfo만  삭제시킴");

                        delete_whole_onetoone_chatting_data_in_mysql(chattingroomnumber);//서버에서 해당 채팅방의  데이터들 모두 지워줌.

                        //채팅룸  out에 대한  소식을  다른 다른 클라이언트에게 broadcast하기 위한 코드
                        //채팅방 나가기 버튼을 누르면 ->  MainForstudent로 나가진다.
                        Intent intent=new Intent(ChattingRoomActivityForStudent.this,MainactiviyForstudent.class);
                        intent.putExtra("checkbackstack", 2);//해당  checkbackstack에서 2를 보낼경우 -> 프래그먼트가 chattingtype으로  보여준다.

                        //채팅룸  out에 대한  소식을  다른 다른 클라이언트에게 broadcast하기 위한 코드
                        socket.emit("chattingroomout1",useruid,chattingroomnumber);

                        socket.disconnect();//socket 연결 종료


                        //나가므로 기존에 해당  룸넘버에 관련된 채팅내용을  모두 지워준다.
                        String sql_fordelete="DELETE FROM chatting_data_store WHERE roomnumber='"+chattingroomnumber+"'";
                        database.execSQL(sql_fordelete);
                        database.close();


                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);//메인으로 가질때 이전에 있던 엑티비티 스택은  모두 정리하기위한  플래그
                        startActivity(intent);//해당 엑티비티 실행
                    }//1대1 채팅에서 방을 나갔을때 일어나는 경우


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }//onResponse()끝

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.v("check", String.valueOf(t));

            }//onFailure끝.
        });//chattingroom_out_call 끝



    }//chattingroomout()메소드 끝


    //일대일 채팅방에 경우 해당방을 학생이 나갈때  서버 채팅 숫자까지 모두 지워준다.
    //서버 디비 savechattingdata에  들어있는 모든 채팅 데이터 삭제시켜버림.
    private void delete_whole_onetoone_chatting_data_in_mysql(String roomnumber){

        Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create())//json으로 받고 싶으면 쓰면안됨..
                .build();//리트로핏 뷸딩
        ApiService apiService = retrofit.create(ApiService.class);//api인터페이스 크리에이트
        final RequestBody chatting_room_num=RequestBody.create(MediaType.parse("text/plain"), roomnumber);//서버로 보낼  현재 학생이 들어간  채팅룸 번호

        //해당방의  채팅 룸넘버를 모두 지워준다.
        Call<ResponseBody>delete_whole_chatting_data_in_onetoone=apiService.delete_whole_onetoone_messenger(chatting_room_num);

        //delete callback결과
        delete_whole_chatting_data_in_onetoone.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    String result=response.body().string();

                    if(result.equals("1")){
                        Log.v("check", getLocalClassName()+"의   delete_whole_chatting_data_in_onetoone()에서 서버에서 채팅데이터 delete 성공");

                    }else if(result.equals("2")){
                        Log.v("check", getLocalClassName()+"의   delete_whole_chatting_data_in_onetoone()에서 서버에서 delete 실패");

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }//onResponse 끝

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
               Log.v("check", getLocalClassName()+"의   delete_whole_chatting_data_in_onetoone()에서  error값 받아옴->"+t);

            }//onFailure()끝
        });//  delete_whole_chatting_data_in_onetoone 콜백 결과끝



    }//delete_whole_onetoone_chatting_data_in_mysql() 끝

    @SuppressLint("ClickableViewAccessibility")//리사이클러뷰 터치 리스너 ->  warning 해결 하기 위해서
    @Override
    protected void onResume() {
        super.onResume();
        Log.v("check", getLocalClassName()+"의 onResume()함수  실행됨");





        //채팅 내용 받는 서비스가  실행중일때 조건이다.
        if(isMyServiceRunning(ServiceForGetChattingData.class)){

             Log.v("check", getLocalClassName()+"에서 ServiceForGetChattingData가 실행중이지만, onResume에서 stopService 멈춤");


             //서비스를  onResume에서  한번 멈춰준다.  이밎  실행 되어있는 상황에서 들어오니까 당연히 멈춰줘야됨.
             //채팅 내용 받는 백그라운드 서비스  멈추게 한다.
             Intent stop_backgroun_chatting_service=new Intent(ChattingRoomActivityForStudent.this,ServiceForGetChattingData.class);
             stopService(stop_backgroun_chatting_service);


        }//서비스가 실행중일때 -> 해당  서비스를 한번 멈춰준다. -> 멈추는 이유는  해당 채팅방에서는 다른 소켓 연결이 시도되기 때문이다.
         //밑에서 -> 채팅방용 소켓 연결이 끝나면 그때  다시   서비스로 연결 시켜준다. -> 이렇게 하면 joinedornot의 값이 1인 값은  제외 되므로, 현재 채팅방에서는
         //채팅방용 소켓만  추가되고 서비스에선  제외된다. -> 바로가기 12-1


        //학생 정보 가져오기 메소드 실행
        getstudentinfo(useremail);


        //아래는  키보드 처리관련  코드들
        //onpause로 생명주기가 갔다가 오면 ->  onCreate애선  키보드관련  코드가 안먹히는 것을 발견하여  onresume으로 넣어주기로함.
        basic_layout=findViewById(R.id.mainlayout_chatting_room_activity);//키보드 변환을 감지하기 위해서  필요한  ->  현재  엑티비티  basic 레이아웃
        //키보드  관련 메니저  객체
        managerforkeyboardaction= (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);//2-5
        mSoftKeyboard = new Softkeyboard(basic_layout, managerforkeyboardaction);//키보드 변환 감지 용 클래스 객체


        //키보드 체인지  감지해서 callback
        mSoftKeyboard.setSoftKeyboardCallback(new Softkeyboard.SoftKeyboardChanged() {
            @Override
            public void onSoftKeyboardHide() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {

                        Log.v("check", getLocalClassName()+"의 키보드가 내려감");

                        //키보드 내려왔을때


                    }

                });//handler끝
            }//onSoftkeyboardHide() 끝

            @Override
            public void onSoftKeyboardShow() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {

                        Log.v("check", getLocalClassName()+"의 키보드가 올라옴");
                        // 키보드 올라왔을때

                    }//run() 끝

                });//handler끝
            }//onSoftkeyboardshow() 끝
        });//키보드 calllback끝


        // 키보드 올라올 때 RecyclerView의 위치를 마지막 포지션으로 이동
        chatting_recyclerview.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    v.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            chatting_recyclerview.smoothScrollToPosition(chatting_Data_RecyclerView_Adapter.getItemCount());
                        }
                    }, 100);
                }
            }
        });


        //리사이클러뷰 터치시 키보드 내려가는  이벤트
        chatting_recyclerview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Log.v("check", getLocalClassName()+"의 리사이클러뷰 클릭됨-> 키보드 내려감");
                mSoftKeyboard.closeSoftKeyboard();// 키보드 닫침 진행.

                return false;
            }
        });//리사이클러뷰 setOnTouchListener

    }//onResume() 끝





    //툴바에서  오른쪽에  추가되는  옵션 메뉴를 설정하는 메소드
    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        //커스톰한  옵셥 메뉴 버튼을 인플레이트 함.
        getMenuInflater().inflate(R.menu.chatting_toolbar_drawable_btn, menu);


        return super.onCreateOptionsMenu(menu);

    }//onCreateOptionMenu() 끝

    //툴바  옵션 메뉴들의  이벤트를  설정하기 위한   itemselected()메소드
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//옵션아이템들 클릭시 진행되는 코드
        switch (item.getItemId()){

            case android.R.id.home: { //toolbar의 back키 눌렀을 때 동작

                Log.v("check", getLocalClassName()+"의  툴바 뒤로가기 눌림.");

                finish();//현재 엑티비티 끝냄.

                return true;

            }

            case R.id.chatting_right_toolbar_icon:{//툴바에서  햄버거 버튼  눌렀을때.

//

                 drawer.openDrawer(Gravity.RIGHT);//네비게이션 드로워 오른쪽에서 열리도록 함.

            }

        }
        return super.onOptionsItemSelected(item);
    }//onOptionsitemSelected();



    //edditext로  focus되어   키보드가 올라왔을때,  ->  edittext이외의 역역 즉  전체 레이아웃 영역이  터치되면 다시 사라지도록 만듬 / 카카오톡 참고
    //해당  전체  레이아웃에  onclick부분에 hidekeyboard  넣어줌.
    public void hidekeyboard(View view){

        //키보드  관련 메니저  객체
        managerforkeyboardaction= (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);//2-5

        //윈도우에서 사라지도록  만듬.
        managerforkeyboardaction.hideSoftInputFromWindow(input_chatting_content.getWindowToken(), 0);
   }//hidekeyboard()끝


    //10-1
    //처음에  소켓  connection이 되었을때.
    private Emitter.Listener onConnect=new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            String userposition="s";//유저  포지션  s-> 학생


            //처음에  소켓이 연결될때 ->   유저의 uid,  접속하려는  채팅방 룸 넘버 / user의 포지션 (학생 or 선생)/노티에서 사용될  선생님 이름.
            socket.emit("join",useruid ,chattingroomnumber,userposition,0);

        }
    };//socket connection 이벤트 끝.


    //10-2
    //1대1 채팅방에 처음에  소켓  connection이 되었을때.
    private Emitter.Listener onConnect_for_one_to_one_chatting=new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            String userposition="s";//유저  포지션  s-> 학생


            //처음에  소켓이 연결될때 ->   유저의 uid,  접속하려는  채팅방 룸 넘버 / user의 포지션 (학생 or 선생)-> 마지막 파라미터는  상대방 uid를 넣어주는데
            //현재 엑티비티 같은 경우에는 학생용 채팅방이므로,  선생님 -> uid를 넣어준다.
            socket.emit("join",useruid ,chattingroomnumber,userposition,teacheruid,0);

        }
    };//socket connection 이벤트 끝.



   //학생 정보  가져오기 위한  메소드
   private void getstudentinfo(String studentemail){

        Log.v("check", getLocalClassName()+"에서 학생 정보를 가지고 오기 위한 getsudentinfo() 실행됨.");




       Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create())//json으로 받고 싶으면 쓰면안됨..
               .build();//리트로핏 뷸딩
       ApiService apiService = retrofit.create(ApiService.class);//api인터페이스 크리에이트

       Call<studentinforesult> getstudentinfo=apiService.sendemailtogetiprofile(studentemail);//학생정보 얻기위한  call객체

       //callback 실행.
       getstudentinfo.enqueue(new Callback<studentinforesult>() {
           @Override
           public void onResponse(Call<studentinforesult> call, Response<studentinforesult> response) {
               Log.v("check", getLocalClassName()+"의  getstudentinfo-> 학생정보  가져오기 성공"+response.body().toString());
               if(response.body() != null){//response가 0이 아닐때,

                   useruid=response.body().getUid();//5-5


                   studentmakechatonnection(useruid);//학생 소켓 연결  함수 실행.



               }//response !=nulll 조건 끝

           }//onResponse()끝

           @Override
           public void onFailure(Call<studentinforesult> call, Throwable t) {

               Log.v("check", getLocalClassName()+"의  getstudentinfo-> 학생정보 가져오기실팽함 -> 에러 내용 ->"+t);

           }//onFailure끝
       });//학생 정보 가져오기 callback 함수 끝

   }//getstudentinfo() 끝


    //채팅방에서 본 -> 모든 데이터들의  unread 컬럼의 값을  읽은 형태  0으로 만들어준다.
    private void make_chatting_data_read(String roomnumber,String databasename){

        Log.v("check", getLocalClassName()+"의  make_chatting_data_read() 실행됨");

        //현재까지 sqlite 디비에  저장되어있던  데이터들을  가지고  unreadcount -> 0(읽은 상태) 로  만들어줌.
        SqLiteOpenHelperClass update_chatting_database=new SqLiteOpenHelperClass(ChattingRoomActivityForStudent.this,databasename,null,1);
        SQLiteDatabase get_database = update_chatting_database.getWritableDatabase();//writeable 하게 데이터베이스 접근


        String upadate_sql="UPDATE chatting_data_store SET readornot=0 WHERE roomnumber='"+roomnumber+"'";//해당방의  -> update쿼리문 날림 -> reaornot의 값을 0으로 바꿔줌.
        get_database.execSQL(upadate_sql);//업데이트 쿼리문 날림
        get_database.close();//데이터베이스 닫아줌.

    }//makechatting_data_read() 끝

    //현재 채팅 서버에  맨처음 소켓연결을 진행하는  메소드
    private void studentmakechatonnection(String useruid){
        Log.v("check", getLocalClassName()+"의 makechatconnection() 함수 실행됨");

        try {

            Manager manager=new Manager(new URI(iport));//소켓 라이브러리에서  매니저

            if(chattingroomtype==0){//채팅방 타입이   오픈채팅방일때
                Log.v("check", getLocalClassName()+"에서 채팅방  타입이  오픈채팅방일때-> socket connection 진행");

                chattingroomnumber=teacheruid;//오픈 채팅방일때  채팅방 번호는 선생님uid이다.

                //해당 채팅방 참여자 리스트 받아오기 진행
                get_room_joined_users_list(chattingroomnumber,recyclerView_for_put_room_members,useruid);


                if(count_check==1) {
                    //미디어 파일  최근  4개를  가지고 오기위한  메소드 실행 -> 매개변수로는   데이터  이름과,  현재 채팅방 번호 파일이 들어갈 이미지뷰  4개를 가져간다.
                    get_recent_four_meida_file_info(loginid_for_sql, chattingroomnumber, getShow_recent_mediafile1, getShow_recent_mediafile2, getShow_recent_mediafile3, getShow_recent_mediafile4);

                }

                //sqlite데이터베이승서  -> 현재 채팅방의 채팅 메세지의  readornot 값이  1인  데이터들을 찾아서
                //서버에  보내 -> 서버 쪽 데이터들 중에  해당 데이터들에 대하여  1씩 값을 낮춰준다.
                refresh_server_unreadcount(chattingroomnumber,loginid_for_sql );


                select_chatting_data(chattingroomnumber, useruid);//sqlite에 저장된  채팅 내용 가지고옴.

                socket=manager.socket("/openchat");//서버 채팅용 socket  네임스페이스중 openchat에 연결한다.
                socket.connect();//서버 소켓 연결 시도
                socket.once(Socket.EVENT_CONNECT, onConnect);//소켓  연결후 첫 이벤트로 connection이벤트를 날린다. //10-1

                socket.on("joinbroadcast", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.v("CKKKKKKKKK학생방", "처음 들어옴");
                                chatting_Data_RecyclerView_Adapter.notifyDataSetChanged();//리사이클러뷰 어뎁터 다시 데이터 체인지 하게  ㄱ해줌.

                            }
                        });
                    }
                });

                //다른 채팅 클라이언트가  broadcast한 내용을 받는 리스너이다.
                //이경우 ->  새로들어온 참여자가 있거나, 방을 나가는 참여자가 있을 경우 실행된다.
                socket.on("messagebroadcast", new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                try {


                                    //해당 값들을 json으로 받는다.
                                    JSONObject receivedata = new JSONObject(args[0].toString());

                                    save_chatting_data_in_sql(receivedata,sqLiteOpenHelper,database);//채팅내용 sqlite에  넣어줌.

                                    chattingdata.add(receivedata);//메세지 들어온거  chattingdata어레이에 넣어줌.
                                    chatting_Data_RecyclerView_Adapter.notifyDataSetChanged();//리사이클러뷰 어뎁터 다시 데이터 체인지 하게  ㄱ해줌.
                                    chatting_recyclerview.scrollToPosition(chattingdata.size()-1);




                                    Log.v("check", getLocalClassName()+"의 오픈채팅방에서 받은 현재 채팅 데이터 내용-> "+chattingdata.toString());

                                     //viewtype에 따라  메세지를 분류하고, 그에 맞는 처리를 해준다.
                                    String viewtype=receivedata.getString("viewtype");

                                    //viewtype이 1의 경우 -> 해당 오픈 채팅방에  새로운 참여자가 들어올때의 경우이다.
                                    if(viewtype.equals("1")){

                                        //기존에 room 디비에서 받아온 채팅 참여자 숫자에서  +1을 해준다.
                                        //viewtype 1은  새롭게 상대가 들어왔다는 뜻이니까.
                                        //+1해준 값을  채팅방 참여 숫자  텍스트 뷰에 넣어줌.
                                        chattingclientcount=chattingclientcount+1;
                                        chattingclientcount_tv.setText("("+chattingclientcount+")");




                                    }//viewtype1 일 경우 끝.
                                    else if(viewtype.equals("2")){//viewtype이  2인 경우에는 -> 해당 오픈 채팅방을 나갈때의 경우이다.


                                        //viewtype2는  채팅방  유저가 나갔다는 뜻이니까.
                                        //--1해준 값을  채팅방 참여 숫자 텍스트뷰에 넣어준다.
                                        chattingclientcount=chattingclientcount-1;
                                        chattingclientcount_tv.setText("("+chattingclientcount+")");

                                    }//viewtype2의 경우 끝.
                                    else if(viewtype.equals("3")){//viewtype이  3일 경우에는 -> 일반 적인 채팅 메세지를 주고 받는 것이다.





                                    }//viewtype3일 경우 끝.



                                 }catch (JSONException e) {

                                    e.printStackTrace();

                                }

                            }//run 끝
                        });//runOnUiThread()
                    }//call() 끝
                });//socket messagebroadcast 끝/


                //채팅보내기 버튼 클릭 이벤트
                send_chatting_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Log.v("check", getLocalClassName()+"의 채팅 보내기 버튼이 눌림.");

                        messagecontent_from_edittext=input_chatting_content.getText().toString();
                        messagecontent_from_edittext.trim();

                        //채팅 내용이 없는 경우이다.
                        if(messagecontent_from_edittext.getBytes().length <= 0){//빈값이 넘어올때의 처리

                                 Animation wrongshake= AnimationUtils.loadAnimation(ChattingRoomActivityForStudent.this,R.anim.shakeedittext);//쉐이크하는 애니메이션 선언
                                 input_chatting_content.setHint("Write Any Message !!");//아무것도 안썼다고 멘트
                                 input_chatting_content.startAnimation(wrongshake);//쉐이크 애니메이션 실시
                                 input_chatting_content.setHintTextColor(Color.parseColor("#FFF43434"));// 아무것도 안씀을 경고하므로  빨간색으로 힌트 넣어줌.

                        //채팅 내용이 있을때임.
                        //채팅 보내기를 클릭하면  채팅 서버로 해당 내용 보내지는 코드와
                        //editext안  내용  사라지게 하는 코드 들어가야됨
                        }else{

                            socket.emit("message",input_chatting_content.getText());//서버로 보냄.

                            //editext내용  사라지게함
                            input_chatting_content.setText("");
                            input_chatting_content.setHint("insert your chat here.....");//다시 처음  hint 메세지로 리셋
                            input_chatting_content.setHintTextColor(Color.GRAY);//만약에 머  실수해서 -> 빨간색으로 바뀌었으면  채팅 메세지를 보내고 난뒤에는 다시 바꿔줌.

                        }

                    }//onClick
                });//채팅보내기 클릭이벤트 끝.


            }else if(chattingroomtype==1){//채팅방 타입이  일대일  채팅방일떄,

                Log.v("check", "채팅방  타입이  일대일 채팅방일때-> socket connection 진행");

                chattingroomnumber=teacheruid+useruid;//일대일 채팅방일때는  채팅방번호가  선생님 uid + 학생 uid 이다.

                    //해당 채팅방 참여자 리스트 받아오기 진행
                    get_room_joined_users_list(chattingroomnumber,recyclerView_for_put_room_members,useruid);

                if(count_check==1) {

                    //미디어 파일  최근  4개를  가지고 오기위한  메소드 실행 -> 매개변수로는   데이터  이름과,  현재 채팅방 번호 파일이 들어갈 이미지뷰  4개를 가져간다.
                    get_recent_four_meida_file_info(loginid_for_sql, chattingroomnumber, getShow_recent_mediafile1, getShow_recent_mediafile2, getShow_recent_mediafile3, getShow_recent_mediafile4);

                }

                //sqlite데이터베이승서  -> 현재 채팅방의 채팅 메세지의  readornot 값이  1인  데이터들을 찾아서
                //서버에  보내 -> 서버 쪽 데이터들 중에  해당 데이터들에 대하여  1씩 값을 낮춰준다.
                refresh_server_unreadcount(chattingroomnumber,loginid_for_sql );
                select_chatting_data(chattingroomnumber, useruid);//sqlite에 저장된  채팅 내용 가지고옴.


                socket=manager.socket("/onechat");//서버 채팅 네임스페이스 중 /onechat에 연결
                socket.connect();//서버 소켓 연결 시도
                socket.once(Socket.EVENT_CONNECT, onConnect_for_one_to_one_chatting);//소켓  연결후 첫 이벤트로 connection이벤트를 날린다. //10-2

                socket.on("joinbroadcast", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Log.v("CKKKKKKKKK학생방", "처음 들어옴");
                                chatting_Data_RecyclerView_Adapter.notifyDataSetChanged();//리사이클러뷰 어뎁터 다시 데이터 체인지 하게  ㄱ해줌.

                            }
                        });
                    }
                });



                //다른 채팅 클라이언트가  broadcast한 내용을 받는 리스너이다.
                //이경우 ->  새로들어온 참여자가 있거나, 방을 나가는 참여자가 있을 경우 실행된다.//메세지를 받을때도.
                socket.on("messagebroadcast", new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                try {

                                    //해당 값들을 json으로 받는다.
                                    JSONObject receivedata = new JSONObject(args[0].toString());

                                    save_chatting_data_in_sql(receivedata,sqLiteOpenHelper,database);//채팅내용 sqlite에  넣어줌.

                                    chattingdata.add(receivedata);//메세지 들어온거  chattingdata어레이에 넣어줌.
                                    chatting_Data_RecyclerView_Adapter.notifyDataSetChanged();//리사이클러뷰 어뎁터 다시 데이터 체인지 하게  ㄱ해줌.
                                    chatting_recyclerview.scrollToPosition(chattingdata.size()-1);

                                    Log.v("check",getLocalClassName()+"의 1대1 채팅 현재 받은 채팅데이터-> "+ chattingdata.toString());

                                }catch (JSONException e) {

                                    e.printStackTrace();

                                }

                            }//run 끝
                        });//runOnUiThread()
                    }//call() 끝
                });//socket messagebroadcast 끝/


                //채팅보내기 버튼 클릭 이벤트
                send_chatting_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Log.v("check", getLocalClassName()+"의 채팅 보내기 버튼이 눌림.");

                        messagecontent_from_edittext=input_chatting_content.getText().toString();//채팅 치는 editext부분  내용
                        messagecontent_from_edittext.trim();


                        //채팅 내용이 없는 경우이다.
                        if(messagecontent_from_edittext.getBytes().length <= 0){//빈값이 넘어올때의 처리


                            Animation wrongshake= AnimationUtils.loadAnimation(ChattingRoomActivityForStudent.this,R.anim.shakeedittext);//쉐이크하는 애니메이션 선언
                            input_chatting_content.setHint("Write Any Message !!");//아무것도 안썼다고 멘트
                            input_chatting_content.startAnimation(wrongshake);//쉐이크 애니메이션 실시
                            input_chatting_content.setHintTextColor(Color.parseColor("#FFF43434"));// 아무것도 안씀을 경고하므로  빨간색으로 힌트 넣어줌.

                            //채팅 내용이 있을때임.
                            //채팅 보내기를 클릭하면  채팅 서버로 해당 내용 보내지는 코드와
                            //editext안  내용  사라지게 하는 코드 들어가야됨
                        }else{


                            socket.emit("message",input_chatting_content.getText());//서버로 보냄.

                            //editext내용  사라지게함
                            input_chatting_content.setText("");
                            input_chatting_content.setHint("insert your chat here.....");//다시 처음  hint 메세지로 리셋
                            input_chatting_content.setHintTextColor(Color.GRAY);//만약에 머  실수해서 -> 빨간색으로 바뀌었으면  채팅 메세지를 보내고 난뒤에는 다시 바꿔줌.

                        }

                    }//onClick
                });//채팅보내기 클릭이벤트 끝.


            }//채팅방  타입 일대일일때 조건 끝.


             getroominfo(chattingroomnumber);//방 정보 가져오기


        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


         //바닥에서  업로드 관련  보여주기위한 Fragmentforbottomsheet dialog
        FragmentForBottomSheetDialog bottomSheetDialogFragment_for_upload_file=new FragmentForBottomSheetDialog(socket);

        //파일  업로드를 위한 버튼 클릭시 -> Fragementforbottomsheet dialog 실행됨
        get_chatting_file_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                bottomSheetDialogFragment_for_upload_file.setCancelable(false);//해당 다이얼로그  다른  위치눌러서 취소 못함.
                bottomSheetDialogFragment_for_upload_file.show(getSupportFragmentManager(), bottomSheetDialogFragment_for_upload_file.getTag());//다이얼로그 보여주기

            }//onClick()끝
        });//파일업로드 버튼 클릭시 끝


    }//makechatconnection() 끝



   //한글을 감지 학위한  필터링 이벤트
   protected InputFilter filter= new InputFilter() {

        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {


            Pattern ps = Pattern.compile( ".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*");//한글 정규식

            if (ps.matcher(source).matches()) {//한글이  에딧텍스에서 감지되었을때

                Log.v("check", getLocalClassName()+"에서 채팅 메세지 중  한글이 감지됨.");


                Animation wrongshake= AnimationUtils.loadAnimation(ChattingRoomActivityForStudent.this,R.anim.shakeedittext);//쉐이크하는 애니메이션 선언
                input_chatting_content.setHint("Plz use English !!");//영어만 쓰라고 hint 멘트
                input_chatting_content.startAnimation(wrongshake);//쉐이크 애니메이션 실시
                input_chatting_content.setHintTextColor(Color.parseColor("#FFF43434"));// 학생 이메일  힌트 부분의 색깔을  바꿔준다.
                input_chatting_content.setText("");//한글이 들어간 문장이므로  다시 강제로 리셋 시켜줌.

            }//한글 감지되었을때 끝.

          return null;
        }//문자열 필터 메소드 끝.

    };//edit_text입력어  필터링 끝.


    @Override
    protected void onPause() {
        super.onPause();

        //onpause에 하면  현재 엑티비티에서 pause상태로 스택에 넣고  다른 앱을 실행할때 ->  그 도중에 받은
        Log.v("check", getLocalClassName()+"의  onPuase() 실행됨/  소켓 disconnect진행");


        //pause에서는 무조건 service  실행을 멈춰야한다.
        //ServiceForGetChattingData 서비스가 실행중일때이다.
        if(isMyServiceRunning(ServiceForGetChattingData.class)){

            Log.v("check", getLocalClassName()+"에서 ServiceForGetChattingData가 실행중이어서  stopService 실행함");

            //채팅 내용 받는 백그라운드 서비스  멈추게 한다.
            Intent stop_backgroun_chatting_service=new Intent(ChattingRoomActivityForStudent.this,ServiceForGetChattingData.class);
            stopService(stop_backgroun_chatting_service);

        }//ServiceForGetChattingData 서비스가 실행중일때 조건 끝

        socket.disconnect();//socket연결을 끊는다.- (채팅방 소통용 소켓)

        //12-1
        //여기서는 현재 채팅방의  소켓이 연결이 끝난 상태이므로,  다른 방들의  소켓을  서비스를 통해서 다시 연결 시켜준다. -> 위에 onResume에서
        //서비스 끊었으므로,  여기서 다시  실행시켜주면 됨.
        //여기는 거의 마지막 부분이서서 -> 해당방으 joinedornot부분이 해결되었을듯함.
        if(isMyServiceRunning(ServiceForGetChattingData.class)){

            Log.v("check", getLocalClassName()+"의 12-1 에서 ServiceForGetChattingData가 실행중이어서  stopService 실행함");

        }else{//채팅 내용 받는 서비스가 실행중이 아닐때 조건

            //채팅 백그라운드 받는  서비스(ServiceForGetChattingData) 를 실행시켜준다.
            Intent start_chatting_background_service=new Intent(ChattingRoomActivityForStudent.this,ServiceForGetChattingData.class);
            startService(start_chatting_background_service);//서비스 실행시킴.
            Log.v("check", getLocalClassName()+"의 12-1 에서 ServiceForGetChattingData가 실행중이지 않아서 서비스 실행함");

        }//서비스 멈춰있을때 조건 끝.

        mSoftKeyboard.unRegisterSoftKeyboardCallback();//키보드 콜백 연결 끊어줌.


        count_check=0;//다시 0으로 돌려줌.

    }//onPause()끝




    //채팅 룸 정보 가저오기 위한 메소드 -> 해당 유저가 들어간 방번호를  파라미터로 받아 서버로 보낸다.
    private void getroominfo(final String roomnumber){

        Log.v("check", getLocalClassName()+"의 getroominfo() 함수 실행됨");

        Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create())//json으로 받고 싶으면 쓰면안됨..
                .build();//리트로핏 뷸딩
        ApiService apiService = retrofit.create(ApiService.class);//api인터페이스 크리에이트

        Call<GetRoomInfoClass> getroominfo=apiService.getchattingroominfo(roomnumber);//룸정보 얻기위한  call객체

        //룸 정보  콜백
        getroominfo.enqueue(new Callback<GetRoomInfoClass>() {
            @Override
            public void onResponse(Call<GetRoomInfoClass> call, Response<GetRoomInfoClass> response) {


                if (response.body() != null) {//받아온  response가 null이 아닐때

                    String namespace = response.body().getNamespace();//해당 룸이 오픈채팅인지 일대일 채팅인지 구별
                    String openchattingroomname = response.body().getRoomname();//오픈 채팅이므로  룸 디비안에 있는 룸 이름 테이블 받아옴.
                    chattingclientcount=response.body().getChatclientcount();//해당 채팅방  참여 인원  수
                    String teachername=response.body().getTeachername()+" teacher";//채팅방  선생님 이름 -> 선생님과 대화이므로 학생 입장에서는  teacher을  붙여준다.
                    String teacherment=response.body().getTeachername();

                    //만약에  서버에서  룸디비 정보를 제대로 가지고 오지 못한다면,
                    if(response.body().toString().equals("0")){
                        Log.v("check", getLocalClassName()+"의 room디비의  info를  제대로 가지고 오지 못함.");

                    }else if(response.body().toString().equals("1")){
                        Log.v("check", getLocalClassName()+"의 room 데이터 에서  선생님 이메일로  선생님 이름을  받아오는 과정에서  에러생김");

                    }else if(response.body().toString().equals("2")){
                        Log.v("check", getLocalClassName()+"의 room 데이터에서  1대1 채팅방일때  학생 uid로  학생이름   가져오는 것에서 에러생김.");

                    }else{//위  경우의  에러들이 없을 경우.

                        if (namespace.equals("0")) {//오픈 채팅방일때,

                            Log.v("check", getLocalClassName()+"의  room 정보  오픈 채팅방용으로 가져옴");

                            room_name_textview.setText(openchattingroomname);//오픈채팅방 이름 텍스트뷰에  ->  해당  이름 넣어줌.
                            chattingclientcount_tv.setVisibility(View.VISIBLE);


                            firsttimevisitornot(useruid, roomnumber,teacherment);//해당 유저가 처음참여인지 아닌지 여부 알아내기-> 오픈채팅방에 경우만 가능하다.(일대일은  클라이언트 숫자 필요 x)

                        } else if (namespace.equals("1")) {// 1대1 채팅방일때.
                            Log.v("check", getLocalClassName()+"의  room 정보  1대1 채팅방용으로 가져옴");


                            //1대1 채팅방에서는  상대의 이름이  채팅방 이름
                            room_name_textview.setText(teachername);

                            //해당 텍스트 사이즈
                            room_name_textview.setTextSize(21);

                            //1대1 채팅방의 경우는 -> 채팅방 참여  인원 수가 필요없다.
                            chattingclientcount_tv.setVisibility(View.INVISIBLE);

                        }//1대1 채팅방일떄 끝.
                    }//위  echo 0,1,2 에러들이 없을 경우.
                }//ressponse.body() 가  null이 아닐경우 끝.


                //이붑분에서는 현재 메소드 맨끔에 놓으면 ,  onresponse부분은  비동기로  작동해서 ->  response가  다 실행되지 않음에도 ->  해당  서비스가 실행이 먼저가 실행되는 경우가 생김
                //그부분을 해결 해주기 위해 onResponse와  onFailure 아래에다가 하나씩 넣어줌
                //솔직히 ->  chattingroomactivityforstudent부분에서는  빠르게 response과정이 끝나서 상관 없었지만,  혹시 모르는 사태와    선생님 부분에서  이와 관련된
                //값을  늦게 받는 경우가 생겨서  이렇게 선생님 쪽이랑  같이  코드 위치를  바꿔줌.//매소드 끝에서 -> response안 끝으로


                //12-1
                //여기서는 현재 채팅방의  소켓이 연결이 끝난 상태이므로,  다른 방들의  소켓을  서비스를 통해서 다시 연결 시켜준다. -> 위에 onResume에서
                //서비스 끊었으므로,  여기서 다시  실행시켜주면 됨.
                //여기는 거의 마지막 부분이서서 -> 해당방으 joinedornot부분이 해결되었을듯함.
                if(isMyServiceRunning(ServiceForGetChattingData.class)){

                    Log.v("check", getLocalClassName()+"의 12-1 에서 ServiceForGetChattingData가 실행중이어서  stopService 실행함");

                }else{//채팅 내용 받는 서비스가 실행중이 아닐때 조건

                    //채팅 백그라운드 받는  서비스(ServiceForGetChattingData) 를 실행시켜준다.
                    Intent start_chatting_background_service=new Intent(ChattingRoomActivityForStudent.this,ServiceForGetChattingData.class);
                    startService(start_chatting_background_service);//서비스 실행시킴.
                    Log.v("check", getLocalClassName()+"의 12-1 에서 ServiceForGetChattingData가 실행중이지 않아서 서비스 실행함");

                }//서비스 멈춰있을때 조건 끝.

            }//onResponse()끝



            @Override
            public void onFailure(Call<GetRoomInfoClass> call, Throwable t) {
                Log.v("check", getLocalClassName()+"의 getroominfo()에서 방 정보 받아오는 중에 에러 생김 /에러 내용->"+t);



                //맨처음  1대1 채팅방  만들어서 선생님 이름을 받아올때는  ->  바로 받아오지를 못해서 에러가 나는경우가 남.
                //이때는 채팅방 입장전  받아온  선생님 이름을 넣어준다.

                //1대1  채팅방만  해놓은 이유는??
                //이유는  오픈채팅방에 경우는  미리 만들어져 있어서  기존에 room디비 조회시  바로  room이름을 가지고 올수 있다
                //하지만,  1대1 채팅방의 경우는 학생이  채팅방을  구성할때 생성되므로 현재 getroominfo를  실행하면 해당방 정보가 null로 나와서 에러가 뜬다.
                //이경우  ->  이전  엑티비티에서 보낸 선생님 이름 intent를 사용한다.
                room_name_textview.setText(teachername+" teacher");
                room_name_textview.setTextSize(21);
                chattingclientcount_tv.setVisibility(View.INVISIBLE);

                //실패해도 -> 서비스는 다시  작동 시켜야하기 때문에...
                //12-1
                //여기서는 현재 채팅방의  소켓이 연결이 끝난 상태이므로,  다른 방들의  소켓을  서비스를 통해서 다시 연결 시켜준다. -> 위에 onResume에서
                //서비스 끊었으므로,  여기서 다시  실행시켜주면 됨.
                //여기는 거의 마지막 부분이서서 -> 해당방으 joinedornot부분이 해결되었을듯함.
                if(isMyServiceRunning(ServiceForGetChattingData.class)){

                    Log.v("check", getLocalClassName()+"의 12-1 에서 ServiceForGetChattingData가 실행중이어서  stopService 실행함");

                }else{//채팅 내용 받는 서비스가 실행중이 아닐때 조건

                    //채팅 백그라운드 받는  서비스(ServiceForGetChattingData) 를 실행시켜준다.
                    Intent start_chatting_background_service=new Intent(ChattingRoomActivityForStudent.this,ServiceForGetChattingData.class);
                    startService(start_chatting_background_service);//서비스 실행시킴.
                    Log.v("check", getLocalClassName()+"의 12-1 에서 ServiceForGetChattingData가 실행중이지 않아서 서비스 실행함");

                }//서비스 멈춰있을때 조건 끝.

            }//onFailure () 끝
        });//룸정보 콜백  끝

    }//getroominfo() 끝



    //드로워 네비게시션에서 ->  현재 채팅방에   가장 최근  이미지 비디오 (thumbnail) 파일  4개를   가지고 와서 보여준다.
    //이미지가  부족할경우->  리사이클러뷰는 만들어지지 않는다.
    //최대 4개까지  0부터  4까지 아이ㅌ템 유동적 생성 한다.
    private  void get_recent_four_meida_file_info(String databasename, String roomnumber,ImageView getShow_recent_mediafile1,ImageView getShow_recent_mediafile2, ImageView getShow_recent_mediafile3,ImageView getShow_recent_mediafile4){

        //sqlite에서  읽지 않은 메세지로 분류된  채팅 메세지들을 가지고 온다
        SqLiteOpenHelperClass GetsqLite_chatting_Database = new SqLiteOpenHelperClass (ChattingRoomActivityForStudent.this,databasename , null, 1);
        SQLiteDatabase Get_saved_database = GetsqLite_chatting_Database.getReadableDatabase();//데이터 베이스 읽기로  데이터에 접근.
        String sql = "select * from chatting_data_store where roomnumber='"+roomnumber+"' and viewtype=4 or roomnumber='"+roomnumber+"'and viewtype=5";//chatting_data_store테이블 에서 해당방 vietype 4(이미지,  또는


        Cursor cursor=Get_saved_database.rawQuery(sql,null);///위 쿼리문  데이터베이스로 보냄.


        cursor.moveToLast();//커서를 맨 마지막으로 보내고,
        cursor.moveToPosition(cursor.getPosition()+1);//맨 마지막에서  +1 을 함,

        //이렇게 하면,  cursor.moveToPrevious를 할때  -> 맨처음에  맨마지막  포지션 부터 이전으로 차례차례 조회 가능하다.
        while (cursor.moveToPrevious()){//가지고온  쿼리 목록을 담은  cursor를  while문으로 하나씩 돌림

            String mediafile_server_loaction=cursor.getString(11);//서버에 미디어 파일의 위치
            URL url_for_image = null;
            try {
                url_for_image = new URL("http://13.209.249.1/" +mediafile_server_loaction);//미디어 파일  전체 url

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            //영상 이미지,모두 glide로  처리가됨
            if (!hasImage(getShow_recent_mediafile1) ) {//1번 이미지 칸에  이미지가  없을 경우

                getShow_recent_mediafile1.setVisibility(View.VISIBLE);
                Glide.with(ChattingRoomActivityForStudent.this).load(url_for_image).placeholder(R.drawable.img_error)
                        .into(getShow_recent_mediafile1);//해당 프로필 이미지 서버에서 받아와  넣어줌.

            } else if (!hasImage(getShow_recent_mediafile2)) {//2번 이미지 칸에  이미지가 없을 경우

                getShow_recent_mediafile2.setVisibility(View.VISIBLE);
                Glide.with(ChattingRoomActivityForStudent.this).load(url_for_image).placeholder(R.drawable.img_error)
                        .into(getShow_recent_mediafile2);//해당 프로필 이미지 서버에서 받아와  넣어줌.

            } else if (!hasImage(getShow_recent_mediafile3)) {//3번 이미지 칸에 이미지가 없을 경우


                getShow_recent_mediafile3.setVisibility(View.VISIBLE);
                Glide.with(ChattingRoomActivityForStudent.this).load(url_for_image).placeholder(R.drawable.img_error)
                        .into(getShow_recent_mediafile3);//해당 프로필 이미지 서버에서 받아와  넣어줌.

            } else if (!hasImage(getShow_recent_mediafile4)) {//4번 이미지 칸에  이미지가 없을 겨우.

                getShow_recent_mediafile4.setVisibility(View.VISIBLE);
                Glide.with(ChattingRoomActivityForStudent.this).load(url_for_image).placeholder(R.drawable.img_error)
                        .into(getShow_recent_mediafile4);//해당 프로필 이미지 서버에서 받아와  넣어줌.

            }

        }//cursor가 null이 아닐때 ->  cursor  movettonext로 while문 끝.

        cursor.close();//cursor 닫아줌.

    }//get_recenet_four_media_file_info()끝


    //이미지  가지고 있는지 여부  판단.
    private boolean hasImage(@NonNull ImageView view) {
        //뷰의   drawable 을  가지고옴,
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);// drawable이 null이면  -> false 아니면  true

        if (hasImage && (drawable instanceof BitmapDrawable)) {//true( null이고) drwable이  비트맵일때-> 이러면  -placeholder는  안쳐줌.
            hasImage = ((BitmapDrawable)drawable).getBitmap() != null;//bitmap인  drawable만 가지고  판단  ->  값 return함.
        }

        return hasImage;
    }


    //해당 학생이 오픈 채팅방에 들어와서   연결이 되어있는지 안되어있는지 여부확인
    //안되어있으면,  처음  들어오는  client 이므로,  해당 채팅방  참여 인원수를 1올려준다.
    private void firsttimevisitornot(final String useruid, final String chattingroomnumber, final String teachername_forment){

       Log.v("check", getLocalClassName()+"의 firsttimevisitornot() 시작됨");


        Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create())//json으로 받고 싶으면 쓰면안됨..
                .build();//리트로핏 뷸딩
        ApiService apiService = retrofit.create(ApiService.class);//api인터페이스 크리에이트

        RequestBody studentuid=RequestBody.create(MediaType.parse("text/plain"), useruid);//서버로 보내  학생 uid
        RequestBody roomnumber=RequestBody.create(MediaType.parse("text/plain"), chattingroomnumber);//서버로 보낼  현재 학생이 들어간  채팅룸 번호

        Call<ResponseBody> check_chatuser_con_info =apiService.check_chatuser_con_info(studentuid,roomnumber);//학생정보 얻기위한  call객체

        check_chatuser_con_info.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.body() != null) {


                    try {
                        String responsevalue=  response.body().string();//response값  String 변수에 넣어줌.//  responsebody는 한번  말하면 사라지기때문

                        Log.v("check값알아내자", responsevalue);

                        if(responsevalue.equals("1")){//해당 방과  유저의 연결  기록이 있는 경우이다.

                            Log.v("check", getLocalClassName()+"의 firsttimevisitornot()의  response값 ->  기존 방 참여자임");

                            chattingclientcount_tv.setText("("+chattingclientcount+")");//숫자 보여지는텍스트뷰에  client 숫자 담음.



                        }else  if(responsevalue.equals("2")){//해당방에  유저와  연결 기록이 없는 경우이다. -> 이경우에는 해당 유저가 현재 방에 새로 온것이므로  카운트를 +1 해준다.



                            Log.v("check", getLocalClassName()+"의 firsttimevisitornot()의  response값 ->  새로운 방 참여자임");

                            chattingclientcount=chattingclientcount+1;//처음 참여 한것이므로  해당 채팅방에  클라이언트 수 +1해줌,
                            chattingclientcount_tv.setText("("+chattingclientcount+")");


                            SimpleDateFormat entrant_date = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss");//방들어왔을때 시간 형식
                            Calendar calendar=Calendar.getInstance();//현재 날짜 및 시간 받아옴.
                            String time=entrant_date.format(calendar.getTime());//스트링 으로 date변환해줌.


                            //처음 들어왓을때  보여주는 멘트
                            String ment="Welcome to "+teachername_forment+"''s OpenChatting Room!!\n\nPlease Write only English \nin this Chatting Room!! :)";

                            //json에  처음 들어온 기록 담기
                            JSONObject jsonObject=new JSONObject();

                            jsonObject.put("id", useruid);//유저uid
                            jsonObject.put("roomnamespace", 0);//오픈 채팅방에서만  처음 들어온것을 보여주므로 ->0
                            jsonObject.put("roomnumber", chattingroomnumber);//채팅방 번호
                            jsonObject.put("teachername","0");//유저 이름 필요없음
                            jsonObject.put("userposition", "s");//유저 포지션은 학생임.-> 선생님은 항상 있음.
                            jsonObject.put("name","0");//유저 이름
                            jsonObject.put("profile", "0");//유저 프로필
                            jsonObject.put("viewtype",0);//뷰타입 0
                            jsonObject.put("date",time);//현재 시간
                            jsonObject.put("chatorder",1);//처음 들어오면 그순간 부터 멘트가 1이므로,  1로 정해준다.
                            jsonObject.put("message", ment);//유저  처음 들어옴 알리는 멘트
                            Log.v("check",getLocalClassName()+"의  처음 들어왔을때 메세지 -> " +jsonObject.toString());
                            save_chatting_data_in_sql(jsonObject,sqLiteOpenHelper,database);//채팅내용 sqlite에  넣어줌.

                            chattingdata.add(0,jsonObject);//위 데이터 채팅 데이터 어레이에 넣어줌



                            chatting_Data_RecyclerView_Adapter.notifyDataSetChanged();//리사이클러뷰 어뎁터 다시 데이터 체인지 하게  ㄱ해줌.


                             Log.v("check", getLocalClassName()+"의  내가 처음 들어갔을때  나오는  첫 채팅 데이터 -> "+chattingdata.toString());

                        }else if(responsevalue.equals("3")){//chatuserconinfo 데이터를 조회하는 부분에서 에러가난 경우,

                            Log.v("check", getLocalClassName()+"의 firsttimevisitornot()의  response값 ->  쿼리문 에러남,");
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }//response가  null값이 아닐때

            }//onResponse() 끝


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            Log.v("check", getLocalClassName()+"의 firsttimevisitornot()에서  에러 걸림-> "+t.toString());


            }//onFailure()끝
        });//callback()
    }//firsttimevistiornot()끝


    private void save_chatting_data_in_sql(JSONObject receivedata, SqLiteOpenHelperClass sqLiteOpenHelper, SQLiteDatabase database) {

       try {
           //보낸 사람의  uid
           String senderuid = receivedata.getString("id");

           //오픈 채팅방 =0 or 1대1 =1   여부
           String roomnamespace = receivedata.getString("roomnamespace");

           //채팅방 번호.
           String roomnumber = receivedata.getString("roomnumber");

           //viewtype에 따라  메세지를 분류하고, 그에 맞는 처리를 해준다.
           String viewtype = receivedata.getString("viewtype");

           //선생님 이름 - 오픈 채팅방에서만 사용됨.
           String teachername=null;

           if(roomnamespace.equals("0")){//오픈 채팅방일때 jsonobject에 들어있는 값 그대로 "teachername" 쓰면됨

               //여기서는 오픈 채팅방일때 -> 해당 뷰타입 -> 나가는것 들어오는것  그냥 채팅 메세지  에따라서  처리가 달라진다.
               //viewtype 3 의 경우 teachername이  json데이터 에  포함되어있으므로, teachername을 받아야 하지만,
               //나머지 나가거나 들어오는 viewtype(1, 2)들은  ->  teachername이  데이터에 포함되어있지 않아서  새로 쓰레기값을 넣은  string변수를 만들어줬다.

               if(viewtype.equals("1")||viewtype.equals("2")){//나가기 또는 들어오기 메세지 타입

                   teachername="1";//teachername이 필요x 이므로, 쓰레기값을 1넣어줌.

               }else if(viewtype.equals("3")){//채팅메세지 타입일때.

                   teachername=receivedata.getString("teachername");
               }


           }else if(roomnamespace.equals("1")){//1대1 채팅방일때 -> 이때는  teachername이  jsononject에 들어있지 않다. // 그래도 값을  너줘야  query 를 날리고 에러가 안난다.
               teachername="1";//값을 1넣어줌.
           }

           //보낸사람의 position ( t or  s)
           String senderposition = receivedata.getString("userposition");

           //보낸 사람의 이름.
           String sendername = receivedata.getString("name");

           //보낸 사람의  프로필 이미지
           String profile = receivedata.getString("profile");


           //메세지  보낸 날짜
           String date = receivedata.getString("date");

           //해당 채팅방에서 현재 채팅의 순서.
           String chatting_order = receivedata.getString("chatorder");

           //해당 채팅의 메세지 내용
           String chatting_message = receivedata.getString("message");

           int read_or_not = 0;//unread이다.  read는  0임.

           //백그라운드에서 받은 채팅 데이터  sqlite에  넣어줌.
           sqLiteOpenHelper.inserChattingData(database, senderuid, roomnamespace, roomnumber, teachername, senderposition, sendername, profile, viewtype, date, chatting_order, chatting_message, read_or_not);

       }catch (JSONException e){

           e.printStackTrace();

       }

    }


    //채팅 데이터 가져오는 메소드 - 파라미터로  해당 방 번호 와  유저의 uid를 가져감.
    private  void select_chatting_data(String roomnumber_to_get,String useruid){


        Log.v("check", getLocalClassName()+"의 select_chatting_data() 실행됨");



        //기존 채팅방에서  저장해두었던  데이터들을  가지고와서  뿌려준다. -> 현재방 기준의  데이터들을  가져와서 보내준다
        SqLiteOpenHelperClass GetsqLite_chatting_Database = new SqLiteOpenHelperClass (ChattingRoomActivityForStudent.this,loginid_for_sql , null, 1);
        SQLiteDatabase Get_saved_database = GetsqLite_chatting_Database.getReadableDatabase();
        String sql = "select * from chatting_data_store";//chatting_data_store테이블  내용 조회

        Cursor cursor=Get_saved_database.rawQuery(sql,null);///위 쿼리문  데이터베이스로 보냄.

        //새롭게 가져오는 것이므로,  기존 chattingdata clear시켜줌.
        chattingdata.clear();


        //recyclerview -> useruid 필요해서   여기다  넣어줌.
        chatting_Data_RecyclerView_Adapter=new ChattingDataRecyclerViewAdapter(chattingdata,ChattingRoomActivityForStudent.this,useruid,"s",activity);
        chatting_Recyclerview_manager=new LinearLayoutManager(ChattingRoomActivityForStudent.this,RecyclerView.VERTICAL,false);
        ((LinearLayoutManager) chatting_Recyclerview_manager).setReverseLayout(false);
        ((LinearLayoutManager) chatting_Recyclerview_manager).setStackFromEnd(false);

        chatting_recyclerview.setLayoutManager(chatting_Recyclerview_manager);
        chatting_recyclerview.setAdapter(chatting_Data_RecyclerView_Adapter);

        ((SimpleItemAnimator) chatting_recyclerview.getItemAnimator()).setSupportsChangeAnimations(false);//리사이클러뷰에  변경 될때 진행되는 애니메이션  false값 넣어줌.
        chatting_recyclerview.setNestedScrollingEnabled(false);//스크롤 부드럽게

        //select쿼리 로 가지고 온 값들-> while문으로 돌려서
        //하나 하나씩 ->chattingdata array에 닮음.
        while (cursor !=null && cursor.moveToNext()){


            try {

                if(cursor.getString(3).equals(roomnumber_to_get)) {//이때 roomnumber가  -> 현재 채팅방  roomnumber와 같은 채팅데이터만 jSONOnject에 넣어준다

                    //jsonObject 각 while문 cycle마다  새롭게
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id", cursor.getString(1));
                    jsonObject.put("roomnamespace", Integer.parseInt(cursor.getString(2)));
                    jsonObject.put("roomnumber", cursor.getString(3));
                    jsonObject.put("teachername", cursor.getString(4));
                    jsonObject.put("userposition", cursor.getString(5));
                    jsonObject.put("name", cursor.getString(6));
                    jsonObject.put("profile", cursor.getString(7));
                    jsonObject.put("viewtype", Integer.parseInt(cursor.getString(8)));
                    jsonObject.put("date", cursor.getString(9));
                    jsonObject.put("chatorder", Integer.parseInt(cursor.getString(10)));
                    jsonObject.put("message", cursor.getString(11));

                    //해당값을  JSONObject에 담았으면,  이제  recyclerview로  보내지는 채팅데이터 array에 넣어준다.
                    chattingdata.add(jsonObject);
                }//roomnumber가  맞는  채팅데이터 일때.

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }//while문 끝



        //sharedpreference에 저장된 scroll postion가지고 오기
        SharedPreferences getscrollposition=ChattingRoomActivityForStudent.this.getSharedPreferences("saverecyclerviewposition", Context.MODE_PRIVATE);
        int scrollpositon=getscrollposition.getInt("scrollposition", -12);
        SharedPreferences.Editor delete_scrollposition_edit=getscrollposition.edit();
        delete_scrollposition_edit.clear().apply();//해당 쉐어드에 저장된 리사이클러뷰 포지션 없애줌.


        Log.v("checkscorll", getLocalClassName()+"에서  스크롤값  보여줌. ->"+scrollpositon);

        //스크롤 값이  -1( default값) 이 아닐경우
        if(scrollpositon != -12){

            Log.v("checkscorll", getLocalClassName()+"에서  스크롤 이동");


            chatting_recyclerview.scrollToPosition(scrollpositon-1);//리사이클러뷰 -> 포지션 옮겨줌.

        }else{

            Log.v("checkscorll", getLocalClassName()+"에서  스크롤 이동1");

            chatting_Data_RecyclerView_Adapter.notifyDataSetChanged();//리사이클러뷰 어뎁터 다시 데이터 체인지 하게  ㄱ해줌.
            chatting_recyclerview.scrollToPosition(chattingdata.size()-1);

            //스크롤 값  -1 아닐 경우 끝.
        }


    }//select_chatting_data() 끝



}//ChattingRoomActivityForStudnet 클래스 끝
