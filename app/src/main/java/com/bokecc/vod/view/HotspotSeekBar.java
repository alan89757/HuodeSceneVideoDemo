package com.bokecc.vod.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bokecc.sdk.mobile.play.HotSpotInfo;
import com.bokecc.vod.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author 获得场景视频
 * <p>
 * 自定义进度条
 */
@SuppressLint("NonConstantResourceId")
public class HotspotSeekBar extends View implements View.OnClickListener {

    private final Context mContext;
    private final WindowManager mWindowManager;
    private Paint backgroundPaint, progressPaint, secondProgressPaint,
            thumbPaint, hotSpotPaint, knowledgeRangePaint, knowledgePaint;

    int progress = 0;

    int secondProgress = 0, max = 100;

    private long currentDuration;

    /**
     * 热点是否显示
     */
    private boolean isHotspotShown = false;

    /**
     * 知识点片段绘制起始进度
     */
    private float startPosition, endPosition;

    /**
     * 知识点片段的岂止时间点
     */
    private long startTime, endTime;

    private boolean isTouch = false;

    private float currentThumbStart;

    /**
     * 时间轴是否绘制知识点片段
     */
    private boolean timeAxisStatus;

    /**
     * 当前是否处在知识点范围内
     */
    private boolean inKnowledge;

    private final List<HotSpot> hotspotList = new ArrayList<>();

    private LinkedHashMap<Integer, HotSpotInfo> hotSpotInfoMap;

    private HotSpot clickedHotSpot;

    private int currentHotPosition = -1;

    private long duration = -1;

    private List<String> thumbnailsList;

    public HotspotSeekBar(Context context) {
        this(context, null);
    }

    public HotspotSeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HotspotSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        initPaint();
    }

    /**
     * 设置热点是否显示，默认为不显示热点
     *
     * @param isHotspotShown isHotspotShown
     */
    public void setHotspotShown(boolean isHotspotShown) {
        this.isHotspotShown = isHotspotShown;
        if (!isHotspotShown) {
            dismissPopupWindow();
        }
        invalidate();
    }

    public void setSecondaryProgress(int progress) {
        this.secondProgress = progress;
    }

    public int getProgress() {
        return progress;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMax() {
        return max;
    }

    /**
     * 知识点片段绘制
     *
     * @param startPosition startPosition
     * @param endPosition   endPosition
     */
    public void setKnowledgeRange(float startPosition, float endPosition) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    public void setKnowledgeTime(long startTime, long endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void setTimeAxisStatus(boolean timeAxisStatus) {
        this.timeAxisStatus = timeAxisStatus;
    }

    public boolean inKnowledgeRange(long position) {
        inKnowledge = position >= startTime * 1000 && position < endTime * 1000;
        return inKnowledge;
    }

    public void setInKnowledge(boolean inKnowledge) {
        this.inKnowledge = inKnowledge;
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        currentThumbStart = event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isHotspotShown && isHotSpotClicked()) {
                    float progressPercent = (currentThumbStart - (getHeight() >> 1)) / (getWidth() - getHeight());
                    if (progressPercent > 1.0f) {
                        progressPercent = 1.0f;
                    }
                    currentDuration = (long) (duration * progressPercent);
                    showPopupWindow();
                    return false;
                } else {
                    isTouch = true;
                    showPreviewPopup();
                    if (onSeekBarChangeListener != null && duration != -1) {
                        onSeekBarChangeListener.onStartTrackingTouch(this);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isTouch) {
                    return false;
                } else {
                    float progressPercent = (thumbStart - (getHeight() >> 1)) / (getWidth() - getHeight());
                    if (progressPercent > 1.0f) {
                        progressPercent = 1.0f;
                    }
                    currentDuration = (long) (duration * progressPercent);
                    invalidatePreviewInfo();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isTouch) {
                    isTouch = false;
                    dismissPreview();
                    if (onSeekBarChangeListener != null) {
                        float progressPercent = (thumbStart - (getHeight() >> 1)) / (getWidth() - getHeight());
                        if (progressPercent > 1.0f) {
                            progressPercent = 1.0f;
                        }
                        onSeekBarChangeListener.onStopTrackingTouch(this, progressPercent);
                    }
                    break;
                }
            default:
                return false;
        }
        thumbStart = currentThumbStart;
        processThumbStart();
        invalidate();
        return true;
    }

    private PopupWindow previewPopupWindow;
    private TextView previewDuration, durationText, previewDesc;
    private ImageView previewImage;

    private void showPreviewPopup() {
        if (previewPopupWindow == null) {
            initPreviewPopup();
        }
        if (thumbnailsList == null || thumbnailsList.isEmpty()) {
            previewImage.setVisibility(INVISIBLE);
        } else {
            previewImage.setVisibility(VISIBLE);
        }
        if (previewPopupWindow != null && previewPopupWindow.isShowing()) {
            return;
        }
        int[] position = new int[2];
        getLocationOnScreen(position);
        previewPopupWindow.getContentView().measure(0, 0);
        previewPopupWindow.showAtLocation(this, Gravity.CENTER, 0, 0);
    }

    public void dismissPreview() {
        if (previewPopupWindow != null && previewPopupWindow.isShowing()) {
            previewPopupWindow.dismiss();
        }
    }

    /**
     * 帧预览相关
     */
    private void initPreviewPopup() {
        previewPopupWindow = new PopupWindow(mContext);
        previewPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        previewPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        final View view = LayoutInflater.from(mContext).inflate(R.layout.preview_popupwindow, null);
        previewDuration = view.findViewById(R.id.previewDuration);
        durationText = view.findViewById(R.id.durationText);
        previewImage = view.findViewById(R.id.previewImage);
        previewDesc = view.findViewById(R.id.previewDesc);
        invalidatePreviewInfo();
        previewPopupWindow.setContentView(view);
        previewPopupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        previewPopupWindow.setOutsideTouchable(true);
    }


    private boolean isHotSpotClicked() {
        for (int i = 0; i < hotspotList.size(); i++) {
            float mPositionPercent = hotspotList.get(i).getHotspotPercent();
            float centerPosition = (getWidth() - getHeight()) * mPositionPercent + (getHeight() >> 1);
            boolean isOnHotPoint = (currentThumbStart > centerPosition - hotspotWidth * 3) && currentThumbStart < (centerPosition + hotspotWidth * 3);
            if (isOnHotPoint) {
                clickedHotSpot = hotspotList.get(i);
                this.currentThumbStart = centerPosition;
                this.currentHotPosition = i;
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawProgress(canvas);
        drawHotspot(canvas);
        drawThumb(canvas);
        if (timeAxisStatus && endPosition != 0 && inKnowledge) {
            drawKnowledge(canvas);
        }
        //解决Android8.1bug
        canvas.save();
        canvas.restore();
    }


    private void processThumbStart() {
        int progressRightSide = getWidth() - getHeight() / 2;
        int progressLeftSide = getHeight() / 2;
        if (thumbStart > progressRightSide) {
            thumbStart = progressRightSide;
        } else if (thumbStart < progressLeftSide) {
            thumbStart = progressLeftSide;
        }
    }

    private void drawProgress(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        float heightBaseLine = getHeightBaseLine(height);
        float leftPosition = 0.0f + (height >> 1);
        float topPosition = 0.0f + heightBaseLine;
        float bottomPosition = height - heightBaseLine;
        float secondaryProgressRightPosition = (float) (width - height) * secondProgress / 100 + (height >> 1);
        canvas.drawRect(leftPosition, topPosition, width - height, bottomPosition, backgroundPaint);
        canvas.drawRect(leftPosition, topPosition, secondaryProgressRightPosition, bottomPosition, secondProgressPaint);
        canvas.drawRect(leftPosition, topPosition, thumbStart, bottomPosition, progressPaint);
    }

    float thumbStart = 0.0f;

    private void drawThumb(Canvas canvas) {
        if (thumbStart <= 0.0f) {
            //TODO 这里导致了设置layout_height的时候，不能为wrap_content
            thumbStart = getLayoutParams().height >> 1;
        }
        int height = canvas.getHeight();
        if (isTouch) {
            // 绘制两个圆，这样展示出来的就是有边框的圆
            canvas.drawCircle(thumbStart, height >> 1, (height >> 1) - 3, thumbPaint);
        } else {
            canvas.drawCircle(thumbStart, height >> 1, height * 2 / 5 - 3, thumbPaint);
        }
    }

    float hotspotWidth = 8.0f;

    private void drawHotspot(Canvas canvas) {
        if (!isHotspotShown) {
            return;
        }
        int height = canvas.getHeight();
        int width = canvas.getWidth();
        float heightBaseLine = getHeightBaseLine(height);
        float topPosition = 0.0f + heightBaseLine;
        float bottomPosition = height - heightBaseLine;
        if (hotspotList.size() > 0) {
            for (HotSpot hotSpot : hotspotList) {
                float mPositionPercent = hotSpot.getHotspotPercent();
                float leftPosition = (width - height) * mPositionPercent + (height >> 1) - hotspotWidth;
                float rightPosition = (width - height) * mPositionPercent + (height >> 1) + hotspotWidth;
                float roundRectRadius = (height * 3) >> 1;
                RectF mRectF = new RectF(leftPosition, topPosition, rightPosition, bottomPosition);
                canvas.drawRoundRect(mRectF, roundRectRadius, roundRectRadius, hotSpotPaint);
            }
        }
    }


    private void drawKnowledge(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        float heightBaseLine = getHeightBaseLine(height);
        float leftPosition = width * startPosition;
        float rightPosition = width * endPosition;
        float topPosition = 0.0f + heightBaseLine;
        float bottomPosition = height - heightBaseLine;
        canvas.drawRect(leftPosition - 10, topPosition + 20, leftPosition + 10, bottomPosition + 20, knowledgeRangePaint);
        canvas.drawRect(leftPosition, topPosition + 20, rightPosition, bottomPosition + 20, knowledgePaint);
        canvas.drawRect(rightPosition, topPosition + 20, rightPosition + 10, bottomPosition + 20, knowledgeRangePaint);
    }

    private float getHeightBaseLine(int height) {
        return (float) height * 2 / 5;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        MeasureSpec.getMode(widthMeasureSpec);
        MeasureSpec.getSize(widthMeasureSpec);
    }

    /**
     * 设置当前的进度
     *
     * @param progress 当前进度
     * @param duration 总时长
     */
    public void setProgress(int progress, int duration) {
        if (isTouch) {
            return;
        }
        this.progress = progress;
        this.max = duration;
        thumbStart = (getWidth() - getHeight()) * (float) progress / duration + (getHeight() >> 1);
        postInvalidate();
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setThumbnails(List<String> thumbnailsList) {
        this.thumbnailsList = thumbnailsList;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.preHotspot:
                //切换到上一个
                if (hotspotList.isEmpty() || currentHotPosition < 0) {
                    return;
                }
                if (currentHotPosition != 0) {
                    currentHotPosition--;
                }
                clickedHotSpot = hotspotList.get(currentHotPosition);
                invalidateHotInfo();
                break;
            case R.id.nextHotspot:
                //切换到下一个
                if (hotspotList.isEmpty() || currentHotPosition < 0) {
                    return;
                }
                if (currentHotPosition != hotspotList.size() - 1) {
                    currentHotPosition++;
                    clickedHotSpot = hotspotList.get(currentHotPosition);
                }
                invalidateHotInfo();
                break;
            case R.id.hotspotImage:
            case R.id.spotBottomRoot:
                //跳转
                if (onIndicatorTouchListener != null && clickedHotSpot != null) {
                    onIndicatorTouchListener.onIndicatorTouch(clickedHotSpot.getHotspotPosition());
                }
                dismissPopupWindow();
                break;
            default:
                break;
        }
    }

    /**
     * 重绘帧预览相关UI
     */
    private void invalidatePreviewInfo() {
        previewDuration.setText(getMinuteSecondStr(currentDuration / 1000));
        durationText.setText(String.format("/%s", getMinuteSecondStr(duration / 1000)));
        int nearByHotspotIndex = nearByHotspot(currentDuration);
        if (nearByHotspotIndex != -1) {
            previewDesc.setText(hotspotList.get(nearByHotspotIndex).getHotspotDesc());
            previewDesc.setVisibility(VISIBLE);
        } else {
            previewDesc.setVisibility(INVISIBLE);
        }
        int thumbnailsPageIndex = (int) (currentDuration / (6 * 1000 * 100));
        int thumbnailsIndex = (int) ((currentDuration / (6 * 1000)) - thumbnailsPageIndex * 100);
        if (thumbnailsList != null && !thumbnailsList.isEmpty() && thumbnailsPageIndex < thumbnailsList.size()) {
            previewImage.setVisibility(VISIBLE);
            String url = thumbnailsList.get(thumbnailsPageIndex);
            decodePreview(url, thumbnailsIndex);
        } else {
            previewImage.setVisibility(INVISIBLE);
        }
    }

    private void decodePreview(final String url, final int thumbnailsIndex) {
        Glide.with(mContext).asBitmap().load(url).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                int left = 160 * (thumbnailsIndex % 10);
                int top = 90 * (thumbnailsIndex / 10);
                Bitmap bitmap = Bitmap.createBitmap(resource, left, top, 160, 90);
                previewImage.setImageBitmap(bitmap);
            }
        });
    }

    /**
     * 当前拖动进度是否在打点附近
     *
     * @return nearBy
     */
    private int nearByHotspot(long currentDuration) {
        if (hotspotList == null || hotspotList.isEmpty()) {
            return -1;
        }
        for (int i = 0; i < hotspotList.size(); i++) {
            int spotDuration = hotspotList.get(i).hotspotPosition * 1000;
            boolean nearBy = Math.abs(currentDuration - spotDuration) <= 1500;
            if (nearBy) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 重绘打点相关UI
     */
    @SuppressLint("DefaultLocale")
    private void invalidateHotInfo() {
        if (currentHotPosition != -1 && hotspotList != null && !hotspotList.isEmpty()) {
            preHotspot.setVisibility(currentHotPosition > 0 ? VISIBLE : INVISIBLE);
            nextHotspot.setVisibility(currentHotPosition == (hotspotList.size() - 1) ? INVISIBLE : VISIBLE);
            spotIndex.setText(String.format("看点%d", currentHotPosition + 1));
            spotTime.setText(getMinuteSecondStr(clickedHotSpot.getHotspotPosition()));
            hotspotDescTextView.setText(clickedHotSpot.getHotspotDesc());
            if (hotSpotInfoMap != null && !hotSpotInfoMap.isEmpty()) {
                int time = hotspotList.get(currentHotPosition).hotspotPosition;
                if (hotSpotInfoMap.containsKey(time)) {
                    HotSpotInfo hotSpotInfo = hotSpotInfoMap.get(time);
                    if (hotSpotInfo != null) {
                        String spotImagePath = hotSpotInfo.getSpotImagePath();
                        if (spotImagePath != null && !TextUtils.isEmpty(spotImagePath)) {
                            Glide.with(mContext).load(spotImagePath).into(hotspotImage);
                        }
                    } else {
                        loadPreview();
                    }
                } else {
                    loadPreview();
                }
            } else {
                loadPreview();
            }
        }
    }

    private void loadPreview() {
        clickedHotSpot = hotspotList.get(currentHotPosition);
        int current = (int) (duration * clickedHotSpot.hotspotPercent);
        int thumbnailsPageIndex = (current / (6 * 1000 * 100));
        final int thumbnailsIndex = ((current / (6 * 1000)) - thumbnailsPageIndex * 100);
        if (thumbnailsList != null && !thumbnailsList.isEmpty() && thumbnailsPageIndex < thumbnailsList.size()) {
            String url = thumbnailsList.get(thumbnailsPageIndex);
            Glide.with(mContext).asBitmap().load(url).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    int left = 160 * (thumbnailsIndex % 10);
                    int top = 90 * (thumbnailsIndex / 10);
                    Bitmap bitmap = Bitmap.createBitmap(resource, left, top, 160, 90);
                    hotspotImage.setImageBitmap(bitmap);
                }
            });
        } else {
            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
            Toast.makeText(mContext, "资源加载中，请稍后", Toast.LENGTH_SHORT).show();
        }
    }


    private PopupWindow popupWindow;
    private ImageView preHotspot, hotspotImage, nextHotspot;
    private TextView spotIndex, spotTime, hotspotDescTextView;

    private void showPopupWindow() {
        if ((hotSpotInfoMap == null || hotSpotInfoMap.isEmpty()) && (thumbnailsList == null || thumbnailsList.isEmpty())) {
            Toast.makeText(mContext, "资源加载中，请稍后", Toast.LENGTH_SHORT).show();
            return;
        }
        if (popupWindow == null) {
            initPopupWindow();
        }
        invalidateHotInfo();
        popupPopupWindow();
    }


    /**
     * 图文打点相关
     */
    private void initPopupWindow() {
        popupWindow = new PopupWindow(mContext);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        final View view = LayoutInflater.from(mContext).inflate(R.layout.indicator_popupwindow, null);
        preHotspot = view.findViewById(R.id.preHotspot);
        preHotspot.setOnClickListener(this);
        nextHotspot = view.findViewById(R.id.nextHotspot);
        nextHotspot.setOnClickListener(this);
        hotspotImage = view.findViewById(R.id.hotspotImage);
        hotspotImage.setOnClickListener(this);
        view.findViewById(R.id.spotBottomRoot).setOnClickListener(this);
        spotIndex = view.findViewById(R.id.spotIndex);
        spotTime = view.findViewById(R.id.spotTime);
        hotspotDescTextView = view.findViewById(R.id.hotspot_desc);
        invalidateHotInfo();
        popupWindow.setContentView(view);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setOutsideTouchable(true);
    }

    private void popupPopupWindow() {
        if (isPopupWindowShow()) {
            return;
        }
        int[] position = new int[2];
        getLocationOnScreen(position);
        popupWindow.getContentView().measure(0, 0);
        popupWindow.showAtLocation(this, Gravity.CENTER, 0, 0);
    }

    /**
     * 提示框是否弹出
     */
    public boolean isPopupWindowShow() {
        return popupWindow != null && popupWindow.isShowing();
    }

    /**
     * 隐藏弹出来的提示框
     */
    public void dismissPopupWindow() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    public interface OnSeekBarChangeListener {
        /**
         * 开始滑动
         *
         * @param seekBar seekBar
         */
        void onStartTrackingTouch(HotspotSeekBar seekBar);

        /**
         * 结束滑动
         *
         * @param seekBar          seekBar
         * @param trackStopPercent 结束滑动的百分比位置
         */
        void onStopTrackingTouch(HotspotSeekBar seekBar, float trackStopPercent);
    }

    private OnSeekBarChangeListener onSeekBarChangeListener;

    /**
     * 设置seekBar的滑动监听器
     */
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener onSeekBarChangeListener) {
        this.onSeekBarChangeListener = onSeekBarChangeListener;
    }

    public interface OnIndicatorTouchListener {
        /**
         * 回调当前点击的指示框对应的播放器的位置，时间单位为s
         *
         * @param currentPosition currentPosition
         */
        void onIndicatorTouch(int currentPosition);
    }

    OnIndicatorTouchListener onIndicatorTouchListener;

    /**
     * 设置点击指示框的监听
     */
    public void setOnIndicatorTouchListener(OnIndicatorTouchListener onIndicatorTouchListener) {
        this.onIndicatorTouchListener = onIndicatorTouchListener;
    }


    /**
     * 设置热点位置信息
     *
     * @param videoMarks 热点信息
     * @param duration   播放器总时长
     */
    public void setHotSpotPosition(TreeMap<Integer, String> videoMarks, float duration) {
        hotspotList.clear();
        for (Map.Entry<Integer, String> entry : videoMarks.entrySet()) {
            float hotspotPercent = entry.getKey().floatValue() / duration;
            hotspotList.add(new HotSpot(entry.getKey(), entry.getValue(), hotspotPercent));
        }
        postInvalidate();
    }

    public void setHotspotInfo(LinkedHashMap<Integer, HotSpotInfo> hotSpotInfoMap) {
        this.hotSpotInfoMap = hotSpotInfoMap;
    }

    /**
     * 清除已有的打点信息
     */
    public void clearHotSpots() {
        if (hotspotList != null) {
            hotspotList.clear();
        }
        if (thumbnailsList != null) {
            thumbnailsList.clear();
        }
        if (hotSpotInfoMap != null) {
            hotSpotInfoMap.clear();
        }
    }

    /**
     * 格式化时间为分秒的形式
     */
    private String getMinuteSecondStr(int time) {
        int minute = time / 60;
        int second = time % 60;
        return addZeroOnTime(minute) + ":" + addZeroOnTime(second);
    }

    /**
     * 格式化时间为分秒的形式
     */
    private String getMinuteSecondStr(long time) {
        long minute = time / 60;
        long second = time % 60;
        return addZeroOnTime(minute) + ":" + addZeroOnTime(second);
    }

    /**
     * 格式化时间，十位为0的时候，十位补0。如0，则返回"00"
     */
    private String addZeroOnTime(int time) {
        if (time < 10) {
            return "0" + time;
        } else {
            return String.valueOf(time);
        }
    }

    /**
     * 格式化时间，十位为0的时候，十位补0。如0，则返回"00"
     */
    private String addZeroOnTime(long time) {
        if (time < 10) {
            return "0" + time;
        } else {
            return String.valueOf(time);
        }
    }

    /**
     * 获取当前的屏幕宽度
     */
    private int getWindowWidth() {
        return mWindowManager.getDefaultDisplay().getWidth();
    }

    private void initPaint() {
        backgroundPaint = getPaint(SeekBarColorConfig.BACKGROUND_COLOR);
        secondProgressPaint = getPaint(SeekBarColorConfig.SECOND_PROGRESS_COLOR);
        progressPaint = getPaint(SeekBarColorConfig.PLAY_PROGRESS_COLOR);
        thumbPaint = getPaint(SeekBarColorConfig.THUMB_COLOR);
        hotSpotPaint = getPaint(SeekBarColorConfig.HOTSPOT_COLOR);
        knowledgeRangePaint = getPaint(SeekBarColorConfig.KNOWLEDGE_RANGE_COLOR);
        knowledgePaint = getPaint(SeekBarColorConfig.KNOWLEDGE_COLOR);
    }

    private Paint getPaint(int color) {
        Paint mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(color);
        return mPaint;
    }

    /**
     * 热点类
     */
    private static class HotSpot {
        private final int hotspotPosition;
        private final String hotspotDesc;
        /**
         * 热点的百分比位置
         */
        private final float hotspotPercent;

        public HotSpot(int hotspotPosition, String markPosition, float timePercent) {
            this.hotspotPosition = hotspotPosition;
            this.hotspotDesc = markPosition;
            this.hotspotPercent = timePercent;
        }

        public int getHotspotPosition() {
            return hotspotPosition;
        }

        public String getHotspotDesc() {
            return hotspotDesc;
        }

        public float getHotspotPercent() {
            return hotspotPercent;
        }
    }

}