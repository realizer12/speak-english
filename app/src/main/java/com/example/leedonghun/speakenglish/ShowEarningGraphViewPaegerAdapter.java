package com.example.leedonghun.speakenglish;

import android.content.Context;
import android.graphics.Color;
import android.icu.util.GregorianCalendar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * speakenglish
 * Class: ShowEarningGraphViewPaegerAdapter.
 * Created by leedonghun.
 * Created On 2020-03-23.
 *
 * Description: 선생님이  얻는 point량을  맵으로 보여주기 위한  클래스이다.
 * 오늘 얻은 point량  및 몇시  포인트 얻었는지를 보여주는 그래프와
 * 오늘 부터 지난 7일 까지  point 획득이 있는 날짜와 포인트 량을 보여주는 그래프가 있다
 * 그래서 뷰페이져 수는  총 2개이다.
 */
public class ShowEarningGraphViewPaegerAdapter extends PagerAdapter {

    private  String ment_for_log="ShowEarningGraphViewPaegerAdapter의";
    private  Context context;//사용될  컨텍스트
    private  BarChart barChart;//뷰페이저에 들어갈  라인차트

    private  ArrayList<JsonObject> result_arraylist;//서버에서 받아온  선생님  지난  7일간의  포인트 획득 데이터

    //뷰페이져  생성자
    ShowEarningGraphViewPaegerAdapter(Context context, ArrayList<JsonObject> result_arraylist){

        this.result_arraylist=result_arraylist;
        this.context=context;

    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {

        //뷰페이지 인플레이트 할 인플레이터  선언.
        LayoutInflater layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewpager_for_show_earning_graph=layoutInflater.inflate(R.layout.teacher_point_graph, container,false);

        container.addView(viewpager_for_show_earning_graph);//뷰페이져에  chart view 넣어줌.



        //바 차트
        barChart=viewpager_for_show_earning_graph.findViewById(R.id.Bar_chart_graph);

        Date todaydate=new Date(System.currentTimeMillis());//현재시간


        SimpleDateFormat date_compare=new SimpleDateFormat("yyyy-MM-dd");//오늘 날짜인지를  확인 하기 위한  dateformat
        SimpleDateFormat entrie_date_format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");//전체 날짜  format
        SimpleDateFormat simpleDate_get_hour = new SimpleDateFormat("HH");//시간만  받아오기위한  format

        SimpleDateFormat simpleDate_get_day = new SimpleDateFormat("MM-dd");//시간만  받아오기위한  format

        String compare_today_date=date_compare.format(todaydate);//현재시간을   년월일 포맷으로 뽑아 그래프화 시킬  날짜를  뽑아낸다.


        if(position==0){//오늘 하루


            //bar 차트에  들어갈  값  넎는  어레이리스트
            ArrayList<BarEntry> entries = new ArrayList<>();

            ArrayList<String>check_array=new ArrayList<>();
            final ArrayList<JSONObject> today_time_list = new ArrayList<>();//서버에서 가져온 시간중에  오늘 시간만 다시 넣어줌.

            //전체 선생님 받은 7주일치 포인트 기록  어레이 반복문  돌림
            for(int i=0; i<result_arraylist.size(); i++){

                Log.v("check", ment_for_log+" 어레이값 ->"+result_arraylist.get(i));
                try {

                    //스트링 타입  날짜를  date로 변환  yyyy-MM-dd 포맷으로..
                    Date pased_date= date_compare.parse(result_arraylist.get(i).get("class_date").getAsString());

                    String date_parsed=date_compare.format(pased_date);
                    Log.v("check",ment_for_log+" date_parsed=>  "+ date_parsed);

                    Date pased_dateto_hour= entrie_date_format.parse(result_arraylist.get(i).get("class_date").getAsString());
                    String date_parsed_to_hour=simpleDate_get_hour.format(pased_dateto_hour);
                    Log.v("check",ment_for_log+" date_hour_parsed=>  "+ date_parsed_to_hour);

                    //오늘 시간과  서버에  가져온 시간  확인 해서  오늘에 해당 하는 시간만  다시 어레이에 넣어준다.
                    if(compare_today_date.equals(date_parsed)){



                            Log.v("today_time_list", check_array +" datepare"+date_parsed_to_hour);
                            if(check_array.contains(date_parsed_to_hour)){
                                Log.v("check", ment_for_log+" today_time_list =>  포함");

                                int chcek=check_array.indexOf(date_parsed_to_hour);
                                int point= (int) today_time_list.get(chcek).get("point");
                                point=point+10;

                                JSONObject jsonObject = new JSONObject();

                                jsonObject.put("class_date", date_parsed_to_hour);
                                jsonObject.put("point", point);

                                today_time_list.set(chcek,jsonObject);


                            }else{
                                Log.v("check", ment_for_log+" today_time_list =>  미포함");

                                check_array.add(date_parsed_to_hour);
                                JSONObject jsonObject = new JSONObject();

                                jsonObject.put("class_date", date_parsed_to_hour);
                                jsonObject.put("point", 10);

                                today_time_list.add(jsonObject);
                            }

                    }

                }catch (ParseException e) {

                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }//for문 끝


            Log.v("check", ment_for_log+" today_time_list =>  "+today_time_list);
            Log.v("check", ment_for_log+" today_time_list =>  "+check_array);

            for(int h=0; h<today_time_list.size(); h++){

                try {
                    entries.add(new BarEntry(Float.parseFloat(String.valueOf(h)), today_time_list.get(h).getInt("point")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }//for문 끝

            BarDataSet barDataSet = new BarDataSet(entries, "TODAY  Earning points");
            barDataSet.setBarBorderWidth(1f);

            barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
            BarData barData = new BarData(barDataSet);

            final ArrayList<String> time = new ArrayList<>();

            for(int k=0; k<check_array.size(); k++){

                time.add(k,check_array.get(k)+"시");
            }

            final String[] point_count = new String[51];
            point_count[0]="0p";
            point_count[10]="10p";
            point_count[20]="20p";
            point_count[30]="30p";
            point_count[40]="40p";
            point_count[50]="50p";

            IndexAxisValueFormatter formatter_for_point_count = new IndexAxisValueFormatter(point_count);


            YAxis yL = barChart.getAxisLeft();//Y축  왼쪽거 ->  보이는 부분
            YAxis yr=barChart.getAxisRight();//Y축  오른쪽거  -> 안보이게 하기

            yr.setEnabled(false);// Y축  오른쪽 꺼는  안보이게 해준다.

            yL.setAxisMaxValue(50);
            yL.setAxisMinValue(0);
            yL.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
            yL.setValueFormatter(formatter_for_point_count);

            IndexAxisValueFormatter formatter = new IndexAxisValueFormatter(time);
            XAxis xAxis = barChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setLabelCount(today_time_list.size());
            xAxis.setTextSize(10f);
            xAxis.setDrawAxisLine(true);
            xAxis.setGranularity(1f);

            xAxis.setValueFormatter(formatter);

            barChart.setData(barData);
            barChart.animateXY(1200, 1200);
            barChart.setScaleEnabled(false);
            barChart.invalidate();

        }//포지션이  0일때  끝->  (today) 포인트 기록
        else if(position==1){


            //bar 차트에  들어갈  값  넎는  어레이리스트
            ArrayList<BarEntry> entries = new ArrayList<>();

            ArrayList<String>check_array=new ArrayList<>();//day  중복  제거하고  들어가는  어레이 리스트
            final ArrayList<JSONObject> this_week_day_list = new ArrayList<>();//서버에서 가져온 시간중에  오늘 시간만 다시 넣어줌.

            //전체 선생님 받은 7주일치 포인트 기록  어레이 반복문  돌림
            for(int i=0; i<result_arraylist.size(); i++){

                Log.v("check", ment_for_log+" 어레이값 ->"+result_arraylist.get(i));
                try {

                    //스트링 타입  날짜를  date로 변환  yyyy-MM-dd 포맷으로.. 비교에는 날짜만 필요하므로  시간  빼줌.
                    Date pased_date= date_compare.parse(result_arraylist.get(i).get("class_date").getAsString());
                    String date_parsed=date_compare.format(pased_date);//날짜 스트링  변환 yyyy-MM-dd포맷
                    Log.v("check",ment_for_log+" date_parsed=>  "+ date_parsed);

                    //date 전체  날짜  다시 yyyy-MM-dd HH:ss  포맷으로 다시 받아서  ->  시간 DD만  다시  뽑아낸다.
                    Date pased_dateto_day= entrie_date_format.parse(result_arraylist.get(i).get("class_date").getAsString());
                    String date_parsed_to_day=simpleDate_get_day.format(pased_dateto_day);
                    Log.v("check",ment_for_log+" date_hour_parsed=>  "+ date_parsed_to_day);
                    Log.v("today_time_list", check_array +" datepare"+date_parsed_to_day);


                         //만약에 중복 제거된  day리스트에  -> 서버에서 받아온  데이터의  같은 day가  존재한다면,
                        //중복 제거된  day리스트에  해당하는 day의  포인트 수만  10포인트 올리고  제거 해준다.
                        if(check_array.contains(date_parsed_to_day)){

                            Log.v("check", ment_for_log+" today_time_list =>  포함");

                            //해당  day 가 잇는 인덱스를  알아낸다.
                            //해당 중복제거된  day리스트와   이번주  daylist의  index는 같다.
                            //왜냐면  중복제거된 리스트는  daylist 에  day를 넣기위한  체크용이기 때문
                            //그래서  해당  중복제건된  데이 리스트의  인덱스를 가지고 오면
                            //이번주 daylist 의   index에  똑같은  day값이 들어있므로, index를  돌려 쓸수 있다.
                            int chcek=check_array.indexOf(date_parsed_to_day);

                            //위  cheak_array에서  가져온  index를  week_day_list 에  넣어서 해당 index값의  point값을 뺴온다.
                            //그리고  그 포인트에 10을  더해준다.
                            int point= (int) this_week_day_list.get(chcek).get("point");
                            point=point+10;

                            //새롭게 jsonobject를  만들어서
                            //해당 this_week_day_list 인덱스 의 값에  교체 시켜준다.
                            JSONObject jsonObject = new JSONObject();

                            jsonObject.put("class_date", date_parsed_to_day);
                            jsonObject.put("point", point);

                            this_week_day_list.set(chcek,jsonObject);//index값  교체해줌.


                        }else{//만약에  기존  리스트에 있는 day가  아닌  새로운 day인 경우 ->  새롭게 this_week_day_list에  값을 추가 시켜준다.

                            Log.v("check", ment_for_log+" today_time_list =>  미포함");

                            check_array.add(date_parsed_to_day);
                            JSONObject jsonObject = new JSONObject();

                            jsonObject.put("class_date", date_parsed_to_day);
                            jsonObject.put("point", 10);

                            this_week_day_list.add(jsonObject);

                        }



                }catch (ParseException e) {

                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.v("check", ment_for_log+" today_time_list_for_weekday   this_week_day_list=>  "+this_week_day_list);
                Log.v("check", ment_for_log+" today_time_list_for_weekday  check_array=>  "+check_array);

            }//for문 끝


            //weeo_day_list 반복문으로 돌려서  barchart  entry에  넣어줌.
            for( int h=0; h < this_week_day_list.size(); h++ ){
                try{
                    entries.add(new BarEntry(Float.parseFloat(String.valueOf(h)), this_week_day_list.get(h).getInt("point")));
                }catch(JSONException e) {
                    e.printStackTrace();
                }
            }//for문 끝

            //barchart  데이터 세팅
            BarDataSet barDataSet = new BarDataSet(entries, "past 7 days  Earning points   which is  more than 10points");
            barDataSet.setBarBorderWidth(1f);

            barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
            BarData barData = new BarData(barDataSet);


            //x축  라벨로  쓰기 위해   해당 라벨들을  넣어준다 어레이 리스트
            final ArrayList<String> time = new ArrayList<>();

            Calendar cal=Calendar.getInstance();//캘린더 객체
            cal.add(Calendar.DATE, -1);//하루전 칼린더

            for(int k=0; k<check_array.size(); k++){

                String today_date=simpleDate_get_day.format(todaydate);


                String yester_day=simpleDate_get_day.format(cal.getTime());//오늘 보다  1루전  날짜

                //어제랑 오늘은  아래와 같이  처리해준다.
                if(today_date.equals(check_array.get(k))){//오늘이랑 날짜가 같으면,

                    time.add(k,"오늘");


                }else if(yester_day.equals(check_array.get(k))) {//하루전 날짜일경우 d어제

                    time.add(k,"어제");

                }else{//오늘이랑 어제 날짜외에는  그냥  일반  날짜를 그대로 넣어준다.

                    time.add(k,check_array.get(k));
                }

            }

            //y축 라벨로 사용할  값들
            final String[] point_count = new String[101];
            point_count[0]="0p";
            point_count[10]="10p";
            point_count[20]="20p";
            point_count[30]="30p";
            point_count[40]="40p";
            point_count[50]="50p";
            point_count[60]="60p";
            point_count[70]="70p";
            point_count[80]="80p";
            point_count[90]="90p";
            point_count[100]="100p";

            IndexAxisValueFormatter formatter_for_point_count = new IndexAxisValueFormatter(point_count);


            YAxis yL = barChart.getAxisLeft();//Y축  왼쪽거 ->  보이는 부분
            YAxis yr=barChart.getAxisRight();//Y축  오른쪽거  -> 안보이게 하기

            yr.setEnabled(false);// Y축  오른쪽 꺼는  안보이게 해준다.

            yL.setAxisMaxValue(100);//y 왼쪽 축  최대  값 100
            yL.setAxisMinValue(0);//y 왼쪽 축  최소값 0
            yL.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);//chart 밖에  라벨 포지션함
            yL.setValueFormatter(formatter_for_point_count);// y축 라벨  연결 시켜줌.

            //x축  라벨  fommatter
            IndexAxisValueFormatter formatter = new IndexAxisValueFormatter(time);
            XAxis xAxis = barChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setLabelCount(this_week_day_list.size());
            xAxis.setTextSize(10f);
            xAxis.setDrawAxisLine(true);
            xAxis.setGranularity(1f);

            xAxis.setValueFormatter(formatter);

            barChart.setData(barData);
            barChart.animateXY(1200, 1200);//bar 애니메이션
            barChart.setScaleEnabled(false);
            barChart.invalidate();


        }//포지션 1일때  끝


        return viewpager_for_show_earning_graph;
    }



    //뷰페이져 에서  해당 뷰 사라지게 해줌.
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        //  super.destroyItem(container, position, object);



        container.removeView((View) object);
    }//destroyitem

    @Override
    public int getCount() {

        return 2;// 페이지는 2개   첫번째는  today 두번째는  지난 7일
    }



    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {


        return (view== object);
    }//isViewFromObject 끝



}
