package com.example.leedonghun.speakenglish;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * speakenglish
 * Class: RequestPermission.
 * Created by leedonghun.
 * Created On 2019-07-12.
 * Description:
 *
 * 이 엑티비티는  loadingactivity에서  사용자에게 권한요청을  신청했을시
 * 권한 요청 중 하나라도  거부를  하였을 경우에,  권한 요청의 필요를  알리기위해
 * 사용자에게 관련 설명을 해주는 activity이다.
 * 이곳에서 다시 권한 요청이 일어나고 사용자는  모든 권한을 승인해야지만,
 * loginactivity로 넘어갈수 있다.
 */
public class RequestPermission extends AppCompatActivity {
    private boolean check;//checkpermission의  return 값을 받을 변수
    private Button  requestbtn;//권한 요청 버튼
    private String[] permissions;//요청할 권한들이 담길  string 배열
    private  boolean sawappdetailactivity=false;//권한 설정을 하기위해  설정  창으로 이동했는지 여부

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.requestpermissionactivity);

         Log.v("check ", getLocalClassName()+"의  oncreate함수 실행됨");

        requestbtn=findViewById(R.id.buttonforrequsetpermission);//권한 요청버튼 선언

        //필요한 권한들  리스트
        //permissions=new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.GET_ACCOUNTS, Manifest.permission.READ_CONTACTS};
        permissions= new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.GET_ACCOUNTS, Manifest.permission.READ_CONTACTS};;;




        //권한요청 버튼을 클릭하면,  해당 권한들을 요청하는 DIALOG를 화면에 뛰운다.
        requestbtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               Log.v("check","권한요청 버튼 눌림.");
               //버튼이 클릭되면  checkpermission의 함수가 실행된다.
               check=checkPermissions();

               //checkpermission 함수가 true를 리턴했을 경우에  모든 권한이 승인되었으므로,  loginactivity로 간다.
              if(check){

                  Log.v("check","권한이  모두 승인되어있는 상태여서  loginactivity로  넘어감.");
                  Intent intent=new Intent(RequestPermission.this,LoginActivity.class);
                  startActivity(intent);

                  finish();

              }//check true일때 조건 끝

           }//onclick 끝

       });//권한요청 버튼 리스너 끝.



    }//onCreate 끝


    @Override
    protected void onResume() {
        super.onResume();

       Log.v("check",getLocalClassName()+"의  on resume메소드 실행");

        //아래는  권한설정창을 갔는지 여부를 체크하여  진행하는 코드다.
        //권한 설정 창을 갔다와서 바로  swapdetailactiv 변수가 true 상태일때 진행됨.
        //checkpermission()을 실행해서  모든 권한이  승인된 -> check 변수가 true일때는 다음 loginactivity이동
        //check false 일때는 다시 swappdetailactivity= false로 바꾸줌.
        if(sawappdetailactivity){


           check=checkPermissions();
           Log.v("check","여기서 이거 뭐야 "+check);

           //checkpermission 함수가 true를 리턴했을 경우에  모든 권한이 승인되었으므로,  loginactivity로 간다.
           if(check){

               Log.v("check","권한이  모두 승인되어있는 상태여서  loginactivity로  넘어감.");
               Intent intent=new Intent(RequestPermission.this,LoginActivity.class);
               startActivity(intent);

               finish();

           //check가 false일 경우에는
           }else{

               //모든 권한이 승인된것이 아니므로,  다시  sawappdetailactivity변수를 false 값으로 놔줘서 원상태로  로직들이  진행되게  바꾼다.
               sawappdetailactivity=false;
               Log.v("check","권한 중 승인 안된경우가 있음");
           }

        }//sawappdetailactivity가  true 일때


    }//onReume 메소드 실행

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
            permissioncheck = ContextCompat.checkSelfPermission(RequestPermission.this, pm);//retun값  타고 올라가면  int 0(승인), -1(승인 안됨) 로 나옴,

            Log.v("check","권한 승인=0 또는 승인안됨=-1 임." +pm +"승인여부는?"+permissioncheck);



            if (permissioncheck != PackageManager.PERMISSION_GRANTED) { //사용자가 해당 권한을 가지고 있지 않을 경우 permission리스트에 해당 권한명 추가

                //아래는  설정창 갔다와서  sawappdetailactivity true되어서  onreume 실행시 true 조건 진행되는데,
                //문제는  sawappdetailactivity가  true여서 아래  설정창만 가고 권한 승인을 안한경우에도  checkpermission이  아래  sawappdetailactivity관련 조건문을  넘어가고  true로 리턴됨.
                //이렇게 되면  승인이안되도  loginactivity로 넘어가는 경우가 발생해서
                //아래와 같이 checkpermisssion return 값이  true로 나오기전에  승인안된게 있으면 sawappdetailactivity를  false를  바꾸준다.
                sawappdetailactivity=false;


                permissionList.add(pm);//리스트에 해당권한 추가해줌.
                Log.v("check","권한 승인인 안되어 있어서 permissionList에 넣어줌"+permissionList);
            }
        }//for문 끝

     //sawappdetailactivity가  false일때이다. ->  권한설정을  안갔다온걸로 됨.
     if(!sawappdetailactivity ) {
         //위에서  권한 체크를 했을때, permissonList가 비워져있다면, 모든 권한이  승인되어있다는 것을 의미한다.
         //그래서  조건으로  permissionList가  비워지지 않았다면으로  로직 짬.
         if (!permissionList.isEmpty()) {


             Log.v("check", "권한 요청 들어감.");
             //권한 요청하는 멤소드로  프로세스 팝업이 뜸.-> realm 개발자  글에 따르면, 여기서 나오는 dialog 는 커스톰 불가능하다고함.
             ActivityCompat.requestPermissions(RequestPermission.this, permissionList.toArray(new String[0]), 101);

             //checkpermission값에  false를 리턴한다.
             Log.v("check","checkpermission()의  return 값 false");
             return false;
         }
     }//sawappdetailactivity가  false일때 조건 끝

        Log.v("check","checkpermission()의  return 값 true");
        //checkpermission의  true 값을 리턴한다.
        return true;

    }
    //checkpermissison() -> 현재  핻당  권한이  승인되어있는 상태인지 아닌지를  체크하는 메소드 끝



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
     boolean check11;
     int sum=0;
        Log.v("check","권한요청 콜백함수 실행");

        //아래  권한거절이있을경우  break a를 타고 switch를 나가야 함으로 이렇게 switch 앞에 a가 존재한다.
         switch (requestCode) {

            //requestcode가 101일경우
            case 101: {

                //shouldshowpermissionrationale ->  true 값을  리턴하면,
                for(String pm:permissions){

                    //shouldshowRequestRequestPermissionRationale ->
                    //1. 앱이 해당 권한에 대해 요청한 이력이 없는 경우(처음인 경우)  False 반환
                    //2. 다시 묻지 않기를 선택하고 거절한 이력이 있는 경우 False 반환
                    //3. 다시 묻지 않기를 선택하지 않고 거절한 이력이 있는 경우 True 반환

                    //1번의  경우  loadingactivity에서 이미  요청을 한번식 했으므로  관련해서 실행될 일이 없음.
                    //결국  2번 3번 경우로  shouldShowRequestPermissionRationale 은  리턴 값을 내줌.
                    //이때  checkbox(다시 묻지 않기) 를 선택했으면,  false를 반환,  선택안했으면  true를  반환한다.
                    //false가 반환될때는  프로세스 다이얼로그가  안뜰것이고, true 가  나올때는  프로세스다이엉ㄹ로그가  띄어져 있을것임.
                    //결국 false가 반환되었을때는  상대에게  알리는 dialog를  새로 만들어주줘야됨.
                    //이경우에는  3개의  권한 요청이있는데(사실  5개 권한이지만,  그룹으로 묶여서 진행되므로.. 3그룹),
                    //이중에서 true값이있어서  요청 버튼을 눌렀을때,  프로세스 다이얼로그가  보여야하는 경우가  한번이라도 있으면,
                    //설정창으로  옮겨지게 하는  다이얼로그가  나오지 않기로 하기위해  sum 변수를 이용해서  check를 진행한다.

                    //shouldShowRequestPermisssionRationale 이  true값을  낼때마다  sum 함수에  1을 넣어주고
                    //check값이  true가  더이상 나오지 않을때  그냥  sum==0일때  조건을 실행시켜  그 조건 안에다가
                    //설정 창으로 가는 다이얼로그 나오는 코드를 넣어준다.



                    //아래는  checkbox여부를  알기위한  함수
                    check11=ActivityCompat.shouldShowRequestPermissionRationale(RequestPermission.this,pm);
                    Log.v("check",pm+" 권한은 "+check11+" 이다.");

                    //true로  리턴이 되었을경우,  checkbox를 선택안하고 거부한  권한이므로,  아래 조건을 실행한다.
                    //sum=0이 아니게 되기 때문에 결국  설정 창가는  다이얼로그는 실행안된다.
                    if(check11){
                         sum=sum+1;
                        Log.v("check","sum 값이  0이 아니어서 승인 거부 묻는  프로세스 다이얼로그 또 나올것임.");
                    }//check =true 일때 끝
                }//for문 끝남.




            //결국  모두 다시 보지 않기를  체크한 거부들이면  아래  sum==0이  성립되어 아래 조건  코드들  실행
            if(sum==0) {

                //여기서  sum이 0일때는 shouldrationale 함수가  false값이 나올때이다.
                //그런데,  여기서 문제가 허용을 모두 누를때랑 거절을 모두 누를때  둘다  false가  나온다는 것이다.
                //원래는  grant 매개 변수를 사용해서  이부분에서  허용과 거절 부분을  한번더  나누어야하지만,
                //checkpermission 클래스에  checkpermission을 진행하고 프로그레스 다이얼로그를 보내는건  생략된 코드가 있어서
                //그냥  이 코드를  사용하기로함.
                //원래는  grant를 쓰는게 맞다.
                //암튼 이렇게 다시  권한들을  검색하고  true값이  나오면 login창으로
                //false값이 나오면 권한 설정 다이얼로그 가  뜨도록  진행함.
                Checkpermission checkpermission=new Checkpermission(RequestPermission.this);
                boolean checkagain=checkpermission.checkPermissions();
                Log.v("check", "sum값이 0이어서 설정창 가는 다이얼로그 실행");

                if (!checkagain) {

                    //alertdailog  빌드함.
                    AlertDialog.Builder altdialogforsetting = new AlertDialog.Builder(RequestPermission.this);


                    //알러트 다이얼로그 버튼  여기서는 취소랑  ,권한설정 버튼 두개가 필요했음.

                    //권한설정 버튼  리스너
                    altdialogforsetting.setPositiveButton("권한 설정", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {


                            // 사용자가  권한설정 버튼 눌렀을때
                            //현재 앱 페키지 이름의  uri로  앱  디테일 세팅창으로 간다.
                            Intent appDetail = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));

                            appDetail.addCategory(Intent.CATEGORY_DEFAULT);
                            appDetail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(appDetail);

                            //권한창을 갔다때는 다음과 같이  sawappdetailactivity변수를 true값으로  바꾸준다.
                            //갔다와서 onreume에서  sawappdetaiolactivity가  true값이면  진행서  checkpermisssion을 실행  모든 권한이 승인되었으면
                            //바로  loginactivity로  가질수 있게 하기위해서임 -> 버튼 따로 안누르고,
                            sawappdetailactivity = true;
                            Log.v("check", "권한설정 버튼 눌림.,  설정창가고  swappdetailactivity는 true임.");
                        }
                    });//권한설정 버튼  리스너 끝

                    //취소버튼 리스너
                    altdialogforsetting.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            //다이얼로그 취소한다.
                            dialog.cancel();

                            Log.v("check", "다이얼로그 취소함.");
                        }
                    });

                    //알러트  다이얼로그 관련 필요 세부사항 빌딩
                    altdialogforsetting.setTitle("권한 설정창으로 가기");

                    //false가 아닐때는  취소버튼 이외에  터치또는  백버튼으로  다이얼로그 취소가 가능함,
                    //그래서 그부분을 막음
                    altdialogforsetting.setCancelable(false);

                    altdialogforsetting.setMessage("'권한설정' 버튼을 누른후   \n'권한'파트로 들어가 \n모든 권한을 체크해주세요!");

                    //알럴트 다이얼로그 선언 및  보이기
                    AlertDialog alertDialog = altdialogforsetting.create();
                    alertDialog.show();

                }else{


                    Log.v("check","권한이  모두 승인되어있는 상태여서  loginactivity로  넘어감.");
                    Intent intent=new Intent(RequestPermission.this,LoginActivity.class);
                    startActivity(intent);

                    finish();

                }
            }
            }//REQUESTCODE case 끝
        }//switch 끝
    }//권한 요청 callback 함수 끝


}//requestPermission class  끝
