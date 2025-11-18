package com.bokecc.vod.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.bokecc.vod.R;
import com.bokecc.vod.inter.ExeOperation;
import com.bokecc.vod.utils.MultiUtils;

public class ShowExeDialog extends Dialog {

    private final Context context;
    private final boolean isCanJump;
    private final ExeOperation exeOperation;

    public ShowExeDialog(Context context, boolean isCanJump, ExeOperation exeOperation) {
        super(context, R.style.DeleteFileDialog);
        this.context = context;
        this.isCanJump = isCanJump;
        this.exeOperation = exeOperation;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_exe, null);
        setContentView(view);
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = MultiUtils.dipToPx(context, 285);
        lp.height = MultiUtils.dipToPx(context, 185);
        dialogWindow.setAttributes(lp);
        setCanceledOnTouchOutside(false);

        Button btn_listen_class = view.findViewById(R.id.btn_listen_class);
        Button btn_do_exe = view.findViewById(R.id.btn_do_exe);
        ImageView iv_close_exercise = view.findViewById(R.id.iv_close_exercise);

        btn_listen_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                exeOperation.listenClass();
            }
        });

        btn_do_exe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                exeOperation.doExe();
            }
        });

        iv_close_exercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCanJump) {
                    dismiss();
                    exeOperation.jump();
                } else {
                    MultiUtils.showToast((Activity) context, "不能跳过");
                }
            }
        });
    }

}
