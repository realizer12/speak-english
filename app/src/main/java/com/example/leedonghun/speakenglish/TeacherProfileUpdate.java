package com.example.leedonghun.speakenglish;

import android.Manifest;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
//import android.support.annotation.Nullable;
//import android.support.v4.content.FileProvider;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
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
 * Class: TeacherProfileUpdate.
 * Created by leedonghun.
 * Created On 2019-01-19.
 * Description:
 * 선생쪽에서 자기 프로필 정보를 바꿀때 사용할수 있다.
 */
public class TeacherProfileUpdate extends AppCompatActivity {

    Retrofit retrofit;//리트로핏 선언
    ApiService apiService;//api service 인터페이스
    int changechekinteacherprofile = 0;//선생님 프로필에서 변화를 감지.
    int checkprofilechange=0;
    //사진 관련 필요한  변수들
    private static final int PICK_FROM_CAMERA = 1; //카메라 촬영으로 사진 가져오기
    private static final int PICK_FROM_ALBUM = 2; //앨범에서 사진 가져오기
    private static final int CROP_FROM_CAMERA = 3; //가져온 사진을 자르기 위한 변수
    Uri photoUri;
    private String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}; //권한 설정 변수
    private static final int MULTIPLE_PERMISSIONS = 101; //권한 동의 여부 문의 후 CallBack 함수에 쓰일 변수
    private String mCurrentPhotoPath;//프로필 이미지 절대경로

    Toastcustomer toastcustomer;///커스톰 으로 나오는 토스트
    ImageView updateteacherprofile;//선생님 프로필  이미지 뷰

    TextView teacheremailshobox;//선생님 이메일 보이는 텍스트 뷰
    EditText teachernameshowbox;//선생님 이름 바꿀수있는 에딧텍스트
    EditText teachershortsentence;//선생님의 짧은 한마디.
    EditText teachercareer;//선생님 경력 적기
    EditText teachergreeting;//선생님이 학생들에게 하는 인사말
    Button updateportfoliobtn;//포트폴리오 업데이트 버튼
    Button previeportfoliobtn;//포트폴리오 미리보기 버튼
    Toolbar toolbar;//선생님  업데이트 프로필  툴바 선언.
    String teachercountry;//선생님  나라 문자열로 받기 위한  변수

    String originalteachernametext;//선생님 오리지널 이름
    String originalteacheronesentencetext;//선생님 오리지널 한문장
    String originalteachercareertext;//선생님 오리지널  커리어
    String originalteachergreeting;

    File uploadFile1;
    String uploadFilenochange;
    String uploadFilenoprofile;
    //Checkpermission checkpermission = new Checkpermission(permissions, TeacherProfileUpdate.this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changemyinfo_teacher);

        toastcustomer = new Toastcustomer(getApplicationContext());//커스톰 토스트 선언

        updateteacherprofile = (ImageView) findViewById(R.id.updateteacherprofile2);//선생님 프로필 업데이트 와 연결
        teacheremailshobox = (TextView) findViewById(R.id.teacheremailshow2);//선생님의 이메일이 보여지는 칸
        teachernameshowbox = (EditText) findViewById(R.id.teachernameeditbox);//선생님 이름 바꿀수 있는 에딧텍스트
        teachershortsentence = (EditText) findViewById(R.id.teachershortsentencebox);//선생님의 짧은 한마디 입력하는 에딧텍스트
        teachercareer = (EditText) findViewById(R.id.teachercareereditbox);//선생님의 카리어(경력)쓰는 에딧텍스트
        teachergreeting = (EditText) findViewById(R.id.hellotostudentedittext);//학생들에게 하는 인사말
        updateportfoliobtn = (Button) findViewById(R.id.btnforupadateteacherporfoloio);//선생님 프로필 업데이트 하는 버튼
        previeportfoliobtn = (Button) findViewById(R.id.btnforpreviewteacherportfolio);//선생님 프로필 미리보기 버튼




        SharedPreferences getid = getSharedPreferences("loginteacherid", MODE_PRIVATE);//로그인 아이디  쉐어드에 담긴거 가져옴
        final String loginedid = getid.getString("loginidteacher", "");//로그인 아이디 가져옴


        retrofit = new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create()).build();
        apiService = retrofit.create(ApiService.class);


        //전체  부분을  받아오는  리트로핏  call 부분
        Call<teacherinforesult> getteacherinfo = apiService.sendemailtogetteacherprofile(loginedid);//
        updateteacherprofile.setOnClickListener(new View.OnClickListener() {//프로필 이미지 를 눌렀을때  시작되는 이벤트
            @Override
            public void onClick(View view) {// 프로필을 눌렀을 때 진행됨.
                new AlertDialog.Builder(TeacherProfileUpdate.this)
                   .setTitle("Change profile image")// 사진 촬영  다이얼로그  제목
                   .setItems(new CharSequence[]{ "return to original", "Take photo", "Goto Album ", "Cancel"},
                    new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                                        // The 'which' argument contains the index position
                                        // of the selected item
                    switch (which) {


                         case 0://원상태로 이미지를 복귀할려면 다시 서버에서 해당 이미지를 받아와야 한다.

                         Call<teacherinforesult> getteacherinfo = apiService.sendemailtogetteacherprofile(loginedid);//studentinforesult 클래스를 return 타입으로
                         getteacherinfo.enqueue(new Callback<teacherinforesult>() {
                          @Override
                          public void onResponse(Call<teacherinforesult> call, Response<teacherinforesult> response) {

                            try {
                                  //이미지 경로 만  다시 받아오면 됨.
                                  // Log.d("dd",response.body().getProfilepath());//이미지 경로 받아오는지 확인함.
                                  URL url = new URL("http://13.209.249.1/" + response.body().getProfilepath());//해당 이미지 url 받아옴
                                  URLConnection conn = url.openConnection();//
                                  conn.connect();//url과 커넥트함
                                  BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());//인풋스트림으로  받아오기 사진
                                  Bitmap bm = BitmapFactory.decodeStream(bis);
                                  bis.close();//인풋스트립 닫음
                                  updateteacherprofile.setImageBitmap(bm);//받아온 bitmap값 학생 프뢸사진에 넣어줌.

                                  } catch (MalformedURLException e) {
                                      e.printStackTrace();
                                  } catch (IOException e) {
                                      e.printStackTrace();
                                  }

                                  }

                                  @Override
                                  public void onFailure(Call<teacherinforesult> call, Throwable t) {
                                  toastcustomer.showcustomtaost(null,  "Fail to take member info");
                                  finish();//실패하면 해당 엑티비티 꺼짐.
                                  }
                                  });

                                  changechekinteacherprofile=0;  //사진 체크 0은  변화없음 의미

                                  break;//case1

                                  case 1:

                                    takePhoto();// 사진찍기  함수 실행
                                    changechekinteacherprofile=2;//사진 체크 2는  사진이 바뀜 의미

                                    break;//case2

                                  case 2:

                                    goToAlbum();//앨범에서 픽 함수 실행
                                    changechekinteacherprofile=2;//사진 체크 2는  사진이 바뀜 의미
                                    break;//case3

                                  case 3:

                                     dialog.dismiss();///취소 버튼을 눌러서  다이얼로그 취소됨.
                                     break;//case4

                              }//switch문 끝
                             }//onclick 끝  //다이얼로그 인터페이스중
                            })
                        .create().show();//다이얼로그 보여줌


                    }//프로필 이미지 onclick 끝
                  });//프로필 이미지 눌렀을때 이벤트 끝



        getteacherinfo.enqueue(new Callback<teacherinforesult>() {

            @Override
            public void onResponse(Call<teacherinforesult> call, Response<teacherinforesult> response) {

                try {

                    originalteachernametext=response.body().getName();
                    originalteachercareertext=response.body().getCareer();
                    originalteacheronesentencetext=response.body().getShortsentence();
                    originalteachergreeting=response.body().getHellowtostudent();

                    teachernameshowbox.setText(originalteachernametext);//영어이름 들어가는 텍스트에 응답받은 json파일중 get name을 사용해서 해당값 받아옴
                    teacheremailshobox.setText(response.body().getEmail());//이메일 들어가는 텍스트에 응답받은 json 파일중  email값 받아옴.
                    teachershortsentence.setText(originalteacheronesentencetext);// 선생님의  짧은문장으로  보여주기.
                    teachercareer.setText(originalteachercareertext);// 선생님의  커리어 받아오기
                    teachergreeting.setText(originalteachergreeting);//선생님이 학생들에게 하는 인사말.



                    teachercountry = response.body().getTeachercountry();//선생님  나라이름 가져오기

                    // Log.d("dd",response.body().getProfilepath());//이미지 경로 받아오는지 확인함.
                    URL url = new URL("http://13.209.249.1/" + response.body().getProfilepath());//해당 이미지 url 받아옴
//                    URLConnection conn = url.openConnection();//dddd
//                    BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());//인풋스트림으로  받아오기 사진
//                    Bitmap bm = BitmapFactory.decodeStream(bis);
//                    bis.close();//인풋스트립 닫음
//                    updateteacherprofile.setImageBitmap(bm);//받아온 bitmap값 학생 프뢸사진에 넣어줌.

                    //위처럼  진행하면   노티피케이션  가져올때  url연결 부분에서  비동기 처리가안됨
                    //그래서 glide 라이브러리를 사용함.
                    Glide.with(TeacherProfileUpdate.this).load(url).into(updateteacherprofile);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<teacherinforesult> call, Throwable t) {
                toastcustomer.showcustomtaost(null, "Fail to take member info");
                finish();//실패하면 해당 엑티비티 꺼짐.
            }
        });//전제 정보를 담아오는 리트로핏 끝


        toolbar = (Toolbar) findViewById(R.id.realtoolbarforupdateprofileteacher2);//툴바 선언
        setSupportActionBar(toolbar);//툴바에  액션바  서포트 연결
        getSupportActionBar().setDisplayShowTitleEnabled(false);//엑션바에서 타이틀 안보이게함
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//액션바에서 Homeashup 버튼 가능하게함
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.whiteback);//해당 버튼  뒤로가기 버튼모양 으로  이미지 지정
        //아래  onOptionitemselected로 연결됨.


        //학생쪽 사이드에서 프로필  미리 보기  버튼
        previeportfoliobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent startpreviewteacherprofile = new Intent(TeacherProfileUpdate.this, PreviewTeacherProfile.class);//명시적 인텐트


                Bitmap image = ((BitmapDrawable) updateteacherprofile.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 30, stream);
                //quality 부분에서 100에 놓으니  사진이  1mb를 넘길경우가 있고  1메가 바이트를 넘겼을때
                //인텐트로 바이트 값을 보낼수 가 없는 현상이 생겻다.
                //그래서  quality를 30으로  낮췄다.

                byte[] byteArray = stream.toByteArray();



                startpreviewteacherprofile.putExtra("teacherphotobitmap", byteArray);//선생님 프로필 사진 비트맵으로 넘겨줌.
                startpreviewteacherprofile.putExtra("teachername", teachernameshowbox.getText().toString());//선생님 이름 가져옴
                teachercountry = teachercountry.replace("\n", "").replace("\r", "");//\n\r부분이 선생님 나라  텍스트에 같이 달려와서   replace함수로 없애줌.
                startpreviewteacherprofile.putExtra("teachercountry", teachercountry);//선생님 나라 넘겨줌.

                startpreviewteacherprofile.putExtra("teachercareer",teachercareer.getText().toString());//선생님 커리어 넘겨줌.
                startpreviewteacherprofile.putExtra("teacheronesentence",teachershortsentence.getText().toString());//선생님 한마디 넘겨줌.
                startpreviewteacherprofile.putExtra("teachersayhellow",teachergreeting.getText().toString());//선생님 학생들에게 하는 인사말 넘겨줌.

                startActivity(startpreviewteacherprofile);// 프로필 미리보기 로 시작됨.

            }
        });//학생쪽 사이드에서 프로필 미리보기 버튼  끝


        //프로필 업데이트 완료 버튼
        updateportfoliobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //이곳에서 프로필 업데이트 내용 들어가야됨.

                //Express your  class in a short sentenece


                if(changechekinteacherprofile==2) {
                    uploadFile1 = new File(mCurrentPhotoPath);//선생님 이미지 파일
                }else{

                    uploadFile1= new File("1");
                }
                String teacheremailtext = teacheremailshobox.getText().toString();//선생님 이메일 텍스트
                String teachercareertext = teachercareer.getText().toString();//선생님 커리어 텍스트
                String teacheronsentencetext = teachershortsentence.getText().toString();//선생님 원 센텐스 텍스트
                String teachersayhellowtostudenttext = teachergreeting.getText().toString();//선생님 인사말 텍스트
                String teachernametext= teachernameshowbox.getText().toString();//선생님 이름 텍스트




                RequestBody teacheremail = RequestBody.create(MediaType.parse("text/plain"), teacheremailtext);// 서버에서 구별하기위한 선생님 이메일
                RequestBody teachername= RequestBody.create(MediaType.parse("text/plain"),teachernametext);
                RequestBody teachercareer = RequestBody.create(MediaType.parse("text/plain"), teachercareertext);//선생님 커리어
                RequestBody teacheronsentence = RequestBody.create(MediaType.parse("text/plain"), teacheronsentencetext);//선생님 한마디
                RequestBody teachersayhellowtostudent = RequestBody.create(MediaType.parse("text/plain"), teachersayhellowtostudenttext);//선생님 학생들에게 인사
                RequestBody reqFile = RequestBody.create(MediaType.parse("image/jpeg"), uploadFile1);//프로필 사진;


                if (changechekinteacherprofile == 0) {//이미지의 변화가 아무것도 없을때이다.
                      //받아오는 값은  이미  on create될때  정해지니까  null 값으로 정해저 있음.

                    if( changenull(originalteachercareertext).equals(teachercareertext) && changenull(originalteachergreeting).equals(teachersayhellowtostudenttext) && changenull(originalteachernametext).equals(teachernametext) &&  changenull(originalteacheronesentencetext).equals(teacheronsentencetext)){
                       //여기서  아무일도 일어나지 않아 ,  아래  토스트를  내보낸다.

                        Log.d("changeconfrim", changenull(originalteachercareertext)+"a");//+> changenull로  null 값이  ""으로 치환 되는지 보여줌.

                        toastcustomer.showcustomtaost(null,"Profile has Nothing changed!!");
                    }else {
                       //이경우에는  변화가 있는 것이므로   아래  리트로핏 코드가 진행된다.

                        Call<ResponseBody> updateteacherporifle = apiService.updateteacherprofile(teacheremail, teachername, null, teachercareer, teacheronsentence, teachersayhellowtostudent);
                        updateteacherporifle.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                try {

                                    // toastcustomer(null,response.body().string());
                                    Log.d("이거닷성공", response.body().string());
                                    finish();

                                } catch (IOException e) {

                                    e.printStackTrace();

                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                                Log.d("이거닷실패", t.toString());
                            }
                        });

                    }

                //이미지 변화없을때 조건문 끝
                }else if(changechekinteacherprofile==2){//이미지 변화가 있을때이다.

                    Call<ResponseBody> updateteacherporifle = apiService.updateteacherprofile(teacheremail,teachername, reqFile, teachercareer, teacheronsentence, teachersayhellowtostudent);
                    updateteacherporifle.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {

                                // toastcustomer(null,response.body().string());
                                Log.d("이거닷성공", response.body().string());
                                finish();

                            } catch (IOException e) {

                                e.printStackTrace();

                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                            Log.d("이거닷실패", t.toString());
                        }
                    });

                }//이미지 변화 있을때  조건문 끝
              }//onclick  눌렀을때 끝
           });//프로필 업데이트 버튼 실행 이벤트 끝


         }//on create 끝남.

       public String changenull(String  a){//null 값을 ""으로  치환 해줄떄 사용하기 위해 만든   메소드이다.
                                          //위의 경우  null 값과 ""값이 달라서  식이 진행이 되지 않는 경우가 생김.
              if(a==null){//a가 null 일떄
                 a="";//"" 으로 치환해줌.
              }
              return a;//""치환된값   string 으로  리턴
    }


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


    private void takePhoto() {//사진을 찍는 함수.

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //사진을 찍기 위하여 설정합니다.(암시적 인텐트)
        //MediaStore 클래스에  ACTION_IMAGE_CAPTURE 이라는 상수가 저장됨.
        //ACTION_IMAGE_CAPTURE 는 카메라를 불러서 이미지를 캡쳐하고 리턴해준다고 한다.


        File photoFile = null;//사진 파일 경로 null 값으로 리셋
        try {
            photoFile = createImageFile();// createimagefile  함수 실행  이함수의 return값이 photofile값이 됨.->  이미지의  임시 경로를 받아와서 넣어줌
        } catch (IOException e) {//오류가 났을때

            Toast.makeText(TeacherProfileUpdate.this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
        }

        //위에서  이미지  임시 경로를 받아와서 넣어줬기 때문에  null값이 아님.
        //아래 진행됨.
        if (photoFile != null) {//만약에 사진 파일 경로가 NULL값이 아니면,

//            if (photoFile != null) {
//                Uri photoURI = Uri.fromFile(createImageFile());
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
//            }
            //위에 경우에는 sdk  24이하 일때 사용되 던  방법.

            photoUri = FileProvider.getUriForFile(TeacherProfileUpdate.this,
                    "com.example.leedonghun.speakenglish.provider", photoFile);//sdk 24이상일떄  파일 프로바이더 클래스를  사용하여  uri에  임시 엑세스 권한을 부여한다.
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri); //사진을 찍어 해당 Content uri를 photoUri에 적용시키기 위함
            if (intent.resolveActivity(getPackageManager()) != null) {///혹시나  해당  인텐트를  수용할  구성요소가  없다면  resolveActivity(getPackageManager()) != null를  이용해  비교해줘야됨
                //안그러면  수용 가능한  구성요소가 없을때  null 값일때  앱이 멈처버림  하지만 이렇게 조건문 걸어 놓으면  null값일때  아무 변화  안일어나는 걸로 끝남.

                startActivityForResult(intent, PICK_FROM_CAMERA);//  이렇게 해서  해당  MediaStore.ACTION_IMAGE_CAPTURe를  진행시키고  다시 결과값을 picfrom camera로 request코드를 넣어서 보냄.
            }
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());//현재 시간을 나타내는 타임 스탬프 변수이다.

        String imageFileName = "IP" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/test/"); //test라는 경로에 이미지를 저장하기 위함
        if (!storageDir.exists()) {//만약에  test라는 경로가 존재 하지 않을 경우
            storageDir.mkdirs();// 해당  경로를  만들어준다 .
        }
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);//임시  파일  경로  만들어줌 .
        //static File createTempFile(String prefix, String suffix, File directory)
        // 새로운 임시파일을 파일 이름에 prefix와 suffix를 붙여 directory 폴더에 생성한다.

        mCurrentPhotoPath = image.getAbsolutePath();//해당 임시 파일의  이미지 절대경로 받아서 해당 변수에 넣어줌.

        return image;//임시  파일 경로가 보내짐.
    }

    private void goToAlbum() {

        Intent intent = new Intent(Intent.ACTION_PICK);//  앨범  픽을 위해
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
            intent.putExtra("aspectX", 10);
            intent.putExtra("aspectY", 10);
            intent.putExtra("scale", true);
            File croppedFileName = null;
            try {
                croppedFileName = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            File folder = new File(Environment.getExternalStorageDirectory() + "/test/");
            File tempFile = new File(folder.toString(), croppedFileName.getName());

            photoUri = FileProvider.getUriForFile(TeacherProfileUpdate.this,
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

    }// crop image끝


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FROM_ALBUM) {
            if (data == null) {
                return;
            }
            photoUri = data.getData();
            cropImage();


        } else if (requestCode == PICK_FROM_CAMERA) {
            cropImage();
            MediaScannerConnection.scanFile(TeacherProfileUpdate.this, //앨범에 사진을 보여주기 위해 Scan을 합니다.
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

                updateteacherprofile.setImageBitmap(thumbImage);
                //changechek=1;//사진이 새로운걸로 들어왔으니까  수정 사항이 있는걸로 체크
                toastcustomer.showcustomtaost(null,  "Image will be changed after finish update");


            } catch (Exception e) {
                Log.e("ERROR", e.getMessage().toString());
            }
        }
    }//onactivityresult 끝

}//클래스 끝남.
