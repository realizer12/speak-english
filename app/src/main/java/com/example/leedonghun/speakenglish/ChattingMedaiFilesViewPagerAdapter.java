package com.example.leedonghun.speakenglish;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.opencv.video.Video;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * speakenglish
 * Class: ChattingMedaiFilesViewPagerAdapter.
 * Created by leedonghun.
 * Created On 2020-01-12.
 * Description: 미디어 파일들 리스트를  뷰페이져에  모두 담아 뷰페이져 이동시마다
 * 해당 포지션의   채팅방 미디어 파일들을 모두 보여준다.
 */
public class ChattingMedaiFilesViewPagerAdapter extends PagerAdapter {

   private JSONArray medeiafiles_list;//미디어 파일들  담긴  어레이리스트 1-1
   private Context mcontext;//context 1-2

   ImageView imageView_for_viewpager;// 미디어 파일 중 이미지 담는 뷰  2-1
   VideoView videoView_for_viewpager;// 미디어 파일 중 비디오 담는 뷰  2-2
   ImageView imageView_for_play_videobtn;// 미디어 파일중  비디오를  재생시키는  뷰 2-3


    public ChattingMedaiFilesViewPagerAdapter(Context context, JSONArray media_file_jsonarray){

        this.mcontext=context;//parent context 받음.
        this.medeiafiles_list=media_file_jsonarray;//미디어 파일들 array받음

    }//ChattingMedaiFilesViewPagerAdapter 생성자 끝

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        //뷰페이지 인플레이트 할 인플레이터  선언.
        LayoutInflater layoutInflater= (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        View viewpage_for_show_chatting_mediafiles=layoutInflater.inflate(R.layout.childeview_for_viewpager_in_mediafiles, container,false);

        container.addView(viewpage_for_show_chatting_mediafiles);//뷰페이져에  ->  해당 child view(리사이클러뷰) 넣어줌.

        //2-1
        imageView_for_viewpager=viewpage_for_show_chatting_mediafiles.findViewById(R.id.imageView_for_media_list);


        //2-2
        videoView_for_viewpager=viewpage_for_show_chatting_mediafiles.findViewById(R.id.videovew_for_medialist);


        //2-3
        imageView_for_play_videobtn=viewpage_for_show_chatting_mediafiles.findViewById(R.id.imgbtn_for_play_mediafiles);

        try {


            int viewtype=medeiafiles_list.getJSONObject(position).getInt("viewtype");//현재  미디어 파일  뷰타입 -(4=이미지,  5= 비디오)
            String server_path=medeiafiles_list.getJSONObject(position).getString("server_path");// 미디어 파일  서버 위치
            int total_count=medeiafiles_list.getJSONObject(position).getInt("total_count");//현재 채팅방의  미디어파일  총 count
            int present_position=medeiafiles_list.getJSONObject(position).getInt("present_position")+1;//현재  보이는  미디어 파일의  포지션
            String sender_name=medeiafiles_list.getJSONObject(position).getString("sender");//보낸사람의 이름.


            String whole_server_path= "http://13.209.249.1/"+server_path;


            Log.v("check_viewpager_info","viewtyp->"+viewtype);//
            Log.v("check_viewpager_info","server_path->"+server_path);
            Log.v("check_viewpager_info","totalcount->"+total_count);
            Log.v("check_viewpager_info","present_position->"+present_position);
            Log.v("check_viewpager_info","sender_name->"+sender_name);


            Uri castvideo_server_URL= Uri.parse(whole_server_path);//비디오 서버 url을  uri로 변환.
            MediaController mediaController=new MediaController(mcontext);//미디어 컨트롤러  1-5


            if(viewtype==4){//이미지 일때


                //이미지 일때이므로 비디오뷰는 INVISIBLE형태로
                videoView_for_viewpager.setVisibility(View.GONE);
                imageView_for_play_videobtn.setVisibility(View.GONE);

                //이미지뷰는 보이게함
                imageView_for_viewpager.setVisibility(View.VISIBLE);

                URL url_for_image = new URL(whole_server_path);//미디어 파일  전체 url
                Glide.with(mcontext).load(url_for_image).placeholder(R.drawable.img_error)
                        .into(imageView_for_viewpager);//해당 이미지를 넣어줌.




            }else if(viewtype==5){//비디오일때,


                imageView_for_play_videobtn.setVisibility(View.VISIBLE);

                videoView_for_viewpager.requestFocus();//비디오뷰에  포커스
                videoView_for_viewpager.setVideoURI(castvideo_server_URL);//비디오뷰에  위에서 전환한  서버 uri를 넣음.

                videoView_for_viewpager.seekTo(1);

                imageView_for_play_videobtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //준비가 되면 -> 비디오뷰와 미디컨트롤러와 연결시켜줌.

                        imageView_for_play_videobtn.setVisibility(View.GONE);
                        videoView_for_viewpager.setMediaController(mediaController);
                        mediaController.setAnchorView(videoView_for_viewpager);//미디어 컨트롤러 비디오뷰 안으로 들어가게 설정
                        videoView_for_viewpager.start();


                    }
                });

                Log.v("CHECKPAGESCROLLED-1", String.valueOf(getItemPosition(viewpage_for_show_chatting_mediafiles)));



                //비디오뷰는 보이고 이미ㅣ뷰는 안보이게 함.
                videoView_for_viewpager.setVisibility(View.VISIBLE);
                imageView_for_viewpager.setVisibility(View.GONE);



            }//비디오 일때 (vietype=5)

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        return viewpage_for_show_chatting_mediafiles;

    }

    //뷰페이져 에서  해당 뷰 사라지게 해줌.
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        //  super.destroyItem(container, position, object);



        container.removeView((View) object);
    }//destroyitem

    @Override
    public int getItemPosition(@NonNull Object object) {

        return super.getItemPosition(object);
    }

    @Override
    public int getCount() {


        return medeiafiles_list.length();//미디어 파일 개수 만큼 늘어남.
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {


        return (view==(View)object);
    }
}
