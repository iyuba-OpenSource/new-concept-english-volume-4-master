//package com.iyuba.conceptEnglish.util;
//
//import android.annotation.TargetApi;
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.os.Build;
//import android.util.Log;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.core.util.Consumer;
//
//import com.iyuba.configation.Constant;
//import com.youdao.sdk.nativeads.ImageService;
//import com.youdao.sdk.nativeads.NativeErrorCode;
//import com.youdao.sdk.nativeads.NativeResponse;
//import com.youdao.sdk.nativeads.RequestParameters;
//import com.youdao.sdk.nativeads.YouDaoNative;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
///**
// * 加载banner广告
// */
//public class AdBannerUtil {
//    private static final String TAG = "Ad : Banner : ";
//    private Context context;
//    private YouDaoNative youdaoNative;
//    private View adView;
//    private ImageView adImageView;
//    private Consumer<Integer> errorListener;
//
//    public void setErrorListener(Consumer<Integer> errorListener) {
//        this.errorListener = errorListener;
//    }
////    private AddamBanner addamBanner;
////    private ViewGroup adMiaozeParent;
////    private AdView miaozeBannerAdView;
//
////    private String lastAD;
////    private String TYPE_DAM = "addam";
////    private String TYPE_YOUDAO = "youdao";
////    private String TYPE_IYUBA = "web";
////    private String TYPE_MIAOZE = "ssp";
//    // iyuba广告一分钟加载一次
////    private int intervalTime = 60 * 1000;
//
//    // 广告自己切换的时间
////    private int adIntervalTime = 10;
//
////    private TextView close;
////    private boolean isIyubaAdTimerStarted = false;
////    private Handler iyubaAdHandler = new Handler();
//
//    public AdBannerUtil(Context context) {
//        this.context = context;
//        youdaoNative = new YouDaoNative(context, Constant.YOUDAO_BANNER_ID, youDaoAdListener);
//    }
//
//    public void setView(View view, ImageView imageView, TextView textView) {
//        this.adImageView = imageView;
//        this.adView = view;
////        this.close = textView;
//    }
//
//
////    public void setMiaozeView(ViewGroup viewGroup) {
////        this.adMiaozeParent = viewGroup;
////    }
//
//
//    public void loadYouDaoAD() {
//        if (context == null) {
//            return;
//        }
//        Log.e(TAG, "加载有道广告");
//        RequestParameters requestParameters = new RequestParameters.RequestParametersBuilder().build();
//        youdaoNative.makeRequest(requestParameters);
//    }
//
//    public void destroyAd(){
//        if (youdaoNative!=null){
//            youdaoNative.destroy();
//        }
//    }
//
//
//    private YouDaoNative.YouDaoNativeNetworkListener youDaoAdListener = new YouDaoNative.YouDaoNativeNetworkListener() {
//        @Override
//        public void onNativeLoad(final NativeResponse nativeResponse) {
//            Log.e(TAG, "有道广告加载成功");
//            if (context == null) {
//                return;
//            }
//
//            List<String> imageUrls = new ArrayList<>();
//            imageUrls.add(nativeResponse.getMainImageUrl());
//            adImageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    nativeResponse.handleClick(adImageView);
//
//                    if (onCallBackListener!=null){
//                        onCallBackListener.onClick();
//                    }
//                }
//            });
//            ImageService.get(context, imageUrls, new ImageService.ImageServiceListener() {
//                @TargetApi(Build.VERSION_CODES.KITKAT)
//                @Override
//                public void onSuccess(final Map<String, Bitmap> bitmaps) {
//                    if (onCallBackListener!=null){
//                        onCallBackListener.onSuccess();
//                    }
//
//                    if (nativeResponse.getMainImageUrl() != null) {
//                        Bitmap bitMap = bitmaps.get(nativeResponse.getMainImageUrl());
//                        if (bitMap != null) {
//                            adView.setVisibility(View.VISIBLE);
//                            adImageView.setImageBitmap(bitMap);
//                            adImageView.setVisibility(View.VISIBLE);
//                            nativeResponse.recordImpression(adImageView);
//                        }
//                    }
//                }
//
//                @Override
//                public void onFail() {
//                    if (onCallBackListener!=null){
//                        onCallBackListener.onFail();
//                    }
//
//                    adView.setVisibility(View.GONE);
//                }
//            });
//        }
//
//        @Override
//        public void onNativeFail(NativeErrorCode nativeErrorCode) {
//            if (onCallBackListener!=null){
//                onCallBackListener.onFail();
//            }
//            Log.e(TAG, "有道广告加载失败onNativeFail:  " + nativeErrorCode.name());
////            adView.setVisibility(View.GONE);
//            errorListener.accept(0);
//        }
//    };
//
//
//    //新的回调接口
//    public OnCallBackListener onCallBackListener;
//
//    public interface OnCallBackListener{
//        //成功
//        void onSuccess();
//        //失败
//        void onFail();
//        //点击操作
//        void onClick();
//    }
//
//    public void setOnCallBackListener(OnCallBackListener onCallBackListener) {
//        this.onCallBackListener = onCallBackListener;
//    }
//}
