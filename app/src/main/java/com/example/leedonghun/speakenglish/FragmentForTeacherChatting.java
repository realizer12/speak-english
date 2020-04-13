package com.example.leedonghun.speakenglish;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
//import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
 * Class: FragmentForTeacherChatting.
 * Created by leedonghun.
 * Created On 2019-01-12.
 *
 * Description:  선생님 로그인후 화면에서  나오는  프래그먼트 중에서  채팅방 리스트를  보여주는  프래그먼트이다.
 * 오픈 채팅방의 경우는  각 선생님 마다 하나씩  분배되므로  기존에  첫 로그인 부터  해당 방을  리스트에서 확인할수 있다 ->  리사이클러뷰에  포함 x
 * 그리고 1대1  채팅방의 경우에는 학생이  선생님에게  채팅을  걸면  갯수상관 없이 계속해서  늘어나야 하므로,  이부분은  리사이클러뷰로 계속되는 ui를  업데이트 한다.
 * 그리고  fcm을 받아서 최근  메세지를  채팅방에서  보여주며,   해당  인원수 밑  읽지 않은   메세지의  총수도 보여준다.
 * 그리고  해당 방들을 누르면 해당 방의 채팅 엑티비티로 가지도록  처리가 들어가고,
 * 방이름은  기본적으로, 해당  인원들의  이름을  나열해준다.
 * 해당 방을 길게 누르면 방 삭제가 가능하며  오픈 채팅방의 경우는  선생님 직속  채팅방이어서  삭제 할수가 없다.
 */
public class FragmentForTeacherChatting extends Fragment {

    String  stringforthisfragmentname="Fragmentforteacherchatting";//로그에  사용될  현재 프래그먼트  이름.
    String loginedid;//쉐어드로 받아온  해당 선생님 로그인 아이디.
    JSONObject roominfo;
    //오픈 채팅방
    LinearLayout open_chatting_linearlayout;//오픈 채팅 방 리스트 리니어 레이아웃 / 1-1
    ImageView open_chatting_roomimage;//오픈 채팅방  리스트 이미지뷰/1-2
    TextView  open_chatting_name_list;//오픈 채팅방 참여인원들 이름들 담긴 텍스트뷰/1-3
    TextView  open_chatting_student_count;//오픈 채팅방 참여 학생들  카운트/ 1-4
    TextView  open_chatting_recent_date;//오픈 채팅방 최근 메세지  온 날짜 /1-5
    TextView  open_chatting_recent_message;//오픈 채팅방  최근 메세지 /1-6
    TextView  open_chatting_unread_count;//오픈 채팅방  안읽은 메세지 숫자 /1-7

    //1대1 메신저
    RecyclerView onetoonerecyclerview;//1대1  채팅방 리스트  들어갈   리사이클러뷰 / 2-1
    ChattingRoomListRecyclerviewAdapter chattingRoomListRecyclerviewAdapter;//roomlist_recyclerview에  방목록을 뿌릴  리사이클러뷰 어뎁터 2-2
    RecyclerView.LayoutManager chatting_room_list_Recyclerview_manager;//리사이클러뷰 레이아웃 매니저 2-3

    ArrayList<JsonObject> room_list_data;//룸 리스트가 담길

    String teacheruid;//선생님 uid.

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.v("check", stringforthisfragmentname+" 의 onAttach() 실행");


    }//onAttach() 끝


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("check", stringforthisfragmentname+" 의 onCreate() 실행");


    }//onCreate()끝


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.v("check", stringforthisfragmentname+" 의 onCreateView() 실행");

        //프래그먼트 ->  해당  레이아웃 인플레이트
        ViewGroup rootView=(ViewGroup)inflater.inflate(R.layout.fragment_forchattingteacher,container,false);

        open_chatting_linearlayout=rootView.findViewById(R.id.open_chatting_room_layout);//1-1
        open_chatting_roomimage=rootView.findViewById(R.id.open_chatting_profile_image);//1-2
        open_chatting_name_list=rootView.findViewById(R.id.open_chatting_student_namelist);//1-3
        open_chatting_student_count=rootView.findViewById(R.id.open_chatting_student_number);//1-4
        open_chatting_recent_date=rootView.findViewById(R.id.open_chatting_recent_date);//1-5
        open_chatting_recent_message=rootView.findViewById(R.id.open_chatting_recent_message);//1-6
        onetoonerecyclerview=rootView.findViewById(R.id.one_to_one_message_recyclerview);//2-1
        open_chatting_unread_count=rootView.findViewById(R.id.open_chatting_unread_count);//1-7

        final SharedPreferences getid = getActivity().getSharedPreferences("loginteacherid",MODE_PRIVATE);//선생님 로그인 아이디  쉐어드에 담긴거 가져옴
        loginedid= getid.getString("loginidteacher","");//선생님 로그인 아이디 가져옴


        GlobalBus.getBus_data().register(this);//이벤트 버스 등록

         getTeacheruid(loginedid);


        //1-1  해당 방 리스트가 눌렸을때  ->  해당 선생님의 오픈 채팅 엑티비티로 넘어간다.
        open_chatting_linearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.v("check", loginedid+"의 openchatting방  목록  클릭됨 ->   해당 선생님  오픈 채팅방으로 가진다.");


                Intent  gotochattingroomactivity=new Intent(getActivity(),ChattingRoomActivityForTeacher.class);//채팅 방 엑티비티로 가는 인텐트
                gotochattingroomactivity.putExtra("RoomName", open_chatting_name_list.getText().toString());//채팅방 이름을  보내준다.
                gotochattingroomactivity.putExtra("chattingroomtype", 0);  //오픈 채팅  인지  1대일  채팅인지 여부를  채팅방으로 보내준다.   /1이면  일대일 채팅방,   0이면  오픈 채팅방

                startActivity(gotochattingroomactivity);//해당 엑티비티 실행.


            }//onClick
        });//클릭리스너 끝.




        return rootView;
    }//onCreateView 끝



    //오픈 채팅방
    private void getOpenChatting_data_for_roomlist(String teacheruid) {

        //오픈 채팅방  기존  채팅 데이터 가지고 오기 위한  코드
        String loginid_for_sql=loginedid.replaceAll("@", "");//데이터베이스 값은 -> 유저의 로그인 이메일 에서 @ 뺜것임.

        //기존 채팅방에서  저장해두었던  데이터들을  가지고와서  뿌려준다. -> 현재방 기준의  데이터들을  가져와서 보내준다
        SqLiteOpenHelperClass GetsqLite_chatting_Database = new SqLiteOpenHelperClass (getActivity(),loginid_for_sql , null, 1);
        SQLiteDatabase Get_saved_database = GetsqLite_chatting_Database.getReadableDatabase();//읽어들이는 형태로  데이터 베이스 접근함.

        String sql = "select * from chatting_data_store where roomnumber=?";//chatting_data_store테이블에서  해당  룸넘버에 들어있는 내용 조회한다.

        Cursor cursor=Get_saved_database.rawQuery(sql,new String []{teacheruid});///위 쿼리문  데이터베이스로 보냄

        if( cursor != null && cursor.moveToLast()){//커서가 null이 아니고 cursor의 가장  맨 마지막 값 즉 가지고 온 rawquery중에서 가장  마지막 값 인경우

            Log.v("check", "ChattingRoomListRecyclerview에서 최근 메세지 가지고옴" + cursor.getString(11));
            String recentmessage= cursor.getString(11);//가장 최근 메세지
            String recent_message_data=cursor.getString(9);

            //해당 데이터의 룸넘버와 현재  홀더가 보여주는 방의 룸넘버가  같다면
            if(cursor.getString(3).equals(teacheruid) && Integer.parseInt(cursor.getString(8))==3 ||cursor.getString(3).equals(teacheruid) && Integer.parseInt(cursor.getString(8))==4  ||cursor.getString(3).equals(teacheruid) && Integer.parseInt(cursor.getString(8))==5) {//가장 최근에 해당하는  룸넘버의 최근 메세지를 넣어줌.

                ///최근 메세지 칸 보이게 만듬
                open_chatting_recent_message.setVisibility(View.VISIBLE);

                if(cursor.getString(8).equals("3")){//일반 메세지타입

                    //채팅 뷰 홀더에서  최근 메세지->  넣어줌
                    open_chatting_recent_message.setText(recentmessage);

                }else if(cursor.getString(8).equals("4")){//사진 타입 메세지일 경우

                    //최근 메세지가  이미지 업로드 이므로, 이미지 path대신 pictureuploade라고  멘트날림.
                    open_chatting_recent_message.setText("Picture uploaded!");

                }else if(cursor.getString(8).equals("5")){//비디오 타입 메세지일 경우

                    //최근메세지에  Video uploaded넣음.
                   open_chatting_recent_message.setText("Video uploaded!");

               }





                try {
                    Calendar calendar_for_get_present_time=Calendar.getInstance();//현제 시간을 가지고 오기 위한  캘린더 변수
                    calendar_for_get_present_time.add(Calendar.DATE, 0);//캘린더의 현재 시간 가지고옴.
                    Date present_date=calendar_for_get_present_time.getTime();//date 객체로 바꿔줌.


                    //현재 포지션 다음  포지션의 채팅메세지의  날짜
                    Date  message_date = new SimpleDateFormat("yyyy-MM-dd hh:mm").parse(recent_message_data);
                    //다음 포지션 날짜 -> 시간 형태로
                    String message_time=new SimpleDateFormat("aaa hh:mm").format(message_date);


                    //아래  두변수를 가지고 년월일 을  비교해서 같은 년,월,일 이라면  시간만 표시하게 하고  다른 시간이라면,
                    //채팅방 리스트의 채팅메세지의  시간을 년,월,일로 표기하도록 한다.
                    //메세지의  시간 -> 년월 일만 가지고옴.
                    String message_date_for_compare=new  SimpleDateFormat("yyyy-MM-dd").format(message_date);

                    //현재 시간 -> 년 월 일만 가지고옴.
                    String present_date_for_compare=new SimpleDateFormat("yyyy-MM-dd").format(present_date);



                    if(message_date_for_compare.equals(present_date_for_compare)){//메세지의 저장된 날짜가  오늘과 같을때-> 시간을  표시해줌.


                        //룸 리스트 중에 최근 메세지 에대한 시간 포맷으로  넣어줌.
                        open_chatting_recent_date.setVisibility(View.VISIBLE);
                        open_chatting_recent_date.setText(message_time);

                    }else{//메세지 저장날짜와 오늘 날짜가 다를때  년, 월, 일로  표기 해준다.

                        //룸 리스트 중에 최근 메세지에  년,월, 일  형태로 넣어준다
                        open_chatting_recent_date.setVisibility(View.VISIBLE);
                        open_chatting_recent_date.setText(message_date_for_compare);//
                    }






                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }//해당 데이터의 룸넘버와 현재  홀더가 보여주는 방의 룸넘버가  같을 경우 끝


        }//커서가 null이 아니고 cursor의 가장  맨 마지막 값 즉 가지고 온 rawquery중에서 가장  마지막 값 인경우 끝

    }//getOpenChatting_data_for_roomlist() 끝


    //해당 룸넘버의  -> 안읽은 메세지  숫자를 가지고온다.
    public  void get_unread_count(String chatting_roomnumber,TextView unreadcount) {


        String sql_for_get_unread_count="select * from  chatting_data_store where roomnumber=? and readornot=1";
        Cursor cursor_for_get_unread_count;
        SQLiteDatabase  database;

        SharedPreferences getid_teacher = getActivity().getSharedPreferences("loginteacherid",MODE_PRIVATE);//로그인 아이디  쉐어드에 담긴거 가져옴
        String teacherloginedid= getid_teacher.getString("loginidteacher","");//로그인 아이디 가져옴

        SharedPreferences getid_student = getActivity().getSharedPreferences("loginstudentid", MODE_PRIVATE);//로그인 아이디  쉐어드에 담긴거 가져옴
        String studentloginedid = getid_student.getString("loginid", "");//로그인 아이디 가져옴


        if(teacherloginedid.equals("")){//선생님 로그인이 아닐때-> 학생 로그인

            Log.v("check", studentloginedid+"학생입니다 ");

            //학생 로그인 아이디에서  @빼줘서 데이터 베이스 -> 이름  가지고옴.
            String loginid_for_sql=studentloginedid.replaceAll("@", "");
            SQLiteOpenHelper sqLiteOpenHelper=new SqLiteOpenHelperClass(getActivity(),loginid_for_sql , null,1 );//sqlite 클래스
            database=sqLiteOpenHelper.getReadableDatabase();//데이터베이스 -> sqlite클래스-> 읽기로 접근


        }else{//학생 로그인 아닐때 선생님 로그인-> 선생님 로그인

            Log.v("check", teacherloginedid+"선생님입니다. ");

            String loginid_for_sql=teacherloginedid.replaceAll("@", "");
            SQLiteOpenHelper sqLiteOpenHelper=new SqLiteOpenHelperClass(getActivity(),loginid_for_sql , null,1 );
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

    //서비스에서 백그라운드로 받은  채팅 데이터를 이벤트버스로 보냈고,  그걸 받기위한  메소드이다.
    @Subscribe
    public  void getMessage(JSONObject receivedata){

        if(receivedata.has("onetoonefirst_messge")){
            Log.v("dddddddd", "onetoonefirst_messge가 실행됨0->"+room_list_data);



            JsonParser jsonParser = new JsonParser();
            JsonObject gsonObject = null;
            try {
                gsonObject = (JsonObject)jsonParser.parse(receivedata.getJSONObject("onetoonefirst_messge").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(!room_list_data.contains(gsonObject)) {

                room_list_data.add(gsonObject);


                Log.v("dddddddd", "onetoonefirst_messge가 실행됨1->" + room_list_data);

                Log.v("dddddddd", "onetoonefirst_messge가 실행됨2->" + room_list_data);


                //어레이 리스트에 해당 방 리스트 담아줌 -> 포지션 1일때- 일대일 채팅
                ArrayList<JsonObject> jsonObjects1 = new ArrayList<>();


                //해당 유저가 속해있는 방리스트 중->  namespace 1-> 1대1 채팅방을  가져오기 위한  포문
                for (int i = 0; i < room_list_data.size(); i++) {

                    //해당 방 namespace 가져오기
                    String a = room_list_data.get(i).get("roomnamespace").toString().replaceAll("\"", "");

                    //namespace 1인경우 (1대1 채팅방인 경우)->  jsonObject1에다가  넣어줌.
                    if (a.equals("1")) {//for문 돌린  방 리스트 중  namespace 1=> 일대일 채팅방에 해당하는 룸 리스트  어레이에  넣어줌.
                        jsonObjects1.add(room_list_data.get(i));
                    }

                }//for문 끝

//            chattingRoomListRecyclerviewAdapter.notifyDataSetChanged();
//            chattingRoomListRecyclerviewAdapter.notifyItemInserted(0);
                //리사이클러뷰 어뎁터 처리및 레이아웃 매니징 처리
                chattingRoomListRecyclerviewAdapter = new ChattingRoomListRecyclerviewAdapter(jsonObjects1, getActivity(), "t", "");//1-2
                ((SimpleItemAnimator) onetoonerecyclerview.getItemAnimator()).setSupportsChangeAnimations(false);
                onetoonerecyclerview.setAdapter(chattingRoomListRecyclerviewAdapter);
                chatting_room_list_Recyclerview_manager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
                ((LinearLayoutManager) chatting_room_list_Recyclerview_manager).setReverseLayout(false);
                ((LinearLayoutManager) chatting_room_list_Recyclerview_manager).setStackFromEnd(false);
                onetoonerecyclerview.setLayoutManager(chatting_room_list_Recyclerview_manager);
                onetoonerecyclerview.setNestedScrollingEnabled(false);

                Log.v("dddddddd", "onetoonefirst_messge가 실행됨3->" + room_list_data);

            }
            return;
        }



        Log.v("checkeventbusget", stringforthisfragmentname+"에서 eventbus받음"+String.valueOf(receivedata));

        try {


            //해당 룸과 같을때, 선생님의 경우는 ->  오픈 채팅방이 따로  되어있어서  여기 프래그먼트에서 처리해주고 -> 그리고  선생님 uid가   오픈 채팅방  번호이므로 다음과 같이 코드를 구성함.
            if(receivedata.has("roomnumber")&&receivedata.getString("roomnumber").equals(teacheruid)) {

                Calendar calendar_for_get_present_time=Calendar.getInstance();//현제 시간을 가지고 오기 위한  캘린더 변수
                calendar_for_get_present_time.add(Calendar.DATE, 0);//캘린더의 현재 시간 가지고옴.
                Date present_date=calendar_for_get_present_time.getTime();//date 객체로 바꿔줌.


                //현재 포지션 다음  포지션의 채팅메세지의  날짜
                Date message_date=new SimpleDateFormat("yyyy-MM-dd hh:mm").parse(receivedata.get("date").toString());

                //다음 포지션 날짜 -> 시간 형태로
                String message_time=new SimpleDateFormat("aaa hh:mm").format(message_date);



                //아래  두변수를 가지고 년월일 을  비교해서 같은 년,월,일 이라면  시간만 표시하게 하고  다른 시간이라면,
                //채팅방 리스트의 채팅메세지의  시간을 년,월,일로 표기하도록 한다.
                //메세지의  시간 -> 년월 일만 가지고옴.
                String message_date_for_compare=new  SimpleDateFormat("yyyy-MM-dd").format(message_date);

                //현재 시간 -> 년 월 일만 가지고옴.
                String present_date_for_compare=new SimpleDateFormat("yyyy-MM-dd").format(present_date);


                if(message_date_for_compare.equals(present_date_for_compare)){//메세지의 저장된 날짜가  오늘과 같을때-> 시간을  표시해줌.

                    //룸 리스트 중에 최근 메세지 에대한 시간 포맷으로  넣어줌.
                    open_chatting_recent_date.setVisibility(View.VISIBLE);
                    open_chatting_recent_date.setText(message_time);//

                }else{//메세지 저장날짜와 오늘 날짜가 다를때  년, 월, 일로  표기 해준다.

                    //룸 리스트 중에 최근 메세지에  년,월, 일  형태로 넣어준다
                    open_chatting_recent_date.setVisibility(View.VISIBLE);
                    open_chatting_recent_date.setText(message_date_for_compare);//
                }


                if(receivedata.getString("viewtype").equals("3")){//일반 메세지타입
                    //오픈 채팅방  최근 메세지가 보이도록한다.
                    //그리고-> 최근 메세지  텍스트뷰에  ->  방금 서비스로 받은 채팅 내용을 넣어준다 (소켓이 계속 연결되었을시 가장 최근 메세지 이므로,)
                    open_chatting_recent_message.setVisibility(View.VISIBLE);
                    open_chatting_recent_message.setText(receivedata.getString("message"));

                }else if(receivedata.getString("viewtype").equals("4")){//사진 타입 메세지일 경우

                    //채팅 뷰 홀더에서  최근 메세지->  넣어줌
                    open_chatting_recent_message.setVisibility(View.VISIBLE);
                    open_chatting_recent_message.setText("Picture uploaded!");
                }







                //오픈 채팅방 안읽은 메세지 카운트 보여주는  메소드
                get_unread_count(teacheruid, open_chatting_unread_count);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }//이벤트 getMessage() 끝

    @Override
    public void onResume() {
        super.onResume();
        Log.v("check", stringforthisfragmentname+" 의  onResume() 실행됨.");

        teacherroomlist(loginedid);//해당 선생님의 uid 가  속해 있는  방의 정보들을 가지고 오는 메소드 -> 파라미터로  선생님 이메일 넣어줌.

        if(teacheruid != null) {//선생님 uid가 null이 아닐때

            //채팅방을 들어갔다 나와서 채팅방 리스트 프래그먼트로 왔을때 -> 오픈 채팅방을 업데이트 시켜준다. ->
            //학생의 경우는 오픈 채팅방,  일대일 채팅방 모두  리사이클러뷰에 들어있어서 -> 한번에  notifychande로  업데이트 가능하지만,
            //선생님 프래그먼트의 경우는 ->  오픈 채팅방이 리사이클러뷰 없는 텍스트뷰로 되어있으므로,  이렇게 새로  값을 가지고와서 textview에  넣어준다.
            getOpenChatting_data_for_roomlist(teacheruid);

            //해당 채팅방에서 -> 안읽은 메세지  숫자 카운트해서 넣어주는 메소드
            get_unread_count(teacheruid,open_chatting_unread_count);

        }

        if(chattingRoomListRecyclerviewAdapter != null){//리사이클러뷰 어뎁터가 null이 아닐경우 -> 즉  처음  작동하고 후예 null값이 아닐때 ->  채팅방에서 돌아와 -> notifydatachaned를 사용해서 ->  리스트 업데이트 가능

            chattingRoomListRecyclerviewAdapter.notifyDataSetChanged();//null이 아니면  혹시나 바뀐  값을 위해 리스트 업데이트 해준다.
        }


    }//onResume 끝


    @Override
    public void onPause() {
        super.onPause();
        Log.v("check", stringforthisfragmentname+" 의  onPause() 실행됨.");


    }//onPause 끝

    /*해당선생님이 속해있는 방  정보들을  가지고 오는 메소드*/
    private void teacherroomlist(String teacheremail){

        Log.v("check", "선생님 채팅방 정보들 가져오는 메소드 실행됨 teacherroomlist()");

        Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create())//json으로 받고 싶으면 쓰면안됨..
                .build();//리트로핏 뷸딩
        ApiService apiService = retrofit.create(ApiService.class);//api인터페이스 크리에이트
        RequestBody teacheremail_to_send =RequestBody.create(MediaType.parse("text/plain"), teacheremail);//서버로 보낼 선생님  이메일  메개변수로 받아와서  request변수로 연결
        Call<ResponseBody> getroominfo= apiService.getchattingroomlistinfo(teacheremail_to_send);//서버로  콜 보내고  결과 받아올 함수.


        //서버로  콜 보냄.
        getroominfo.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

              try {



                  if (response.body() != null) {

                      if(response.body().toString().equals("0")){//0 값-> room정보  json화 하지 못함.

                        Log.v("check", "방정보 가져오기 0가져옴 ->  select 쿼리에서  문제가 생김");

                    }else {//룸  정보  성공적으로 가져옴.


                        //여기서 getteacherroomlist.php코드를 보면, 룸  정보 전체를 가지고 오는 것이 아닌 room 테이블에서  teacheremeail
                        //컬럼이 실제 선생님  이메일과  같을 때만  가능하므로,  오픈 채팅방  정보를 가지고 온디.
                        roominfo = new JSONObject(response.body().string());//룸 정보  json객체로 받음
                        Log.v("check-> 방정보??", String.valueOf(roominfo));


                        if (!roominfo.get("roomimage").toString().equals("")) {//roomimage가 있을경우

                            URL url = new URL("http://13.209.249.1/" + roominfo.get("roomimage"));//해당 이미지 url 받아옴
                            Glide.with(getActivity()).load(url)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)//디스크 캐시 저장도 꺼줌. -> glide는  빠르게 이미지 로딩을 위해 url이 같으면, 캐시에 저장된 똑같은 이미지 그대로 넣어줌.
                                    .skipMemoryCache(true)//캐시 저장 안함.  -> 이러니까 계속  서버 이미지 경로  같은데 내용물 바껴도  계속  예전꺼 나옴  그래서 캐쉬 없애줌.
                                    .into(open_chatting_roomimage);
                          }

                         //오픈채팅방이름은  -> 선생님의 경우는 나의 오픈 채팅방으로 써줌.
                         open_chatting_name_list.setText("My Open Chatting Room");

                         //clientcount -> String으로 왔으므로,   int로 변환시켜줌.
                         int clientcount = Integer.parseInt(roominfo.get("clientcount").toString());

                         //clientcount가  -> -1을 할경우 0이면  현재  선생만  방 참여멤버이므로,  카운트를 안보여줘도됨.
                         if ((clientcount - 1) == 0) {
                            open_chatting_student_count.setText(null);

                         }else {// 방 채팅 참여자가 0 이상일 경우.

                            open_chatting_student_count.setText(roominfo.get("clientcount").toString());
                         }//방채팅 참여자가 0 이상일 경우이다.

                      }//room정보  성공적으로 가지고 왔을때 경우.
                  }//responsebody null값이 아닐때.

              } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


              }//onResponse() 끝

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
               Log.v("check", "룸정보 가져오는것  실패 -> "+t);

            }//onFailure끝

        });//failure끝
    }//teacherroomlist끝



    //chatuserconinfo에서  해당 유저가  참여한  1대1 채팅방들의 모든 리스트를 가지고 온다.
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

                    room_list_data=response.body().getRoomlist_data_for_user();
                    Log.v("check", stringforthisfragmentname+"의  해당 유저 속한 방 리스트 가져오기 실행 결과 -> "+room_list_data);

                    //어레이 리스트에 해당 방 리스트 담아줌 -> 포지션 1일때- 일대일 채팅
                    ArrayList<JsonObject> jsonObjects1 =new ArrayList<>();

                    //해당 유저가 속해있는 방리스트 중->  namespace 1-> 1대1 채팅방을  가져오기 위한  포문
                    for(int i=0; i<room_list_data.size(); i++){

                        //해당 방 namespace 가져오기
                        String a=room_list_data.get(i).get("roomnamespace").toString().replaceAll("\"", "");

                        //namespace 1인경우 (1대1 채팅방인 경우)->  jsonObject1에다가  넣어줌.
                        if(a.equals("1")){//for문 돌린  방 리스트 중  namespace 1=> 일대일 채팅방에 해당하는 룸 리스트  어레이에  넣어줌.
                            jsonObjects1.add(room_list_data.get(i));
                        }

                    }//for문 끝


                    //리사이클러뷰 어뎁터 처리및 레이아웃 매니징 처리
                    chattingRoomListRecyclerviewAdapter=new ChattingRoomListRecyclerviewAdapter(jsonObjects1,getActivity(),"t",useremail);//1-2
                    ((SimpleItemAnimator) onetoonerecyclerview.getItemAnimator()).setSupportsChangeAnimations(false);
                    onetoonerecyclerview.setAdapter(chattingRoomListRecyclerviewAdapter);
                    chatting_room_list_Recyclerview_manager=new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false);
                    ((LinearLayoutManager) chatting_room_list_Recyclerview_manager).setReverseLayout(false);
                    ((LinearLayoutManager) chatting_room_list_Recyclerview_manager).setStackFromEnd(false);
                    onetoonerecyclerview.setLayoutManager(chatting_room_list_Recyclerview_manager);
                    onetoonerecyclerview.setNestedScrollingEnabled(false);


                }//response 값 null이 아닐때.

            }//onResponse끝

            @Override
            public void onFailure(Call<GetRoomList> call, Throwable t) {
                Log.v("check", stringforthisfragmentname+"의  해당 유저 속한 방 리스트 가져오기 실행중 에러뜸  에러내용-> "+t.getMessage());


            }//onFailure 끝
        });//callback 실행 끝.
    }//getroomlist_from_chatuserconinfo() 끝끝


    //선생님 uid를  가지고  오기위해  선생님 이메일을 보낸다.
    private void getTeacheruid(String teacheremail){

        Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create())
                .build();//리트로핏 뷸딩
        ApiService apiService = retrofit.create(ApiService.class);//api인터페이스 크리에이트
        Call<teacherinforesult> getteacherinfo=apiService.sendemailtogetteacherprofile(teacheremail);//선생님 정보 얻기위한  call객체

        getteacherinfo.enqueue(new Callback<teacherinforesult>() {
            @Override
            public void onResponse(Call<teacherinforesult> call, Response<teacherinforesult> response) {

                teacheruid=response.body().getTeacheruid();

                //오픈채팅방 - 최근 메세지랑 - 데이트 받아오기 위한 메소드
                getOpenChatting_data_for_roomlist(teacheruid);

                //해당 채팅방에서 -> 안읽은 메세지  숫자 카운트해서 넣어주는 메소드
                get_unread_count(teacheruid,open_chatting_unread_count);

                //참여한  일대1 채팅방 모두가져오기 위한 메소드
                getroomlist_from_chatuserconinfo(teacheruid, "");

            }

            @Override
            public void onFailure(Call<teacherinforesult> call, Throwable t) {

            }
        });


    }//getTeacheruid() 끝


    @Override
    public void onDetach() {
        super.onDetach();
        GlobalBus.getBus_data().unregister(this);//이벤트 버스 등록
    }
}//fragmentforteacherchatting class끝
