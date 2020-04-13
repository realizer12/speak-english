package com.example.leedonghun.speakenglish;

import android.content.Context;
import android.content.Intent;
//import android.support.annotation.NonNull;
//import android.support.v7.widget.CardView;
//import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * speakenglish
 * Class: LoginTeacherInfoRecyvlerviewAdapter.
 * Created by leedonghun.
 * Created On 2019-09-23.
 * Description:로그인한 선생님 리스트를  리사이클러뷰로
 * 뿌려주기위해  필요한  리사이클러뷰 어뎁터이다.
 */
public class LoginTeacherInfoRecyvlerviewAdapter extends RecyclerView.Adapter<LoginTeacherInfoRecyvlerviewAdapter.LoginTeacherInfoRecyvlerviewViewholder> {

    //로그인한 선생님 리스트 데이터 들어가는 어레이리스트
    private   ArrayList<JsonObject> loginteacherlistdata;

    //필터링에서  각 필터값마다  로그인된 선생님 리스트를 나눠서  다시  넣어주기위한  어레이리스트이다.
    private   ArrayList<JsonObject> filteredlistdata=new ArrayList<>();

    //로그인한 선생님중 현재 로그인한 학생의 mytutor로 등록되어있는 선생님들을  담기위한 어레이 리스트이다.
    private   ArrayList<JsonObject> mytutorcount=new ArrayList<>();



   //레이아웃 인플레이터
   private LayoutInflater layoutInflater;

   //리사이클러뷰 뷰홀더
   private   LoginTeacherInfoRecyvlerviewViewholder  teacherInfoRecyclerviewHoler;





   //로그인 adapter 생성자
    LoginTeacherInfoRecyvlerviewAdapter(ArrayList<JsonObject> listdata, Context context,int checkfilter, TextView textviewfornologinfilterment,Button mytutorbtn){

        Log.v("check", "Login_Teacher_List에서 넘어온  필터링 체크 값 => "+checkfilter);

        //리사이클러뷰에서 쓸  arratlist 변수는  ->  filtering 메소드를 통해  값을  도출해  받는다.
        this.loginteacherlistdata= filtering(listdata, checkfilter,context,textviewfornologinfilterment);

        //Logined_Teacher_List 에서  내튜터 버튼에  text를  현재 선생님  숫자를 같이 넣어서  보여준다.
        mytutorbtn.setText("내 튜터"+"("+countmytutor(listdata)+")");


    }//로그인 adapter 생성자 끝.



    //로그인 선생님 리스트 리사이클러뷰   홀더  클래스
    class LoginTeacherInfoRecyvlerviewViewholder extends RecyclerView.ViewHolder{


        CardView teachercardview;//로그인한 선생님 정보 들어가는 카드뷰
        TextView teachername;//로그인한 선생님 이름
        TextView teachernativeornot;//로그인한 선생님 native or global
        TextView teachershortsentence;//로그인한 선생님 shortsentence
        ImageView teacherprofile;//로그인한 선생님  프로필이미지
        ImageView teachercountryflag;//로그인한 선생님 국적  국기이미지
        Button loginteacherstartclassbtn;//로그인한 선생님 수업 시작 버튼.
        Context contextforviewholder;//인텐트 사용하기 위해서  -> 필요한  context


        //뷰홀더 생성자
        LoginTeacherInfoRecyvlerviewViewholder(final View itemView) {
            super(itemView);

            //카드뷰xml하고  뷰  객체 연결
            teachercardview=itemView.findViewById(R.id.loginteachercardview);
            teachername=itemView.findViewById(R.id.loginteachername);
            teachernativeornot=itemView.findViewById(R.id.loginteacherglobalornative);
            teachershortsentence=itemView.findViewById(R.id.loginteachershortsentence);
            teacherprofile=itemView.findViewById(R.id.loginteacherprofile);
            teachercountryflag=itemView.findViewById(R.id.loginteacherflagimg);
            loginteacherstartclassbtn=itemView.findViewById(R.id.loginteacherstartclassbtn);
            contextforviewholder=itemView.getContext();

            //각  아이템 클릭리스너
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.v("check_", "선생님 목록  리사이클러뷰 아이템 눌림");

                    int posiotn=getAdapterPosition();//현재 클릭된 아이템 뷰 포지션

                     //Teacherprofile로 보냄.
                     Intent gototeacherprofile=new Intent(contextforviewholder,TeacherProfile.class);

                     //각 클릭된  포지션의 선생님 정보를 보내서 teacherprofile을 완성
                     gototeacherprofile.putExtra("teacherinfo", String.valueOf(loginteacherlistdata.get(posiotn)));

                     //Teacherprofile엑티비티에서  선생님 정보가  어디서 왔는 지 여부를 체크 하기 위해  넣어줌.
                     gototeacherprofile.putExtra("teacherinfocheck", 1);//1은   로그인된 선생님 리스트에서 보낸거임.  0은  전체 선생님 리스트에서.
                     contextforviewholder.startActivity(gototeacherprofile);//teacherprofile 클래스  시작.

                }//onclick 끝

            });//리사이클러뷰 아이템 클릭리스너 끝.



            //수업 시작 버튼 눌렀을때  리스너
            loginteacherstartclassbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(contextforviewholder, "수업시작 버튼 눌림.", Toast.LENGTH_SHORT).show();
                }
            });
            //수업시작 버튼 눌렀을때 리스너 끝.


        }//뷰홀더 생성자 끝

    }//뷰 홀더 끝.




     //뷰홀더 생성하기.
    @NonNull
    @Override
    public LoginTeacherInfoRecyvlerviewViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext() ;//부모 레이아웃  컨텍스트 사용.
       layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//인플레이터   컨텍스트 권한 줌
        View view=layoutInflater.inflate(R.layout.cardviewforloginteacherinfo, parent,false);//로그인한 선생님  데이터 담을 카드뷰 인플레이트 함.

        //위  뷰홀더  생성자에  카드뷰 넣어서  생성자에  선언됬던 모든 뷰들 카드뷰에 연결
        teacherInfoRecyclerviewHoler=new LoginTeacherInfoRecyvlerviewViewholder(view);


        return teacherInfoRecyclerviewHoler;
    }//oncreateviewholder 끝



    //데이터와  뷰  홀더 안  뷰들   연결 시킴.  -> bindviewholder
    @Override
    public void onBindViewHolder(@NonNull LoginTeacherInfoRecyvlerviewViewholder holder, int position) {




            //로그인한 선생님 이름 합침.
            //선생님 데이터 중에   이름
            String teachername = loginteacherlistdata.get(position).get("name").toString();

            //선생님  shorsentence들
            String shortsentence = loginteacherlistdata.get(position).get("shortsentence").toString();

            //선생님  프로필 이미지  경로
            String profilepath = String.valueOf(loginteacherlistdata.get(position).get("profilepath")).replaceAll("\"", "");
            //프로필 이미지 url 스트링
            String profilemageurl = "http://13.209.249.1/" + profilepath;

            //선생님  나라 코드
            String flagimag = String.valueOf(loginteacherlistdata.get(position).get("countrycode")).replaceAll("\"", "");
            //국기 url 스트링
            String flagimageUrl = "https://www.countryflags.io/" + flagimag + "/flat/16.png";


            //카드뷰 안  뷰들에  위  서버  json파일에서 받은  포지션별  데이터들 뷰들에  넣어줌.
            Glide.with(holder.contextforviewholder).load(flagimageUrl).into(holder.teachercountryflag);//나라 국기  넣어줌.
            Glide.with(holder.contextforviewholder).load(profilemageurl).into(holder.teacherprofile);//선생님 프로필 넣어줌.

            //선생님  native or global 여부 체크
            String nativeornotcheck = String.valueOf(loginteacherlistdata.get(position).get("nativeornot")).replaceAll("\"", "");
            String nativeornot = null;//native or global 스트링 담기

            //1일결우 native
            if (nativeornotcheck.equals("1")) {
                nativeornot = "native";
            } else if (nativeornotcheck.equals("0")) {//0일 경우  global
                nativeornot = "global";
            }


            holder.teachernativeornot.setText(nativeornot);//선생님 nativeornot 여부텍스트 넣기
            holder.teachername.setText(teachername.replaceAll("\"", ""));//선생님 이름 넣음.
            holder.teachershortsentence.setText(shortsentence.replaceAll("\"", ""));//선생님  shortsentence 넣어줌.

    }//onBindViewholder 끝


    //아이템  사이즈 -> 아이템  개수들.
    @Override
    public int getItemCount() {

        return loginteacherlistdata.size();
    }//getitemcount 끝



    //필터링 메소드 ->  필터링  값을 받아오면  각각  값에 따라  받아온  jsonarray -> 'listdata'의 데이터들을  다시  분석하여,
    //그중에   native인 사람들만,  또는  global인 사람들만, 또는  모든 선생님 ,  내 성생님 인경우로 나눠서  필터링해줌,
    //매개 변수로  ->  기본 listdata -> filterinf 값  context가 있음.
    private ArrayList<JsonObject> filtering(ArrayList<JsonObject> listdata,int filtercheck,Context context,TextView textView){



        if(filtercheck==0){//전체 리스트가 들어가는 부분.

            filteredlistdata=listdata;//전체 리스트 넣어줌,-> 전체여서  따로 필터링 할 필요 x-> for문 안넣어도 됨.

        }else{//필터링 값이  0이 아닐경우-> 즉 전체 가 아닐경우

            //전체 데이터  한번씩 보기위한 for문
            for (int i = 0; i < listdata.size(); i++) {
                Log.v("check", "로그인 된 선생님 데이터 하나씩 보기 ->인덱스" + i + "의  데이터  => " + listdata.get(i));

                if (filtercheck == 1) {//내튜터  누를때  일어나는 조건
                    if (listdata.get(i).get("mytutorornot").toString().replaceAll("\"", "").equals("1")) {
                        //jobj에  각각  글로벌 인 데이터들만 넣어줌.
                        JsonObject jobj = new JsonObject();
                        jobj.add("id", listdata.get(i).get("id"));
                        jobj.add("email", listdata.get(i).get("email"));
                        jobj.add("name", listdata.get(i).get("name"));
                        jobj.add("countrycode", listdata.get(i).get("countrycode"));
                        jobj.add("nativeornot", listdata.get(i).get("nativeornot"));
                        jobj.add("profilepath", listdata.get(i).get("profilepath"));
                        jobj.add("teachercareer", listdata.get(i).get("teachercareer"));
                        jobj.add("shortsentence", listdata.get(i).get("shortsentence"));
                        jobj.add("teachersayhellow", listdata.get(i).get("teachersayhellow"));

                        filteredlistdata.add(jobj);//filterlistdata 어레이에  차례대로 넣어줌.
                    }
                } else if (filtercheck == 2) {//필터 값이 global 일 경우

                     //해당 인덱스의 데이터가  글로벌 선생님  데이터일경우 ->0
                    if (listdata.get(i).get("nativeornot").toString().replaceAll("\"", "").equals("0")) {

                        //jobj에  각각  글로벌 인 데이터들만 넣어줌.
                        JsonObject jobj = new JsonObject();
                        jobj.add("id", listdata.get(i).get("id"));
                        jobj.add("email", listdata.get(i).get("email"));
                        jobj.add("name", listdata.get(i).get("name"));
                        jobj.add("countrycode", listdata.get(i).get("countrycode"));
                        jobj.add("nativeornot", listdata.get(i).get("nativeornot"));
                        jobj.add("profilepath", listdata.get(i).get("profilepath"));
                        jobj.add("teachercareer", listdata.get(i).get("teachercareer"));
                        jobj.add("shortsentence", listdata.get(i).get("shortsentence"));
                        jobj.add("teachersayhellow", listdata.get(i).get("teachersayhellow"));

                        filteredlistdata.add(jobj);//filterlistdata 어레이에  차례대로 넣어줌.
                    }

                } else if (filtercheck == 3) {//필터값이 native 일 경우

                     //해당 인덱스 데이터가  네이티브 선생님 일경우 ->1
                    if (listdata.get(i).get("nativeornot").toString().replaceAll("\"", "").equals("1")) {

                        //jobj에  각각  글로벌 인 데이터들만 넣어줌.
                        JsonObject jobj = new JsonObject();
                        jobj.add("id", listdata.get(i).get("id"));
                        jobj.add("email", listdata.get(i).get("email"));
                        jobj.add("name", listdata.get(i).get("name"));
                        jobj.add("countrycode", listdata.get(i).get("countrycode"));
                        jobj.add("nativeornot", listdata.get(i).get("nativeornot"));
                        jobj.add("profilepath", listdata.get(i).get("profilepath"));
                        jobj.add("teachercareer", listdata.get(i).get("teachercareer"));
                        jobj.add("shortsentence", listdata.get(i).get("shortsentence"));
                        jobj.add("teachersayhellow", listdata.get(i).get("teachersayhellow"));

                        filteredlistdata.add(jobj);//filterlistdata 어레이에  차례대로 넣어줌.
                    }

                }//filtercheck=3 ->  native선생님들만  필터링 끝

                Log.v("check", "필터링해서  새 어레이리스트에 넣어준다. -> 새어레이리스트 -> 데이터 =>" + filteredlistdata);

            }//for문   끝

        }//필터링 0 아닐경우 -> 전체가 아닐경우

        if(filteredlistdata.size()<=0){
            //필터링된  사이즈가 0이하이면  -> 해당 필터에  데이터가 없는것이므로, 새로고침하라는 멘트를 알리는 텍스트뷰를  보이게한다.
               textView.setVisibility(View.VISIBLE);

        }else{
            //필터링된  사이즈가 0이상이면  -> 해당 필터에  데이터가 있는것이므로, 새로고침하라는 멘트를 알리는 텍스트뷰를 gone을 이용해  레이아웃에서 없어지게 한다.
            textView.setVisibility(View.GONE);
        }

        return filteredlistdata;//새롭게 필터된  값들을  넣은  arraylist를  리턴값으로 하여 보낸다.
    }//filertring메소드 끝


   //로그인된 선생님 리스트에서  내튜터로  등록된 선생님들의 숫자 세기위한 메소드
   private int countmytutor(ArrayList<JsonObject> listdata){

        //리스트데이터 index별로 판단하기위한  for문
       for (int i = 0; i < listdata.size(); i++) {

           //각각의 index들  중에서 내 튜터 여부가  1일때 조건문->  현재로그인한 학생의 mytutor등록되어있다는 의미
           if(listdata.get(i).get("mytutorornot").toString().replaceAll("\"", "").equals("1")) {

             //해당  index별 데이터들을  mytutorcouunt  arraylist에  담아준다.
            JsonObject jobj = new JsonObject();
            jobj.add("id", listdata.get(i).get("id"));
            mytutorcount.add(jobj);

           }
       }

       return mytutorcount.size();//위에서 내튜터인 선생님들  리스트에 담은것의 사이즈를 리턴한다.
   }

}//LoginTeacherInforRecyclerviewAdapter  클래스 끝.
