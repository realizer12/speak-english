package com.example.leedonghun.speakenglish;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
//import android.support.annotation.Nullable;
//import android.support.annotation.RequiresApi;
//import android.support.design.widget.TabLayout;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.support.v4.content.CursorLoader;
//import android.support.v4.content.FileProvider;
//import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kakao.util.helper.FileUtils;
import com.kakao.util.helper.log.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import static com.kakao.auth.StringSet.file;

/**
 * speakenglish
 * Class: MakeidForteacher.
 * Created by leedonghun.
 * Created On 2018-12-28.
 * Description:강사 회원 가입을 위한  정보를 받아들이는 부분이다.
 * 이메일 인증 부분이 들어간다.
 */
public class MakeidForteacher extends AppCompatActivity {

    final int FACEDETECTIONRESULT=101;
    final int FACEDETECTIONALBUM=102;

    final int[] emailcheck1 = {0};// 이메일  인증 확인 했는지 여부 체크
    int natglo=0;//네이티브인지 글로벌인지 여부
    //사진 관련 필요한  변수들
    private static final int PICK_FROM_CAMERA = 1; //카메라 촬영으로 사진 가져오기
    private static final int PICK_FROM_ALBUM = 2; //앨범에서 사진 가져오기
    private static final int CROP_FROM_CAMERA = 3; //가져온 사진을 자르기 위한 변수
    Uri photoUri;

    private String mCurrentPhotoPath;
    //사진 관련 필요한 변수들 끝


    ImageView profileimg;

    private Toastcustomer toastcustomer;//커스톰 토스트
    private Checkpermission checkpermission;//권한  체크클래스




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.makeidforteacher);

        Log.v("check",getLocalClassName()+"의  onCreate 실행됨.");

        final Animation wrongshake = AnimationUtils.loadAnimation(MakeidForteacher.this, R.anim.shakeedittext);//쉐이크하는 애니메이션 선언

        Button maketeacheridfinish = (Button) findViewById(R.id.finalmaketeacheridbtn);//선생 가입 완료 버튼
        Button maketeacheridcancel = (Button) findViewById(R.id.maketeacheridcancel);//선생 가입 취소
        final Button chekemailbtnforteacher = (Button) findViewById(R.id.checkemailbtn);//선생님 이메일 인증 버튼
        final EditText teacherpasswd = (EditText) findViewById(R.id.teacherpasswd);//회원가입때 들어가는 선생 비밀번호
        final EditText teacherpasswdcheck = (EditText) findViewById(R.id.teacherpasswdcheck);//선생비밀번호 한번더 확인
        final EditText teacheremail = (EditText) findViewById(R.id.teacheremail);//회원가입때 들어가는 선생 이메일
        final EditText teacherenglishname = (EditText) findViewById(R.id.teachername);//회원가입때 들어가는 선생 영어이름
        final TextView nativeornottxt = (TextView) findViewById(R.id.nativeorglobal);//글로벌인지 네이티브인지 보여주는 텍스트뷰
        final TextView countrycodetxt = (TextView) findViewById(R.id.countcode);//나라 코드
        final TextView countryname = (TextView) findViewById(R.id.countryname);//나라 이름 코드

        //프로필 사진
        profileimg = (ImageView) findViewById(R.id.teacherprofileimg);
        toastcustomer=new Toastcustomer(getApplicationContext());//커스톰토스트  선언
        NetworkUtil.setNetworkPolicy();//서버아 네트워크연결하기위한 정책 설정.



        //프로필 이미지 클릭시  다이얼로그로  사진 찍기, 앨범에서 사진 가져오기, 취소 버튼이 보이는 알럴트 다이얼로그 보여준다.
        profileimg.setOnClickListener(new View.OnClickListener() {

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


                new AlertDialog.Builder(MakeidForteacher.this)
                        .setTitle("Select your profile image")// 사진 촬영  다이얼로그  제목
                        .setPositiveButton("Cancel", cancelListner)//사진촬영  다이얼로그 취소버튼
                        .setNeutralButton("Camera", cameraListner)//사진촬영 다이얼로그 카메라
                        .setNegativeButton("Album", albumListner)//사진촬영 다이얼로그 앨범 버튼
                        .setCancelable(false)//다이얼로그가  화면의 다른곳을  클릭해도  취소되지 않음.
                        .show();//보이기
            }//onclick 끝
        });//프로필 이미지 클릭시  다이얼로그  뜨는 곳  끝





        final Spinner spinner = (Spinner) findViewById(R.id.countryspinner);//나라고르는  스피너 를 선언한다.
        final ArrayList<String> countrylist = getcountries("countrylist.json");//국가 리스트
        final ArrayList<String> countrycode = countrycode("countrylist.json");//국가 코드
        final ArrayList<String> nativeornot = getglobalornative("countrylist.json");//국가 별 native or  global 비교
        spinner.setSelection(0);//스피너를 고른다.

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {//스피너 값을 고를때마다  발생하는 이벤트를  진행하는 리스너이다.
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // int a = spinner.getSelectedItemPosition();//나라 고르는 스피너의 포지션 값이다./따로 스피너로부터  값을 받을때

                int a=position;//나라 고르는 스피너의 포지션 값이다.
                if (a == 0) {//포지션값이 0일때는 select country부분이어서
                    //해당 값들이  null인 상태이다
                    //null값이므로 selectcountry와   global or native부분에는  nothing이  보일 것임.
                    nativeornottxt.setText(null);
                    countrycodetxt.setText(null);
                    countryname.setText(null);

                } else if (a > 0) {
                    //해당 스피너 포지션이 0이상일때 그의 맞는 각 포지션 별로의 값을 넣어준다.
                    nativeornottxt.setText(nativeornot.get(a));
                    countrycodetxt.setText(countrycode.get(a));
                    countryname.setText(countrylist.get(a));
                }
            }
            //스피너를 사용한아이템 셀렉티드 끝

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //스피너에 아무것도  셀렉트 되어있지 않을때
            }
        });
        ///////////////////////////스피너  아이템  셀렉트 리스너  끝


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, R.id.spinnertxt, countrylist);
        //어레이리스트중 나라 이름이 들어간 리스트를  띄어 준다.
        spinner.setAdapter(adapter);//스피너 어뎁터에 위 어뎁터를 결합시켜줌.





        //회원가입 완료 창을 눌렀을때 필요한 정보들이  edittext들 안에 들어있는지 여부를 확인하고 들어있지 않으면 토스트를 보내고 ui색을 바꿔준다.
        //그리고  정보들이 모두 있으면 값을 db에   저장한다.
        maketeacheridfinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (teacheremail.getText().toString().replace(" ", "").equals("")) {
                    //이메일 쓰는 곳에 아무것도 없이 공백이 있을경우 " "이런경우는 ""으로 변환해서
                    //공백으로 처줌
                    toastcustomer.showcustomtaost(teacheremail, "insert your email on the box!");//토스트 나옴
                     teacheremail.startAnimation(wrongshake);

                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(teacheremail.getText().toString()).matches()) {//이메일  형식이 틀릴때
                    toastcustomer.showcustomtaost(teacheremail, "follow the email format!");
                    teacheremail.setText(null);//형식이 틀렸으므로  다시 쓰라고  에딧 텍스트의 값을 null로 바꾼다.
                    teacheremail.startAnimation(wrongshake);

                } else if (teacherpasswd.getText().toString().replace(" ", "").equals("")) {//패스워드  부분이  공백일때
                    toastcustomer.showcustomtaost(teacherpasswd, "insert your password!!");
                    teacherpasswd.startAnimation(wrongshake);

                } else if (!Pattern.matches("^(?=.*[a-zA-Z]+)(?=.*[!@#$%^*+=-]|.*[0-9]+).{2,16}$", teacherpasswd.getText().toString())) {//패스워드 부분이  정규식으로  맞지 않을때 (영어, 숫자외 다른 언어일때)

                    toastcustomer.showcustomtaost(teacherpasswd, "follow the password format!");
                    teacherpasswd.setText(null);//형식이 틀렸으므로  다시 쓰라고  에딧 텍스트의 값을 null로 바꾼다.
                    teacherpasswd.startAnimation(wrongshake);

                } else if (teacherpasswdcheck.getText().toString().replace(" ", "").equals("")) {//패스워드 체크 부분이 공백일때
                    toastcustomer.showcustomtaost(teacherpasswdcheck, "insert your password again!");
                    teacherpasswdcheck.startAnimation(wrongshake);

                } else if (!(teacherpasswdcheck.getText().toString().equals(teacherpasswd.getText().toString()))) {//비밀번호와 비밀번호 체크란의 값이 다를때
                    toastcustomer.showcustomtaost(teacherpasswdcheck, "different password with above");
                    teacherpasswdcheck.setText(null);
                    teacherpasswdcheck.startAnimation(wrongshake);

                } else if (teacherenglishname.getText().toString().replace(" ", "").equals("")) {//영어이름이 아무것도 안써져있을때
                    toastcustomer.showcustomtaost(teacherenglishname, "insert your  name!");
                    teacherenglishname.startAnimation(wrongshake);

                } else if (!Pattern.matches("^[a-zA-Z]*$", teacherenglishname.getText().toString())) {//영어 이름이 영어만이 아닌 다른언어로 써졌을때
                    toastcustomer.showcustomtaost(teacherenglishname, "please wirte your name in english!");
                    teacherenglishname.startAnimation(wrongshake);
                    teacherenglishname.setText(null);


                } else if (nativeornottxt.getText().toString().equals("")) {//국적을  선택하지 않았을때  울리도록 만듬.
                    toastcustomer.showcustomtaost(null, "select your country!!");
                     spinner.startAnimation(wrongshake);

                }else if(mCurrentPhotoPath==null){//프로필 이미지가 올라가져있는지 여부를 판단하여  이미지가 없으면  알린다.

                    toastcustomer.showcustomtaost(null, "plz upload your profileimage");
                    profileimg.startAnimation(wrongshake);


                }else if (emailcheck1[0] == 0) {//이메일 체크가 안되어있을때


                    chekemailbtnforteacher.startAnimation(wrongshake);
                    toastcustomer.showcustomtaost(null, "click the Auth box for auth");



                } else if (emailcheck1[0] == 1) {//이메일 체크도 되어있는 상태이다

                    String nativeornotcheck=nativeornottxt.getText().toString();
                    if(nativeornotcheck.equals("global")){
                       natglo=0;
                    }else{
                       natglo=1;
                    }
                    String charset = "UTF-8";
                    File uploadFile1 = new File(mCurrentPhotoPath);
                    //toastcustomer(null,mCurrentPhotoPath);
                    String requestURL = "http://13.209.249.1/maketeacherid.php";

                    try {

                        //회원가입을 위한 모든 처리가 다 되어있으므로 maketeacherid.php로 선생님 회원 등록 정보를 보내준다.

                        MultipartUtility multipart = new MultipartUtility(requestURL, charset);
                        multipart.addFilePart("imageupload12", uploadFile1);
                        multipart.addFormField("countrycode",countrycodetxt.getText().toString());//국가 코드
                        multipart.addFormField("teacheremail",teacheremail.getText().toString());//선생님 이메일
                        multipart.addFormField("teacherpasswd",teacherpasswd.getText().toString());//선생님 패스워드
                        multipart.addFormField("teachername",teacherenglishname.getText().toString());//선생님 이름
                        multipart.addFormField("nativeornot", String.valueOf(natglo));//네이티브 또는 글로벌 여부
                        multipart.addFormField("countryname",countryname.getText().toString());//국가 이름


                        List<String> response = multipart.finish();
                        for (String line : response) {

                            if(line.equals("1")){//선생님 가입이 완료 되었을때.

                                toastcustomer.showcustomtaost(null,"welcome to sayhellow!!");
                                Intent intent=new Intent();
                                intent.putExtra("useremail",teacheremail.getText().toString());//이메일을  맨처음  로그인 화면으로 보내기 위한  인텐트
                                setResult(120,intent);//결과값을 120으로 해서  가입완료된 이메일을 맨앞으로 보낸다.


                                //중간 엑티비인 makeidactivity를  선언하고  종료시켜줘서 현재 엑티비티가 종료되면 바로
                                //맨앞  엑티비티가 나오도록 해준다.
                                MakeidActivity makeidActivity=(MakeidActivity) MakeidActivity.MakeidActivity;
                                makeidActivity.finish();
                                MakeidForteacher.this.finish();// 맨앞으로 가야됨.

                            }else{//선생님 가입  중 서버 처리부분에서 에러가 있을때.
                                Log.v("이거닷",line);
                                toastcustomer.showcustomtaost(null,"there are some error detected"+line,1500,300);

                                //line에서  2가 나오면  프로필 이미지 서버에 올리는 부분에서 잘못됨.
                                //3이나오면 -> 해당  선생님 정보를 디비에  넣는게 잘못됨.
                                //4가  나오면  -> 해당 선생님  uid를 가져오는 부분에서 잘못됨.
                                //5가  나오면  -> 해당 선생님  오픈 채팅방을  서버에 넣어주는 부분에서 문제가  생김.
                                //6나오면 ->  chatuserconinfo에  해당 선생님과 해당 오픈채팅방  연결여부 진행해주는 곳에서 에러가 생겼을 경우이다.
                            }

                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }//이메일 체크도 되어있는 상태 끝
            }
        });//가입완료 버튼 눌렀을경우 끝


        //메일 인증 버튼 눌렀을때
        chekemailbtnforteacher.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //버튼 눌렀을떄 진행되는 곳

                if (teacheremail.getText().toString().replace(" ", "").equals("")) {
                    //이메일 쓰는 곳에 아무것도 없이 공백이 있을경우 " "이런경우는 ""으로 변환해서
                    //공백으로 처줌
                    toastcustomer.showcustomtaost(teacheremail, "insert your email on the box!");//토스트 나옴


                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(teacheremail.getText().toString()).matches()) {//이메일  형식이 틀릴때

                    toastcustomer.showcustomtaost(teacheremail, "follow the email format!");
                    teacheremail.setText(null);//형식이 틀렸으므로  다시 쓰라고  에딧 텍스트의 값을 null로 바꾼다.


                } else {//이메일이 써졌고 써진 내용이 이메일 형식이 맞을때

                    try {
                        PHPRequest request = new PHPRequest("http://13.209.249.1/realemailcheckforteacher.php");//이메일  중복 여부를 체크하낟.
                        String result = request.PhPtest(teacheremail.getText().toString(), "", "", "");

                        if (result.equals("1")) {
                            toastcustomer.showcustomtaost(null, "you can use this mail!");
                            try {
                                PHPRequest request1 = new PHPRequest("http://13.209.249.1/PHPMailer/testemail.php");//메일 보내기 서버 코드로 연결
                                String result1 = request1.PhPtest(teacheremail.getText().toString(), "", "", "");
                                if (result1.equals("1")) {//메일 보내기에 성공 하였음

                                    String tors = "teacher";
                                    //메일 보내는데  성공 하였을때 나오는 칸입니다.
                                    toastcustomer.showcustomtaost(null, "check your emailbox in 3minutes!");//메일을 보내는데 성공 메세지를 보냄.
                                    Intent emailcheckresult = new Intent(MakeidForteacher.this, EmailCheckPage.class);
                                    emailcheckresult.putExtra("emailforcheck", teacheremail.getText().toString());
                                    emailcheckresult.putExtra("teacherofstudent", tors);
                                    // 이메일 체크하는곳에서 이메일 인증번호가 알맞게 들어가야지 emailcheck값을1로
                                    //받을수 있으므로  사용가능한 메일일때 바로 emailcheck값을 바꾸지 않고  startactivyforresult로  값의  진위여부를 확인후  맞을때 체크를 1로 바꿔준다.
                                    startActivityForResult(emailcheckresult, 3);

                                } else {
                                    toastcustomer.showcustomtaost(null, "there is an error while sending mail!");//메일을 보내는데 있어서  오류가 생김.
                                }
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }

                        } else if (result.equals("2")) {//임시용 디비 또는회원 (학생, 강사)용 디비에  해당 메일이 있어서 중복되는경우이다.
                            toastcustomer.showcustomtaost(null, "someone use this mail!");
                            emailcheck1[0] = 0;
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }//이메일이 써졌고 써진 내용이 이메일 형식이 맞을때 끝
            }//버튼 눌렀을떄 진행되는 곳끝
        });////메일 인증 버튼 눌렀을때 끝



        //회원가입창을  취소를 눌러 현재 엑티비틀  피니쉬 해버린다.
        //이렇게 하여  스택이 쌓이지 않고 바로 전단계 엑티비티가 보이도록 진행하였다.
        maketeacheridcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MakeidForteacher.this.finish();


            }
        });//회원가입 취소버튼을 눌렀을때 끝


        final TextView mentforcheckedemail = (TextView) findViewById(R.id.mentforcheckedemailteacher);//메일 인증되었을때 보이는 인증 멘트
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {//텍스트 변화 전

            }


            @SuppressLint("NewApi")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {//텍스트가 변화되는 중일때
                if (emailcheck1[0] == 1) {

                    chekemailbtnforteacher.setText("AUTH");//메일 버튼의  글
                    mentforcheckedemail.setVisibility(View.INVISIBLE);//메일 인증되었을때 보이는 인증 멘트
                    emailcheck1[0] = 0;//이메일 체크 0
                    teacheremail.setBackground(getDrawable(R.drawable.border));//학생 이메일 백그라운드

                }

            }


            @SuppressLint("NewApi")
            @Override
            public void afterTextChanged(Editable edit) {//텍스트가 변화가 되었을때
                if (emailcheck1[0] == 1) {

                    chekemailbtnforteacher.setText("AUTH");
                    mentforcheckedemail.setVisibility(View.INVISIBLE);
                    emailcheck1[0] = 0;
                    teacheremail.setBackground(getDrawable(R.drawable.border));

                }
            }
        };//텍스트 리스너  종료

        teacheremail.addTextChangedListener(textWatcher);//강사 이메일 란에  인증이 된후 에  다시  이메일을 수정할때 이메일인증이 풀리는 효과를 줌.

    }
    //oncreate끝/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////




    @Override
    protected void onResume() {
        super.onResume();

        Log.v("check",getLocalClassName()+"의  onreume() 실행됨.");

        //onresume에  권한 체크를 넣은 이유는  혹시나 안드로이드 시스템 스택에 현재 엑티비티가  실행중인 상태에서  잠시
        //설정창을 들어가서  사용자가  권한 승인 취소를 하는 행위를 막기위해서  다시  background 에서  foreground올라올떄,
        //권한 체크를 한번더 해준다.

        //Checkpermission클래스를 실행해서   권한 들의   현재  상태를  리턴받는다.
        //혹시나  사용중  사용자가  앱 실행시 승인했던 권한을 다시 취소하면,  다시 돌아가서  권한승인을 하도록
        //Requestpermisssion 엑티비티로 넘겨준다.
        //checkpermission 클래스 선언
        checkpermission=new Checkpermission( MakeidForteacher.this);

        //checkpermission클래스의  checkpermission클래스 함수 리턴값 받기
        boolean permissioncheck=checkpermission.checkPermissions();

        //리턴값이 false이면 승인이 취소된 권한이 있는것 이므로,  requestpermisssion 클래스 로 다시 보내준다.
        if(!permissioncheck){

            Log.v("check","권한 중  승인 안된  권한이 발견되어  requestpermission엑티비티로 다시 넘어간다.");
            Intent intent=new Intent(MakeidForteacher.this,RequestPermission.class);
            startActivity(intent);

            //현재 activity를 종료시키는 이유는  Requestpermission 엑티비티에서  권한을 다  승인 했을 경우에  다시 로그인 엑티비티로 넘어갈텐데,
            //이때, 현재 activity를  종료시켜놓지 않으면  스택에 남아있어서  로그인 엑티비티가  스택에  두번  올라가지기 때문이다.
            //뒤로가기 누르다가 발견함.
            finish();
        }

    }//onreumes 끝





    private void takePhoto() {

        Intent intent=new Intent(MakeidForteacher.this,FaceDetectionCamera.class);
        startActivityForResult(intent,FACEDETECTIONRESULT);

    }



    private void goToAlbum() {

        Intent intent=new Intent(MakeidForteacher.this,FaceDetectionAlbum.class);
        startActivityForResult(intent,FACEDETECTIONALBUM);

    }



    public ArrayList<String> getcountries (String filename){
        //  나라이름 json파일 에서  나라 이름들을  어레이리스트에 넣기 위한 메소드이다.
        JSONArray jsonArray=null;
        ArrayList<String> cList=new ArrayList<String>();

        try {
            InputStream is=getResources().getAssets().open(filename);
            int size=is.available();
            byte[] data=new byte[size];
            is.read(data);
            is.close();
            String json=new String(data,"UTF-8");
            jsonArray=new JSONArray(json);
            if(jsonArray!=null){
                for(int i=0; i<jsonArray.length(); i++){
                    cList.add(jsonArray.getJSONObject(i).getString("name"));

                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
         catch (JSONException je){
            je.printStackTrace();
        }
         return cList;
    }

    //나라이름 json파일에서  각  나라 순서별 붙여진 global  native를 구분한내용을  가져오기위한 어레이리스트이다..
    public ArrayList<String> getglobalornative (String filename){

        JSONArray jsonArray=null;

        ArrayList<String> nativeornot=new ArrayList<String>();
        try {
            InputStream is=getResources().getAssets().open(filename);
            int size=is.available();
            byte[] data=new byte[size];
            is.read(data);
            is.close();
            String json=new String(data,"UTF-8");
            jsonArray=new JSONArray(json);
            if(jsonArray!=null){
                for(int i=0; i<jsonArray.length(); i++){
                    nativeornot.add(jsonArray.getJSONObject(i).getString("nativeorglobal"));

                }
            }
        }catch (IOException e){e.printStackTrace();}
        catch (JSONException je){je.printStackTrace();}
        return nativeornot;

    }


    //나라코드를 json파일에서  각  나라 순서별 붙여진  나라의 코드를 가지고온다.
    //나중에  관리자 페이지에서  강사 신청을 한  강사들의 목록을 볼때  각나라의 국기를 보여주기 위함이다.
    public ArrayList<String> countrycode (String filename){
        JSONArray jsonArray=null;

        ArrayList<String> code=new ArrayList<String>();
        try {
            InputStream is=getResources().getAssets().open(filename);
            int size=is.available();
            byte[] data=new byte[size];
            is.read(data);
            is.close();
            String json=new String(data,"UTF-8");
            jsonArray=new JSONArray(json);
            if(jsonArray!=null){
                for(int i=0; i<jsonArray.length(); i++){
                    code.add(jsonArray.getJSONObject(i).getString("code"));

                }
            }
        }catch (IOException e){e.printStackTrace();}
        catch (JSONException je){je.printStackTrace();}
        return code;
    }



    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        EditText emailtext=(EditText)findViewById(R.id.teacheremail);
        final Button chekemailbtn=(Button)findViewById(R.id.checkemailbtn);
        TextView mentforcheckedemail=(TextView) findViewById(R.id.mentforcheckedemailteacher);
        super.onActivityResult(requestCode, resultCode, data);

            if(resultCode==133){
                if(requestCode==3){
                String emailcheckresult=data.getStringExtra("check");

                if(emailcheckresult.equals("0")){// 이메일 체크가 안되었을때

                }else if(emailcheckresult.equals("1")){//이메일 체크가 되었을때
                    emailcheck1[0]=1;//이메일 체크 변수도 1로 체크되어 가입완료 버튼을 눌렀을때  가입이 완료 될수 있는 환경으로 만들어진다.
                   //emailtext.setFocusable(false);//아래는 이메일 체크란이  더이상 수정할수 없도록 바뀌는  코드이다.
                    //emailtext.setClickable(false);
                    chekemailbtn.setText("sucess");
                    mentforcheckedemail.setVisibility(View.VISIBLE);
                   // chekemailbtn.setFocusable(false);
                    //chekemailbtn.setClickable(false);
                    emailtext.setBackgroundColor(Color.parseColor("#53f0b4"));
                }

            }//request code==3 일때
        }

        if(resultCode==200){

            switch (requestCode){


                //얼굴 인식 카메라  결과 받아옴.
                case FACEDETECTIONRESULT:

                    profileimg.setImageURI(null);//처음에  이미지를  취소했경우를  대비하여 null값으로 보이게만듬.
                    Log.v("check", "카메라 촬영으로 가져온 얼굴인식 사진 경로 "+data.getStringExtra("result_photo_path"));
                    mCurrentPhotoPath=data.getStringExtra("result_photo_path");//절대경로 받아온 것
                    profileimg.setImageURI(Uri.parse(mCurrentPhotoPath));//이미지에 절대 경로 넣어줌

                    break;

                //얼굴  인식  앨범  결과 받아옴.
                case FACEDETECTIONALBUM:


                    profileimg.setImageBitmap(null);
                    Log.v("check", "앨범으로 가져온 얼굴인식 비트맵 파일"+data.getStringExtra("result_album_path"));
                    mCurrentPhotoPath=data.getStringExtra("result_album_path");
                    profileimg.setImageURI(Uri.parse(mCurrentPhotoPath));


                    break;

            }//스위치의 끝
        }//result==200일때


      if (requestCode == CROP_FROM_CAMERA) {
            try {

                //해당 사진을 bitmap 형태의 이미지로 가져와서  넣음 .
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                Bitmap thumbImage = ThumbnailUtils.extractThumbnail(bitmap, 128, 128);
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                thumbImage.compress(Bitmap.CompressFormat.JPEG, 100, bs); //이미지가 클 경우 OutOfMemoryException 발생이 예상되어 압축

                //여기서는 ImageView에 setImageBitmap을 활용하여 해당 이미지에 그림을 띄우시면 됩니다.
                profileimg.setImageBitmap(thumbImage);

            } catch (Exception e) {
                Log.e("ERROR", e.getMessage().toString());
            }
        }//request cropcamera result받을 때


    }//onactivityresult 끝





    @Override
    protected void onDestroy() {
        //이부분은  가입시 갑작 스러운 취소를 할경우 가입도중 중복 가입을 막기위해 임시  이메일 체크 디비에 남겨둔  디비를  지워주는  구간이다.
        super.onDestroy();

        NetworkUtil.setNetworkPolicy();//서버아 네트워크연결하기위한 정책 설정.

        if(emailcheck1[0]==1){//버튼 체크값이 1일 경우임.
            EditText emailtext=(EditText)findViewById(R.id.teacheremail);
            // 이경우에는 디비에 들어간 값들을 다시 사라지게 해준다.

            try {
                PHPRequest request = new PHPRequest("http://13.209.249.1/DeleteEmailCheck.php");//임시 이메일 디비를 지워주는 파일  부르기
                String result = request.PhPtest(emailtext.getText().toString(),"","","");

                if (result.equals("1")){

                    //성공적으로 지움


                 }else if(result.equals("2")){


                    //성공적으로 못지움
                    toastcustomer.showcustomtaost(null,"임시 저장소에서 해당 이메일이 지워지지 않았습니다.");

                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }


    }//destroy 끝

}//현재 클래스 끝
