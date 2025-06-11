package com.iyuba.conceptEnglish.util;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import androidx.core.content.FileProvider;

/**
 * 文件打开失败
 * 
 * @author chentong
 * 
 */
public class FailOpera {
	private static FailOpera instance;
	private static Context mContext;

	private FailOpera() {
	}

	public static synchronized FailOpera Instace(Context context) {
		mContext = context;
		if (instance == null) {
			instance = new FailOpera();
		}
		return instance;
	}

	public void openFile(String filePath) {
		File apkFile = new File(filePath);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

			String authorities = mContext.getString(personal.iyuba.presonalhomelibrary.R.string.file_provider_name_personal);
			//android:authorities="${applicationId}"
//			String authorities=mContext.getPackageName();
			Uri uri = FileProvider.getUriForFile(mContext,authorities, apkFile);
			intent.setDataAndType(uri, "application/vnd.android.package-archive");
		} else {
			intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
		}

		mContext.startActivity(intent);
	}
}
