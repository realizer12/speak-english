package com.example.leedonghun.speakenglish;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.UiThread;

import com.example.leedonghun.speakenglish.webrtc.AppRTCClient;
import com.example.leedonghun.speakenglish.webrtc.PeerConnectionClient;
import com.example.leedonghun.speakenglish.webrtc.WebSocketRTCClient;

import org.opencv.video.Video;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.IceCandidate;
import org.webrtc.Logging;
import org.webrtc.RendererCommon;
import org.webrtc.SessionDescription;
import org.webrtc.StatsReport;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoFrame;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoSink;

import java.util.ArrayList;
import java.util.List;

/**
 * speakenglish
 * Class: VideoCallActivity.
 * Created by leedonghun.
 * Created On 2020-02-22.
 * Description:
 */
public class VideoCallActivity extends Activity implements AppRTCClient.SignalingEvents, PeerConnectionClient.PeerConnectionEvents {

    private static final String TAG = "CallActivity";
    private static final String APPRTC_URL = "https://appr.tc";
    //private static final String APPRTC_URL = "http://13.209.249.1";

    private int check_finish_call=0;


    // Peer connection statistics callback period in ms.
    private static final int STAT_CALLBACK_PERIOD = 1000;
    private final ProxyRenderer remoteProxyRenderer = new ProxyRenderer();
    private final ProxyVideoSink localProxyVideoSink = new ProxyVideoSink();
    private final List<VideoRenderer.Callbacks> remoteRenderers = new ArrayList<>();
    private PeerConnectionClient peerConnectionClient = null;
    private AppRTCClient appRtcClient;
    private AppRTCClient.SignalingParameters signalingParameters;
    private SurfaceViewRenderer pipRenderer;
    private SurfaceViewRenderer fullscreenRenderer;
    private Toast logToast;
    private boolean activityRunning;
    private AppRTCClient.RoomConnectionParameters roomConnectionParameters;
    private PeerConnectionClient.PeerConnectionParameters peerConnectionParameters;
    private boolean iceConnected;
    private boolean isError;
    private long callStartedTimeMs = 0;
    private boolean micEnabled = true;
    private boolean isSwappedFeeds;
    // Control buttons for limited UI
    private ImageButton disconnectButton;
    private ImageButton cameraSwitchButton;
    private ImageButton toggleMuteButton;

    private TextView class_time_show_txt;

    private TextView txt_for_other;//상대방  직업 넣어줌

    TaskToTimer taskToTimer;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.video_call_activity);

        iceConnected = false;
        signalingParameters = null;


        txt_for_other=findViewById(R.id.textView_for_other);

        // Create UI controls.
        pipRenderer = findViewById(R.id.pip_video_view);
        fullscreenRenderer = findViewById(R.id.fullscreen_video_view);

        disconnectButton = findViewById(R.id.button_call_disconnect);
        cameraSwitchButton = findViewById(R.id.button_call_switch_camera);
        toggleMuteButton = findViewById(R.id.button_call_toggle_mic);

        // Add buttons click events.
        disconnectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onCallHangUp();


            }
        });

        cameraSwitchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onCameraSwitch();
            }
        });

        toggleMuteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                boolean enabled = onToggleMic();
                toggleMuteButton.setAlpha(enabled ? 1.0f : 0.3f);
            }
        });

        remoteRenderers.add(remoteProxyRenderer);

        // Create peer connection client.
        peerConnectionClient = new PeerConnectionClient();

        // Create video renderers.
        pipRenderer.init(peerConnectionClient.getRenderContext(), null);
        pipRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);

        fullscreenRenderer.init(peerConnectionClient.getRenderContext(), null);
        fullscreenRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);

        pipRenderer.setZOrderMediaOverlay(true);
        pipRenderer.setEnableHardwareScaler(true /* enabled */);
        fullscreenRenderer.setEnableHardwareScaler(true /* enabled */);
        // Start with local feed in fullscreen and swap it to the pip when the call is connected.
        //setSwappedFeeds(true /* isSwappedFeeds */);

        // Generate a random room ID with 7 uppercase letters and digits
        // String randomRoomID = randomString(7, UPPER_ALPHA_DIGITS);

        // Show the random room ID so that another client can join from https://appr.tc
        class_time_show_txt = findViewById(R.id.class_time_show);


        int  positioncheck=user_position_check();
        Intent intent=getIntent();
        String randomRoomID=intent.getStringExtra("roomnumber");//방번호 받음
        String teacheruid=null;
        String studentname=null;
        String student_profileurl=null;
        String student_uid=null;
         GlobalApplication globalApplication=(GlobalApplication) getApplicationContext();
        if(positioncheck==1) {//선생님일때,

            txt_for_other.setText("student");

           student_uid=intent.getStringExtra("student_uid");//학생 uid받음.
           studentname = intent.getStringExtra("student_name");//학생이름 받음
           student_profileurl=intent.getStringExtra("student_profileurl");//학생 이미지 url받음.

        }else if(positioncheck==0){//학생일때

            txt_for_other.setText("선생님");
            teacheruid=intent.getStringExtra("teacheruid");//선생님 uid
        }

        Log.d(TAG, getString(R.string.room_id_caption) + randomRoomID);

        // Connect video call to the random room
        connectVideoCall(randomRoomID+"leedong");//--------------------------------------------------------->  비디오 콜  연결----------------------------------------------------다시 해줘야됨.

        taskToTimer=new TaskToTimer();


        if(positioncheck==0){//학생일때

            taskToTimer.setTextView(R.id.class_time_show,VideoCallActivity.this,positioncheck,teacheruid,globalApplication.getStudnet_uid());

        }else if(positioncheck==1){//선생 일때,

            taskToTimer.setTextView(R.id.class_time_show,VideoCallActivity.this,positioncheck,student_profileurl,studentname,globalApplication.getTeacheruid(),student_uid);
        }

        int time=90;
        taskToTimer.setTime(time);
        taskToTimer.execute();

    }//onCreate() 끝




    // Join video call with randomly generated roomId
    private void connectVideoCall(String roomId) {
        Uri roomUri = Uri.parse(APPRTC_URL);

        int videoWidth = 0;
        int videoHeight = 0;

        peerConnectionParameters =
                new PeerConnectionClient.PeerConnectionParameters(true,
                        false,
                        false,
                        videoWidth,
                        videoHeight,
                        0,
                        Integer.parseInt(getString(R.string.pref_maxvideobitratevalue_default)),
                        getString(R.string.pref_videocodec_default),
                        true,
                        false,
                        Integer.parseInt(getString(R.string.pref_startaudiobitratevalue_default)),
                        getString(R.string.pref_audiocodec_default),
                        false,
                        false,
                        false,
                        false,
                        false,
                        false,
                        false,
                        false,
                        null);

        // Create connection client. Use the standard WebSocketRTCClient.
        // DirectRTCClient could be used for point-to-point connection
        appRtcClient = new WebSocketRTCClient(this);
        // Create connection parameters.
        roomConnectionParameters =
                new AppRTCClient.RoomConnectionParameters(
                        roomUri.toString(),
                        roomId,
                        false,
                        null);

        peerConnectionClient.createPeerConnectionFactory(
                getApplicationContext(), peerConnectionParameters, VideoCallActivity.this);

        startCall();
    }





    public void onCallHangUp() {


        disconnect();

        finish();
    }

    public void onCameraSwitch() {
        if (peerConnectionClient != null) {
            peerConnectionClient.switchCamera();
        }
    }

    public boolean onToggleMic() {
        if (peerConnectionClient != null) {
            micEnabled = !micEnabled;
            peerConnectionClient.setAudioEnabled(micEnabled);
        }
        return micEnabled;
    }

    //쉐어드에  저장된 값으로  현재 유저가 선생님인지 학생인지를 구별한다.
    public int user_position_check(){

        Log.v("check", getLocalClassName()+"의 user_position_check()실행됨");

        int result=-1;//학생 또는 선생님 결과

        SharedPreferences getstudentemail = getSharedPreferences("loginstudentid", MODE_PRIVATE);//로그인 아이디  쉐어드에 담긴거 가져옴
        final String studentemail = getstudentemail.getString("loginid", "");//학생 로그인 이메일 가져옴.


        //선생님 이메일 쉐어드에  넣기.
        final SharedPreferences getteacheremail = getBaseContext().getSharedPreferences("loginteacherid",MODE_PRIVATE);//선생님 로그인 아이디  쉐어드에 담긴거 가져옴
        String teacheremail= getteacheremail.getString("loginidteacher","");//선생님 로그인 이메일 가져옴

        if(studentemail.equals("")){//선생님 로그인 일때

            result=1;//선생님일때 1-7

        }else if(teacheremail.equals("")){//학생 로그인 일때

            result=0;//학생일때 1-7
        }

        return result;

    }//user_position_check()끝

    private void startCall() {
        if (appRtcClient == null) {
            Log.e(TAG, "AppRTC client is not allocated for a call.");
            return;
        }
        callStartedTimeMs = System.currentTimeMillis();

        // Start room connection.

        appRtcClient.connectToRoom(roomConnectionParameters);
    }

    @UiThread
    private void callConnected() {
        final long delta = System.currentTimeMillis() - callStartedTimeMs;
        Log.i(TAG, "Call connected: delay=" + delta + "ms");
        if (peerConnectionClient == null || isError) {
            Log.w(TAG, "Call is connected in closed or error state");
            return;
        }
        // Enable statistics callback.
        peerConnectionClient.enableStatsEvents(true, STAT_CALLBACK_PERIOD);
        setSwappedFeeds(false /* isSwappedFeeds */);
    }

    // Disconnect from remote resources, dispose of local resources, and exit.
    private void disconnect() {

        activityRunning = false;
        remoteProxyRenderer.setTarget(null);
        localProxyVideoSink.setTarget(null);
        if (appRtcClient != null) {
            appRtcClient.disconnectFromRoom();
            appRtcClient = null;
        }
        if (pipRenderer != null) {
            pipRenderer.release();
            pipRenderer = null;
        }
        if (fullscreenRenderer != null) {
            fullscreenRenderer.release();
            fullscreenRenderer = null;
        }
        if (peerConnectionClient != null) {
            peerConnectionClient.close();
            peerConnectionClient = null;
        }
        if (iceConnected && !isError) {
            setResult(RESULT_OK);
        } else {
            setResult(RESULT_CANCELED);
        }

        //수업중에  disconnect면  전부 disconnect해준다.
        //수업이 끝난 상태라면  각자  tasktoimer에서 끝내기 처리해줌.
        if(taskToTimer.getStatus() != AsyncTask.Status.FINISHED){
            finish();
        }


    }




    private void disconnectWithErrorMessage(final String errorMessage) {
        if (!activityRunning) {
            Log.e(TAG, "Critical error: " + errorMessage);
            disconnect();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(getText(R.string.channel_error_title))
                    .setMessage(errorMessage)
                    .setCancelable(false)
                    .setNeutralButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    disconnect();
                                }
                            })
                    .create()
                    .show();
        }
    }

    // Log |msg| and Toast about it.
    private void logAndToast(String msg) {
        Log.d(TAG, msg);
        if (logToast != null) {
            logToast.cancel();
        }
        logToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        logToast.show();
    }

    private void reportError(final String description) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isError) {
                    isError = true;
                    disconnectWithErrorMessage(description);
                }
            }
        });
    }

    // Create VideoCapturer
    private VideoCapturer createVideoCapturer() {
        final VideoCapturer videoCapturer;
        Logging.d(TAG, "Creating capturer using camera2 API.");
        videoCapturer = createCameraCapturer(new Camera2Enumerator(this));
        if (videoCapturer == null) {
            reportError("Failed to open camera");
            return null;
        }
        return videoCapturer;
    }

    // Create VideoCapturer from camera
    private VideoCapturer createCameraCapturer(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();

        // First, try to find front facing camera
        Logging.d(TAG, "Looking for front facing cameras.");
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                Logging.d(TAG, "Creating front facing camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        // Front facing camera not found, try something else
        Logging.d(TAG, "Looking for other cameras.");
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                Logging.d(TAG, "Creating other camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        return null;
    }

    private void setSwappedFeeds(boolean isSwappedFeeds) {

        Logging.d(TAG, "setSwappedFeeds: " + isSwappedFeeds);
        this.isSwappedFeeds = isSwappedFeeds;
        localProxyVideoSink.setTarget(isSwappedFeeds ? fullscreenRenderer : pipRenderer);
        remoteProxyRenderer.setTarget(isSwappedFeeds ? pipRenderer : fullscreenRenderer);
        fullscreenRenderer.setMirror(isSwappedFeeds);
        pipRenderer.setMirror(!isSwappedFeeds);

    }

    // -----Implementation of AppRTCClient.AppRTCSignalingEvents ---------------
    // All callbacks are invoked from websocket signaling looper thread and
    // are routed to UI thread.
    private void onConnectedToRoomInternal(final AppRTCClient.SignalingParameters params) {
        final long delta = System.currentTimeMillis() - callStartedTimeMs;

        signalingParameters = params;
        logAndToast("Creating peer connection, delay=" + delta + "ms");
        VideoCapturer videoCapturer = null;
        if (peerConnectionParameters.videoCallEnabled) {
            videoCapturer = createVideoCapturer();
        }
        peerConnectionClient.createPeerConnection(
                localProxyVideoSink, remoteRenderers, videoCapturer, signalingParameters);

        if (signalingParameters.initiator) {
            logAndToast("Creating OFFER...");
            // Create offer. Offer SDP will be sent to answering client in
            // PeerConnectionEvents.onLocalDescription event.
            peerConnectionClient.createOffer();
        } else {
            if (params.offerSdp != null) {
                peerConnectionClient.setRemoteDescription(params.offerSdp);
                logAndToast("Creating ANSWER...");
                // Create answer. Answer SDP will be sent to offering client in
                // PeerConnectionEvents.onLocalDescription event.
                peerConnectionClient.createAnswer();
            }
            if (params.iceCandidates != null) {
                // Add remote ICE candidates from room.
                for (IceCandidate iceCandidate : params.iceCandidates) {
                    peerConnectionClient.addRemoteIceCandidate(iceCandidate);
                }
            }
        }
    }

    @Override
    public void onConnectedToRoom(final AppRTCClient.SignalingParameters params) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onConnectedToRoomInternal(params);
            }
        });
    }

    @Override
    public void onRemoteDescription(final SessionDescription sdp) {
        final long delta = System.currentTimeMillis() - callStartedTimeMs;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (peerConnectionClient == null) {
                    Log.e(TAG, "Received remote SDP for non-initilized peer connection.");
                    return;
                }
                logAndToast("Received remote " + sdp.type + ", delay=" + delta + "ms");
                peerConnectionClient.setRemoteDescription(sdp);
                if (!signalingParameters.initiator) {
                    logAndToast("Creating ANSWER...");
                    // Create answer. Answer SDP will be sent to offering client in
                    // PeerConnectionEvents.onLocalDescription event.
                    peerConnectionClient.createAnswer();
                }
            }
        });
    }

    @Override
    public void onRemoteIceCandidate(final IceCandidate candidate) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (peerConnectionClient == null) {
                    Log.e(TAG, "Received ICE candidate for a non-initialized peer connection.");
                    return;
                }
                peerConnectionClient.addRemoteIceCandidate(candidate);
            }
        });
    }

    @Override
    public void onRemoteIceCandidatesRemoved(final IceCandidate[] candidates) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (peerConnectionClient == null) {
                    Log.e(TAG, "Received ICE candidate removals for a non-initialized peer connection.");
                    return;
                }
                peerConnectionClient.removeRemoteIceCandidates(candidates);
            }
        });
    }

    @Override
    public void onChannelClose() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logAndToast("Remote end hung up; dropping PeerConnection");
                disconnect();
            }
        });
    }

    @Override
    public void onChannelError(final String description) {
        reportError(description);
    }


    // -----Implementation of PeerConnectionClient.PeerConnectionEvents.---------
    // Send local peer connection SDP and ICE candidates to remote party.
    // All callbacks are invoked from peer connection client looper thread and
    // are routed to UI thread.
    @Override
    public void onLocalDescription(final SessionDescription sdp) {
        final long delta = System.currentTimeMillis() - callStartedTimeMs;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (appRtcClient != null) {
                    logAndToast("Sending " + sdp.type + ", delay=" + delta + "ms");
                    if (signalingParameters.initiator) {
                        appRtcClient.sendOfferSdp(sdp);
                    } else {
                        appRtcClient.sendAnswerSdp(sdp);
                    }
                }
                if (peerConnectionParameters.videoMaxBitrate > 0) {
                    Log.d(TAG, "Set video maximum bitrate: " + peerConnectionParameters.videoMaxBitrate);
                    peerConnectionClient.setVideoMaxBitrate(peerConnectionParameters.videoMaxBitrate);
                }
            }
        });
    }

    @Override
    public void onIceCandidate(final IceCandidate candidate) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (appRtcClient != null) {
                    appRtcClient.sendLocalIceCandidate(candidate);
                }
            }
        });
    }

    @Override
    public void onIceCandidatesRemoved(final IceCandidate[] candidates) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (appRtcClient != null) {
                    appRtcClient.sendLocalIceCandidateRemovals(candidates);
                }
            }
        });
    }

    @Override
    public void onIceConnected() {
        final long delta = System.currentTimeMillis() - callStartedTimeMs;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logAndToast("ICE connected, delay=" + delta + "ms");
                iceConnected = true;
                callConnected();
            }
        });
    }

    @Override
    public void onIceDisconnected() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logAndToast("ICE disconnected");
                iceConnected = false;
                disconnect();
            }
        });
    }

    @Override
    public void onPeerConnectionClosed() {
    }

    @Override
    public void onPeerConnectionStatsReady(final StatsReport[] reports) {
    }

    @Override
    public void onPeerConnectionError(final String description) {
        reportError(description);
    }

    // Activity interfaces
    @Override
    public void onStop() {
        super.onStop();
        activityRunning = false;
        if (peerConnectionClient != null) {
            peerConnectionClient.stopVideoSource();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        activityRunning = true;
        // Video is not paused for screencapture. See onPause.
        if (peerConnectionClient != null) {
            peerConnectionClient.startVideoSource();
        }
    }

    @Override
    protected void onDestroy() {
        Thread.setDefaultUncaughtExceptionHandler(null);
         disconnect();
        if (logToast != null) {
            logToast.cancel();
        }

        taskToTimer.cancel(true);
        activityRunning = false;
        super.onDestroy();
    }

    private static class ProxyRenderer implements VideoRenderer.Callbacks {
        private VideoRenderer.Callbacks target;

        @Override
        synchronized public void renderFrame(VideoRenderer.I420Frame frame) {
            if (target == null) {
                Logging.d(TAG, "Dropping frame in proxy because target is null.");
                VideoRenderer.renderFrameDone(frame);
                return;
            }

            target.renderFrame(frame);
        }

        synchronized public void setTarget(VideoRenderer.Callbacks target) {
            this.target = target;
        }
    }

    private static class ProxyVideoSink implements VideoSink {
        private VideoSink target;

        @Override
        synchronized public void onFrame(VideoFrame frame) {
            if (target == null) {
                Logging.d(TAG, "Dropping frame in proxy because target is null.");
                return;
            }

            target.onFrame(frame);
        }

        synchronized public void setTarget(VideoSink target) {
            this.target = target;
        }
    }


}
