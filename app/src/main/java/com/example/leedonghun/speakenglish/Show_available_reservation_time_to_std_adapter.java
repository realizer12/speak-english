package com.example.leedonghun.speakenglish;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;


import java.util.ArrayList;


/**
 * speakenglish
 * Class: Show_available_reservation_time_to_std_adapter.
 * Created by leedonghun.
 * Created On 2020-03-12.
 * Description: 학생이  teacherprofile에서  선생님 수업 예약 할때  예약하기 버튼 클릭시 나오는  다이얼로그에서
 * 해당  선생님이  추가한  예약 가능한 수업 리스트를  리사이클러뷰에  뿌려주는 adapter이다.
 */
public class Show_available_reservation_time_to_std_adapter extends RecyclerView.Adapter {

    private  int Selected_count=0;//카운트 체크하기

    private Context context;//context
    private LayoutInflater layoutInflater;//레이아웃 인플레이터
    private ArrayList<JsonObject> list_for_available_class_time;//선생님  예약 가능한  리스트;

    private ArrayList<Integer> list_for_get_selected_class=new ArrayList<>();

    //어뎁터 생성자
    Show_available_reservation_time_to_std_adapter(Context context,ArrayList<JsonObject> list_for_available_class_time){

        this.list_for_available_class_time=list_for_available_class_time;//
        this.context=context;//context받음

    }//어뎁터 생성자 끝.


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context=parent.getContext();//context

        View view;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view=layoutInflater.inflate(R.layout.item_for_show_available_reservation_time_for_std_dialog, parent,false);

        return new show_available_time_list_for_student_viewholder(view);

    }//onCreateViewHolder 끝

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        String available_time=list_for_available_class_time.get(position).get("availabledate").getAsString();//수업 가능한 시간

        int status_for_reserve=list_for_available_class_time.get(position).get("reserve_status").getAsInt();//예약 여부->  0이면  아직 예약 안되었으므로,  예약 가능하지만,  1이면  예약이 되었으므로  뿌려주지 않는다.


        if(status_for_reserve==0){//0일때  예약이 안됨

            ((show_available_time_list_for_student_viewholder)holder).btn_for_show_available_time_in_std_dialog.setText(available_time);

        }else{//예약이 된것들은  GONE처리로  안보이게 한다.
            ((show_available_time_list_for_student_viewholder)holder).btn_for_show_available_time_in_std_dialog.setVisibility(View.GONE);
        }



    }//onBindviewHolder () 끝

    @Override
    public int getItemCount() {

        return list_for_available_class_time.size();
    }

    public int show_selected_count(){


        return Selected_count;
    }

    public ArrayList<Integer> show_selected_class_list(){


        return list_for_get_selected_class;
    }


    class show_available_time_list_for_student_viewholder extends  RecyclerView.ViewHolder{

        Button btn_for_show_available_time_in_std_dialog;

        public show_available_time_list_for_student_viewholder(@NonNull View itemView) {
            super(itemView);

            btn_for_show_available_time_in_std_dialog=itemView.findViewById(R.id.btn_for_show_available_time_in_std_dialog);



            btn_for_show_available_time_in_std_dialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                   //하얀색이면 선택된거임.-> 다시 선택 된걸  눌러주니까  update되넉를 취소 시킴.
                  if(btn_for_show_available_time_in_std_dialog.getCurrentTextColor()==Color.WHITE) {


                      btn_for_show_available_time_in_std_dialog.setTextColor(Color.BLACK);//버튼 글씨 색  검정
                      btn_for_show_available_time_in_std_dialog.setBackground(ContextCompat.getDrawable(context,R.drawable.roundedittext));//버튼 백그라운드  round로 다시
                      btn_for_show_available_time_in_std_dialog.setBackgroundTintList(null);//원래 검정이었던 색은

                      Selected_count=Selected_count-1;//취소했으므로  선택  카운트를  1 낮춰준다.

                  }else{//해당 텍스트가 하얀색이 아니면  아직 선택 안한 것이므로,  의사 확인하는 알럴트 띄움


                      AlertDialog.Builder alert_builder=new AlertDialog.Builder(context);

                      alert_builder.setCancelable(true);
                      alert_builder.setTitle("예약 확인");
                      alert_builder.setMessage("정말 "+btn_for_show_available_time_in_std_dialog.getText()+"시간에 \n"+"예약을 하시겠어요??");

                      alert_builder.setNeutralButton("취소", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialog, int which) {


                              dialog.dismiss();

                          }
                      });

                      alert_builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialog, int which) {


                              btn_for_show_available_time_in_std_dialog.setTextColor(Color.WHITE);
                              btn_for_show_available_time_in_std_dialog.setBackgroundTintList(context.getResources().getColorStateList(R.color.black_for_availabletime));

                              list_for_get_selected_class.add(list_for_available_class_time.get(getAdapterPosition()).get("uid").getAsInt());

                              Selected_count=Selected_count+1;//선택했으므로  selected count를  1  올려준다.
                              dialog.dismiss();

                          }
                      });


                      AlertDialog alertDialog=alert_builder.create();

                      alertDialog.show();

                  }//아직  선택 안했을때 조건 끝



                }//onClick() 끝
            });



        }//show_available_time_list_for_student_viewholder()끝
    }//show_available_time_list_for_student_viewholder끝



}//Show_available_reservation_time_to_std_adapter 끝
