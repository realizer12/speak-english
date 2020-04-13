package com.example.leedonghun.speakenglish;

import android.Manifest;
import android.app.Activity;
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
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.design.widget.BottomNavigationView;
//import android.support.design.widget.NavigationView;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.app.NotificationManagerCompat;
//import android.support.v4.content.ContextCompat;
//import android.support.v4.content.FileProvider;
//import android.support.v4.widget.DrawerLayout;
//import android.support.v7.app.ActionBarDrawerToggle;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationManagerCompat;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import org.mozilla.javascript.tools.jsc.Main;

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
 * Class: MainactivityForTeacher.
 * Created by leedonghun.
 * Created On 2018-12-23.
 * Description:
 * 선생님 아이디로  로그인후 나오는  선생님용 메인화면이다.
 *
 *
 */
public class MainactivityForTeacher extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {



    private Checkpermission checkpermission;
    BottomNavigationView bottomNavigationViewforteacher;// 선생 화면의 bottomnavigation vie
    FragmentManager fm;//프래그먼트 매니저이
    FragmentTransaction tran;//프래그먼트 전환해주는 역활
    FragmentForTeacherChatting teacherChattingfrag;//내 수업목록의 프래그먼트
    FragmentForTeacherClass teacherClassfrag;//수업별  목록
    FragmentForTeacherSchdule teacherSchdule;//채팅 보관함.
    ImageView teacherprofile;//선생님 프로필
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    //사진 관련 필요한  변수들
    private static final int PICK_FROM_CAMERA = 1; //카메라 촬영으로 사진 가져오기
    private static final int PICK_FROM_ALBUM = 2; //앨범에서 사진 가져오기
    private static final int CROP_FROM_CAMERA = 3; //가져온 사진을 자르기 위한 변수
    Uri photoUri;
    private String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}; //권한 설정 변수
    private static final int MULTIPLE_PERMISSIONS = 101; //권한 동의 여부 문의 후 CallBack 함수에 쓰일 변수
    private String mCurrentPhotoPath;//사진 절대경로

    Retrofit retrofit;//리트로핏 선언
    ApiService apiService;//api service 인터페이스
    TextView teacheremail;//선생님 이메일 텍스트
    TextView  teachername;//선생님 이름 텍스트

    private Toastcustomer toastcustomer;//커스톰 토스트


    //토큰보낼때 사용할 서버 통신을 위한  retrofit 변수들.
    private  Retrofit retrofitforsendfcmtoken;
    private  ApiService apiServiceforsendfcmtoken;

    //선생님 로그아웃 할때 토큰 저장한  디비에  선생 로그인 상태 체크 컬럼  값  변경위한  retrofit 변수들
    private Retrofit retrofitforsendlogoutstatus;
    private ApiService apiServiceforlogoutstatus;

     SqLiteOpenHelperClass sqLiteOpenHelperClass; //sqlite ->  chatting data를 생성하기 위한   객체  선언
     SQLiteDatabase database;//sqlite 데이터 베이스 객체

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_drawablenavigation);
        Log.v("check",getLocalClassName()+"의  onCreate 실행됨.");



        bottomNavigationViewforteacher=(BottomNavigationView)findViewById(R.id.bottom_navigationforteacher);//학생 페이지에서 네비게이션 뷰누름
        teacherChattingfrag=new FragmentForTeacherChatting();//선생님 채팅보관 프래그먼트
        teacherClassfrag=new FragmentForTeacherClass();//선생님 수업 클래스
        teacherSchdule=new FragmentForTeacherSchdule();//선생님 스케쥴
        setFrag(0); //프래그먼트 교체-> 맨처음에 엑티비티 시작되면  0번 프래그먼트 실행- MY  CLASS  진행


        NavigationView navigationView1 = (NavigationView) findViewById(R.id.nav_view_teacheris);//네비게이션 뷰 선언
        View nav_header_view1 = navigationView1.inflateHeaderView(R.layout.teacher_drawable_navi_header);//학생 헤더부분 인플레이터
        teacherprofile=(ImageView) nav_header_view1.findViewById(R.id.teacherprofile);//학생 헤더부분  학생프로필 이미지 뷰선언
        teacheremail=(TextView)nav_header_view1.findViewById(R.id.teacheremail_nav_head);//드로어 네비게이션  헤더부분 학생이메일
        teachername=(TextView)nav_header_view1.findViewById(R.id.teacher_englishname_nav_head);//드로어 네비게이션 헤더부분  영어이름

        toastcustomer=new Toastcustomer(MainactivityForTeacher.this);//커스톰 토스트  선언

        SharedPreferences getid = getSharedPreferences("loginteacherid",MODE_PRIVATE);//로그인 아이디  쉐어드에 담긴거 가져옴
        final String loginedid= getid.getString("loginidteacher","");//로그인 아이디 가져옴


        String loginid_for_sql=loginedid.replaceAll("@", "");


        //sqliteOpenHelperClass 를 사용해서 -> 채팅  보관하는  sqlite 테이블을 만들어준다.
        sqLiteOpenHelperClass=new SqLiteOpenHelperClass(MainactivityForTeacher.this, loginid_for_sql, null, 1);
        database = sqLiteOpenHelperClass.getWritableDatabase();

        //노티 눌러서 왔을때 -> 백인텐트로 생기는데 이때 -> 채팅 프래그먼트 보여주는 값 받아옴.
        Intent getbackintentcheck=getIntent();
        int checkbackintentornot=getbackintentcheck.getIntExtra("checkbackstack", -1);

        //받아온 값이  1일때 -> 채팅 프래그먼트보여주는 조건 실행.
        if(checkbackintentornot==1){
            setFrag(1);//채팅 프래그먼트로  보여줌.
            bottomNavigationViewforteacher.setSelectedItemId(R.id.teacherchatting);//채팅버튼  클릭되어있는 상태로
        }


        bottomNavigationViewforteacher.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.teacherclass://내수업 버튼을 눌렀을 경우이다.

                        setFrag(0); //프래그먼트 교체

                        break;

                    case R.id.teacherchatting://채팅 별 눌렀을 경우이다.


                        setFrag(1); //프래그먼트 교체

                        break;

                }
                return true;
            }
        });//bottomnavigationview 클릭이벤트 끝



        Toolbar toolbar1 = (Toolbar) findViewById(R.id.toolbar_teacher);//툴바
        DrawerLayout drawer1 = (DrawerLayout) findViewById(R.id.drawer_layout_teacher);//드로어 네비게이션 이 들어간 드로어 선언
        ActionBarDrawerToggle toggle1 = new ActionBarDrawerToggle(
                this, drawer1, toolbar1,R.string.navigation_drawer_open_teacher, R.string.navigation_drawer_close_teacher);
        drawer1.addDrawerListener(toggle1);
        toggle1.syncState();
        toolbar1.setNavigationIcon(R.drawable.account3);//툴바에서 네비게이션 담당 아이콘 커스톰






        teacherprofile.setOnClickListener(new View.OnClickListener() {//프로필 사진을 클릭하면
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


                new AlertDialog.Builder(MainactivityForTeacher.this)
                        .setTitle("select profile img")// 사진 촬영  다이얼로그  제목
                        .setPositiveButton("cancel", cancelListner)//사진촬영  다이얼로그 취소버튼
                        .setNeutralButton("camera", cameraListner)//사진촬영 다이얼로그 카메라
                        .setNegativeButton("album", albumListner)//사진촬영 다이얼로그 앨범 버튼
                        .show();
            }
        });
        navigationView1.setNavigationItemSelectedListener(this);//네비게이션 드로어 리스너


        //선생님 용 fcm 토큰  가져오기
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("check", "fcm 토큰 가져오기  실패함.", task.getException());
                            return;
                        }

                        //선생님 fcm 토큰을  받아옴.
                        String token = task.getResult().getToken();

                        //앱 서버로  토큰을 보내줌.
                       sendtokentoserver(token);

                        // Log
                        Log.v("checkfcmtoken", "선생님  fcm 토큰 가져옴 / 토큰 정보 ->"+token);

                    }//onComplete메소드 끝,
                });//토큰 가져오는 메소드 끝

    }//ONCREATE 끝



    //서버로  fcm토큰 보내기
    private  void sendtokentoserver(String fcmtoken){

        Log.v("check", getLocalClassName()+"의  sendtoekntoserver()메소드 실행됨");

        SharedPreferences getid = getSharedPreferences("loginteacherid",MODE_PRIVATE);//로그인 아이디  쉐어드에 담긴거 가져옴
        final String loginedid= getid.getString("loginidteacher","");//로그인 아이디 가져옴

        //retrofit 통신 ..
        retrofitforsendfcmtoken=new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create()).build();
        apiServiceforsendfcmtoken=retrofitforsendfcmtoken.create(ApiService.class);

        //서버로 보낼  학생 구분을 위한 이메일
        RequestBody teacheremail=RequestBody.create(MediaType.parse("text/plain"), loginedid);

        //서버로 보낼  fcm토큰
        RequestBody teacherfcmtoken=RequestBody.create(MediaType.parse("text/plain"), fcmtoken);


        //서버로  fcm토큰 보냄
        Call<ResponseBody> sendstudentfcmtoken=apiServiceforsendfcmtoken.sendteacherfcmtoken(teacheremail, teacherfcmtoken);

        //서버 통신 callback
        sendstudentfcmtoken.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.body() != null) {//reposne가 null 값이 아닐때.

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

                Log.v("check", "서버로  선생님 fcm토큰보내는것  fail 내용->"+t);



            }//onFailure 끝
        });//enqueue  메소드 끝.

    }//sendtokentoserver()메소드끝






    @Override
    protected void onStart() {
        super.onStart();
        Log.v("check",getLocalClassName()+"의  onStart 실행됨.");



        SharedPreferences getid = getSharedPreferences("loginteacherid",MODE_PRIVATE);//로그인 아이디  쉐어드에 담긴거 가져옴
        final String loginedid= getid.getString("loginidteacher","");//로그인 아이디 가져옴

        retrofit=new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create())//json으로 받고 싶으면 쓰면안됨..
                .build();//리트로핏 뷸딩
        apiService=retrofit.create(ApiService.class);//api인터페이스 크리에이트


        Call<teacherinforesult> getteacherinfo=apiService.sendemailtogetteacherprofile(loginedid);//studentinforesult 클래스를 return 타입으로
        //사용하는 apisrviece에 정의된 retrofit call함.
        getteacherinfo.enqueue(new Callback<teacherinforesult>() {//실행
            @Override
            public void onResponse(Call<teacherinforesult> call, Response<teacherinforesult> response) {//응답부분

                try {
                    teachername.setText(response.body().getName());//영어이름 들어가는 텍스트에 응답받은 json파일중 get name을 사용해서 해당값 받아옴
                    teacheremail.setText(response.body().getEmail());//이메일 들어가는 텍스트에 응답받은 json 파일중  email값 받아옴.
                    //Log.d("dd",response.body().getProfilepath());//이미지 경로 받아오는지 확인함.
                    if(response.body().getProfilepath().equals("1")){
                        teacherprofile.setImageDrawable(getResources().getDrawable(R.drawable.profilenone));//이미지가 프로필이 없는 사진으로 바뀝니다. drawable파일의 있는 이미지로 바꿔줌
                    }else {
                        URL url = new URL("http://13.209.249.1/" + response.body().getProfilepath());//해당 이미지 url 받아옴
//                        URLConnection conn = url.openConnection();//
//                        conn.connect();//url과 커넥트함
//                        BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());//인풋스트림으로  받아오기 사진
//                        Bitmap bm = BitmapFactory.decodeStream(bis);
//                        bis.close();//인풋스트립 닫음
//                        teacherprofile.setImageBitmap(bm);//받아온 bitmap값 학생 프뢸사진에 넣어줌.

                         //선생님uid 넣어줌.
                         GlobalApplication globalApplication=(GlobalApplication)getApplicationContext();
                         globalApplication.setTeacheruid(response.body().getTeacheruid());

                        //위처럼  진행하면   노티피케이션  가져올때  url연결 부분에서  비동기 처리가안됨
                        //그래서 glide 라이브러리를 사용함.
                        Glide.with(MainactivityForTeacher.this).load(url).into(teacherprofile);

                        //선생님  teacherpoint 전체 포인트 량  취합 시켜줌.
                        make_teacher_point_refresh(response.body().getTeacheruid());
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<teacherinforesult> call, Throwable t) {
                toastcustomer.showcustomtaost(null,"fail to get member info!");
                Log.v("show-problem",t.getMessage());
                finish();//실패하면 해당 엑티비티 꺼짐.
            }
        });


    }//on start 끝

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
            Intent start_chatting_background_service=new Intent(MainactivityForTeacher.this,ServiceForGetChattingData.class);
            startService(start_chatting_background_service);//서비스 실행시킴.

        }// //ServiceForGetChattingData 서비스가 실행중일때 조건 끝


         //권한 체크용 클래스 부름.
        checkpermission=new Checkpermission(MainactivityForTeacher.this);

        //해당 권한들의  체크 상태를  확인하고,  하나라도 체크가 안되어있으면,  false를  return
        boolean checkcpermissionresult=checkpermission.checkPermissions();


        //위 권한 체크 메소드가 false를 return 했다면,
        if(!checkcpermissionresult){

            Log.v("check",getLocalClassName()+"클래스에서   권한 체크 중  승인 안된  권한이 발견되어  requestpermission엑티비티로 다시 넘어간다.");
            Intent intent=new Intent(MainactivityForTeacher.this,RequestPermission.class);
            startActivity(intent);

            //현재 activity를 종료시키는 이유는  Requestpermission 엑티비티에서  권한을 다  승인 했을 경우에  다시 로그인 엑티비티로 넘어갈텐데,
            //이때, 현재 activity를  종료시켜놓지 않으면  스택에 남아있어서  로그인 엑티비티가  스택에  두번  올라가지기 때문이다.
            //뒤로가기 누르다가 발견함.
            finish();

        }


    }//onResume 끝




    //프래그먼트를 교체하는 작업을 하는 메소드
    public void setFrag(int n){
        fm = getFragmentManager();
        tran = fm.beginTransaction();
        switch (n){

            case 0:

                Log.v("check", getLocalClassName()+"에서  프래그먼트  클래스 프래그먼트로 바꿈.");
                tran.replace(R.id.frag_container_teacher,teacherClassfrag);  //replace의 매개변수는 (프래그먼트를 담을 영역 id, 프래그먼트 객체) 입니다.
                tran.commit();//변환을 확인까지 해주어야함.
                break;

                
            case 1:

                Log.v("check", getLocalClassName()+"에서 프래그먼트  선생님 채팅으로 바꿈.");
                tran.replace(R.id.frag_container_teacher,teacherChattingfrag);  //replace의 매개변수는 (프래그먼트를 담을 영역 id, 프래그먼트 객체) 입니다.
                tran.commit();//변환을 확인까지 해주어야함
                break;

        }
    }//프래그먼트 교체하는 작업 끝









    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //사진을 찍기 위하여 설정합니다.
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(MainactivityForTeacher.this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();              finish();
        }
        if (photoFile != null) {

            photoUri = FileProvider.getUriForFile(MainactivityForTeacher.this,
                    "com.example.leedonghun.speakenglish.provider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri); //사진을 찍어 해당 Content uri를 photoUri에 적용시키기 위함

            startActivityForResult(intent, PICK_FROM_CAMERA);
        }
    }

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
    }



    private void goToAlbum() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

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

            photoUri = FileProvider.getUriForFile(MainactivityForTeacher.this,
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

    }

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
            MediaScannerConnection.scanFile(MainactivityForTeacher.this, //앨범에 사진을 보여주기 위해 Scan을 합니다.
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
                SharedPreferences getid = getSharedPreferences("loginteacherid",MODE_PRIVATE);//로그인 아이디  쉐어드에 담긴거 가져옴
                final String loginedid= getid.getString("loginidteacher","");//로그인 아이디 가져옴

                RequestBody reqFile = RequestBody.create(MediaType.parse("image/jpeg"), uploadFile1);
                RequestBody email = RequestBody.create(MediaType.parse("text/plain"),loginedid );

                retrofit=new Retrofit.Builder().baseUrl(ApiService.baseurl).build();
                apiService=retrofit.create(ApiService.class);

                Call<ResponseBody> addteacherprofile=apiService.uploadprofileteacher(email,reqFile);
                addteacherprofile.enqueue(new Callback<ResponseBody>() {
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
                //여기서는 ImageView에 setImageBitmap을 활용하여 해당 이미지에 그림을 띄우시면 됩니다.

                teacherprofile.setImageBitmap(thumbImage);
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage().toString());
            }
        }
    }//onactivityresult 끝





    @Override
    public void onBackPressed() {//뒤로 가기  이벤트

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_teacher);
        if (drawer.isDrawerOpen(androidx.core.view.GravityCompat.START)) {
            drawer.closeDrawer(androidx.core.view.GravityCompat.START);
        } else {//뒤로가기를 두번 눌렀을때 종료된다.
            long tempTime = System.currentTimeMillis();
            long intervalTime = tempTime - backPressedTime;

            if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {//backpress 누루고  일정 시간이  지나면  다시  두번 누르기가  취소됨.
                super.onBackPressed();
            } else {
                backPressedTime = tempTime;
                toastcustomer.showcustomtaost(null,"press backbtn twice to exit");

            }

            //이 토스트가 나오고  바로 두번 클릭후  어플리케이션이  종료가 되었을때
            //토스트가  남아 있지 않고 종료되도록  해야한다.


            }
    }//뒤로가기 이벤트 끝




    @Override
    protected void onDestroy() {
        super.onDestroy();
          Log.v("check", getLocalClassName()+"의  onDestroy() 작동");



    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {//드로어블 네비게이션  셀렉트 리ㅡ너이다.
        int id = item.getItemId();//해당 메뉴의 아이디

        if (id == R.id.changeteacherinfo) {
           Log.v("check", getLocalClassName()+"의 네비게이션아이템 중  선생님  프로필 업데이트 버튼 눌림");
           Intent gotochangeteacherinfo =new Intent(MainactivityForTeacher.this,TeacherProfileUpdate.class);
           startActivity(gotochangeteacherinfo);//선생님 프로필 수정 공간으로 넘어간다.

        }else if (id == R.id.teacherlogout) {
            //로그아웃 버튼을 눌렀을 경우 정말 끝내겠냐는  알럴트 다이얼로그를 한번더 날린다.
            Log.v("check", getLocalClassName()+"의 네비게이션아이템 중  선생님 로그아웃 버튼 눌림");

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Logout");
            builder.setMessage("Are you sure you wanna logout?");
            builder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Log.v("check", "선생님 로그아웃 버튼 ok눌림");

                            UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                                @Override
                                public void onCompleteLogout() {

                                    //선생님 fcmtokenfort 에서 로그아웃 처리
                                    make_teacher_login_status_logged_out();


                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(intent);
                                    finish();//해당  선생님 페이지  지움.

                                    changeteacherstatus();//선생님 로그아웃시  해당  노티 지워주고,  서버  로그인 상태 변경

                                    SharedPreferences pref = getSharedPreferences("loginteacherid", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = pref.edit();
                                    editor.remove("loginidteacher");// 해당 강사 이메일  너놓는 쉐어드 프린스에  로그아웃 했으므로 삭제한다.
                                    editor.commit();

                                    //ServiceForGetChattingData 서비스가 실행중일때이다.
                                    if(isMyServiceRunning(ServiceForGetChattingData.class)){

                                        Log.v("check", getLocalClassName()+"에서 유저가 로그아웃함으로  ServiceForGetChattingData 서비스가  멈춰줌.");

                                        //ServiceForGetChattingData 서비스   stop하도록  인텐트 날림.
                                        Intent stop_chatting_connect_service=new Intent(MainactivityForTeacher.this,ServiceForGetChattingData.class);
                                        stopService(stop_chatting_connect_service);

                                    }//ServiceForGetChattingData실행중일때.조건 끝

                                }
                            });
                        }
                    });
            builder.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Log.v("check", "선생님 로그아웃 버튼 취소누름");

                        }
                    });
            builder.show();


        }else if(id==R.id.teacherchangepassword){
            //비밀번호 변경  엑티비티로 가짐.
            Log.v("check", getLocalClassName()+"의 네비게이션아이템 중 선생님 비밀번호 바꾸기");

            //TeacherChnagePassWord 엑티비티로 가는 인텐트
            Intent gototeacherchangepasswordactivity=new Intent(MainactivityForTeacher.this,TeacherChangePassWord.class);
            startActivity(gototeacherchangepasswordactivity);//비밀번호 변경 엑티비티  시작.


        }//선생님 비밀번호 바꾸기  버튼 끝
        else if(id==R.id.setting_available_reservation_time){//선생님  예약 시간 세팅하기 버튼

            Log.v("check", getLocalClassName()+"의 예약 가능  시간 만드는 엑티비티로 가는 네비게이션 버튼 눌림.");

            //예약 가능 시간만드는  엑티비티로 ㄱㄱ
            Intent intent_to_go_settingreservationTime=new Intent(MainactivityForTeacher.this,SettingReservationTime.class);
            startActivity(intent_to_go_settingreservationTime);

        }//선생님 예약 시간 세팅 버튼 끝
        else if(id==R.id.show_teacher_point){//선생님  v포인트 획득 량  그래프로 보여주는 엑티비티 가기

            Log.v("check", getLocalClassName()+"의  eraning graph 가기 눌림");

            //earning graph 보여주는 엑티비티로 감
            Intent intent_to_go_earning_graph=new Intent(MainactivityForTeacher.this,ShowEarningGraphActivity.class);
            startActivity(intent_to_go_earning_graph);

        }//선생님  v포인트 획득 량  그래프로 보여주는 엑티비티 가기 끝
        else if(id==R.id.exchange_teacher_point){//선생님 포인트  환전  신청  엑티비티 가기
            Log.v("check", getLocalClassName()+"의 exchange_teacher_point가기  눌림");

            //ExchangeEarningPoint 로감
            Intent intent_to_go_exchange_point=new Intent(MainactivityForTeacher.this,ExchangeEarningPoint.class);
            startActivity(intent_to_go_exchange_point);

        }////선생님 포인트  환전  신청  엑티비티 가기 끝




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_teacher);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }//드로어블 네비게이션 셀렉터  끝



    //로그아웃했을떄를  대비하여   선생님  로그인 상태  서버값 변경해주고 노티 지워주는 메소드
   private  void changeteacherstatus(){

        String onoffresult="0";
        Retrofit retrofit=new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create())//json으로 받고 싶으면 쓰면안됨..
                .build();//리트로핏 뷸딩
        SharedPreferences getid = MainactivityForTeacher.this.getSharedPreferences("loginteacherid",MODE_PRIVATE);//선생님 로그인 아이디  쉐어드에 담긴거 가져옴
        String loginedid= getid.getString("loginidteacher","");//선생님 로그인 아이디 가져옴

        ApiService apiService=retrofit.create(ApiService.class);//api인터페이스 크리에이트
        RequestBody loginemail=RequestBody.create(MediaType.parse("text/plain"),loginedid);
        RequestBody onoffresultsend=RequestBody.create(MediaType.parse("text/plain"),onoffresult);
        Call<ResponseBody> teacheronoff1=apiService.sendteacherloginresult(onoffresultsend,loginemail);

        teacheronoff1.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.v("check","로그아웃->선생 로그인 상태 offline"+response.body());
                NotificationManagerCompat.from(MainactivityForTeacher.this).cancel(1);//기존의  알람이 떠있다면  꺼줌.
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.v("check","로그아웃->로그인 상태 변경 실패"+t.toString());
            }
        });

    }



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


    //선생님  로그인 상태  fcmtokenfort 디비 테이블에서   logout 형태로  만들기위해
    //서버에 로그아웃 알림
    //나중에  로그아웃된 선생에게는  학생관련 알람을 보내지 않기 위해서이다.
    private void make_teacher_login_status_logged_out(){

        Log.v("check", getLocalClassName()+"의  make_teacher_login_status_logged_out() 메소드 실행됨.");


        SharedPreferences getid = MainactivityForTeacher.this.getSharedPreferences("loginteacherid",MODE_PRIVATE);//선생님 로그인 아이디  쉐어드에 담긴거 가져옴
        String loginedid= getid.getString("loginidteacher","");//선생님 로그인 아이디 가져옴


        //선생님 로그아웃 서버로 알리기위한 retrofit 변수들
        retrofitforsendlogoutstatus=new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create()).build();
        apiServiceforlogoutstatus=retrofitforsendlogoutstatus.create(ApiService.class);

        //서버로 보낼  선생님  아이디
        RequestBody teacheremail=RequestBody.create(MediaType.parse("text/plain"),loginedid);

        //서버로 선생님 아이디 보냄.
        Call<ResponseBody> sencstudentlogoutinfo=apiServiceforlogoutstatus.maketeacherlogoutstatus(teacheremail);

        //학생 로그아웃 정보 서버로 보낸후  callback 결과
        sencstudentlogoutinfo.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    //결과  받는  스트링 값값
                    String logoutstatusresult=response.body().string();
                    Log.v("check", getLocalClassName()+"로그아웃시 선생님 fcmtokenfort db 로그아웃처리 결과"+logoutstatusresult);


                } catch (IOException e) {

                    e.printStackTrace();

                }


            }//onResponse 끝

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Log.v("check", getLocalClassName()+"로그아웃시  선생님  디비 로그아웃상태 처리  에러 내용-> "+t);


            }//onFailure 끝

        });//서버 callback 메소드 끝

    }//makestudentloginstatusloggedout()끝


}//CLASS 끝

