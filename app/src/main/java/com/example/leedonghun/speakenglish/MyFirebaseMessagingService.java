package com.example.leedonghun.speakenglish;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.AppWidgetTarget;
import com.bumptech.glide.request.target.NotificationTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static androidx.core.app.NotificationCompat.DEFAULT_SOUND;
import static androidx.core.app.NotificationCompat.DEFAULT_VIBRATE;

/**
 * speakenglish
 * Class: MyFirebaseMessagingService.
 * Created by leedonghun.
 * Created On 2019-10-02.
 * Description: 파이어베이스 메세징 서비스하기.
 * 파이어베이스 메시지  받기 위한  서비스 컴포넌트이다.
 * 이곳에서  FCM서버 로부터 받은 메세지를  가지고 노티피케이션을 만들수 있다.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {



    /**
    * * 구글 토큰을 얻는 값입니다.
    * 아래 토큰은 앱이 설치된 디바이스에 대한 고유값으로 푸시를 보낼때 사용됩니다.
    * **/
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("Firebase", "FirebaseInstanceIDService : " + s);

    }



    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {


        Log.v("Checkfcm", "getData()->"+ remoteMessage.getData());
        Log.v("Checkfcm", "getFrom()->" + remoteMessage.getFrom());


        Log.v("Checkfcm", "getCollapseKey()->" + remoteMessage.getCollapseKey());
        Log.v("Checkfcm", "getMessageId()->" + remoteMessage.getMessageId());

        Log.v("Checkfcm", "getMessageType()->" + remoteMessage.getMessageType());
        Log.v("Checkfcm", "getTo()->" + remoteMessage.getTo());
        Log.v("Checkfcm", "getSentTime()->" + remoteMessage.getSentTime());



        String collapsekey=remoteMessage.getCollapseKey();//서버에서 지정한 fcm collapsekey ;



        if(collapsekey.equals("1")){//채팅방에서  나갈때랑  들어올때  메세지 ->  sql라이트에  저장 처리 하기 위한  fcm메세들이다.


             //오픈채팅방 주고 오고 나가고  sqlite 에 저장 하기위한 메소드
            make_chatting_in_and_out_fcm_message(remoteMessage);

        }else if(collapsekey.equals("2")){//오픈 채팅방  메세지 주고 받을때

            //오픈채팅방  주고 오고 나가고 하는 메세지  푸쉬 알람.
            make_chatting_messege_push_noti_fcm_message(remoteMessage);



        }else if(collapsekey.equals("0")){//선생님  로그인 설정했을때 나오는  노티

            //커스톰 노티피케이션 만드는 메소드 실행 fcm 서버에서 받아온 remotedata 를  같이 보내줌.
            makefcmcustomnotification(remoteMessage);


        }else if(collapsekey.equals("4")) {//학생이  수업을 예약했을때  해당 예약 된 정보  선생님에게 알림.

             //학생이  해당  수업을   예약한 fcm내용을
            //노티로  선생에게  알려주기 위한  메소드
            make_alert_for_reservation_class(remoteMessage);


            //학생이름 ->새롭게  알람등록을 할때 필요한  정보이다.
            String student_name=remoteMessage.getData().get("std_name");//학생이름

            JSONArray jsonArray_for_reservated_class_time= null;//예약된 클래스의  시간이  들어갈  json array이다.
            JSONArray jsonArray_for_reservated_class_uid=null;//예약된 클래스의 uid 값이 들어갈  json array 이다.
            try {

                jsonArray_for_reservated_class_uid=new JSONArray(remoteMessage.getData().get("reservation_time_uid"));//해당 예약한 클래스의 uid array를 담아줌.
                jsonArray_for_reservated_class_time = new JSONArray(remoteMessage.getData().get("reservation_time"));//해당 예약한  클래스의 시간 array을 담아줌.


            } catch (JSONException e) {
                e.printStackTrace();
            }

            //위 예약된 클래스  어레이를  for문으로 돌려  ->  noti 메세지를  추가 시켜준다.
            for(int i=0; i<jsonArray_for_reservated_class_time.length(); i++){

                try {

                    int reserve_date_uid= (int) jsonArray_for_reservated_class_uid.get(i);//uid를  통해서  -> 알람을 request코드를 줘  구분한다. -> 나주에 취소할때 해당 uid 를 가지고  알람을 취소 시키면됨.
                    String reserved_date = String.valueOf(jsonArray_for_reservated_class_time.get(i));//이건  시간
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//date format으로 바꾸고  timemills로  변형해  알람에 적용한다.
                    Date date = null;
                    date = sdf.parse(reserved_date);

                    //예약한  시간  timemills
                    long reserved_date_timemillis = date.getTime();

                    //알람을  등록하기 위한 메소드
                    register_alarm(reserved_date_timemillis,student_name,reserve_date_uid);

                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }//for문 끝


        }//학생이 선생님 수업을 예약했을때  해당 예약  된 정보를 선생님에게 알려준다.

        else if(collapsekey.equals("5")){//학생이  수업을 취소 했을때  해당 예약된 수업 취소된것을  선생님에게  알림

            make_alert_for_cancel_reservation_class(remoteMessage);

            //등록된 알람 다시  취소 시켜줌.
            int class_uid= Integer.parseInt(remoteMessage.getData().get("reservation_time_uid"));
            cancelAlarmManger(class_uid);
        }

    }//onMessageReceievd()끝


    //예약 수업 취소시 등록되었던  알람도 취소 시켜준다.
    private void cancelAlarmManger(int canceled_reserved_class_uid){


        AlarmManager  mAlarmMgr = (AlarmManager) getBaseContext().getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getBaseContext().getApplicationContext(), Broadcast_for_alarm_reservation_class.class);
        PendingIntent mAlarmIntent = PendingIntent.getBroadcast(getBaseContext().getApplicationContext(), canceled_reserved_class_uid, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmMgr.cancel(mAlarmIntent);
        mAlarmIntent.cancel();


    }//cancelAlarmManger()끝




    //예약한  시간의  알람을 등록한다.
    private void register_alarm(long alarm_time,String student_name,int reserve_Date_uid){


        AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getBaseContext(), Broadcast_for_alarm_reservation_class.class);
        intent.putExtra("student_or_teacher", 0);//1이면 학생에 알람 등록  0이면 선생님에 알람 등록
        intent.putExtra("student_name", student_name);//수업할 학생 이름 보냄

        //팬딩인텐드  request code를  해당  예약 수업 시간 uid로  등록함.
        PendingIntent sender = PendingIntent.getBroadcast(getBaseContext(), reserve_Date_uid, intent, 0);

        Date date = new Date(alarm_time);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MINUTE, -10);//알람지정시간의 10분전으로
        date.setTime( c.getTime().getTime() );
        alarm_time = date.getTime();


        //알람 예약
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//도즈 모드 관련
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarm_time, sender);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            am.setExact(AlarmManager.RTC_WAKEUP, alarm_time, sender);
        } else {

            am.set(AlarmManager.RTC_WAKEUP, alarm_time, sender);
        }

    }//register_alarm() 끝



    //학생이 예약했던 선생님 클래스 취소 했을때  관련  내용을 푸쉬로 알린다.
    private  void make_alert_for_cancel_reservation_class(RemoteMessage remoteMessage){
        Log.v("checkfcm", "make_alert_for_cancel_reservation_class() 실행됨");

        String  student_profile="http://13.209.249.1/"+remoteMessage.getData().get("std_profile");//학생 프로필 url
        String student_name=remoteMessage.getData().get("std_name");//학생이름
        String message_content=student_name+" canceled a Reservation \nfew moment ago \nplz check  the new Reservation schedule!";//노티 메세지


        //예약을 확인하기 위해  클릭시  예약 시간 리스트가 보이는 방으로 들어가진다.
        Intent goto_teacher_chatting_activity=new Intent(getBaseContext(),SettingReservationTime.class);
        goto_teacher_chatting_activity.putExtra("fcmornot_teacher_uid", remoteMessage.getData().get("teacher_uid"));//fcm으로  받은  선생님  uid  넣어줌.


        //태스크를  다시 지정할것이므로  예약  리스트 엑티비티로 가면  그다음에  그뒤 스택으로  메인 엑티비티를  실행 시켜놓는다.
        Intent backIntent=new Intent(getBaseContext(),MainactivityForTeacher.class);


        //노티를 클릭시  예약  리스트 엑티비티와  백스택에 메인만 남겨두고  다른 스택은 모두 지워버린다.
        backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);

        //팬딩인텐트로  노티 클릭이 되었을 경우에 pendingintent로
        //requestcode 에는  -> currentTimeMills() 를  int로 cast해서 ->  각 pendingintent를  구분ㅅ켜줌
        //그렇게 안하면,  최근에 온  노티만 클릭이벤트가 작동한다.
        PendingIntent  pendingIntent=PendingIntent.getActivities(getBaseContext(), (int) System.currentTimeMillis(), new Intent[]{backIntent,goto_teacher_chatting_activity}, PendingIntent.FLAG_ONE_SHOT);

        //받은 데이터를 가지고  노티 작성
        //노티피케이션 메니저
        NotificationManager notichannel = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //노티 채널 id
        int id= (int) System.currentTimeMillis();
        String channel = "reservation_alert";



        String channel_nm = "speakenglish";//사용자에게 보여지는 채널의 이름

        //프로필 이미지 가져와서  비트맵화 시킴.
        //여기서는 원래 glide쓸려고 했는데  비동기식이다 보니  노티가 보내질때  이미지가  있거나 안있거나 하는경우가 있음 이미지 받아오는 시간보다
        //노티가 빨리 가지면, 그래서 아래와 같이  버퍼 인풋스트림을 사용함.
        Bitmap bm = null;
        try {
            URL url = new URL(student_profile);
            URLConnection conn = url.openConnection();//
            conn.connect();//url과 커넥트함
            BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());//인풋스트림으로  받아오기 사진
            bm = BitmapFactory.decodeStream(bis);
            bis.close();//인풋스트립 닫음

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        //커스톰  노티피케이션의 프로필 이미지  뷰에  위  비트맵화 시킨 이미지를 넣어줌.
        //customnotificationView.setImageViewBitmap(R.id.profileimgforteacherfcmalarm, bm);
        //커스톰 노티 피케이션에서  각 선생님의   이름 +현재 수업 가능함을 알려줌.
        //수업이 시작하면  로그인이  아닌 상태로 바꿀것이므로,   로그인 처리 했을 경우는  수업이 가능한  단계가 맞음.
        //customnotificationView.setTextViewText(R.id.teachernameforfcmalarm, message_content);
        //노티피케이션 빌더
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channel)
                .setSmallIcon(R.drawable.sayhellologoblack)//스몰 아이콘
                .setContentTitle("Alert Cancel Reservation")//유저의 이름 넣어줌.
                .setSubText(student_name)//subtext  채팅방 이름
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)//우선순위 중간
                .setLargeIcon(bm)//보낸 상대의 프로필 넣어줌.
                .setContentIntent(pendingIntent)//클릭시 -> 진행되는 pendingintent
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message_content))//bigstext-> 긴  글도  펼쳐보기 형태로 다 볼수 있음
                .setContentText(message_content)//위 경우처럼 긴글이 아닐때는 -> 그냗 텍스트 ㅏㄹ림.
                .setDefaults(DEFAULT_SOUND)//sound는 없애줌.
                .setAutoCancel(true);//클릭하면 사라짐.



        //오래오  이상  버전일때
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            if (notichannel != null) {

                //노티피케이션  채널  만들어냄
                notichannel.createNotificationChannel(new NotificationChannel(channel, channel_nm,NotificationManager.IMPORTANCE_HIGH));

                //notify id부분을  현재 시간으로 계속 다르게  넣어주니까  노티가  겹치지 않고  새롭게 뜨게됨.
                notichannel.notify(id,notificationBuilder.build());

            }//노티피케이션  매니저  null값이  아닐때 조건 끝.

        }//오래오 이상 버전  일때 조건 끝.


    }//make_alert_for_cancel_reservation_class() 끝



  //학생이  선생님 수업 예약 했을때  관련  내용을  푸쉬 알림으로 알린다.
  private void make_alert_for_reservation_class(RemoteMessage remoteMessage){
      Log.v("checkfcm", "make_alert_for_reservation_class() 실행됨");

      String  student_profile="http://13.209.249.1/"+remoteMessage.getData().get("std_profile");//학생 프로필 url
      String student_name=remoteMessage.getData().get("std_name");//학생이름

      String message_content="";//노티 메세지


      JSONArray jsonArray_for_reservated_class_uid= null;//예약된 클래스의 uid 값이 들어갈  json array 이다.
      try {

          jsonArray_for_reservated_class_uid = new JSONArray(remoteMessage.getData().get("reservation_time"));//해당 예약한  클래스의 uid


      } catch (JSONException e) {
          e.printStackTrace();
      }


      //위 예약된 클래스  어레이를  for문으로 돌려  ->  noti 메세지를  추가 시켜준다.
      for(int i=0; i<jsonArray_for_reservated_class_uid.length(); i++){

          try {
              if(!message_content.equals("")){
                   message_content=message_content+"\n"+jsonArray_for_reservated_class_uid.getString(i);
              }else {
                  message_content = jsonArray_for_reservated_class_uid.getString(i);
              }
          } catch (JSONException e) {
              e.printStackTrace();
          }

      }//for문 끝


     if(jsonArray_for_reservated_class_uid.length()==1){//예약한  클래스의 수가 1개라면

         message_content=message_content+"\n class is reserved by "+student_name;

     }else{//예약한 클래스가  1개 이상일떄떄

        message_content=message_content+"\n classes are reserved by "+student_name;
     }


      //예약을 확인하기 위해  클릭시  예약 시간 리스트가 보이는 방으로 들어가진다.
      Intent goto_teacher_chatting_activity=new Intent(getBaseContext(),SettingReservationTime.class);
      goto_teacher_chatting_activity.putExtra("fcmornot_teacher_uid", remoteMessage.getData().get("teacher_uid"));//fcm으로  받은  선생님  uid  넣어줌.


      //태스크를  다시 지정할것이므로  예약  리스트 엑티비티로 가면  그다음에  그뒤 스택으로  메인 엑티비티를  실행 시켜놓는다.
      Intent backIntent=new Intent(getBaseContext(),MainactivityForTeacher.class);


      //노티를 클릭시  예약  리스트 엑티비티와  백스택에 메인만 남겨두고  다른 스택은 모두 지워버린다.
      backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);

      //팬딩인텐트로  노티 클릭이 되었을 경우에 pendingintent로
      //requestcode 에는  -> currentTimeMills() 를  int로 cast해서 ->  각 pendingintent를  구분ㅅ켜줌
      //그렇게 안하면,  최근에 온  노티만 클릭이벤트가 작동한다.
      PendingIntent  pendingIntent=PendingIntent.getActivities(getBaseContext(), (int) System.currentTimeMillis(), new Intent[]{backIntent,goto_teacher_chatting_activity}, PendingIntent.FLAG_ONE_SHOT);


      //받은 데이터를 가지고  노티 작성
      //노티피케이션 메니저
      NotificationManager notichannel = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

      //노티 채널 id
      int id= (int) System.currentTimeMillis();
      String channel = "reservation_alert";



      String channel_nm = "speakenglish";//사용자에게 보여지는 채널의 이름

      //프로필 이미지 가져와서  비트맵화 시킴.
      //여기서는 원래 glide쓸려고 했는데  비동기식이다 보니  노티가 보내질때  이미지가  있거나 안있거나 하는경우가 있음 이미지 받아오는 시간보다
      //노티가 빨리 가지면, 그래서 아래와 같이  버퍼 인풋스트림을 사용함.
      Bitmap bm = null;
      try {
          URL url = new URL(student_profile);
          URLConnection conn = url.openConnection();//
          conn.connect();//url과 커넥트함
          BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());//인풋스트림으로  받아오기 사진
          bm = BitmapFactory.decodeStream(bis);
          bis.close();//인풋스트립 닫음

      } catch (MalformedURLException e) {
          e.printStackTrace();
      } catch (IOException e) {
          e.printStackTrace();
      }


      //커스톰  노티피케이션의 프로필 이미지  뷰에  위  비트맵화 시킨 이미지를 넣어줌.
      //customnotificationView.setImageViewBitmap(R.id.profileimgforteacherfcmalarm, bm);
      //커스톰 노티 피케이션에서  각 선생님의   이름 +현재 수업 가능함을 알려줌.
      //수업이 시작하면  로그인이  아닌 상태로 바꿀것이므로,   로그인 처리 했을 경우는  수업이 가능한  단계가 맞음.
      //customnotificationView.setTextViewText(R.id.teachernameforfcmalarm, message_content);
      //노티피케이션 빌더
      NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channel)
              .setSmallIcon(R.drawable.sayhellologoblack)//스몰 아이콘
              .setContentTitle("Alert Reserved Class")//유저의 이름 넣어줌.
              .setSubText(student_name)//subtext  채팅방 이름
              .setPriority(NotificationCompat.PRIORITY_DEFAULT)//우선순위 중간
              .setLargeIcon(bm)//보낸 상대의 프로필 넣어줌.
              .setContentIntent(pendingIntent)//클릭시 -> 진행되는 pendingintent
              .setStyle(new NotificationCompat.BigTextStyle().bigText(message_content))//bigstext-> 긴  글도  펼쳐보기 형태로 다 볼수 있음
              .setContentText(message_content)//위 경우처럼 긴글이 아닐때는 -> 그냗 텍스트 ㅏㄹ림.
              .setDefaults(DEFAULT_SOUND)//sound는 없애줌.
              .setAutoCancel(true);//클릭하면 사라짐.



      //오래오  이상  버전일때
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

          if (notichannel != null) {

              //노티피케이션  채널  만들어냄
              notichannel.createNotificationChannel(new NotificationChannel(channel, channel_nm,NotificationManager.IMPORTANCE_HIGH));

              //notify id부분을  현재 시간으로 계속 다르게  넣어주니까  노티가  겹치지 않고  새롭게 뜨게됨.
              notichannel.notify(id,notificationBuilder.build());

          }//노티피케이션  매니저  null값이  아닐때 조건 끝.

      }//오래오 이상 버전  일때 조건 끝.






}//make_alert_for_reservation_class()끝




  //채팅  메세지 중  노티가 뜨지 않는 오픈채팅방 다른유저가 들어오고  나가는 메세지들의 관한 ->  내용들을 sqlite에만 저장하기 위한 메소드이다.
  private void make_chatting_in_and_out_fcm_message(RemoteMessage remoteMessage){

        Log.v("checkfcm", "make_chatting_in_and_out_fcm_message() 실행됨");


  }//make_chatting_in_and_out_fcm_message()끝



  //1대1 채팅방 오픈 채팅방 채팅 메세지 서로 주고 받을 때  뜨는 노티 띠워지는 메세지 관련  fcm받은 내용
  private void make_chatting_messege_push_noti_fcm_message(RemoteMessage remoteMessage){

        Log.v("check", "make_chatting_messege_push_noti_fcm_message 실행됨");


      SharedPreferences getstudentemail = getSharedPreferences("loginstudentid", MODE_PRIVATE);//로그인 아이디  쉐어드에 담긴거 가져옴
      final String studentemail = getstudentemail.getString("loginid", "");//학생 로그인 이메일 가져옴.

      //내가  선생님 일때  노티 클릭 이벤트
      //선생님 이메일 쉐어드에  넣기.
      final SharedPreferences getteacheremail = getBaseContext().getSharedPreferences("loginteacherid",MODE_PRIVATE);//선생님 로그인 아이디  쉐어드에 담긴거 가져옴
      String teacheremail= getteacheremail.getString("loginidteacher","");//선생님 로그인 이메일 가져옴

      String loginid_for_sql=null;
      if(studentemail.equals("")){//선생님 로그인 일때

        loginid_for_sql=teacheremail.replaceAll("@", "");

      }else if(teacheremail.equals("")){//학생 로그인 일때

          loginid_for_sql=studentemail.replaceAll("@", "");
      }




      try {

          JSONObject jsonObject=new JSONObject(remoteMessage.getData().get("chatting_data"));//서버에서  json chatting_data라는  이름으로 옴.

          //채팅 메세지 오가는 방 번호.
          String roomnumber=jsonObject.getString("roomnumber");

          //보낸 사람 uid
          String uid=jsonObject.getString("id");

          //보낸사람  포지션 -> t는  선생님   s는  학생
          String userposition=jsonObject.getString("userposition");


          //채팅 보낸  유저의 이름.
          String username=jsonObject.getString("name");

          //채팅 보낸 방의  namespace 0 ->  오픈 채팅방    1-> 1대1 채팅방
          String namespace=jsonObject.getString("roomnamespace");

          //보낸 사람의  프로필 경로
          String profileimage="http://13.209.249.1/"+jsonObject.getString("profile");

          String profilepath=jsonObject.getString("profile");

          //보낸 메세지의 뷰타입.
          String  viewtype=jsonObject.getString("viewtype");

          //보낸 날짜.
          String date=jsonObject.getString("date");

          //보낸 메세지 내용


          String message_content=null;//fcm에  담길  채팅 메세지

          if(viewtype.equals("3")){//뷰타입  3일떄  ->  일반적인 메세지이므로,  fcm으로 날아온  메세지 그대로 넣어준다.

              message_content=jsonObject.getString("message");

          }else if(viewtype.equals("4")){//뷰타입이 4 일때  ->  이미지  메세지 이므로,  fcm날아오면  메세지는 Picture uploaded!라고 말해준다.

              message_content="Picture uploaded!";
          }else if(viewtype.equals("5")){

              message_content="Video uploaded!";
          }


          //채팅방 이름
          String chatting_room_name=null;

          String  message_chatorder=jsonObject.getString("chatorder");




          if(message_chatorder.equals("1") && namespace.equals("1")){//1대1 채팅방에 채팅순서 1인 경우.-> 선생님은 새롭게  소켓이 연결되어야 하므로,  학생이 보낸
                                                                     //채팅 내용을  저장 못할수 있음 그래서 ->  fcm을 이용해 해당 내용을 저장해준다.
              new Handler(Looper.getMainLooper()).post(new Runnable() {
                  @Override
                  public void run() {
                      GlobalBus.getBus_data().register(this);//이벤트 버스 등록시킴.-> 서비스가 시스템에 의해 강제 종료 후에 시작되면  oncraete 가 아니라 -> onStartcommmand에서  시작될수 있어 누락될까봐

                  }
              });


              Log.v("check", "1대1 채팅방  채팅순서 1인 경우로 fcm으로 온  내용을  해당 채팅방 sqlite에 담는다. ");

              SqLiteOpenHelperClass sqLiteOpenHelper=new SqLiteOpenHelperClass(getBaseContext(),loginid_for_sql , null,1 );
              SQLiteDatabase database=sqLiteOpenHelper.getWritableDatabase();

              //백그라운드에서 받은 채팅 데이터  sqlite에  넣어줌.
              sqLiteOpenHelper.inserChattingData(database,uid,namespace,roomnumber ,"1",userposition ,username ,profilepath ,viewtype, date,message_chatorder,message_content,1);

              if(isMyServiceRunning(ServiceForGetChattingData.class)){

                  //서비스를  onResume에서  한번 멈춰준다.  이밎  실행 되어있는 상황에서 들어오니까 당연히 멈춰줘야됨.
                  //채팅 내용 받는 백그라운드 서비스  멈추게 한다.
                  Intent stop_backgroun_chatting_service=new Intent(getBaseContext(),ServiceForGetChattingData.class);
                  stopService(stop_backgroun_chatting_service);


              }//서비스가 실행중일때 -> 해당  서비스를 한번 멈춰준다. -> 멈추는 이유는  해당 채팅방에서는 다른 소켓 연결이 시도되기 때문이다.
              //밑에서 -> 채팅방용 소켓 연결이 끝나면 그때  다시   서비스로 연결 시켜준다. -> 이렇게 하면 joinedornot의 값이 1인 값은  제외 되므로, 현재 채팅방에서는
              //채팅방용 소켓만  추가되고 서비스에선  제외된다. -> 바로가기 12-1

              if(!isMyServiceRunning(ServiceForGetChattingData.class)){


              Intent start_chatting_background_service=new Intent(getBaseContext(),ServiceForGetChattingData.class);
              startService(start_chatting_background_service);//서비스 실행시킴.


              }//서비스가 실행중일때 -> 해당  서비스를 한번 멈춰준다. -> 멈추는 이유는  해당 채팅방에서는 다른 소켓 연결이 시도되기 때문이다.

              String useruid=roomnumber.replaceAll(uid, "");
              final JSONObject firstonemessenger =new JSONObject();
              JSONObject jdd=new JSONObject();
              try {


                  jdd.put("useruid", useruid);
                  jdd.put("roomnamespace", "1");
                  jdd.put("userposition", "t");
                  jdd.put("roomnumber", roomnumber);
                  jdd.put("roomjoinedornot", "0");


              } catch (JSONException e) {
                  e.printStackTrace();
              }

              firstonemessenger.put("onetoonefirst_messge",jdd);
              Log.v("CHECKKKKKKKK", "FSFSDFSDFSDFSDFSDFDS");
              //이벤트 버스 -> 메인  스레드 루퍼 가지고 와서 돌림 ->  안쓰면  메인 쓰레드 관련 에러남
              new Handler(Looper.getMainLooper()).post(new Runnable() {
                  @Override
                  public void run() {

                      try {
                          Log.v("CHECKKKKKKKK", "FSFSDFSDFSDFSDFSDFDS"+firstonemessenger.get("onetoonefirst_messge"));
                      } catch (JSONException e) {
                          e.printStackTrace();
                      }
                      GlobalBus.getBus_data().post(firstonemessenger);;//이벤트 버스로  -> 백그라운드 채팅 데이터 보냄.
                  }
              });


              new Handler(Looper.getMainLooper()).post(new Runnable() {
                  @Override
                  public void run() {
                      GlobalBus.getBus_data().unregister(this);//이벤트 버스 등록시킴.-> 서비스가 시스템에 의해 강제 종료 후에 시작되면  oncraete 가 아니라 -> onStartcommmand에서  시작될수 있어 누락될까봐

                  }
              });
          }//1대1 채팅방에 채팅순서 1인 경우. 끝끝



          if(namespace.equals("0")){// 채팅방이 오픈 채팅방일 때이다.
             //오픈 채팅방인 경우 ->  선생님 이름과 +'s OpenChattingRoom이 들어가야됨.


              if(userposition.equals("t")){// 오픈 채팅방에서 메세지 보낸 사람이 선생님이면  보낸 사람의 이름을 사용

                  chatting_room_name=username+"'s OpenChatRoom";

              }else if(userposition.equals("s")){//학생일때는 해당방  선생님의 이름이 필요하므로,  새로  받아서 보내준다.

                  //선생님 이름
                  String teachername=jsonObject.getString("teachername");
                  chatting_room_name=teachername+"'s OpenChatRoom";

              }//오픈 채팅방에서 채팅 메세지 보낸 사람이  학생일때  조건 끝 .


          }else if(namespace.equals("1")) {//채팅방이  1대1  채팅방 일때다.

              //1대1 채팅방일 경우이다.
              //이경우는 상대에게  채팅방 이름이  보낸 사람의 이름으로 보이면된다.
              //여기서 학생  선생을 구분해서 ->  선생인경우에는  teacher을 붙여준다.
              if (userposition.equals("t")) {//보낸사람이 선생인경우

                  chatting_room_name=username+" teacher [1:1 chat]";

              }else{//보낸 사람이  학생인경우

                  chatting_room_name=username+ " [1:1 chat]";

              }

          }//채팅방이  1대1 채팅방 일때  조건 끝



          //노티피케이션 메니저
          NotificationManager notichannel = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

          //노티 채널 id
          int id= (int) System.currentTimeMillis();
          String channel = "chatting_message";



          String channel_nm = "speakenglish";//사용자에게 보여지는 채널의 이름


          //프로필 이미지 가져와서  비트맵화 시킴.
          //여기서는 원래 glide쓸려고 했는데  비동기식이다 보니  노티가 보내질때  이미지가  있거나 안있거나 하는경우가 있음 이미지 받아오는 시간보다
          //노티가 빨리 가지면, 그래서 아래와 같이  버퍼 인풋스트림을 사용함.
          Bitmap bm = null;
          try {
              URL url = new URL(profileimage);
              URLConnection conn = url.openConnection();//
              conn.connect();//url과 커넥트함
              BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());//인풋스트림으로  받아오기 사진
              bm = BitmapFactory.decodeStream(bis);
              bis.close();//인풋스트립 닫음

          } catch (MalformedURLException e) {
              e.printStackTrace();
          } catch (IOException e) {
              e.printStackTrace();
          }



          //팬딩인텐트로  노티 클릭이 되었을 경우에 pendingintent로
          PendingIntent pendingIntent=null;//-> 초기값 null넣어줌.



         //해당  노티를 눌렀을때  ->    해당  채팅방으로 들어가지고  뒤로가기를 누르면,  채팅 리스트  프래그먼트록  가지도록  진행한다.
         //선생님과  학생  나눠줘서   진행해준다.
         if(teacheremail.equals("")){//학생 로그인 상태일때

             Intent goto_student_chatting_activity=new Intent(getBaseContext(),ChattingRoomActivityForStudent.class);

             if(namespace.equals("0")){//오픈 채팅방일때


                 goto_student_chatting_activity.putExtra("studentemail", studentemail);//학생 이메일
                 goto_student_chatting_activity.putExtra("teacheruid",roomnumber);//오픈챗일때는 roomnumber가  선생님 uid이므로..
                 goto_student_chatting_activity.putExtra("teachername","1");//해당 activity에서 선생님 이름은  필요가 없다  -> 그래서 그냥 쓰레기값 1넣음.
                 goto_student_chatting_activity.putExtra("chattingroomtype", 0);//채팅 룸 타입 0-> 오픈 채팅룸일때

             }else if(namespace.equals("1")){//1대1 채팅방일때


                 goto_student_chatting_activity.putExtra("studentemail", studentemail);//학생 이메일
                 goto_student_chatting_activity.putExtra("teacheruid",uid);//일대일 챗일때는 ->상대가 선생님이므로 선생님 uid그대로 넣어줌.
                 goto_student_chatting_activity.putExtra("teachername", username);//선생님 이름  그로 넣어줌. -> 상대가  선생님이므로.
                 goto_student_chatting_activity.putExtra("chattingroomtype", 1);//채팅룸 타입 1->  일대일 채팅룸일때.

             }//1대1 채팅방일때  조건 끝.

             //실행하는  학생용 채팅엑티비티 뒤에  백스택에  만들어질  메인 스택
             Intent backIntent=new Intent(getBaseContext(),MainactiviyForstudent.class);
             backIntent.putExtra("checkbackstack", 2);

             //노티를 클릭시  teacherprofile과  백스택에 메인만 남겨두고  다른 스택은 모두 지워버린다.
             backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);

             //팬딩인텐트로  노티 클릭이 되었을 경우에 pendingintent로
             //requestcode 에는  -> currentTimeMills() 를  int로 cast해서 ->  각 pendingintent를  구분ㅅ켜줌
             //그렇게 안하면,  최근에 온  노티만 클릭이벤트가 작동한다.
             pendingIntent=PendingIntent.getActivities(getBaseContext(), (int) System.currentTimeMillis(), new Intent[]{backIntent,goto_student_chatting_activity}, PendingIntent.FLAG_ONE_SHOT);


         //학생로그인  상태일때 끝.
         }else if(studentemail.equals("")){//선생님 로그인 상태일때

             //선생이므로  ChattingRoomActivityForTeacher로 가짐.
             Intent goto_teacher_chatting_activity=new Intent(getBaseContext(),ChattingRoomActivityForTeacher.class);

             if(namespace.equals("0")){//오픈 채팅방일때


                 goto_teacher_chatting_activity.putExtra("RoomName","My Open Chatting Room");//룸 이름
                 goto_teacher_chatting_activity.putExtra("Roomnumber", roomnumber);//룸넘버
                 goto_teacher_chatting_activity.putExtra("chattingroomtype", 0);//오픈 채팅 방 타입

             }else if(namespace.equals("1")){//일대일 채팅방일때

                 goto_teacher_chatting_activity.putExtra("RoomName",username);//룸 이름 -> 그런데  학생 이름 넣어줘도 ->  채팅방에서  다시  받게 됨.
                 goto_teacher_chatting_activity.putExtra("Roomnumber", roomnumber);//룸넘버
                 goto_teacher_chatting_activity.putExtra("chattingroomtype", 1);//1대1 채팅 방 타입

             }//일대일 채팅방 끝 .

             //실행하는  선생님용 채팅방 뒤에  백스택에  만들어질  메인 엑티비티 선생님 용으로-> 선생님 로그인 상태니까
             Intent backIntent=new Intent(getBaseContext(),MainactivityForTeacher.class);
             backIntent.putExtra("checkbackstack", 1);

             //노티를 클릭시  teacherprofile과  백스택에 메인만 남겨두고  다른 스택은 모두 지워버린다.
             backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);

             //팬딩인텐트로  노티 클릭이 되었을 경우에 pendingintent로
             //requestcode 에는  -> currentTimeMills() 를  int로 cast해서 ->  각 pendingintent를  구분ㅅ켜줌
             //그렇게 안하면,  최근에 온  노티만 클릭이벤트가 작동한다.
             pendingIntent=PendingIntent.getActivities(getBaseContext(), (int) System.currentTimeMillis(), new Intent[]{backIntent,goto_teacher_chatting_activity}, PendingIntent.FLAG_ONE_SHOT);

         }//선생님 로그인 상태일때 끝.





          //커스톰  노티피케이션의 프로필 이미지  뷰에  위  비트맵화 시킨 이미지를 넣어줌.
          //customnotificationView.setImageViewBitmap(R.id.profileimgforteacherfcmalarm, bm);
          //커스톰 노티 피케이션에서  각 선생님의   이름 +현재 수업 가능함을 알려줌.
          //수업이 시작하면  로그인이  아닌 상태로 바꿀것이므로,   로그인 처리 했을 경우는  수업이 가능한  단계가 맞음.
          //customnotificationView.setTextViewText(R.id.teachernameforfcmalarm, message_content);
          //노티피케이션 빌더
          NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channel)
                  .setSmallIcon(R.drawable.sayhellologoblack)//스몰 아이콘
                  .setContentTitle(username)//유저의 이름 넣어줌.
                  .setSubText(chatting_room_name)//subtext  채팅방 이름
                  .setPriority(NotificationCompat.PRIORITY_DEFAULT)//우선순위 중간
                  .setLargeIcon(bm)//보낸 상대의 프로필 넣어줌.
                  .setContentIntent(pendingIntent)//클릭시 -> 진행되는 pendingintent
                  .setStyle(new NotificationCompat.BigTextStyle().bigText(message_content))//bigstext-> 긴  글도  펼쳐보기 형태로 다 볼수 있음
                  .setContentText(message_content)//위 경우처럼 긴글이 아닐때는 -> 그냗 텍스트 ㅏㄹ림.
                  .setDefaults(DEFAULT_SOUND)//sound는 없애줌.
                  .setAutoCancel(true);//클릭하면 사라짐.





          //오래오  이상  버전일때
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

              if (notichannel != null) {

                  //노티피케이션  채널  만들어냄
                  notichannel.createNotificationChannel(new NotificationChannel(channel, channel_nm,NotificationManager.IMPORTANCE_HIGH));

                  //notify id부분을  현재 시간으로 계속 다르게  넣어주니까  노티가  겹치지 않고  새롭게 뜨게됨.
                  notichannel.notify(id,notificationBuilder.build());

              }//노티피케이션  매니저  null값이  아닐때 조건 끝.

          }//오래오 이상 버전  일때 조건 끝.

      } catch (JSONException e) {
          e.printStackTrace();
      }



  }//make_chatting_message_push_noti_fcm_message()끝



    //서비스가 실행중인지  판단하기 위한 메소드
    private boolean isMyServiceRunning(Class<?> serviceClass) {

        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);//엑티ㅣ비티 매니져

        //엑티비티 매니저에서  실행중인 서비스 정보  객체 안에  -> 실행중인  서비스들  하나씩  넣어서 -> 해당 서비스와 -> 이름이 같은지  체크하기.
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {

            if (serviceClass.getName().equals(service.service.getClassName())) {//해당 서비스가  실행중인 서비스 중 하나와  이름이 같다면
                return true;//true로  -> 실행중임을 알린다.

            }
        }
        return false;//그외 경우 ->  false로 실행주이지 않을을 알린다.

    }//isMyServiceRunning() 메소드 끝,



  //커스톰 노티피케이션  만들어내기
  private void  makefcmcustomnotification(RemoteMessage remoteMessage){

    Log.v("checkfcm", "fcmmakefcmcustomnotification() 실행됨");

    //선생님 uid ->  notification id로 사용해서 -> 같은 아이디 인경우에는  노티를  없애지 않는한  중복  이므로,  새로운 노티를  띄지 않게 해준다.
    int teacheruid= Integer.parseInt(remoteMessage.getData().get("teacheruid"));

    //선생님 이름
    String teachername=remoteMessage.getData().get("teachername");

    //선생님 프로필 이미지 서버에 저장된 경로
    String teacherprofilepath=remoteMessage.getData().get("profilepath");

    //선생님 프로필  경로 이용해서  실제  프로필  경로  url 넣음.
    String teacherimageUrl = "http://13.209.249.1/"+teacherprofilepath;

    //노티피케이션  커스톰화 하기 위해서  ->  커스톰 뷰를 받아옴 -> remoteview를 사용함.
    RemoteViews customnotificationView=new RemoteViews(getPackageName(),R.layout.custom_teacherlogin_alarm_notification);

      //노티피케이션 메니저
      NotificationManager notichannel = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

      //노티 채널 id
      String channel = "teacherloginalarmfcmchannel";

      String channel_nm = "speakenglish";//사용자에게 보여지는 채널의 이름

      //프로필 이미지 가져와서  비트맵화 시킴.
      //여기서는 원래 glide쓸려고 했는데  비동기식이다 보니  노티가 보내질때  이미지가  있거나 안있거나 하는경우가 있음 이미지 받아오는 시간보다
      //노티가 빨리 가지면, 그래서 아래와 같이  버퍼 인풋스트림을 사용함.
      Bitmap bm = null;
      try {
          URL url = new URL(teacherimageUrl);
          URLConnection conn = url.openConnection();//
          conn.connect();//url과 커넥트함
          BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());//인풋스트림으로  받아오기 사진
          bm = BitmapFactory.decodeStream(bis);
          bis.close();//인풋스트립 닫음
         
      } catch (MalformedURLException e) {
          e.printStackTrace();
      } catch (IOException e) {
          e.printStackTrace();
      }



      //커스톰  노티피케이션의 프로필 이미지  뷰에  위  비트맵화 시킨 이미지를 넣어줌.
      customnotificationView.setImageViewBitmap(R.id.profileimgforteacherfcmalarm, bm);

      //커스톰 노티 피케이션에서  각 선생님의   이름 +현재 수업 가능함을 알려줌.
      //수업이 시작하면  로그인이  아닌 상태로 바꿀것이므로,   로그인 처리 했을 경우는  수업이 가능한  단계가 맞음.
      customnotificationView.setTextViewText(R.id.teachernameforfcmalarm, teachername+" 선생님이 현재 수업이 가능 합니다.");

      //노티피케이션을 클릭했을때   해당 선생님 프로필로 가기위한   처리
      //인텐트 ->  선생님 프로필  엑티비티  플래그
      Intent intent=new Intent(getBaseContext(),TeacherProfile.class);



      //intent로  선생님 프로필  내용을 담아서  보내준다.
      JSONObject jsonObject=new JSONObject(remoteMessage.getData());//map<string,string>으로 값을 받았으므로 -> 이경우  JSONOBJECT시킬수 있음.
      intent.putExtra("teacherinfo", String.valueOf(jsonObject));//map<string,string>에서  JSONobject로 변환된  -> 값  ->string 으로 변환해서 보내줌.
      intent.putExtra("teacherinfocheck", 3);//check값  3으로 보내서 teacherprofile에서  해당  값으로  처리하게 만들어준다.

       //실행하는  teacherprofile클래스 뒤에  백스택에  만들어질  메인 스택
      Intent backIntent=new Intent(getBaseContext(),MainactiviyForstudent.class);
      backIntent.putExtra("checkbackstack", 3);

      //노티를 클릭시  teacherprofile과  백스택에 메인만 남겨두고  다른 스택은 모두 지워버린다.
      backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);


      //팬딩인텐트로  노티 클릭이 되었을 경우에 pendingintent로
      PendingIntent pendingIntent=PendingIntent.getActivities(getBaseContext(), 0, new Intent[]{backIntent,intent}, PendingIntent.FLAG_ONE_SHOT);

      //노티피케이션 빌더
      NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channel)
              .setSmallIcon(R.drawable.sayhellologowhite)
              .setCustomContentView(customnotificationView)
              .setPriority(NotificationCompat.PRIORITY_DEFAULT)
              .setContentIntent(pendingIntent)
              .setDefaults(DEFAULT_SOUND)
              .setAutoCancel(true);

       //오래오  이상  버전일때
 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

          if (notichannel != null) {

              //노티피케이션  채널  만들어냄
              notichannel.createNotificationChannel(new NotificationChannel(channel, channel_nm,NotificationManager.IMPORTANCE_HIGH));

              //notify id부분을  현재 시간으로 계속 다르게  넣어주니까  노티가  겹치지 않고  새롭게 뜨게됨.


             notichannel.notify(teacheruid,notificationBuilder.build());

          }//노티피케이션  매니저  null값이  아닐때 조건 끝.

      }//오래오 이상 버전  일때 조건 끝.
  }//makefcmcustomnotification() 끝.



}//MyFirebaseMessagingService
