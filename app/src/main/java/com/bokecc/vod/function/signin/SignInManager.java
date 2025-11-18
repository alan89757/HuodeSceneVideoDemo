package com.bokecc.vod.function.signin;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.widget.PopupWindow;

import com.bokecc.vod.data.SignInBean;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SignInManager {
    private View view;
    private Activity context;
    private SignInManagerCallBack signInManagerCallBack;
    private SignInBean data;
    private List<Long> seconds;
    private SignInPopup signInPopup;
    private int num ;
    private SignInPopup.OnSignInListener onSignInListener = new SignInPopup.OnSignInListener() {
        @Override
        public void onSignIn() {
            if (signInManagerCallBack!=null){
                signInManagerCallBack.onSignInClose();
            }
        }
    };
    public SignInManager(Activity context, SignInManagerCallBack signInManagerCallBack, View view) {
        this.signInManagerCallBack=signInManagerCallBack;
        this.context=context;
        this.view=view;
    }
    public void timeChange(long time) {
        if (data == null||seconds == null||seconds.size()<=0){
            return;
        }
        if (signInPopup!=null&&signInPopup.isShowing()){
            return;
        }
        if (seconds.get(0)<=time){
            //展示弹框
            num++;
            Long remove = seconds.remove(0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O&&context.isInPictureInPictureMode()) {

            }else{
                if (signInPopup == null){
                    signInPopup = new SignInPopup(context,onSignInListener);
                    signInPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            signInPopup = null;
                        }
                    });
                }
                signInPopup.show(data,view,num);
            }
            if (signInManagerCallBack!=null){
                signInManagerCallBack.onSignInStart(data,num);
            }
        }
    }

    public void setData(SignInBean data) {
        this.data = data;
        if (data == null){
            return;
        }
        this.num=0;
        seconds = data.getSeconds();
        if (seconds!=null){
            Collections.sort(seconds, new Comparator<Long>() {
                @Override
                public int compare(Long o1, Long o2) {
                    return (int) (o1- o2);
                }
            });
            if (seconds.size()>5){
                this.seconds = seconds.subList(0,5);
            }
        }

    }

    public void destroy() {
        view = null;
        context = null;
        data = null;
        seconds = null;
        if (signInPopup!=null&&signInPopup.isShowing()){
            signInPopup.dismiss();
            signInPopup = null;
        }
    }

    public void reShowSignin() {
        if (signInPopup == null){
            signInPopup = new SignInPopup(context,onSignInListener);
            signInPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    signInPopup = null;
                }
            });
        }
        if (signInPopup.isShowing()){
            return;
        }
        signInPopup.show(data,view,num);
    }
}
