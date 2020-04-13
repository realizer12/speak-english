package com.example.leedonghun.speakenglish;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Manager;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Date;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * speakenglish
 * Class: ServiceForGetChattingData.
 * Created by leedonghun.
 * Created On 2019-12-13.
 * Description: 채팅 데이터를 백그라운드에서도 계속 가지고온다.
 * 채팅의 연결을 -> 백그라운드에서 시킬때 ->  채팅방에서는 새롭게  연결된다.
 * 채팅방에서 연결시에  백그라운드 연결은 취소되고 해당 채팅방 연결만 지속된다.
 *
 *
 */
public class ServiceForGetChattingData extends Service {

    //로그용 -> 현재 서비스 클래스 이름.
    private final static String TAG="ServiceForGetChattingData";//1-0

    Socket socket_for_openchat;// 서버 연결 위한 소켓- 오픈 채팅용  /1-1
    Socket socket_for_onechat;//서버 연결 위한 소켓 - 일대일 채팅용 /1-1-1

    ArrayList<JSONObject> chattingdata;// json으로 만들어진 chatting 데이터가  담길  어레이 리스트//1-2
    String iport="http://13.209.249.1:8888";//ip 연결

    String student_uid;//학생용 uid를 담기 위한  변수.
    String teacher_uid;//선생님 uid를 담기 위한  변수.

    String teacherloginedid;//선생님 로그인 이메일 담을 객체
    String studentloginedid;//학생  로그인 이메일 담을 객체
    String loginid_for_sql;//로그인한 유저의 이메일에서 @를 뺀 sqlite database 이름.


    SqLiteOpenHelperClass sqLiteOpenHelper;//sqlite 생성하는 클래스
    SQLiteDatabase database;//sqlite 데이터베이스

    ArrayList<JsonObject>userroomlsit;//유저가 들어가 있는 방 전체 값.

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }//bind 용으로 쓰는 메소드 -> 지금은 null


    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("check", TAG+"의  onCreate() 실행됨");

    }//onCreate() 끝


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("check", TAG+"의 onStartCommand() 실행됨");


        try {
            Manager  manager = new Manager(new URI(iport));//소켓 라이브러리에서  매니저

            //오픈 채팅 소켓 연결 코드
            if(socket_for_openchat == null ) {//소켓이  null값 일때만  연결시켜서  -> 불필요한 소켓 연결을 줄인다.

                socket_for_openchat = manager.socket("/openchat");//서버 채팅용 socket  네임스페이스중 openchat에 연결한다.

            }//오픈 채팅  소켓 연결 코드 끝


            //일대일 채팅 소켓 연결 코드
            if(socket_for_onechat ==null) {

                socket_for_onechat = manager.socket("/onechat");//서버 채팅 네임스페이스 중 /onechat에 연결

            }//일대일 채팅 소켓 연결 코드 끝.

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


        GlobalBus.getBus_data().register(this);//이벤트 버스 등록시킴.-> 서비스가 시스템에 의해 강제 종료 후에 시작되면  oncraete 가 아니라 -> onStartcommmand에서  시작될수 있어 누락될까봐

        SharedPreferences getid_teacher = getSharedPreferences("loginteacherid",MODE_PRIVATE);//로그인 아이디  쉐어드에 담긴거 가져옴
        teacherloginedid= getid_teacher.getString("loginidteacher","");//로그인 아이디 가져옴

        SharedPreferences getid_student = getSharedPreferences("loginstudentid", MODE_PRIVATE);//로그인 아이디  쉐어드에 담긴거 가져옴
        studentloginedid = getid_student.getString("loginid", "");//로그인 아이디 가져옴

              if(teacherloginedid.equals("")){//선생님 로그인이 아닐때-> 학생 로그인

                  Log.v("check", studentloginedid+"학생입니다 ");
                  getstudentinfo(studentloginedid);//학생 uid정보를 가지고 오고  ->  이 메소드안에서  모든  참여 방에 대한 join과  메세지 받기 가  이루워 지는 메소드로 연결된다. .

                  loginid_for_sql=studentloginedid.replaceAll("@", "");
                  sqLiteOpenHelper=new SqLiteOpenHelperClass(ServiceForGetChattingData.this,loginid_for_sql , null,1 );
                  database=sqLiteOpenHelper.getWritableDatabase();




              }else{//학생 로그인 아닐때 선생님 로그인-> 선생님 로그인

                  Log.v("check", teacherloginedid+"선생님입니다. ");
                  getteacherinfo(teacherloginedid);//선생님 uid정보를 가지고 오는 메소드 -> 여기서소켓 참여 메소드까지 연결된다.

                  loginid_for_sql=teacherloginedid.replaceAll("@", "");
                  sqLiteOpenHelper=new SqLiteOpenHelperClass(ServiceForGetChattingData.this,loginid_for_sql , null,1 );
                  database=sqLiteOpenHelper.getWritableDatabase();

              }


        return START_STICKY;// 이걸로 하면  강제 종료시 -> 다시 서비스를 시작 할때  INTENT  없이  실행될수 있도록 만들어줌.
    }//onStartCommand() 끝



    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("check", TAG+"의  onDestroy() 실행됨");

        if(socket_for_openchat.connected()) {//오픈 채팅용 소켓이 connect되어 있다면,

            Log.v("check",TAG+"에서 오픈 채팅 소켓 연결되어있음->  disconnect 실행");
            socket_for_openchat.disconnect();//오픈용 채팅  disconnect해줌.
        }

        if(socket_for_onechat.connected()) {//일대일 채팅용 소켓이  connnect되어있다면,

            Log.v("check", TAG + "에서 1대1 채팅 소켓 연결되어있음->  disconnect 실행");
            socket_for_onechat.disconnect();//1대1용 채팅 disconnect해줌

        }


        GlobalBus.getBus_data().unregister(this);//이벤트 버스 등록해제

            //오픈 채팅이랑  일대일 채팅 다 꺼줌.
            //일단 일대일 채팅 이 연결되어있다면,

    }//onDestroy() 끝


    //선생님 정보 가져오기 위한  메소드,
    private void getteacherinfo(String teacheremail){

        Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create())//json으로 받고 싶으면 쓰면안됨..
                .build();//리트로핏 뷸딩
        ApiService apiService = retrofit.create(ApiService.class);//api인터페이스 크리에이트
        Call<teacherinforesult> getteacherinfo=apiService.sendemailtogetteacherprofile(teacheremail);//선생님 정보 서버로부터 가져오기위한 call 객체

        getteacherinfo.enqueue(new Callback<teacherinforesult>() {
            @Override
            public void onResponse(Call<teacherinforesult> call, Response<teacherinforesult> response) {

                  teacher_uid=response.body().getTeacheruid();//선생님 uid
                  Log.v("check", TAG+"에서  선생님 UID를 받아왔습니다. ->  선생님 UID: "+teacher_uid);

                  usergetroomlist(teacher_uid);

            }//onResponse()끝

            @Override
            public void onFailure(Call<teacherinforesult> call, Throwable t) {

                Log.v("check", TAG+"에서   getteacherinfo-> 선생님 정보 가져오기실팽함 -> 에러 내용 ->"+t);


            }//onFailure() 끝
        });//getteacherinfo 끝



    }//getteacherinfo()끝




    //학생 정보  가져오기 위한  메소드
    private void getstudentinfo(String studentemail){

        Log.v("check", TAG+"에서 학생 정보를 가지고 오기 위한 getsudentinfo() 실행됨.");


        Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create())//json으로 받고 싶으면 쓰면안됨..
                .build();//리트로핏 뷸딩
        ApiService apiService = retrofit.create(ApiService.class);//api인터페이스 크리에이트

        Call<studentinforesult> getstudentinfo=apiService.sendemailtogetiprofile(studentemail);//학생정보 얻기위한  call객체

        //callback 실행.
        getstudentinfo.enqueue(new Callback<studentinforesult>() {
            @Override
            public void onResponse(Call<studentinforesult> call, Response<studentinforesult> response) {
                Log.v("check", TAG+"의  getstudentinfo-> 학생정보  가져오기 성공"+response.body().toString());
                if(response.body() != null){//response가 0이 아닐때,

                    student_uid=response.body().getUid();//5-5
                    Log.v("check", TAG+"에서  학생 UID를 받아왔습니다. ->  학생 UID: "+student_uid);

                   usergetroomlist(student_uid);//학생 참여한  방 리스트 가져오기



                }//response !=nulll 조건 끝

            }//onResponse()끝

            @Override
            public void onFailure(Call<studentinforesult> call, Throwable t) {

                Log.v("check", TAG+"에서   getstudentinfo-> 학생정보 가져오기실팽함 -> 에러 내용 ->"+t);

            }//onFailure끝
        });//학생 정보 가져오기 callback 함수 끝

    }//getstudentinfo() 끝




    //현재 채팅 서버에  맨처음 소켓연결을 진행하는  메소드
    private void usergetroomlist(String useruid) {

        Log.v("check", TAG+ "의 studentmakechatconnection() 함수 실행됨");

        //먼저  해당 uid가  참여하고  joinedornot이  1이 아닌 0인  모든 채팅방의  roomnumber와  namespace를 가지고 온다.

        //jsonobject의  어레이이므로 -> gson을 사용한다.
        GsonBuilder gsonBuilder=new GsonBuilder();
        gsonBuilder.setLenient();
        Gson gson=gsonBuilder.create();//gson객체 만들어냄.


        Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create(gson))
                .build();//리트로핏 뷸딩
        ApiService apiService = retrofit.create(ApiService.class);//api인터페이스 크리에이트

        //방  리스트 받아오는 call객체선언
        final Call<GetRoomList> getuserjoined_room_list=apiService.get_user_joined_room_list(useruid);
         getuserjoined_room_list.enqueue(new Callback<GetRoomList>() {
             @Override
             public void onResponse(Call<GetRoomList> call, Response<GetRoomList> response) {

                  //해당 룸값  받아옴.
                 userroomlsit=response.body().getRoomlist_data_for_user();
                 try {

                     usermakeconnection(userroomlsit);//채팅 서버에 유저를 연결 시켜주는 메소드에  룸  리스트 넣어줌.

                 } catch (URISyntaxException e) {
                     e.printStackTrace();
                 }

             }//onResponse()끝


             @Override
             public void onFailure(Call<GetRoomList> call, Throwable t) {

             }//onFailure() 끝


         });//eque 끝
    }//studetmakechatconnection() 끝


    //해당  유저가  참여한  모든 방에  connecion을 해준다. -> 혹시 모르니까  joinedornot 의 값을 0인것만  체크 하는 코드 추가
    private  void usermakeconnection(final ArrayList<JsonObject>userroomlsit) throws URISyntaxException {
        Log.v("check", TAG+"에서 유저  참여 룸  리스트 ->"+userroomlsit);





        //해당유저의   룸  정보 가져온것들을  하나씩  for문으로 돌려서 ->  방에  join 시켜준다.
        for(int i=0; i<userroomlsit.size(); i++){

            String roomjoinedorjot=userroomlsit.get(i).get("roomjoinedornot").toString().replaceAll("\"", "");//유저가 방에 참여 한여부 ->  0(참여안함) 인 방등만  서비스에  받아준다.
            final String uid=userroomlsit.get(i).get("useruid").toString().replaceAll("\"", "");//유저의 uid
            final String chattingroomnumber=userroomlsit.get(i).get("roomnumber").toString().replaceAll("\"", "");//유저가 참여한  방번호
            final String userposition=userroomlsit.get(i).get("userposition").toString().replaceAll("\"", "");//유저의 직업
            String roomnamespace=userroomlsit.get(i).get("roomnamespace").toString().replaceAll("\"", "");//해당 방의   룸-> 오픈 or 1:1챗방



            //해당방에 joine하지 않은 경우-  해당 방에 조인 했을 경우에는 새로운 소켓을  생성해서 통신하므로,   여기서는 1(방에 참여한 경우)를 제외한
            //나머지  경우들을  다룸.
            if(roomjoinedorjot.equals("0")){

                if(roomnamespace.equals("0")){//해당 방  오픈 채팅방일때


                    socket_for_openchat.connect();//서버 소켓 연결 시도

                    socket_for_openchat.once(Socket.EVENT_CONNECT, new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {


                            //connection과 함께  해당 방에  들어가기 위해서  join함수로  방과  정보를 보내준다.
                            socket_for_openchat.emit("join",uid ,chattingroomnumber,userposition,1);
                        }//call끝
                    });//소켓  연결후 첫 이벤트로 connection이벤트를 날린다.- 오픈 채팅방


                }else if(roomnamespace.equals("1")){//해당방  일대일 채팅방일때때


                    socket_for_onechat.connect();//서버 소켓 연결 시도
                    socket_for_onechat.once(Socket.EVENT_CONNECT, new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {

                            //세번 째 파라미터 -1은 원래 일대일 채팅이므로 상대 유저에 이름이 들어가는데  이곳엣는  그저  백그라운드에서  소통만  유지하기 위함이므로,
                            //쓰레기값인 -1을 넣어줬다.
                            socket_for_onechat.emit("join",uid ,chattingroomnumber,userposition,"-1",1);

                        }//call끝
                    });//소켓  연결후 첫 이벤트로 connection이벤트를 날린다-1대1


                }//1대1 채팅 방일때  끝.

            }else if(roomjoinedorjot.equals("1")){//해당 에 join한 경우일때.

                //솔직히 -> 해당 채팅방에 참여하면  서비스는  stop되게 만들어나서 상관 없지만,  혹시나 해서 코드 추가 해둠.
                Log.v("check", TAG+"에서 유저아이디"+uid+"가 "+chattingroomnumber+"에서는 참여중인 값 1이어서  해당 방은  서비스에서  채팅 내용을 받지 않음.");

            }//해당방에 join한 경우 끝
        }//userroomlist  for문  끝


        //다른 오픈 채팅 클라이언트가  broadcast한 내용을 받는 리스너이다.
        //이경우 ->  새로들어온 참여자가 있거나, 방을 나가는 참여자가 있을 경우 실행된다.
        socket_for_openchat.on("messagebroadcast", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {


                        try {

                            //소켓 서버로부터 해당 값들을 json으로 받는다.
                            final JSONObject receivedata_openchat = new JSONObject(args[0].toString());

                            //이벤트 버스 -> 메인  스레드 루퍼 가지고 와서 돌림 ->  안쓰면  메인 쓰레드 관련 에러남
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    GlobalBus.getBus_data().post(receivedata_openchat);;//이벤트 버스로  -> 백그라운드 채팅 데이터 보냄.
                                }
                            });


                            //chattingdata.add(receivedata);//메세지 들어온거  chattingdata어레이에 넣어줌.
                            Log.v("check", TAG+"의 오픈채팅방에서 받은 현재 채팅 데이터 내용-백그라운드용-> "+receivedata_openchat.toString());

                            //보낸 사람의  uid
                            String senderuid=receivedata_openchat.getString("id");

                            //오픈 채팅방 =0 or 1대1 =1   여부
                            String roomnamespace=receivedata_openchat.getString("roomnamespace");

                            //채팅방 번호.
                            String roomnumber=receivedata_openchat.getString("roomnumber");


                            //보낸사람의 position ( t or  s)
                            String senderposition=receivedata_openchat.getString("userposition");

                            //보낸 사람의 이름.
                            String sendername=receivedata_openchat.getString("name");

                            //보낸 사람의  프로필 이미지
                            String profile=receivedata_openchat.getString("profile");

                            //viewtype에 따라  메세지를 분류하고, 그에 맞는 처리를 해준다.
                            String viewtype=receivedata_openchat.getString("viewtype");

                            //메세지  보낸 날짜
                            String date=receivedata_openchat.getString("date");

                            //해당 채팅방에서 현재 채팅의 순서.
                            String chatting_order=receivedata_openchat.getString("chatorder");

                            //해당 채팅의 메세지 내용
                            String chatting_message=receivedata_openchat.getString("message");

                            //여기서는 오픈 채팅방일때 -> 해당 뷰타입 -> 나가는것 들어오는것  그냥 채팅 메세지  에따라서  처리가 달라진다.
                            //viewtype 3 의 경우 teachername이  json데이터 에  포함되어있으므로, teachername을 받아야 하지만,
                            //나머지 나가거나 들어오는 viewtype(1, 2)들은  ->  teachername이  데이터에 포함되어있지 않아서  새로 쓰레기값을 넣은  string변수를 만들어줬다.
                            String teachername=null;
                            if(viewtype.equals("1")||viewtype.equals("2")){//나가기 또는 들어오기 메세지 타입

                                teachername="1";//teachername이 필요x 이므로, 쓰레기값을 1넣어줌.

                            }else if(viewtype.equals("3")){//채팅메세지 타입일때.

                                teachername=receivedata_openchat.getString("teachername");
                            }

                            int read_or_not= 1;//unread이다.  read는  0임.

                            //백그라운드에서 받은 채팅 데이터  sqlite에  넣어줌.
                            sqLiteOpenHelper.inserChattingData(database,senderuid,roomnamespace,roomnumber ,teachername,senderposition ,sendername ,profile ,viewtype, date,chatting_order,chatting_message,read_or_not);

                        }catch (JSONException e) {

                            e.printStackTrace();

                        }

            }//call() 끝
        });//오픈채팅용 백그라운드 socket messagebroadcast 끝


        //다른 일대일 채팅 클라이언트가  broadcast한 내용을 받는 리스너이다.
        //이경우 ->  새로들어온 참여자가 있거나, 방을 나가는 참여자가 있을 경우 실행된다.//메세지를 받을때도.
        socket_for_onechat.on("messagebroadcast", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {


                        try {

                                 //이벤트 버스 -> 메인  스레드 루퍼 가지고 와서 돌림 ->  안쓰면  메인 쓰레드 관련 에러남
                                final JSONObject receivedata_one_to_one = new JSONObject(args[0].toString());

                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        GlobalBus.getBus_data().post(receivedata_one_to_one);;//이벤트 버스로  -> 백그라운드 채팅 데이터 보냄.
                                    }
                                });


                            //여기서 메세지를 받자마자 바로  sqlite에다가저장 시켜준다.
                            //chattingdata.add(receivedata);//메세지 들어온거  chattingdata어레이에 넣어줌.
                            Log.v("check",TAG+"의 1대1 채팅 현재 받은 채팅데이터- 백그라운드용 -> "+ receivedata_one_to_one.toString());

                            //보낸 사람의  uid
                            String senderuid=receivedata_one_to_one.getString("id");

                            //오픈 채팅방 =0 or 1대1 =1   여부
                            String roomnamespace=receivedata_one_to_one.getString("roomnamespace");

                            //채팅방 번호.
                            String roomnumber=receivedata_one_to_one.getString("roomnumber");

                            //선생님 이름 - 오픈 채팅방에서만 사용됨.
                            String teachername=null;

                            if(roomnamespace.equals("0")){//오픈 채팅방일때 jsonobject에 들어있는 값 그대로 "teachername" 쓰면됨
                                teachername=receivedata_one_to_one.getString("teachername");

                            }else if(roomnamespace.equals("1")){//1대1 채팅방일때 -> 이때는  teachername이  jsononject에 들어있지 않다. // 그래도 값을  너줘야  query 를 날리고 에러가 안난다.
                                teachername="1";//값을 1넣어줌.
                            }

                            //보낸사람의 position ( t or  s)
                            String senderposition=receivedata_one_to_one.getString("userposition");

                            //보낸 사람의 이름.
                            String sendername=receivedata_one_to_one.getString("name");

                            //보낸 사람의  프로필 이미지
                            String profile=receivedata_one_to_one.getString("profile");

                            //viewtype에 따라  메세지를 분류하고, 그에 맞는 처리를 해준다.
                            String viewtype=receivedata_one_to_one.getString("viewtype");

                            //메세지  보낸 날짜
                            String date=receivedata_one_to_one.getString("date");

                            //해당 채팅방에서 현재 채팅의 순서.
                            String chatting_order=receivedata_one_to_one.getString("chatorder");

                            //해당 채팅의 메세지 내용
                            String chatting_message=receivedata_one_to_one.getString("message");

                            int read_or_not= 1;//unread이다.  read는  0임.


                            //일대일 채팅방에   첫번째  -> 멘트가 날라올때는  -> fcm에서  날라온  값을  sqlite에  저장해주므로,
                            //서비스에서  해당 메세지를 바더라도 저장해주지 않는다.
                            //왜냐하면, 1대1 채팅방 첫메세지 받는 경우엔  해당 룸에  소켓이 항상  연결되어있지 않을수 있기 때문이다.



                            if(chatting_order.equals("1") && roomnamespace.equals("1") ){



                            }else{//위의 경우를 제회한 모든  경우-> 서비스에서 받은 채팅 내용을  sqlite에 넣어준다.

                                //백그라운드에서 받은 채팅 데이터  sqlite에  넣어줌.
                                sqLiteOpenHelper.inserChattingData(database,senderuid,roomnamespace,roomnumber ,teachername,senderposition ,sendername ,profile ,viewtype, date,chatting_order,chatting_message,read_or_not);
                            }


                        }catch (JSONException e) {

                            e.printStackTrace();

                        }//catch()끝


            }//call() 끝
        });//socket messagebroadcast 끝/


        //학생이 1대1  채팅방  나갔을 경우에 -> 해당 채팅방과 연결된 선생님의  채팅방  리스트에서 해당 채팅방을 바로 못들어가게  조치를 시키기위해
        //나간 broadcast를 받고 event 버스로 보내주기위한  코드이다.
        socket_for_onechat.on("outbroadcast", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                final JSONObject jsonObject=new JSONObject();
                try {
                    jsonObject.put("onetooneout", "1");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.v("CHECKKKKKKKK", "FSFSDFSDFSDFSDFSDFDS");
                //이벤트 버스 -> 메인  스레드 루퍼 가지고 와서 돌림 ->  안쓰면  메인 쓰레드 관련 에러남
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        GlobalBus.getBus_data().post(jsonObject);;//이벤트 버스로  -> 백그라운드 채팅 데이터 보냄.
                    }
                });
            }
        });//나간 broadcast를 받고 event 버스로 보내주기위한  코드 끝

    }//Sstudentmakeconnection() 끝

}//ServiceForGetChattingData 서비스 끝.
