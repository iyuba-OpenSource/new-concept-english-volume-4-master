package com.iyuba.core.talkshow.lesson;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.VideoControls;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.google.android.material.tabs.TabLayout;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.activity.login.LoginUtil;
import com.iyuba.core.common.data.local.DubDBManager;
import com.iyuba.core.common.data.model.TalkLesson;
import com.iyuba.core.common.data.remote.IntegralService;
import com.iyuba.core.common.util.NetStateUtil;
import com.iyuba.core.common.util.ScreenUtils;
import com.iyuba.core.common.util.StorageUtil;
import com.iyuba.core.common.util.TimeUtil;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.common.widget.Util;
import com.iyuba.core.common.widget.dialog.DownloadDialog;
import com.iyuba.core.event.DownloadEvent;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.talkshow.TalkShowFragment;
import com.iyuba.core.talkshow.dub.DubbingActivity;
import com.iyuba.core.talkshow.dub.DubbingPresenter;
import com.iyuba.core.talkshow.dub.preview.Share;
import com.iyuba.core.talkshow.lesson.rank.RankFragment;
import com.iyuba.core.talkshow.lesson.recommend.RecommendFragment;
import com.iyuba.core.talkshow.lesson.videoView.BaseVideoControl;
import com.iyuba.core.talkshow.lesson.videoView.MyOnTouchListener;
import com.iyuba.dlex.bizs.DLManager;
import com.iyuba.lib.R;
import com.iyuba.module.headlinetalk.ui.widget.LoadingDialog;
import com.iyuba.widget.popmenu.ActionItem;
import com.iyuba.widget.popmenu.PopMenu;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import devcontrol.DevControlActivity;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;
import personal.iyuba.personalhomelibrary.utils.StatusBarUtil;
import rx.Subscription;
import timber.log.Timber;

@RuntimePermissions
public class LessonPlayActivity extends AppCompatActivity implements LessonPlayMvpView{

    private static final String VOA = "voa_data";
    private static final String VOA_LIST = "voa_data_list";

    //新的view
    VideoView mVideoView;
    RatingBar mDifficultyRb;
    ImageView ivCollect;
    ImageView icMore;
    TabLayout mTabLayout;
    ViewPager mViewPager;

//    private ProgressDialog loadDialog;

    private LoadingDialog newLoadingDialog;

    private DownloadDialog mDownloadDialog;

    private NormalVideoControl mVideoControl;
    private Subscription subscribe;
    private String mPathMp4;

    private boolean mIsPause;
    private long mCurPosition;
    private TalkLesson mTalkLesson;
    private List<TalkLesson> mTalkLessonList;

    private List<Fragment> mFragmentList = new ArrayList<>();

    private String mUid;
    private int mUidInt;
    private Context mContext;
    private CommonPagerAdapter mFragmentAdapter;

    private IntroductionFragment introductionFragment;
    private RankFragment myTalkListFragment;
    private RecommendFragment recommendFragment;

    private String mDir;
    private PopMenu menu;

    private LessonPlayPresenter mPresenter;

    private DubbingPresenter mDownloadPresenter;
    private Timer timer;
    private Handler handler=new Handler(Looper.myLooper(), message -> {
        catchPlayError();
        return false;
    });

    public static Intent buildIntent(Context context, TalkLesson voa, List<TalkLesson> itemList) {
        Intent intent = new Intent();
        intent.setClass(context, LessonPlayActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(VOA, voa);
        intent.putParcelableArrayListExtra(VOA_LIST, (ArrayList<? extends Parcelable>) itemList);
        return intent;
    }

    private String getMp4Path(){
        //http://staticvip.iyuba.cn/video/voa/321/321001.mp4
        StringBuilder builder = new StringBuilder();
        builder.append("http://");
        if (UserInfoManager.getInstance().isVip()){
            builder.append(Constant.staticStr);
        }else {
            builder.append("static0.");
        }
        builder.append(Constant.IYUBA_CN_IN)
                .append(mTalkLesson.video);
        return builder.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_play);
        initOldView();

        StatusBarUtil.setColor(this, ResourcesCompat.getColor(getResources(), R.color.black, getTheme()));
        TalkShowFragment.addActivity(this);
        timer=new Timer(true);
        mContext = this;
        mTalkLesson = getIntent().getParcelableExtra(VOA);
        mTalkLessonList = getIntent().getParcelableArrayListExtra(VOA_LIST);
        mPathMp4 = getMp4Path();
        mDir = StorageUtil
                .getMediaDir(mContext, mTalkLesson.voaId())
                .getAbsolutePath();
        //mPathMp4 = "http://"+Constant.staticStr+Constant.IYUBA_CN+"video/voa/321/321001.mp4";
        mUid = String.valueOf(UserInfoManager.getInstance().getUserId());
        mUidInt = UserInfoManager.getInstance().getUserId();
        initVideo();
        initView();
        initFragment();
        menu = buildMenu();
        mPresenter = new LessonPlayPresenter();
        mPresenter.attachView(this);
//        loadDialog=new ProgressDialog(this);
//        loadDialog.setMessage("下载中...");
//        loadDialog.setTitle("提示");
////        loadDialog.setCancelable(false);
//        loadDialog.setMax(100);
        DLManager dlManager = DLManager.getInstance();//单例引用对象了

        mDownloadPresenter = new DubbingPresenter(dlManager);
        mDownloadPresenter.init(mContext,mTalkLesson);

        EventBus.getDefault().register(this);
        mDownloadDialog = new DownloadDialog(this);
        mDownloadDialog.setmOnDownloadListener(new DownloadDialog.OnDownloadListener() {
            @Override
            public void onContinue() {
                mDownloadDialog.dismiss();
            }

            @Override
            public void onCancel() {
                mDownloadPresenter.cancelDownload();
                finish();
            }
        });

        icMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu.show(view);
            }
        });
    }

    //使用手写初始化数据
    private void initOldView(){
        mVideoView = findViewById(R.id.video_view);
        mDifficultyRb = findViewById(R.id.difficulty_rb);
        ivCollect = findViewById(R.id.collect);
        icMore = findViewById(R.id.ic_more);
        mTabLayout = findViewById(R.id.detail_tabs);
        mViewPager = findViewById(R.id.viewpager);

        TextView dubbing = findViewById(R.id.dubbing);
        dubbing.setOnClickListener(v->{
            onDubbingClick();
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mVideoView.pause();
        mTalkLesson = intent.getParcelableExtra(VOA);
        mTalkLessonList = intent.getParcelableArrayListExtra(VOA_LIST);
        mPathMp4 =getMp4Path();
        mDir = StorageUtil
                .getMediaDir(mContext, mTalkLesson.voaId())
                .getAbsolutePath();
        if (checkFileExist()) {
            Timber.e("文件存在，本地播放");
            mVideoView.setVideoURI(Uri.fromFile(StorageUtil.getVideoFile(this, mTalkLesson.voaId())));
        } else {
            Timber.e("文件不存在，播放网络音频");
            mVideoView.setVideoPath(mPathMp4);
        }


        if (introductionFragment != null) {
            introductionFragment.setText(mTalkLesson.DescCn);
        }
        if (recommendFragment != null) {
            recommendFragment.upData(mTalkLessonList, mTalkLesson.Id);
        }
    }

    @Override
    protected void onPause() {
        MobclickAgent.onPause(this);
        pauseVideoPlayer("0");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mDownloadDialog.dismiss();
        mPresenter.detachView();
        //mDownloadPresenter.detachView();
        EventBus.getDefault().unregister(this);
        if (mVideoView.isPlaying()) {
            mVideoView.pause();
        }
        mVideoView.release();
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        Timber.d("onConfigurationChanged");
        setVideoViewParams();
    }

    @Override
    public void onBackPressed() {
        if (isDownloading()) {
            mDownloadDialog.show();
        } else {
            if (mVideoControl.isFullScreen()) {
                mVideoControl.exitFullScreen();
            } else {
                finish();
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void initVideo() {
        MyOnTouchListener listener = new MyOnTouchListener(this);
        listener.setSingleTapListener(mSingleTapListener);
        mVideoControl = new NormalVideoControl(this);
        mVideoControl.setMode(BaseVideoControl.Mode.SHOW_AUTO);
        mVideoControl.setBackCallback(new BaseVideoControl.BackCallback() {
            @Override
            public void onBack() {
                if (!isDownloading()) {
                    finish();
                } else {
                    mDownloadDialog.show();
                }
                finish();
            }
        });
        mVideoControl.setToTvCallBack(new BaseVideoControl.ToTvCallback() {
            @Override
            public void onToTv() {
                List<String> voaUrls =new ArrayList<>();
                List<String> voaTitles = new ArrayList<>();
                for (TalkLesson lesson :mTalkLessonList){
                    voaUrls.add(lesson.getVideoPath());
                    voaTitles.add(lesson.TitleCn);
                }
                chooseDevice(mPathMp4,mTalkLesson.TitleCn,voaUrls,voaTitles);
            }
        });
        mVideoView.setControls(mVideoControl);//VideoControlsCore
        mVideoControl.setOnTouchListener(listener);
        mVideoView.setOnPreparedListener(videoPreparedListener);
        mVideoView.setOnCompletionListener(videoCompletionListener);
        //mVideoView.setVideoURI(mPresenter.getVideoUri());
        if (checkFileExist()) {
            Timber.e("文件存在，本地播放");
            mVideoView.setVideoURI(Uri.fromFile(StorageUtil.getVideoFile(this, mTalkLesson.voaId())));
//            handler.sendEmptyMessageDelayed(0,2500);
        } else {
            Timber.e("文件不存在，播放网络音频");
            mVideoView.setVideoPath(mPathMp4);
        }
        setVideoViewParams();
    }

    //不知道为啥，这里非要删除视频文件
    private void catchPlayError(){
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
        if (!mVideoView.isPlaying()){
//                    Looper.prepare();
            boolean delete=StorageUtil.getVideoFile(LessonPlayActivity.this, mTalkLesson.voaId()).delete();
            new AlertDialog.Builder(LessonPlayActivity.this)
                    .setMessage("文件已损坏，请重新下载")
                    .setTitle("提示")
                    .setPositiveButton("确定", (dialogInterface, i) -> startDownload())
                    .setNegativeButton("取消", (d, i) -> {
                        mVideoView.setVideoPath(mPathMp4);
                        menu = buildMenu();
                    })
                    .show();
//                    Looper.loop();
        }
//            }
//        },3000,Integer.MAX_VALUE);
    }

    private void chooseDevice(String url, String title, List<String> voaUrls, List<String> voaTitles) {
        Intent intent = new Intent(this, DevControlActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("urls", (Serializable) voaUrls);
        intent.putExtra("titles", (Serializable) voaTitles);
        intent.putExtra("title", title);
        startActivity(intent);
    }

    MyOnTouchListener.SingleTapListener mSingleTapListener = new MyOnTouchListener.SingleTapListener() {
        @Override
        public void onSingleTap() {
            if (mVideoControl != null) {
                if (mVideoControl.getControlVisibility() == View.GONE) {
                    mVideoControl.show();
                    if (mVideoView.isPlaying()) {
                        mVideoControl.hideDelayed(VideoControls.DEFAULT_CONTROL_HIDE_DELAY);
                    }
                } else {
                    mVideoControl.hideDelayed(0);
                }
            }
        }
    };

    OnPreparedListener videoPreparedListener = new OnPreparedListener() {
        @Override
        public void onPrepared() {
            if (mCurPosition != 0) {
                pauseVideoPlayer("0");
            } else {
                if (!mIsPause) {
                    mVideoView.start();
                }
            }
        }
    };

    OnCompletionListener videoCompletionListener = new OnCompletionListener() {
        @Override
        public void onCompletion() {
            mVideoView.restart();
            mVideoView.setOnPreparedListener(new OnPreparedListener() {
                @Override
                public void onPrepared() {
                    pauseVideoPlayer("1");
                }
            });
        }
    };

    private void setVideoViewParams() {
        ViewGroup.LayoutParams lp = mVideoView.getLayoutParams();
        int[] screenSize = ScreenUtils.getScreenSize(this);
        lp.width = screenSize[0];
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            lp.height = screenSize[1]; // 16 : 9
        } else {
            lp.height = (int) (lp.width * 0.5625);
        }
        mVideoView.setLayoutParams(lp);
    }

    private void pauseVideoPlayer(String flag) {
        mVideoView.pause();
        //studyRecordUpdateUtil.stopStudyRecord(getApplicationContext(), mPresenter.checkLogin(), flag, mDataManager.getUploadStudyRecordService());
    }

    private void initView() {
        Drawable drawable = mDifficultyRb.getProgressDrawable();
        int drawableSize = (int) getResources().getDimension(R.dimen.difficulty_image_size);
        drawable.setBounds(0, 0, drawableSize, drawableSize);
        mDifficultyRb.setMax(5);
        mDifficultyRb.setProgress(mTalkLesson.getDifficulty());
        final DubDBManager dubDBManager = DubDBManager.getInstance();
        boolean isCollect = false;
        if (!TextUtils.isEmpty(mUid)) {
            isCollect = dubDBManager.getCollect(mTalkLesson.Id, mUid);
        }

        final Drawable collect = getResources().getDrawable(R.drawable.select);
        final Drawable collectNot = getResources().getDrawable(R.drawable.not_selected);
        ivCollect.setImageDrawable(isCollect ? collect : collectNot);
        ivCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mUid)) {
                    boolean ok = dubDBManager.setCollect(mTalkLesson.Id, mUid, mTalkLesson.Title, mTalkLesson.DescCn,
                            mTalkLesson.Pic, mTalkLesson.series);
                    if (ok) {
                        boolean isCollect = dubDBManager.getCollect(mTalkLesson.Id, mUid);
                        if (isCollect) {
                            ivCollect.setImageDrawable(collect);
                        } else {
                            ivCollect.setImageDrawable(collectNot);
                        }
                    }
                } else {
//                    showMessage("请先登录");
//                    startActivity(new Intent(mContext, Login.class));
                    LoginUtil.startToLogin(mContext);
                }
            }
        });

    }

    private void initFragment() {
        String[] titles = {"简介", "排行", "更多"};
        introductionFragment = IntroductionFragment.newInstance(mTalkLesson.DescCn);
        myTalkListFragment = RankFragment.newInstance(mTalkLesson);
        recommendFragment = RecommendFragment.newInstance(mTalkLessonList, mTalkLesson.Id);

        mFragmentList.add(introductionFragment);
        mFragmentList.add(myTalkListFragment);
        mFragmentList.add(recommendFragment);

        mFragmentAdapter = new CommonPagerAdapter(
                this.getSupportFragmentManager(), mFragmentList, titles);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mFragmentAdapter);
        mViewPager.setCurrentItem(0);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private boolean isDownloading() {
        return newLoadingDialog.isShowing() && !mDownloadPresenter.checkFileExist();
    }

    public void stopPlaying() {
        if (mVideoView.isPlaying()) {
            mCurPosition = mVideoView.getCurrentPosition();
            pauseVideoPlayer("0");
        }
    }

    public boolean checkFileExist() {
        return StorageUtil.checkFileExist(mDir, mTalkLesson.voaId());
    }

    private PopMenu buildMenu() {
        String title = checkFileExist()?"已下载":"下载";
        boolean isDownload =(!TextUtils.isEmpty(mTalkLesson.Title)&&!TextUtils.isEmpty(mTalkLesson.DescCn));
        List<ActionItem> items = new ArrayList<>(4);

        if ((!"com.sdiyuba.concept".equals(getPackageName()))
                &&(!"com.sdiyb.conceptStudy".equals(getPackageName()))){
            items.add(new ActionItem(this, "分享", R.drawable.ic_talk_share));
        }
        items.add(new ActionItem(this, "导出PDF",R.drawable.ic_talk_pdf));
        if (isDownload) items.add(new ActionItem(this, title, R.drawable.ic_talk_download));
        menu = new PopMenu(this, items);
        menu.setItemClickListener(new PopMenu.PopMenuOnItemClickListener() {
            @Override
            public void setItemOnclick(ActionItem item, int position) {

                pauseVideoPlayer("0");

//                switch (position) {
//                    case 0:
//                        onShareClick();
//                        break;
//                    case 1:
//                        if (accountManager.checkUserLogin()) {
//                            if (accountManager.isVip()) {
//                                showPDFDialog(true);//里面也扣积分了啊
//                            } else {
//                                showCreditDialog();
//                            }
//                        } else {
//                            ToastUtils.toast("请登录后操作");
//                        }
//                        break;
//                    case 2:
//                        startDownload();
//                        break;
//                }

                if ("com.sdiyuba.concept".equals(getPackageName())
                        ||"com.sdiyb.conceptStudy".equals(getPackageName())){
                    switch (position) {
                        case 0:
                            if (UserInfoManager.getInstance().isLogin()) {
                                if (UserInfoManager.getInstance().isVip()) {
                                    showPDFDialog(true);//里面也扣积分了啊
                                } else {
                                    showCreditDialog();
                                }
                            } else {
                                ToastUtil.showToast(mContext,"请登录后操作");
                            }
                            break;
                        case 1:
                            startDownload();
                            break;
                    }
                }else {
                    switch (position) {
                        case 0:
                            onShareClick();
                            break;
                        case 1:
                            if (UserInfoManager.getInstance().isLogin()) {
                                if (UserInfoManager.getInstance().isVip()) {
                                    showPDFDialog(true);//里面也扣积分了啊
                                } else {
                                    showCreditDialog();
                                }
                            } else {
                                ToastUtil.showToast(mContext,"请登录后操作");
                            }
                            break;
                        case 2:
                            startDownload();
                            break;
                    }
                }
            }
        });
        return menu;
    }

    private void startDownload(){
        if (UserInfoManager.getInstance().isLogin()) {
            downloadLesson();
        } else {
            LoginUtil.startToLogin(this);
        }
    }

    public void onDubbingClick() {
        LessonPlayActivityPermissionsDispatcher.requestAudioSuccessWithPermissionCheck(LessonPlayActivity.this);
    }

    public void onShareClick() {
        pauseVideoPlayer("0");
        IntegralService integralService = IntegralService.Creator.newIntegralService();
        Share.prepareVideoMessage(this, mTalkLesson, integralService,
                UserInfoManager.getInstance().getUserId());
    }

    private void downloadLesson() {
        if (checkFileExist()) {
            showMessage("文件已存在");
        } else {
            if (!NetStateUtil.isConnected(mContext)) {
                showToast("网络异常");
                return;
            }
//            loadDialog.show();
            //下载音频视频
            showLoading("正在下载~");
            mDownloadPresenter.download();
        }
    }
    private int progress=50;
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadFinish(DownloadEvent downloadEvent) {
        switch (downloadEvent.status) {
            case DownloadEvent.Status.FINISH:
                closeLoading();
                setVideoAndAudio();
//                loadDialog.dismiss();
                //此处下载完视频 音频后 本地数据库记录一下 ，应该不会出现 数据拿不到的情况，发布的配音不进入这个页面
                if (!TextUtils.isEmpty(mTalkLesson.Title)&&!TextUtils.isEmpty(mTalkLesson.DescCn)) {
                    DubDBManager.getInstance().setDownload(mTalkLesson.Id, mUid, mTalkLesson.Title,
                            mTalkLesson.DescCn, mTalkLesson.Pic, mTalkLesson.series);
                    ToastUtil.showToast(this,"下载完成");
                }else {
                    ToastUtil.showToast(this,"下载数据不全，无法保存！");
                }
                menu = buildMenu();
                //mDownloadPresenter.addFreeDownloadNumber();
                break;
            case DownloadEvent.Status.DOWNLOADING:
                progress+=10;
                if (progress>=100){
                    progress=50;
                }
                if (downloadEvent.msg!=null){
//                    loadDialog.setMessage(downloadEvent.msg);
                    showLoading(downloadEvent.msg);
                }
                break;
            default:
                int a=0;
                break;
        }
    }

    public void setVideoAndAudio() {
        try {
            if (mVideoView.isPlaying()) {
                mVideoView.pause();
            }
            int pos = (int) mVideoView.getCurrentPosition();
            mVideoView.setVideoURI(Uri.fromFile(StorageUtil.getVideoFile(this, mTalkLesson.voaId())));
            mVideoView.seekTo(pos - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LessonPlayActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission(Manifest.permission.RECORD_AUDIO)
    public void requestAudioSuccess() {
        stopPlaying();
        mIsPause = true;
        if (UserInfoManager.getInstance().isLogin()) {
            long timestamp = TimeUtil.getTimeStamp();
            Intent intent = DubbingActivity.buildIntent(this, mTalkLesson, timestamp);
            startActivity(intent);
        } else {
            LoginUtil.startToLogin(mContext);
        }
    }

    @OnPermissionDenied(Manifest.permission.RECORD_AUDIO)
    public void requestAudioFailed() {
        Toast.makeText(this, "请授予必要的权限", Toast.LENGTH_LONG).show();
    }

    private void showMessage(String msg) {
        ToastUtil.showToast(mContext, msg);
    }

    private void showCreditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("温馨提示");
        builder.setMessage("非VIP用户每篇PDF需扣除20积分");
        builder.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showPDFDialog(false);
                dialog.dismiss();
            }
        });
        builder.show();

    }

    public void showPDFDialog(final boolean isVip) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] choices = {"英文", "中英双语"};
        builder.setTitle("请选择导出文件的语言").setItems(choices, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                switch (which) {
                    case 0:
                        if (isVip){
                            mPresenter.getPdf(mTalkLesson.voaId(), 1);
                        }else {
                            mPresenter.deductIntegral(LessonPlayPresenter.PDF_ENG, mUidInt, mTalkLesson.voaId());
                        }
                        break;
                    case 1:
                        if (isVip){
                            mPresenter.getPdf(mTalkLesson.voaId(), 0);
                        }else {
                            mPresenter.deductIntegral(LessonPlayPresenter.PDF_BOTH, mUidInt, mTalkLesson.voaId());
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        builder.show();
    }

    @Override
    public void showToast(int resId) {
        ToastUtil.showToast(mContext,resId);
    }

    @Override
    public void showToast(String message) {
        ToastUtil.showToast(mContext,message);
    }

    @Override
    public void onDeductIntegralSuccess(int type) {
        if(type ==LessonPlayPresenter.TYPE_DOWNLOAD ){
            downloadLesson();
        }else if (type == LessonPlayPresenter.PDF_ENG){
            mPresenter.getPdf(mTalkLesson.voaId(), 1);
        }else {
            mPresenter.getPdf(mTalkLesson.voaId(), 0);
        }
    }

    @Override
    public void showPdfFinishDialog(String url) {
        String downloadPath = "http://apps."+ Constant.IYUBA_CN+"iyuba" + url;
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        //针对临时的包名处理
        AlertDialog dialog = null;
        if ("com.sdiyuba.concept".equals(getPackageName())
                ||"com.sdiyb.conceptStudy".equals(getPackageName())){
            dialog = builder.setTitle("PDF已生成 请妥善保存。")
                    .setMessage("下载链接：" + downloadPath + "\n[已复制到剪贴板]\n")
                    .setNegativeButton("下载", null)
                    .setPositiveButton("关闭", null)
                    .create();
        }else {
            dialog = builder.setTitle("PDF已生成 请妥善保存。")
                    .setMessage("下载链接：" + downloadPath + "\n[已复制到剪贴板]\n")
                    .setNegativeButton("下载", null)
                    .setPositiveButton("关闭", null)
                    .setNeutralButton("发送", null)
                    .create();
        }

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        try {
            View v = dialog.getWindow().getDecorView().findViewById(android.R.id.message);
            if (v != null) {
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Util.copy2ClipBoard(mContext, downloadPath);
                        ToastUtil.showToast(mContext,"PDF下载链接已复制到剪贴板");
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Button positive = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    ToastUtil.showToast(mContext,"文件将会下载到" + "iyuba/" + Constant.AppName + "/ 目录下");
                    download(mTalkLesson.TitleCn, downloadPath, mContext);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Button neutral = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        neutral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = mTalkLesson.Title + " PDF";
                Share.shareMessage(mContext, Constant.APP_ICON, "", downloadPath, title, null);
            }
        });
        Util.copy2ClipBoard(mContext, downloadPath);
        ToastUtil.showToast(mContext,"PDF下载链接已复制到剪贴板");
    }

    public void download(String title, String url, Context mContext) {
        //创建下载任务,downloadUrl就是下载链接
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        //指定下载路径和下载文件名
        request.setTitle(title);
        request.setDestinationInExternalPublicDir("/iyuba/" +Constant.AppName, title + ".pdf");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        //获取下载管理器
        DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        //将下载任务加入下载队列，否则不会进行下载
        downloadManager.enqueue(request);
    }

    //显示加载
    private void showLoading(String msg){
        if (newLoadingDialog==null){
            newLoadingDialog = new LoadingDialog(this);
        }
        newLoadingDialog.setMessage(msg);
        if (!newLoadingDialog.isShowing()){
            newLoadingDialog.show();
        }
    }

    //关闭加载
    private void closeLoading(){
        if (newLoadingDialog!=null&&newLoadingDialog.isShowing()){
            newLoadingDialog.dismiss();
        }
    }
}