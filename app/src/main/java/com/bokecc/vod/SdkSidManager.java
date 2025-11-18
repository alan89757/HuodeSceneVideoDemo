package com.bokecc.vod;
import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import com.bokecc.sdk.mobile.play.SdkSidProvider;
import com.bokecc.sdk.mobile.util.SSLClient;
import com.bokecc.vod.data.SidInfo;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SdkSidManager {
    // TODO: 2023/2/16 该地址为测试地址 用户可自行修改为自己的sid获取接口
    private static final String TEST_SID = "https://spark.bokecc.com/demo/app-api/sdk-sid.bo?uid="+ConfigUtil.USER_ID;
    private static class InitializeHolder {
        @SuppressLint("StaticFieldLeak")
        public static SdkSidManager instance = new SdkSidManager();
    }

    private SdkSidManager() {
    }

    public static SdkSidManager getInstance() {
        return SdkSidManager.InitializeHolder.instance;
    }
    private SidInfo sid;
    private SidProvider sidProvider = new SidProvider();

    public SidProvider getSidProvider() {
        return sidProvider;
    }

    public class SidProvider extends SdkSidProvider{

        @Override
        public String getVerificationKey(String userId) {
            if (sid!=null&&sid.checkSid()){
                return sid.getSdkSid();
            }
            SidInfo sidInfo = updateSid();
            if (sidInfo!=null){
                return sidInfo.getSdkSid();
            }else{
                return null;
            }
        }
    }
    public SidInfo updateSid(){
        InputStream inputStream = null;
        BufferedReader br = null;
        OutputStream outputStream;
        HttpURLConnection connection = null;
        try {
            URL mUrl = new URL(TEST_SID);
            connection = SSLClient.getUrlConnection(TEST_SID, mUrl);
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("Connection", "keep-alive");
            connection.setRequestProperty("Content-Type", "text/plain; charset=utf-8");

            connection.setRequestMethod("GET");
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode >= 300) {
                Log.e("Error response code: ", responseCode + "");
                return null;
            }
            inputStream = connection.getInputStream();
            if (inputStream == null) {
                Log.e("NULL entity instream. ", TEST_SID);
                return null;
            }
            br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder responseContent = new StringBuilder();
            String tmp;
            while ((tmp = br.readLine()) != null) {
                responseContent.append(tmp).append("\r\n");
            }
            if (!TextUtils.isEmpty(responseContent.toString())) {
                responseContent = new StringBuilder(responseContent.substring(0, responseContent.lastIndexOf("\r\n")));
                sid = new SidInfo(new JSONObject(responseContent.toString()));
                return sid;
            }else{
                sid = null;
            }
            return sid;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
