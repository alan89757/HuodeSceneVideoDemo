package com.bokecc.vod.function;

import android.app.Activity;

import com.bokecc.vod.data.SignInBean;
import com.bokecc.vod.function.signin.SignInManager;
import com.bokecc.vod.function.signin.SignInManagerCallBack;

public class FunctionHandler {
    private Activity context;
    private FunctionHandlerCallBack callBack;
    private SignInManager signInManager;
    private SignInManagerCallBack signInManagerCallBack = new SignInManagerCallBack() {
        @Override
        public void onSignInStart(SignInBean data, int num) {
            if (callBack!=null){
                callBack.onSignInStart(data,num);
            }
        }

        @Override
        public void onSignInClose() {
            if (callBack!=null){
                callBack.onSignInClose();
            }
        }
    };
    public  FunctionHandler(FunctionHandlerCallBack callBack, Activity activity){
        this.callBack=callBack;
        this.context=activity;
        signInManager = new SignInManager(context,signInManagerCallBack,activity.getWindow().getDecorView());
    }
    public void setSignInDate(SignInBean data){
        signInManager.setData(data);
    }
    public void onTimeChange(long time){
        signInManager.timeChange(time);
    }
    public void reShowSignin(){
        signInManager.reShowSignin();
    }
    public void onDestroy(){
        callBack = null;
        signInManager.destroy();
        signInManager = null;
    }
}
