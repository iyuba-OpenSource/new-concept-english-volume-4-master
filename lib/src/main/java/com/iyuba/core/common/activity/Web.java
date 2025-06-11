package com.iyuba.core.common.activity;

/**
 * 网页显示
 * 
 * @author chentong
 * @version 1.0
 * @para 传入"url" 网址；"title"标题显示
 */
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.lib.R;

public class Web extends BasisActivity {
	private ImageView backButton;
	private WebView web;
	private TextView textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.lib_web);
		setProgressBarVisibility(true);
		CrashApplication.getInstance().addActivity(this);
		backButton = (ImageView) findViewById(R.id.lib_button_back);
		textView = (TextView) findViewById(R.id.web_buyiyubi_title);
		web = (WebView) findViewById(R.id.webView);
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		web.loadUrl(this.getIntent().getStringExtra("url"));
		
		textView.setText(this.getIntent().getStringExtra("title"));
//		textView.setText("购买爱语币");
		WebSettings websettings = web.getSettings();
		websettings.setJavaScriptEnabled(true);
		websettings.setDomStorageEnabled(true);
		websettings.setBuiltInZoomControls(true);

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
					}
					else
					{
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
			// TODO Auto-generated method stub
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
