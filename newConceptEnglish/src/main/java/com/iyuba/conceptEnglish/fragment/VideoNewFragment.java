package com.iyuba.conceptEnglish.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.ad.show.AdShowUtil;
import com.iyuba.config.AdTestKeyData;
import com.iyuba.configation.ConfigManager;
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
import com.iyuba.module.dl.BasicDLPart;
import com.iyuba.module.dl.DLItemEvent;
import com.iyuba.module.user.IyuUserManager;
import com.iyuba.module.user.User;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * 非懒加载的界面操作
 */

public class VideoNewFragment extends Fragment {
    private DropdownTitleFragmentNew videoFragment;
    private View root;

    private Handler handler = new Handler();

    public static VideoNewFragment getInstance(){
        VideoNewFragment fragment = new VideoNewFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = View.inflate(getActivity(),R.layout.layout_container,null);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initStudy();
    }

    public void initStudy() {
        setUser();
        //动态设置
        UserInfoManager.getInstance().initHeadlineVideoInfo();
        //设置广告
        IHeadline.setAdAppId(String.valueOf(AdShowUtil.NetParam.getAdId()));
        IHeadline.setStreamAdPosition(AdShowUtil.NetParam.SteamAd_startIndex,AdShowUtil.NetParam.SteamAd_intervalIndex);
        IHeadline.setYoudaoStreamId(AdTestKeyData.KeyData.TemplateAdKey.template_youdao);
        IHeadline.setYdsdkTemplateKey(AdTestKeyData.KeyData.TemplateAdKey.template_csj,AdTestKeyData.KeyData.TemplateAdKey.template_ylh,AdTestKeyData.KeyData.TemplateAdKey.template_ks,AdTestKeyData.KeyData.TemplateAdKey.template_baidu,AdTestKeyData.KeyData.TemplateAdKey.template_vlion);

        String[] types = null;
        //根据包名处理
        if (getActivity().getPackageName().equals("com.iyuba.learnNewEnglish")){
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
        videoFragment = DropdownTitleFragmentNew.newInstance(bundle);

        getChildFragmentManager().beginTransaction().add(R.id.container, videoFragment).show(videoFragment).commitAllowingStateLoss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BasicDLPart event) {
        jumpToCorrectDLActivityByCate(getActivity(), event);
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
