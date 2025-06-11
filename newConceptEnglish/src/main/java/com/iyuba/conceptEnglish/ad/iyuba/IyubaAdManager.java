//package com.iyuba.conceptEnglish.ad.iyuba;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//
//import com.bumptech.glide.Glide;
//import com.iyuba.conceptEnglish.activity.Web;
//import com.iyuba.conceptEnglish.activity.WelcomeActivity;
//import com.iyuba.conceptEnglish.ad.IAdManager;
//
//public class IyubaAdManager implements IAdManager {
//    private String mSplashImageUrl;
//    private String mSplashTurnedUrl;
//
//
//    public IyubaAdManager(String splashImageUrl, String splashTurnedUrl) {
//        mSplashImageUrl = splashImageUrl;
//        mSplashTurnedUrl = splashTurnedUrl;
//    }
//
//    @Override
//    public void loadSplashAd(ViewGroup viewGroup, ImageView imageView, Context context, Activity activity) {
//        if (activity.isFinishing()){
//            return;
//        }
//        WelcomeActivity welcomeActivity= (WelcomeActivity) activity;
//        welcomeActivity.adLoadFinsh = true;
//        Glide.with(context).load(mSplashImageUrl).into(imageView);
////        boolean xiaomiLi= AdvertisingKey.xiaomiPackage.equals(context.getPackageName())&& Constant.COMPANY_NAME.equals(Constant.AIYUBA);
////        imageView.setEnabled(!xiaomiLi);
////        imageView.setOnClickListener(v -> {
////            if ("com.iyuba.englishfm".equals(context.getPackageName())&& Constant.COMPANY_NAME.equals(Constant.AIYUBA)){
////                Snackbar.make(imageView,"将要跳转到主页",Snackbar.LENGTH_INDEFINITE).setAction("确定", new View.OnClickListener() {
////                    @Override
////                    public void onClick(View view) {
////                        startWeb(context);
////                    }
////                }).show();
////            }else {
////                startWeb(context);
////            }
////        });
//    }
//    private void startWeb(Context context){
//        context.startActivity(new Intent(context, Web.class).putExtra("url", mSplashTurnedUrl));
//    }
//
//    @Override
//    public void onDestory() {
//
//    }
//
//}
