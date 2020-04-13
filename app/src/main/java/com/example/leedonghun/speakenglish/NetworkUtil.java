package com.example.leedonghun.speakenglish;

/**
 * speakenglish
 * Class: NetworkUtil.
 * Created by leedonghun.
 * Created On 2018-12-23.
 * Description:
 */
import android.annotation.SuppressLint;
import android.os.StrictMode;
public class NetworkUtil {
    @SuppressLint("NewApi")
    static public void setNetworkPolicy() {
        //이부분  httpurlconnection  api 9이상부터 이지만,  현재 이앱은  최소 sdk level이  16이므로,  16이하로 버전  조건을 써주면 warning으로  쓰잘데기 없는 행옫이라고 말해줌.
        if (android.os.Build.VERSION.SDK_INT > 16) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }
}


