package com.bokecc.vod.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bokecc.sdk.mobile.core.Core;
import com.bokecc.sdk.mobile.play.PlayInfo;
import com.bokecc.vod.R;
import com.bokecc.vod.utils.NetUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CheckNetworkDialog extends Dialog {
    private final Activity context;
    private final String videoId;
    private String videoServerUrl;
    private String pingVideoUrl;
    private String netType;
    private final PlayInfo playInfo;
    private ClipboardManager cmb;
    private final String ccUrl = "http://p.bokecc.com";
    private int successTimesCC, failTimesCC, totalRttTime;
    private int successTimesVideoServer, failTimesVideoServer, totalRttTimeVideoServer;
    private int firstCcAvgRTT, secondCcAvgRTT, thirdCcAvgRTT, fourthCcAvgRTT;
    private int firstVideoServerAvgRTT, secondVideoServerAvgRTT, thirdVideoServerAvgRTT, fourthVideoServerAvgRTT;
    private final List<Integer> rtt = new ArrayList<>();
    private final List<Integer> rttVideoServer = new ArrayList<>();
    private boolean isPingCcOk = false, isPingVideoServerOk = false;
    private LinearLayout ll_ping_video_server, ll_ping_cc;
    private ProgressBar pb_ping_video_server, pb_ping_cc;
    private TextView tv_network, tv_first_ping_bokecc, tv_second_ping_bokecc, tv_third_ping_bokecc,
            tv_fourth_ping_bokecc, tv_result_ping_bokecc, tv_abstract_ping_bokecc, tv_video_title,
            tv_play_url, tv_ping_video_server, tv_first_ping_video_server, tv_second_ping_video_server,
            tv_third_ping_video_server, tv_fourth_ping_video_server, tv_result_ping_video_server,
            tv_abstract_ping_video_server, tv_local_ip, tv_videoid;

    public CheckNetworkDialog(@NonNull Activity context, String videoId, PlayInfo playInfo) {
        super(context, R.style.CheckNetworkDialog);
        this.context = context;
        this.videoId = videoId;
        this.playInfo = playInfo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_check_network, null);
        setContentView(view);
        videoServerUrl = playInfo.getPlayUrl();
        ImageView iv_back_check_network = view.findViewById(R.id.iv_back_check_network);
        tv_videoid = view.findViewById(R.id.tv_videoid);
        tv_network = view.findViewById(R.id.tv_network);
        tv_first_ping_bokecc = view.findViewById(R.id.tv_first_ping_bokecc);
        tv_second_ping_bokecc = view.findViewById(R.id.tv_second_ping_bokecc);
        tv_third_ping_bokecc = view.findViewById(R.id.tv_third_ping_bokecc);
        tv_fourth_ping_bokecc = view.findViewById(R.id.tv_fourth_ping_bokecc);
        tv_result_ping_bokecc = view.findViewById(R.id.tv_result_ping_bokecc);
        tv_abstract_ping_bokecc = view.findViewById(R.id.tv_abstract_ping_bokecc);
        tv_video_title = view.findViewById(R.id.tv_video_title);
        tv_play_url = view.findViewById(R.id.tv_play_url);
        tv_ping_video_server = view.findViewById(R.id.tv_ping_video_server);
        tv_first_ping_video_server = view.findViewById(R.id.tv_first_ping_video_server);
        tv_second_ping_video_server = view.findViewById(R.id.tv_second_ping_video_server);
        tv_third_ping_video_server = view.findViewById(R.id.tv_third_ping_video_server);
        tv_fourth_ping_video_server = view.findViewById(R.id.tv_fourth_ping_video_server);
        tv_result_ping_video_server = view.findViewById(R.id.tv_result_ping_video_server);
        tv_abstract_ping_video_server = view.findViewById(R.id.tv_abstract_ping_video_server);
        ll_ping_video_server = view.findViewById(R.id.ll_ping_video_server);
        ll_ping_cc = view.findViewById(R.id.ll_ping_cc);
        pb_ping_video_server = view.findViewById(R.id.pb_ping_video_server);
        pb_ping_cc = view.findViewById(R.id.pb_ping_cc);
        Button btnCopyCheckInfo = view.findViewById(R.id.btn_copy_check_info);
        Button btn_recheck = view.findViewById(R.id.btn_recheck);
        tv_local_ip = view.findViewById(R.id.tv_local_ip);
        tv_videoid.setText(videoId);
        iv_back_check_network.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics();
        lp.width = (int) (d.widthPixels * 1.0);
        lp.height = (int) (d.heightPixels * 1.0);
        dialogWindow.setAttributes(lp);


        if (playInfo != null) {
            tv_video_title.setText(playInfo.getTitle());
            tv_play_url.setText(playInfo.getPlayUrl());
            if (!TextUtils.isEmpty(videoServerUrl)) {
                if (videoServerUrl.startsWith("https")) {
                    pingVideoUrl = videoServerUrl.substring(8);
                } else if (videoServerUrl.startsWith("http")) {
                    pingVideoUrl = videoServerUrl.substring(7);
                }
                tv_ping_video_server.setText(String.format("ping %s", pingVideoUrl));
            }
        }
        checkNetInfo();

        btnCopyCheckInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPingCcOk && isPingVideoServerOk) {
                    String checkInfo = "视频ID：" + getText(tv_videoid) + "\n" + "当前网络：" + getText(tv_network) + "\n" + "本地出口IP：" + getText(tv_local_ip)
                            + "\n" + "ping p.bokecc.com" + "\n" + getText(tv_first_ping_bokecc) + "\n" + getText(tv_second_ping_bokecc) + "\n" + getText(tv_third_ping_bokecc)
                            + "\n" + getText(tv_fourth_ping_bokecc) + "\n" + getText(tv_result_ping_bokecc) + "\n" + getText(tv_abstract_ping_bokecc) + "\n" + "视频信息：" + getText(tv_video_title)
                            + "\n" + "服务地址：" + getText(tv_play_url) + "\n" + "ping " + getText(tv_ping_video_server) + "\n" + getText(tv_first_ping_video_server)
                            + "\n" + getText(tv_second_ping_video_server) + "\n" + getText(tv_third_ping_video_server) + "\n" + getText(tv_fourth_ping_video_server)
                            + "\n" + getText(tv_result_ping_video_server) + "\n" + getText(tv_abstract_ping_video_server);
                    cmb.setText(checkInfo);
                    Toast.makeText(context, "复制剪切信息成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "请稍候，本次网络检测还没结束", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_recheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPingCcOk && isPingVideoServerOk) {
                    isPingCcOk = false;
                    isPingVideoServerOk = false;
                    successTimesCC = 0;
                    failTimesCC = 0;
                    totalRttTime = 0;
                    successTimesVideoServer = 0;
                    failTimesVideoServer = 0;
                    totalRttTimeVideoServer = 0;
                    rtt.clear();
                    rttVideoServer.clear();
                    ll_ping_video_server.setVisibility(View.GONE);
                    pb_ping_video_server.setVisibility(View.VISIBLE);
                    pb_ping_cc.setVisibility(View.VISIBLE);
                    ll_ping_cc.setVisibility(View.GONE);
                    checkNetInfo();
                } else {
                    Toast.makeText(context, "请稍候，本次网络检测还没结束", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private String getText(TextView textView) {
        return textView.getText().toString();
    }

    private void checkNetInfo() {
        //获取当前网络状态
        getNetworkType(tv_network);
        //获取本地出口IP
        Core.getInstance().getExecutorSupplier()
                .forBackgroundTasks()
                .execute(new Runnable() {
                    @Override
                    public void run() {
                        final String localIPAddress = NetUtils.getOutNetIP(context, 0);

                        //获取第一次ping值
                        final String ipFromUrl = NetUtils.getIPFromUrl(ccUrl);
                        firstCcAvgRTT = NetUtils.getAvgRTT(ccUrl);
                        secondCcAvgRTT = NetUtils.getAvgRTT(ccUrl);
                        thirdCcAvgRTT = NetUtils.getAvgRTT(ccUrl);
                        fourthCcAvgRTT = NetUtils.getAvgRTT(ccUrl);
                        if (firstCcAvgRTT > 0) {
                            successTimesCC = successTimesCC + 1;
                            rtt.add(firstCcAvgRTT);
                            totalRttTime = totalRttTime + firstCcAvgRTT;
                        } else {
                            failTimesCC = failTimesCC + 1;
                        }

                        if (secondCcAvgRTT > 0) {
                            successTimesCC = successTimesCC + 1;
                            rtt.add(secondCcAvgRTT);
                            totalRttTime = totalRttTime + secondCcAvgRTT;
                        } else {
                            failTimesCC = failTimesCC + 1;
                        }

                        if (thirdCcAvgRTT > 0) {
                            successTimesCC = successTimesCC + 1;
                            rtt.add(thirdCcAvgRTT);
                            totalRttTime = totalRttTime + thirdCcAvgRTT;
                        } else {
                            failTimesCC = failTimesCC + 1;
                        }

                        if (fourthCcAvgRTT > 0) {
                            successTimesCC = successTimesCC + 1;
                            rtt.add(fourthCcAvgRTT);
                            totalRttTime = totalRttTime + fourthCcAvgRTT;
                        } else {
                            failTimesCC = failTimesCC + 1;
                        }


                        context.runOnUiThread(new Runnable() {
                            @SuppressLint("DefaultLocale")
                            @Override
                            public void run() {
                                tv_local_ip.setText(localIPAddress);
                                pb_ping_cc.setVisibility(View.GONE);
                                ll_ping_cc.setVisibility(View.VISIBLE);
                                tv_first_ping_bokecc.setText(String.format("来自 %s 的回复：字节=32  时间=%dms", ipFromUrl, firstCcAvgRTT));
                                tv_second_ping_bokecc.setText(String.format("来自 %s 的回复：字节=32  时间=%dms", ipFromUrl, secondCcAvgRTT));
                                tv_third_ping_bokecc.setText(String.format("来自 %s 的回复：字节=32  时间=%dms", ipFromUrl, thirdCcAvgRTT));
                                tv_fourth_ping_bokecc.setText(String.format("来自 %s 的回复：字节=32  时间=%dms", ipFromUrl, fourthCcAvgRTT));
                                int failRate = failTimesCC * 100 / 4;
                                tv_result_ping_bokecc.setText(String.format("数据包： 已发送 = 4， 已接收 = %d，丢失 = %d（%d%% 丢失）", successTimesCC, failTimesCC, failRate));
                                Integer max = Collections.max(rtt);
                                Integer min = Collections.min(rtt);
                                if (max == null) {
                                    max = 0;
                                }
                                if (min == null) {
                                    min = 0;
                                }
                                int avRttTime = totalRttTime / successTimesCC;
                                tv_abstract_ping_bokecc.setText(String.format("最短 = %dms， 最长 = %dms， 平均 = %dms", min, max, avRttTime));
                                isPingCcOk = true;
                            }
                        });
                    }
                });

        Core.getInstance().getExecutorSupplier()
                .forBackgroundTasks()
                .execute(new Runnable() {
                    @Override
                    public void run() {
                        //获取第一次ping值
                        final String videoServerIp = NetUtils.getIPFromUrl(videoServerUrl);
                        firstVideoServerAvgRTT = NetUtils.getAvgRTT(videoServerUrl);
                        secondVideoServerAvgRTT = NetUtils.getAvgRTT(videoServerUrl);
                        thirdVideoServerAvgRTT = NetUtils.getAvgRTT(videoServerUrl);
                        fourthVideoServerAvgRTT = NetUtils.getAvgRTT(videoServerUrl);

                        if (firstVideoServerAvgRTT > 0) {
                            successTimesVideoServer = successTimesVideoServer + 1;
                            rttVideoServer.add(firstVideoServerAvgRTT);
                            totalRttTimeVideoServer = totalRttTimeVideoServer + firstVideoServerAvgRTT;
                        } else {
                            failTimesVideoServer = failTimesVideoServer + 1;
                        }

                        if (secondVideoServerAvgRTT > 0) {
                            successTimesVideoServer = successTimesVideoServer + 1;
                            rttVideoServer.add(secondVideoServerAvgRTT);
                            totalRttTimeVideoServer = totalRttTimeVideoServer + secondVideoServerAvgRTT;
                        } else {
                            failTimesVideoServer = failTimesVideoServer + 1;
                        }

                        if (thirdVideoServerAvgRTT > 0) {
                            successTimesVideoServer = successTimesVideoServer + 1;
                            rttVideoServer.add(thirdVideoServerAvgRTT);
                            totalRttTimeVideoServer = totalRttTimeVideoServer + thirdVideoServerAvgRTT;
                        } else {
                            failTimesVideoServer = failTimesVideoServer + 1;
                        }

                        if (fourthVideoServerAvgRTT > 0) {
                            successTimesVideoServer = successTimesVideoServer + 1;
                            rttVideoServer.add(fourthVideoServerAvgRTT);
                            totalRttTimeVideoServer = totalRttTimeVideoServer + fourthVideoServerAvgRTT;
                        } else {
                            failTimesVideoServer = failTimesVideoServer + 1;
                        }

                        context.runOnUiThread(new Runnable() {
                            @SuppressLint("DefaultLocale")
                            @Override
                            public void run() {
                                ll_ping_video_server.setVisibility(View.VISIBLE);
                                pb_ping_video_server.setVisibility(View.GONE);
                                tv_first_ping_video_server.setText(String.format("来自 %s 的回复：字节=32  时间=%dms", videoServerIp, firstVideoServerAvgRTT));
                                tv_second_ping_video_server.setText(String.format("来自 %s 的回复：字节=32  时间=%dms", videoServerIp, secondVideoServerAvgRTT));
                                tv_third_ping_video_server.setText(String.format("来自 %s 的回复：字节=32  时间=%dms", videoServerIp, thirdVideoServerAvgRTT));
                                tv_fourth_ping_video_server.setText(String.format("来自 %s 的回复：字节=32  时间=%dms", videoServerIp, fourthVideoServerAvgRTT));
                                int failRate = failTimesVideoServer * 100 / 4;
                                tv_result_ping_video_server.setText(String.format("数据包： 已发送 = 4， 已接收 = %d，丢失 = %d（%d%% 丢失）", successTimesVideoServer, failTimesVideoServer, failRate));
                                Integer max = Collections.max(rttVideoServer);
                                Integer min = Collections.min(rttVideoServer);
                                if (max == null) {
                                    max = 0;
                                }
                                if (min == null) {
                                    min = 0;
                                }
                                int avRttTime = totalRttTimeVideoServer / successTimesVideoServer;
                                tv_abstract_ping_video_server.setText(String.format("最短 = %dms， 最长 = %dms， 平均 = %dms", min, max, avRttTime));
                                isPingVideoServerOk = true;
                            }
                        });
                    }
                });
    }

    private void getNetworkType(TextView tv_network) {
        int networkState = NetUtils.getNetworkState(context);
        switch (networkState) {
            case 0:
                netType = "没有网络连接";
                break;
            case 1:
                netType = "Wifi";
                break;
            case 2:
                netType = "2G";
                break;
            case 3:
                netType = "3G";
                break;
            case 4:
                netType = "4G";
                break;
            case 5:
                netType = "手机流量";
                break;
            default:
                break;
        }
        tv_network.setText(netType);
    }
}
