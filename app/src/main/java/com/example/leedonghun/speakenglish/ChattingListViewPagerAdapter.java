package com.example.leedonghun.speakenglish;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.viewpager.widget.PagerAdapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * speakenglish
 * Class: ChattingListViewPagerAdapter.
 * Created by leedonghun.
 * Created On 2019-11-27.
 * Description: 학생 쪽  chatting 프래그먼트에서 -> 해당  유저가  참여한  오픈 채팅방과  일대일 채팅방응
 * 나눠주기 위해서 뷰페이져를 사용하였따
 * 이부분은  뷰페이져  어뎁터 부분으로  해당  뷰페이져에  뿌려질 ui관련 처리를  담당하는 곳이다.
 */
public class ChattingListViewPagerAdapter extends PagerAdapter {


    private final String class_name_for_log="ChattingListViewPagerAdapter";
    private Context mcontext;//어뎁터 안에서 슬 context
    private ArrayList<JsonObject> get_room_data_list;//room 목록이 들어갈 jsonobject 어레이 리스트
    private  String museruid;//학생 uid
    private String studentemail;//학생 이메일

    ChattingRoomListRecyclerviewAdapter chattingRoomListRecyclerviewAdapter;//roomlist_recyclerview에  방목록을 뿌릴  리사이클러뷰 어뎁터
    ChattingRoomListRecyclerviewAdapter chattingRoomListRecyclerviewAdapter1;//roomlist_recyclerview에  방목록을 뿌릴  리사이클러뷰 어뎁터
    RecyclerView roomlist_recyclerview;//채팅방이 뿌려질  리사이클려뷰

    RecyclerView.LayoutManager chatting_room_list_Recyclerview_manager;//리사이클러뷰 레이아웃 매니저


    public ChattingListViewPagerAdapter(Context context,ArrayList<JsonObject>arrayList,String studentemail){

        this.mcontext=context;//생성자로  가저온 context연결
        this.studentemail=studentemail;//생성자로 가져온  유저 콘텍스트
        this.get_room_data_list=arrayList;//student chatting type 프래그먼트로 부터 받아온   방 데이터  리스트.

    }//ChattingListViewPagerAdapter생성자 끝.



    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {


        //뷰페이지 인플레이트 할 인플레이터  선언.
        LayoutInflater layoutInflater= (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewpage_for_show_chatting_room_list=layoutInflater.inflate(R.layout.chatting_viewpager_page, container,false);

        container.addView(viewpage_for_show_chatting_room_list);//뷰페이져에  ->  해당 child view(리사이클러뷰) 넣어줌.

        //뷰 페이져 안에 있는  리사이클러뷰 ->  채팅방의 룸 리스틀 넣어준다.
        roomlist_recyclerview=viewpage_for_show_chatting_room_list.findViewById(R.id.chatting_room_list_recyclerview);


        //어레이 리스트에  해당 방  리스트 담아줌. -> 포지션 0일때임- 오픈 채팅
        ArrayList<JsonObject> jsonObjects0 =new ArrayList<>();

        //어레이 리스트에 해당 방 리스트 담아줌 -> 포지션 1일때- 일대일 채팅
        ArrayList<JsonObject> jsonObjects1 =new ArrayList<>();



        if(position==0){//포지션 0 일때  -> 이경우 오픈 채팅방  리스트가  리사이클러뷰에 뿌려진다.
            Log.v("check",class_name_for_log+"의 뷰페이져 포지션이  0일때 조건 실행됨");



            //해당 유저의 방 리스트  for문으로 돌려  -> 포지션 별로 나눠준다. 여기서는  포지션 0일때로 나눠줌.
            for(int i=0; i<get_room_data_list.size(); i++){

                //방 리스트 에서 namespace 부분  가져오기. -> replaceall 은  가져오니까  ""0""으로  되어있어서  ""를 한번 빼줬따.
                String a=get_room_data_list.get(i).get("roomnamespace").toString().replaceAll("\"", "");

                //방 리스트 돌리는 도중 ->  namespace가 0인  리스트를 을 뽑아서 위에 만든 리스트에 넣어준다.
                if(a.equals("0")){
                    jsonObjects0.add(get_room_data_list.get(i));
                }

            }//해당 방 리스트  for문 돌리기 끝


            //리사이클러뷰 처리
            chattingRoomListRecyclerviewAdapter=new ChattingRoomListRecyclerviewAdapter(jsonObjects0,mcontext,"s",studentemail);
            roomlist_recyclerview.setAdapter(chattingRoomListRecyclerviewAdapter);//리사이클러뷰 어뎁터에 연결시켜줌.
            ((SimpleItemAnimator) roomlist_recyclerview.getItemAnimator()).setSupportsChangeAnimations(false);//리사이클러뷰에  변경 될때 진행되는 애니메이션  false값 넣어줌.

            //리사이클러뷰 매니저 로  -> 리니어 형태의  vertical 방향으로  설정해줌.
            chatting_room_list_Recyclerview_manager=new LinearLayoutManager(mcontext,RecyclerView.VERTICAL,false);
            ((LinearLayoutManager) chatting_room_list_Recyclerview_manager).setReverseLayout(false);
            ((LinearLayoutManager) chatting_room_list_Recyclerview_manager).setStackFromEnd(false);

            //리사이클러뷰 매니저  연결 시켜줌.
            roomlist_recyclerview.setLayoutManager(chatting_room_list_Recyclerview_manager);
            roomlist_recyclerview.setNestedScrollingEnabled(false);//부드럽게 -> 스크롤링 하게



        }//포지션 0일때 조건 끝.
        else if(position==1){//포지션 1일때  ->> 이경우  1 대 1 채팅방 리스트가 리사이클러뷰에 뿌려진다.
            Log.v("check",class_name_for_log+"의 뷰페이져 포지션이  1일때 조건 실행됨");



            for(int i=0; i<get_room_data_list.size(); i++){


                String a=get_room_data_list.get(i).get("roomnamespace").toString().replaceAll("\"", "");

                if(a.equals("1")){//for문 돌린  방 리스트 중  namespace 1=> 일대일 채팅방에 해당하는 룸 리스트  어레이에  넣어줌.

                    jsonObjects1.add(get_room_data_list.get(i));
                }

            }//for문 끝


             //리사이클러뷰 처리
            chattingRoomListRecyclerviewAdapter1=new ChattingRoomListRecyclerviewAdapter(jsonObjects1,mcontext,"s",studentemail);
            roomlist_recyclerview.setAdapter(chattingRoomListRecyclerviewAdapter1);//리사이클러뷰 어뎁터에 연결시켜줌.
            ((SimpleItemAnimator) roomlist_recyclerview.getItemAnimator()).setSupportsChangeAnimations(false);//리사이클러뷰에  변경 될때 진행되는 애니메이션  false값 넣어줌.

             //리사이클러뷰 매니저 로  -> 리니어 형태의  vertical 방향으로  설정해줌.
            chatting_room_list_Recyclerview_manager=new LinearLayoutManager(mcontext,RecyclerView.VERTICAL,false);
            ((LinearLayoutManager) chatting_room_list_Recyclerview_manager).setReverseLayout(false);
            ((LinearLayoutManager) chatting_room_list_Recyclerview_manager).setStackFromEnd(false);

            //리사이클러뷰 매니저  연결 시켜줌.
            roomlist_recyclerview.setLayoutManager(chatting_room_list_Recyclerview_manager);
            roomlist_recyclerview.setNestedScrollingEnabled(false);//부드럽게 -> 스크롤링 하게


        }//포지션 1일때 조건 끝

        return viewpage_for_show_chatting_room_list;

    }// 뷰 인플레이터



    //뷰페이져 에서  해당 뷰 사라지게 해줌.
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
      //  super.destroyItem(container, position, object);



        container.removeView((View) object);
    }//destroyitem

    //뷰페이져의 수
    @Override
    public int getCount() {


        return 2;//뷰페이져 만들어내는  최대 카운트  2개.- 오픈 채팅방 ,  일대일 채팅방
    }//getCount() 끝



    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {


        return (view==(View)object);

    }//isViewFromObject 끝


}//ChattingListViewagerAdapter 끝
