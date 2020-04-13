package com.example.leedonghun.speakenglish;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * speakenglish
 * Class: ChattingDataRecyclerViewAdapter.
 * Created by leedonghun.
 * Created On 2019-11-04.
 * Description:채팅내용을  뿌려줄  리사클러뷰에서 해당  ui작업을  뿌려줄  리사이클러뷰 adapter이다.
 */
public class ChattingDataRecyclerViewAdapter extends RecyclerView.Adapter {

    String adapternameforlog="ChattingDataRecyvlerViewAdapter";//로그용 현재 어뎁터 이름.

    private LayoutInflater layoutInflater;//채팅 내용이 담길  커스텀뷰 인플레이터 하기위한 인플레이터

    private ArrayList<JSONObject> chattingtext;//어뎁터에서 받아서 뿌려질  채팅 데이터들
    private Context context;//context
    private String useruid;//본인 아이디인지 아닌지를 판별하여 채팅 ui를  다르게 해야하므로  필요.
    private String userposition;//채팅 메세지를 보낸 사람의 직업 - s or t

    private Activity activity;


    private JSONArray jsonArray1;
//    Map<Integer, String> cargo;

    //채팅용 리사이클러 뷰 어뎁터 생성자
    ChattingDataRecyclerViewAdapter(ArrayList<JSONObject>text,Context context,String userid,String userpositioncheck,Activity activity){

        this.context=context;//외부 context연결시켜줌.
        this.chattingtext=text;//받아온  채팅데이터 연결시켜줌.
        this.useruid=userid;//유저 uid 받아옴,
        this.userposition=userpositioncheck;//유저의 직업 데이터 받아옴.
        this.jsonArray1=new JSONArray();

        this.activity=activity;
        Log.v("check",adapternameforlog+"의 생성자 실행됨.-> 받은 문자 내용->"+chattingtext+", 유저 uid- > "+useruid+", 유저의 포지션->"+userposition);

    }

    //메세지별  타입 구별
    @Override
    public int getItemViewType(int position) {

          Log.v("check", "gettypa비교");

        String message_sender_id = "";//메세지 보낸 사람 uid
        String message_viewtype = "";//메세지  뷰타입.

        try {
            message_sender_id=chattingtext.get(position).get("id").toString();
            message_viewtype=chattingtext.get(position).get("viewtype").toString();


        } catch (JSONException e) {
            e.printStackTrace();
        }

         if(message_viewtype.equals("3")){
             Log.v("check", adapternameforlog+"의 message vietypw =3일때");

             //메세지가  본인이 보낸거일떄
             if(message_sender_id.equals(useruid)){


                 Log.v("checkgetitemviewtype", "1체크됨.");
                 return 1;//16-2


             }else{//메세지 본인이 보낸게 아닐때,

                 Log.v("checkgetitemviewtype", "0체크됨.");
                 return 0;//16-1

             }

         }else if(message_viewtype.equals("1")){//채팅메세지가 아닌  vietype들  1-> 처음들어온 사람-내가 아닌 다른 사람.
             Log.v("check", adapternameforlog+"의 message vietypw =1일때");


             return 2;//16-3

         }else if(message_viewtype.equals("2")){//2->  방을 나간사람.
             Log.v("check", adapternameforlog+"의 message vietypw =2일때");



             return 3;//16-4

         }else if(message_viewtype.equals("0")){//내가 처음 들어왔을때  viewtype
             Log.v("check", adapternameforlog+"의 message vietypw =0일때");


             return 4;

         }else if(message_viewtype.equals("4")){//이미지 메세지가  들어왔을때,
             Log.v("check", adapternameforlog+"의 message vietypw =4일때");


             //해당 이미지 메세지가  본인이 보낸거일떄
             if(message_sender_id.equals(useruid)){


                 return 8;//17-1 -> 본인이 보낸거

             }else {//해당 이미지 메세지가 남이 보낸거일때


                return 9;//17-2  -> 남이  보낸거
             }

         }else if(message_viewtype.equals("5")) {//비디오 메세지가 들어왔을때

             Log.v("check", adapternameforlog+"의 message vietypw =5일때");

             //비디오 메세지를 내가 보냈을때
             if(message_sender_id.equals(useruid)){


                 return 10;//8-1 -> 비디오 메세지 본인이 보냄.

             }else {//해당 비디오 메세지를 남이 보냈을때


                 return 11;//8-2 -> 비디오 메세지 남이 보냄.

             }//비디오 메세지 남이 보냈을때 끝.

        }else{//어떤 viewtype에도 해당이 안될때



             return -1;//어떤 뷰타입에도 해당이 안될때
         }

    }//getviewtype 끝




    //viewholder 내용 인플레이트 하기.
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewtype) {
        Log.v("check", "ChattingDataRecyclerViewAdapter 의  onCreateViewHolder 실행");

        Context context=parent.getContext();//context
        View view;

        if(viewtype==0) {//getitemViewtype에서 return한 결과가 0일 경우->  //16-1- 내가 보낸게 아닐때

            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.sendchattinglayout, parent, false);
            Log.v("checkoncreateviewholder", "viewtype에서 0이됨.");


            return new chatrecyclerviewadapterviewholder(view);


        }else if(viewtype==1){//getitemviewtype 리턴 결과  1인 경우 //16-2 // 내가 보냈을 경우

            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.receivechattinglayout, parent, false);
            Log.v("checkoncreateviewholder", "viewtype에서 1이됨.");


            return new mycahttingviewholder(view);


        }else if(viewtype==2){//getitemtype 리턴 결과 2인경우 - //16-3- 상대가 처음 들어온 경우

            layoutInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=layoutInflater.inflate(R.layout.in_and_out__layout_from_chatting, parent,false);

            return new in_and_out_viewholder(view);


        }else if(viewtype==3){//getitemtype리턴 결과 3인경우 - //16-4 - 나간경우

            layoutInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=layoutInflater.inflate(R.layout.in_and_out__layout_from_chatting, parent,false);

            return new in_and_out_viewholder(view);

        }else if(viewtype==4){//getitemtype리턴 결과 4인경우 - //16-5 - 내가 처음 들어온 경우

            layoutInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=layoutInflater.inflate(R.layout.in_and_out__layout_from_chatting, parent,false);

            return new in_and_out_viewholder(view);

        }else if(viewtype==8){//getitemtype리턴 결과 8인경우-  17-1  내가 이미지를 보낸 경우

            layoutInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=layoutInflater.inflate(R.layout.receiveimage_chatting, parent,false);


            return new mychatting_image_viewholder(view);

        }else if(viewtype==9){//getitemtype리턴 결과 9인경우-  17-2  상대가 이미지를 보낸 경우

            layoutInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=layoutInflater.inflate(R.layout.sendimage_chatting, parent,false);

            return new chatting_image_viewholder(view);

        }else if(viewtype==10){//내가 비디오를 보냈을 경우  18-1

            layoutInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=layoutInflater.inflate(R.layout.receieve_chatting_video, parent,false);

            return  new mychatting_video_viewholder(view);


        }else if(viewtype==11){//상대가 비디오를 보냈을 경우   18-2

            layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=layoutInflater.inflate(R.layout.send_video_chatting, parent,false);

            return new chatting_video_viewholder(view);
        }


        return null;
    }//onCreateViewholder() 끝




    //

    //서버에서  savechattingdata 부분에서 ->  해당  메세지의  unreadcount를  조회해서 가지고 오는 역할을 진행한다.
    //이 메소드 안에서  조회한  unreadcount를  unreadcount가 들어가는 텍스트뷰에 넣어준다.
    public void get_server_chatting_read_count(String roomnumber, final int chatorder, final TextView unreadcount_text){

       Log.v("check", adapternameforlog+"에서 받은  해당 체팅의  방번호: "+roomnumber+"  채팅순서번호: "+chatorder);


        Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create())//json으로 받고 싶으면 쓰면안됨..
                .build();//리트로핏 뷸딩
        ApiService apiService = retrofit.create(ApiService.class);//api인터페이스 크리에이트

        RequestBody chatting_roomnumber=RequestBody.create(MediaType.parse("text/plain"), roomnumber);//서버로 채팅방 번호
        RequestBody chatting_order=RequestBody.create(MediaType.parse("text/plain"), String.valueOf(chatorder));//서버로 보낼 채팅순서 .


        //서버로  해당  방  해당 chatorderㅇ  unreadcount를 가지고 오도록 call함.
        Call<ResponseBody>get_chatting_unread_count=apiService.get_unread_count_for_chatting_message(chatting_roomnumber, chatting_order);

        //d위  call에 대한  callback
        get_chatting_unread_count.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {


                       String unread_count=response.body().string();
                       int unread_cout_cast_int= Integer.parseInt(unread_count);
                            if (unread_cout_cast_int<=0) {//unreadcount가 - 0일때이다.

                                unreadcount_text.setText("");

                            }else if(unread_cout_cast_int>0){

                                //서버로 부터 받은 읽지 않은 숫자 ->  해당 텍스트뷰에 넣어줌.
                                unreadcount_text.setText(unread_count);
                            }




                } catch (IOException e) {
                    e.printStackTrace();
                }


            }//onResponse() 끝

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {



            }//onFailure() 끝
        });


    }//get_server_chatting_read_count끝




    //리사이클러뷰에 아이템  포지션별  맞는 데이터 이기
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        MediaController controller = new MediaController(context);
        try{

        String chatcontent;//현재 포지션 메세지 보낸 유저의  메세지 내용.
        String imagepath;//현재 포지션  메세지 보낸 유저의  이미지 프로필 경로
        String username;//현재 포지션 메세지 보낸 유저의  이름.
        Date date;//현재 포지션  채팅메세지 날짜시간
        String time;//현재 포지션  채팅메세지  시간

        String pastid;//이전 포지션에서  메세지를 보낸  유저의  uid
        String senderuid;//현재 포지션 보낸사람 uid

        String user_job_position;//현재 유저의  직업 포지션을  넣기 위한  스트링 변수.

        chatcontent = chattingtext.get(position).get("message").toString();//현재 포지션의  채팅 메세지
        username= chattingtext.get(position).get("name").toString();//현재 포지션의  유저 이름.
        imagepath=chattingtext.get(position).get("profile").toString();//현재 포지션의 이미지 경로


        user_job_position=chattingtext.get(position).get("userposition").toString();// 현재 포지션의  메세지를 보낸 유저의  직업. 학생 :s, 선생:t


        //현재 포지션  서버 시간으로 받아서  date로 넣어줌.
        date = new SimpleDateFormat("YYYY-MM-DD HH:mm").parse(chattingtext.get(position).get("date").toString());

        //현재 포지션  시간  time형태로 바꿈,
        time = new SimpleDateFormat("aaa hh:mm").format(date);


        //URL -> 이미지 프로필 URL받아오기
        URL url = new URL("http://13.209.249.1/" + imagepath);



        //아이템 뷰타입이 0일때 상대가 보냈을때
        if(viewHolder.getItemViewType()==0){
            Log.v("check", "ChattingDataRecyclerViewAdapter 의   viewHolder.getItemType==0 일때 실행됨-> 상대가 보낸 메세지타입");

            if(user_job_position.equals("t")){

                username=username+" teacher";
                ((chatrecyclerviewadapterviewholder) viewHolder).textviewforusername.setTextColor(Color.GREEN);

            }else if(user_job_position.equals("s")){

                ((chatrecyclerviewadapterviewholder) viewHolder).textviewforusername.setTextColor(Color.BLACK);
            }


            int i=0;//해당 타입의 메세지 (남이 보낸 메세지 타입)가  연속적으로  실행되는지를 체크 하기 위한  값
            if(position>0){// 포지션이  0초과 일때-> 즉  리사이클러뷰  아이템이  2개 이상 일때이다. 이전 데이터가  있으므로,  같은 사람이  반복적으로  채팅을 보낼때  gui처리를 할수 있다


                //현재 포지션 이전 포지션의 날짜.
                Date pastdate = new SimpleDateFormat("YYYY-MM-DD HH:mm").parse(chattingtext.get(position - 1).get("date").toString());

                //위  이전 포지션 날짜  time으로  변형 시켜줌.
                String pasttime = new SimpleDateFormat("aaa hh:mm").format(pastdate);

                //현재 포지션  이전 포지션의  채팅메세지  보낸 유저의  uid
                pastid=chattingtext.get(position-1).get("id").toString();

                //현재 포지션에서  sender 의 uid
                senderuid=chattingtext.get(position).get("id").toString();


                //이전 포지션의 viewtype
               String pastviewtype=chattingtext.get(position-1).get("viewtype").toString();

                //현재 포지션 이전 텍스트의  시간과 현재시간이  같고, 포지션 이전 아이디와  해당 유저의 아이디가 같을때이다.+ 그리고 viewtype이 3일 경우만이다.
                if((pasttime.equals(time) && pastid.equals(senderuid) && pastviewtype.equals("3")) || (pasttime.equals(time) && pastid.equals(senderuid) && pastviewtype.equals("4")) || (pasttime.equals(time) && pastid.equals(senderuid) && pastviewtype.equals("5"))){

                    i=i+1;//이전 포지션의 채팅메세지의 시간과  유저가 현재 유저 본인과  같을 경우-> i를  1씩 계속 올려줘서 -> 계속 같은 유저가  연속으로  채팅을 보내는건지 확인한다.


                    if(i>0) {//i 값이  0초과  ->  같은 유저의 채팅이 연속으로 진행될때이다.

                        //이경우 -> 이전  채팅메세지 유저와 같은 유저가  연속적으로 메세지를 보낸것이므로,  normal ui형태로 메세지 ui를 바꿔준다.
                        Log.v("check", "채팅 순서 체크 : 아이템은 2개 이상인상태이고,  이전 포지션 채팅시간과 현재 포지션 채팅시간이 맞고, i체크가 0이상 일때 즉 -> 노말 채팅ui나옴");


                        //채팅이 지속될때 나오는 normal 채팅내용 textview
                        ((chatrecyclerviewadapterviewholder) viewHolder).textviewfornormalchat.setVisibility(View.VISIBLE);//연속 진행이므로,  normal 채팅ui는  보임.
                        ((chatrecyclerviewadapterviewholder) viewHolder).textviewfornormalchat.setText(chatcontent);//연속이므로  normal에  채팅내용을 담아줌..


                        //맨처음 채팅때 나오는  채팅내용 textview
                        ((chatrecyclerviewadapterviewholder) viewHolder).textViewforshowchat.setVisibility(View.GONE);//연속이므로 맨처음 채팅 gui는  gone으로 보내줌.
                        ((chatrecyclerviewadapterviewholder) viewHolder).textViewforshowchat.setText("");//해당 포지션  채팅 내용은 아무것도 안담김.

                        //보낸 사람 프로필 이미지
                        ((chatrecyclerviewadapterviewholder) viewHolder).imageViewforuserprofile.setVisibility(View.GONE);//처음 채팅이므로  이미지뷰도 GONE

                        //보낸사람  이름 담기는 텍스트뷰
                        ((chatrecyclerviewadapterviewholder) viewHolder).textviewforusername.setVisibility(View.GONE);//처음 채팅이 아니므로  보낸 사람 이름 GONE
                        ((chatrecyclerviewadapterviewholder) viewHolder).textviewforusername.setText("");//채팅 이름도 안넣어줘도됨..


                        //i가  0이상인 경우에서 -> 리사이클러뷰 아이템수 -1 이  현재 포지션 보다 클때이다.
                        // -> 이거는  현재 포지션 다음 포지션이 존재한가 아닌가의 여부를 알기 위해서이다
                        //결국  시간이랑  안읽은 사람 수 보여줄지 아닐지 판별용임.
                        if(getItemCount()-1>position){

                            //현재 포지션 다음  포지션의 채팅메세지의  날짜
                            Date futuredate=new  SimpleDateFormat("YYYY-MM-DD HH:mm").parse(chattingtext.get(position + 1).get("date").toString());

                            //다음 포지션 날짜 -> 시간 형태로
                            String futuretime=new SimpleDateFormat("aaa hh:mm").format(futuredate);

                            //다음 포지션  채팅 메세지  보낸 유저의 uid
                            String futureid=chattingtext.get(position+1).get("id").toString();

                            if(futuretime.equals(time)){//포지션 1앞 시간과  현재 포지션 시간이 같을때이다.

                                if(futureid.equals(senderuid)){//포지션 1앞과  현재 포지션 시간이 같고,  uid가  두포지션 모두  똑같을 때이다.

                                     //채팅 본사람 숫자 텍스트뷰
                                     //cargo.get(chatorder);
                                    ((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount.setText("");//일단  1로 해놓음.-> 나중에 처리해야됨.
                                    ((chatrecyclerviewadapterviewholder) viewHolder).chatpresenttime.setText("");//처음 채팅이므로  채팅 보내진 시간도 보내준다.//여기에다가 -> 본사람 숫자 넣어줌.
                                    ((chatrecyclerviewadapterviewholder) viewHolder).chatpresenttime.setTextColor(Color.YELLOW);
                                    ((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount.setTextColor(Color.BLACK);

                                   // 해당  ->  채팅의 유저 읽은수  넣어주기.
                                   get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatrecyclerviewadapterviewholder) viewHolder).chatpresenttime);

                                }else{//두 포지션 시간이 같고  uid가 틀릴때 즉  다음 메세지가  상대 메세지 일때는  현재 포지션에서  시간을 출려해야한다.

                                    ((chatrecyclerviewadapterviewholder) viewHolder).chatpresenttime.setText(time);//현재 포지션 시간 출력
                                    ((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount.setText("");//현재 포지션 채팅 메세지  안읽은 유저의 숫자  보여주기.
                                    ((chatrecyclerviewadapterviewholder) viewHolder).chatpresenttime.setTextColor(Color.BLACK);
                                    ((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                                    //해당  ->  채팅의 유저 읽은수  넣어주기.
                                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount);

                                }

                            }else{//두 포지션  시간이 다를때이다.

                                ((chatrecyclerviewadapterviewholder) viewHolder).chatpresenttime.setText(time);//다음 메세지 시간이 다르면  현재 퐂션에서 무조건 시간을 출력해야한다.
                                ((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount.setText("");
                                ((chatrecyclerviewadapterviewholder) viewHolder).chatpresenttime.setTextColor(Color.BLACK);
                                ((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                                //해당  ->  채팅의 유저 읽은수  넣어주기.
                                get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount);

                            }

                        }else{//다음 포지션이  존재 하지 않는경우이다.-> 이경우에는 마지막 아이템이므로  시간이 출력된다.

                            ((chatrecyclerviewadapterviewholder) viewHolder).chatpresenttime.setText(time);
                            ((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount.setText("");
                            ((chatrecyclerviewadapterviewholder) viewHolder).chatpresenttime.setTextColor(Color.BLACK);
                            ((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                            //해당  ->  채팅의 유저 읽은수  넣어주기.
                            get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount);

                        }//if(getItemCount()-1>position){ 끝

                    }else{//이전 포지션과 같은 시간에서  i 값이  0초과가 아닐때  -> 이때는  같은 유저으 채팅이 연속적으로 진행되지 않는다.
                        Log.v("check", "채팅 순서 체크 : 아이템이  2개 이상인 경우,  과거시간과 현재시간이 맞지만, i체크가 0일때 즉 -> 맨처음다시 리셋");

                        //채팅이 지속될때 나오는 normal 채팅내용 textview
                        ((chatrecyclerviewadapterviewholder) viewHolder).textviewfornormalchat.setVisibility(View.GONE);//리셋이므로 normalchat 다시 사라짐.
                        ((chatrecyclerviewadapterviewholder) viewHolder).textviewfornormalchat.setText("");//연속이므로  normal에  채팅내용을 담아줌..

                        //맨처음 채팅때 나오는  채팅내용 textview
                        ((chatrecyclerviewadapterviewholder) viewHolder).textViewforshowchat.setVisibility(View.VISIBLE);//리셋이므로 다시 본 채팅ui나옴,
                        ((chatrecyclerviewadapterviewholder) viewHolder).textViewforshowchat.setText(chatcontent);//리셋이니까  본 채팅 ui에  채팅 내용 넣어줌.

                        //보낸 사람 프로필 이미지
                        ((chatrecyclerviewadapterviewholder) viewHolder).imageViewforuserprofile.setVisibility(View.VISIBLE);//리셋이니까 보낸사람 프로필 사진도 나옴.

                        //프로필 이미지 path가 null아 아닐경우
                        if(!imagepath.equals("null")) {
                            Glide.with(context).load(url).into(((chatrecyclerviewadapterviewholder) viewHolder).imageViewforuserprofile);//해당 프로필 이미지 서버에서 받아와  넣어줌.
                        }else{
                            ((chatrecyclerviewadapterviewholder) viewHolder).imageViewforuserprofile.setImageResource(R.mipmap.ic_launcher);
                        }

                        //보낸사람  이름 담기는 텍스트뷰
                        ((chatrecyclerviewadapterviewholder) viewHolder).textviewforusername.setVisibility(View.VISIBLE);//리셋이니까 이름 텍스트뷰도 나와야됨.
                        ((chatrecyclerviewadapterviewholder) viewHolder).textviewforusername.setText(username);//리셋이어서 이름 넣어줌


                        //i가  0이상인 경우에서 -> 리사이클러뷰 아이템수 -1 이  현재 포지션 보다 클때이다.
                        // -> 이거는  현재 포지션 다음 포지션이 존재한가 아닌가의 여부를 알기 위해서이다
                        //결국  시간이랑  안읽은 사람 수 보여줄지 아닐지 판별용임.
                        if(getItemCount()-1>position){

                            //현재 포지션 다음  포지션의 채팅메세지의  날짜
                            Date futuredate=new  SimpleDateFormat("YYYY-MM-DD HH:mm").parse(chattingtext.get(position + 1).get("date").toString());

                            //다음 포지션 날짜 -> 시간 형태로
                            String futuretime=new SimpleDateFormat("aaa hh:mm").format(futuredate);

                            //다음 포지션  채팅 메세지  보낸 유저의 uid
                            String futureid=chattingtext.get(position+1).get("id").toString();

                            if(futuretime.equals(time)){//포지션 1앞 시간과  현재 포지션 시간이 같을때이다.

                                if(futureid.equals(senderuid)){//포지션 1앞과  현재 포지션 시간이 같고,  uid가  두포지션 모두  똑같을 때이다.

                                    //채팅 본사람 숫자 텍스트뷰
                                    ((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount.setText("");//일단  1로 해놓음.-> 나중에 처리해야됨.
                                    ((chatrecyclerviewadapterviewholder) viewHolder).chatpresenttime.setText("");//처음 채팅이므로  채팅 보내진 시간도 보내준다.// 여기에다가 -> 본사람 숫자 넣어줌.
                                    ((chatrecyclerviewadapterviewholder) viewHolder).chatpresenttime.setTextColor(Color.YELLOW);
                                    ((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount.setTextColor(Color.BLACK);

                                    //해당  ->  채팅의 유저 읽은수  넣어주기.
                                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatrecyclerviewadapterviewholder) viewHolder).chatpresenttime);


                                }else{//두 포지션 시간이 같고  uid가 틀릴때 즉  다음 메세지가  상대 메세지 일때는  현재 포지션에서  시간을 출려해야한다.

                                    ((chatrecyclerviewadapterviewholder) viewHolder).chatpresenttime.setText(time);//현재 포지션 시간 출력
                                    ((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount.setText("");//현재 포지션 채팅 메세지  안읽은 유저의 숫자  보여주기.
                                    ((chatrecyclerviewadapterviewholder) viewHolder).chatpresenttime.setTextColor(Color.BLACK);
                                    ((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                                    //해당  ->  채팅의 유저 읽은수  넣어주기.
                                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount);

                                }

                            }else{//두 포지션  시간이 다를때이다.

                                ((chatrecyclerviewadapterviewholder) viewHolder).chatpresenttime.setText(time);//다음 메세지 시간이 다르면  현재 퐂션에서 무조건 시간을 출력해야한다.
                                ((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount.setText("");
                                ((chatrecyclerviewadapterviewholder) viewHolder).chatpresenttime.setTextColor(Color.BLACK);
                                ((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                                //해당  ->  채팅의 유저 읽은수  넣어주기.
                                get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount);

                            }

                        }else{//다음 포지션이  존재 하지 않는경우이다.-> 이경우에는 마지막 아이템이므로  시간이 출력된다.

                            ((chatrecyclerviewadapterviewholder) viewHolder).chatpresenttime.setText(time);
                            ((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount.setText("");
                            ((chatrecyclerviewadapterviewholder) viewHolder).chatpresenttime.setTextColor(Color.BLACK);
                            ((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                            //해당  ->  채팅의 유저 읽은수  넣어주기.
                            get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount);

                        }////if(getItemCount()-1>position){ 끝

                    }//i 값  0 초과 아닐때 끝

                }else{//이전 시간과  현재 시간이  같지 않거나 ,  이전아이디와 현재 아이디가  안같거나  둘다 같지않을때

                    if(pastid.equals(senderuid)) {//이전 아이디와  현재 유저의 아이디가  같은 경우-> 이경우는  이전 시간과 현재 시간이  같지 않은경우.

                        i=0;//시간이 안맞으므로,  i =0으로 리셋

                        //채팅이 지속될때 나오는 normal 채팅내용 textview
                        ((chatrecyclerviewadapterviewholder) viewHolder).textviewfornormalchat.setVisibility(View.GONE);//맨처음이므로,  normal 채팅ui는  안보임.
                        ((chatrecyclerviewadapterviewholder) viewHolder).textviewfornormalchat.setText("");//혹시나  noraml챗팅 텍스트에 내용이 있을수 있으니 없애줌.


                        //맨처음 채팅때 나오는  채팅내용 textview
                        ((chatrecyclerviewadapterviewholder) viewHolder).textViewforshowchat.setVisibility(View.VISIBLE);//기존  채팅 gui  보여줌.
                        ((chatrecyclerviewadapterviewholder) viewHolder).textViewforshowchat.setText(chatcontent);//해당 포지션  채팅 내용  기존 채팅 gui에 담아줌

                        //보낸 사람 프로필 이미지
                        ((chatrecyclerviewadapterviewholder) viewHolder).imageViewforuserprofile.setVisibility(View.VISIBLE);//처음 채팅이므로  이미지뷰를 보여준다.

                        //프로필 이미지 path가 null아 아닐경우
                        if(!imagepath.equals("null")) {

                            Glide.with(context).load(url).into(((chatrecyclerviewadapterviewholder) viewHolder).imageViewforuserprofile);//해당 프로필 이미지 서버에서 받아와  넣어줌.

                        }else{

                            ((chatrecyclerviewadapterviewholder) viewHolder).imageViewforuserprofile.setImageResource(R.mipmap.ic_launcher);

                        }


                        //보낸사람  이름 담기는 텍스트뷰
                        ((chatrecyclerviewadapterviewholder) viewHolder).textviewforusername.setVisibility(View.VISIBLE);//처음 채팅이므로 보낸사람 이름을 보여준다.
                        ((chatrecyclerviewadapterviewholder) viewHolder).textviewforusername.setText(username);//채팅 이름 닫아줌.

                        //i가  0이상인 경우에서 -> 리사이클러뷰 아이템수 -1 이  현재 포지션 보다 클때이다.
                        // -> 이거는  현재 포지션 다음 포지션이 존재한가 아닌가의 여부를 알기 위해서이다
                        //결국  시간이랑  안읽은 사람 수 보여줄지 아닐지 판별용임.
                        if(getItemCount()-1>position){

                            //현재 포지션 다음  포지션의 채팅메세지의  날짜
                            Date futuredate=new  SimpleDateFormat("YYYY-MM-DD HH:mm").parse(chattingtext.get(position + 1).get("date").toString());

                            //다음 포지션 날짜 -> 시간 형태로
                            String futuretime=new SimpleDateFormat("aaa hh:mm").format(futuredate);

                            //다음 포지션  채팅 메세지  보낸 유저의 uid
                            String futureid=chattingtext.get(position+1).get("id").toString();

                            if(futuretime.equals(time)){//포지션 1앞 시간과  현재 포지션 시간이 같을때이다.

                                if(futureid.equals(senderuid)){//포지션 1앞과  현재 포지션 시간이 같고,  uid가  두포지션 모두  똑같을 때이다.

                                    //채팅 본사람 숫자 텍스트뷰
                                    ((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount.setText("");//일단  1로 해놓음.-> 나중에 처리해야됨.
                                    ((chatrecyclerviewadapterviewholder) viewHolder).chatpresenttime.setText("");//처음 채팅이므로  채팅 보내진 시간도 보내준다.여기에다가 -> 본사람 숫자 넣어줌.
                                    ((chatrecyclerviewadapterviewholder) viewHolder).chatpresenttime.setTextColor(Color.YELLOW);
                                    ((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount.setTextColor(Color.BLACK);

                                    //해당  ->  채팅의 유저 읽은수  넣어주기.
                                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatrecyclerviewadapterviewholder) viewHolder).chatpresenttime);

                                }else{//두 포지션 시간이 같고  uid가 틀릴때 즉  다음 메세지가  상대 메세지 일때는  현재 포지션에서  시간을 출려해야한다.

                                    ((chatrecyclerviewadapterviewholder) viewHolder).chatpresenttime.setText(time);//현재 포지션 시간 출력
                                    ((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount.setText("");//현재 포지션 채팅 메세지  안읽은 유저의 숫자  보여주기.
                                    ((chatrecyclerviewadapterviewholder) viewHolder).chatpresenttime.setTextColor(Color.BLACK);
                                    ((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                                    //해당  ->  채팅의 유저 읽은수  넣어주기.
                                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount);

                                }

                            }else{//두 포지션  시간이 다를때이다.

                                ((chatrecyclerviewadapterviewholder) viewHolder).chatpresenttime.setText(time);//다음 메세지 시간이 다르면  현재 퐂션에서 무조건 시간을 출력해야한다.
                                ((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount.setText("");
                                ((chatrecyclerviewadapterviewholder) viewHolder).chatpresenttime.setTextColor(Color.BLACK);
                                ((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                                //해당  ->  채팅의 유저 읽은수  넣어주기.
                                get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount);

                            }

                        }else{//다음 포지션이  존재 하지 않는경우이다.-> 이경우에는 마지막 아이템이므로  시간이 출력된다.

                            ((chatrecyclerviewadapterviewholder) viewHolder).chatpresenttime.setText(time);
                            ((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount.setText("");
                            ((chatrecyclerviewadapterviewholder) viewHolder).chatpresenttime.setTextColor(Color.BLACK);
                            ((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                            //해당  ->  채팅의 유저 읽은수  넣어주기.
                            get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount);

                        }////if(getItemCount()-1>position){ 끝

                    }else{//이전 아이디와 현재 유저 아이디가  안같을 경우.

                        i=0;//시간이 안맞으므로,  i =0으로 리셋

                        //채팅이 지속될때 나오는 normal 채팅내용 textview
                        ((chatrecyclerviewadapterviewholder) viewHolder).textviewfornormalchat.setVisibility(View.GONE);//맨처음이므로,  normal 채팅ui는  안보임.
                        ((chatrecyclerviewadapterviewholder) viewHolder).textviewfornormalchat.setText("");//혹시나  noraml챗팅 텍스트에 내용이 있을수 있으니 없애줌.


                        //맨처음 채팅때 나오는  채팅내용 textview
                        ((chatrecyclerviewadapterviewholder) viewHolder).textViewforshowchat.setVisibility(View.VISIBLE);//기존  채팅 gui  보여줌.
                        ((chatrecyclerviewadapterviewholder) viewHolder).textViewforshowchat.setText(chatcontent);//해당 포지션  채팅 내용  기존 채팅 gui에 담아줌

                        //보낸 사람 프로필 이미지
                        ((chatrecyclerviewadapterviewholder) viewHolder).imageViewforuserprofile.setVisibility(View.VISIBLE);//처음 채팅이므로  이미지뷰를 보여준다.

                        //프로필 이미지 path가 null아 아닐경우
                        if(!imagepath.equals("null")) {
                            Glide.with(context).load(url).into(((chatrecyclerviewadapterviewholder) viewHolder).imageViewforuserprofile);//해당 프로필 이미지 서버에서 받아와  넣어줌.

                        }else{

                            ((chatrecyclerviewadapterviewholder) viewHolder).imageViewforuserprofile.setImageResource(R.mipmap.ic_launcher);
                        }


                        //보낸사람  이름 담기는 텍스트뷰
                        ((chatrecyclerviewadapterviewholder) viewHolder).textviewforusername.setVisibility(View.VISIBLE);//처음 채팅이므로 보낸사람 이름을 보여준다.
                        ((chatrecyclerviewadapterviewholder) viewHolder).textviewforusername.setText(username);//채팅 이름 닫아줌.

                        //i가  0이상인 경우에서 -> 리사이클러뷰 아이템수 -1 이  현재 포지션 보다 클때이다.
                        // -> 이거는  현재 포지션 다음 포지션이 존재한가 아닌가의 여부를 알기 위해서이다
                        //결국  시간이랑  안읽은 사람 수 보여줄지 아닐지 판별용임.
                        if(getItemCount()-1>position){

                            //현재 포지션 다음  포지션의 채팅메세지의  날짜
                            Date futuredate=new  SimpleDateFormat("YYYY-MM-DD HH:mm").parse(chattingtext.get(position + 1).get("date").toString());

                            //다음 포지션 날짜 -> 시간 형태로
                            String futuretime=new SimpleDateFormat("aaa hh:mm").format(futuredate);

                            //다음 포지션  채팅 메세지  보낸 유저의 uid
                            String futureid=chattingtext.get(position+1).get("id").toString();

                            if(futuretime.equals(time)){//포지션 1앞 시간과  현재 포지션 시간이 같을때이다.

                                if(futureid.equals(senderuid)){//포지션 1앞과  현재 포지션 시간이 같고,  uid가  두포지션 모두  똑같을 때이다.

                                    //채팅 본사람 숫자 텍스트뷰

                                    ((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount.setText("");//일단  1로 해놓음.-> 나중에 처리해야됨.
                                    ((chatrecyclerviewadapterviewholder) viewHolder).chatpresenttime.setText("");//처음 채팅이므로  채팅 보내진 시간도 보내준다.여기에다가 -> 본사람 숫자 넣어줌.
                                    ((chatrecyclerviewadapterviewholder) viewHolder).chatpresenttime.setTextColor(Color.YELLOW);
                                    ((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount.setTextColor(Color.BLACK);

                                    //해당  ->  채팅의 유저 읽은수  넣어주기.
                                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatrecyclerviewadapterviewholder) viewHolder).chatpresenttime);


                                }else{//두 포지션 시간이 같고  uid가 틀릴때 즉  다음 메세지가  상대 메세지 일때는  현재 포지션에서  시간을 출려해야한다.


                                    ((chatrecyclerviewadapterviewholder) viewHolder).chatpresenttime.setText(time);//현재 포지션 시간 출력
                                    ((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount.setText("");//현재 포지션 채팅 메세지  안읽은 유저의 숫자  보여주기.
                                    ((chatrecyclerviewadapterviewholder) viewHolder).chatpresenttime.setTextColor(Color.BLACK);
                                    ((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                                    //해당  ->  채팅의 유저 읽은수  넣어주기.
                                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount);

                                }

                            }else{//두 포지션  시간이 다를때이다.

                                ((chatrecyclerviewadapterviewholder) viewHolder).chatpresenttime.setText(time);//다음 메세지 시간이 다르면  현재 퐂션에서 무조건 시간을 출력해야한다.
                                ((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount.setText("");
                                ((chatrecyclerviewadapterviewholder) viewHolder).chatpresenttime.setTextColor(Color.BLACK);
                                ((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                                //해당  ->  채팅의 유저 읽은수  넣어주기.
                                get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount);

                            }

                        }else{//다음 포지션이  존재 하지 않는경우이다.-> 이경우에는 마지막 아이템이므로  시간이 출력된다.

                            ((chatrecyclerviewadapterviewholder) viewHolder).chatpresenttime.setText(time);
                            ((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount.setText("");
                            ((chatrecyclerviewadapterviewholder) viewHolder).chatpresenttime.setTextColor(Color.BLACK);
                            ((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                            //해당  ->  채팅의 유저 읽은수  넣어주기.
                            get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount);

                        }////if(getItemCount()-1>position){ 끝


                    }//이전 포지션 아이디와 현재 포지션 아이디가  같지 않을 경우 끝


                }////이전 시간과  현재 시간이  같지 않거나 ,  이전아이디와 현재 아이디가  안같거나  둘다 같지않을 경우 끝


            }else {//현재 포지션이 0초과가 아닐때  -> 즉  채팅  맨처음 칠때  조건// 남이 보냈을때 경우.


                i=0;//그전에 연속된게 없으므로 i=0이다.
                Log.v("check", "채팅 순서 체크 : position 0일때");

                //현재 포지션에서  sender 의 uid
                senderuid=chattingtext.get(position).get("id").toString();

                //채팅이 지속될때 나오는 normal 채팅내용 textview
                ((chatrecyclerviewadapterviewholder) viewHolder).textviewfornormalchat.setVisibility(View.GONE);//맨처음이므로,  normal 채팅ui는  안보임.
                ((chatrecyclerviewadapterviewholder) viewHolder).textviewfornormalchat.setText("");//혹시나  noraml챗팅 텍스트에 내용이 있을수 있으니 없애줌.


                //맨처음 채팅때 나오는  채팅내용 textview
                ((chatrecyclerviewadapterviewholder) viewHolder).textViewforshowchat.setVisibility(View.VISIBLE);//기존  채팅 gui  보여줌.
                ((chatrecyclerviewadapterviewholder) viewHolder).textViewforshowchat.setText(chatcontent);//해당 포지션  채팅 내용  기존 채팅 gui에 담아줌

                //보낸 사람 프로필 이미지
                ((chatrecyclerviewadapterviewholder) viewHolder).imageViewforuserprofile.setVisibility(View.VISIBLE);//처음 채팅이므로  이미지뷰를 보여준다.

                //프로필 이미지 path가 null아 아닐경우
                if(!imagepath.equals("null")) {
                    Glide.with(context).load(url).into(((chatrecyclerviewadapterviewholder) viewHolder).imageViewforuserprofile);//해당 프로필 이미지 서버에서 받아와  넣어줌.
                }else{
                    ((chatrecyclerviewadapterviewholder) viewHolder).imageViewforuserprofile.setImageResource(R.mipmap.ic_launcher);
                }


                //보낸사람  이름 담기는 텍스트뷰
                ((chatrecyclerviewadapterviewholder) viewHolder).textviewforusername.setVisibility(View.VISIBLE);//처음 채팅이므로 보낸사람 이름을 보여준다.
                ((chatrecyclerviewadapterviewholder) viewHolder).textviewforusername.setText(username);//채팅 이름 닫아줌.

                //i가  0이상인 경우에서 -> 리사이클러뷰 아이템수 -1 이  현재 포지션 보다 클때이다.
                // -> 이거는  현재 포지션 다음 포지션이 존재한가 아닌가의 여부를 알기 위해서이다
                //결국  시간이랑  안읽은 사람 수 보여줄지 아닐지 판별용임.
                if(getItemCount()-1>position){

                    //현재 포지션 다음  포지션의 채팅메세지의  날짜
                    Date futuredate=new  SimpleDateFormat("YYYY-MM-DD HH:mm").parse(chattingtext.get(position + 1).get("date").toString());

                    //다음 포지션 날짜 -> 시간 형태로
                    String futuretime=new SimpleDateFormat("aaa hh:mm").format(futuredate);

                    //다음 포지션  채팅 메세지  보낸 유저의 uid
                    String futureid=chattingtext.get(position+1).get("id").toString();

                    if(futuretime.equals(time)){//포지션 1앞 시간과  현재 포지션 시간이 같을때이다.

                        if(futureid.equals(senderuid)){//포지션 1앞과  현재 포지션 시간이 같고,  uid가  두포지션 모두  똑같을 때이다.

                            //채팅 본사람 숫자 텍스트뷰

                            ((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount.setText("");//일단  1로 해놓음.-> 나중에 처리해야됨.
                            ((chatrecyclerviewadapterviewholder) viewHolder).chatpresenttime.setText("");//처음 채팅이므로  채팅 보내진 시간도 보내준다.여기에다가 -> 본사람 숫자 넣어줌.
                            ((chatrecyclerviewadapterviewholder) viewHolder).chatpresenttime.setTextColor(Color.YELLOW);
                            ((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount.setTextColor(Color.BLACK);

                            //해당  ->  채팅의 유저 읽은수  넣어주기.
                            get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatrecyclerviewadapterviewholder) viewHolder).chatpresenttime);


                        }else{//두 포지션 시간이 같고  uid가 틀릴때 즉  다음 메세지가  상대 메세지 일때는  현재 포지션에서  시간을 출려해야한다.


                            ((chatrecyclerviewadapterviewholder) viewHolder).chatpresenttime.setText(time);//현재 포지션 시간 출력
                            ((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount.setText("");//현재 포지션 채팅 메세지  안읽은 유저의 숫자  보여주기.
                            ((chatrecyclerviewadapterviewholder) viewHolder).chatpresenttime.setTextColor(Color.BLACK);
                            ((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                            //해당  ->  채팅의 유저 읽은수  넣어주기.
                            get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount);

                        }

                    }else{//두 포지션  시간이 다를때이다.

                        ((chatrecyclerviewadapterviewholder) viewHolder).chatpresenttime.setText(time);//다음 메세지 시간이 다르면  현재 퐂션에서 무조건 시간을 출력해야한다.
                        ((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount.setText("");
                        ((chatrecyclerviewadapterviewholder) viewHolder).chatpresenttime.setTextColor(Color.BLACK);
                        ((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                        //해당  ->  채팅의 유저 읽은수  넣어주기.
                        get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount);

                    }

                }else{//다음 포지션이  존재 하지 않는경우이다.-> 이경우에는 마지막 아이템이므로  시간이 출력된다.

                    ((chatrecyclerviewadapterviewholder) viewHolder).chatpresenttime.setText(time);
                    ((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount.setText("");
                    ((chatrecyclerviewadapterviewholder) viewHolder).chatpresenttime.setTextColor(Color.BLACK);
                    ((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                    //해당  ->  채팅의 유저 읽은수  넣어주기.
                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatrecyclerviewadapterviewholder) viewHolder).chatwatchercount);

                }////if(getItemCount()-1>position){ 끝



            }//현재 포지션이 0초과가 아닐때  -> 즉  채팅  맨처음 칠때  조건// 남이 보냈을때 경우. 끝

        }//아이템 뷰타입  0일때  -> 상대가 보냈을때  끝
        else if(viewHolder.getItemViewType()==1){ //아이템 뷰타입이  1일때-> 내가 보냈을때

            Log.v("check", "ChattingDataRecyclerViewAdapter 의   viewHolder.getItemType==1 일때 실행됨-> 내가 보낸 메세지 타입");

            int i=0;//해당 타입의 메세지 (내가 보낸 메세지 타입)가  연속적으로  실행되는지를 체크 하기 위한  값

            if(position>0) {// 포지션이  0초과 일때-> 즉  리사이클러뷰  아이템이  2개 이상 일때이다. 이전 데이터가  있으므로,  같은 사람이  반복적으로  채팅을 보낼때  gui처리를 할수 있다.


                //현재 포지션 이전 포지션의 날짜.
                Date pastdate = new SimpleDateFormat("YYYY-MM-DD HH:mm").parse(chattingtext.get(position - 1).get("date").toString());

                //위  이전 포지션 날짜  time으로  변형 시켜줌.
                String pasttime = new SimpleDateFormat("aaa hh:mm").format(pastdate);

                //현재 포지션  이전 포지션의  채팅메세지  보낸 유저의  uid
                pastid=chattingtext.get(position-1).get("id").toString();

                //이전 포지션 값이 3일때 경우이다.
                String pastviewtype=chattingtext.get(position-1).get("viewtype").toString();


                 //현재 포지션 이전 텍스트의  시간과 현재시간이  같고, 포지션 이전 아이디와  해당 유저의 아이디가 같을때이다.
                 //그리고 뷰타입이 3일때 또는 4일때 -> 이미지 채팅과  채팅사이즈  가지고옴.
                if((pasttime.equals(time) && pastid.equals(useruid) && pastviewtype.equals("3")) || (pasttime.equals(time) && pastid.equals(useruid) && pastviewtype.equals("4")) ||(pasttime.equals(time) && pastid.equals(useruid) && pastviewtype.equals("5"))){

                    i=i+1;//이전 포지션의 채팅메세지의 시간과  유저가 현재 유저 본인과  같을 경우-> i를  1씩 계속 올려줘서 -> 계속 같은 유저가  연속으로  채팅을 보내는건지 확인한다.



                    if(i>0) {//i 값이  0초과  ->  같은 유저의 채팅이 연속으로 진행될때이다.

                        //이경우 -> 이전  채팅메세지 유저와 같은 유저가  연속적으로 메세지를 보낸것이므로,  normal ui형태로 메세지 ui를 바꿔준다.
                        Log.v("check", "채팅 순서 체크 : 아이템은 2개 이상인상태이고,  이전 포지션 채팅시간과 현재 포지션 채팅시간이 맞고, i체크가 0이상 일때 즉 -> 노말 채팅ui나옴");

                        ((mycahttingviewholder) viewHolder).normalchat.setVisibility(View.VISIBLE);//normal -> 보여줌.

                        //원래 채팅 말풍선 ui gone으로 처리 -> gone이유는 해당  ui는 margintop을 설정해놔서 다른  포지션 ui와  간격이 있지만, 연속적인 채팅에선
                        //필요하지 않기 때문이다.
                        ((mycahttingviewholder) viewHolder).mychat.setVisibility(View.GONE);

                        //원래 채팅 말풍선 ->  텍스트 내용 x
                        ((mycahttingviewholder)viewHolder).mychat.setText("");

                        //normal ui에 넣어줌.
                        ((mycahttingviewholder) viewHolder).normalchat.setText(chatcontent);


                        //i가  0이상인 경우에서 -> 리사이클러뷰 아이템수 -1 이  현재 포지션 보다 클때이다.
                        // -> 이거는  현재 포지션 다음 포지션이 존재한가 아닌가의 여부를 알기 위해서이다.
                        if(getItemCount()-1>position){

                           //현재 포지션 다음  포지션의 채팅메세지의  날짜
                           Date futuredate=new  SimpleDateFormat("YYYY-MM-DD HH:mm").parse(chattingtext.get(position + 1).get("date").toString());

                           //다음 포지션 날짜 -> 시간 형태로
                           String futuretime=new SimpleDateFormat("aaa hh:mm").format(futuredate);

                           //다음 포지션  채팅 메세지  보낸 유저의 uid
                           String futureid=chattingtext.get(position+1).get("id").toString();

                            if(futuretime.equals(time)){//포지션 1앞 시간과  현재 포지션 시간이 같을때이다.

                                if(futureid.equals(useruid)){//포지션 1앞과  현재 포지션 시간이 같고,  uid가  두포지션 모두  똑같을 때이다.

                                    ((mycahttingviewholder)viewHolder).chattime.setText("");//이때  현재 포지션에는  시간이 나올 필요없으므로 없애주낟.
                                    ((mycahttingviewholder) viewHolder).chatwatchercount.setText("");//이때 현재 포지션 아이템에는  채팅 메세지 안읽은 유저의 숫자가 나올 필요 x
                                    ((mycahttingviewholder)viewHolder).chattime.setTextColor(Color.YELLOW);
                                    ((mycahttingviewholder) viewHolder).chatwatchercount.setTextColor(Color.BLACK);

                                    //해당  ->  채팅의 유저 읽은수  넣어주기.
                                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mycahttingviewholder)viewHolder).chattime);



                                }else{//두 포지션 시간이 같고  uid가 틀릴때 즉  다음 메세지가  상대 메세지 일때는  현재 포지션에서  시간을 출려해야한다.


                                    ((mycahttingviewholder)viewHolder).chattime.setText(time);//현재 포지션 시간 출력
                                    ((mycahttingviewholder) viewHolder).chatwatchercount.setText("");//현재 포지션 채팅 메세지  안읽은 유저의 숫자  보여주기.
                                    ((mycahttingviewholder)viewHolder).chattime.setTextColor(Color.BLACK);
                                    ((mycahttingviewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                                    //해당  ->  채팅의 유저 읽은수  넣어주기.
                                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mycahttingviewholder)viewHolder).chatwatchercount);

                                }

                            }else{//두 포지션  시간이 다를때이다.

                                ((mycahttingviewholder)viewHolder).chattime.setText(time);//다음 메세지 시간이 다르면  현재 퐂션에서 무조건 시간을 출력해야한다.
                                ((mycahttingviewholder) viewHolder).chatwatchercount.setText("");
                                ((mycahttingviewholder)viewHolder).chattime.setTextColor(Color.BLACK);
                                ((mycahttingviewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                                //해당  ->  채팅의 유저 읽은수  넣어주기.
                                get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mycahttingviewholder)viewHolder).chatwatchercount);

                            }

                        }else{//다음 포지션이  존재 하지 않는경우이다.-> 이경우에는 마지막 아이템이므로  시간이 출력된다.

                            ((mycahttingviewholder)viewHolder).chattime.setText(time);
                            ((mycahttingviewholder) viewHolder).chatwatchercount.setText("");
                            ((mycahttingviewholder)viewHolder).chattime.setTextColor(Color.BLACK);
                            ((mycahttingviewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                            //해당  ->  채팅의 유저 읽은수  넣어주기.
                            get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mycahttingviewholder)viewHolder).chatwatchercount);

                        }////if(getItemCount()-1>position){ 끝


                    }else{//이전 포지션과 같은 시간에서  i 값이  0초과가 아닐때  -> 이때는  같은 유저으 채팅이 연속적으로 진행되지 않는다.

                        Log.v("check", "채팅 순서 체크 : 아이템이  2개 이상인 경우,  과거시간과 현재시간이 맞지만, i체크가 0일때 즉 -> 맨처음다시 리셋");
                        ((mycahttingviewholder) viewHolder).normalchat.setVisibility(View.INVISIBLE);
                        ((mycahttingviewholder) viewHolder).mychat.setVisibility(View.VISIBLE);
                        ((mycahttingviewholder) viewHolder).normalchat.setText("");
                        ((mycahttingviewholder)viewHolder).mychat.setText(chatcontent);

                        if(getItemCount()-1>position){

                            Date futuredate=new  SimpleDateFormat("YYYY-MM-DD HH:mm").parse(chattingtext.get(position + 1).get("date").toString());
                            String futuretime=new SimpleDateFormat("aaa hh:mm").format(futuredate);
                            String futureid=chattingtext.get(position+1).get("id").toString();

                            if(futuretime.equals(time)){//포지션 1앞 시간과  현재 포지션 시간이 같으면  -> 여기에 시간이 들어가면 안된다.

                                if(futureid.equals(useruid)){

                                    ((mycahttingviewholder)viewHolder).chattime.setText("");
                                    ((mycahttingviewholder) viewHolder).chatwatchercount.setText("");
                                    ((mycahttingviewholder)viewHolder).chattime.setTextColor(Color.YELLOW);
                                    ((mycahttingviewholder) viewHolder).chatwatchercount.setTextColor(Color.BLACK);

                                    //해당  ->  채팅의 유저 읽은수  넣어주기.
                                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mycahttingviewholder)viewHolder).chattime);

                                }else{

                                    ((mycahttingviewholder)viewHolder).chattime.setText(time);
                                    ((mycahttingviewholder) viewHolder).chatwatchercount.setText("");
                                    ((mycahttingviewholder)viewHolder).chattime.setTextColor(Color.BLACK);
                                    ((mycahttingviewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                                    //해당  ->  채팅의 유저 읽은수  넣어주기.
                                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mycahttingviewholder)viewHolder).chatwatchercount);
                                }

                            }else{

                                ((mycahttingviewholder)viewHolder).chattime.setText(time);
                                ((mycahttingviewholder) viewHolder).chatwatchercount.setText("");
                                ((mycahttingviewholder)viewHolder).chattime.setTextColor(Color.BLACK);
                                ((mycahttingviewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                                //해당  ->  채팅의 유저 읽은수  넣어주기.
                                get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mycahttingviewholder)viewHolder).chatwatchercount);

                            }
                        }else{

                            ((mycahttingviewholder)viewHolder).chattime.setText(time);
                            ((mycahttingviewholder) viewHolder).chatwatchercount.setText("");
                            ((mycahttingviewholder)viewHolder).chattime.setTextColor(Color.BLACK);
                            ((mycahttingviewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                            //해당  ->  채팅의 유저 읽은수  넣어주기.
                            get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mycahttingviewholder)viewHolder).chatwatchercount);


                        }//if(getItemCount()-1>position){ 끝

                       }
                }else{//이전 시간과  현재 시간이  같지 않거나 ,  이전아이디와 현재 아이디가  안같거나  둘다 같지않을때

                    if(pastid.equals(useruid)){//이전 아이디와  현재 유저의 아이디가  같은 경우-> 이경우는  이전 시간과 현재 시간이  같지 않은경우.

                        i=0;//시간이 안맞으므로,  i =0으로 리셋
                        Log.v("check", "채팅 순서 체크 : position 0이상이지만,  과거 시간이 현재 시간과 안맞고, 이전 유저가  나일때");

                        ((mycahttingviewholder) viewHolder).normalchat.setVisibility(View.INVISIBLE);
                        ((mycahttingviewholder) viewHolder).mychat.setVisibility(View.VISIBLE);
                        ((mycahttingviewholder) viewHolder).normalchat.setText("");
                        ((mycahttingviewholder)viewHolder).mychat.setText(chatcontent);

                        if(getItemCount()-1>position){

                            Date futuredate=new  SimpleDateFormat("YYYY-MM-DD HH:mm").parse(chattingtext.get(position + 1).get("date").toString());
                            String futuretime=new SimpleDateFormat("aaa hh:mm").format(futuredate);
                            String futureid=chattingtext.get(position+1).get("id").toString();

                            if(futuretime.equals(time)){//포지션 1앞 시간과  현재 포지션 시간이 같으면  -> 여기에 시간이 들어가면 안된다.

                                if(futureid.equals(useruid)){
                                    ((mycahttingviewholder)viewHolder).chattime.setText("");
                                    ((mycahttingviewholder) viewHolder).chatwatchercount.setText("");
                                    ((mycahttingviewholder)viewHolder).chattime.setTextColor(Color.YELLOW);
                                    ((mycahttingviewholder) viewHolder).chatwatchercount.setTextColor(Color.BLACK);
                                    //해당  ->  채팅의 유저 읽은수  넣어주기.
                                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mycahttingviewholder)viewHolder).chattime);


                                }else{

                                    ((mycahttingviewholder)viewHolder).chattime.setText(time);
                                    ((mycahttingviewholder) viewHolder).chatwatchercount.setText("");
                                    ((mycahttingviewholder)viewHolder).chattime.setTextColor(Color.BLACK);
                                    ((mycahttingviewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);
                                    //해당  ->  채팅의 유저 읽은수  넣어주기.
                                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mycahttingviewholder)viewHolder).chatwatchercount);


                                }


                            }else{

                                ((mycahttingviewholder)viewHolder).chattime.setText(time);
                                ((mycahttingviewholder) viewHolder).chatwatchercount.setText("");
                                ((mycahttingviewholder)viewHolder).chattime.setTextColor(Color.BLACK);
                                ((mycahttingviewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);
                                //해당  ->  채팅의 유저 읽은수  넣어주기.
                                get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mycahttingviewholder)viewHolder).chatwatchercount);

                            }
                        }else{

                            ((mycahttingviewholder)viewHolder).chattime.setText(time);
                            ((mycahttingviewholder) viewHolder).chatwatchercount.setText("");
                            ((mycahttingviewholder)viewHolder).chattime.setTextColor(Color.BLACK);
                            ((mycahttingviewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);
                            //해당  ->  채팅의 유저 읽은수  넣어주기.
                            get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mycahttingviewholder)viewHolder).chatwatchercount);

                        }

                    }else{//이전 아이디와  현재 아이디가  안맞을때,

                        i=0;//이경우도 역시  채팅이  나로  연속되지 않기 때문에  i=0으로  리셋
                        Log.v("check", "채팅 순서 체크 : position 0이상이지만,  과거 시간이 현재 시간과 안맞고, 이전 유저가 내가아닐때,");

                        ((mycahttingviewholder) viewHolder).normalchat.setVisibility(View.INVISIBLE);
                        ((mycahttingviewholder) viewHolder).mychat.setVisibility(View.VISIBLE);
                        ((mycahttingviewholder) viewHolder).normalchat.setText("");
                        ((mycahttingviewholder)viewHolder).mychat.setText(chatcontent);



                        if(getItemCount()-1>position){

                            Date futuredate=new  SimpleDateFormat("YYYY-MM-DD HH:mm").parse(chattingtext.get(position + 1).get("date").toString());
                            String futuretime=new SimpleDateFormat("aaa hh:mm").format(futuredate);
                            String futureid=chattingtext.get(position+1).get("id").toString();

                            if(futuretime.equals(time)){//포지션 1앞 시간과  현재 포지션 시간이 같으면  -> 여기에 시간이 들어가면 안된다.

                                if(futureid.equals(useruid)){
                                    ((mycahttingviewholder)viewHolder).chattime.setText("");
                                    ((mycahttingviewholder) viewHolder).chatwatchercount.setText("");
                                    ((mycahttingviewholder)viewHolder).chattime.setTextColor(Color.YELLOW);
                                    ((mycahttingviewholder) viewHolder).chatwatchercount.setTextColor(Color.BLACK);
                                    //해당  ->  채팅의 유저 읽은수  넣어주기.
                                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mycahttingviewholder)viewHolder).chattime);



                                }else{

                                    ((mycahttingviewholder)viewHolder).chattime.setText(time);
                                    ((mycahttingviewholder) viewHolder).chatwatchercount.setText("");
                                    ((mycahttingviewholder)viewHolder).chattime.setTextColor(Color.BLACK);
                                    ((mycahttingviewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);
                                    //해당  ->  채팅의 유저 읽은수  넣어주기.
                                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mycahttingviewholder)viewHolder).chatwatchercount);

                                }


                            }else{

                                ((mycahttingviewholder)viewHolder).chattime.setText(time);
                                ((mycahttingviewholder) viewHolder).chatwatchercount.setText("");
                                ((mycahttingviewholder)viewHolder).chattime.setTextColor(Color.BLACK);
                                ((mycahttingviewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                                //해당  ->  채팅의 유저 읽은수  넣어주기.
                                get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mycahttingviewholder)viewHolder).chatwatchercount);

                            }

                        }else{

                            ((mycahttingviewholder)viewHolder).chattime.setText(time);
                            ((mycahttingviewholder) viewHolder).chatwatchercount.setText("");
                            ((mycahttingviewholder)viewHolder).chattime.setTextColor(Color.BLACK);
                            ((mycahttingviewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                            //해당  ->  채팅의 유저 읽은수  넣어주기.
                            get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mycahttingviewholder)viewHolder).chatwatchercount);

                        }//if(getItemCount()-1>position){끝


                    }//이전 아이디와 현재 아이디 안맞을때 끝

                }//시간 또는 아이디  //  둘다  안맞을때

            }else{//현재 포지션이 0초과가 아닐때  -> 즉  채팅  맨처음 칠때  조건

                i=0;//그전에 연속된게 없으므로 i=0이다.
                Log.v("check", "채팅 순서 체크 : position 0일때");


                ((mycahttingviewholder) viewHolder).normalchat.setVisibility(View.INVISIBLE);
                ((mycahttingviewholder) viewHolder).mychat.setVisibility(View.VISIBLE);
                ((mycahttingviewholder)viewHolder).mychat.setText(chatcontent);
                ((mycahttingviewholder) viewHolder).normalchat.setText("");

                if(getItemCount()>1){

                    Date futuredate=new  SimpleDateFormat("YYYY-MM-DD HH:mm").parse(chattingtext.get(position + 1).get("date").toString());
                    String futuretime=new SimpleDateFormat("aaa hh:mm").format(futuredate);
                    String futureid=chattingtext.get(position+1).get("id").toString();

                    if(futuretime.equals(time)){//포지션 1앞 시간과  현재 포지션 시간이 같으면  -> 여기에 시간이 들어가면 안된다.

                        if(futureid.equals(useruid)){

                            ((mycahttingviewholder)viewHolder).chattime.setText("");
                            ((mycahttingviewholder) viewHolder).chatwatchercount.setText("");

                            ((mycahttingviewholder)viewHolder).chattime.setTextColor(Color.YELLOW);
                            ((mycahttingviewholder) viewHolder).chatwatchercount.setTextColor(Color.BLACK);

                            //해당  ->  채팅의 유저 읽은수  넣어주기.
                            get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mycahttingviewholder)viewHolder).chattime);

                        }else{

                            ((mycahttingviewholder)viewHolder).chattime.setText(time);
                            ((mycahttingviewholder) viewHolder).chatwatchercount.setText("");

                            ((mycahttingviewholder)viewHolder).chattime.setTextColor(Color.BLACK);
                            ((mycahttingviewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                            //해당  ->  채팅의 유저 읽은수  넣어주기.
                            get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mycahttingviewholder)viewHolder).chatwatchercount);

                        }


                    }else{

                        ((mycahttingviewholder)viewHolder).chattime.setText(time);
                        ((mycahttingviewholder) viewHolder).chatwatchercount.setText("");
                        ((mycahttingviewholder)viewHolder).chattime.setTextColor(Color.BLACK);
                        ((mycahttingviewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                        //해당  ->  채팅의 유저 읽은수  넣어주기.
                        get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mycahttingviewholder)viewHolder).chatwatchercount);

                    }

                }else{

                    ((mycahttingviewholder)viewHolder).chattime.setText(time);
                    ((mycahttingviewholder) viewHolder).chatwatchercount.setText("");
                    ((mycahttingviewholder)viewHolder).chattime.setTextColor(Color.BLACK);
                    ((mycahttingviewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                    //해당  ->  채팅의 유저 읽은수  넣어주기.
                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mycahttingviewholder)viewHolder).chatwatchercount);

                }//    if(getItemCount()>1){ 끝

            }//현재 포지션  0일때 끝

        }//채팅 타입  1일때 -> 내가 보낸 채팅 타입일때 끝
        else if(viewHolder.getItemViewType()==2){//채팅타입 2일떄 -> 처음 들어왔을때,

            String entrant_message=chattingtext.get(position).get("message").toString();
            ((in_and_out_viewholder)viewHolder).in_and_out_chatting_ment_textview.setText(entrant_message);

        }//채팅 타입  처음 들어왔을때 끝
        else if(viewHolder.getItemViewType()==3){//채팅 타입  3일때  나갔을때

            String entrant_message=chattingtext.get(position).get("message").toString();

            ((in_and_out_viewholder)viewHolder).in_and_out_chatting_ment_textview.setText(entrant_message);

        }//채티아입 나갔을때 타입 끝
        else if(viewHolder.getItemViewType()==4){//내가  처음 들어왔을때 경우 - 상대가 들어왔을때랑 멘트가 달라야 함으로,

            String entrant_message=chattingtext.get(position).get("message").toString();

            ((in_and_out_viewholder)viewHolder).in_and_out_chatting_ment_textview.setText(entrant_message);


        }//내가 처음 들어왔을 경우 끝
        else if(viewHolder.getItemViewType()==8){//내가  이미지를 보냈을때

            String my_image_message=chattingtext.get(position).get("message").toString();

            //URL -> 이미지 프로필 URL받아오기
            URL url_for_image_chat = new URL("http://13.209.249.1/" + my_image_message);

            Log.v("check_img_chatting", my_image_message);
            Log.v("check", "ChattingDataRecyclerViewAdapter 의   viewHolder.getItemType==8 일때 실행됨-> 내가 보낸 이미지 메세지 타입");

            int i=0;//해당 타입의 메세지 (내가 보낸 메세지 타입)가  연속적으로  실행되는지를 체크 하기 위한  값

            if(position>0) {// 포지션이  0초과 일때-> 즉  리사이클러뷰  아이템이  2개 이상 일때이다. 이전 데이터가  있으므로,  같은 사람이  반복적으로  채팅을 보낼때  gui처리를 할수 있다.


                //현재 포지션 이전 포지션의 날짜.
                Date pastdate = new SimpleDateFormat("YYYY-MM-DD HH:mm").parse(chattingtext.get(position - 1).get("date").toString());

                //위  이전 포지션 날짜  time으로  변형 시켜줌.
                String pasttime = new SimpleDateFormat("aaa hh:mm").format(pastdate);

                //현재 포지션  이전 포지션의  채팅메세지  보낸 유저의  uid
                pastid = chattingtext.get(position - 1).get("id").toString();

                //이전 포지션 값이 3일때 경우이다.
                String pastviewtype = chattingtext.get(position - 1).get("viewtype").toString();


                //현재 포지션 이전 텍스트의  시간과 현재시간이  같고, 포지션 이전 아이디와  해당 유저의 아이디가 같을때이다.
                //그리고 뷰타입이 3일때 또는 4일때 -> 이미지 채팅과  채팅사이즈  가지고옴.
                if ((pasttime.equals(time) && pastid.equals(useruid) && pastviewtype.equals("3")) || (pasttime.equals(time) && pastid.equals(useruid) && pastviewtype.equals("4")) ||(pasttime.equals(time) && pastid.equals(useruid) && pastviewtype.equals("5"))) {//11-1

                    i = i + 1;//이전 포지션의 채팅메세지의 시간과  유저가 현재 유저 본인과  같을 경우-> i를  1씩 계속 올려줘서 -> 계속 같은 유저가  연속으로  채팅을 보내는건지 확인한다.


                if (i > 0) {//i 값이  0초과  ->  같은 유저의 채팅이 연속으로 진행될때이다.

                    //이경우 -> 이전  채팅메세지 유저와 같은 유저가  연속적으로 메세지를 보낸것이므로,  normal ui형태로 메세지 ui를 바꿔준다.
                    Log.v("check", "채팅 순서 체크 : 아이템은 2개 이상인상태이고,  이전 포지션 채팅시간과 현재 포지션 채팅시간이 맞고, i체크가 0이상 일때_> 연속되는 이미지 채팅 나옴.");
                    LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) ((mychatting_image_viewholder) viewHolder).chatting_img_content.getLayoutParams();


                    //채팅 보낸 이미지 path가 null아 아닐경우
                    if(!my_image_message.equals("null")) {

                        layoutParams.rightMargin=dpToPx(7);
                        layoutParams.width= LinearLayout.LayoutParams.WRAP_CONTENT;
                        layoutParams.height=LinearLayout.LayoutParams.WRAP_CONTENT;
                        layoutParams.gravity= Gravity.RIGHT;
                        ((mychatting_image_viewholder) viewHolder).chatting_img_content.setMaxHeight(dpToPx(270));
                        ((mychatting_image_viewholder) viewHolder).chatting_img_content.setMaxWidth(dpToPx(270));
                        ((mychatting_image_viewholder) viewHolder).chatting_img_content.setMinimumHeight(dpToPx(140));
                        ((mychatting_image_viewholder) viewHolder).chatting_img_content.setScaleType(ImageView.ScaleType.FIT_END);
                        layoutParams.topMargin=dpToPx(0);
                        ((mychatting_image_viewholder) viewHolder).chatting_img_content.setLayoutParams(layoutParams);

                        Glide.with(context).load(url_for_image_chat).placeholder(R.drawable.img_error)  .override(dpToPx(160),dpToPx(160))
                                .fitCenter().error(R.drawable.img_error).into(((mychatting_image_viewholder) viewHolder).chatting_img_content);//해당 프로필 이미지 서버에서 받아와  넣어줌.

                     }

                    //i가  0이상인 경우에서 -> 리사이클러뷰 아이템수 -1 이  현재 포지션 보다 클때이다.
                    // -> 이거는  현재 포지션 다음 포지션이 존재한가 아닌가의 여부를 알기 위해서이다.
                    if(getItemCount()-1>position){

                        //현재 포지션 다음  포지션의 채팅메세지의  날짜
                        Date futuredate=new  SimpleDateFormat("YYYY-MM-DD HH:mm").parse(chattingtext.get(position + 1).get("date").toString());

                        //다음 포지션 날짜 -> 시간 형태로
                        String futuretime=new SimpleDateFormat("aaa hh:mm").format(futuredate);

                        //다음 포지션  채팅 메세지  보낸 유저의 uid
                        String futureid=chattingtext.get(position+1).get("id").toString();

                        if(futuretime.equals(time)){//포지션 1앞 시간과  현재 포지션 시간이 같을때이다.

                            if(futureid.equals(useruid)){//포지션 1앞과  현재 포지션 시간이 같고,  uid가  두포지션 모두  똑같을 때이다.

                                ((mychatting_image_viewholder)viewHolder).chattime_img.setText("");//이때  현재 포지션에는  시간이 나올 필요없으므로 없애주낟.
                                ((mychatting_image_viewholder) viewHolder).chatwatchercount_img.setText("");//이때 현재 포지션 아이템에는  채팅 메세지 안읽은 유저의 숫자가 나올 필요 x
                                ((mychatting_image_viewholder)viewHolder).chattime_img.setTextColor(Color.YELLOW);
                                ((mychatting_image_viewholder) viewHolder).chatwatchercount_img.setTextColor(Color.BLACK);

                                //해당  ->  채팅의 유저 읽은수  넣어주기.
                                get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mychatting_image_viewholder)viewHolder).chattime_img);

                            }else{//두 포지션 시간이 같고  uid가 틀릴때 즉  다음 메세지가  상대 메세지 일때는  현재 포지션에서  시간을 출려해야한다.


                                ((mychatting_image_viewholder)viewHolder).chattime_img.setText(time);//현재 포지션 시간 출력
                                ((mychatting_image_viewholder) viewHolder).chatwatchercount_img.setText("");//현재 포지션 채팅 메세지  안읽은 유저의 숫자  보여주기.
                                ((mychatting_image_viewholder)viewHolder).chattime_img.setTextColor(Color.BLACK);
                                ((mychatting_image_viewholder) viewHolder).chatwatchercount_img.setTextColor(Color.YELLOW);

                                //해당  ->  채팅의 유저 읽은수  넣어주기.
                                get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mychatting_image_viewholder)viewHolder).chatwatchercount_img);

                            }

                        }else{//두 포지션  시간이 다를때이다.

                            ((mychatting_image_viewholder)viewHolder).chattime_img.setText(time);//다음 메세지 시간이 다르면  현재 퐂션에서 무조건 시간을 출력해야한다.
                            ((mychatting_image_viewholder) viewHolder).chatwatchercount_img.setText("");
                            ((mychatting_image_viewholder)viewHolder).chattime_img.setTextColor(Color.BLACK);
                            ((mychatting_image_viewholder) viewHolder).chatwatchercount_img.setTextColor(Color.YELLOW);

                            //해당  ->  채팅의 유저 읽은수  넣어주기.
                            get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mychatting_image_viewholder)viewHolder).chatwatchercount_img);

                        }

                    }else{//다음 포지션이  존재 하지 않는경우이다.-> 이경우에는 마지막 아이템이므로  시간이 출력된다.

                        ((mychatting_image_viewholder)viewHolder).chattime_img.setText(time);
                        ((mychatting_image_viewholder) viewHolder).chatwatchercount_img.setText("");
                        ((mychatting_image_viewholder)viewHolder).chattime_img.setTextColor(Color.BLACK);
                        ((mychatting_image_viewholder) viewHolder).chatwatchercount_img.setTextColor(Color.YELLOW);

                        //해당  ->  채팅의 유저 읽은수  넣어주기.
                        get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mychatting_image_viewholder)viewHolder).chatwatchercount_img);


                    }////if(getItemCount()-1>position){ 끝

                }else{//i 값 0 초과 아닐때 -> 연속된 채팅이 아닐때
                    /////////////////////////////////////////////////////////////////////////////////////////////////////////

                    Log.v("check", "채팅 순서 체크 : 아이템이  2개 이상인 경우,  과거시간과 현재시간이 맞지만, i체크가 0일때 즉 -> 맨처음다시 리셋");

                    //채팅 보낸 이미지 path가 null아 아닐경우
                    if(!my_image_message.equals("null")) {

                        LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) ((mychatting_image_viewholder) viewHolder).chatting_img_content.getLayoutParams();

                        layoutParams.rightMargin=dpToPx(7);
                        layoutParams.width= LinearLayout.LayoutParams.WRAP_CONTENT;
                        layoutParams.height=LinearLayout.LayoutParams.WRAP_CONTENT;
                        layoutParams.gravity= Gravity.RIGHT;
                        ((mychatting_image_viewholder) viewHolder).chatting_img_content.setMaxHeight(dpToPx(270));
                        ((mychatting_image_viewholder) viewHolder).chatting_img_content.setMaxWidth(dpToPx(270));
                        ((mychatting_image_viewholder) viewHolder).chatting_img_content.setMinimumHeight(dpToPx(140));
                        ((mychatting_image_viewholder) viewHolder).chatting_img_content.setScaleType(ImageView.ScaleType.FIT_END);
                        ((mychatting_image_viewholder) viewHolder).chatting_img_content.setLayoutParams(layoutParams);

                        layoutParams.topMargin=dpToPx(20);
                        Glide.with(context).load(url_for_image_chat).placeholder(R.drawable.img_error).override(dpToPx(160),dpToPx(160))
                                .fitCenter().error(R.drawable.img_error).into(((mychatting_image_viewholder) viewHolder).chatting_img_content);//해당 프로필 이미지 서버에서 받아와  넣어줌.

                    }


                    if(getItemCount()-1>position){

                        Date futuredate=new  SimpleDateFormat("YYYY-MM-DD HH:mm").parse(chattingtext.get(position + 1).get("date").toString());
                        String futuretime=new SimpleDateFormat("aaa hh:mm").format(futuredate);
                        String futureid=chattingtext.get(position+1).get("id").toString();

                        if(futuretime.equals(time)){//포지션 1앞 시간과  현재 포지션 시간이 같으면  -> 여기에 시간이 들어가면 안된다.

                            if(futureid.equals(useruid)){
                                ((mychatting_image_viewholder)viewHolder).chattime_img.setText("");
                                ((mychatting_image_viewholder) viewHolder).chatwatchercount_img.setText("");
                                ((mychatting_image_viewholder)viewHolder).chattime_img.setTextColor(Color.YELLOW);
                                ((mychatting_image_viewholder) viewHolder).chatwatchercount_img.setTextColor(Color.BLACK);

                                //해당  ->  채팅의 유저 읽은수  넣어주기.
                                get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mychatting_image_viewholder)viewHolder).chattime_img);

                            }else{

                                ((mychatting_image_viewholder)viewHolder).chattime_img.setText(time);
                                ((mychatting_image_viewholder) viewHolder).chatwatchercount_img.setText("");
                                ((mychatting_image_viewholder)viewHolder).chattime_img.setTextColor(Color.BLACK);
                                ((mychatting_image_viewholder) viewHolder).chatwatchercount_img.setTextColor(Color.YELLOW);

                                //해당  ->  채팅의 유저 읽은수  넣어주기.
                                get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mychatting_image_viewholder)viewHolder).chatwatchercount_img);

                            }


                        }else{

                            ((mychatting_image_viewholder)viewHolder).chattime_img.setText(time);
                            ((mychatting_image_viewholder) viewHolder).chatwatchercount_img.setText("");
                            ((mychatting_image_viewholder)viewHolder).chattime_img.setTextColor(Color.BLACK);
                            ((mychatting_image_viewholder) viewHolder).chatwatchercount_img.setTextColor(Color.YELLOW);

                            //해당  ->  채팅의 유저 읽은수  넣어주기.
                            get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mychatting_image_viewholder)viewHolder).chatwatchercount_img);

                        }
                    }else{

                        ((mychatting_image_viewholder)viewHolder).chattime_img.setText(time);
                        ((mychatting_image_viewholder) viewHolder).chatwatchercount_img.setText("");
                        ((mychatting_image_viewholder)viewHolder).chattime_img.setTextColor(Color.BLACK);
                        ((mychatting_image_viewholder) viewHolder).chatwatchercount_img.setTextColor(Color.YELLOW);

                        //해당  ->  채팅의 유저 읽은수  넣어주기.
                        get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mychatting_image_viewholder)viewHolder).chatwatchercount_img);


                    }//if(getItemCount()-1>position){ 끝

                }//i 값 0 초과 아닐때 -> 연속된 채팅이 아닐때끝

            }else{ //11-1 이전  포지션  snender와  uid가 틀리거나,  시간 이 다를때이다.  또는

                    if(pastid.equals(useruid)){//이전 아이디와  현재 유저의 아이디가  같은 경우-> 이경우는  이전 시간과 현재 시간이  같지 않은경우.

                        i=0;//시간이 안맞으므로,  i =0으로 리셋
                        Log.v("check", "채팅 순서 체크 : position 0이상이지만,  과거 시간이 현재 시간과 안맞고, 이전 유저가  나일때");



                        //채팅 보낸 이미지 path가 null아 아닐경우
                        if(!my_image_message.equals("null")) {

                            LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) ((mychatting_image_viewholder) viewHolder).chatting_img_content.getLayoutParams();

                            layoutParams.rightMargin=dpToPx(7);
                            layoutParams.width= LinearLayout.LayoutParams.WRAP_CONTENT;
                            layoutParams.height=LinearLayout.LayoutParams.WRAP_CONTENT;
                            layoutParams.gravity= Gravity.RIGHT;
                            ((mychatting_image_viewholder) viewHolder).chatting_img_content.setMaxHeight(dpToPx(270));
                            ((mychatting_image_viewholder) viewHolder).chatting_img_content.setMaxWidth(dpToPx(270));
                            ((mychatting_image_viewholder) viewHolder).chatting_img_content.setMinimumHeight(dpToPx(140));
                            ((mychatting_image_viewholder) viewHolder).chatting_img_content.setScaleType(ImageView.ScaleType.FIT_END);
                            layoutParams.topMargin=dpToPx(20);
                            ((mychatting_image_viewholder) viewHolder).chatting_img_content.setLayoutParams(layoutParams);

                            Glide.with(context).load(url_for_image_chat).placeholder(R.drawable.img_error)
                                    .override(dpToPx(160),dpToPx(160))
                                    .fitCenter().error(R.drawable.img_error).into(((mychatting_image_viewholder) viewHolder).chatting_img_content);//해당 프로필 이미지 서버에서 받아와  넣어줌.

                        }


                        if(getItemCount()-1>position){

                            Date futuredate=new  SimpleDateFormat("YYYY-MM-DD HH:mm").parse(chattingtext.get(position + 1).get("date").toString());
                            String futuretime=new SimpleDateFormat("aaa hh:mm").format(futuredate);
                            String futureid=chattingtext.get(position+1).get("id").toString();

                            if(futuretime.equals(time)){//포지션 1앞 시간과  현재 포지션 시간이 같으면  -> 여기에 시간이 들어가면 안된다.

                                if(futureid.equals(useruid)){
                                    ((mychatting_image_viewholder)viewHolder).chattime_img.setText("");
                                    ((mychatting_image_viewholder) viewHolder).chatwatchercount_img.setText("");
                                    ((mychatting_image_viewholder)viewHolder).chattime_img.setTextColor(Color.YELLOW);
                                    ((mychatting_image_viewholder) viewHolder).chatwatchercount_img.setTextColor(Color.BLACK);
                                    //해당  ->  채팅의 유저 읽은수  넣어주기.
                                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mychatting_image_viewholder)viewHolder).chattime_img);


                                }else{

                                    ((mychatting_image_viewholder)viewHolder).chattime_img.setText(time);
                                    ((mychatting_image_viewholder)viewHolder).chatwatchercount_img.setText("");
                                    ((mychatting_image_viewholder)viewHolder).chattime_img.setTextColor(Color.BLACK);
                                    ((mychatting_image_viewholder)viewHolder).chatwatchercount_img.setTextColor(Color.YELLOW);
                                    //해당  ->  채팅의 유저 읽은수  넣어주기.
                                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mychatting_image_viewholder)viewHolder).chatwatchercount_img);


                                }


                            }else{

                                ((mychatting_image_viewholder)viewHolder).chattime_img.setText(time);
                                ((mychatting_image_viewholder) viewHolder).chatwatchercount_img.setText("");
                                ((mychatting_image_viewholder)viewHolder).chattime_img.setTextColor(Color.BLACK);
                                ((mychatting_image_viewholder) viewHolder).chatwatchercount_img.setTextColor(Color.YELLOW);
                                //해당  ->  채팅의 유저 읽은수  넣어주기.
                                get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mychatting_image_viewholder)viewHolder).chatwatchercount_img);

                            }
                        }else{

                            ((mychatting_image_viewholder)viewHolder).chattime_img.setText(time);
                            ((mychatting_image_viewholder) viewHolder).chatwatchercount_img.setText("");
                            ((mychatting_image_viewholder)viewHolder).chattime_img.setTextColor(Color.BLACK);
                            ((mychatting_image_viewholder) viewHolder).chatwatchercount_img.setTextColor(Color.YELLOW);
                            //해당  ->  채팅의 유저 읽은수  넣어주기.
                            get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mychatting_image_viewholder)viewHolder).chatwatchercount_img);

                        }

                    }else{//이전 아이디와  현재 아이디가  안맞을때,

                        i=0;//이경우도 역시  채팅이  나로  연속되지 않기 때문에  i=0으로  리셋
                        Log.v("check", "채팅 순서 체크 : position 0이상이지만,  과거 시간이 현재 시간과 안맞고, 이전 유저가 내가아닐때,");

                        //채팅 보낸 이미지 path가 null아 아닐경우

                        if(!my_image_message.equals("null")) {

                            LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) ((mychatting_image_viewholder) viewHolder).chatting_img_content.getLayoutParams();

                            layoutParams.rightMargin=dpToPx(7);
                            layoutParams.width= LinearLayout.LayoutParams.WRAP_CONTENT;
                            layoutParams.height=LinearLayout.LayoutParams.WRAP_CONTENT;
                            layoutParams.gravity= Gravity.RIGHT;
                            ((mychatting_image_viewholder) viewHolder).chatting_img_content.setMaxHeight(dpToPx(270));
                            ((mychatting_image_viewholder) viewHolder).chatting_img_content.setMaxWidth(dpToPx(270));
                            ((mychatting_image_viewholder) viewHolder).chatting_img_content.setMinimumHeight(dpToPx(140));
                            ((mychatting_image_viewholder) viewHolder).chatting_img_content.setScaleType(ImageView.ScaleType.FIT_END);

                            layoutParams.topMargin=dpToPx(20);
                            ((mychatting_image_viewholder) viewHolder).chatting_img_content.setLayoutParams(layoutParams);
                            Glide.with(context).load(url_for_image_chat).placeholder(R.drawable.img_error)  .override(dpToPx(160),dpToPx(160))
                                    .fitCenter().error(R.drawable.img_error).into(((mychatting_image_viewholder) viewHolder).chatting_img_content);//해당 프로필 이미지 서버에서 받아와  넣어줌.

                        }


                        if(getItemCount()-1>position){

                            Date futuredate=new  SimpleDateFormat("YYYY-MM-DD HH:mm").parse(chattingtext.get(position + 1).get("date").toString());
                            String futuretime=new SimpleDateFormat("aaa hh:mm").format(futuredate);
                            String futureid=chattingtext.get(position+1).get("id").toString();

                            if(futuretime.equals(time)){//포지션 1앞 시간과  현재 포지션 시간이 같으면  -> 여기에 시간이 들어가면 안된다.

                                if(futureid.equals(useruid)){
                                    ((mychatting_image_viewholder)viewHolder).chattime_img.setText("");
                                    ((mychatting_image_viewholder) viewHolder).chatwatchercount_img.setText("");
                                    ((mychatting_image_viewholder)viewHolder).chattime_img.setTextColor(Color.YELLOW);
                                    ((mychatting_image_viewholder) viewHolder).chatwatchercount_img.setTextColor(Color.BLACK);
                                    //해당  ->  채팅의 유저 읽은수  넣어주기.
                                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mychatting_image_viewholder)viewHolder).chattime_img);



                                }else{

                                    ((mychatting_image_viewholder)viewHolder).chattime_img.setText(time);
                                    ((mychatting_image_viewholder) viewHolder).chatwatchercount_img.setText("");
                                    ((mychatting_image_viewholder)viewHolder).chattime_img.setTextColor(Color.BLACK);
                                    ((mychatting_image_viewholder) viewHolder).chatwatchercount_img.setTextColor(Color.YELLOW);
                                    //해당  ->  채팅의 유저 읽은수  넣어주기.
                                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mychatting_image_viewholder)viewHolder).chatwatchercount_img);

                                }


                            }else{

                                ((mychatting_image_viewholder)viewHolder).chattime_img.setText(time);
                                ((mychatting_image_viewholder) viewHolder).chatwatchercount_img.setText("");
                                ((mychatting_image_viewholder)viewHolder).chattime_img.setTextColor(Color.BLACK);
                                ((mychatting_image_viewholder) viewHolder).chatwatchercount_img.setTextColor(Color.YELLOW);

                                //해당  ->  채팅의 유저 읽은수  넣어주기.
                                get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mychatting_image_viewholder)viewHolder).chatwatchercount_img);

                            }

                        }else{

                            ((mychatting_image_viewholder)viewHolder).chattime_img.setText(time);
                            ((mychatting_image_viewholder) viewHolder).chatwatchercount_img.setText("");
                            ((mychatting_image_viewholder)viewHolder).chattime_img.setTextColor(Color.BLACK);
                            ((mychatting_image_viewholder) viewHolder).chatwatchercount_img.setTextColor(Color.YELLOW);

                            //해당  ->  채팅의 유저 읽은수  넣어주기.
                            get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mychatting_image_viewholder)viewHolder).chatwatchercount_img);

                        }//if(getItemCount()-1>position){끝


                    }//이전 아이디와 현재 아이디 안맞을때 끝

                }//이전 포지션이랑  시간 또는 아이디가  또는 둘다 다를때.

           }//포지션 0 초과 일때 끝,
            else{//현재 포지션이 0초과가 아닐때  -> 즉  맨처음  채팅으로 이미지를 올릴때  조건



                i=0;//그전에 연속된게 없으므로 i=0이다.
                Log.v("check", "채팅 순서 체크 : position 0일때");



                if(!my_image_message.equals("null")) {//이미지 path값이  null이 아니라면,  이미지를 넣어준다.

                    LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) ((mychatting_image_viewholder) viewHolder).chatting_img_content.getLayoutParams();
                    layoutParams.rightMargin=dpToPx(7);
                    layoutParams.width= LinearLayout.LayoutParams.WRAP_CONTENT;
                    layoutParams.height=LinearLayout.LayoutParams.WRAP_CONTENT;
                    layoutParams.gravity= Gravity.RIGHT;
                    ((mychatting_image_viewholder) viewHolder).chatting_img_content.setMaxHeight(dpToPx(270));
                    ((mychatting_image_viewholder) viewHolder).chatting_img_content.setMaxWidth(dpToPx(270));
                    ((mychatting_image_viewholder) viewHolder).chatting_img_content.setMinimumHeight(dpToPx(140));
                    ((mychatting_image_viewholder) viewHolder).chatting_img_content.setScaleType(ImageView.ScaleType.FIT_END);

                    layoutParams.topMargin=dpToPx(20);
                    ((mychatting_image_viewholder) viewHolder).chatting_img_content.setLayoutParams(layoutParams);
                    Glide.with(context).load(url_for_image_chat).placeholder(R.drawable.img_error)  .override(dpToPx(160),dpToPx(160))
                            .fitCenter().error(R.drawable.img_error).into(((mychatting_image_viewholder) viewHolder).chatting_img_content);//해당 프로필 이미지 서버에서 받아와  넣어줌.

                }

                if(getItemCount()>1){

                    Date futuredate=new  SimpleDateFormat("YYYY-MM-DD HH:mm").parse(chattingtext.get(position + 1).get("date").toString());
                    String futuretime=new SimpleDateFormat("aaa hh:mm").format(futuredate);
                    String futureid=chattingtext.get(position+1).get("id").toString();

                    if(futuretime.equals(time)){//포지션 1앞 시간과  현재 포지션 시간이 같으면  -> 여기에 시간이 들어가면 안된다.

                        if(futureid.equals(useruid)){

                            ((mychatting_image_viewholder)viewHolder).chattime_img.setText("");
                            ((mychatting_image_viewholder) viewHolder).chatwatchercount_img.setText("");

                            ((mychatting_image_viewholder)viewHolder).chattime_img.setTextColor(Color.YELLOW);
                            ((mychatting_image_viewholder) viewHolder).chatwatchercount_img.setTextColor(Color.BLACK);

                            //해당  ->  채팅의 유저 읽은수  넣어주기.
                            get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mychatting_image_viewholder)viewHolder).chattime_img);

                        }else{

                            ((mychatting_image_viewholder)viewHolder).chattime_img.setText(time);
                            ((mychatting_image_viewholder) viewHolder).chatwatchercount_img.setText("");

                            ((mychatting_image_viewholder)viewHolder).chattime_img.setTextColor(Color.BLACK);
                            ((mychatting_image_viewholder) viewHolder).chatwatchercount_img.setTextColor(Color.YELLOW);

                            //해당  ->  채팅의 유저 읽은수  넣어주기.
                            get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mychatting_image_viewholder)viewHolder).chatwatchercount_img);

                        }

                    }else{

                        ((mychatting_image_viewholder)viewHolder).chattime_img.setText(time);
                        ((mychatting_image_viewholder) viewHolder).chatwatchercount_img.setText("");
                        ((mychatting_image_viewholder)viewHolder).chattime_img.setTextColor(Color.BLACK);
                        ((mychatting_image_viewholder) viewHolder).chatwatchercount_img.setTextColor(Color.YELLOW);

                        //해당  ->  채팅의 유저 읽은수  넣어주기.
                        get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mychatting_image_viewholder)viewHolder).chatwatchercount_img);

                    }

                }else{

                    ((mychatting_image_viewholder)viewHolder).chattime_img.setText(time);
                    ((mychatting_image_viewholder) viewHolder).chatwatchercount_img.setText("");
                    ((mychatting_image_viewholder)viewHolder).chattime_img.setTextColor(Color.BLACK);
                    ((mychatting_image_viewholder) viewHolder).chatwatchercount_img.setTextColor(Color.YELLOW);

                    //해당  ->  채팅의 유저 읽은수  넣어주기.
                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mychatting_image_viewholder)viewHolder).chatwatchercount_img);

                }//    if(getItemCount()>1){ 끝

            }

        }//내가 이미지를 보냈을때  끝.
        else if(viewHolder.getItemViewType()==9){//상대가 이미지를 보냈을때

            if(user_job_position.equals("t")){//유저의  position이  선생님일떄,

                username=username+" teacher";//선생님을  넣어준다.
                ((chatting_image_viewholder) viewHolder).textviewforusername.setTextColor(Color.GREEN);//선생님 이름은  초록 색으로 설정해줌.

            }else if(user_job_position.equals("s")){//유저의 position이 학생일떄.

                ((chatting_image_viewholder) viewHolder).textviewforusername.setTextColor(Color.BLACK);//학생은  이름 그대로  검정색으로 보내줌.

            }//유저의  position이  학생일떄 끝.

            String image_message=chattingtext.get(position).get("message").toString();

            //URL -> 이미지 프로필 URL받아오기
            URL url_for_image_chat = new URL("http://13.209.249.1/" + image_message);

            Log.v("check_img_chatting", image_message);
            Log.v("check", "ChattingDataRecyclerViewAdapter 의   viewHolder.getItemType==9 일때 실행됨-> 상대가 보낸 이미지 메세지 타입");

            int i=0;//해당 타입의 메세지 (남이 보낸 메세지 타입)가  연속적으로  실행되는지를 체크 하기 위한  값

            if(position>0) {// 포지션이  0초과 일때-> 즉  리사이클러뷰  아이템이  2개 이상 일때이다. 이전 데이터가  있으므로,  같은 사람이  반복적으로  채팅을 보낼때  gui처리를 할수 있다

                //현재 포지션 이전 포지션의 날짜.
                Date pastdate = new SimpleDateFormat("YYYY-MM-DD HH:mm").parse(chattingtext.get(position - 1).get("date").toString());

                //위  이전 포지션 날짜  time으로  변형 시켜줌.
                String pasttime = new SimpleDateFormat("aaa hh:mm").format(pastdate);

                //현재 포지션  이전 포지션의  채팅메세지  보낸 유저의  uid
                pastid = chattingtext.get(position - 1).get("id").toString();

                //현재 포지션에서  sender 의 uid
                senderuid = chattingtext.get(position).get("id").toString();

                //이전 포지션의 viewtype
                String pastviewtype = chattingtext.get(position - 1).get("viewtype").toString();


                //현재 포지션 이전 텍스트의  시간과 현재시간이  같고, 포지션 이전 아이디와  해당 유저의 아이디가 같을때이다.+ 그리고 viewtype이 3일 경우와  4일 경우이다.
                if((pasttime.equals(time) && pastid.equals(senderuid) && pastviewtype.equals("3")) || (pasttime.equals(time) && pastid.equals(senderuid) && pastviewtype.equals("4")) ||(pasttime.equals(time) && pastid.equals(senderuid) && pastviewtype.equals("5"))){
                //1-19 시작

                    i=i+1;//이전 포지션의 채팅메세지의 시간과  유저가 현재 유저 본인과  같을 경우-> i를  1씩 계속 올려줘서 -> 계속 같은 유저가  연속으로  채팅을 보내는건지 확인한다.

                    if(i>0) {//i 값이  0초과  ->  같은 유저의 채팅이 연속으로 진행될때이다.

                        //이경우 -> 이전  채팅메세지 유저와 같은 유저가  연속적으로 메세지를 보낸것이므로,  normal ui형태로 메세지 ui를 바꿔준다.
                        Log.v("check", "채팅 순서 체크 : 아이템은 2개 이상인상태이고,  이전 포지션 채팅시간과 현재 포지션 채팅시간이 맞고, i체크가 0이상 일때 즉 -> 노말 채팅ui나옴");

                        LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) ((chatting_image_viewholder) viewHolder).chatting_img_content.getLayoutParams();

                        layoutParams.leftMargin=200;
                        layoutParams.width= LinearLayout.LayoutParams.WRAP_CONTENT;
                        layoutParams.height=LinearLayout.LayoutParams.WRAP_CONTENT;
                        layoutParams.gravity= Gravity.LEFT;
                        ((chatting_image_viewholder) viewHolder).chatting_img_content.setMaxHeight(dpToPx(270));
                        ((chatting_image_viewholder) viewHolder).chatting_img_content.setMaxWidth(dpToPx(270));
                        //((chatting_image_viewholder) viewHolder).chatting_img_content.setMinimumHeight(dpToPx(140));
                        ((chatting_image_viewholder) viewHolder).chatting_img_content.setLayoutParams(layoutParams);
                        ((chatting_image_viewholder) viewHolder).chatting_img_content.setScaleType(ImageView.ScaleType.FIT_START);


                        //보낸 사람 프로필 이미지
                        ((chatting_image_viewholder) viewHolder).imageViewforuserprofile.setVisibility(View.GONE);//처음 채팅이므로  이미지뷰도 GONE

                        //보낸사람  이름 담기는 텍스트뷰
                        ((chatting_image_viewholder) viewHolder).textviewforusername.setVisibility(View.GONE);//처음 채팅이 아니므로  보낸 사람 이름 GONE
                        ((chatting_image_viewholder) viewHolder).textviewforusername.setText("");//채팅 이름도 안넣어줘도됨..

                        if(!image_message.equals("null")) {//이미지 path값이  null이 아니라면,  이미지를 넣어준다.


                            Glide.with(context).load(url_for_image_chat)
                                    .override(dpToPx(160),dpToPx(160))
                                    .fitCenter().into(((chatting_image_viewholder) viewHolder).chatting_img_content);//해당 프로필 이미지 서버에서 받아와  넣어줌.

                        }

                        //i가  0이상인 경우에서 -> 리사이클러뷰 아이템수 -1 이  현재 포지션 보다 클때이다.
                        // -> 이거는  현재 포지션 다음 포지션이 존재한가 아닌가의 여부를 알기 위해서이다
                        //결국  시간이랑  안읽은 사람 수 보여줄지 아닐지 판별용임.
                        if(getItemCount()-1>position){

                            //현재 포지션 다음  포지션의 채팅메세지의  날짜
                            Date futuredate=new  SimpleDateFormat("YYYY-MM-DD HH:mm").parse(chattingtext.get(position + 1).get("date").toString());

                            //다음 포지션 날짜 -> 시간 형태로
                            String futuretime=new SimpleDateFormat("aaa hh:mm").format(futuredate);

                            //다음 포지션  채팅 메세지  보낸 유저의 uid
                            String futureid=chattingtext.get(position+1).get("id").toString();

                            if(futuretime.equals(time)){//포지션 1앞 시간과  현재 포지션 시간이 같을때이다.

                                if(futureid.equals(senderuid)){//포지션 1앞과  현재 포지션 시간이 같고,  uid가  두포지션 모두  똑같을 때이다.

                                    //채팅 본사람 숫자 텍스트뷰
                                    //cargo.get(chatorder);
                                    ((chatting_image_viewholder) viewHolder).chatwatchercount.setText("");//일단  1로 해놓음.-> 나중에 처리해야됨.
                                    ((chatting_image_viewholder) viewHolder).chatpresenttime.setText("");//처음 채팅이므로  채팅 보내진 시간도 보내준다.//여기에다가 -> 본사람 숫자 넣어줌.
                                    ((chatting_image_viewholder) viewHolder).chatpresenttime.setTextColor(Color.YELLOW);
                                    ((chatting_image_viewholder) viewHolder).chatwatchercount.setTextColor(Color.BLACK);

                                    // 해당  ->  채팅의 유저 읽은수  넣어주기.
                                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatting_image_viewholder) viewHolder).chatpresenttime);




                                }else{//두 포지션 시간이 같고  uid가 틀릴때 즉  다음 메세지가  상대 메세지 일때는  현재 포지션에서  시간을 출려해야한다.


                                    ((chatting_image_viewholder) viewHolder).chatpresenttime.setText(time);//현재 포지션 시간 출력
                                    ((chatting_image_viewholder) viewHolder).chatwatchercount.setText("");//현재 포지션 채팅 메세지  안읽은 유저의 숫자  보여주기.
                                    ((chatting_image_viewholder) viewHolder).chatpresenttime.setTextColor(Color.BLACK);
                                    ((chatting_image_viewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                                    //해당  ->  채팅의 유저 읽은수  넣어주기.
                                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatting_image_viewholder) viewHolder).chatwatchercount);

                                }

                            }else{//두 포지션  시간이 다를때이다.

                                ((chatting_image_viewholder) viewHolder).chatpresenttime.setText(time);//다음 메세지 시간이 다르면  현재 퐂션에서 무조건 시간을 출력해야한다.
                                ((chatting_image_viewholder) viewHolder).chatwatchercount.setText("");
                                ((chatting_image_viewholder) viewHolder).chatpresenttime.setTextColor(Color.BLACK);
                                ((chatting_image_viewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                                //해당  ->  채팅의 유저 읽은수  넣어주기.
                                get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatting_image_viewholder) viewHolder).chatwatchercount);


                            }

                        }else{//다음 포지션이  존재 하지 않는경우이다.-> 이경우에는 마지막 아이템이므로  시간이 출력된다.

                            ((chatting_image_viewholder) viewHolder).chatpresenttime.setText(time);
                            ((chatting_image_viewholder) viewHolder).chatwatchercount.setText("");
                            ((chatting_image_viewholder) viewHolder).chatpresenttime.setTextColor(Color.BLACK);
                            ((chatting_image_viewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                            //해당  ->  채팅의 유저 읽은수  넣어주기.
                            get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatting_image_viewholder) viewHolder).chatwatchercount);



                        }////if(getItemCount()-1>position){ 끝

                    } else {//이전 포지션과 같은 시간에서  i 값이  0초과가 아닐때  -> 이때는  같은 유저으 채팅이 연속적으로 진행되지 않는다.
                        Log.v("check", "채팅 순서 체크 : 아이템이  2개 이상인 경우,  과거시간과 현재시간이 맞지만, i체크가 0일때 즉 -> 맨처음다시 리셋");

                        LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) ((chatting_image_viewholder) viewHolder).chatting_img_content.getLayoutParams();

                        layoutParams.leftMargin=50;
                        layoutParams.width= LinearLayout.LayoutParams.WRAP_CONTENT;
                        layoutParams.height=LinearLayout.LayoutParams.WRAP_CONTENT;
                        layoutParams.gravity= Gravity.LEFT;
                        ((chatting_image_viewholder) viewHolder).chatting_img_content.setMaxHeight(dpToPx(270));
                        ((chatting_image_viewholder) viewHolder).chatting_img_content.setMaxWidth(dpToPx(270));
                        //((chatting_image_viewholder) viewHolder).chatting_img_content.setMinimumHeight(dpToPx(140));
                        ((chatting_image_viewholder) viewHolder).chatting_img_content.setLayoutParams(layoutParams);
                        ((chatting_image_viewholder) viewHolder).chatting_img_content.setScaleType(ImageView.ScaleType.FIT_START);
                        ((chatting_image_viewholder) viewHolder).chatting_img_content.setLayoutParams(layoutParams);

                        if(!image_message.equals("null")) {//이미지 path값이  null이 아니라면,  이미지를 넣어준다.

                            Glide.with(context)
                                    .load(url_for_image_chat)
                                    .placeholder(R.drawable.img_error)
                                    .error(R.drawable.img_error)
                                    .override(dpToPx(160),dpToPx(160))
                                    .fitCenter()
                                    .into(((chatting_image_viewholder) viewHolder).chatting_img_content);//해당 프로필 이미지 서버에서 받아와  넣어줌.

                        }

                        //보낸 사람 프로필 이미지
                        ((chatting_image_viewholder) viewHolder).imageViewforuserprofile.setVisibility(View.VISIBLE);//리셋이니까 보낸사람 프로필 사진도 나옴.

                        //프로필 이미지 path가 null아 아닐경우
                        if(!imagepath.equals("null")) {
                            Glide.with(context).load(url).into(((chatting_image_viewholder) viewHolder).imageViewforuserprofile);//해당 프로필 이미지 서버에서 받아와  넣어줌.
                        }else{
                            ((chatting_image_viewholder) viewHolder).imageViewforuserprofile.setImageResource(R.mipmap.ic_launcher);
                        }

                        //보낸사람  이름 담기는 텍스트뷰
                        ((chatting_image_viewholder) viewHolder).textviewforusername.setVisibility(View.VISIBLE);//리셋이니까 이름 텍스트뷰도 나와야됨.
                        ((chatting_image_viewholder) viewHolder).textviewforusername.setText(username);//리셋이어서 이름 넣어줌


                        //i가  0이상인 경우에서 -> 리사이클러뷰 아이템수 -1 이  현재 포지션 보다 클때이다.
                        // -> 이거는  현재 포지션 다음 포지션이 존재한가 아닌가의 여부를 알기 위해서이다
                        //결국  시간이랑  안읽은 사람 수 보여줄지 아닐지 판별용임.
                        if(getItemCount()-1>position){

                            //현재 포지션 다음  포지션의 채팅메세지의  날짜
                            Date futuredate=new  SimpleDateFormat("YYYY-MM-DD HH:mm").parse(chattingtext.get(position + 1).get("date").toString());

                            //다음 포지션 날짜 -> 시간 형태로
                            String futuretime=new SimpleDateFormat("aaa hh:mm").format(futuredate);

                            //다음 포지션  채팅 메세지  보낸 유저의 uid
                            String futureid=chattingtext.get(position+1).get("id").toString();

                            if(futuretime.equals(time)){//포지션 1앞 시간과  현재 포지션 시간이 같을때이다.

                                if(futureid.equals(senderuid)){//포지션 1앞과  현재 포지션 시간이 같고,  uid가  두포지션 모두  똑같을 때이다.

                                    //채팅 본사람 숫자 텍스트뷰
                                    ((chatting_image_viewholder) viewHolder).chatwatchercount.setText("");//일단  1로 해놓음.-> 나중에 처리해야됨.
                                    ((chatting_image_viewholder) viewHolder).chatpresenttime.setText("");//처음 채팅이므로  채팅 보내진 시간도 보내준다.// 여기에다가 -> 본사람 숫자 넣어줌.
                                    ((chatting_image_viewholder) viewHolder).chatpresenttime.setTextColor(Color.YELLOW);
                                    ((chatting_image_viewholder) viewHolder).chatwatchercount.setTextColor(Color.BLACK);

                                    //해당  ->  채팅의 유저 읽은수  넣어주기.
                                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatting_image_viewholder) viewHolder).chatpresenttime);



                                }else{//두 포지션 시간이 같고  uid가 틀릴때 즉  다음 메세지가  상대 메세지 일때는  현재 포지션에서  시간을 출려해야한다.


                                    ((chatting_image_viewholder) viewHolder).chatpresenttime.setText(time);//현재 포지션 시간 출력
                                    ((chatting_image_viewholder) viewHolder).chatwatchercount.setText("");//현재 포지션 채팅 메세지  안읽은 유저의 숫자  보여주기.
                                    ((chatting_image_viewholder) viewHolder).chatpresenttime.setTextColor(Color.BLACK);
                                    ((chatting_image_viewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                                    //해당  ->  채팅의 유저 읽은수  넣어주기.
                                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatting_image_viewholder) viewHolder).chatwatchercount);

                                }

                            }else{//두 포지션  시간이 다를때이다.

                                ((chatting_image_viewholder) viewHolder).chatpresenttime.setText(time);//다음 메세지 시간이 다르면  현재 퐂션에서 무조건 시간을 출력해야한다.
                                ((chatting_image_viewholder) viewHolder).chatwatchercount.setText("");
                                ((chatting_image_viewholder) viewHolder).chatpresenttime.setTextColor(Color.BLACK);
                                ((chatting_image_viewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                                //해당  ->  채팅의 유저 읽은수  넣어주기.
                                get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatting_image_viewholder) viewHolder).chatwatchercount);

                            }

                        }else{//다음 포지션이  존재 하지 않는경우이다.-> 이경우에는 마지막 아이템이므로  시간이 출력된다.

                            ((chatting_image_viewholder) viewHolder).chatpresenttime.setText(time);
                            ((chatting_image_viewholder) viewHolder).chatwatchercount.setText("");
                            ((chatting_image_viewholder) viewHolder).chatpresenttime.setTextColor(Color.BLACK);
                            ((chatting_image_viewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                            //해당  ->  채팅의 유저 읽은수  넣어주기.
                            get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatting_image_viewholder) viewHolder).chatwatchercount);

                        }////if(getItemCount()-1>position){ 끝

                    }//i 값  0 초과 아닐때 끝


                //1-19 끝
                }else{//위 시간이 같고,  아이디가 같고 viewtype이 3 또는 4일때의 조건을  충족하지 못한경우


                    LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) ((chatting_image_viewholder) viewHolder).chatting_img_content.getLayoutParams();

                    layoutParams.leftMargin=50;
                    layoutParams.width= LinearLayout.LayoutParams.WRAP_CONTENT;
                    layoutParams.height=LinearLayout.LayoutParams.WRAP_CONTENT;
                    layoutParams.gravity= Gravity.LEFT;
                    ((chatting_image_viewholder) viewHolder).chatting_img_content.setMaxHeight(dpToPx(270));
                    ((chatting_image_viewholder) viewHolder).chatting_img_content.setMaxWidth(dpToPx(270));
                    //((chatting_image_viewholder) viewHolder).chatting_img_content.setMinimumHeight(dpToPx(140));
                    ((chatting_image_viewholder) viewHolder).chatting_img_content.setLayoutParams(layoutParams);
                    ((chatting_image_viewholder) viewHolder).chatting_img_content.setScaleType(ImageView.ScaleType.FIT_START);
                    ((chatting_image_viewholder) viewHolder).chatting_img_content.setLayoutParams(layoutParams);

                    if(pastid.equals(senderuid)) {//이전 아이디와  현재 유저의 아이디가  같은 경우-> 이경우는  이전 시간과 현재 시간이  같지 않은경우.

                        i=0;//시간이 안맞으므로,  i =0으로 리셋


                        if(!image_message.equals("null")) {//이미지 path값이  null이 아니라면,  이미지를 넣어준다.

                            Glide.with(context).load(url_for_image_chat)
                                    .override(dpToPx(160),dpToPx(160))
                                    .fitCenter().into(((chatting_image_viewholder) viewHolder).chatting_img_content);//해당 프로필 이미지 서버에서 받아와  넣어줌.

                        }

                        //보낸 사람 프로필 이미지
                        ((chatting_image_viewholder) viewHolder).imageViewforuserprofile.setVisibility(View.VISIBLE);//처음 채팅이므로  이미지뷰를 보여준다.

                        //프로필 이미지 path가 null아 아닐경우
                        if(!imagepath.equals("null")) {
                            Glide.with(context).load(url).into(((chatting_image_viewholder) viewHolder).imageViewforuserprofile);//해당 프로필 이미지 서버에서 받아와  넣어줌.
                        }else{
                            ((chatting_image_viewholder) viewHolder).imageViewforuserprofile.setImageResource(R.mipmap.ic_launcher);
                        }


                        //보낸사람  이름 담기는 텍스트뷰
                        ((chatting_image_viewholder) viewHolder).textviewforusername.setVisibility(View.VISIBLE);//처음 채팅이므로 보낸사람 이름을 보여준다.
                        ((chatting_image_viewholder) viewHolder).textviewforusername.setText(username);//채팅 이름 닫아줌.

                        //i가  0이상인 경우에서 -> 리사이클러뷰 아이템수 -1 이  현재 포지션 보다 클때이다.
                        // -> 이거는  현재 포지션 다음 포지션이 존재한가 아닌가의 여부를 알기 위해서이다
                        //결국  시간이랑  안읽은 사람 수 보여줄지 아닐지 판별용임.
                        if(getItemCount()-1>position){

                            //현재 포지션 다음  포지션의 채팅메세지의  날짜
                            Date futuredate=new  SimpleDateFormat("YYYY-MM-DD HH:mm").parse(chattingtext.get(position + 1).get("date").toString());

                            //다음 포지션 날짜 -> 시간 형태로
                            String futuretime=new SimpleDateFormat("aaa hh:mm").format(futuredate);

                            //다음 포지션  채팅 메세지  보낸 유저의 uid
                            String futureid=chattingtext.get(position+1).get("id").toString();

                            if(futuretime.equals(time)){//포지션 1앞 시간과  현재 포지션 시간이 같을때이다.

                                if(futureid.equals(senderuid)){//포지션 1앞과  현재 포지션 시간이 같고,  uid가  두포지션 모두  똑같을 때이다.

                                    //채팅 본사람 숫자 텍스트뷰

                                    ((chatting_image_viewholder) viewHolder).chatwatchercount.setText("");//일단  1로 해놓음.-> 나중에 처리해야됨.
                                    ((chatting_image_viewholder) viewHolder).chatpresenttime.setText("");//처음 채팅이므로  채팅 보내진 시간도 보내준다.여기에다가 -> 본사람 숫자 넣어줌.
                                    ((chatting_image_viewholder) viewHolder).chatpresenttime.setTextColor(Color.YELLOW);
                                    ((chatting_image_viewholder) viewHolder).chatwatchercount.setTextColor(Color.BLACK);

                                    //해당  ->  채팅의 유저 읽은수  넣어주기.
                                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatting_image_viewholder) viewHolder).chatpresenttime);

                                }else{//두 포지션 시간이 같고  uid가 틀릴때 즉  다음 메세지가  상대 메세지 일때는  현재 포지션에서  시간을 출려해야한다.


                                    ((chatting_image_viewholder) viewHolder).chatpresenttime.setText(time);//현재 포지션 시간 출력
                                    ((chatting_image_viewholder) viewHolder).chatwatchercount.setText("");//현재 포지션 채팅 메세지  안읽은 유저의 숫자  보여주기.
                                    ((chatting_image_viewholder) viewHolder).chatpresenttime.setTextColor(Color.BLACK);
                                    ((chatting_image_viewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                                    //해당  ->  채팅의 유저 읽은수  넣어주기.
                                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatting_image_viewholder) viewHolder).chatwatchercount);


                                }

                            }else{//두 포지션  시간이 다를때이다.

                                ((chatting_image_viewholder) viewHolder).chatpresenttime.setText(time);//다음 메세지 시간이 다르면  현재 퐂션에서 무조건 시간을 출력해야한다.
                                ((chatting_image_viewholder) viewHolder).chatwatchercount.setText("");
                                ((chatting_image_viewholder) viewHolder).chatpresenttime.setTextColor(Color.BLACK);
                                ((chatting_image_viewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                                //해당  ->  채팅의 유저 읽은수  넣어주기.
                                get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatting_image_viewholder) viewHolder).chatwatchercount);

                            }

                        }else{//다음 포지션이  존재 하지 않는경우이다.-> 이경우에는 마지막 아이템이므로  시간이 출력된다.

                            ((chatting_image_viewholder) viewHolder).chatpresenttime.setText(time);
                            ((chatting_image_viewholder) viewHolder).chatwatchercount.setText("");
                            ((chatting_image_viewholder) viewHolder).chatpresenttime.setTextColor(Color.BLACK);
                            ((chatting_image_viewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                            //해당  ->  채팅의 유저 읽은수  넣어주기.
                            get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatting_image_viewholder) viewHolder).chatwatchercount);


                        }////if(getItemCount()-1>position){ 끝


                    }else{//이전 아이디와 현재 유저 아이디가  안같을 경우.

                        i=0;//시간이 안맞으므로,  i =0으로 리셋

                        if(!image_message.equals("null")) {//이미지 path값이  null이 아니라면,  이미지를 넣어준다.

                            Glide.with(context).load(url_for_image_chat)
                                    .override(dpToPx(160),dpToPx(160))
                                    .fitCenter().into(((chatting_image_viewholder) viewHolder).chatting_img_content);//해당 프로필 이미지 서버에서 받아와  넣어줌.

                        }

                        //보낸 사람 프로필 이미지
                        ((chatting_image_viewholder) viewHolder).imageViewforuserprofile.setVisibility(View.VISIBLE);//처음 채팅이므로  이미지뷰를 보여준다.

                        //프로필 이미지 path가 null아 아닐경우
                        if(!imagepath.equals("null")) {
                            Glide.with(context).load(url).into(((chatting_image_viewholder) viewHolder).imageViewforuserprofile);//해당 프로필 이미지 서버에서 받아와  넣어줌.

                        }else{

                            ((chatting_image_viewholder) viewHolder).imageViewforuserprofile.setImageResource(R.mipmap.ic_launcher);
                        }


                        //보낸사람  이름 담기는 텍스트뷰
                        ((chatting_image_viewholder) viewHolder).textviewforusername.setVisibility(View.VISIBLE);//처음 채팅이므로 보낸사람 이름을 보여준다.
                        ((chatting_image_viewholder) viewHolder).textviewforusername.setText(username);//채팅 이름 닫아줌.

                        //i가  0이상인 경우에서 -> 리사이클러뷰 아이템수 -1 이  현재 포지션 보다 클때이다.
                        // -> 이거는  현재 포지션 다음 포지션이 존재한가 아닌가의 여부를 알기 위해서이다
                        //결국  시간이랑  안읽은 사람 수 보여줄지 아닐지 판별용임.
                        if(getItemCount()-1>position){

                            //현재 포지션 다음  포지션의 채팅메세지의  날짜
                            Date futuredate=new  SimpleDateFormat("YYYY-MM-DD HH:mm").parse(chattingtext.get(position + 1).get("date").toString());

                            //다음 포지션 날짜 -> 시간 형태로
                            String futuretime=new SimpleDateFormat("aaa hh:mm").format(futuredate);

                            //다음 포지션  채팅 메세지  보낸 유저의 uid
                            String futureid=chattingtext.get(position+1).get("id").toString();

                            if(futuretime.equals(time)){//포지션 1앞 시간과  현재 포지션 시간이 같을때이다.

                                if(futureid.equals(senderuid)){//포지션 1앞과  현재 포지션 시간이 같고,  uid가  두포지션 모두  똑같을 때이다.

                                    //채팅 본사람 숫자 텍스트뷰

                                    ((chatting_image_viewholder) viewHolder).chatwatchercount.setText("");//일단  1로 해놓음.-> 나중에 처리해야됨.
                                    ((chatting_image_viewholder) viewHolder).chatpresenttime.setText("");//처음 채팅이므로  채팅 보내진 시간도 보내준다.여기에다가 -> 본사람 숫자 넣어줌.
                                    ((chatting_image_viewholder) viewHolder).chatpresenttime.setTextColor(Color.YELLOW);
                                    ((chatting_image_viewholder) viewHolder).chatwatchercount.setTextColor(Color.BLACK);

                                    //해당  ->  채팅의 유저 읽은수  넣어주기.
                                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatting_image_viewholder) viewHolder).chatpresenttime);


                                }else{//두 포지션 시간이 같고  uid가 틀릴때 즉  다음 메세지가  상대 메세지 일때는  현재 포지션에서  시간을 출려해야한다.


                                    ((chatting_image_viewholder) viewHolder).chatpresenttime.setText(time);//현재 포지션 시간 출력
                                    ((chatting_image_viewholder) viewHolder).chatwatchercount.setText("");//현재 포지션 채팅 메세지  안읽은 유저의 숫자  보여주기.
                                    ((chatting_image_viewholder) viewHolder).chatpresenttime.setTextColor(Color.BLACK);
                                    ((chatting_image_viewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                                    //해당  ->  채팅의 유저 읽은수  넣어주기.
                                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatting_image_viewholder) viewHolder).chatwatchercount);

                                }

                            }else{//두 포지션  시간이 다를때이다.

                                ((chatting_image_viewholder) viewHolder).chatpresenttime.setText(time);//다음 메세지 시간이 다르면  현재 퐂션에서 무조건 시간을 출력해야한다.
                                ((chatting_image_viewholder) viewHolder).chatwatchercount.setText("");
                                ((chatting_image_viewholder) viewHolder).chatpresenttime.setTextColor(Color.BLACK);
                                ((chatting_image_viewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                                //해당  ->  채팅의 유저 읽은수  넣어주기.
                                get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatting_image_viewholder) viewHolder).chatwatchercount);

                            }

                        }else{//다음 포지션이  존재 하지 않는경우이다.-> 이경우에는 마지막 아이템이므로  시간이 출력된다.

                            ((chatting_image_viewholder) viewHolder).chatpresenttime.setText(time);
                            ((chatting_image_viewholder) viewHolder).chatwatchercount.setText("");
                            ((chatting_image_viewholder) viewHolder).chatpresenttime.setTextColor(Color.BLACK);
                            ((chatting_image_viewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                            //해당  ->  채팅의 유저 읽은수  넣어주기.
                            get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatting_image_viewholder) viewHolder).chatwatchercount);

                        }////if(getItemCount()-1>position){ 끝


                    }//이전 포지션 아이디와 현재 포지션 아이디가  같지 않을 경우 끝

                }//위 시간이 같고,  아이디가 같고 viewtype이 3 또는 4일때의 조건을  충족하지 못한경우 끝.


            }//포지션이  0초과 일때 ->  즉  첫 채팅 멘트가 아닐때이다.

            else {//포지션이 0일떄 -> 즉  첫 채팅 멘트일경우다.

                LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) ((chatting_image_viewholder) viewHolder).chatting_img_content.getLayoutParams();

                layoutParams.leftMargin=30;
                ((chatting_image_viewholder) viewHolder).chatting_img_content.setLayoutParams(layoutParams);

                i=0;//그전에 연속된게 없으므로 i=0이다.
                Log.v("check", "채팅 순서 체크 : position 0일때");

                //현재 포지션에서  sender 의 uid
                senderuid=chattingtext.get(position).get("id").toString();

                if(!image_message.equals("null")) {//이미지 path값이  null이 아니라면,  이미지를 넣어준다.

                    Glide.with(context).load(url_for_image_chat)  .override(dpToPx(160),dpToPx(160))
                            .fitCenter().into(((chatting_image_viewholder) viewHolder).chatting_img_content);//해당 프로필 이미지 서버에서 받아와  넣어줌.

                }

                //보낸 사람 프로필 이미지
                ((chatting_image_viewholder) viewHolder).imageViewforuserprofile.setVisibility(View.VISIBLE);//처음 채팅이므로  이미지뷰를 보여준다.

                //프로필 이미지 path가 null아 아닐경우
                if(!imagepath.equals("null")) {
                    Glide.with(context).load(url).into(((chatting_image_viewholder) viewHolder).imageViewforuserprofile);//해당 프로필 이미지 서버에서 받아와  넣어줌.
                }else{
                    ((chatting_image_viewholder) viewHolder).imageViewforuserprofile.setImageResource(R.mipmap.ic_launcher);
                }


                //보낸사람  이름 담기는 텍스트뷰
                ((chatting_image_viewholder) viewHolder).textviewforusername.setVisibility(View.VISIBLE);//처음 채팅이므로 보낸사람 이름을 보여준다.
                ((chatting_image_viewholder) viewHolder).textviewforusername.setText(username);//채팅 이름 닫아줌.

                //i가  0이상인 경우에서 -> 리사이클러뷰 아이템수 -1 이  현재 포지션 보다 클때이다.
                // -> 이거는  현재 포지션 다음 포지션이 존재한가 아닌가의 여부를 알기 위해서이다
                //결국  시간이랑  안읽은 사람 수 보여줄지 아닐지 판별용임.
                if(getItemCount()-1>position){

                    //현재 포지션 다음  포지션의 채팅메세지의  날짜
                    Date futuredate=new  SimpleDateFormat("YYYY-MM-DD HH:mm").parse(chattingtext.get(position + 1).get("date").toString());

                    //다음 포지션 날짜 -> 시간 형태로
                    String futuretime=new SimpleDateFormat("aaa hh:mm").format(futuredate);

                    //다음 포지션  채팅 메세지  보낸 유저의 uid
                    String futureid=chattingtext.get(position+1).get("id").toString();

                    if(futuretime.equals(time)){//포지션 1앞 시간과  현재 포지션 시간이 같을때이다.

                        if(futureid.equals(senderuid)){//포지션 1앞과  현재 포지션 시간이 같고,  uid가  두포지션 모두  똑같을 때이다.

                            //채팅 본사람 숫자 텍스트뷰

                            ((chatting_image_viewholder) viewHolder).chatwatchercount.setText("");//일단  1로 해놓음.-> 나중에 처리해야됨.
                            ((chatting_image_viewholder) viewHolder).chatpresenttime.setText("");//처음 채팅이므로  채팅 보내진 시간도 보내준다.여기에다가 -> 본사람 숫자 넣어줌.
                            ((chatting_image_viewholder) viewHolder).chatpresenttime.setTextColor(Color.YELLOW);
                            ((chatting_image_viewholder) viewHolder).chatwatchercount.setTextColor(Color.BLACK);

                            //해당  ->  채팅의 유저 읽은수  넣어주기.
                            get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatting_image_viewholder) viewHolder).chatpresenttime);


                        }else{//두 포지션 시간이 같고  uid가 틀릴때 즉  다음 메세지가  상대 메세지 일때는  현재 포지션에서  시간을 출려해야한다.


                            ((chatting_image_viewholder) viewHolder).chatpresenttime.setText(time);//현재 포지션 시간 출력
                            ((chatting_image_viewholder) viewHolder).chatwatchercount.setText("");//현재 포지션 채팅 메세지  안읽은 유저의 숫자  보여주기.
                            ((chatting_image_viewholder) viewHolder).chatpresenttime.setTextColor(Color.BLACK);
                            ((chatting_image_viewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                            //해당  ->  채팅의 유저 읽은수  넣어주기.
                            get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatting_image_viewholder) viewHolder).chatwatchercount);

                        }

                    }else{//두 포지션  시간이 다를때이다.

                        ((chatting_image_viewholder) viewHolder).chatpresenttime.setText(time);//다음 메세지 시간이 다르면  현재 퐂션에서 무조건 시간을 출력해야한다.
                        ((chatting_image_viewholder) viewHolder).chatwatchercount.setText("");
                        ((chatting_image_viewholder) viewHolder).chatpresenttime.setTextColor(Color.BLACK);
                        ((chatting_image_viewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                        //해당  ->  채팅의 유저 읽은수  넣어주기.
                        get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatting_image_viewholder) viewHolder).chatwatchercount);

                    }

                }else{//다음 포지션이  존재 하지 않는경우이다.-> 이경우에는 마지막 아이템이므로  시간이 출력된다.

                    ((chatting_image_viewholder) viewHolder).chatpresenttime.setText(time);
                    ((chatting_image_viewholder) viewHolder).chatwatchercount.setText("");
                    ((chatting_image_viewholder) viewHolder).chatpresenttime.setTextColor(Color.BLACK);
                    ((chatting_image_viewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                    //해당  ->  채팅의 유저 읽은수  넣어주기.
                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatting_image_viewholder) viewHolder).chatwatchercount);

                }////if(getItemCount()-1>position){ 끝


            }//포지션 0 ->  상대가 보낸 이미지 채팅이  채팅방의 맨처음 멘트일때이다.




        }//상대가 이미지를 보냈을때 끝.- viewtype  return value =9d일때
         else if(viewHolder.getItemViewType()==10){//내가 비디오를 보냈을때  viewtype=5  retturn값 10


            String my_video_message=chattingtext.get(position).get("message").toString();

            //URL -> 이미지 프로필 URL받아오기
            String url_for_video_chat = "http://13.209.249.1/" + my_video_message;

            Log.v("check_img_chatting", my_video_message);
            Log.v("check", "ChattingDataRecyclerViewAdapter 의   viewHolder.getItemType=10 일때 실행됨-> 내가 보낸 비디오 메세지 타입");

            int i=0;//해당 타입의 메세지 (내가 보낸 메세지 타입)가  연속적으로  실행되는지를 체크 하기 위한  값

            if(position>0) {// 포지션이  0초과 일때-> 즉  리사이클러뷰  아이템이  2개 이상 일때이다. 이전 데이터가  있으므로,  같은 사람이  반복적으로  채팅을 보낼때  gui처리를 할수 있다.


                //현재 포지션 이전 포지션의 날짜.
                Date pastdate = new SimpleDateFormat("YYYY-MM-DD HH:mm").parse(chattingtext.get(position - 1).get("date").toString());

                //위  이전 포지션 날짜  time으로  변형 시켜줌.
                String pasttime = new SimpleDateFormat("aaa hh:mm").format(pastdate);

                //현재 포지션  이전 포지션의  채팅메세지  보낸 유저의  uid
                pastid = chattingtext.get(position - 1).get("id").toString();

                //이전 포지션 값이 3일때 경우이다.
                String pastviewtype = chattingtext.get(position - 1).get("viewtype").toString();


                //현재 포지션 이전 텍스트의  시간과 현재시간이  같고, 포지션 이전 아이디와  해당 유저의 아이디가 같을때이다.
                //그리고 뷰타입이 3일때 또는 4일때 -> 이미지 채팅과  채팅사이즈  가지고옴.
                if ((pasttime.equals(time) && pastid.equals(useruid) && pastviewtype.equals("3")) || (pasttime.equals(time) && pastid.equals(useruid) && pastviewtype.equals("4")) ||(pasttime.equals(time) && pastid.equals(useruid) && pastviewtype.equals("5")) ) {//11-1

                    i = i + 1;//이전 포지션의 채팅메세지의 시간과  유저가 현재 유저 본인과  같을 경우-> i를  1씩 계속 올려줘서 -> 계속 같은 유저가  연속으로  채팅을 보내는건지 확인한다.


                    if (i > 0) {//i 값이  0초과  ->  같은 유저의 채팅이 연속으로 진행될때이다.

                        //이경우 -> 이전  채팅메세지 유저와 같은 유저가  연속적으로 메세지를 보낸것이므로,  normal ui형태로 메세지 ui를 바꿔준다.
                        Log.v("check", "채팅 순서 체크 : 아이템은 2개 이상인상태이고,  이전 포지션 채팅시간과 현재 포지션 채팅시간이 맞고, i체크가 0이상 일때_> 연속되는 이미지 채팅 나옴.");


                        //채팅 보낸 이미지 path가 null아 아닐경우
                        if (!my_video_message.equals("null")) {


                          //  ((mychatting_video_viewholder) viewHolder).chatting_video_view.setMediaController(controller);
                            ((mychatting_video_viewholder) viewHolder).chatting_video_view.requestFocus();
                            ((mychatting_video_viewholder) viewHolder).chatting_video_view.setVideoURI(Uri.parse(url_for_video_chat));

                            //동영상  재생 준비가 다 되었을 경우
                            ((mychatting_video_viewholder) viewHolder).chatting_video_view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    //준비가 다되었을 경우-> 플레이버튼을 보여준다.
                                    ((mychatting_video_viewholder) viewHolder).chatting_video_playbtn.setVisibility(View.VISIBLE);
                                    ((mychatting_video_viewholder) viewHolder).chatting_video_view.seekTo(1);//1초쯤에  보여지는 프레임 -> 비디오 준비 끝나면 보여줌.

                                }

                            });


                            //동영상재생이 끝났을 경우에도 -> 플레이버튼을 보여줌.
                            ((mychatting_video_viewholder) viewHolder).chatting_video_view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {

                                    ((mychatting_video_viewholder) viewHolder).chatting_video_playbtn.setVisibility(View.VISIBLE);
                                    ((mychatting_video_viewholder) viewHolder).chatting_video_view.seekTo(1);

                                }
                            });


                        }//비디오 경로가 null이 아닌경우




                        //i가  0이상인 경우에서 -> 리사이클러뷰 아이템수 -1 이  현재 포지션 보다 클때이다.
                        // -> 이거는  현재 포지션 다음 포지션이 존재한가 아닌가의 여부를 알기 위해서이다.
                        if (getItemCount() - 1 > position) {

                            //현재 포지션 다음  포지션의 채팅메세지의  날짜
                            Date futuredate = new SimpleDateFormat("YYYY-MM-DD HH:mm").parse(chattingtext.get(position + 1).get("date").toString());

                            //다음 포지션 날짜 -> 시간 형태로
                            String futuretime = new SimpleDateFormat("aaa hh:mm").format(futuredate);

                            //다음 포지션  채팅 메세지  보낸 유저의 uid
                            String futureid = chattingtext.get(position + 1).get("id").toString();

                            if (futuretime.equals(time)) {//포지션 1앞 시간과  현재 포지션 시간이 같을때이다.

                                if (futureid.equals(useruid)) {//포지션 1앞과  현재 포지션 시간이 같고,  uid가  두포지션 모두  똑같을 때이다.

                                    ((mychatting_video_viewholder) viewHolder).chattime_video.setText("");//이때  현재 포지션에는  시간이 나올 필요없으므로 없애주낟.
                                    ((mychatting_video_viewholder) viewHolder).chatwatchercount_video.setText("");//이때 현재 포지션 아이템에는  채팅 메세지 안읽은 유저의 숫자가 나올 필요 x
                                    ((mychatting_video_viewholder) viewHolder).chattime_video.setTextColor(Color.YELLOW);
                                    ((mychatting_video_viewholder) viewHolder).chatwatchercount_video.setTextColor(Color.BLACK);

                                    //해당  ->  채팅의 유저 읽은수  넣어주기.
                                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"), chattingtext.get(position).getInt("chatorder"), ((mychatting_video_viewholder) viewHolder).chattime_video);


                                } else {//두 포지션 시간이 같고  uid가 틀릴때 즉  다음 메세지가  상대 메세지 일때는  현재 포지션에서  시간을 출려해야한다.


                                    ((mychatting_video_viewholder) viewHolder).chattime_video.setText(time);//현재 포지션 시간 출력
                                    ((mychatting_video_viewholder) viewHolder).chatwatchercount_video.setText("");//현재 포지션 채팅 메세지  안읽은 유저의 숫자  보여주기.
                                    ((mychatting_video_viewholder) viewHolder).chattime_video.setTextColor(Color.BLACK);
                                    ((mychatting_video_viewholder) viewHolder).chatwatchercount_video.setTextColor(Color.YELLOW);

                                    //해당  ->  채팅의 유저 읽은수  넣어주기.
                                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"), chattingtext.get(position).getInt("chatorder"), ((mychatting_video_viewholder) viewHolder).chatwatchercount_video);

                                }

                            } else {//두 포지션  시간이 다를때이다.

                                ((mychatting_video_viewholder) viewHolder).chattime_video.setText(time);//다음 메세지 시간이 다르면  현재 퐂션에서 무조건 시간을 출력해야한다.
                                ((mychatting_video_viewholder) viewHolder).chatwatchercount_video.setText("");
                                ((mychatting_video_viewholder) viewHolder).chattime_video.setTextColor(Color.BLACK);
                                ((mychatting_video_viewholder) viewHolder).chatwatchercount_video.setTextColor(Color.YELLOW);

                                //해당  ->  채팅의 유저 읽은수  넣어주기.
                                get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"), chattingtext.get(position).getInt("chatorder"), ((mychatting_video_viewholder) viewHolder).chatwatchercount_video);

                            }

                        } else {//다음 포지션이  존재 하지 않는경우이다.-> 이경우에는 마지막 아이템이므로  시간이 출력된다.

                            ((mychatting_video_viewholder) viewHolder).chattime_video.setText(time);
                            ((mychatting_video_viewholder) viewHolder).chatwatchercount_video.setText("");
                            ((mychatting_video_viewholder) viewHolder).chattime_video.setTextColor(Color.BLACK);
                            ((mychatting_video_viewholder) viewHolder).chatwatchercount_video.setTextColor(Color.YELLOW);

                            //해당  ->  채팅의 유저 읽은수  넣어주기.
                            get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"), chattingtext.get(position).getInt("chatorder"), ((mychatting_video_viewholder) viewHolder).chatwatchercount_video);


                        }////if(getItemCount()-1>position){ 끝


                    } else {//i 값 0 초과 아닐때 -> 연속된 채팅이 아닐때
                        /////////////////////////////////////////////////////////////////////////////////////////////////////////

                        Log.v("check", "채팅 순서 체크 : 아이템이  2개 이상인 경우,  과거시간과 현재시간이 맞지만, i체크가 0일때 즉 -> 맨처음다시 리셋");


                        //채팅 보낸 이미지 path가 null아 아닐경우
                        if (!my_video_message.equals("null")) {

                           // ((mychatting_video_viewholder) viewHolder).chatting_video_view.setMediaController(controller);
                            ((mychatting_video_viewholder) viewHolder).chatting_video_view.requestFocus();
                            ((mychatting_video_viewholder) viewHolder).chatting_video_view.setVideoURI(Uri.parse(url_for_video_chat));

                            //동영상  재생 준비가 다 되었을 경우
                            ((mychatting_video_viewholder) viewHolder).chatting_video_view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    //준비가 다되었을 경우-> 플레이버튼을 보여준다.
                                    ((mychatting_video_viewholder) viewHolder).chatting_video_playbtn.setVisibility(View.VISIBLE);
                                    ((mychatting_video_viewholder) viewHolder).chatting_video_view.seekTo(1);
                                }
                            });


                            //동영상재생이 끝났을 경우에도 -> 플레이버튼을 보여줌.
                            ((mychatting_video_viewholder) viewHolder).chatting_video_view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {

                                    ((mychatting_video_viewholder) viewHolder).chatting_video_playbtn.setVisibility(View.VISIBLE);
                                    ((mychatting_video_viewholder) viewHolder).chatting_video_view.seekTo(1);

                                }
                            });


                        }//비디오 경로가 null이 아닌 경우.




                        if (getItemCount() - 1 > position) {

                            Date futuredate = new SimpleDateFormat("YYYY-MM-DD HH:mm").parse(chattingtext.get(position + 1).get("date").toString());
                            String futuretime = new SimpleDateFormat("aaa hh:mm").format(futuredate);
                            String futureid = chattingtext.get(position + 1).get("id").toString();

                            if (futuretime.equals(time)) {//포지션 1앞 시간과  현재 포지션 시간이 같으면  -> 여기에 시간이 들어가면 안된다.

                                if (futureid.equals(useruid)) {
                                    ((mychatting_video_viewholder) viewHolder).chattime_video.setText("");
                                    ((mychatting_video_viewholder) viewHolder).chatwatchercount_video.setText("");
                                    ((mychatting_video_viewholder) viewHolder).chattime_video.setTextColor(Color.YELLOW);
                                    ((mychatting_video_viewholder) viewHolder).chatwatchercount_video.setTextColor(Color.BLACK);

                                    //해당  ->  채팅의 유저 읽은수  넣어주기.
                                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"), chattingtext.get(position).getInt("chatorder"), ((mychatting_video_viewholder) viewHolder).chattime_video);

                                } else {

                                    ((mychatting_video_viewholder) viewHolder).chattime_video.setText(time);
                                    ((mychatting_video_viewholder) viewHolder).chatwatchercount_video.setText("");
                                    ((mychatting_video_viewholder) viewHolder).chattime_video.setTextColor(Color.BLACK);
                                    ((mychatting_video_viewholder) viewHolder).chatwatchercount_video.setTextColor(Color.YELLOW);

                                    //해당  ->  채팅의 유저 읽은수  넣어주기.
                                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"), chattingtext.get(position).getInt("chatorder"), ((mychatting_video_viewholder) viewHolder).chatwatchercount_video);

                                }


                            } else {

                                ((mychatting_video_viewholder) viewHolder).chattime_video.setText(time);
                                ((mychatting_video_viewholder) viewHolder).chatwatchercount_video.setText("");
                                ((mychatting_video_viewholder) viewHolder).chattime_video.setTextColor(Color.BLACK);
                                ((mychatting_video_viewholder) viewHolder).chatwatchercount_video.setTextColor(Color.YELLOW);

                                //해당  ->  채팅의 유저 읽은수  넣어주기.
                                get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"), chattingtext.get(position).getInt("chatorder"), ((mychatting_video_viewholder) viewHolder).chatwatchercount_video);

                            }
                        } else {

                            ((mychatting_video_viewholder) viewHolder).chattime_video.setText(time);
                            ((mychatting_video_viewholder) viewHolder).chatwatchercount_video.setText("");
                            ((mychatting_video_viewholder) viewHolder).chattime_video.setTextColor(Color.BLACK);
                            ((mychatting_video_viewholder) viewHolder).chatwatchercount_video.setTextColor(Color.YELLOW);

                            //해당  ->  채팅의 유저 읽은수  넣어주기.
                            get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"), chattingtext.get(position).getInt("chatorder"), ((mychatting_video_viewholder) viewHolder).chatwatchercount_video);


                        }//if(getItemCount()-1>position){ 끝

                    }//i 값 0 초과 아닐때 -> 연속된 채팅이 아닐때끝

                }else {//위 시간이 같고,  아이디가 같고 viewtype이 3,4,5 또는 3,4,5 일때의 조건을  충족하지 못한경우


                    FrameLayout.LayoutParams layoutParams= (FrameLayout.LayoutParams) ((mychatting_video_viewholder) viewHolder).chatting_video_view.getLayoutParams();

                    layoutParams.topMargin=dpToPx(15);//연속되지 않고 처음  올라온  비디오 뷰는  위를  10 dp만큼  뛰어 준다.
                    ((mychatting_video_viewholder) viewHolder).chatting_video_view.setLayoutParams(layoutParams);

                    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    if(pastid.equals(useruid)){//이전 아이디와  현재 유저의 아이디가  같은 경우-> 이경우는  이전 시간과 현재 시간이  같지 않은경우.




                        i=0;//시간이 안맞으므로,  i =0으로 리셋
                        Log.v("check", "채팅 순서 체크 : position 0이상이지만,  과거 시간이 현재 시간과 안맞고, 이전 유저가  나일때");


                        //채팅 보낸 이미지 path가 null아 아닐경우
                        if (!my_video_message.equals("null")) {



                            //((mychatting_video_viewholder) viewHolder).chatting_video_view.setMediaController(controller);
                            ((mychatting_video_viewholder) viewHolder).chatting_video_view.requestFocus();
                            ((mychatting_video_viewholder) viewHolder).chatting_video_view.setVideoURI(Uri.parse(url_for_video_chat));

                            //동영상  재생 준비가 다 되었을 경우
                            ((mychatting_video_viewholder) viewHolder).chatting_video_view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    //준비가 다되었을 경우-> 플레이버튼을 보여준다.
                                    ((mychatting_video_viewholder) viewHolder).chatting_video_playbtn.setVisibility(View.VISIBLE);
                                    ((mychatting_video_viewholder) viewHolder).chatting_video_view.seekTo(1);
                                }
                            });


                            //동영상재생이 끝났을 경우에도 -> 플레이버튼을 보여줌.
                            ((mychatting_video_viewholder) viewHolder).chatting_video_view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {

                                    ((mychatting_video_viewholder) viewHolder).chatting_video_playbtn.setVisibility(View.VISIBLE);
                                    ((mychatting_video_viewholder) viewHolder).chatting_video_view.seekTo(1);

                                }
                            });



                        }//비디오 경로가 null이 아닌경우



                        if(getItemCount()-1>position){

                            Date futuredate=new  SimpleDateFormat("YYYY-MM-DD HH:mm").parse(chattingtext.get(position + 1).get("date").toString());
                            String futuretime=new SimpleDateFormat("aaa hh:mm").format(futuredate);
                            String futureid=chattingtext.get(position+1).get("id").toString();

                            if(futuretime.equals(time)){//포지션 1앞 시간과  현재 포지션 시간이 같으면  -> 여기에 시간이 들어가면 안된다.

                                if(futureid.equals(useruid)){
                                    ((mychatting_video_viewholder)viewHolder).chattime_video.setText("");
                                    ((mychatting_video_viewholder) viewHolder).chatwatchercount_video.setText("");
                                    ((mychatting_video_viewholder)viewHolder).chattime_video.setTextColor(Color.YELLOW);
                                    ((mychatting_video_viewholder) viewHolder).chatwatchercount_video.setTextColor(Color.BLACK);
                                    //해당  ->  채팅의 유저 읽은수  넣어주기.
                                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mychatting_video_viewholder)viewHolder).chattime_video);


                                }else{

                                    ((mychatting_video_viewholder)viewHolder).chattime_video.setText(time);
                                    ((mychatting_video_viewholder)viewHolder).chatwatchercount_video.setText("");
                                    ((mychatting_video_viewholder)viewHolder).chattime_video.setTextColor(Color.BLACK);
                                    ((mychatting_video_viewholder)viewHolder).chatwatchercount_video.setTextColor(Color.YELLOW);
                                    //해당  ->  채팅의 유저 읽은수  넣어주기.
                                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mychatting_video_viewholder)viewHolder).chatwatchercount_video);


                                }


                            }else{

                                ((mychatting_video_viewholder)viewHolder).chattime_video.setText(time);
                                ((mychatting_video_viewholder) viewHolder).chatwatchercount_video.setText("");
                                ((mychatting_video_viewholder)viewHolder).chattime_video.setTextColor(Color.BLACK);
                                ((mychatting_video_viewholder) viewHolder).chatwatchercount_video.setTextColor(Color.YELLOW);
                                //해당  ->  채팅의 유저 읽은수  넣어주기.
                                get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mychatting_video_viewholder)viewHolder).chatwatchercount_video);

                            }
                        }else{

                            ((mychatting_video_viewholder)viewHolder).chattime_video.setText(time);
                            ((mychatting_video_viewholder) viewHolder).chatwatchercount_video.setText("");
                            ((mychatting_video_viewholder)viewHolder).chattime_video.setTextColor(Color.BLACK);
                            ((mychatting_video_viewholder) viewHolder).chatwatchercount_video.setTextColor(Color.YELLOW);
                            //해당  ->  채팅의 유저 읽은수  넣어주기.
                            get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mychatting_video_viewholder)viewHolder).chatwatchercount_video);

                        }

                    }else{//이전 아이디와  현재 아이디가  안맞을때,

                        i=0;//이경우도 역시  채팅이  나로  연속되지 않기 때문에  i=0으로  리셋
                        Log.v("check", "채팅 순서 체크 : position 0이상이지만,  과거 시간이 현재 시간과 안맞고, 이전 유저가 내가아닐때,");

                        //채팅 보낸 이미지 path가 null아 아닐경우



                        //채팅 보낸 이미지 path가 null아 아닐경우
                        if (!my_video_message.equals("null")) {



                            //((mychatting_video_viewholder) viewHolder).chatting_video_view.setMediaController(controller);
                            ((mychatting_video_viewholder) viewHolder).chatting_video_view.requestFocus();
                            ((mychatting_video_viewholder) viewHolder).chatting_video_view.setVideoURI(Uri.parse(url_for_video_chat));

                            //동영상  재생 준비가 다 되었을 경우
                            ((mychatting_video_viewholder) viewHolder).chatting_video_view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    //준비가 다되었을 경우-> 플레이버튼을 보여준다.
                                    ((mychatting_video_viewholder) viewHolder).chatting_video_playbtn.setVisibility(View.VISIBLE);
                                    ((mychatting_video_viewholder) viewHolder).chatting_video_view.seekTo(1);
                                }
                            });


                            //동영상재생이 끝났을 경우에도 -> 플레이버튼을 보여줌.
                            ((mychatting_video_viewholder) viewHolder).chatting_video_view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {

                                    ((mychatting_video_viewholder) viewHolder).chatting_video_playbtn.setVisibility(View.VISIBLE);
                                    ((mychatting_video_viewholder) viewHolder).chatting_video_view.seekTo(1);

                                }
                            });


                        }//비디오 경로가 null이 아닌경우



                        if(getItemCount()-1>position){

                            Date futuredate=new  SimpleDateFormat("YYYY-MM-DD HH:mm").parse(chattingtext.get(position + 1).get("date").toString());
                            String futuretime=new SimpleDateFormat("aaa hh:mm").format(futuredate);
                            String futureid=chattingtext.get(position+1).get("id").toString();

                            if(futuretime.equals(time)){//포지션 1앞 시간과  현재 포지션 시간이 같으면  -> 여기에 시간이 들어가면 안된다.

                                if(futureid.equals(useruid)){
                                    ((mychatting_video_viewholder)viewHolder).chattime_video.setText("");
                                    ((mychatting_video_viewholder) viewHolder).chatwatchercount_video.setText("");
                                    ((mychatting_video_viewholder)viewHolder).chattime_video.setTextColor(Color.YELLOW);
                                    ((mychatting_video_viewholder) viewHolder).chatwatchercount_video.setTextColor(Color.BLACK);
                                    //해당  ->  채팅의 유저 읽은수  넣어주기.
                                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mychatting_video_viewholder)viewHolder).chattime_video);



                                }else{

                                    ((mychatting_video_viewholder)viewHolder).chattime_video.setText(time);
                                    ((mychatting_video_viewholder) viewHolder).chatwatchercount_video.setText("");
                                    ((mychatting_video_viewholder)viewHolder).chattime_video.setTextColor(Color.BLACK);
                                    ((mychatting_video_viewholder) viewHolder).chatwatchercount_video.setTextColor(Color.YELLOW);
                                    //해당  ->  채팅의 유저 읽은수  넣어주기.
                                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mychatting_video_viewholder)viewHolder).chatwatchercount_video);

                                }


                            }else{

                                ((mychatting_video_viewholder)viewHolder).chattime_video.setText(time);
                                ((mychatting_video_viewholder) viewHolder).chatwatchercount_video.setText("");
                                ((mychatting_video_viewholder)viewHolder).chattime_video.setTextColor(Color.BLACK);
                                ((mychatting_video_viewholder) viewHolder).chatwatchercount_video.setTextColor(Color.YELLOW);

                                //해당  ->  채팅의 유저 읽은수  넣어주기.
                                get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mychatting_video_viewholder)viewHolder).chatwatchercount_video);

                            }

                        }else{

                            ((mychatting_video_viewholder)viewHolder).chattime_video.setText(time);
                            ((mychatting_video_viewholder) viewHolder).chatwatchercount_video.setText("");
                            ((mychatting_video_viewholder)viewHolder).chattime_video.setTextColor(Color.BLACK);
                            ((mychatting_video_viewholder) viewHolder).chatwatchercount_video.setTextColor(Color.YELLOW);

                            //해당  ->  채팅의 유저 읽은수  넣어주기.
                            get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mychatting_video_viewholder)viewHolder).chatwatchercount_video);
                        }//if(getItemCount()-1>position){끝
                    }//이전 아이디와 현재 아이디 안맞을때 끝
                }//위 시간이 같고,  아이디가 같고 viewtype이 3,4,5 또는 3,4,5 일때의 조건을  충족하지 못한경우 끝

            }else{//포지션 0초과 아닐때 ->  맨처음  들어왔을때

                FrameLayout.LayoutParams layoutParams= (FrameLayout.LayoutParams) ((mychatting_video_viewholder) viewHolder).chatting_video_view.getLayoutParams();

                layoutParams.topMargin=dpToPx(15);//연속되지 않고 처음  올라온  비디오 뷰는  위를  10 dp만큼  뛰어 준다.
                ((mychatting_video_viewholder) viewHolder).chatting_video_view.setLayoutParams(layoutParams);

                i=0;//그전에 연속된게 없으므로 i=0이다.
                Log.v("check", "채팅 순서 체크 : position 0일때");



                //채팅 보낸 이미지 path가 null아 아닐경우
                if (!my_video_message.equals("null")) {



                   // ((mychatting_video_viewholder) viewHolder).chatting_video_view.setMediaController(controller);
                    ((mychatting_video_viewholder) viewHolder).chatting_video_view.requestFocus();
                    ((mychatting_video_viewholder) viewHolder).chatting_video_view.setVideoURI(Uri.parse(url_for_video_chat));

                    //동영상  재생 준비가 다 되었을 경우
                    ((mychatting_video_viewholder) viewHolder).chatting_video_view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            //준비가 다되었을 경우-> 플레이버튼을 보여준다.
                            ((mychatting_video_viewholder) viewHolder).chatting_video_playbtn.setVisibility(View.VISIBLE);
                            ((mychatting_video_viewholder) viewHolder).chatting_video_view.seekTo(1);
                        }
                    });


                    //동영상재생이 끝났을 경우에도 -> 플레이버튼을 보여줌.
                    ((mychatting_video_viewholder) viewHolder).chatting_video_view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {

                            ((mychatting_video_viewholder) viewHolder).chatting_video_playbtn.setVisibility(View.VISIBLE);
                            ((mychatting_video_viewholder) viewHolder).chatting_video_view.seekTo(1);

                        }
                    });



                }//비디오 경로가 null이 아닌경우


                if(getItemCount()>1){

                    Date futuredate=new  SimpleDateFormat("YYYY-MM-DD HH:mm").parse(chattingtext.get(position + 1).get("date").toString());
                    String futuretime=new SimpleDateFormat("aaa hh:mm").format(futuredate);
                    String futureid=chattingtext.get(position+1).get("id").toString();

                    if(futuretime.equals(time)){//포지션 1앞 시간과  현재 포지션 시간이 같으면  -> 여기에 시간이 들어가면 안된다.

                        if(futureid.equals(useruid)){

                            ((mychatting_video_viewholder)viewHolder).chattime_video.setText("");
                            ((mychatting_video_viewholder) viewHolder).chatwatchercount_video.setText("");

                            ((mychatting_video_viewholder)viewHolder).chattime_video.setTextColor(Color.YELLOW);
                            ((mychatting_video_viewholder) viewHolder).chatwatchercount_video.setTextColor(Color.BLACK);

                            //해당  ->  채팅의 유저 읽은수  넣어주기.
                            get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mychatting_video_viewholder)viewHolder).chattime_video);

                        }else{

                            ((mychatting_video_viewholder)viewHolder).chattime_video.setText(time);
                            ((mychatting_video_viewholder) viewHolder).chatwatchercount_video.setText("");

                            ((mychatting_video_viewholder)viewHolder).chattime_video.setTextColor(Color.BLACK);
                            ((mychatting_video_viewholder) viewHolder).chatwatchercount_video.setTextColor(Color.YELLOW);

                            //해당  ->  채팅의 유저 읽은수  넣어주기.
                            get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mychatting_video_viewholder)viewHolder).chatwatchercount_video);

                        }


                    }else{

                        ((mychatting_video_viewholder)viewHolder).chattime_video.setText(time);
                        ((mychatting_video_viewholder) viewHolder).chatwatchercount_video.setText("");
                        ((mychatting_video_viewholder)viewHolder).chattime_video.setTextColor(Color.BLACK);
                        ((mychatting_video_viewholder) viewHolder).chatwatchercount_video.setTextColor(Color.YELLOW);

                        //해당  ->  채팅의 유저 읽은수  넣어주기.
                        get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mychatting_video_viewholder)viewHolder).chatwatchercount_video);

                    }

                }else{

                    ((mychatting_video_viewholder)viewHolder).chattime_video.setText(time);
                    ((mychatting_video_viewholder) viewHolder).chatwatchercount_video.setText("");
                    ((mychatting_video_viewholder)viewHolder).chattime_video.setTextColor(Color.BLACK);
                    ((mychatting_video_viewholder) viewHolder).chatwatchercount_video.setTextColor(Color.YELLOW);

                    //해당  ->  채팅의 유저 읽은수  넣어주기.
                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"), ((mychatting_video_viewholder)viewHolder).chatwatchercount_video);

                }//    if(getItemCount()>1){ 끝



            }//포지션 0초과 아닐때 조건 끝.



         //내가 비디오를 보냈을때  viewtype=5  retturn값 10 끝
        }else if(viewHolder.getItemViewType()==11){//상대가 비디오 보냈을때 viewtype=5  return 값  11



            if(user_job_position.equals("t")){//유저의  position이  선생님일떄,


                username=username+" teacher";//선생님을  넣어준다.

                ((chatting_video_viewholder) viewHolder).textviewforusername.setTextColor(Color.GREEN);//선생님 이름은  초록 색으로 설정해줌.

            }else if(user_job_position.equals("s")){//유저의 position이 학생일떄.

                ((chatting_video_viewholder) viewHolder).textviewforusername.setTextColor(Color.BLACK);//학생은  이름 그대로  검정색으로 보내줌.
            }//유저의  position이  학생일떄 끝.


            String video_message=chattingtext.get(position).get("message").toString();

            //URL -> 이미지 프로필 URL받아오기
            String url_for_video_chat = "http://13.209.249.1/" + video_message;

            Log.v("check_img_chatting", video_message);
            Log.v("check", "ChattingDataRecyclerViewAdapter 의   viewHolder.getItemType==9 일때 실행됨-> 상대가 보낸 이미지 메세지 타입");

            int i=0;//해당 타입의 메세지 (남이 보낸 메세지 타입)가  연속적으로  실행되는지를 체크 하기 위한  값
            if(position>0) {// 포지션이  0초과 일때-> 즉  리사이클러뷰  아이템이  2개 이상 일때이다. 이전 데이터가  있으므로,  같은 사람이  반복적으로  채팅을 보낼때  gui처리를 할수 있다


                //현재 포지션 이전 포지션의 날짜.
                Date pastdate = new SimpleDateFormat("YYYY-MM-DD HH:mm").parse(chattingtext.get(position - 1).get("date").toString());

                //위  이전 포지션 날짜  time으로  변형 시켜줌.
                String pasttime = new SimpleDateFormat("aaa hh:mm").format(pastdate);

                //현재 포지션  이전 포지션의  채팅메세지  보낸 유저의  uid
                pastid = chattingtext.get(position - 1).get("id").toString();

                //현재 포지션에서  sender 의 uid
                senderuid = chattingtext.get(position).get("id").toString();


                //이전 포지션의 viewtype
                String pastviewtype = chattingtext.get(position - 1).get("viewtype").toString();

                //현재 포지션 이전 텍스트의  시간과 현재시간이  같고, 포지션 이전 아이디와  해당 유저의 아이디가 같을때이다.+ 그리고 viewtype이 3일 경우와  4일 경우이다.
                if((pasttime.equals(time) && pastid.equals(senderuid) && pastviewtype.equals("3")) || (pasttime.equals(time) && pastid.equals(senderuid) && pastviewtype.equals("4")) ||  (pasttime.equals(time) && pastid.equals(senderuid) && pastviewtype.equals("5"))){
                    //1-19 시작

                    i=i+1;//이전 포지션의 채팅메세지의 시간과  유저가 현재 유저 본인과  같을 경우-> i를  1씩 계속 올려줘서 -> 계속 같은 유저가  연속으로  채팅을 보내는건지 확인한다.

                    if(i>0) {//i 값이  0초과  ->  같은 유저의 채팅이 연속으로 진행될때이다.


                        ((chatting_video_viewholder) viewHolder).imageViewforuserprofile.setVisibility(View.GONE);//프로필 이미지 GONE시킴.
                        ((chatting_video_viewholder) viewHolder).textviewforusername.setVisibility(View.GONE);//채팅유저  이름  없앰.


                        //연속으로  이어지는 비디오 채팅 메세지에서 ->  보낸 유저의 프로필사진이랑  이름 사라지게 함으로, -> 거기에 맞춰 viewdo view의  margin 값  변경시켜줌
                        FrameLayout.LayoutParams layoutParams0= (FrameLayout.LayoutParams) ((chatting_video_viewholder) viewHolder).chatting_video_playbtn.getLayoutParams();
                        layoutParams0.leftMargin=dpToPx(26);
                        ((chatting_video_viewholder) viewHolder).chatting_video_playbtn.setLayoutParams(layoutParams0);


                        FrameLayout.LayoutParams layoutParams= (FrameLayout.LayoutParams) ((chatting_video_viewholder) viewHolder).chatting_video_view.getLayoutParams();
                        layoutParams.leftMargin=dpToPx(48);//연속되지 않고 처음  올라온  비디오 뷰는  위를  10 dp만큼  뛰어 준다.
                        ((chatting_video_viewholder) viewHolder).chatting_video_view.setLayoutParams(layoutParams);




                        //이경우 -> 이전  채팅메세지 유저와 같은 유저가  연속적으로 메세지를 보낸것이므로,  normal ui형태로 메세지 ui를 바꿔준다.
                        Log.v("check", "채팅 순서 체크 : 아이템은 2개 이상인상태이고,  이전 포지션 채팅시간과 현재 포지션 채팅시간이 맞고, i체크가 0이상 일때 즉 -> 노말 채팅ui나옴");


                        //채팅 보낸 이미지 path가 null아 아닐경우
                        if (!video_message.equals("null")) {

                            ((chatting_video_viewholder) viewHolder).chatting_video_view.requestFocus();
                            ((chatting_video_viewholder) viewHolder).chatting_video_view.setVideoURI(Uri.parse(url_for_video_chat));

                            //동영상  재생 준비가 다 되었을 경우
                            ((chatting_video_viewholder) viewHolder).chatting_video_view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    //준비가 다되었을 경우-> 플레이버튼을 보여준다.
                                    ((chatting_video_viewholder) viewHolder).chatting_video_playbtn.setVisibility(View.VISIBLE);
                                    ((chatting_video_viewholder) viewHolder).chatting_video_view.seekTo(1);
                                }
                            });


                            //동영상재생이 끝났을 경우에도 -> 플레이버튼을 보여줌.
                            ((chatting_video_viewholder) viewHolder).chatting_video_view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {

                                    ((chatting_video_viewholder) viewHolder).chatting_video_playbtn.setVisibility(View.VISIBLE);
                                    ((chatting_video_viewholder) viewHolder).chatting_video_view.seekTo(1);

                                }
                            });

                        }//비디오 경로가 null이 아닌경우


                        //i가  0이상인 경우에서 -> 리사이클러뷰 아이템수 -1 이  현재 포지션 보다 클때이다.
                        // -> 이거는  현재 포지션 다음 포지션이 존재한가 아닌가의 여부를 알기 위해서이다
                        //결국  시간이랑  안읽은 사람 수 보여줄지 아닐지 판별용임.
                        if(getItemCount()-1>position){

                            //현재 포지션 다음  포지션의 채팅메세지의  날짜
                            Date futuredate=new  SimpleDateFormat("YYYY-MM-DD HH:mm").parse(chattingtext.get(position + 1).get("date").toString());

                            //다음 포지션 날짜 -> 시간 형태로
                            String futuretime=new SimpleDateFormat("aaa hh:mm").format(futuredate);

                            //다음 포지션  채팅 메세지  보낸 유저의 uid
                            String futureid=chattingtext.get(position+1).get("id").toString();

                            if(futuretime.equals(time)){//포지션 1앞 시간과  현재 포지션 시간이 같을때이다.

                                if(futureid.equals(senderuid)){//포지션 1앞과  현재 포지션 시간이 같고,  uid가  두포지션 모두  똑같을 때이다.

                                    //채팅 본사람 숫자 텍스트뷰
                                    //cargo.get(chatorder);
                                    ((chatting_video_viewholder) viewHolder).chatwatchercount.setText("");//일단  1로 해놓음.-> 나중에 처리해야됨.
                                    ((chatting_video_viewholder) viewHolder).chatpresenttime.setText("");//처음 채팅이므로  채팅 보내진 시간도 보내준다.//여기에다가 -> 본사람 숫자 넣어줌.
                                    ((chatting_video_viewholder) viewHolder).chatpresenttime.setTextColor(Color.YELLOW);
                                    ((chatting_video_viewholder) viewHolder).chatwatchercount.setTextColor(Color.BLACK);

                                    // 해당  ->  채팅의 유저 읽은수  넣어주기.
                                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatting_video_viewholder) viewHolder).chatpresenttime);




                                }else{//두 포지션 시간이 같고  uid가 틀릴때 즉  다음 메세지가  상대 메세지 일때는  현재 포지션에서  시간을 출려해야한다.


                                    ((chatting_video_viewholder) viewHolder).chatpresenttime.setText(time);//현재 포지션 시간 출력
                                    ((chatting_video_viewholder) viewHolder).chatwatchercount.setText("");//현재 포지션 채팅 메세지  안읽은 유저의 숫자  보여주기.
                                    ((chatting_video_viewholder) viewHolder).chatpresenttime.setTextColor(Color.BLACK);
                                    ((chatting_video_viewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                                    //해당  ->  채팅의 유저 읽은수  넣어주기.
                                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatting_video_viewholder) viewHolder).chatwatchercount);

                                }

                            }else{//두 포지션  시간이 다를때이다.

                                ((chatting_video_viewholder) viewHolder).chatpresenttime.setText(time);//다음 메세지 시간이 다르면  현재 퐂션에서 무조건 시간을 출력해야한다.
                                ((chatting_video_viewholder) viewHolder).chatwatchercount.setText("");
                                ((chatting_video_viewholder) viewHolder).chatpresenttime.setTextColor(Color.BLACK);
                                ((chatting_video_viewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                                //해당  ->  채팅의 유저 읽은수  넣어주기.
                                get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatting_video_viewholder) viewHolder).chatwatchercount);


                            }

                        }else{//다음 포지션이  존재 하지 않는경우이다.-> 이경우에는 마지막 아이템이므로  시간이 출력된다.

                            ((chatting_video_viewholder) viewHolder).chatpresenttime.setText(time);
                            ((chatting_video_viewholder) viewHolder).chatwatchercount.setText("");
                            ((chatting_video_viewholder) viewHolder).chatpresenttime.setTextColor(Color.BLACK);
                            ((chatting_video_viewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                            //해당  ->  채팅의 유저 읽은수  넣어주기.
                            get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatting_video_viewholder) viewHolder).chatwatchercount);



                        }////if(getItemCount()-1>position){ 끝

                    } else {//이전 포지션과 같은 시간에서  i 값이  0초과가 아닐때  -> 이때는  같은 유저으 채팅이 연속적으로 진행되지 않는다.
                        Log.v("check", "채팅 순서 체크 : 아이템이  2개 이상인 경우,  과거시간과 현재시간이 맞지만, i체크가 0일때 즉 -> 맨처음다시 리셋");


                        //채팅 보낸 이미지 path가 null아 아닐경우
                        if (!video_message.equals("null")) {

                            ((chatting_video_viewholder) viewHolder).chatting_video_view.requestFocus();
                            ((chatting_video_viewholder) viewHolder).chatting_video_view.setVideoURI(Uri.parse(url_for_video_chat));

                            //동영상  재생 준비가 다 되었을 경우
                            ((chatting_video_viewholder) viewHolder).chatting_video_view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    //준비가 다되었을 경우-> 플레이버튼을 보여준다.
                                    ((chatting_video_viewholder) viewHolder).chatting_video_playbtn.setVisibility(View.VISIBLE);
                                    ((chatting_video_viewholder) viewHolder).chatting_video_view.seekTo(1);
                                }
                            });


                            //동영상재생이 끝났을 경우에도 -> 플레이버튼을 보여줌.
                            ((chatting_video_viewholder) viewHolder).chatting_video_view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {

                                    ((chatting_video_viewholder) viewHolder).chatting_video_playbtn.setVisibility(View.VISIBLE);
                                    ((chatting_video_viewholder) viewHolder).chatting_video_view.seekTo(1);

                                }
                            });

                        }//비디오 경로가 null이 아닌경우

                        //보낸 사람 프로필 이미지
                        ((chatting_video_viewholder) viewHolder).imageViewforuserprofile.setVisibility(View.VISIBLE);//리셋이니까 보낸사람 프로필 사진도 나옴.

                        //프로필 이미지 path가 null아 아닐경우
                        if(!imagepath.equals("null")) {
                            Glide.with(context).load(url).into(((chatting_video_viewholder) viewHolder).imageViewforuserprofile);//해당 프로필 이미지 서버에서 받아와  넣어줌.
                        }else{
                            ((chatting_video_viewholder) viewHolder).imageViewforuserprofile.setImageResource(R.mipmap.ic_launcher);
                        }

                        //보낸사람  이름 담기는 텍스트뷰
                        ((chatting_video_viewholder) viewHolder).textviewforusername.setVisibility(View.VISIBLE);//리셋이니까 이름 텍스트뷰도 나와야됨.
                        ((chatting_video_viewholder) viewHolder).textviewforusername.setText(username);//리셋이어서 이름 넣어줌


                        //i가  0이상인 경우에서 -> 리사이클러뷰 아이템수 -1 이  현재 포지션 보다 클때이다.
                        // -> 이거는  현재 포지션 다음 포지션이 존재한가 아닌가의 여부를 알기 위해서이다
                        //결국  시간이랑  안읽은 사람 수 보여줄지 아닐지 판별용임.
                        if(getItemCount()-1>position){

                            //현재 포지션 다음  포지션의 채팅메세지의  날짜
                            Date futuredate=new  SimpleDateFormat("YYYY-MM-DD HH:mm").parse(chattingtext.get(position + 1).get("date").toString());

                            //다음 포지션 날짜 -> 시간 형태로
                            String futuretime=new SimpleDateFormat("aaa hh:mm").format(futuredate);

                            //다음 포지션  채팅 메세지  보낸 유저의 uid
                            String futureid=chattingtext.get(position+1).get("id").toString();

                            if(futuretime.equals(time)){//포지션 1앞 시간과  현재 포지션 시간이 같을때이다.

                                if(futureid.equals(senderuid)){//포지션 1앞과  현재 포지션 시간이 같고,  uid가  두포지션 모두  똑같을 때이다.

                                    //채팅 본사람 숫자 텍스트뷰
                                    ((chatting_video_viewholder) viewHolder).chatwatchercount.setText("");//일단  1로 해놓음.-> 나중에 처리해야됨.
                                    ((chatting_video_viewholder) viewHolder).chatpresenttime.setText("");//처음 채팅이므로  채팅 보내진 시간도 보내준다.// 여기에다가 -> 본사람 숫자 넣어줌.
                                    ((chatting_video_viewholder) viewHolder).chatpresenttime.setTextColor(Color.YELLOW);
                                    ((chatting_video_viewholder) viewHolder).chatwatchercount.setTextColor(Color.BLACK);

                                    //해당  ->  채팅의 유저 읽은수  넣어주기.
                                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatting_video_viewholder) viewHolder).chatpresenttime);



                                }else{//두 포지션 시간이 같고  uid가 틀릴때 즉  다음 메세지가  상대 메세지 일때는  현재 포지션에서  시간을 출려해야한다.


                                    ((chatting_video_viewholder) viewHolder).chatpresenttime.setText(time);//현재 포지션 시간 출력
                                    ((chatting_video_viewholder) viewHolder).chatwatchercount.setText("");//현재 포지션 채팅 메세지  안읽은 유저의 숫자  보여주기.
                                    ((chatting_video_viewholder) viewHolder).chatpresenttime.setTextColor(Color.BLACK);
                                    ((chatting_video_viewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                                    //해당  ->  채팅의 유저 읽은수  넣어주기.
                                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatting_video_viewholder) viewHolder).chatwatchercount);

                                }

                            }else{//두 포지션  시간이 다를때이다.

                                ((chatting_video_viewholder) viewHolder).chatpresenttime.setText(time);//다음 메세지 시간이 다르면  현재 퐂션에서 무조건 시간을 출력해야한다.
                                ((chatting_video_viewholder) viewHolder).chatwatchercount.setText("");
                                ((chatting_video_viewholder) viewHolder).chatpresenttime.setTextColor(Color.BLACK);
                                ((chatting_video_viewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                                //해당  ->  채팅의 유저 읽은수  넣어주기.
                                get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatting_video_viewholder) viewHolder).chatwatchercount);

                            }

                        }else{//다음 포지션이  존재 하지 않는경우이다.-> 이경우에는 마지막 아이템이므로  시간이 출력된다.

                            ((chatting_video_viewholder) viewHolder).chatpresenttime.setText(time);
                            ((chatting_video_viewholder) viewHolder).chatwatchercount.setText("");
                            ((chatting_video_viewholder) viewHolder).chatpresenttime.setTextColor(Color.BLACK);
                            ((chatting_video_viewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                            //해당  ->  채팅의 유저 읽은수  넣어주기.
                            get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatting_video_viewholder) viewHolder).chatwatchercount);

                        }////if(getItemCount()-1>position){ 끝

                    }//i 값  0 초과 아닐때 끝


                    //1-19 끝
                }else{//위 시간이 같고,  아이디가 같고 viewtype이 3 또는 4일때의 조건을  충족하지 못한경우





                    if(pastid.equals(senderuid)) {//이전 아이디와  현재 유저의 아이디가  같은 경우-> 이경우는  이전 시간과 현재 시간이  같지 않은경우.

                        i=0;//시간이 안맞으므로,  i =0으로 리셋


                        //보낸 사람 프로필 이미지
                        ((chatting_video_viewholder) viewHolder).imageViewforuserprofile.setVisibility(View.VISIBLE);//리셋이니까 보낸사람 프로필 사진도 나옴.

                        //프로필 이미지 path가 null아 아닐경우
                        if(!imagepath.equals("null")) {
                            Glide.with(context).load(url).into(((chatting_video_viewholder) viewHolder).imageViewforuserprofile);//해당 프로필 이미지 서버에서 받아와  넣어줌.
                        }else{
                            ((chatting_video_viewholder) viewHolder).imageViewforuserprofile.setImageResource(R.mipmap.ic_launcher);
                        }

                        //보낸사람  이름 담기는 텍스트뷰
                        ((chatting_video_viewholder) viewHolder).textviewforusername.setVisibility(View.VISIBLE);//리셋이니까 이름 텍스트뷰도 나와야됨.
                        ((chatting_video_viewholder) viewHolder).textviewforusername.setText(username);//리셋이어서 이름 넣어줌


                        //채팅 보낸 이미지 path가 null아 아닐경우
                        if (!video_message.equals("null")) {



                            //((mychatting_video_viewholder) viewHolder).chatting_video_view.setMediaController(controller);
                            ((chatting_video_viewholder) viewHolder).chatting_video_view.requestFocus();
                            ((chatting_video_viewholder) viewHolder).chatting_video_view.setVideoURI(Uri.parse(url_for_video_chat));

                            //동영상  재생 준비가 다 되었을 경우
                            ((chatting_video_viewholder) viewHolder).chatting_video_view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    //준비가 다되었을 경우-> 플레이버튼을 보여준다.
                                    ((chatting_video_viewholder) viewHolder).chatting_video_playbtn.setVisibility(View.VISIBLE);
                                    ((chatting_video_viewholder) viewHolder).chatting_video_view.seekTo(1);
                                }
                            });


                            //동영상재생이 끝났을 경우에도 -> 플레이버튼을 보여줌.
                            ((chatting_video_viewholder) viewHolder).chatting_video_view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {

                                    ((chatting_video_viewholder) viewHolder).chatting_video_playbtn.setVisibility(View.VISIBLE);
                                    ((chatting_video_viewholder) viewHolder).chatting_video_view.seekTo(1);

                                }
                            });

                        }//비디오 경로가 null이 아닌경우


                        //i가  0이상인 경우에서 -> 리사이클러뷰 아이템수 -1 이  현재 포지션 보다 클때이다.
                        // -> 이거는  현재 포지션 다음 포지션이 존재한가 아닌가의 여부를 알기 위해서이다
                        //결국  시간이랑  안읽은 사람 수 보여줄지 아닐지 판별용임.
                        if(getItemCount()-1>position){

                            //현재 포지션 다음  포지션의 채팅메세지의  날짜
                            Date futuredate=new  SimpleDateFormat("YYYY-MM-DD HH:mm").parse(chattingtext.get(position + 1).get("date").toString());

                            //다음 포지션 날짜 -> 시간 형태로
                            String futuretime=new SimpleDateFormat("aaa hh:mm").format(futuredate);

                            //다음 포지션  채팅 메세지  보낸 유저의 uid
                            String futureid=chattingtext.get(position+1).get("id").toString();

                            if(futuretime.equals(time)){//포지션 1앞 시간과  현재 포지션 시간이 같을때이다.

                                if(futureid.equals(senderuid)){//포지션 1앞과  현재 포지션 시간이 같고,  uid가  두포지션 모두  똑같을 때이다.

                                    //채팅 본사람 숫자 텍스트뷰

                                    ((chatting_video_viewholder) viewHolder).chatwatchercount.setText("");//일단  1로 해놓음.-> 나중에 처리해야됨.
                                    ((chatting_video_viewholder) viewHolder).chatpresenttime.setText("");//처음 채팅이므로  채팅 보내진 시간도 보내준다.여기에다가 -> 본사람 숫자 넣어줌.
                                    ((chatting_video_viewholder) viewHolder).chatpresenttime.setTextColor(Color.YELLOW);
                                    ((chatting_video_viewholder) viewHolder).chatwatchercount.setTextColor(Color.BLACK);

                                    //해당  ->  채팅의 유저 읽은수  넣어주기.
                                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatting_video_viewholder) viewHolder).chatpresenttime);

                                }else{//두 포지션 시간이 같고  uid가 틀릴때 즉  다음 메세지가  상대 메세지 일때는  현재 포지션에서  시간을 출려해야한다.


                                    ((chatting_video_viewholder) viewHolder).chatpresenttime.setText(time);//현재 포지션 시간 출력
                                    ((chatting_video_viewholder) viewHolder).chatwatchercount.setText("");//현재 포지션 채팅 메세지  안읽은 유저의 숫자  보여주기.
                                    ((chatting_video_viewholder) viewHolder).chatpresenttime.setTextColor(Color.BLACK);
                                    ((chatting_video_viewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                                    //해당  ->  채팅의 유저 읽은수  넣어주기.
                                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatting_video_viewholder) viewHolder).chatwatchercount);


                                }

                            }else{//두 포지션  시간이 다를때이다.

                                ((chatting_video_viewholder) viewHolder).chatpresenttime.setText(time);//다음 메세지 시간이 다르면  현재 퐂션에서 무조건 시간을 출력해야한다.
                                ((chatting_video_viewholder) viewHolder).chatwatchercount.setText("");
                                ((chatting_video_viewholder) viewHolder).chatpresenttime.setTextColor(Color.BLACK);
                                ((chatting_video_viewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                                //해당  ->  채팅의 유저 읽은수  넣어주기.
                                get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatting_video_viewholder) viewHolder).chatwatchercount);

                            }

                        }else{//다음 포지션이  존재 하지 않는경우이다.-> 이경우에는 마지막 아이템이므로  시간이 출력된다.

                            ((chatting_video_viewholder) viewHolder).chatpresenttime.setText(time);
                            ((chatting_video_viewholder) viewHolder).chatwatchercount.setText("");
                            ((chatting_video_viewholder) viewHolder).chatpresenttime.setTextColor(Color.BLACK);
                            ((chatting_video_viewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                            //해당  ->  채팅의 유저 읽은수  넣어주기.
                            get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatting_video_viewholder) viewHolder).chatwatchercount);


                        }////if(getItemCount()-1>position){ 끝


                    }else{//이전 아이디와 현재 유저 아이디가  안같을 경우.

                        i=0;//시간이 안맞으므로,  i =0으로 리셋

                        //채팅 보낸 이미지 path가 null아 아닐경우
                        if (!video_message.equals("null")) {



                            ((chatting_video_viewholder) viewHolder).chatting_video_view.requestFocus();
                            ((chatting_video_viewholder) viewHolder).chatting_video_view.setVideoURI(Uri.parse(url_for_video_chat));

                            //동영상  재생 준비가 다 되었을 경우
                            ((chatting_video_viewholder) viewHolder).chatting_video_view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    //준비가 다되었을 경우-> 플레이버튼을 보여준다.
                                    ((chatting_video_viewholder) viewHolder).chatting_video_playbtn.setVisibility(View.VISIBLE);
                                    ((chatting_video_viewholder) viewHolder).chatting_video_view.seekTo(1);
                                }
                            });


                            //동영상재생이 끝났을 경우에도 -> 플레이버튼을 보여줌.
                            ((chatting_video_viewholder) viewHolder).chatting_video_view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {

                                    ((chatting_video_viewholder) viewHolder).chatting_video_playbtn.setVisibility(View.VISIBLE);
                                    ((chatting_video_viewholder) viewHolder).chatting_video_view.seekTo(1);

                                }
                            });


                        }//비디오 경로가 null이 아닌경우

                        //보낸 사람 프로필 이미지
                        ((chatting_video_viewholder) viewHolder).imageViewforuserprofile.setVisibility(View.VISIBLE);//리셋이니까 보낸사람 프로필 사진도 나옴.

                        //프로필 이미지 path가 null아 아닐경우
                        if(!imagepath.equals("null")) {
                            Glide.with(context).load(url).into(((chatting_video_viewholder) viewHolder).imageViewforuserprofile);//해당 프로필 이미지 서버에서 받아와  넣어줌.
                        }else{
                            ((chatting_video_viewholder) viewHolder).imageViewforuserprofile.setImageResource(R.mipmap.ic_launcher);
                        }

                        //보낸사람  이름 담기는 텍스트뷰
                        ((chatting_video_viewholder) viewHolder).textviewforusername.setVisibility(View.VISIBLE);//리셋이니까 이름 텍스트뷰도 나와야됨.
                        ((chatting_video_viewholder) viewHolder).textviewforusername.setText(username);//리셋이어서 이름 넣어줌


                        //i가  0이상인 경우에서 -> 리사이클러뷰 아이템수 -1 이  현재 포지션 보다 클때이다.
                        // -> 이거는  현재 포지션 다음 포지션이 존재한가 아닌가의 여부를 알기 위해서이다
                        //결국  시간이랑  안읽은 사람 수 보여줄지 아닐지 판별용임.
                        if(getItemCount()-1>position){

                            //현재 포지션 다음  포지션의 채팅메세지의  날짜
                            Date futuredate=new  SimpleDateFormat("YYYY-MM-DD HH:mm").parse(chattingtext.get(position + 1).get("date").toString());

                            //다음 포지션 날짜 -> 시간 형태로
                            String futuretime=new SimpleDateFormat("aaa hh:mm").format(futuredate);

                            //다음 포지션  채팅 메세지  보낸 유저의 uid
                            String futureid=chattingtext.get(position+1).get("id").toString();

                            if(futuretime.equals(time)){//포지션 1앞 시간과  현재 포지션 시간이 같을때이다.

                                if(futureid.equals(senderuid)){//포지션 1앞과  현재 포지션 시간이 같고,  uid가  두포지션 모두  똑같을 때이다.

                                    //채팅 본사람 숫자 텍스트뷰

                                    ((chatting_video_viewholder) viewHolder).chatwatchercount.setText("");//일단  1로 해놓음.-> 나중에 처리해야됨.
                                    ((chatting_video_viewholder) viewHolder).chatpresenttime.setText("");//처음 채팅이므로  채팅 보내진 시간도 보내준다.여기에다가 -> 본사람 숫자 넣어줌.
                                    ((chatting_video_viewholder) viewHolder).chatpresenttime.setTextColor(Color.YELLOW);
                                    ((chatting_video_viewholder) viewHolder).chatwatchercount.setTextColor(Color.BLACK);

                                    //해당  ->  채팅의 유저 읽은수  넣어주기.
                                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatting_video_viewholder) viewHolder).chatpresenttime);


                                }else{//두 포지션 시간이 같고  uid가 틀릴때 즉  다음 메세지가  상대 메세지 일때는  현재 포지션에서  시간을 출려해야한다.


                                    ((chatting_video_viewholder) viewHolder).chatpresenttime.setText(time);//현재 포지션 시간 출력
                                    ((chatting_video_viewholder) viewHolder).chatwatchercount.setText("");//현재 포지션 채팅 메세지  안읽은 유저의 숫자  보여주기.
                                    ((chatting_video_viewholder) viewHolder).chatpresenttime.setTextColor(Color.BLACK);
                                    ((chatting_video_viewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                                    //해당  ->  채팅의 유저 읽은수  넣어주기.
                                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatting_video_viewholder) viewHolder).chatwatchercount);

                                }

                            }else{//두 포지션  시간이 다를때이다.

                                ((chatting_video_viewholder) viewHolder).chatpresenttime.setText(time);//다음 메세지 시간이 다르면  현재 퐂션에서 무조건 시간을 출력해야한다.
                                ((chatting_video_viewholder) viewHolder).chatwatchercount.setText("");
                                ((chatting_video_viewholder) viewHolder).chatpresenttime.setTextColor(Color.BLACK);
                                ((chatting_video_viewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                                //해당  ->  채팅의 유저 읽은수  넣어주기.
                                get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatting_video_viewholder) viewHolder).chatwatchercount);

                            }

                        }else{//다음 포지션이  존재 하지 않는경우이다.-> 이경우에는 마지막 아이템이므로  시간이 출력된다.

                            ((chatting_video_viewholder) viewHolder).chatpresenttime.setText(time);
                            ((chatting_video_viewholder) viewHolder).chatwatchercount.setText("");
                            ((chatting_video_viewholder) viewHolder).chatpresenttime.setTextColor(Color.BLACK);
                            ((chatting_video_viewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                            //해당  ->  채팅의 유저 읽은수  넣어주기.
                            get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatting_video_viewholder) viewHolder).chatwatchercount);

                        }////if(getItemCount()-1>position){ 끝


                    }//이전 포지션 아이디와 현재 포지션 아이디가  같지 않을 경우 끝

                }//위 시간이 같고,  아이디가 같고 viewtype이 3 또는 4일때의 조건을  충족하지 못한경우 끝.


            }//포지션이  0초과 일때 ->  즉  첫 채팅 멘트가 아닐때이다.

            else {//포지션이 0일떄 -> 즉  첫 채팅 멘트일경우다.



                i=0;//그전에 연속된게 없으므로 i=0이다.
                Log.v("check", "채팅 순서 체크 : position 0일때");

                //현재 포지션에서  sender 의 uid
                senderuid=chattingtext.get(position).get("id").toString();

                //채팅 보낸 이미지 path가 null아 아닐경우
                if (!video_message.equals("null")) {



                    // ((mychatting_video_viewholder) viewHolder).chatting_video_view.setMediaController(controller);
                    ((chatting_video_viewholder) viewHolder).chatting_video_view.requestFocus();
                    ((chatting_video_viewholder) viewHolder).chatting_video_view.setVideoURI(Uri.parse(url_for_video_chat));

                    //동영상  재생 준비가 다 되었을 경우
                    ((chatting_video_viewholder) viewHolder).chatting_video_view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            //준비가 다되었을 경우-> 플레이버튼을 보여준다.
                            ((chatting_video_viewholder) viewHolder).chatting_video_playbtn.setVisibility(View.VISIBLE);
                            ((chatting_video_viewholder) viewHolder).chatting_video_view.seekTo(1);
                        }
                    });


                    //동영상재생이 끝났을 경우에도 -> 플레이버튼을 보여줌.
                    ((chatting_video_viewholder) viewHolder).chatting_video_view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {

                            ((chatting_video_viewholder) viewHolder).chatting_video_playbtn.setVisibility(View.VISIBLE);
                            ((chatting_video_viewholder) viewHolder).chatting_video_view.seekTo(1);

                        }
                    });


                }//비디오 경로가 null이 아닌경우


                //보낸 사람 프로필 이미지
                ((chatting_video_viewholder) viewHolder).imageViewforuserprofile.setVisibility(View.VISIBLE);//처음 채팅이므로  이미지뷰를 보여준다.

                //프로필 이미지 path가 null아 아닐경우
                if(!imagepath.equals("null")) {
                    Glide.with(context).load(url).into(((chatting_video_viewholder) viewHolder).imageViewforuserprofile);//해당 프로필 이미지 서버에서 받아와  넣어줌.
                }else{
                    ((chatting_video_viewholder) viewHolder).imageViewforuserprofile.setImageResource(R.mipmap.ic_launcher);
                }


                //보낸사람  이름 담기는 텍스트뷰
                ((chatting_video_viewholder) viewHolder).textviewforusername.setVisibility(View.VISIBLE);//처음 채팅이므로 보낸사람 이름을 보여준다.
                ((chatting_video_viewholder) viewHolder).textviewforusername.setText(username);//채팅 이름 닫아줌.

                //i가  0이상인 경우에서 -> 리사이클러뷰 아이템수 -1 이  현재 포지션 보다 클때이다.
                // -> 이거는  현재 포지션 다음 포지션이 존재한가 아닌가의 여부를 알기 위해서이다
                //결국  시간이랑  안읽은 사람 수 보여줄지 아닐지 판별용임.
                if(getItemCount()-1>position){

                    //현재 포지션 다음  포지션의 채팅메세지의  날짜
                    Date futuredate=new  SimpleDateFormat("YYYY-MM-DD HH:mm").parse(chattingtext.get(position + 1).get("date").toString());

                    //다음 포지션 날짜 -> 시간 형태로
                    String futuretime=new SimpleDateFormat("aaa hh:mm").format(futuredate);

                    //다음 포지션  채팅 메세지  보낸 유저의 uid
                    String futureid=chattingtext.get(position+1).get("id").toString();

                    if(futuretime.equals(time)){//포지션 1앞 시간과  현재 포지션 시간이 같을때이다.

                        if(futureid.equals(senderuid)){//포지션 1앞과  현재 포지션 시간이 같고,  uid가  두포지션 모두  똑같을 때이다.

                            //채팅 본사람 숫자 텍스트뷰

                            ((chatting_video_viewholder) viewHolder).chatwatchercount.setText("");//일단  1로 해놓음.-> 나중에 처리해야됨.
                            ((chatting_video_viewholder) viewHolder).chatpresenttime.setText("");//처음 채팅이므로  채팅 보내진 시간도 보내준다.여기에다가 -> 본사람 숫자 넣어줌.
                            ((chatting_video_viewholder) viewHolder).chatpresenttime.setTextColor(Color.YELLOW);
                            ((chatting_video_viewholder) viewHolder).chatwatchercount.setTextColor(Color.BLACK);

                            //해당  ->  채팅의 유저 읽은수  넣어주기.
                            get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatting_video_viewholder) viewHolder).chatpresenttime);


                        }else{//두 포지션 시간이 같고  uid가 틀릴때 즉  다음 메세지가  상대 메세지 일때는  현재 포지션에서  시간을 출려해야한다.


                            ((chatting_video_viewholder) viewHolder).chatpresenttime.setText(time);//현재 포지션 시간 출력
                            ((chatting_video_viewholder) viewHolder).chatwatchercount.setText("");//현재 포지션 채팅 메세지  안읽은 유저의 숫자  보여주기.
                            ((chatting_video_viewholder) viewHolder).chatpresenttime.setTextColor(Color.BLACK);
                            ((chatting_video_viewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                            //해당  ->  채팅의 유저 읽은수  넣어주기.
                            get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatting_video_viewholder) viewHolder).chatwatchercount);

                        }

                    }else{//두 포지션  시간이 다를때이다.

                        ((chatting_video_viewholder) viewHolder).chatpresenttime.setText(time);//다음 메세지 시간이 다르면  현재 퐂션에서 무조건 시간을 출력해야한다.
                        ((chatting_video_viewholder) viewHolder).chatwatchercount.setText("");
                        ((chatting_video_viewholder) viewHolder).chatpresenttime.setTextColor(Color.BLACK);
                        ((chatting_video_viewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                        //해당  ->  채팅의 유저 읽은수  넣어주기.
                        get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatting_video_viewholder) viewHolder).chatwatchercount);

                    }

                }else{//다음 포지션이  존재 하지 않는경우이다.-> 이경우에는 마지막 아이템이므로  시간이 출력된다.

                    ((chatting_video_viewholder) viewHolder).chatpresenttime.setText(time);
                    ((chatting_video_viewholder) viewHolder).chatwatchercount.setText("");
                    ((chatting_video_viewholder) viewHolder).chatpresenttime.setTextColor(Color.BLACK);
                    ((chatting_video_viewholder) viewHolder).chatwatchercount.setTextColor(Color.YELLOW);

                    //해당  ->  채팅의 유저 읽은수  넣어주기.
                    get_server_chatting_read_count(chattingtext.get(position).getString("roomnumber"),chattingtext.get(position).getInt("chatorder"),((chatting_video_viewholder) viewHolder).chatwatchercount);

                }////if(getItemCount()-1>position){ 끝


            }//포지션 0 ->  상대가 보낸 이미지 채팅이  채팅방의 맨처음 멘트일때이다.


        }//상대가 비디오 보냈을때 viewtype=5  return 값  11 끝

       } catch (JSONException e) {
          e.printStackTrace();

       } catch (MalformedURLException e) {
          e.printStackTrace();

       } catch (ParseException e) {
          e.printStackTrace();

       }//try_catch 구문 끝.


    }//onBindviewholder끝

    //자바 언어로  ->  ui 값   지정할때  -> 해당 값은 px로 들어간다. -> 그래서 그걸  dp 로  바꿔서 받기위한  메소드
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }


    //아이템 아이디를  서로 다르게 -> hashcode를  추가해줌. -> 이렇게해서 아이템 뷰들이  겹치는 현상 줄여줌.
    @Override
    public long getItemId(int position) {


        return chattingtext.get(position).hashCode();
    }

    class mychatting_video_viewholder extends RecyclerView.ViewHolder{

        TextView chattime_video;//보낸사람의 채팅 내용의  시간이 담길  텍스트뷰
        ImageView chatting_video_playbtn;//보낸사람의  채팅  비디오의  플레이버튼이 담긴 이미지뷰
        TextView chatwatchercount_video;//채팅 본사람 카운트가 담길  텍스트뷰
        VideoView chatting_video_view;// 보낸사람의  채팅 비디오 컨텐츠가 담길 비디오뷰


        public mychatting_video_viewholder(@NonNull View itemView) {
            super(itemView);

            Log.v("check", "mychatting_video_viewholder 생성자 실행");

            chattime_video=itemView.findViewById(R.id.recieve_chatpresenttime_video);
            chatting_video_playbtn=itemView.findViewById(R.id.receieve_video_chat_playbtn);
            chatwatchercount_video=itemView.findViewById(R.id.receiver_chat_watcher_count_video);
            chatting_video_view=itemView.findViewById(R.id.receive_video_view);



            //내가 보낸 비디오  플레이 버튼 눌렀을 경우 이벤트
            chatting_video_playbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String video_server_url = null;
                    String chatting_sender_position=null;//채팅 보낸 사람의  포지션
                    String chatting_sender_name=null;//채팅 보낸 사람의 uid

                    try {
                        String sender_uid=chattingtext.get(getAdapterPosition()).get("id").toString();

                        //비디오 저장된 서버 url
                        video_server_url=  chattingtext.get(getAdapterPosition()).get("message").toString();


                        //유저의 uid와  비디오 sender의 uid가 같을 경우
                        if(useruid.equals(sender_uid)){

                            chatting_sender_name="Me";//sender의 이름 은 -> Me

                        }else{//같은 uid가 아닐경우

                            chatting_sender_name=chattingtext.get(getAdapterPosition()).get("name").toString();

                        }


                        chatting_sender_position=chattingtext.get(getAdapterPosition()).get("userposition").toString();



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //비디오 재생되고 다운로드 가능한 엑티비티로 가짐.
                    Intent goto_chatting_video_magnified=new Intent(context,ChattingVideoMagnifiedActivity.class);

                    //비디오 url가짐.
                    goto_chatting_video_magnified.putExtra("video_url",video_server_url);

                    //비디오 보낸 사람의 이름.
                    goto_chatting_video_magnified.putExtra("video_sender_name", chatting_sender_name);

                    //비디오 보낸 사람의  포지션
                    goto_chatting_video_magnified.putExtra("video_sender_position", chatting_sender_position);

                    //현재  스크롤 포지션을  유지 위해 shared 에  저장 .
                    SharedPreferences sharedprefer_for_save_scrollposition=context.getSharedPreferences("saverecyclerviewposition",Context.MODE_PRIVATE);
                    SharedPreferences.Editor save_scrollposition_edit=sharedprefer_for_save_scrollposition.edit();
                    save_scrollposition_edit.putInt("scrollposition", getAdapterPosition());
                    save_scrollposition_edit.apply();


                    context.startActivity(goto_chatting_video_magnified);//ChattingImageMagnfiedActivity클래스 로 위 정보를 가지고 가짐.


                }//onclick() 끝
            });



        }//mychatting_video_viewholder생성자 끝
    }//mychatting_video_viewholder 클래스 끝.



    //상대 채팅 비디오 눌렸을때
    class chatting_video_viewholder extends RecyclerView.ViewHolder{

        TextView textviewforusername;//보낸 사람의  이름이 담길  텍스트뷰
        ImageView imageViewforuserprofile;//보낸 사람의 프로필 이미지가 담길 이미지뷰

        TextView chatpresenttime;//현재 시간이 담길  텍스트뷰
        TextView chatwatchercount;//채팅 본 사람  숫자 나오는  카운트 담길 ->  텍스트뷰

        ImageView chatting_video_playbtn;//보낸사람의  채팅  비디오의  플레이버튼이 담긴 이미지뷰

        VideoView chatting_video_view;//보낸사람의  채팅 비디오 컨텐츠가 담길 비디오뷰

        public chatting_video_viewholder(@NonNull View itemView) {
            super(itemView);

            textviewforusername=itemView.findViewById(R.id.chatsendername_video);
            imageViewforuserprofile=itemView.findViewById(R.id.chatsenderprofile_video);
            chatpresenttime=itemView.findViewById(R.id.chatpresenttime_video);
            chatwatchercount=itemView.findViewById(R.id.chatwhatchercount_video);
            chatting_video_playbtn=itemView.findViewById(R.id.sender_video_chat_playbtn);
            chatting_video_view=itemView.findViewById(R.id.sender_video_View);


            //비디오 플레이 버튼 눌림.
            chatting_video_playbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    String video_server_url = null;
                    String chatting_sender_position=null;//채팅 보낸 사람의  포지션
                    String chatting_sender_name=null;//채팅 보낸 사람의 uid

                    try {
                        String sender_uid=chattingtext.get(getAdapterPosition()).get("id").toString();

                        //비디오 저장된 서버 url
                        video_server_url=  chattingtext.get(getAdapterPosition()).get("message").toString();


                        //유저의 uid와  비디오 sender의 uid가 같을 경우
                        if(useruid.equals(sender_uid)){

                            chatting_sender_name="Me";//sender의 이름 은 -> Me

                        }else{//같은 uid가 아닐경우

                            chatting_sender_name=chattingtext.get(getAdapterPosition()).get("name").toString();

                        }


                       chatting_sender_position=chattingtext.get(getAdapterPosition()).get("userposition").toString();



                   } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //비디오 재생되고 다운로드 가능한 엑티비티로 가짐.
                    Intent goto_chatting_video_magnified=new Intent(context,ChattingVideoMagnifiedActivity.class);

                    //비디오 url가짐.
                    goto_chatting_video_magnified.putExtra("video_url",video_server_url);

                    //비디오 보낸 사람의 이름.
                    goto_chatting_video_magnified.putExtra("video_sender_name", chatting_sender_name);

                    //비디오 보낸 사람의  포지션
                    goto_chatting_video_magnified.putExtra("video_sender_position", chatting_sender_position);

                    //현재  스크롤 포지션을  유지 위해 shared 에  저장 .
                    SharedPreferences sharedprefer_for_save_scrollposition=context.getSharedPreferences("saverecyclerviewposition",Context.MODE_PRIVATE);
                    SharedPreferences.Editor save_scrollposition_edit=sharedprefer_for_save_scrollposition.edit();
                    save_scrollposition_edit.putInt("scrollposition", getAdapterPosition());
                    save_scrollposition_edit.apply();


                    context.startActivity(goto_chatting_video_magnified);//ChattingImageMagnfiedActivity클래스 로 위 정보를 가지고 가짐.


                }//onClick() 끝
            });


            //비디오 보낸 유저의  프로필 이미지 클릭 이벤트
            imageViewforuserprofile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    String profileuser_position=null;//해당 프로필 유저의 position
                    String profileuser_name=null;//해당 유저의 이름
                    String profileuser_uid=null;//해당 유저의 uid

                    try {

                        profileuser_position=chattingtext.get(getAdapterPosition()).get("userposition").toString();//해당 프로필 유저의 포지션
                        profileuser_name=chattingtext.get(getAdapterPosition()).get("name").toString();//해당 프로필 유저의  이름
                        profileuser_uid=chattingtext.get(getAdapterPosition()).get("id").toString();//해당 프로필 유저의 uid

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    //해당 클릭한 프로필 이미지 byte array로 바꿔서 보내줌.
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    Bitmap bitmap=((BitmapDrawable)imageViewforuserprofile.getDrawable()).getBitmap();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] byteArray = stream.toByteArray();


                    //선생일 때는 프로필  관련  -> 선택지가 더 많게 하기위해  프로필 이미지 보기 전  중간에  카카오톡 같은 엑티비티가 들어간다.
                    //학생일때는 바로  프로필 사진을 확대해서 보여준다.
                    if(profileuser_position.equals("t")){//해당 프로필 유저가  선생님일때

                        Log.v("check", "비디오 보낸 선생 유저의  프로필 이미지 클릭됨");


                        //TeacherChattingProdileActivity.class로 가는  intent
                        Intent goto_TeacherChatting_Profile_activity=new Intent(context,TeacherChattingProfileActivity.class);

                        //현재  스크롤 포지션을  유지 위해 shared 에  저장 .
                        SharedPreferences sharedprefer_for_save_scrollposition=context.getSharedPreferences("saverecyclerviewposition",Context.MODE_PRIVATE);
                        SharedPreferences.Editor save_scrollposition_edit=sharedprefer_for_save_scrollposition.edit();
                        save_scrollposition_edit.putInt("scrollposition", getAdapterPosition());
                        save_scrollposition_edit.apply();

                        //intent에  담을 -  내용들
                        goto_TeacherChatting_Profile_activity.putExtra("profileimage", byteArray);//프로필 이미지 보냄.
                        goto_TeacherChatting_Profile_activity.putExtra("teachername",profileuser_name);//선생님 이름 보냄.
                        goto_TeacherChatting_Profile_activity.putExtra("teacheruid", profileuser_uid);//선생님 uid보냄.



                        //TeacherChattingProdileActivity 실행
                        context.startActivity(goto_TeacherChatting_Profile_activity);

                        //아래 overridePendingTransition adpater에서는  엑티비티 클래스에 context 연결시킨 후, 해당 엑티비티 객체로
                        //사용해야됨.
                        Activity activity= (Activity) context;
                        activity.overridePendingTransition(R.anim.slide_in_up_activity,R.anim.slide_out_up_activity);//아래에서 위로 다음 엑티비티 실행 애니메이션.



                    }else if(profileuser_position.equals("s")){//해당 프로필 유저가 학생일때
                        Log.v("check", "비디오 보낸 학생 유저의  프로필 이미지 클릭됨");


                        Intent goto_profile_photo_magnifyactivity=new Intent(context,Teacher_Profile_Photo_magnify.class);//프로필 확대  엑티비티로 가는 인텐트

                        goto_profile_photo_magnifyactivity.putExtra("image", byteArray);//위 프로필 이미지 byte어레이로 넣은거  intent로 보냄.

                        //현재  스크롤 포지션을  유지 위해 shared 에  저장 .
                        SharedPreferences sharedprefer_for_save_scrollposition=context.getSharedPreferences("saverecyclerviewposition",Context.MODE_PRIVATE);
                        SharedPreferences.Editor save_scrollposition_edit=sharedprefer_for_save_scrollposition.edit();
                        save_scrollposition_edit.putInt("scrollposition", getAdapterPosition());
                        save_scrollposition_edit.apply();


                        context.startActivity(goto_profile_photo_magnifyactivity);//프로필 이미지 확대 엑티비티로 감.

                    }

                }
            });



        }//chattig_video_viewholder 끝
    }//chatting_video_viewholder 클래스 끝



    //내가 보낸 이미지 채팅뷰 홀더
    class mychatting_image_viewholder extends RecyclerView.ViewHolder{


        TextView chattime_img;//보낸사람의 채팅 내용의  시간이 담길  텍스트뷰
        ImageView chatting_img_content;//보낸사람의  채팅 이미지가 담길  이미지뷰 -> normal gui
        TextView chatwatchercount_img;//채팅 본사람 카운트가 담길  텍스트뷰

        public mychatting_image_viewholder(@NonNull View itemView) {
            super(itemView);

            Log.v("checkholder", "viewholder 생성자 실행");
            chattime_img= itemView.findViewById(R.id.recieve_chatpresenttime_img);
            chatting_img_content=itemView.findViewById(R.id.receieve_img_chat);
            chatwatchercount_img=itemView.findViewById(R.id.receiver_chat_watcher_count_img);

            //내가 보낸 채팅 이미지 파일  클릭시  ->  이벤트  //17-1
            chatting_img_content.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Log.v("check", adapternameforlog+"에서  유저가 보낸  이미지 클릭됨");

                    Intent intent_to_go_Magnfied_actiyity=new Intent(context,ChattingImageMagnfiedActivity.class);//이미지 확대해서 보여주는 ChattingImageMagnfiedActivity 엑티비티로 가기위한  인텐트

                    //이미지 path.
                    String my_image_message= null;//이미지
                    String chatting_sender_position=null;//채팅 보낸 사람의  포지션
                    String chatting_sender_name=null;//채팅 보낸 사람의 이름


                    try {

                        //해당 포지션의 다음 요구하는 값들  담아줌.
                        my_image_message = chattingtext.get(getAdapterPosition()).get("message").toString();
                        chatting_sender_position=chattingtext.get(getAdapterPosition()).get("userposition").toString();

                        String sender_uid=chattingtext.get(getAdapterPosition()).get("id").toString();

                        if(useruid.equals(sender_uid)){// 메세지 보낸 사람의 uid가  현재 유저의 보낸 uid와 같을때 -> 내가 보낸것임.

                            chatting_sender_name="Me";

                        }else {

                            chatting_sender_name=chattingtext.get(getAdapterPosition()).get("name").toString();

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    Bitmap bitmap=((BitmapDrawable)chatting_img_content.getDrawable()).getBitmap();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    intent_to_go_Magnfied_actiyity.putExtra("image_bitmap",byteArray);//비트맵 바이트 어레이로 compress하여  보내줌.  ->

                    intent_to_go_Magnfied_actiyity.putExtra("image_url",my_image_message);//채팅 이미지의  서버 url 보냄.
                    intent_to_go_Magnfied_actiyity.putExtra("image_sender_name",chatting_sender_name);//채팅 보낸 사람의 uid
                    intent_to_go_Magnfied_actiyity.putExtra("image_sender_position", chatting_sender_position);//채팅 보낸 사람의 position


                    Log.v("checkscorll", "현재 클릭 아이템  포지션 값.->"+getAdapterPosition());


                    //현재  스크롤 포지션을  유지 위해 shared 에  저장 .
                    SharedPreferences sharedprefer_for_save_scrollposition=context.getSharedPreferences("saverecyclerviewposition",Context.MODE_PRIVATE);
                    SharedPreferences.Editor save_scrollposition_edit=sharedprefer_for_save_scrollposition.edit();
                    save_scrollposition_edit.putInt("scrollposition", getAdapterPosition());
                    save_scrollposition_edit.apply();

                    context.startActivity(intent_to_go_Magnfied_actiyity);//ChattingImageMagnfiedActivity클래스 로 위 정보를 가지고 가짐.


                }//onClick

            });//17-1 끝




        }//mychatting_image_viewholder생성자 끝

    }//mychatting_image_viewholder 끝


    //상대가 보낸 이미지 채팅뷰 홀더
    class chatting_image_viewholder extends RecyclerView.ViewHolder{


        TextView textviewforusername;//보낸 사람의  이름이 담길  텍스트뷰
        ImageView imageViewforuserprofile;//보낸 사람의 프로필 이미지가 담길 이미지뷰

        TextView chatpresenttime;//현재 시간이 담길  텍스트뷰
        TextView chatwatchercount;//채팅 본 사람  숫자 나오는  카운트 담길 ->  텍스트뷰

        ImageView chatting_img_content;//보낸사람의  채팅 이미지가 담길  이미지뷰 -> normal gui

        public chatting_image_viewholder(@NonNull View itemView) {
            super(itemView);

            textviewforusername=itemView.findViewById(R.id.chatsendername_img);
            imageViewforuserprofile=itemView.findViewById(R.id.chatsenderprofile_img);
            chatpresenttime=itemView.findViewById(R.id.chatpresenttime_img);
            chatwatchercount=itemView.findViewById(R.id.chatwhatchercount_img);
            chatting_img_content=itemView.findViewById(R.id.sender_img_chat);


            //상대가 보낸 채팅 이미지 파일  클릭시  ->  이벤트  //17-2
            chatting_img_content.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Log.v("check", adapternameforlog+"에서  유저가 보낸  이미지 클릭됨");

                    Intent intent_to_go_Magnfied_actiyity=new Intent(context,ChattingImageMagnfiedActivity.class);//이미지 확대해서 보여주는 ChattingImageMagnfiedActivity 엑티비티로 가기위한  인텐트

                    //이미지 path.
                    String my_image_message= null;//이미지
                    String chatting_sender_position=null;//채팅 보낸 사람의  포지션
                    String chatting_sender_name=null;//채팅 보낸 사람의 uid


                    try {

                        //해당 포지션의 다음 요구하는 값들  담아줌.
                        my_image_message = chattingtext.get(getAdapterPosition()).get("message").toString();
                        chatting_sender_position=chattingtext.get(getAdapterPosition()).get("userposition").toString();

                        String sender_uid=chattingtext.get(getAdapterPosition()).get("id").toString();

                        if(useruid.equals(sender_uid)){// 메세지 보낸 사람의 uid가  현재 유저의 보낸 uid와 같을때 -> 내가 보낸것임.

                            chatting_sender_name="Me";//내가 보냈을때는 Me로  보냄..

                        }else {

                            chatting_sender_name=chattingtext.get(getAdapterPosition()).get("name").toString();

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    Bitmap bitmap=((BitmapDrawable)chatting_img_content.getDrawable()).getBitmap();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    intent_to_go_Magnfied_actiyity.putExtra("image_bitmap",byteArray);//비트맵 바이트 어레이로 compress하여  보내줌.  ->


                    intent_to_go_Magnfied_actiyity.putExtra("image_url",my_image_message);//채팅 이미지의  서버 url 보냄.
                    intent_to_go_Magnfied_actiyity.putExtra("image_sender_name",chatting_sender_name);//채팅 보낸 사람의 uid
                    intent_to_go_Magnfied_actiyity.putExtra("image_sender_position", chatting_sender_position);//채팅 보낸 사람의 position


                    Log.v("checkscorll", "현재 클릭 아이템  포지션 값.->"+getAdapterPosition());


                    //현재  스크롤 포지션을  유지 위해 shared 에  저장 .
                    SharedPreferences sharedprefer_for_save_scrollposition=context.getSharedPreferences("saverecyclerviewposition",Context.MODE_PRIVATE);
                    SharedPreferences.Editor save_scrollposition_edit=sharedprefer_for_save_scrollposition.edit();
                    save_scrollposition_edit.putInt("scrollposition", getAdapterPosition());
                    save_scrollposition_edit.apply();

                    context.startActivity(intent_to_go_Magnfied_actiyity);//ChattingImageMagnfiedActivity클래스 로 위 정보를 가지고 가짐.

                }//onClick




            });//17-2 끝


            //이미지 보낸 유저의  프로필 이미지 클릭 이벤트
            imageViewforuserprofile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String profileuser_position=null;//해당 프로필 유저의 position
                    String profileuser_name=null;
                    String profileuser_uid=null;

                    try {

                        profileuser_position=chattingtext.get(getAdapterPosition()).get("userposition").toString();//해당 프로필 유저의 포지션
                        profileuser_name=chattingtext.get(getAdapterPosition()).get("name").toString();//해당 프로필 유저의  이름
                        profileuser_uid=chattingtext.get(getAdapterPosition()).get("id").toString();//해당 프로필 유저의 uid

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    //해당 클릭한 프로필 이미지 byte array로 바꿔서 보내줌.
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    Bitmap bitmap=((BitmapDrawable)imageViewforuserprofile.getDrawable()).getBitmap();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] byteArray = stream.toByteArray();


                    //선생일 때는 프로필  관련  -> 선택지가 더 많게 하기위해  프로필 이미지 보기 전  중간에  카카오톡 같은 엑티비티가 들어간다.
                    //학생일때는 바로  프로필 사진을 확대해서 보여준다.
                    if(profileuser_position.equals("t")){//해당 프로필 유저가  선생님일때
                        Log.v("check", "이미지 보낸  선생 유저의  프로필 이미지 클릭됨");


                        //TeacherChattingProdileActivity.class로 가는  intent
                        Intent goto_TeacherChatting_Profile_activity=new Intent(context,TeacherChattingProfileActivity.class);

                        //현재  스크롤 포지션을  유지 위해 shared 에  저장 .
                        SharedPreferences sharedprefer_for_save_scrollposition=context.getSharedPreferences("saverecyclerviewposition",Context.MODE_PRIVATE);
                        SharedPreferences.Editor save_scrollposition_edit=sharedprefer_for_save_scrollposition.edit();
                        save_scrollposition_edit.putInt("scrollposition", getAdapterPosition());
                        save_scrollposition_edit.apply();

                        //intent에  담을 -  내용들
                        goto_TeacherChatting_Profile_activity.putExtra("profileimage", byteArray);//프로필 이미지 보냄.
                        goto_TeacherChatting_Profile_activity.putExtra("teachername",profileuser_name);//선생님 이름 보냄.
                        goto_TeacherChatting_Profile_activity.putExtra("teacheruid", profileuser_uid);//선생님 uid보냄.



                        //TeacherChattingProdileActivity 실행
                        context.startActivity(goto_TeacherChatting_Profile_activity);

                        //아래 overridePendingTransition adpater에서는  엑티비티 클래스에 context 연결시킨 후, 해당 엑티비티 객체로
                        //사용해야됨.
                        Activity activity= (Activity) context;
                        activity.overridePendingTransition(R.anim.slide_in_up_activity,R.anim.slide_out_up_activity);//아래에서 위로 다음 엑티비티 실행 애니메이션.



                    }else if(profileuser_position.equals("s")){//해당 프로필 유저가 학생일때

                        Log.v("check", "이미지 보낸 학생 유저의  프로필 이미지 클릭됨");

                        Intent goto_profile_photo_magnifyactivity=new Intent(context,Teacher_Profile_Photo_magnify.class);
                        goto_profile_photo_magnifyactivity.putExtra("image", byteArray);//위 프로필 이미지 byte어레이로 넣은거  intent로 보냄.

                        //현재  스크롤 포지션을  유지 위해 shared 에  저장 .
                        SharedPreferences sharedprefer_for_save_scrollposition=context.getSharedPreferences("saverecyclerviewposition",Context.MODE_PRIVATE);
                        SharedPreferences.Editor save_scrollposition_edit=sharedprefer_for_save_scrollposition.edit();
                        save_scrollposition_edit.putInt("scrollposition", getAdapterPosition());
                        save_scrollposition_edit.apply();

                        context.startActivity(goto_profile_photo_magnifyactivity);//프로필 이미지 확대 엑티비티로 감.

                    }

                }
            });


        }//chatting_image_viewholder 생성자 끝.


    }//chatting_image_viewholder 끝



    //남이 보낸 채팅뷰  홀더
    class chatrecyclerviewadapterviewholder extends RecyclerView.ViewHolder{

        TextView textViewforshowchat;//보낸 사람의  채팅 내용이 담기는  텍스트뷰 ->  normal말고 처음이나,  나의 채팅 다음에  나올때 나오는 gui- 7-1
        TextView textviewforusername;//보낸 사람의  이름이 담길  텍스트뷰-7-2
        ImageView imageViewforuserprofile;//보낸 사람의 프로필 이미지가 담길 이미지뷰-7-3
        TextView chatpresenttime;//현재 시간이 담길  텍스트뷰-7-4
        TextView chatwatchercount;//채팅 본 사람  숫자 나오는  카운트 담길 ->  텍스트뷰-7-5
        TextView textviewfornormalchat;//보낸 사람의 채팅 내용이 담기는 텍스트뷰 ->  normal버전  gui-7-6


        //남이 보낸 채팅뷰 홀더 생성자
        @SuppressLint("ClickableViewAccessibility")
        chatrecyclerviewadapterviewholder(@NonNull View itemView) {
            super(itemView);

            Log.v("checkholder", "viewholder 생성자 실행");
            chatpresenttime=itemView.findViewById(R.id.chatpresenttime);//7-4
            chatwatchercount=itemView.findViewById(R.id.chatwhatchercount);//7-5
            textViewforshowchat=itemView.findViewById(R.id.chatsendercontent);//7-1
            textviewforusername=itemView.findViewById(R.id.chatsendername);//7-2
            imageViewforuserprofile=itemView.findViewById(R.id.chatsenderprofile);//7-3
            textviewfornormalchat=itemView.findViewById(R.id.sender_normal_chat);//7-6


            //메세지 보낸 유저의  프로필 이미지 클릭 이벤트
            imageViewforuserprofile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String profileuser_position=null;//해당 프로필 유저의 position
                    String profileuser_name=null;
                    String profileuser_uid=null;

                    try {

                        profileuser_position=chattingtext.get(getAdapterPosition()).get("userposition").toString();//해당 프로필 유저의 포지션
                        profileuser_name=chattingtext.get(getAdapterPosition()).get("name").toString();//해당 프로필 유저의  이름
                        profileuser_uid=chattingtext.get(getAdapterPosition()).get("id").toString();//해당 프로필 유저의 uid

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                    //해당 클릭한 프로필 이미지 byte array로 바꿔서 보내줌.
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    Bitmap bitmap=((BitmapDrawable)imageViewforuserprofile.getDrawable()).getBitmap();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] byteArray = stream.toByteArray();


                    //선생일 때는 프로필  관련  -> 선택지가 더 많게 하기위해  프로필 이미지 보기 전  중간에  카카오톡 같은 엑티비티가 들어간다.
                    //학생일때는 바로  프로필 사진을 확대해서 보여준다.
                    if(profileuser_position.equals("t")){//해당 프로필 유저가  선생님일때

                        Log.v("check", "메세지 보낸 선생님 유저의  프로필 이미지 클릭됨");


                        //TeacherChattingProdileActivity.class로 가는  intent
                        Intent goto_TeacherChatting_Profile_activity=new Intent(context,TeacherChattingProfileActivity.class);

                        //현재  스크롤 포지션을  유지 위해 shared 에  저장 .
                        SharedPreferences sharedprefer_for_save_scrollposition=context.getSharedPreferences("saverecyclerviewposition",Context.MODE_PRIVATE);
                        SharedPreferences.Editor save_scrollposition_edit=sharedprefer_for_save_scrollposition.edit();
                        save_scrollposition_edit.putInt("scrollposition", getAdapterPosition());
                        save_scrollposition_edit.apply();


                        //intent에  담을 -  내용들
                        goto_TeacherChatting_Profile_activity.putExtra("profileimage", byteArray);//프로필 이미지 보냄.
                        goto_TeacherChatting_Profile_activity.putExtra("teachername",profileuser_name);//선생님 이름 보냄.
                        goto_TeacherChatting_Profile_activity.putExtra("teacheruid", profileuser_uid);//선생님 uid보냄.


                        //TeacherChattingProdileActivity 실행
                        context.startActivity(goto_TeacherChatting_Profile_activity);

                        //아래 overridePendingTransition adpater에서는  엑티비티 클래스에 context 연결시킨 후, 해당 엑티비티 객체로
                        //사용해야됨.
                        Activity activity= (Activity) context;
                        activity.overridePendingTransition(R.anim.slide_in_up_activity,R.anim.slide_out_up_activity);//아래에서 위로 다음 엑티비티 실행 애니메이션.



                    }else if(profileuser_position.equals("s")){//해당 프로필 유저가 학생일때

                        Log.v("check", "메세지 보낸  학생 유저의  프로필 이미지 클릭됨");


                        Intent goto_profile_photo_magnifyactivity=new Intent(context,Teacher_Profile_Photo_magnify.class);


                        goto_profile_photo_magnifyactivity.putExtra("image", byteArray);//위 프로필 이미지 byte어레이로 넣은거  intent로 보냄.

                        //현재  스크롤 포지션을  유지 위해 shared 에  저장 .
                        SharedPreferences sharedprefer_for_save_scrollposition=context.getSharedPreferences("saverecyclerviewposition",Context.MODE_PRIVATE);
                        SharedPreferences.Editor save_scrollposition_edit=sharedprefer_for_save_scrollposition.edit();
                        save_scrollposition_edit.putInt("scrollposition", getAdapterPosition());
                        save_scrollposition_edit.apply();


                        context.startActivity(goto_profile_photo_magnifyactivity);//프로필 이미지 확대 엑티비티로 감.

                    }

                }
            });

            //해당 유저가  학생인경우  ->  채팅 글중  텍스트  사전 검색을 할수 있다,
            if(userposition.equals("s")) {

                 //상대가 보냈을때 가장 첫번째  메세지  버전
                textViewforshowchat.setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        //어느 포지션  터치 되었는지 offset
                        int offset = textViewforshowchat.getOffsetForPosition(event.getX(), event.getY());

                        //해당  offset의 단어  찾아서  touched_word에 넣음
                        String touched_word = findWordForRightHanded(textViewforshowchat.getText().toString(), offset);

                        //해당단어를 다음 사전 사이트에 검색해  웹뷰로 보여주는   다이얼로그 실행
                        show_dictionery(touched_word);


                        return false;
                    }
                });

                //상대가 보낸  채팅  -> 노말버전 터치되었을때
                textviewfornormalchat.setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        //어느 포지션  터치 되었는지 offset
                        int offset = textviewfornormalchat.getOffsetForPosition(event.getX(), event.getY());

                        //해당  offset의 단어  찾아서  touched_word에 넣음
                        String touched_word = findWordForRightHanded(textviewfornormalchat.getText().toString(), offset);

                        //해당단어를 다음 사전 사이트에 검색해  웹뷰로 보여주는   다이얼로그 실행
                        show_dictionery(touched_word);


                        return false;
                    }
                });

            }//해당 유저가 학생인 경우,


        }//남이 보낸 채팅뷰 홀더 생성자 끝
    }//chatrecyclerviewdapterviewholer 끝



    //내가 쓴  문자들 보일때 사용되는 뷰홀더
    class mycahttingviewholder extends RecyclerView.ViewHolder{

        TextView mychat;//보낸사람의 채팅 내용이 담길 텍스트뷰-> fist gui -8-1
        TextView chattime;//보낸사람의 채팅 내용의  시간이 담길  텍스트뷰 -8-2
        TextView normalchat;//보낸사람의  채팅 내용이 담길  텍스트뷰 -> normal gui -8-3
        TextView chatwatchercount;//채팅 본사람 카운트가 담길  텍스트뷰 -8-4


        //내가 보낸  채팅뷰홀더  생성자
        @SuppressLint("ClickableViewAccessibility")
        public mycahttingviewholder(@NonNull View itemView) {
            super(itemView);

            normalchat=itemView.findViewById(R.id.receivechatcontent2);//8-3
            mychat=itemView.findViewById(R.id.receivechatcontent);//8-1
            chattime=itemView.findViewById(R.id.recieve_chatpresenttime);//8-2
            chatwatchercount=itemView.findViewById(R.id.receiver_chat_watcher_count);//8-4


         //해당 유저가  학생인경우  ->  채팅 글중  텍스트  사전 검색을 할수 있다,
         if(userposition.equals("s")) {

             //내가 보낸 채팅 -> 맨처음  보낼때 스타일  터치되며 진행
             mychat.setOnTouchListener(new View.OnTouchListener() {

                 @Override
                 public boolean onTouch(View v, MotionEvent event) {

                     //어느 포지션  터치 되었는지 offset
                     int offset = mychat.getOffsetForPosition(event.getX(), event.getY());

                     //해당  offset의 단어  찾아서  touched_word에 넣음
                     String touched_word = findWordForRightHanded(mychat.getText().toString(), offset);

                     //해당단어를 다음 사전 사이트에 검색해  웹뷰로 보여주는   다이얼로그 실행
                     show_dictionery(touched_word);


                     return false;
                 }
             });

             //내가 보낸 채팅  노말 버전  터치되면 진행
             normalchat.setOnTouchListener(new View.OnTouchListener() {

                 @Override
                 public boolean onTouch(View v, MotionEvent event) {

                     //어느 포지션  터치 되었는지 offset
                     int offset = normalchat.getOffsetForPosition(event.getX(), event.getY());

                     //해당  offset의 단어  찾아서  touched_word에 넣음
                     String touched_word = findWordForRightHanded(normalchat.getText().toString(), offset);

                     //해당단어를 다음 사전 사이트에 검색해  웹뷰로 보여주는   다이얼로그 실행
                     show_dictionery(touched_word);


                     return false;
                 }
             });

         }//해당 유저가  학생인 경우 끝


        }//내가 보낸 채팅뷰홀더 생성자 끝
    }//mycahttingviewholder 끝


    //jsoup을 이용해서 -> 해당  웹사이트에  단어를  검색해 -> 뜻을 가지고온다.
    private void  connect_website(String url,String word)  {


        //jsoup으로 파싱해 올때는 thread를 사용한다.
        new Thread(new Runnable() {

            @Override
            public void run() {

                //document -> doc가지고옴.
                Document doc = null;

                try {

                    //doc -> jsoup으로  해당  사전 url 연결 시킨거  가지고오낟.
                    doc = Jsoup.connect(url).get();

                }catch (IOException e) {
                    e.printStackTrace();
                }


                //meta태그에서 - 담긴 뜻을 가지고 와야됨



                Elements   metaTag = doc.getElementsByTag("meta");
                Log.v("check_connnect", "metatag "+metaTag);

                //해당  영어 단어가 -> txt_emph1 클래스 이름에  들어있음
                Elements searched_word=doc.getElementsByClass("txt_emph1");
                Log.v("check_connnect", "searched_word "+searched_word);

                for (Element metaTags : metaTag) {

                    String content = metaTags.attr("content");
                    String description = metaTags.attr("property");//메타 테그에서 property -속성

                    String   original_word =searched_word.eachText().get(0);

                    // 검색한  단어 뜻 -> 만약에 사전에 없는 단어를 검색시 유사한단어가 들어가짐. (뜻은 어차피  유사한 단어로 가지고 올것이므로)


                    if(description.equals("og:description")){//메타 테그에서 property -속성이 og:description 일때



                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Log.v("check_connnect", "단어-> "+ original_word +",  뜻-> "+ content);
                                Log.v("check_connect_dd", String.valueOf(original_word));


                                //여기에  sqlite에  해당 단어 저장하는 것을 구현 해야됨.
                                SharedPreferences sharedPreferences =context.getSharedPreferences("mydictionery", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor= sharedPreferences.edit();
                                editor.putString(original_word, content);

                                boolean save_word=editor.commit();//쉐어드 저장  실패 또는 성공시

                                if(save_word){//쉐어드 저장 성공시

                                    new Toastcustomer(context).showcustomtaost(null, "내 사전에  추가되었습니다.!");

                                }else{//쉐어드 저장  실패시

                                    new Toastcustomer(context).showcustomtaost(null, "내 사전에  추가 중 에러가 발생했습니다.");

                                }//쉐어드 저장 실패시 경우 끝

                            }//run()
                        });//해당  엑티비티 runonUiThread()끝

                    }//description 이 og:description  일때.

                }//for문 끝


            }//run() 끝

        }).start();//쓰레드 시작.



    }

    //해당 텍스트뷰에서 -> 단어를 추출해서 보여준다.
    private String findWordForRightHanded(String str, int offset) { // when you touch ' ', this method returns left word.
        if (str.length() == offset) {
            offset--; // without this code, you will get exception when touching end of the text
        }

        if (str.charAt(offset) == ' ') {
            offset--;
        }
        int startIndex = offset;
        int endIndex = offset;

        try {
            while (str.charAt(startIndex) != ' ' && str.charAt(startIndex) != '\n') {
                startIndex--;
            }
        } catch (StringIndexOutOfBoundsException e) {
            startIndex = 0;
        }

        try {
            while (str.charAt(endIndex) != ' ' && str.charAt(endIndex) != '\n') {
                endIndex++;
            }
        } catch (StringIndexOutOfBoundsException e) {
            endIndex = str.length();
        }

        // without this code, you will get 'here!' instead of 'here'
        // if you use only english, just check whether this is alphabet,
        // but 'I' use korean, so i use below algorithm to get clean word.
        char last = str.charAt(endIndex - 1);
        if (last == ',' || last == '.' ||
                last == '!' || last == '?' ||
                last == ':' || last == ';') {
            endIndex--;
        }

        return str.substring(startIndex, endIndex);

    }//findWordForRightHanded() 이부분 끝.


    //해당  궁금한 단어를 매개 변수로 받아서 ->  웹뷰로  보여줌.
    private void show_dictionery(String data){

                //alertdialog에  webview를 넣어서 ->해당  단어를 검색한  사전 페이지를 보여준다.
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("사전 뜻 보기");
                alert.setCancelable(false);

                WebView wv = new WebView(context);//웹뷰-> 사전페이지  보옂ㅁ.
                wv.loadUrl("https://dic.daum.net/search.do?q="+ data);//해당 단어가  data에 들어감.
                wv.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);

                        return true;
                    }
                });

                alert.setView(wv);

                //다이얼로그  취소 버튼
                alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

                //해당 버튼을 누르면  sqlite에 ->  해당 단어와  뜻이  저장된다.
                //나중에  학생이  나만의 단어장에 들어가서 볼수 있음.
                alert.setNeutralButton("To My Dictionery", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                         //해당 단어와  단어뜻을  다음 사전 페이지에서  가지고옴.
                         connect_website("https://dic.daum.net/search.do?q="+data,data);



                    }
                });

                //다이얼로그 보여주는데  단어장  추가 버튼은  초록색으로  바꿔줌,
                 alert.show().getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.GREEN);

    }//show_dictionery()끝





    class in_and_out_viewholder extends RecyclerView.ViewHolder{

        TextView in_and_out_chatting_ment_textview;//들어오고 나가는 멘트 들어가는  텍스트뷰 //11-1

        public in_and_out_viewholder(@NonNull View itemView) {
            super(itemView);


            in_and_out_chatting_ment_textview=itemView.findViewById(R.id.in_and_out_ment_text);//11-1


        }//나가고 들어가는 멘트 뷰홀더 생성자 끝.

    }//in_and_out_viewholder클래스 끝.


    //리사이클러뷰 아이템  숫자 .
    @Override
    public int getItemCount() {
        Log.v("check", "현재  chattingdata  개수->"+ chattingtext.size());


        return chattingtext.size();//chattingtext ->  어레이 안에  들어간 jSONObject화 된  데이터들의 개수 return
    }//getItemCount() 끝
}
