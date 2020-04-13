package com.example.leedonghun.speakenglish;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
//import android.support.annotation.NonNull;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * speakenglish
 * Class: Checkpermission.
 * Created by leedonghun.
 * Created On 2019-01-24.
 * Description:
 * 이클래스는  필수권한들이  승인되어있는지 여부를  각  엑티비티마다  체크하기위해  만들어 놓은  클래스이다.
 */
public class Checkpermission  {
   private String[] permissions= new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.GET_ACCOUNTS, Manifest.permission.READ_CONTACTS};;;
   private Activity activity;

//생성자로 해당 엑티비테에서 권한 체크할  권한 관련스트링 어레이리스트와 해당 엑티비를 넣는 생성자를 넣어줌.
    public Checkpermission ( Activity activity){


        this.activity= activity;
        Log.v("check","권한 체크 객체 선언됨- 생성자실행");
    }



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
            permissioncheck = ContextCompat.checkSelfPermission(activity.getApplicationContext(), pm);//retun값  타고 올라가면  int 0(승인), -1(승인 안됨) 로 나옴,


            if (permissioncheck != PackageManager.PERMISSION_GRANTED) { //사용자가 해당 권한을 가지고 있지 않을 경우 permission리스트에 해당 권한명 추가
                permissionList.add(pm);//리스트에 해당권한 추가해줌.

            }
        }//for문 끝



        //위에서  권한 체크를 했을때, permissonList가 비워져있다면, 모든 권한이  승인되어있다는 것을 의미한다.
        //그래서  조건으로  permissionList가  비워지지 않았다면으로  로직 짬.
        if (!permissionList.isEmpty()) {

        Log.v("check","필수 권한 체크 중 안된거 있음");
        return false;
        }
       Log.v("check","필수 권한들  모두 체크됨");
        return true;
    }
    //현재  핻당  권한이  승인되어있는 상태인지 아닌지를  체크하는 함수 끝


}
