//package com.iyuba.conceptEnglish.util;
//
//import android.content.Context;
//
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.iyuba.conceptEnglish.R;
//import com.iyuba.headlinelibrary.ui.common.ss.StreamNonVipStrategy;
//import com.iyuba.headlinelibrary.ui.title.HolderType;
//import com.iyuba.sdk.data.iyu.IyuNative;
//import com.iyuba.sdk.data.ydsdk.YDSDKTemplateNative;
//import com.iyuba.sdk.data.youdao.YDNative;
//import com.iyuba.sdk.mixnative.MixNative;
//import com.iyuba.sdk.nativeads.NativeAdPositioning;
//import com.iyuba.sdk.nativeads.NativeAdRenderer;
//import com.iyuba.sdk.nativeads.NativeRecyclerAdapter;
//import com.iyuba.sdk.nativeads.NativeViewBinder;
//import com.youdao.sdk.nativeads.RequestParameters;
//
//import java.util.EnumSet;
//import java.util.HashMap;
//
//
///**
// * 暂无用
// * @deprecated
// */
//public class ContentMixStrategy extends StreamNonVipStrategy {
//
//    private int mHolderType;
//    private int mStart;
//    private int mInterval;
//    private int[] mStreamTypes;
//
//    public ContentMixStrategy(int start, int interval, int[] streamTypes, int holderType) {
//        mStart = start;
//        mInterval = interval;
//        mStreamTypes = streamTypes;
//        mHolderType = holderType;
//    }
//
//    @Override
//    public RecyclerView.Adapter buildWorkAdapter(Context context, RecyclerView.Adapter originalAdapter) {
//        NativeAdPositioning.ClientPositioning cp = new NativeAdPositioning.ClientPositioning();
//        cp.addFixedPosition(mStart);
//        cp.enableRepeatingPositions(mInterval);
//        NativeRecyclerAdapter nativeAdapter = new NativeRecyclerAdapter(context, originalAdapter, cp);
//
//        EnumSet<RequestParameters.NativeAdAsset> desiredAssets = EnumSet.of(
//                RequestParameters.NativeAdAsset.TITLE,
//                RequestParameters.NativeAdAsset.TEXT,
//                RequestParameters.NativeAdAsset.ICON_IMAGE,
//                RequestParameters.NativeAdAsset.MAIN_IMAGE,
//                RequestParameters.NativeAdAsset.CALL_TO_ACTION_TEXT);
//        RequestParameters requestParameters = new RequestParameters.RequestParametersBuilder()
//                .location(null)
//                .keywords(null)
//                .desiredAssets(desiredAssets)
//                .build();
//        YDNative ydNative = new YDNative(context, "edbd2c39ce470cd72472c402cccfb586", requestParameters);
//
//
//        IyuNative iyuNative = new IyuNative(context, com.iyuba.configation.Constant.APPID);
//        HashMap<Integer, YDSDKTemplateNative> hashMap = new HashMap<>();
//
//        //更换为新的
//        /*MixNative mixNative = new MixNative(ydNative, iyuNative);
//        mixNative.setStreamSource(mStreamTypes);*/
//        MixNative mixNative = new MixNative(ydNative,iyuNative,hashMap);
//
//        nativeAdapter.setAdSource(mixNative);
//        nativeAdapter.setAdViewTypeMax(56);
//
//        return nativeAdapter;
//    }
//
//    @Override
//    public void init(RecyclerView recyclerView, RecyclerView.Adapter adapter) {
//        NativeRecyclerAdapter nativeAdapter = (NativeRecyclerAdapter) adapter;
//        NativeAdRenderer nativeAdRenderer = makeAdRenderer();
//        nativeAdapter.registerAdRenderer(nativeAdRenderer);
//
//        recyclerView.setAdapter(adapter);
//    }
//
//    private NativeAdRenderer makeAdRenderer() {
//        switch (mHolderType) {
//
////            case HolderType.LARGE: {
////                NativeViewBinder viewBinder = new NativeViewBinder.Builder(com.iyuba.headlinelibrary.R.layout.headline_youdao_ad_row_small)
////                        .titleId(com.iyuba.headlinelibrary.R.id.native_title)
////                        .iconImageId(com.iyuba.headlinelibrary.R.id.native_main_image)
////                        .mainImageId(com.iyuba.headlinelibrary.R.id.native_main_image)
////                        .build();
////                return new NativeAdRenderer(viewBinder);
////            }
//            case HolderType.SMALL:
//            default: {
//                NativeViewBinder viewBinder = new NativeViewBinder.Builder(R.layout.youdao_ad_row_small)
//                        .titleId(com.iyuba.headlinelibrary.R.id.native_title)
//                        .mainImageId(com.iyuba.headlinelibrary.R.id.native_main_image)
//                        .build();
//                return new NativeAdRenderer(viewBinder);
//            }
//        }
//    }
//
//    @Override
//    public int getOriginalAdapterPosition(RecyclerView.Adapter adapter, int position) {
//        return ((NativeRecyclerAdapter) adapter).getOriginalPosition(position);
//    }
//
//    @Override
//    public void loadAd(RecyclerView.Adapter adapter) {
//        ((NativeRecyclerAdapter) adapter).loadAds();
//    }
//
//    @Override
//    public void refreshAd(RecyclerView recyclerView, RecyclerView.Adapter adapter) {
//        ((NativeRecyclerAdapter) adapter).refreshAds();
//    }
//
//}
