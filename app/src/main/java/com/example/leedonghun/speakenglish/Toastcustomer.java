package com.example.leedonghun.speakenglish;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * speakenglish
 * Class: Toastcustomer.
 * Created by leedonghun.
 * Created On 2019-01-22.
 * Description:
 */
 class Toastcustomer {

      private Context context;//커스텀

      Toastcustomer(Context context){

         this.context=context;
      }


    public void showcustomtaost(EditText editText,String text){

        customtoastcode(editText,text,0,0,0,0);

      }

     //위의 showcustomtaost 를  오버로딩하여,   기존에  커스톰 토스트의  width와  height를  1500,  150으로 고정해놨지만,
     //사용자가 바꾸고 싶을때  width와  height를  바꿀수 있도록  조치함.
     public void showcustomtaost(EditText editText,String text, int width, int height){

        customtoastcode(editText,text,width,height,0,0);

     }

     //shwotcustoma toast를  오버로딩 함. -> 이때  x_offset과  y_offset을   파라미터에  추가 시켜줘서 ->  위치를 조정하도록  지원함.
     public void showcustomtaost(EditText editText,String text, int width, int height,int x_offset,int y_offset){

          customtoastcode(editText,text ,width, height, x_offset,y_offset );

     }


     private void customtoastcode(EditText editText,String text, int width, int height,int x_offset,int y_offset){


          Animation wrongshake= AnimationUtils.loadAnimation(context,R.anim.shakeedittext);//쉐이크하는 애니메이션 선언
          //LayoutInflater inflater = getLayoutInflater();//<- 이경우에는 원래 엑티비티 쪽에서 레이아웃 인플레이터를 선언할때 사용되었던 부분이다.
          //하지만  현재 클래스 처럼  엑티비티가 아닌 부분에서  인플레이터를  부를때는 아래와 같이  context를 사용하여서  부르면 된다.

          LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );//레이아웃 인플래에터를 사용하여
          View toastview = inflater.inflate(R.layout.customtoast,null);//customtoast레이아웃의  뷰를  인플레이트한다.// 이미  다른 xml파일이  setcontentviewfh 묶여있기때문
          //customtaost레이아웃의 텍스트뷰를 커스텀한다-> 이 텍스트뷰가  토스트 메세지 처럼  나오기 때문 앞으로

          TextView tv = (TextView) toastview.findViewById(R.id.toasttext);//가지고온 코스트 뷰

           //위에서  오버로딩을 사용해서 width를  지정해놓을때와 아닐때가 있으므로, 아래와 같이  구분지어 높음.
          if(width==0 && height==0) {

              tv.setWidth(1500);//텍스트뷰의  넓이
              tv.setHeight(150);//텍스트뷰의 높이

          }else{

              tv.setWidth(width);//텍스트뷰의  넓이
              tv.setHeight(height);//텍스트뷰의 높이
          }

          tv.setText(text);//테스트뷰 안에 들어가는 말
          tv.setBackgroundColor(Color.parseColor("#FFFB5252"));//토스트 메세지 뷰의  배경  색을 바꾼다.

          Toast toast = new Toast(context);//토스트 메세지로 아이디 입력을 유도한다.
          toast.setView(toastview);//토스트 메세지의  뷰를  위에서  만들어놓은  toastview와 연결 시킨다.-> 토스트 메세지 뷰를 저걸로 사용한다는 뜻임.
          toast.setGravity(Gravity.CENTER_HORIZONTAL, x_offset, y_offset);//토스트 메세지의 위치를  default위치인  bottom에서  화면 중앙으로 가지고 온다.
          toast.show();

          if(editText!=null) {
              editText.startAnimation(wrongshake);//학생 이메일 부분의  흔들림  애니메이션을 준다.
              editText.setHintTextColor(Color.parseColor("#FFF43434"));// 학생 이메일  힌트 부분의 색깔을  바꿔준다.
          }
      }//customtoastcode()끝


}//ToastCustomer() 끝
