package com.bokecc.vod.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bokecc.vod.R;
import com.bokecc.vod.adapter.SelectVideoAdapter;
import com.bokecc.vod.data.HuodeVideoInfo;
import com.bokecc.vod.inter.SelectVideo;

import java.util.ArrayList;
import java.util.List;

/**
 * SelectVideoDialog
 *
 * @author CC
 */
public class SelectVideoDialog extends Dialog {
    private final Context context;
    private final String currentVideoId;
    private final SelectVideo selectVideo;
    private final ArrayList<HuodeVideoInfo> videoList;

    public SelectVideoDialog(Context context, ArrayList<HuodeVideoInfo> videoList, String currentVideoId, SelectVideo selectVideo) {
        super(context, R.style.SetVideoDialog);
        this.context = context;
        this.currentVideoId = currentVideoId;
        this.selectVideo = selectVideo;
        this.videoList = videoList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_select_video, null);
        setContentView(view);
        ListView lvSelectVideo = view.findViewById(R.id.lv_select_video);
        List<HuodeVideoInfo> data = new ArrayList<>();
        for (int i = 0; i < videoList.size(); i++) {
            boolean isSelected;
            HuodeVideoInfo videoInfo = videoList.get(i);
            if (videoInfo != null) {
                isSelected = videoInfo.getVideoId().equals(currentVideoId);
                data.add(new HuodeVideoInfo(videoInfo.getVideoCover(), videoInfo.getVideoTitle(), videoInfo.getVideoId(), isSelected));
            }
        }
        final SelectVideoAdapter selectVideoAdapter = new SelectVideoAdapter(context, data);
        lvSelectVideo.setAdapter(selectVideoAdapter);
        lvSelectVideo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HuodeVideoInfo item = (HuodeVideoInfo) selectVideoAdapter.getItem(position);
                if (selectVideo != null && item != null) {
                    selectVideo.selectedVideo(item.getVideoTitle(), item.getVideoId(), item.getVideoCover());
                    dismiss();
                }
            }
        });
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics();
        lp.width = (int) (d.widthPixels * 0.35);
        lp.height = (int) (d.heightPixels * 1.0);
        dialogWindow.setAttributes(lp);
        dialogWindow.setGravity(Gravity.END);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            dialogWindow.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

}
