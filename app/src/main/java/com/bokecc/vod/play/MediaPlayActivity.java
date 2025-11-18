package com.bokecc.vod.play;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.PictureInPictureParams;
import android.app.RemoteAction;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.util.Rational;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bokecc.marquee.TransparentMarquee;
import com.bokecc.marquee.TransparentMarqueeAdaper;
import com.bokecc.sdk.mobile.ad.DWMediaAD;
import com.bokecc.sdk.mobile.ad.DWMediaADListener;
import com.bokecc.sdk.mobile.ad.EndADInfo;
import com.bokecc.sdk.mobile.ad.FrontADInfo;
import com.bokecc.sdk.mobile.ad.PauseADInfo;
import com.bokecc.sdk.mobile.download.DownloadConfig;
import com.bokecc.sdk.mobile.download.VodDownloadManager;
import com.bokecc.sdk.mobile.entry.AnswerCommitResult;
import com.bokecc.sdk.mobile.entry.AnswerSheetInfo;
import com.bokecc.vod.SdkSidManager;
import com.bokecc.vod.data.SignInBean;
import com.bokecc.sdk.mobile.exception.HuodeException;
import com.bokecc.sdk.mobile.play.AnswerSheetListener;
import com.bokecc.sdk.mobile.play.DWMediaPlayer;
import com.bokecc.sdk.mobile.play.DanmuInfo;
import com.bokecc.sdk.mobile.play.HotSpotInfo;
import com.bokecc.sdk.mobile.play.KnowledgeListener;
import com.bokecc.sdk.mobile.play.MarqueeAction;
import com.bokecc.sdk.mobile.play.MarqueeInfo;
import com.bokecc.sdk.mobile.play.MarqueeView;
import com.bokecc.sdk.mobile.play.MediaMode;
import com.bokecc.sdk.mobile.play.OnAuthMsgListener;
import com.bokecc.sdk.mobile.play.OnDanmuListListener;
import com.bokecc.sdk.mobile.play.OnDreamWinErrorListener;
import com.bokecc.sdk.mobile.play.OnExercisesMsgListener;
import com.bokecc.sdk.mobile.play.OnHotspotListener;
import com.bokecc.sdk.mobile.play.OnPlayModeListener;
import com.bokecc.sdk.mobile.play.OnQAMsgListener;
import com.bokecc.sdk.mobile.play.OnSendDanmuListener;
import com.bokecc.sdk.mobile.play.OnVisitMsgListener;
import com.bokecc.sdk.mobile.play.PlayInfo;
import com.bokecc.sdk.mobile.play.ThumbnailsCallback;
import com.bokecc.vod.BuildConfig;
import com.bokecc.vod.ConfigUtil;
import com.bokecc.vod.HuoDeApplication;
import com.bokecc.vod.R;
import com.bokecc.vod.adapter.ChoiceAdapter;
import com.bokecc.vod.adapter.PlayListAdapter;
import com.bokecc.vod.adapter.StatisticsAdapter;
import com.bokecc.vod.callback.ChoiceSelectListener;
import com.bokecc.vod.data.DanmuInfoParse;
import com.bokecc.vod.data.DataSet;
import com.bokecc.vod.data.DataUtil;
import com.bokecc.vod.data.Exercise;
import com.bokecc.vod.data.HuodeVideoInfo;
import com.bokecc.sdk.mobile.entry.KnowledgeBean;
import com.bokecc.vod.data.LogoInfo;
import com.bokecc.vod.data.ObjectBox;
import com.bokecc.vod.data.Question;
import com.bokecc.vod.data.VibrationInfo;
import com.bokecc.vod.data.VideoPosition;
import com.bokecc.vod.data.VideoPositionDBHelper;
import com.bokecc.vod.data.VisitorInfo;
import com.bokecc.vod.function.FunctionHandler;
import com.bokecc.vod.function.FunctionHandlerCallBack;
import com.bokecc.vod.inter.CommitOrJumpVisitorInfo;
import com.bokecc.vod.inter.ExeOperation;
import com.bokecc.vod.inter.ExercisesContinuePlay;
import com.bokecc.vod.inter.IsUseMobieNetwork;
import com.bokecc.vod.inter.MoreSettings;
import com.bokecc.vod.inter.OnDanmuSet;
import com.bokecc.vod.inter.OnEditDanmuText;
import com.bokecc.vod.inter.SelectDefinition;
import com.bokecc.vod.inter.SelectSpeed;
import com.bokecc.vod.inter.SelectVideo;
import com.bokecc.vod.utils.ItemDecoration;
import com.bokecc.vod.utils.JsonUtil;
import com.bokecc.vod.utils.SaveLogoUtil;
import com.bokecc.vod.utils.MultiUtils;
import com.bokecc.vod.view.CheckNetworkDialog;
import com.bokecc.vod.view.CustomLogoView;
import com.bokecc.vod.view.DanmuSetDialog;
import com.bokecc.vod.view.DoExerciseDialog;
import com.bokecc.vod.view.EditDanmuTextDialog;
import com.bokecc.vod.view.ExerciseGuideDialog;
import com.bokecc.vod.view.GifMakerThread;
import com.bokecc.vod.view.HotspotSeekBar;
import com.bokecc.vod.view.IsUseMobileNetworkDialog;
import com.bokecc.vod.view.KnowledgeDialog;
import com.bokecc.vod.view.LandscapeVisitorInfoDialog;
import com.bokecc.vod.view.MoreSettingsDialog;
import com.bokecc.vod.view.PauseVideoAdDialog;
import com.bokecc.vod.view.PortraitVisitorInfoDialog;
import com.bokecc.vod.view.ProgressObject;
import com.bokecc.vod.view.ProgressView;
import com.bokecc.vod.view.QAView;
import com.bokecc.vod.view.SelectDefinitionDialog;
import com.bokecc.vod.view.SelectSpeedDialog;
import com.bokecc.vod.view.SelectVideoDialog;
import com.bokecc.vod.view.ShowExeDialog;
import com.bokecc.vod.view.SubtitleView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;

@SuppressLint("NonConstantResourceId")
public class MediaPlayActivity extends Activity implements View.OnClickListener, TextureView.SurfaceTextureListener,
        DWMediaPlayer.OnPreparedListener, DWMediaPlayer.OnInfoListener, DWMediaPlayer.OnBufferingUpdateListener,
        DWMediaPlayer.OnCompletionListener, DWMediaPlayer.OnErrorListener, OnDreamWinErrorListener, SensorEventListener, ChoiceSelectListener {

    private static final String TAG = MediaPlayActivity.class.getSimpleName();
    /**
     * 当出现错误时(OnError回调中)，尝试自动重连的次数，可按需修改触发逻辑与次数
     */
    private static final int ON_ERROR_RETRY_TIME = 3;

    private static final int GET_DAN_MU_INTERVAL = 60 * 1000;
    /**
     * 是否展示课堂练习
     */
    private static final boolean isShowExercise = true;
    /**
     * 未看部分禁止拖动：isForbidDragToUnPlayPart
     * 为false时允许拖动到未看部分，
     * 为true时不允许拖动到未看部分
     */
    private static final boolean isForbidDragToUnPlayPart = false;

    private static final String MP3 = ".mp3";
    private static final String TEXT = "text";
    private static final String IMAGE = "image";

    private static final int gifMax = 15 * 1000;
    private static final int gifMin = 3 * 1000;
    private static final int gifInterval = 200;

    private static final int SMALL_WINDOW_PLAY_OR_PAUSE = 5;

    private static final String smallWindowAction = "com.bokecc.vod.play.SMALL_WINDOW";

    private String videoId, videoTitle, videoCover;
    private TextView tv_video_title, tv_current_time, tv_video_time, tv_play_definition,
            tv_video_select, tv_error_info, tv_operation, gifTips, gifCancel, tv_ad_countdown, tv_skip_ad,
            tv_know_more, tv_watch_tip, tv_pre_watch_over;
    private ImageView iv_back;
    private ImageView iv_video_full_screen;
    private ImageView iv_next_video;
    private ImageView iv_play_pause;
    private ImageView iv_switch_to_audio;
    private ImageView iv_more_settings;
    private ImageView iv_create_gif;
    private ImageView ivGifShow;
    private ImageView ivGifStop;
    private ImageView iv_lock_or_unlock;
    private ImageView iv_pause_ad;
    private TextureView tv_video;
    private LinearLayout ll_load_video;
    private LinearLayout ll_progress_and_fullscreen;
    private LinearLayout ll_title_and_audio;
    private LinearLayout ll_speed_def_select;
    private LinearLayout ll_play_error;
    private LinearLayout ll_audio_view;
    private LinearLayout ll_confirm_or_cancel;
    private LinearLayout ll_show_gif;
    private LinearLayout ll_ad;
    private LinearLayout ll_pre_watch_over;
    private RelativeLayout rl_play_video, rl_pause_ad;
    private Button btn_download;
    private HotspotSeekBar sb_progress;
    private PlayListAdapter playListAdapter;
    private DWMediaPlayer player;
    private Activity activity;
    private Surface playSurface;
    private boolean isFullScreen = false, isPrepared = false, isAudioMode = false;
    private int landScapeHeight, landScapeMarginTop, videoHeight, videoWidth, playIndex,
            currentVideoSizePos = 1, currentBrightness, lastPlayPosition = 0;
    //当前播放位置、视频总时长
    private long currentPosition = 0, videoDuration = 0;
    private VideoTask videoTask;
    private AdTask adTask;
    private controlHideTask controlHideTask;
    private Timer timer, hideTimer, adTimer;
    private ArrayList<HuodeVideoInfo> videoList = new ArrayList<>();
    private List<String> videoIds;
    // 默认设置为普清
    private int currentDefinition = DWMediaPlayer.NORMAL_DEFINITION;
    //视频清晰度选项
    private Map<String, Integer> definitions;
    //切换清晰度时视频播放的位置
    private long switchDefPos = 0;

    private PlayInfo playInfo;
    //字幕
    private SubtitleView sv_subtitle;
    //在本地数据记录播放位置的辅助类
    private VideoPositionDBHelper videoPositionDBHelper;
    //记录上次播放的位置
    private VideoPosition lastVideoPosition;
    //问答数据
    private QAView qaView;
    TreeMap<Integer, Question> questions;
    //课堂练习
    private List<Exercise> exercises;
    private ShowExeDialog exeDialog;
    private DoExerciseDialog doExerciseDialog;
    private boolean isShowConfirmExerciseDialog = false;
    private int returnListenTime = 0;
    //视频打点数据
    private TreeMap<Integer, String> hotSpotData;
    //授权验证码
    private String verificationCode;
    //是否是本地播放
    private boolean isLocalPlay;
    //本地视频的路径
    private String path;
    //本地视频的格式
    private String format;
    //批量下载视频
    private List<String> batchDownload;
    //生成gif
    private boolean isGifStart = false, isGifCancel = true, isGifFinish = false;
    private GifMakerThread gifMakerThread;
    private File gifFile;
    private ProgressView gifProgressView;

    ProgressObject progressObject = new ProgressObject();
    private long lastTimeMillis;
    private int gifVideoWidth, gifVideoHeight;
    private int gifRecordTime = 0;
    private TimerTask gifCreateTimerTask;
    //是否锁定
    private boolean isLock = false;
    private int controlHide = 8;
    private String videoADClickUrl;
    //暂停广告
    private PauseADInfo pauseADInfoData;
    private String pauseAdClickUrl;
    //片尾广告
    private EndADInfo endADInfoData;
    //是否正在播放片头广告、是否可以点击广告、是否已启动广告计时
    private boolean isPlayVideoAd = false;
    private boolean isPlayFrontAd = false;
    private boolean isPlayEndAd = false;
    private boolean isCanClickAd = false;
    private boolean isShowImgEndAd = false;
    private boolean isStartAdTimer = false;
    private boolean isFrontVideoAd = true;
    private int skipAdTime, adTime;
    private ImageView iv_image_ad;
    private LinearLayout ll_image_ad;
    private PauseVideoAdDialog pauseVideoAdDialog;
    //访客信息收集
    private long showVisitorTime;
    private String visitorImageUrl, visitorJumpUrl, visitorTitle, visitorInfoId;
    private int visitorIsJump;
    private List<VisitorInfo> visitorInfos;
    private LandscapeVisitorInfoDialog visitorInfoDialog;
    private PortraitVisitorInfoDialog portraitVisitorInfoDialog;
    private boolean isShowVisitorInfoDialog = false, isVideoShowVisitorInfoDialog = false;
    //视频播放状态
    private boolean isPlayVideo = true;
    //授权验证
    private int isAllowPlayWholeVideo = 2;
    private int freeWatchTime = 0;
    private String freeWatchOverMsg = "";
    //网络状态监听
    private NetChangedReceiver netReceiver;
    //记录网络状态 0：无网络 1：WIFI 2：移动网络 3：WIFI和移动网络
    private int netWorkStatus = 1;
    private boolean isNoNetPause = false;
    private boolean isShowUseMobie = false;
    //投屏
    private String playUrl;
    private Integer volumeValue = 0;
    private int SEARCH_DEVICE_TIME = 8;
    //本地回放是否开启了透明度跑马灯
    private boolean isInvisibleMarquee;
    //重力感应旋转方向
    private int mX, mY, mZ;
    private long lastSensorTime = 0;
    private SensorManager sensorManager;

    //滑动调节进度亮度和音量
    private float downX, downY, upX, upY, xMove, yMove, absxMove, absyMove, lastX, lastY;
    private AudioManager audioManager;
    private int currentVolume, maxVolume;
    private final int maxBrightness = 100;
    private int halfWidth = 0, controlChange = 70;
    private boolean isChangeBrightness = false;
    private LinearLayout ll_volume;
    private ProgressBar pb_volume;
    private LinearLayout ll_brightness;
    private ProgressBar pb_brightness;
    private long slideProgress;
    private TextView tv_slide_progress;
    private ImageView coverImage;
    //跑马灯
    private MarqueeView mv_video;
    private ImageView iv_small_window_play;
    private SmallWindowReceiver smallWindowReceiver;
    private ArrayList<RemoteAction> actions;
    private RemoteAction pauseRemoteAction, playRemoteAction;
    private boolean isSmallWindow = false;
    private PictureInPictureParams.Builder builder;
    private ImageView iv_landscape_screenshot, iv_portrait_screenshot;
    /**
     * 选择题
     */
    private LinearLayout choiceRoot;
    /**
     * 答题器，跳过按钮
     */
    private TextView choiceSkip;
    /**
     * 答题器容错ui
     */
    private LinearLayout commitAnswerErrorLayout;
    /**
     * 答题器选择列表
     */
    private RecyclerView choiceRecycler;
    private ChoiceAdapter choiceAdapter;
    /**
     * 当前要回答的问题
     */
    private AnswerSheetInfo currentSheetInfo;
    /**
     * 答题器显示时间
     */
    private List<Integer> sheetTimeList;
    /**
     * 所有答题器数据
     */
    private List<AnswerSheetInfo> answerSheetInfoList;
    /**
     * 统计结果
     */
    private LinearLayout statisticsLayout;
    /**
     * 回答结果展示
     */
    private TextView statisticsResult;
    private RecyclerView statisticsRecycler;
    /**
     * 答题结果，回放
     */
    private TextView backView;
    /**
     * 课堂练习拦截状态
     */
    private boolean exerciseInterrupt;
    /**
     * 访客信息拦截状态
     */
    private boolean visitorInterrupt;
    //网速
    private String netSpeed;
    private TextView tv_loading;
    private Timer netSpeedTimer;
    private NetSpeedTask netSpeedTask;
    //弹幕
    private IDanmakuView dm_view;
    private DanmakuContext danmakuContext;
    private HotspotSeekBar sb_portrait_progress;
    private TextView tv_portrait_current_time, tv_portrait_video_time;
    private LinearLayout ll_landscape_progress, ll_portrait_progress, ll_landscape_danMu,
            ll_portrait_danMu_off, ll_portrait_danMu_on, ll_landscape_danMu_set_send;
    private TextView tv_input_danMu, tv_portrait_input_danMu;
    private int danMuSec = -1, currentMinutePos = 0;
    private ImageView iv_portrait_danMu_set;
    private ImageView iv_landscape_danMu_switch;
    private int currentOpaqueness = 100, currentFontSizeLevel = 2, currentDanMuSpeedLevel = 2,
            currentDisplayArea = 100;
    private float fontSizeScale = 1.5f, danMuSpeed = 1.0f;
    private boolean isDanMuOn = true, isCanSendDanMu = true, isPlayAfterSendDanMu = true, isEditDanMu = false;
    private RelativeLayout rl_danMu;
    private int sendDanMuInterval = 5;
    //动感视频
    private List<VibrationInfo> vibrationInfoList;
    private Vibrator vibrator;
    private boolean isDynamicVideo;
    /**
     * 当前重试状态
     */
    private boolean retryStatue;
    /**
     * 当前的重连次数
     */
    private int retryPlayTimes = 0;
    private long playedTime = 0;
    private boolean isPlayCompleted;
    //自定义logo
    /**
     * 自定义logo
     */
    private CustomLogoView clv_logo;
    private final String logoUrl = "";
    private String logoPath;
    private String marqueeData;
    /**
     * 是否使用点播admin配置的知识点数据源
     */
    private static final boolean SERVER_DATA_SOURCE = true;
    /**
     * 知识点按钮
     */
    private TextView tvKnowledge;
    /**
     * 知识点Ui
     */
    private KnowledgeDialog knowledgeDialog;
    /**
     * 知识点数据源
     */
    private KnowledgeBean knowledgeBean;
    /**
     * 知识点结束时间点
     */
    private int knowledgeStartTime, knowledgeEndTime;

    /**
     * 当前知识点播放完毕后是否暂停
     */
    private boolean knowledgePauseStatue;

    /**
     * 当前播放区域是否在选中的知识点内
     */
    private boolean inKnowledgeRange;

    /**
     * 当前是否已经执行获取视频截图
     */
    private boolean executeStatue = false;
    private MediaMode currentMode;
    private RelativeLayout signinRoot;
    private TextView tv_signin,tv_signin_control;
    private FunctionHandler functionHandler ;
    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    if (iv_back.getVisibility() == View.VISIBLE) {
                        hideViews();
                        return;
                    }
                    if (isFullScreen) {
                        iv_lock_or_unlock.setVisibility(View.VISIBLE);
                        iv_create_gif.setVisibility(View.VISIBLE);
                        iv_landscape_screenshot.setVisibility(View.VISIBLE);
                    }
                    if (isLock) {
                        iv_lock_or_unlock.setImageResource(R.mipmap.iv_lock);
                        iv_lock_or_unlock.setVisibility(View.VISIBLE);
                    } else {
                        iv_lock_or_unlock.setImageResource(R.mipmap.iv_unlock);
                        showViews();
                    }
                    break;
            }
        }
    };
    private Runnable signinRunnable;
    private FunctionHandlerCallBack functionHandlerCallBack = new FunctionHandlerCallBack() {
        @Override
        public void onSignInStart(SignInBean data, int num) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O&&isInPictureInPictureMode()){
                signinRoot.setVisibility(View.VISIBLE);
                tv_signin.setText(String.valueOf(num));
                tv_signin_control.setText(data.getBtnText());
            }
            if (coverImage.getVisibility() == View.VISIBLE) {
                coverImage.setVisibility(View.GONE);
            }
            pause();
        }

        @Override
        public void onSignInClose() {
            if (coverImage.getVisibility() == View.VISIBLE) {
                coverImage.setVisibility(View.GONE);
            }
            start();
        }
    };
    private boolean isPrepareing = false;
    private TransparentMarquee transparentMarquee;
    private float currentSpeed=1.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_play);
        MultiUtils.setStatusBarColor(this, R.color.black, false);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        functionHandler = new FunctionHandler(functionHandlerCallBack,this);
        activity = this;
        regNetworkReceiver();
        initView();
        initPlayer();
    }

    //注册网络状态监听
    private void regNetworkReceiver() {
        netWorkStatus = MultiUtils.getNetWorkStatus(activity);
        if (netReceiver == null) {
            netReceiver = new NetChangedReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netReceiver, filter);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        videoId = getIntent().getStringExtra("videoId");
        videoTitle = getIntent().getStringExtra("videoTitle");
        videoCover = getIntent().getStringExtra("videoCover");
        format = getIntent().getStringExtra("format");
        logoPath = getIntent().getStringExtra("logoPath");
        isLocalPlay = getIntent().getBooleanExtra("isLocalPlay", false);
        marqueeData = getIntent().getStringExtra("marqueeData");
        isInvisibleMarquee = getIntent().getBooleanExtra("isInvisibleMarquee", false);
        iv_back = findViewById(R.id.iv_back);
        iv_video_full_screen = findViewById(R.id.iv_video_full_screen);
        iv_next_video = findViewById(R.id.iv_next_video);
        iv_play_pause = findViewById(R.id.iv_play_pause);
        iv_switch_to_audio = findViewById(R.id.iv_switch_to_audio);
        iv_more_settings = findViewById(R.id.iv_more_settings);
        iv_create_gif = findViewById(R.id.iv_create_gif);
        ImageView iv_save_gif = findViewById(R.id.iv_save_gif);
        ivGifShow = findViewById(R.id.gif_show);
        ivGifStop = findViewById(R.id.iv_gif_stop);
        iv_lock_or_unlock = findViewById(R.id.iv_lock_or_unlock);
        ImageView iv_ad_full_screen = findViewById(R.id.iv_ad_full_screen);
        tv_video_title = findViewById(R.id.tv_video_title);
        tv_current_time = findViewById(R.id.tv_current_time);
        tv_video_time = findViewById(R.id.tv_video_time);
        tvKnowledge = findViewById(R.id.tvKnowledge);
        tv_play_definition = findViewById(R.id.tv_play_definition);
        tv_video_select = findViewById(R.id.tv_video_select);
        tv_error_info = findViewById(R.id.tv_error_info);
        tv_operation = findViewById(R.id.tv_operation);
        gifTips = findViewById(R.id.gif_tips);
        gifCancel = findViewById(R.id.gif_cancel);
        tv_ad_countdown = findViewById(R.id.tv_ad_countdown);
        tv_skip_ad = findViewById(R.id.tv_skip_ad);
        tv_know_more = findViewById(R.id.tv_know_more);
        ImageView iv_close_pause_ad = findViewById(R.id.iv_close_pause_ad);
        tv_watch_tip = findViewById(R.id.tv_watch_tip);
        tv_pre_watch_over = findViewById(R.id.tv_pre_watch_over);
        ListView lv_play_list = findViewById(R.id.lv_play_list);
        tv_video = findViewById(R.id.tv_video);
        ll_load_video = findViewById(R.id.ll_load_video);
        ll_progress_and_fullscreen = findViewById(R.id.ll_progress_and_fullscreen);
        ll_title_and_audio = findViewById(R.id.ll_title_and_audio);
        ll_speed_def_select = findViewById(R.id.ll_speed_def_select);
        ll_play_error = findViewById(R.id.ll_play_error);
        ll_audio_view = findViewById(R.id.ll_audio_view);
        rl_play_video = findViewById(R.id.rl_play_video);
        rl_pause_ad = findViewById(R.id.rl_pause_ad);
        sb_progress = findViewById(R.id.sb_progress);
        sv_subtitle = findViewById(R.id.sv_subtitle);
        btn_download = findViewById(R.id.btn_download);
        Button btn_confirm = findViewById(R.id.btn_confirm);
        Button btn_cancel = findViewById(R.id.btn_cancel);
        ll_confirm_or_cancel = findViewById(R.id.ll_confirm_or_cancel);
        ll_show_gif = findViewById(R.id.ll_show_gif);
        ll_pre_watch_over = findViewById(R.id.ll_pre_watch_over);
        LinearLayout ll_rewatch = findViewById(R.id.ll_rewatch);
        ll_ad = findViewById(R.id.ll_ad);
        iv_pause_ad = findViewById(R.id.iv_pause_ad);
        iv_image_ad = findViewById(R.id.iv_image_ad);
        ll_image_ad = findViewById(R.id.ll_image_ad);
        gifProgressView = findViewById(R.id.gif_progress_view);
        gifProgressView.setMaxDuration(gifMax);
        gifProgressView.setMinTime(gifMin);
        gifProgressView.setData(progressObject);

        ll_volume = findViewById(R.id.ll_volume);
        ll_brightness = findViewById(R.id.ll_brightness);
        pb_volume = findViewById(R.id.pb_volume);
        pb_brightness = findViewById(R.id.pb_brightness);
        tv_slide_progress = findViewById(R.id.tv_slide_progress);
        mv_video = findViewById(R.id.mv_video);
        iv_small_window_play = findViewById(R.id.iv_small_window_play);

        coverImage = findViewById(R.id.coverImage);
        iv_landscape_screenshot = findViewById(R.id.iv_landscape_screenshot);
        iv_portrait_screenshot = findViewById(R.id.iv_portrait_screenshot);
        tv_loading = findViewById(R.id.tv_loading);

        dm_view = findViewById(R.id.dm_view);
        sb_portrait_progress = findViewById(R.id.sb_portrait_progress);
        tv_portrait_current_time = findViewById(R.id.tv_portrait_current_time);
        tv_portrait_video_time = findViewById(R.id.tv_portrait_video_time);
        ll_landscape_progress = findViewById(R.id.ll_landscape_progress);
        ll_portrait_progress = findViewById(R.id.ll_portrait_progress);
        ll_landscape_danMu = findViewById(R.id.ll_landscape_danmu);
        tv_input_danMu = findViewById(R.id.tv_input_danmu);
        tv_input_danMu.setOnClickListener(this);
        ImageView iv_landscape_danmu_set = findViewById(R.id.iv_landscape_danmu_set);
        iv_landscape_danmu_set.setOnClickListener(this);
        ll_portrait_danMu_off = findViewById(R.id.ll_portrait_danmu_off);
        ll_portrait_danMu_off.setOnClickListener(this);
        ll_portrait_danMu_on = findViewById(R.id.ll_portrait_danmu_on);
        ImageView iv_portrait_danmu_on = findViewById(R.id.iv_portrait_danmu_on);
        iv_portrait_danmu_on.setOnClickListener(this);
        tv_portrait_input_danMu = findViewById(R.id.tv_portrait_input_danmu);
        tv_portrait_input_danMu.setOnClickListener(this);
        iv_portrait_danMu_set = findViewById(R.id.iv_portrait_danmu_set);
        iv_portrait_danMu_set.setOnClickListener(this);
        iv_landscape_danMu_switch = findViewById(R.id.iv_landscape_danmu_switch);
        iv_landscape_danMu_switch.setOnClickListener(this);
        ll_landscape_danMu_set_send = findViewById(R.id.ll_landscape_danmu_set_send);
        TextView tv_play_speed = findViewById(R.id.tv_play_speed);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
            tv_play_speed.setVisibility(View.VISIBLE);
            tv_play_speed.setOnClickListener(this);
        }else{
            tv_play_speed.setVisibility(View.GONE);
        }
        rl_danMu = findViewById(R.id.rl_danmu);
        clv_logo = findViewById(R.id.clv_logo);
        signinRoot = findViewById(R.id.rl_signin_root);
        tv_signin = findViewById(R.id.tv_signin);
        tv_signin_control = findViewById(R.id.tv_signin_control);
        tv_video_title.setText(videoTitle);

        transparentMarquee = findViewById(R.id.id_transparent_marquee);

        iv_back.setOnClickListener(this);
        iv_video_full_screen.setOnClickListener(this);
        iv_next_video.setOnClickListener(this);
        iv_play_pause.setOnClickListener(this);
        tvKnowledge.setOnClickListener(this);
        tv_play_definition.setOnClickListener(this);
        tv_video_select.setOnClickListener(this);
        iv_switch_to_audio.setOnClickListener(this);
        iv_more_settings.setOnClickListener(this);
        btn_download.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        iv_create_gif.setOnClickListener(this);
        iv_save_gif.setOnClickListener(this);
        gifCancel.setOnClickListener(this);
        ivGifStop.setOnClickListener(this);
        tv_skip_ad.setOnClickListener(this);
        tv_know_more.setOnClickListener(this);
        iv_lock_or_unlock.setOnClickListener(this);
        iv_ad_full_screen.setOnClickListener(this);
        iv_pause_ad.setOnClickListener(this);
        iv_image_ad.setOnClickListener(this);
        iv_close_pause_ad.setOnClickListener(this);
        ll_rewatch.setOnClickListener(this);
        iv_small_window_play.setOnClickListener(this);
        tv_video.setSurfaceTextureListener(this);
        iv_landscape_screenshot.setOnClickListener(this);
        iv_portrait_screenshot.setOnClickListener(this);
        iv_play_pause.setImageResource(ConfigUtil.AutoPlay ? R.mipmap.iv_pause : R.mipmap.iv_play);
        //本地播放音频
        if (!TextUtils.isEmpty(format) && TextUtils.equals(format, MP3)) {
            isAudioMode = true;
            ll_audio_view.setVisibility(View.VISIBLE);
        }
        batchDownload = new ArrayList<>();
        videoList = getIntent().getParcelableArrayListExtra("videoDatas");
        if (videoList != null && videoList.size() > 0) {
            videoIds = new ArrayList<>();
            for (int i = 0; i < videoList.size(); i++) {
                HuodeVideoInfo videoInfo = videoList.get(i);
                if (videoInfo != null) {
                    videoIds.add(videoList.get(i).getVideoId());
                }
            }
            playIndex = videoIds.indexOf(videoId);
            playListAdapter = new PlayListAdapter(MediaPlayActivity.this, videoList);
            lv_play_list.setAdapter(playListAdapter);
        }

        //拖动视频
        sb_progress.setOnSeekBarChangeListener(new HotspotSeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStartTrackingTouch(HotspotSeekBar seekBar) {
                returnListenTime = seekBar.getProgress();
            }

            @Override
            public void onStopTrackingTouch(HotspotSeekBar seekBar, float trackStopPercent) {
                int stopPosition = (int) (trackStopPercent * player.getDuration());
                if (stopPosition > playedTime && isForbidDragToUnPlayPart && !isPlayCompleted) {
                    return;
                }
                sb_progress.inKnowledgeRange(stopPosition);
                player.seekTo(stopPosition,MediaPlayer.SEEK_CLOSEST);
                seekToDanmu(stopPosition);
                danMuSec = (stopPosition / GET_DAN_MU_INTERVAL) - 1;
                //拖动进度条展示课堂练习
                isShowConfirmExerciseDialog = isShowExercise(stopPosition);
            }
        });

        sb_portrait_progress.setOnSeekBarChangeListener(new HotspotSeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStartTrackingTouch(HotspotSeekBar seekBar) {
                returnListenTime = seekBar.getProgress();
            }

            @Override
            public void onStopTrackingTouch(HotspotSeekBar seekBar, float trackStopPercent) {
                int stopPostion = (int) (trackStopPercent * player.getDuration());
                if (stopPostion > playedTime && isForbidDragToUnPlayPart && !isPlayCompleted) {
                    return;
                }
                player.seekTo(stopPostion,MediaPlayer.SEEK_CLOSEST);
                seekToDanmu(stopPostion);
                danMuSec = (stopPostion / GET_DAN_MU_INTERVAL) - 1;
                //拖动进度条展示课堂练习
                isShowConfirmExerciseDialog = isShowExercise(stopPostion);
            }
        });

        //点击打点位置，从这个位置开始播放
        sb_progress.setOnIndicatorTouchListener(new HotspotSeekBar.OnIndicatorTouchListener() {
            @Override
            public void onIndicatorTouch(int currentPosition) {
                player.seekTo(currentPosition * 1000,MediaPlayer.SEEK_CLOSEST);
                seekToDanmu(currentPosition * 1000);
                if (player != null && !player.isPlaying()) {
                    player.start();
                    iv_play_pause.setImageResource(R.mipmap.iv_pause);
                }
            }
        });

        sb_portrait_progress.setOnIndicatorTouchListener(new HotspotSeekBar.OnIndicatorTouchListener() {
            @Override
            public void onIndicatorTouch(int currentPosition) {
                player.seekTo(currentPosition * 1000,MediaPlayer.SEEK_CLOSEST);
                seekToDanmu(currentPosition * 1000);
            }
        });

        lv_play_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HuodeVideoInfo item = (HuodeVideoInfo) playListAdapter.getItem(position);
                if (!item.isShowSelectButton()&&isPrepareing){
                    MultiUtils.showToast(activity, "切换太频繁了");
                    return;
                }
                if (choiceRoot.getVisibility() == View.VISIBLE) {
                    choiceRoot.setVisibility(View.GONE);
                }
                if (statisticsLayout.getVisibility() == View.VISIBLE) {
                    statisticsLayout.setVisibility(View.GONE);
                }
                if (commitAnswerErrorLayout.getVisibility() == View.VISIBLE) {
                    commitAnswerErrorLayout.setVisibility(View.GONE);
                }
                if (doExerciseDialog != null && doExerciseDialog.isShowing()) {
                    doExerciseDialog.dismiss();
                }
                if (item.isShowSelectButton()) {
                    item.setSelectedDownload(!item.isSelectedDownload());
                    playListAdapter.notifyDataSetChanged();
                } else {
                    isPrepareing = true;
                    transparentMarquee.release();
                    iv_play_pause.setImageResource(R.mipmap.iv_play);
                    videoId = item.getVideoId();
                    videoTitle = item.getVideoTitle();
                    videoCover = item.getVideoCover();
                    playIndex = position;
                    resetInfo();
                    updateLastPlayPosition();
                    getAdInfo();
                    player.resetPlayedAndPausedTime();
                }
            }
        });

        rl_play_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ll_pre_watch_over.getVisibility() == View.VISIBLE) {
                    return;
                }
                if (isPlayVideoAd) {
                    knowMoreFrontAdInfo();
                    return;
                }
                if (!isPrepared) {
                    return;
                }
                if (handler.hasMessages(1)){
                    handler.removeMessages(1);
                    playOrPauseVideo();
                    showViews();
                }else{
                    handler.sendEmptyMessageDelayed(1,200);
                }

            }
        });

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        pb_volume.setMax(maxVolume);
        pb_volume.setProgress(currentVolume);

        //获取当前亮度
        currentBrightness = MultiUtils.getSystemBrightness(activity);
        pb_brightness.setMax(maxBrightness);
        pb_brightness.setProgress(currentBrightness);
        //滑动调节
        rl_play_video.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getX();
                        downY = event.getY();
                        lastX = downX;
                        lastY = downY;
                        slideProgress = currentPosition;
                        halfWidth = MultiUtils.getScreenWidth(activity) / 2;
                        if (downX > halfWidth) {
                            isChangeBrightness = false;
                            controlChange = 70;
                        } else {
                            isChangeBrightness = true;
                            controlChange = 15;
                        }

                        break;

                    case MotionEvent.ACTION_MOVE:
                        float x = event.getX();
                        float y = event.getY();

                        float xMoveVolume = x - lastX;
                        float yMoveVolume = y - lastY;
                        float absxMoveVolume = Math.abs(xMoveVolume);
                        float absyMoveVolume = Math.abs(yMoveVolume);

                        if (absyMoveVolume > absxMoveVolume && absyMoveVolume > controlChange && !isLock) {
                            lastX = x;
                            lastY = y;
                            if (isChangeBrightness) {
                                //调节亮度
                                int changeBrightness = (int) (absyMoveVolume / controlChange);
                                if (yMoveVolume > 0) {
                                    currentBrightness = currentBrightness - changeBrightness;
                                } else {
                                    currentBrightness = currentBrightness + changeBrightness;
                                }
                                if (currentBrightness < 0) {
                                    currentBrightness = 0;
                                }
                                if (currentBrightness > maxBrightness) {
                                    currentBrightness = maxBrightness;
                                }
                                ll_brightness.setVisibility(View.VISIBLE);
                                ll_volume.setVisibility(View.GONE);
                                changeBrightness(activity, currentBrightness);
                                pb_brightness.setProgress(currentBrightness);
                            } else {
                                currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                                //调节音量
                                int changeVolume = (int) (absyMoveVolume / controlChange);
                                if (yMoveVolume > 0) {
                                    currentVolume = currentVolume - changeVolume;
                                } else {
                                    currentVolume = currentVolume + changeVolume;
                                }
                                if (currentVolume < 0) {
                                    currentVolume = 0;
                                }
                                if (currentVolume > maxVolume) {
                                    currentVolume = maxVolume;
                                }
                                ll_volume.setVisibility(View.VISIBLE);
                                ll_brightness.setVisibility(View.GONE);
                                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
                                pb_volume.setProgress(currentVolume);
                            }

                        } else if (absxMoveVolume > absyMoveVolume && absxMoveVolume > 50 && !isLock) {
                            lastX = x;
                            lastY = y;
                            int screenWidth = MultiUtils.getScreenWidth(activity);
                            long changeProgress = (long) (absxMoveVolume * videoDuration / screenWidth);

                            if (xMoveVolume > 0) {
                                //往右滑
                                slideProgress = slideProgress + changeProgress;
                            } else {
                                //往左滑
                                slideProgress = slideProgress - changeProgress;
                            }
                            if (slideProgress > videoDuration) {
                                slideProgress = videoDuration;
                            }
                            if (slideProgress < 0) {
                                slideProgress = 0;
                            }
                            String videoTime = MultiUtils.millsecondsToMinuteSecondStr(videoDuration);
                            String currentTime = MultiUtils.millsecondsToMinuteSecondStr(slideProgress);
                            tv_slide_progress.setVisibility(View.VISIBLE);
                            tv_slide_progress.setText(String.format("%s/%s", currentTime, videoTime));
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        upX = event.getX();
                        upY = event.getY();

                        xMove = upX - downX;
                        yMove = upY - downY;
                        absxMove = Math.abs(xMove);
                        absyMove = Math.abs(yMove);

                        if (absxMove >= absyMove && absxMove > 50 && !isLock) {
                            //调节进度
                            tv_slide_progress.setVisibility(View.GONE);
                            if (!(slideProgress > playedTime && isForbidDragToUnPlayPart && !isPlayCompleted)) {
                                sb_progress.inKnowledgeRange(slideProgress);
                                player.seekTo((int) slideProgress,MediaPlayer.SEEK_CLOSEST);
                                seekToDanmu((int) slideProgress);
                            }
                        }
                        break;
                }
                return false;
            }
        });

        verificationCode = MultiUtils.getVerificationCode();
        verificationCode = "4";
        //获取上次播放的位置
        videoPositionDBHelper = new VideoPositionDBHelper(ObjectBox.get());
        getLastVideoPostion();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
        }

        //小窗播放
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            iv_small_window_play.setVisibility(View.VISIBLE);
            actions = new ArrayList<>();
            Icon icon = Icon.createWithResource(activity, R.mipmap.iv_small_window_pause);
            PendingIntent pauseIntent = PendingIntent.getBroadcast(activity,
                    SMALL_WINDOW_PLAY_OR_PAUSE,
                    new Intent(smallWindowAction).putExtra("control", 1),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            pauseRemoteAction = new RemoteAction(icon, "", "", pauseIntent);
            Icon iconPlay = Icon.createWithResource(activity, R.mipmap.iv_small_window_play);
            PendingIntent playIntent = PendingIntent.getBroadcast(activity,
                    SMALL_WINDOW_PLAY_OR_PAUSE,
                    new Intent(smallWindowAction).putExtra("control", 2),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            playRemoteAction = new RemoteAction(iconPlay, "", "", playIntent);
            smallWindowReceiver = new SmallWindowReceiver();
            IntentFilter intentFilter = new IntentFilter(smallWindowAction);
            registerReceiver(smallWindowReceiver, intentFilter);
        }

        //弹幕
        danmakuContext = DanmakuContext.create();
        HashMap<Integer, Boolean> overlappingEnable = new HashMap<>();
        overlappingEnable.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnable.put(BaseDanmaku.TYPE_FIX_TOP, true);
        danmakuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 2)
                .setDuplicateMergingEnabled(false)
                .preventOverlapping(overlappingEnable)
                .setDanmakuMargin(50);
        if (dm_view != null) {
            DanmuInfoParse danmuInfoParse = new DanmuInfoParse();
            dm_view.setCallback(new master.flame.danmaku.controller.DrawHandler.Callback() {
                @Override
                public void updateTimer(DanmakuTimer timer) {

                }

                @Override
                public void drawingFinished() {

                }

                @Override
                public void danmakuShown(BaseDanmaku danmaku) {

                }

                @Override
                public void prepared() {
                    dm_view.start();
                    if (!isDanMuOn) {
                        dm_view.hide();
                    }
                }
            });
            dm_view.prepare(danmuInfoParse, danmakuContext);
            dm_view.enableDanmakuDrawingCache(true);
        }
        initChoiceLayout();
        initErrorLayout();
        initStatistics();
    }

    /**
     * 选择题相关ui
     */
    private void initChoiceLayout() {
        choiceRoot = findViewById(R.id.choiceRoot);
        choiceSkip = findViewById(R.id.choiceSkip);
        choiceSkip.setOnClickListener(this);
        findViewById(R.id.commitAnswer).setOnClickListener(this);
        choiceRecycler = findViewById(R.id.choiceRecycler);
        choiceRecycler.addItemDecoration(new ItemDecoration(MultiUtils.dipToPx(this, 5), 4));
    }

    /**
     * 答案提交失败
     */
    private void initErrorLayout() {
        commitAnswerErrorLayout = findViewById(R.id.commitAnswerErrorLayout);
        findViewById(R.id.resubmit).setOnClickListener(this);
        findViewById(R.id.resumePlay).setOnClickListener(this);
        findViewById(R.id.dismissErrorLayout).setOnClickListener(this);
    }

    /**
     * 答题统计结果
     */
    private void initStatistics() {
        statisticsLayout = findViewById(R.id.statisticsLayout);
        statisticsResult = findViewById(R.id.statisticsResult);
        backView = findViewById(R.id.backView);
        backView.setOnClickListener(this);
        findViewById(R.id.dismissStatistics).setOnClickListener(this);
        findViewById(R.id.goOnPlay).setOnClickListener(this);
        statisticsRecycler = findViewById(R.id.statisticsRecycler);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        statisticsRecycler.addItemDecoration(new ItemDecoration(10, 8));
        statisticsRecycler.setLayoutManager(manager);
    }

    /**
     * brightnessValue 取值范围0-100
     */
    private void changeBrightness(Activity context, int brightnessValue) {
        Window localWindow = context.getWindow();
        WindowManager.LayoutParams localLayoutParams = localWindow.getAttributes();
        localLayoutParams.screenBrightness = brightnessValue / 100.0F;
        localWindow.setAttributes(localLayoutParams);
    }

    private void getLastVideoPostion() {
        lastVideoPosition = videoPositionDBHelper.getVideoPosition(videoId);
        if (lastVideoPosition == null) {
            lastPlayPosition = 0;
            if (TextUtils.isEmpty(videoId)) {
                return;
            }
            lastVideoPosition = new VideoPosition(videoId, 0);

        } else {
            lastPlayPosition = lastVideoPosition.getPosition();
            isPlayCompleted = lastVideoPosition.isPlayCompleted();
        }
    }

    //初始化播放器
    private void initPlayer() {
        player = new DWMediaPlayer(ConfigUtil.FORCE_ANSWER_SHEET);
        player.setAutoPlay(ConfigUtil.AutoPlay);
        player.setHideAnswerSheet(false);
        player.setOnPreparedListener(this);
        player.setOnInfoListener(this);
        player.setOnBufferingUpdateListener(this);
        player.setOnCompletionListener(this);
        player.setOnDreamWinErrorListener(this);
        player.setOnErrorListener(this);
//        开启防录屏，会使加密视频投屏功能不能正常使用
//        player.setAntiRecordScreen(this);
        //设置CustomId
        player.setCustomId("HIHA2019");
        //获取字幕信息
        sv_subtitle.getSubtitlesInfo(player);
        //获得问答信息
        player.setOnQAMsgListener(new OnQAMsgListener() {
            @Override
            public void onQAMessage(JSONArray jsonArray) {
                if (questions == null) {
                    questions = new TreeMap<>();
                    createQuestionMap(jsonArray);
                }
            }
        });
        player.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                if (qaView != null && qaView.isPopupWindowShown()) {
                    player.pauseWithoutAnalyse();
                }
            }
        });
        //知识点
        if (SERVER_DATA_SOURCE) {
            //使用点播admin配置的知识点相关信息
            player.setKnowledgeListener(new KnowledgeListener() {
                @Override
                public void onKnowledge(final KnowledgeBean knowledgeBean) {
                    MediaPlayActivity.this.knowledgeBean = knowledgeBean;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sb_progress.setTimeAxisStatus(knowledgeBean.isTimeAxisStatus());
                            if (!TextUtils.isEmpty(knowledgeBean.getTitle())) {
                                tvKnowledge.setText(knowledgeBean.getTitle());
                                tvKnowledge.setVisibility(View.VISIBLE);
                            } else {
                                tvKnowledge.setVisibility(View.GONE);
                            }
                        }
                    });
                }

                @Override
                public void onError(int errorCode, String errorMessage, String errorDetail) {
                    tvKnowledge.setVisibility(View.GONE);
                    Log.e(TAG, "onKnowledgeError,errorCode:" + errorCode + ",errorMessage:" + errorMessage + ",errorDetail:" + errorDetail);
                }
            });
        } else {
            //使用自定义数据源
            String localJson = JsonUtil.getLocalJson(this, "knowledge.json");
            this.knowledgeBean = new Gson().fromJson(localJson, KnowledgeBean.class);
            if (knowledgeBean != null) {
                if (!TextUtils.isEmpty(knowledgeBean.getTitle())) {
                    tvKnowledge.setVisibility(View.VISIBLE);
                    tvKnowledge.setText(knowledgeBean.getTitle());
                } else {
                    tvKnowledge.setVisibility(View.GONE);
                }
                sb_progress.setTimeAxisStatus(knowledgeBean.isTimeAxisStatus());
            }
        }

        //答题器相关数据
        player.setOnAnswerSheetListener(new AnswerSheetListener() {
            @Override
            public void onAnswerSheet(List<AnswerSheetInfo> answerSheetInfoList) {
                MediaPlayActivity.this.answerSheetInfoList = answerSheetInfoList;
                sheetTimeList = new ArrayList<>();
                for (int i = 0; i < answerSheetInfoList.size(); i++) {
                    sheetTimeList.add(answerSheetInfoList.get(i).getShowTime());
                }
                Collections.sort(sheetTimeList);
            }

            @Override
            public void onError(int errorCode, String errorMessage, String errorDetail) {

            }

            @Override
            public void onAnswerCommitSuccess(final List<AnswerCommitResult> commitResultList) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        choiceRoot.setVisibility(View.GONE);
                        statisticsLayout.setVisibility(View.VISIBLE);
                        if (currentSheetInfo != null) {
                            backView.setVisibility(currentSheetInfo.getBackSecond() >= 0 ? View.VISIBLE : View.GONE);
                        } else {
                            backView.setVisibility(View.GONE);
                        }
                        boolean result = judgeAnswerResult();
                        statisticsResult.setText(result ? "你答对了" : "你答错了");
                        statisticsResult.setBackgroundResource(result ? R.drawable.shape_answer_right_bg : R.drawable.shape_answer_wrong_bg);
                        StatisticsAdapter statisticsAdapter = new StatisticsAdapter(MediaPlayActivity.this, currentSheetInfo, commitResultList);
                        statisticsRecycler.setAdapter(statisticsAdapter);
                    }
                });
            }

            @Override
            public void onAnswerCommitFailed(int errorCode, String errorMessage) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        commitAnswerErrorLayout.setVisibility(View.VISIBLE);
                        choiceRoot.setVisibility(View.GONE);
                    }
                });
            }
        });

        //视频缩略图相关数据
        player.setThumbnailsCallback(new ThumbnailsCallback() {

            @Override
            public void onThumbnailsInfo(List<String> thumbnailsList) {
                sb_progress.setThumbnails(thumbnailsList);
            }
        });
        //设置访客信息收集监听器
        visitorInfos = new ArrayList<>();
        player.setOnVisitMsgListener(new OnVisitMsgListener() {
            @Override
            public void onVisitMsg(int appearTime, String imageURL, int isJump, String jumpURL, String title, String visitorId, JSONArray visitorMessage) {
                showVisitorTime = appearTime * 1000;
                visitorImageUrl = imageURL;
                visitorIsJump = isJump;
                visitorJumpUrl = jumpURL;
                visitorTitle = title;
                visitorInfoId = visitorId;

                if (visitorMessage != null && visitorMessage.length() > 0) {
                    isShowVisitorInfoDialog = true;
                    for (int i = 0; i < visitorMessage.length(); i++) {
                        try {
                            VisitorInfo visitorInfo = new VisitorInfo(visitorMessage.getJSONObject(i));
                            visitorInfos.add(visitorInfo);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        });

        //课堂练习
        exercises = new ArrayList<>();
        player.setOnExercisesMsgListener(new OnExercisesMsgListener() {
            @Override
            public void onExercisesMessage(JSONArray exArray) {
                if (exArray != null && exArray.length() > 0) {
                    for (int i = 0; i < exArray.length(); i++) {
                        try {
                            Exercise exercise = new Exercise(exArray.getJSONObject(i));
                            exercises.add(exercise);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        //设置鉴权监听器
        player.setOnAuthMsgListener(new OnAuthMsgListener() {

            @Override
            public void onAuthMsg(final int enable, final int freetime, final String messaage, final MarqueeInfo marqueeInfo) {
                runOnUiThread(new Runnable() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void run() {
                        isAllowPlayWholeVideo = enable;
                        freeWatchTime = freetime;
                        freeWatchOverMsg = messaage;
                        if (isAllowPlayWholeVideo == 0) {
                            if (freeWatchTime > 0) {
                                tv_watch_tip.setVisibility(View.VISIBLE);
                            }
                            int minute = freeWatchTime / 60;
                            int second = freeWatchTime % 60;
                            tv_watch_tip.setText(String.format("可试看%d分钟%d秒，购买会员查看完整版", minute, second));
                        }
                        tv_pre_watch_over.setText(freeWatchOverMsg);
                        //设置跑马灯
                        setMarqueeView(marqueeInfo);
                        //透明跑马灯
                        if (marqueeInfo!=null&&player.isInvisibleMarquee()){
                            startTransparentMarquee(marqueeInfo);
                        }
                    }
                });
            }
        });

        //获得视频打点信息
        vibrationInfoList = new ArrayList<>();
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        player.setOnHotspotListener(new OnHotspotListener() {
            @Override
            public void onHotspots(TreeMap<Integer, String> hotspotMap) {
                hotSpotData = hotspotMap;
                if (hotspotMap.size() > 0) {
                    for (Map.Entry<Integer, String> entry : hotspotMap.entrySet()) {
                        vibrationInfoList.add(new VibrationInfo(entry.getKey()));
                    }
                }
            }

            @Override
            public void onHotSpotInfo(LinkedHashMap<Integer, HotSpotInfo> hotSpotInfoList) {
                executeStatue = false;
                sb_progress.setHotspotInfo(hotSpotInfoList);
            }
        });
        // DRM加密播放
        player.setDRMServerPort(HuoDeApplication.getDrmServerPort());
        if (isLocalPlay) {
            btn_download.setVisibility(View.INVISIBLE);
            iv_switch_to_audio.setVisibility(View.GONE);
            //离线播放
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                path = Environment.getExternalStorageDirectory() + "/".concat(ConfigUtil.DOWNLOAD_PATH).concat("/").concat(videoTitle).concat(format);
                if (!new File(path).exists()) {
                    return;
                }
                //离线字幕
                String subtitlePath = Environment.getExternalStorageDirectory() + "/".concat(ConfigUtil.DOWNLOAD_PATH).concat("/").concat(videoTitle).concat("subtitle.srt");
                if (new File(subtitlePath).exists()) {
                    sv_subtitle.initFirstOfflineSubtitleInfo(subtitlePath);
                }
                String subtitlePath2 = Environment.getExternalStorageDirectory() + "/".concat(ConfigUtil.DOWNLOAD_PATH).concat("/").concat(videoTitle).concat("subtitle2.srt");
                if (new File(subtitlePath2).exists()) {
                    sv_subtitle.initSecondOfflineSubtitleInfo(subtitlePath2);
                }
                String subtitleSetPath = Environment.getExternalStorageDirectory() + "/".concat(ConfigUtil.DOWNLOAD_PATH).concat("/").concat(videoTitle).concat("subtitleSet.json");
                if (new File(subtitleSetPath).exists()) {
                    sv_subtitle.initOfflineSubtitleSet(activity, subtitleSetPath);
                }
            }
            //离线跑马灯数据
            if (!TextUtils.isEmpty(marqueeData)) {
                MarqueeInfo marqueeInfo = player.getMarqueeInfo(marqueeData);
                setMarqueeView(marqueeInfo);
                if (isInvisibleMarquee) {
                    startTransparentMarquee(marqueeInfo);
                }
            }
            try {
                player.setOfflineVideoPath(path, activity);
                hideOnlineOperation();
                HuoDeApplication.getDRMServer().resetLocalPlay();
                player.prepareAsync();
                functionHandler.setSignInDate(DataUtil.getSignInList());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //获取广告信息
            getAdInfo();
        }
        //查询弹幕结果
        player.setOnDanMuListListener(new OnDanmuListListener() {
            @Override
            public void onSuccess(final ArrayList<DanmuInfo> danMuInfoList) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (dm_view != null) {
                            dm_view.removeAllDanmakus(true);
                        }
                        for (DanmuInfo danmuInfo : danMuInfoList) {
                            addDanmu(danmuInfo.getContent(), danmuInfo.getFc(), danmuInfo.getPt(), false);
                        }
                        seekToDanmu(currentPosition);
                    }
                });
            }

            @Override
            public void onFail(String msg) {
                danMuSec = currentMinutePos - 1;
            }
        });
        if (BuildConfig.isShowCustomMarquee){
            //自定义跑马灯数据,该数据优先级高于admin配置，如均存在，则仅展示该数据
            //此处以离线json为例，可按需配置数据源由服务器返回或本地存储，按固定的json格式即可
            MarqueeInfo marqueeInfo = player.setCustomMarqueeData(JsonUtil.getLocalJson(this, "marquee.json"));
            if (marqueeInfo!=null){
                startTransparentMarquee(marqueeInfo);
            }
            setMarqueeView(marqueeInfo);
        }
    }

    /**
     * 判断回答结果
     */
    private boolean judgeAnswerResult() {
        ArrayList<AnswerSheetInfo.Answer> rightAnswerList = new ArrayList<>();
        for (int i = 0; i < currentSheetInfo.getAnswers().size(); i++) {
            if (currentSheetInfo.getAnswers().get(i).isRight()) {
                rightAnswerList.add(currentSheetInfo.getAnswers().get(i));
            }
        }
        List<AnswerSheetInfo.Answer> selectedAnswer = choiceAdapter.getSelectedAnswer();
        return selectedAnswer.size() == rightAnswerList.size() && selectedAnswer.containsAll(rightAnswerList);
    }

    private boolean isMultiple(List<AnswerSheetInfo.Answer> answers) {
        if (answers != null && !answers.isEmpty()) {
            int rightCount = 0;
            for (AnswerSheetInfo.Answer answer : answers) {
                if (answer.isRight()) {
                    rightCount++;
                }
            }
            return rightCount != 1;
        }
        return false;
    }

    private void setMarqueeView(MarqueeInfo marqueeInfo) {
        if (marqueeInfo != null) {
            String type = marqueeInfo.getType();
            int loop = marqueeInfo.getLoop();
            ArrayList<MarqueeAction> action = marqueeInfo.getAction();
            mv_video.setLoop(loop);
            mv_video.setMarqueeActions(action);
            MarqueeInfo.TextBean textBean = marqueeInfo.getTextBean();
            MarqueeInfo.ImageBean imageBean = marqueeInfo.getImageBean();
            if (!TextUtils.isEmpty(type) && TextUtils.equals(type, TEXT)) {
                mv_video.setType(MarqueeView.TEXT);
                if (textBean != null) {
                    String content = textBean.getContent();
                    if (!TextUtils.isEmpty(content)) {
                        mv_video.setTextContent(content);
                    }
                    int font_size = textBean.getFont_size();
                    mv_video.setTextFontSize(font_size);
                    String color = textBean.getColor();
                    if (!TextUtils.isEmpty(color) ) {
                        //textColor：文字颜色，格式如：#ffffff
                        mv_video.setTextColor(color);
                    }
                }
            } else if (!TextUtils.isEmpty(type) && TextUtils.equals(type, IMAGE)) {
                mv_video.setType(MarqueeView.IMAGE);
                if (imageBean != null) {
                    mv_video.setMarqueeImage(activity, imageBean.getImage_url(), imageBean.getWidth(), imageBean.getHeight());
                }
            }
        }
    }

    //获得广告信息
    private void getAdInfo() {
        if (netWorkStatus == 0) {
            tv_error_info.setText("请检查你的网络连接");
            showPlayErrorView();
            hideOtherOperations();
            tv_operation.setText("重试");
            tv_operation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    retryPlayTimes = 0;
                    hidePlayErrorView();
                    getAdInfo();
                }
            });
            return;
        }

        if (netWorkStatus == 2) {
            IsUseMobileNetworkDialog isUseMobileNetworkDialog = new IsUseMobileNetworkDialog(activity, new IsUseMobieNetwork() {
                @Override
                public void exit() {
                    finish();
                    isShowUseMobie = false;
                }

                @Override
                public void continuePlay() {
                    requestAd();
                    isShowUseMobie = false;
                }
            });
            if (!isUseMobileNetworkDialog.isShowing()) {
                isUseMobileNetworkDialog.show();
                isShowUseMobie = true;
            }
            return;
        }
        requestAd();
    }

    private void requestAd() {
        startNetSpeedTimer();
        isPrepared = false;
        //广告信息获取
        DWMediaAD dwMediaAD = new DWMediaAD(dwMediaADListener, ConfigUtil.USER_ID, videoId);
        dwMediaAD.getFrontAd();
        dwMediaAD.getPauseAd();
        dwMediaAD.getEndAd();
        updatePlayingItem();
    }

    private final DWMediaADListener dwMediaADListener = new DWMediaADListener() {

        @Override
        public void onFrontAD(final FrontADInfo frontADInfo) {
            if (frontADInfo != null && frontADInfo.getAd() != null) {
                //片头广告播放和点击信息
                List<FrontADInfo.AdBean> frontAd = frontADInfo.getAd();
                final FrontADInfo.AdBean adBean = frontAd.get(0);
                isFrontVideoAd = frontADInfo.isVideo();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideViews();
                        if (frontADInfo.getCanskip() == 1) {
                            tv_skip_ad.setVisibility(View.VISIBLE);
                            skipAdTime = frontADInfo.getSkipTime();
                        } else {
                            tv_skip_ad.setVisibility(View.GONE);
                        }
                        adTime = frontADInfo.getTime();
                        if (frontADInfo.getCanclick() == 1) {
                            tv_know_more.setVisibility(View.VISIBLE);
                        } else {
                            tv_know_more.setVisibility(View.GONE);
                        }

                        if (isFrontVideoAd) {
                            if (adBean != null) {
                                playFrontAd(adBean);
                            }
                        } else {
                            if (adBean != null) {
                                final String material = adBean.getMaterial();
                                videoADClickUrl = adBean.getClickurl();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!TextUtils.isEmpty(material)) {
                                            if (player.isPlaying()) {
                                                player.stop();
                                            }
                                            ll_load_video.setVisibility(View.GONE);
                                            ll_image_ad.setVisibility(View.VISIBLE);
                                            Glide.with(HuoDeApplication.getContext()).load(material).listener(new RequestListener<Drawable>() {
                                                @Override
                                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                    return false;
                                                }

                                                @Override
                                                public boolean onResourceReady(@NonNull Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                    if (resource instanceof GifDrawable) {
                                                        ((GifDrawable) resource).setLoopCount(GifDrawable.LOOP_FOREVER);
                                                    }
                                                    ll_ad.setVisibility(View.VISIBLE);
                                                    startAdTimer();
                                                    return false;
                                                }
                                            }).into(iv_image_ad);
                                        } else {
                                            playVideoOrAudio(isAudioMode, true);
                                        }

                                    }
                                });

                            }
                        }
                    }
                });


            }

        }

        @Override
        public void onPauseAD(PauseADInfo pauseADInfo) {
            pauseADInfoData = pauseADInfo;
        }

        @Override
        public void onEndAD(EndADInfo endADInfo) {
            endADInfoData = endADInfo;
        }

        @Override
        public void onFrontADError(final HuodeException e) {
            //播放正片
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    playVideoOrAudio(isAudioMode, true);
                }
            });
        }

        @Override
        public void onPauseADError(HuodeException e) {

        }

        @Override
        public void onEndADError(HuodeException e) {

        }
    };

    /**
     * 播放片头广告
     */
    private void playFrontAd(final FrontADInfo.AdBean adBean) {
        if (player == null) {
            return;
        }
        try {
            String material = adBean.getMaterial();
            if (!TextUtils.isEmpty(material)) {
                videoADClickUrl = adBean.getClickurl();
                isPlayVideoAd = true;
                isPlayFrontAd = true;
                player.pause();
                player.stop();
                player.reset();
                if (BuildConfig.VerificationVersion == 0){
                    player.setVideoPlayInfo(null, null, null, null, activity);
                }else if (BuildConfig.VerificationVersion == 1){
                    player.setVideoPlayInfo(null, null,null, activity, SdkSidManager.getInstance().getSidProvider());
                }
                player.setDataSource(material);
                player.setSurface(playSurface);
                HuoDeApplication.getDRMServer().reset();
                player.prepareAsync();
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        playVideoOrAudio(isAudioMode, true);
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //播放片尾广告
    private void playEndAd(final EndADInfo.AdBean adBean) {
        try {
            String material = adBean.getMaterial();
            if (!TextUtils.isEmpty(material)) {
                endADInfoData = null;
                videoADClickUrl = adBean.getClickurl();
                isPlayVideoAd = true;
                isPlayEndAd = true;
                isShowImgEndAd = false;
                player.pause();
                player.stop();
                player.reset();
                if (BuildConfig.VerificationVersion == 0){
                    player.setVideoPlayInfo(null, null, null, null, activity);
                }else if (BuildConfig.VerificationVersion == 1){
                    player.setVideoPlayInfo(null, null,null, activity,SdkSidManager.getInstance().getSidProvider());
                }
                player.setDataSource(material);
                player.setSurface(playSurface);
                HuoDeApplication.getDRMServer().reset();
                player.prepareAsync();
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        currentPosition = 0;
                        playNextVideo();
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //展示访客信息框
    private void showVisitorInfoDialog() {
        if (isVideoShowVisitorInfoDialog) {
            return;
        }
        if (isShowVisitorDialog((int) currentPosition) && visitorInfos != null && visitorInfos.size() > 0) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                if (visitorInfoDialog != null && visitorInfoDialog.isShowing()) {
                    return;
                }
                if (portraitVisitorInfoDialog != null && portraitVisitorInfoDialog.isShowing()) {
                    return;
                }
                if (!isShowVisitorInfoDialog) {
                    return;
                }
                visitorInterrupt = true;
                visitorInfoDialog = new LandscapeVisitorInfoDialog(activity, videoId, visitorImageUrl, visitorJumpUrl,
                        visitorTitle, visitorInfoId, visitorIsJump, visitorInfos, new CommitOrJumpVisitorInfo() {
                    @Override
                    public void commit() {
                        visitorInterrupt = false;
                        isShowVisitorInfoDialog = false;
                        if (!isPlayVideo) {
                            playOrPauseVideo();
                        }
                    }

                    @Override
                    public void jump() {
                        visitorInterrupt = false;
                        isShowVisitorInfoDialog = false;
                        if (!isPlayVideo) {
                            playOrPauseVideo();
                        }
                    }
                });
                visitorInfoDialog.setCanceledOnTouchOutside(false);
                isVideoShowVisitorInfoDialog = true;
                visitorInfoDialog.show();
                visitorInfoDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        visitorInterrupt = false;
                        finish();
                    }
                });
            } else {
                if (portraitVisitorInfoDialog != null && portraitVisitorInfoDialog.isShowing()) {
                    return;
                }
                if (visitorInfoDialog != null && visitorInfoDialog.isShowing()) {
                    return;
                }
                if (!isShowVisitorInfoDialog) {
                    return;
                }
                visitorInterrupt = true;
                portraitVisitorInfoDialog = new PortraitVisitorInfoDialog(activity, videoId, visitorImageUrl, visitorJumpUrl,
                        visitorTitle, visitorInfoId, visitorIsJump, visitorInfos, new CommitOrJumpVisitorInfo() {
                    @Override
                    public void commit() {
                        visitorInterrupt = false;
                        isShowVisitorInfoDialog = false;
                        if (!isPlayVideo) {
                            playOrPauseVideo();
                        }
                    }

                    @Override
                    public void jump() {
                        visitorInterrupt = false;
                        isShowVisitorInfoDialog = false;
                        if (!isPlayVideo) {
                            playOrPauseVideo();
                        }
                    }
                });
                portraitVisitorInfoDialog.setCanceledOnTouchOutside(false);
                isVideoShowVisitorInfoDialog = true;
                portraitVisitorInfoDialog.show();
                portraitVisitorInfoDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        visitorInterrupt = false;
                        finish();
                    }
                });
            }
            if (isPlayVideo) {
                playOrPauseVideo();
            }
        }
    }

    private boolean isShowVisitorDialog(int currentPosition) {
        long timeInterval = currentPosition - showVisitorTime;
        return timeInterval >= 0 && timeInterval < 1000;
    }

    private void resetVisitorInfo() {
        if (visitorInfos != null && visitorInfos.size() > 0) {
            visitorInfos.clear();
        }
        visitorInfoDialog = null;
        portraitVisitorInfoDialog = null;
        isShowVisitorInfoDialog = false;
    }

    private void hideOnlineOperation() {
        iv_next_video.setVisibility(View.GONE);
        tv_play_definition.setVisibility(View.GONE);
        tv_video_select.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                if (isFullScreen && !isLocalPlay) {
                    setPortrait();
                } else {
                    finish();
                }
                break;

            case R.id.iv_video_full_screen:
            case R.id.iv_ad_full_screen:
                if (isFullScreen) {
                    setPortrait();
                } else {
                    setLandScape();
                }
                break;
            case R.id.iv_next_video:
                playNextVideo();
                break;
            case R.id.tvKnowledge:
                //知识点
                hideViews();
                if (knowledgeBean == null) {
                    MultiUtils.showToast(activity, "无可用知识点相关数据");
                    return;
                }
                initKnowledgeDialog();
                showKnowledgeDialog();
                break;
            case R.id.tv_play_definition:
                hideViews();
                selectDefinition();
                break;
            case R.id.tv_play_speed:
                hideViews();
                SelectSpeedDialog selectSpeedDialog = new SelectSpeedDialog(activity, currentSpeed, new SelectSpeed() {
                    @Override
                    public void selectedSpeed(float speed) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            player.setSpeed(speed);
                        }
                        currentSpeed = speed;
                    }
                });
                selectSpeedDialog.show();
                break;
            case R.id.tv_video_select:
                hideViews();
                selectVideo();
                break;
            case R.id.iv_play_pause:
                if (coverImage.getVisibility() == View.VISIBLE) {
                    coverImage.setVisibility(View.GONE);
                }
                playOrPauseVideo();
                break;
            case R.id.iv_more_settings:
                showMoreSettings();
                break;
            case R.id.btn_download:
                btn_download.setVisibility(View.GONE);
                ll_confirm_or_cancel.setVisibility(View.VISIBLE);
                isShowSelectButton(true);
                break;
            case R.id.btn_confirm:
                confirmDownload();
                break;
            case R.id.iv_switch_to_audio:
                if (currentMode!=MediaMode.VIDEOAUDIO){
                    if (currentMode == MediaMode.AUDIO&&isAudioMode){
                        MultiUtils.showToast(activity, "不支持视频" );
                        return;
                    }else if (currentMode == MediaMode.VIDEO&&!isAudioMode){
                        MultiUtils.showToast(activity, "不支持音频");
                        return;
                    }
                }
                //音视频互相切换
                player.playModelChanged();
                switchDefPos = player.getCurrentPosition();
                if (isAudioMode) {
                    isAudioMode = false;
                    playVideoOrAudio(isAudioMode, false);
                } else {
                    isAudioMode = true;
                    playVideoOrAudio(isAudioMode, false);
                }
                break;
            case R.id.btn_cancel:
                btn_download.setVisibility(View.VISIBLE);
                ll_confirm_or_cancel.setVisibility(View.GONE);
                isShowSelectButton(false);
                break;
            case R.id.iv_create_gif:
                //录制gif图
                startCreateGif();
                hideViews();
                break;
            case R.id.iv_save_gif:
                //保存gif图
                MultiUtils.showToast(activity, "gif保存在：" + gifFile.getAbsolutePath());
                cancelGif();
                break;
            case R.id.gif_cancel:
                //取消制作gif
                cancelGif();
                break;
            case R.id.iv_gif_stop:
                //停止录制gif
                stopGif();
                break;
            case R.id.iv_lock_or_unlock:
                if (isLock) {
                    isLock = false;
                    iv_lock_or_unlock.setImageResource(R.mipmap.iv_unlock);
                    showViews();
                } else {
                    isLock = true;
                    iv_lock_or_unlock.setImageResource(R.mipmap.iv_lock);
                    hideViews();
                }
                break;
            case R.id.tv_know_more:
            case R.id.iv_image_ad:
                //点击广告的了解更多
                knowMoreFrontAdInfo();
                break;
            case R.id.tv_skip_ad:
                if (isCanClickAd) {
                    playVIdeoAfterAd();
                }
                break;
            case R.id.iv_pause_ad:
                if (!TextUtils.isEmpty(pauseAdClickUrl)) {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri uri = Uri.parse(pauseAdClickUrl);
                    intent.setData(uri);
                    startActivity(intent);
                    rl_pause_ad.setVisibility(View.GONE);
                }
                break;
            case R.id.iv_close_pause_ad:
                rl_pause_ad.setVisibility(View.GONE);
                break;
            //重新试看
            case R.id.ll_rewatch:
                player.seekTo(0,MediaPlayer.SEEK_CLOSEST);
                player.start();
                ll_pre_watch_over.setVisibility(View.GONE);
                tv_watch_tip.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_small_window_play:
                useSmallWindowPlay();
                break;
            case R.id.iv_landscape_screenshot:
            case R.id.iv_portrait_screenshot:
                getVideoScreenShot();
                break;
            case R.id.tv_input_danmu:
                sendDanMu(false);
                break;
            case R.id.iv_landscape_danmu_set:
            case R.id.iv_portrait_danmu_set:
                showDanMuSet();
                break;
            case R.id.ll_portrait_danmu_off:
                openDanMu();
                break;
            case R.id.iv_portrait_danmu_on:
                closeDanMu();
                break;
            case R.id.iv_landscape_danmu_switch:
                if (isDanMuOn) {
                    closeDanMu();
                } else {
                    openDanMu();
                }
                break;
            case R.id.tv_portrait_input_danmu:
                sendDanMu(true);
                break;
            //跳过该问题
            case R.id.choiceSkip:
                //as
                choiceRoot.setVisibility(View.GONE);
                player.start();
                break;
            //提交答案
            case R.id.commitAnswer:
            case R.id.resubmit:
                //提交答案或提交失败，重新提交
                if (choiceAdapter != null) {
                    player.onSubmitAnswer(currentSheetInfo.getId(), choiceAdapter.getSelectedAnswer());
                }
                break;
            //提交答案失败，放弃提交，继续播放
            case R.id.resumePlay:
                commitAnswerErrorLayout.setVisibility(View.GONE);
                break;
            case R.id.dismissErrorLayout:
                commitAnswerErrorLayout.setVisibility(View.GONE);
                player.start();
                break;
            //关闭统计布局
            case R.id.dismissStatistics:
                statisticsLayout.setVisibility(View.GONE);
                iv_play_pause.setImageResource(R.mipmap.iv_play);
                break;
            case R.id.goOnPlay:
                statisticsLayout.setVisibility(View.GONE);
                iv_play_pause.setImageResource(R.mipmap.iv_pause);
                player.start();
                break;
            //答题器，回看
            case R.id.backView:
                if (currentSheetInfo != null) {
                    int backSecond = currentSheetInfo.getBackSecond();
                    if (backSecond != -1) {
                        player.seekTo(backSecond * 1000,MediaPlayer.SEEK_CLOSEST);
                    }
                }
                player.start();
                statisticsLayout.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    //打开弹幕
    private void openDanMu() {
        showInputDanMu();
        isDanMuOn = true;
        if (!dm_view.isShown()) {
            dm_view.show();
        }
        resumeDanmu();
        seekToDanmu(currentPosition);
    }

    private void showInputDanMu() {
        ll_portrait_danMu_off.setVisibility(View.GONE);
        ll_portrait_danMu_on.setVisibility(View.VISIBLE);
        ll_landscape_danMu_set_send.setVisibility(View.VISIBLE);
        iv_landscape_danMu_switch.setImageResource(R.mipmap.iv_danmu_on);
    }

    //关闭弹幕
    private void closeDanMu() {
        ll_portrait_danMu_on.setVisibility(View.GONE);
        ll_portrait_danMu_off.setVisibility(View.VISIBLE);
        ll_landscape_danMu_set_send.setVisibility(View.INVISIBLE);
        iv_landscape_danMu_switch.setImageResource(R.mipmap.iv_danmu_off);
        isDanMuOn = false;
        if (dm_view.isShown()) {
            dm_view.hide();
        }
        pauseDanmu();
    }

    //弹幕设置
    private void showDanMuSet() {
        hideViews();
        DanmuSetDialog danmuSetDialog = new DanmuSetDialog(activity, isFullScreen, currentOpaqueness, currentFontSizeLevel, currentDanMuSpeedLevel, currentDisplayArea, new OnDanmuSet() {
            @Override
            public void setOpaqueness(int opaqueness) {
                currentOpaqueness = opaqueness;
                float opaquenessFloat = MultiUtils.calFloat(2, opaqueness, 100);
                danmakuContext.setDanmakuTransparency(opaquenessFloat);
            }

            @Override
            public void setFontSizeLevel(int fontSizeLevel) {
                currentFontSizeLevel = fontSizeLevel;
                if (fontSizeLevel == 0) {
                    fontSizeScale = 0.5f;
                } else if (fontSizeLevel == 1) {
                    fontSizeScale = 1.0f;
                } else if (fontSizeLevel == 2) {
                    fontSizeScale = 1.5f;
                } else if (fontSizeLevel == 3) {
                    fontSizeScale = 2.0f;
                } else if (fontSizeLevel == 4) {
                    fontSizeScale = 2.5f;
                }
                danmakuContext.setScaleTextSize(fontSizeScale);
            }

            @Override
            public void setDanmuSpeed(int danMuSpeedLevel) {
                currentDanMuSpeedLevel = danMuSpeedLevel;
                if (danMuSpeedLevel == 0) {
                    danMuSpeed = 2.0f;
                } else if (danMuSpeedLevel == 1) {
                    danMuSpeed = 1.5f;
                } else if (danMuSpeedLevel == 2) {
                    danMuSpeed = 1.0f;
                } else if (danMuSpeedLevel == 3) {
                    danMuSpeed = 0.75f;
                } else if (danMuSpeedLevel == 4) {
                    danMuSpeed = 0.5f;
                }
                danmakuContext.setScrollSpeedFactor(danMuSpeed);
            }

            @Override
            public void setDisplayArea(int displayArea) {
                currentDisplayArea = displayArea;
                float displayAreaFloat = MultiUtils.calFloat(2, currentDisplayArea, 100);
                int height = rl_play_video.getHeight();
                int danMuViewHeight = (int) (displayAreaFloat * height);
                ViewGroup.LayoutParams layoutParams = rl_danMu.getLayoutParams();
                layoutParams.height = danMuViewHeight;
                rl_danMu.setLayoutParams(layoutParams);
            }
        });
        danmuSetDialog.show();
        danmuSetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (!isFullScreen) {
                    iv_portrait_danMu_set.setImageResource(R.mipmap.iv_danmu_set_gray);
                }
            }
        });
    }

    //发送弹幕
    private void sendDanMu(boolean isPortrait) {
        if (!isCanSendDanMu) {
            return;
        }
        final EditDanmuTextDialog editDanmuTextDialog = new EditDanmuTextDialog(activity, isPortrait, new OnEditDanmuText() {
            @Override
            public void getDanmuText(final String danMuText, final String danMuColor) {

                player.sendDanMu(videoId, danMuText, currentPosition, danMuColor, new OnSendDanmuListener() {
                    @Override
                    public void onSuccess() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                isCanSendDanMu = false;
                                sendDanMuInterval = 5;
                                addDanmu(danMuText, danMuColor, currentPosition, true);
                            }
                        });
                    }

                    @Override
                    public void onFail(final String msg) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MultiUtils.showToast(activity, "发送弹幕失败：" + msg);
                            }
                        });
                    }
                });

            }
        });
        editDanmuTextDialog.show();
        isEditDanMu = true;
        if (isPlayVideo && isFullScreen) {
            playOrPauseVideo();
            isPlayAfterSendDanMu = true;
        } else if (isFullScreen) {
            isPlayAfterSendDanMu = false;
        }
        editDanmuTextDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                isEditDanMu = false;
                if (isPlayAfterSendDanMu && isFullScreen) {
                    playOrPauseVideo();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MultiUtils.hideSoftKeyboard(tv_video);
                    }
                }, 100);
            }
        });
    }

    //获取视频截图
    private void getVideoScreenShot() {
        String videoScreenShotOutPath = MultiUtils.getVideoScreenShotOutPath();
        if (!TextUtils.isEmpty(videoScreenShotOutPath)) {
            Bitmap bitmap = tv_video.getBitmap();
            boolean videoScreenShot = player.getVideoScreenShot(bitmap, videoScreenShotOutPath);
            if (videoScreenShot) {
                //通知系统相册更新
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.fromFile(new File(videoScreenShotOutPath))));
                MultiUtils.showToast(activity, "已截图");
            }
        }
    }

    //8.0及以上系统支持小窗播放
    private void useSmallWindowPlay() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (builder == null) {
                builder = new PictureInPictureParams.Builder();
            }
            int width = rl_play_video.getWidth();
            int height = rl_play_video.getHeight();
            // 设置宽高比例值
            if (width > 0 && height > 0) {
                float whRate = MultiUtils.calFloat(2, width, height);
                if (whRate > 0.42 && whRate < 2.39) {
                    Rational aspectRatio = new Rational(width, height);
                    builder.setAspectRatio(aspectRatio);
                }
            }

            if (actions != null && actions.size() > 0) {
                actions.clear();
            }

            if (player.isPlaying()) {
                actions.add(pauseRemoteAction);
            } else {
                actions.add(playRemoteAction);
            }
            if (actions != null && actions.size() > 0) {
                builder.setActions(actions);
            }
            // 进入小窗模式
            enterPictureInPictureMode(builder.build());
        }

    }

    private void updateSmallWindowActions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (builder == null) {
                builder = new PictureInPictureParams.Builder();
            }
            if (actions != null && actions.size() > 0) {
                actions.clear();
            }

            if (player.isPlaying()) {
                actions.add(pauseRemoteAction);
            } else {
                actions.add(playRemoteAction);
            }
            if (actions != null && actions.size() > 0) {
                builder.setActions(actions);
            }
            setPictureInPictureParams(builder.build());
        }

    }

    @Override
    public void onChoiceSelectStateChange(List<AnswerSheetInfo.Answer> selectedAnswer, int currentPosition, int prePosition, boolean currentSelected) {
        if (choiceAdapter.isMultiple()) {
            RecyclerView.ViewHolder holder = choiceRecycler.findViewHolderForLayoutPosition(currentPosition);
            if (holder != null) {
                View view = holder.itemView.findViewById(R.id.choiceItem);
                view.setBackgroundResource(currentSelected ? R.mipmap.answer_selected : R.drawable.shape_answer_normal);
            }
        } else {
            if (prePosition == -1) {
                return;
            }
            RecyclerView.ViewHolder holder = choiceRecycler.findViewHolderForLayoutPosition(prePosition);
            if (holder != null) {
                View view = holder.itemView.findViewById(R.id.choiceItem);
                view.setBackgroundResource(R.drawable.shape_answer_normal);
            }
        }
    }

    class SmallWindowReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int control = intent.getIntExtra("control", 0);
            if (control == 1 || control == 2) {
                if (signinRoot.getVisibility() !=View.VISIBLE){
                    playOrPauseVideo();
                    updateSmallWindowActions();
                }
            } else if (control == 3) {
                if (isSmallWindow) {
                    finish();
                }
            }
        }
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        isSmallWindow = isInPictureInPictureMode;
        if (isInPictureInPictureMode) {
            if (iv_back.getVisibility() == View.VISIBLE) {
                hideViews();
            }
            if (dm_view.isShown()) {
                dm_view.hide();
            }
        } else {
            if (signinRoot.getVisibility() == View.VISIBLE){
                signinRoot.setVisibility(View.GONE);
                if (signinRunnable == null){
                    signinRunnable = new Runnable() {
                        @Override
                        public void run() {
                            functionHandler.reShowSignin();
                        }
                    };
                }
                handler.postDelayed(signinRunnable,500);
            }
            if (!dm_view.isShown()) {
                dm_view.show();
            }
        }
    }

    //发送弹幕
    private void addDanmu(String danmuText, String danmuColor, long time, boolean isSend) {
        BaseDanmaku danmaku = danmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        if (danmaku == null || dm_view == null) {
            return;
        }
        danmaku.text = danmuText;
        danmaku.padding = 5;
        danmaku.isLive = false;
        danmaku.priority = 0;
        if (isSend) {
            danmaku.setTime(time + 1500);
        } else {
            danmaku.setTime(time);
        }
        danmaku.textSize = 60f;
        if (!TextUtils.isEmpty(danmuColor) && danmuColor.length() == 8) {
            String textColor = "#" + danmuColor.substring(2, 8);
            if (textColor.length() == 7) {
                //textColor：颜色，格式如：#ffffff
                int color = Color.parseColor(textColor);
                danmaku.textColor = color;
                if (isSend) {
                    danmaku.borderColor = color;
                }
            }
        }
        danmaku.textShadowColor = Color.BLACK;
        dm_view.addDanmaku(danmaku);
    }

    private void seekToDanmu(long time) {
        if (dm_view != null && dm_view.isPrepared() && isDanMuOn) {
            dm_view.seekTo(time);
        }
    }

    private void pauseDanmu() {
        if (dm_view != null && dm_view.isPrepared()) {
            dm_view.pause();
        }
    }

    private void resumeDanmu() {
        if (dm_view != null && dm_view.isPrepared() && dm_view.isPaused()) {
            dm_view.resume();
        }
    }

    private void knowMoreFrontAdInfo() {
        if (!TextUtils.isEmpty(videoADClickUrl)) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri uri = Uri.parse(videoADClickUrl);
            intent.setData(uri);
            startActivity(intent);
        }
    }

    private void hideViews() {
        hideOtherOperations();
        iv_create_gif.setVisibility(View.GONE);
        iv_landscape_screenshot.setVisibility(View.GONE);
        iv_back.setVisibility(View.GONE);
        iv_lock_or_unlock.setVisibility(View.GONE);
    }

    private void showViews() {
        controlHide = 8;
        showOtherOperations();
        if (isFullScreen ) {
            iv_lock_or_unlock.setVisibility(View.VISIBLE);
        } else {
            iv_lock_or_unlock.setVisibility(View.GONE);
        }

        if (isAudioMode) {
            iv_create_gif.setVisibility(View.GONE);
            iv_landscape_screenshot.setVisibility(View.GONE);
            iv_portrait_screenshot.setVisibility(View.GONE);
        } else {
            if (isFullScreen) {
                iv_create_gif.setVisibility(View.VISIBLE);
                iv_landscape_screenshot.setVisibility(View.VISIBLE);
            }
        }
        iv_back.setVisibility(View.VISIBLE);
    }

    //确认下载
    private void confirmDownload() {
        btn_download.setVisibility(View.VISIBLE);
        ll_confirm_or_cancel.setVisibility(View.GONE);
        isShowSelectButton(false);
        int downloadCount = 0;
        int selectedCount = 0;
        ArrayList<DownloadConfig> downloadConfigs = new ArrayList<>();
        ArrayList<LogoInfo> logoInfos = new ArrayList<>();
        for (int i = 0; i < videoList.size(); i++) {
            HuodeVideoInfo videoInfo = videoList.get(i);
            if (videoInfo.isSelectedDownload()) {
                String videoTitle = videoInfo.getVideoTitle();
                String videoCover = videoInfo.getVideoCover();
                //过滤掉本地已存在或正在下载中的文件，不重复下载
                if (!VodDownloadManager.getInstance().isExistDownloadInfo(videoTitle) && !DataSet.hasDownloadInfo(videoTitle)) {
                    DownloadConfig downloadConfig = new DownloadConfig(videoInfo.getVideoId(), verificationCode, videoTitle,
                            0, 0, videoCover, player.getSubtitleModel(), player.getMarqueeData(),player.isInvisibleMarquee());
                    downloadConfigs.add(downloadConfig);
                    downloadCount++;
                    if (!TextUtils.isEmpty(logoUrl)) {
                        LogoInfo logoInfo = new LogoInfo(videoTitle, logoUrl);
                        logoInfos.add(logoInfo);
                    }
                }
                selectedCount++;
            }
        }
        VodDownloadManager.getInstance().insertBatchDownload(downloadConfigs);
        SaveLogoUtil.saveLogo(logoInfos);
        if (downloadCount > 0) {
            MultiUtils.showToast(activity, "文件已加入下载队列");
        } else {
            if (selectedCount > 0) {
                MultiUtils.showToast(activity, "文件已存在");
            }
        }
    }

    private void isShowSelectButton(boolean isSHow) {
        if (!isSHow) {
            if (batchDownload != null && batchDownload.size() > 0) {
                batchDownload.clear();
            }
        }
        for (int i = 0; i < videoList.size(); i++) {
            videoList.get(i).setShowSelectButton(isSHow);
            if (isSHow) {
                videoList.get(i).setSelectedDownload(false);
            }
        }
        playListAdapter.notifyDataSetChanged();
    }

    //更多设置
    private void showMoreSettings() {
        int selectedSubtitle = sv_subtitle.getSelectedSubtitle();
        String firstSubName = sv_subtitle.getFirstSubName();
        String secondSubName = sv_subtitle.getSecondSubName();
        MoreSettingsDialog moreSettingsDialog = new MoreSettingsDialog(activity, isAudioMode, currentVideoSizePos, selectedSubtitle, firstSubName, secondSubName, currentBrightness, new MoreSettings() {
            @Override
            public void playAudioOrVideo() {
                player.playModelChanged();
                if (isLocalPlay) {
                    MultiUtils.showToast(activity, "本地播放不支持切换");
                    return;
                }
                //播放音频
                switchDefPos = player.getCurrentPosition();
                isAudioMode = !isAudioMode;
                playVideoOrAudio(isAudioMode, false);
            }

            @Override
            public void checkNetWork() {
                //网络检测
                if (playInfo != null) {
                    CheckNetworkDialog checkNetworkDialog = new CheckNetworkDialog(activity, videoId, playInfo);
                    checkNetworkDialog.show();
                }
            }

            @Override
            public void downloadVideo() {
                downloadFile();
            }

            @Override
            public void setVideoSize(int position) {
                setSize(position);
            }

            @Override
            public void setSubTitle(int selectedSubtitle) {
                sv_subtitle.setSubtitle(selectedSubtitle);
            }

            @Override
            public void setBrightness(int brightness) {
                currentBrightness = brightness;
            }

            @Override
            public void smallWindowPlay() {
                useSmallWindowPlay();
            }
        });
        moreSettingsDialog.show();
        hideViews();
        moreSettingsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                showOtherOperations();
            }
        });
    }
    /**
     * 下载文件
     */
    private void downloadFile() {
        String downloadTitle = videoTitle;
        //0：音视频模式 1：下载视频 2：下载音频 默认下载视频
        int downloadMode = 1;
        if (!isAudioMode) {
            downloadTitle = downloadTitle + "-" + currentDefinition;
            downloadMode = 1;
        } else {
            downloadMode = 2;
        }
        if (VodDownloadManager.getInstance().isExistDownloadInfo(downloadTitle) || DataSet.hasDownloadInfo(downloadTitle)) {
            MultiUtils.showToast(activity, "文件已存在");
            return;
        }
        DownloadConfig downloadConfig;
        if (isAudioMode) {
            downloadConfig = new DownloadConfig(videoId, verificationCode, downloadTitle, downloadMode, 0,
                    videoCover, player.getSubtitleModel(), player.getMarqueeData(),player.isInvisibleMarquee());
        } else {
            downloadConfig = new DownloadConfig(videoId, verificationCode, downloadTitle, downloadMode,
                    currentDefinition, videoCover, player.getSubtitleModel(), player.getMarqueeData(),player.isInvisibleMarquee());
        }
        VodDownloadManager.getInstance().insertDownload(downloadConfig);

        ArrayList<LogoInfo> logoInfoList = new ArrayList<>();
        if (!TextUtils.isEmpty(logoUrl)) {
            LogoInfo logoInfo = new LogoInfo(downloadTitle, logoUrl);
            logoInfoList.add(logoInfo);
        }
        SaveLogoUtil.saveLogo(logoInfoList);
        MultiUtils.showToast(activity, "文件已加入下载队列");
    }

    /**
     * 设置画面尺寸
     */
    private void setSize(int position) {
        currentVideoSizePos = position;
        if (videoHeight > 0) {
            ViewGroup.LayoutParams videoParams = tv_video.getLayoutParams();
            int landVideoHeight = MultiUtils.getScreenHeight(activity);
            int landVideoWidth = landVideoHeight * videoWidth / videoHeight;
            int screenHeight = MultiUtils.getScreenWidth(activity);
            if (landVideoWidth > screenHeight) {
                landVideoWidth = screenHeight;
                landVideoHeight = landVideoWidth * videoHeight / videoWidth;
            }
            if (position == 0) {
                landVideoHeight = MultiUtils.getScreenHeight(activity);
                landVideoWidth = MultiUtils.getScreenWidth(activity);
            } else if (position == 2) {
                landVideoHeight = (int) (0.75 * landVideoHeight);
                landVideoWidth = (int) (0.75 * landVideoWidth);
            } else if (position == 3) {
                landVideoHeight = (int) (0.5 * landVideoHeight);
                landVideoWidth = (int) (0.5 * landVideoWidth);
            }
            videoParams.height = landVideoHeight;
            videoParams.width = landVideoWidth;
            tv_video.setLayoutParams(videoParams);
        }
    }
    private void pause(){
        player.pause();
        pauseDanmu();
        isPlayVideo = false;
        iv_play_pause.setImageResource(R.mipmap.iv_play);
        //展示暂停广告
        if (pauseADInfoData != null) {
            if (exeDialog != null && exeDialog.isShowing()) {
                return;
            }
            if (doExerciseDialog != null && doExerciseDialog.isShowing()) {
                return;
            }
            if (qaView != null && qaView.isPopupWindowShown()) {
                return;
            }
            if (portraitVisitorInfoDialog != null && portraitVisitorInfoDialog.isShowing()) {
                return;
            }
            if (visitorInfoDialog != null && visitorInfoDialog.isShowing()) {
                return;
            }
            if (isEditDanMu) {
                return;
            }
            boolean isPauseVideoAd = pauseADInfoData.isVideo();
            List<PauseADInfo.AdBean> ad = pauseADInfoData.getAd();
            if (ad != null && ad.get(0) != null) {
                String material = ad.get(0).getMaterial();
                pauseAdClickUrl = ad.get(0).getClickurl();
                if (isPauseVideoAd) {
                    if (!TextUtils.isEmpty(material)) {
                        pauseVideoAdDialog = new PauseVideoAdDialog(activity, isFullScreen, material, pauseAdClickUrl);
                        pauseVideoAdDialog.show();
                    }
                } else {
                    if (!TextUtils.isEmpty(material)) {
                        rl_pause_ad.setVisibility(View.VISIBLE);
                        Glide.with(HuoDeApplication.getContext()).load(material).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                if (resource != null) {
                                    if (resource instanceof GifDrawable) {
                                        ((GifDrawable) resource).setLoopCount(GifDrawable.LOOP_FOREVER);
                                    }

                                    int intrinsicWidth = resource.getIntrinsicWidth();
                                    int intrinsicHeight = resource.getIntrinsicHeight();
                                    if (intrinsicWidth > 0) {
                                        int screenWidth = MultiUtils.getScreenWidth(activity);
                                        int newWidth = (int) (0.6 * screenWidth);
                                        int newHeight = newWidth * intrinsicHeight / intrinsicWidth;
                                        ViewGroup.LayoutParams pauseAdParams = rl_pause_ad.getLayoutParams();
                                        pauseAdParams.height = newHeight;
                                        pauseAdParams.width = newWidth;
                                        rl_pause_ad.setLayoutParams(pauseAdParams);
                                    }
                                }
                                return false;
                            }
                        }).into(iv_pause_ad);
                    }
                }

            }
        }
    }
    private void start(){
        if (exeDialog != null && exeDialog.isShowing()) {
            return;
        }
        if (doExerciseDialog != null && doExerciseDialog.isShowing()) {
            return;
        }
        player.start();
        resumeDanmu();
        isPlayVideo = true;
        iv_play_pause.setImageResource(R.mipmap.iv_pause);
        if (rl_pause_ad.getVisibility() == View.VISIBLE) {
            rl_pause_ad.setVisibility(View.GONE);
        }
        if (pauseVideoAdDialog != null && pauseVideoAdDialog.isShowing()) {
            pauseVideoAdDialog.dismiss();
            pauseVideoAdDialog = null;
        }
    }
    //暂停或开始播放
    private void playOrPauseVideo() {
        if (coverImage.getVisibility() == View.VISIBLE) {
            coverImage.setVisibility(View.INVISIBLE);
        }
        if (player.isPlaying()) {
           pause();
        } else {
            start();
        }
    }

    /**
     * 播放视频
     */
    private void startPlay() {
        iv_play_pause.setImageResource(R.mipmap.iv_pause);
        if (!player.isPlaying()) {
            player.start();
        }
    }


    /**
     * 初始化知识点ui
     */
    private void initKnowledgeDialog() {
        knowledgeDialog = new KnowledgeDialog(activity, knowledgeBean);
        knowledgeDialog.setKnowledgeCallback(new KnowledgeDialog.KnowledgeCallback() {
            @Override
            public void onKnowledgeClick(KnowledgeBean.Category.Info info, boolean pauseStatue) {
                knowledgeStartTime = info.getStartTime();
                knowledgeEndTime = info.getEndTime();
                knowledgePauseStatue = pauseStatue;
                String desc = info.getDesc();
                if (player != null) {
                    player.seekTo(knowledgeStartTime * 1000,MediaPlayer.SEEK_CLOSEST);
                    if (!player.isPlaying()) {
                        player.start();
                    }
                }
                if (!TextUtils.isEmpty(desc) && desc != null) {
                    Toast.makeText(MediaPlayActivity.this, desc, Toast.LENGTH_SHORT).show();
                }
                if (knowledgeEndTime != 0) {
                    long duration = player.getDuration();
                    float startPosition = (float) knowledgeStartTime * 1000 / duration;
                    float endPosition = (float) knowledgeEndTime * 1000 / duration;
                    sb_progress.setKnowledgeTime(knowledgeStartTime, knowledgeEndTime);
                    sb_progress.setKnowledgeRange(startPosition, endPosition);
                    sb_progress.invalidate();
                }
                showOtherOperations();
            }
        });
    }

    /**
     * 知识点
     */
    private void showKnowledgeDialog() {
        knowledgeDialog.show();
        hideOtherOperations();
    }

    //选集
    private void selectVideo() {
        SelectVideoDialog selectVideoDialog = new SelectVideoDialog(activity, videoList, videoId, new SelectVideo() {
            @Override
            public void selectedVideo(String selectedVideoTitle, String selectedVideoId, String selectedVideoCover) {
                videoId = selectedVideoId;
                videoTitle = selectedVideoTitle;
                videoCover = selectedVideoCover;
                resetInfo();
                getAdInfo();
                executeStatue = false;
                //TODO 如果不使用admin配置的知识点相关数据，需在这里重新赋值knowledgeBean
                if (!videoList.isEmpty() && !SERVER_DATA_SOURCE) {
                    if (TextUtils.equals(videoId, videoList.get(0).getVideoId())) {
                        String localJson = JsonUtil.getLocalJson(MediaPlayActivity.this, "knowledge.json");
                        knowledgeBean = new Gson().fromJson(localJson, KnowledgeBean.class);
                        if (knowledgeBean != null) {
                            if (!TextUtils.isEmpty(knowledgeBean.getTitle())) {
                                tvKnowledge.setVisibility(View.VISIBLE);
                                tvKnowledge.setText(knowledgeBean.getTitle());
                            } else {
                                tvKnowledge.setVisibility(View.GONE);
                            }
                            sb_progress.setTimeAxisStatus(knowledgeBean.isTimeAxisStatus());
                        }
                    }
                }
            }
        });
        selectVideoDialog.show();
        hideOtherOperations();
        selectVideoDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                iv_play_pause.setImageResource(ConfigUtil.AutoPlay ? R.mipmap.iv_pause : R.mipmap.iv_play);
                showOtherOperations();
            }
        });
    }

    //显示视频操作
    private void showOtherOperations() {
        ll_progress_and_fullscreen.setVisibility(View.VISIBLE);
        ll_title_and_audio.setVisibility(View.VISIBLE);
        if (!isFullScreen) {
            iv_switch_to_audio.setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                iv_small_window_play.setVisibility(View.VISIBLE);
            }
            if (!isAudioMode) {
                iv_portrait_screenshot.setVisibility(View.VISIBLE);
            }
        }
        ll_title_and_audio.setBackgroundColor(getResources().getColor(R.color.play_ope_bac_color));
        iv_back.setVisibility(View.VISIBLE);
    }

    //隐藏视频操作
    private void hideOtherOperations() {
        ll_progress_and_fullscreen.setVisibility(View.INVISIBLE);
        ll_title_and_audio.setVisibility(View.INVISIBLE);
        ll_volume.setVisibility(View.GONE);
        ll_brightness.setVisibility(View.GONE);
        tv_slide_progress.setVisibility(View.GONE);
        iv_portrait_screenshot.setVisibility(View.GONE);
    }

    //切换清晰度
    private void selectDefinition() {
        SelectDefinitionDialog selectDefinitionDialog = new SelectDefinitionDialog(activity, currentDefinition, definitions, new SelectDefinition() {
            @Override
            public void selectedDefinition(String definitionText, int definition) {
                tv_play_definition.setText(definitionText);
                try {
                    currentDefinition = definition;
                    switchDefPos = player.getCurrentPosition();
                    ll_load_video.setVisibility(View.VISIBLE);
                    hideOtherOperations();
                    player.reset();
                    player.setSurface(playSurface);
                    HuoDeApplication.getDRMServer().reset();
                    isPrepared = false;
                    player.setDefaultDefinition(definition);
                    player.setDefinition(activity, definition);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        selectDefinitionDialog.show();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        playSurface = new Surface(surface);
        player.setSurface(playSurface);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        if (player.getOnSubtitleMsgListener() != null) {
            player.getOnSubtitleMsgListener().onSizeChanged(width, height);
        }
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        if (isGifStart) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTimeMillis > 100) {
                Bitmap bitmap = tv_video.getBitmap(gifVideoWidth, gifVideoHeight);
                gifMakerThread.addBitmap(bitmap);
                lastTimeMillis = currentTime;
            }
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        this.isPrepareing = false;
        playInfo = player.getPlayInfo();

        if (playInfo != null) {
            playUrl = playInfo.getPlayUrl();
            currentDefinition = playInfo.getCurrentDefinition();
            sb_progress.setDuration(player.getDuration());
        }
        if (!isLocalPlay) {
            if (playInfo != null && !TextUtils.isEmpty(playInfo.getCoverImage())) {
                Glide.with(this).load(playInfo.getCoverImage())
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(coverImage);
            }
            if (player.definitionChanged()) {
                coverImage.setVisibility(View.INVISIBLE);
                iv_play_pause.setImageResource(R.mipmap.iv_pause);
            } else {
                coverImage.setVisibility(player.isAutoPlay() ? View.INVISIBLE : View.VISIBLE);
            }
        } else {
            coverImage.setVisibility(View.GONE);
        }
        definitions = player.getDefinitions();
        if (playInfo != null) {
            if (definitions!=null){
                for (String key:definitions.keySet()){
                    Integer integer = definitions.get(key);
                    if (currentDefinition == integer){
                        tv_play_definition.setText(key);
                        break;
                    }
                }
            }
        }
        isPrepared = true;
        retryPlayTimes = 0;
        //切换清晰度续播
        if (switchDefPos > 0) {
            if (coverImage.getVisibility() == View.VISIBLE) {
                coverImage.setVisibility(View.INVISIBLE);
            }
            player.seekTo((int) switchDefPos,MediaPlayer.SEEK_CLOSEST);
            iv_play_pause.setImageResource(R.mipmap.iv_pause);
            startPlay();
            resumeDanmu();
            isPlayVideo = true;
        } else {
            if (lastPlayPosition > 0) {
                playedTime = lastPlayPosition;
                if (isPlayVideoAd) {
                    lastPlayPosition = 0;
                }
                if (isPlayVideoAd) {
                    startPlay();
                } else {
                    //从上次播放的位置开始播放
                    player.seekTo(lastPlayPosition,MediaPlayer.SEEK_CLOSEST);
                    startPlay();
                    danMuSec = (lastPlayPosition / GET_DAN_MU_INTERVAL) - 1;
                    //从记忆播放处展示课堂练习
                    isShowConfirmExerciseDialog = isShowExercise(lastPlayPosition);
                    returnListenTime = 0;
                }
            } else {
                if (player.isPlayModelChanged()) {
                    coverImage.setVisibility(View.INVISIBLE);
                    iv_play_pause.setImageResource(R.mipmap.iv_pause);
                    player.start();
                } else {
                    iv_play_pause.setImageResource(ConfigUtil.AutoPlay ? R.mipmap.iv_pause : R.mipmap.iv_play);
                    if (ConfigUtil.AutoPlay) {
                        player.start();
                    }
                }
            }
        }
        hidePlayErrorView();
        //得到视频的宽和高
        videoHeight = player.getVideoHeight();
        videoWidth = player.getVideoWidth();

        //设置生成gif图片的分辨率
        if (videoWidth > 320) {
            gifVideoWidth = 320;
            float ratio = videoWidth / 320.0f;
            gifVideoHeight = (int) (videoHeight / ratio);
        } else {
            gifVideoWidth = videoWidth;
            gifVideoHeight = videoHeight;
        }
        if (!isFullScreen) {
            setPortVideo();
            sv_subtitle.setLandScape(false);
        } else {
            //重置画面大小
            setSize(1);
        }
        ll_load_video.setVisibility(View.GONE);
        //视频模式，隐藏音频界面
        if (!isAudioMode) {
            ll_audio_view.setVisibility(View.GONE);
        }
        //获得视频清晰度列表
        float xPosRate = 0.1f;
        float logoWidthRate = 0.1f;
        float logoHeightRate = 0.1f;
        float yPosRate = 0.1f;
        if (!isLocalPlay) {
            definitions = player.getDefinitions();
            if (definitions != null) {
                for (String key : definitions.keySet()) {
                    Integer integer = definitions.get(key);
                    if (currentDefinition == integer) {
                        tv_play_definition.setText(key);
                    }
                }
            }
            //自定义logo
            clv_logo.setCustomLogoInfo(logoUrl, xPosRate, yPosRate, logoWidthRate, logoHeightRate);
        } else {
            clv_logo.setCustomLogoInfo(logoPath, xPosRate, yPosRate, logoWidthRate, logoHeightRate);
            setLandScape();
        }
        clv_logo.show();
        //设置视频总时长
        videoDuration = player.getDuration();
        tv_video_time.setText(MultiUtils.millsecondsToMinuteSecondStr(videoDuration));
        tv_portrait_video_time.setText(MultiUtils.millsecondsToMinuteSecondStr(videoDuration));
        showInputDanMu();
        //如果正在播放广告
        if (isPlayVideoAd) {
            ll_ad.setVisibility(View.VISIBLE);
            if (!isStartAdTimer) {
                startAdTimer();
            }
        } else {
            showOtherOperations();
            //更新播放进度
            startVideoTimer();
            //控制界面的隐藏
            controlHideView();
        }
        //展示视频打点信息
        if (hotSpotData != null && hotSpotData.size() > 0) {
            sb_progress.setHotSpotPosition(hotSpotData, videoDuration / 1000f);
            sb_portrait_progress.setHotSpotPosition(hotSpotData, videoDuration / 1000f);
        }
        //运行跑马灯
        mv_video.start();
        //获取图文打点相关信息
        if (!executeStatue) {
            player.executePortInfo();
            executeStatue = true;
        }
    }
    private void startTransparentMarquee(final MarqueeInfo marqueeInfo){
        transparentMarquee.setVisibility(View.VISIBLE);
        transparentMarquee.setMarqueeAdapter(new TransparentMarqueeAdaper() {
            @Override
            public String onContent() {
                return marqueeInfo.getTextBean().getContent();
            }
            //是否是深色系 默认是深色系
            @Override
            public boolean invisibleMarqueeLightMode() {
                return super.invisibleMarqueeLightMode();
            }
        });
    }
    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case DWMediaPlayer.MEDIA_INFO_BUFFERING_START:
                if (isDanMuOn) {
                    pauseDanmu();
                }
                netWorkStatus = MultiUtils.getNetWorkStatus(activity);
                if (netWorkStatus == 0 && !isLocalPlay) {
                    isNoNetPause = true;
                    showPlayErrorView();
                    hideOtherOperations();
                    tv_error_info.setText("请检查你的网络连接");
                    tv_operation.setText("重试");
                    tv_operation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            retryPlayTimes = 0;
                            hidePlayErrorView();
                            playVideoOrAudio(isAudioMode, false);
                        }
                    });
                } else {
                    ll_load_video.setVisibility(View.VISIBLE);
                }
                break;
            case DWMediaPlayer.MEDIA_INFO_BUFFERING_END:
                if (isDanMuOn) {
                    resumeDanmu();
                }
                if (!isLocalPlay) {
                    isNoNetPause = false;
                }
                ll_load_video.setVisibility(View.GONE);
                break;
        }
        return false;
    }

    //播放错误事件
    @Override
    public boolean onError(MediaPlayer mp, final int what, int i1) {
        runOnUiThread(new Runnable() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                isPrepareing = false;
                netWorkStatus = MultiUtils.getNetWorkStatus(activity);
                if (netWorkStatus == 0) {
                    isNoNetPause = true;
                }
                if (what == -10000 && netWorkStatus != 0 && retryPlayTimes < ON_ERROR_RETRY_TIME && !isLocalPlay) {
                    if (!retryStatue) {
                        ll_load_video.setVisibility(View.VISIBLE);
                        retryStatue = true;
                        ll_load_video.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                playVideoOrAudio(isAudioMode, false);
                                retryPlayTimes++;
                                retryStatue = false;
                            }
                        }, 500);
                    }
                    return;
                }
                tv_error_info.setText(String.format("播放出现异常（%d）", what));
                showPlayErrorView();
                hideOtherOperations();
                tv_operation.setText("重试");
                tv_operation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (netWorkStatus == 0) {
                            MultiUtils.showToast(activity, "请检查你的网络连接");
                            return;
                        }
                        retryPlayTimes = 0;
                        hidePlayErrorView();
                        playVideoOrAudio(isAudioMode, false);
                    }
                });

            }
        });
        return true;
    }

    //获得场景视频自定义错误类型
    @Override
    public void onPlayError(final HuodeException e) {
        runOnUiThread(new Runnable() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                switch (e.getIntErrorCode()) {
                    case 103:
                        tv_error_info.setText(String.format("音频无播放节点（%d）", e.getIntErrorCode()));
                        showPlayErrorView();
                        hideOtherOperations();
                        tv_operation.setText("切换到视频");
                        tv_operation.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                isAudioMode = false;
                                hidePlayErrorView();
                                playVideoOrAudio(isAudioMode, false);
                            }
                        });
                        break;
                    case 102:
                        //切换到音频
                        isAudioMode = true;
                        playVideoOrAudio(isAudioMode, false);
                        break;
                    case 104:
                        tv_error_info.setText(String.format("授权验证失败（%d）", e.getIntErrorCode()));
                        showPlayErrorView();
                        hideOtherOperations();
                        tv_operation.setVisibility(View.GONE);
                        break;
                    case 108:
                        tv_error_info.setText(String.format("账号信息不匹配（%d）", e.getIntErrorCode()));
                        showPlayErrorView();
                        hideOtherOperations();
                        tv_operation.setVisibility(View.GONE);
                        break;
                    case 114:
                        tv_error_info.setText(String.format("SdkSidProvider未设置（%d）", e.getIntErrorCode()));
                        showPlayErrorView();
                        hideOtherOperations();
                        tv_operation.setVisibility(View.GONE);
                        break;
                    case 115:
                        tv_error_info.setText(String.format("SdkSid为空（%d）", e.getIntErrorCode()));
                        showPlayErrorView();
                        hideOtherOperations();
                        tv_operation.setVisibility(View.GONE);
                        break;
                    case 116:
                        tv_error_info.setText(String.format("SdkSid校验失败（%d）", e.getIntErrorCode()));
                        showPlayErrorView();
                        hideOtherOperations();
                        tv_operation.setVisibility(View.GONE);
                        break;
                    default:
                        tv_error_info.setText(String.format("播放异常（%d）", e.getIntErrorCode()));
                        showPlayErrorView();
                        hideOtherOperations();
                        tv_operation.setVisibility(View.GONE);
                        break;
                }
            }
        });

    }

    //网络状态监听
    class NetChangedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                NetworkInfo dataInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                if (wifiInfo.isConnected() && dataInfo.isConnected()) {
                    //wifi和移动数据同时连接
                    netWorkStatus = 3;
                } else if (wifiInfo.isConnected() && !dataInfo.isConnected()) {
                    //wifi已连接，移动数据断开
                    netWorkStatus = 1;
                    resumePlay();
                } else if (!wifiInfo.isConnected() && dataInfo.isConnected()) {
                    //wifi断开 移动数据连接
                    netWorkStatus = 2;
                    showIsUseMobileNetwork();
                } else {
                    //wifi断开 移动数据断开
                    netWorkStatus = 0;
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
                        netWorkStatus = 0;
                        break;
                    case 2:
                        //wifi断开 移动数据连接
                        netWorkStatus = 2;
                        showIsUseMobileNetwork();
                        break;
                    case 4:
                        //wifi已连接，移动数据断开
                        netWorkStatus = 1;
                        resumePlay();
                        break;
                    case 5:
                        //wifi和移动数据同时连接
                        netWorkStatus = 3;
                        break;
                }
            }
        }
    }

    private void resumePlay() {
        if (isNoNetPause && !isLocalPlay) {
            if (tv_error_info.getVisibility() == View.VISIBLE) {
                hidePlayErrorView();
            }
            playVideoOrAudio(isAudioMode, false);
        }
    }

    private void showIsUseMobileNetwork() {
        if (isLocalPlay) {
            return;
        }
        if (isShowUseMobie) {
            return;
        }
        IsUseMobileNetworkDialog isUseMobileNetworkDialog = new IsUseMobileNetworkDialog(activity, new IsUseMobieNetwork() {
            @Override
            public void exit() {
                finish();
            }

            @Override
            public void continuePlay() {
                if (tv_error_info.getVisibility() == View.VISIBLE) {
                    hidePlayErrorView();
                }
                isPlayVideo = true;
                iv_play_pause.setImageResource(R.mipmap.iv_pause);
                playVideoOrAudio(isAudioMode, false);
                ll_load_video.setVisibility(View.GONE);

            }
        });
        if (!isUseMobileNetworkDialog.isShowing()) {
            isUseMobileNetworkDialog.show();
        }
        if (isPlayVideo) {
            playOrPauseVideo();
        }
    }

    private void hidePlayErrorView() {
        ll_load_video.setVisibility(View.VISIBLE);
        ll_play_error.setVisibility(View.GONE);
    }

    private void showPlayErrorView() {
        ll_load_video.setVisibility(View.GONE);
        ll_play_error.setVisibility(View.VISIBLE);
    }

    //缓冲进度
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        sb_progress.setSecondaryProgress(percent);
        sb_portrait_progress.setSecondaryProgress(percent);
    }

    //播放完成
    @Override
    public void onCompletion(MediaPlayer mp) {
        sb_progress.dismissPopupWindow();
        sb_progress.dismissPreview();
        if (isSmallWindow) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                sendBroadcast(new Intent("com.bokecc.vod.play.SMALL_WINDOW").putExtra("control", 3));
            }
            return;
        }
        if (isLocalPlay) {
            currentPosition = 0;
            updateLastPlayPosition();
            updatePlayCompleted();
            finish();
            return;
        }

        if (isPlayVideoAd && adTime > 0) {
            player.seekTo(0,MediaPlayer.SEEK_CLOSEST);
            iv_play_pause.setImageResource(R.mipmap.iv_pause);
            player.start();
            return;
        }

        if (isPlayFrontAd) {
            playVIdeoAfterAd();
            return;
        }

        updatePlayCompleted();

        if (endADInfoData != null && endADInfoData.getAd() != null) {
            isCanClickAd = false;
            EndADInfo.AdBean adBean = endADInfoData.getAd().get(0);
            boolean isEndVideoAd = endADInfoData.isVideo();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hideViews();
                    if (endADInfoData.getCanskip() == 1) {
                        tv_skip_ad.setVisibility(View.VISIBLE);
                        skipAdTime = endADInfoData.getSkipTime();
                    } else {
                        tv_skip_ad.setVisibility(View.GONE);
                    }
                    adTime = endADInfoData.getTime();
                    if (endADInfoData.getCanclick() == 1) {
                        tv_know_more.setVisibility(View.VISIBLE);
                    } else {
                        tv_know_more.setVisibility(View.GONE);
                    }
                }
            });
            if (isEndVideoAd) {
                if (adBean != null) {
                    playEndAd(adBean);
                }
            } else {
                if (adBean != null) {
                    final String material = adBean.getMaterial();
                    videoADClickUrl = adBean.getClickurl();
                    endADInfoData = null;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!TextUtils.isEmpty(material)) {
                                isShowImgEndAd = true;
                                ll_load_video.setVisibility(View.GONE);
                                ll_image_ad.setVisibility(View.VISIBLE);
                                Glide.with(HuoDeApplication.getContext()).load(material).listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        if (resource instanceof GifDrawable) {
                                            ((GifDrawable) resource).setLoopCount(GifDrawable.LOOP_FOREVER);
                                        }

                                        ll_ad.setVisibility(View.VISIBLE);
                                        startAdTimer();
                                        return false;
                                    }
                                }).into(iv_image_ad);
                            } else {
                                currentPosition = 0;
                                playNextVideo();
                            }
                        }
                    });
                }
            }
        } else {
            currentPosition = 0;
            playNextVideo();
        }
    }

    private void updatePlayCompleted() {
        if (!TextUtils.isEmpty(videoId) && lastVideoPosition != null) {
            lastVideoPosition.setPlayCompleted(true);
            videoPositionDBHelper.updateVideoPosition(lastVideoPosition);
        }
    }

    //播放下一个视频
    private void playNextVideo() {
        executeStatue = false;
        isPrepared = false;
        iv_play_pause.setImageResource(ConfigUtil.AutoPlay ? R.mipmap.iv_pause : R.mipmap.iv_play);
        player.resetPlayedAndPausedTime();
        resetInfo();
        playIndex = playIndex + 1;
        if (playIndex >= videoIds.size()) {
            playIndex = 0;
        }
        videoId = videoIds.get(playIndex);
        HuodeVideoInfo videoInfo = videoList.get(playIndex);
        if (videoInfo != null) {
            videoTitle = videoInfo.getVideoTitle();
            videoCover = videoInfo.getVideoCover();
        }
        //记录播放位置
        updateLastPlayPosition();
        getAdInfo();
    }

    private void resetInfo() {
        tvKnowledge.setText("");
        tvKnowledge.setVisibility(View.GONE);
        knowledgeBean = null;
        // 切换视频，重置questions
        if (questions != null) {
            questions.clear();
            questions = null;
        }
        // 切换视频时，重置打点信息
        if (hotSpotData != null) {
            hotSpotData.clear();
            sb_progress.clearHotSpots();
            sb_portrait_progress.clearHotSpots();
        }
        //清除访客信息
        resetVisitorInfo();
        //重置课堂练习
        if (exercises != null && exercises.size() > 0) {
            exercises.clear();
        }
        //重置授权验证信息
        isAllowPlayWholeVideo = 2;
        freeWatchTime = 0;
        freeWatchOverMsg = "";
        //重置视频是否展示过访客信息
        isVideoShowVisitorInfoDialog = false;
        //重置震动信息
        if (vibrationInfoList != null) {
            vibrationInfoList.clear();
        }
        sv_subtitle.resetSubtitle();
        cancelAdTimer();
        isPlayVideoAd = false;
        isCanClickAd = false;
        isPlayEndAd = false;
        isShowImgEndAd = false;
        pauseADInfoData = null;
        endADInfoData = null;
        if (pauseVideoAdDialog != null && pauseVideoAdDialog.isShowing()) {
            pauseVideoAdDialog.dismiss();
        }
        ll_image_ad.setVisibility(View.GONE);
        ll_ad.setVisibility(View.GONE);
        rl_pause_ad.setVisibility(View.GONE);
        playedTime = 0;
    }

    /**
     * 播放音视频
     *
     * @param isAudioMode 是否是音频模式
     * @param isResetPos  是否重置当前记录播放的位置
     */
    private void playVideoOrAudio(boolean isAudioMode, boolean isResetPos) {
        iv_back.setVisibility(View.VISIBLE);
        if (isResetPos) {
            switchDefPos = 0;
        }
        updateLastPlayPosition();
        isPrepared = false;
        getLastVideoPostion();
        if (isAudioMode) {
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDefaultPlayMode(MediaMode.AUDIO, new OnPlayModeListener() {
                @Override
                public void onPlayMode(MediaMode mediaMode) {
                    MediaPlayActivity.this.currentMode = mediaMode;
                }
            });
            ll_audio_view.setVisibility(View.VISIBLE);
            tv_play_definition.setVisibility(View.GONE);
            iv_create_gif.setVisibility(View.GONE);
            iv_landscape_screenshot.setVisibility(View.GONE);
            iv_switch_to_audio.setImageResource(R.mipmap.iv_video_mode);
        } else {
            iv_switch_to_audio.setImageResource(R.mipmap.iv_audio_mode);
            if (isFullScreen && !isLocalPlay) {
                iv_more_settings.setVisibility(View.VISIBLE);
            }

            if (!isLocalPlay) {
                tv_play_definition.setVisibility(View.VISIBLE);
            }
            player.setDefaultPlayMode(MediaMode.VIDEO, new OnPlayModeListener() {
                @Override
                public void onPlayMode(MediaMode mediaMode) {
                    MediaPlayActivity.this.currentMode = mediaMode;
                }
            });
        }
        ll_load_video.setVisibility(View.VISIBLE);
        hideOtherOperations();
        tv_video_title.setText(videoTitle);
        if (player != null) {
            player.pause();
            player.stop();
            player.reset();
        }
        //setClientId()方法在setVideoPlayInfo()前调用，可以通过此方法设置用户标识，出问题时方便排查
        player.setClientId("");
        if (BuildConfig.VerificationVersion == 0){
            player.setVideoPlayInfo(videoId, ConfigUtil.USER_ID, ConfigUtil.API_KEY, verificationCode, activity);
        }else if (BuildConfig.VerificationVersion == 1){
            player.setVideoPlayInfo(videoId, ConfigUtil.USER_ID, verificationCode, activity,SdkSidManager.getInstance().getSidProvider());
        }
        player.setSurface(playSurface);
        HuoDeApplication.getDRMServer().resetLocalPlay();
        player.setAudioPlay(isAudioMode);
        player.prepareAsync();
        functionHandler.setSignInDate(DataUtil.getSignInList());
        updatePlayingItem();
    }

    //更新播放列表正在播放项
    private void updatePlayingItem() {
        for (int i = 0; i < videoList.size(); i++) {
            HuodeVideoInfo videoInfo = videoList.get(i);
            if (videoInfo != null) {
                videoInfo.setSelected(videoInfo.getVideoId().equals(videoId));
            }
        }
        playListAdapter.notifyDataSetChanged();
    }

    //退出全屏播放
    private void setPortrait() {
        iv_video_full_screen.setVisibility(View.VISIBLE);
        ll_speed_def_select.setVisibility(View.GONE);
        iv_next_video.setVisibility(View.GONE);
        iv_lock_or_unlock.setVisibility(View.GONE);
        iv_create_gif.setVisibility(View.GONE);
        iv_landscape_screenshot.setVisibility(View.GONE);
        iv_switch_to_audio.setVisibility(View.VISIBLE);
        ll_landscape_progress.setVisibility(View.GONE);
        ll_portrait_progress.setVisibility(View.VISIBLE);
        ll_landscape_danMu.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            iv_small_window_play.setVisibility(View.VISIBLE);
        }
        if (!isAudioMode) {
            iv_portrait_screenshot.setVisibility(View.VISIBLE);
        }
        sv_subtitle.setLandScape(false);
        //小屏播放隐藏打点信息
        sb_progress.setHotspotShown(false);
        sb_portrait_progress.setHotspotShown(false);
        iv_more_settings.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        RelativeLayout.LayoutParams videoLayoutParams = (RelativeLayout.LayoutParams) rl_play_video.getLayoutParams();
        videoLayoutParams.topMargin = landScapeMarginTop;
        videoLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        videoLayoutParams.height = landScapeHeight;
        rl_play_video.setLayoutParams(videoLayoutParams);
        //设置竖屏TextureView的宽和高
        setPortVideo();
        isFullScreen = false;

        if (pauseVideoAdDialog != null && pauseVideoAdDialog.isShowing()) {
            pauseVideoAdDialog.updateView(isFullScreen);
        }

        clv_logo.refreshView();
    }

    //设置为全屏播放
    private void setLandScape() {
        iv_video_full_screen.setVisibility(View.GONE);
        ll_speed_def_select.setVisibility(View.VISIBLE);
        if (!isLocalPlay) {
            iv_next_video.setVisibility(View.VISIBLE);
            iv_more_settings.setVisibility(View.VISIBLE);
        }
        iv_switch_to_audio.setVisibility(View.GONE);
        iv_small_window_play.setVisibility(View.GONE);
        iv_portrait_screenshot.setVisibility(View.GONE);
        ll_landscape_progress.setVisibility(View.VISIBLE);
        ll_portrait_progress.setVisibility(View.GONE);
        ll_landscape_danMu.setVisibility(View.VISIBLE);
        if (!isAudioMode && !isPlayVideoAd) {
            iv_create_gif.setVisibility(View.VISIBLE);
            iv_landscape_screenshot.setVisibility(View.VISIBLE);
        }
        if (!isPlayVideoAd) {
            iv_lock_or_unlock.setVisibility(View.VISIBLE);
        }
        sv_subtitle.setLandScape(true);
        //全屏播放展示打点信息
        sb_progress.setHotspotShown(true);
        sb_portrait_progress.setHotspotShown(true);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        RelativeLayout.LayoutParams videoLayoutParams = (RelativeLayout.LayoutParams) rl_play_video.getLayoutParams();
        landScapeHeight = videoLayoutParams.height;
        landScapeMarginTop = videoLayoutParams.topMargin;
        videoLayoutParams.topMargin = 0;
        videoLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        videoLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        rl_play_video.setLayoutParams(videoLayoutParams);
        setLandScapeVideo();
        isFullScreen = true;

        if (pauseVideoAdDialog != null && pauseVideoAdDialog.isShowing()) {
            pauseVideoAdDialog.updateView(isFullScreen);
        }

        clv_logo.refreshView();
    }

    //设置横屏TextureView的宽和高,使视频高度和屏幕宽度一致
    private void setLandScapeVideo() {
        if (videoHeight > 0) {
            ViewGroup.LayoutParams videoParams = tv_video.getLayoutParams();
            int landVideoHeight = MultiUtils.getScreenWidth(activity);
            int limitedVideoWidth = MultiUtils.getScreenHeight(activity);
            int screenWidth = MultiUtils.getScreenWidth(activity);
            int screenHeight = MultiUtils.getScreenHeight(activity);
            if (screenWidth > screenHeight) {
                landVideoHeight = screenHeight;
                limitedVideoWidth = screenWidth;
            }

            int landVideoWidth = landVideoHeight * videoWidth / videoHeight;
            if (landVideoWidth > limitedVideoWidth) {
                landVideoWidth = limitedVideoWidth;
                landVideoHeight = landVideoWidth * videoHeight / videoWidth;
            }
            videoParams.height = landVideoHeight;
            videoParams.width = landVideoWidth;
            tv_video.setLayoutParams(videoParams);
        }

    }

    //小屏播放时按比例计算宽和高，使视频不变形
    private void setPortVideo() {
        if (videoHeight > 0) {
            ViewGroup.LayoutParams videoParams = tv_video.getLayoutParams();
            int portVideoHeight = MultiUtils.dipToPx(activity, 200);
            int limitedVideoHeight = MultiUtils.dipToPx(activity, 200);
            int portVideoWidth;
            int phoneWidth;
            int screenWidth = MultiUtils.getScreenWidth(activity);
            int screenHeight = MultiUtils.getScreenHeight(activity);
            phoneWidth = Math.min(screenWidth, screenHeight);
            portVideoWidth = phoneWidth;
            portVideoHeight = portVideoWidth * videoHeight / videoWidth;
            if (portVideoHeight > limitedVideoHeight) {
                portVideoHeight = limitedVideoHeight;
                portVideoWidth = portVideoHeight * videoWidth / videoHeight;
            }
            videoParams.height = portVideoHeight;
            videoParams.width = portVideoWidth;
            tv_video.setLayoutParams(videoParams);
        }
    }

    /**
     * 开启更新播放进度任务
     */
    private void startVideoTimer() {
        cancelVideoTimer();
        timer = new Timer();
        videoTask = new VideoTask();
        timer.schedule(videoTask, 0, 1000);
    }

    /**
     * 取消更新播放进度任务
     */
    private void cancelVideoTimer() {
        if (timer != null) {
            timer.cancel();
        }
        if (videoTask != null) {
            videoTask.cancel();
        }
    }

    /**
     * 播放进度计时器
     */
    class VideoTask extends TimerTask {
        @Override
        public void run() {
            if (MultiUtils.isActivityAlive(activity)) {
                activity.runOnUiThread(new Runnable() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void run() {
                        if (isPrepared) {
                            currentPosition = player.getCurrentPosition();
                            if (playedTime < currentPosition) {
                                playedTime = currentPosition;
                            }
                            tv_current_time.setText(MultiUtils.millsecondsToMinuteSecondStr(currentPosition));
                            tv_portrait_current_time.setText(MultiUtils.millsecondsToMinuteSecondStr(currentPosition));
                            sb_progress.setProgress((int) currentPosition, (int) videoDuration);
                            sb_portrait_progress.setProgress((int) currentPosition, (int) videoDuration);
                            //更新字幕
                            sv_subtitle.refreshSubTitle(currentPosition);
                            //展示问答题
                            if (isQuestionTimePoint((int) currentPosition) && (qaView == null || !qaView.isPopupWindowShown())) {
                                showQuestion();
                            }
                            //展示访客信息对话框
                            if (currentPosition > showVisitorTime && isShowVisitorInfoDialog && !isVideoShowVisitorInfoDialog) {
                                player.seekTo((int) showVisitorTime,MediaPlayer.SEEK_CLOSEST);
                                showVisitorInfoDialog();
                            }
                            //展示课堂练习
                            if (isShowExercise((int) currentPosition)) {
                                if (!isShowExercise) {
                                    return;
                                }
                                if (exeDialog != null && exeDialog.isShowing()) {
                                    return;
                                }
                                if (doExerciseDialog != null && doExerciseDialog.isShowing()) {
                                    return;
                                }
                                if (!isFullScreen) {
                                    setLandScape();
                                }
                                if (isShowConfirmExerciseDialog) {
                                    showExercise();
                                    return;
                                }
                                exerciseInterrupt = true;
                                showDoExerciseDialog(true);
                            }
                            //展示答题器
                            if (isAnswerSheetTimePoint((int) currentPosition)) {
                                if (player.isHideAnswerSheet()) {
                                    return;
                                }
                                for (int j = 0; j < sheetTimeList.size(); j++) {
                                    int currentAnswerTime = sheetTimeList.get(j);
                                    for (int i = 0; i < answerSheetInfoList.size(); i++) {
                                        AnswerSheetInfo answerSheetInfo = answerSheetInfoList.get(i);
                                        if (currentAnswerTime == answerSheetInfoList.get(i).getShowTime()) {
                                            List<AnswerSheetInfo.Answer> answers = answerSheetInfoList.get(i).getAnswers();
                                            if (player.isForceDisplayAnswerSheet()) {
                                                if (!answerSheetInfo.isHasBeanAnswered() && ((int) (currentPosition / 1000) >= answerSheetInfo.getShowTime())) {
                                                    currentSheetInfo = answerSheetInfo;
                                                    answerSheetInfo.setHasBeanAnswered(true);
                                                    showAnswerSheet(answers);
                                                    choiceSkip.setVisibility(answerSheetInfoList.get(i).isJump() ? View.VISIBLE : View.GONE);
                                                    player.pauseWithoutAnalyse();
                                                    return;
                                                }
                                            } else {
                                                if (answerSheetInfo.getShowTime() == ((int) (currentPosition / 1000))) {
                                                    currentSheetInfo = answerSheetInfo;
                                                    if (!currentSheetInfo.isHasBeanAnswered()) {
                                                        answerSheetInfo.setHasBeanAnswered(true);
                                                        showAnswerSheet(answers);
                                                        choiceSkip.setVisibility(answerSheetInfo.isJump() ? View.VISIBLE : View.GONE);
                                                        player.pauseWithoutAnalyse();
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            //如果大于试看时长就暂停
                            if (isAllowPlayWholeVideo == 0 && currentPosition > freeWatchTime * 1000) {
                                player.pause();
                                tv_watch_tip.setVisibility(View.GONE);
                                ll_pre_watch_over.setVisibility(View.VISIBLE);
                                hideViews();
                            }

                            if (qaView != null && qaView.isPopupWindowShown()) {
                                player.pauseWithoutAnalyse(); //针对有的手机上无法暂停，反复调用pause()
                            }
                            //请求弹幕数据，间隔1分钟
                            currentMinutePos = (int) (currentPosition / GET_DAN_MU_INTERVAL);
                            if (currentMinutePos != danMuSec && isDanMuOn && player.isPlaying()) {
                                player.getDanMuList(videoId, (danMuSec + 1));
                                danMuSec = currentMinutePos;
                            }
                            //展示签到
                            if (signinRoot.getVisibility() != View.VISIBLE){
                                functionHandler.onTimeChange(currentPosition);
                            }

                            //透明度跑马灯
                            if (transparentMarquee.isPrepared()){
                                if (isLocalPlay){
                                    if (isInvisibleMarquee){
                                        transparentMarquee.onTimeChange(currentPosition);
                                    }
                                }else{
                                    if (player.isInvisibleMarquee()){
                                        transparentMarquee.onTimeChange(currentPosition);
                                    }
                                }
                            }
                            //发送弹幕间隔
                            if (sendDanMuInterval >= 0 && !isCanSendDanMu) {
                                tv_input_danMu.setText(String.format("%ds", sendDanMuInterval));
                                tv_portrait_input_danMu.setText(String.format("%ds", sendDanMuInterval));
                                if (sendDanMuInterval == 0) {
                                    isCanSendDanMu = true;
                                    tv_input_danMu.setText("弹幕走一波");
                                    tv_portrait_input_danMu.setText("点我发弹幕");
                                }
                                sendDanMuInterval--;
                            }
                            //震动
                            isDynamicVideo = MultiUtils.getIsDynamicVideo();
                            if (choiceRoot.getVisibility() == View.VISIBLE || commitAnswerErrorLayout.getVisibility() == View.VISIBLE
                                    || statisticsLayout.getVisibility() == View.VISIBLE) {
                                return;
                            }
                            for (int i = 0; i < vibrationInfoList.size(); i++) {
                                VibrationInfo vibrationInfo = vibrationInfoList.get(i);
                                Integer vibratePosition = vibrationInfo.getVibratePosition();
                                if (vibratePosition == null) {
                                    break;
                                }
                                int msVibratePosition = vibratePosition * 1000;
                                if (currentPosition > msVibratePosition && (currentPosition - msVibratePosition) < 1000) {
                                    if (vibrator.hasVibrator() && isDynamicVideo && player.isPlaying()) {
                                        if (isFullScreen) {
                                            MultiUtils.showToast(activity, "前方重点预警");
                                        } else {
                                            MultiUtils.showTopToast(activity, "前方重点预警");
                                        }
                                        //震动时长，单位毫秒
                                        vibrator.vibrate(500);
                                    }
                                    break;
                                }
                            }
                            if (knowledgeEndTime != 0 && currentPosition / 1000 == knowledgeEndTime) {
                                inKnowledgeRange = true;
                                if (player != null && player.isPlaying() && knowledgePauseStatue) {
                                    playOrPauseVideo();
                                    knowledgePauseStatue = false;
                                }
                                return;
                            }
                            sb_progress.inKnowledgeRange(currentPosition);
                        }
                    }
                });
            }
        }
    }

    /**
     * 展示答题器ui
     */
    private void showAnswerSheet(List<AnswerSheetInfo.Answer> answerList) {
        choiceRoot.setVisibility(View.VISIBLE);
        boolean multiple = isMultiple(answerList);
        choiceAdapter = new ChoiceAdapter(MediaPlayActivity.this, answerList);
        choiceAdapter.setMultiple(multiple);
        choiceAdapter.setChoiceSelectListener(this);
        if (answerList != null && answerList.size() <= 4) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            choiceRecycler.setLayoutManager(linearLayoutManager);
        } else {
            GridLayoutManager manager = new GridLayoutManager(this, 4);
            choiceRecycler.setLayoutManager(manager);
        }
        choiceRecycler.setAdapter(choiceAdapter);
        choiceAdapter.notifyDataSetChanged();
    }

    //开启网速计时器任务
    private void startNetSpeedTimer() {
        cancelNetSpeedTimer();
        netSpeedTimer = new Timer();
        netSpeedTask = new NetSpeedTask();
        netSpeedTimer.schedule(netSpeedTask, 0, 1000);
    }

    //取消网速计时器任务
    private void cancelNetSpeedTimer() {
        if (netSpeedTimer != null) {
            netSpeedTimer.cancel();
        }
        if (netSpeedTask != null) {
            netSpeedTask.cancel();
        }
    }

    //网速计时器任务
    class NetSpeedTask extends TimerTask {
        @Override
        public void run() {
            if (MultiUtils.isActivityAlive(activity)) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        netSpeed = MultiUtils.getNetSpeed(activity.getApplicationInfo().uid);
                        tv_loading.setText(String.format("加载中 %s", netSpeed));
                    }
                });
            }
        }
    }

    //展示课堂练习
    private void showExercise() {
        int isJump = exercises.get(0).getIsJump();
        boolean isCanJump = false;
        if (isJump == 1) {
            isCanJump = true;
        }
        exeDialog = new ShowExeDialog(activity, isCanJump, new ExeOperation() {
            @Override
            public void listenClass() {
                player.seekTo(returnListenTime,MediaPlayer.SEEK_CLOSEST);
                playOrPauseVideo();
                isShowConfirmExerciseDialog = false;
            }

            @Override
            public void doExe() {
                showDoExerciseDialog(false);
            }

            @Override
            public void jump() {
                exercises.remove(0);
                playOrPauseVideo();
            }

        });
        exeDialog.show();
        if (isPlayVideo) {
            playOrPauseVideo();
        }
    }

    private void showDoExerciseDialog(boolean isChangePlayState) {
        if (doExerciseDialog == null) {
            doExerciseDialog = new DoExerciseDialog(activity, exercises.get(0), videoId, new ExercisesContinuePlay() {
                @Override
                public void continuePlay() {
                    exerciseInterrupt = false;
                    doExerciseDialog = null;
                    exercises.remove(0);
                    playOrPauseVideo();
                }

                @Override
                public void backPlay(int backPlayTime, boolean isExerciseBackPlay, boolean isRemoveExercise) {
                    exerciseInterrupt = false;
                    if (isExerciseBackPlay) {
                        doExerciseDialog = null;
                    }
                    if (isRemoveExercise) {
                        exercises.remove(0);
                    }
                    isShowConfirmExerciseDialog = false;
                    player.seekTo(backPlayTime * 1000,MediaPlayer.SEEK_CLOSEST);
                    playOrPauseVideo();
                }
            });
        }
        doExerciseDialog.show();
        if (isChangePlayState) {
            playOrPauseVideo();
        }
        boolean isReadExerciseGuide = MultiUtils.getIsReadExerciseGuide();
        if (!isReadExerciseGuide) {
            ExerciseGuideDialog exerciseGuideDialog = new ExerciseGuideDialog(activity);
            exerciseGuideDialog.show();
        }
    }

    //开启广告倒计时
    private void startAdTimer() {
        cancelAdTimer();
        isStartAdTimer = true;
        adTimer = new Timer();
        adTask = new AdTask();
        adTimer.schedule(adTask, 0, 1000);
    }

    //取消更新播放进度任务
    private void cancelAdTimer() {
        isStartAdTimer = false;
        if (adTimer != null) {
            adTimer.cancel();
        }
        if (adTask != null) {
            adTask.cancel();
        }
    }

    // 广告进度计时器
    class AdTask extends TimerTask {
        @Override
        public void run() {
            if (MultiUtils.isActivityAlive(activity)) {
                activity.runOnUiThread(new Runnable() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void run() {
                        if (isPlayVideoAd) {
                            if (player.isPlaying()) {
                                adTime--;
                                if (skipAdTime != 0) {
                                    skipAdTime--;
                                }
                            }
                        } else {
                            adTime--;
                            if (skipAdTime != 0) {
                                skipAdTime--;
                            }
                        }

                        if (adTime < 0) {
                            adTime = 0;
                        }
                        tv_ad_countdown.setText(String.format("广告剩余%dS", adTime));
                        if (adTime == 0) {
                            //广告播放完成 播放正片
                            playVIdeoAfterAd();
                        }
                        if (skipAdTime == 0) {
                            isCanClickAd = true;
                            tv_skip_ad.setText("跳过广告");
                        } else {
                            tv_skip_ad.setText(String.format("%dS后跳过广告", skipAdTime));
                        }
                    }
                });
            }
        }
    }

    private void playVIdeoAfterAd() {
        ll_ad.setVisibility(View.GONE);
        ll_image_ad.setVisibility(View.GONE);
        if (isPlayEndAd || isShowImgEndAd) {
            currentPosition = 0;
            playNextVideo();
        } else {
            isPlayFrontAd = false;
            playVideoOrAudio(isAudioMode, true);
        }

        cancelAdTimer();
        isPlayVideoAd = false;
    }

    //控制界面的隐藏
    private void controlHideView() {
        cancelControlHideView();
        controlHide = 8;
        hideTimer = new Timer();
        controlHideTask = new controlHideTask();
        hideTimer.schedule(controlHideTask, 0, 1000);
    }

    //取消控制界面的隐藏
    private void cancelControlHideView() {
        if (hideTimer != null) {
            hideTimer.cancel();
        }
        if (controlHideTask != null) {
            controlHideTask.cancel();
        }
    }

    // 控制界面的隐藏计时器
    class controlHideTask extends TimerTask {
        @Override
        public void run() {
            if (MultiUtils.isActivityAlive(activity)) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        controlHide = controlHide - 1;
                        if (controlHide == 0) {
                            hideViews();
                        }
                    }
                });
            }
        }

    }

    //QA问答
    private void createQuestionMap(JSONArray qaJsonArray) {
        for (int i = 0; i < qaJsonArray.length(); i++) {
            try {
                Question question = new Question(qaJsonArray.getJSONObject(i));
                questions.put(question.getShowTime(), question);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isQuestionTimePoint(int currentPosition) {
        if (questions == null || questions.size() < 1) {
            return false;
        }
        //需要换算成毫秒
        int questionTimePoint = questions.firstKey() * 1000;
        return currentPosition >= questionTimePoint;
    }

    /**
     * 当前是否需要展示答题器
     *
     * @param currentPosition 当前播放进度
     * @return show
     */
    private boolean isAnswerSheetTimePoint(int currentPosition) {
        if (qaView != null && qaView.isPopupWindowShown()) {
            return false;
        }
        if (exerciseInterrupt || visitorInterrupt) {
            return false;
        }
        if (sheetTimeList == null || sheetTimeList.isEmpty()) {
            return false;
        }
        if (choiceRoot.getVisibility() == View.VISIBLE || commitAnswerErrorLayout.getVisibility() == View.VISIBLE ||
                statisticsLayout.getVisibility() == View.VISIBLE) {
            return false;
        }
        return sheetTimeList.get(0) * 1000 <= currentPosition;
    }

    QAView.QAViewDismissListener myQAViewDismissListener = new QAView.QAViewDismissListener() {
        @Override
        public void seeBackPlay(int backPlay, boolean isRight) {
            player.seekTo(backPlay * 1000,MediaPlayer.SEEK_CLOSEST);
            playOrPauseVideo();
            if (isRight) {
                questions.remove(questions.firstKey());
            }
        }

        @Override
        public void continuePlay() {
            playOrPauseVideo();
            questions.remove(questions.firstKey());
        }

        @Override
        public void jumpQuestion() {
            playOrPauseVideo();
            questions.remove(questions.firstKey());
        }

    };

    private void showQuestion() {
        if (qaView != null && qaView.isPopupWindowShown()) {
            return;
        }
        if (qaView == null) {
            qaView = new QAView(this, videoId,"HIHA2019");
            qaView.setQAViewDismissListener(myQAViewDismissListener);
        }
        Map.Entry<Integer, Question> firstEntry = questions.firstEntry();
        if (questions != null && firstEntry != null) {
            qaView.setQuestion(firstEntry.getValue());
            qaView.show(getWindow().getDecorView().findViewById(android.R.id.content));
        }
        playOrPauseVideo();
    }

    //课堂练习
    private boolean isShowExercise(int currentPosition) {
        if (exercises == null || exercises.size() < 1) {
            return false;
        }
        int exerciseTimePoint = exercises.get(0).getShowTime() * 1000; //需要换算成毫秒
        return currentPosition >= exerciseTimePoint;
    }

    //开始生成gif
    @SuppressLint("SetTextI18n")
    private void startCreateGif() {
        if (gifMakerThread != null && gifMakerThread.isAlive()) {
            MultiUtils.showToast(activity, "处理中，请稍候");
            return;
        }
        gifFile = new File(Environment.getExternalStorageDirectory().getPath() + "/" + ConfigUtil.DOWNLOAD_PATH + "/" + System.currentTimeMillis() + ".gif");
        if (!player.isPlaying()) {
            playOrPauseVideo();
        }
        ivGifShow.setImageBitmap(null);
        ivGifStop.setImageResource(R.mipmap.iv_gif_stop_can_not_use);
        ivGifStop.setVisibility(View.VISIBLE);
        setGifViewStatus(View.VISIBLE);
        gifTips.setText("录制3s，即可分享");
        isGifStart = true;
        isGifCancel = false;
        isGifFinish = false;
        lastTimeMillis = 0;
        gifRecordTime = 0;
        startGifTimerTask();
        progressObject.setDuration(0);
        gifMakerThread = new GifMakerThread(gifMakerListener, gifFile.getAbsolutePath(), 100, 0);
        gifMakerThread.startGif();
    }

    GifMakerThread.GifMakerListener gifMakerListener = new GifMakerThread.GifMakerListener() {

        @Override
        public void onGifFinish() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isGifFinish = true;
                    playOrPauseVideo();
                    gifTips.setVisibility(View.GONE);
                    ll_show_gif.setVisibility(View.VISIBLE);
                    ivGifShow.setVisibility(View.VISIBLE);
                    ivGifStop.setVisibility(View.GONE);
                    gifProgressView.setVisibility(View.GONE);
                    Glide.with(MediaPlayActivity.this).load(gifFile).diskCacheStrategy(DiskCacheStrategy.NONE).into(ivGifShow);
                }
            });

        }

        @Override
        public void onGifError(final Exception e) {
            MultiUtils.showToast(MediaPlayActivity.this, "制作gif出错");
        }
    };

    private void startGifTimerTask() {
        stopGifTimerTask();
        gifCreateTimerTask = new TimerTask() {
            @Override
            public void run() {
                gifRecordTime = gifRecordTime + gifInterval;
                runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        progressObject.setDuration(gifRecordTime);
                        if (gifRecordTime >= gifMin) {
                            ivGifStop.setImageResource(R.mipmap.iv_gif_stop_can_use);
                            gifTips.setText("点击停止，即可生成gif图片");
                        }
                    }
                });
                if (gifRecordTime >= gifMax) {
                    stopGif();
                }
            }
        };
        timer.schedule(gifCreateTimerTask, gifInterval, gifInterval);
    }

    private void stopGifTimerTask() {
        if (gifCreateTimerTask != null) {
            gifCreateTimerTask.cancel();
        }
    }

    // 停止获取新的gif帧
    private void stopGif() {
        if (gifRecordTime < gifMin) {
            return;
        }
        endCreateGif();
    }

    //在cancelGif的时候直接调用
    private void endCreateGif() {
        if (isGifStart) {
            isGifStart = false;
            if (gifMakerThread != null) {
                gifMakerThread.stopGif();
            }
            stopGifTimerTask();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gifTips.setText("制作中，请等待...");
                }
            });
        }
    }

    // 取消gif的制作
    private void cancelGif() {
        endCreateGif();
        gifMakerThread.cancelGif();
        isGifCancel = true;
        setGifViewStatus(View.GONE);
        ll_show_gif.setVisibility(View.GONE);
        startPlay();
    }

    private void setGifViewStatus(int status) {
        ivGifStop.setVisibility(status);
        gifProgressView.setVisibility(status);
        gifTips.setVisibility(status);
        gifCancel.setVisibility(status);
        ivGifShow.setVisibility(status);
    }

    //更新本地数据库记录的播放位置
    private void updateLastPlayPosition() {
        if (!TextUtils.isEmpty(videoId) && lastVideoPosition != null && isPrepared && !isPlayVideoAd) {
            lastVideoPosition.setPosition((int) currentPosition);
            videoPositionDBHelper.updateVideoPosition(lastVideoPosition);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isSmallWindow){
            finish();
            return;
        }
        if (isPrepared) {
            if (!isAudioMode) {
               pause();
            }
        }
        pauseDanmu();
        if (pauseVideoAdDialog != null && pauseVideoAdDialog.isShowing()) {
            pauseVideoAdDialog.pauseVideoAd();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (pauseVideoAdDialog != null && pauseVideoAdDialog.isShowing()) {
            pauseVideoAdDialog.resumeVideoAd();
        }
        if (isPlayFrontAd || isPlayEndAd) {
            start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (signinRunnable !=null){
            handler.removeCallbacks(signinRunnable);
        }
        handler.removeMessages(1);
        if (transparentMarquee!=null){
            transparentMarquee.release();
        }
        functionHandler.onDestroy();
        HuoDeApplication.getDRMServer().disconnectCurrentStream();
        updateLastPlayPosition();
        cancelVideoTimer();
        cancelControlHideView();
        cancelAdTimer();
        cancelNetSpeedTimer();
        if (player != null) {
            player.release();
        }
        if (netReceiver != null) {
            unregisterReceiver(netReceiver);
        }
        if (smallWindowReceiver != null) {
            unregisterReceiver(smallWindowReceiver);
        }
        if (dm_view != null) {
            dm_view.release();
            dm_view = null;
        }
        sensorManager.unregisterListener(this);
        if (vibrator != null) {
            vibrator.cancel();
        }
    }

    //返回事件监听
    @Override
    public void onBackPressed() {
        if (!isGifCancel) {
            cancelGif();
            return;
        }
        if (isLock) {
            return;
        }
        if (isFullScreen && !isLocalPlay) {
            setPortrait();
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == null) {
            return;
        }
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            int x = (int) event.values[0];
            int y = (int) event.values[1];
            int z = (int) event.values[2];
            long sensorTime = System.currentTimeMillis();
            int absX = Math.abs(mX - x);
            int absY = Math.abs(mY - y);
            int absZ = Math.abs(mZ - z);
            int maxvalue = MultiUtils.getMaxValue(absX, absY, absZ);
            if (maxvalue > 2 && (sensorTime - lastSensorTime) > 1000) {
                lastSensorTime = sensorTime;
                if (isFullScreen) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                }
            }
            mX = x;
            mY = y;
            mZ = z;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN
                || event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
            if (ll_volume.getVisibility() == View.VISIBLE) {
                ll_volume.setVisibility(View.GONE);
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
