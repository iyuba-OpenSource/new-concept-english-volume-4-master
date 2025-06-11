package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.ad.show.interstitial;

/**
 * 广告显示的模型-插屏广告
 */
public class AdInterstitialViewBean {

    private AdInterstitialShowManager.OnAdInterstitialShowListener onAdInterstitialShowListener;//广告回调接口

    public AdInterstitialViewBean(AdInterstitialShowManager.OnAdInterstitialShowListener onAdInterstitialShowListener) {
        this.onAdInterstitialShowListener = onAdInterstitialShowListener;
    }

    public AdInterstitialShowManager.OnAdInterstitialShowListener getOnAdInterstitialShowListener() {
        return onAdInterstitialShowListener;
    }
}
