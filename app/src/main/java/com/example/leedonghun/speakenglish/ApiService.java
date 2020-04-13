package com.example.leedonghun.speakenglish;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * speakenglish
 * Class: ApiService.
 * Created by leedonghun.
 * Created On 2019-01-10.
 * Description:
 *
 * RETROFIT 사용시  사용되는 메소드들 정의
 */

public interface ApiService {

    public static final String baseurl = "http://13.209.249.1/";


    //선생님  환전  신청  info를   가지고 옴.
    @FormUrlEncoded
    @POST("get_teacher_exchange_request_info.php")
    Call<GetExchangePointInfo>get_teacher_exchange_request_info(@Field("teacher_uid")String teacher_uid);

    //선생님 포인트 환전 신청  정보를 받아서  디비에  저장시켜준다.
    @FormUrlEncoded
    @POST("upload_teacher_exchange_request_info.php")
    Call<ResponseBody>upload_teacher_exchange_request_info(@Field("teacher_uid")String teacher_uid,@Field("bank_position")int bank_position,@Field("request_point")int request_exchange_point,@Field("bank_account")String bank_account,@Field("time")float timemills);

    //선생님 전체  포인트 를 가지고 온다.
    @FormUrlEncoded
    @POST("get_teacher_entire_point.php")
    Call<ResponseBody>get_teacher_entrie_point(@Field("teacher_uid")String teacher_uid);

    //선생님 전체 포인트 량   업데이트 및   추가 해주기
    //기존에  선생님  포인트는  각  수업 시간 및  그 시간에 얻은  포인트량을 디비에 저장해놓았음
    //전체  선생님 포인트량이  필요하므로  해당  포인트 량을  저장하는 db를 만들어  이전 수업 포인트  데이터들의 포인트량의 총합을  계산해  전체 포인트량을  넣어주거나  업데이트 해주기로 함.
    @FormUrlEncoded
    @POST("make_teacher_entire_point_info.php")
    Call<ResponseBody>make_teacher_entire_point_info(@Field("teacher_uid")String teacheruid);


    //선생님  포인트 정보 가져오기
    @FormUrlEncoded
    @POST("get_teacher_point_info.php")
    Call<GetTeacherPointInfo>get_teacher_point_info(@Field("teacher_uid")String teacher_uid,@Field("today_date")float today_date_mills);

    //수업이 끝나면  학생 포인트 차감 그리고 선생님 포인트 up시켜주는 거  진행한다.
    @FormUrlEncoded
    @POST("change_std_and_teacher_point_amount.php")
    Call<ResponseBody>chagne_std_and_teacher_point_amount(@Field("student_uid")String student_uid,@Field("teacher_uid")String teacher_uid,@Field("finish_time")float class_finish_time);

    //학생 포인트를 조회해서 가지고온다
    @FormUrlEncoded
    @POST("get_student_point_amount.php")
    Call<ResponseBody>get_std_present_point_amount(@Field("student_uid")String student_uid);

    //카카오페이로 결제후 디비에  학생  포인트를  업데이트 해준다.
    @FormUrlEncoded
    @POST("charging_student_point.php")
    Call<ResponseBody>update_std_point_amount(@Field("student_uid")String std_uid,@Field("point_amount")int point_to_charge);

    //kakopay.php 에서 해당  next_redirect_app_url 을  받아온다.
    @FormUrlEncoded
    @POST("kakaopay.php")
    Call<GetKakaoPayReadyInfo>get_kakao_redirect_app_url(@Field("student_uid")String student_uid,@Field("quantity")int quantity,@Field("order_id") int orderid);


    //학생이  예약한 클래스  취소할때  선생님께 알리는 fcm을 보낸다.
    @FormUrlEncoded
    @POST("send_fcm_to_teacher_std_cancel_class.php")
    Call<ResponseBody>send_cancel_reservation_info_to_teacher_fcm(@Field("student_name")String student_name,@Field("student_profile_url")String student_profile,@Field("tuid")String teacher_uid,@Field("cancel_reservation_time_uid")String canceld_reservation_time_uid);


    //어떤 학생이  어떤 선생님의  몇시를 예약했는지 정보를 보내서  해당 선생님에게 정보를  fcm으로 보낸다
    @FormUrlEncoded
    @POST("send_fcm_to_teacher_class_reservation.php")
    Call<ResponseBody>send_reservation_info_to_send_fcm_teacher(@Field("suid")String student_uid,@Field("student_profile_url")String student_profile,@Field("student_name")String student_name,@Field("tuid")String teacher_uid,@Field("reservation_time_uids") JSONArray selected_class_uids);


    //학생이 예약한  수업 리스트  서버에서 가지고옴.
    @FormUrlEncoded
    @POST("get_student_available_class_time.php")
    Call<GetStudentReservedClassTime> get_student_available_class_time(@Field("suid")String student_uid,@Field("tuid")String teacher_uid);


    //선생님이  지정 가능 예약 시간을  길게 누를때, 삭제 또는 수정  내용을 받아서 수정또는 삭제시켜줌
    //edit_or_delete 값이  0일때  수정 ,  1일때 삭제이다.
    @FormUrlEncoded
    @POST("delete_or_edit_available_class_time.php")
    Call<ResponseBody>delete_or_edit_available_class_time(@Field("uid")String data_uid,@Field("editordelete")int edit_or_delete,@Field("updatedtime")float update_datetime,@Field("suid")String studentuid,@Field("selected_class") JSONArray selected_class_uid);


    //선생님이  지정한 예약 가능한 시간 서버에서  가지고옴
    @FormUrlEncoded
    @POST("get_teacher_available_class_time.php")
    Call<GetTeacherAvailableClassTime>get_teacher_available_class_time(@Field("tuid")String teacher_uid);


    //선생님이 지정한  예약 가능한  시간  서버로  저장 시켜줌
    @FormUrlEncoded
    @POST("upload_available_class_time.php")
    Call<ResponseBody>upload_available_class_time(@Field("tuid")String teacheruid,@Field("availabledate")float datetime);

    //선생님 uid로  선생의  이름이랑  프로필 url 받아옴.
    @FormUrlEncoded
    @POST("get_teacher_url_and_name.php")
    Call<GetTeacherUrlandName>get_teacher_url_and_nmae(@Field("tuid")String teacher_uid);

    //학생이 받은  피드백들  서버에서 받아옴.
    @FormUrlEncoded
    @POST("get_student_feedback.php")
    Call<GetStudentFeedback>get_student_feedack(@Field("studentemail")String studentemail);

    //학생 uid로  학생의  이름이랑  프로필 url 받아옴.
    @FormUrlEncoded
    @POST("get_student_url_and_name.php")
    Call<GetStudentUrlandName>get_student_url_and_nmae(@Field("suid")String student_uid);


    //선생님이 받은 피드백들을 서버에서 받아옴.
    @FormUrlEncoded
    @POST("get_teacher_feedback.php")
    Call<GetTeacherFeedback>get_teacher_feedback(@Field("teacheremail")String teacheremail);

    //선생님이 수업후 학생에게 피드백을 주면  피드백 서버에  저장 시켜줌.
    @FormUrlEncoded
    @POST("teach_feedback.php")
    Call<ResponseBody>upload_teacher_feedbackto_s(@Field("suid")String suid,@Field("tuid")String tuid, @Field("content") String feedback_content);

    //학생이  수업후  선생님 피드백을 주면  피드백 서버에 저장 시켜준다.
    @FormUrlEncoded
    @POST("std_feedback.php")
    Call<ResponseBody>upload_std_feedbackto_t(@Field("suid")String suid,@Field("tuid")String tuid,@Field("rating")float rating,@Field("content")String feedback_content);


    //해당 참여방  참여자들 리스트를  서버에서 받아온다.
    @FormUrlEncoded
    @POST("get_room_joined_users_list.php")
    Call<GetUserList>get_room_joined_users_list(@Field("roomnumber")String roomnumber);


    //오픈 채팅방  룸 프로필 이미지를  선생님이  업로드 할떄
    //사용하는 프로필  이미지를 서버로 업로드 한다.
    @Multipart
    @POST("upload_room_profile_image.php")
    Call<ResponseBody>upload_room_profile_image(@Part("image\"; filename=\"myfile.jpg\" ") RequestBody room_profile_image,@Part("roomnumber")RequestBody roomnumber);


    //선생님 채팅방을 시작할때 오픈 채팅방에 경우
    //해당방의  프로필 이미지가  필요하므로,  서버에서 저장된  프로필 이미지를 가지고온다.
    @Multipart
    @POST("get_room_profile_image.php")
    Call<ResponseBody>get_room_profile_image(@Part("roomnumber")RequestBody rooomnumber);


    //채팅에서 선생님 프로필 누른다음  나온 선생님 프로필 정보에서 -> 프로필 홈 버튼 눌렀을때 필요한
    //선생님 정보를 가지고 오기위한 call
    //매게  변수는  선생님 uid를  사용한다.
    @Multipart
    @POST("getteacher_profile_info_from_chatting.php")
    Call<ResponseBody>getteacher_profileinfo(@Part("teacheruid")RequestBody teacheruid);


    //선생님 uid로 onoff라인  결과를 가지고오기위한  call 객체.
    @Multipart
    @POST("get_teacher_loginonoff.php")
    Call<ResponseBody>getteacherlogin_status(@Part("teacheruid") RequestBody teacheruid );


    //채팅 시  서버에 업로드할  비디오를  받는 call 객체이다.
    @Multipart
    @POST("uplaode_video_chatting_files.php")
    Call<ResponseBody>uploadeVideo(@Part MultipartBody.Part video);

    ///서버에  업로드할  이미지를 받는   call 객체이다.
    @Multipart
    @POST("upload_image_chatting_files.php")
    Call<GetChattingImagePath> uploadImages(@Part("image\"; filename=\"chatting.jpg\" ") RequestBody file,@Part("image1\"; filename=\"chatting.jpg\" ") RequestBody file1,@Part("image2\"; filename=\"chatting.jpg\" ") RequestBody file2);
   // Call<ResponseBody> uploadImages(@Part("image\"; filename=\"chatting.jpg\" ") ArrayList<RequestBody> files);


    //서버 디비 savechattingdata 테이블에서  해당  채팅방  sqlite 값  reaornot이  1인 채팅 데이터의  unreadcount를  1 낮춰준다.
    @Multipart
    @POST("update_unread_user_count.php")
    Call<ResponseBody> update_unread_count_for_chatting_message(@Part("chatting_order_array")RequestBody chatting_data_readornot_list);

    //채팅 메세지에서 읽지 않은  인원수를  서버로부터 가지고 온다.
    @Multipart
    @POST("select_unread_user_count.php")
    Call<ResponseBody> get_unread_count_for_chatting_message (@Part("roomnumber")RequestBody roomnumber,@Part("chatorder")RequestBody chatorder);


    //학생이 해당 채팅방을 나갈때, -> 서버 디비 savechattingdata에  저장된  모든  데이터들을  삭제 시켜준다.
    @Multipart
    @POST("delete_whole_one_messnger_data_in_server.php")
    Call<ResponseBody>delete_whole_onetoone_messenger(@Part("roomnumber")RequestBody roomnumber);



    //1대1 메신저에  조인된  사람의 숫자를  검색해서 ->  2명이 아니라면,  선생님 쪽에서 해당 1대1 채팅방에 못들어가도록  학생이 채팅 거부함을  알린다.
    @Multipart
    @POST("check_onemessenger_user_count.php")
    Call<ResponseBody>get_user_joined_count_in_onemessenger(@Part("roomnumber")RequestBody roomnumber);


    //채팅방리스트 리사이클러뷰에 뿌려질때 각 방별  info를  room db에서 가져오기위한  call객체
    @FormUrlEncoded
    @POST("getchattingroom_info.php")
    Call<GetRoomInfoForChattingRoomList> get_user_joined_room_info(@Field("roomnumber")String roomnumber,@Field("roomnamespace")String roomnamespace,@Field("useruid")String useruid,@Field("userposition")String userposition);


    //해당 유저가 참여한  방의  리스트를 가지고 오기 위한  Call객체
    //파라미터로는 서버에서 참여한 방을  식별하기위한  해당 유저의 uid를 받는다.
    @FormUrlEncoded
    @POST("getuserjoinedroomlist.php")
    Call<GetRoomList>get_user_joined_room_list(@Field("useruid") String useruid);



    //오픈 채팅방 입장시 -> 해당방에  기존에 연결 기록이 있는지를 체크하여,  없다면
    //첫 방문으로 간주해  해당  방의  카운트를  1올려준다.
    //파라미터로  해당  유저 uid  와  해당방 번호를 받는다.
    @Multipart
    @POST("checkchatuserconinfo.php")
    Call<ResponseBody>check_chatuser_con_info(@Part("useruid")RequestBody useruid,@Part("roomnumber")RequestBody roomnumber);


    //학생이  채팅방을 나갈때 -> 해당 채팅방 기록 지워주기
    //파라미터로  학생 이메일이랑  방  번호를 가져가서 서버에서 식별 한다.
    @Multipart
    @POST("deletechattingroomclientinfo.php")
    Call<ResponseBody> deleteclientinfoinchattingroom(@Part("studentuid")RequestBody studentuid,@Part("roomnumber")RequestBody roomnumber,@Part("chattingtype")RequestBody chattingtype);

    //채팅방  정보를  가져온다.
    //파라미터로  기기에서  해당 방  룸   번호를  받아서  서버에 보내준다.
    @FormUrlEncoded
    @POST("getroominfo.php")
    Call<GetRoomInfoClass> getchattingroominfo(@Field("roomnumber")String roomnumber);


    //채팅방에 입장했을때,  해당 방의   유저가  처음입장인지 아닌지 여부를 알아낸뒤,
    //선생님 해당 채팅방 정보를 가지고 온다.
    @Multipart
    @POST("getteacherroomlist.php")
    Call<ResponseBody>getchattingroomlistinfo(@Part("teacheremail")RequestBody teacheremail);



    //선생님쪽에서  로그인 표시  취할때 값 날려서 해당 학생들에게  fcm 날리도록  진행하기
    //선생님  이메일을  서버로 보내  서버에서 해당 선생님  이메일 정보로
    //로그인  알람 신청한  학생들을 판별하여  알람을 fcm으로 보내줌.
    @Multipart
    @POST("sendfcmwhenteacherlogin.php")
    Call<ResponseBody>notifyteacherlogined(@Part("teacheremail")RequestBody teacheremail);


     //선생님 로그아웃시 fcmtokenfort db테이블에서  teacherlogin-> 0으로 로그아웃 처리 해주기.
    @Multipart
    @POST("teacherlogoutinfcmtokenfort.php")
    Call<ResponseBody>maketeacherlogoutstatus(@Part("teacheremail")RequestBody teacheremail);

    //학생  로그아웃시  fcmtoken  db 테이블에서  studentlogin  ->0으로  로그아웃 처리 해주기.
    @Multipart
    @POST("studentlogoutinfcmtoken.php")
    Call<ResponseBody>makestudentlogoutstatus(@Part("studentemail")RequestBody studentemail);




    //선생님이 로그인 할때 생성되는 fcm토큰을 서버로 보내 디비에 저장하기.
    @Multipart
    @POST("sendteacherfcmtoken.php")
    Call<ResponseBody>sendteacherfcmtoken(@Part("teacheremail")RequestBody teacheremail,@Part("teacherfcmtoken")RequestBody teacherfcmtoken);

    //학생 로그인할때 생성되는  fcm토큰을  서버로  보내  디비에 저장하기
    @Multipart
    @POST("sendstudentfcmtoken.php")
    Call<ResponseBody>sendstudentfcmtoken(@Part("studentemail")RequestBody studentemail,@Part("studentfcmtoken")RequestBody studentfcmtoken);


    //선생님이  로그인시  로그인 알람 받기 요청을 한 경우 fcm보내기위한  정보 처리
    @Multipart
    @POST("registerteachertogetalarm.php")
    Call<ResponseBody>registerteachertogetloginalarm(@Part("studentemail")RequestBody studentemail,@Part("teacheruid")RequestBody teacheruid,@Part("checkregister")RequestBody checkregister);



    //선생님을 내 튜터로 등록하기 위해서  서버로 이메일을 보내고 등록결과값 가져오기.
    @Multipart
    @POST("registermytutor.php")
    Call<ResponseBody>registerteacherasmytutor(@Part("studentemail")RequestBody studentemail,@Part("teacheruid")RequestBody teacheremail,@Part("checkregister")RequestBody checkregister);


    //로그인한 선생님 전체 목록 정보  가져오기.
    @FormUrlEncoded
    @POST("teachergetloginedlist.php")
    Call<GetLoginedTeachersInfo> getloginedteachersinfolist(@Field("studentemail")String studentemail);


    //전체 선생님  목록  정보  가져오기.
    @POST("teacherinfojson.php")
    Call<GetWholeTeacherInfo> getthewholeteacherinfo();

    //선생님  패스워드 바꾸기
    @Multipart
    @POST("teacherchangepassword.php")
    Call<ResponseBody>changeteacherpassword(@Part("teachernewpassword")RequestBody teachnewpassword,@Part("teacheremail")RequestBody teacheremail);


    //선생님  온오프 바꿔주기 결과물
    @Multipart
    @POST("teacherloginstatus.php")
    Call<ResponseBody> sendteacherloginresult(@Part("onoffresult")RequestBody onoff,
                                              @Part("teacheremail")RequestBody teacheremail);

    //선생님 온오프 상태 가져오기
    @FormUrlEncoded
    @POST("teacherloginstatusfromdata.php")
    Call<getteacheronoffresult>gettheteacheronoffresult(@Field("teacheremail") String resultforteacheronoff);


    //선생님 프로필  정보 가져오기
    @FormUrlEncoded
    @POST("getteacherprofileinfo.php")
    Call<teacherinforesult> sendemailtogetteacherprofile(@Field("email") String email);

    //학생  프로필정보 가져오기
    @FormUrlEncoded
    @POST("getstudentprofile.php")
    Call<studentinforesult> sendemailtogetiprofile(@Field("email") String email);

    //학생 프로필 업로드
    @Multipart
    @POST("studentuploadprofile.php")
    Call<ResponseBody> uploadprofile(@Part("email") RequestBody email, @Part("image\"; filename=\"myfile.jpg\" ") RequestBody file);

    //선생님 프로필  업로드
    @Multipart
    @POST("teacheruploadprofile.php")
    Call<ResponseBody> uploadprofileteacher(@Part("email") RequestBody email, @Part("image\"; filename=\"myfile.jpg\" ") RequestBody file);

    //학생 프로필  업데이트  사진이 있을떄
    @Multipart
    @POST("updatestudentprofile.php")
    Call<ResponseBody>updatestudentprofile(@Part("email") RequestBody email, @Part("image\"; filename=\"myfile.jpg\" ") RequestBody file, @Part("newpassword") RequestBody password, @Part("englishname") RequestBody englishname );


    //학생 프로필 업데이트 용
    @Multipart
    @POST("updatestudentprofile1.php")
    Call<ResponseBody>updatestudentprofile1(@Part("email") RequestBody email,  @Part("newpassword") RequestBody password, @Part("englishname") RequestBody englishname,@Part("checkprofilenone")RequestBody checkprofilenone );




    //선생님  프로필 업데이트 용
    @Multipart
    @POST("updateteacherprofile.php")
    Call<ResponseBody>updateteacherprofile(@Part("teacheremail")RequestBody email,
                                           @Part("teachername") RequestBody teachername,
                                           @Part("teacherprofilephoto\"; filename=\"myfile.jpg\"")RequestBody file,
                                           @Part("teachercareer")RequestBody career,
                                           @Part("teacheronesentence")RequestBody onsentence,
                                           @Part("teachersayhellotostudent")RequestBody sayhellowtostudent);

    //로그인 api  체크용
   @Multipart
   @POST("checkloginapiregistered.php")
   Call<ResponseBody>checkloginapiregisered(@Part("loginapiemail")RequestBody loginapiemail);


   @Multipart
   @POST("checkemail.php")
   Call<ResponseBody>uploadloginapistudentinfo( @Part("Data1")RequestBody loginapiemail,
                                                @Part("Data2")RequestBody loginapipasswd,
                                                @Part("Data3")RequestBody loginapistudentenglishname,
                                                @Part("Data4")RequestBody loginapi);




}



