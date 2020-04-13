package com.example.leedonghun.speakenglish;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * speakenglish
 * Class: MyDictionaryAdapter.
 * Created by leedonghun.
 * Created On 2020-01-22.
 * Description:  내  단어장에 들어가는 데이터들을 받아서 리사이클러뷰에
 * 넣어주는  역할을 하는 adapter이다.
 */
public class MyDictionaryAdapter extends RecyclerView.Adapter {

     final String mentforlog="MyDictionaryAdapter";

     //내가 저장한  영단어 어레이 리스트
     ArrayList<JSONObject> my_word_list;

     //내가 사용할 context 넣어줌.
     Context context;

    private LayoutInflater layoutInflater;//단어장 내용이 담길  커스텀뷰 인플레이터 하기위한 인플레이터


    //MyDictionaryAdapter 생성자
    MyDictionaryAdapter(Context getcontext,ArrayList<JSONObject>get_my_word_list) {

        this.context = getcontext;
        this.my_word_list = get_my_word_list;

    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.v("check", mentforlog+"의 onCreateViewHolder 실행됨 ");

        View view;

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.my_dictionary_item_view, parent, false);

        return new my_dictionaryy_viewholder(view);


    }//onCreateViewHolder()끝

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.v("check", mentforlog+"의 onBindViewHolder 실행됨 ");


        try {

            //내 단어 리스트에서 뜻 말고 단어
            String my_word=my_word_list.get(position).getString("word");

            //내 단어 리스트에서 단어 말고 뜻
            String my_word_meaning=my_word_list.get(position).getString("meaning");


            ((my_dictionaryy_viewholder) holder).textView_for_myword.setText(my_word);// 내 단어 넣어줌.
            ((my_dictionaryy_viewholder) holder).textView_for_word_meaning.setText(my_word_meaning);//내 단어 뜻 넣어줌.



        } catch (JSONException e) {
            e.printStackTrace();
        }


    }//onBindViewHolder()끝


    @Override
    public int getItemCount() {
        Log.v("check", mentforlog+"의 아이템 수->"+my_word_list.size());

        return my_word_list.size();
    }//getItemCount()끝


    //내 단어장  뷰홀더
    class my_dictionaryy_viewholder extends RecyclerView.ViewHolder{

        //내 단어 가들어가는 텍스트뷰
        TextView textView_for_myword;//1-1

        //내 단어의  해석이 들어가는 텍스트뷰
        TextView textView_for_word_meaning;//1-2


        public my_dictionaryy_viewholder(@NonNull View itemView) {
            super(itemView);

            textView_for_myword=itemView.findViewById(R.id.textview_for_word);//1-1
            textView_for_word_meaning=itemView.findViewById(R.id.textview_for_meaning);//1-2


            //뜻이클릭되면 -> 해당 뜻을  사라지게 하거나 보이게 한다.
            textView_for_word_meaning.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.v("check", mentforlog+"의 +단어 뜻  클릭됨");

                    //뜻 텍스뷰에 텍스트가 있을때이다
                    if( textView_for_word_meaning.getText().length()>0){

                        //visible 상태이므로 inveisble로 바꿔준다.
                       textView_for_word_meaning.setText("");

                    }else {//뜻 텍스트뷰에 텍스트가 없을때이다.

                        try {

                            //해당 뜻텍스트뷰의  뜻을  다시  넣어준다.
                            textView_for_word_meaning.setText(my_word_list.get(getAdapterPosition()).getString("meaning"));

                        } catch (JSONException e) {
                            e.printStackTrace();


                        }

                    }///뜻 텍스트뷰에 텍스트가 없을때 끝


                }//onClic()끝
            });//뜻 텍스트뷰 클릭시.

            //단어가 클릭되면 -> 해당  단어를  사라지게 하거나  보이게 한다.
            textView_for_myword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.v("check", mentforlog+"의 +단어 클릭됨");

                    //단어 텍스뷰에 텍스트가 있을때이다
                    if( textView_for_myword.getText().length()>0){

                        //해당 단어를 없앰.
                        textView_for_myword.setText("");

                    }else {// 단어 텍스트뷰에 텍스트가 없을때이다.

                        try {

                            //해당 단어 텍스트뷰의  단어를  다시  넣어준다.
                            textView_for_myword.setText(my_word_list.get(getAdapterPosition()).getString("word"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }///뜻 텍스트뷰에 텍스트가 없을때 끝


                }//onClic()끝
            });//뜻 텍스트뷰 클릭시.



        }//my_dictionaryy_viewholder() 끝


    }//새 단어장 뷰홀더 끝,



}//MyDictionaryAdapter 끝
