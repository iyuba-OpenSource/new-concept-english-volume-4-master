package com.iyuba.conceptEnglish.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.iyuba.ConstantNew;
import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.adapter.RankFragmentAdapter;
import com.iyuba.conceptEnglish.databinding.MainBinding;
import com.iyuba.conceptEnglish.fragment.CourseFragment;
import com.iyuba.conceptEnglish.fragment.CourseNewFragment;
import com.iyuba.conceptEnglish.fragment.HomeFragment;
import com.iyuba.conceptEnglish.fragment.MeFragment;
import com.iyuba.conceptEnglish.fragment.PassFragment;
import com.iyuba.conceptEnglish.fragment.VideoFragment;
import com.iyuba.conceptEnglish.fragment.VideoNewFragment;
import com.iyuba.conceptEnglish.han.utils.AdvertisingKey;
import com.iyuba.conceptEnglish.lil.concept_other.MainBottomAdapter;
import com.iyuba.conceptEnglish.lil.concept_other.MainBottomBean;
import com.iyuba.conceptEnglish.lil.concept_other.download.FileDownloadManager;
import com.iyuba.conceptEnglish.lil.concept_other.me_wallet.WalletListActivity;
import com.iyuba.conceptEnglish.lil.concept_other.verify.AbilityControlManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.event.RefreshDataEvent;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.event.RefreshUserInfoEvent;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.StudySettingManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.ConceptDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.concept.LocalMarkEntity_conceptDownload;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.search.NewSearchActivity;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.PermissionFixUtil;
import com.iyuba.conceptEnglish.lil.fix.concept.ConceptFragment;
import com.iyuba.conceptEnglish.lil.fix.concept.bgService.ConceptBgPlayEvent;
import com.iyuba.conceptEnglish.lil.fix.concept.bgService.ConceptBgPlayManager;
import com.iyuba.conceptEnglish.lil.fix.concept.bgService.ConceptBgPlayService;
import com.iyuba.conceptEnglish.lil.fix.junior.JuniorFragment;
import com.iyuba.conceptEnglish.lil.fix.junior.bgService.JuniorBgPlayEvent;
import com.iyuba.conceptEnglish.lil.fix.junior.bgService.JuniorBgPlayManager;
import com.iyuba.conceptEnglish.lil.fix.junior.bgService.JuniorBgPlayService;
import com.iyuba.conceptEnglish.lil.fix.novel.NovelFragment;
import com.iyuba.conceptEnglish.lil.fix.novel.bgService.NovelBgPlayEvent;
import com.iyuba.conceptEnglish.lil.fix.novel.bgService.NovelBgPlayManager;
import com.iyuba.conceptEnglish.lil.fix.novel.bgService.NovelBgPlayService;
import com.iyuba.conceptEnglish.listener.AppUpdateCallBack;
import com.iyuba.conceptEnglish.manager.VersionManager;
import com.iyuba.conceptEnglish.manager.sharedpreferences.InfoHelper;
import com.iyuba.conceptEnglish.sqlite.ImportDatabase;
import com.iyuba.conceptEnglish.sqlite.mode.VoaSound;
import com.iyuba.conceptEnglish.sqlite.op.ArticleRecordOp;
import com.iyuba.conceptEnglish.sqlite.op.VoaSoundOp;
import com.iyuba.conceptEnglish.sqlite.op.WordPassOp;
import com.iyuba.conceptEnglish.util.AdTimeCheck;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.configation.RuntimeManager;
import com.iyuba.core.common.activity.WxLoginEvent;
import com.iyuba.core.common.activity.login.LoginType;
import com.iyuba.core.common.activity.login.TempDataManager;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.retrofitapi.UidResponse;
import com.iyuba.core.common.sqlite.ImportLibDatabase;
import com.iyuba.core.common.util.RxUtil;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.event.VipChangeEvent;
import com.iyuba.core.event.WordSearchEvent;
import com.iyuba.core.lil.DataHelpManager;
import com.iyuba.core.lil.WxLoginSession;
import com.iyuba.core.lil.base.StackUtil;
import com.iyuba.core.lil.event.ShowPageEvent;
import com.iyuba.core.lil.temp.DrawerEvent;
import com.iyuba.core.lil.temp.DrawerSession;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.user.listener.UserinfoCallbackListener;
import com.iyuba.core.lil.util.ResLibUtil;
import com.iyuba.core.lil.util.SPUtil;
import com.iyuba.core.me.activity.NewVipCenterActivity;
import com.iyuba.headlinelibrary.IHeadlineManager;
import com.iyuba.headlinelibrary.event.HeadlineGoVIPEvent;
import com.iyuba.headlinelibrary.ui.content.AudioContentActivity;
import com.iyuba.headlinelibrary.ui.content.VideoContentActivity;
import com.iyuba.imooclib.ImoocManager;
import com.iyuba.module.dl.BasicDLPart;
import com.iyuba.module.dl.DLItemEvent;
import com.iyuba.module.user.IyuUserManager;
import com.iyuba.module.user.User;
import com.iyuba.sdk.other.NetworkUtil;
import com.mob.secverify.PreVerifyCallback;
import com.mob.secverify.SecVerify;
import com.mob.secverify.common.exception.VerifyException;
import com.tencent.vasdolly.helper.ChannelReaderUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import personal.iyuba.personalhomelibrary.PersonalHome;
import personal.iyuba.personalhomelibrary.PersonalType;
import timber.log.Timber;


//主界面的类
//@RuntimePermissions
public class MainFragmentActivity extends BasisActivity implements AppUpdateCallBack {

    private Context mContext;
    private String version_code;
    private boolean isExit = false;// 是否点过退出

    private VoaSoundOp voaSoundOp;

    //界面布局适配器
    private RankFragmentAdapter fragmentAdapter;
    //界面数据
    private List<Fragment> fragmentList = new ArrayList<>();
    //底部数据
    private List<MainBottomBean> bottomList = new ArrayList<>();
    //底部操作
    private MainBottomAdapter bottomAdapter;
    //布局界面
    private MainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        Timber.d("TestTimberExist: this.requestWindowFeature(Window.FEATURE_NO_TITLE);");

        //设置界面
        binding = MainBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        ButterKnife.bind(this);
        mContext = this;
        EventBus.getDefault().register(this);

        RuntimeManager.setApplication(getApplication());
        RuntimeManager.setApplicationContext(getApplicationContext());
        RuntimeManager.setDisplayMetrics(this);
        Constant.videoAddr = ConfigManager.Instance().loadString("media_saving_path");
        ArticleRecordOp articleRecordOp = new ArticleRecordOp(mContext);
        if (!articleRecordOp.checkSoundUrlExist1()) {
            //学习记录表
            articleRecordOp.updateTable();
        }
        voaSoundOp = new VoaSoundOp(mContext);
        if (!voaSoundOp.checkSoundUrlExist1()) {
            //评测表里加字段
            voaSoundOp.updateTable();
        }


        //绑定新的服务
        bindConceptPlayService();
        //绑定定时器
        registerTimeTick();

        //bind view and data
        initView();

        //检查更新显示
        checkAppUpdate();

        InfoHelper.init(this);

        //这里不知道为啥，重复出现引导界面，直接设置下
        StudySettingManager.getInstance().setGuideVersion(ConstantNew.guide_version);
        //获取用户数据并设置到本地
        if (UserInfoManager.getInstance().isLogin()) {
            getUid(UserInfoManager.getInstance().getUserId());
        }else {
            //这里处理下，如果另一个缓存中可以查到uid，则使用新的uid进行处理
            SharedPreferences tempPreference = SPUtil.getPreferences(ResLibUtil.getInstance().getContext(), "config");
            String userIdStr = tempPreference.getString("userId", "0");
            if (!TextUtils.isEmpty(userIdStr)&&!userIdStr.equals("0")){
                int userId = Integer.parseInt(userIdStr);
                getUid(userId);

                //将原来的数据处理掉
                tempPreference.edit().clear().apply();
                return;
            }
        }

        //针对临时的包名处理
        if (com.iyuba.core.InfoHelper.getInstance().openShare()) {
            IHeadlineManager.enableShare = true;
            PersonalHome.setEnableShare(true);
            ImoocManager.enableShare = true;
        } else {
            IHeadlineManager.enableShare = false;
            PersonalHome.setEnableShare(false);
            ImoocManager.enableShare = false;
        }

        //预登录校验
        if (ConstantNew.loginType.equals(LoginType.loginByVerify)) {
            SecVerify.preVerify(new PreVerifyCallback() {
                @Override
                public void onComplete(Void unused) {
                    TempDataManager.getInstance().setMobVerify(true);
                }

                @Override
                public void onFailure(VerifyException e) {
                    TempDataManager.getInstance().setMobVerify(false);
                }
            });
        }
    }

    private void initViewPager() {
        HomeFragment homeFragmentNew = new HomeFragment();
        Fragment courseFragment = new CourseFragment();
        Fragment videoFragment = VideoFragment.getInstance();
        MeFragment meFragmentNew = new MeFragment();
        Bundle args = new Bundle();
        args.putBoolean("showLocal", true);
        meFragmentNew.setArguments(args);

        if (getPackageName().equals(Constant.package_conceptStory)){
            //新概念人工智能学英语(微课-接口控制、新概念-接口控制、小说-接口控制、视频-接口控制)
            fragmentList.clear();
            bottomList.clear();

            if (ConstantNew.isShowConcept && !AbilityControlManager.getInstance().isLimitConcept()) {
                fragmentList.add(ConceptFragment.getInstance());
                bottomList.add(new MainBottomBean(R.drawable.ic_main_home_unselected, R.drawable.ic_main_home_selected, "新概念"));
            }

            // TODO: 2024/8/7 根据展姐要求，tiktok版本需要增加小学英语内容
            String channel = ChannelReaderUtil.getChannel(this);
            if (channel.equals("tiktok")){
                fragmentList.add(JuniorFragment.getInstance());
                bottomList.add(new MainBottomBean(R.drawable.ic_main_junior_unselected, R.drawable.ic_main_junior_selected, "中小学"));
            }

            if (ConstantNew.isShowNovel && !AbilityControlManager.getInstance().isLimitNovel()) {
                fragmentList.add(NovelFragment.getInstance());
                bottomList.add(new MainBottomBean(R.drawable.ic_main_read_unselected, R.drawable.ic_main_read_selected, "小说"));
            }

            if (!AbilityControlManager.getInstance().isLimitMoc()) {
                //前两个如果被限制了，这里需要更换界面类型，取消懒加载
                if (AbilityControlManager.getInstance().isLimitConcept()
                        && AbilityControlManager.getInstance().isLimitNovel()) {
                    courseFragment = new CourseNewFragment();
                }
                fragmentList.add(courseFragment);
                bottomList.add(new MainBottomBean(R.drawable.ic_main_moc_unselected, R.drawable.ic_main_moc_selected, "课程"));
            }

            if (!AbilityControlManager.getInstance().isLimitVideo()) {
                //前三个如果被限制了，这里需要更换界面类型，取消懒加载
                if (AbilityControlManager.getInstance().isLimitConcept()
                        && AbilityControlManager.getInstance().isLimitNovel()
                        && AbilityControlManager.getInstance().isLimitMoc()) {
                    videoFragment = new VideoNewFragment();
                }
                fragmentList.add(videoFragment);
                bottomList.add(new MainBottomBean(R.drawable.ic_main_video_unselected, R.drawable.ic_main_video_selected, "抖语"));
            }

            fragmentList.add(meFragmentNew);
            bottomList.add(new MainBottomBean(R.drawable.ic_main_me_unselected, R.drawable.ic_main_me_selected, "个人"));
        } else if (getPackageName().equals(Constant.package_learnNewEnglish)) {
            //新概念英语微课(微课-更换位置显示+接口控制、新概念、中小学-接口控制、视频-接口控制、我的)
            fragmentList.clear();
            bottomList.clear();

            fragmentList.add(ConceptFragment.getInstance());
            bottomList.add(new MainBottomBean(R.drawable.ic_main_home_unselected, R.drawable.ic_main_home_selected, "新概念"));

            if (!AbilityControlManager.getInstance().isLimitMoc()) {
                courseFragment = new CourseFragment();
                fragmentList.add(courseFragment);
                bottomList.add(new MainBottomBean(R.drawable.ic_main_moc_unselected, R.drawable.ic_main_moc_selected, "课程"));
            }

            if (!AbilityControlManager.getInstance().isLimitNovel()) {
                fragmentList.add(NovelFragment.getInstance());
                bottomList.add(new MainBottomBean(R.drawable.ic_main_read_unselected, R.drawable.ic_main_read_selected, "小说"));
            }

            if (!AbilityControlManager.getInstance().isLimitJunior()) {
                fragmentList.add(JuniorFragment.getInstance());
                bottomList.add(new MainBottomBean(R.drawable.ic_main_junior_unselected, R.drawable.ic_main_junior_selected, "中小学"));
            }

            fragmentList.add(meFragmentNew);
            bottomList.add(new MainBottomBean(R.drawable.ic_main_me_unselected, R.drawable.ic_main_me_selected, "我的"));
        } else if (getPackageName().equals(Constant.package_nce)) {
            //nce版本-vivo上的
            fragmentList.clear();
            bottomList.clear();

            fragmentList.add(homeFragmentNew);
            fragmentList.add(new PassFragment());
            fragmentList.add(courseFragment);
            fragmentList.add(VideoFragment.getInstance());
            fragmentList.add(meFragmentNew);

            bottomList.add(new MainBottomBean(R.drawable.ic_main_home_unselected, R.drawable.ic_main_home_selected, "课本"));
            bottomList.add(new MainBottomBean(R.drawable.ic_main_word_unselected, R.drawable.ic_main_word_selected, "词汇"));
            bottomList.add(new MainBottomBean(R.drawable.ic_main_moc_unselected, R.drawable.ic_main_moc_selected, "课程"));
            bottomList.add(new MainBottomBean(R.drawable.ic_main_video_unselected, R.drawable.ic_main_video_selected, "视频"));
            bottomList.add(new MainBottomBean(R.drawable.ic_main_me_unselected, R.drawable.ic_main_me_selected, "个人"));
        } else if (getPackageName().equals(Constant.package_concept2)
                || getPackageName().equals(Constant.package_englishfm)) {
            //新概念英语全四册-大版本
            fragmentList.clear();
            bottomList.clear();

            //新概念显示
            if (ConstantNew.isShowConcept) {
                //课程
                fragmentList.add(homeFragmentNew);
                bottomList.add(new MainBottomBean(R.drawable.ic_main_home_unselected, R.drawable.ic_main_home_selected, getResources().getString(R.string.home_text)));

                //单词
                fragmentList.add(PassFragment.getInstance(false));
                bottomList.add(new MainBottomBean(R.drawable.ic_main_word_unselected, R.drawable.ic_main_word_selected, getResources().getString(R.string.word_title)));
            }

            //微课审核
            if (!AbilityControlManager.getInstance().isLimitMoc()) {
                fragmentList.add(courseFragment);
                bottomList.add(new MainBottomBean(R.drawable.ic_main_moc_unselected, R.drawable.ic_main_moc_selected, getResources().getString(R.string.mobile_class)));
            }

            //小说
            if (ConstantNew.isShowNovel && !AbilityControlManager.getInstance().isLimitNovel()) {
                fragmentList.add(NovelFragment.getInstance());
                bottomList.add(new MainBottomBean(R.drawable.ic_main_read_unselected, R.drawable.ic_main_read_selected, "阅读"));
            }

            //视频界面(这里关闭)
//            fragmentList.add(VideoNewFragment.getInstance());
//            bottomList.add(new MainBottomBean(R.drawable.main_talk,R.drawable.main_talk_on,getResources().getString(R.string.video)));

            //我的
            fragmentList.add(meFragmentNew);
            bottomList.add(new MainBottomBean(R.drawable.ic_main_me_unselected, R.drawable.ic_main_me_selected, getResources().getString(R.string.me_text)));
        } else {
            fragmentList.clear();
            bottomList.clear();

            fragmentList.add(homeFragmentNew);
            bottomList.add(new MainBottomBean(R.drawable.ic_main_home_unselected, R.drawable.ic_main_home_selected, getResources().getString(R.string.home_text)));

            fragmentList.add(new PassFragment());
            bottomList.add(new MainBottomBean(R.drawable.ic_main_word_unselected, R.drawable.ic_main_word_selected, getResources().getString(R.string.word_title)));

            //微课
            if (!AbilityControlManager.getInstance().isLimitMoc()) {
                fragmentList.add(new CourseFragment());
                bottomList.add(new MainBottomBean(R.drawable.ic_main_moc_unselected, R.drawable.ic_main_moc_selected, getResources().getString(R.string.mobile_class)));
            }

            //应用宝因为版权问题暂时关闭
            //视频
            if (!AbilityControlManager.getInstance().isLimitVideo()){
                fragmentList.add(VideoFragment.getInstance());
                bottomList.add(new MainBottomBean(R.drawable.ic_main_video_unselected, R.drawable.ic_main_video_selected, getResources().getString(R.string.video)));
            }

            fragmentList.add(meFragmentNew);
            bottomList.add(new MainBottomBean(R.drawable.ic_main_me_unselected, R.drawable.ic_main_me_selected, getResources().getString(R.string.me_text)));
        }


        FragmentManager fm = getSupportFragmentManager();

        fragmentAdapter = new RankFragmentAdapter(fm, fragmentList);
        binding.viewPager.setAdapter(fragmentAdapter);
        binding.viewPager.setOffscreenPageLimit(fragmentList.size());
        binding.viewPager.setCurrentItem(0, false);

        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                bottomAdapter.setIndex(position);

                //暂停播放-新概念
                EventBus.getDefault().post(new ConceptBgPlayEvent(ConceptBgPlayEvent.event_control_pause));
                EventBus.getDefault().post(new ConceptBgPlayEvent(ConceptBgPlayEvent.event_audio_pause));
                //暂停播放-中小学
                EventBus.getDefault().post(new JuniorBgPlayEvent(JuniorBgPlayEvent.event_control_pause));
                EventBus.getDefault().post(new JuniorBgPlayEvent(JuniorBgPlayEvent.event_audio_pause));
                //暂停播放-故事
                EventBus.getDefault().post(new NovelBgPlayEvent(NovelBgPlayEvent.event_control_pause));
                EventBus.getDefault().post(new NovelBgPlayEvent(NovelBgPlayEvent.event_audio_pause));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //底部处理(前任真是个人才啊，前人挖坑，后人骂娘)
        //有时间尽量做成recyclerview的样式，把数据动态设置，然后进行显示
        bottomAdapter = new MainBottomAdapter(this, bottomList);
        GridLayoutManager manager = new GridLayoutManager(this, bottomList.size());
        binding.bottomView.setLayoutManager(manager);
        binding.bottomView.setAdapter(bottomAdapter);
        bottomAdapter.setOnClickListener(new MainBottomAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {
                //切换
                if (position == binding.viewPager.getCurrentItem()) {
                    return;
                }

                //切换显示
                binding.viewPager.setCurrentItem(position);

                //暂停播放-新概念
                EventBus.getDefault().post(new ConceptBgPlayEvent(ConceptBgPlayEvent.event_control_pause));
                EventBus.getDefault().post(new ConceptBgPlayEvent(ConceptBgPlayEvent.event_audio_pause));
                //暂停播放-中小学
                EventBus.getDefault().post(new JuniorBgPlayEvent(JuniorBgPlayEvent.event_control_pause));
                EventBus.getDefault().post(new JuniorBgPlayEvent(JuniorBgPlayEvent.event_audio_pause));
                //暂停播放-故事
                EventBus.getDefault().post(new NovelBgPlayEvent(NovelBgPlayEvent.event_control_pause));
                EventBus.getDefault().post(new NovelBgPlayEvent(NovelBgPlayEvent.event_audio_pause));
            }
        });

        if (bottomList.size() <= 1) {
            binding.bottomView.setVisibility(View.GONE);
        }
    }

    public void initView() {
        initViewPager();

        binding.viewPager.setCurrentItem(0, false);
    }

    /**
     * 检查新版本
     */
    public void checkAppUpdate() {
        if (getPackageName().equals(AdvertisingKey.releasePackage)
                || getPackageName().equals(AdvertisingKey.xiaomiPackage)
                || getPackageName().equals(AdvertisingKey.smallClassPackage)
                || getPackageName().equals("com.iyuba.learnNewEnglish")) {
            VersionManager.Instace(this).checkNewVersion(VersionManager.version, ConstantNew.PACKAGE_TYPE, this);
            Timber.d("当前传递版本号：" + VersionManager.version);
        }
    }

    private void exit() {
//        saveFix();
        new Thread() {
            @Override
            public void run() {
                super.run();
//                FlurryAgent.onEndSession(mContext);
                Log.d("当前退出的界面1001", "run: ");
                StackUtil.getInstance().finishAll();
                ImportDatabase.mdbhelper.close();
                ImportLibDatabase.mdbhelper.close();
                CrashApplication.getInstance().exit();
            }
        }.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case Menu.FIRST + 1:
                exit();
                break;
        }
        return false;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    new AlertDialog.Builder(MainFragmentActivity.this)
                            .setTitle("版本更新")
                            .setMessage(getResources().getString(R.string.about_update_alert_1) + version_code + getResources().getString(R.string.about_update_alert_2))
                            .setPositiveButton("更新版本", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent();
                                    intent.setClass(mContext, AboutActivity.class);
                                    startActivity(intent);
                                }
                            }).setNegativeButton("暂不更新",null)
                            .setCancelable(false)
                            .create().show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        //解绑新的服务
        unbindConceptPlayService();
        //解绑接收器
        unregisterTimeTick();
        //设置定时播放
        ConfigManager.Instance().setAlarmItem(0);
        //关闭下载
        FileDownloadManager.getInstance().stopDownload();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        pressAgainExit();
    }

    private void pressAgainExit() {
        int isdowning = ConfigManager.Instance().loadInt("isdowning");
        if (isdowning <= 0) {
            if (isExit) {
                ConfigManager.Instance().putInt("isdowning", 0);
                exit();
                ((NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE)).cancel(222);
            } else {
                CustomToast.showToast(getApplicationContext(), R.string.alert_exit, 1000);
                doExitInOneSecond();
            }
        } else {
            Dialog dialog = new AlertDialog.Builder(mContext).setIcon(android.R.drawable.ic_dialog_alert).setTitle(R.string.alert_title).setMessage(R.string.alert_exit_content).setPositiveButton(R.string.alert_btn_exit, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    finish();
                    Log.d("当前退出的界面0010", getClass().getName());
                }
            }).setNeutralButton(R.string.alert_btn_cancel, (dialog1, whichButton) -> {
            }).create();
            dialog.show();// 如果要显示对话框，一定要加上这句
        }
    }

    private void doExitInOneSecond() {
        isExit = true;
        HandlerThread thread = new HandlerThread("doTask");
        thread.start();
        new Handler(thread.getLooper()).postDelayed(task, 1500);// 1.5秒内再点有效
    }


    private Runnable task = new Runnable() {
        @Override
        public void run() {
            isExit = false;
        }
    };

    private void getUid(int userId) {
        //先初始化数据
        if (UserInfoManager.getInstance().isLogin()){
            UserInfoManager.getInstance().initUserInfo();
            //之后获取接口数据
            UserInfoManager.getInstance().getRemoteUserInfo(userId, null);
        }else {
            UserInfoManager.getInstance().clearUserInfo();
        }
    }

    public static long getTimeDate() {
        Date date = new Date();
        long unixTimestamp = date.getTime() / 1000 + 3600 * 8; //东八区;
        long days = unixTimestamp / 86400;
        return days;
    }

    @Override
    public void appUpdateSave(String version_code, String newAppNetworkUrl) {
        this.version_code = version_code;
        handler.sendEmptyMessage(0);
    }

    @Override
    public void appUpdateFaild() {
    }

    @Override
    public void appUpdateBegin(String newAppNetworkUrl) {

    }


    /*@SuppressLint("NeedOnRequestPermissionsResult")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainFragmentActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }*/

    /*@NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION})
    public void initLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            MainFragmentActivityPermissionsDispatcher.getUidRequsetWithPermissionCheck(MainFragmentActivity.this);
        }
    }*/

    /*@OnPermissionDenied({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION})
    public void locationDenied() {
        CustomToast.showToast(MainFragmentActivity.this, "存储和定位权限开通才可以正常使用app，请到系统设置中开启", 3000);
    }*/

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(WordSearchEvent wordSearchEvent) {
        NewSearchActivity.start(this,wordSearchEvent.word);
    }


    @NeedsPermission(Manifest.permission.READ_PHONE_STATE)
    public void getUidRequset() {
        if (UserInfoManager.getInstance().isLogin()) {
            getUid(UserInfoManager.getInstance().getUserId());
        }
    }

    @OnPermissionDenied(Manifest.permission.READ_PHONE_STATE)
    public void getUidDenied() {

    }

    @SuppressLint("MissingPermission")
    public static String newPhoneDiviceId() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Build.getSerial();
        } else {
            return Build.SERIAL;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(VipChangeEvent vipChangeEvent) {
        if (!ConfigManager.Instance().isWordPassLoad() && UserInfoManager.getInstance().isLogin()) {
            WordPassOp wordPassOp = new WordPassOp(mContext);
            wordPassOp.initWordPassLevel();
            ConfigManager.Instance().setWordPassLoad();
            try {
                VoaSoundOp voaSoundOp = new VoaSoundOp(mContext);
                ArrayList<VoaSound> oldListUK = voaSoundOp.findDataUK();
                ArrayList<VoaSound> oldListUS = voaSoundOp.findDataUS();
                for (VoaSound voaSound : oldListUK) {
                    voaSoundOp.updateUK(voaSound.wordScore, voaSound.totalScore, voaSound.voa_id, voaSound.filepath, voaSound.time, voaSound.itemId, voaSound.sound_url);
                }
                for (VoaSound voaSound : oldListUS) {
                    voaSoundOp.updateUS(voaSound.wordScore, voaSound.totalScore, voaSound.voa_id, voaSound.filepath, voaSound.time, voaSound.itemId, voaSound.sound_url);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        setUser();
    }


    private void setUser() {
        User user = new User();
        user.vipStatus = UserInfoManager.getInstance().getVipStatus();
        user.name = UserInfoManager.getInstance().getVipStatus();
        user.uid = UserInfoManager.getInstance().getUserId();
        if (!AdTimeCheck.setAd()) {
            user.vipStatus = "1";
        } else {
            user.vipStatus = UserInfoManager.getInstance().getVipStatus();
        }
        IyuUserManager.getInstance().setCurrentUser(user);
        if (user.name.isEmpty()) {
            IyuUserManager.getInstance().logout();
        }
        //个人中心
        //用户登录时配置！！！
        int uid = UserInfoManager.getInstance().getUserId();
        String username = UserInfoManager.getInstance().getUserName();
        String vipState = UserInfoManager.getInstance().getVipStatus();

        PersonalHome.setAppInfo(Constant.APPID, getResources().getString(R.string.app_name));
        String mainPath = "com.iyuba.conceptEnglish.activity.MainFragmentActivity";

        PersonalHome.setSaveUserinfo(uid, username, vipState);
        PersonalHome.setCategoryType(PersonalType.NCE);
        PersonalHome.setAppInfo(Constant.APPID, Constant.APPName);
        PersonalHome.setMainPath(mainPath);
        //开启昵称修改
        PersonalHome.setEnableEditNickname(true);
    }

    private boolean isActivityStarted = false;

    @Override
    protected void onResume() {
        super.onResume();
        isActivityStarted = false;

        //这里循环查找没有下载的数据，然后重置为未下载状态
        List<LocalMarkEntity_conceptDownload> noDownList = ConceptDataManager.getLocalMarkDownloadStatusData(UserInfoManager.getInstance().getUserId(),TypeLibrary.FileDownloadStateType.file_isDownloading);
        if (noDownList!=null&&noDownList.size()>0){
            for (int i = 0; i < noDownList.size(); i++) {
                LocalMarkEntity_conceptDownload download = noDownList.get(i);
                ConceptDataManager.updateLocalMarkDownloadStatus(download.voaId,download.lessonType,UserInfoManager.getInstance().getUserId(), TypeLibrary.FileDownloadStateType.file_no,download.position);
            }
        }

        //查找通知是否开启
        /*if (XXPermissions.isGranted(this, Permission.NOTIFICATION_SERVICE)) {
            bindConceptPlayService();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("请注意")
                    .setMessage("当前应用需要开启通知栏权限，请手动开启后使用")
                    .setPositiveButton("前往授权", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            XXPermissions.startPermissionActivity(MainFragmentActivity.this, Permission.NOTIFICATION_SERVICE, new OnPermissionPageCallback() {
                                @Override
                                public void onGranted() {
                                    dialog.dismiss();
                                }

                                @Override
                                public void onDenied() {

                                }
                            });
                        }
                    })
                    .setNegativeButton("取消授权",null)
                    .setCancelable(false).create().show();
        }*/
    }

    //视频下载后点击
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DLItemEvent dlEvent) {
        BasicDLPart dlPart = dlEvent.items.get(dlEvent.position);
        switch (dlPart.getType()) {
            case "voa":
            case "csvoa":
            case "bbc":
            case "song":
                startActivity(AudioContentActivity.getIntent2Me(this,
                        dlPart.getCategoryName(), dlPart.getTitle(), dlPart.getTitleCn(),
                        dlPart.getPic(), dlPart.getType(), dlPart.getId()));
                break;
            case "voavideo":
            case "meiyu":
            case "ted":
            case "bbcwordvideo":
            case "topvideos":
            case "japanvideos":
                startActivity(VideoContentActivity.getIntent2Me(this,
                        dlPart.getCategoryName(), dlPart.getTitle(), dlPart.getTitleCn(),
                        dlPart.getPic(), dlPart.getType(), dlPart.getId()));
                break;
        }

    }

    /**
     * 获取视频模块“现在升级的点击”
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HeadlineGoVIPEvent headlineGoVIPEvent) {
        NewVipCenterActivity.start(this, NewVipCenterActivity.VIP_APP);
    }

    /***************************临时功能处理********************/
    //是否显示侧边栏
    public boolean isDrawerOpen() {
        return binding.drawerLayout.isDrawerOpen(GravityCompat.START);
    }

    //开启侧边栏
    public void openDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.START);
    }

    //关闭侧边栏
    public void closeDrawer() {
        binding.drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DrawerEvent event) {
        if (event.isOpen()) {
            openDrawer();
        } else {
            closeDrawer();
        }

        DrawerSession.getInstance().setLeftOpen(isDrawerOpen());
    }

    /*************小程序登陆回调***************/
    //小程序登陆回调
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(WxLoginEvent event) {
        if (!NetworkUtil.isConnected(this)) {
            ToastUtil.showToast(this, "网络链接已断开，请链接网络后重试");
            return;
        }

        if (WxLoginSession.getInstance().getWxSmallToken() == null) {
            return;
        }

        if (event.getErrCode() == 0) {
            getUidByToken();
        } else {
            ToastUtil.showToast(this, "微信一键登录失败，请重试或者更换登录方式");
        }
    }

    //刷新用户信息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RefreshDataEvent event) {
        if (event.getType().equals(TypeLibrary.RefreshDataType.userInfo)) {
            getUserInfo(true, String.valueOf(UserInfoManager.getInstance().getUserId()));
        }

        if (event.getType().equals(TypeLibrary.RefreshDataType.existApp)){
            //退出app
            exit();
        }

        if (event.getType().equals(TypeLibrary.RefreshDataType.word_pass)){
            boolean isToWord = false;
            if (getPackageName().equals(Constant.package_concept2)
                    ||getPackageName().equals(Constant.package_englishfm)
                    ||getPackageName().equals(Constant.package_nce)
                    ||getPackageName().equals(Constant.package_newconcepttop)){
                isToWord = true;
            }

            //跳转到单词界面
            if (isToWord){
                //检查单词是哪个界面，然后跳转
                int showIndex = 0;
                for (int i = 0; i < bottomList.size(); i++) {
                    String showText = bottomList.get(i).getText();
                    if (showText.equals("单词")
                            ||showText.equals("词汇")){
                        showIndex = i;
                    }
                }
                binding.viewPager.setCurrentItem(showIndex,false);
            }
        }
    }

    //界面跳转回调
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ShowPageEvent event){
        if (event.getShowPage().equals(ShowPageEvent.Page_WalletList)){
            //钱包记录界面
            WalletListActivity.start(this);
        }
    }

    //刷新用户信息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RefreshUserInfoEvent event){
        if (UserInfoManager.getInstance().isLogin()){
            UserInfoManager.getInstance().getRemoteUserInfo(UserInfoManager.getInstance().getUserId(), null);
        }
    }

    //获取uid
    private Disposable uidSub;

    private void getUidByToken() {
        RxUtil.unsubscribe(uidSub);
        DataHelpManager.getInstance().getUserIdByToken(WxLoginSession.getInstance().getWxSmallToken())
                .subscribe(new Observer<UidResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        uidSub = d;
                    }

                    @Override
                    public void onNext(UidResponse response) {
                        if (response != null && response.getResult().equals("200")) {
                            getUserInfo(false, response.getUid());
                        } else {
                            ToastUtil.showToast(MainFragmentActivity.this, "微信一键登录失败，请重试或更换其他登录方式");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showToast(MainFragmentActivity.this, "微信一键登录失败，请重试或更换其他登录方式");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //获取用户信息
    private void getUserInfo(boolean isFist, String userId) {
        UserInfoManager.getInstance().getRemoteUserInfo(Integer.parseInt(userId), new UserinfoCallbackListener() {
            @Override
            public void onSuccess() {
                EventBus.getDefault().post(new VipChangeEvent());
            }

            @Override
            public void onFail(String errorMsg) {
                if (!isFist) {
                    ToastUtil.showToast(MainFragmentActivity.this, "微信一键登录失败，请重试或更换其他登录方式");
                }
            }
        });
    }

    /*******************************后台播放********************/
    //是否已经绑定了服务
    private boolean isBindService = false;

    //绑定播放器
    private void bindConceptPlayService() {
        if (!isBindService) {
            isBindService = true;

            //新概念的后台服务
            Intent intent = new Intent();
            intent.setClass(this, ConceptBgPlayService.class);
            bindService(intent, ConceptBgPlayManager.getInstance().getConnection(), Context.BIND_AUTO_CREATE);
            //中小学的后台服务
            if (!AbilityControlManager.getInstance().isLimitJunior()){
                Intent juniorIntent = new Intent();
                juniorIntent.setClass(this, JuniorBgPlayService.class);
                bindService(juniorIntent, JuniorBgPlayManager.getInstance().getConnection(), Context.BIND_AUTO_CREATE);
            }
            //故事的后台服务
            if (!AbilityControlManager.getInstance().isLimitNovel()){
                Intent novelIntent = new Intent();
                novelIntent.setClass(this, NovelBgPlayService.class);
                bindService(novelIntent, NovelBgPlayManager.getInstance().getConnection(),  Context.BIND_AUTO_CREATE);
            }
        }
    }

    //解绑播放器
    private void unbindConceptPlayService() {
        unbindService(ConceptBgPlayManager.getInstance().getConnection());
        unbindService(JuniorBgPlayManager.getInstance().getConnection());
        unbindService(NovelBgPlayManager.getInstance().getConnection());
    }

    /*********************************分钟广播********************/
    //绑定分钟广播
    private void registerTimeTick(){
        IntentFilter filter=new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(timeReceiver,filter);
    }

    //解绑分钟广播
    private void unregisterTimeTick(){
        unregisterReceiver(timeReceiver);
    }

    private BroadcastReceiver timeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_TIME_TICK)){
                //分钟广播

                //先获取定时时间
                long alarmTime = ConfigManager.Instance().getDelayTime();
                if (alarmTime==0){
                    return;
                }

                if (System.currentTimeMillis() >= alarmTime){
                    //停止三个播放功能
                    //暂停播放-新概念
                    EventBus.getDefault().post(new ConceptBgPlayEvent(ConceptBgPlayEvent.event_control_pause));
                    EventBus.getDefault().post(new ConceptBgPlayEvent(ConceptBgPlayEvent.event_audio_pause));
                    //暂停播放-中小学
                    EventBus.getDefault().post(new JuniorBgPlayEvent(JuniorBgPlayEvent.event_control_pause));
                    EventBus.getDefault().post(new JuniorBgPlayEvent(JuniorBgPlayEvent.event_audio_pause));
                    //暂停播放-故事
                    EventBus.getDefault().post(new NovelBgPlayEvent(NovelBgPlayEvent.event_control_pause));
                    EventBus.getDefault().post(new NovelBgPlayEvent(NovelBgPlayEvent.event_audio_pause));
                    //设置归0
                    ConfigManager.Instance().setAlarmItem(0);
                }
            }
        }
    };

    /******************************统一权限管理***********************************/
    //xxpermission在Android15上存在问题，因此需要统一管理下
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode== PermissionFixUtil.concept_home_downloadFile_code){
            if (grantResults.length<permissions.length){
                ToastUtil.showToast(this,"当前权限为功能所必需的权限，请全部授权后使用");
            }else {
                ToastUtil.showToast(this,"授权成功，请点击按钮后下载音频");
            }
        }
    }
}
