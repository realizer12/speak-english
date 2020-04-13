package com.example.leedonghun.speakenglish;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Manager;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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
 * Class: ChattingRoomActivity.
 * Created by leedonghun.
 * Created On 2019-10-30.
 * Description:실제  채팅이 진행되는 채팅방이다.
 * 이곳에 들어오면, 소켓이  서버  와 연결되어
 * 새로운  소켓이  생성되고
 * tcp/ip통신이  진행된다
 * 여기에서는  이미지와  비디오를  받아서 올려준다.
 *
 *
 * 나는  채팅방을  선생님 쪽에서 들어갈때랑  학생이  들어갈때를  나누었따
 * 그게 만들어야되는 코드는  더 많아지지만,  각각  필요 기능에 따라  나눠줄수 있어서
 * 그렇게 하기로 하였다.
 * 그예로 선생님 부분에서는  채팅방 나가기 기능은 주어지지않는다.
 */

public class ChattingRoomActivityForTeacher extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    int count_check=0;//sqlite로  채팅방 이미지 가지고 올떄,  -> socket connection부분에다가 위치하게 해놔서
    //다른  엑티비티 갔다가 돌아와도  다시 조회해옴 여기서 ->  기존  이미지  4개가  다 안차있으면,  똑같은걸  다음  이미지뷰에다가 넣어주는 현상나와서
    //oncreate에서만  가지고 오게  check 변수를 만듬.  -> checkcount는  oncreate가 실행되었을때만  1로 변하고,pause를 호츨히면   0이다.-> 0때는  다시 sqlite문에서 미디어 파일들을 조회하지 않는다.


    static final int IMAGE_FILTERING_REQUEST=101;//이미지 필터링 startactivityforresult에서 사용될   request값.

    EditText input_chatting_content;//채팅 edittext /1-1
    Button   send_chatting_btn;//채팅 보내기 버튼 /1-2
    ImageButton get_chatting_file_btn;//사진 영상 등 파일 가져오기 위한 버튼/1-3
    Toolbar chatting_room_toolbar;//채팅방에서  나가기 밑  뒤로가기 버튼이 들어갈  툴바이다. //1-4


    ChattingDataRecyclerViewAdapter chatting_Data_RecyclerView_Adapter;//리사이클러뷰 어뎁터
    RecyclerView.LayoutManager chatting_Recyclerview_manager;//리사이클러뷰 매니져
    RecyclerView chatting_recyclerview;//채팅 내용이  보여질  리사이클러뷰 /2-2


    TextView room_name_textview;//현재 채팅방의 이름이 들어간다.//2-3

    InputMethodManager managerforkeyboardaction;//키보드 관련/2-5


    Activity thisactivity;//현재 엑티비티 넣어주기위한 엑티비티 객체

    Socket socket;//자바  서버 연결 위한 소켓
    ArrayList<JSONObject> chattingdata;// json으로 만들어진 chatting 데이터가  담길  어레이 리스트

    String useremail;//선생님 이메일//2-1
    String teacheruid;//선생님 방번호// 선생님 방번호의 경우 선생님 uid가  방번호가 된다.

    String iport="http://13.209.249.1:8888";//ip 연결

    int chattingroomtype;//현재 채팅방의  타입을  받는 변수 -> 오픈 채팅방인지 일대일  채팅방인지.


    String chattingroomnumber;//연결할  채팅방 번호 들어갈  변수->  오픈채팅일때는 -> 선생님 uid,  일대일 채팅일때는 학생 uid+ 선생님 uid가 들어감.//5-5
    TextView chattingclientcount_tv;// 채팅방 참여 인원이  들어가는  텍스트뷰 / 2명이상일때부터 숫자가 보이도록 설정한다.//5-6

    Softkeyboard mSoftKeyboard;//키보드 변환  감지용  객체
    ConstraintLayout basic_layout;//현재 엑티비티 전체 레이아웃.

    DrawerLayout drawer;//드로워 레이아웃 객체

    int chattingclientcount;//채팅방 참여인원 숫자

    String messagecontent_from_edittext;//채팅내용 적히는 에딧텍스트 내용 -> 스트링에 담음.

    String roomnumber;//해당 방의  룸넘버-> 1대1 채팅방일때는  넘어옴.


    SqLiteOpenHelperClass sqLiteOpenHelper;//sqlite 생성하는 클래스
    SQLiteDatabase database;//sqlite 데이터베이스

    String loginid_for_sql;//채팅 저장 데이터 베이스는  로그인 아이디로 -> @를 빼서  사용한다.




     /** 네비게이션에 들어가는 뷰 **/

     TextView videos_and_photos_textview; //해당방   사진이랑  동영상 모아 놓은  엑티비티로 가는 버튼 용 텍스트뷰   3-1
     ImageView imageView_for_room_profile; //해당방  룸 사진  들어가는 이미지뷰 -> 클릭시  이미지  change가 가능함. 3-2

     TextView textview_for_show_user_total_count; // 해당방  참여 인원의 전체 숫자를 보여주는  텍스트뷰 3-2-1

     LinearLayout container_for_roomimage; //해당방  룸  이미지 들어가는  리니어 레이아웃 -> 오픈 채팅방 일때는 visible이고  1대1 채팅방일때는  gone상태로 한다.  3-3

     //최근 -> 추가된  미디어 파일 (비디오,  이미지) 파일  들어가는  이미지뷰들
     //리사이클러뷰로 추가 하려다가  4개 고장이고 많지 않아서 -> 이미지뷰로 하나씩 넣어주기로함
     ImageView getShow_recent_mediafile1; //4-1
     ImageView getShow_recent_mediafile2; //4-2
     ImageView getShow_recent_mediafile3; //4-3
     ImageView getShow_recent_mediafile4; //4-4

     RecyclerView recyclerView_for_put_room_members;//  룸에  참여중인 멤버들  리스트가 나열되는 리사이클러뷰 3-5

    ChattingMembersListRecyclerViewadapter chattingMembersListRecyclerViewadapter;//채팅 멤버  리사이클러뷰 어뎁터

    LinearLayoutManager chattingmemberlistmanager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatting_drawable_navigation_for_teacher);

        Log.v("check", getLocalClassName()+"의  onCreate() 실행됨");



         //선생님 이메일 쉐어드에  넣기.
         final SharedPreferences getid = ChattingRoomActivityForTeacher.this.getSharedPreferences("loginteacherid",MODE_PRIVATE);//선생님 로그인 아이디  쉐어드에 담긴거 가져옴
         useremail= getid.getString("loginidteacher","");//선생님 로그인 아이디 가져옴//2-1

         input_chatting_content=findViewById(R.id.edittextforchat);//1-1
         send_chatting_btn=findViewById(R.id.btnforsendchat);//1-2
         get_chatting_file_btn=findViewById(R.id.btnforsendfile);//1-3
         chatting_recyclerview=findViewById(R.id.showingchatrecyvlerview);//2-2
         chatting_room_toolbar=findViewById(R.id.toolbarforchattingroom);//1-4
         room_name_textview=findViewById(R.id.room_name_text);//2-3

         chattingclientcount_tv=findViewById(R.id.chatclientcounttextview);//5-6

         thisactivity=ChattingRoomActivityForTeacher.this;//현재 엑티비티 넣어줌.

        videos_and_photos_textview=findViewById(R.id.text_view_for_show_room_files);//3-1
        imageView_for_room_profile=findViewById(R.id.imageview_for_room_profileimg);//3-2
        container_for_roomimage=findViewById(R.id.contatiner_for_roomimage);//3-3


        recyclerView_for_put_room_members=findViewById(R.id.recyclerview_for_show_room_members);//3-5

        textview_for_show_user_total_count=findViewById(R.id.textview_for_show_user_total_count);//3-2-1

        getShow_recent_mediafile1=findViewById(R.id.show_recent_file1);//4-1
        getShow_recent_mediafile2=findViewById(R.id.show_recent_file2);//4-2
        getShow_recent_mediafile3=findViewById(R.id.show_recent_file3);//4-3
        getShow_recent_mediafile4=findViewById(R.id.show_recent_file4);//4-4


        //이전  채팅룸 리스트에서 intent로 보낸 데이터를  받아오기 위한  getintent():
        Intent getintent=getIntent();


        //채팅방 이름이 담길  스트링변수에  해당 채팅방이름을 넘겨준다.
        String room_name=getintent.getStringExtra("RoomName");

        //해당  룸넘버로  들어감.
        roomnumber=getintent.getStringExtra("Roomnumber");


        //유저로부터 intent로 받은  채팅방  type -> 0 =오픈채팅,   1=  1대1채팅
        chattingroomtype=getintent.getIntExtra("chattingroomtype", -1);

        loginid_for_sql =useremail.replaceAll("@", "");//sqlite 데이터베이스 이름 -> 현재유저의  이메일에 @를 뺀 혀애
        sqLiteOpenHelper=new SqLiteOpenHelperClass(ChattingRoomActivityForTeacher.this,loginid_for_sql , null,1 );
        database=sqLiteOpenHelper.getWritableDatabase();

        chattingdata=new ArrayList<>();//6-6

        //가지고온  채팅방 이름을  채팅룸  이름 들어가는 textview에 넣어줌.
        room_name_textview.setText(room_name);


        //보내기 버튼이 눌렸을때
        send_chatting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //스트링 ->  키보드로 input된  채팅 내용  String 으로 받아옴.
                String chattingcontent= input_chatting_content.getText().toString();
                socket.emit("message",chattingcontent);
                input_chatting_content.setText("");// 내용 없애줌.

            }//onclick 끝
        });
        //보내기 버튼 눌렸을때 끝


         //현재 엑티비티 ->  툴바  부분  설정
         setSupportActionBar(chatting_room_toolbar);
         getSupportActionBar().setDisplayHomeAsUpEnabled(true);
         getSupportActionBar().setDisplayShowTitleEnabled(false);//엑션바에서 타이틀 안보이게함
         getSupportActionBar().setHomeAsUpIndicator(R.drawable.whiteback);




        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_teacher_chatting);//드로워 안에  넣어두었던  네비게이션 뷰 선언
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_chatting_for_teacher);//드로어 레이아웃 선언

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

            }//onDrawerOpenc() 끝



            @Override
            public void onDrawerClosed(View drawerView) {//드로워가  닫힐때
                Log.v("check", getLocalClassName()+"의 드로워 네비게이션 닫힘.");

                //드로워가 닫힐때에는 열렸을때  풀어져있던  락모드를  다시 걸어준다.
                //이렇게 하면 다시  처음처럼  스와이프로  드로워를 open할수 없다.
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);//드로어를   스와프 형태로  가지고 오지 못하고  코딩으로 (버튼 클릭)만으로 가져오도록 하기위한 코드

                super.onDrawerClosed(drawerView);

            }//onDrawerClosed() 끝


        });//드로워 리스너  끝

        //채팅 내용들어가는  edittext 부분에서 ->  필터링 효과  적용시킴
        //한글이 내용중에 들어가면  필터링 하게됨.
        input_chatting_content.setFilters(new InputFilter[]{filter});


        //아래는  네비게이션 뷰들 관련한 이벤트...........................................................................

        //채팅 타입이 오픈 채팅방일떄
        if(chattingroomtype==0){

            //오픈 채팅방일때는  -> 룸 이미지 관련  수정처리가 필요함으로  룸이미지가 담기 Linaer을 visible로 놓는다.
            container_for_roomimage.setVisibility(View.VISIBLE);

        }else if(chattingroomtype==1){// 채팅방이 일대일 채팅방일때.

            //일대일 채팅방의 경우 룸이미지가 따로 없으므로, 해당 Linearlayout을  gone처리 해준다.
            container_for_roomimage.setVisibility(View.GONE);

        }//채팅방 일대일 채팅방 일때 경우 끝





        //3-1 클릭이벤트 -> 현재 방에서 주고받은 파일들  목록 보기
        videos_and_photos_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("check", getLocalClassName() + "에서 videos_and_photos_textview 버튼 눌림-> 파일 리스트가 있는 엑티비티로 넘어감.");


                //미디어 파일들 모아둔  엑티비티로 넘어간다.
                Intent goto_show_chatting_files_activity = new Intent(ChattingRoomActivityForTeacher.this, ShowChattingFilesActivity.class);
                goto_show_chatting_files_activity.putExtra("databasename", loginid_for_sql);//데이터 베이스 이름
                goto_show_chatting_files_activity.putExtra("roomnumber", chattingroomnumber);//방 번호.


                //showChattingFilesActivity로 넘어간다.
                startActivity(goto_show_chatting_files_activity);

            }//onCLick() 끝
        });//3-1 클릭 끝.



        //3-2 해당방의  룸  프로필 이미지를 누르면 진행되는 이벤트 ->  이미지 수정을 가능하게 한다. / 오픈 채팅방일때만 보임.
        imageView_for_room_profile.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                Log.v("check", getLocalClassName()+"의  룸 이미지가 눌림");


                DialogInterface.OnClickListener cancelListner = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.v("check", getLocalClassName()+"에서  이미지 프로필 변경  다이얼로그 취소눌림");

                        dialogInterface.dismiss();///취소 버튼을 눌러서  다이얼로그 취소됨.

                    }//onClick()끝

                };//취소 시


                DialogInterface.OnClickListener yesbtn = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.v("check", getLocalClassName()+"에서  이미지 프로필 변경  다이얼로그 사진촬영 버튼 눌림");

                        // 오픈 채팅룸  이미지 고르고 필터링 적용시켜주는  클래스로 감.
                        Intent intent_to_go_room_profile_change=new Intent(ChattingRoomActivityForTeacher.this,ActivityForRoomImageChange.class);


                        //프로필 이미지뷰에 이미지가 올라가있다면 ->  비트맵 처리해서  필터링  엑티비티에  넘겨준다.
                        if(hasImage(imageView_for_room_profile)){


                            Bitmap bitmap_from_roomprofileimg = ((BitmapDrawable) imageView_for_room_profile.getDrawable()).getBitmap();
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap_from_roomprofileimg.compress(Bitmap.CompressFormat.JPEG, 30, stream);
                            //quality 부분에서 100에 놓으니  사진이  1mb를 넘길경우가 있고  1메가 바이트를 넘겼을때
                            //인텐트로 바이트 값을 보낼수 가 없는 현상이 생겻다.
                            //그래서  quality를 30으로  낮췄다.

                            byte[] byteArray_for_roomprofileimg = stream.toByteArray();

                            intent_to_go_room_profile_change.putExtra("profileroom_bitmap_byte", byteArray_for_roomprofileimg);//프로필 이미지byte어레이 넣어줌

                        }//프로필 이미지 오라가있있
                        else{

                            intent_to_go_room_profile_change.putExtra("profileroom_bitmap_byte", (byte[]) null);//프로필 이미지byte어레이 넣어줌

                        }


                        //채팅방 번호  같이 넘겨줌.
                        intent_to_go_room_profile_change.putExtra("roomnumber", chattingroomnumber);

                        //수정된 이미지 bitmap 받아옴.
                        startActivityForResult(intent_to_go_room_profile_change,IMAGE_FILTERING_REQUEST);//엑티비티 시작

                    }//onClic()끝

                };//룸 프로필  수정 하는 엑티비티로 넘어감,


                new AlertDialog.Builder(ChattingRoomActivityForTeacher.this)
                        .setTitle("Wanna change Room Image??")// 사진 촬영  다이얼로그  제목
                        .setCancelable(false)
                        .setNegativeButton("Yes", yesbtn)//프로필 이미지 변경 확인 버튼
                        .setPositiveButton("No", cancelListner)//프로필 이미지 변경 취소 버튼
                        .show();

            }


        });//3-2 클릭 끝.

        count_check=1;//onCREATE 경우 1로  놓아준다.


    }//oncreate() 끝



    //이미지 프로필  필터링  한 값  받아옴.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode==IMAGE_FILTERING_REQUEST){

            if(resultCode== Activity.RESULT_OK) {


                if (data != null) {
                    byte[] byteArray_for_roomprofileimg = data.getByteArrayExtra("filtered_image");
                    Log.v("checkgggggggg", String.valueOf(byteArray_for_roomprofileimg));
                    Bitmap roomimage = BitmapFactory.decodeByteArray(byteArray_for_roomprofileimg, 0, byteArray_for_roomprofileimg.length);//해당 바이트 비트맵으로 연결

                    imageView_for_room_profile.setBackground(null);

                    imageView_for_room_profile.setImageBitmap(roomimage);
                }

            }
        }


    }//onActivityResult() 끝



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

                    textview_for_show_user_total_count.setText("Chatting Members  ("+totalcount+")");


                    Log.v("check", getLocalClassName()+"의  "+chattingroomnumber+"방  user 리스트->"+user_array_list);
                    //recyclerview -> useruid 필요해서   여기다  넣어줌.


                    //해당 멤버 리스트 보여주는  리사이클러뷰 관련 코드
                    chattingMembersListRecyclerViewadapter=new ChattingMembersListRecyclerViewadapter(ChattingRoomActivityForTeacher.this,user_array_list,useruid) ;
                    chattingmemberlistmanager=new LinearLayoutManager(ChattingRoomActivityForTeacher.this,RecyclerView.VERTICAL,false);
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






    //채팅 방중에  오픈 채팅방의 경우  해당 이미지를 가지고 오기 위함이다.
    private void get_chatting_roomimage(ImageView chatting_profile_imageview,String roomnumber){

        Log.v("check", getLocalClassName()+"의  get_chattig_roomimage() 실행됨 ");

        Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create())//json으로 받고 싶으면 쓰면안됨..
                .build();//리트로핏 뷸딩
        ApiService apiService = retrofit.create(ApiService.class);//api인터페이스 크리에이트
        RequestBody chatting_roomnumber =RequestBody.create(MediaType.parse("text/plain"),roomnumber);//서버로 보내  roomnumber

        Call<ResponseBody>get_openchatting_imagepath=apiService.get_room_profile_image(chatting_roomnumber);


        get_openchatting_imagepath.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                     String image_path=response.body().string();

                     Log.v("check",getLocalClassName()+"의  룸 이미지 프로필 path->"+image_path);


                     if(image_path.equals("2")){// 이미지 path가  ""일때 ->default의 경우다.
                         Log.v("check", getLocalClassName()+"의 이미지 경로를 받아왔는데 '' 값인경우");


                     }else if(image_path.equals("-1")){//-1은 서버 코드에서  룸 이미지 조회시  나온 조회 실패 에러이다.
                         Log.v("check", getLocalClassName()+"의 이미지 경로를 서버에서  조회하다가 에러가남");



                     }else{//성공적으로 rommimage_profile을 가지고옴.

                      Log.v("check", getLocalClassName()+"의 이미지 경로 서버에서  성공적으로 받아옴.");


                     //전체 채팅방 프로필 이미지 path
                     String whole_image_path="http://13.209.249.1/"+image_path;

                     //위 string 으로 된  이미지 path ->  URL로  넘겨줌.
                     URL url_for_roomimage = new URL(whole_image_path);//미디어 파일  전체 url


                     chatting_profile_imageview.setBackground(null);// 프로필이미지  bacground에  아무것도 안남기기 위해서.

                     //프로필 이미지 넣어줌.
                     Glide.with(ChattingRoomActivityForTeacher.this).load(url_for_roomimage).placeholder(R.drawable.img_error)
                               .diskCacheStrategy(DiskCacheStrategy.NONE)//디스크 캐시 저장도 꺼줌. -> glide는  빠르게 이미지 로딩을 위해 url이 같으면, 캐시에 저장된 똑같은 이미지 그대로 넣어줌.
                               .skipMemoryCache(true)//캐시 저장 안함.  -> 이러니까 계속  서버 이미지 경로  같은데 내용물 바껴도  계속  예전꺼 나옴  그래서 캐쉬 없애줌.
                               .into(chatting_profile_imageview);//해당 프로필 이미지 서버에서 받아와  넣어줌.


                     }//성공적으로 roomimage_profile path 가지고 온 경우

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }//onResponse() 끝

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Log.v("check", getLocalClassName()+"의  get_chatting_roomimage실행하여 룸 프로필 이미지 경로 받는 중  에러+->"+t);



            }//onFailure() 끝

        });






    }//get_Chatting_Roomiamge() 끝




    //드로워 네비게시션에서 ->  현재 채팅방에   가장 최근  이미지 비디오 (thumbnail) 파일  4개를   가지고 와서 보여준다.
    //이미지가  부족할경우->  리사이클러뷰는 만들어지지 않는다.
    //최대 4개까지  0부터  4까지 아이ㅌ템 유동적 생성 한다.
    private  void get_recent_four_meida_file_info(String databasename, String roomnumber,ImageView getShow_recent_mediafile1,ImageView getShow_recent_mediafile2, ImageView getShow_recent_mediafile3,ImageView getShow_recent_mediafile4){

        //sqlite에서  읽지 않은 메세지로 분류된  채팅 메세지들을 가지고 온다
        SqLiteOpenHelperClass GetsqLite_chatting_Database = new SqLiteOpenHelperClass (ChattingRoomActivityForTeacher.this,databasename , null, 1);
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
                   if (!hasImage(getShow_recent_mediafile1)) {//1번 이미지 칸에  이미지가  없을 경우

                           getShow_recent_mediafile1.setVisibility(View.VISIBLE);
                           Glide.with(ChattingRoomActivityForTeacher.this).load(url_for_image).placeholder(R.drawable.img_error)
                               .into(getShow_recent_mediafile1);//해당 프로필 이미지 서버에서 받아와  넣어줌.

                   } else if (!hasImage(getShow_recent_mediafile2)) {//2번 이미지 칸에  이미지가 없을 경우

                       getShow_recent_mediafile2.setVisibility(View.VISIBLE);
                       Glide.with(ChattingRoomActivityForTeacher.this).load(url_for_image).placeholder(R.drawable.img_error)
                              .into(getShow_recent_mediafile2);//해당 프로필 이미지 서버에서 받아와  넣어줌.

                   } else if (!hasImage(getShow_recent_mediafile3)) {//3번 이미지 칸에 이미지가 없을 경우


                       getShow_recent_mediafile3.setVisibility(View.VISIBLE);
                       Glide.with(ChattingRoomActivityForTeacher.this).load(url_for_image).placeholder(R.drawable.img_error)
                              .into(getShow_recent_mediafile3);//해당 프로필 이미지 서버에서 받아와  넣어줌.

                   } else if (!hasImage(getShow_recent_mediafile4)) {//4번 이미지 칸에  이미지가 없을 겨우.

                       getShow_recent_mediafile4.setVisibility(View.VISIBLE);
                       Glide.with(ChattingRoomActivityForTeacher.this).load(url_for_image).placeholder(R.drawable.img_error)
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


    @SuppressLint("ClickableViewAccessibility")//리사이클러뷰 터치 리스너 ->  warning 해결 하기 위해서
    @Override
    protected void onResume() {
        super.onResume();

        //채팅 내용 받는 서비스가  실행중일때 조건이다.
        if(isMyServiceRunning(ServiceForGetChattingData.class)){

            Log.v("check", getLocalClassName()+"에서 ServiceForGetChattingData가 실행중이지만, onResume에서 stopService 멈춤");


            //서비스를  onResume에서  한번 멈춰준다.  이밎  실행 되어있는 상황에서 들어오니까 당연히 멈춰줘야됨.
            //채팅 내용 받는 백그라운드 서비스  멈추게 한다.
            Intent stop_backgroun_chatting_service=new Intent(ChattingRoomActivityForTeacher.this,ServiceForGetChattingData.class);
            stopService(stop_backgroun_chatting_service);


        }//서비스가 실행중일때 -> 해당  서비스를 한번 멈춰준다. -> 멈추는 이유는  해당 채팅방에서는 다른 소켓 연결이 시도되기 때문이다.
        //밑에서 -> 채팅방용 소켓 연결이 끝나면 그때  다시   서비스로 연결 시켜준다. -> 이렇게 하면 joinedornot의 값이 1인 값은  제외 되므로, 현재 채팅방에서는
        //채팅방용 소켓만  추가되고 서비스에선  제외된다. -> 바로가기 12-1


        getteacherinfo(useremail,chattingroomtype);// 선생님 이메일 정보를 서버로 보내서 선생님 정보를 받아옴,



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

    }//onResume 끝





    //10-1
    //처음에  소켓  오픈 채팅 connection이 되었을때.
    private Emitter.Listener onConnect=new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            String userposition="t";

            //처음에  소켓이 연결될때 ->   유저의 uid,  접속하려는  채팅방 룸 넘버 / user의 포지션 (학생 or 선생)
            socket.emit("join", teacheruid,chattingroomnumber,userposition,0);


        }
    };//socket connection 이벤트 끝.


    //10-1
    //처음에  소켓  1대1 채팅방에 connection이 되었을때.
    private Emitter.Listener onConnect_to_one_to_one_messenger=new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            String userposition="t";// 유저의 포지션


             //일대일 채팅방은 항상 -> 선생님 uid+ 학생 uid로  구성되어  있으므로,  해당 채팅방에서 ->  선생님 uid길이 만클을  substring해주면
             //선생님 uid만 빠진 학생 uid가  나온다. ->
             //ex) 방번호 25166 / 학생 uid-> 25.  선생 uid는  -> 166
             //이때 substring으로 학생 uid크기만큼 빼주면  학생uid크기는 2이기 때문에 2부터 시작되는  단어가 들어감.  딱  166부터만 나올수 있음.
            String studentuid= chattingroomnumber.substring(teacheruid.length());


            //처음에  소켓이 연결될때 ->   유저의 uid,  접속하려는  채팅방 룸 넘버 / user의 포지션 (학생 or 선생)
            socket.emit("join", teacheruid,chattingroomnumber,userposition,studentuid,0);


        }//call() 끝
    };//socket connection 이벤트 끝.



    //툴바에서  오른쪽에  추가되는  옵션 메뉴를 설정하는 메소드
    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        //커스톰한  옵셥 메뉴 버튼을 인플레이트 함.
        getMenuInflater().inflate(R.menu.chatting_toolbar_drawable_btn, menu);


        return super.onCreateOptionsMenu(menu);
    }//onCreateOptionMenu() 끝


    //채팅방에서 본 -> 모든 데이터들의  unread 컬럼의 값을  읽은 형태  0으로 만들어준다.
    private void make_chatting_data_read(String roomnumber,String databasename){

        Log.v("check", getLocalClassName()+"의  make_chatting_data_read() 실행됨");

        //현재까지 sqlite 디비에  저장되어있던  데이터들을  가지고  unreadcount -> 0(읽은 상태) 로  만들어줌.
        SqLiteOpenHelperClass update_chatting_database=new SqLiteOpenHelperClass(ChattingRoomActivityForTeacher.this,databasename,null,1);
        SQLiteDatabase get_database = update_chatting_database.getWritableDatabase();//writeable 하게 데이터베이스 접근


        String upadate_sql="UPDATE chatting_data_store SET readornot=0 WHERE roomnumber='"+roomnumber+"'";//해당방의  -> update쿼리문 날림 -> reaornot의 값을 0으로 바꿔줌.
        get_database.execSQL(upadate_sql);//업데이트 쿼리문 날림
        get_database.close();//데이터베이스 닫아줌.

    }//makechatting_data_read() 끝


    //해당방의 sqlite부분에서  unreadcount를 가지고 온다.
    private void refresh_server_unreadcount(String roomnumber,String databasename){

        ArrayList<JSONObject> arrayList=new ArrayList<>();

        //sqlite에서  읽지 않은 메세지로 분류된  채팅 메세지들을 가지고 온다
        SqLiteOpenHelperClass GetsqLite_chatting_Database = new SqLiteOpenHelperClass (ChattingRoomActivityForTeacher.this,databasename , null, 1);
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




    //채팅 데이터 가져오는 메소드  파라미터로  해당 방 번호 와  유저의 uid를 가져감.
    private  void select_chatting_data(String roomnumber_to_get,String useruid){


        Log.v("check", getLocalClassName()+"의 select_chatting_data() 실행됨");

        //기존 채팅방에서  저장해두었던  데이터들을  가지고와서  뿌려준다. -> 현재방 기준의  데이터들을  가져와서 보내준다
        SqLiteOpenHelperClass GetsqLite_chatting_Database = new SqLiteOpenHelperClass (ChattingRoomActivityForTeacher.this,loginid_for_sql , null, 1);
        SQLiteDatabase Get_saved_database = GetsqLite_chatting_Database.getReadableDatabase();
        String sql = "select * from chatting_data_store";//chatting_data_store테이블  내용 조회

        Cursor cursor=Get_saved_database.rawQuery(sql,null);///위 쿼리문  데이터베이스로 보냄.

        //새롭게 가져오는 것이므로,  기존 chattingdata clear시켜줌.
        chattingdata.clear();

        //recyclerview -> useruid 필요해서   여기다  넣어줌.
        chatting_Data_RecyclerView_Adapter=new ChattingDataRecyclerViewAdapter(chattingdata,ChattingRoomActivityForTeacher.this,useruid,"t",thisactivity);
        chatting_Recyclerview_manager=new LinearLayoutManager(ChattingRoomActivityForTeacher.this,RecyclerView.VERTICAL,false);
        ((LinearLayoutManager) chatting_Recyclerview_manager).setReverseLayout(false);
        ((LinearLayoutManager) chatting_Recyclerview_manager).setStackFromEnd(false);


        ((SimpleItemAnimator) chatting_recyclerview.getItemAnimator()).setSupportsChangeAnimations(false);//리사이클러뷰에  변경 될때 진행되는 애니메이션  false값 넣어줌.

        chatting_recyclerview.setLayoutManager(chatting_Recyclerview_manager);
        chatting_recyclerview.setAdapter(chatting_Data_RecyclerView_Adapter);


        chatting_recyclerview.setNestedScrollingEnabled(false);
       // chatting_recyclerview.scrollToPosition(chattingdata.size()-1);


        //select쿼리 로 가지고 온 값들-> while문으로 돌려서
        //하나 하나씩 ->chattingdata array에 닮음.
        while (cursor !=null && cursor.moveToNext()){


                try {

                    if(cursor.getString(3).equals(roomnumber_to_get)) {//이때 roomnumber가  -> 현재 채팅방  roomnumber와 같은 채팅데이터만 jSONOnject에 넣어준다

                        //jsonObject 각 while문 cycle마다  새롭게->sqlite 데이터들  가지고 jsonobject화 시켜줌.
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


        }

        //sharedpreference에 저장된 scroll postion가지고 오기
        SharedPreferences getscrollposition=ChattingRoomActivityForTeacher.this.getSharedPreferences("saverecyclerviewposition", Context.MODE_PRIVATE);
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


    //edditext로  focus되어   키보드가 올라왔을때,  ->  edittext이외의 역역 즉  전체 레이아웃 영역이  터치되면 다시 사라지도록 만듬 / 카카오톡 참고
    //해당  전체  레이아웃에  onclick부분에 hidekeyboard  넣어줌.
    public void hidekeyboard(View view){

        //키보드  관련 메니저  객체
        managerforkeyboardaction= (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);//2-5

        //윈도우에서 사라지도록  만듬.
        managerforkeyboardaction.hideSoftInputFromWindow(input_chatting_content.getWindowToken(), 0);
    }


    //현재 선생님 정보를 서버에서 가지고 오기  위한   메소드
    //선생님 uid가   채팅   서버에서  필요한  정보 이므로   가져와서 보내준다.
    private void getteacherinfo(String teacheremail, final int roomtype){

            Log.v("check", getLocalClassName()+"의  현재 선생님  정보를  디비로 부터 가져오기 위한 메소드 getteacherinfo() 진행");

            Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create())//json으로 받고 싶으면 쓰면안됨..
                    .build();//리트로핏 뷸딩
            ApiService apiService = retrofit.create(ApiService.class);//api인터페이스 크리에이트


            Call<teacherinforesult> getteacerinfo=apiService.sendemailtogetteacherprofile(teacheremail);//선생님 정보 요청위한  call 객체 선언

             //선생님 정보  callback 함수 진행.
            getteacerinfo.enqueue(new Callback<teacherinforesult>() {
                @Override
                public void onResponse(Call<teacherinforesult> call, Response<teacherinforesult> response) {
                    Log.v("check", "getteacherinfo  response 내용-> "+ response.body().toString());


                    if (response.body() != null) {//선생님 정보 가져온  결과값이 null이 아닐겨우.




                        if(roomtype==0){//오픈채팅방일때

                            teacheruid= response.body().getTeacheruid();//선생님  uid를  가지고 와서  roomnumber 변수에 넣어줌.

                            chattingroomnumber=response.body().getTeacheruid();//선생님  uid= 채팅방 number

                            makechatonnection(teacheruid);//서버 소켓 connection 실행 메소드

                            //서버에 저장된  룸 이미지 가져오기  메소드 -> 매개변수 로  현재 방번호와 룸 이미지가 담길  이미지뷰 넣어줌.
                            get_chatting_roomimage(imageView_for_room_profile,chattingroomnumber);


                        }else if(roomtype==1){//일대일 채팅방일때


                            teacheruid= response.body().getTeacheruid();//선생님  uid를  가지고 와서  roomnumber 변수에 넣어줌.
                            chattingroomnumber=roomnumber;//선생님 uid+teacheruid



                            makechatonnection(teacheruid);//서버 소켓 connection 실행 메소드

                            //서버에 저장된  룸 이미지 가져오기  메소드 -> 매개변수 로  현재 방번호와 룸 이미지가 담길  이미지뷰 넣어줌.
                            get_chatting_roomimage(imageView_for_room_profile,chattingroomnumber);

                        }//일대일 채팅발일때 조건 끝

                    }//responsebody값이  null값이 아닌경우.
                }//onResponse 끝

                @Override
                public void onFailure(Call<teacherinforesult> call, Throwable t) {
                    Log.v("check", "getteacherinfo onFailure()실행 됨 / failure 내용 -> "+t);

                }//onFailure() 끝
            });




    }//getteacherinfo()끝

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




    //툴바 뒤로가기  기능  적용 하기위한  itemselected()메소드
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//옵션아이템들 클릭시 진행되는 코드
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                Log.v("check", getLocalClassName()+"의  툴바 뒤로가기 눌림.");

                finish();//현재 엑티비티 끝냄.
                return true;
            }//뒤로가기 버튼 눌렸을때


            case R.id.chatting_right_toolbar_icon:{//툴바에서  햄버거 버튼  눌렀을때.

//

                drawer.openDrawer(Gravity.RIGHT);//네비게이션 드로워 오른쪽에서 열리도록 함.

            }


        }//switch문 끝
        return super.onOptionsItemSelected(item);

    }//onOptionItemSelected() 끝



    //현재 채팅 서버에  맨처음 소켓연결을 진행하는  메소드
    private void makechatonnection(String useruid){
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

                select_chatting_data(chattingroomnumber,useruid);//sqlite에 저장된  채팅 내용 가지고옴.

                socket=manager.socket("/openchat");//서버 채팅용 socket  네임스페이스중 openchat에 연결한다.
                socket.connect();//서버 소켓 연결 시도
                socket.once(Socket.EVENT_CONNECT, onConnect);//소켓  연결후 첫 이벤트로 connection이벤트를 날린다. //10-1

                socket.on("joinbroadcast", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                chatting_Data_RecyclerView_Adapter.notifyDataSetChanged();//리사이클러뷰 어뎁터 다시 데이터 체인지 하게  ㄱ해줌.

                                Log.v("CKKKKKKKKK", "처음 들어옴");
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




                                    Log.v("checknamenamename", chattingdata.toString());

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


                            Animation wrongshake= AnimationUtils.loadAnimation(ChattingRoomActivityForTeacher.this,R.anim.shakeedittext);//쉐이크하는 애니메이션 선언
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

                chattingroomnumber=roomnumber;

                //해당 채팅방 참여자 리스트 받아오기 진행
                get_room_joined_users_list(chattingroomnumber,recyclerView_for_put_room_members,useruid);

                if(count_check==1) {
                    //미디어 파일  최근  4개를  가지고 오기위한  메소드 실행 -> 매개변수로는   데이터  이름과,  현재 채팅방 번호 파일이 들어갈 이미지뷰  4개를 가져간다.
                    get_recent_four_meida_file_info(loginid_for_sql, chattingroomnumber, getShow_recent_mediafile1, getShow_recent_mediafile2, getShow_recent_mediafile3, getShow_recent_mediafile4);

                }

                //sqlite데이터베이승서  -> 현재 채팅방의 채팅 메세지의  readornot 값이  1인  데이터들을 찾아서
                //서버에  보내 -> 서버 쪽 데이터들 중에  해당 데이터들에 대하여  1씩 값을 낮춰준다.
                refresh_server_unreadcount(chattingroomnumber,loginid_for_sql );

                select_chatting_data(chattingroomnumber,useruid);//sqlite에 저장된  채팅 내용 가지고옴.

                socket=manager.socket("/onechat");//서버 채팅 네임스페이스 중 /onechat에 연결
                socket.connect();//서버 소켓 연결 시도

                socket.once(Socket.EVENT_CONNECT, onConnect_to_one_to_one_messenger);//소켓  연결후 첫 이벤트로 connection이벤트를 날린다. //10-1


                socket.on("joinbroadcast", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.v("CKKKKKKKKK", "처음 들어옴");
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




                                    Log.v("checknamenamename", chattingdata.toString());


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


                            Animation wrongshake= AnimationUtils.loadAnimation(ChattingRoomActivityForTeacher.this,R.anim.shakeedittext);//쉐이크하는 애니메이션 선언
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


                Animation wrongshake= AnimationUtils.loadAnimation(ChattingRoomActivityForTeacher.this,R.anim.shakeedittext);//쉐이크하는 애니메이션 선언
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

        count_check=0;

        Log.v("check", getLocalClassName()+"의  onPuase() 실행됨/  소켓 disconnect진행");

        //pause에서는 무조건 service  실행을 멈춰야한다.
        //ServiceForGetChattingData 서비스가 실행중일때이다.
        if(isMyServiceRunning(ServiceForGetChattingData.class)){

            Log.v("check", getLocalClassName()+"에서 ServiceForGetChattingData가 실행중이어서  stopService 실행함");

            //채팅 내용 받는 백그라운드 서비스  멈추게 한다.
            Intent stop_backgroun_chatting_service=new Intent(ChattingRoomActivityForTeacher.this,ServiceForGetChattingData.class);
            stopService(stop_backgroun_chatting_service);

        }//ServiceForGetChattingData 서비스가 실행중일때 조건 끝


        socket.disconnect();//socket연결을 끊는다.

        //12-1
        //여기서는 현재 채팅방의  소켓이 연결이 끝난 상태이므로,  다른 방들의  소켓을  서비스를 통해서 다시 연결 시켜준다. -> 위에 onResume에서
        //서비스 끊었으므로,  여기서 다시  실행시켜주면 됨.
        //여기는 거의 마지막 부분이서서 -> 해당방으 joinedornot부분이 해결되었을듯함.
        if(isMyServiceRunning(ServiceForGetChattingData.class)){

            Log.v("check", getLocalClassName()+"의 12-1 에서 ServiceForGetChattingData가 실행중이어서  stopService 실행함");

        }else{//채팅 내용 받는 서비스가 실행중이 아닐때 조건

            //채팅 백그라운드 받는  서비스(ServiceForGetChattingData) 를 실행시켜준다.
            Intent start_chatting_background_service=new Intent(ChattingRoomActivityForTeacher.this,ServiceForGetChattingData.class);
            startService(start_chatting_background_service);//서비스 실행시킴.
            Log.v("check", getLocalClassName()+"의 12-1 에서 ServiceForGetChattingData가 실행중이지 않아서 서비스 실행함");

        }//서비스 멈춰있을때 조건 끝.


    }//onPause()끝




    @Override
    protected void onStop() {
        super.onStop();
        Log.v("check", getLocalClassName()+"의  onStop 실행됨");

    }  //onStop() 끝





    /**********  기억해둘것 ****************/
    //드로워 레이아웃 네비게시션 ->  아이템 셀렉트  이벤트
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {





        return false;
    }//onNavigationItemSelected() 끝


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
                    //String openchattingroomname = response.body().getRoomname();//오픈 채팅이므로  룸 디비안에 있는 룸 이름 테이블 받아옴.
                    chattingclientcount=response.body().getChatclientcount();//해당 채팅방  참여 인원  수

                    String studentname=response.body().getStudentname();//학생 이름

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


                            chattingclientcount_tv.setVisibility(View.VISIBLE);//오픈채팅방 인원수 넣어주는 텍스트뷰  visible로
                            chattingclientcount_tv.setText("("+chattingclientcount+")");//숫자 보여지는텍스트뷰에  client 숫자 담음.


                        } else if (namespace.equals("1")) {// 1대1 채팅방일때.
                            Log.v("check", getLocalClassName()+"의  room 정보  1대1 채팅방용으로 가져옴");



                            //1대1 채팅방에서는  상대의 이름이  채팅방 이름-> 선생님  채팅방이므로  1대1 채팅은 학생 이름이 들어감.
                            room_name_textview.setText(studentname);

                            //해당 텍스트 사이즈
                            room_name_textview.setTextSize(21);

                            //1대1 채팅방의 경우는 -> 채팅방 참여  인원 수가 필요없다.
                            chattingclientcount_tv.setVisibility(View.INVISIBLE);

                        }//1대1 채팅방일떄 끝.


                    }//위  echo 0,1,2 에러들이 없을 경우.


                    //이붑분에서는 현재 메소드 맨끔에 놓으면 ,  onresponse부분은  비동기로  작동해서 ->  response가  다 실행되지 않음에도 ->  해당  서비스가 실행이 먼저가 실행되는 경우가 생김
                    //그부분을 해결 해주기 위해 onResponse와  onFailure 아래에다가 하나씩 넣어줌.

                    //12-1
                    //여기서는 현재 채팅방의  소켓이 연결이 끝난 상태이므로,  다른 방들의  소켓을  서비스를 통해서 다시 연결 시켜준다. -> 위에 onResume에서
                    //서비스 끊었으므로,  여기서 다시  실행시켜주면 됨.
                    //여기는 거의 마지막 부분이서서 -> 해당방으 joinedornot부분이 해결되었을듯함.
                    if(isMyServiceRunning(ServiceForGetChattingData.class)){

                        Log.v("check", getLocalClassName()+"의 12-1 에서 ServiceForGetChattingData가 실행중이어서  stopService 실행함");

                    }else{//채팅 내용 받는 서비스가 실행중이 아닐때 조건

                        //채팅 백그라운드 받는  서비스(ServiceForGetChattingData) 를 실행시켜준다.
                        Intent start_chatting_background_service=new Intent(ChattingRoomActivityForTeacher.this,ServiceForGetChattingData.class);
                        startService(start_chatting_background_service);//서비스 실행시킴.
                        Log.v("check", getLocalClassName()+"의 12-1 에서 ServiceForGetChattingData가 실행중이지 않아서 서비스 실행함");

                    }//서비스 멈춰있을때 조건 끝.


                }//ressponse.body() 가  null이 아닐경우 끝.

            }//onResponse()끝

            @Override
            public void onFailure(Call<GetRoomInfoClass> call, Throwable t) {
                Log.v("check", getLocalClassName()+"의 getroominfo()에서 방 정보 받아오는 중에 에러 생김 /에러 내용->"+t);


                //실패해도 -> 서비스는 다시  작동 시켜야하기 때문에...
                //12-1
                //여기서는 현재 채팅방의  소켓이 연결이 끝난 상태이므로,  다른 방들의  소켓을  서비스를 통해서 다시 연결 시켜준다. -> 위에 onResume에서
                //서비스 끊었으므로,  여기서 다시  실행시켜주면 됨.
                //여기는 거의 마지막 부분이서서 -> 해당방으 joinedornot부분이 해결되었을듯함.
                if(isMyServiceRunning(ServiceForGetChattingData.class)){

                    Log.v("check", getLocalClassName()+"의 12-1 에서 ServiceForGetChattingData가 실행중이어서  stopService 실행함");

                }else{//채팅 내용 받는 서비스가 실행중이 아닐때 조건

                    //채팅 백그라운드 받는  서비스(ServiceForGetChattingData) 를 실행시켜준다.
                    Intent start_chatting_background_service=new Intent(ChattingRoomActivityForTeacher.this,ServiceForGetChattingData.class);
                    startService(start_chatting_background_service);//서비스 실행시킴.
                    Log.v("check", getLocalClassName()+"의 12-1 에서 ServiceForGetChattingData가 실행중이지 않아서 서비스 실행함");

                }//서비스 멈춰있을때 조건 끝.

            }//onFailure () 끝
        });//룸정보 콜백  끝




    }//getroominfo() 끝



    //채팅 데이터 sql에  저장하는 메소드
    private void save_chatting_data_in_sql(JSONObject receivedata, SqLiteOpenHelperClass sqLiteOpenHelper, SQLiteDatabase database) {

        Log.v("check", getLocalClassName()+"에서  svae_chatting_data_in_Sql()실행됨");

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

            int read_or_not = 0;//unread 1.  read는  0임. -> 채팅방에서는   읽은 상태이므로,  read -> 0형태로 넣어준다

            //백그라운드에서 받은 채팅 데이터  sqlite에  넣어줌.
            sqLiteOpenHelper.inserChattingData(database, senderuid, roomnamespace, roomnumber, teachername, senderposition, sendername, profile, viewtype, date, chatting_order, chatting_message, read_or_not);

        }catch (JSONException e){

            e.printStackTrace();

        }

    }//save_chatting_data_in_sql() 끝

}//ChattingRoomActivity 선생님 용 클래스 끝.
