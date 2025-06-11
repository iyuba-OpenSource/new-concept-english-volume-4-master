package com.iyuba.conceptEnglish.ad;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.iyuba.conceptEnglish.BuildConfig;

public interface IAdManager {
    String TAG = "new concept AD";


    //jd飞雷神小学英语

    String jdAppid = "BuildConfig.jdAppid";
    String jdSplashCodeId = "BuildConfig.jdSplashCodeId";
    String jdStreamCodeId1 = "BuildConfig.jdStreamCodeId1";
//    String jdSplashAdvertisementId="9096";
//    String jdStreamAdvertisementId1="9097";

    //youdao

    void loadSplashAd(ViewGroup viewGroup, ImageView imageView, Context context, Activity activity);

    void onDestory();
}
