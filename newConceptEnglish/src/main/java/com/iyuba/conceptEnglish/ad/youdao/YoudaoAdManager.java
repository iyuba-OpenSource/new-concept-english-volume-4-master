//package com.iyuba.conceptEnglish.ad.youdao;
//
//import android.annotation.TargetApi;
//import android.app.Activity;
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.os.Build;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//
//import androidx.core.util.Consumer;
//
//import com.iyuba.conceptEnglish.activity.WelcomeActivity;
//import com.iyuba.conceptEnglish.ad.IAdManager;
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
//import timber.log.Timber;
//
//public class YoudaoAdManager implements IAdManager {
//    private Consumer<String> onErrorListener;
//
//    public void setOnErrorListener(Consumer<String> onErrorListener) {
//        this.onErrorListener = onErrorListener;
//    }
//
//    @Override
//    public void loadSplashAd(ViewGroup viewGroup, ImageView imageView, Context context, Activity activity) {
//        WelcomeActivity welcomeActivity= (WelcomeActivity) activity;
//        YouDaoNative youdaoNative = new YouDaoNative(context, Constant.YOUDAO_WELCOME_ID, new YouDaoNative.YouDaoNativeNetworkListener() {
//            @Override
//            public void onNativeLoad(final NativeResponse nativeResponse) {
//                welcomeActivity.adLoadFinsh = true;
//                List<String> imageUrls = new ArrayList<>();
//                imageUrls.add(nativeResponse.getMainImageUrl());
//                imageView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        nativeResponse.handleClick(imageView);
//                    }
//                });
//                if (!activity.isFinishing()){
//                    ImageService.get(context, imageUrls, new ImageService.ImageServiceListener() {
//                        @TargetApi(Build.VERSION_CODES.KITKAT)
//                        @Override
//                        public void onSuccess(final Map<String, Bitmap> bitmaps) {
//                            if (nativeResponse.getMainImageUrl() != null) {
//                                Bitmap bitMap = bitmaps.get(nativeResponse.getMainImageUrl());
//                                if (bitMap != null) {
//                                    imageView.setImageBitmap(bitMap);
//                                    nativeResponse.recordImpression(imageView);
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onFail() {
//
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onNativeFail(NativeErrorCode nativeErrorCode) {
//                if (onErrorListener!=null){
//                    onErrorListener.accept(nativeErrorCode.toString());
//                }
//                welcomeActivity.adLoadFinsh = true;
//                Timber.e("onNativeFail %s", nativeErrorCode.toString());
//
//            }
//        });
//
//
//        RequestParameters requestParameters = new RequestParameters.RequestParametersBuilder().build();
//        youdaoNative.makeRequest(requestParameters);
//    }
//
//    @Override
//    public void onDestory() {
//
//    }
//}
