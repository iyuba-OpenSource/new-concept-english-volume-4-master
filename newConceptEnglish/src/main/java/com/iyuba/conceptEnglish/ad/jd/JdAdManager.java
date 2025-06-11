//package com.iyuba.concept2.ad.jd;
//
//import android.app.Activity;
//import android.content.Context;
//import android.util.Log;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import androidx.core.util.Consumer;
//
//import com.bumptech.glide.Glide;
//import com.iyuba.concept2.activity.WelcomeActivity;
//import com.iyuba.concept2.ad.IAdManager;
//import com.iyuba.concept2.util.ScreenUtils;
//import com.jd.ad.sdk.JadNative;
//import com.jd.ad.sdk.core.an.JadMaterialData;
//import com.jd.ad.sdk.core.an.JadNativeAd;
//import com.jd.ad.sdk.core.an.JadNativeAdCallback;
//import com.jd.ad.sdk.core.an.JadSplashNativeAdInteractionListener;
//import com.jd.ad.sdk.imp.JadListener;
//import com.jd.ad.sdk.imp.splash.JadSplash;
//import com.jd.ad.sdk.model.JadNativeSlot;
//import com.jd.ad.sdk.model.error.JadError;
//import com.jd.ad.sdk.work.JadPlacementParams;
//
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Locale;
//
//import timber.log.Timber;
//
//public class JdAdManager implements IAdManager {
//    private Consumer<String> onErrorListener;
//
//    public void setOnErrorListener(Consumer<String> onErrorListener) {
//        this.onErrorListener = onErrorListener;
//    }
//    /**
//     * 开屏广告实例
//     */
////
//
//    @Override
//    public void loadSplashAd(ViewGroup viewGroup, ImageView imageView, Context context, Activity activity) {
//        WelcomeActivity welcomeActivity = (WelcomeActivity) activity;
//        //准备广告位 宽高  单位 dp
//        int widthDp = (int) ScreenUtils.px2dp(context, ScreenUtils.getScreenWidth(context));
//
//        //100dp demo 布局文件中 底部 logo 图片高度,开发者根据自身实际情况设置
////        int heightDp = (int) (ScreenUtils.px2dp(context, ScreenUtils.getScreenHeight(context)) * 0.86);
//        int heightDp = (int) (widthDp / 0.68);
//
//        // 2021.08.16 测试用
////        widthDp = 800;
////        heightDp = 1200;
//
//        Timber.e("%s Splash AD widthDp %d", IAdManager.TAG, widthDp);
//        Timber.e("%s Splash AD heightDp %d", IAdManager.TAG, heightDp);
//
//        //Step1:创建自渲染广告参数，包括广告位id、图片宽高、是否支持 deeplink
//        /**
//         * 注意：
//         *  1、宽度必须为屏幕宽度，高度必须大于等于屏幕高度的50%。否则影响有效曝光
//         *  2、宽高单位必须为 dp
//         */
//        JadNativeSlot jadParams = new JadNativeSlot.Builder()
//                .setPlacementId(jdSplashCodeId)
//                .setImageSize(widthDp, heightDp)
//                .setSkipTime(5)
//                .build();
//        //Step2:加载自渲染相关广告数据，监听加载回调
//        JadNative.getInstance().loadSplashAd(activity, jadParams, new JadNativeAdCallback() {
//            @Override
//            public void nativeAdDidLoad(JadNativeAd nativeAd) {
//                welcomeActivity.adLoadFinsh = true;
//                if (activity.isFinishing()) {
//                    return;
//                }
//                // 获取竞价价格
//                if (nativeAd != null) {
//                    int price = nativeAd.getJadExtra().getPrice();
//                    Timber.d("%s SplashNativeAd Load price is %s", IAdManager.TAG, price);
//                }
//                JadMaterialData data = nativeAd.getDataList().get(0);
//
//                Timber.e("%s AdTitle %s", IAdManager.TAG, data.getAdTitle());
//                Timber.e("%s AdDescription %s", IAdManager.TAG, data.getAdDescription());
//                Timber.e("%s AdImages %s", IAdManager.TAG, data.getAdImages().get(0));
//                Timber.e("%s AdResource %s", IAdManager.TAG, data.getAdResource());
//                Timber.e("%s AdVideo %s", IAdManager.TAG, data.getAdVideo());
//
//                Glide.with(context)
//                        .load(data.getAdImages().get(0))
//                        .into(imageView);
//                if (nativeAd == null) {
//                    return;
//                }
//
//                List<View> listView = new ArrayList<>();
//                listView.add(viewGroup);
//                listView.add(imageView);
//                //Step4: 注册需要监听的视图，包括整体的广告View、点击视图列表、关闭视图列表
//                // 这里非常重要，不要在View的listener中做点击操作，否则影响计费
//                nativeAd.registerNativeView(activity, viewGroup, listView, null, new JadSplashNativeAdInteractionListener() {
//                    // 这里不再推荐使用View 实现 JadSkipInterface 接口的方式，JadSkipInterface在之后的版本中将不再对外开放
//                    // 关于倒计时视图修改推荐到这个回调中进行操作
//                    @Override
//                    public void nativeAdForSplashCountdown(JadNativeAd ad, int time) {
//                        Timber.d("%s SplashAd TimeChange %s", IAdManager.TAG, time);
//                    }
//
//                    @Override
//                    public void nativeAdDidClick(JadNativeAd nativeAd, View view) {
//                        Timber.d("%s SplashAd Clicked", IAdManager.TAG);
//                    }
//
//                    @Override
//                    public void nativeAdDidClose(JadNativeAd nativeAd, View view) {
//                        Timber.d("%s SplashAd Dismissed", IAdManager.TAG);
//                    }
//
//                    @Override
//                    public void nativeAdBecomeVisible(JadNativeAd nativeAd) {
//                        Timber.d("%s SplashAd Exposure Success", IAdManager.TAG);
//                    }
//                });
//            }
//
//            @Override
//            public void nativeAdDidFail(JadNativeAd nativeAd, JadError error) {
//                welcomeActivity.adLoadFinsh = true;
//                onErrorListener.accept(error.toString());
////                Toast.makeText(context,error.toString(),Toast.LENGTH_SHORT).show();
//                Timber.e("%s SplashAd Load Failed %s", IAdManager.TAG, error);
//            }
//        });
//    }
//
//    @Override
//    public void onDestory() {
//    }
//}
