package com.bokecc.vod.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bokecc.sdk.mobile.download.DownloadOperator;
import com.bokecc.sdk.mobile.download.Downloader;
import com.bokecc.vod.R;
import com.bokecc.vod.utils.MultiUtils;

import java.util.List;

public class DownloadViewAdapter extends BaseAdapter {

    private final List<DownloadOperator> downloadInfo;

    private final Context context;

    public DownloadViewAdapter(Context context, List<DownloadOperator> downloadInfo) {
        this.context = context;
        this.downloadInfo = downloadInfo;
    }

    @Override
    public int getCount() {
        return downloadInfo.size();
    }

    @Override
    public Object getItem(int position) {
        return downloadInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DownloadOperator downloadOperator = downloadInfo.get(position);
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_downloading, null);
            TextView titleView = convertView.findViewById(R.id.download_title);
            TextView statusView = convertView.findViewById(R.id.download_status);
            TextView speedView = convertView.findViewById(R.id.download_speed);
            ImageView ivVideoCover = convertView.findViewById(R.id.iv_video_cover);
            TextView progressView = convertView.findViewById(R.id.download_progress);
            ProgressBar downloadProgressBar = convertView.findViewById(R.id.download_progressBar);
            downloadProgressBar.setMax(100);
            holder = new ViewHolder();
            holder.downloadProgressBar = downloadProgressBar;
            holder.progressView = progressView;
            holder.speedView = speedView;
            holder.statusView = statusView;
            holder.titleView = titleView;
            holder.ivVideoCover = ivVideoCover;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.titleView.setText(downloadOperator.getVodDownloadBean().getFileName());
        holder.statusView.setText(getStatusStr(downloadOperator.getStatus()));
        MultiUtils.showVideoCover(holder.ivVideoCover, downloadOperator.getVodDownloadBean().getVideoCover());
        if (downloadOperator.getStatus() == Downloader.DOWNLOAD) {
            holder.speedView.setText(downloadOperator.getSpeed(context));
        } else {
            holder.speedView.setText("");
        }
        holder.progressView.setText(downloadOperator.getDownloadProgressText(context));
        holder.downloadProgressBar.setProgress((int) downloadOperator.getDownloadProgressBarValue());

        return convertView;
    }

    private String getStatusStr(int status) {
        String statusStr = "";
        switch (status) {
            case Downloader.WAIT:
                statusStr = "等待中";
                break;
            case Downloader.DOWNLOAD:
                statusStr = "下载中";
                break;
            case Downloader.PAUSE:
                statusStr = "已暂停";
                break;
            case Downloader.FINISH:
                statusStr = "已完成";
                break;
            default:
                break;
        }

        return statusStr;
    }

    public static class ViewHolder {
        public TextView titleView;
        public ImageView ivVideoCover;
        public TextView statusView;
        public TextView speedView;
        public TextView progressView;
        public ProgressBar downloadProgressBar;
    }
}
