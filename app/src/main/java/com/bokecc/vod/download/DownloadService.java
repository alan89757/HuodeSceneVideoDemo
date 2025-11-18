package com.bokecc.vod.download;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.bokecc.sdk.mobile.download.VodDownloadBean;
import com.bokecc.sdk.mobile.download.VodDownloadManager;
import com.bokecc.vod.data.DataSet;
import com.bokecc.vod.data.DownloadInfo;
import com.bokecc.vod.utils.MultiUtils;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * DownloadService，用于支持后台下载
 *
 * @author CC
 */
public class DownloadService extends Service {
    private NetChangedReceiver netReceiver;
    /**
     * 是否使用移动网络下载，可根据实际自行设置
     */
    private final boolean isUseMobileNetwork = true;
    private boolean isPaused = false;
    Timer timer ;
    private TimerTask task;

    @Override
    public void onCreate() {
        super.onCreate();
        if (netReceiver == null) {
            netReceiver = new NetChangedReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netReceiver, filter);

        task = new TimerTask() {
            @Override
            public void run() {
                if (isUseMobileNetwork) {
                    VodDownloadManager.getInstance().update();
                } else {
                    int netWorkStatus = MultiUtils.getNetWorkStatus(DownloadService.this);
                    //网络状态 0：无网络 1：WIFI 2：移动网络
                    if (netWorkStatus == 1) {
                        VodDownloadManager.getInstance().update();
                        isPaused = false;
                    } else {
                        if (!isPaused) {
                            VodDownloadManager.getInstance().pauseAllDownload();
                            isPaused = true;
                        }
                    }
                }

            }
        };
        timer = new Timer();
        timer.schedule(task, 1 * 1000, 1 * 1000);

        //监听下载完成的文件
        VodDownloadManager.getInstance().setOnDownloadFinishListener(new VodDownloadManager.OnDownloadFinishListener() {
            @Override
            public void onDownloadFinish(final VodDownloadBean vodDownloadBean) {
                String fileName = vodDownloadBean.getFileName();
                if (DataSet.hasDownloadInfo(fileName)) {
                    DownloadInfo downloadInfo = DataSet.getDownloadInfo(fileName);
                    saveDownloadInfo(vodDownloadBean, downloadInfo);
                    DataSet.updateDownloadInfo(downloadInfo);
                    if (onUpdateDownloadedView != null) {
                        onUpdateDownloadedView.updateDownloadedView(downloadInfo);
                    }
                } else {
                    DownloadInfo downloadInfo = new DownloadInfo();
                    saveDownloadInfo(vodDownloadBean, downloadInfo);
                    DataSet.addDownloadInfo(downloadInfo);
                    if (onUpdateDownloadedView != null) {
                        onUpdateDownloadedView.updateDownloadedView(downloadInfo);
                    }
                }
            }
        });
    }

    private void saveDownloadInfo(VodDownloadBean vodDownloadBean, DownloadInfo downloadInfo) {
        downloadInfo.setVideoId(vodDownloadBean.getVideoId());
        downloadInfo.setTitle(vodDownloadBean.getFileName());
        downloadInfo.setFormat(vodDownloadBean.getFormat());
        downloadInfo.setDownloadMode(vodDownloadBean.getDownloadMode());
        downloadInfo.setVideoCover(vodDownloadBean.getVideoCover());
        downloadInfo.setEnd(vodDownloadBean.getEnd());
        downloadInfo.setStatus(vodDownloadBean.getStatus());
        downloadInfo.setCreateTime(new Date());
        downloadInfo.setDefinition(vodDownloadBean.getDefinition());
        downloadInfo.setFirstSubtitleStatus(vodDownloadBean.getFirstSubtitleStatus());
        downloadInfo.setSecondSubtitleStatus(vodDownloadBean.getSecondSubtitleStatus());
        downloadInfo.setSubtitleNum(vodDownloadBean.getSubtitleNum());
        downloadInfo.setSubtitleModel(vodDownloadBean.getSubtitleModel());
        downloadInfo.setMarqueeData(vodDownloadBean.getMarqueeData());
        downloadInfo.setInvisibleMarquee(vodDownloadBean.getIsInvisibleMarquee()==1?true:false);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (task!=null){
            task.cancel();
            task = null;
        }
        if (timer!=null){
            timer.cancel();
            timer = null;
        }
        VodDownloadManager.getInstance().pauseAllDownload();
        if (netReceiver != null) {
            unregisterReceiver(netReceiver);
        }
        super.onDestroy();
    }

    public interface OnUpdateDownloadedView {
        /**
         * updateDownloadedView
         *
         * @param downloadInfo downloadInfo
         */
        void updateDownloadedView(DownloadInfo downloadInfo);
    }

    private static OnUpdateDownloadedView onUpdateDownloadedView;

    public static void setOnUpdateDownloadedView(OnUpdateDownloadedView updateDownloadedView) {
        onUpdateDownloadedView = updateDownloadedView;
    }

    static class NetChangedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                NetworkInfo dataInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                if (wifiInfo.isConnected() && dataInfo.isConnected()) {
                    //wifi和移动数据同时连接
                    VodDownloadManager.getInstance().resumeAllDownload();
                } else if (wifiInfo.isConnected() && !dataInfo.isConnected()) {
                    //wifi已连接，移动数据断开，恢复下载
                    VodDownloadManager.getInstance().resumeAllDownload();
                } else if (!wifiInfo.isConnected() && dataInfo.isConnected()) {
                    //wifi断开 移动数据连接
                } else {
                    //wifi断开 移动数据断开
                }
            } else {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                Network[] networks = connectivityManager.getAllNetworks();
                int nets = 0;
                for (Network network : networks) {
                    NetworkInfo netInfo = connectivityManager.getNetworkInfo(network);
                    if (netInfo.getType() == ConnectivityManager.TYPE_MOBILE && !netInfo.isConnected()) {
                        nets += 1;
                    }
                    if (netInfo.getType() == ConnectivityManager.TYPE_MOBILE && netInfo.isConnected()) {
                        nets += 2;
                    }
                    if (netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        nets += 4;
                    }
                }
                switch (nets) {
                    case 0:
                        //wifi断开 移动数据断开
                        break;
                    case 2:
                        //wifi断开 移动数据连接
                        break;
                    case 4:
                        //wifi已连接，移动数据断开，恢复下载
                    case 5:
                        //wifi和移动数据同时连接
                        VodDownloadManager.getInstance().resumeAllDownload();
                        break;
                    default:
                        break;
                }
            }
        }
    }

}
