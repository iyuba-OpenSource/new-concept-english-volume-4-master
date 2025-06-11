//package com.iyuba.conceptEnglish.manager;
//
///**
// * 后台播放管理
// *
// * @author chentong
// * @version 1.0
// */
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.ServiceConnection;
//import android.os.IBinder;
//
//import com.iyuba.conceptEnglish.service.Background;
//import com.iyuba.configation.RuntimeManager;
//
//public class BackgroundManager {
//	private static BackgroundManager instance;
//	public static Context mContext;
//	public Background bindService;
//	public ServiceConnection conn;
//
//	private BackgroundManager() {
//		conn = new ServiceConnection() {
//			@Override
//			public void onServiceDisconnected(ComponentName name) {
//				// TODO Auto-generated method stub
//			}
//
//			@Override
//			public void onServiceConnected(ComponentName name, IBinder service) {
//				// TODO Auto-generated method stub
//				Background.MyBinder binder = (Background.MyBinder) service;
//				bindService = binder.getService();
//				bindService.init(mContext);
//			}
//		};
//	};
//
//	public static synchronized BackgroundManager Instace() {
//		mContext = RuntimeManager.getContext();
//		if (instance == null) {
//			instance = new BackgroundManager();
//		}
//		return instance;
//	}
//}
