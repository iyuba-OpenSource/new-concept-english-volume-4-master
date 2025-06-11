package com.iyuba.conceptEnglish.listener;

public interface OnPlayStateChangedListener {
	public void playSuccess();
	public void setPlayTime(String currTime,String allTime);
	public void playFaild();
	public void playCompletion();
}
