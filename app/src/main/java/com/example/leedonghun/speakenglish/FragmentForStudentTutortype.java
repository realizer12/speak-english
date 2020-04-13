package com.example.leedonghun.speakenglish;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.widget.SwipeRefreshLayout;
//import android.support.v7.widget.CardView;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

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
 * Class: FragmentForStudentTutortype.
 * Created by leedonghun.
 * Created On 2019-01-11.
 * Description:
 * 학생 로그인 후 화면에서   선생님 목록 관련  프래그먼트이다.
 * 리사이클러뷰 로  native global 선생님을 나누워서  리스트를  보여준다
 * 로그인한  선생님들의  수를 보여주는  뷰가  있고,  그 뷰를  눌렀을때  로그인 한  선생님들만  보이는  엑티비티로  옮겨진다.
 * 각  선생님의  프로필을 눌렀을 경우, 선생님  프로필 방으로 들어가져서  정보를 볼수 있다.
 *
 */
public class FragmentForStudentTutortype extends Fragment {

    //로그 쓸때 편하게
    String thisclassname="FragmentForStudentTutortype클래스";

    private Retrofit retrofit;//리트로핏 선언
    private ApiService apiService;//api service 인터페이스

    //리사이클러뷰  native ,  global  용으로  나눔,
    RecyclerView recyclerViewfornativeteacher;
    RecyclerView recyclerViewforgloabalteacher;

    TextView onlineteachercount;


    //리사이클러뷰 레이아웃  매니저
    RecyclerView.LayoutManager recyclerlayoutmanagerfornative;
    RecyclerView.LayoutManager recyclerlayoutmanagerforglobal;


    //리사이클러뷰  어뎁터
    TeacherInfoRecyclerviewAdapter teacherInfoRecyclerviewAdapterfornative;
    TeacherInfoRecyclerviewAdapter teacherInfoRecyclerviewAdapterforglobal;


    //native, global 선생님 리스트 받아오기 위하 JsonObject 어레이리스트
    ArrayList<JsonObject>arrayListfornativeteacher;
    ArrayList<JsonObject>arrayListforglobalteacher;

    //online인  선생님  수
    int total_online_teacher_count;


    //새로고침 버튼
    Button refreshbtn;

    //ONLine인  선생님 숫자 보여주는 cardview
    CardView onlineteachershowcardview;


    //커스톰 토스트
    Toastcustomer toastcustomer;
    ViewGroup rootView;
    //on attach
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.v("check", thisclassname+"의  onattach 실행됨.");


    }//onattach 끝


    //oncreate
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("check", thisclassname+"의  oncreate 실행됨.");




    }//oncreate 끝


    //oncreateview
    //oncreateview에  위  서버  통신 내용을 안받은 이유는  프래그먼트가  detroyview를 하고 난다음에 다시  foreground로 올라갈때  oncreate 는  거치치 않기 때문에
    //다시  서버 요청을  할 필요가 없어지기 때문이다.
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.v("check", thisclassname+"의  oncreateview 실행됨.");


        //프래그 먼트 컨테이너  xml받아옴.
        rootView=(ViewGroup)inflater.inflate(R.layout.fragment_for_totortypeinstudent,container,false);




        //커스톰 토스트
        toastcustomer=new Toastcustomer(getActivity());


        //새로고침 버튼
        refreshbtn=rootView.findViewById(R.id.refreshtutortyoefragmentbtn);

        //online인 선생님 숫자 보여주는 카드뷰
        onlineteachershowcardview=rootView.findViewById(R.id.cardviewonlineteacher);




        //카드뷰 클릭 리스너 -> 클릭할때  숫자가  0이라면, 토스트날릭 1이면  online 선생님 보여주는 엑티비티로 넘겨줌,
        onlineteachershowcardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //로그인한 선생님 목록이  0일때,
                if(total_online_teacher_count==0){
                    Log.v("check", "online선생님  숫자 카드뷰 눌렸음 ->  0이어서  못넘어감.");

                    toastcustomer.showcustomtaost(null, "온라인 상태인 선생님이 없네요 ㅠ  \n아래 '정보 새로고침' 버튼을 눌러보세요!",1500,200);

                //로그인한  선생님 목록이  0초과 일때
                }else if(total_online_teacher_count>0){
                    Log.v("check", "online선생님 숫자 카드뷰 눌림 -> 0이상 이므로  Logined_Teacher_List로 넘어감");


                    //로그인한 선생님 목록을 보여주는 엑티비티로  넘어감
                    Intent gototheLogined_teacher_list=new Intent(getActivity(),Logined_Teacher_List.class);
                    startActivity(gototheLogined_teacher_list);

                }

            }
        });//카드뷰  클릭리스너 끝.


        //새로고침 버튼 누르면  ->  선생님 데이터 다시 받아옴.
        refreshbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getteacherdata(rootView);//뷰들  새로고침 해주는  메소드
            }
        });


        getteacherdata(rootView);
        return rootView;
    }//oncreateview끝


    private void getteacherdata(ViewGroup rootView){

         refreshbtn.setText(R.string.teacherrefreshing);//선생님  데이터 가져오는 동안  ->  refresh 버튼  -> 새로고침 중 으로  바뀜.

        //global, native 선생님 리사이크럴뷰  객체 선언
        recyclerViewforgloabalteacher=rootView.findViewById(R.id.recyclervieforglobalteacher);
        recyclerViewfornativeteacher=rootView.findViewById(R.id.recyclerviewforativeteacher);


        //온라인 선생님  숫자보여주는  텍스트뷰
        onlineteachercount=rootView.findViewById(R.id.onlineteachercount);

        //gson내용 알아내기.
        GsonBuilder gsonBuilder=new GsonBuilder();
        gsonBuilder.setLenient();
        Gson gson=gsonBuilder.create();

        //리트로핏 빌드
        retrofit = new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create(gson))//json으로 받고 싶으면 쓰면안됨..
                .build();//리트로핏 뷸딩
        apiService = retrofit.create(ApiService.class);//api인터페이스 크리에이트


        //GetWholeTeacher 클래스로  서버 콜
        Call<GetWholeTeacherInfo> getWholeTeacherInfo= apiService.getthewholeteacherinfo();

        //서버  받아온 내용
        getWholeTeacherInfo.enqueue(new Callback<GetWholeTeacherInfo>() {
            @Override
            public void onResponse(Call<GetWholeTeacherInfo> call, Response<GetWholeTeacherInfo> response) {


                if (response.body() != null) {//responsebody가  null이 아닐때

                    //각  native, global 선생님 어레이리스트에  값들을 넣어줌.
                    arrayListfornativeteacher =response.body().getWholeteacherinfornative();
                    arrayListforglobalteacher =response.body().getWholeteacherinforglobal();

                    //온라인 선생님 수
                    total_online_teacher_count=response.body().getOnlineteachercount();

                    Log.v("check1native", String.valueOf(arrayListfornativeteacher));
                    Log.v("check1global", String.valueOf(arrayListforglobalteacher));
                    Log.v("checkteachercount", "online 선생님 수-> "+response.body().getOnlineteachercount());

                    refreshbtn.setText(R.string.teacherrefresh);//새로고침이라는 멘트로  다시 바뀜.
                }else{
                    Log.v("check", "FragmentForStudentTutortype클래스에서  선생님 목곡  request에서  null 값이  뜸. 선생님 목록 정보를 못가지고옴.");
                }



                //리사이클러뷰  어뎁터
                teacherInfoRecyclerviewAdapterfornative=new TeacherInfoRecyclerviewAdapter(arrayListfornativeteacher,getActivity());
                teacherInfoRecyclerviewAdapterforglobal=new TeacherInfoRecyclerviewAdapter(arrayListforglobalteacher,getActivity());


                //레이아웃 매니저   ->  horizontal 형태로 만듬.
                recyclerlayoutmanagerforglobal=new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
                recyclerlayoutmanagerfornative=new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);


                //레이아웃 매니저 속서  조정-global
                ((LinearLayoutManager) recyclerlayoutmanagerforglobal).setReverseLayout(false);//레이아웃 순서 뒤집기
                ((LinearLayoutManager) recyclerlayoutmanagerforglobal).setStackFromEnd(false);//setstackfrombottm과 같이 사용가능-> 리스트의  마지막  부분 부터 시작됨.

                //레이아웃 매니저 속성  조정 -native
                ((LinearLayoutManager) recyclerlayoutmanagerfornative).setReverseLayout(false);
                ((LinearLayoutManager) recyclerlayoutmanagerfornative).setStackFromEnd(false);


                //리사이클러뷰에  레이아웃 매니저  연결 시켜줌.
                recyclerViewforgloabalteacher.setLayoutManager(recyclerlayoutmanagerforglobal);
                recyclerViewfornativeteacher.setLayoutManager(recyclerlayoutmanagerfornative);

                //리사이클러뷰에 adapter 연결 시켜줌.
                recyclerViewforgloabalteacher.setAdapter(teacherInfoRecyclerviewAdapterforglobal);
                recyclerViewfornativeteacher.setAdapter(teacherInfoRecyclerviewAdapterfornative);

                //스크롤  부드럽게  해주기 위해서
                recyclerViewforgloabalteacher.setNestedScrollingEnabled(false);
                recyclerViewfornativeteacher.setNestedScrollingEnabled(false);


                //데이터가  바뀌었을때 data  바뀐 부분을 알려줌.
                teacherInfoRecyclerviewAdapterforglobal.notifyDataSetChanged();
                teacherInfoRecyclerviewAdapterfornative.notifyDataSetChanged();

                //위치를 맨 앞에서  시작하도록만듬.
                recyclerViewforgloabalteacher.scrollToPosition(0);
                recyclerViewfornativeteacher.scrollToPosition(0);

                onlineteachercount.setText(" "+total_online_teacher_count+"명");//선생님 개수 넣어줌.

            }//onResponse 끝


            @Override
            public void onFailure(Call<GetWholeTeacherInfo> call, Throwable t) {

                Log.v("check", "FragmentForStudentTutortype.class 에서  선생님 리스트  받아오는 부분  문제 생김 에러 내용 => "+ t);
                Toastcustomer toastcustomer=new Toastcustomer(getActivity());
                toastcustomer.showcustomtaost(null, "서버에서 에러뜸"+t,1500 ,350);//에러 내용 보여줌.
                refreshbtn.setText(R.string.teacherrefresh);//새로고침이라는 멘트로 다시 바뀜.
            }//onFailure 끝
        });//enaue()끝
    }



    @Override
    public void onResume() {
        super.onResume();
        Log.v("check", "FragmentForStudetTutorType에서  onResume 살행됨");


        ///Logined_teacher_list 에서  돌아왔을때만  해당 엑티비티에서 저장된 쉐어드값으로 구별하여  5일 경우  다시 화면을 refresh해준다.
       SharedPreferences sharedPreferences=getActivity().getSharedPreferences("logined_teacher_list_finish_check",MODE_PRIVATE);
       int i=sharedPreferences.getInt("finishcheck",0);


        if(i==5){//5일 경우 Logined_teacher_list에서  체크되서 돌아옴.

           //기존에  쉐드에 저장된  logined_teacher_list에서  돌아온 여부 체크  값을  삭제해줘서  다른  경우 onresume이 시작될때는   새로고침이 없도록 만든다.
           SharedPreferences pref = getActivity().getSharedPreferences("logined_teacher_list_finish_check", MODE_PRIVATE);
           SharedPreferences.Editor editor = pref.edit();
           editor.remove("finishcheck");
           editor.commit();

           //Logined_teacher_list 엑티비티에서 다시 돌아와  foreground로  현재 프래그먼트가 foregorund로 나올때-> 다시  새로고침  해서  목록을 받아온다.
           getteacherdata(rootView);
       }
    }
    //onresume 끝


    @Override
    public void onDetach() {
        super.onDetach();

    }
}//클래스 끝
