//package com.iyuba.core.advertise;
//
//import android.app.Activity;
//import android.view.View;
//
//import com.iyuba.sdk.nativeads.INativeAd;
//import com.jd.ad.sdk.core.an.JadMaterialData;
//import com.jd.ad.sdk.core.an.JadNativeAd;
//import com.jd.ad.sdk.core.an.JadNativeAdInteractionListener;
//
//import java.lang.ref.WeakReference;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import timber.log.Timber;
//
///**
// * 苏州爱语吧科技有限公司
// */
//public class JDNativeAdIyuba implements INativeAd {
//    private JadMaterialData mData;
//    private WeakReference<Activity> weakReferenceA;
//    private JadNativeAd mNativeAd;
//    private JadNativeAdInteractionListener mListener;
//
//    public JDNativeAdIyuba(Activity activity, JadNativeAd jadNativeAd, JadNativeAdInteractionListener listener){
//        weakReferenceA=new WeakReference<>(activity);
//        mNativeAd=jadNativeAd;
//        mListener=listener;
//        mData = jadNativeAd.getDataList().get(0);
//    }
//
//    @Override
//    public String getMainImageUrl() {
//        Timber.d("%s trigger function getMainImageUrl", IyubaJDConstant.timerTag);
//        return mData.getAdImages().get(0);
//    }
//
//    @Override
//    public String getIconImageUrl() {
//        Timber.d("%s trigger function getIconImageUrl", IyubaJDConstant.timerTag);
//        return mData.getAdImages().get(0);
//    }
//
//    @Override
//    public String getClickDestinationUrl() {
//        Timber.d("%s trigger function getClickDestinationUrl", IyubaJDConstant.timerTag);
//        return mData.getAdResource();
//    }
//
//    @Override
//    public String getCallToAction() {
//        Timber.d("%s trigger function getCallToAction", IyubaJDConstant.timerTag);
//        return null;
//    }
//
//    @Override
//    public String getTitle() {
//        Timber.d("%s trigger function getTitle", IyubaJDConstant.timerTag);
//        return mData.getAdTitle();
//    }
//
//    @Override
//    public String getText() {
//        Timber.d("%s trigger function getText", IyubaJDConstant.timerTag);
//        return mData.getAdDescription();
//    }
//
//    @Override
//    public Double getStarRating() {
//        Timber.d("%s trigger function getStarRating", IyubaJDConstant.timerTag);
//        return null;
//    }
//
//    @Override
//    public String getRenderName() {
//        Timber.d("%s trigger function getRenderName", IyubaJDConstant.timerTag);
//        return null;
//    }
//
//    @Override
//    public Set<String> d() {
//        Timber.d("%s trigger function d", IyubaJDConstant.timerTag);
//        return null;
//    }
//
//    @Override
//    public Set<String> e() {
//        Timber.d("%s trigger function e", IyubaJDConstant.timerTag);
//        return null;
//    }
//
//    @Override
//    public Set<String> f() {
//        Timber.d("%s trigger function f", IyubaJDConstant.timerTag);
//        return null;
//    }
//
//    @Override
//    public int l() {
//        Timber.d("%s trigger function l", IyubaJDConstant.timerTag);
//        return 0;
//    }
//
//    @Override
//    public int m() {
//        Timber.d("%s trigger function m", IyubaJDConstant.timerTag);
//        return 0;
//    }
//
//    @Override
//    public boolean p() {
//        Timber.d("%s trigger function p", IyubaJDConstant.timerTag);
//        return false;
//    }
//
//    @Override
//    public boolean o() {
//        //进入view1
//        Timber.d("%s trigger function o", IyubaJDConstant.timerTag);
//        return false;
//    }
//
//    @Override
//    public Object getExtra(String key) {
//        Timber.d("%s trigger function getExtra", IyubaJDConstant.timerTag);
//        return null;
//    }
//
//    @Override
//    public Map<String, Object> getExtras() {
//        Timber.d("%s trigger function getExtras", IyubaJDConstant.timerTag);
//        return null;
//    }
//
//    @Override
//    public void a(View view) {
//        //进入view2
//        List<View> list=new ArrayList<>();
//        list.add(view);
//        mNativeAd.registerNativeView(weakReferenceA.get(), null, list, null, mListener);
//        Timber.d("%s trigger function a", IyubaJDConstant.timerTag);
//
//    }
//
//    @Override
//    public void q() {
//        Timber.d("%s trigger function q", IyubaJDConstant.timerTag);
//
//    }
//
//    @Override
//    public void b(View view) {
//        Timber.d("%s trigger function b", IyubaJDConstant.timerTag);
//
//    }
//
//    @Override
//    public void c(View view) {
//        //退出view
//        Timber.d("%s trigger function c", IyubaJDConstant.timerTag);
//
//    }
//
//    @Override
//    public void r() {
//        Timber.d("%s trigger function r", IyubaJDConstant.timerTag);
//
//    }
//}