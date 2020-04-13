package com.example.leedonghun.speakenglish;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.googlecode.tesseract.android.TessBaseAPI;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import gun0912.tedbottompicker.TedBottomPicker;
import gun0912.tedbottompicker.TedBottomSheetDialogFragment;

/**
 * speakenglish
 * Class: ActivityForGetWordFromImage.
 * Created by leedonghun.
 * Created On 2020-01-27.
 *
 * Description:  이미지속  영어 문장을 ocr기능으로 추출해서  문장 속  모르는 단어 클릭시 ->  다음 사전 연결 시켜주고,  원하면  내단어장에 추가 가능한 엑티비티이다.
 *
 */
@SuppressLint("ClickableViewAccessibility")
public class ActivityForGetWordFromImage extends AppCompatActivity {

    //영어단어 추출할  사진이 들어가는 이미지뷰 1-1
    private ImageView  img_for_get_image_enlgish_word;

    //영어 단어 추출될 이미지 가지고오는   tedbottom라이브러리 실행 버튼  1-2
    private Button btn_for_get_image;

    //추출된 결과물을 보여주는 텍스트뷰  1-3
    private TextView txt_view_for_show_result;

    //현재 엑티비티 toolbar  1-4
    private Toolbar toolbar_for_get_englis_word;


    private TessBaseAPI mTess; //Tess API reference

    String datapath = "" ; //언어데이터가 있는 경로

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_word_from_image);
        Log.v("check", getLocalClassName()+"의 onCreate() 실행됨");

        img_for_get_image_enlgish_word=findViewById(R.id.imgview_for_get_image_english_word);//1-1
        btn_for_get_image=findViewById(R.id.btn_for_get_image_english_word);//1-2
        txt_view_for_show_result=findViewById(R.id.txtview_for_get_image_english_word);//1-3

        toolbar_for_get_englis_word=findViewById(R.id.toolbarfor_get_img_english_word);//1-4
        txt_view_for_show_result.setMovementMethod(new ScrollingMovementMethod());//텍스트뷰 스크롤  넣어주는거





        //언어파일 경로
         datapath = getFilesDir()+ "/tesseract/";
        //트레이닝데이터가 카피되어 있는지 체크
        checkFile(new File(datapath + "tessdata/"),datapath);



        //현재 엑티비티 ->  툴바  부분  설정
        setSupportActionBar(toolbar_for_get_englis_word);//액션바를  툴바 설정
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//toolbar에서 home키  부분 활성화
        getSupportActionBar().setDisplayShowTitleEnabled(false);//엑션바에서 타이틀 안보이게함
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.whiteback);//홈키 부분  뒤로가기 모양으로 바꿔줌.


        mTess=new TessBaseAPI();



        //영어 단어 추출할  사진 가져오기 버튼 클릭  1-2
        btn_for_get_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.v("check", getLocalClassName()+"의  사진 가져오기 버튼 클릭됨");



                //tedbottompicker 라이브러리 이용해서 -> 가지고옴.
                TedBottomPicker.with(ActivityForGetWordFromImage.this)
                        .setTitle("Select image for detect english word")
                        .showTitle(true)
                        .setPeekHeight(1200)
                        .setSelectMinCountErrorText("You should picke at least one!")
                        .show(new TedBottomSheetDialogFragment.OnImageSelectedListener() {
                            @Override
                            public void onImageSelected(Uri uri) {

                                Bitmap bitmap = null;
                                try {
                                    bitmap = MediaStore.Images.Media.getBitmap(ActivityForGetWordFromImage.this.getContentResolver(), uri);

                                    ExifInterface exif = new ExifInterface(uri.getPath());
                                    int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                                    int exifDegree = exifOrientationToDegrees(exifOrientation);
                                    bitmap=getRotatedBitmap(bitmap, exifDegree);


                                } catch (FileNotFoundException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }



                                ocr_detection ocr_detection=new ocr_detection(bitmap,mTess,datapath);
                                ocr_detection.execute();

                            }
                        });


            }
        });


    }//onCreate()끝



    //목록 새로고침 버튼 눌렸을때,  다이얼로그  띄워서  보여주기위한  asynctask
    private class ocr_detection extends AsyncTask<Void,Void,Void> {

        ProgressDialog refreshprogressbar=new ProgressDialog(ActivityForGetWordFromImage.this);

        Bitmap bitmap;
        TessBaseAPI tessBaseAPI;
        String OCRresult = null;
        String datapath;
        ocr_detection(Bitmap bitmap,TessBaseAPI tessBaseAPI, String datapath){

            this.bitmap=bitmap;
            this.tessBaseAPI=tessBaseAPI;
            this.datapath=datapath;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v("check", getLocalClassName()+"의  목록 새로고침  프로그래스 다이얼로그  실행");

            String lang = "eng";
            tessBaseAPI.init(datapath, lang);

            img_for_get_image_enlgish_word.setImageBitmap(bitmap);

            //프로그래스 다이얼로그  스타일 및  멘트 및  도중 취소 금지시킴.
            refreshprogressbar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            refreshprogressbar.setMessage("OCR 판독중....");
            refreshprogressbar.setCancelable(false);

            //프로그래스바  실행.
            refreshprogressbar.show();


        }

        @Override
        protected Void doInBackground(Void... voids) {

            Log.v("check", getLocalClassName()+"의  목록 새로고침  프로그래스 다이얼로그 띄우고  선생님 목록 받아오는 중.");


            mTess.setImage(bitmap);//해당 판독할 이미지 넣어줌.
            OCRresult = mTess.getUTF8Text();
            //for문으로  어느정도  프로그래스 바  느낌을 주기 위해 노력함.
            //왜냐면,  선생님 목록만 받아오려고 하니까 너무 빨리 받아와 줘서 다이얼로그가 안보였음.
            publishProgress();


            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            Log.v("check", getLocalClassName()+"의  목록 새로고침  프로그레스 다이얼로그  끝남.");

            super.onPostExecute(aVoid);
            //백그라운드 행위 다 끝났으니까  -> 프로그레스 다이얼로그 없애주낟.
            refreshprogressbar.dismiss();

            if(OCRresult !=null){

                Log.v("check", "nulll");
                txt_view_for_show_result.setText(OCRresult);


                txt_view_for_show_result.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        Log.v("check", "터치  봄+"+event);


                        if(event.getAction()==MotionEvent.ACTION_DOWN) {

                            //어느 포지션  터치 되었는지 offset
                            int offset = txt_view_for_show_result.getOffsetForPosition(event.getX(), event.getY());

                            //해당  offset의 단어  찾아서  touched_word에 넣음
                            String touched_word = findWordForRightHanded(txt_view_for_show_result.getText().toString(), offset);

                            //해당단어를 다음 사전 사이트에 검색해  웹뷰로 보여주는   다이얼로그 실행
                            show_dictionery(touched_word);
                        }
                        return false;


                    }
                });
            }

        }//onPostexcute


    }//로그인 선생님  목록 새로고침  ->  프로그레스 다이얼로그  asynck task로  작동 시키는거 끝.

    private Bitmap getRotatedBitmap(Bitmap bitmap, int degree) {
        if (degree != 0 && bitmap != null) {
            Matrix matrix = new Matrix();
            matrix.setRotate(degree, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);

            try {
                Bitmap tmpBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                if (bitmap != tmpBitmap) {
                    bitmap.recycle();
                    bitmap = tmpBitmap;
                }
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }

        return bitmap;
    }

    public int exifOrientationToDegrees(int exifOrientation){

        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {

          return 90;

        }else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {

          return 180;

        }else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {

          return 270;
      }

        return 0;
      }

    //툴바  옵션 메뉴들의  이벤트를  설정하기 위한   itemselected()메소드
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//옵션아이템들 클릭시 진행되는 코드
        switch (item.getItemId()){

            case android.R.id.home: { //toolbar의 back키 눌렀을 때 동작

                Log.v("check", getLocalClassName()+"의  툴바 뒤로가기 눌림.");

                finish();//현재 엑티비티 끝냄.

                return true;

            }

        }
        return super.onOptionsItemSelected(item);

    }//onOptionsitemSelected();



    //asset 폴더에 있는 eng.traineddata복사해서
    private void copyFiles(String datapath) {
        try{
            String filepath = datapath + "/tessdata/eng.traineddata";
            AssetManager assetManager = getAssets();
            InputStream instream = assetManager.open("tessdata/eng.traineddata");
            OutputStream outstream = new FileOutputStream(filepath);


            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }
            outstream.flush();
            outstream.close();
            instream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //check file on the device
    private void checkFile(File dir,String datapath) {
        //디렉토리가 없으면 디렉토리를 만들고 그후에 파일을 카피
        if(!dir.exists()&& dir.mkdirs()) {
            copyFiles(datapath);
        }
        //디렉토리가 있지만 파일이 없으면 파일카피 진행
        if(dir.exists()) {
            String datafilepath = datapath+ "/tessdata/eng.traineddata";
            File datafile = new File(datafilepath);
            if(!datafile.exists()) {
                copyFiles(datapath);
            }
        }
    }


    //해당 텍스트뷰에서 -> 단어를 추출해서 보여준다.
    private String findWordForRightHanded(String str, int offset) { // when you touch ' ', this method returns left word.
        if (str.length() == offset) {
            offset--; // without this code, you will get exception when touching end of the text
        }

        if (str.charAt(offset) == ' ') {
            offset--;
        }
        int startIndex = offset;
        int endIndex = offset;

        try {
            while (str.charAt(startIndex) != ' ' && str.charAt(startIndex) != '\n') {
                startIndex--;
            }
        } catch (StringIndexOutOfBoundsException e) {
            startIndex = 0;
        }

        try {
            while (str.charAt(endIndex) != ' ' && str.charAt(endIndex) != '\n') {
                endIndex++;
            }
        } catch (StringIndexOutOfBoundsException e) {
            endIndex = str.length();
        }

        // without this code, you will get 'here!' instead of 'here'
        // if you use only english, just check whether this is alphabet,
        // but 'I' use korean, so i use below algorithm to get clean word.
        char last = str.charAt(endIndex - 1);
        if (last == ',' || last == '.' ||
                last == '!' || last == '?' ||
                last == ':' || last == ';') {
            endIndex--;
        }

        return str.substring(startIndex, endIndex);

    }//findWordForRightHanded() 이부분 끝.

    //jsoup을 이용해서 -> 해당  웹사이트에  단어를  검색해 -> 뜻을 가지고온다.
    private void  connect_website(String url,String word)  {


        //jsoup으로 파싱해 올때는 thread를 사용한다.
        new Thread(new Runnable() {

            @Override
            public void run() {

                //document -> doc가지고옴.
                Document doc = null;

                try {

                    //doc -> jsoup으로  해당  사전 url 연결 시킨거  가지고오낟.
                    doc = Jsoup.connect(url).get();

                }catch (IOException e) {
                    e.printStackTrace();
                }


                //meta태그에서 - 담긴 뜻을 가지고 와야됨



                Elements metaTag = doc.getElementsByTag("meta");
                Log.v("check_connnect", "metatag "+metaTag);

                //해당  영어 단어가 -> txt_emph1 클래스 이름에  들어있음
                Elements searched_word=doc.getElementsByClass("txt_emph1");
                Log.v("check_connnect", "searched_word "+searched_word);

                for (Element metaTags : metaTag) {

                    String content = metaTags.attr("content");
                    String description = metaTags.attr("property");//메타 테그에서 property -속성

                    String   original_word =searched_word.eachText().get(0);

                    // 검색한  단어 뜻 -> 만약에 사전에 없는 단어를 검색시 유사한단어가 들어가짐. (뜻은 어차피  유사한 단어로 가지고 올것이므로)


                    if(description.equals("og:description")){//메타 테그에서 property -속성이 og:description 일때



                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Log.v("check_connnect", "단어-> "+ original_word +",  뜻-> "+ content);
                                Log.v("check_connect_dd", String.valueOf(original_word));


                                //여기에  sqlite에  해당 단어 저장하는 것을 구현 해야됨.
                                SharedPreferences sharedPreferences =ActivityForGetWordFromImage.this.getSharedPreferences("mydictionery", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor= sharedPreferences.edit();
                                editor.putString(original_word, content);

                                boolean save_word=editor.commit();//쉐어드 저장  실패 또는 성공시

                                if(save_word){//쉐어드 저장 성공시

                                    new Toastcustomer(ActivityForGetWordFromImage.this).showcustomtaost(null, "내 사전에  추가되었습니다.!");

                                }else{//쉐어드 저장  실패시

                                    new Toastcustomer(ActivityForGetWordFromImage.this).showcustomtaost(null, "내 사전에  추가 중 에러가 발생했습니다.");

                                }//쉐어드 저장 실패시 경우 끝

                            }//run()
                        });//해당  엑티비티 runonUiThread()끝

                    }//description 이 og:description  일때.

                }//for문 끝


            }//run() 끝

        }).start();//쓰레드 시작.



    }
    //해당  궁금한 단어를 매개 변수로 받아서 ->  웹뷰로  보여줌.
    private void show_dictionery(String data){

        Log.v("check", "show_dictionery 끝");

        //alertdialog에  webview를 넣어서 ->해당  단어를 검색한  사전 페이지를 보여준다.
        AlertDialog.Builder alert = new AlertDialog.Builder(ActivityForGetWordFromImage.this);
        alert.setTitle("사전 뜻 보기");
        alert.setCancelable(false);

        WebView wv = new WebView(ActivityForGetWordFromImage.this);//웹뷰-> 사전페이지  보옂ㅁ.
        wv.loadUrl("https://dic.daum.net/search.do?q="+ data);//해당 단어가  data에 들어감.
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);

                return true;
            }
        });

        alert.setView(wv);

        //다이얼로그  취소 버튼
        alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        //해당 버튼을 누르면  sqlite에 ->  해당 단어와  뜻이  저장된다.
        //나중에  학생이  나만의 단어장에 들어가서 볼수 있음.
        alert.setNeutralButton("To My Dictionery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //해당 단어와  단어뜻을  다음 사전 페이지에서  가지고옴.
                connect_website("https://dic.daum.net/search.do?q="+data,data);



            }
        });

        //다이얼로그 보여주는데  단어장  추가 버튼은  초록색으로  바꿔줌,
        alert.show().getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.GREEN);

    }//show_dictionery()끝

}//ActivityForGetWordFromImage 끝
