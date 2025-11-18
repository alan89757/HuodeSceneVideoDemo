package com.bokecc.vod.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bokecc.vod.R;
import com.bokecc.vod.data.DownloadInfo;
import com.bokecc.vod.utils.MultiUtils;

import java.util.List;


/**
 * DownloadedViewAdapter
 *
 * @author CC
 */
public class DownloadedViewAdapter extends BaseAdapter {

    private final List<DownloadInfo> downloadInfoArray;

    private final Context context;

    public DownloadedViewAdapter(Context context, List<DownloadInfo> downloadInfoArray) {
        this.context = context;
        this.downloadInfoArray = downloadInfoArray;
    }

    @Override
    public int getCount() {
        return downloadInfoArray.size();
    }

    @Override
    public Object getItem(int position) {
        return downloadInfoArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DownloadInfo downloadInfo = downloadInfoArray.get(position);
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_downloaded_video, null);
            holder.tvFileSize = convertView.findViewById(R.id.tv_filesize);
            holder.titleView = convertView.findViewById(R.id.downloaded_title);
            holder.ivVideoCover = convertView.findViewById(R.id.iv_video_cover);
            holder.tvSubtitleState = convertView.findViewById(R.id.tv_subtitle_state);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.titleView.setText(downloadInfo.getTitle());
        long end = downloadInfo.getEnd();
        if (end > 0) {
            holder.tvFileSize.setVisibility(View.VISIBLE);
            holder.tvFileSize.setText(Formatter.formatFileSize(context, downloadInfo.getEnd()));
        } else {
            holder.tvFileSize.setVisibility(View.INVISIBLE);
        }
        MultiUtils.showVideoCover(holder.ivVideoCover, downloadInfo.getVideoCover());
        int firstSubtitleStatus = downloadInfo.getFirstSubtitleStatus();
        if (firstSubtitleStatus == 2) {
            holder.tvSubtitleState.setVisibility(View.VISIBLE);
            holder.tvSubtitleState.setText("下载字幕失败");
        } else {
            holder.tvSubtitleState.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    public static class ViewHolder {
        public TextView titleView;
        public TextView tvFileSize;
        public TextView tvSubtitleState;
        public ImageView ivVideoCover;
    }
}
