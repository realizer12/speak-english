package com.example.leedonghun.speakenglish;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
//import android.support.annotation.NonNull;
//import android.support.v7.widget.CardView;
//import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * speakenglish
 * Class: TeacherInfoRecyclerviewAdapter.
 * Created by leedonghun.
 * Created On 2019-08-28.
 * Description:선생님 목록 가져와서 뿌리는  리사이클러뷰의  어뎁터  부분
 */
public class TeacherInfoRecyclerviewAdapter extends RecyclerView.Adapter<TeacherInfoRecyclerviewAdapter.TeacherInfoRecyclerviewHoler> {


    private  ArrayList<JsonObject> mData;//받아온  선생님 목록 어레이리스트를  받을  어뎁터 내부 어레이리스트
    private  LayoutInflater inflater;//레이아웃 인플레이터 -> cardview  xml 리스트뷰 아이템으로 사용하기 위해서
    private  Context contextparent;//context 받아고기
    private  TeacherInfoRecyclerviewHoler teacherInfoRecyclerviewHoler;

    //뷰홀더
    class TeacherInfoRecyclerviewHoler extends RecyclerView.ViewHolder{


        //선생님  카드뷰에 들어가는  뷰들
        TextView  teachername;//선생님 이름 텍스트뷰
        ImageView profileimageView;//선생님  이프로필 미지뷰
        ImageView nationflag;//국가  국기   이미지뷰
        CardView cardView;//선생님 목록 들어가는 카드뷰
        TextView textViewfornativeornot;//native , global  나타내는   텍스트뷰
        TextView textViewforshortsentence;//선생님  짧은 소개  텍스트뷰
        TextView showteacheronoffline;

        Context contextforintent;

        //뷰홀더  생성자
        TeacherInfoRecyclerviewHoler(final View itemView) {
            super(itemView);

            //item뷰는  카드뷰가 들어가질것임.

             //위에  쓴  카드뷰 내부  뷰들   실제뷰와 연결
             teachername=itemView.findViewById(R.id.listtextforteachername);
             cardView=itemView.findViewById(R.id.cardviewforteacherinfo);
             profileimageView=itemView.findViewById(R.id.listimageViewforteacherprofile);
             textViewfornativeornot=itemView.findViewById(R.id.textViewfornationlocation);
             textViewforshortsentence=itemView.findViewById(R.id.textViewforshortsentence);
             nationflag=itemView.findViewById(R.id.imageViewfornationflag);
             showteacheronoffline=itemView.findViewById(R.id.showonoffline);

             //각  아이템 클릭리스너
              itemView.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                      Log.v("check_", "선생님 목록  리사이클러뷰 아이템 눌림");

                      int posiotn=getAdapterPosition();//현재 클릭된 아이템 뷰 포지션

                      contextforintent=itemView.getContext();//context받음.
                      Intent gototeacherprofile=new Intent(contextforintent,TeacherProfile.class);//인텐트 teacherprofile로가짐.
                      gototeacherprofile.putExtra("teacherinfo", String.valueOf(mData.get(posiotn)));//클릭된 포지션의  jsonobject 어레이값 넘기기.

                      //TeacherProfile의 경우에는 로그인한 선생님 리사이클러뷰에서,,  그리고 전체 선생님 리사이클러뷰
                      //모두에서   사용해야하므로,   각각   체크로  다르게  데이터를 받아서  각각의 맞는  데이터 처리를 해야한다.
                      //0일 경우는  일반  전체 선생님 목록에서  아이템클릭으로 가졌을 경우.  1일경우는  로그인한 선생님 목록에서 아이템 클릭으로 가졌을경우.
                      gototeacherprofile.putExtra("teacherinfocheck", 0);

                      contextforintent.startActivity(gototeacherprofile);//teacherprofile 실행


                  }
              });//리사이클러뷰 아이템 클릭리스너 끝.



        }//뷰홀더 생정자 끝

    }//viewholder클레스  끝



    //어뎁터 생성자 -> 해당  선생님 목록  어레리 리스를 받아옴.
    TeacherInfoRecyclerviewAdapter(ArrayList<JsonObject> list,Context context) {
        Log.v("check", "Teacherinforecyclerview 어뎁터  ->  생성자 실행");

        this.contextparent=context;//프레그먼트 , 정확히는 프레그먼트를 담은 parentlayout의   context  프래그 먼트에서  담아옴.
        this.mData = list;//선생님 목록  jsonObject어레이  리스트  받아옴

    }//어뎁터 생성자  끝





    @NonNull
    @Override
    public TeacherInfoRecyclerviewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.v("check", "TeacherInfoRecyclerviewAdapter 클래스  onCreateViewHolder 메소드 실행");

        Context context = parent.getContext() ;//부모 레이아웃  컨텍스트 사용.
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//인플레이터   컨텍스트 권한 줌
        View view=inflater.inflate(R.layout.cardviewforteacherinfo, parent,false);//카드뷰 인플레이트 함.

        //위  뷰홀더  생성자에  카드뷰 넣어서  생성자에  선언됬던 모든 뷰들 카드뷰에 연결
       teacherInfoRecyclerviewHoler=new TeacherInfoRecyclerviewHoler(view);

        return teacherInfoRecyclerviewHoler;
    }//oncreateViewHolder 끝



    @Override
    public void onBindViewHolder(@NonNull TeacherInfoRecyclerviewHoler holder, int position) {

        Log.v("check", "TeacherInfoRecyclerviewAdapter 클래스  onBindViewHolder 메소드 실행");


        String textforteachername=mData.get(position).get("teacherNAME").toString();//선생님 이름
        String textforteachernativeornotnumber=mData.get(position).get("teacherNativeOrNot").toString();//선생님  native or  global  여부
        String teacheronofflineccheck=mData.get(position).get("teacherstatuscheck").toString();//선생님  온오프라인 체크 여부

        //서버로 부터  받아오는  global. native 값들은  0 또는 1이므로, 아래 조건 문으로 구분해서  global, native textnativeornot에 넣어줌,
        String textnativeonot;




        //선생님 온오프라인 값  ->  null 값 (애초에  온오프 시도를 안했던 경우) 이거나  0이면  offline 임.
        if(teacheronofflineccheck.replaceAll("\"","").equals("0") || teacheronofflineccheck.equals("null")){
             Log.v("check", "선생님  목록 리스트  ->  온오프라인값이  0 또는 null -> 오프라인 상태일떄");

             //선생님 온라인이면  -> 파란색  오프라인이면  회색으로  보 보이게 함.
            //sdk버전  롤리팜 이상일때
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
             Log.v("check", "선생님 목록  리스트 -> 롤리팜 이상 버전으로 온라인 오프라인  색깔 표시 진행 ");

                //선생님  백그라운드로 -> 라운드  효과 주어서 ->  틴트로  색을 바꿨는데,  여기서 색을 바꾸려고 하니까  백그라운드 티트리스트를 사용하여야해음
                //아니면  백그라운드 칼라를 써야하는데 그걸 쓰면  라운드 효과가 풀림
                //선생님 onoffline 체크-> 회색으로 보이게함.
                holder.showteacheronoffline.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));


            }else {
                Log.v("check", "선생님 목록  리스트 -> 롤리팜 이하 버전으로 온라인 오프라인  색깔 표시 진행 ");

                //롤리팜  이전  버전들은  어쩔수 없이 라운드 효과를  풒린 상태로 진행함.
                holder.showteacheronoffline.setBackgroundColor(Color.GRAY);
            }

          //선생님 온오프라인 값 -> 1일 경우 online 임.
        }else if(teacheronofflineccheck.replaceAll("\"","").equals("1")){
            Log.v("check", "선생님  목록 리스트  ->  온오프라인값이  1로  해당 선생님 온라인 상태");

            //롤리팝 이상일떄,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Log.v("check", "선생님 목록  리스트 -> 롤리팜 이상 버전으로 온라인 오프라인  색깔 표시 진행 ");

                //선생님 onoline 체크->  파란색으로 보이게함 ->#1092F5
                holder.showteacheronoffline.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF4AF70D")));

            }else{
                Log.v("check", "선생님 목록  리스트 -> 롤리팜 이하 버전으로 온라인 오프라인  색깔 표시 진행 ");

                holder.showteacheronoffline.setBackgroundColor(Color.parseColor("#FF4AF70D"));

            }

        }//선생님 온오프라인 값 ->1일 경우 ONLINE끝






        //선생님 native or   global여부 판단해서  스트링으 값으로 넣어줌.
        if(textforteachernativeornotnumber.replaceAll("\"","").equals("0")){
            textnativeonot="global";
        }else{
            textnativeonot="native";
        }

        String textforteachersentence=mData.get(position).get("teachershorsentence").toString();//선생님  shortsentence
        try {

            String a= String.valueOf(mData.get(position).get("teacherPHOTOpath")).replaceAll("\"","");//선생님  프로필 이미지 패스
            URL url = new URL("http://13.209.249.1/" +a);//해당 이미지 url 받아옴


//            conn.connect();//url과 커넥트함
//            BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());//인풋스트림으로  받아오기 사진
//            Bitmap bm = BitmapFactory.decodeStream(bis);
//            bis.close();//인풋스트립 닫음
//            holder.profileimageView.setImageBitmap(bm);//받아온 bitmap값 학생 프뢸사진에 넣어줌.

            //위는  동기식이어서  비동기식으로 이미지 가져올수 있는 글라이드 사용함.
            Glide.with(contextparent).load(url).into(holder.profileimageView);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // teacherCOUNTRYCODE
        String textteachercountrycode=mData.get(position).get("teacherCOUNTRYCODE").toString();// 선생님  국가 코드

        //선생님  국가  깃발을 가져오기 위해서 "" 부분을  없애놈.
        String textteachercountryflag=textteachercountrycode.replaceAll("\"","");

        //외국 깃발  가져다 주는 사이트에서  해당  국가 코드  넣어 나온  깃발  가져와서  넣어주기.
        String imageUrl = "https://www.countryflags.io/"+textteachercountryflag+"/flat/16.png";



        //카드뷰 안  뷰들에  위  서버  json파일에서 받은  포지션별  데이터들 뷰들에  넣어줌.
        Glide.with(contextparent).load(imageUrl).into(holder.nationflag);//나라 국기  넣어줌.
        holder.teachername.setText(textforteachername.replaceAll("\"",""));//선생님 이름
        holder.textViewfornativeornot.setText(textnativeonot);//선생님  global  or  native 텍스트




        //sentence값이  null일경우  -> 서버에서  null 의 경우도 "null"로  들어와져서  String 으로 받을 필요가 있음.
        if(textforteachersentence.equals("null")){
            holder.textViewforshortsentence.setText(null);//선생님  shortsentence

        }else{

            holder.textViewforshortsentence.setText(textforteachersentence);//선생님  shortsentence
        }

    }//onBindViewHolder 끝끝


    //리사이클러뷰  item개수    @Override
    public int getItemCount() {

        Log.v("check", "TeacherInfoRecyclerview 아이템 개수"+mData.size());
        return mData.size();
    }//getitemCount  끝












}
