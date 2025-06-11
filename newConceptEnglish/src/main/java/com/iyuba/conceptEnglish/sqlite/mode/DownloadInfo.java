package com.iyuba.conceptEnglish.sqlite.mode;

import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;

public class DownloadInfo {
    public int voaId;
    public String url;
    public long downloadedBytes;
    //1: 正在下载  -2： 等待下载
    public int downloadedState;
    public long totalBytes;
    public int downloadPer;
    public String savePath;

    public DownloadInfo() {
        super();
    }

    public DownloadInfo(int voaId) {

        this.voaId = voaId;
        boolean isAmerican = ConfigManager.Instance().isAmercan();
        if (isAmerican) {
            this.url = (voaId / 1000) + "_" + (voaId % 1000) + Constant.append;
        } else {
            this.url = "british/" + voaId / 10000 + "/" + (voaId / 10000) + "_" + (voaId / 10 % 1000) + Constant.append;

        }
        this.downloadedBytes = 0;
        this.downloadedState = -2;
    }

}
