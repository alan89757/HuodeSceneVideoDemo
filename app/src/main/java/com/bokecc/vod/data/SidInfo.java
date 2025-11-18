package com.bokecc.vod.data;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

public class SidInfo {
    //sdk
    private String sdkSid;
    //有效期相对时间
    private long expireSeconds;
    private long startTime;
    public SidInfo(JSONObject jsonObject){
        sdkSid = jsonObject.optString("sdkSid");
        expireSeconds = jsonObject.optLong("expireSeconds")*1000;
        startTime = System.currentTimeMillis();
    }

    public String getSdkSid() {
        return sdkSid;
    }

    public boolean checkSid(){
        if (System.currentTimeMillis()-startTime-expireSeconds>=0
        || TextUtils.isEmpty(sdkSid)){
            return false;
        }
        return true;
    }
}
