package com.example.leedonghun.speakenglish;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import gun0912.tedbottompicker.TedBottomPicker;
import gun0912.tedbottompicker.TedBottomSheetDialogFragment;
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
 * Class: ActivityForRoomChange.
 * Created by leedonghun.
 * Created On 2020-01-15.
 *
 * Description:  이  엑티비티는  ->  오픈 채팅방의 경우 룸 프로필을  선생님이  지정할수 있는데
 * 선생님이  룸프로필을  새로 지정하고 싶을때  들어오게 되는 엑티비티이다.
 * 프로필로 사용할 이미지를  지정할수 있으며,  이미지 지정 시-> opencv를 이용해서 -> 필터링 효과를 줄수 있다.
 * 이미지  선택은 tedbottompicker 라이브러리를  이용하여  선택한다.
 *
 *
 */
public class ActivityForRoomImageChange extends AppCompatActivity {




   private Button btnfor_load_roomimage;// 수정할 이미지를 로드 하기위한 버튼이다. -> 누르면  tedbottompicker실행됨.  1-1
   private ImageView imgview_for_get_room_profleimg;// 수정 할 이미지가  올려질   이미지뷰이다. -> 1-2


   private TextView textView_for_ment_for_imgfiltering;//이미지 필터링 주라는 멘트 담긴  textview;  1-3
   private LinearLayout container_for_filtering_function;//필터링 기능들 전부 담겨있는  linearlayout; 1-4

   private ImageView imgview_for_original;//필터링  목록중에  original 이미지 2-1
   private ImageView imgview_for_Edge;//필터링 목록중에 Edge효과  2-2
   private ImageView imgview_for_Cartoon;//필터링 목록중에 Cartoon효과 2-3
   private ImageView imageView_for_Gray;//필터링 목록중에 Gray 효과 2-4
   private ImageView imageView_for_Horror;//필터링 목록중에 Horror효과 2-5


   private ImageView checkbutton;//필터링  관련    2-6

   private  ImageView finishbtn;//현재 엑티비티  finish해주는  버튼 3-1


    String roomnumber;

    static {

        System.loadLibrary("Image-filtering");//이미지 필터링 코드 있는 native 라이브러리  로드함.

    }


      //native 코드 끝,
      public  native  void  edge(long inputimage,long outputimage, int th1,int th2);// 이미지 필터 edge 효과
      public  native  void  cartoon(long inputimage,long outputimage, int th1,int th2);//이미지 필터 카툰 효과
      public  native  void  gray(long inputimage,long outputimage, int th1,int th2);//이미지 필터 gray 효과
      public  native  void  horror(long inputimage,long outputimage, int th1,int th2);//이미지 필터 horror 효과



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_room_image_change);
        Log.v("check", getLocalClassName()+"의  onCreate() 실행됨");


        btnfor_load_roomimage=findViewById(R.id.load_image_btn_for_room_profilefilter);//1-1
        imgview_for_get_room_profleimg=findViewById(R.id.imageview_for_room_profileimg_filtering);//1-2
        textView_for_ment_for_imgfiltering=findViewById(R.id.txt_ment_for_roomimage_filtering);//1-3
        container_for_filtering_function=findViewById(R.id.filter_for_roomimg);//1-4

        imgview_for_original=findViewById(R.id.originalimage_for_filter);//2-1
        imgview_for_Edge=findViewById(R.id.roomimg1_for_filtering);//2-2
        imgview_for_Cartoon=findViewById(R.id.roomimg2_for_filtering);//2-3
        imageView_for_Gray=findViewById(R.id.roomimg3_for_filtering);//2-4
        imageView_for_Horror=findViewById(R.id.roomimg4_for_filtering);//2-5

        checkbutton=findViewById(R.id.check_filtering_btn);//2-6

        finishbtn=findViewById(R.id.finish_filtering_activity_btn);//3-1


        //룸 이미지 비트맵 intent로 보낸거 받음.
        Intent intent_profile_room_img=getIntent();
        byte[] byteArray_for_roomprofileimg=intent_profile_room_img.getByteArrayExtra("profileroom_bitmap_byte");
        roomnumber=intent_profile_room_img.getStringExtra("roomnumber");


        //기존  룸 프로필 이미지뷰에서 가지고온  bytearray가  null 이 아닐경우이다.
        //즉,  기존에  룸  프로필 이미지가  있는 경우이다.
        if(byteArray_for_roomprofileimg !=null){

            Log.v("check",getLocalClassName()+"의 기존 프로필  이미지가 있음 ");

            //기존  룸 프로필 이미지가 있으므로, 해당  수정할  이미지뷰에 먼저 기존 이미지를 올려준다.
            Bitmap roomimage = BitmapFactory.decodeByteArray(byteArray_for_roomprofileimg, 0, byteArray_for_roomprofileimg.length);//해당 바이트 비트맵으로 연결

            imgview_for_get_room_profleimg.setImageBitmap(roomimage);
            imgview_for_get_room_profleimg.setBackground(null);



            //수정할 이미지뷰 에  이미지가 올라가져있으므로, 필터링  관련  뷰들이 보이게 한다.
            container_for_filtering_function.setVisibility(View.VISIBLE);
            textView_for_ment_for_imgfiltering.setVisibility(View.VISIBLE);


            //기존  프로필 이미지에 대해  type을 정하는  메소드이다
            try {
                set_filter_image(1,byteArray_for_roomprofileimg,imgview_for_original, imgview_for_Edge, imgview_for_Cartoon, imageView_for_Gray, imageView_for_Horror,null,imgview_for_get_room_profleimg );
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else{//기존 룸 프로필 이  없는 경우이다. null값이  경우.

            Log.v("check",getLocalClassName()+"의 기존 프로필  이미지가 없음");


         //룸 프로필이 없으므로 수정 이미지가 올라가는 뷰에 아직  올라갈  이미지  데이터가 없음.
         //그래서 -> 필터링과  관련  텍스트 멘트를  invisible로 설정해준다.
         container_for_filtering_function.setVisibility(View.INVISIBLE);
         textView_for_ment_for_imgfiltering.setVisibility(View.INVISIBLE);


        }//기존 룸 프로필 이 없는 경우,




        //이미지   로드  버튼 -> tedbottompicker호출
        btnfor_load_roomimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("check", getLocalClassName()+"의  이미지 로드 버튼  클릭됨 -> tedbtoompicker진행함.");


                //tedbottompicker 라이브러리 이용해서 -> 가지고옴.
                TedBottomPicker.with(ActivityForRoomImageChange.this)
                        .setTitle("Select Room profile img !")
                        .showTitle(true)//타이틀 보임
                        .setPeekHeight(1200)//높이 지정
                        .setSelectMinCountErrorText("You should picke at least one!")
                        .show(new TedBottomSheetDialogFragment.OnImageSelectedListener() {
                            @Override
                            public void onImageSelected(Uri uri) {
                                Log.v("check", getLocalClassName()+"의 tedbottompicker로  선택된  룸 프로필 이미지 url->"+uri);

                                //bottompicker로 가지고온  이미지 uri  비트맵으로  전환 시킴.
                                try {
                                    Bitmap new_room_profile_img=MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                                    //가지고온 이미지 비트맵 한거 -> imgview_for_get_room_profleimg에다가  넣어줌.
                                    imgview_for_get_room_profleimg.setImageBitmap(new_room_profile_img);

                                    //이미지가 선택된 상태이므로, background이미지  null 로 만들어줌.
                                    imgview_for_get_room_profleimg.setBackground(null);


                                    //가지고온 이미지 비트맵 ->  사용해서 -   필터링 효과 줌.
                                    set_filter_image(0,null,imgview_for_original, imgview_for_Edge, imgview_for_Cartoon, imageView_for_Gray, imageView_for_Horror,uri,imgview_for_get_room_profleimg );



                                    //필터링  목록들 담고있는 ->  리니어 레이아웃  비져블  상태  0이면  visible상태이다
                                    // 그상태가 아니면  visible로 바꿔준다. >수정될  이미지  선택했기 때문에
                                    int container_visible=container_for_filtering_function.getVisibility();
                                    int texview_imgfilterment_visible=textView_for_ment_for_imgfiltering.getVisibility();

                                    if(container_visible !=0 && texview_imgfilterment_visible != 0){//visible상태가 아니라면,

                                        //수정할 이미지뷰 에  이미지가 올라가져있으므로, 필터링  관련  뷰들이 보이게 한다.
                                        container_for_filtering_function.setVisibility(View.VISIBLE);
                                        textView_for_ment_for_imgfiltering.setVisibility(View.VISIBLE);

                                    }





                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                            }//onImageSelected() 끝
                        });//TedBottomPicker 끝



            }
        });


        //현재  필터링  수정  완료  버튼  2-6
        checkbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //프로필 이미지뷰에 이미지가 올라가있다면 ->  비트맵 처리해서  필터링  엑티비티에  넘겨준다.
                if(hasImage(imgview_for_get_room_profleimg)){
                    Log.v("check", getLocalClassName()+"의 수정완료버튼 눌림 -> 수정된 이미지 있음");

                    Bitmap bitmap_from_roomprofileimg = ((BitmapDrawable) imgview_for_get_room_profleimg.getDrawable()).getBitmap();

                    uploade_new_room_profile_img(bitmap_from_roomprofileimg,roomnumber);

                }else{//프로필 수정 이미지 안올라가져 있는 경우

                    Log.v("check", getLocalClassName()+"의 수정완료버튼 눌림 -> 수정된 이미지 없음");

                    new Toastcustomer(ActivityForRoomImageChange.this).showcustomtaost(null, "No image Checked !");


                }


                //finish();//finish() 함수  호출됨.

            }//onClick() 끝

        });//2-6 클릭 이벤트 끝


        //현재 엑티비티 종료 위한 FINISH() 메소드  호출됨  3-1
         finishbtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Log.v("check", getLocalClassName()+"의  필터링 x 버튼  눌림");


                 finish(); //finish() 함수  호출됨.
             }
         });//3-1 클릭 이벤트 끝.


    }//onCreate()끝


    //서버에  새로 수정된 룸 프로필 이미지 올려주는 메소드
    private void uploade_new_room_profile_img(Bitmap bitmap,String roomnumber){

        Log.v("check", getLocalClassName()+"의  uploade_new_room_profile_img 메소드 실행");


        //비트맵을  파일 로   변환 해줌.
        //수정된  프로필이미지는 비트맵 상태이므로  파일 성질로 바꿔서  서버에 업로드 한다.
        File filesDir = ActivityForRoomImageChange.this.getFilesDir();
        File imageFile = new File(filesDir, System.currentTimeMillis() + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
        }




        Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create())//json으로 받고 싶으면 쓰면안됨..
                .build();//리트로핏 뷸딩
        ApiService apiService = retrofit.create(ApiService.class);//api인터페이스 크리에이트
        RequestBody room_profile_img_File = RequestBody.create(MediaType.parse("image/jpeg"), imageFile);//룸 프로필 이미지 파일 ;
        RequestBody roomnumber_for_profileimg= RequestBody.create(MediaType.parse("plain/text") ,roomnumber);

        //룸 프로필 이미지  업로드 객체
        Call<ResponseBody>upload_room_profile_image=apiService.upload_room_profile_image(room_profile_img_File,roomnumber_for_profileimg);

        //룸 프로필 이미지  업로드  call 객체
        upload_room_profile_image.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    String result_for_upload_profile_img=response.body().string();//이미지 업로드 결과
                    Log.v("check", getLocalClassName()+"의  uploade_new_room_profile_img 메소드 실행 서버 결과 값 -> "+result_for_upload_profile_img);


                    if(result_for_upload_profile_img.equals("1")){//새로운  프로필이미지  서버에 업로드 성공했을 경우이다.


                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream);
                        //quality 부분에서 100에 놓으니  사진이  1mb를 넘길경우가 있고  1메가 바이트를 넘겼을때
                        //인텐트로 바이트 값을 보낼수 가 없는 현상이 생겻다.
                        //그래서  quality를 30으로  낮췄다.
                        byte[] byteArray_for_roomprofileimg = stream.toByteArray();


                        //startactivityforresult의   result값으로  수정되 이미지 bytearray 보내줌.
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("filtered_image",byteArray_for_roomprofileimg);
                        setResult(Activity.RESULT_OK,returnIntent);

                        finish();


                    }else if(result_for_upload_profile_img.equals("2")){//이미지 업로드 실패시


                        new Toastcustomer(ActivityForRoomImageChange.this).showcustomtaost(null, "upload image failed!");

                    }else if(result_for_upload_profile_img.equals("3")){//이미지  서버업로드 성공했지만 경로  , 디비에  업데이트 실ㅐ

                        new Toastcustomer(ActivityForRoomImageChange.this).showcustomtaost(null, "upload image failed!");

                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }


            }//onResponse() 끝

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Log.v("check", getLocalClassName()+"의  uploade_new_room_profile_img 메소드 실행  에러남 -> "+t);

            }//onFailure() 끝

        });




    }//uploade_new_room_profile_img() 끝




    //이미지  가지고 있는지 여부  판단.
    private boolean hasImage(@NonNull ImageView view) {
        //뷰의   drawable 을  가지고옴,
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);// drawable이 null이면  -> false 아니면  true

        if (hasImage && (drawable instanceof BitmapDrawable)) {//true( null이고) drwable이  비트맵일때-> 이러면  -placeholder는  안쳐줌.
            hasImage = ((BitmapDrawable)drawable).getBitmap() != null;//bitmap인  drawable만 가지고  판단  ->  값 return함.
        }

        return hasImage;
    }



    //필터링  목록에도   ->해당 이미지가 들어가서 ->   미리 필터링 된 상태로  원 모양으로  보이는 것을 볼수있음
    // 그부분에 대한  메소드+  여기서  해당  필터 이미지 눌렸을때  수정하되는 이미지 필터링 되는 클릭이벤트 도 진행된다.
    private void set_filter_image(int type,byte[] a,ImageView img1,ImageView img2,ImageView img3, ImageView img4, ImageView img5,Uri uri,ImageView imageView) throws IOException {

        if(type==1){



            detectEdge1(BitmapFactory.decodeByteArray(a, 0, a.length), img1, 1, 0);// 필터링 -> 첫번쨰 이미지 -original
            detectEdge1(BitmapFactory.decodeByteArray(a, 0, a.length), img2, 2, 0);// 필터링 -> 두번째 이미지 -edge
            detectEdge1(BitmapFactory.decodeByteArray(a, 0, a.length), img3, 3, 0);// 필터링 -> 세번쨰 이미지 -cartoon
            detectEdge1(BitmapFactory.decodeByteArray(a, 0, a.length), img4, 4, 0);// 필터링 -> 네번쨰 이미지 -Gray
            detectEdge1(BitmapFactory.decodeByteArray(a, 0, a.length), img5, 5, 0);// 필터링 -> 마지막 이미지 -Horror




            //original 이미지 클릭이벤트
            img1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.v("check", String.valueOf(BitmapFactory.decodeByteArray(a, 0, a.length)));
                    detectEdge1(BitmapFactory.decodeByteArray(a, 0, a.length), imageView, 1, 3);// 필터링 -> 첫번쨰 이미지 -original

                }
            });



            //edge -이미지 클릭이벤트
            img2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.v("check", "dsfsdfsdfsdfsdf");
                    detectEdge1(BitmapFactory.decodeByteArray(a, 0, a.length), imageView, 2, 3);// 필터링 -> 첫번쨰 이미지 -original
                }
            });


            //cartton 이미지 클릭이벤트
            img3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.v("check", "dsfsdfsdfsdfsdf");
                    detectEdge1(BitmapFactory.decodeByteArray(a, 0, a.length), imageView, 3, 3);// 필터링 -> 첫번쨰 이미지 -original
                }
            });
            //Gray 이미지  클리이벤트
            img4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.v("check", "dsfsdfsdfsdfsdf");
                    detectEdge1(BitmapFactory.decodeByteArray(a, 0, a.length), imageView, 4, 3);// 필터링 -> 첫번쨰 이미지 -original
                }
            });


            //horror 이미지 클릭이벤트
            img5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.v("check", "dsfsdfsdfsdfsdf");
                    detectEdge1(BitmapFactory.decodeByteArray(a, 0, a.length), imageView, 5, 3);// 필터링 -> 첫번쨰 이미지 -original

                }//onClick()
            });


        }if(type==0){

            //아래  5개 필터는 ->  필터링 버튼의 대한  코드임.
            detectEdge(uri, img1, 1, 0);// 필터링 -> 첫번쨰 이미지 -original
            detectEdge(uri, img2, 2, 0);// 필터링 -> 두번째 이미지 -edge
            detectEdge(uri, img3, 3, 0);// 필터링 -> 세번쨰 이미지 -cartoon
            detectEdge(uri, img4, 4, 0);// 필터링 -> 네번쨰 이미지 -Gray
            detectEdge(uri, img5, 5, 0);// 필터링 -> 마지막 이미지 -Horror


            //original 이미지 클릭이벤트
            img1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {

                        detectEdge(uri, imageView, 1,3);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });


        }if(type==0){

            //original 이미지 클릭이벤트
            img1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {

                        detectEdge(uri, imageView, 1,3);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });



            //edge -이미지 클릭이벤트
            img2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        detectEdge(uri, imageView, 2,3);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });


            //cartton 이미지 클릭이벤트
            img3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        detectEdge(uri, imageView, 3,3);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            //Gray 이미지  클리이벤트
            img4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        detectEdge(uri, imageView, 4,3);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });


            //horror 이미지 클릭이벤트
            img5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        detectEdge(uri ,imageView, 5,3);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }//onClick()
            });



        }


    }//set_filter_image 끝

    //위   native  메소드 를  사용해서
    //매개변수로 받은 이미지뷰에  필터 효과를 주는 메소드이다.
    public void detectEdge1(Bitmap mBitmap1,ImageView imageView, int concept,int c)  {



        //위 이미지를  라운드 형태로 만들어준다.
        RoundedBitmapDrawable bitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(),mBitmap1);
        bitmapDrawable.setCornerRadius(Math.max(mBitmap1.getWidth(), mBitmap1.getHeight()) / 3.0f);
        bitmapDrawable.setAntiAlias(true);


        switch (concept){// concep  1-> 오리지널  2-> edge  3-> cartoon  4->gray   5->horror

            //오리지널 인때
            case 1:

                if(c==0){


                    imageView.setImageDrawable(bitmapDrawable);

                }else if(c==3){

                    imageView.setImageBitmap(mBitmap1);

                }

                break;

            //edge 일때
            case 2:


                Mat src =new Mat();
                Utils.bitmapToMat(mBitmap1,src );
                Mat edge12=new Mat();

                edge(src.getNativeObjAddr(), edge12.getNativeObjAddr(),50 , 50);

                Utils.matToBitmap(edge12, mBitmap1);

                if(c==0){
                    imageView.setImageDrawable(bitmapDrawable);
                }else if(c==3){

                    imageView.setImageBitmap(mBitmap1);

                }

                break;


            //cartoon일때
            case  3:

                Mat src1 =new Mat();
                Utils.bitmapToMat(mBitmap1,src1 );
                Mat edge1=new Mat();

                cartoon(src1.getNativeObjAddr(), edge1.getNativeObjAddr(),50 , 50);

                Utils.matToBitmap(edge1, mBitmap1);

                if(c==0){
                    imageView.setImageDrawable(bitmapDrawable);
                }else if(c==3){

                    imageView.setImageBitmap(mBitmap1);

                }


                break;


            //gray일떄.
            case 4:

                Mat src2 =new Mat();
                Utils.bitmapToMat(mBitmap1,src2 );
                Mat edge2=new Mat();

                gray(src2.getNativeObjAddr(), edge2.getNativeObjAddr(),50 , 50);

                Utils.matToBitmap(edge2, mBitmap1);

                if(c==0){

                    imageView.setImageDrawable(bitmapDrawable);
                }else if(c==3){

                    imageView.setImageBitmap(mBitmap1);

                }
                break;

            //horror일때
            case 5:

                Mat src5 =new Mat();
                Utils.bitmapToMat(mBitmap1,src5 );
                Mat edge5=new Mat();

                horror(src5.getNativeObjAddr(), edge5.getNativeObjAddr(),50 , 50);

                Utils.matToBitmap(edge5, mBitmap1);

                if(c==0){

                    imageView.setImageDrawable(bitmapDrawable);

                }else if(c==3){

                    imageView.setImageBitmap(mBitmap1);

                }

                break;

        }//switch문 끝

    }//detectEdge() 끝



    //위   native  메소드 를  사용해서
    //매개변수로 받은 이미지뷰에  필터 효과를 주는 메소드이다.
    public void detectEdge(Uri uri,ImageView imageView, int concept,int c) throws IOException {

        //c 매개변수 의 경우 ->  수정되는 이미지 인지,   필터링에서 보여주는 이미지인지를  나눠주는  변수이다.
        //c가  0일 경우->  필터링 용 이미지여서 -> 라운드 형태 적용
        //c가  3일 경우->  수정되는  이미지여서 -> 이미지  뷰 그대로 적용.



        Bitmap   mBitmap1 = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);//uri를  ->  bitmap으로 바꿔줌.



        //위 이미지를  라운드 형태로 만들어준다.
        RoundedBitmapDrawable bitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(),mBitmap1);
        bitmapDrawable.setCornerRadius(Math.max(mBitmap1.getWidth(), mBitmap1.getHeight()) / 3.0f);
        bitmapDrawable.setAntiAlias(true);


        switch (concept){// concep  1-> 오리지널  2-> edge  3-> cartoon  4->gray   5->horror

            //오리지널 인때
            case 1:

                if(c==0){
                    imageView.setImageDrawable(bitmapDrawable);
                }else if(c==3){

                    imageView.setImageBitmap(mBitmap1);

                }

                break;

            //edge 일때
            case 2:


                Mat src =new Mat();
                Utils.bitmapToMat(mBitmap1,src );
                Mat edge12=new Mat();

                edge(src.getNativeObjAddr(), edge12.getNativeObjAddr(),50 , 50);

                Utils.matToBitmap(edge12, mBitmap1);

                if(c==0){
                    imageView.setImageDrawable(bitmapDrawable);
                }else if(c==3){

                    imageView.setImageBitmap(mBitmap1);

                }

                break;


            //cartoon일때
            case  3:

                Mat src1 =new Mat();
                Utils.bitmapToMat(mBitmap1,src1 );
                Mat edge1=new Mat();

                cartoon(src1.getNativeObjAddr(), edge1.getNativeObjAddr(),50 , 50);

                Utils.matToBitmap(edge1, mBitmap1);

                if(c==0){
                    imageView.setImageDrawable(bitmapDrawable);
                }else if(c==3){

                    imageView.setImageBitmap(mBitmap1);

                }


              break;


            //gray일떄.
            case 4:

                Mat src2 =new Mat();
                Utils.bitmapToMat(mBitmap1,src2 );
                Mat edge2=new Mat();

                gray(src2.getNativeObjAddr(), edge2.getNativeObjAddr(),50 , 50);

                Utils.matToBitmap(edge2, mBitmap1);

                if(c==0){

                    imageView.setImageDrawable(bitmapDrawable);
                }else if(c==3){

                    imageView.setImageBitmap(mBitmap1);

                }
                break;

            //horror일때
            case 5:

                Mat src5 =new Mat();
                Utils.bitmapToMat(mBitmap1,src5 );
                Mat edge5=new Mat();

                horror(src5.getNativeObjAddr(), edge5.getNativeObjAddr(),50 , 50);

                Utils.matToBitmap(edge5, mBitmap1);

                if(c==0){

                    imageView.setImageDrawable(bitmapDrawable);

                }else if(c==3){

                    imageView.setImageBitmap(mBitmap1);

                }

             break;

        }//switch문 끝

    }//detectEdge() 끝






}//클래스 끝.
