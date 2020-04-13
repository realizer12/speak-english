package com.example.leedonghun.speakenglish;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * speakenglish
 * Class: ActivityForWordExam.
 * Created by leedonghun.
 * Created On 2020-01-22.
 *
 *
 * Description: 이 엑티비티는 단어 시험을 볼때 나오는 엑티비티이다. ->
 * 맨처음 들어오면  시작 하겠냐는 알럴트가 뜬다. ok를  누르면  진행  cancel을 누르면  다시 나가진다.
 * 단어가 10개 이상일때 볼수 있으며
 * 10초 동안 시간초를 주고 ,10개의  단어를 맞추며   맞으면  맞았다고  틀리면 틀렸다고 소리가 나온다.
 * 그리고  10초가 지날때까지  아무것도 클릭안하면  정답을 보여주고,  다음  단계로 넘어간다.
 * 다 끝나면,  틀린단어를 보여주는  alert 창이 나온다.
 */
public class ActivityForWordExam extends AppCompatActivity {

    //위의 상태바

    //현재 엑티비티 나가는  x 이미지뷰  1-1
    ImageView img_view_for_finish_exam;

    //텍스트뷰 -시간 타이머  들어가는 텍스트뷰 1-2
    TextView txt_for_time_watch;

    //현재  시험  포지션을 보여주는 텍스트뷰 1-3
    TextView txt_for_show_test_position;

    //문제가 들어가는  텍스트뷰  1-4
    TextView  txt_for_question;

    //첫번째  보기 단어   2-1
    TextView first_selection_word;

    //두번째  보기 단어  2-2
    TextView second_selection_word;

    //세번째 보기 단어 2-3
    TextView third_selection_word;

    //네번째  보기 단어 2-4
    TextView fourth_selection_word;


    //정답일때 나오는  텍스트뷰  3-1
    TextView txt_for_correct;

    //틀렸을때 나오는  텍스트뷰  3-2
    TextView txt_for_wrong;


   //현재 보는 TEST의 시간초 담당하는 asynk task  클래스
   Timer_For_Test timer_fortest;

    //ActivityForMyDictionary에서  보낸 내 단어리스트 받는 어레이 리스트.
     ArrayList<JSONObject>get_my_word_list;


     Activity thisactivity;
    AnswerSound answerSound;
     //api kitkat 이상  -> JSONArray-. remove  못씀 현재는 그이상이어서  해당  어노테이션 달음.
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_word_exam);

        Log.v("check", getLocalClassName()+"의  onCreate() 실행됨");

        img_view_for_finish_exam=findViewById(R.id.btn_for_cancel_exam);//1-1
        txt_for_time_watch=findViewById(R.id.text_view_for_time_watch);//1-2
        txt_for_show_test_position=findViewById(R.id.txt_show_position_for_exam_page);//1-3
        txt_for_question=findViewById(R.id.txt_for_question);//1-4

        first_selection_word=findViewById(R.id.first_word);//2-1
        second_selection_word=findViewById(R.id.second_word);//2-2
        third_selection_word=findViewById(R.id.third_word);//2-3
        fourth_selection_word=findViewById(R.id.forth_word);//2-4


        txt_for_correct=findViewById(R.id.textview_for_correct);//3-1
        txt_for_wrong=findViewById(R.id.textview_for_wrong);//3-2

        answerSound=new AnswerSound(ActivityForWordExam.this);

        thisactivity=this;


        //내 단어장에서 받아온  -> 내 단어리스트.
        Intent get_word_list=getIntent();
        String wordlist= get_word_list.getStringExtra("words_list");
        Log.v("check",getLocalClassName()+"의 ActivityForMyDictionary에서 받아온  내 단어 리스트 ->"+wordlist);

        //내 단어 리스트 -> arraylist에 넣어줌.
        get_my_word_list=new ArrayList<>();


        try {

            //위  스트링 형태로 받아온  json  형식 내 단어 리스트를 -> JSONArray에  넣어준다.
            JSONArray jsonArray_for_mydictionary = new JSONArray(wordlist);
            Log.v("check",getLocalClassName()+"의 ActivityForMyDictionary에서 받아온  내 단어 리스트 JSONArray 넣고 length 확인 -> 내 단어 전체수"+ jsonArray_for_mydictionary.length());


            //내 단어 리스트에서  총  10개를  추출한다.
            for(int i=0; i<10; i++){

                int random_count = new Random().nextInt(jsonArray_for_mydictionary.length());//내  단어 리스트 숫자 만큼 해서 랜덤으로  추출해줌. -> nextint하면 0~ length-1 까지  수중  하나를 반환한다.
                JSONObject get_Card = (JSONObject) jsonArray_for_mydictionary.get(random_count);//총 단어장에서 10개를  추출해서
                get_my_word_list.add(get_Card);//getmy_word_list에 넣어줌.
                jsonArray_for_mydictionary.remove(random_count);// 넣어준 ->index는  삭제 시켜준다. -> 삭제 시켜주면  jsonArray_for_mydictionary.length()의 총 길이는  바뀔꺼고 다시 삭제된 인덱스 채운 새로운 인덱스들에서  랜덤 뽑기 가능해짐.


                if(get_my_word_list.size()>10){//그렇게 해서  총 10개를  추출하면  break == 10개이상 추출하려면 break
                    break;
                }

            }//내 단어 리스트 for문 끝

            Log.v("check", getLocalClassName()+"의  시험용으로 새롭게 랜덤 추출한 10개 단어 담긴  어레이 리스트 size->"+get_my_word_list.size());
            Log.v("check",   getLocalClassName()+"의  새롭게 랜덤 추출한 10개 단어 리스트 보여줌. ->"+get_my_word_list);


        } catch (JSONException e) {
            e.printStackTrace();
        }






        //alertdialog에서  해당 단어 테스트 시작 여부  물어봄.
        AlertDialog.Builder alert = new AlertDialog.Builder(ActivityForWordExam.this);
        alert.setTitle("단어 TEST 시작");
        alert.setMessage("단어 테스트를 시작하시겠습니까??");
        alert.setCancelable(false);//cancel 이외 터치로 알럴트 취소 못함.

        //단어 테스트 취소 버튼
        alert.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {



                dialog.dismiss();//다이얼로그 없애주고
                finish();//현재 엑티비티 종료함.
            }
        });


        //단어 테스트 진행 버튼
        alert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //단어 테스트를 위한 카운트 및 ui업데이트 기능하는 asyncktask 실행함.  -> 매개 변수로 내단어 리스트 및 ui  업데이트용  텍스트뷰 넣어줌.
                timer_fortest=new Timer_For_Test(get_my_word_list,thisactivity,ActivityForWordExam.this,txt_for_time_watch,
                        txt_for_show_test_position, txt_for_correct, txt_for_wrong, txt_for_question, first_selection_word,
                        second_selection_word, third_selection_word, fourth_selection_word);


                timer_fortest.execute();//단어 리스트 실행.

            }
        });

        //단어 시험 시작 여부  알럴트 보이기.
        alert.show();

        //X버튼 눌렀을 때  엑티비티 끝내기
        img_view_for_finish_exam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //alertdialog에  webview를 넣어서 ->해당  단어를 검색한  사전 페이지를 보여준다.
                AlertDialog.Builder alert = new AlertDialog.Builder(ActivityForWordExam.this);
                alert.setTitle("단어 테스트 중단");
                alert.setMessage("단어 테스트 도중입니다.  정말 나가시겠습니까??");
                alert.setCancelable(false);//cancel 이외 터치로 알럴트 취소 못함.

                //테스트 중단 클릭시
                alert.setNegativeButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        timer_fortest.cancel(true);//해당  단어테스트용 asyncktask 종료 true
                        dialog.dismiss();
                        finish();//엑티비티 종료
                    }
                });

                //테스트 중단 취소
                alert.setPositiveButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();//다이얼로그만 없애줌.

                    }
                });

                //단어테스트 중단 여부 알럴트 실행.
                alert.show();


            }
        });  //X버튼 눌렀을 때 끝

    }//onCreate 끝


    //시험이 진행되는 asynck task 클래스
    @SuppressLint("StaticFieldLeak")
    class Timer_For_Test extends AsyncTask<Integer,Integer,Integer>{

        int time;//테스트 타이머 시간
        int position;//테스트  포지션 -> 몇번쨰  문제인지 ..
        ArrayList<JSONObject>  myword_lis_in_asynk;//내 단어 리스트.11-0

        //ui업데이트용  텍스트뷰
        TextView txt_for_show_test_position;//현재  테스트  진행 단계-> 몇번째 문제인지..보여주는  텍스트뷰  11-1
        TextView txt_for_time_watch;//현재  테스트  남은 시간초 보여주는 텍스트뷰  11-2
        TextView txt_for_correct;//맞았을때 나오는 텍스트뷰 11-3
        TextView txt_for_wrong;//틀렸을때 나오는 텍스트뷰  11-4
        TextView first_selection_word;//첫번째 보기  단어  11-5
        TextView second_selection_word;//두번쨰 보기 단어  11-6
        TextView third_selection_word;//세번째 보기 단어  11-7
        TextView fourth_selection_word;//네번쨰 보기 단어 11-8
        TextView txt_for_question;//질문 텍스트뷰 11-9

        Context context;//11-10

        //현재 엑티비티
        Activity activity_for_activityforwordexam; //11-11
        ArrayList<JSONObject> wrrong_wordslist=new ArrayList<>();

        //Timer_For_Test() 생성자
        Timer_For_Test(ArrayList<JSONObject>  myword_lis,Activity activity_for_activityforwordexam ,Context context, TextView txt_for_time_watch, TextView txt_for_show_test_position, TextView txt_for_correct, TextView txt_for_wrong, TextView txt_for_question, TextView first_selection_word, TextView second_selection_word, TextView third_selection_word, TextView fourth_selection_word){

            this.txt_for_show_test_position=txt_for_show_test_position;//11-1
            this.myword_lis_in_asynk=myword_lis;//11-0
            this.txt_for_time_watch=txt_for_time_watch;//11-2
            this.txt_for_correct=txt_for_correct;//11-3
            this.txt_for_wrong=txt_for_wrong;//11-4
            this.first_selection_word=first_selection_word;//11-5
            this.second_selection_word=second_selection_word;//11-6
            this.third_selection_word=third_selection_word;//11-7
            this.fourth_selection_word=fourth_selection_word;//11-8
            this.txt_for_question=txt_for_question;//11-9
            this.context=context;//11-10
            this.activity_for_activityforwordexam=activity_for_activityforwordexam;//11-11

        }//Timer_For_Test() 생성자 끝

        @Override
        protected void onPreExecute() {// 테스트 진행 전 ui
            super.onPreExecute();

            position=1;//현재 포지션 1
            time=11;// 타이머는 11로 맞춘 이유는  doinbackground에서  --를  진행해서  딱 10초 느낌을 만들기 위해서
            txt_for_time_watch.setText(time+"초");
            txt_for_show_test_position.setText(position+"/10");

        }//onPreExcute()끝

        @Override
        protected Integer doInBackground(Integer... integers) {

            time --;//시간 -1 해줌, -10됨.


            while (!isCancelled() && time<11){// cancel이 안불리고  time이 11이하일때 계속

                if(position==11){//총 10번째  테스트 까지 이므로 positon 11.이면 break해버린다.

                    break;
                }

                time --;
                publishProgress();//onProgressUpdate 호출 한다.

                try {
                    Thread.sleep(1000);//1초 sleep시간으로 준다.
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(time==0){//time 0되면  포지션이  1 올라감. 그리고  time은 다시 10으로 맞춰준다.


                    position++;


                    time=10;

                    //시간 초가시  포지션 올라가면  여기서 바로 11일때,  break해준다.
                    if(position==11){

                        break;
                    }

                }
            }//while문 끝.

            return 0;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);


            if(time==9) {//타임 9 일때 부터  유아이 업데이트 시작.

                //맞음 틀림  보여주는 텍스트뷰는  처음에는  invisible상태로
                txt_for_correct.setVisibility(View.INVISIBLE);
                txt_for_wrong.setVisibility(View.INVISIBLE);

                try {

                    //해당 단어리스트의  word와  meaning을  받아서  넣어준다. -> 이게  정답으로 페어될
                    //word이다.
                    //position-1하면  position은  1~10까지니까  0~9의  인덱스의 값들을 받아올수 있음.
                    String   english_word = myword_lis_in_asynk.get(position - 1).getString("word");
                    String  korean_meaning = myword_lis_in_asynk.get(position - 1).getString("meaning");


                    // 정답 word와  랜덤 추출  3개 단어가 들어갈 -> 어레이리스트
                    ArrayList<String> arrayList = new ArrayList<String>(4);
                    txt_for_question.setText(korean_meaning);//질문 텍스트뷰에는 위  meaning 을  넣어준다.

                    //총 10개 단어들이니까  10개 까지 로 넣음
                    for (int i = 0; i < 10; i++) {

                        //0~9까지 랜던 추출
                        int idx = new Random().nextInt(10);

                        //위 랜덤 추출한  숫자가 -> positon-1 ->  즉 정답이되는 인덱스의 값이  아닌경우
                        if (idx != position - 1) {

                            //해당  보기에 들어가는 단어 어레이리스트 사이즈가  3개 초과면  break해줌. -> 총 3개까지만 받는다는거임.
                            if (arrayList.size() > 3) {
                                break;
                            }

                            //해당 랜덤 추출 숫자를 인덱스에 넣어서 -> word를  가지고옴,.
                            String word = myword_lis_in_asynk.get(idx).getString("word");

                            //보기 단어 리스트  어레이에  각 인덱스별로 조사하기 위해서.-> 그런데 사실 필요 없음. -> 로그에  쓰려고  남겨둠.
                            for(int a=0; a<5; a++){

                                //만약에 단어 어레이리스트에 해당  랜덤 추출word가 포함되어있지 않으면,  이때는  비교할게 있는 거니까  어레이리스트 사이즈가 0보다 클때 조건 들어감.
                                if(!arrayList.contains(word) && arrayList.size()>0) {
                                    Log.v("FFDFDFD", arrayList.get(a) + "가 " + word + "와 같지 않음.");
                                    arrayList.add(word);

                                    break;//그리고  for(int a=0; a<5; a++)  반복문 나감.

                                }else if(arrayList.size()==0){//보기 단어 어레이리스트에 아무것도 없으면  그냥 넣어줌.

                                    arrayList.add(word);
                                }

                            }//for(int a=0; a<5; a++)끝

                        }//랜덤 추출 번호가 정답 포지션 인덱스가  아닐때,


                    }//전체 반복문 끝.

                    Log.v("FFDFDFD", String.valueOf(arrayList));
                    int idx1 = new Random().nextInt(4);//랜덤  0~3

                    Log.v("FFDFDFD숫자", String.valueOf(idx1));
                    arrayList.add(idx1,english_word);//단어보기 리스트에  랜덤 인뎃스에  해당  단어 넣어줌. -> 이렇게 하면,  위 보기 리스트에서 해당 정답  word가  들어가고,  기존 단어는 밀려남.

                    //정답 단어까지 넣어줬으면-> 해당 어레이 값들을 보기 텍스트뷰들에 차례대로 넣어준다.
                    //정답 word는 랜덤으로  0~3사이에 들어갔으므로,  랜덤으로 배치될수 있다.
                    first_selection_word.setText(arrayList.get(0));
                    second_selection_word.setText(arrayList.get(1));
                    third_selection_word.setText(arrayList.get(2));
                    fourth_selection_word.setText(arrayList.get(3));

                    arrayList.clear();//다 끝났으면  보기 단어 리스트는  다시 클리어  해준다.



                    //첫번째 보기 단어 클릭시
                    first_selection_word.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String having_word=first_selection_word.getText().toString();//해당 텍스트뷰 gettext를 통해서 -> 단어를 가지고온다.
                            compare_word_correct_or_not(having_word, english_word,wrrong_wordslist,txt_for_question.getText().toString());//해당 단어 맞는지 틀린지 여부 판단및  그뒤 행동 진행하는 메소드
                        }
                    });


                    second_selection_word.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String having_word=second_selection_word.getText().toString();
                            compare_word_correct_or_not(having_word,english_word,wrrong_wordslist,txt_for_question.getText().toString());//해당 단어 맞는지 틀린지 여부 판단및  그뒤 행동 진행하는 메소드
                        }
                    });

                    third_selection_word.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String having_word=third_selection_word.getText().toString();
                            compare_word_correct_or_not(having_word,english_word,wrrong_wordslist,txt_for_question.getText().toString());//해당 단어 맞는지 틀린지 여부 판단및  그뒤 행동 진행하는 메소드
                        }
                    });

                    fourth_selection_word.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String having_word=fourth_selection_word.getText().toString();
                            compare_word_correct_or_not(having_word,english_word,wrrong_wordslist,txt_for_question.getText().toString());//해당 단어 맞는지 틀린지 여부 판단및  그뒤 행동 진행하는 메소드
                        }
                    });



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }//time 9 일때 ->  테스트 시작한 경우 -> 보통  시작하면 9부터  ui에 사람반응 가능 ,

            //time이  0인경우는  - 답을 못고르고 넘어갔을때이다. -> 이때도 틀린걸로 간주해서 넣어준다.
            if(time==0) {

                //틀린 글자의  뜻은 아는데,   word는  -> 위 english word로 못가지고옴.
                //그래서 -> 전체  테스트 word  를 반복문으로 돌려  같은  meaning의  word를  찾아서 -> 틀린단어  리스트에  넣어줌.
                String wrong_word_meaning=txt_for_question.getText().toString();
                String wrongword = null;

                //전체 테스트 단어  for문 돌림.
                for(int i=0; i<myword_lis_in_asynk.size(); i++){

                    try {
                        //현재  뜻과  같은  뜻의  word를  찾아냄.-> break으로  for문 나감.
                        if(myword_lis_in_asynk.get(i).getString("meaning").equals(wrong_word_meaning)){

                            //틀린단어.
                            wrongword=myword_lis_in_asynk.get(i).getString("word");

                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                 //틀린단어 JSONObject형태로 넣어줌.
                JSONObject wrong_word = new JSONObject();

                try {
                    wrong_word.put("word",wrongword);
                    wrong_word.put("meaning", wrong_word_meaning);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //틀린단어 리스트에 넣어줌.
                wrrong_wordslist.add(wrong_word);
            }//시간이  0일때,


                //포지션이랑  시간은  필요에 의해 계속해서 업데이트
                txt_for_show_test_position.setText(position+"/10");
                txt_for_time_watch.setText(time+"초");


        }

        //해당 단어 맞는지 틀린지 여부 판단및  그뒤 행동 진행하는 메소드
        private void compare_word_correct_or_not(String having_word,String english_word, ArrayList<JSONObject> wrrong_wordslist,String word_meaning){




            if(having_word.equals(english_word)){//정답과 보기단어가 맞는 경우  정답




                try {

                    answerSound.playRightAnswerSound();
                    txt_for_correct.setVisibility(View.VISIBLE);//정답 알려주는 텍스트뷰 보여줌.
                    Thread.sleep(700);//0.7초 정도 딜레이

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                time=10;//다음 문제 넘어가야함으로  time은  다시 10으로

                position=position+1;//포지션은 1올려준다.


            }else{//정답과 보기단어가 틀린경우  오답 처리한다.



                //틀린단어 -> JSONObject화 함.
                JSONObject wrong_word=new JSONObject();
                try {
                    wrong_word.put("word",english_word);
                    wrong_word.put("meaning", word_meaning);


                } catch (JSONException e) {
                    e.printStackTrace();
                }


                //틀린단어 리스트에 넣어줌.
                wrrong_wordslist.add(wrong_word);

                //정답때와 마찬가지로 다음 문제 넘어감으로 time이랑 positon제조정해주고
                //틀림  텍스트뷰 보여준다.
                try {
                    answerSound.playWrongAnswerSound();
                    txt_for_wrong.setVisibility(View.VISIBLE);
                    Thread.sleep(700);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //틀렸어도 다음 테스트로 넘어감으로 -> 아래와 같이  time과 positon 값 재조정해줌.
                time=10;

                position=position+1;

            }
        }//compare_word_correct_or_not() 끝


        //테스트 진행 되고 난뒤
        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);


            if(position==11){//포지션이  11-> 즉  10문제 다풀어서 +1 또 되서  11일때


                Log.v("틀린것들", String.valueOf(wrrong_wordslist));

                //테스트가 끝났으므로,  틀린 단어보여주고,  다시 보기 또는 단어장 돌아가기 -> 선택하는 알럴트 창 띄움.
                //alertdialog에  webview를 넣어서 ->해당  단어를 검색한  사전 페이지를 보여준다.
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("단어 TEST 결과");
                alert.setCancelable(false);//cancel 이외 터치로 알럴트 취소 못함.


                LayoutInflater inflater = activity_for_activityforwordexam.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.exam_result, null);

                LinearLayout linearLayout=dialogView.findViewById(R.id.exam_result);
                TextView txt_for_wrongcount=dialogView.findViewById(R.id.wrong_count_txt);

                TextView exam_result_ment1=dialogView.findViewById(R.id.textView5222);
                TextView exam_result_ment2=dialogView.findViewById(R.id.textView52212);

                if(wrrong_wordslist.size()==0){//틀린개수가 없을때

                    exam_result_ment1.setText("축하드립니다 짝짝짝 .\n  다 맞았습니다!!");
                    exam_result_ment2.setVisibility(View.GONE);
                }//틀린개수 0일때 조건 끝

                txt_for_wrongcount.setText("틀린 개수는 총 "+wrrong_wordslist.size()+"개 입니다. ");


                //틀린 단어 리스트 ->  alert에  넣어서 보여줌.
                for(int i=0; i<wrrong_wordslist.size(); i++){

                    TextView textView=new TextView(context);
                    textView.setTextSize(30);
                    textView.setTextColor(Color.WHITE);
                    Typeface type = Typeface.createFromAsset(getAssets(),"juache.ttf");
                    textView.setTypeface(type);
                    textView.setGravity(Gravity.CENTER);


                    try {
                        String word=wrrong_wordslist.get(i).getString("word");
                        String meaning=wrrong_wordslist.get(i).getString("meaning");
                         textView.setText(word);

                         linearLayout.addView(textView);//갯수대로 반복문 진행되어서 textview 추가됨


                        //틀린단어 클릭하면 ->  뜻 보여줌.
                         textView.setOnClickListener(new View.OnClickListener() {
                             @Override
                             public void onClick(View v) {


                                     textView.setTextSize(15);
                                     textView.setText(meaning);


                             }
                         });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }



                alert.setView(dialogView);


                //시험 다시 보지 않음
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        activity_for_activityforwordexam.finish();
                    }
                });



                //시험 다시 보기
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //시험 다시보기클릭되었으므로,  테스트 asynktastk  다시  진행함.
                        timer_fortest=new Timer_For_Test(get_my_word_list,thisactivity,ActivityForWordExam.this,txt_for_time_watch,
                                txt_for_show_test_position, txt_for_correct, txt_for_wrong, txt_for_question, first_selection_word,
                                second_selection_word, third_selection_word, fourth_selection_word);

                        timer_fortest.execute();

                    }
                });


                alert.show();//다시보기 여부 및  시험 결과 보여주는 alert 보여주기

            }



        }


        //cancel눌렸을땡다. -.x이미지 버튼 눌려서  도중에 나갔을때 호출됨
        @Override
        protected void onCancelled() {
            super.onCancelled();
            Log.v("check", getLocalClassName()+"의  단어시험 asynctask onCancelled 호출됨");

        }//onCancelled


    }







}//ActivityForWordExam클래스 끝
