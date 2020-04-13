package com.example.leedonghun.speakenglish;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.Image;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * speakenglish
 * Class: ChattingRoomMediaFilesRecyclerViewAdapter.
 * Created by leedonghun.
 * Created On 2020-01-10.
 * Description:현재 채팅룸에 있는 미디어 파일들을   받아서  -리사이클러뷰에  뿌려주기 위한 adpeter이다.
 */
public class ChattingRoomMediaFilesRecyclerViewAdapter extends RecyclerView.Adapter<ChattingRoomMediaFilesRecyclerViewAdapter.ChattingRoomMediaFilesRecyclerViewHolder>  {


    private String ment_for_log="ChattingRoomMediaRecyclerViewAdapter";//로그 작성을 위한 값

    private LayoutInflater layoutInflater;//채팅 내용이 담길  커스텀뷰 인플레이터 하기위한 인플레이터
    private ArrayList<JSONObject> mediafilelist;//어레이리스트
    private Context mcontext;//context

    //채팅방  이미지  담을  뷰홀더 객체
    ChattingRoomMediaFilesRecyclerViewHolder chattingRoomMediaFilesRecyclerViewHolder;

    public ChattingRoomMediaFilesRecyclerViewAdapter(ArrayList<JSONObject> mediafilelist,Context context){

        Log.v("check", ment_for_log+"의  어뎁터 생성자 실행됨");

        this.mediafilelist=mediafilelist;
        this.mcontext=context ;

    }//어댑터 생성자


    //ViewHolder create
    @NonNull
    @Override
    public ChattingRoomMediaFilesRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.v("check", ment_for_log+"의 onCreateViewholder 실행됨");

        Context context = parent.getContext();//부모 레이아웃  컨텍스트 사용.
        layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//인플레이터   컨텍스트 권한 줌
        View view=layoutInflater.inflate(R.layout.chatting_room_mediafiles_container, parent,false);//채팅방  미디어 파일  담길  뷰 인플레이트

        //채팅 뷰 홀더 ->  객체 선언 -> 뷰 홀더에  해당  view 연결 해줌.
        chattingRoomMediaFilesRecyclerViewHolder=new ChattingRoomMediaFilesRecyclerViewAdapter.ChattingRoomMediaFilesRecyclerViewHolder(view);


        //뷰 홀더 리턴
        return chattingRoomMediaFilesRecyclerViewHolder;

    }//뷰홀더 끝

    //자바 언어로  ->  ui 값   지정할때  -> 해당 값은 px로 들어간다. -> 그래서 그걸  dp 로  바꿔서 받기위한  메소드
    public static int dpToPx(int dp) {


        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);

    }//doToPx 끝


    @Override
    public void onBindViewHolder(@NonNull ChattingRoomMediaFilesRecyclerViewHolder viewholder, int position) {
        Log.v("check", ment_for_log+"의 onBindViewHolder 실행됨");
        Log.v("check", ment_for_log+"->>>>"+mediafilelist);


        try {
             String serverpath=mediafilelist.get(position).getString("server_path");
             int viewtype=mediafilelist.get(position).getInt("viewtype");

             Log.v("check", ment_for_log+"의 onbindviewholder-> serverpath : "+serverpath);

             if(viewtype==4){//해당 파일이 이미지일때

                 viewholder.chatting_media_file_button.setVisibility(View.INVISIBLE);//이미지 이므로  플레이버튼을  안보이게 해준다.
                 URL url_for_image = new URL("http://13.209.249.1/" +serverpath);//미디어 파일  전체 url
                 Glide.with(mcontext).load(url_for_image).placeholder(R.drawable.img_error).override(dpToPx(120),dpToPx(120))
                         .into(viewholder.chatting_media_file_imageview);//해당 이미지를 넣어줌.

             }else if(viewtype==5){//해당 파일이  비디오 일떄

                 viewholder.chatting_media_file_button.setVisibility(View.VISIBLE);//플레이 버튼이 보이도록  설정해준다.
                 URL url_for_image = new URL("http://13.209.249.1/" +serverpath);//미디어 파일  전체 url
                 Glide.with(mcontext).load(url_for_image).placeholder(R.drawable.img_error).override(dpToPx(120),dpToPx(120))
                         .into(viewholder.chatting_media_file_imageview);//해당 이미지를 넣어줌.
             }



        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


    }//onBindViewHolder() 끝


    @Override
    public int getItemCount() {
        Log.v("check", ment_for_log+"의 itemcount->"+mediafilelist.size());

        return mediafilelist.size();

    }//getItemCount() 끝



    //채팅 미디어 뷰 혿더
    class ChattingRoomMediaFilesRecyclerViewHolder extends RecyclerView.ViewHolder{

           ImageView chatting_media_file_button;//채팅 파일 중 동영상에  올라가는  재생 버튼 1-1
           ImageView chatting_media_file_imageview;//채팅 파일 들어가는 이미지뷰 1-2

        public ChattingRoomMediaFilesRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            Log.v("check", ment_for_log+"의 ChattingRoomMediaFilesRecyclerViewHolder 실행됨");

            chatting_media_file_button=itemView.findViewById(R.id.playbtn_for_media_file);//(비디오 재생 버튼)  1-1
            chatting_media_file_imageview=itemView.findViewById(R.id.mediafile_item);// 미디어 이미지 (비디오는 thumnail)  1-2





                //1-2  클릭이벤트 -> 이미지인경우 클릭됨.
                chatting_media_file_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        int viewtype=-1;  // 현재 포지션 아이템의  vietype

                        try{

                            viewtype=mediafilelist.get(getAdapterPosition()).getInt("viewtype");//해당  미디어 파일  뷰타입

                        }catch (JSONException e){

                            e.printStackTrace();
                        }


                        if(viewtype==4){// 미디어 파일이  이미지 인 경우 만 이미지 클릭시 넘어가도록 해준다. -> 동영상에 경우는 플레이 버튼을  눌르면  넘어가야하기 때문에...



                            //showWHole_media_files_chatting_room 엑티비티로 넘어간다.
                            Intent goto_showWHole_media_files_chatting_room=new Intent(mcontext,ShowWholeMediaFilesInChattingRoom.class);

                            //미디어 파일리스트 전체를 보내준다.
                            goto_showWHole_media_files_chatting_room.putExtra("mediafilelist", mediafilelist.toString());

                            //현재 클릭된  미디어 파일의  포지션을 보내준다.
                            goto_showWHole_media_files_chatting_room.putExtra("clicked_file_positon", getAdapterPosition());



                            //showWHole_media_files_chatting_room 실행
                            mcontext.startActivity(goto_showWHole_media_files_chatting_room);

                        }//viewtype =4일때




                    }//onClick() 끝

                });//1-2 클릭이벤트 끝.



                //1-1  클릭이벤트 -> 동영상인 경우 -> 앞에 플레이버튼을 누를시  넘어간다.
                chatting_media_file_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        //showWHole_media_files_chatting_room 엑티비티로 넘어간다.
                        Intent goto_showWHole_media_files_chatting_room=new Intent(mcontext,ShowWholeMediaFilesInChattingRoom.class);

                         //미디어 파일리스트 전체를 보내준다.
                         goto_showWHole_media_files_chatting_room.putExtra("mediafilelist",  mediafilelist.toString());

                        //현재 클릭된  미디어 파일의  포지션을 보내준다.
                        goto_showWHole_media_files_chatting_room.putExtra("clicked_file_positon", getAdapterPosition());

                        //showWHole_media_files_chatting_room 실행
                        mcontext.startActivity(goto_showWHole_media_files_chatting_room);


                    }//onClick() 끝

                });//1-1 클릭이벤트 끝.







        }//ChattingRoomMediaFilesRecyclerViewHolder 끝

    }//chattingroommediaFileRecyclerViewHolder 끝


}//ChattingRoomMediaFilesRecyclerViewAdapter() 끝
