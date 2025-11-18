package com.bokecc.vod.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.bokecc.vod.R;


public class PrivacyPolicyDialog extends Dialog {

    private TextView policyContent;
    private Button ignoreButton;
    private Button agreeButton;

    public PrivacyPolicyDialog(Context context) {
        super(context);
        initView(context);
    }

    public PrivacyPolicyDialog(Context context, int style) {
        super(context, style);
        initView(context);
    }


    private void initView(Context context){
        View view = View.inflate(context, R.layout.dialog_privacy_policy,null);
        policyContent = view.findViewById(R.id.policy_content);
        policyContent.setMovementMethod(LinkMovementMethod.getInstance());
        ignoreButton = view.findViewById(R.id.ignore);
        agreeButton = view.findViewById(R.id.agree);
        setContentView(view);
    }

    public void showBottom(){
        onCreateToBottom();
        show();
    }

    public PrivacyPolicyDialog setPolicyTipString(SpannableString policy){
        policyContent.setText(policy);
        return this;
    }

    public PrivacyPolicyDialog setOnClickAgree(View.OnClickListener listener){
        agreeButton.setOnClickListener(listener);
        return this;
    }

    public PrivacyPolicyDialog setOnClickIgnore(View.OnClickListener listener){
        ignoreButton.setOnClickListener(listener);
        return this;
    }

    /**
     * 从底部弹出一个自适应高，宽占满的对话框，并且居底部
     */
    protected void onCreateToBottom() {
        Window window = this.getWindow();
        window.setWindowAnimations(R.style.cs_bottomMenuAnimStyle);
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        window.setBackgroundDrawableResource(android.R.color.transparent);
        //设置弹窗大小为会屏
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        setCanceledOnTouchOutside(false);
        setCancelable(false);
        //去除阴影
        window.setAttributes(params);
    }

}
