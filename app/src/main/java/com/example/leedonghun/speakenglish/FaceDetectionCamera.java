package com.example.leedonghun.speakenglish;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
//import android.support.annotation.Nullable;
//import android.support.v4.content.FileProvider;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * speakenglish
 * Class: Face_Detection_Camera.
 * Created by leedonghun.
 * Created On 2019-08-18.
 * Description:
 * 선생님 회원가입시  얼굴이 필요해서
 * 얼굴을 검출하는 카메라 화면이  나오는  엑티비티이다.
 * opencv 에서 제공하는 java Camera view를  이용해서
 * SURFACE를 이용한  카메라  화면을  보여준다.
 *
 */


public class FaceDetectionCamera extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2{

    private Thread t;
    //얼굴검출 상태 받는 쓰레드 값  보내는  handler
    Handler handler=null;
    ImageButton btnfortakephoto;//카메라 찍는 버튼
    TextView mentfordetectedface;//얼굴인식 되면 찍으라고 말해주는 텍스트뷰
    Button swapcamera;//카메라 전환 버튼
    Checkpermission checkpermission;//권한 체크를 위한 클래스
    Toastcustomer customtoast;//커스톰 토스트 가져옴.
    TextView explanation_for_focus_face;//얼굴검출 전에  얼굴  포커싱 하라고 설명

    private String mCurrentPhotoPath;
    private static final int CROP_FROM_CAMERA = 3; //가져온 사진을 자르기 위한 변수
    Uri photoUri;

    boolean dialogcheck=false;

    int ret;

    private Mat matInput;
    private Mat matResult;

    Bitmap thumbImage;

    private int mCameraId = 0; //add this one
    int i=0;

    private CameraBridgeViewBase OpencvCameraView;


    //ndk라이브러리에  작성해둔  메소드들
    public native long loadCascade(String cascadeFileName);
    public native int detect(long cascadeClassifier_face, long cascadeClassifier_eye,int a, long matAddrInput, long matAddrResult);


    public long cascadeClassifier_face = 0;
    public long cascadeClassifier_eye = 0;


    private final Semaphore writeLock = new Semaphore(1);//리소스에  접근 할수 있는  쓰레드수를 1개로  제한한다.

    public void getWriteLock() throws InterruptedException {
        writeLock.acquire();//1개로 확보
    }

    public void releaseWriteLock() {
        writeLock.release();//1개 permit을 다시 반납한다.
    }



    //ndk 파일 라이브러리 형태로 소환
    static {
        System.loadLibrary("face-detect");
    }


    //cascade파일을 복사해오기위한  메소드
    private void copyFile(String filename) {
        String baseDir = Environment.getExternalStorageDirectory().getPath();
        String pathDir = baseDir + File.separator + filename;

        AssetManager assetManager = this.getAssets();

        InputStream inputStream = null;
        OutputStream outputStream = null;


        try {

            Log.d( "check", "copyFile :: 다음 경로로 파일복사 "+ pathDir);
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
            Log.d("check", "copyFile :: 파일 복사 중 예외 발생 "+e.toString() );
        }

    }//copy파일 끝끝


   //얼굴과  눈 검출 할때 사용되는 원 xml파일
    private void read_cascade_file(){
        copyFile("haarcascade_frontalface_alt.xml");
        copyFile("haarcascade_eye_tree_eyeglasses.xml");

        Log.d("check", "read_cascade_file:");

        cascadeClassifier_face = loadCascade( "haarcascade_frontalface_alt.xml");
        Log.d("check", "read_cascade_file:");

        cascadeClassifier_eye = loadCascade( "haarcascade_eye_tree_eyeglasses.xml");
    }//cascaed 파일 읽는 메소드  끝



    //ndk  라이브러리  연결
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {

                    Log.v("checking", "Cmaeraenable");
                    // OpencvCameraView.enableView();
                    //카메라  권한 확인되어있는지  확인해서  camerabridege로 넘겨주는 메소드
                     OpencvCameraView.setCameraPermissionGranted();

                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.face_detection_take_photo);


        //얼굴 감지할떄 사용되는  얼굴 ,눈 xml 파일 읽는 메소드
        read_cascade_file();

        customtoast=new Toastcustomer(FaceDetectionCamera.this);


        //얼굴인식이 진행될 카메라를 띄우기위한  surfaceviewc
        OpencvCameraView=(CameraBridgeViewBase) findViewById(R.id.activity_surface_view_for_speakenglish);
        OpencvCameraView.setVisibility(SurfaceView.VISIBLE);
        OpencvCameraView.setCvCameraViewListener(this);
        OpencvCameraView.setCameraIndex(1); // 맨처음 시작 front 카메라로.

        //얼굴인식 되기전에  얼굴  포커싱하라고  알려줌
        explanation_for_focus_face=findViewById(R.id.explanation_for_focus_face);

        //얼굴인식 되었을때 카메라 찍으라고 말해주는 멘트
        mentfordetectedface=findViewById(R.id.Mentforpresscamerabtn);

        //사진 촬영  버튼
        btnfortakephoto=findViewById(R.id.btnfortakephoto);

        //사진 전환 버튼
        swapcamera=findViewById(R.id.swapcamera_btn);

        //시작할떄 한번더  각  뷰들  visibility상태  지정해줌.
        mentfordetectedface.setVisibility(View.INVISIBLE);
        btnfortakephoto.setVisibility(View.INVISIBLE);
        swapcamera.setVisibility(View.VISIBLE);
        explanation_for_focus_face.setVisibility(View.VISIBLE);


        //다이얼로그 oncreate시에는 false로  체크되어  최초실행이므로 나옴.
        dialogcheck=false;
        //최초 설명  다이얼로그 실행
        showdialog();



        //카메라 전환 버튼 눌렸을때
        swapcamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                swapCamera();
            }
        });//카메라 전환  버튼 끝


        //사진촬영 버튼  클릭
        btnfortakephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //얼굴 검출이 되어있고,  검출이 연속으로 20 프레임이  되었다면
                if(ret>=1 && i>20){
                    // customtoast.showcustomtaost(null, "Face Detected!!");
                    try {
                        getWriteLock();
                        try {
                            //해당 사진
                            createImageFile();
                            cropImage();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    releaseWriteLock();
                }else if(i==0){
                     customtoast.showcustomtaost(null, "Face is not detected");

                }else if(i<11){
                    customtoast.showcustomtaost(null, "Detecting... PLZ keep this pose until camera btn comeout");
                }

            }//onclick끝
        });//사진촬영 버튼 끝


    }//onCreate 끝


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //이미지 crop이 끝나고 진행
        if (requestCode == CROP_FROM_CAMERA) {
            try {


                Intent sendphotobitmap = new Intent();//사진보내는 인텐트
                //해당 사진 파일 절대경로 보냄.
                sendphotobitmap.putExtra("result_photo_path", mCurrentPhotoPath);
                setResult(200,sendphotobitmap);
                t.interrupt();//해당  sucess 멘트 중지
                finish();//현재엑티비티 끝냄.

            } catch (Exception e) {
                Log.e("ERROR", e.getMessage().toString());
            }

        }//CROP_FROM_CAMERA 끝
    }//onActivityResult  끝


    private File createImageFile() throws IOException{

        //저장할  파일  위치
        File path = new File(Environment.getExternalStorageDirectory() + "/Face_detection/");

        if(!path.exists()){//해당 폴더이 없다면,
            path.mkdirs();//해당 폴더 위치 만듬.
        }

       // String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        File file = new File(path, "image.jpg");//위 경로에 image.png-> 있으면  새로운걸로 덧씌어짐.
        String filename = file.toString();//경로  string화

        Imgproc.cvtColor(matResult, matResult, Imgproc.COLOR_BGR2RGB, 4);
        boolean ret = Imgcodecs.imwrite(filename, matResult);//얼굴 검출 이미지  파일로 만듬.


        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        sendBroadcast(mediaScanIntent);

        if (ret) {
           Log.d("check", "얼굴 검출 사진 만듬");
        }
        else{
           Log.d("check", "얼굴 검출 사진 못만듬.");
        }

        //해당 파일 절대경로
        mCurrentPhotoPath =file.getAbsolutePath();

        photoUri = FileProvider.getUriForFile(FaceDetectionCamera.this,
                "com.example.leedonghun.speakenglish.provider", file);

        return file;
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
            intent.putExtra("aspectX", 10);
            intent.putExtra("aspectY", 10);
            intent.putExtra("scale", true);

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




    @Override
    public void onResume() {
        super.onResume();
        Log.v("check", getLocalClassName()+"onResume() 실행");

        if(dialogcheck) {
            OpencvCameraView.enableView();//다시 엑티비티 resume되면   카메라 실행
        }

            //onresume에  권한 체크를 넣은 이유는  혹시나 안드로이드 시스템 스택에 현재 엑티비티가  실행중인 상태에서  잠시
            //설정창을 들어가서  사용자가  권한 승인 취소를 하는 행위를 막기위해서  다시  background 에서  foreground올라올떄,
            //권한 체크를 한번더 해준다.

            //Checkpermission클래스를 실행해서   권한 들의   현재  상태를  리턴받는다.
            //혹시나  사용중  사용자가  앱 실행시 승인했던 권한을 다시 취소하면,  다시 돌아가서  권한승인을 하도록
            //Requestpermisssion 엑티비티로 넘겨준다.
            //checkpermission 클래스 선언
            checkpermission=new Checkpermission( FaceDetectionCamera.this);

            //checkpermission클래스의  checkpermission클래스 함수 리턴값 받기
            boolean permissioncheck=checkpermission.checkPermissions();

            //리턴값이 false이면 승인이 취소된 권한이 있는것 이므로,  requestpermisssion 클래스 로 다시 보내준다.
            if(!permissioncheck){

                Log.v("check","권한 중  승인 안된  권한이 발견되어  requestpermission엑티비티로 다시 넘어간다.");
                Intent intent=new Intent(FaceDetectionCamera.this,RequestPermission.class);
                startActivity(intent);

                //현재 activity를 종료시키는 이유는  Requestpermission 엑티비티에서  권한을 다  승인 했을 경우에  다시 로그인 엑티비티로 넘어갈텐데,
                //이때, 현재 activity를  종료시켜놓지 않으면  스택에 남아있어서  로그인 엑티비티가  스택에  두번  올라가지기 때문이다.
                //뒤로가기 누르다가 발견함.
                finish();
            }



        if (!OpenCVLoader.initDebug()) {
            Log.d("check", "onResume :: Internal OpenCV library not found.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
        } else {
            Log.d("check", "onResum :: OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

    }//on resume닫힘




    @Override
    public void onPause() {
        super.onPause();
        Log.v("check", getLocalClassName()+"onPause() 실행");
        //pause 될때  카메라뷰가  켜져있다면  꺼준다.
        if (OpencvCameraView != null) {
            OpencvCameraView.disableView();
        }


    }//ompause잠김





    public void onDestroy() {
        super.onDestroy();
        Log.v("check", getLocalClassName()+"onDestroy() 실행");
        //destroy될때  카메라뷰가  켜져있다면  꺼준다.
        if (OpencvCameraView != null) {
            OpencvCameraView.disableView();
        }
    }//on destory 잠김


    //맨처음에  사용자에게  얼굴인식 이유 알리고  카메라 뷰 실행.
    private void  showdialog(){
        Log.v("check", getLocalClassName()+"showdialog() 실행");

        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Face Dectection");
        builder.setMessage("Teacher Profile should be with their face\nThis screen is for face detection\nplz show your front face\nuntil the camera button come out!");
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                OpencvCameraView.enableView();//카메라뷰  시작함.
                final Animation anim = new AlphaAnimation(0.0f, 1.0f);//alphaanimation으로  투명도  조절을 진행하여 깜박거리는 효과 넣어줌
                dialog.cancel();//다이얼로그 닫음.
                 dialogcheck=true;//다이얼로그  ok버튼 눌렸으므로, 다시 oncreate실행하지않으면 true로 남음

                handler=new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        switch (msg.what){

                            case 1:
                               //1일 경우  얼굴이 검출된 상황이므로  사진  촬영 버튼, 과  촬영하라는 멘트와 멘트에 애니메이션준게 동작한다.
                                //화면 전환 버튼은 사라진다.
                               btnfortakephoto.setVisibility(View.VISIBLE);
                               mentfordetectedface.setVisibility(View.VISIBLE);
                               swapcamera.setVisibility(View.INVISIBLE);
                               explanation_for_focus_face.setVisibility(View.INVISIBLE);

                                anim.setDuration(200); //애니메이션 지속시간.
                                anim.setStartOffset(20);//애니메이션 시작하기전 대기 시간.
                                anim.setRepeatMode(Animation.REVERSE);//애니메이션 거꾸로 다시실행
                                anim.setRepeatCount(Animation.INFINITE);//애니메이션 반복 카운트 무한 반복
                                mentfordetectedface.startAnimation(anim);//얼굴인식 알림 멘트 텍스트에  애니메이션 추가
                                break;

                            case 2:

                                //메세지 2가  온경우 얼굴 검출이 안된 상태이므로,  전환 버튼 빼고 나머지는 숨긴다.,
                                // 애니메이션은  진행되어있는 상태일수 있으니  cancel시켜준다.
                                explanation_for_focus_face.setVisibility(View.VISIBLE);
                                btnfortakephoto.setVisibility(View.INVISIBLE);
                                mentfordetectedface.setVisibility(View.INVISIBLE);
                                swapcamera.setVisibility(View.VISIBLE);
                                anim.cancel();
                                break;
                        }

                        super.handleMessage(msg);
                    }


                };

                t=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true){
                            try {
                                Thread.sleep(200);
                                Log.v("checkcount", i+"");

                                if(i==20){
                                    //아래  프레임이 총 20번 연속  얼굴을  검출했을때  i>=20이 되고  쓰레드로  i를 지속적으로 검출하여  20이 되면  메세지 1을   hanler로 날린다.
                                    Message msg=handler.obtainMessage(1,i);
                                    handler.sendMessage(msg);

                                }else if(i==0){
                                    //아래  프레임이  얼굴검출이 안되거나  도중에 취소되면 i=0값으로 됨  쓰레드로 지속적으로 검출하는데  i=0이라면  메세지 2를  handler로 날리낟.
                                    Message msg=handler.obtainMessage(2,i);
                                    handler.sendMessage(msg);
                                }

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });
                t.start();//쓰레드 실행
            }
        });
        builder.show();//다이얼로그 띄움


    }//showdialog끝


    // 카메라 전방 후방  스와핑  메서드드
    private void swapCamera() {
        Log.v("check", getLocalClassName()+"swapCamera() 실행");
        // mCameraId = mCameraId^1; //bitwise not operation to flip 1 to 0 and vice versa
        OpencvCameraView.disableView();//카메라 전환시  먼저 카메라 작동 꺼준다.
        if(mCameraId==0){//카메라  default 값 0이면  front임.
            mCameraId=1;//값 1->  back임
            OpencvCameraView.setCameraIndex(0);//index 0->  back 카메라
        }else if(mCameraId==1){
            mCameraId=0;
            OpencvCameraView.setCameraIndex(1);//index 1->  front카메라
        }

        OpencvCameraView.enableView();//전환이 끝났으니 다시 카메라 실행

    }


    @Override
    public void onCameraViewStarted(int width, int height) {
        Log.v("checking", "camerastart");
    }

    @Override
    public void onCameraViewStopped() {
        Log.v("checking", "camerastopped");
    }



    //camera브리짓지   이미지  프레임 함수
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        //Imgproc.warpAffine(mGray, mGray, rotImage, matInput.size());
        Log.v("checking", "CmaeraFRAME");

        try {
            getWriteLock();

            matInput = inputFrame.rgba();


            Mat rotImage = Imgproc.getRotationMatrix2D(new Point(matInput.cols() /2, matInput.rows()/2 ), 90, 1.0);

            Imgproc.warpAffine(matInput, matInput, rotImage, matInput.size());

            if (matResult == null) {
                matResult = new Mat(matInput.rows(), matInput.cols(), matInput.type());
            }

            if(mCameraId==0){

                Core.flip(matInput, matInput, 1);
                //flipcode 1이면 좌우반전
                //flipcode 0이면 상하반전
            }else{

                Core.flip(matInput, matInput, 0);
                Core.flip(matInput, matInput, 1);
            }


            ret=detect(cascadeClassifier_face,cascadeClassifier_eye,1, matInput.getNativeObjAddr(),matResult.getNativeObjAddr());

            if(ret!=0){//ret가  0아니면  무언가  detect된것임.
                i++;//얼굴이 검출되면  1프레임마다  i를  1씩 증가시켜 총  20이상  즉  20프레임 이상 연속으로  검출이 되면, 얼굴로 보고 진행한다.

                if(i>20) {
                    //detect 에서  a부분에  1이들어가면  얼굴인식 중  원이 씌어지지만,  0이들어가면  얼굴인식 원이 사라진다.
                    //화면을 스캔해서 앨범에 저장하는데  원이 있으면 같이 스캔되기 때문에  지정함.
                    ret = detect(cascadeClassifier_face, cascadeClassifier_eye, 0, matInput.getNativeObjAddr(), matResult.getNativeObjAddr());
                 }
                Log.d("check", "face" + ret + "found" +i);
            }else{//ret가 0이므로  아무것도 검출이 안됨.
                i=0;//i를 다시 초기화 시키낟.
                Log.d("check", "face not found");
            }

        }catch (InterruptedException e) {
            e.printStackTrace();
        }

        releaseWriteLock();

        return matResult;
    }


}//현재 클래스 닫힘



