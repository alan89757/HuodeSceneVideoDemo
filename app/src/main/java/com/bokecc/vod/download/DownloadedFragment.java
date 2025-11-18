package com.bokecc.vod.download;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.bokecc.vod.ConfigUtil;
import com.bokecc.vod.R;
import com.bokecc.vod.adapter.DownloadedViewAdapter;
import com.bokecc.vod.data.DataSet;
import com.bokecc.vod.data.DownloadInfo;
import com.bokecc.vod.inter.DeleteFile;
import com.bokecc.vod.inter.SelectPlayer;
import com.bokecc.vod.play.MediaPlayActivity;
import com.bokecc.vod.play.SpeedPlayActivity;
import com.bokecc.vod.view.DeleteFileDialog;
import com.bokecc.vod.view.SelectPlayerDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 已下载视频
 */
public class DownloadedFragment extends Fragment implements DownloadService.OnUpdateDownloadedView {

    private ListView lvDownloaded;
    private final List<DownloadInfo> downloadedInfoArray = new ArrayList<>();
    private DownloadedViewAdapter videoListViewAdapter;
    private FragmentActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = getActivity();
        View view = inflater.inflate(R.layout.fragment_download, null);
        lvDownloaded = view.findViewById(R.id.lv_download);
        initData();
        lvDownloaded.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final DownloadInfo downloadInfo = (DownloadInfo) videoListViewAdapter.getItem(position);
                if (downloadInfo == null) {
                    return;
                }
                SelectPlayerDialog selectPlayerDialog = new SelectPlayerDialog(activity, new SelectPlayer() {
                    @Override
                    public void selectDWIjkMediaPlayer() {
                        Intent intent = new Intent(activity, SpeedPlayActivity.class);
                        intent.putExtra("videoId", downloadInfo.getVideoId());
                        intent.putExtra("isLocalPlay", true);
                        intent.putExtra("videoTitle", downloadInfo.getTitle());
                        intent.putExtra("format", downloadInfo.getFormat());
                        intent.putExtra("logoPath", downloadInfo.getLogoPath());
                        intent.putExtra("marqueeData", downloadInfo.getMarqueeData());
                        intent.putExtra("isInvisibleMarquee", downloadInfo.isInvisibleMarquee());
                        startActivity(intent);
                    }

                    @Override
                    public void selectDWMediaPlayer() {
                        Intent intent = new Intent(activity, MediaPlayActivity.class);
                        intent.putExtra("videoId", downloadInfo.getVideoId());
                        intent.putExtra("isLocalPlay", true);
                        intent.putExtra("videoTitle", downloadInfo.getTitle());
                        intent.putExtra("format", downloadInfo.getFormat());
                        intent.putExtra("logoPath", downloadInfo.getLogoPath());
                        intent.putExtra("marqueeData", downloadInfo.getMarqueeData());
                        intent.putExtra("isInvisibleMarquee", downloadInfo.isInvisibleMarquee());
                        startActivity(intent);
                    }
                });
                selectPlayerDialog.show();
            }
        });

        lvDownloaded.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long id) {
                DeleteFileDialog deleteFileDialog = new DeleteFileDialog(activity, new DeleteFile() {
                    @Override
                    public void deleteFile() {
                        DownloadInfo downloadInfo = (DownloadInfo) videoListViewAdapter.getItem(position);
                        DataSet.removeDownloadInfo(downloadInfo);
                        downloadedInfoArray.remove(position);
                        File file = new File(Environment.getExternalStorageDirectory() + "/" + ConfigUtil.DOWNLOAD_PATH, downloadInfo.getTitle() + downloadInfo.getFormat());
                        if (file.exists()) {
                            file.delete();
                        }
                        File subtitle = new File(Environment.getExternalStorageDirectory() + "/" + ConfigUtil.DOWNLOAD_PATH, downloadInfo.getTitle() + "subtitle.srt");
                        if (subtitle.exists()) {
                            subtitle.delete();
                        }
                        File subtitle2 = new File(Environment.getExternalStorageDirectory() + "/" + ConfigUtil.DOWNLOAD_PATH, downloadInfo.getTitle() + "subtitle2.srt");
                        if (subtitle2.exists()) {
                            subtitle2.delete();
                        }
                        File subtitleSet = new File(Environment.getExternalStorageDirectory() + "/" + ConfigUtil.DOWNLOAD_PATH, downloadInfo.getTitle() + "subtitleSet.json");
                        if (subtitleSet.exists()) {
                            subtitleSet.delete();
                        }
                        String logoPath = downloadInfo.getLogoPath();
                        if (!TextUtils.isEmpty(logoPath)) {
                            File logoFile = new File(logoPath);
                            if (logoFile.exists()) {
                                logoFile.delete();
                            }
                        }
                        updateListView();
                    }
                });
                deleteFileDialog.show();
                return true;
            }
        });
        return view;
    }

    private void initData() {
        List<DownloadInfo> downloadInfoList = DataSet.getDownloadInfos();
        for (DownloadInfo downloadInfo : downloadInfoList) {
            if (TextUtils.isEmpty(downloadInfo.getVideoId())) {
                downloadInfoList.remove(downloadInfo);
            }
        }
        downloadedInfoArray.addAll(downloadInfoList);
        videoListViewAdapter = new DownloadedViewAdapter(activity, downloadedInfoArray);
        lvDownloaded.setAdapter(videoListViewAdapter);
    }

    private void updateListView() {
        videoListViewAdapter.notifyDataSetChanged();
        lvDownloaded.invalidate();
    }

    @Override
    public void updateDownloadedView(DownloadInfo downloadInfo) {
        downloadedInfoArray.add(downloadInfo);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (lvDownloaded != null && videoListViewAdapter != null) {
                    updateListView();
                }
            }
        });
    }

    @Override
    public void onResume() {
        DownloadService.setOnUpdateDownloadedView(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        DownloadService.setOnUpdateDownloadedView(null);
        super.onPause();
    }

}