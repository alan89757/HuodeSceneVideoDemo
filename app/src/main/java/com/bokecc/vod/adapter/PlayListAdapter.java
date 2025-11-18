package com.bokecc.vod.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bokecc.vod.R;
import com.bokecc.vod.data.HuodeVideoInfo;
import com.bokecc.vod.utils.MultiUtils;

import java.util.List;

public class PlayListAdapter extends BaseAdapter {
    private List<HuodeVideoInfo> datas;
    private final LayoutInflater layoutInflater;
    private final Context context;

    public PlayListAdapter(Context context, List<HuodeVideoInfo> datas) {
        this.context = context;
        this.datas = datas;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_play_list, null);
            holder = new ViewHolder();
            holder.ivVideoImg = (ImageView) convertView.findViewById(R.id.iv_video_img);
            holder.ivSelectButton = (ImageView) convertView.findViewById(R.id.iv_select_button);
            holder.tvVideoTitle = (TextView) convertView.findViewById(R.id.tv_video_title);
            holder.tvVideoTime = (TextView) convertView.findViewById(R.id.tv_video_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        HuodeVideoInfo videoInfo = datas.get(position);
        if (videoInfo != null) {
            MultiUtils.showCornerVideoCover(holder.ivVideoImg,videoInfo.getVideoCover());
            holder.tvVideoTitle.setText(videoInfo.getVideoTitle());
            holder.tvVideoTime.setText(videoInfo.getVideoTime());
            if (videoInfo.isShowSelectButton()){
                holder.ivSelectButton.setVisibility(View.VISIBLE);
            }else {
                holder.ivSelectButton.setVisibility(View.GONE);
            }
            if (videoInfo.isSelectedDownload()){
                holder.ivSelectButton.setImageResource(R.mipmap.iv_selected);
            }else {
                holder.ivSelectButton.setImageResource(R.mipmap.iv_unselected);
            }
            if (videoInfo.isSelected()){
                holder.tvVideoTitle.setTextColor(context.getResources().getColor(R.color.orange));
            }else {
                holder.tvVideoTitle.setTextColor(context.getResources().getColor(R.color.videoTitle));
            }
        }
        return convertView;
    }

    static class ViewHolder {
        ImageView ivVideoImg;
        ImageView ivSelectButton;
        TextView tvVideoTitle;
        TextView tvVideoTime;
    }
}
