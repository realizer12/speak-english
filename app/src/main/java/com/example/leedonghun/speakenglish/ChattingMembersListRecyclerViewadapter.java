package com.example.leedonghun.speakenglish;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

/**
 * speakenglish
 * Class: ChattingMembersListRecyclerView.
 * Created by leedonghun.
 * Created On 2020-01-17.
 * Description:채팅방  멤버들의  리스트가  담기는 리사이클러뷰의  어뎁터이다.
 * 서버에서  각 방의  채팅 참여자를 조회하고, 추가시켜준다.
 * 오픈 채팅방에는 새로 사람이 들어오거나  ->  사람이 나가면, 해당  리사이클러뷰를  업데이트 해줘서 실시간으로 참여 인원들을 볼수 있도록 해준다.
 */
public class ChattingMembersListRecyclerViewadapter extends RecyclerView.Adapter {

    private ArrayList<JsonObject>chattingmember_list_info;//채팅멤버  리스트  정보
    private Context context;//context
    private LayoutInflater layoutInflater;//채팅 내용이 담길  커스텀뷰 인플레이터 하기위한 인플레이터

    private String user_uid1;

    //현재  채팅 멤버 리스트 리사이클러뷰 -생성자
    ChattingMembersListRecyclerViewadapter(Context context, ArrayList<JsonObject> chattingmember_list_info,String useruid){

        this.context=context;//context
        this.chattingmember_list_info=chattingmember_list_info;//채팅멤버 리스트 정보
        this.user_uid1=useruid;//현재 유저의 uid

        //만약에 -> 맨처음  유저 리스트에 들어가는 유저가  학생으로 배정되어왔을때 -> position이 0으로 선생님이 될때까지  반복문을 돌려준다.
        while(chattingmember_list_info.get(0).get("userposition").toString().replaceAll("\"", "").equals("s")){

            Collections.swap(chattingmember_list_info, 0, 1);// 채팅 리스트의  포지션을  0-> 1로 보낸다.

        }//선생님이 포지션 0이될 때까지 -> 반복문을 돌려준다.


    }//ChattingMembersListRecyclerView 생성자


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context=parent.getContext();//context

        View view;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.chattingmember_recyclervie_item, parent, false);



        return new chatting_member_list_viewholder(view);

    }//onCreateViewHolder() 끝



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {



        //유저의 이름
        String user_name= chattingmember_list_info.get(position).get("username").toString().replaceAll("\"", "");

        //유저의 포지션
        String user_position= chattingmember_list_info.get(position).get("userposition").toString().replaceAll("\"", "");

        //유저의 프로필 이미지 주소
        String user_profilepath= chattingmember_list_info.get(position).get("profilepath").toString().replaceAll("\"", "");

        //해당  유저의 uid
        String user_uid=chattingmember_list_info.get(position).get("uid").toString().replaceAll("\"", "");


        if(user_uid.equals(user_uid1)){//유저 uid와  나의 uid가  같을 경우

            user_name="Me";//유저의 이름은 Me로 넣어줌.
        }

        if(user_position.equals("t")){//선생님 포지션일때

            if(!user_uid.equals(user_uid1)) {//유저 uid와  나의 uid가  같을 경우가 아닌경우 - > 이유는 내가  선생님인 경우에는 teacher이  나오면 안되닌까.

                //해당 이름 textview에  서버에서 가져온 유저 이름 넣어줌.
                ((chatting_member_list_viewholder) holder).user_name.setText(user_name + " teacher");
            }else{

                //해당 이름 textview에  서버에서 가져온 유저 이름 넣어줌.
                ((chatting_member_list_viewholder)holder).user_name.setText(user_name);

            }



            ((chatting_member_list_viewholder)holder).user_name.setTextColor(Color.GREEN);


        }else if(user_position.equals("s")){//학생 포지션일때,

            //해당 이름 textview에  서버에서 가져온 유저 이름 넣어줌.
            ((chatting_member_list_viewholder)holder).user_name.setText(user_name);
        }

        URL url_for_profileimage = null;
        try {
            url_for_profileimage = new URL("http://13.209.249.1/" +user_profilepath);//프로필 이미지  전체 url

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Glide.with(context).load(url_for_profileimage).placeholder(R.drawable.img_error)
                .into(((chatting_member_list_viewholder)holder).user_profile_img);//해당 유저의 프로필 이미지 서버에서 받아와  넣어줌.



    }//onBindViewHolder() 끝

    @Override
    public int getItemCount(){


        return chattingmember_list_info.size();//채팅 멤버 -> 어레이리스트  사이즈

    }//getItemCount() 끝


    //남이 보낸 채팅뷰  홀더
    class chatting_member_list_viewholder extends RecyclerView.ViewHolder {

        ImageView user_profile_img;// 채팅방 유저의  프로필 이미지가 담기는 이미지뷰 1-1
        TextView  user_name;//채팅방 유저의 이름이  담기는  텍스트뷰  1-2

        public chatting_member_list_viewholder(@NonNull View itemView) {
            super(itemView);

            user_profile_img=itemView.findViewById(R.id.imgview_for_chattingmember_profile);//1-1
            user_name=itemView.findViewById(R.id.txtviewfor_chattingmember_name);//1-2


        }//chatting_member_list_viewholder()끝
    }//chatting_member_list_viewholder 클래스 끝.


}//adapter  끝남.
