package com.example.leedonghun.speakenglish;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * speakenglish
 * Class: ShowWholeMediaFilesInChattingRoom.
 * Created by leedonghun.
 * Created On 2020-01-10.
 *
 * Description: 전체  미디어 파일   -> 확대해서  보여주는 엑티비티이이다
 * 뷰페이져를  이용해서  이미지 파일과  동영상 파일을 모두 보여준다.
 * 그리고  각  파일 마다 의 포지션에서  버튼을  누르면, 그 해당 파일을 다운로드 할수 있다.
 */
public class ShowWholeMediaFilesInChattingRoom extends AppCompatActivity {

    private ImageButton btn_for_finish_activity;//현재  엑티비티 종료시켜주는  finish  버튼 1-1;
    private ImageButton down_load_media_fil_btn;//미디어 파일  다운로드  버튼  1-2;

    private TextView media_file_sender;//미디어 파일을  보낸 사람 이름 들어가는 textview. 1-3
    private TextView show_media_file_position;//현재 방의  미디어파일 전체 카운트에서 포지션 보여주는 textview 1-4

    private ViewPager viewPager_for_whole_medaifiles;//1-5  미디어 파일을 담아서 보여줄 뷰페이져 객체이다.

    private ChattingMedaiFilesViewPagerAdapter chattingMedaiFilesViewPagerAdapter;// 미디어 파일  정보 가지고  뿌려줄  뷰 페이져  2-1


    String teacherloginedid;//선생님 로그인이메일

    String studentloginedid;//학생 로그인 이메일

    private long id=0;//해당 다운로드된 비디오 파일 구별할 id
    DownloadManager downloadManager;//다운로드 매니져

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatting_media_files_magnify);
        Log.v("check",getLocalClassName()+"의  onCreate() 실행됨" );


        btn_for_finish_activity=findViewById(R.id.button_for_finish_magnified_chatting_media_files);//1-1
        down_load_media_fil_btn=findViewById(R.id.download_chatting_media_btn);//1-2

        media_file_sender=findViewById(R.id.chatting_media_sender_name);//1-3
        show_media_file_position=findViewById(R.id.textview_for_show_position);//1-4

        viewPager_for_whole_medaifiles=findViewById(R.id.viewPager_for_magnify_chatting_media_files);//1-5

        //미디어 파일 리스트 및  클릭된 포지션 정보 받아오기
        Intent get_mediafileinfo=getIntent();


        //medaifile list -> intent 로 넘기기위해  -> 스트링으로 받음.
        String mediafilelist=get_mediafileinfo.getStringExtra("mediafilelist");

        //사용자가 터치해서 본 엑티비티로 오게된  해당 미디어 파일  포지션
        int position1=get_mediafileinfo.getIntExtra("clicked_file_positon", -10);




        JSONArray cast_medialist_JSONarray = null;//우선 스트링 으로  받은  미디어 파일리스트 -> JSONArray로  cast해줌.

        int total_count_of_media_files = 0;//전체  미디어 파일 숫자.



        try {

            //string으로  변환되어 넘어온 ->ArrayList<JSONarray>를  JSONArray로 받음.
            cast_medialist_JSONarray=new JSONArray(mediafilelist);

            total_count_of_media_files= cast_medialist_JSONarray.length();//전체 미디어 파일  길이  나옴.



        } catch (JSONException e) {
            e.printStackTrace();
        }



        chattingMedaiFilesViewPagerAdapter=new ChattingMedaiFilesViewPagerAdapter(ShowWholeMediaFilesInChattingRoom.this, cast_medialist_JSONarray);//2-1 뷰페이져 연결시킴.

        viewPager_for_whole_medaifiles.setAdapter(chattingMedaiFilesViewPagerAdapter);// 뷰페이져에  뷰페이져 어뎁터 연결 시켜줌.

        viewPager_for_whole_medaifiles.setCurrentItem(position1);//현재 포지션으로  viewpager포지션 해줌.

        // 현재 ->  미디어 파일의  포지션과 전체 카운트를  보여주어 ->  미디어 파일 중  어느 위치의  파일을 보는지 본다.
        show_media_file_position.setText((position1+1)+"/"+total_count_of_media_files);


        //보낸 사람이름
        String sender_name= null;
        int sender_uid=-1;
        int viewtype=-1;

        try {

            //보낸 사람 이름
            sender_name = cast_medialist_JSONarray.getJSONObject(position1).getString("sender");

            //보낸 사람  uid
            sender_uid=cast_medialist_JSONarray.getJSONObject(position1).getInt("sender_uid");

            //viewtype
            viewtype=cast_medialist_JSONarray.getJSONObject(position1).getInt("viewtype");


            //uid 체크 해서 -> 알맞게  이미지 보낸 사람 정보 넣어주는  메소드
            sender_uid_check_with_user(sender_uid,sender_name,media_file_sender,viewtype);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        //1-1  현재 엑티비티 종료 버튼
        btn_for_finish_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("check", getLocalClassName()+"의  엑티비티 종료 버튼 눌림");


                finish(); //현재 엑티비티 종료.

            }//onClick()끝
        });//1-1  버튼 눌림



        JSONArray finalCast_medialist_JSONarray = cast_medialist_JSONarray;//jsonarray 복사본.?'


        viewPager_for_whole_medaifiles.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }//onPageScrolled 끝

            @Override
            public void onPageSelected(int position) {



                //현재 페이지
                Log.v("check",getLocalClassName()+String.valueOf(position));
                // 현재 ->  미디어 파일의  포지션과 전체 카운트를  보여주어 ->  미디어 파일 중  어느 위치의  파일을 보는지 본다.
                show_media_file_position.setText((position+1)+"/"+ finalCast_medialist_JSONarray.length());


                try {

                    //보낸 사람이름
                    String sender_name=finalCast_medialist_JSONarray.getJSONObject(position).getString("sender");

                    //보낸 사람  uid
                    int sender_uid=finalCast_medialist_JSONarray.getJSONObject(position).getInt("sender_uid");


                    int viewtype=finalCast_medialist_JSONarray.getJSONObject(position).getInt("viewtype");


                    //uid 체크 해서 -> 알맞게  이미지 보낸 사람 정보 넣어주는  메소드
                    sender_uid_check_with_user(sender_uid,sender_name,media_file_sender,viewtype);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }//onPageSelected 끝

            @Override
            public void onPageScrollStateChanged(int state) {

            }//onPageScrollStateChanged()끝
        });



        //다운로드 버튼 클릭되었을때  1-2
        down_load_media_fil_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("check", getLocalClassName()+"의  다운로드 버튼 클릭됨");

                //현재 아이템의  포짓ㄴ
                int currentitem_position=viewPager_for_whole_medaifiles.getCurrentItem();

                //미디어 파일의  viewtype
                int viewtype = -3;//viewtype  default값은 -3으로 설정

                String media_sender_name=null;

                String whole_medai_serverpath=null;



                try {

                    //현재 포지션  미디어  파일의  viewtype
                    viewtype=finalCast_medialist_JSONarray.getJSONObject(currentitem_position).getInt("viewtype");

                    //현재 포지션 미디어 파일을 보낸 사람의 이름.
                    media_sender_name=finalCast_medialist_JSONarray.getJSONObject(currentitem_position).getString("sender");

                    whole_medai_serverpath="http://13.209.249.1/"+finalCast_medialist_JSONarray.getJSONObject(currentitem_position).getString("server_path");




                } catch (JSONException e) {
                    e.printStackTrace();
                }




                //해당  폴더가 있는 경우  또는 새로 성공적으로 만들어진 경우.
                if(check_Folder_exist_or_not("/speakenglish_chatting")) {

                 if(viewtype==4){// 미디어 파일이 이미지 일경우

                   String filename= System.currentTimeMillis()+media_sender_name+".jpg";

                     Uri chatting_video_uri= Uri.parse(whole_medai_serverpath);//채팅 비디오 서버 url -> uri로 parse해줌,
                     downloadManager= (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);//다운로드 매니저  1-6

                     //다운로드 매니져 -> request
                     DownloadManager.Request request=new DownloadManager.Request(chatting_video_uri);
                     request.setTitle("Speakenglish DownLoad Image");//타이틀 정해줌.
                     request.setDescription("DownLoading Image....");//내용 정해줌.
                     request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);//노티 보여주고,  다운로드 completed되도 안사라지게 납둠.
                     request.setVisibleInDownloadsUi(true);//다운로드 ui보여줌,
                     request.allowScanningByMediaScanner();//미디어 스캐너  스캐닝  allow
                     request.setAllowedOverMetered(true);//모바일 네트워크 연결되었을떄도 가능
                     request.setAllowedOverRoaming(true);//로밍 커넥션에서도  다운로드 가능

                     //아래 파일 디렉터리가  -> 다운로드  목적지
                     request.setDestinationInExternalPublicDir(Environment.getExternalStorageDirectory()+"/speakenglish_chatting",filename);

                     //id를 통해 ->  해당 id 파일의 다운로드 상태를  알아낼수있음.
                     id=downloadManager.enqueue(request);//다운로드  시작함.-> return값으로  id값을 리턴함.



                 }else if(viewtype==5){//미디어 파일이  비디오 일  경우.

                     String filename= System.currentTimeMillis()+media_sender_name+".mp4";

                     Uri chatting_video_uri= Uri.parse(whole_medai_serverpath);//채팅 비디오 서버 url -> uri로 parse해줌,
                     downloadManager= (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);//다운로드 매니저  1-6

                     //다운로드 매니져 -> request
                     DownloadManager.Request request=new DownloadManager.Request(chatting_video_uri);
                     request.setTitle("Speakenglish DownLoad video");//타이틀 정해줌.
                     request.setDescription("DownLoading video....");//내용 정해줌.
                     request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);//노티 보여주고,  다운로드 completed되도 안사라지게 납둠.
                     request.setVisibleInDownloadsUi(true);//다운로드 ui보여줌,
                     request.allowScanningByMediaScanner();//미디어 스캐너  스캐닝  allow
                     request.setAllowedOverMetered(true);//모바일 네트워크 연결되었을떄도 가능
                     request.setAllowedOverRoaming(true);//로밍 커넥션에서도  다운로드 가능

                     //아래 파일 디렉터리가  -> 다운로드  목적지
                     request.setDestinationInExternalPublicDir(Environment.getExternalStorageDirectory()+"/speakenglish_chatting",filename);

                     //id를 통해 ->  해당 id 파일의 다운로드 상태를  알아낼수있음.
                     id=downloadManager.enqueue(request);//다운로드  시작함.-> return값으로  id값을 리턴함.

                 }//미디어 파일이  비오일 경우 끝


                }else{//해당 폴더가 없는데  만들기 실패 한경우

                    new Toastcustomer(ShowWholeMediaFilesInChattingRoom.this).showcustomtaost(null, "fail to make folder for media-files");

                }

                }//onClick() 끝
        });//다운로드 버튼 클릭 이벤트 끝.



    }//onCreate() 끝

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("check", getLocalClassName()+"의  onResume() 끝");


        //다운로드 매니저의   다운로드  완료 경우-> 인텐트 실행
        IntentFilter intentFilter=new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadreceiver, intentFilter);///브로드 캐스트 실행

    }//onResume() 끝

    //다운로드 완료 시  -> 토스트를 날리기위한 브로드캐스트리시버
    private final BroadcastReceiver downloadreceiver=new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            //다운로드 매니져에  쿼리날림.-> cursor가  리턴
            DownloadManager.Query query=new DownloadManager.Query();
            query.setFilterById(id);//위  리턴된 id값을  날려,  결과 받기
            Cursor cursor=downloadManager.query(query);

            if(cursor.moveToFirst()){

                int columnIndex=cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);//현재 상태 받아옴.
                int status=cursor.getInt(columnIndex);//현재 상태

                if(status==DownloadManager.STATUS_SUCCESSFUL){//현재 상태가 다운로드 succefsul일때

                    //다운로드 성공을 알림.
                    new Toastcustomer(ShowWholeMediaFilesInChattingRoom.this).showcustomtaost(null, "Media File Download Success");

                }else if(status==DownloadManager.STATUS_FAILED){
                    new Toastcustomer(ShowWholeMediaFilesInChattingRoom.this).showcustomtaost(null, "Media Download Fail..");


                }

            }//cursor.moveToFirst() 끝
        }//onReceive()끝
    };


    @Override
    protected void onPause() {
        super.onPause();
        Log.v("check", getLocalClassName()+"의 onPause() 실행됨");

        unregisterReceiver(downloadreceiver);//동영상 다운로드 알림 브로드캐스트 해제

    }//onPause() 끝


    //해당 미디아 파일이 저장될 폴더가 있는지 여부를 체크하고, 없으면
    //해당 폴더를 만들어준다.
    private boolean  check_Folder_exist_or_not(String foldername){

        //미디어파일이  저장될 폴더,
        File folder_for_save_chatting_video=new File(Environment.getExternalStorageDirectory()+foldername);

        //해당  폴더가 없는경우 ->  folder_for_save_chatting_video 만들어줌
        if(!folder_for_save_chatting_video.exists()){

            boolean makefodler=folder_for_save_chatting_video.mkdir();//해당 폴더  경로 새로 만듬.

            if(makefodler){//해당  폴더 만들기 성공시

                Log.v("check", "speakenglish_chatting  폴더 만들어짐");

                return true;

            }else{

                Log.v("check", "speakenglish_chatting 폴더 만들기 실패");

                return  false;
            }


        }else{//해당 폴더가 존재하는 경우


            return true;
        }
    }//check_Folder_exist_or_not 끝



    //해당  미디어 파일 보낸 사람과  현재 유저의 uid를  체크 하기위해 ->  현재 유저의  이메일을 보내 uid를  얻어온다.
    //그다음에  비교  해서 결과 값을 출력한다.
    private void sender_uid_check_with_user(int sender_uid,String sender_name,TextView media_file_sender,int viewtype){

        Log.v("check", getLocalClassName()+"의 sender_uid_check_with_user실행됨");


        SharedPreferences getid_teacher = getSharedPreferences("loginteacherid",MODE_PRIVATE);//로그인 아이디  쉐어드에 담긴거 가져옴
        teacherloginedid= getid_teacher.getString("loginidteacher","");//로그인 아이디 가져옴

        SharedPreferences getid_student = getSharedPreferences("loginstudentid", MODE_PRIVATE);//로그인 아이디  쉐어드에 담긴거 가져옴
        studentloginedid = getid_student.getString("loginid", "");//로그인 아이디 가져옴

        if(teacherloginedid.equals("")){//선생님 로그인이 아닐때-> 학생 로그인
            Log.v("check", getLocalClassName()+"의"+studentloginedid+"학생입니다 ");


            Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create())//json으로 받고 싶으면 쓰면안됨..
                    .build();//리트로핏 뷸딩
            ApiService apiService = retrofit.create(ApiService.class);//api인터페이스 크리에이트

            Call<studentinforesult> getstudentinfo=apiService.sendemailtogetiprofile(studentloginedid);//학생정보 얻기위한  call객체

           getstudentinfo.enqueue(new Callback<studentinforesult>() {
               @Override
               public void onResponse(Call<studentinforesult> call, Response<studentinforesult> response) {

                   int studentuid= Integer.parseInt(response.body().getUid());// 학생 uid일떄.

                   if(sender_uid==studentuid){//현재 유저의 uid랑 미디어 파일 보낸 사람 uid가 같을 경우

                       if(viewtype==4){//이미지 일때

                           //보낸 사람이름  넣어줌.
                           media_file_sender.setText("Image from me");

                       }else if(viewtype==5){//비디오 일때

                           //보낸 사람이름  넣어줌.
                           media_file_sender.setText("Video from me");
                       }


                   }else {//두 uid가 다를 경우,


                       if(viewtype==4){//이미지 일때


                           //보낸 사람이름  넣어줌.
                           media_file_sender.setText("Image from "+sender_name);

                       }else if(viewtype==5){//비디오 일때

                           //보낸 사람이름  넣어줌.
                           media_file_sender.setText("Video from "+sender_name);
                       }

                   }



               }//onResponse()끝

               @Override
               public void onFailure(Call<studentinforesult> call, Throwable t) {

                   Log.v("check", getLocalClassName()+"의 sender_uid_check_with_user()에서 uid비교 위해  서버에서 uid정보 가져오는 중 에러남->"+t);

               }//onFailure() 끝
           });





        }else{//학생 로그인 아닐때 선생님 로그인-> 선생님 로그인

            Log.v("check", getLocalClassName()+"의"+teacherloginedid+"선생님입니다.");

            Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create())//json으로 받고 싶으면 쓰면안됨..
                    .build();//리트로핏 뷸딩
            ApiService apiService = retrofit.create(ApiService.class);//api인터페이스 크리에이트


            Call<teacherinforesult> getteacerinfo=apiService.sendemailtogetteacherprofile(teacherloginedid);//선생님 정보 요청위한  call 객체 선언

            getteacerinfo.enqueue(new Callback<teacherinforesult>() {
                @Override
                public void onResponse(Call<teacherinforesult> call, Response<teacherinforesult> response) {

                    int teacher_uid= Integer.parseInt(response.body().getTeacheruid());//선생님 uid

                    if(teacher_uid==sender_uid){


                        if(viewtype==4){//이미지 일떄

                            //보낸 사람이름  넣어줌.
                            media_file_sender.setText("Image from me");


                        }else if(viewtype==5){//비디오 일때

                            //보낸 사람이름  넣어줌.
                            media_file_sender.setText("Video from me");

                        }

                    }else {//두 uid가 다를 경우,

                        //이미지일떄
                        if(viewtype==4){

                            //보낸 사람이름  넣어줌.
                            media_file_sender.setText("Image from "+sender_name);


                        //비디오 일떄
                        }else if(viewtype==5){

                            //보낸 사람이름  넣어줌.
                            media_file_sender.setText("Video from "+sender_name);
                        }


                    }//두 uid가 다를 경우 끝.

                }//onResponse() 끝

                @Override
                public void onFailure(Call<teacherinforesult> call, Throwable t) {

                    Log.v("check", getLocalClassName()+"의 sender_uid_check_with_user()에서 uid비교 위해  서버에서 uid정보 가져오는 중 에러남->"+t);


                }//onFailure() 끝
            });//enque


        }////선생님이 로그인한 상태일떄,



    }//sender_uid_check_with_user() 끝



}//ShowWholeMediaFilesInChattingRoom
