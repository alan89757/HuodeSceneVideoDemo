package com.bokecc.vod.download;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.bokecc.sdk.mobile.download.DownloadOperator;
import com.bokecc.sdk.mobile.download.VodDownloadManager;
import com.bokecc.vod.R;
import com.bokecc.vod.adapter.DownloadViewAdapter;
import com.bokecc.vod.data.DataSet;
import com.bokecc.vod.data.DownloadInfo;
import com.bokecc.vod.inter.DeleteFile;
import com.bokecc.vod.view.DeleteFileDialog;

import java.io.File;
import java.util.List;

/**
 * 下载中标签页
 *
 * @author 获得场景视频
 */
public class DownloadingFragment extends Fragment {

    private FragmentActivity activity;
    private Button btn_all_pause_or_start;
    private ListView lv_download;
    private List<DownloadOperator> downloadingInfos;
    private DownloadViewAdapter downloadAdapter;
    private DeleteFileDialog deleteFileDialog;
    private boolean isAllPause = false;
    private int downloadCount = 0;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = getActivity();
        View view = inflater.inflate(R.layout.fragment_downloading, null);
        lv_download = view.findViewById(R.id.lv_download);
        btn_all_pause_or_start = view.findViewById(R.id.btn_all_pause_or_start);
        initData();

        lv_download.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DownloadOperator downloadOperator = (DownloadOperator) downloadAdapter.getItem(position);
                VodDownloadManager.getInstance().resumeOrPauseDownload(downloadOperator);
                updateListView();
                initAllPause();
            }
        });

        lv_download.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                deleteFileDialog = new DeleteFileDialog(activity, new DeleteFile() {
                    @Override
                    public void deleteFile() {
                        DownloadOperator downloadOperator = (DownloadOperator) downloadAdapter.getItem(position);
                        VodDownloadManager.getInstance().deleteDownloadInfo(downloadOperator);

                        String fileName = downloadOperator.getVodDownloadBean().getFileName();
                        if (DataSet.hasDownloadInfo(fileName)) {
                            DownloadInfo downloadInfo = DataSet.getDownloadInfo(fileName);
                            DataSet.removeDownloadInfo(downloadInfo);
                            String logoPath = downloadInfo.getLogoPath();
                            if (!TextUtils.isEmpty(logoPath)) {
                                File logoFile = new File(logoPath);
                                if (logoFile.exists()) {
                                    logoFile.delete();
                                }
                            }
                        }
                        updateListView();
                        initAllPause();
                    }
                });
                deleteFileDialog.show();
                return true;
            }
        });

        initAllPause();

        btn_all_pause_or_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAllPause) {
                    VodDownloadManager.getInstance().resumeAllDownload();
                } else {
                    VodDownloadManager.getInstance().pauseAllDownload();
                }
                updateListView();
                if (isAllPause) {
                    isAllPause = false;
                    btn_all_pause_or_start.setText("全部暂停");
                } else {
                    isAllPause = true;
                    btn_all_pause_or_start.setText("全部开始");
                }
            }
        });
        VodDownloadManager.getInstance().setOnUpdateDownload(new VodDownloadManager.OnUpdateDownload() {
            @Override
            public void updateDownload() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initAllPause();
                        updateListView();
                        //为防止出现删除提示框展示的时候，新的下载视频完成，导致删除错误的bug，故当有新的下载完成时，取消删除对话框
                        int currentDownloadCount = VodDownloadManager.getInstance().getDownloadOperators().size();
                        if (currentDownloadCount < downloadCount) {
                            downloadCount = currentDownloadCount;
                            if (deleteFileDialog != null && deleteFileDialog.isShowing()) {
                                deleteFileDialog.dismiss();
                            }
                        }
                    }
                });

            }
        });
        return view;
    }

    private void initAllPause() {
        if (VodDownloadManager.getInstance().getDownloadingCount() > 0) {
            isAllPause = false;
            btn_all_pause_or_start.setText("全部暂停");
            btn_all_pause_or_start.setVisibility(View.VISIBLE);
        } else if (VodDownloadManager.getInstance().getPauseAndWaitCount() > 0) {
            isAllPause = true;
            btn_all_pause_or_start.setText("全部开始");
        } else {
            isAllPause = true;
            btn_all_pause_or_start.setVisibility(View.GONE);
        }
    }


    private void initData() {
        downloadingInfos = VodDownloadManager.getInstance().getDownloadOperators();
        downloadAdapter = new DownloadViewAdapter(activity, downloadingInfos);
        lv_download.setAdapter(downloadAdapter);
        downloadAdapter.notifyDataSetChanged();
    }

    private void updateListView() {
        downloadAdapter.notifyDataSetChanged();
        lv_download.invalidate();
    }

    @Override
    public void onResume() {
        super.onResume();
        downloadCount = VodDownloadManager.getInstance().getDownloadOperators().size();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        VodDownloadManager.getInstance().setOnUpdateDownload(null);
    }

}