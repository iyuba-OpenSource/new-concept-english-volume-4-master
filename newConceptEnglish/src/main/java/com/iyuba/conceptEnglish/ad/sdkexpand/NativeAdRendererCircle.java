//package com.iyuba.concept2.ad.sdkexpand;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.iyuba.sdk.nativeads.IAdRenderer;
//import com.iyuba.sdk.nativeads.NativeResponse;
//import com.iyuba.sdk.nativeads.NativeViewBinder;
//import com.iyuba.sdk.nativeads.NativeViewHolder;
//
//import java.util.WeakHashMap;
//
//import timber.log.Timber;
//
//public class NativeAdRendererCircle implements IAdRenderer<NativeResponseCircle> {
//    private final NativeViewBinder mViewBinder;
//    private final WeakHashMap<View, NativeViewHolder> mViewHolderMap;
//
//    public NativeAdRendererCircle(NativeViewBinder viewBinder) {
//        mViewBinder = viewBinder;
//        mViewHolderMap = new WeakHashMap<>();
//    }
//
//    @Override
//    public View createAdView(Context context, ViewGroup parent) {
//        return LayoutInflater.from(context).inflate(mViewBinder.layoutId, parent, false);
//    }
//
//    @Override
//    public void renderAdView(View itemView, NativeResponseCircle response, int position) {
//        Timber.i("render ad view");
//        NativeViewHolder viewHolder = getOrCreateNativeViewHolder(itemView, mViewBinder);
//        if (viewHolder == null) {
//            Timber.i("Could not create NativeViewHolder.");
//        } else {
//            populateConvertViewSubViews(itemView, viewHolder, response, mViewBinder);
//            itemView.setVisibility(View.VISIBLE);
//        }
//    }
//
//    private NativeViewHolder getOrCreateNativeViewHolder(View itemView, NativeViewBinder viewBinder) {
//        NativeViewHolder holder = mViewHolderMap.get(itemView);
//        if (holder == null) {
//            holder = NativeViewHolder.build(itemView, viewBinder);
//            mViewHolderMap.put(itemView, holder);
//            return holder;
//        } else {
//            return holder;
//        }
//    }
//
//    private void populateConvertViewSubViews(View convertView, NativeViewHolder viewHolder, NativeResponse nativeResponse, NativeViewBinder viewBinder) {
//        viewHolder.setMainPart(nativeResponse,viewBinder);
//        viewHolder.setExtraPart(convertView, nativeResponse, viewBinder);
//    }
//}
