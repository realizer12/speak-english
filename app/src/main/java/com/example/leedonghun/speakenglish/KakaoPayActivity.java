package com.example.leedonghun.speakenglish;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * speakenglish
 * Class: KakaoPayActivity.
 * Created by leedonghun.
 * Created On 2020-03-20.
 * Description: 카카오 페이가 진행되는  엑티비티이다.
 */
public class KakaoPayActivity extends AppCompatActivity {


    private WebView webView_for_show_kakao;// 카카오 페이가  진행되는 웹뷰  1-1
    private String tid;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kakopay_webview_activity);

        int order_id= (int) System.currentTimeMillis();
        GlobalApplication globalApplication=(GlobalApplication)getApplicationContext();//학생 uid가지고 옴.
        webView_for_show_kakao=findViewById(R.id.webview_for_show_kakao_pay);//1-1
        webView_for_show_kakao.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView_for_show_kakao.getSettings().setJavaScriptEnabled(true);//자바  스크립트  가능하게 해줌.


        Intent intent=getIntent();//SelectChargingPointAmountActivity에서 보낸  값  받기 위한 intent

        int point_quantity=intent.getIntExtra("quantity", -1);//구매할 포인트 지정하는 엑티비티에서 지정한 10p 개수.


        webView_for_show_kakao.setWebViewClient(new WebViewClient() {


            public boolean shouldOverrideUrlLoading(WebView view, String url){

                Log.v("CHECKASDSAD", "확인 URL+"+url);

                if (url.startsWith("intent://")) {// url을  받아올때,  app_redirect_url 이  intent를  석어서 가지고옴.  ->  앱의 경우
                    try {
                        Context context = view.getContext();
                        Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);

                        if (intent != null) {
                            view.stopLoading();

                            PackageManager packageManager = context.getPackageManager();
                            ResolveInfo info = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
                            if (info != null) {

                                context.startActivity(intent);
                            } else {

                                view.loadUrl("https://www.naver.com/");

                            }

                            return true;
                        }
                    } catch (URISyntaxException e) {

                    }

                }else{

                    CookieManager.getInstance().setCookie(url, "tid="+tid);
                    CookieManager.getInstance().setCookie(url, "user_uid="+globalApplication.getStudnet_uid());
                    CookieManager.getInstance().setCookie(url, "order_id="+order_id);
                }

                return false;

            }

            @Override
            public void onPageFinished(WebView view, String url){

                 //해당 웹뷰의  쿠키를 가지고온다.
                 String cookie = CookieManager.getInstance().getCookie(url);
                Log.v("check", getLocalClassName()+"의 확인 URL->"+url+" 확인 cookie"+cookie);

                     //쿠키  가  null 이 아닐때 즉  이경우는 kakopay_approval이  진행됬을때이다.  나머지의 url의 경우에는  쿠키가 존재하지 않는다.
                    if(cookie != null && getCookie(url, "success_or_not")!=null) {

                        //결제 성공 실패 결과

                          int success_or_not = Integer.parseInt(getCookie(url, "success_or_not"));
                          if(success_or_not == 1){//결제  성공 결과 관련해서 쿠키 1

                              new Toastcustomer(KakaoPayActivity.this).showcustomtaost(null, "결제  성공하였습니다!");
                              CookieManager.getInstance().removeAllCookie();//웹 뷰에  남아있는 쿠키들을  모두 지워준다.


                              //학생 디비에  포인트  값  수정하거나  새로 추가 시켜준다.
                              charging_student_point_db_value(globalApplication.getStudnet_uid(),point_quantity);

                              finish();



                          }else {//결제  성공 결과 관련해서 쿠키 0

                              new Toastcustomer(KakaoPayActivity.this).showcustomtaost(null, "결제 실패 하였습니다..");
                              CookieManager.getInstance().removeAllCookie();//웹 뷰에  남아있는 쿠키들을  모두 지워준다.


                              finish();

                          }

                    }//쿠키 값이 null 이 아닐때 -> approval_url 진행시
                      else if(cookie != null && getCookie(url, "success_or_not")==null) {
                        new Toastcustomer(KakaoPayActivity.this).showcustomtaost(null, "결제 실패 하였습니다..");
                        CookieManager.getInstance().removeAllCookie();//웹 뷰에  남아있는 쿠키들을  모두 지워준다.

                        finish();

                    }

            }//onPageFinished-  페이지가  다 로드 되면  실행됨.
        });




        //ready 끝나고 받은  값 중에서 app_redirect_url을  실행시켜  해당  결제가 진행되는  url 을  webview에  띄어준다.
        //이때  결제가 끝나고,  필요힌 tid(주문번호)를   같이 받아온다.
         get_app_redirect_url(globalApplication.getStudnet_uid(),point_quantity,webView_for_show_kakao,order_id);


    }//onCreate() 끝


    //선생님  디비 포인트 값   충전시켜준다
    private void charging_student_point_db_value(String std_uid,int point_amount){

        Log.v("check", getLocalClassName()+"의 charging_student_point_db_value() 실행됨");

        //retrofit 통신 ..
        Retrofit retrofit_for_charging_std_point=new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create()).build();
        ApiService apiService=retrofit_for_charging_std_point.create(ApiService.class);

        //서버로  fcm토큰 보냄
        Call<ResponseBody> charging_std_point=apiService.update_std_point_amount(std_uid,point_amount);

        charging_std_point.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();

                    Log.v("check", getLocalClassName()+"의 charging_student_point_db_value() response 값->"+result);


                    if(result.equals("1")){
                        Log.v("check", getLocalClassName()+"의 charging_student_point_db_value response 값 1은  새롭게  학생 포인트  db insert 성공함 ");
                        //이땐 그냥 넘어가자

                    }else if(result.equals("2")){
                        Log.v("check", getLocalClassName()+"의 charging_student_point_db_value response 값 2는 포인트 insert도중  에러남 ");
                        new Toastcustomer(KakaoPayActivity.this).showcustomtaost(null , "포인트  업데이트 안됨 error 2");

                    }else if(result.equals("3")){
                        Log.v("check", getLocalClassName()+"의 charging_student_point_db_value response 값 3은 학생 포인트 기록 업데이트 성공");
                        //이때도 그냥 넘어가자


                    }else if(result.equals("4")){
                        Log.v("check", getLocalClassName()+"의 charging_student_point_db_value response 값 1은  새롭게  학생 포인트  db insert 실패함 ");
                        new Toastcustomer(KakaoPayActivity.this).showcustomtaost(null , "포인트  업데이트 안됨 error 4");

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }//onResponse() 끝

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.v("check", getLocalClassName()+"의 charging_student_point_db_value() response 실페 값->"+t.getMessage());



            }//onFailure(()끝
        });

    }//charging_student_point_db_value() 끝


    //가지고 온 쿠키중에  해당  이름의  쿠키 값을 가지고 오는 메소드이다.
    public String getCookie(String siteName,String cookieName){
        String CookieValue = null;//해당  이름의  쿠키값

        ///쿠키를 가져오기위함 쿠키 매니저 인스턴스
        CookieManager cookieManager = CookieManager.getInstance();

        //해당 url에서  받은  쿠키
        String cookies = cookieManager.getCookie(siteName);

        //쿠키들 ; 로  나눠저서  붙어있으므로  ;를 기준으로 split해줌
        String[] temp=cookies.split(";");

        //그다음 하나씩  string 변수 ar1 에 넣어서  해당  찾으려는  쿠키 이름을 포함하는지  확인하낟.
        for (String ar1 : temp ){

            if(ar1.contains(cookieName)){//해당 쿠키 이름을 포함하는 경우 쿠키 값을  return하게  cookie value를 넣어줌.

                String[] temp1=ar1.split("=");
                CookieValue = temp1[1];
                break;
            }
        }
        return CookieValue;//쿠키값 return  맞는게없으면 null일거임.
    }

    //모바일 앱 결제가 진행되는 app_redirect_url을 가지고 온다.
    private  void get_app_redirect_url(String student_uid,int quantity,WebView webView,int orderid){
        Log.v("check", getLocalClassName()+"의 get_app_redirect_url() 실행됨");

        Gson gson = new GsonBuilder().setLenient().create();
        //retrofit 통신 ..
        Retrofit retrofitfor_get_redirect_url=new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create(gson)).build();
        ApiService apiServiceforget_redirect_url=retrofitfor_get_redirect_url.create(ApiService.class);

        //서버로  fcm토큰 보냄
        Call<GetKakaoPayReadyInfo> sendstudentfcmtoken=apiServiceforget_redirect_url.get_kakao_redirect_app_url(student_uid, quantity,orderid);
        sendstudentfcmtoken.enqueue(new Callback<GetKakaoPayReadyInfo>() {
            @Override
            public void onResponse(Call<GetKakaoPayReadyInfo> call, Response<GetKakaoPayReadyInfo> response) {

              String app_redirect_url=response.body().getApp_redirect();// 결제가 진행되는  카카오페이 app_redirect_url
              tid=response.body().getTid();//approval에서 필요한 주문 번호  결제  준비 과정에서  결과로 받아올수 있음.->  서버에서  넘길수 없으므로, 기기에서  쿠키로 적용해서 넘겨준다.


              Log.v("check",getLocalClassName()+"의  app_redirect_url 가져오기 성공 -> "+app_redirect_url+"  tid->"+tid);

                //웹뷰를 통해 가지고온  카카오페이 결제 url을  연결 시켜준다.
                webView.loadUrl(app_redirect_url);

            }//onResponse()끝

            @Override
            public void onFailure(Call<GetKakaoPayReadyInfo> call, Throwable t) {
              Log.v("check",getLocalClassName()+"의  app_redirect_url 가져오기 실패 -> "+t);



            }//onfailure()끝
        });

    }//get_app_redirect_url() 끝

}//KakaoPayActivity클래스 끝
