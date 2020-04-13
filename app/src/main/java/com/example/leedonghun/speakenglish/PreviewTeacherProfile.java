package com.example.leedonghun.speakenglish;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
//import android.support.v7.widget.Toolbar;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;

/**
 * speakenglish
 * Class: PreviewTeacherProfile.
 * Created by leedonghun.
 * Created On 2019-02-02.
 * Description:
 * 선생님 프로필 수정하는 곳에서 해당 수정 내용을
 * 바로  학생이 보는 화면에서 볼수 있는 구간이다.
 * 해당 강사를  내 강사로 등록할지 안할지를 하트클릭을 통해서 해결할수 있다,.
 *
 */
public class PreviewTeacherProfile extends AppCompatActivity {
    int a=0;//선생님 나라가  country.json에  위치하는지 여부를 체크 하기 위한  int 값
    String cLIST;//country리스트에서 컨트리이름을  담을  변수

    ImageView whiteheariconentire;// 하얀색 버튼 전체 꽉참
    ImageView whitehearticon;//하얀색 테두리 하트 버튼

    TextView teachernameandglobalcheck;//선생님의 이름과  글로벌 여부
    TextView teachercareerbox;//선생님의  커리어
    TextView teacherdearstudentbox;//선생님이  학생에게  써주는 말

    ImageView checkedicon;//체크가 된 아이콘
    ImageView uncheckedicon;//체크가 안된 아이콘
    ImageView profileimage;

    TextView onesentenceforstudenttotable;//선생님이 학생들에게 쓰는 한  문장

    Button startclassbtn;//수업 시작 버튼
    Button messengerbtn;//1:1메신저 버튼
    Button openchattingbtn;//오픈채팅  버튼

    Toastcustomer toastcustomer;
    ScrollView scrollViewforprevieactivity;
    Toolbar toolbar;
    int Checkforvisibleheart;
    TabHost tabHost;

    TextView whoisteacherment;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.previewteacheprofile);
        Log.v("lifecycle","oncreate 실행");

          toastcustomer=new Toastcustomer(getApplicationContext());
          whiteheariconentire=(ImageView)findViewById(R.id.whiteheartentire);//하얀 꽉찬 하트 선언
          whitehearticon=(ImageView)findViewById(R.id.whiteheart);// 하얀색 하트  테두리 선언

          teachernameandglobalcheck=(TextView)findViewById(R.id.nameandglobalcheck);//선생님 이름과 글로벌 체크
          teachercareerbox=(TextView)findViewById(R.id.careertextbox);//선생님 경력  박스.
          teacherdearstudentbox=(TextView)findViewById(R.id.dearstudenttextbox); //선생님이 학생에게 하는말  박스.

          checkedicon=(ImageView)findViewById(R.id.checkedalarmlogin);//선생님 로그인시 알람 체크 아이콘
          uncheckedicon=(ImageView)findViewById(R.id.uncheckedalarmlogin);//선생님 로그인  알람체크 취소 아이콘

          whoisteacherment=(TextView)findViewById(R.id.whoisteachermenttext);//선생님 소개글  제목 멘트

          onesentenceforstudenttotable=(TextView)findViewById(R.id.onesentenceforstudent);//선생님이 말하는 자기  수업에대한  문장;
          startclassbtn=(Button)findViewById(R.id.startclassbutton);//수업 시작  버튼
          messengerbtn=(Button)findViewById(R.id.messengerbutton);//1:1 메신저 버튼
          openchattingbtn=(Button)findViewById(R.id.openchattingbutton);//오픈 채팅 시작 하는 버튼

          scrollViewforprevieactivity=(ScrollView)findViewById(R.id.scrollView3);//이 엑티비티에 쓰인  스크롤뷰 선언.
          toolbar=(Toolbar)findViewById(R.id.toolbarforupdateprofileteacher2);//툴바
          profileimage=(ImageView)findViewById(R.id.profileimage);//프로필 이미지

          Intent intent=getIntent();//이 엑티비티로 날아온 인텐트들 겟 하기.

          String teachername = intent.getStringExtra("teachername");//선생님 이름을 받은 인텐트
          byte[] arr = intent.getByteArrayExtra("teacherphotobitmap");//bitmap  바이트 인텐트 받음.
          Bitmap image = BitmapFactory.decodeByteArray(arr, 0, arr.length);//해당 바이트 비트맵으로 연결
          String teachercountry123 = intent.getStringExtra("teachercountry");//선생님  나라 받아옴.
          //String b = String.Valueof(parseIntent.getStringExtra("넘어온 인텐트"))

          profileimage.setImageBitmap(image);//프로필 이미지 비트맵 형식으로 연결 해서 보여줌.


          String teachercareer=intent.getStringExtra("teachercareer");//선생님 커리어 받아옴.
          teachercareerbox.setText(teachercareer);//선생님 커리어 연결 시켜줌.

          String teachersayhello=intent.getStringExtra("teachersayhellow");//선생님 학생들에게 하는 인사
          teacherdearstudentbox.setText(teachersayhello);//인사  부분 연결시켜줌

          String teacheronesentence=intent.getStringExtra("teacheronesentence");//선생님 자기 클래스 한마디
          onesentenceforstudenttotable.setText(teacheronesentence);//한마디 해당 텍스트 뷰에 연결 시켜줌.

           String whoisment="who is "+teachername+" tutor?";
          whoisteacherment.setText(whoisment);


        View.OnClickListener onClickListener=new View.OnClickListener() {// 뷰 클릭 리스터 이벤트(버튼, 이미지뷰, 텍스트뷰 등등)
            @Override
            public void onClick(View view) {
                switch (view.getId()){


                    case  R.id.whiteheart://하얀색 테두리 하트를 누를때


                        //꽉찬  하얀색 하트 보여지고  나머지 하트들은 다 안보이는 상태로 바뀐다.
                        //그리고 내 튜터리스트에  추가되었다는 토스트를 보여준다.
                        whiteheariconentire.setVisibility(View.VISIBLE);
                        whitehearticon.setVisibility(View.INVISIBLE);
                        toastcustomer.showcustomtaost(null,"registered on my tutor list!");
                        Checkforvisibleheart=0;  //하트 체크 0의 경우 -> 하얀 꽉찬 하트 보이는 경우
                        Log.v("buttoncheck","하얀색 테두리 하트를 눌렀음");

                        break;


                    case R.id.whiteheartentire://하얀색 꽉찬하트를 누를때


                        //하얀색 테두리 하트 보여지고  나머지 하트들은 다 안보이는 상태로 바뀐다.
                        //그리고 내 튜터리스트에  추가되었다는 토스트를 보여준다.
                        whiteheariconentire.setVisibility(View.INVISIBLE);
                        whitehearticon.setVisibility(View.VISIBLE);
                        toastcustomer.showcustomtaost(null,"Except on my tutor list");
                        Checkforvisibleheart=1; //하트 체크 1의 경우 -> 하얀 테두리 보이는 경우
                        Log.v("buttoncheck","하얀색 꽉찬 하트를 눌렀음");

                        break;

                    case R.id.startclassbutton://수업 시작 버튼을 눌렀을때


                        toastcustomer.showcustomtaost(null,"startclass");
                        Log.v("buttonforstartclass","수업시작 버튼 눌림");

                        break;

                    case R.id.messengerbutton://메신저  버튼  눌렀을때

                        toastcustomer.showcustomtaost(null,"messengerbtn");
                        Log.v("buttonformessengetbtn","1:1대화 시작");


                        break;

                    case R.id.openchattingbutton://오픈 채팅 버튼 눌렀을때

                        toastcustomer.showcustomtaost(null,"openchattingbtn");
                        Log.v("buttonforopenchatting","오픈 채팅 시작");


                        break;


                    case R.id.checkedalarmlogin://로그인 알람 언체크

                        checkedicon.setVisibility(View.INVISIBLE);//체크된 아이콘 안보이기
                        uncheckedicon.setVisibility(View.VISIBLE);//체크 안될때 아이콘 보이기
                        toastcustomer.showcustomtaost(null,"uncheckalarm");
                        Log.v("checkicon","알람 체크 아이콘 시작");

                         break;

                   case R.id.uncheckedalarmlogin:// 알람 로그인 취소

                       checkedicon.setVisibility(View.VISIBLE);//체크된 아이콘 보이기
                       uncheckedicon.setVisibility(View.INVISIBLE);//체크 안될때 아이콘 안보이기
                       toastcustomer.showcustomtaost(null,"checkalarm");
                       Log.v("unchekedicon","알람취소 아이콘 시작");

                       break;


                   }// switch끝
                }//onclick 끝
             };//뷰 클릭 리스너 끝


           startclassbtn.setOnClickListener(onClickListener);//메신저 수업 시작 버튼 리스너 연결 시켜줌.
           messengerbtn.setOnClickListener(onClickListener);//메신저 버튼  리스너 연결 시켜줌.
           openchattingbtn.setOnClickListener(onClickListener);//위에 오픈 채팅  버튼  리스너 연결 시켜줌.
           checkedicon.setOnClickListener(onClickListener);//위에 선언된  체크 아이콘  리스너 연결 시켜줌
           uncheckedicon.setOnClickListener(onClickListener);//위에  선언된 언체크 아이콘 리스너 연결 시켜줌.
           whitehearticon.setOnClickListener(onClickListener);//위에 선언된 하얀색 하트에 리스너 연결 시켜줌
           whiteheariconentire.setOnClickListener(onClickListener);//위에 선언됨  꽉찬  하얀색 하트에 리스너 연결.


        //현재 구현해야되는 부분은  스크롤뷰에서 스크롤이  맨위로가면  툴바가  투명해지고
        //다른 위치로 가게되면 툴바가  다시  파란색으로 돌아오는 것을 구현해야한다.
        scrollViewforprevieactivity.post(new Runnable() {
            @Override
            public void run() {
                //scrollViewforprevieactivity.fullScroll(ScrollView.FOCUS_UP);
                scrollViewforprevieactivity.scrollTo(0,0);//
                Log.v("scrollcheck","처음시작하면 스크롤은 맨위지");
            }
        });



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//api 23 이상인데  현재  gradle에  최소 버전  16으로 해놔서  이러게  조건문 걸어줘야됨.
            scrollViewforprevieactivity.setOnScrollChangeListener(new ScrollView.OnScrollChangeListener() {//스크롤 리스너 이다.
                @Override
                public void onScrollChange(View view, int scrollX, int scrollY, int oldScrollx, int oldscrolly) {
                    if (scrollY <10) {//스크롤의 위치 범위가 10이하이면//투명도가 적용됨.

                        toolbar.setBackgroundColor(Color.parseColor("#001863C4"));
                        Log.v("scrollcheck","맨위입니다.");


                    }else if(scrollY>10){//스크롤의 범위가 10이상으로 올라가면  투명도가 풀림

                        toolbar.setBackgroundColor(Color.parseColor("#1863c4"));
                        Log.v("scrollcheck","맨위가 아닙니다.");
                    }


                }
            });//스크롤 리스너 끝
        }//버전  조건문  끝


        toolbar = (Toolbar) findViewById(R.id.toolbarforupdateprofileteacher2);//툴바 선언
        setSupportActionBar(toolbar);//툴바에  액션바  서포트 연결
        getSupportActionBar().setDisplayShowTitleEnabled(false);//엑션바에서 타이틀 안보이게함
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//액션바에서 Homeashup 버튼 가능하게함
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.whiteback);//해당 버튼  뒤로가기 버튼모양 으로  이미지 지정
        //아래  onOptionitemselected로 연결됨.


        //탭 호스트 vs  탭 레이아웃 차이점 알아보기
        tabHost=(TabHost)findViewById(R.id.tabhostforpreview);
        tabHost.setup();


        //Tab 1
        TabHost.TabSpec spec = tabHost.newTabSpec("Tab One");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Profile");
        tabHost.addTab(spec);


        //Tab 2
        spec = tabHost.newTabSpec("Tab Three");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Review");
        tabHost.addTab(spec);


        //Tab3
        spec = tabHost.newTabSpec("Tab Four");
        spec.setContent(R.id.tab3);
        spec.setIndicator("Reserv");
        tabHost.addTab(spec);


        tabHost.setCurrentTab(0);  //해당 엑티비티  create될떄 진행되는 탭 번호이다.

        //ArrayList<String> cList=new ArrayList<String>();
        JSONArray jsonArray=null;
        try {
            InputStream is=getResources().getAssets().open("countrylist.json");
            int size=is.available();
            byte[] data=new byte[size];
            is.read(data);
            is.close();
            String json=new String(data,"UTF-8");
            jsonArray=new JSONArray(json);
            if(jsonArray!=null){
               for(int i=0; i<jsonArray.length(); i++){
                   cLIST=jsonArray.getJSONObject(i).getString("name");

                   if(cLIST.equals(teachercountry123)){

                        a=i;//나라별 어레이리스트 포지션 값이  체크 값에 할당됨.

                       break;
                   }else{

                       continue;
                   }


                }

                Log.v("존재여부 체크값", String.valueOf(a));
                Log.v("선생님 나라",teachercountry123);
                Log.v("country.json파일에서 나라",cLIST);
                Log.v("해당  체크값  ",jsonArray.getJSONObject(a).getString("nativeorglobal"));

            }

            String teachernameglobalchecktext=teachername+"/"+jsonArray.getJSONObject(a).getString("nativeorglobal");
            teachernameandglobalcheck.setText(teachernameglobalchecktext);


        }catch (IOException e){
         e.printStackTrace();
        }

        catch (JSONException je){
        je.printStackTrace();
        }

    }//on create 끝


    //toolbar 아이템  셀렉트
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//옵션아이템들 클릭시 진행되는 코드
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                finish();//현재 엑티비티 끝냄.
                Log.v("check",getLocalClassName()+"의 툴바 뒤로가기 눌림.");
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }//옵션 아이템 클릭시 진행 끝


}//클래스 끝
