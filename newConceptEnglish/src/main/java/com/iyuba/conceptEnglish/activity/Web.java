package com.iyuba.conceptEnglish.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.iyuba.conceptEnglish.R;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public class Web extends BasisActivity {                           //实现应用广场功能

    private Button backButton;
    private WebView web;
    private TextView textView, tv_close;
    private String mReffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.web);
        setProgressBarVisibility(true);
        CrashApplication.getInstance().addActivity(this);
        backButton = (Button) findViewById(R.id.button_back);
        textView = (TextView) findViewById(R.id.play_title_info);
        web = (WebView) findViewById(R.id.webView);
        tv_close = findViewById(R.id.tv_close);

        tv_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                onBackPressed();
            }
        });

        String url = this.getIntent().getStringExtra("url");
        if (TextUtils.isEmpty(url)){
            url = "https://act.tinman.cn/p108.html?uid=0&adId=1854&linkId=108&appId=222&channel=BJTF_RW_MENGQI_NONE&advertId=6";
            //url = "https://wxpay.wxutil.com/mch/pay/h5.v2.php";
            //            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//            startActivity(intent);
        }
        web.loadUrl(url);
        Timber.e("web" + url);
        textView.setText(this.getIntent().getStringExtra("title"));
        WebSettings websettings = web.getSettings();
        websettings.setJavaScriptEnabled(true);
        websettings.setBuiltInZoomControls(true);
        websettings.setDomStorageEnabled(true);


        web.setWebViewClient(new WebViewClient() {

            // Handle API 21+
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                ///获取请求uir
                String url = request.getUrl().toString();
                ///获取RequestHeader中的所有 key value
                Map<String, String> lRequestHeaders = request.getRequestHeaders();
                Timber.e("测试URI"+url);
                for (Map.Entry<String, String> lStringStringEntry : lRequestHeaders.entrySet()) {
                    Timber.d("测试header"+lStringStringEntry.getKey() + "  " + lStringStringEntry.getValue());
                }
                if (lRequestHeaders.containsKey("Referer")) {
                    mReffer = lRequestHeaders.get("Referer");
                }
                return super.shouldInterceptRequest(view, request);
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url == null) return false;

                if (url.startsWith("weixin://wap/pay?")) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    Timber.e("web,微信 "+url);
                    return true;
                }
                Timber.e("web,Click "+url);
                try {
                    if (url.startsWith("http:") || url.startsWith("https:")) {
                        HashMap<String, String> lStringStringHashMap = new HashMap<>();
                        if (!TextUtils.isEmpty(mReffer)) {
                            lStringStringHashMap.put("referer", mReffer);
                            view.loadUrl(url, lStringStringHashMap);
                        } else {
                            view.loadUrl(url, lStringStringHashMap);
                        }
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
                Web.this.setProgress(progress * 100);
            }
        });
        web.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (web.canGoBack()) {
            web.goBack(); // goBack()表示返回webView的上一页面
        } else if (!web.canGoBack()) {
            finish();
        }
    }
}
