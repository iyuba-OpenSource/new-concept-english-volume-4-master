//package com.iyuba.concept2.ad.sdkexpand;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.widget.ImageView;
//
//import androidx.core.graphics.drawable.RoundedBitmapDrawable;
//import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
//
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.request.target.BitmapImageViewTarget;
//import com.iyuba.sdk.nativeads.INativeAd;
//import com.iyuba.sdk.nativeads.NativeEventListener;
//import com.iyuba.sdk.nativeads.NativeResponse;
//import com.iyuba.sdk.nativeads.NativeViewBinder;
//
//public class NativeResponseCircle extends NativeResponse {
//    private INativeAd mNativeAdCircle;
//    private Context mContextCircle;
//    public NativeResponseCircle(Context context, INativeAd nativeAd, NativeEventListener eventListener) {
//        super(context, nativeAd, eventListener);
//        this.mNativeAdCircle =nativeAd;
//        mContextCircle=context;
//    }
//
//    @Override
//    public void loadMainImage(ImageView imageView, NativeViewBinder viewBinder) {
//        imageView.setPadding(viewBinder.mainImagePadding, viewBinder.mainImagePadding, viewBinder.mainImagePadding, viewBinder.mainImagePadding);
//        Glide.with(imageView.getContext()).load(this.mNativeAdCircle.getMainImageUrl()).asBitmap().into(new BitmapImageViewTarget(imageView) {
//            protected void setResource(Bitmap resource) {
//                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(NativeResponseCircle.this.mContextCircle.getResources(), resource);
//                circularBitmapDrawable.setCircular(true);
//                imageView.setImageDrawable(circularBitmapDrawable);
//            }
//        });
//    }
//}
