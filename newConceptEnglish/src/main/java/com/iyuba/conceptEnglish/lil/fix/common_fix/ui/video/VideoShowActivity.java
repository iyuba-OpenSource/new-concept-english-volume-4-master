package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.video;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.databinding.LayoutContainerBinding;
import com.iyuba.conceptEnglish.fragment.VideoFragment;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingActivity;
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
 * @title: 视频界面
 * @date: 2023/8/1 18:03
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class VideoShowActivity extends BaseViewBindingActivity<LayoutContainerBinding> {

    public static void start(Context context){
        Intent intent = new Intent();
        intent.setClass(context,VideoShowActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        showVideo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void showVideo(){
        setUser();
        //动态设置
        UserInfoManager.getInstance().initHeadlineVideoInfo();

        //视频
        String[] types = null;
        //根据包名处理
        if (getPackageName().equals("com.iyuba.learnNewEnglish")){
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

        Bundle bundle = DropdownTitleFragmentNew.buildArguments(10, HolderType.LARGE, types, true);
        DropdownTitleFragmentNew videoFragment = DropdownTitleFragmentNew.newInstance(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container,videoFragment).show(videoFragment).commitNowAllowingStateLoss();
    }


    /*************************回调数据**************************/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BasicDLPart event) {
        jumpToCorrectDLActivityByCate(this, event);
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
                startActivity(AudioContentActivityNew.getIntent2Me(this, Constant.APPID, dlPart.getCategoryName(), dlPart.getTitle(), dlPart.getTitleCn(), dlPart.getPic(), dlPart.getType(), dlPart.getId()));
                break;
            case "song":
                startActivity(AudioContentActivity.getIntent2Me(this, Constant.APPID, dlPart.getCategoryName(), dlPart.getTitle(), dlPart.getTitleCn(), dlPart.getPic(), dlPart.getType(), dlPart.getId()));
                break;
            case "voavideo":
            case "meiyu":
            case "ted":
            case "bbcwordvideo":
            case "topvideos":
            case "japanvideos":
                startActivity(VideoContentActivityNew.getIntent2Me(this, dlPart.getCategoryName(), dlPart.getTitle(), dlPart.getTitleCn(), dlPart.getPic(), dlPart.getType(), dlPart.getId(), ""));
                break;
        }

    }

    /*****************************辅助功能************************/
    private void setUser() {
        User user = new User();
        user.vipStatus = UserInfoManager.getInstance().getVipStatus();
        user.name = UserInfoManager.getInstance().getUserName();
        user.uid = UserInfoManager.getInstance().getUserId();
        IyuUserManager.getInstance().setCurrentUser(user);
        if (user.uid == 0){
            IyuUserManager.getInstance().logout();
        }
    }
}
