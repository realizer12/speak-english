package com.example.leedonghun.speakenglish;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

/**
 * speakenglish
 * Class: ChattingVideoMagnifiedActivity.
 * Created by leedonghun.
 * Created On 2020-01-07.
 * Description:채팅방에  올라온 비디오 컨텐츠  클릭시  -> 실행되는 엑티비티 이다.
 * 이엑티비테에서는  동영상 재생이 가능하고,  해당 동영상을  다운로드도 할수 있다.
 * 그리고  누가 보낸 비디오인지 해당 sender의  이름도 같이  명시된다.
 */
public class ChattingVideoMagnifiedActivity extends AppCompatActivity {


    VideoView chatting_video_view;//채팅방에서 보내온 비디오가 담길 뷰= 1-1
    ImageButton btn_for_finish_activity;//현재 채팅방  취소 버튼 =1-2
    ImageButton btn_for_download_video;//현재 비디오  다운로드 진행 버튼 1-3
    TextView video_sender_name_text;//현재 비디오 보낸  채팅  유저의 이름 들어가는  텍스트뷰 1-4

    MediaController mediaController;//비디오 뷰 -> 미디어 컨트롤러  1-5

    DownloadManager downloadManager;// 다운로드 담당할 다운로드 매니저 1-6


    private long id=0;//해당 다운로드된 비디오 파일 구별할 id

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatting_video_play_activity);
        Log.v("check", getLocalClassName()+"의  onCreate() 실행됨");


        chatting_video_view=findViewById(R.id.magnified_chatting_video);//1-1
        btn_for_finish_activity=findViewById(R.id.button_for_finish_magnified_chatting_video);//1-2
        btn_for_download_video=findViewById(R.id.download_chatting_video_btn);//1-3
        video_sender_name_text=findViewById(R.id.chatting_video_sender_name);//1-4


        //리사이클러뷰adapter에서 보낸  ->  비디오  정보들  받는 intent  2-1
        Intent getvideo_info=getIntent();

        String video_url=getvideo_info.getStringExtra("video_url");//비디오 서버 url 2-1
        String video_positioncheck=getvideo_info.getStringExtra("video_sender_position");//비디오 보낸 사람의 position  2-1
        String video_sender_name=getvideo_info.getStringExtra("video_sender_name");//비디오 보낸 사람의  이름. 2-1


        //비디오 파일 보낸 사람의 정보를 구분하여 넣어주기위한 - 메소드
        get_username_for_videoInfo(video_sender_name_text, video_sender_name, video_positioncheck);


        //비디오  서버  전체 url
        String video_whole_serverpath="http://13.209.249.1/"+video_url;

        Uri castvideo_server_URL=Uri.parse(video_whole_serverpath);//비디오 서버 url을  uri로 변환.
        mediaController=new MediaController(ChattingVideoMagnifiedActivity.this);//미디어 컨트롤러  1-5

        chatting_video_view.requestFocus();//비디오뷰에  포커스
        chatting_video_view.setVideoURI(castvideo_server_URL);//비디오뷰에  위에서 전환한  서버 uri를 넣음.

        //비디오 준비리스너
        chatting_video_view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                //준비가 되면 -> 비디오뷰와 미디컨트롤러와 연결시켜줌.
                chatting_video_view.setMediaController(mediaController);
                mediaController.setAnchorView(chatting_video_view);//미디어 컨트롤러 비디오뷰 안으로 들어가게 설정

                chatting_video_view.seekTo(1);//비디오뷰 맨처음 보이는  이미지는  비디오 1초뒤 프레이므로 보여줌.

            }//onprepared() 끝
        });//비디오서버 전체 url




        //비디오 다운로드 버튼 눌림 이벤트 1-3
        btn_for_download_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("check", getLocalClassName()+"의  비디오 다운로드 버튼 눌림");

                //해당  폴더가 있는 경우
                if(check_Folder_exist_or_not("/speakenglish_chatting")){

                    //파일에  현재 시간+ 보낸사람의 이름 붙여서  겹치는 파일이 없도록 함.
                    String filename=System.currentTimeMillis()+video_sender_name+".mp4";

                    Uri chatting_video_uri= Uri.parse(video_whole_serverpath);//채팅 비디오 서버 url -> uri로 parse해줌,
                    downloadManager= (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);//다운로드 매니저  1-6

                    //다운로드 매니져 -> request
                    DownloadManager.Request request=new DownloadManager.Request(chatting_video_uri);
                    request.setTitle("Speakenglish DownLoad Video");//타이틀 정해줌.
                    request.setDescription("DownLoading Video....");//내용 정해줌.
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);//노티 보여주고,  다운로드 completed되도 안사라지게 납둠.
                    request.setVisibleInDownloadsUi(true);//다운로드 ui보여줌,
                    request.allowScanningByMediaScanner();//미디어 스캐너  스캐닝  allow
                    request.setAllowedOverMetered(true);//모바일 네트워크 연결되었을떄도 가능
                    request.setAllowedOverRoaming(true);//로밍 커넥션에서도  다운로드 가능

                    //아래 파일 디렉터리가  -> 다운로드  목적지
                    request.setDestinationInExternalPublicDir(Environment.getExternalStorageDirectory()+"/speakenglish_chatting",filename);

                    //id를 통해 ->  해당 id 파일의 다운로드 상태를  알아낼수있음.
                    id=downloadManager.enqueue(request);//다운로드  시작함.-> return값으로  id값을 리턴함.


                }else{

                    new Toastcustomer(ChattingVideoMagnifiedActivity.this).showcustomtaost(null, "해당 비디오 저장 폴더 생성 실패.");

                }



            }
        });//1-3 끝



        //엑티비티 종료 버튼 이벤트   1-2
        btn_for_finish_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.v("check", getLocalClassName()+"에서 엑티비티 종료 버튼 눌림 finish() 호출됨");

                finish();//엑티비티 종료

            }
        });//1-2 이벤트 끝


    }//oncreate()끝

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
                    new Toastcustomer(ChattingVideoMagnifiedActivity.this).showcustomtaost(null, "Video Download Success");

                }else if(status==DownloadManager.STATUS_FAILED){
                    new Toastcustomer(ChattingVideoMagnifiedActivity.this).showcustomtaost(null, "Video Download Fail..");


                }

            }//cursor.moveToFirst() 끝
        }//onReceive()끝
    };



    //비디오 정보에  유저의 이름을 나타내기위해
    //intent로 가지고온 유저의 uid를 이용해서 -> 이름을 가지고 와 넣어준다.
    private void get_username_for_videoInfo (TextView textview_for_show_videosender,String sender_name,String sender_position){


        Log.v("check",getLocalClassName()+"에서 -> getusername_for_imageinfo 실행됨");
        Log.v("check", getLocalClassName()+"에서 받은  -> 보낸사람 이름 :"+sender_name+",  보낸 사람 포지션 :"+sender_position);

        String sender_text_words=null;


        if(sender_name.equals("Me")){// 비디오 샌더가  나일때,

            sender_text_words="Video From me";
            textview_for_show_videosender.setText(sender_text_words);

        }else {//비디오 샌더가  내가 아닐때,


            if (sender_position.equals("t")) {//보낸 사람의 직업 포지션이  선생님일때

                sender_text_words = "Video From " + sender_name + " teacher";//선생님은 teacher 뒤에 넣어줌,

            } else if (sender_position.equals("s")) {//보낸 사람의 직업 포지션이  학생일떄,

                sender_text_words = "Video From " + sender_name;// 학생은 그냥  이름.
            }

            if (sender_text_words != null) {//초기값 null이 아닌경우 -> 해당 텍스트뷰에 보낸사람 정보 넣어줌.

                textview_for_show_videosender.setText(sender_text_words);
            }
        }

    }//get_username_for_imageinfo()끝




    //비디오 채팅이 저장될 폴더가 있는지 여부를 체크하고, 없으면
    //해당 폴더를 만들어준다.
    private boolean  check_Folder_exist_or_not(String foldername){

        //채팅 비디오가  저장될 폴더,
        File folder_for_save_chatting_video=new File(Environment.getExternalStorageDirectory()+foldername);

         //해당  파일이 없는경우 ->  folder_for_save_chatting_video 만들어줌
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


    @Override
    protected void onPause() {
        super.onPause();
        Log.v("check", getLocalClassName()+"의 onPause() 실행됨");

        unregisterReceiver(downloadreceiver);//동영상 다운로드 알림 브로드캐스트 해제

    }//onPause() 끝

}//ChattingVideoMagnifiedActivity 클래스 끝,.
