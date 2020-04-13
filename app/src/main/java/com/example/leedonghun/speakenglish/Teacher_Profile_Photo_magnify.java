package com.example.leedonghun.speakenglish;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * speakenglish
 * Class: Teacher_Profile_Photo_magnify.
 * Created by leedonghun.
 * Created On 2019-09-19.
 * Description:학생 로그인 한다으멩 선생님  프로필 들어갔을떄  프로필 사진을 누르면  해당  사진을 확대해서 보여주는 엑티비티이다.
 * 사진을 핀치 행위로 더 확대도 가능하다.
 */
public class Teacher_Profile_Photo_magnify extends AppCompatActivity {

    //엑티비티 종료 이미지 버튼
    ImageButton finishactivitybtn;//1-1

    //확대된 선생님 프로필 이미지
    ImageView magnifiedprofileimage;//1-2



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_profile_photo_manify);
        Log.v("check", getLocalClassName()+"의 oncreate실행됨.");




        //1-1
        finishactivitybtn=findViewById(R.id.button_for_finish_magnified_profile_photo);

        //1-2
        magnifiedprofileimage= findViewById(R.id.magnified_teacher_profile);

        Intent intent=getIntent();//인텐트 받기
        byte[] arr = intent.getByteArrayExtra("image");
        Bitmap image = BitmapFactory.decodeByteArray(arr, 0, arr.length);//비트맵 바이트 받은거  비트맵화
        magnifiedprofileimage.setImageBitmap(image);//이미지 넣어줌,


        //import uk.co.senab.photoview.PhotoViewAttacher;-> 라이브러리 이미지 뷰 핀치 확대 줌  해주는거
        PhotoViewAttacher photoAttacher;
        photoAttacher= new PhotoViewAttacher(magnifiedprofileimage);
        photoAttacher.update();



        //엑티비티 종료 버튼 눌렸을떄,
        finishactivitybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();//엑티비티 종료
            }
        });
        //엑티비티 종료버튼 리스너 끝.


    }//oncreate 끝

}//엑티비티 끝.
