package com.bokecc.vod;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.Nullable;


public class PolicyActivity extends Activity {
    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);
        String url = getIntent().getStringExtra("url");
        WebView webView = findViewById(R.id.webView);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        // 优先渲染界面
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // 自动播放
        settings.setMediaPlaybackRequiresUserGesture(false);
        // 不支持缩放
        settings.setBuiltInZoomControls(false);
        settings.setSupportZoom(false);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        // 设置WebView字体按照Normal形式展示
        settings.setTextZoom(100);
        // 只使用网络的数据，不使用缓存
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        // WebView 默认都是支持cookies
        CookieManager.getInstance().setAcceptCookie(true);
        /*
         * 支持HTTPS、HTTP混合模式
         * http://blog.csdn.net/qq_16472137/article/details/54346078
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        /* 支持cookies 5.0以上的手机不支持自动同步第三方cookies
         *（一般都是iframe里面的页面要存储cookies操作的设置）
         * http://blog.sina.com.cn/s/blog_6e73239a0102viku.html
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        }
        // 设置和js交互接口
        webView.addJavascriptInterface(this, "android");
        webView.loadUrl(url);
    }

}
