package com.bokecc.vod.utils;

import android.text.TextUtils;

import com.bokecc.sdk.mobile.core.Core;
import com.bokecc.sdk.mobile.util.SSLClient;
import com.bokecc.vod.data.DataSet;
import com.bokecc.vod.data.DownloadInfo;
import com.bokecc.vod.data.LogoInfo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * SaveLogoUtil
 *
 * @author CC
 */
public class SaveLogoUtil {
    private static final int BUFFER_SIZE = 1024 * 30;

    public static void saveLogo(final ArrayList<LogoInfo> logoInfoList) {
        if (logoInfoList != null && logoInfoList.size() > 0) {
            Core.getInstance().getExecutorSupplier()
                    .forBackgroundTasks()
                    .execute(new Runnable() {
                        @Override
                        public void run() {
                            for (LogoInfo logoInfo : logoInfoList) {
                                downloadLogo(logoInfo);
                            }
                        }
                    });
        }
    }

    private static void downloadLogo(LogoInfo logoInfo) {
        String fileName = logoInfo.getFileName();
        String logoUrl = logoInfo.getLogoUrl();
        if (TextUtils.isEmpty(fileName) || TextUtils.isEmpty(logoUrl)) {
            return;
        }
        String format = ".jpg";
        if (logoUrl.contains(".jpg")) {
            format = ".jpg";
        } else if (logoUrl.contains(".jpeg")) {
            format = ".jpeg";
        } else if (logoUrl.contains(".png")) {
            format = ".png";
        } else if (logoUrl.contains(".gif")) {
            format = ".gif";
        }
        String downloadPath = MultiUtils.createDownloadPath();
        String saveLogoPath = downloadPath + fileName + format;
        File file = new File(saveLogoPath);
        HttpURLConnection urlConnection = null;
        RandomAccessFile randomAccessFile = null;
        InputStream inputStream = null;
        boolean isEnd = false;
        long start = 0;
        long end;
        if (file != null) {
            long fileLength = file.length();
            if (fileLength >= 0L) {
                start = fileLength;
            }
        }
        try {
            URL url = new URL(logoUrl);
            urlConnection = SSLClient.getUrlConnection(logoUrl, url);
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            urlConnection.setRequestProperty("accept", "*/*");
            urlConnection.setRequestProperty("connection", "Keep-Alive");
            urlConnection.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
            urlConnection.setRequestProperty("Accept-Language", "zh-CN");
            if (start > 0) {
                urlConnection.setRequestProperty("Range", "bytes=" + start + "-");
            }
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            int responseCode = urlConnection.getResponseCode();
            if (responseCode == 416) {
                saveLogoPath(fileName, saveLogoPath);
                return;
            }
            if (responseCode >= 400) {
                return;
            }
            String contentLength = urlConnection.getHeaderField("Content-Length");
            end = start + Long.parseLong(contentLength);
            randomAccessFile = new RandomAccessFile(file, "rwd");
            randomAccessFile.seek(start);
            inputStream = urlConnection.getInputStream();
            byte[] buffer = new byte[BUFFER_SIZE];
            while (!isEnd) {
                int bufferCount = 0;
                while (bufferCount < BUFFER_SIZE) {
                    int offset = inputStream.read(buffer, bufferCount, BUFFER_SIZE - bufferCount);
                    if (offset == -1) {
                        break;
                    } else {
                        bufferCount = bufferCount + offset;
                    }
                }
                randomAccessFile.write(buffer, 0, bufferCount);
                // 更新下载起始节点
                start += bufferCount;
                // 读到文件尾时
                if (start >= end && start > 0 && end > 0) {
                    isEnd = true;
                    saveLogoPath(fileName, saveLogoPath);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private static void saveLogoPath(String fileName, String saveLogoPath) {
        if (TextUtils.isEmpty(fileName) || TextUtils.isEmpty(saveLogoPath)) {
            return;
        }
        if (DataSet.hasDownloadInfo(fileName)) {
            DownloadInfo downloadInfo = DataSet.getDownloadInfo(fileName);
            downloadInfo.setTitle(fileName);
            downloadInfo.setLogoPath(saveLogoPath);
            DataSet.updateDownloadInfo(downloadInfo);
        } else {
            DownloadInfo downloadInfo = new DownloadInfo();
            downloadInfo.setTitle(fileName);
            downloadInfo.setLogoPath(saveLogoPath);
            DataSet.addDownloadInfo(downloadInfo);
        }
    }

}
