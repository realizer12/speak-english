package com.example.leedonghun.speakenglish;



import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
/**
 * speakenglish
 * Class: MyAuthentication.
 * Created by leedonghun.
 * Created On 2018-12-23.
 * Description:
 */
public class MyAuthentication  extends Authenticator{

    PasswordAuthentication pa;

    public MyAuthentication(){  //생성자를 통해 구글 ID/PW 인증

        String id = "nadadhl12";       // 구글 ID
        String pw = "M7r42907!@#";          // 구글 비밀번호

        // ID와 비밀번호를 입력한다.
        pa = new PasswordAuthentication(id, pw);
    }

    // 시스템에서 사용하는 인증정보
    public PasswordAuthentication getPasswordAuthentication() {
        return pa;
    }



}
