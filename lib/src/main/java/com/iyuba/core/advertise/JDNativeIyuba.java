//package com.iyuba.core.advertise;
//
//import android.app.Activity;
//import android.content.Context;
//import android.view.View;
//
//import com.iyuba.sdk.nativeads.IAdSource;
//import com.iyuba.sdk.nativeads.NativeErrorCode;
//import com.iyuba.sdk.nativeads.NativeEventListener;
//import com.iyuba.sdk.nativeads.NativeMultiAdRenderer;
//import com.iyuba.sdk.nativeads.NativeNetworkListener;
//import com.iyuba.sdk.nativeads.NativeResponse;
//import com.jd.ad.sdk.JadNative;
//import com.jd.ad.sdk.core.an.JadNativeAd;
//import com.jd.ad.sdk.core.an.JadNativeAdCallback;
//import com.jd.ad.sdk.core.an.JadNativeAdInteractionListener;
//import com.jd.ad.sdk.model.JadNativeSlot;
//import com.jd.ad.sdk.model.error.JadError;
//import com.jd.ad.sdk.model.error.JadErrorBuilder;
//
//import timber.log.Timber;
//
///**
// * 苏州爱语吧科技有限公司
// */
//public class JDNativeIyuba implements IAdSource {
//    private Context mContext;
//    private Activity mActivity;
//    private JadNativeSlot mJadSlot;
//    private JadNativeAd mNativeAd;
//    private NativeNetworkListener mListener;
//
//    private static final NativeEventListener EMPTY_EVENT_LISTENER = new NativeEventListener() {
//        @Override
//        public void onNativeImpression(View view, com.iyuba.sdk.nativeads.NativeResponse nativeResponse) {
//        }
//
//        @Override
//        public void onNativeClick(View view, com.iyuba.sdk.nativeads.NativeResponse nativeResponse) {
//        }
//    };
//
//    private JadNativeAdCallback jadNativeAdCallback = new JadNativeAdCallback() {
//        @Override
//        public void nativeAdDidLoad(JadNativeAd jadNativeAd) {
//            if (jadNativeAd != null) {
//                int price = jadNativeAd.getJadExtra().getPrice();
//                Timber.d("%s FeedNativeAd Load price is %s", IyubaJDConstant.timerTag,price);
//
//                if (jadNativeAd.getDataList() != null
//                        && !jadNativeAd.getDataList().isEmpty()
//                        && jadNativeAd.getDataList().get(0) != null) {
//
//                    mNativeAd = jadNativeAd;
////          mNativeAd.registerNativeView(mActivity, mViewGroup, mViewList, null, listener);
//
//                    JDNativeAdIyuba jdNativeAdIyuba=new JDNativeAdIyuba(mActivity,mNativeAd, listener);
//                    NativeResponse response = new NativeResponse(mContext, jdNativeAdIyuba, EMPTY_EVENT_LISTENER);
//                    if (mListener != null) {
//                        mListener.onNativeLoad(response);
//                    }
//                } else {
//                    nativeAdDidFail(jadNativeAd, JadErrorBuilder.buildError(-1, "load ad is empty"));
//                }
//            }
//        }
//
//        @Override
//        public void nativeAdDidFail(JadNativeAd jadNativeAd, JadError jadError) {
//            Timber.d("%s FeedAd Load onAdLoadFailed %s", IyubaJDConstant.timerTag, jadError);
//            if (mListener != null) {
//                mListener.onNativeFail(NativeErrorCode.EMPTY_AD_RESPONSE);
//            }
//        }
//    };
//
//
//    JadNativeAdInteractionListener listener = new JadNativeAdInteractionListener() {
//        @Override
//        public void nativeAdDidClick(JadNativeAd jadNativeAd, View view) {
//
//        }
//
//        @Override
//        public void nativeAdDidClose(JadNativeAd jadNativeAd, View view) {
//
//        }
//
//        @Override
//        public void nativeAdBecomeVisible(JadNativeAd jadNativeAd) {
//
//        }
//    };
//
//    public JDNativeIyuba(Context context, Activity activity, JadNativeSlot jadSlot) {
//        mContext = context;
//        mJadSlot = jadSlot;
//        mActivity = activity;
//    }
//
//    @Override
//    public int getLastBrandRequest() {
//        return 0;
//    }
//
//    @Override
//    public void setStreamAd(boolean value) {
//
//    }
//
//    @Override
//    public void setMultiAdRenderer(NativeMultiAdRenderer multiAdRenderer) {
//
//    }
//
//    @Override
//    public void setNativeEventListener(NativeEventListener listener) {
//    }
//
//    @Override
//    public void setNativeNetworkListener(NativeNetworkListener listener) {
//        mListener = listener;
//    }
//
//    @Override
//    public void destroy() {
//        if (this.mNativeAd != null) {
//            this.mNativeAd.destroy();
//            this.mNativeAd = null;
//        }
//        mActivity = null;
//        mContext = null;
//    }
//
//    @Override
//    public void doLoadWork(Object... objects) {
//        JadNative.getInstance().loadFeedAd(mActivity, mJadSlot, jadNativeAdCallback);
//    }
//}
