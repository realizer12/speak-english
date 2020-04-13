package com.example.leedonghun.speakenglish;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
//import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;

/**
 * speakenglish
 * Class: FragmentForStudentChattingtype.
 * Created by leedonghun.
 * Created On 2019-01-11.
 * Description:학생의  채팅  리스트가 들어간다.  -> 오픈 채팅방 리스트와  일대일 채팅방 리스트가 나눠지며
 * 뷰페이져를 이용해  오픈 채팅방 영역과  일대일 채팅방 영역을  스와이프 형식으로 오갈수 있다.
 */
public class FragmentForStudentChattingtype extends Fragment{


    private final String fragment_name_for_log="FragmentForStudentChattngtype";//로그용 ->  프래그먼트 이름.

    private String studentuid;//학생 uid


    private ViewPager viewPager_for_chatting_room_list;//채팅방 리스트가 담길  뷰페이져 // 1-1
    private ChattingListViewPagerAdapter chattingListViewPagerAdapter;//채팅방  리스트에서 방이 뿌려질 어뎁터 //1-2

    private TextView chatting_type_name;//어떤 타입의 채팅인지 보여주는 텍스트뷰-> 오픈채팅 or 일반 채팅//1-3

    private TextView ment_for_swipe;//1-4



    ArrayList<JsonObject>arrayList;
    ViewGroup rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        rootView=(ViewGroup)inflater.inflate(R.layout.fragment_for_chattinginstudent,container,false);//프레그 먼트  root view 연결

        Log.v("check", fragment_name_for_log+"에서 onCreateView()시작됨");


        SharedPreferences getid = getActivity().getSharedPreferences("loginstudentid", MODE_PRIVATE);//로그인 아이디  쉐어드에 담긴거 가져옴
        final String loginedid = getid.getString("loginid", "");//로그인 아이디 가져옴
        getstudentuid(loginedid);



        viewPager_for_chatting_room_list=rootView.findViewById(R.id.viewpager_for_chatting);//1-1
        chatting_type_name=rootView.findViewById(R.id.chatting_type_name);//1-3

        ment_for_swipe=rootView.findViewById(R.id.ment_for_swipe);//1-4
        ment_for_swipe.setText("<= Swipe Screen");





        viewPager_for_chatting_room_list.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            }//onPageScrolled() 끝

            @Override
            public void onPageSelected(int position) {
                Log.v("check 포지션", "현재 포지샨 -> "+position);

                if(position==0){

                    chatting_type_name.setText("Open Chatting");
                    ment_for_swipe.setText("<= Swipe Screen to [1:1 Chat]");


                 }else if(position==1){

                    chatting_type_name.setText("1 : 1 Chatting");
                    ment_for_swipe.setText("Swipe Screen to [Open Chat] =>");
                }

            }//onPageSelected() 끝

            @Override
            public void onPageScrollStateChanged(int state) {

            }//onPageScrollStateChanged() 끝

        });//viewPager_for_chatting_room_list 끝



        return rootView;

    }//onCreateView() 끝


    @Override
    public void onResume() {
        super.onResume();
        Log.v("check", fragment_name_for_log+"에서 onResume 시작됨");

      if(chattingListViewPagerAdapter != null) {//뷰페이져 어뎁터가  null이 아닐겨우이다. ->즉  이전에 한번  뷰페이져 실행되서 뷰들을 보여주었을때임


          //null이 아닐때이므로,  채팅창을 갔다가 돌아왔을 경우도 됨 -> 그래서 채팅창 기록 추가를 대비해 -> notifydatachanged를  해준다.
          Log.v("check", fragment_name_for_log+"에서 notifydatachanged 실행됨");
          chattingListViewPagerAdapter.chattingRoomListRecyclerviewAdapter.notifyDataSetChanged();
          chattingListViewPagerAdapter.chattingRoomListRecyclerviewAdapter1.notifyDataSetChanged();
      }
    }//onResume() 끝


    @Override
    public void onDetach() {
        super.onDetach();
        Log.v("check", fragment_name_for_log+"의 onDetach() 끝남");



    }//onDetach() 끝



    //chatuserconinfo에서  해당 유저가  참여한  방들의 모든 리스트를 가지고 온다.
    private  void getroomlist_from_chatuserconinfo(String useruid, final String useremail){


        //jsonobject의  어레이이므로 -> gson을 사용한다.
        GsonBuilder gsonBuilder=new GsonBuilder();
        gsonBuilder.setLenient();
        Gson gson=gsonBuilder.create();//gson객체 만들어냄.


        Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create(gson))
                .build();//리트로핏 뷸딩
        ApiService apiService = retrofit.create(ApiService.class);//api인터페이스 크리에이트

        //방  리스트 받아오는 call객체선언
        final Call<GetRoomList> getuserjoined_room_list=apiService.get_user_joined_room_list(useruid);

        //방 리스트 받아오기 callback
        getuserjoined_room_list.enqueue(new Callback<GetRoomList>() {
            @Override
            public void onResponse(Call<GetRoomList> call, Response<GetRoomList> response) {


                if (response.body() != null) {//response가  맞게 들어왔을때
                    arrayList=response.body().getRoomlist_data_for_user();
                    Log.v("check", fragment_name_for_log+"의  해당 유저 속한 방 리스트 가져오기 실행 결과 -> "+arrayList);

                    //뷰페이져 어뎁터에  학생 uid 보냄.
                    chattingListViewPagerAdapter=new ChattingListViewPagerAdapter(getActivity(),arrayList,useremail);//1-2




                    //뷰페이저에 뷰페이져 어뎁터 연결 시켜줌.
                    viewPager_for_chatting_room_list.setAdapter(chattingListViewPagerAdapter);



                }//response 값 null이 아닐때.

            }//onResponse끝

            @Override
            public void onFailure(Call<GetRoomList> call, Throwable t) {
                Log.v("check", fragment_name_for_log+"의  해당 유저 속한 방 리스트 가져오기 실행중 에러뜸  에러내용-> "+t.getMessage());


            }//onFailure 끝
        });//callback 실행 끝.
    }//getroomlist_from_chatuserconinfo() 끝끝


    //채팅방 리스트에  학생 uid가 들어가야하므로, 현재로그인한 학생의 이메일을 이용해
    //학생 uid를 가지고 온다.
    private void getstudentuid(final String studentemail){



        Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create())//json으로 받고 싶으면 쓰면안됨..
                .build();//리트로핏 뷸딩
        ApiService apiService = retrofit.create(ApiService.class);//api인터페이스 크리에이트
        Call<studentinforesult> getstudentinfo=apiService.sendemailtogetiprofile(studentemail);//학생정보 얻기위한  call객체

        //학생 정보 Callback rufrhk
        getstudentinfo.enqueue(new Callback<studentinforesult>() {
            @Override
            public void onResponse(Call<studentinforesult> call, Response<studentinforesult> response) {
                Log.v("check", fragment_name_for_log+"의 getsutdnetinfo()실행  통신  결과 ->  "+response.body());

                studentuid=response.body().getUid();//학생 정보  json으로 받아온것 중에서 ->  uid 내용 받아서 넣어줌.
                getroomlist_from_chatuserconinfo(studentuid,studentemail);


            }//onResponse()끝

            @Override
            public void onFailure(Call<studentinforesult> call, Throwable t) {
                Log.v("check", fragment_name_for_log+"의  getstudentinfo () 실행중  통신 에러뜸,  에러 내용 -> "+t);


            }//onFailure 끝
        });//getstudentinfo 콜백 끝.

    }//getstudentuid() 끝




    protected void refresh(){

        chattingListViewPagerAdapter.notifyDataSetChanged();
    }
}//FragmentForStudentChattingtype 끝
