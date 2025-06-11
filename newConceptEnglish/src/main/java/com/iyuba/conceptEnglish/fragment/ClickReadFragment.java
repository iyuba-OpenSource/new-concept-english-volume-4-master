package com.iyuba.conceptEnglish.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.api.ApiRetrofit;
import com.iyuba.conceptEnglish.api.GetDataForReadClick;
import com.iyuba.conceptEnglish.lil.concept_other.download.FilePathUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.PermissionFixUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.rxjava2.RxTimer;
import com.iyuba.conceptEnglish.manager.VoaDataManager;
import com.iyuba.conceptEnglish.widget.ClickReadImageView;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.util.LibGlide3Util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ClickReadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClickReadFragment extends Fragment {

    private static String IMAGE_BASE_URL = "http://"+Constant.staticStr+ Constant.IYUBA_CN+"images/voa";

    private ViewPager mViewPager;
    private TextView mPageCount;
    private TextView mTranslation_cn;
    private static final String ARG_VOAID = "arg_voaid";

    private String mVoaid;

    private Context mContext;
    //课文播放器
//    public static IJKPlayer textPlayer;
    public static MediaPlayer textPlayer;

    private MyHandler myHandler;

    private ImageView noData;

    private TextView noData_txt;

    //播放定时器
    private static final String clickImageTimerTag = "clickImageTimerTag";

    public ClickReadFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param voaid Parameter 1.
     * @return A new instance of fragment ClickReadFragment.
     */
    public static ClickReadFragment newInstance(String voaid) {
        ClickReadFragment fragment = new ClickReadFragment();
        Bundle args = new Bundle();
        args.putString(ARG_VOAID, voaid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mVoaid = getArguments().getString(ARG_VOAID);
        }
        mContext = getContext();
        myHandler = new MyHandler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_click_read, container, false);
        mViewPager = view.findViewById(R.id.viewPager);
        mPageCount = view.findViewById(R.id.pageCount);
        mTranslation_cn = view.findViewById(R.id.translation_cn);
        mTranslation_cn.setMovementMethod(ScrollingMovementMethod.getInstance());
        noData = view.findViewById(R.id.noData);
        noData_txt = view.findViewById(R.id.noData_txt);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initPlay();

        requestDataFromNet();
    }

    //音频的错误信息显示
    private String showAudioMsg = "初始化音频失败";
    //初始化音频
    private void initPlay(){
        try {
            textPlayer = new MediaPlayer();
            textPlayer.setOnErrorListener((mp, what, extra) -> {
                showAudioMsg = "音频加载错误("+what+")";
                return false;
            });

            String localPath = getLocalSoundPath();
            if (TextUtils.isEmpty(localPath)){
                textPlayer.setDataSource(getRemoteSoundPath());
            }else {
                textPlayer.setDataSource(localPath);
            }
            textPlayer.prepareAsync();
        }catch (Exception e){
            ToastUtil.showToast(getActivity(),showAudioMsg);
        }
    }

    //获取当前章节的音频网络路径
    private String getRemoteSoundPath(){
        String soundUrl = null;
        //这里针对会员和非会员不要修改，测试也不要修改
        if (UserInfoManager.getInstance().isVip()){
            soundUrl="http://staticvip2." + Constant.IYUBA_CN + "newconcept/";
        }else {
            soundUrl=Constant.sound;
        }

//        switch (ConfigManager.Instance().getBookType()) {
        switch (VoaDataManager.getInstance().voaTemp.lessonType) {
            case TypeLibrary.BookType.conceptFourUS:
            default:
                //美音
                soundUrl = soundUrl
                        + VoaDataManager.getInstance().voaTemp.voaId / 1000
                        + "_"
                        + VoaDataManager.getInstance().voaTemp.voaId % 1000
                        + Constant.append;
                break;
            case TypeLibrary.BookType.conceptFourUK: //英音
                soundUrl = soundUrl
                        + "british/"
                        + VoaDataManager.getInstance().voaTemp.voaId / 1000
                        + "/"
                        + VoaDataManager.getInstance().voaTemp.voaId / 1000
                        + "_"
                        + VoaDataManager.getInstance().voaTemp.voaId % 1000
                        + Constant.append;
                break;
            case TypeLibrary.BookType.conceptJunior:
                soundUrl = "http://"+Constant.staticStr+Constant.IYUBA_CN+"sounds/voa/sentence/202005/"
                        + VoaDataManager.getInstance().voaTemp.voaId
                        + "/"
                        + VoaDataManager.getInstance().voaTemp.voaId
                        + Constant.append;
                break;
        }

        return soundUrl;
    }

    //获取当前章节的音频本地路径
    private String getLocalSoundPath() {
        String localPath = "";

        // TODO: 2025/4/17 针对android 15
        if (!PermissionFixUtil.isCanUseExternalPermission(getActivity())) {
            return localPath;
        }

        //这里不获取当前的数据，而是获取数据中的类型
        /*switch (VoaDataManager.getInstance().voaTemp.lessonType) {
            case TypeLibrary.BookType.conceptFourUS:
            case TypeLibrary.BookType.conceptJunior:
            default:
                // 美音原文音频的存放路径
                String pathString = Constant.videoAddr + VoaDataManager.getInstance().voaTemp.voaId + Constant.append;
                File fileTemp = new File(pathString);
                if (fileTemp.exists()) {
                    localPath =  pathString;
                }
                break;
            case TypeLibrary.BookType.conceptFourUK:
                // 英音原文音频的存放路径
                String pathStringEng = Constant.videoAddr + VoaDataManager.getInstance().voaTemp.voaId + "_B" + Constant.append;
                File fileTempEng = new File(pathStringEng);
                if (fileTempEng.exists()) {
                    localPath = pathStringEng;
                }
                break;
        }*/

        //更换路径获取方式
        String pathString = FilePathUtil.getHomeAudioPath(VoaDataManager.getInstance().voaTemp.voaId,VoaDataManager.getInstance().voaTemp.lessonType);
        File file = new File(pathString);
        if (file.exists()){
            localPath = pathString;
        }

        return localPath;
    }

    private void requestDataFromNet() {
        ApiRetrofit.getInstance().getGetDataForReadClick()
                .getData(GetDataForReadClick.url, GetDataForReadClick.FORMAT, mVoaid)
                .enqueue(new Callback<GetDataForReadClick.ClickReadResponse>() {
                    @Override
                    public void onResponse(Call<GetDataForReadClick.ClickReadResponse> call, Response<GetDataForReadClick.ClickReadResponse> response) {
                        if (response.body() != null) {
                            initViewPager(response.body());
                        } else {
                            showDataOrImageView(false);
                        }
                    }

                    @Override
                    public void onFailure(Call<GetDataForReadClick.ClickReadResponse> call, Throwable t) {
                        ToastUtil.showToast(mContext, "网络异常，请检查网络状态");
                        showDataOrImageView(false);
                    }
                });
    }

    private void initViewPager(GetDataForReadClick.ClickReadResponse clickReadResponse) {
        String[] imgArray = clickReadResponse.Images.split(",");
        if (imgArray ==null || imgArray.length < 1){
            showDataOrImageView(false);
            return;
        }
        showDataOrImageView(true);
        pageChange(1, imgArray.length);
        List<ImageView> list = new ArrayList<>();
        for (String str : imgArray) {
            ClickReadImageView clickReadImageView = new ClickReadImageView(mContext, null, 0);
            /*Glide.with(mContext)
                    .load(IMAGE_BASE_URL + str)
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            //加载图片
                            clickReadImageView.setImageDrawable(resource);
                            //加载点击图片
                            for (GetDataForReadClick.VoatextDetail detail : clickReadResponse.voatext) {
                                if (str.equals(detail.ImgPath)) {
                                    clickReadImageView.addDetails(detail);
                                }
                            }
                        }
                    });*/
            //加载图片
            LibGlide3Util.loadImg(mContext,IMAGE_BASE_URL + str,0,clickReadImageView);
            //加载点击图片数据
            for (GetDataForReadClick.VoatextDetail detail : clickReadResponse.voatext) {
                if (str.equals(detail.ImgPath)) {
                    clickReadImageView.addDetails(detail);
                }
            }
            clickReadImageView.setAudioCallback(mAudioRealCallback);
            clickReadImageView.setScaleType(ImageView.ScaleType.FIT_XY);
            list.add(clickReadImageView);
        }
        viewPagerAdapter viewPagerAdapter = new viewPagerAdapter(mContext, list);
        mViewPager.setAdapter(viewPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                pageChange(position + 1, imgArray.length);
            }

            @Override
            public void onPageSelected(int position) {
                pageChange(position + 1, imgArray.length);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void showDataOrImageView(boolean showData){

        if (showData){
            mViewPager.setVisibility(View.VISIBLE);
            mPageCount.setVisibility(View.VISIBLE);
            mTranslation_cn.setVisibility(View.VISIBLE);
            noData.setVisibility(View.GONE);
            noData_txt.setVisibility(View.GONE);
        }else {
            mViewPager.setVisibility(View.GONE);
            mPageCount.setVisibility(View.GONE);
            mTranslation_cn.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
            noData_txt.setVisibility(View.VISIBLE);
        }
    }
    private void pageChange(int current, int total) {
        mPageCount.setText(current + "/" + total);
    }

    private final AudioRealCallback mAudioRealCallback = new AudioRealCallback();

    public class AudioRealCallback implements ClickReadImageView.AudioCallback {

        @Override
        public void playAudio(GetDataForReadClick.VoatextDetail detail) {
            if (textPlayer!=null&&textPlayer.isPlaying()) {
                RxTimer.getInstance().cancelTimer(clickImageTimerTag);
                myHandler.removeMessages(0);

                textPlayer.pause();
            }
            textPlayer.seekTo((int) (detail.Timing * 1000));
            textPlayer.start();

            Log.d("点读详情显示", detail.Timing+"---"+detail.EndTiming);

            long delayTime = (long) ((detail.EndTiming - detail.Timing) * 1000);
            RxTimer.getInstance().timerInMain(clickImageTimerTag, delayTime, new RxTimer.RxActionListener() {
                @Override
                public void onAction(long number) {
                    RxTimer.getInstance().cancelTimer(clickImageTimerTag);

                    if (textPlayer!=null&&textPlayer.isPlaying()){
                        textPlayer.pause();
                    }
                    Log.d("点读详情显示", "播放结束--"+delayTime);
                }
            });
            mTranslation_cn.setText(detail.sentence_cn);

//            myHandler.sendEmptyMessageDelayed(0, (long) ((detail.EndTiming - detail.Timing) * 1000));
        }
    }

    static class MyHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 0:
                    //暂停点读
                    if (textPlayer.isPlaying()) {
                        textPlayer.pause();
                    }
                    Log.d("点读详情显示", "结束");
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * viewPager的adapter类
     */
    static class viewPagerAdapter extends PagerAdapter {

        private List<ImageView> mList;
        private Context mContext;

        public viewPagerAdapter(Context context, List<ImageView> list) {
            mContext = context;
            mList = list;
        }

        @Override
        public int getCount() {
            return mList != null ? mList.size() : 0;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ImageView imageView = mList.get(position);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            ImageView imageView = (ImageView) object;
            container.removeView(imageView);
        }
    }

    //设置是否可以播放
    private boolean isCanPlay = true;
    public void setPlayState(boolean isPlay){
        this.isCanPlay = isPlay;

        if (!isPlay){
            if (textPlayer!=null&&textPlayer.isPlaying()){
                textPlayer.pause();
            }
            if (myHandler!=null){
                myHandler.removeMessages(0);
            }
        }
    }
}