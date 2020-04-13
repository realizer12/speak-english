package com.example.leedonghun.speakenglish;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.facebook.stetho.Stetho;

import java.util.ArrayList;
import java.util.List;

/**
 * speakenglish
 * Class: LoadingActivity.
 * Created by leedonghun.
 * Created On 2019-07-11.
 * Description:
 *
 * 로딩화면이다
 * 로딩화면의 경우  따로 layout을  만들어  붙이지 않고,
 * guswo  activity만  만들고  이 activit에  theme를 주어
 * 핸들러 이런걸로  시간초 지난다음에  넘어가는게 아닌  다음 엑티비티  준비되면  바로 넘어가도록 만들었따.
 * 그리고  여기서  넘어가기전에  권한 관련 요청이 있으면  요청을  끝내고 가게  만들었고
 * 요청 중  거부를  한  권한이  하나라도 있으면,  권한 요청이 왜 필요한지 설명해주는 RequestPermission activity로 넘어가게 만들었다.
 * 그리고 요청이 loading쪽에서  허용시켜주면,  바로 loginactivity로 넘어가도록 만들었다.
 *
 */
public class LoadingActivity extends AppCompatActivity {
    private String[] permissions;
    private final int REQUESTCODE=101;//권한요청 하고 콜백할때  받는 request 코드
    private boolean check;
    private boolean sawappdetailactivity=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //아래는  chrome://insepect에서 기기를  insepect 할수 있도록 만들어준다.
        Stetho.initializeWithDefaults(this);//steho -> 선생 학생 모두 공통적으로 싲ㄱ되는  loagingactivity 에  실행시져줌,

        Log.v("check",getLocalClassName()+"의 onCreate()가 실행");
        //아래는 받아올 권한들을  넣기  위한  string 배열이다.
        //String 배열인 이유는 ->  public static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE"; 해당  권한을  타고 올라가면,
        //String 이라는것을 알수 있따.
        //현재  manifest에 선언된 위험권한들이 들어감
        //1. READ_EXTERNAL_STORAGE
        //2. WRITE_EXTERNAL_STORAGE
        //3. CAMERA
        //4. GET_ACCOUNT
        //5. READ_CONTACT
        //현재 로딩화면에서 설정할 권한은 이렇게 5개임.
        //permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.GET_ACCOUNTS, Manifest.permission.READ_CONTACTS};
        permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.GET_ACCOUNTS, Manifest.permission.READ_CONTACTS};;;




    }//create 끝

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("check",getLocalClassName()+"의 onreume가 실행");
        //권한이  승인되어있는지를 체크하기위한  메소드

        check=checkPermissions();

        //check 값이 true-> checkpermissions()-> true값 -> permissionList에 아무것도 없음 -> 모든 권한 승인됬다는 뜻임.
        //check가 false일경우에는  승인안된  권한이 있는것이므로, else 부분 코드가 실행됨.
        if(check){


            Log.v("check","권한이  모두 승인되어있는 상태여서  loginactivity로  넘어감.");
            Intent intent=new Intent(LoadingActivity.this,LoginActivity.class);
            startActivity(intent);

            finish();

        }


    }//onReume 메소드 실행


    //*****************************************************************************************************************
    //안드로이드 마쉬멜로우 미만버전에서 앱 권한은 AndroidManifest.xml에 지정하고, 설치시에 앱이 사용하는 권한을 보여주기만함.
    //그래서 그 앱이 실제로 언제 그 권한을 사용하는지 실제로 사용하고는 있는지 등을 유저가 알 수 없었음.
    //그래서 마쉬멜로우 이상부터 실제 권한 요청시에 유저에게 권한을 사용 할지 확인 받는 과정이 추가 됨.
    //*****************************************************************************************************************
    //현재  해당  권한이  승인되어있는 상태인지 아닌지를  체크하는 메소드.
    public boolean checkPermissions(){

        //런타임 관련 앱 권한
        Log.v("check","checkpermission()실행됨.");

        int permissioncheck;//위험권한 승인  여부  return 값 받은 int 변수.

        List<String> permissionList = new ArrayList<>();
        //승인되지 않은 권한들 담는 String 어레이 리스트
        //여기서 Arraylist안쓰고  List쓸수 있는것  LIST가  인터페이스임.  -> LIST가 규정한  규칙을 따르면서,  Arraylist생성한다.

        for (String pm : permissions) {//pm에  위에서  지정한 permission들 하나씩 대입후

            //int permiCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR);-> 권한이 있는지 없는지 여부를 체크하는것이다.
            //원래 하나만 할떄 이 렇게  ContexCompat의  checkselfpermission메소드를 사용함.
            //이때, 매개변수로 1,context,  2.해당 요청할 권한이 들어간다.
            permissioncheck = ContextCompat.checkSelfPermission(LoadingActivity.this, pm);//retun값  타고 올라가면  int 0(승인), -1(승인 안됨) 로 나옴,

            Log.v("check","권한 승인=0 또는 승인안됨=-1 임." +pm +"승인여부는?"+permissioncheck);



            if (permissioncheck != PackageManager.PERMISSION_GRANTED) { //사용자가 해당 권한을 가지고 있지 않을 경우 permission리스트에 해당 권한명 추가
                permissionList.add(pm);//리스트에 해당권한 추가해줌.
                Log.v("check","권한 승인인 안되어 있어서 permissionList에 넣어줌"+permissionList);
            }
        }//for문 끝



        //위에서  권한 체크를 했을때, permissonList가 비워져있다면, 모든 권한이  승인되어있다는 것을 의미한다.
        //그래서  조건으로  permissionList가  비워지지 않았다면으로  로직 짬.
        if (!permissionList.isEmpty()) {

            //권한 요청하는 멤소드로  프로세스 팝업이 뜸.-> realm 개발자  글에 따르면, 여기서 나오는 dialog 는 커스톰 불가능하다고함.
            ActivityCompat.requestPermissions(LoadingActivity.this, permissionList.toArray(new String[0]), 101);

            Log.v("check","권한 요청 들어감.");

            return false;
        }


        return true;
    }
    //현재  핻당  권한이  승인되어있는 상태인지 아닌지를  체크하는 함수 끝







    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {


        Log.v("check","권한요청 콜백함수 실행");

       //아래  권한거절이있을경우  break a를 타고 switch를 나가야 함으로 이렇게 switch 앞에 a가 존재한다.
       a: switch (requestCode) {

            //requestcode가 101일경우
            case REQUESTCODE: {

                //나중에  승인된  권한들의  grantreulst를  담을   리스트이다.
                //이렇게하여, 전부 허용 했을때,  한번더
                List<Integer> list = new ArrayList<>();


                //grantresult의 길이가  0보다 크다는 것은  권한관련  결과가  있다는 뜻이다.
                //솔직히  현재는 권한  무조건 있는거여서 안해줘도 되지만
                //혹시 몰라서 넣음.
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {

                        Log.v("check", "" + grantResults[i]);

                        //아래 조건은  권한요청 결과중에 denied가 있는지 여부
                        if (grantResults[i] < 0) {

                            Log.v("check","권한요청중  거절된것이 있다.");

                            //거절된 권한이  존재하므로, requestpermission엑티비티로 가진다.
                            Intent intent = new Intent(LoadingActivity.this, RequestPermission.class);
                            startActivity(intent);
                            finish();

                            break a;//for문을 나오고  switch 문 까지 나와야됨  for문만 나오면 아래 loginactivity가는것도 같이 실행된다.

                        }//승인결과중에 -1 즉,  거절된 요청이 하나라도 있을때 조건  끝

                    }//for문 끝
                } //권한 요청 결과가  1나 이상일때  ->  당연히  1나이상이겠지만,  혹시 몰라서,


                //권한들이 다 승인되었으므로,  loginactivity로 넘어간다.
                showPermissioncompletewell();


        }//REQUESTCODE case 끝

        }//switch 끝

    }
    //권한 요청 callback 함수 끝


    //권한을  전체 동의 하였을때 실행되는 메소드로  loginactivity로 가진다.
    private void showPermissioncompletewell() {

        Log.v("check","권한이  모두 승인되어서  loginactivity로 넘어간다.");
        Intent intent=new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
    //권한 동의를 하지 않았을경우 알럴트 띄우기 끝






}//LoadingActivity 끝
