package com.bokecc.vod;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bokecc.common.application.ApplicationData;
import com.bokecc.sdk.mobile.core.Core;
import com.bokecc.sdk.mobile.download.VodDownloadManager;
import com.bokecc.sdk.mobile.drm.DRMServer;
import com.bokecc.sdk.mobile.play.InitializeManager;
import com.bokecc.sdk.mobile.play.VerificationMode;
import com.bokecc.sdk.mobile.util.DWSdkStorage;
import com.bokecc.sdk.mobile.util.DWStorageUtil;
import com.bokecc.vod.adapter.PlayVideoAdapter;
import com.bokecc.vod.data.DataSet;
import com.bokecc.vod.data.DataUtil;
import com.bokecc.vod.data.HuodeVideoInfo;
import com.bokecc.vod.data.ObjectBox;
import com.bokecc.vod.download.DownloadListActivity;
import com.bokecc.vod.download.DownloadService;
import com.bokecc.vod.inter.SelectPlayer;
import com.bokecc.vod.play.MediaPlayActivity;
import com.bokecc.vod.play.SpeedPlayActivity;
import com.bokecc.vod.upload.UploadController;
import com.bokecc.vod.upload.UploadManageActivity;
import com.bokecc.vod.upload.UploadService;
import com.bokecc.vod.utils.MultiUtils;
import com.bokecc.vod.utils.SPUtil;
import com.bokecc.vod.view.HeadGridView;
import com.bokecc.vod.view.PrivacyPolicyDialog;
import com.bokecc.vod.view.RequestExternalStorageDialog;
import com.bokecc.vod.view.SelectPlayerDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * MainActivity
 *
 * @author CC
 */
public class MainActivity extends AppCompatActivity {
    private PlayVideoAdapter playVideoAdapter;
    private ImageView ivMainImg;
    private LinearLayout llRetry;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private ArrayList<HuodeVideoInfo> videoDatas;
    private boolean showKnowledge;
    public static final String IS_READ_POLICY = "is_read_vod_policy";
    private PrivacyPolicyDialog privacyPolicyDialog;
    private SPUtil spUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MultiUtils.setStatusBarColor(this, R.color.transparent, true);
        initView();
        spUtil = new SPUtil(this);
        if (!spUtil.getBoolean(IS_READ_POLICY)) {
            if (privacyPolicyDialog == null) {
                privacyPolicyDialog = new PrivacyPolicyDialog(this).setOnClickAgree(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        spUtil.put(IS_READ_POLICY, true);
                        privacyPolicyDialog.dismiss();
                        initSDK();
                    }
                }).setOnClickIgnore(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        privacyPolicyDialog.dismiss();
                        finish();
                    }
                });

                String policy = "感谢您选择云点播！\n" +
                        "我们非常看重您的个人信息和隐私保护。为了更好的保护您的个人权益，在您使用我们产品前，请务必审慎阅读《隐私协议》、《服务协议》、《第三方共享》、《个人信息手机清单》所有条款。\n" +
                        "如您对以上协议有任何疑问，可发送邮件到sdk@bokecc.com或通过官方反馈后台反馈。您点击“同意”的行为即表示您已阅读完毕并同意以上协议的全部内容。\n" +
                        "请在同意隐私协议政策后再申请获取用户个人信息及权限。\n" +
                        "相关法律规定请参考《网络安全法》及《关于开展APP侵害用户权益专项整改工作的通知》。";
                int startIndex1 = policy.indexOf("《");
                int endIndex1 = policy.indexOf("》");
                SpannableString spannableString = new SpannableString(policy);
                spannableString.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, PolicyActivity.class);
                        intent.putExtra("url","https://admin.bokecc.com/privacy.bo");
                        startActivity(intent);
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        ds.setUnderlineText(false);
                    }
                }, startIndex1+1, endIndex1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#ff842f")), startIndex1 + 1, endIndex1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                int startIndex2 = policy.indexOf("《",endIndex1);
                int endIndex2 = policy.indexOf("》",endIndex1+1);
                spannableString.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, PolicyActivity.class);
                        intent.putExtra("url","https://admin.bokecc.com/agreement.bo");
                        startActivity(intent);
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        ds.setUnderlineText(false);
                    }
                }, startIndex2+1,endIndex2+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#ff842f")), startIndex2 + 1, endIndex2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                int startIndex3 = policy.indexOf("《",endIndex2);
                int endIndex3 = policy.indexOf("》",endIndex2+1);
                spannableString.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, PolicyActivity.class);
                        intent.putExtra("url","https://admin.bokecc.com/shareinfo.bo");
                        startActivity(intent);
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        ds.setUnderlineText(false);
                    }
                }, startIndex3+1,endIndex3+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#ff842f")), startIndex3 + 1, endIndex3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


                int startIndex4 = policy.indexOf("《",endIndex3);
                int endIndex4 = policy.indexOf("》",endIndex3+1);
                spannableString.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, PolicyActivity.class);
                        intent.putExtra("url","https://admin.bokecc.com/collectinfo.bo");
                        startActivity(intent);
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        ds.setUnderlineText(false);
                    }
                }, startIndex4+1,endIndex4+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#ff842f")), startIndex4 + 1, endIndex4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                privacyPolicyDialog.setPolicyTipString(spannableString);
            }
            privacyPolicyDialog.showBottom();
        } else {
            initSDK();
        }

    }
    public void initSDK(){
        //初始化本地数据库
        ObjectBox.init(this.getApplication());
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                &&ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            initExternalStorage();
        }
        initDWStorage();
        startDRMServer();
        if (BuildConfig.VerificationVersion == 0){
            InitializeManager.getInstance(this).initialize(true, VerificationMode.ORDINARY);
        }else if (BuildConfig.VerificationVersion == 1){
            InitializeManager.getInstance(this).initialize(true, VerificationMode.SID);
        }
        ApplicationData.isDebug(BuildConfig.DEBUG);
        //初始化数据库和下载数据 没有开通授权播放和下载功能的账号 verificationCode可为空值
        String verificationCode = MultiUtils.getVerificationCode();
        //初始化上传数据库
        UploadController.init();
        //请求数据
        initData();
    }
    public void initExternalStorage(){
        //初始化VodDownloadManager
        String downloadPath = MultiUtils.createDownloadPath();
        //使用VodDownloadManager需要以单例VodDownloadManager.getInstance()的形式调用
        if (BuildConfig.VerificationVersion == 0){
            VodDownloadManager.getInstance().init(HuoDeApplication.getContext(), ConfigUtil.USER_ID, ConfigUtil.API_KEY, downloadPath);
        }else if (BuildConfig.VerificationVersion == 1){
            VodDownloadManager.getInstance().init(HuoDeApplication.getContext(), ConfigUtil.USER_ID, downloadPath,SdkSidManager.getInstance().getSidProvider());
        }

        //启动下载service
        Intent intent = new Intent(this, DownloadService.class);
        startService(intent);
        //启动上传service
        Intent uploadIntent = new Intent(this, UploadService.class);
        startService(uploadIntent);
    }
    public void startDRMServer() {
        if (HuoDeApplication.drmServer == null) {
            HuoDeApplication.drmServer = new DRMServer();
            HuoDeApplication.drmServer.setRequestRetryCount(20);
        }
        try {
            HuoDeApplication.drmServer.start();
            HuoDeApplication.drmServerPort = HuoDeApplication.drmServer.getPort();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "启动解密服务失败，请检查网络限制情况:" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void initDWStorage() {
        DWSdkStorage myStorage = new DWSdkStorage() {
            private final SharedPreferences sp = getApplicationContext().getSharedPreferences("mystorage", MODE_PRIVATE);

            @Override
            public void put(String key, String value) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(key, value);
                editor.commit();
            }

            @Override
            public String get(String key) {
                return sp.getString(key, "");
            }
        };
        DWStorageUtil.setDWSdkStorage(myStorage);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            initExternalStorage();
        }
    }

    private RequestExternalStorageDialog requestExternalStorageDialog;
    private void initView() {
        HeadGridView gvVideoList = findViewById(R.id.gv_video_list);
        ImageView ivAccountInfo = findViewById(R.id.iv_account_info);
        ImageView ivUpload = findViewById(R.id.iv_upload);
        ImageView ivDownload = findViewById(R.id.iv_download);
        llRetry = findViewById(R.id.ll_retry);
        Button btnRetry = findViewById(R.id.btn_retry);
        View headView = LayoutInflater.from(MainActivity.this).inflate(R.layout.headview_main, null);
        ivMainImg = headView.findViewById(R.id.iv_main_img);
        gvVideoList.addHeaderView(headView);
        videoDatas = new ArrayList<>();
        if (!BuildConfig.isDefault){
            videoDatas = DataUtil.getVideoList();
        }
        playVideoAdapter = new PlayVideoAdapter(MainActivity.this, videoDatas);
        gvVideoList.setAdapter(playVideoAdapter);
        ivMainImg.setImageResource(R.mipmap.iv_default_img);
        gvVideoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (checkExternalStoragePermission()){
                    showKnowledge = position == 0;
                    final HuodeVideoInfo item = (HuodeVideoInfo) playVideoAdapter.getItem(position);
                    selectPlayer(item);
                }else{
                    showExternalStoragePermissionDialog();
                }
            }
        });
        ivMainImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoDatas != null && videoDatas.size() > 0) {
                    if (checkExternalStoragePermission()){
                        showKnowledge = true;
                        HuodeVideoInfo huodeVideoInfo = videoDatas.get(0);
                        selectPlayer(huodeVideoInfo);
                    }else{
                        showExternalStoragePermissionDialog();
                    }
                }
            }
        });
        ivAccountInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AccountInfoActivity.class));
            }
        });
        ivDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkExternalStoragePermission()){
                    startActivity(new Intent(MainActivity.this, DownloadListActivity.class));
                }else{
                    showExternalStoragePermissionDialog();
                }
            }
        });
        ivUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkExternalStoragePermission()){
                    startActivity(new Intent(MainActivity.this, UploadManageActivity.class));
                }else{
                    showExternalStoragePermissionDialog();
                }
            }
        });
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llRetry.setVisibility(View.GONE);
                initData();
            }
        });
    }
    private boolean checkExternalStoragePermission(){
        return ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                &&ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
    private void showExternalStoragePermissionDialog(){
        requestExternalStorageDialog = new RequestExternalStorageDialog(MainActivity.this).setOnClickAgree(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestExternalStorageDialog.dismiss();
                ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_STORAGE, 1);
            }
        }).setOnClickIgnore(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestExternalStorageDialog.dismiss();
            }
        });
        requestExternalStorageDialog.setContent("提示：\n \u3000\u3000该权限将用于“Gif”“截屏”功能生成的图片、视频打点信息、下载的视频存储到本地；上传功能获取本地文件进行上传等场景；");
        requestExternalStorageDialog.showBottom();
    }

    private void selectPlayer(final HuodeVideoInfo item) {
        String videoId = item.getVideoId();
        if (TextUtils.isEmpty(videoId)) {
            return;
        }
        SelectPlayerDialog selectPlayerDialog = new SelectPlayerDialog(MainActivity.this, new SelectPlayer() {
            @Override
            public void selectDWIjkMediaPlayer() {
                closeSmallWindow();
                Intent playIntent = new Intent(MainActivity.this, SpeedPlayActivity.class);
                playIntent.putExtra("videoId", item.getVideoId());
                playIntent.putExtra("videoTitle", item.getVideoTitle());
                playIntent.putExtra("videoCover", item.getVideoCover());
                playIntent.putExtra("showKnowledge", showKnowledge);
                playIntent.putParcelableArrayListExtra("videoDatas", videoDatas);
                startActivity(playIntent);
            }

            @Override
            public void selectDWMediaPlayer() {
                closeSmallWindow();
                Intent playIntent = new Intent(MainActivity.this, MediaPlayActivity.class);
                playIntent.putExtra("videoId", item.getVideoId());
                playIntent.putExtra("videoTitle", item.getVideoTitle());
                playIntent.putExtra("videoCover", item.getVideoCover());
                playIntent.putExtra("showKnowledge", showKnowledge);
                playIntent.putParcelableArrayListExtra("videoDatas", videoDatas);
                startActivity(playIntent);
            }
        });
        selectPlayerDialog.show();
    }


    private void initData() {
        //TODO 配置自己的视频时取消这行注释
        if (BuildConfig.isDefault){
            Core.getInstance().getExecutorSupplier()
                    .forBackgroundTasks()
                    .execute(new Runnable() {
                        @Override
                        public void run() {
                            request();
                        }
                    });
        }
    }

    private void request() {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(ConfigUtil.DATA_URL);
            connection = (HttpURLConnection) url.openConnection();
            //设置请求方法
            connection.setRequestMethod("GET");
            //设置连接超时时间（毫秒）
            connection.setConnectTimeout(5000);
            //设置读取超时时间（毫秒）
            connection.setReadTimeout(5000);
            //返回输入流
            InputStream in = connection.getInputStream();
            //读取输入流
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String result = sb.toString();
            if (!TextUtils.isEmpty(result)) {
                if (videoDatas != null && videoDatas.size() > 0) {
                    videoDatas.removeAll(videoDatas);
                }
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    String videoTitle = jsonObject.getString("videoTitle");
                    String videoId = jsonObject.getString("videoId");
                    String videoTime = jsonObject.getString("videoTime");
                    String videoCover = jsonObject.getString("videoCover");
                    HuodeVideoInfo huodeVideoInfo = new HuodeVideoInfo(videoTitle, videoId, videoTime, videoCover);
                    videoDatas.add(huodeVideoInfo);
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    playVideoAdapter.notifyDataSetChanged();
                    HuodeVideoInfo huodeVideoInfo = videoDatas.get(0);
                    if (huodeVideoInfo != null) {
                        MultiUtils.showCornerVideoCover(ivMainImg, huodeVideoInfo.getVideoCover());
                    }
                }
            });
        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    llRetry.setVisibility(View.VISIBLE);
                }
            });
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataSet.saveUploadData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        closeSmallWindow();
    }

    private void closeSmallWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            sendBroadcast(new Intent("com.bokecc.vod.play.SMALL_WINDOW").putExtra("control", 3));
        }
    }
}
