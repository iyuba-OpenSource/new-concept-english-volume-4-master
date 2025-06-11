package com.iyuba.conceptEnglish.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.ad.show.AdShowUtil;
import com.iyuba.conceptEnglish.util.ScreenUtils;
import com.iyuba.config.AdTestKeyData;
import com.iyuba.configation.Constant;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.headlinelibrary.HeadlineType;
import com.iyuba.headlinelibrary.IHeadline;
import com.iyuba.headlinelibrary.IHeadlineManager;
import com.iyuba.headlinelibrary.ui.content.AudioContentActivity;
import com.iyuba.headlinelibrary.ui.content.AudioContentActivityNew;
import com.iyuba.headlinelibrary.ui.content.VideoContentActivityNew;
import com.iyuba.headlinelibrary.ui.title.DropdownTitleFragmentNew;
import com.iyuba.headlinelibrary.ui.title.HolderType;
import com.iyuba.imooclib.ui.mobclass.MobClassFragment;
import com.iyuba.module.dl.BasicDLPart;
import com.iyuba.module.dl.DLItemEvent;
import com.iyuba.module.user.IyuUserManager;
import com.iyuba.module.user.User;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * Created by iyuba on 2017/7/27.
 */

public class VideoFragment extends Fragment {
    private Context mContext;

    private DropdownTitleFragmentNew mExtraFragment;

    private WindowManager mWindowManager;

    private MobClassFragment mobClassFragment;

    private View root;


    /**
     * 标记已加载完成，保证懒加载只能加载一次
     */
    private boolean hasLoaded = false;
    /**
     * 标记Fragment是否已经onCreate
     */
    private boolean isCreated = false;
    /**
     * 界面对于用户是否可见
     */
    private boolean isVisibleToUser = false;


    private Handler handler = new Handler();

    public static VideoFragment getInstance(){
        VideoFragment videoFragment = new VideoFragment();
        return videoFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVariables();
    }


    protected void initVariables() {
        //mContext = this;
        //CrashApplication.getInstance().addActivity(this);
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        //nightModeManager = new NightModeManager(mWindowManager, mContext);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_layout_video, null);

        /**
         * 设置view高度为statusbar的高度，并填充statusbar
         */
        View mStatusBar = root.findViewById(R.id.fillStatusBarView);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mStatusBar.getLayoutParams();
        lp.width = LinearLayout.LayoutParams.MATCH_PARENT;
        lp.height = ScreenUtils.getStatusHeight(mContext);
        mStatusBar.setLayoutParams(lp);

        lazyLoad();
        isCreated = true;
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        return root;
    }


    /**
     * 监听界面是否展示给用户，实现懒加载
     * 这个方法也是网上的一些方法用的最多的一个，我的思路也是这个，不过把整体思路完善了一下
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        //注：关键步骤
        this.isVisibleToUser = isVisibleToUser;
        lazyLoad();
    }

    /**
     * 懒加载方法，获取数据什么的放到这边来使用，在切换到这个界面时才进行网络请求
     */
    private void lazyLoad() {

        //如果该界面不对用户显示、已经加载、fragment还没有创建，
        //三种情况任意一种，不获取数据
        if (!isVisibleToUser || hasLoaded || !isCreated) {
            return;
        }
        initViewVideo();
        //注：关键步骤，确保数据只加载一次
        hasLoaded = true;
    }


    public void initViewVideo() {
        setUser();
        //动态设置
        UserInfoManager.getInstance().initHeadlineVideoInfo();
        //设置广告
        IHeadline.setAdAppId(String.valueOf(AdShowUtil.NetParam.getAdId()));
        IHeadline.setStreamAdPosition(AdShowUtil.NetParam.SteamAd_startIndex,AdShowUtil.NetParam.SteamAd_intervalIndex);
        IHeadline.setYoudaoStreamId(AdTestKeyData.KeyData.TemplateAdKey.template_youdao);
        IHeadline.setYdsdkTemplateKey(AdTestKeyData.KeyData.TemplateAdKey.template_csj,AdTestKeyData.KeyData.TemplateAdKey.template_ylh,AdTestKeyData.KeyData.TemplateAdKey.template_ks,AdTestKeyData.KeyData.TemplateAdKey.template_baidu,AdTestKeyData.KeyData.TemplateAdKey.template_vlion);

        //视频
        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        String[] types = null;
        //根据包名处理
        if (getActivity().getPackageName().equals(Constant.package_learnNewEnglish)){
            types = new String[]{
                    HeadlineType.SMALLVIDEO
            };
        }else {
            types = new String[]{
                    HeadlineType.SMALLVIDEO,
                    HeadlineType.MEIYU,
                    HeadlineType.VOAVIDEO,
                    HeadlineType.TED,
                    HeadlineType.BBCWORDVIDEO,
                    HeadlineType.TOPVIDEOS,
                    HeadlineType.HEADLINE};
        }
        Bundle bundle = DropdownTitleFragmentNew.buildArguments(10, HolderType.LARGE, types, false);
        mExtraFragment = DropdownTitleFragmentNew.newInstance(bundle);
        //mExtraFragment.setSmallVideoFragment();

        transaction.replace(R.id.content_video, mExtraFragment);
        //transaction.show(mExtraFragment);
        transaction.commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BasicDLPart event) {
        jumpToCorrectDLActivityByCate(mContext, event);
    }

    public void jumpToCorrectDLActivityByCate(Context context, BasicDLPart basicHDsDLPart) {

        switch (basicHDsDLPart.getType()) {
            case "voa":
            case "csvoa":
            case "bbc":
                startActivity(AudioContentActivityNew.getIntent2Me(context, Constant.APPID, basicHDsDLPart.getCategoryName(), basicHDsDLPart.getTitle(), basicHDsDLPart.getPic(), basicHDsDLPart.getType(), basicHDsDLPart.getId(), basicHDsDLPart.getTitleCn()));
                break;
            case "song":
                startActivity(AudioContentActivity.getIntent2Me(context, Constant.APPID, basicHDsDLPart.getCategoryName(), basicHDsDLPart.getTitle(), basicHDsDLPart.getPic(), basicHDsDLPart.getType(), basicHDsDLPart.getId(), basicHDsDLPart.getTitleCn()));
                break;
            case "voavideo":
            case "meiyu":
            case "ted":
            case "bbcwordvideo":
            case "japanvideos":
            case "topvideos":
                startActivity(VideoContentActivityNew.getIntent2Me(context, basicHDsDLPart.getCategoryName(), basicHDsDLPart.getTitle(), basicHDsDLPart.getPic(), basicHDsDLPart.getType(), basicHDsDLPart.getId(), basicHDsDLPart.getTitleCn(), ""));
                break;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DLItemEvent dlEvent) {
        //视频下载后点击
        BasicDLPart dlPart = dlEvent.items.get(dlEvent.position);
        switch (dlPart.getType()) {
            case "voa":
            case "csvoa":
            case "bbc":
                startActivity(AudioContentActivityNew.getIntent2Me(getContext(), Constant.APPID, dlPart.getCategoryName(), dlPart.getTitle(), dlPart.getTitleCn(), dlPart.getPic(), dlPart.getType(), dlPart.getId()));
                break;
            case "song":
                startActivity(AudioContentActivity.getIntent2Me(getContext(), Constant.APPID, dlPart.getCategoryName(), dlPart.getTitle(), dlPart.getTitleCn(), dlPart.getPic(), dlPart.getType(), dlPart.getId()));
                break;
            case "voavideo":
            case "meiyu":
            case "ted":
            case "bbcwordvideo":
            case "topvideos":
            case "japanvideos":
                startActivity(VideoContentActivityNew.getIntent2Me(getContext(), dlPart.getCategoryName(), dlPart.getTitle(), dlPart.getTitleCn(), dlPart.getPic(), dlPart.getType(), dlPart.getId(), ""));
                break;
        }

    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void event(VipChangeEvent vipChangeEvent) {
//
//        setUser();
////        handler.post(() -> {
////            if (mExtraFragment instanceof ITitleRefresh) {
////                ((ITitleRefresh) mExtraFragment).refreshTitleContent();
////            }
////        });
//    }

    private void setUser() {
        User user = new User();
        user.vipStatus = UserInfoManager.getInstance().getVipStatus();
        user.name = UserInfoManager.getInstance().getUserName();
        user.uid = UserInfoManager.getInstance().getUserId();
        IyuUserManager.getInstance().setCurrentUser(user);
        if (user.name.isEmpty()){
            IyuUserManager.getInstance().logout();
        }
    }
}
