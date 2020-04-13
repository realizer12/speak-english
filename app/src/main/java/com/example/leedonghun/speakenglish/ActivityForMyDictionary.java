package com.example.leedonghun.speakenglish;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * speakenglish
 * Class: ActivityForMyDictionary.
 * Created by leedonghun.
 * Created On 2020-01-22.
 * Description: 내가  추가한 단어들이  모여있는 엑티비티이다.
 * 해당  단어들과 뜻을 볼수 있고  단어 뜻  텍스트뷰를 누르면
 * 단어가 담긴  텍스트뷰의 경우 -invisible로 바껴서 이렇게  외우기 공부를 할수 있다.
 * 그리고 시험보기 아이콘을  클릭하면  시간초내에 10개의 단어 문제를 풀수 있는  기능이 실행된다. -이부분 나중에 구현하기
 *
 */
public class ActivityForMyDictionary extends AppCompatActivity {


    //내  단어 리스트 보여주는 리사이클러뷰   1-1
    RecyclerView recyclerv_for_my_words;

    //레이아웃 매니저
    LinearLayoutManager linearLayoutManager_for_mydictionary;//1-2

    //내 단어장 어뎁터
    MyDictionaryAdapter myDictionaryAdapter;//1-3


    //내 단어 장  엑티비티 툴바    1-4
    Toolbar toolbar_for_mydictionary;

    //시험보는 이미지 뷰 아이콘
    ImageView imgView_for_exam;//1-5


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_my_dictionary);
        Log.v("check", getLocalClassName()+"의  onCreate() 실행됨");


        recyclerv_for_my_words=findViewById(R.id.recyclerview_for_mydictionary);//1-1
        toolbar_for_mydictionary=findViewById(R.id.toolbar_for_my_dictionary);//1-2
        imgView_for_exam=findViewById(R.id.imgView_for_exam);//1-5

        recyclerv_for_my_words.bringToFront();//리사이클러뷰 맨앞으로 가지고옴.

        ArrayList<JSONObject>mydictionery_arraylist=new ArrayList<>();//내 사전  어레이 리스트


        //내단어 저장한  쉐어드 프리퍼런스 가지고와서 ->  map 에 넣고,  key value를 -> JSONObject에 넣어
        //json화  한다음에  어레이리스트에  넣어줌.
        SharedPreferences sharedPreferences=getSharedPreferences("mydictionery",MODE_PRIVATE);

        Iterator iter = sharedPreferences.getAll().entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry pair = (Map.Entry)iter.next();
           Log.v("check_diction", String.valueOf(pair));
           JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("word", pair.getKey());
                jsonObject.put("meaning", pair.getValue());

                mydictionery_arraylist.add(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


        Log.v("arraylist 확인", String.valueOf(mydictionery_arraylist));



        //현재 엑티비티 ->  툴바  부분  설정
        setSupportActionBar(toolbar_for_mydictionary);//액션바를  툴바 설정
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//toolbar에서 home키  부분 활성화
        getSupportActionBar().setDisplayShowTitleEnabled(false);//엑션바에서 타이틀 안보이게함
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.whiteback);//홈키 부분  뒤로가기 모양으로 바꿔줌.


        //recyclerview -> useruid 필요해서   여기다  넣어줌.
        myDictionaryAdapter=new MyDictionaryAdapter(ActivityForMyDictionary.this,mydictionery_arraylist);
        linearLayoutManager_for_mydictionary=new LinearLayoutManager(ActivityForMyDictionary.this,RecyclerView.VERTICAL,false);
        linearLayoutManager_for_mydictionary.setReverseLayout(false);
        linearLayoutManager_for_mydictionary.setStackFromEnd(false);

        recyclerv_for_my_words.setLayoutManager(linearLayoutManager_for_mydictionary);
        recyclerv_for_my_words.setAdapter(myDictionaryAdapter);

        recyclerv_for_my_words.setNestedScrollingEnabled(false);//스크롤 부드럽게



        // 시험보기 아이콘 눌림 클릭리스너 실행됨.
        imgView_for_exam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mydictionery_arraylist.size()>9){// 해당  사전 글수 >10개 이상일때

                    Log.v("check", getLocalClassName()+"의  시험보기 아이콘 눌림");


                    //시험  보기  엑티비티로 넘어감.
                    Intent goto_word_test=new Intent(ActivityForMyDictionary.this,ActivityForWordExam.class);

                    //단어 시험 엑티비티에  words_list 키로  단어 리스트  보냄.
                    goto_word_test.putExtra("words_list", mydictionery_arraylist.toString());
                    startActivity(goto_word_test);//단어 시험 엑티비티 시작.

                }else {// 해당 사전 글수 10개 이하일때-> 시험 못봄.

                    Log.v("check", getLocalClassName()+"의  시험보기 아이콘 눌림-> 단어가  10개 이상이 아니어서 못넘어감. ");
                    new Toastcustomer(ActivityForMyDictionary.this).showcustomtaost(null, "시험을 보려면 최소 단어가 \n 10개 이상 이어야 합니다! ",1500,320);

                }



            }//onClick() 끝
        });// 이미지 뷰 시험 클릭리스너 끝



    }//onCreate()끝

    //툴바  옵션 메뉴들의  이벤트를  설정하기 위한   itemselected()메소드
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//옵션아이템들 클릭시 진행되는 코드
        switch (item.getItemId()){

            case android.R.id.home: { //toolbar의 back키 눌렀을 때 동작

                Log.v("check", getLocalClassName()+"의  툴바 뒤로가기 눌림.");

                finish();//현재 엑티비티 끝냄.

                return true;

            }

        }
        return super.onOptionsItemSelected(item);

    }//onOptionsitemSelected();

}//ActivityForMyDictionary 끝끝
