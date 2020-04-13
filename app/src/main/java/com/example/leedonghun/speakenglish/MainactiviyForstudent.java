package com.example.leedonghun.speakenglish;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;


import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
 * Class: MainactiviyForstudent.
 * Created by leedonghun.
 * Created On 2018-12-23.
 * Description:
 * 학생 로그인을 하면 나오게 되는  학생  메인 화면이다.
 * fcm을  받을  구글  토큰을 생성해서   디비에  넣어준다.
 */
public class MainactiviyForstudent extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private Checkpermission checkpermission;

    BottomNavigationView bottomNavigationViewforstudent;// 학생 화면의
    FragmentManager fm;//프래그먼트 매니저이
    FragmentTransaction tran;//프래그먼트 전환해주는 역활
    FragmentForStudentMyclass myclassfrag;//내 수업목록의 프래그먼트
    FragmentForSudentClasstype classtypefrag;//수업별  목록
    FragmentForStudentTutortype tutortypefrag;//튜터별 목록
    FragmentForStudentChattingtype chattingtypefrag;//채팅 보관함.

    //뒤로가기 버튼 눌리고 두번째 버튼 눌릴때까지 간격 시간초  2초 측정 -> 넘어가면  다시 리셋
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    //사진 관련 필요한  변수들
    private static final int PICK_FROM_CAMERA = 1; //카메라 촬영으로 사진 가져오기
    private static final int PICK_FROM_ALBUM = 2; //앨범에서 사진 가져오기
    private static final int CROP_FROM_CAMERA = 3; //가져온 사진을 자르기 위한 변수

    //사진 uri
    Uri photoUri;

    private String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}; //권한 설정 변수
    private static final int MULTIPLE_PERMISSIONS = 101; //권한 동의 여부 문의 후 CallBack 함수에 쓰일 변수
    private String mCurrentPhotoPath;
    private ImageView studentprofile;
    private TextView  enlglishnam;
    private TextView  studentemail;
    private Retrofit retrofit;//리트로핏 선언
    private ApiService apiService;//api service 인터페이스

    private Toastcustomer toastcustomer;//커스톰 토스트

    //토큰보낼때 사용할 서버 통신을 위한  retrofit 변수들.
    private  Retrofit retrofitforsendfcmtoken;
    private  ApiService apiServiceforsendfcmtoken;

    //학생이 로그아웃 할때 토큰 저장한  디비에  학생 로그인 상태 체크 컬럼  값  변경위한  retrofit 변수들
    private Retrofit retrofitforsendlogoutstatus;
    private ApiService apiServiceforlogoutstatus;

    SqLiteOpenHelperClass sqLiteOpenHelperClass; //sqlite ->  chatting data를 생성하기 위한   객체  선언
    SQLiteDatabase database;//sqlite 데이터 베이스 객체
    String loginedid;


    TextView txt_for_show_point_amount;//현재 가진  포인트 량을 보여주는 텍스트뷰
    Button  btn_for_charging_point;//포인트 충전 엑티비티로 가는  버튼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v("check",getLocalClassName()+"의  onCreate 실행됨.");
        setContentView(R.layout.studentdrawablenavigation);//네비게이션 xml에 mainactivity_forstudent가  같이  결합되어 있음 include 사용



        SharedPreferences getid = getSharedPreferences("loginstudentid", MODE_PRIVATE);//로그인 아이디  쉐어드에 담긴거 가져옴
        loginedid = getid.getString("loginid", "");//로그인 아이디 가져옴

        String loginid_for_sql=loginedid.replaceAll("@", "");





        bottomNavigationViewforstudent=(BottomNavigationView)findViewById(R.id.bottom_navigationforstudent);//학생 페이지에서 네비게이션 뷰누름
        myclassfrag=new FragmentForStudentMyclass();// 내수업 관련 프래크먼트 선언.
        tutortypefrag=new FragmentForStudentTutortype();//튜터별 프래그먼트 선언
        classtypefrag=new FragmentForSudentClasstype();//클래스 별  프래그먼트 선언.
        chattingtypefrag=new FragmentForStudentChattingtype();//채팅별  프래그먼트 선언

        //BottomNavigationHelper.disableShiftMode(bottomNavigationViewforstudent);//botttom 네비게이션의 쉬프트 모드를 종료시킨다.

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);//네비게이션 뷰 선언
        View nav_header_view = navigationView.inflateHeaderView(R.layout.student_drawable_navi_header);//학생 헤더부분 인플레이터
        studentprofile=(ImageView) nav_header_view.findViewById(R.id.studentprofile);//학생 헤더부분  학생프로필 이미지 뷰선언

        studentemail=(TextView)nav_header_view.findViewById(R.id.studentemail_nav_head);//드로어 네비게이션  헤더부분 학생이메일
        enlglishnam=(TextView)nav_header_view.findViewById(R.id.englishname_nav_head);//드로어 네비게이션 헤더부분  영어이름

        txt_for_show_point_amount=nav_header_view.findViewById(R.id.txt_for_show_point_amount);//현재 보유  포인트 량을  보여주는 텍스트뷰
        btn_for_charging_point=nav_header_view.findViewById(R.id.btn_for_charging_point);//포인트 충전 엑티비티로가는 버튼

       //선생님 헤더를  넣어줘야됨


        toastcustomer=new Toastcustomer(MainactiviyForstudent.this);//커스톰 엑티비티 선언

         //sqliteOpenHelperClass 를 사용해서 -> 채팅  보관하는  sqlite 테이블을 만들어준다.
         sqLiteOpenHelperClass=new SqLiteOpenHelperClass(MainactiviyForstudent.this, loginid_for_sql, null, 1);
         database = sqLiteOpenHelperClass.getWritableDatabase();

        //노티로 선생님 프로필 본경우  뒤로가기를 하면  선생님 찾기  프래그먼트가 띄우도도록 하기로함.
        //그래서  학생 메인을  실행시켰고,   노티로 온건지 그냥  실행시켜서  메인이 나온건지  구별하기위해
        //노티에서 보낸 구별용 인텐트 값을  보고  판단한다.
        Intent getbackintentcheck=getIntent();
        int checkbackintentornot=getbackintentcheck.getIntExtra("checkbackstack", -1);

        //노티로 선생님 프로필 보고 왔을때
        if(checkbackintentornot==3){

            //현재 엑티비티 시작되면   bottomnavigation뷰는 항상  myclass를  누르고 있으므로 tutortype이 눌린것처럼 보이게 설정함.
            bottomNavigationViewforstudent.setSelectedItemId(R.id.tutortype);
            setFrag(1);//노티로 선생님 프로필 보고 돌아온것이므로,  선생님 찾기 프래그먼트를 보여줌..


        }else if(checkbackintentornot==2){//채팅방에서 나가질때 무조건  메인 엑티비티나오고  채팅 프래그먼트를  보여준다.


            //현재 엑티비티 시작되면   bottomnavigation뷰는 항상  myclass를  누르고 있으므로 chattingtype 이 눌린것처럼 보이게 설정함.
            bottomNavigationViewforstudent.setSelectedItemId(R.id.chattingtype);
            setFrag(3);//채팅방을 나가고 난  상태이므로  채팅 프래그먼트를 보여주어  해당 방 리스트에  나간 방이 없어짐을 보여준다.


        }else{//체크값이  3 또는 2가 아닐때 ->  노티로 선생님 프로필 보고 온게 아닐때

            setFrag(0); //프래그먼트 교체-> 맨처음에 엑티비티 시작되면  0번 프래그먼트 실행

        }


        //아래쪽 네비게이션 뷰 클릭 이벤트
        bottomNavigationViewforstudent.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.myclasstype://내수업 버튼을 눌렀을 경우이다.
                        setFrag(0); //프래그먼트 교체
                        break;

                    case R.id.tutortype://튜터  별 눌렀을 경우이다.
                        setFrag(1); //프래그먼트 교체
                        break;

                    case R.id.chattingtype://채팅 별  눌렀을 경우이다.
                        setFrag(3); //프래그먼트 교체
                        break;
                }
                return true;
            }
        });



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarforupdateprofilestu);//툴바
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);//드로어 네비게이션 이 들어간 드로어 선언
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toolbar.setNavigationIcon(R.drawable.account3);//툴바에서 네비게이션 담당 아이콘 커스톰

        navigationView.setNavigationItemSelectedListener(this);//네비게이션 드로어 리스너


        studentprofile.setOnClickListener(new View.OnClickListener() {//프로필 사진을 클릭하면
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener cancelListner = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();///취소 버튼을 눌러서  다이얼로그 취소됨.

                    }
                };//취소 시


                DialogInterface.OnClickListener cameraListner = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        takePhoto();// 사진 촬영 메소드 실행

                    }
                };//사진촬영

                DialogInterface.OnClickListener albumListner = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        goToAlbum();//앨범 픽  메소드 실행시킨다.

                    }

                };//앨범 픽


                new AlertDialog.Builder(MainactiviyForstudent.this)
                        .setTitle("프로필 이미지 선택")// 사진 촬영  다이얼로그  제목
                        .setPositiveButton("취소", cancelListner)//사진촬영  다이얼로그 취소버튼
                        .setNeutralButton("사진찍기", cameraListner)//사진촬영 다이얼로그 카메라
                        .setNegativeButton("사진앨범", albumListner)//사진촬영 다이얼로그 앨범 버튼
                        .show();
            }
        });





        //fcm 토큰  가져오기
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("check", "fcm 토큰 가져오기  실패함.", task.getException());
                            return;
                        }

                        //fcm 토큰을  받아옴.
                        String token = task.getResult().getToken();

                        //앱 서버로  토큰을 보내줌.
                        sendtokentoserver(token);

                        // Log
                        Log.v("checkfcmtoken", "학생  fcm 토큰 가져옴 / 토큰 정보 ->"+token);

                    }//onComplete메소드 끝,
                });//토큰 가져오는 메소드 끝.



        //포인트 충전 버튼을 눌렀을때 이벤트
        btn_for_charging_point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //충전할 포인트 선택하는 엑티비티로 감
                Intent intent_for_goto_Sslectcharging_point=new Intent(MainactiviyForstudent.this,SelectChargingPointAmountActivity.class);
                startActivity(intent_for_goto_Sslectcharging_point);


            }
        });


    } //on create 끝



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


    //서버로  fcm토큰 보내기
    private  void sendtokentoserver(String fcmtoken){

        Log.v("check", getLocalClassName()+"의  sendtoekntoserver()메소드 실행됨");

        SharedPreferences getid = getSharedPreferences("loginstudentid", MODE_PRIVATE);//로그인 아이디  쉐어드에 담긴거 가져옴
        final String loginedid = getid.getString("loginid", "");//로그인 아이디 가져옴

        //retrofit 통신 ..
        retrofitforsendfcmtoken=new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create()).build();
        apiServiceforsendfcmtoken=retrofitforsendfcmtoken.create(ApiService.class);

        //서버로 보낼  학생 구분을 위한 이메일
        RequestBody studentemail=RequestBody.create(MediaType.parse("text/plain"), loginedid);

        //서버로 보낼  fcm토큰
        RequestBody studentfcmtoken=RequestBody.create(MediaType.parse("text/plain"), fcmtoken);


        //서버로  fcm토큰 보냄
        Call<ResponseBody> sendstudentfcmtoken=apiServiceforsendfcmtoken.sendstudentfcmtoken(studentemail, studentfcmtoken);

        //서버 통신 callback
        sendstudentfcmtoken.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.body() != null) {

                    try {

                        String sendtokenCallbackresult=response.body().string();
                        Log.v("check", getLocalClassName()+"의  토큰 서버 보내기  콜백값 ->"+sendtokenCallbackresult);

                     }catch (IOException e) {
                        e.printStackTrace();
                    }

                }//response.body() != null 끝
            }//onresponse끝

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Log.v("check", "서버로  학생 fcm토큰보내는것  fail 내용->"+t);

            }//onFailure 끝
        });//enqueue  메소드 끝.

    }//sendtokentoserver()메소드끝



    //현재 보유 포인트를 보여주기 위해
    //서버에서 해당 학생의  포인트를 가지고 온다
    private  void get_student_present_point(String student_uid,TextView txt_for_show_present_point){

        //retrofit 통신 ..
        Retrofit retrofit_for_charging_std_point=new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create()).build();
        ApiService apiService=retrofit_for_charging_std_point.create(ApiService.class);

        //학생 포인트 가지고오기
        Call<ResponseBody> get_std_present_point=apiService.get_std_present_point_amount(student_uid);

        get_std_present_point.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    String result=response.body().string();

                    if(result.equals("-1")){
                        Log.v("check", getLocalClassName()+"의 get_student_present_point()에서  학생 포인트 가져오기 실패함 ");


                    }else{//성공시

                        txt_for_show_present_point.setText("현재 보유 포인트: "+result+" p");

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

    @Override
    protected void onStart() {
        super.onStart();

        Log.v("check",getLocalClassName()+"의  onStart 실행됨");

        //onstart에서  학생 정보를 가지고 온 이유는   프로필 변경이라든지  업데이트 엑티비티에서 돌아올떄 -> 바뀐 내용을  서버로부터 가져와  사용자가  바로 볼수 있기에 적합하게
        //절차가  들어맞는   생명주기이기 떄문이다..
        SharedPreferences getid = getSharedPreferences("loginstudentid", MODE_PRIVATE);//로그인 아이디  쉐어드에 담긴거 가져옴
        final String loginedid = getid.getString("loginid", "");//로그인 아이디 가져옴
        retrofit = new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create())//json으로 받고 싶으면 쓰면안됨..
                .build();//리트로핏 뷸딩
        apiService = retrofit.create(ApiService.class);//api인터페이스 크리에이트


        Call<studentinforesult> getstudentinfo = apiService.sendemailtogetiprofile(loginedid);//studentinforesult 클래스를 return 타입으로
        //사용하는 apisrviece에 정의된 retrofit call함.
        getstudentinfo.enqueue(new Callback<studentinforesult>() {//실행
            @Override
            public void onResponse(Call<studentinforesult> call, Response<studentinforesult> response) {//응답부분

                try {
                    enlglishnam.setText(response.body().getName());//영어이름 들어가는 텍스트에 응답받은 json파일중 get name을 사용해서 해당값 받아옴
                    studentemail.setText(response.body().getEmail());//이메일 들어가는 텍스트에 응답받은 json 파일중  email값 받아옴.
                    //Log.d("dd",response.body().getProfilepath());//이미지 경로 받아오는지 확인함.
                    if (response.body().getProfilepath().equals("1")) {
                        studentprofile.setImageDrawable(getResources().getDrawable(R.drawable.profilenone));//이미지가 프로필이 없는 사진으로 바뀝니다. drawable파일의 있는 이미지로 바꿔줌
                    } else {

                        URL url = new URL("http://13.209.249.1/" + response.body().getProfilepath());//해당 이미지 url 받아옴

                        //가끔  mainthreand  와   중첩되어  본역할을 못함으로.
                        //글라이드 라이브러리로   비동기 식으로  가져오게 만듬.
                        Glide.with(MainactiviyForstudent.this).load(url).into(studentprofile);


                    }

                    GlobalApplication globalApplication=(GlobalApplication)getApplicationContext();//application클래스
                    globalApplication.setStudent_name(response.body().getName());//이름 넣어줌.
                    globalApplication.setStudnet_uid(response.body().getUid());//uid넣어줌.
                    globalApplication.setStudnet_profile_url(response.body().getProfilepath());//프로필 url넣어줌.



                    //학생 포인트 가지고옴
                    get_student_present_point(globalApplication.getStudnet_uid(),txt_for_show_point_amount);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<studentinforesult> call, Throwable t) {
                toastcustomer.showcustomtaost(null, "학생 정보를 가져오는데 실패했습니다.");
                finish();//실패하면 해당 엑티비티 꺼짐.
            }
        });


    }//on start 끝



    @Override
    protected void onResume() {
        super.onResume();
        Log.v("check",getLocalClassName()+"의  onreume() 실행됨.");



        //ServiceForGetChattingData 서비스가 실행중일때이다.
        if(isMyServiceRunning(ServiceForGetChattingData.class)){

            Log.v("check", getLocalClassName()+"에서 ServiceForGetChattingData 서비스가  실행중이어서 ->해당  서비스  실행  조건  실행안함.");

        }else{ //ServiceForGetChattingData 서비스가 실행중이지 않을대이다.
            Log.v("check", getLocalClassName()+"에서 ServiceForGetChattingData 서비스가  멈춰있어서 ->해당 서비스  실행  조건  됨.");

            //채팅 백그라운드 받는  서비스(ServiceForGetChattingData) 를 실행시켜준다.
            Intent start_chatting_background_service=new Intent(MainactiviyForstudent.this,ServiceForGetChattingData.class);
            startService(start_chatting_background_service);//서비스 실행시킴.

        }// //ServiceForGetChattingData 서비스가 실행중일때 조건 끝



        //권한 체크용 클래스 부름.
         checkpermission=new Checkpermission(MainactiviyForstudent.this);

        //해당 권한들의  체크 상태를  확인하고,  하나라도 체크가 안되어있으면,  false를  return
         boolean checkpermissionresult=checkpermission.checkPermissions();


         if(!checkpermissionresult){

             Log.v("check",getLocalClassName()+"클래스에서   권한 체크 중  승인 안된  권한이 발견되어  requestpermission엑티비티로 다시 넘어간다.");
             Intent intent=new Intent(MainactiviyForstudent.this,RequestPermission.class);
             startActivity(intent);

             //현재 activity를 종료시키는 이유는  Requestpermission 엑티비티에서  권한을 다  승인 했을 경우에  다시 로그인 엑티비티로 넘어갈텐데,
             //이때, 현재 activity를  종료시켜놓지 않으면  스택에 남아있어서  로그인 엑티비티가  스택에  두번  올라가지기 때문이다.
             //뒤로가기 누르다가 발견함.
             finish();
         }




    }//onResume 끝


    private void takePhoto() {
        Log.v("check",getLocalClassName()+"에서  takePhoto()메소드 실행됨");

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //사진을 찍기 위하여 설정합니다.
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(MainactiviyForstudent.this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
        }


        if (photoFile != null) {

            photoUri = FileProvider.getUriForFile(MainactiviyForstudent.this,
                    "com.example.leedonghun.speakenglish.provider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri); //사진을 찍어 해당 Content uri를 photoUri에 적용시키기 위함

            startActivityForResult(intent, PICK_FROM_CAMERA);
        }

    }//takePhoto() 메소드 끝



    private File createImageFile() throws IOException {

        // Create an image file name
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "IP" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/test/"); //test라는 경로에 이미지를 저장하기 위함
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath =image.getAbsolutePath();


        return image;

    }//createimageFile()메소드



    private void goToAlbum() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);

    }//goToAlbum()끝


    public void cropImage() {


        this.grantUriPermission("com.android.camera", photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(photoUri, "image/*");

        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);

        grantUriPermission(list.get(0).activityInfo.packageName, photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        int size = list.size();
        if (size == 0) {

            return;
        } else {


            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            intent.putExtra("crop", "true");
            intent.putExtra("aspectX",10);
            intent.putExtra("aspectY",10);
            intent.putExtra("scale", true);
            File croppedFileName = null;
            try {
                croppedFileName = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            File folder = new File(Environment.getExternalStorageDirectory() + "/test/");
            File tempFile = new File(folder.toString(), croppedFileName.getName());

            photoUri = FileProvider.getUriForFile(MainactiviyForstudent.this,
                    "com.example.leedonghun.speakenglish.provider", tempFile);


            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.putExtra("return-data", false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString()); //Bitmap 형태로 받기 위해 해당 작업 진행

            Intent i = new Intent(intent);
            ResolveInfo res = list.get(0);


            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            grantUriPermission(res.activityInfo.packageName, photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

            i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            startActivityForResult(i, CROP_FROM_CAMERA);


        }

    }//cropimag()끝

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
     
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FROM_ALBUM) {
            if(data==null){
                return;
            }
            photoUri = data.getData();
            cropImage();


        } else if (requestCode == PICK_FROM_CAMERA) {
            cropImage();
            MediaScannerConnection.scanFile(MainactiviyForstudent.this, //앨범에 사진을 보여주기 위해 Scan을 합니다.
                    new String[]{photoUri.getPath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });
        } else if (requestCode == CROP_FROM_CAMERA) {
            try { //저는 bitmap 형태의 이미지로 가져오기 위해 아래와 같이 작업하였으며 Thumbnail을 추출하였습니다.

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                Bitmap thumbImage = ThumbnailUtils.extractThumbnail(bitmap, 128, 128);
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                thumbImage.compress(Bitmap.CompressFormat.JPEG, 100, bs); //이미지가 클 경우 OutOfMemoryException 발생이 예상되어 압축


                //여기서는 ImageView에 setImageBitmap을 활용하여 해당 이미지에 그림을 띄우시면 됩니다.
                File uploadFile1 = new File(mCurrentPhotoPath);
                SharedPreferences getid = getSharedPreferences("loginstudentid",MODE_PRIVATE);
                final String loginedid= getid.getString("loginid","");

                RequestBody reqFile = RequestBody.create(MediaType.parse("image/jpeg"), uploadFile1);
                RequestBody email = RequestBody.create(MediaType.parse("text/plain"),loginedid );

                retrofit=new Retrofit.Builder().baseUrl(ApiService.baseurl).build();
                apiService=retrofit.create(ApiService.class);

                Call<ResponseBody> addstudentprofile=apiService.uploadprofile(email,reqFile);
                addstudentprofile.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                           // toastcustomer(null,response.body().string());
                            Log.v("이거닷",response.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        toastcustomer.showcustomtaost(null,"실패");
                        Log.v("이거닷",t.toString());
                    }
                });

                studentprofile.setImageBitmap(thumbImage);

            } catch (Exception e) {
                Log.e("ERROR", e.getMessage().toString());
            }
        }
    }//onactivityresult 끝


    public void setFrag(int n){    //프래그먼트를 교체하는 작업을 하는 메소드를 만들었습니다
        fm = getFragmentManager();
        tran = fm.beginTransaction();
        switch (n){
            case 0:
                tran.replace(R.id.frag_container_student,myclassfrag);  //replace의 매개변수는 (프래그먼트를 담을 영역 id, 프래그먼트 객체) 입니다.
                tran.commit();//변환을 확인까지 해주어야함.
                break;
            case 1:
                tran.replace(R.id.frag_container_student,tutortypefrag);  //replace의 매개변수는 (프래그먼트를 담을 영역 id, 프래그먼트 객체) 입니다.
                tran.commit();//변환을 확인까지 해주어야함
                break;
            case 2:
                tran.replace(R.id.frag_container_student,classtypefrag);  //replace의 매개변수는 (프래그먼트를 담을 영역 id, 프래그먼트 객체) 입니다.
                tran.commit();//변환을 확인까지 해주어야함
                break;

            case 3:
                tran.replace(R.id.frag_container_student,chattingtypefrag);  //replace의 매개변수는 (프래그먼트를 담을 영역 id, 프래그먼트 객체) 입니다.
                tran.commit();//변환을 확인까지 해주어야함
                break;
        }
    }//프래그먼트 교체하는 작업 끝

    @Override
    public void onBackPressed() {//뒤로 가기  이벤트

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {//뒤로가기를 두번 눌렀을때 종료된다.
            long tempTime = System.currentTimeMillis();
            long intervalTime = tempTime - backPressedTime;

            if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
                super.onBackPressed();
            } else {
                backPressedTime = tempTime;
             toastcustomer.showcustomtaost(null,"뒤로가기 한번더 누르면 종료됩니다.!");
            }
        }
    }//뒤로가기 이벤트 끝


    //학생 메인화면  드로워블  네비게션에서  해당 메뉴들 셀렉될때마다 의 진행 경우들  나눠놓은  메소드
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();//해당 메뉴의 아이디

        if (id == R.id.changestudentinfo) {//내 정보 수정하는  드로어 네비게이션 버튼

            //학생 프로필  수정 화면으로 간다.
            Intent gotochangemyinfo = new Intent(MainactiviyForstudent.this, StudentProfileUpdate.class);
            startActivity(gotochangemyinfo);


        }else if(id==R.id.student_my_dictionary){//학생 드로어 네비게이션에서  내 단어장 버튼 누를 경우

          Log.v("check", getLocalClassName()+"의 내 단어장 버튼 눌림");

            //내 단어장  엑티비티(ActivityForMyDictionary) 로 가짐.
           Intent intent_to_go_mydictionary=new Intent(MainactiviyForstudent.this,ActivityForMyDictionary.class);
           startActivity(intent_to_go_mydictionary);


        }else if(id==R.id.student_get_word_ocr) {//학생 드로어 네비게이션에서 사진속 영어단어 추출  버튼 누를 경우
            Log.v("check", getLocalClassName()+"의  사진속  영어 단어 추출하기 버튼 눌림");

            //ocr기능을 이용해 이미지속  영어 문장 가지고 오는 엑티비티 (ActivityForGetWordFromImage) 로 가짐
            Intent intent_to_go_ActivityForGetWordFromImage=new Intent(MainactiviyForstudent.this,ActivityForGetWordFromImage.class);
            startActivity(intent_to_go_ActivityForGetWordFromImage);


        }else if (id == R.id.studentlogout) {//학생 드로어 네비게시션에서 누른  로그아웃 버튼

            //로그아웃 버튼을 눌렀을 경우 정말 끝내겠냐는  알럴트 다이얼로그를 한번더 날린다.
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("학생 계정 로그아웃");
            builder.setMessage("정말 로그아웃 하시겠어요??");

            //로그아웃을  선택할경우.
            builder.setPositiveButton("예",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {


                            //카카오톡 로그인  세션 해제를 진행하면서  쉐어드  저장된  로그인 이메일 기록도 같이  삭제시켜버림.
                            //카카오로그인의 경우  세션연결만  하고 애초에  이메일만 받아간경운데,  혹시모르니  세션 해제  절차도  정석대로 진행함.
                            //결국  모든  카카오톡 및 일반 로그인 기록들의 로그아웃 처리는  쉐어드를  비워버리면서  진행됨.
                            UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {//카카오톡 로그인 api 세션 해제
                                @Override
                                public void onCompleteLogout() {

                                    //학생  로그인 상태 서버로 로그아웃 상태로 보내기
                                    makestudentloginstatusloggedout();


                                    //로그아웃을 했으므로,  로그인 엑티비티로
                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(intent);
                                    finish();

                                    //기존에  쉐어드에 저장되었던 자동로그인용 이메일은 삭제 시켜준다.
                                    SharedPreferences pref = getSharedPreferences("loginstudentid", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = pref.edit();
                                    editor.remove("loginid");
                                    editor.commit();

                                    //ServiceForGetChattingData 서비스가 실행중일때이다.
                                    if(isMyServiceRunning(ServiceForGetChattingData.class)){

                                        Log.v("check", getLocalClassName()+"에서 유저가 로그아웃함으로  ServiceForGetChattingData 서비스가  멈춰줌.");

                                        //ServiceForGetChattingData 서비스   stop하도록 인텐트 날림.
                                        Intent stop_chatting_connect_service=new Intent(MainactiviyForstudent.this,ServiceForGetChattingData.class);
                                        stopService(stop_chatting_connect_service);

                                    }//ServiceForGetChattingData실행중일때.조건 끝
                                }
                            });
                        }
                    });

            //로그아웃  취소
            builder.setNegativeButton("아니오",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

            //로그아웃 물어보는  다이얼로그 실행
            builder.show();


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        //드로워가  선택이 되면  다시 닫혀야 되므로.
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }//드로어블 네비게이션 셀렉터  끝



 //학생  로그인 상태  fcmtoken디비 테이블에서   logout 형태로  만들기위해
 //서버에 로그아웃 알림
 //나중에  로그아웃된 학생에게는  선생님  로그인 알람을 보내지 않기 위해서이다.
 private void makestudentloginstatusloggedout(){

    Log.v("check", getLocalClassName()+"의 makestudentloginstatusloggedout()메소드 실행됨.");


    SharedPreferences getid = getSharedPreferences("loginstudentid", MODE_PRIVATE);//로그인 아이디  쉐어드에 담긴거 가져옴
    final String loginedid = getid.getString("loginid", "");//로그인 아이디 가져옴

    //학생 로그아웃 서버로 알리기위한 retrofit 변수들
    retrofitforsendlogoutstatus=new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create()).build();
    apiServiceforlogoutstatus=retrofitforsendlogoutstatus.create(ApiService.class);

    //서버로 보낼  학생  아이디
    RequestBody studentemail=RequestBody.create(MediaType.parse("text/plain"),loginedid);

    //서버로 학생 아이디 보냄.
    Call<ResponseBody> sencstudentlogoutinfo=apiServiceforlogoutstatus.makestudentlogoutstatus(studentemail);

    //학생 로그아웃 정보 서버로 보낸후  callback 결과
    sencstudentlogoutinfo.enqueue(new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            try {

                //결과  받는  스트링 값값
                String logoutstatusresult=response.body().string();
                Log.v("check", getLocalClassName()+"로그아웃시 학생 fcmtoken db 로그아웃처리 결과"+logoutstatusresult);


            } catch (IOException e) {

                e.printStackTrace();

            }


        }//onResponse 끝

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {

            Log.v("check", getLocalClassName()+"로그아웃시  학생  디비 로그아웃상태 처리  에러 내용-> "+t);


        }//onFailure 끝

    });//서버 callback 메소드 끝

 }//makestudentloginstatusloggedout()끝




}//클래스 끝