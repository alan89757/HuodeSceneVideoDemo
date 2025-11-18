package com.bokecc.vod.function.signin;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bokecc.vod.data.SignInBean;
import com.bokecc.vod.R;
import com.bokecc.vod.function.BasePopupWindow;

/**
 * 投票/答题卡功能
 */
public class SignInPopup extends BasePopupWindow {

    private TextView tvTitle,tvContent,tvNum;
    private Button btSign;
    private ImageView ivSign;
    private OnSignInListener onSignInListener;
    public SignInPopup(Context context,OnSignInListener onSignInListener) {
        super(context);
        this.onSignInListener=onSignInListener;
    }


    @Override
    protected void onViewCreated() {
        tvTitle = findViewById(R.id.tv_signin_title);
        tvContent = findViewById(R.id.tv_signin_content);
        btSign = findViewById(R.id.bt_signin);
        ivSign = findViewById(R.id.iv_signin);
        tvNum = findViewById(R.id.tv_signin_num);
        setOutsideCancel(false);
        setBackPressedCancel(false);
        setBackPressCancel(false);
        btSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onSignInListener!=null){
                    onSignInListener.onSignIn();
                }
                dismiss();
            }
        });
    }
    public void show(SignInBean signInBean,View view,int num){
        tvTitle.setText(TextUtils.isEmpty(signInBean.getTitle())?"":signInBean.getTitle());
        tvContent.setText(TextUtils.isEmpty(signInBean.getContent())?"":signInBean.getContent());
        String btnText = signInBean.getBtnText();
        if (TextUtils.isEmpty(btnText)){
            btnText="";
        }else{
            char[] chars = btnText.toCharArray();
            if (chars.length>8){
                StringBuilder sb = new StringBuilder();
                for (int i =0;i<8;i++){
                    sb.append(chars[i]);
                }
                btnText = sb.toString();
            }
        }
        btSign.setText(btnText);
        tvNum.setText(String.valueOf(num));
        super.show(view);
    }
    @Override
    protected int getContentView() {
        return R.layout.signin_layout;
    }

    @Override
    protected Animation getEnterAnimation() {
        return null;
    }

    @Override
    protected Animation getExitAnimation() {
        return null;
    }

    public interface OnSignInListener {

        /**
         * 消失后调用
         */
        void onSignIn();
    }
}