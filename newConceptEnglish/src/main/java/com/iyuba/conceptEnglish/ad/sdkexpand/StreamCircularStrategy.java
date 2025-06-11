//package com.iyuba.conceptEnglish.ad.sdkexpand;
//
//import android.app.Activity;
//import android.content.Context;
//
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.iyuba.conceptEnglish.R;
//import com.iyuba.configation.Constant;
//import com.iyuba.headlinelibrary.data.DataManager;
//import com.iyuba.headlinelibrary.ui.common.ss.StreamNonVipStrategy;
//import com.iyuba.sdk.data.iyu.IyuNative;
//import com.iyuba.sdk.data.youdao.YDNative;
//import com.iyuba.sdk.nativeads.NativeAdPositioning;
//import com.iyuba.sdk.nativeads.NativeAdRenderer;
//import com.iyuba.sdk.nativeads.NativeRecyclerAdapter;
//import com.iyuba.sdk.nativeads.NativeViewBinder;
//import com.youdao.sdk.nativeads.RequestParameters;
//
//import java.util.EnumSet;
//
//public final class StreamCircularStrategy extends StreamNonVipStrategy {
//
//    private int mStart;
//    private int mInterval;
//    private int[] mStreamTypes;
//    private int mImageCorner;
//    private Activity activity;
//    private NativeAdRenderer mNativeAdRenderer;
//    private RecyclerView voaListView;
//
//    public StreamCircularStrategy(int start,
//                                  int interval,
//                                  int[] streamTypes,
//                                  int imageCorner,
//                                  int imagePadding,
//                                  Activity activity,
//                                  RecyclerView voaListView) {
//        mStart = start;
//        mInterval = interval;
//        mStreamTypes = streamTypes;
//        mImageCorner = imageCorner;
//        this.activity = activity;
//        this.voaListView = voaListView;
//    }
//
//
//    @Override
//    public RecyclerView.Adapter buildWorkAdapter(Context context, RecyclerView.Adapter originalAdapter) {
//
//        NativeAdPositioning.ClientPositioning cp = new NativeAdPositioning.ClientPositioning();
//        cp.addFixedPosition(mStart);
//        cp.enableRepeatingPositions(mInterval);
//        NativeRecyclerAdapter nativeAdapter = new NativeRecyclerAdapter(context, originalAdapter, cp);
//
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
//        YDNative ydNative = new YDNative(context, Constant.YOUDAO_STREAM_ID, requestParameters);
//
////    IyuNative iyuNative = new IyuNative(context, IHeadlineManager.appId, DataManager.getInstance().okHttpClient());
//        IyuNative iyuNative = new IyuNative(context, Constant.APPID, DataManager.getInstance().okHttpClient());
//
//        // 2021.08.16 测试用
////        int width = 480;
////        int hight = 320;
//
//        /*float width = 66.0f;//单位 dp ，扩大两倍
//        float hight = 43.0f;//单位 dp ，扩大两倍
//
//        JadNativeSlot jadSlot = new JadNativeSlot.Builder()
//                .setPlacementId(IAdManager.jdStreamCodeId1)
//                .setImageSize(width, hight)
//                .build();
//        JDNativeIyuba jdNativeIyuba = new JDNativeIyuba(context, activity, jadSlot);*/
//        SummaryNative summaryNative = new SummaryNative(ydNative, iyuNative);
//        summaryNative.setStreamSource(mStreamTypes);
//
//        nativeAdapter.setAdSource(summaryNative);
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
//        if (mNativeAdRenderer != null) {
//            return mNativeAdRenderer;
//        }
//        /*NativeViewBinder viewBinder = new NativeViewBinder.Builder(R.layout.headline_youdao_ad_row_circular)
//                .titleId(com.iyuba.headlinelibrary.R.id.native_title)
//                .mainImageId(com.iyuba.headlinelibrary.R.id.native_main_image)
//                .isMainImageCircular(true)
//                .mainImageCorner(mImageCorner)
//                .build();*/
//        NativeViewBinder viewBinder = new NativeViewBinder.Builder(R.layout.headline_youdao_ad_row_circular)
//                .titleId(R.id.native_title)
//                .mainImageId(R.id.native_main_image)
//                .build();
//        mNativeAdRenderer = new NativeAdRenderer(viewBinder);
//        return mNativeAdRenderer;
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
