package com.iyuba.conceptEnglish.listener;

import android.graphics.Bitmap;

/**
 * 下载回调
 * @author lijingwei
 *
 */
public interface DownLoadCallBack {
	public void downLoadSuccess(Bitmap image);
	public void downLoadFaild(String error);
}
