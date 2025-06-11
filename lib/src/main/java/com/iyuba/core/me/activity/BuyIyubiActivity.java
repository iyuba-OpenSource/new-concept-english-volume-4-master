package com.iyuba.core.me.activity;

/**
 * 爱语币购买界面
 *
 * @author chentong
 * @version 1.0
 * @para 传入"url" 网址；"title"标题显示
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.core.common.activity.login.LoginUtil;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.me.pay.PayOrderActivity;
import com.iyuba.lib.R;

/**
 * 购买爱语币界面
 */
public class BuyIyubiActivity extends BasisActivity implements OnClickListener {
    private ImageView backButton;
    private TextView textView;
    private ImageView iv_buy1;
    private ImageView iv_buy2;
    private ImageView iv_buy3;
    private ImageView iv_buy4;
    private ImageView iv_buy5;

    private String orderInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_buy_iyubi);
        setProgressBarVisibility(true);
        CrashApplication.getInstance().addActivity(this);
        backButton = (ImageView) findViewById(R.id.lib_button_back);
        textView = (TextView) findViewById(R.id.web_buyiyubi_title);
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                onBackPressed();
            }
        });

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        textView.setText(title);

        initView();
    }

    private void initView() {
        iv_buy1 = (ImageView) findViewById(R.id.iv_buy1);
        iv_buy2 = (ImageView) findViewById(R.id.iv_buy2);
        iv_buy3 = (ImageView) findViewById(R.id.iv_buy3);
        iv_buy4 = (ImageView) findViewById(R.id.iv_buy4);
        iv_buy5 = (ImageView) findViewById(R.id.iv_buy5);

        iv_buy1.setOnClickListener(this);
        iv_buy2.setOnClickListener(this);
        iv_buy3.setOnClickListener(this);
        iv_buy4.setOnClickListener(this);
        iv_buy5.setOnClickListener(this);
    }

    //TODO 警告，价格已经修改
    @Override
    public void onClick(View view) {
        if (!UserInfoManager.getInstance().isLogin()) {
            LoginUtil.startToLogin(this);
        } else {
            String vipPrice = "0";
            int vipAmount = 0;

            if (view == iv_buy1) {
                vipPrice = "19.9";
                vipAmount = 210;
            } else if (view == iv_buy2) {
                vipPrice = "59.9";
                vipAmount = 650;
            } else if (view == iv_buy3) {
                vipPrice = "99.9";
                vipAmount = 1100;
            } else if (view == iv_buy4) {
                vipPrice = "599";
                vipAmount = 6600;
            } else if (view == iv_buy5) {
                vipPrice = "999";
                vipAmount = 12000;
            }

            String vipSubject = "爱语币";
            String vipBody = "花费"+vipPrice+"元购买"+vipAmount+"个"+vipSubject;
            String vipDesc = "购买"+vipAmount+"个"+vipSubject;

            Intent intent = PayOrderActivity.buildIntent(this,vipPrice,vipAmount,1,vipSubject,vipBody,vipDesc,PayOrderActivity.Order_iyubi);
            startActivity(intent);
        }
    }

}
