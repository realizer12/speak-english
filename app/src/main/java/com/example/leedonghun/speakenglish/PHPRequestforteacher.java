package com.example.leedonghun.speakenglish;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * speakenglish
 * Class: PHPRequestforteacher.
 * Created by leedonghun.
 * Created On 2018-12-29.
 * Description:
 */
public class PHPRequestforteacher {
    private URL url;

    public PHPRequestforteacher(String url) throws MalformedURLException { this.url = new URL(url); }

    private String readStream(InputStream in) throws IOException {
        StringBuilder jsonHtml = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String line;

        while((line = reader.readLine()) != null)
            jsonHtml.append(line);

        reader.close();
        return jsonHtml.toString();
    }

    public String PhPtestforteacher(final String data1, final String data2, final String data3,final String data4,final String data5,final String data6 ) {//php로 보내려고 하는 데이터들 3개
        try {
            String postData = "Data1="+data1+"&"+"Data2="+data2+"&"+"Data3="+data3+"&"+"Data4="+data4+"&"+"Data5="+data5+"&"+"Data6="+data6;
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(5000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(postData.getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();
            String result = readStream(conn.getInputStream());
            conn.disconnect();
            return result;
        }
        catch (Exception e) {
            Log.i("PHPRequest", "request was failed.");
            return null;
        }
    }
}

