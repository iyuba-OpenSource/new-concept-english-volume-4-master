package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.ad.show.reward;

/**
 * 广告显示的模型-激励视频广告
 */
public class AdRewardViewBean {


    private AdRewardShowManager.OnAdRewardShowListener onAdRewardShowListener;

    public AdRewardViewBean(AdRewardShowManager.OnAdRewardShowListener onAdRewardShowListener) {
        this.onAdRewardShowListener = onAdRewardShowListener;
    }

    public AdRewardShowManager.OnAdRewardShowListener getOnAdRewardShowListener() {
        return onAdRewardShowListener;
    }
}
