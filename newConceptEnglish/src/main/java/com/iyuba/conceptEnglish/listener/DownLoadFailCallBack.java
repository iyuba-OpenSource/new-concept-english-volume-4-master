package com.iyuba.conceptEnglish.listener;

public interface DownLoadFailCallBack {
	public void downLoadSuccess(String localFilPath);

	public void downLoadFaild(String errorInfo);
}
