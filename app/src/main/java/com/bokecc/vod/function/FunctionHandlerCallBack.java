package com.bokecc.vod.function;

import com.bokecc.vod.data.SignInBean;

public interface FunctionHandlerCallBack {
    void onSignInStart(SignInBean data, int num);
    void onSignInClose();
}
