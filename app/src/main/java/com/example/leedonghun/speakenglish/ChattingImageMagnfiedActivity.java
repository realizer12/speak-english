package com.example.leedonghun.speakenglish;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.nkzawa.socketio.client.Url;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * speakenglish
 * Class: ChattingImageMagnfiedActivity.
 * Created by leedonghun.
 * Created On 2020-01-06.
 * Description:채팅 에서  주고 받은  이미지를 클릭시   확대해서 보여주는 엑티비티이다.
 * 다운로드 버튼을  누르게 되면  이미지가  sqlite로부터  다운로드 된다.
 * 맨위에는  이미지를 보낸 사람의  이름이  나온다.
 */
public class ChattingImageMagnfiedActivity  extends AppCompatActivity {


    private ImageView magnified_chatting_imageview;//채팅 이미지 들어가는 이미지뷰   1-1
    private ImageButton button_for_cancel_activity;//현재 엑티비티  취소 시킬  -> 버튼   1-2
    private ImageButton button_for_download_button;//현재 채팅 이미지 다운로드 받기위한 버튼   1-3
    private TextView textview_for_show_imagesender;//이미지 보낸 사람 이름 담길  텍스트뷰  1-4

    //사진 핀치로  확대 하게 만들어주는 라이브러리. uk.co.senab.photoview
    private PhotoViewAttacher photoAttacher;//1-5

    private long id=0;//해당 다운로드 파일  구별할  id
    DownloadManager downloadManager;//다운로드 매니져

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatting_image_magnify);

        magnified_chatting_imageview=findViewById(R.id.magnified_chatting_image);//1-1
        button_for_cancel_activity=findViewById(R.id.button_for_finish_magnified_chatting_image);//1-2
        button_for_download_button=findViewById(R.id.download_chatting_image_btn);// 1-3
        textview_for_show_imagesender=findViewById(R.id.chatting_image_sender_name);//1-4



       //확대한  이미지 정보  받기위한 getintent
       Intent intent_for_get_info=getIntent();

       //이미지 서버 경로
       String image_server_url= intent_for_get_info.getStringExtra("image_url");

        byte[] arr = intent_for_get_info.getByteArrayExtra("image_bitmap");
        Bitmap image = BitmapFactory.decodeByteArray(arr, 0, arr.length);//비트맵 바이트 받은거  비트맵화


        //이미지 보낸 사람  uid
       String image_sender_name= intent_for_get_info.getStringExtra("image_sender_name");

       //이미지 보낸 사람 position
       String image_sender_positon=intent_for_get_info.getStringExtra("image_sender_position");

       //이미지 채팅 보낸 유저의 정보  각 포지션별 알맞게 text에 넣어주는 메소드
       get_username_for_imageInfo(textview_for_show_imagesender, image_sender_name, image_sender_positon);

       //전체 이미지 uri
       String image_whole_url="http://13.209.249.1/" +image_server_url;


        magnified_chatting_imageview.setImageBitmap(image);
        //사진  확대 조절 라이브러리 실행- 1-5
        photoAttacher= new PhotoViewAttacher(magnified_chatting_imageview);
        photoAttacher.update();


        //다운로드 버튼 클릭시  이벤트   1-3
        button_for_download_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("check", getLocalClassName()+"의  이미지 다운로드 버튼 클릭됨");

                //speakenglish_chatting folder 체크 하여
                //폴더가 있는 경우-> 다운로드를 실행한다.
               if(check_Folder_exist_or_not("/speakenglish_chatting")){

                   //파일에  현재 시간을  붙여서  겹치는 파일이 없도록 함.
                   String filename=System.currentTimeMillis()+image_sender_name+".jpg";
                   Uri chatting_image_uri= Uri.parse(image_whole_url);//채팅 이미지 서버 url -> uri로 parse해줌,
                   downloadManager= (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);//다운로드 매니저

                   //다운로드 매니져 -> request
                   DownloadManager.Request request=new DownloadManager.Request(chatting_image_uri);
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

               }else{//폴더가 없는 경우다-> 아래처럼  이미지 폴더 생성 실패라고  토스트 날린다.

                   new Toastcustomer(ChattingImageMagnfiedActivity.this).showcustomtaost(null, "해당 이미지 저장 폴더 생성 실패.");

               }

            }//onClick () 끝
        });//1-3 클릭이벤트 끝.


        //취소 버튼 클릭시 이벤트   1-2
        button_for_cancel_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.v("check",getLocalClassName()+"의 취소 버튼 눌려서  엑티비티 finish() 호출됨");

                finish();//현재 엑티비티 종료.
            }
        });//1-2 클릭이벤트 끝

    }//onCreate() 끝



    @Override
    protected void onResume() {
        super.onResume();
        Log.v("check", getLocalClassName()+"의  onResume() 실행됨");

        //다운로드 매니저의   다운로드  완료 경우-> 인텐트 실행
        IntentFilter intentFilter=new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadreceiver, intentFilter);///브로드 캐스트 실행

    }//onResume()끝



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
                   new Toastcustomer(ChattingImageMagnfiedActivity.this).showcustomtaost(null, "Image Download Success");

               }else if(status==DownloadManager.STATUS_FAILED){
                   new Toastcustomer(ChattingImageMagnfiedActivity.this).showcustomtaost(null, "Image Download Fail..");


               }

            }//cursor.moveToFirst() 끝
        }//onReceive()끝
    };



    @Override
    protected void onPause() {
        super.onPause();


        unregisterReceiver(downloadreceiver);//리시버  해제함.


    }//onPause() 끝



//이미지 채팅이 저장될 폴더가 있는지 여부를 체크하고, 없으면
//해당 폴더를 만들어준다.
private boolean  check_Folder_exist_or_not(String foldername){

//채팅 이미지가  저장될 폴더,
File folder_for_save_chatting_iamge=new File(Environment.getExternalStorageDirectory()+foldername);

//해당  파일이 없는경우 ->  folder_for_save_chatting_iamge만들어줌
if(!folder_for_save_chatting_iamge.exists()){

    boolean makefodler=folder_for_save_chatting_iamge.mkdir();//해당 폴더  경로 새로 만듬.

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


//이미지 정보에  유저의 이름을 나타내기위해
//intent로 가지고온 유저의 uid를 이용해서 -> 이름을 가지고 와 넣어준다.
private void get_username_for_imageInfo (TextView textview_for_show_imagesender,String sender_name,String sender_position){


        Log.v("check",getLocalClassName()+"에서 -> getusername_for_imageinfo 실행됨");
        Log.v("check", getLocalClassName()+"에서 받은  -> 보낸사람 이름 :"+sender_name+",  보낸 사람 포지션 :"+sender_position);

        String sender_text_words=null;


        if(sender_name.equals("Me")){// 이미지 샌더가  나일때,

            sender_text_words="Image From me";
            textview_for_show_imagesender.setText(sender_text_words);

        }else {//이미지 샌더가  내가 아닐때,


            if (sender_position.equals("t")) {//보낸 사람의 직업 포지션이  선생님일때

                sender_text_words = "Image From " + sender_name + " teacher";//선생님은 teacher 뒤에 넣어줌,

            } else if (sender_position.equals("s")) {//보낸 사람의 직업 포지션이  학생일떄,

                sender_text_words = "Image From " + sender_name;// 학생은 그냥  이름.
            }

            if (sender_text_words != null) {//초기값 null이 아닌경우 -> 해당 텍스트뷰에 보낸사람 정보 넣어줌.

                textview_for_show_imagesender.setText(sender_text_words);
            }
        }

}//get_username_for_imageinfo()끝





}//클래스 끝
