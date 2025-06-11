package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.download;

public interface DownloadFixCallback {
    void onDownloadState(String downloadState,long progress,long total);
}
