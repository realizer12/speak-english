package com.example.leedonghun.speakenglish;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.otto.Subscribe;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;

/**
 * speakenglish
 * Class: ChattingRoomListRecyclerviewAdapter.
 * Created by leedonghun.
 * Created On 2019-11-27.
 * Description:채팅방 리스트가  쀼려질 리사이클러뷰의  어뎁터이다.
 * 여기서  1대1 채팅방 과  오픈 채팅방으로  나눠져서  ->  리스트에  뿌려지게 된다.
 */
public class ChattingRoomListRecyclerviewAdapter extends RecyclerView.Adapter<ChattingRoomListRecyclerviewAdapter.ChattingroomlistrecyclerviewViewholder>  {

    private String name_adapter_for_log="ChattingRoomListRecyclerviewAdapter";
    private LayoutInflater layoutInflater;//채팅 내용이 담길  커스텀뷰 인플레이터 하기위한 인플레이터
    private ArrayList<JsonObject> chatting_room_list;//어뎁터에서 받아서 뿌려질  채팅 데이터들
    private Context mcontext;//외부에서 받아온 ccontext 담기위한  context 변수
    private String muserposition;//해당 유저가 선생님인지 학생인지 여부
    private String stundetemail;//학생아이디로  채팅방에 들어갈때  학생이메일을 요구하므로  필요.



    //채팅 정보 담을  뷰홀더
    ChattingroomlistrecyclerviewViewholder chattingroomlistrecyclerviewViewholder;

    public ChattingRoomListRecyclerviewAdapter(){

    }

    //채팅룸  리스트 리사이클러뷰 어뎁터 생성자
    public ChattingRoomListRecyclerviewAdapter (ArrayList<JsonObject> chattinglist,Context context,String userposition,String studentemail){

       this.mcontext=context;//context연결
       this.chatting_room_list=chattinglist;//채팅룸 리스트 연결
       this.muserposition=userposition;//해당  유저  포지션
       this.stundetemail=studentemail;//해당 유저의 학생이메일
       GlobalBus.getBus_data().register(this);


       Log.v("check", name_adapter_for_log+"의 생성자 실행됨 -> 생성자  받은 파라미터중  룸 리스트 내용 확인 -> "+chatting_room_list);



    }//생성자 끝.


    @NonNull
    @Override
    public ChattingroomlistrecyclerviewViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext() ;//부모 레이아웃  컨텍스트 사용.
        layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//인플레이터   컨텍스트 권한 줌
        View view=layoutInflater.inflate(R.layout.chatting_room_list_container, parent,false);//채팅룸 리스트 정보  담을  뷰  인플레이트

        //채팅 뷰 홀더 ->  객체 선언 -> 뷰 홀더에  해당  view 연결 해줌.
        chattingroomlistrecyclerviewViewholder=new ChattingRoomListRecyclerviewAdapter.ChattingroomlistrecyclerviewViewholder(view);


        //뷰 홀더 리턴
        return chattingroomlistrecyclerviewViewholder;

    }//onCreateViewHolder 끝


    String userposition;//해당유저의 포지션
    String roomnumber;//해당 유저가 참여한 방번호
    String useruid;//해당 유저의 uid
    String namespace;//해당 참여한 방의 종류




    //뷰홀더에  필요 데이터 연결 시켜줌.
    @Override
    public void onBindViewHolder(@NonNull ChattingroomlistrecyclerviewViewholder holder, int position) {

          Log.v("onBindeviewholder확인", "viewholder 실행됨 -> postion:"+position);

        userposition=chatting_room_list.get(position).get("userposition").toString().replaceAll("\"", "");//해당 유저의  직업 포지션
        roomnumber=chatting_room_list.get(position).get("roomnumber").toString().replaceAll("\"", "");//해당 유저가 참여한  방이름
        useruid=chatting_room_list.get(position).get("useruid").toString().replaceAll("\"","");//해당  유저의  uid
        namespace=chatting_room_list.get(position).get("roomnamespace").toString().replaceAll("\"","");//해당  방의  종류

        if(userposition.equals("t")){//선생님 일때 경우


            //이부분은 나중에 선생님 부분  방 뿌려질때  - 1대1 채팅방 위주로  진행될  예정임
           Log.v("check", name_adapter_for_log+"에서 bindviewholder에서  userposition 선생일때 실행됨. ");

           getroominfoforbind(roomnumber,namespace, useruid, userposition, holder.chattingroom_number_textview,
                   holder.chattingroom_name_list_textview,holder.chattingroom_profile_imageView,holder.itemView,holder.chattingroom_recent_message_date,holder.chattingroom_recent_message,holder.chattingroom_unread_count);

            Log.v("onBindeviewholder확인", "viewholder 실행됨 -> userposition:"+userposition);
            Log.v("onBindeviewholder확인", "viewholder 실행됨 -> roomnumber:"+roomnumber);
            Log.v("onBindeviewholder확인", "viewholder 실행됨 -> useruid:"+useruid);
            Log.v("onBindeviewholder확인", "viewholder 실행됨 -> namespace:"+namespace);

            getsqlite_chatting_data(roomnumber,holder.chattingroom_recent_message,holder.chattingroom_recent_message_date);//sqlite에서  해당방  최근채팅 데이터 가져오는 메소드 -> 파라미터로 해당 방번호.
            get_unread_count(roomnumber, holder.chattingroom_unread_count);


       }else if(userposition.equals("s")){//학생 일때 경우

            Log.v("check", name_adapter_for_log+"에서 bindviewholder에서  userposition 학생일때 실행됨. ");

           getroominfoforbind(roomnumber,namespace, useruid, userposition, holder.chattingroom_number_textview,
                   holder.chattingroom_name_list_textview,holder.chattingroom_profile_imageView,holder.itemView,holder.chattingroom_recent_message_date,holder.chattingroom_recent_message,holder.chattingroom_unread_count);

            Log.v("onBindeviewholder확인", "viewholder 실행됨 -> userposition:"+userposition);
            Log.v("onBindeviewholder확인", "viewholder 실행됨 -> roomnumber:"+roomnumber);
            Log.v("onBindeviewholder확인", "viewholder 실행됨 -> useruid:"+useruid);
            Log.v("onBindeviewholder확인", "viewholder 실행됨 -> namespace:"+namespace);

            getsqlite_chatting_data(roomnumber,holder.chattingroom_recent_message,holder.chattingroom_recent_message_date);//sqlite에서  해당방  최근채팅 데이터 가져오는 메소드 -> 파라미터로 해당 방번호.
            get_unread_count(roomnumber, holder.chattingroom_unread_count);


       }//userpostion-> 선생님일떄 조건 끝




    }//onBindViewHolder() 끝



    //아이템 개수
    @Override
    public int getItemCount() {

        Log.v("check", name_adapter_for_log+"의  채팅룸 리사이클러뷰 아이템 개수 -> "+chatting_room_list.size());


        return chatting_room_list.size();

    }//getItemCount() 끝



    //채팅룸 리스트  뷰홀더
    class ChattingroomlistrecyclerviewViewholder extends RecyclerView.ViewHolder{


          ImageView chattingroom_profile_imageView;//채팅룸  프로필 이미지뷰  //1-1
          TextView  chattingroom_name_list_textview;//채팅룸 이름 이 들어감 ->  오픈 채팅방의 경우는 oo's openchatting room  / 1대1 채팅의 경우는 -> 상대의 이름 //1-2
          TextView  chattingroom_number_textview;//채팅룸 참여자 수 담을 텍스트뷰 //1-3
          TextView  chattingroom_recent_message;// 채팅룸  최근  메세지 들어가는 텍스트뷰 //1-4
          TextView  chattingroom_unread_count;// 채팅룸  메세지  안읽은  갯수 텍스트뷰//1-5
          TextView  chattingroom_recent_message_date; //채팅룸  가장 최근에 온  메세지 날짜 //1-6


        public ChattingroomlistrecyclerviewViewholder(@NonNull View itemView) {
            super(itemView);

            chattingroom_profile_imageView=itemView.findViewById(R.id.chatting_profile_image);//1-1
            chattingroom_name_list_textview=itemView.findViewById(R.id.chatting_room_namelist);//1-2
            chattingroom_number_textview=itemView.findViewById(R.id.chatting_room_number);//1-3
            chattingroom_recent_message=itemView.findViewById(R.id.chatting_recent_message);//1-4
            chattingroom_unread_count=itemView.findViewById(R.id.chatting_unread_count);//1-5
            chattingroom_recent_message_date=itemView.findViewById(R.id.chatting_recent_date);//1-6


        }//chattingroomlistrecyclerviewViewholder 생성자 끝


    }//뷰홀더 끝


    //서비스에서 백그라운드로 받은  채팅 데이터를 이벤트버스로 보냈고,  그걸 받기위한  메소드이다.
    @Subscribe
    public  void getMessage(JSONObject receivedata) throws JSONException{

        if(receivedata.has("onetooneout") && receivedata.getString("onetooneout").equals("1")){
            Log.v("dddddddd", "onetooneout가 실행됨");
            notifyDataSetChanged();
            return;
        }
        if(receivedata.has("onetoonefirst_messge")){


            return;
        }


        Log.v("dddddddd","ChattingRoomlistrecyclerviewadpater에서 이벤트 버스 옴 -> 내용,"+getItemCount()+ String.valueOf(receivedata));

        String a;//채팅방 리스트에서   강  아이템  카운트 별 for문 돌려 나온   방의  룸넘버들을  넣을  변수.
        String b= receivedata.getString("roomnumber");//백그라운드 서비스에서 이벤트 버스로 받은 데이터의  룸넘버를 넣는 변수.

        //namespace와 charoder로  1대1채팅방 첫번째 메세지가 오는 경우에는 ->  어뎁터 전체를 한번 change시켜준다.
        int namespace= Integer.parseInt(receivedata.getString("roomnamespace"));

        int chatorder = -1;
        if(receivedata.getString("chatorder") != null) {

            chatorder  = Integer.parseInt(receivedata.getString("chatorder"));
        }

        for(int i=0; i<getItemCount(); i++){//현재 방안에  아이템 수만큼  for문을 돌려 -> 방 번호들을  가지고옴.

            //a에  방번호 담아줌.
             a=chatting_room_list.get(i).get("roomnumber").toString().replaceAll("\"", "");


             //만약에  방번호가  서비스로 부터 온 데이터와  방번호가 같다면, 해당  포지션(여기선 각  아이템  카운트가  포지션으로  일정하게 받아놈) 의
             //아이템을 업데이트 해준다
             if(a.equals(b)){
                 Log.v("dddddddd","ChattingRoomListRecyclerviewAdapter에서 roomnumber가 일치한 포지션 -> "+i);

                if(i != 0){//포지션이 0이 아닐때 즉  맨처음 포지션이  아닐때.
                    Log.v("dddddddd","포지션이 0이 아닐때이다.");

                   if(namespace==1 && chatorder==1){//일대일 채팅방에 ->charorder가  1 일경우  -> 이경우는  보낸 사람이 학생일때만 가능함.

                       Log.v("dddddddd", "ChattingRoomListRecyclerviewAdapter에서 채팅메세지 업데이트 된 방이 포지션 0이 아니어서  위치  0으로 옮겨줌 일대일 채팅방에 ->charorder가  1 일경우");
                       JsonObject present_room_data = chatting_room_list.get(i);//밑에서 맨앞에  해당방 데이터를 넣기전에  삭제 시키므로 미리  값을 받아놓는다.
                       notifyDataSetChanged();//이때는  전체를 한번 전체 데이터를  업데이트 시켜준다.
                       notifyItemMoved(i, 0);//메세지가 온게  가장 최근 메세지 이므로 리사이클러뷰의  맨처음 포지션으로 보내준다.
                       chatting_room_list.remove(i);//원래 해당방  위치의  값은 없애준다.
                       chatting_room_list.add(0, present_room_data);//위에서 받아논  해당값 방을  맨앞에 넣어준다. -> 다른  데이터들은 1씩 뒤로 밀린다.

                   }else {
                       Log.v("dddddddd", "ChattingRoomListRecyclerviewAdapter에서 채팅메세지 업데이트 된 방이 포지션 0이 아니어서  위치  0으로 옮겨줌일대일 채팅방에 ->charorder가  1 아닐경우");
                       JsonObject present_room_data = chatting_room_list.get(i);//밑에서 맨앞에  해당방 데이터를 넣기전에  삭제 시키므로 미리  값을 받아놓는다.
                       notifyItemChanged(i);//해당 아이템의 데이터를  변화 시켜준다.
                       notifyItemMoved(i, 0);//메세지가 온게  가장 최근 메세지 이므로 리사이클러뷰의  맨처음 포지션으로 보내준다.
                       chatting_room_list.remove(i);//원래 해당방  위치의  값은 없애준다.
                       chatting_room_list.add(0, present_room_data);//위에서 받아논  해당값 방을  맨앞에 넣어준다. -> 다른  데이터들은 1씩 뒤로 밀린다.
                   }
                }else{//포지션이 0일때  맨처음일때-> 이때는 그냥  아이템 데이터만  변환 시켜주면 됨.

                    Log.v("dddddddd","포지션이 0일 때이다.");

                    if(namespace==1 && chatorder==1){//일대일 채팅방에 ->charorder가  1 일경우

                        Log.v("dddddddd","포지션이 0일때 일대일 채팅방에 ->charorder가  1 일경우 ");
                       notifyDataSetChanged();


                    }else{

                        notifyItemChanged(i);//해당 아이템의 데이터를  변화 시켜준다.
                        Log.v("ddddddd","포지션이 0일때  일대일 채팅방에 ->charorder가  1 일이 아닐경우 ");
                    }

                }


             }else{//방번호가  서비스에서 받은 데이터의 방번호와 -> 채팅리스트 for문 돌린것중 나온  데이터 룸넘버와 같지 않을경우.

                 Log.v("dddddddd","ChattingRoomListRecyclerviewAdapter에서 roomnumber가 일치하지 않은 포지션 -> "+i);
                  notifyDataSetChanged();
             }

        }//for문  끝


    }//이벤트 getMessage() 끝


    private SqLiteOpenHelperClass sqLiteOpenHelper;//sqlite 생성하는 클래스
    private SQLiteDatabase database;//sqlite 데이터베이스
    private String teacherloginedid;//선생님 로그인 아이디 담을  객체
    private String studentloginedid;//학생 로그인 아이디 담을 객체
    private String loginid_for_sql;//유저의 아이디  @ 빼준  sqlite 데이터베이스 이름 담을 객체
    private String recentmessage;//최근 메세지 담을 객체
    private String recent_message_date;//최근 메세지가  온  날짜 담을 객체
    private String roomnumber_for_cursor;//sqlite에서 받아온  데이터의  룸 번호를 담을 객체
    private Cursor cursor;//sqlite데이터를  받아올 cursor객체
    private Date  message_date;//메세지가 보내진  날짜
    private String message_time;//위 메세지 날짜 -> 시간 형식으로 받은것  넣을  스트링 객체
    private int viewtype;//해당 데이터의  viewtype

    private String sql = "select * from chatting_data_store where roomnumber=?";//chatting_data_store테이블에서  해당  룸넘버에 들어있는 내용 조회한다.
    //해당 채팅방  채팅 기록 sqlite에서  가지고 오기
    public void getsqlite_chatting_data(String chatting_roomnumber,TextView recent_message, TextView recentdate){


        SharedPreferences getid_teacher = mcontext.getSharedPreferences("loginteacherid",MODE_PRIVATE);//로그인 아이디  쉐어드에 담긴거 가져옴
        teacherloginedid= getid_teacher.getString("loginidteacher","");//로그인 아이디 가져옴

        SharedPreferences getid_student = mcontext.getSharedPreferences("loginstudentid", MODE_PRIVATE);//로그인 아이디  쉐어드에 담긴거 가져옴
        studentloginedid = getid_student.getString("loginid", "");//로그인 아이디 가져옴

        if(teacherloginedid.equals("")){//선생님 로그인이 아닐때-> 학생 로그인

            Log.v("check", studentloginedid+"학생입니다 ");

            //학생 로그인 아이디에서  @빼줘서 데이터 베이스 -> 이름  가지고옴.
            loginid_for_sql=studentloginedid.replaceAll("@", "");
            sqLiteOpenHelper=new SqLiteOpenHelperClass(mcontext,loginid_for_sql , null,1 );//sqlite 클래스
            database=sqLiteOpenHelper.getWritableDatabase();//데이터베이스 -> sqlite클래스


        }else{//학생 로그인 아닐때 선생님 로그인-> 선생님 로그인

            Log.v("check", teacherloginedid+"선생님입니다. ");

            loginid_for_sql=teacherloginedid.replaceAll("@", "");
            sqLiteOpenHelper=new SqLiteOpenHelperClass(mcontext,loginid_for_sql , null,1 );
            database=sqLiteOpenHelper.getWritableDatabase();

        }


        cursor=database.rawQuery(sql,new String []{chatting_roomnumber});///위 쿼리문  데이터베이스로 보냄
        cursor.moveToLast();//커서를 가지고온  값들중에 가장 마지막으로 보내준다.

        if( cursor != null && cursor.isLast()){//커서가 null이 아니고 cursor의 가장  맨 마지막 값 즉 가지고 온 rawquery중에서 가장  마지막 값 인경우

            Log.v("check", "ChattingRoomListRecyclerview에서 최근 메세지 가지고옴" + cursor.getString(11));
          recentmessage= cursor.getString(11);//가장 최근 메세지
          recent_message_date=cursor.getString(9);//최근  메세지의  날짜

            roomnumber_for_cursor=cursor.getString(3);//커서가 가있는 마지막 데이터의  룸번호
            viewtype= Integer.parseInt(cursor.getString(8));//마지막 데이터의 viewtype
            //해당 데이터의 룸넘버와 현재  홀더가 보여주는 방의 룸넘버가  같다면



            if(roomnumber.equals(chatting_roomnumber) && viewtype==3 || roomnumber.equals(chatting_roomnumber) && viewtype==4 || roomnumber.equals(chatting_roomnumber) && viewtype==5) {//가장 최근에 해당하는  룸넘버의 최근 메세지를 넣어줌.

                Log.v("check", "ChattingRoomListRecyclerview에서  가장 최근 해당 하는 룸넘버 최근 메세지  뷰타입 3 실행됨->"+recentmessage);

                try {

                    ///최근 메세지 칸 보이게 만듬
                    recent_message.setVisibility(View.VISIBLE);
                    if(cursor.getString(8).equals("3")){//일반 메세지타입

                        //채팅 뷰 홀더에서  최근 메세지->  넣어줌
                        recent_message.setText(recentmessage);

                    }else if(cursor.getString(8).equals("4")){//사진 타입 메세지일 경우

                        //최근 메세지가  이미지 업로드 이므로, 이미지 path대신 pictureuploade라고  멘트날림.
                        recent_message.setText("Picture uploaded!");

                    }else if(cursor.getString(8).equals("5")){//최근 업로드 메세지가 비디오 타입인 경우

                        //비디오 업로드이므로,  Videouploaded라고 씀.
                        recent_message.setText("Video uploaded!");
                    }


                    Calendar calendar_for_get_present_time=Calendar.getInstance();//현제 시간을 가지고 오기 위한  캘린더 변수
                    calendar_for_get_present_time.add(Calendar.DATE, 0);//캘린더의 현재 시간 가지고옴.
                    Date present_date=calendar_for_get_present_time.getTime();//date 객체로 바꿔줌.


                    //Date객체에  ->  yyyy-MM-dd HH:mm형태로   string 으로 온  최근 메세지  날짜를  date화 시킴.
                    message_date = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(recent_message_date);

                    //date화 시킨   값을 다시  aaa hh:mm화 시켜주고  string변수로 바꿔줌.
                    message_time=new SimpleDateFormat("aaa hh:mm").format(message_date);


                    //아래  두변수를 가지고 년월일 을  비교해서 같은 년,월,일 이라면  시간만 표시하게 하고  다른 시간이라면,
                    //채팅방 리스트의 채팅메세지의  시간을 년,월,일로 표기하도록 한다.
                    //메세지의  시간 -> 년월 일만 가지고옴.
                    String message_date_for_compare=new  SimpleDateFormat("yyyy-MM-dd").format(message_date);

                    //현재 시간 -> 년 월 일만 가지고옴.
                    String present_date_for_compare=new SimpleDateFormat("yyyy-MM-dd").format(present_date);


                    if(message_date_for_compare.equals(present_date_for_compare)){//메세지의 저장된 날짜가  오늘과 같을때-> 시간을  표시해줌.

                        //룸 리스트 중에 최근 메세지 에대한 시간  넣어줌.
                        recentdate.setVisibility(View.VISIBLE);
                        recentdate.setText(message_time);//

                    }else{//메세지 저장날짜와 오늘 날짜가 다를때  년, 월, 일로  표기 해준다.

                        //룸 리스트 중에 최근 메세지에  년,월, 일  형태로 넣어준다
                        recentdate.setVisibility(View.VISIBLE);
                        recentdate.setText(message_date_for_compare);//
                    }


                    Log.v("check", "ChattingRoomListRecyclerview에서  넣어줌" + cursor.getString(11));

                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }//해당 데이터의 룸넘버와 현재  홀더가 보여주는 방의 룸넘버가  같을 경우 끝

        }//커서가 null이 아니고 cursor의 가장  맨 마지막 값 즉 가지고 온 rawquery중에서 가장  마지막 값 인경우 끝

        if (cursor != null) {//cursor가  null이 아니라면

            cursor.close();//cursor다시 닫아줌.
        }
    }//getsqlite_chatting_data() 끝


  //해당 룸넘버의  -> 안읽은 메세지  숫자를 가지고온다.
  public  void get_unread_count(String chatting_roomnumber,TextView unreadcount){


      String sql_for_get_unread_count="select * from  chatting_data_store where roomnumber=? and readornot=1";
      Cursor cursor_for_get_unread_count;
      SQLiteDatabase  database;

      SharedPreferences getid_teacher = mcontext.getSharedPreferences("loginteacherid",MODE_PRIVATE);//로그인 아이디  쉐어드에 담긴거 가져옴
      String teacherloginedid= getid_teacher.getString("loginidteacher","");//로그인 아이디 가져옴

      SharedPreferences getid_student = mcontext.getSharedPreferences("loginstudentid", MODE_PRIVATE);//로그인 아이디  쉐어드에 담긴거 가져옴
      String studentloginedid = getid_student.getString("loginid", "");//로그인 아이디 가져옴


      if(teacherloginedid.equals("")){//선생님 로그인이 아닐때-> 학생 로그인

          Log.v("check", studentloginedid+"학생입니다 ");

          //학생 로그인 아이디에서  @빼줘서 데이터 베이스 -> 이름  가지고옴.
         String loginid_for_sql=studentloginedid.replaceAll("@", "");
         SQLiteOpenHelper sqLiteOpenHelper=new SqLiteOpenHelperClass(mcontext,loginid_for_sql , null,1 );//sqlite 클래스
        database=sqLiteOpenHelper.getReadableDatabase();//데이터베이스 -> sqlite클래스-> 읽기로 접근


      }else{//학생 로그인 아닐때 선생님 로그인-> 선생님 로그인

          Log.v("check", teacherloginedid+"선생님입니다. ");

          String loginid_for_sql=teacherloginedid.replaceAll("@", "");
          SQLiteOpenHelper sqLiteOpenHelper=new SqLiteOpenHelperClass(mcontext,loginid_for_sql , null,1 );
          database=sqLiteOpenHelper.getReadableDatabase();

      }

      cursor_for_get_unread_count=database.rawQuery(sql_for_get_unread_count, new String []{chatting_roomnumber});//해당 쿼리를  sqlite로 보냄.

      int un_readcount =cursor_for_get_unread_count.getCount();//해당 sqlite에서 안읽은 메세지로 처리된  -> int 객체
      String unread_count_cast_string= String.valueOf(un_readcount);

       Log.v("check", "ChattingRoomListRecyclerVoewAdapter의  안읽은 숫자"+un_readcount);

       if(un_readcount>0){//채팅 읽지 않은 숫자가 0 보다 클때

           Log.v("check", "ChattingRoomListRecyclerVoewAdapter의 안읽은 메세지 0 보다 크므로 읽지 않은 텍스트뷰에  해당  읽지 않은 숫자를 넣어줌");
           unreadcount.setVisibility(View.VISIBLE);//해당  안읽은 메세지가  1미만이므로,  안읽은  텍스트뷰를  보이지 않도록 한다.
           unreadcount.setText(unread_count_cast_string);//해당 숫자를  넣어줌.

       }else if(un_readcount<1){//읽지 않은 숫자가 1보다 작을때 -> 즉  다읽었을때

           Log.v("check", "ChattingRoomListRecyclerVoewAdapter의 안읽은 메세지 1 보다 작으므로 읽지 않은 텍스트뷰가 필요가 없음");
           unreadcount.setVisibility(View.INVISIBLE);//해당  안읽은 메세지가  1미만이므로,  안읽은  텍스트뷰를  보이지 않도록 한다.

       }else {
           Log.v("check", "ChattingRoomListRecyclerVoewAdapter의 안읽은 메세지  -> 카운트가 이상해서 텍스트뷰 일단 없애줌. ");
           unreadcount.setVisibility(View.INVISIBLE);//해당  안읽은 메세지가  1미만이므로,  안읽은  텍스트뷰를  보이지 않도록 한다.
       }




  }//get_unread_count() 끝




 //해당  룸 이름의  룸 정보  디비에서 가져오기 위한  메소드
 public void getroominfoforbind(final String roomnumber, final String namespace, String useruid, final String userposition, final TextView chattingclient_textview, final TextView chatting_room_name_textview, final ImageView room_imageview, final View room_entire_itemview, final TextView recentdate, final TextView recent_message,final TextView unread_count){

          Log.v("check", name_adapter_for_log+"의 getroominfoforbind 실행됭");


        Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create())
                .build();//리트로핏 뷸딩
        ApiService apiService = retrofit.create(ApiService.class);//api인터페이스 크리에이트

        //리스트에 현제 포지션 방의  정보를 가지고 오기위한  call 함수.
        Call<GetRoomInfoForChattingRoomList> getRoomInfoForChattingRoomListCall =apiService.get_user_joined_room_info(roomnumber,namespace,useruid,userposition);

        //위 call객체 callback
        getRoomInfoForChattingRoomListCall.enqueue(new Callback<GetRoomInfoForChattingRoomList>() {
            @Override
            public void onResponse(Call<GetRoomInfoForChattingRoomList> call, Response<GetRoomInfoForChattingRoomList> response) {
               Log.v("check", name_adapter_for_log+"의  getroominfobind 에서  response값->"+response.body());


               String chatting_room_clientcount = response.body().getChatclientcount();//해당방의  clientcount
               final String chatting_room_name = response.body().getRoom_name();//해당방의  이름
               String chatting_room_image_path = response.body().getRoom_profile_image();//해당방의  이미지
               final String teacheruid=response.body().getTeacheruid();//선생님  유아이디

               final String chatting_teacher_info = response.body().getRoom_teacherinfo();//해당방의  teacheremail부분  값.


                if(namespace.equals("0")) {//오픈채팅방일때만  해당 방의 클라이언트 수와  방이름이 같이 들어간다.


                    if(userposition.equals("s")){//해당 유저의  포지션이   학생일때

                        //해당방  클라이언트 수  연결 시켜줌.
                        chattingclient_textview.setText(chatting_room_clientcount);

                        //해당방의 이름이 들어간다.
                        chatting_room_name_textview.setText(chatting_room_name);

                        getsqlite_chatting_data(roomnumber,recent_message,recentdate);//sqlite에서  해당방  최근채팅 데이터 가져오는 메소드 -> 파라미터로 해당 방번호.
                        get_unread_count(roomnumber, unread_count);

                        //해당방  이미지  넣어줌.
                        try {

                            //이미지 저장된 url을  사용해서 ->  glide로  프로필  넣어줌,
                            URL url  = new URL("http://13.209.249.1/" + chatting_room_image_path);
                            Glide.with(mcontext).load(url)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)//디스크 캐시 저장도 꺼줌. -> glide는  빠르게 이미지 로딩을 위해 url이 같으면, 캐시에 저장된 똑같은 이미지 그대로 넣어줌.
                                    .skipMemoryCache(true)//캐시 저장 안함.  -> 이러니까 계속  서버 이미지 경로  같은데 내용물 바껴도  계속  예전꺼 나옴  그래서 캐쉬 없애줌.
                                    .into(room_imageview);// 방 이미지 넣어줌.

                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }



                        //해당 룸 아이템  전체 눌렸을때
                        room_entire_itemview.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                //해당 학생이 클릭한  학생  채팅 룸으로 들어가짐.
                                Intent intent_for_goto_chatiingroom=new Intent(mcontext,ChattingRoomActivityForStudent.class);

                                //현재 이부분에서 클릭리스너는 -> 해당 방이   오픈 채팅방이므로 다음과 같이  값이 채팅방엑티비티로 넘어간다.
                                // 룸넘버는 -> 해당 오픈 채팅방 선생님 uid가  된다.
                                // 학생  이므로  학생 채팅  엑티비티에서  필요한  학생이메일을 보내준다.
                                // 오픈 채팅방의 경우 teachernmae이  별로 필요없는 값이지만,  혹시몰라 받아온 선생님  이메일을 보냄.
                                //마지막  채팅 타입은  0-> 오픈 채팅방인 경우로 보냄.
                                intent_for_goto_chatiingroom.putExtra("teacheruid",roomnumber);
                                intent_for_goto_chatiingroom.putExtra("studentemail", stundetemail);
                                intent_for_goto_chatiingroom.putExtra("teachername", chatting_teacher_info);
                                intent_for_goto_chatiingroom.putExtra("chattingroomtype", 0);

                                mcontext.startActivity(intent_for_goto_chatiingroom);//이텐트  시작 -> 채팅방 엑티비티 가짐.

                            }//onClick()끝
                        });//해당 룸 아이템 전체 눌렸을때 끝.


                    }//선생일때는  아무것도 필요없음 이미  해당  채팅방을 만들어놓음-> 선생님 전용 오픈 챗방이니까.



                }else{//1대1 채팅방일때  해당방의  클라이언트 수는 들어가지 않고 방이름만 들어감


                    if(userposition.equals("s")) {

                        //해당방의 이름이 들어간다.
                        chatting_room_name_textview.setText(chatting_room_name + " teacher");


                        getsqlite_chatting_data(roomnumber,recent_message,recentdate);//sqlite에서  해당방  최근채팅 데이터 가져오는 메소드 -> 파라미터로 해당 방번호.
                        get_unread_count(roomnumber, unread_count);
                        //해당 방의 프로필 이미지.
                        try {

                            URL url  = new URL("http://13.209.249.1/" + chatting_room_image_path);
                            Glide.with(mcontext).load(url).into(room_imageview);// 방 이미지 넣어줌.

                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }

                        room_entire_itemview.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent_for_goto_chatiingroom=new Intent(mcontext,ChattingRoomActivityForStudent.class);


                                intent_for_goto_chatiingroom.putExtra("teacheruid",teacheruid);
                                intent_for_goto_chatiingroom.putExtra("studentemail", stundetemail);

                                //이경우 ->  채팅방 이름에 선생님이름이  넣어져서  보내진다.
                                intent_for_goto_chatiingroom.putExtra("teachername", chatting_room_name);
                                intent_for_goto_chatiingroom.putExtra("chattingroomtype", 1);//채팅타입은 1대1

                                mcontext.startActivity(intent_for_goto_chatiingroom);
                            }
                        });


                    }else if(userposition.equals("t")){// 유저 포지션이  선생님 일때 -> 선생님의 경우는 오픈채팅방은 이미  만들어 놓았기 때문에  1대1 채팅을  뿌리기위한 조치를 해야함

                        Log.v("check", "선생님 로그인 상태인데,   학생이  1대1 채팅방 나감");
                        //그리고 ->학생이 -> 해당 선생님 1대1 채팅방을 삭제 했는지 여부를 판단해서  경우마다 ui를  다르게 내보내기 위한 메소드
                        check_student_delete_one_messenger_or_not(roomnumber,chatting_room_name_textview,chatting_room_name,recent_message,recentdate,chatting_room_image_path,room_imageview,room_entire_itemview,unread_count);//해당 1대1 채팅방 참여인원수를  가지고 온다.

                    }//해당 유저의 포지션이  선생님 일떄.

                }//1대1 채팅방일때 해당방의 클라이언





            }//onResponse()

            @Override
            public void onFailure(Call<GetRoomInfoForChattingRoomList> call, Throwable t) {
                Log.v("check", name_adapter_for_log+"의 getroominfo에서  에러나옴.-> 에러내용"+t);


            }//onFailure()

        });//callback 끝.
    }//getroominfo() 끝


    //1대1 채팅방에서 -> 해당방에서 -> 학생이 나갔는지 여부를 알아봐야함.
    public int check_student_delete_one_messenger_or_not(final String roomnumber, final TextView chatting_room_name_textview, final String chatting_room_name, final TextView recent_message, final TextView recentdate, final String chatting_room_image_path, final ImageView room_imageview, final View room_entire_itemview,final TextView unread_count){

        Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create())
                .build();//리트로핏 뷸딩
        ApiService apiService = retrofit.create(ApiService.class);//api인터페이스 크리에이트

        RequestBody roomnumber_request=RequestBody.create(MediaType.parse("text/plain"), roomnumber);//roomnumber-> requsetbody형태 변수로

        Call<ResponseBody> get_count_of_user_joined_in_onemessenger=apiService.get_user_joined_count_in_onemessenger(roomnumber_request);//call 객체

        // call에대한 -> 결고값
        get_count_of_user_joined_in_onemessenger.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                String result_for_getcount= null;
                try {
                    result_for_getcount = response.body().string();
                    Log.v("check", "ChattingRoomListRecyclerviewAdapter에서 "+roomnumber+"의 참여자 수 결과 나옴 ->"+result_for_getcount);


                    if(result_for_getcount.equals("2")) {//해당 1대1 채팅방에 선생님과 학생 모두 있을때.
                        Log.v("check", "ChattingRoomListRecyclerviewAdapter에서 "+roomnumber+"의 참여자 2명-> 학생 선생 모두 참여중일때이다.");

                        //만약에 -> 학생이  데이터를 지웠을 경우에는 -> 뷰 색과  해당 투명도를  다르게 했으모로  다시  2명이 된경우가 되니까
                        //해당 뷰에 대하여 원상 복귀를 시켜놓는다
                        room_entire_itemview.setBackgroundColor(Color.TRANSPARENT);//다시 돌아오는 것이므로 -> 색깔을  돌려준다
                        room_entire_itemview.setAlpha(1);//투명도도 원래대로 100%임.

                        //1대1 채팅방이므로 채팅 상대의 이름이  방이름으로 들어간다.
                        chatting_room_name_textview.setText(chatting_room_name);
                        getsqlite_chatting_data(roomnumber,recent_message,recentdate);//sqlite에서  해당방  최근채팅 데이터 가져오는 메소드 -> 파라미터로 해당 방번호.
                        get_unread_count(roomnumber, unread_count);
                        //해당 방의 프로필 이미지.
                        try {

                            URL url  = new URL("http://13.209.249.1/" + chatting_room_image_path);
                            Glide.with(mcontext).load(url).into(room_imageview);// 방 이미지 넣어줌.

                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }


                        //해당 룸  클릭 되었을떄
                        room_entire_itemview.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                //선생님 엑티비티로 가짐.
                                Intent intent_for_goto_chatiingroom=new Intent(mcontext,ChattingRoomActivityForTeacher.class);

//
                                intent_for_goto_chatiingroom.putExtra("RoomName",chatting_room_name);
                                intent_for_goto_chatiingroom.putExtra("Roomnumber", roomnumber);
                                intent_for_goto_chatiingroom.putExtra("chattingroomtype", 1);//채팅타입은 1대1

                                mcontext.startActivity(intent_for_goto_chatiingroom);
                            }
                        });


                    }else if(result_for_getcount.equals("1")){//해당 1대1 채팅방에 선생만 남아있는 경우-> 학생이  채팅방 나가기를 함. -> 이경우에는 선생님 채팅방에뿌려지는 리스트에서 해당 채팅방은  -> block처리 해준다.
                        Log.v("check", "ChattingRoomListRecyclerviewAdapter에서 "+roomnumber+"의 참여자 1명-> 학생이 나가서 선생만 있을때이다.");



                        room_entire_itemview.setBackgroundColor(Color.GRAY);//상대가 삭제 했음을 알리기위해-> 해당 채팅방  뷰 색깔을 회색으로 놓고 투명도를 반투명으로 놓는다.
                        room_entire_itemview.setAlpha(0.5F);

                        //1대1 채팅방이므로 채팅 상대의 이름이  방이름으로 들어간다.
                        chatting_room_name_textview.setText(chatting_room_name);
                        getsqlite_chatting_data(roomnumber,recent_message,recentdate);//sqlite에서  해당방  최근채팅 데이터 가져오는 메소드 -> 파라미터로 해당 방번호.
                        get_unread_count(roomnumber, unread_count);
                        //해당 방의 프로필 이미지.
                        try {

                            URL url  = new URL("http://13.209.249.1/" + chatting_room_image_path);
                            Glide.with(mcontext).load(url).into(room_imageview);// 방 이미지 넣어줌.

                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }


                        //해당 룸  클릭 되었을떄
                        room_entire_itemview.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            //이때는 상대가 채팅 내용을 삭제한 상태이므로,
                            //토스트로 선생님에게 알려준다.
                            new Toastcustomer(mcontext).showcustomtaost(null, "Student name : "+chatting_room_name+"\n block this chatting_room!! \n\nIf you have any question \nabout this happen \n\n plz email to SpeakEnglish",1500,600);


                            }
                        });

                        //학생이 나갔기 때문에 -> 해당 방  값을 지워준다.
                        String sql_fordelete="DELETE FROM chatting_data_store WHERE roomnumber='"+roomnumber+"'";
                        database.execSQL(sql_fordelete);

                        database.close();



                    }else{//그밖에 response값이  2또는 1이 아닌경우이다.
                        //이경우에는  먼가 잘못된경우이다. 일단 그냥 -> 납두고  로그로 이상함을 알린다.
                        Log.v("check", "ChattingRoomListRecyclerviewAdapter에서 "+roomnumber+"의 참여자 "+result_for_getcount+"명-> 문제있으니 알아봐야됨");

                    }




                } catch (IOException e) {
                    e.printStackTrace();
                }



            }//onResponse()끝

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
              Log.v("check", "ChattingRoomListRecyclerviewAdapter에서 "+roomnumber+"의 참여자 수 가지고 오는 중 실패함");

            }//onFailure()끝
        });//일대일 메신저 참여자수 가져오기  끝,


        //0일 경우에는 삭제 안한거임.
        return 0;
    }


}//리사이클러뷰 어뎁터 끝.
