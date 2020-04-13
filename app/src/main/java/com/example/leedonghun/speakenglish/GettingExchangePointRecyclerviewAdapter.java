package com.example.leedonghun.speakenglish;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import java.util.ArrayList;

/**
 * speakenglish
 * Class: GettingExchangePointRecyclerviewAdapter.
 * Created by leedonghun.
 * Created On 2020-03-29.
 * Description:선생님  환전 신청  리스트  받아서 뿌려주는 리사이클러뷰 어뎁터이다.
 */
public class GettingExchangePointRecyclerviewAdapter extends RecyclerView.Adapter {

    private ArrayList<JsonObject>arrayList_for_request_exchange;
    private Context context;
    private LayoutInflater layoutInflater;//레이아웃 인플레이터
    //어뎁터 생성자
    GettingExchangePointRecyclerviewAdapter(Context context,ArrayList<JsonObject>arrayList_for_request_exchange){

        this.arrayList_for_request_exchange=arrayList_for_request_exchange;
        this.context=context;

    }//생성자 끝,


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view=layoutInflater.inflate(R.layout.exchange_request_list_item, parent,false);

        return new get_exchange_request_ilist_viewholder(view);
    }//onCreateViewHolder() 끝

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

         //환전 요청한 포인트량
        String request_point_amount=arrayList_for_request_exchange.get(position).get("request_point").getAsString();

        //환전  계좌 번호
        String account_number=arrayList_for_request_exchange.get(position).get("bank_acoount").getAsString();

        //환전 상태여부
        int status=arrayList_for_request_exchange.get(position).get("status").getAsInt();

        int bank_selection=arrayList_for_request_exchange.get(position).get("bank_position").getAsInt();



        //환전되는 돈의 양  요구한 포인트 곱하기  10
        int exchange_money= Integer.parseInt(request_point_amount)*10;


        ((get_exchange_request_ilist_viewholder) holder).txt_for_show_point.setText(request_point_amount+" P");
        ((get_exchange_request_ilist_viewholder) holder).txt_for_show_money.setText(exchange_money+" w");

        //은행  selection값에 따라  맞는 은행을 넣어줌.
        if(bank_selection==1){//은행이 카카오일때
            ((get_exchange_request_ilist_viewholder) holder).txt_for_show_account_number.setText("카카오 "+account_number);

        }else if(bank_selection==2){//은행이 신한일때

            ((get_exchange_request_ilist_viewholder) holder).txt_for_show_account_number.setText("신한 "+account_number);

        }else if(bank_selection==3){//은행이 국민 일때

            ((get_exchange_request_ilist_viewholder) holder).txt_for_show_account_number.setText("국민 "+account_number);

        }else if(bank_selection==4){//은행이 기업일때

            ((get_exchange_request_ilist_viewholder) holder).txt_for_show_account_number.setText("기업 "+account_number);

        }else if(bank_selection==5){//은행이 농협일때

            ((get_exchange_request_ilist_viewholder) holder).txt_for_show_account_number.setText("농협 "+account_number);
        }



        if(status==0){//환급 완료일때

            ((get_exchange_request_ilist_viewholder) holder).txt_for_show_status.setText("Accepted");
            ((get_exchange_request_ilist_viewholder) holder).txt_for_show_status.setTextColor(Color.GREEN);//글씨 색  흰색

        }else if(status==1){//환급 심사 대기중일때

            ((get_exchange_request_ilist_viewholder) holder).txt_for_show_status.setText("Progress");
            ((get_exchange_request_ilist_viewholder) holder).txt_for_show_status.setTextColor(Color.BLACK);

        }else if(status==2){//환급이  취소되었을때

            ((get_exchange_request_ilist_viewholder) holder).txt_for_show_status.setText("Rejected");
            ((get_exchange_request_ilist_viewholder) holder).txt_for_show_status.setTextColor(Color.RED);

        }




    }//onBindViewHolder()끝

    @Override
    public int getItemCount() {

        return arrayList_for_request_exchange.size();
    }//getItemCount()끝



    class get_exchange_request_ilist_viewholder extends RecyclerView.ViewHolder{

        private TextView txt_for_show_point;//포인트 보여줌  1-1
        private TextView txt_for_show_money;//전환되는 금액 보여줌  1-2
        private TextView txt_for_show_account_number;//계좌 번호 보여줌 1-3
        private TextView txt_for_show_status;//환전  상태 보여줌 1-4

        public get_exchange_request_ilist_viewholder(@NonNull View itemView) {
            super(itemView);


            txt_for_show_point=itemView.findViewById(R.id.txt_for_show_point);//1-1
            txt_for_show_money=itemView.findViewById(R.id.txt_for_show_money);//1-2
            txt_for_show_account_number=itemView.findViewById(R.id.txt_for_show_account_number);//1-3
            txt_for_show_status=itemView.findViewById(R.id.txt_for_show_status);//1-4

        }


    }//get_exchange_request_ilist_viewholder 끝



}//GettingExchangePointRecyclerviewAdapter 끝
