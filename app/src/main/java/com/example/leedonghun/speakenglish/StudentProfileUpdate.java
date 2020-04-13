package com.example.leedonghun.speakenglish;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.support.v4.content.FileProvider;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
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


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

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
import java.util.regex.Pattern;

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
 * Class: StudentProfileUpdate.
 * Created by leedonghun.
 * Created On 2019-01-16.
 * Description: 학생 쪽에서 자기 프로필 정보를 바꿀때 사용할수 있다.
 */
public class StudentProfileUpdate extends AppCompatActivity {
    Retrofit retrofit;//리트로핏 선언
    ApiService apiService;//api service 인터페이스
    int changechek=0;

    //사진 관련 필요한  변수들
    private static final int PICK_FROM_CAMERA = 1; //카메라 촬영으로 사진 가져오기
    private static final int PICK_FROM_ALBUM = 2; //앨범에서 사진 가져오기
    private static final int CROP_FROM_CAMERA = 3; //가져온 사진을 자르기 위한 변수
    Uri photoUri;
    private String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}; //권한 설정 변수
    private static final int MULTIPLE_PERMISSIONS = 101; //권한 동의 여부 문의 후 CallBack 함수에 쓰일 변수
    private String mCurrentPhotoPath;

    ImageView updatestuprofile;
    String originalenglishname;

    Toastcustomer toastcustomer;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changemyinfo_student);

        checkPermissions();//사진관련 퍼미션  진행메서드
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarforupdateprofilestu);//툴바 선언
        setSupportActionBar(toolbar);//툴바에  액션바  서포트 연결
        getSupportActionBar().setDisplayShowTitleEnabled(false);//엑션바에서 타이틀 안보이게함
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//액션바에서 Homeashup 버튼 가능하게함
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.whiteback);//해당 버튼  뒤로가기 버튼모양 으로  이미지 지정

        updatestuprofile=(ImageView)findViewById(R.id.updatestuprofile);//학생  프로필 사진 들어가는 이미지뷰.
        final TextView showstudentemailtext=(TextView)findViewById(R.id.showstudentemail);//학생 이메일이 보여지는 텍스트뷰
        final EditText englishnamestudnet=(EditText)findViewById(R.id.englishnamestudnet);
        final EditText writenewpassword=(EditText)findViewById(R.id.newpasswordstudent);//학생 패스워드를 쓰는 에딧텍스트
        final EditText rewritepasswortocheck=(EditText)findViewById(R.id.passwordcheckstudent);//학생 패스워드 체크
        Button updatestudentbtn=(Button)findViewById(R.id.updatestudentbtn);//학생 정보 수정 버튼


        toastcustomer=new Toastcustomer(getApplicationContext());



        SharedPreferences getid = getSharedPreferences("loginstudentid",MODE_PRIVATE);//로그인 아이디  쉐어드에 담긴거 가져옴
        final String loginedid= getid.getString("loginid","");//로그인 아이디 가져옴
        retrofit=new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create())//json으로 받고 싶으면 쓰면안됨..
                .build();//리트로핏 뷸딩
        apiService=retrofit.create(ApiService.class);//api인터페이스 크리에이트


         Call<studentinforesult> getstudentinfo=apiService.sendemailtogetiprofile(loginedid);//studentinforesult 클래스를 return 타입으로
        //사용하는 apisrviece에 정의된 retrofit call함.
        getstudentinfo.enqueue(new Callback<studentinforesult>() {//실행
            @Override
            public void onResponse(Call<studentinforesult> call, Response<studentinforesult> response) {//응답부분

                try {
                    englishnamestudnet.setText(response.body().getName());//영어이름 들어가는 텍스트에 응답받은 json파일중 get name을 사용해서 해당값 받아옴
                    showstudentemailtext.setText(response.body().getEmail());//이메일 들어가는 텍스트에 응답받은 json 파일중  email값 받아옴.
                    originalenglishname=englishnamestudnet.getText().toString();
                    // Log.d("dd",response.body().getProfilepath());//이미지 경로 받아오는지 확인함.
                    URL url = new URL("http://13.209.249.1/"+response.body().getProfilepath());//해당 이미지 url 받아옴
                    URLConnection conn = url.openConnection();//
                    conn.connect();//url과 커넥트함
                    BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());//인풋스트림으로  받아오기 사진
                    Bitmap bm = BitmapFactory.decodeStream(bis);
                    bis.close();//인풋스트립 닫음
                    updatestuprofile.setImageBitmap(bm);//받아온 bitmap값 학생 프뢸사진에 넣어줌.

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<studentinforesult> call, Throwable t) {

                toastcustomer.showcustomtaost(null,"회원 정보를 가져오는데 실패했습니다.");
                finish();//실패하면 해당 엑티비티 꺼짐.

            }
        });//내정보  서버로 부터 가져오기 끝






        updatestuprofile.setOnClickListener(new View.OnClickListener() {//프로필 사진을 클릭하면
            @Override
            public void onClick(View view) {

                 new AlertDialog.Builder(StudentProfileUpdate.this)
                        .setTitle("프로필 이미지 선택")// 사진 촬영  다이얼로그  제목
                        .setItems(new CharSequence[] {"프로필 없음","원래대로 돌리기", "사진찍기", "사진앨범", "취소"},
                         new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position
                                // of the selected item
                                switch (which) {
                                    case 0:
                                        //프로필 사진 삭제하는 내용 날려야됨.

                                        updatestuprofile.setImageDrawable(getResources().getDrawable(R.drawable.profilenone));//이미지가 프로필이 없는 사진으로 바뀝니다. drawable파일의 있는 이미지로 바꿔줌
                                        toastcustomer.showcustomtaost(null,"수정완료를 해야지 이미지가 바뀝니다.");
                                        changechek=2;//프로필 변경이므로 체크 사항이 1로 변함(체크됨)
                                        break;

                                    case 1://원상태로 이미지를 복귀할려면 다시 서버에서 해당 이미지를 받아와야 한다.
                                        Call<studentinforesult> getstudentinfo=apiService.sendemailtogetiprofile(loginedid);//studentinforesult 클래스를 return 타입으로
                                        getstudentinfo.enqueue(new Callback<studentinforesult>() {//실행
                                            @Override
                                            public void onResponse(Call<studentinforesult> call, Response<studentinforesult> response) {//응답부분

                                                try {
                                                     //이미지 부분만 가지고 와서  바꾸어 주면 됨.
                                                    // Log.d("dd",response.body().getProfilepath());//이미지 경로 받아오는지 확인함.
                                                    URL url = new URL("http://13.209.249.1/"+response.body().getProfilepath());//해당 이미지 url 받아옴
                                                    URLConnection conn = url.openConnection();//
                                                    conn.connect();//url과 커넥트함
                                                    BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());//인풋스트림으로  받아오기 사진
                                                    Bitmap bm = BitmapFactory.decodeStream(bis);
                                                    bis.close();//인풋스트립 닫음
                                                    updatestuprofile.setImageBitmap(bm);//받아온 bitmap값 학생 프뢸사진에 넣어줌.


                                                } catch (MalformedURLException e) {
                                                    e.printStackTrace();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }

                                            }

                                            @Override
                                            public void onFailure(Call<studentinforesult> call, Throwable t) {
                                                toastcustomer.showcustomtaost(null,"회원 정보를 가져오는데 실패했습니다.");
                                                finish();//실패하면 해당 엑티비티 꺼짐.
                                            }
                                        });//내정보  서버로 부터 가져오기 끝
                                        changechek=0;//프로필 변경이므로 체크 사항이 1로 변함(체크됨)

                                        break;

                                    case 2:
                                        takePhoto();// 사진 촬영 메소드 실행
                                        break;

                                    case 3:
                                        goToAlbum();//앨범 픽  메소드 실행시킨다.
                                        break;

                                    case 4:
                                        dialog.dismiss();///취소 버튼을 눌러서  다이얼로그 취소됨.
                                        break;

                                }
                            }
                        })
                     .create().show();//다이얼로그 보여줌

            }
        });//프로필 사진 클릭시 진행되는 것 끝


        updatestudentbtn.setOnClickListener(new View.OnClickListener() {//수정완료 버튼을 눌렀을 경우에 진행되는 이벤트이다.
            @Override
            public void onClick(View view) {

                 if(changechek==0){// 이미지가 아무것도 변경이 안되었을 경우이다.

                     if(englishnamestudnet.getText().toString().equals(originalenglishname)){//이미지가 변경안되었고, 이름도 변경이 안되었을경우

                         if(writenewpassword.getText().toString().equals("") && rewritepasswortocheck.getText().toString().equals("")){
                             //비밀번호 쓰는 칸의 값과 비밀번호 확인란의 값이
                             //모두 비워있을때
                             toastcustomer.showcustomtaost(null, "변경된 사항이 없습니다.");

                          }else{//둘중에 하나라도  뭔가 써져있을때

                               if (Pattern.matches("^(?=.*[a-zA-Z]+)(?=.*[!@#$%^*+=-]|.*[0-9]+).{2,16}$", writenewpassword.getText().toString())) {
                                 //비밀번호의 형식 영수 조합이 맞을 때이다..
                                 if (writenewpassword.getText().toString().equals(rewritepasswortocheck.getText().toString())) {
                                     //비밀번호값과 새 비밀번호값이 같을때이다.
                                     toastcustomer.showcustomtaost(null, "수정완료");
                                     changestudent(writenewpassword.getText().toString(),2,englishnamestudnet.getText().toString(),showstudentemailtext.getText().toString(),"0");

                                 } else {
                                     //비밀번호화 새 비밀번호 값이 다를때이다.
                                     toastcustomer.showcustomtaost(rewritepasswortocheck, "재확인 비밀번호가 다릅니다!");
                                 }

                              }else {
                                 //비밀번호 형식이 맞지 않을때이다.
                                 toastcustomer.showcustomtaost(writenewpassword, "비밀번호는 영어 숫자 조합이어야합니다.");

                              }

                             }

                     }else{//이미지는 변경이 안되었지만 이름은 변경이 되었을 경우
                         if(writenewpassword.getText().toString().equals("") && rewritepasswortocheck.getText().toString().equals("")){
                             //비밀번호 쓰는 칸의 값과 비밀번호 확인란의 값이
                             //모두 비워있을때
                             toastcustomer.showcustomtaost(null, "수정완료");
                             changestudent(writenewpassword.getText().toString(),2,englishnamestudnet.getText().toString(),showstudentemailtext.getText().toString(),"0");

                         }else{//둘중에 하나라도  뭔가 써져있을때

                             if (Pattern.matches("^(?=.*[a-zA-Z]+)(?=.*[!@#$%^*+=-]|.*[0-9]+).{2,16}$", writenewpassword.getText().toString())) {
                                 //비밀번호의 형식 영수 조합이 맞을 때이다..
                                 if (writenewpassword.getText().toString().equals(rewritepasswortocheck.getText().toString())) {
                                     //비밀번호값과 새 비밀번호값이 같을때이다.
                                     toastcustomer.showcustomtaost(null, "수정완료");
                                     changestudent(writenewpassword.getText().toString(),2,englishnamestudnet.getText().toString(),showstudentemailtext.getText().toString(),"0");

                                 } else {
                                     //비밀번호화 새 비밀번호 값이 다를때이다.
                                     toastcustomer.showcustomtaost(rewritepasswortocheck, "재확인 비밀번호가 다릅니다!");
                                 }

                             }else {
                                 //비밀번호 형식이 맞지 않을때이다.
                                 toastcustomer.showcustomtaost(writenewpassword, "비밀번호는 영어 숫자 조합이어야합니다.");

                             }

                         }
                     }


                   }else if(changechek==1){//이미지가  변경이 되었을경우이다.
                         toastcustomer.showcustomtaost(null, "수정완료");

                     changestudent(writenewpassword.getText().toString(),1,englishnamestudnet.getText().toString(),showstudentemailtext.getText().toString(),"0");
                   }else if(changechek==2){
                     toastcustomer.showcustomtaost(null, "수정완료");
                     changestudent(writenewpassword.getText().toString(),3,englishnamestudnet.getText().toString(),showstudentemailtext.getText().toString(),"1");

                 }
            }
        });//수정완료 버튼 눌렀을 경우 끝



    }//oncreate 끝


    public void changestudent(String password,int profilepathcheck,String englishname,String email1, String profilecheck){

        if(profilepathcheck==1) {//프로필 변화가 있을 경우에는 1을 넣어서 체크한후 프로필 사진을 같이 보내는 코드를 진행한다..

            //여기서는 ImageView에 setImageBitmap을 활용하여 해당 이미지에 그림을 띄우시면 됩니다.
            File uploadFile1 = new File(mCurrentPhotoPath);
            RequestBody reqFile = RequestBody.create(MediaType.parse("image/jpeg"), uploadFile1);//프로필 사진
            RequestBody email = RequestBody.create(MediaType.parse("text/plain"), email1);// 서버에서 구별하기위한 학생 이메일
            RequestBody newpassword = RequestBody.create(MediaType.parse("text/plain"), password);//새로운 비밀번호
            RequestBody newenglishnema = RequestBody.create(MediaType.parse("text/plain"), englishname);//새로운 영어이름

            retrofit = new Retrofit.Builder().baseUrl(ApiService.baseurl).build();
            apiService = retrofit.create(ApiService.class);

            Call<ResponseBody> updatestudentprofile = apiService.updatestudentprofile(email, reqFile, newpassword, newenglishnema);
            updatestudentprofile.enqueue(new Callback<ResponseBody>() {

                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        // toastcustomer(null,response.body().string());
                        Log.v("이거닷", response.body().string());
                        finish();
                      } catch (IOException e) {
                        e.printStackTrace();
                      }
                    }


                 @Override
                 public void onFailure(Call<ResponseBody> call, Throwable t) {
                     toastcustomer.showcustomtaost(null, "실패");
                     Log.v("이거닷", t.toString());
                 }
              });

        }else if(profilepathcheck==2){//프로필 변화가 없을 경우에는  나머지 부분만  체크를 하여 변경하도록 아래 코드를 진행한다.


            RequestBody email = RequestBody.create(MediaType.parse("text/plain"), email1);// 서버에서 구별하기위한 학생 이메일
            RequestBody newpassword = RequestBody.create(MediaType.parse("text/plain"), password);//새로운 비밀번호
            RequestBody newenglishnema = RequestBody.create(MediaType.parse("text/plain"), englishname);//새로운 영어이름
            RequestBody checkprofilenone=RequestBody.create(MediaType.parse("text/plain"),profilecheck);
            retrofit = new Retrofit.Builder().baseUrl(ApiService.baseurl).build();
            apiService = retrofit.create(ApiService.class);

            Call<ResponseBody> updatestudentprofile = apiService.updatestudentprofile1(email, newpassword, newenglishnema,checkprofilenone);
            updatestudentprofile.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        // toastcustomer(null,response.body().string());
                        Log.v("이거닷", response.body().string());
                        finish();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    toastcustomer.showcustomtaost(null, "실패");
                    Log.v("이거닷", t.toString());
                }
            });
            }else if(profilepathcheck==3){

            RequestBody email = RequestBody.create(MediaType.parse("text/plain"), email1);// 서버에서 구별하기위한 학생 이메일
            RequestBody newpassword = RequestBody.create(MediaType.parse("text/plain"), password);//새로운 비밀번호
            RequestBody newenglishnema = RequestBody.create(MediaType.parse("text/plain"), englishname);//새로운 영어이름
            RequestBody checkprofilenone=RequestBody.create(MediaType.parse("text/plain"),profilecheck);
            retrofit = new Retrofit.Builder().baseUrl(ApiService.baseurl).build();
            apiService = retrofit.create(ApiService.class);

            Call<ResponseBody> updatestudentprofile = apiService.updatestudentprofile1(email, newpassword, newenglishnema,checkprofilenone);
            updatestudentprofile.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        // toastcustomer(null,response.body().string());
                        Log.v("이거닷", response.body().string());
                        finish();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    toastcustomer.showcustomtaost(null, "실패");
                    Log.v("이거닷", t.toString());
                }
            });


        }
    }//스튜던트 프로필 체인지 끝


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//옵션아이템들 클릭시 진행되는 코드
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                finish();//현재 엑티비티 끝냄.
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    //*****************************************************************************************************************
    //안드로이드 마쉬멜로우 미만버전에서 앱 권한은 AndroidManifest.xml에 지정하고, 설치시에 앱이 사용하는 권한을 보여주기만 했습니다.
    //그래서 그 앱이 실제로 언제 그 권한을 사용하는지 실제로 사용하고는 있는지 등을 유저가 알 수 없었음.
    //그래서 마쉬멜로우 에서 실제 권한 요청시에 유저에게 권한을 사용 할지 확인 받는 과정이 추가 됨.
    //*****************************************************************************************************************
    //현재  핻당  권한이  승인되어있는 상태인지 아닌지를  체크하는 함수이다.
    private boolean checkPermissions(){

        int result;
        List<String> permissionList = new ArrayList<>();
        for (String pm : permissions) {
            result = ContextCompat.checkSelfPermission(this, pm);
            if (result != PackageManager.PERMISSION_GRANTED) { //사용자가 해당 권한을 가지고 있지 않을 경우 리스트에 해당 권한명 추가
                permissionList.add(pm);
            }
        }
        if (!permissionList.isEmpty()) { //권한이 추가되었으면 해당 리스트가 empty가 아니므로 request 즉 권한을 요청합니다.
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;

    }
    //현재  핻당  권한이  승인되어있는 상태인지 아닌지를  체크하는 함수 끝



    //아래는 권한 요청 Callback 함수입니다. PERMISSION_GRANTED로 권한을 획득했는지 확인할 수 있습니다. 아래에서는 !=를 사용했기에
    //권한 사용에 동의를 안했을 경우를 if문으로 코딩되었습니다.
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        if (permissions[i].equals(this.permissions[0])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();//관련해서 각각 퍼미션별 퍼미션이 안되었을경우 일어나는 메소드
                            }
                        } else if (permissions[i].equals(this.permissions[1])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();

                            }
                        } else if (permissions[i].equals(this.permissions[2])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();

                            }
                        }
                    }
                } else {
                    showNoPermissionToastAndFinish();
                }
                return;
            }
        }
    }
    //권한 요청 callback 함수 끝


    private void showNoPermissionToastAndFinish() {//권한 동의를 하지 않았을 경우   알럴트를 띄어준다.
        //  Toast.makeText(this, "권한 요청에 동의 하시기 바랍니다.", Toast.LENGTH_SHORT).show();

        toastcustomer.showcustomtaost(null,"퍼미션들을 모두 허락하세요!");
        finish();

    }
    //권한 동의를 하지 않았을경우 알럴트 띄우기 끝


    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //사진을 찍기 위하여 설정합니다.
        File photoFile = null;//
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(StudentProfileUpdate.this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (photoFile != null) {

            photoUri = FileProvider.getUriForFile(StudentProfileUpdate.this,
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
        mCurrentPhotoPath =image.getAbsolutePath();//이미지 절대경로 받아서 해당 변수에 넣어줌.
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
        if (size == 0) {/// 사이즈가  없으면,

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

            photoUri = FileProvider.getUriForFile(StudentProfileUpdate.this,
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
            MediaScannerConnection.scanFile(StudentProfileUpdate.this, //앨범에 사진을 보여주기 위해 Scan을 합니다.
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




                updatestuprofile.setImageBitmap(thumbImage);
                changechek=1;//사진이 새로운걸로 들어왔으니까  수정 사항이 있는걸로 체크
                toastcustomer.showcustomtaost(null,"수정완료를 해야지 이미지가 바뀝니다.");
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage().toString());
            }
        }
    }//onactivityresult 끝



}//현재 엑티비티 클래스  끝

