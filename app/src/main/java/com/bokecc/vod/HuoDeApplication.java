package com.bokecc.vod;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.multidex.MultiDexApplication;
import com.bokecc.sdk.mobile.drm.DRMServer;

/**
 * HuodeApplication
 *
 * @author Zhang
 */
@SuppressLint("StaticFieldLeak")
public class HuoDeApplication extends MultiDexApplication {

    public static DRMServer drmServer;
    public static Context context;
    public static int drmServerPort;
    public static SharedPreferences sp;
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        sp = getSharedPreferences("AccountSettings", Context.MODE_PRIVATE);
    }

    public static Context getContext() {
        return context;
    }

    public static SharedPreferences getSp() {
        return sp;
    }

    /**
     * 启动DRMServer
     */


    @Override
    public void onTerminate() {
        if (drmServer != null) {
            drmServer.stop();
        }
        super.onTerminate();
    }

    public static int getDrmServerPort() {
        return drmServerPort;
    }

    public void setDrmServerPort(int drmServerPort) {
        HuoDeApplication.drmServerPort = drmServerPort;
    }

    public static DRMServer getDRMServer() {
        return drmServer;
    }
}
