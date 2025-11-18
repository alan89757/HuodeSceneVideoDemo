package com.bokecc.vod.data;

import android.text.TextUtils;

import com.bokecc.vod.BuildConfig;
import com.bokecc.vod.R;

import java.util.ArrayList;
import java.util.List;

/**
 * DataUtil
 *
 * @author xxx
 */
public class DataUtil {

    public static ArrayList<HuodeVideoInfo> getVideoList() {
        ArrayList<HuodeVideoInfo> data = new ArrayList<>();
        if (!TextUtils.isEmpty(BuildConfig.videoId)){
            String[] split = BuildConfig.videoId.split(",");
            if (split.length>0){
                for (int i =0;i<split.length;i++){
                    HuodeVideoInfo videoInfo = new HuodeVideoInfo(split[i], split[i]);
                    data.add(videoInfo);
                }
            }
        }
        return data;
    }

    public static SignInBean getSignInList() {
        if (!BuildConfig.isShowSignin){
            return null;
        }
        SignInBean signInBean = new SignInBean();
        signInBean.setResImg(R.mipmap.sign_in);
        signInBean.setTitle("学习签到");
        signInBean.setContent("学员Z你好，请点击签到后继续学习~");
        signInBean.setBtnText("签到");
        List<Long> seconds = new ArrayList<>();
        seconds.add(60000L);
        seconds.add(90000L);
        seconds.add(120000L);
        seconds.add(150000L);
        seconds.add(180000L);
        signInBean.setSeconds(seconds);

        return signInBean;
    }
}
