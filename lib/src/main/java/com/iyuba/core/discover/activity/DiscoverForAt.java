///*
// * 文件名
// * 包含类名列表
// * 版本信息，版本号
// * 创建日期
// * 版权声明
// */
//package com.iyuba.core.discover.activity;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.view.View.OnClickListener;
//
//import com.iyuba.configation.Constant;
//import com.iyuba.core.common.activity.Web;
//import com.iyuba.core.common.activity.login.LoginUtil;
//import com.iyuba.core.discover.activity.mob.SimpleMobClassList;
//import com.iyuba.core.lil.user.UserInfoManager;
//import com.iyuba.core.teacher.activity.FindTeacherActivity;
//import com.iyuba.core.teacher.activity.TeacherBaseInfo;
//import com.iyuba.lib.R;
//
///**
// * 类名  发现Activity 弃用
// *
// * @author 作者 <br/>
// *         实现的主要功能。 创建日期 修改者，修改日期，修改内容。
// */
//public class DiscoverForAt extends Activity {
//	private Context mContext;
//	private View headline,news, exam, mob, all, searchWord, findFriend, vibrate,
//			collectWord, saying, back, discover_search_teacher,
//			discover_search_certeacher, discover_search_circlefriend;
////	private View root;
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.discover);
//		mContext = this;
//		initWidget();
//	}
//
//	private void initWidget() {
//		// TODO Auto-generated method stub
//		back = findViewById(R.id.button_back);
//		back.setVisibility(View.GONE);
//		headline = findViewById(R.id.headline);
//		headline.setOnClickListener(ocl);
//		news = findViewById(R.id.news);
//		news.setOnClickListener(ocl);
//		exam = findViewById(R.id.exam);
//		exam.setOnClickListener(ocl);
//		mob = findViewById(R.id.mob);
//		mob.setOnClickListener(ocl);
//		if (Constant.APPID.equals("238")) {
//			mob.setVisibility(View.GONE);
//		}
//		all = findViewById(R.id.all);
//		all.setOnClickListener(ocl);
//		searchWord = findViewById(R.id.search_word);
//		searchWord.setOnClickListener(ocl);
//		saying = findViewById(R.id.saying);
//		saying.setOnClickListener(ocl);
//		collectWord = findViewById(R.id.collect_word);
//		collectWord.setOnClickListener(ocl);
//		findFriend = findViewById(R.id.discover_search_friend);
//		discover_search_certeacher =
//				findViewById(R.id.discover_search_certeacher);
//		discover_search_circlefriend =
//				findViewById(R.id.discover_search_circlefriend);
//		discover_search_teacher =
//				findViewById(R.id.discover_search_teacher);
//		discover_search_circlefriend.setOnClickListener(ocl);
//		discover_search_certeacher.setOnClickListener(ocl);
//		discover_search_teacher.setOnClickListener(ocl);
//		findFriend.setOnClickListener(ocl);
//		vibrate = findViewById(R.id.discover_vibrate);
//		vibrate.setOnClickListener(ocl);
//
//		if (UserInfoManager.getInstance().getOtherData().getIsteacher().equals("1")) {
//			discover_search_certeacher.setVisibility(View.VISIBLE);
//		} else {
//			discover_search_certeacher.setVisibility(View.VISIBLE);
//
//		}
//
//	}
//
//	private OnClickListener ocl = new OnClickListener() {
//
//		@Override
//		public void onClick(View v) {
//			// TODO Auto-generated method stub
//			Intent intent;
//			int id = v.getId();
//			if(id == R.id.headline){
////				intent = new Intent(mContext,HeadlineActivity.class);
////				startActivity(intent);
//			}else if (id == R.id.news) {
//				intent = new Intent(mContext, AppGround.class);
//				intent.putExtra("title", R.string.discover_news);
//				startActivity(intent);
//			} else if (id == R.id.exam) {
//				intent = new Intent(mContext, AppGround.class);
//				intent.putExtra("title", R.string.discover_exam);
//				startActivity(intent);
//			} else if (id == R.id.mob) {
//				intent = new Intent(mContext, SimpleMobClassList.class);
//				intent.putExtra("title", R.string.discover_mobclass);
//				startActivity(intent);
//			} else if (id == R.id.all) {
//				intent = new Intent();
//				intent.setClass(mContext, Web.class);
//				intent.putExtra("url", "http://app." + Constant.IYUBA_CN + "android");
//				intent.putExtra("title",
//						mContext.getString(R.string.discover_appall));
//				startActivity(intent);
//			} else if (id == R.id.search_word) {
//				intent = new Intent();
//				intent.setClass(mContext, SearchWord.class);
//				startActivity(intent);
//			} else if (id == R.id.saying) {
//				intent = new Intent();
//				intent.setClass(mContext, Saying.class);
//				startActivity(intent);
//			} else if (id == R.id.collect_word) {
//				intent = new Intent();
//				intent.setClass(mContext, WordCollection.class);
//				startActivity(intent);
//			} else if (id == R.id.discover_search_friend) {
//				if (UserInfoManager.getInstance().isLogin()) {
//					intent = new Intent();
//					intent.setClass(mContext, SearchFriend.class);
//					startActivity(intent);
//				} else {
//					LoginUtil.startToLogin(mContext);
//				}
//
//			} else if (id == R.id.discover_search_teacher) {
//				if (UserInfoManager.getInstance().isLogin()) {
//					intent = new Intent();
//					intent.setClass(mContext, FindTeacherActivity.class);
//					startActivity(intent);
//				} else {
//					intent = new Intent();
//					intent.setClass(mContext, FindTeacherActivity.class);
//					startActivity(intent);
//				}
//			}
//
//			else if (id == R.id.discover_search_certeacher) {
//				if (UserInfoManager.getInstance().isLogin()) {
//					intent = new Intent();
//					intent.setClass(mContext, TeacherBaseInfo.class);
//					startActivity(intent);
//				} else {
////					intent = new Intent();
////					intent.setClass(mContext, Login.class);
////					startActivity(intent);
//					LoginUtil.startToLogin(mContext);
//				}
//
//			} else if (id == R.id.discover_search_circlefriend) {
//				if (UserInfoManager.getInstance().isLogin()) {
//					intent = new Intent();
//					intent.setClass(mContext, FriendCircFreshListActivity.class);
//					startActivity(intent);
//				} else {
////					intent = new Intent();
////					intent.setClass(mContext, Login.class);
////					startActivity(intent);
//					LoginUtil.startToLogin(mContext);
//				}
//
//			}
//
//		}
//	};
//
//	@Override
//	public void onPause() {
//		super.onPause();
//		// MobclickAgent.onPause(mContext);
//
//	}
//
//	@Override
//	public void onResume() {
//		super.onResume();
//		// MobclickAgent.onResume(mContext);
//		if (UserInfoManager.getInstance().getOtherData().getIsteacher().equals("1")) {
//			discover_search_certeacher.setVisibility(View.VISIBLE);
//
//		} else {
//			discover_search_certeacher.setVisibility(View.VISIBLE);
//
//		}
//	}
//}
