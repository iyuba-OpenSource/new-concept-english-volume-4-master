package com.iyuba.conceptEnglish.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.event.WelcomeBackEvent;
import com.iyuba.core.common.base.BasisActivity;

import org.greenrobot.eventbus.EventBus;

public class WebActivity extends BasisActivity {  //实现程序启动广告链接

    private Button backButton;
    private WebView web;
    private TextView textView;

    //关闭按钮，这里用不到，直接隐藏
    private TextView tvClose;

    public static Intent buildIntent(Context context,String url,String title){
        Intent intent  = new Intent();
        intent.setClass(context,WebActivity.class);
        intent.putExtra("url",url);
        intent.putExtra("title",title);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.web);
        setProgressBarVisibility(true);
        backButton = (Button) findViewById(R.id.button_back);
        textView = (TextView) findViewById(R.id.play_title_info);
        web = (WebView) findViewById(R.id.webView);
        backButton.setOnClickListener(v -> {
            // TODO Auto-generated method stub
//                onBackPressed();
            finish();

            EventBus.getDefault().post(new WelcomeBackEvent());
        });
        web.loadUrl(this.getIntent().getStringExtra("url"));
        String title = getIntent().getStringExtra("title");
        if (TextUtils.isEmpty(title)){
            title = "精品应用";
        }
        textView.setText(title);
        WebSettings websettings = web.getSettings();
        websettings.setJavaScriptEnabled(true);
        websettings.setBuiltInZoomControls(true);
        websettings.setDomStorageEnabled(true);

        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                if(url == null) return false;

                try {
                    if (url.startsWith("http:") || url.startsWith("https:"))
                    {
                        view.loadUrl(url);
                        return true;
                    } else {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                        return true;
                    }
                } catch (Exception e) { //防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
                    return false;
                }
            }
        });

        web.setWebChromeClient(new WebChromeClient() {
            // Set progress bar during loading
            public void onProgressChanged(WebView view, int progress) {
                setProgress(progress * 100);
            }
        });
        web.setDownloadListener(new DownloadListener() {

            @Override
            // TODO Auto-generated method stub
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        //关闭按钮隐藏
        tvClose = findViewById(R.id.tv_close);
        tvClose.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        //这个应该是为了处理之前的链接点击返回时无法返回的问题的方案，说实话，太垃圾了
//        Intent intent3 = new Intent(WebActivity.this, MainFragmentActivity.class);
//        intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent3.putExtra("isFirstInfo", 0);
//        startActivity(intent3);
//        finish();
//        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        if (web.canGoBack()){
            web.goBack();
        }else {
            super.onBackPressed();
        }
    }
}
