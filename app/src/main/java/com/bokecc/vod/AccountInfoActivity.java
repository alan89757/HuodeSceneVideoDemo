package com.bokecc.vod;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bokecc.vod.utils.MultiUtils;


/**
 * AccountInfoActivity
 * @author CC
 */
public class AccountInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_user_id, tv_api_key;
    private EditText et_verification_code;
    private ImageView iv_back;
    private TextView autoPlayStatue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);
        MultiUtils.setStatusBarColor(this, R.color.transparent, true);
        initView();
    }

    private void initView() {
        tv_user_id = findViewById(R.id.tv_user_id);
        tv_api_key = findViewById(R.id.tv_api_key);
        et_verification_code = findViewById(R.id.et_verification_code);
        autoPlayStatue = findViewById(R.id.autoPlayStatue);
        autoPlayStatue.setOnClickListener(this);
        iv_back = findViewById(R.id.iv_back);

        if (!TextUtils.isEmpty(MultiUtils.getVerificationCode())) {
            et_verification_code.setText(MultiUtils.getVerificationCode());
        }

        tv_user_id.setText(ConfigUtil.USER_ID);
        tv_api_key.setText(ConfigUtil.API_KEY);
        autoPlayStatue.setText(ConfigUtil.AutoPlay ? "是" : "否");
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final Button forceSheet = findViewById(R.id.forceSheet);
        forceSheet.setText("当前强制：" + ConfigUtil.FORCE_ANSWER_SHEET);
        findViewById(R.id.forceSheet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfigUtil.FORCE_ANSWER_SHEET = !ConfigUtil.FORCE_ANSWER_SHEET;
                forceSheet.setText("当前强制：" + ConfigUtil.FORCE_ANSWER_SHEET);
            }
        });
        findViewById(R.id.private_protocol).setOnClickListener(this);
        findViewById(R.id.server_protocol).setOnClickListener(this);
        findViewById(R.id.self_info).setOnClickListener(this);
        findViewById(R.id.third_share).setOnClickListener(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        String code = et_verification_code.getText().toString().trim();
        MultiUtils.setVerificationCode(code);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.autoPlayStatue) {
            ConfigUtil.AutoPlay = !ConfigUtil.AutoPlay;
            autoPlayStatue.setText(ConfigUtil.AutoPlay ? "是" : "否");
        }else if (v.getId() == R.id.private_protocol){
            Intent intent = new Intent(this, PolicyActivity.class);
            intent.putExtra("url","https://admin.bokecc.com/privacy.bo");
            startActivity(intent);
        }else if (v.getId() == R.id.server_protocol){
            Intent intent = new Intent(this, PolicyActivity.class);
            intent.putExtra("url","https://admin.bokecc.com/agreement.bo");
            startActivity(intent);
        }else if (v.getId() == R.id.self_info){
            Intent intent = new Intent(this, PolicyActivity.class);
            intent.putExtra("url","https://admin.bokecc.com/collectinfo.bo");
            startActivity(intent);
        }else if (v.getId() == R.id.third_share){
            Intent intent = new Intent(this, PolicyActivity.class);
            intent.putExtra("url","https://admin.bokecc.com/shareinfo.bo");
            startActivity(intent);
        }
    }
}
