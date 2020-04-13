package com.example.leedonghun.speakenglish;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import static androidx.core.app.NotificationCompat.DEFAULT_SOUND;

/**
 * 선생님 학생   예약  수업  진행시  해당  수업 10분전 수업이 있다는걸  알리기위한  브로드 캐스트
 *
 * */
public class Broadcast_for_alarm_reservation_class extends BroadcastReceiver {



    @Override
    public void onReceive(Context context, Intent intent) {

          int teacher_or_student=intent.getIntExtra("student_or_teacher", -1);

          if(teacher_or_student==1) {//학생에게 알리는  알람 실행

              //선생님 이름
              String teacher_name = intent.getStringExtra("teacher_name");


              //수업을 위해  앱  메인  가기
              Intent goto_stucent_mainactivity = new Intent(context, MainactiviyForstudent.class);
              goto_stucent_mainactivity.putExtra("checkbackstack", 3);//선생님 찾기 프래그먼트로 실행

              //노티를 클릭시  예약  리스트 엑티비티와  백스택에 메인만 남겨두고  다른 스택은 모두 지워버린다.
              goto_stucent_mainactivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

//        //팬딩인텐트로  노티 클릭이 되었을 경우에 pendingintent로
//        //requestcode 에는  -> currentTimeMills() 를  int로 cast해서 ->  각 pendingintent를  구분ㅅ켜줌
//        //그렇게 안하면,  최근에 온  노티만 클릭이벤트가 작동한다.
              PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), goto_stucent_mainactivity, PendingIntent.FLAG_ONE_SHOT);

              //받은 데이터를 가지고  노티 작성
              //노티피케이션 메니저
              NotificationManager notichannel = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

              //노티 채널 id
              int id = (int) System.currentTimeMillis();
              String channel = "reservation_alert";


              String channel_nm = "speakenglish_reservation";//사용자에게 보여지는 채널의 이름



              //customnotificationView.setImageViewBitmap(R.id.profileimgforteacherfcmalarm, bm);
              //커스톰 노티 피케이션에서  각 선생님의   이름 +현재 수업 가능함을 알려줌.
              //수업이 시작하면  로그인이  아닌 상태로 바꿀것이므로,   로그인 처리 했을 경우는  수업이 가능한  단계가 맞음.
              //customnotificationView.setTextViewText(R.id.teachernameforfcmalarm, message_content);
              //노티피케이션 빌더
              NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channel)
                      .setSmallIcon(R.drawable.sayhellologoblack)//스몰 아이콘
                      .setContentTitle("수업 임박!")//유저의 이름 넣어줌.
                      .setSubText("speakenglish")//subtext  채팅방 이름
                      .setPriority(NotificationCompat.PRIORITY_DEFAULT)//우선순위 중간
                      .setContentIntent(pendingIntent)//클릭시 -> 진행되는 pendingintent
                      .setStyle(new NotificationCompat.BigTextStyle().bigText(teacher_name + "선생님과의  수업 10분전입니다!\n 까먹지 말고  수업 하세요!"))//bigstext-> 긴  글도  펼쳐보기 형태로 다 볼수 있음
                      .setContentText(teacher_name + "선생님과의  수업 10분전입니다!\n까먹지 말고 수업 하세요!")//위 경우처럼 긴글이 아닐때는 -> 그냗 텍스트 ㅏㄹ림.
                      .setDefaults(DEFAULT_SOUND)//sound는 없애줌.
                      .setAutoCancel(true);//클릭하면 사라짐.


              //오래오  이상  버전일때
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                  if (notichannel != null) {

                      //노티피케이션  채널  만들어냄
                      notichannel.createNotificationChannel(new NotificationChannel(channel, channel_nm, NotificationManager.IMPORTANCE_HIGH));

                      //notify id부분을  현재 시간으로 계속 다르게  넣어주니까  노티가  겹치지 않고  새롭게 뜨게됨.
                      notichannel.notify(id, notificationBuilder.build());

                  }//노티피케이션  매니저  null값이  아닐때 조건 끝.

              }//오래오 이상 버전  일때 조건 끝.

          }//학생 알람일때
          else if(teacher_or_student==0){//선생님에게 알리는 알람 실행


              //학생 이름
              String student_name = intent.getStringExtra("student_name");


              //수업을 위해  앱  메인  가기
              Intent goto_teachermain = new Intent(context, MainactivityForTeacher.class);


              //노티를 클릭시  예약  리스트 엑티비티와  백스택에 메인만 남겨두고  다른 스택은 모두 지워버린다.
              goto_teachermain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

//        //팬딩인텐트로  노티 클릭이 되었을 경우에 pendingintent로
//        //requestcode 에는  -> currentTimeMills() 를  int로 cast해서 ->  각 pendingintent를  구분ㅅ켜줌
//        //그렇게 안하면,  최근에 온  노티만 클릭이벤트가 작동한다.
              PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), goto_teachermain, PendingIntent.FLAG_ONE_SHOT);

              //받은 데이터를 가지고  노티 작성
              //노티피케이션 메니저
              NotificationManager notichannel = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

              //노티 채널 id
              int id = (int) System.currentTimeMillis();
              String channel = "reservation_alert";


              String channel_nm = "speakenglish_reservation";//사용자에게 보여지는 채널의 이름



              //customnotificationView.setImageViewBitmap(R.id.profileimgforteacherfcmalarm, bm);
              //커스톰 노티 피케이션에서  각 선생님의   이름 +현재 수업 가능함을 알려줌.
              //수업이 시작하면  로그인이  아닌 상태로 바꿀것이므로,   로그인 처리 했을 경우는  수업이 가능한  단계가 맞음.
              //customnotificationView.setTextViewText(R.id.teachernameforfcmalarm, message_content);
              //노티피케이션 빌더
              NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channel)
                      .setSmallIcon(R.drawable.sayhellologoblack)//스몰 아이콘
                      .setContentTitle("You have a Class!")//유저의 이름 넣어줌.
                      .setSubText("speakenglish")//subtext  채팅방 이름
                      .setPriority(NotificationCompat.PRIORITY_DEFAULT)//우선순위 중간
                      .setContentIntent(pendingIntent)//클릭시 -> 진행되는 pendingintent
                      .setStyle(new NotificationCompat.BigTextStyle().bigText("you have a class with "+student_name+" after 10 min \n dont't forget your class!!"))//bigstext-> 긴  글도  펼쳐보기 형태로 다 볼수 있음
                      .setContentText("you have a class with "+student_name+" after 10 min \n dont't forget your class!!")//위 경우처럼 긴글이 아닐때는 -> 그냗 텍스트 ㅏㄹ림.
                      .setDefaults(DEFAULT_SOUND)//sound는 없애줌.
                      .setAutoCancel(true);//클릭하면 사라짐.


              //오래오  이상  버전일때
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                  if (notichannel != null) {

                      //노티피케이션  채널  만들어냄
                      notichannel.createNotificationChannel(new NotificationChannel(channel, channel_nm, NotificationManager.IMPORTANCE_HIGH));

                      //notify id부분을  현재 시간으로 계속 다르게  넣어주니까  노티가  겹치지 않고  새롭게 뜨게됨.
                      notichannel.notify(id, notificationBuilder.build());

                  }//노티피케이션  매니저  null값이  아닐때 조건 끝.

              }//오래오 이상 버전  일때 조건 끝.






          }//선생님에게 알리는 알람 실행 끝.




    }//onReceive () 끝

}//브로드캐스트리시버 끝
