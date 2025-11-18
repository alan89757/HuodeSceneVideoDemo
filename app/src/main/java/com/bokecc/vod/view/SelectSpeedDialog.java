package com.bokecc.vod.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.bokecc.vod.R;
import com.bokecc.vod.inter.SelectSpeed;

import java.util.Map;

/**
 * 倍速播放
 */
public class SelectSpeedDialog extends Dialog {

    private final Context context;
    private final SelectSpeed selectSpeed;
    private final float currentSpeed;

    public SelectSpeedDialog(Context context, float currentSpeed, SelectSpeed selectSpeed) {
        super(context, R.style.SetVideoDialog);
        this.context = context;
        this.selectSpeed = selectSpeed;
        this.currentSpeed = currentSpeed;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_select_speed, null);
        setContentView(view);
        TextView tvZeroPointFive = (TextView) view.findViewById(R.id.tv_zero_point_five);
        TextView tvOnePointZero = (TextView) view.findViewById(R.id.tv_one_point_zero);
        TextView tvOnePointFive = (TextView) view.findViewById(R.id.tv_one_point_five);
        TextView tvTwoPointZero = (TextView) view.findViewById(R.id.tv_two_point_zero);
        TextView tvThreePointZero = (TextView) view.findViewById(R.id.tv_three_point_zero);

        if (currentSpeed == 0.5f) {
            tvZeroPointFive.setTextColor(context.getResources().getColor(R.color.orange));
        } else if (currentSpeed == 1.0f) {
            tvOnePointZero.setTextColor(context.getResources().getColor(R.color.orange));
        } else if (currentSpeed == 1.5f) {
            tvOnePointFive.setTextColor(context.getResources().getColor(R.color.orange));
        } else if (currentSpeed == 2.0f) {
            tvTwoPointZero.setTextColor(context.getResources().getColor(R.color.orange));
        }else if (currentSpeed == 3.0f){
            tvThreePointZero.setTextColor(context.getResources().getColor(R.color.orange));
        }

        tvZeroPointFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectSpeed != null) {
                    selectSpeed.selectedSpeed(0.5f);
                    dismiss();
                }
            }
        });

        tvOnePointZero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectSpeed != null) {
                    selectSpeed.selectedSpeed(1.0f);
                    dismiss();
                }
            }
        });

        tvOnePointFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectSpeed != null) {
                    selectSpeed.selectedSpeed(1.5f);
                    dismiss();
                }
            }
        });

        tvTwoPointZero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectSpeed != null) {
                    selectSpeed.selectedSpeed(2.0f);
                    dismiss();
                }
            }
        });
        tvThreePointZero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectSpeed != null) {
                    selectSpeed.selectedSpeed(3.0f);
                    dismiss();
                }
            }
        });

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics();
        lp.width = (int) (d.widthPixels * 0.35);
        lp.height = (int) (d.heightPixels * 1.0);
        dialogWindow.setAttributes(lp);
        dialogWindow.setGravity(Gravity.RIGHT);
    }

}
