package com.example.leedonghun.speakenglish;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
//import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
import com.kakao.util.helper.log.Logger;

/**
 * speakenglish
 * Class: CustomDialog.
 * Created by leedonghun.
 * Created On 2019-07-24.
 * Description:
 * 커스톰 다이얼로그  클래스이다.
 * 카카오톡,  구글  로그인 api  사용할때 ->  회원의  영어이름을 물어보고  답을 받아와  처리하도록 만든다.
 * 답을 받는 것은  인터페이스로 사용
 *
 */
public class CustomDialog extends Dialog implements View.OnClickListener {
private GetCustomDailogResult getCustomDailogResult;
Context context;
    //커스텀 다이얼로그의 각 위젯들을 정의한다.
    private EditText englishname;
    private Button okButton;
    private Button cancelButton;


    //인터페이스 초기화를 위한  메소드
    public void setGetCustomDailogResult(GetCustomDailogResult getCustomDailogResult){

        this.getCustomDailogResult=getCustomDailogResult;

    }



    //커스톰 다이얼로그 생성자
    public CustomDialog(@NonNull Context context) {
        super(context);
        this.context=context;

    }


    //커스톰 다이얼로그  oncreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 액티비티의 타이틀바를 숨긴다.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        setContentView(R.layout.custom_dialog_loginapi);



        //커스톰 다이얼로그  크기  조정 ->  여기서는  넓이를  matchparent로  늘림.
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;

        //다이얼로그  다른 배경 누르면 안사라지게 적용
        setCancelable(false);


        //영어이름  에딧텍스트
        englishname = (EditText)findViewById(R.id.editText_for_englishname_loginapi);

        //확인 버튼
        okButton = (Button)findViewById(R.id.btn_for_ok_loginapi);

        //취소버튼
        cancelButton = (Button)findViewById(R.id.btn_for_cancel_loginapi);

        //확인 취소버튼  리스너와  연결
        okButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
    }


    //커스톰 다이얼로그 onclick
    @Override
    public void onClick(View view) {


        switch (view.getId()){

            //ok버튼 눌렀을때.
            case R.id.btn_for_ok_loginapi:

                 getCustomDailogResult.onPositveClicked(englishname.getText().toString());

                 break;


            //취소버튼 눌렀을때
            case R.id.btn_for_cancel_loginapi:

                getCustomDailogResult.onNegativeClicked();

                break;

        }

    }//onclick 끝

}//커스톰 다이얼로그 클래스 끝
