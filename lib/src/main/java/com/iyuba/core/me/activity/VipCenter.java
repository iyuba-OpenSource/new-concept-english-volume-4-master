//package com.iyuba.core.me.activity;
//
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.iyuba.configation.ConfigManager;
//import com.iyuba.configation.Constant;
//import com.iyuba.core.common.activity.Web;
//import com.iyuba.core.common.activity.login.LoginUtil;
//import com.iyuba.core.common.base.BasisActivity;
//import com.iyuba.core.common.base.CrashApplication;
//import com.iyuba.core.common.manager.AccountManager;
//import com.iyuba.core.common.manager.SocialDataManager;
//import com.iyuba.core.common.thread.GitHubImageLoader;
//import com.iyuba.lib.R;
//
///**
// * 会员中心 显示VIP信息 特权
// *
// * @author ct
// * @version 1.0
// *
// * 当前界面被废弃
// */
//public class VipCenter extends BasisActivity {
//	private View relativeLayout_noLogin, relativetLayout_login;
//	private Button backBtn, loginBtn, buy_iyubi;
//	private ImageView photo;
//	private String username;
//	private Context mContext;
//	private int isvip;
//	private TextView name, state, deadline, account;
//	private Button buy;
//	private View ad, read, limit, speed;
//
//	protected void onCreate(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.vip_center);
//		CrashApplication.getInstance().addActivity(this);
//		mContext = this;
//		username = AccountManager.Instance(mContext).userName;
//		relativeLayout_noLogin = findViewById(R.id.relativeLayout_noLogin);
//		relativetLayout_login = findViewById(R.id.relativeLayout_Login);
//		loginBtn = (Button) findViewById(R.id.button_to_login);
//		loginBtn.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
////				Intent intent = new Intent();
////				intent.setClass(mContext, Login.class);
////				startActivity(intent);
//				LoginUtil.startToLogin(mContext);
//			}
//		});
//		backBtn = (Button) findViewById(R.id.button_back);
//		backBtn.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				finish();
//				Log.d("退出显示12", this.getClass().getName());
//			}
//		});
//		if (!AccountManager.Instance(mContext).checkUserLogin()) {
//			relativeLayout_noLogin.setVisibility(View.VISIBLE);
//			relativetLayout_login.setVisibility(View.GONE);
//		} else {
//			relativeLayout_noLogin.setVisibility(View.GONE);
//			relativetLayout_login.setVisibility(View.VISIBLE);
//			isvip = ConfigManager.Instance().loadInt("isvip");
//			init();
//		}
//	}
//
//	private void init() {
//		photo = (ImageView) findViewById(R.id.img);
//		name = (TextView) findViewById(R.id.buy_username);
//		state = (TextView) findViewById(R.id.buy_state);
//		deadline = (TextView) findViewById(R.id.buy_deadline);
//		buy = (Button) findViewById(R.id.button_buy);
//		account = (TextView) findViewById(R.id.buy_account);
//		ad = findViewById(R.id.image_ad);
//		read = findViewById(R.id.image_repeat);
//		speed = findViewById(R.id.image_speed);
//		limit = findViewById(R.id.image_limit);
//		buy_iyubi = (Button) findViewById(R.id.buy_iyubi);
//		ad.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				showAlertAndCancel(
//						getResources().getString(R.string.person_ad),
//						getResources().getString(R.string.person_ad_content));
//			}
//		});
//		read.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				showAlertAndCancel(
//						getResources().getString(R.string.person_read),
//						getResources().getString(R.string.person_read_content));
//			}
//		});
//		limit.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				showAlertAndCancel(
//						getResources().getString(R.string.person_down),
//						getResources().getString(R.string.person_down_content));
//			}
//		});
//		speed.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				showAlertAndCancel(
//						getResources().getString(R.string.person_award),
//						getResources().getString(R.string.person_award_content));
//			}
//		});
//		photo.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent();
//				intent.setClass(mContext, PersonalHome.class);
//				SocialDataManager.Instance().userid = AccountManager
//						.Instance(mContext).userId;
//				startActivity(intent);
//			}
//		});
//		buy.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				Intent intent = new Intent();
//				intent.setClass(mContext, BuyVip.class);
//				startActivity(intent);
//			}
//		});
//		buy_iyubi.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				buyIyubi();
//			}
//		});
//	}
//
//	private void Assignment() {
//		name.setText(username);
//		account.setText(AccountManager.Instance(mContext).userInfo.iyubi);
//		GitHubImageLoader.Instace(mContext).setCirclePic(
//				AccountManager.Instance(mContext).userId, photo);
//		if (isvip == 1) {
//			state.setText("VIP");
//			deadline.setText(AccountManager.Instance(mContext).userInfo.deadline);
//		} else {
//			state.setText(R.string.person_common_user);
//			deadline.setText(R.string.person_not_vip);
//		}
//	}
//
//	private void showAlertAndCancel(String title, String msg) {
//		final AlertDialog alert = new AlertDialog.Builder(this).create();
//		alert.setTitle(title);
//		alert.setMessage(msg);
//		alert.setIcon(android.R.drawable.ic_dialog_info);
//		alert.setButton(AlertDialog.BUTTON_POSITIVE,
//				getResources().getString(R.string.alert_btn_ok),
//				new DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						alert.dismiss();
//					}
//				});
//		alert.show();
//	}
//
//	@Override
//	protected void onResume() {
//		// TODO Auto-generated method stub
//		super.onResume();
//		if (!AccountManager.Instance(mContext).checkUserLogin()) {// 未登录
//			relativeLayout_noLogin.setVisibility(View.VISIBLE);
//			relativetLayout_login.setVisibility(View.GONE);
//		} else {
//			relativeLayout_noLogin.setVisibility(View.GONE);
//			relativetLayout_login.setVisibility(View.VISIBLE);
//			isvip = ConfigManager.Instance().loadInt("isvip");
//			init();
//			Assignment();
//		}
//	}
//
//	private void buyIyubi() {
//		Intent intent = new Intent();
//		intent.setClass(mContext, Web.class);
//		intent.putExtra("url", "http://app." + Constant.IYUBA_CN + "wap/index.jsp?uid="
//				+ AccountManager.Instance(mContext).userId + "&appid="
//				+ Constant.APPID);
//		intent.putExtra("title", Constant.APPName);
//		startActivity(intent);
//	}
//}
