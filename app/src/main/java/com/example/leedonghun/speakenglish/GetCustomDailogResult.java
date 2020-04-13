package com.example.leedonghun.speakenglish;

/**
 * speakenglish
 * Class: GetCustomDailogResult.
 * Created by leedonghun.
 * Created On 2019-07-25.
 * Description:
 *
 * 커스톰 토스트로  로그인 api가입시  사용자 영어이름  받아오기위한 인터페이스
 */
public interface GetCustomDailogResult  {

  //확인 버튼을 눌렀을때  메소드이다.
  void onPositveClicked(String englishname);


  //취소버튼을 눌렀을때  메소듣.
  void onNegativeClicked();




}
