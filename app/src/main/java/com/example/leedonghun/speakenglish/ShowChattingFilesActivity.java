package com.example.leedonghun.speakenglish;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * speakenglish
 * Class: ShowChattingFilesActivity.
 * Created by leedonghun.
 * Created On 2020-01-10.
 * Description:해당 채팅방에서  주고 받아  sqlite에  저장되었던  모든 파일들을
 * 한곳에서 grid 형태 리사이크럴뷰로   모아  볼수 있는  엑티비티이다.
 * 각 채팅방  네비게이션 바 클릭 후  이미지  동영상 파일 관련 버튼을 클릭하면 진행된다.
 */
public class ShowChattingFilesActivity extends AppCompatActivity {

   private RecyclerView recyclerView_for_chatting_file_list;//채팅방  파일들을 전부  모아  뿌려지는 리사이클러뷰-> 그리드 형태로 뿌려진다. 1-1
   private ChattingRoomMediaFilesRecyclerViewAdapter chattingRoomMediaFilesRecyclerViewAdapter;//리사이클러뷰에 데이터를 뿌려줄  adpter 1-2

   private ImageView backbtn;//뒤로가기  버튼 1-3

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_whole_chatting_filelist);

        Log.v("check", getLocalClassName()+"의 oncreate() 실행됨");



        recyclerView_for_chatting_file_list=findViewById(R.id.recyclerview_for_show_chatting_files);//1-1
        backbtn=findViewById(R.id.backbtn_from_show_chattingfiles);//1-3



         //채팅방에서  보낸  룸 정보들 받음
         Intent get_roominfo=getIntent();

         String database_name=get_roominfo.getStringExtra("databasename");//sqlite데이터 베이스 이름
         String roomnumber =get_roominfo.getStringExtra("roomnumber");//방 번호

        ArrayList<JSONObject> whole_media_file_jsonarray=new ArrayList();

        whole_media_file_jsonarray=get_whole_file_from_chattingroom(database_name, roomnumber);

        //리사이클러뷰 ->  레아웃을 그리드 형태로 뿌려준다  가로  방향  아이템 슈 3개로 지정함.
        GridLayoutManager gridLayoutManager=new GridLayoutManager(ShowChattingFilesActivity.this, 3);



//        //리사이클러뷰 어뎁터 처리및 레이아웃 매니징 처리
        chattingRoomMediaFilesRecyclerViewAdapter = new ChattingRoomMediaFilesRecyclerViewAdapter(whole_media_file_jsonarray, ShowChattingFilesActivity.this);//1-2
        ((SimpleItemAnimator) recyclerView_for_chatting_file_list.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView_for_chatting_file_list.setAdapter(chattingRoomMediaFilesRecyclerViewAdapter);
        ((GridLayoutManager) gridLayoutManager ).setReverseLayout(false);
        ((GridLayoutManager) gridLayoutManager).setStackFromEnd(false);
        recyclerView_for_chatting_file_list.setLayoutManager(gridLayoutManager);
        recyclerView_for_chatting_file_list.setNestedScrollingEnabled(false);



        //뒤로가기 화살표 버튼 눌렸을때  엑티비티 finsih() 호출
        backbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                finish();//finish() 호출출

           }//onClick()끝
        });

    }//onCreate() 끝


    //해당 방  slqite를 통해  모든 미디어 파일을 가지고 온다.
    //그리고 가지고온  정보를  뿌려준다. -> Jsonarray로 뿌려준다.
    private ArrayList<JSONObject> get_whole_file_from_chattingroom(String databasename, String roomnumber){


        ArrayList<JSONObject> arrayList = new ArrayList<>();//sqlite조회한  내용 당을

        //sqlite에서  읽지 않은 메세지로 분류된  채팅 메세지들을 가지고 온다
        SqLiteOpenHelperClass GetsqLite_chatting_Database = new SqLiteOpenHelperClass (ShowChattingFilesActivity.this,databasename , null, 1);
        SQLiteDatabase Get_saved_database = GetsqLite_chatting_Database.getReadableDatabase();//데이터 베이스 읽기로  데이터에 접근.
        String sql = "select * from chatting_data_store where roomnumber='"+roomnumber+"' and viewtype=4 or roomnumber='"+roomnumber+"'and viewtype=5";//chatting_data_store테이블 에서 해당방 vietype 4(이미지,  또는

        Cursor cursor=Get_saved_database.rawQuery(sql,null);///위 쿼리문  데이터베이스로 보냄.


        //현재 채팅방  총  파일 수
        int whole_count_of_mediafiles=cursor.getCount();


        //가장 최근 미디어 부터 조회 하게 만듬
        //이렇게 하면,  cursor.moveToPrevious를 할때  -> 맨처음에  맨마지막  포지션 부터 이전으로 차례차례 조회 가능하다.
        cursor.moveToLast();//커서를 맨 마지막으로 보내고,
        cursor.moveToPosition(cursor.getPosition()+1);//맨 마지막에서  +1 을 함,

        //이렇게 하면,  cursor.moveToPrevious를 할때  -> 맨처음에  맨마지막  포지션 부터 이전으로 차례차례 조회 가능하다.
        while (cursor.moveToPrevious()){//가지고온  쿼리 목록을 담은  cursor를  while문으로 하나씩 돌림

            JSONObject jsonObject=new JSONObject();

            int mediafile_position=cursor.getPosition();//현재 미디어 파일 포지션
            String mediafile_server_loaction=cursor.getString(11);//서버에 미디어 파일의 위치
            String mediafile_sender=cursor.getString(6);//미디어 파일 보낸 사람.
            int viewtype=cursor.getInt(8);//미디어 파일 viewtype -> 이미지는 4  , 비디오는  5

            int mediafile_sender_uid=cursor.getInt(1);//미디어 파일  보낸 사람의  uid


            try {

                jsonObject.put("total_count", whole_count_of_mediafiles);//전체 미디어 파일  카운트
                jsonObject.put("present_position", mediafile_position);//해당 미디어의  포지션
                jsonObject.put("server_path",mediafile_server_loaction);//이미지  서버  저장 위치
                jsonObject.put("sender_uid", mediafile_sender_uid);// 미디어 파일 보낸 사람의 유아이디.
                jsonObject.put("sender", mediafile_sender);//이미지 보낸 사람
                jsonObject.put("viewtype", viewtype);//해당 미디아 뷰타입



            } catch (JSONException e) {

                e.printStackTrace();
            }


                arrayList.add(jsonObject);//해당 파일들을  어레이에  넣어줌 -> 이 어레이를 통해  모든 파일들을  리사이클러뷰로 뿌려준다.


        }//cursor가 null이 아닐때 ->  cursor  movettonext로 while문 끝.

        cursor.close();//cursor 닫아줌.

        return arrayList;
    }//get_whole_file_from_chattingroom()끝


}//ShowChattingFilesActivity클래스 끝.
