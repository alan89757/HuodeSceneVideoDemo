package com.bokecc.vod.function.signin;

import com.bokecc.vod.data.SignInBean;

public interface SignInManagerCallBack {
    void onSignInStart(SignInBean data, int num);
    void onSignInClose();
}
