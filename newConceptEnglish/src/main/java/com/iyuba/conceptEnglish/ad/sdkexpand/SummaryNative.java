package com.iyuba.conceptEnglish.ad.sdkexpand;

import com.iyuba.module.privacy.PrivacyInfoHelper;
import com.iyuba.sdk.data.iyu.IyuNative;
import com.iyuba.sdk.data.youdao.YDNative;
import com.iyuba.sdk.nativeads.IAdSource;
import com.iyuba.sdk.nativeads.NativeErrorCode;
import com.iyuba.sdk.nativeads.NativeEventListener;
import com.iyuba.sdk.nativeads.NativeMultiAdRenderer;
import com.iyuba.sdk.nativeads.NativeNetworkListener;
import com.iyuba.sdk.nativeads.NativeResponse;

public class SummaryNative implements IAdSource {

    private final PrivacyInfoHelper mPrivacyHelper;
    private NativeNetworkListener mListener;

    private int[] mStreamSource;

    private YDNative mYDNative;
    private IyuNative mIyuNative;
//    private JDNativeIyuba mJdNativeIyuba;

    private int mIndex;

    public SummaryNative(YDNative ydNative, IyuNative iyuNative) {
        mPrivacyHelper = PrivacyInfoHelper.getInstance();

        mYDNative = ydNative;
        mIyuNative = iyuNative;

        mYDNative.setNativeNetworkListener(mInnerListener);
        mIyuNative.setNativeNetworkListener(mInnerListener);

        mIndex = 0;

        mStreamSource = new int[]{SummaryStreamType.YOUDAO, SummaryStreamType.YOUDAO, SummaryStreamType.YOUDAO};
//        mStreamSource = new int[]{SummaryStreamType.JD, SummaryStreamType.JD, SummaryStreamType.JD};
    }

    /*public SummaryNative(YDNative ydNative, IyuNative iyuNative, JDNativeIyuba jdNativeIyuba) {
        mPrivacyHelper = PrivacyInfoHelper.getInstance();

        mYDNative = ydNative;
        mIyuNative = iyuNative;
        mJdNativeIyuba = jdNativeIyuba;

        mYDNative.setNativeNetworkListener(mInnerListener);
        mIyuNative.setNativeNetworkListener(mInnerListener);
        mJdNativeIyuba.setNativeNetworkListener(mInnerListener);

        mIndex = 0;

        mStreamSource = new int[]{SummaryStreamType.YOUDAO, SummaryStreamType.YOUDAO, SummaryStreamType.YOUDAO};
//        mStreamSource = new int[]{SummaryStreamType.JD, SummaryStreamType.JD, SummaryStreamType.JD};
    }*/

    public void setStreamSource(int[] source) {
        if (source.length >= 3) {
            mStreamSource = source;
        }
    }

    @Override
    public int getLastBrandRequest() {
        return 0;
    }

    @Override
    public void setStreamAd(boolean value) {

    }

    @Override
    public void setMultiAdRenderer(NativeMultiAdRenderer multiAdRenderer) {

    }

    @Override
    public void setNativeEventListener(NativeEventListener listener) {

    }

    @Override
    public void setNativeNetworkListener(NativeNetworkListener listener) {
        mListener = listener;
    }

    @Override
    public void destroy() {
        mIyuNative.destroy();
        mYDNative.destroy();
//        mJdNativeIyuba.destroy();
    }

    @Override
    public void doLoadWork(Object... objects) {

//    mJdNativeIyuba.doLoadWork();

        switch (mStreamSource[mIndex]) {
            case SummaryStreamType.IYUBA:
                mIyuNative.doLoadWork();
                break;
            case SummaryStreamType.YOUDAO:
                if (mPrivacyHelper.getApproved()) {
                    mYDNative.doLoadWork();
                } else {
                    mIyuNative.doLoadWork();
                }
                break;
            /*case SummaryStreamType.JD:
                if (AdvertisingKey.releasePackage.equals(ConstantNew.PACK_NAME)) {
                    mJdNativeIyuba.doLoadWork();
                } else {
                    if (mPrivacyHelper.getApproved()) {
                        mYDNative.doLoadWork();
                    } else {
                        mIyuNative.doLoadWork();
                    }
                }
                break;*/
            default:
                if (mPrivacyHelper.getApproved()) {
                    mYDNative.doLoadWork();
                } else {
                    mIyuNative.doLoadWork();
                }
                break;
        }

        mIndex = (mIndex + 1) % 3;
    }

    private NativeNetworkListener mInnerListener = new NativeNetworkListener() {
        @Override
        public void onNativeLoad(NativeResponse nativeResponse) {
            if (mListener != null) mListener.onNativeLoad(nativeResponse);
        }

        @Override
        public void onNativeFail(NativeErrorCode nativeErrorCode) {
            if (mListener != null) mListener.onNativeFail(nativeErrorCode);
        }
    };
}
