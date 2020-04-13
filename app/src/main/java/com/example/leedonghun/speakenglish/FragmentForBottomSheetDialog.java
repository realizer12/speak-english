package com.example.leedonghun.speakenglish;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.nkzawa.socketio.client.Socket;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import gun0912.tedbottompicker.TedBottomPicker;
import gun0912.tedbottompicker.TedBottomSheetDialogFragment;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * speakenglish
 * Class: FragmentForBottomSheetDialog.
 * Created by leedonghun.
 * Created On 2020-01-03.
 * Description:채팅 할떄  파일 보내기 버튼을 클릭시 나오는   bottom_Fragmentsheetdialog의 이벤트들이 들어가는 곳이다.
 * //이곳에서  채팅방에서 업로드하기위해 선택한  비디오 파일과  이미지 파일을  서버로  보낸다
 */
public class FragmentForBottomSheetDialog  extends BottomSheetDialogFragment {

    LinearLayout linearLayout_for_upload_pic;
    LinearLayout linearLayout_for_upload_video;
    TextView cancel_btn;

    Socket socket;

    FragmentForBottomSheetDialog(Socket socket){
        this.socket=socket;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootview=inflater.inflate(R.layout.chatting_filebtn_bottom_fragment, container,false);//현재 프래그먼트 전체 뷰

        linearLayout_for_upload_pic=rootview.findViewById(R.id.uploadpicturentn);
        linearLayout_for_upload_video=rootview.findViewById(R.id.uploadvideobtn);
        cancel_btn=rootview.findViewById(R.id.cancel_bttom_fragment);


        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(socket.connected()){
                    Log.v("socket확인", String.valueOf(socket.id()));
                }
              dismiss();
            }
        });

       linearLayout_for_upload_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                //사진 선택  기능-> tedbottompicker 라이브러리 사용함.
                TedBottomPicker.with(getActivity())
                        .setTitle("Select Viedo")//타이틀 정함.
                        .setPeekHeight(1200)//높이 지정
                        .showTitle(true)//타이틀 보임
                        .setTitle("Select max 1 video !")//타이틀
                        .setCompleteButtonText("Done")//선택완료 버튼
                        .setEmptySelectionText("No Selected")//
                        .setSelectMinCount(1)//최소 1개  사진
                        .setSelectMaxCount(1)//최대  3개 사진
                        .setSelectMinCountErrorText("you should select at least one!")//최소 선택이  안되었을때 나오는 토스트멘트
                        .setSelectMaxCountErrorText("PLZ select max 1 video !!")//최대선택 초과시 나오는 토스트
                        .showVideoMedia()//비디오 타입의 미디어 만 보여준다.
                        .showMultiImage(new TedBottomSheetDialogFragment.OnMultiImageSelectedListener() {
                          @Override
                          public void onImagesSelected(List<Uri> uriList) {

                              if(socket.connected()){
                                  Log.v("socket확인", String.valueOf(socket.id()));
                              }

                              send_vieofile_inchatting(uriList);//비디오 업로드를 위해 만든 메소드 -> 선택한 비디오 uri 보내줌.

                              dismiss();//해당 -bottomsheetdialog 보내줌.
                          }
                      });
            }
        });



       linearLayout_for_upload_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //사진 선택  기능-> tedbottompicker 라이브러리 사용함.
                TedBottomPicker.with(getActivity())
                        .setTitle("Select Photo")//타이틀 정함.
                        .setPeekHeight(1200)//높이 지정
                        .showTitle(true)//타이틀 보임
                        .setTitle("Select max 3 Photos !!")//타이틀
                        .setCompleteButtonText("Done")//선택완료 버튼
                        .setEmptySelectionText("No Selected")//
                        .setSelectMinCount(1)//최소 1개  사진
                        .setSelectMaxCount(3)//최대  3개 사진
                        .setSelectMinCountErrorText("you should select at least one!")//최소 선택이  안되었을때 나오는 토스트멘트
                        .setSelectMaxCountErrorText("PLZ selec max 3 photo!")//최대선택 초과시 나오는 토스트
                        .showMultiImage(new TedBottomSheetDialogFragment.OnMultiImageSelectedListener() {//이미지 여러장  pick가능
                            @Override
                            public void onImagesSelected(List<Uri> uriList) {

                                if(socket.connected()){
                                    Log.v("socket확인", String.valueOf(socket.id()));
                                }
                                send_imagefile_inhatting(uriList);// 이미지 업로드를 위해 만든 -> 메소드에 해당 선택한 이미지 uri 보내줌.

                                dismiss();
                            }//onImageSelected() 끝
                        });// .showMultiImage() 끝

            }
        });


        return rootview;
    }


    //선택한 비디오 보내기위한 -> uri  받는 메소드
    private void send_vieofile_inchatting(List<Uri> uriList){


        String current_video_path;// 사진1 경로

        File uploadFile;//파일 객체

        RequestBody reqFile = null;
        current_video_path=uriList.get(0).getPath();

        uploadFile=new File(current_video_path);
        reqFile = RequestBody.create(MediaType.parse("video/*"),uploadFile);//프로필 사진;

        MultipartBody.Part reqFile1=MultipartBody.Part.createFormData("video", uploadFile.getName(),reqFile);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create())//json으로 받고 싶으면 쓰면안됨..
                .build();//리트로핏 뷸딩
        ApiService apiService = retrofit.create(ApiService.class);//api인터페이스 크리에이트


        //progressdialog ->   사진  채팅방에 업로드 할때   나오는  프로그래스 바
        final ProgressDialog progressDoalog;

        progressDoalog = new ProgressDialog(getActivity());
        progressDoalog.setMax(100);//프로그래스 진행 최대 값
        progressDoalog.setCancelable(false);//프로그래스바  다른 곳 눌렀을때  취소 가능한지 여부 false
        progressDoalog.setMessage("uploading video....");
        progressDoalog.setTitle("Please Wait for a while..");
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//스피너 형태  프로그래스 다이얼로그

        //프로그래스다이얼로그  보여주기
        progressDoalog.show();

        //이미지 업로드  call객체
        Call<ResponseBody> upload_video=apiService.uploadeVideo(reqFile1);

        upload_video.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                try {

                    String video_server_path=response.body().string().replaceAll("\"", "");

                    Log.v("check비디오히히", video_server_path);

                    //비디오 서버 경로 를 채팅 서버에 보내준다.
                    socket.emit("chatting_video", video_server_path);


                } catch (IOException e) {
                    e.printStackTrace();
                }

                progressDoalog.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.v("check비디오히히에러",t.toString());
            }
        });
    }



    //채팅 도중에  이미지를 보낼때 -> 해당 채팅방 이미지 셀렉트에서 선택한  이미지들  가지고와서
    //서버로  넣어주고,  해당  저장 경로를 받아서옴 -> 그리고 여기서 socket채팅으로 해당 경로를 메세지로 보내준다.
    private void send_imagefile_inhatting(List<Uri> uriList){

        //멀티이미지로  3개까지  select가능하므로,  세가지 file을  처리해준다.

        //사진 경로
        String current_photo_path;// 사진1 경로
        String current_photo_path1;//사진2 경로
        String current_photo_path2;//사진3 경로

        //File 객체
        File uploadFile;
        File uploadFile1;
        File uploadFile2;

        //Requestbody 객체
        RequestBody reqFile = null;
        RequestBody reqFile1=null;
        RequestBody reqFile2=null;


        //사진의 개수가 3개일때
        if(uriList.size()==3){

            //선택한 사진  절대경로
            current_photo_path=uriList.get(0).getPath();
            current_photo_path1=uriList.get(1).getPath();
            current_photo_path2=uriList.get(2).getPath();

            uploadFile = new File(current_photo_path);//선생님 이미지 파일
            uploadFile1 = new File(current_photo_path1);//선생님 이미지 파일
            uploadFile2 = new File(current_photo_path2);//선생님 이미지 파일

            reqFile = RequestBody.create(MediaType.parse("image/jpeg"),uploadFile);//프로필 사진;
            reqFile1 = RequestBody.create(MediaType.parse("image/jpeg"),uploadFile1);//프로필 사진;
            reqFile2 = RequestBody.create(MediaType.parse("image/jpeg"),uploadFile2);//프로필 사진;

        }else if(uriList.size()==2){//사진의 개수가 2개일때


            current_photo_path1=uriList.get(0).getPath();
            current_photo_path2=uriList.get(1).getPath();

            uploadFile1 = new File(current_photo_path1);//선생님 이미지 파일
            uploadFile2 = new File(current_photo_path2);//선생님 이미지 파일

            reqFile = RequestBody.create(MediaType.parse("image/jpeg"),uploadFile1);//프로필 사진;
            reqFile1 = RequestBody.create(MediaType.parse("image/jpeg"),uploadFile2);//프로필 사진;

        }else if(uriList.size()==1){//사진의 개수가 1개일때


            current_photo_path2=uriList.get(0).getPath();
            uploadFile2 = new File(current_photo_path2);//선생님 이미지 파일
            reqFile = RequestBody.create(MediaType.parse("image/jpeg"),uploadFile2);//프로필 사진;

        }

        //채팅  이미지 업로드 시 서버에서 보낸  jsonobject를  받기위해서  gson선언
        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiService.baseurl).addConverterFactory(GsonConverterFactory.create(gson))//json으로 받고 싶으면 쓰면안됨..
                .build();//리트로핏 뷸딩
        ApiService apiService = retrofit.create(ApiService.class);//api인터페이스 크리에이트

        //progressdialog ->   사진  채팅방에 업로드 할때   나오는  프로그래스 바
        final ProgressDialog progressDoalog;

        progressDoalog = new ProgressDialog(getActivity());
        progressDoalog.setMax(100);//프로그래스 진행 최대 값
        progressDoalog.setCancelable(false);//프로그래스바  다른 곳 눌렀을때  취소 가능한지 여부 false
        progressDoalog.setMessage("uploading image....");
        progressDoalog.setTitle("Please Wait for a while..");
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//스피너 형태  프로그래스 다이얼로그

        //프로그래스다이얼로그  보여주기
        progressDoalog.show();


        //이미지 업로드  call객체
        Call<GetChattingImagePath> upload_image=apiService.uploadImages(reqFile,reqFile1,reqFile2);

        //이미지 업로드 -> enqueue
        upload_image.enqueue(new Callback<GetChattingImagePath>() {
            @Override
            public void onResponse(Call<GetChattingImagePath> call, Response<GetChattingImagePath> response) {

                ArrayList<JsonObject> arrayList=response.body().getChatting_image_path();//서버에서 받아온  jsonobject 객체  넣어줌.

                //  1이  아닐 경우-> 해당  값에 이미지 경로가 들어있다.
                if(!arrayList.get(0).get("image1").toString().replaceAll("\"", "").equals("1")){//첫번째 이미지

                    Log.v("check","FragmentForBottomSheetDialog 의 채팅사진  넣기 성공 -> 채팅 경로 ->"+arrayList.get(0).get("image1")+"\n");
                    //여기서 소켓 날리기 들어감.

                    String image1_server_path=arrayList.get(0).get("image1").toString().replaceAll("\"", "");
                    socket.emit("chatting_image",image1_server_path);//서버로 해당  채팅 이미지 서버  주소 보내줌.

                }//두번째 이미지 끝.

                if(!arrayList.get(0).get("image2").toString().replaceAll("\"", "").equals("1")){//두번째  이미지
                    Log.v("check", "FragmentForBottomSheetDialog 의 채팅사진  넣기 성공 -> 채팅 경로 ->"+arrayList.get(0).get("image2")+"\n");
                    //여기서 소켓 날리기 들어감.

                    String image2_server_path=arrayList.get(0).get("image2").toString().replaceAll("\"", "");
                    socket.emit("chatting_image",image2_server_path);//서버로 해당  채팅 이미지 서버  주소 보내줌.




                }//두번째 이미지 끝

                if(!arrayList.get(0).get("image3").toString().replaceAll("\"", "").equals("1")){//세번째 이미지

                    Log.v("check", "FragmentForBottomSheetDialog 의 채팅사진  넣기 성공 -> 채팅 경로 ->"+arrayList.get(0).get("image3")+"\n");
                    //여기서 소켓 날리기 들어감.
                    String image3_server_path=arrayList.get(0).get("image3").toString().replaceAll("\"", "");
                    socket.emit("chatting_image", image3_server_path);//서버로 해당  채팅 이미지 서버  주소 보내줌.

                }//세번째 이미지 끝.

                progressDoalog.dismiss();//모든  과정이 끝났으니 -> 프로그래스 다이얼로그 꺼줌.


            }//onResponse()끝

            @Override
            public void onFailure(Call<GetChattingImagePath> call, Throwable t) {

                Log.v("check", "FragmentForBottomSheetDialog 의 채팅사진  넣기 실패 -> 채팅 경로 ->"+t);


            }//onFailure() Rmx
        });


    }//send)image_file_chatting



}
