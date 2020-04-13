package com.example.leedonghun.speakenglish;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.FaceDetector;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
//import android.support.annotation.Nullable;
//import android.support.v4.content.FileProvider;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * speakenglish
 * Class: FaceDetectionAlbum.
 * Created by leedonghun.
 * Created On 2019-08-21.
 * Description:
 */
public class FaceDetectionAlbum extends AppCompatActivity {

    Animation anim;
    Toastcustomer toastcustomer;

    Uri photoUri=null;

    File tempFile;

    String mcuurentpath;
    File path1;

    boolean detectioncheck=false;
    int checkputphoto;

    int ret;
    final private int CROP_FROM_CAMERA = 3; //가져온 사진을 자르기 위한 변수
    final private int REQ_CODE_SELECT_IMAGE=101;
    Button cancel;
    Button takeit;
    Button getalbum;
    ImageView originalphoto;
    ImageView reusltphoto;
    TextView detectedment;
    private Mat matResult;

    private Bitmap mInputImage;
    private Bitmap mOriginalImage;

    static {
        System.loadLibrary("face-detect-album");
    }
    public native long loadCascade(String cascadeFileName);
    public native int detect(long cascadeClassifier_face, long cascadeClassifier_eye,int a, long matAddrInput, long matAddrResult);

    public long cascadeClassifier_face = 0;
    public long cascadeClassifier_eye = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.face_detection_album);
        toastcustomer=new Toastcustomer(FaceDetectionAlbum.this);//커스톰 토스트
        cancel=findViewById(R.id.btn_for_cancel);//취소버튼
        takeit=findViewById(R.id.btn_for_take_it);//takeit 버튼
        getalbum=findViewById(R.id.btn_for_take_album);//앨범가기 버튼
        originalphoto=findViewById(R.id.originalimage);//기존 이미지
        reusltphoto=findViewById(R.id.resultimage);//검출과정후 이미지
        detectedment=findViewById(R.id.textview_for_takeit);//검출됬다고  멘트 알림.

        //detectioncheck 처음에 false임.
        detectioncheck=false;
        //사진이  앨범으로부터 가져왔는지 여부
        checkputphoto=0;

        final Animation wrongshake = AnimationUtils.loadAnimation(FaceDetectionAlbum.this, R.anim.shakeedittext);//쉐이크하는 애니메이션 선언
        //검출 멘트  처음엔  안보임
        detectedment.setVisibility(View.INVISIBLE);

        read_cascade_file();

        //취소버튼 눌렀을때
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                finish();
            }
        });//cancel 버튼  클릭리스너  끝끝


        //사진 사용 버튼
        takeit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                  //얼굴 인식이 되어있는 경우
                 if(detectioncheck){

                     Intent sendphotobitmap = new Intent();//사진보내는 인텐트
                     //해당 사진 파일 절대경로 보냄.
                     sendphotobitmap.putExtra("result_album_path", tempFile.getAbsolutePath());
                     setResult(200,sendphotobitmap);
                     anim.cancel();
                     finish();//현재엑티비티 끝냄.


                 }else{//얼굴 인식이 안되어있는 경우

                     if(checkputphoto==1){//사진이 올려져 있을떄

                         //토스트와 애니메이션 보임.
                         toastcustomer.showcustomtaost(null, "No face detected!");
                         reusltphoto.startAnimation(wrongshake);

                     }else{//사진이   안올려져 있을떄

                         //토스트와 애니메이션 보임
                         toastcustomer.showcustomtaost(null, "Put your facephoto!!");
                         reusltphoto.startAnimation(wrongshake);
                     }

                 }//얼굴인식안었을때 끝
            }//클릭 리스너 onclick 끝
        });//takeit 클릭리스너 끝.



         //앨범 사진고르로 가기 버튼
        getalbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);


            }
        });//앨범가기 끝




    }//oncreate 끝




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == Activity.RESULT_OK) {
          if (requestCode == REQ_CODE_SELECT_IMAGE) {

              String path = getImagePathFromURI(data.getData());//이미지 경로
              path1 = new File(path);
              mcuurentpath= path1.getAbsolutePath();
              checkputphoto=1;
              photoUri=data.getData();

              cropImage();

            }else if(requestCode==CROP_FROM_CAMERA){

              originalphoto.setImageURI(Uri.parse(mcuurentpath));
                  Log.v("check", "crop했을때 실행");
                  try {


                      Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
 //                     Bitmap thumbImage = ThumbnailUtils.extractThumbnail(bitmap, 128, 128);
//                      ByteArrayOutputStream bs = new ByteArrayOutputStream();
//                      thumbImage.compress(Bitmap.CompressFormat.JPEG, 100, bs); //이미지가 클 경우 OutOfMemoryException 발생이 예상되어 압축
//
//                      //여기서는 ImageView에 setImageBitmap을 활용하여 해당 이미지에 그림을 띄우시면 됩니다.
//                      profileimg.setImageBitmap(thumbImage);

                      BitmapFactory.Options options = new BitmapFactory.Options();
                      options.inSampleSize = 4;

                      mInputImage = bitmap;


                      Mat src = new Mat();
                      Utils.bitmapToMat(mInputImage, src);



                      Mat edge = new Mat();
                      matResult = new Mat(edge.rows(), edge.cols(),edge.type());
                      ret=detect(cascadeClassifier_face,cascadeClassifier_eye,1, src.getNativeObjAddr(),matResult.getNativeObjAddr());

                      Utils.matToBitmap(matResult,mInputImage);
                      reusltphoto.setImageBitmap(mInputImage);



                      if(ret !=0){

                          anim = new AlphaAnimation(0.0f, 1.0f);//alphaanimation으로  투명도  조절을 진행하여 깜박거리는 효과 넣어줌
                          anim.setDuration(200); //애니메이션 지속시간.
                          anim.setStartOffset(20);//애니메이션 시작하기전 대기 시간.
                          anim.setRepeatMode(Animation.REVERSE);//애니메이션 거꾸로 다시실행
                          anim.setRepeatCount(Animation.INFINITE);//애니메이션 반복 카운트 무한 반복
                          detectedment.setVisibility(View.VISIBLE);
                          detectedment.startAnimation(anim);//얼굴인식 알림 멘트 텍스트에  애니메이션 추가

                          //얼굴 체크 여부  true
                          detectioncheck=true;

                      }else {

                          if(anim.hasStarted()){//애니메이션이  시작된 상태 -> 얼굴 검출이되었어서...
                              anim.cancel();//애니메이션을 취소시킨다.
                              detectedment.setVisibility(View.INVISIBLE);//발견되었다는 멘트  안보이게함.
                          }

                          //얼굴 체크  false
                          detectioncheck=false;
                      }

                  } catch (Exception e) {
                      e.printStackTrace();
                  }


              }


          }
        }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "IP" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/test/"); //test라는 경로에 이미지를 저장하기 위함
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
      //  mCurrentPhotoPath =image.getAbsolutePath();

        return image;
    }



    //이미지 크롭하기
    public void cropImage() {

        this.grantUriPermission("com.android.camera", photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);


        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(photoUri, "image/*");
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);


        grantUriPermission(list.get(0).activityInfo.packageName, photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);


        int size = list.size();
        if (size == 0) {

            return;
        } else {

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            intent.putExtra("crop", "true");
            intent.putExtra("scale", true);

            File croppedFileName = null;
            try {
                croppedFileName = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            File folder = new File(Environment.getExternalStorageDirectory() + "/test/");
            tempFile = new File(folder.toString(), croppedFileName.getName());

            photoUri = FileProvider.getUriForFile(FaceDetectionAlbum.this,
                    "com.example.leedonghun.speakenglish.provider", tempFile);

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.putExtra("return-data", false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString()); //Bitmap 형태로 받기 위해 해당 작업 진행

            Intent i = new Intent(intent);
            ResolveInfo res = list.get(0);

            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            grantUriPermission(res.activityInfo.packageName, photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

            i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            startActivityForResult(i, CROP_FROM_CAMERA);

        }//size !=0일때 끝




    }//cropimage 끝





    public String getImagePathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            int idx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String imgPath = cursor.getString(idx);
            cursor.close();
            return imgPath;
        }
    }

    private void copyFile(String filename) {
        String baseDir = Environment.getExternalStorageDirectory().getPath();
        String pathDir = baseDir + File.separator + filename;

        AssetManager assetManager = this.getAssets();

        InputStream inputStream = null;
        OutputStream outputStream = null;


        try {

            Log.d( "check-face-album", "copyFile :: 다음 경로로 파일복사 "+ pathDir);
            inputStream = assetManager.open(filename);
            outputStream = new FileOutputStream(pathDir);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }

            inputStream.close();
            inputStream = null;
            outputStream.flush();
            outputStream.close();
            outputStream = null;
        } catch (Exception e) {
            Log.d("check-face-album", "copyFile :: 파일 복사 중 예외 발생 "+e.toString() );
        }

    }

    private void read_cascade_file(){
        copyFile("haarcascade_frontalface_alt.xml");
        copyFile("haarcascade_eye_tree_eyeglasses.xml");

        Log.d("check-face-album", "read_cascade_file:");

        cascadeClassifier_face = loadCascade( "haarcascade_frontalface_alt.xml");
        Log.d("check-face-album", "read_cascade_file:");

        cascadeClassifier_eye = loadCascade( "haarcascade_eye_tree_eyeglasses.xml");
    }

}
