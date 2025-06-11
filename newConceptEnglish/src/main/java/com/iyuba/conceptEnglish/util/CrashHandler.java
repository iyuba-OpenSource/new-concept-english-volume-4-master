package com.iyuba.conceptEnglish.util;

/**
 * 崩溃后的处理
 * 
 * @author chentong
 * @version 1.0
 * 
 */

import android.app.NotificationManager;
import android.content.Context;

import com.iyuba.configation.RuntimeManager;

import java.lang.Thread.UncaughtExceptionHandler;

public class CrashHandler implements UncaughtExceptionHandler {

	private static CrashHandler INSTANCE;
	/** 系统默认的UncaughtException处理类 */

	/** 保证只有一个CrashHandler实例 */
	private CrashHandler() {
	}

	/** 获取CrashHandler实例 ,单例模式 */
	public static CrashHandler getInstance() {
		if (INSTANCE == null)
			INSTANCE = new CrashHandler();
		return INSTANCE;
	}

	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		handleException(ex);
//		Intent intent=new Intent(getApplicationContext(), WelcomeActivity.class);
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		startActivity(intent);
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(0);
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑
	 * 
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false
	 */
	private boolean handleException(Throwable ex) {
		new Thread() {
			@Override
			public void run() {
				// Toast 显示需要出现在一个线程的消息队列中
				NotificationManager notiManager = (NotificationManager) RuntimeManager
						.getContext().getSystemService(
								Context.NOTIFICATION_SERVICE);
				notiManager.cancel(999);
			}
		}.start();
		return true;
	}
}
