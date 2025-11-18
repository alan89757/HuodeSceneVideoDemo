package com.bokecc.vod.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bokecc.sdk.mobile.core.Core;
import com.bokecc.sdk.mobile.play.DWIjkMediaPlayer;
import com.bokecc.sdk.mobile.play.DWMediaPlayer;
import com.bokecc.sdk.mobile.play.OnSubtitleMsgListener;
import com.bokecc.sdk.mobile.play.SubtitleModel;
import com.bokecc.vod.HuoDeApplication;
import com.bokecc.vod.R;
import com.bokecc.vod.utils.MultiUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * SubtitleView
 *
 * @author CC
 */
public class SubtitleView extends RelativeLayout {

    private static final int MIN_SUBTITLE_SIZE = 12;
    private static final int MAX_SUBTITLE_SIZE = 140;

    private final Context mContext;
    private final TextView tvFirstSubtitle;
    private final TextView tv_second_subtitle;
    private Subtitle firstSubtitle, secondSubtitle;
    private int firstBottom, secondBottom, commonBottom, selectedSubtitle = 3, firstSort, secondSort;
    private String firstSubName, secondSubName;
    private double firstBottomRate, secondBottomRate;
    /**
     * 字幕自适应比例
     */
    private float proportion = 1.00f;

    /**
     * 字幕字体大小模式
     */
    private int subtitleModel;

    /**
     * 当前播放器View宽度
     */
    private int surfaceWidth;

    /**
     * 服务器设置的第一个字幕字体大小
     */
    private int firstServerSubtitleSize;
    /**
     * 服务器设置的第二个字幕字体大小
     */
    private int secondServerSubtitleSize;

    /**
     * 当前是否为离线视频
     */
    private boolean offline;
    /**
     * 存储的离线字幕第一个字幕大小
     */
    private int localFirstSubtitleSize;
    /**
     * 存储的离线字幕第二个字幕大小
     */
    private int localSecondSubtitleSize;

    public SubtitleView(Context context) {
        this(context, null);
    }

    public SubtitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.view_subtitle, this, true);
        tvFirstSubtitle = view.findViewById(R.id.tv_first_subtitle);
        tv_second_subtitle = view.findViewById(R.id.tv_second_subtitle);
    }

    public void setSubtitleModel(SubtitleModel subtitleModel) {
        this.subtitleModel = subtitleModel.value();
    }

    public void getSubtitlesInfo(DWIjkMediaPlayer player) {
        offline = false;
        player.setOnSubtitleMsgListener(new OnSubtitleMsgListener() {
            @Override
            public void onSubtitleMsg(String subtitleName, final int sort, String url, String font, final int size, final String color, final String surroundColor, final double bottom, String code) {
                firstServerSubtitleSize = size;
                resetSubtitleSize(surfaceWidth);
                if (!TextUtils.isEmpty(url)) {
                    firstSubName = subtitleName;
                    firstSubtitle = new Subtitle(new Subtitle.OnSubtitleInitedListener() {
                        @Override
                        public void onInited(Subtitle subtitle) {

                        }
                    });
                    firstSubtitle.initSubtitleResource(url);
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setFirstSubtitle(color, surroundColor, bottom, sort);
                        }
                    });
                }
            }

            @Override
            public void onSecSubtitleMsg(String subtitleName, final int sort, String url, String font, final int size, final String color, final String surroundColor, final double bottom, String code) {
                secondServerSubtitleSize = size;
                if (!TextUtils.isEmpty(url)) {
                    secondSubName = subtitleName;
                    secondSubtitle = new Subtitle(new Subtitle.OnSubtitleInitedListener() {
                        @Override
                        public void onInited(Subtitle subtitle) {

                        }
                    });
                    secondSubtitle.initSubtitleResource(url);
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setSecondSubtitle(color, surroundColor, bottom, sort);
                        }
                    });
                }
            }

            @Override
            public void onDefSubtitle(final int defaultSubtitle) {
                selectedSubtitle = defaultSubtitle;
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setDefaultSubtitle(defaultSubtitle);
                    }
                });
            }

            @Override
            public void onSubtitleModel(int model) {
                if (model != -1) {
                    subtitleModel = model;
                }
                resetSubtitleSize(surfaceWidth);
            }

            @Override
            public void onSizeChanged(int width, int height) {
                surfaceWidth = width;
                resetSubtitleSize(width);
            }
        });
    }

    public void getSubtitlesInfo(DWMediaPlayer player) {
        offline = false;
        player.setOnSubtitleMsgListener(new OnSubtitleMsgListener() {
            @Override
            public void onSubtitleMsg(String subtitleName, final int sort, String url, String font, final int size, final String color, final String surroundColor, final double bottom, String code) {
                firstServerSubtitleSize = size;
                resetSubtitleSize(surfaceWidth);
                if (!TextUtils.isEmpty(url)) {
                    firstSubName = subtitleName;
                    firstSubtitle = new Subtitle(new Subtitle.OnSubtitleInitedListener() {
                        @Override
                        public void onInited(Subtitle subtitle) {

                        }
                    });
                    firstSubtitle.initSubtitleResource(url);
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setFirstSubtitle(color, surroundColor, bottom, sort);
                        }
                    });
                }
            }

            @Override
            public void onSecSubtitleMsg(String subtitleName, final int sort, String url, String font, final int size, final String color, final String surroundColor, final double bottom, String code) {
                secondServerSubtitleSize = size;
                if (!TextUtils.isEmpty(url)) {
                    secondSubName = subtitleName;
                    secondSubtitle = new Subtitle(new Subtitle.OnSubtitleInitedListener() {
                        @Override
                        public void onInited(Subtitle subtitle) {

                        }
                    });
                    secondSubtitle.initSubtitleResource(url);
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setSecondSubtitle(color, surroundColor, bottom, sort);
                        }
                    });
                }


            }

            @Override
            public void onDefSubtitle(final int defaultSubtitle) {
                selectedSubtitle = defaultSubtitle;
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setDefaultSubtitle(defaultSubtitle);
                    }
                });
            }

            @Override
            public void onSubtitleModel(int model) {
                if (model != -1) {
                    subtitleModel = model;
                }
                resetSubtitleSize(surfaceWidth);
            }

            @Override
            public void onSizeChanged(int width, int height) {
                surfaceWidth = width;
                resetSubtitleSize(width);
            }
        });
    }

    private void resetSubtitleSize(int width) {
        if (width == 0) {
            return;
        }
        proportion = (width / 480.00f) * 1.2f;
        if (offline) {
            if (subtitleModel == SubtitleModel.SELF_ADAPTION.value()) {
                if (mContext instanceof Activity) {
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvFirstSubtitle.setTextSize(MultiUtils.px2dp(getContext(), getBoundarySize(localFirstSubtitleSize * proportion)));
                            tv_second_subtitle.setTextSize(MultiUtils.px2dp(getContext(), getBoundarySize(localSecondSubtitleSize * proportion)));
                        }
                    });
                }
            } else {
                if (mContext instanceof Activity) {
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvFirstSubtitle.setTextSize(MultiUtils.px2dp(getContext(), getBoundarySize(localFirstSubtitleSize)));
                            tv_second_subtitle.setTextSize(MultiUtils.px2dp(getContext(), getBoundarySize(localSecondSubtitleSize)));
                        }
                    });
                }
            }
        } else {
            if (subtitleModel == SubtitleModel.SELF_ADAPTION.value()) {
                if (mContext instanceof Activity) {
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvFirstSubtitle.setTextSize(MultiUtils.px2dp(getContext(), getBoundarySize(firstServerSubtitleSize * proportion)));
                            tv_second_subtitle.setTextSize(MultiUtils.px2dp(getContext(), getBoundarySize(secondServerSubtitleSize * proportion)));
                        }
                    });
                }
            } else {
                if (mContext instanceof Activity) {
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvFirstSubtitle.setTextSize(MultiUtils.px2dp(getContext(), getBoundarySize(firstServerSubtitleSize)));
                            tv_second_subtitle.setTextSize(MultiUtils.px2dp(getContext(), getBoundarySize(secondServerSubtitleSize)));
                        }
                    });
                }
            }
        }
    }


    private int getBoundarySize(int size) {
        if (size < MIN_SUBTITLE_SIZE) {
            return MIN_SUBTITLE_SIZE;
        } else {
            return Math.min(size, MAX_SUBTITLE_SIZE);
        }
    }

    private float getBoundarySize(float size) {
        if (size < MIN_SUBTITLE_SIZE) {
            return MIN_SUBTITLE_SIZE;
        } else {
            return Math.min(size, MAX_SUBTITLE_SIZE);
        }
    }

    private void setDefaultSubtitle(int defaultSubtitle) {
        if (defaultSubtitle == 0) {
            tvFirstSubtitle.setVisibility(View.VISIBLE);
            tv_second_subtitle.setVisibility(View.GONE);
            tvFirstSubtitle.setPadding(0, 0, 0, commonBottom);
            tv_second_subtitle.setPadding(0, 0, 0, commonBottom);
        } else if (defaultSubtitle == 1) {
            tvFirstSubtitle.setVisibility(View.GONE);
            tv_second_subtitle.setVisibility(View.VISIBLE);
            tvFirstSubtitle.setPadding(0, 0, 0, commonBottom);
            tv_second_subtitle.setPadding(0, 0, 0, commonBottom);
        } else {
            tvFirstSubtitle.setVisibility(View.VISIBLE);
            tv_second_subtitle.setVisibility(View.VISIBLE);
            tvFirstSubtitle.setPadding(0, 0, 0, firstBottom);
            tv_second_subtitle.setPadding(0, 0, 0, secondBottom);
        }
    }

    private void setSecondSubtitle(String color, String surroundColor, double bottom, int sort) {
        try {
            if (!TextUtils.isEmpty(color)) {
                String newColor = color;
                if (color.contains("0x")) {
                    newColor = color.replace("0x", "#");
                }
                tv_second_subtitle.setTextColor(Color.parseColor(newColor));
                tv_second_subtitle.setShadowLayer(10F, 5F, 5F, Color.YELLOW);
            }

            if (!TextUtils.isEmpty(surroundColor)) {
                String newSurroundColor = surroundColor;
                if (surroundColor.contains("0x")) {
                    newSurroundColor = surroundColor.replace("0x", "#");
                }
                tv_second_subtitle.setShadowLayer(10F, 5F, 5F, Color.parseColor(newSurroundColor));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        secondBottomRate = bottom;
        secondSort = sort;
        if (bottom > 0) {
            int paddingBottom;
            Resources resources = getResources();
            if (resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                paddingBottom = (int) (resources.getDisplayMetrics().heightPixels * bottom);
            } else {
                paddingBottom = (int) (resources.getDisplayMetrics().widthPixels * bottom);
            }
            if (sort == 2) {
                commonBottom = paddingBottom;
            }
            secondBottom = paddingBottom;
            tv_second_subtitle.setPadding(0, 0, 0, paddingBottom);
        }
    }

    private void setFirstSubtitle(String color, String surroundColor, double bottom, int sort) {
        try {
            if (!TextUtils.isEmpty(color)) {
                String newColor = color;
                if (color.contains("0x")) {
                    newColor = color.replace("0x", "#");
                }
                tvFirstSubtitle.setTextColor(Color.parseColor(newColor));
                tvFirstSubtitle.setShadowLayer(10F, 5F, 5F, Color.YELLOW);
            }
            if (!TextUtils.isEmpty(surroundColor)) {
                String newSurroundColor = surroundColor;
                if (surroundColor.contains("0x")) {
                    newSurroundColor = surroundColor.replace("0x", "#");
                }
                tvFirstSubtitle.setShadowLayer(10F, 5F, 5F, Color.parseColor(newSurroundColor));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        firstBottomRate = bottom;
        firstSort = sort;
        if (bottom > 0) {
            int paddingBottom ;
            Resources resources = getResources();
            if (resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                paddingBottom = (int) (resources.getDisplayMetrics().heightPixels * bottom);
            } else {
                paddingBottom = (int) (resources.getDisplayMetrics().widthPixels * bottom);
            }
            if (sort == 1) {
                commonBottom = paddingBottom;
            }
            firstBottom = paddingBottom;
            tvFirstSubtitle.setPadding(0, 0, 0, paddingBottom);
        }
    }

    public void setLandScape(boolean isLandScape) {
        int baseHeight ;
        if (isLandScape) {
            Resources resources = getResources();
            if (resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                baseHeight = resources.getDisplayMetrics().heightPixels;
            } else {
                baseHeight = resources.getDisplayMetrics().widthPixels;
            }
        } else {
            baseHeight = MultiUtils.dipToPx(HuoDeApplication.getContext(), 200);
        }
        if (firstBottomRate > 0) {
            int paddingBottom = (int) (baseHeight * firstBottomRate);
            if (firstSort == 1) {
                commonBottom = paddingBottom;
            }
            firstBottom = paddingBottom;
            tvFirstSubtitle.setPadding(0, 0, 0, paddingBottom);
        }

        if (secondBottomRate > 0) {
            int paddingBottom = (int) (baseHeight * secondBottomRate);
            if (secondSort == 2) {
                commonBottom = paddingBottom;
            }
            secondBottom = paddingBottom;
            tv_second_subtitle.setPadding(0, 0, 0, paddingBottom);
        }

    }

    public void initFirstOfflineSubtitleInfo(String subtitlePath) {
        firstSubtitle = new Subtitle(new Subtitle.OnSubtitleInitedListener() {
            @Override
            public void onInited(Subtitle subtitle) {

            }
        });
        firstSubtitle.initOfflineSubtitleResource(subtitlePath);
    }

    public void initSecondOfflineSubtitleInfo(String subtitlePath) {
        secondSubtitle = new Subtitle(new Subtitle.OnSubtitleInitedListener() {
            @Override
            public void onInited(Subtitle subtitle) {

            }
        });
        secondSubtitle.initOfflineSubtitleResource(subtitlePath);
    }

    /**
     * 离线字幕
     *
     * @param activity        activity
     * @param subtitleSetPath subtitleSetPath
     */
    public void initOfflineSubtitleSet(final Activity activity, final String subtitleSetPath) {
        offline = true;
        Core.getInstance().getExecutorSupplier()
                .forBackgroundTasks()
                .execute(new Runnable() {
                    @Override
                    public void run() {
                        StringBuilder stringBuilder = new StringBuilder();
                        InputStream in = null;
                        File file = new File(subtitleSetPath);
                        try {
                            in = new FileInputStream(file);
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                            String line;
                            while ((line = bufferedReader.readLine()) != null) {
                                stringBuilder.append(line);
                            }
                            String result = stringBuilder.toString();
                            if (!TextUtils.isEmpty(result)) {
                                JSONObject jsonObject = new JSONObject(result);
                                if (jsonObject.has("subtitle")) {
                                    JSONObject subtitle = jsonObject.getJSONObject("subtitle");
                                    if (!TextUtils.isEmpty(subtitle.toString())) {
                                        localFirstSubtitleSize = subtitle.optInt("size");
                                        final String color = subtitle.optString("color");
                                        final String surroundColor = subtitle.optString("surroundColor");
                                        final double bottom = subtitle.optDouble("bottom");
                                        final int sort = subtitle.optInt("sort");
                                        ((Activity) mContext).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                setFirstSubtitle(color, surroundColor, bottom, sort);
                                            }
                                        });
                                    }
                                }

                                if (jsonObject.has("subtitle2")) {
                                    JSONObject subtitle = jsonObject.getJSONObject("subtitle2");
                                    if (!TextUtils.isEmpty(subtitle.toString())) {
                                        localSecondSubtitleSize = subtitle.optInt("size");
                                        final String color = subtitle.optString("color");
                                        final String surroundColor = subtitle.optString("surroundColor");
                                        final double bottom = subtitle.optDouble("bottom");
                                        final int sort = subtitle.optInt("sort");
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                setSecondSubtitle(color, surroundColor, bottom, sort);
                                            }
                                        });
                                    }
                                }

                                if (jsonObject.has("defaultSubtitle")) {
                                    final int defaultSubtitle = jsonObject.optInt("defaultSubtitle");
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            setDefaultSubtitle(defaultSubtitle);
                                        }
                                    });
                                }
                                if (jsonObject.has("subtitleModel")) {
                                    subtitleModel = jsonObject.optInt("subtitleModel");
                                }
                                resetSubtitleSize(surfaceWidth);
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            if (in != null) {
                                try {
                                    in.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
    }

    /**
     * 更新字幕
     */
    public void refreshSubTitle(long currentPosition) {
        if (firstSubtitle != null) {
            tvFirstSubtitle.setText(firstSubtitle.getSubtitleByTime(currentPosition));
        } else {
            tvFirstSubtitle.setVisibility(INVISIBLE);
        }

        if (secondSubtitle != null) {
            tv_second_subtitle.setText(secondSubtitle.getSubtitleByTime(currentPosition));
        } else {
            tv_second_subtitle.setVisibility(INVISIBLE);
        }
    }

    /**
     * 重置字幕
     */
    public void resetSubtitle() {
        firstSubtitle = null;
        secondSubtitle = null;
        selectedSubtitle = 3;
        firstSubName = null;
        secondSubName = null;
    }

    /**
     * 获得第一种字幕名字
     */
    public String getFirstSubName() {
        return firstSubName;
    }

    /**
     * 获得第二种字幕名字
     */
    public String getSecondSubName() {
        return secondSubName;
    }

    /**
     * 获得当前选中的字幕
     */
    public int getSelectedSubtitle() {
        return selectedSubtitle;
    }

    /**
     * 控制字幕显示
     */
    public void setSubtitle(int selectedSub) {
        selectedSubtitle = selectedSub;
        if (selectedSub == 0) {
            tvFirstSubtitle.setVisibility(VISIBLE);
            tv_second_subtitle.setVisibility(GONE);
            tvFirstSubtitle.setPadding(0, 0, 0, commonBottom);
        } else if (selectedSub == 1) {
            tvFirstSubtitle.setVisibility(GONE);
            tv_second_subtitle.setVisibility(VISIBLE);
            tv_second_subtitle.setPadding(0, 0, 0, commonBottom);
        } else if (selectedSub == 2) {
            tvFirstSubtitle.setVisibility(View.VISIBLE);
            tv_second_subtitle.setVisibility(View.VISIBLE);
            tvFirstSubtitle.setPadding(0, 0, 0, firstBottom);
            tv_second_subtitle.setPadding(0, 0, 0, secondBottom);
        } else if (selectedSub == 3) {
            tvFirstSubtitle.setVisibility(View.GONE);
            tv_second_subtitle.setVisibility(View.GONE);
        }
    }

}
