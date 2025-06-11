package com.iyuba.conceptEnglish.lil.concept_other.talkshow_detail;

import android.Manifest;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.listener.OnSeekCompletionListener;
import com.devbrackets.android.exomedia.listener.VideoControlsButtonListener;
import com.devbrackets.android.exomedia.ui.widget.VideoControls;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.OnPermissionPageCallback;
import com.hjq.permissions.XXPermissions;
import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.ConceptBookChooseManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.search.NewSearchActivity;
import com.iyuba.conceptEnglish.sqlite.db.TalkShowDBManager;
import com.iyuba.conceptEnglish.util.ConceptApplication;
import com.iyuba.core.common.activity.login.LoginUtil;
import com.iyuba.core.common.data.DataManager;
import com.iyuba.core.common.data.local.DubDBManager;
import com.iyuba.core.common.data.local.EvaluateScore;
import com.iyuba.core.common.data.model.SendEvaluateResponse;
import com.iyuba.core.common.data.model.TalkLesson;
import com.iyuba.core.common.data.model.VoaText;
import com.iyuba.core.common.data.model.WavListItem;
import com.iyuba.core.common.data.remote.WordResponse;
import com.iyuba.core.common.util.StorageUtil;
import com.iyuba.core.common.util.TimeUtil;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.event.DownloadEvent;
import com.iyuba.core.event.VipChangeEvent;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.talkshow.dub.MediaRecordHelper;
import com.iyuba.core.talkshow.dub.preview.PreviewActivity;
import com.iyuba.core.talkshow.dub.preview.PreviewInfoBean;
import com.iyuba.core.talkshow.lesson.videoView.BaseVideoControl;
import com.iyuba.core.talkshow.lesson.videoView.MyOnTouchListener;
import com.iyuba.dlex.bizs.DLManager;
import com.iyuba.play.ExtendedPlayer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * @title: 口语秀-详情界面
 * @date: 2023/5/16 15:07
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class TalkShowDetailFragment extends Fragment implements TalkShowDetailView {

    private View rootView;
    private static final String key_voaId = "voaId";

    //播放音频链接
    private String playUrl = "http://dict.youdao.com/dictvoice?audio=";
    //章节数据
    private TalkLesson mTalkLesson;
    //时间戳
    private long mTimeStamp;
    //操作数据
    public TalkShowDetailPresenter mPresenter;
    //预览信息数据
    private PreviewInfoBean previewInfoBean;
    //加载弹窗
//    private LoadingDialog loadingDialog;
    //录音
    private MediaRecordHelper mediaRecordHelper;
    //背景音乐播放器
    private MediaPlayer mAccAudioPlayer;
    //单词播放器
    private MediaPlayer wordPlayer;
    //播放器
    private ExtendedPlayer mRecordPlayer;
    //视频控制器
    private TalkShowDetailVideoControl mVideoControl;
    //是否已经发送
    public static boolean isSend = false;
    //视频时间长度
    public long mDuration;
    //是否跳转进度
    private boolean isSeekToItem;
    //延迟的时间
    private int delayTime;
    //适配器
    public TalkShowDetailAdapter mAdapter;
    //评测分数回调
    private NewScoreCallback mNewScoreCallback;
    //单词集合信息
    private String wordString;
    //数据
    private Map<Integer, WavListItem> map = new HashMap<>();
    //句子数据
    private List<VoaText> mVoaTextList;
    //是否已经加载文件完成
    private boolean isFileLoadFinish = false;

    /*******控件********/
    private VideoView mVideoView;
    public RecyclerView mRecyclerView;
    public TextView mPreview;
    public FrameLayout mFlLoadError;

    private TextView word;
    private TextView pron;
    private TextView def;
    private CardView wordRoot;
    private Button close;
    private Button add;

    private ImageView audio;

    private LinearLayout showLayout;
    private ProgressBar showLoading;
    private TextView showMsg;
    private TextView showBtn;

    private ImageView rankView;
    private ImageView albumView;

    /******************权限说明******************/
    private String[] showPermissionArray = null;
    private String showPermissionText = null;

    public static TalkShowDetailFragment getInstance(String voaId) {
        TalkShowDetailFragment fragment = new TalkShowDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(key_voaId, voaId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_talk_detail, container,false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initLayouts();
        getData();
    }

    @Override
    public void onPause() {
        super.onPause();

//        closeSearchWordDialog();
        if (mAccAudioPlayer!=null&&mAccAudioPlayer.isPlaying()) {
            mAccAudioPlayer.pause();
        }
    }

    @Override
    public void onDestroyView() {
        if (!isFileLoadFinish&&mPresenter!=null){
            Log.d("视频和音频", "需要删除");
            deleteAudioAndVideo();
        }

        if (mPresenter!=null){
            mPresenter.detachView();
        }
        handler.removeCallbacksAndMessages(null);
        if (mAccAudioPlayer!=null&&mAccAudioPlayer.isPlaying()) {
            mAccAudioPlayer.pause();
            mAccAudioPlayer.release();
        }
        EventBus.getDefault().unregister(this);

        super.onDestroyView();
    }

    /*****************************初始化**********************/
    private void init(){
        showLoadingByProgress(false,false,null,null);
        mTalkLesson = TalkShowDBManager.getInstance().findTalkByVoaId(getArguments().getString(key_voaId));

        mTimeStamp = TimeUtil.getTimeStamp();
        previewInfoBean = new PreviewInfoBean();

        DLManager dlManager = DLManager.getInstance();//单例引用对象了
        mPresenter = new TalkShowDetailPresenter(dlManager);
        mPresenter.attachView(this);
        if (mTalkLesson!=null){
            mPresenter.init(getActivity(),mTalkLesson);
            mPresenter.syncVoaTexts(mTalkLesson.voaId());
        }
        EventBus.getDefault().register(this);

        initMedia();
        initRecyclerView();
        initListener();
        initClick();

        showMsgByStatus();
    }

    private void initLayouts() {
        mVideoView = rootView.findViewById(R.id.video_view_dub);
        mRecyclerView = rootView.findViewById(R.id.recycler_view);
        mPreview = rootView.findViewById(R.id.preview_dubbing);
        mFlLoadError = rootView.findViewById(R.id.fl_load_error);

        //单词
        word = rootView.findViewById(R.id.word);
        pron = rootView.findViewById(R.id.pron);
        def = rootView.findViewById(R.id.def);
        wordRoot = rootView.findViewById(R.id.jiexi_root);
        close = rootView.findViewById(R.id.close);
        add = rootView.findViewById(R.id.dialog_btn_addword);

        audio = rootView.findViewById(R.id.iv_audio);

        showLayout = rootView.findViewById(R.id.showLayout);
        showLoading = rootView.findViewById(R.id.showLoading);
        showMsg = rootView.findViewById(R.id.showMsg);
        showBtn = rootView.findViewById(R.id.showBtn);

        rankView = rootView.findViewById(R.id.rank_dubbing);
        albumView = rootView.findViewById(R.id.more_dubbing);
    }

    private void initMedia() {
        //mAudioEncoder = new AudioEncoder();
        mediaRecordHelper = new MediaRecordHelper();
        mAccAudioPlayer = new MediaPlayer();
        wordPlayer = new MediaPlayer();
        wordPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        mRecordPlayer = new ExtendedPlayer(getActivity());
        MyOnTouchListener listener = new MyOnTouchListener(getActivity());
        listener.setSingleTapListener(mSingleTapListener);
        mVideoControl = new TalkShowDetailVideoControl(getActivity());
        mVideoControl.setMode(BaseVideoControl.Mode.SHOW_MANUAL);
        mVideoControl.setFullScreenBtnVisible(false);
        mVideoControl.setButtonListener(new VideoControlsButtonListener() {
            @Override
            public boolean onPlayPauseClicked() {
                if (mVideoView.isPlaying()) {
                    pause();
                } else {
                    mVideoView.start();
                    //mAdapter.repeatPlayVoaText(mRecyclerView, mAdapter.getOperateVoaText());
                }
                return true;
            }

            @Override
            public boolean onPreviousClicked() {
                return false;
            }

            @Override
            public boolean onNextClicked() {
                return false;
            }

            @Override
            public boolean onRewindClicked() {
                return false;
            }

            @Override
            public boolean onFastForwardClicked() {
                return false;
            }
        });
        mVideoControl.setBackCallback(new BaseVideoControl.BackCallback() {
            @Override
            public void onBack() {
                try {
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mVideoView.setControls(mVideoControl);

        mVideoView.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion() {
                try {
                    mVideoView.setVideoURI(Uri.fromFile(StorageUtil.getVideoFile(getActivity(), mTalkLesson.voaId())));
                    mAccAudioPlayer.reset();
                    String path = StorageUtil.getAudioFile(getActivity(),
                            mTalkLesson.voaId()).getAbsolutePath();
                    mAccAudioPlayer.setDataSource(path);
                    mAccAudioPlayer.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mVideoView.setOnSeekCompletionListener(new OnSeekCompletionListener() {
            @Override
            public void onSeekComplete() {

            }
        });
        mVideoControl.setOnTouchListener(listener);
        mVideoView.setOnSeekCompletionListener(new OnSeekCompletionListener() {
            @Override
            public void onSeekComplete() {
                if (isSeekToItem) {
                    isSeekToItem = false;
                    Timber.e("评测" + "原视频播放暂停");
                    handler.sendEmptyMessageDelayed(1, delayTime);
                }
                startRecording();
            }
        });
    }

    private void initRecyclerView() {
        mAdapter = new TalkShowDetailAdapter(mPresenter,this);
        mAdapter.setPlayVideoCallback(mPlayVideoCallback);
        mAdapter.setPlayRecordCallback(mPlayRecordCallback);
        mAdapter.setRecordingCallback(mRecordingCallback);
        mAdapter.setScoreCallback(new TalkShowDetailAdapter.ScoreCallback() {
            @Override
            public void onResult(int pos, int score, int fluec, String url) {
                previewInfoBean.setSentenceScore(pos, score);
                previewInfoBean.setSentenceFluent(pos, fluec);
                previewInfoBean.setSentenceUrl(pos, url);
                //存入数据库 流畅度
                DubDBManager.getInstance().setFluent(mTalkLesson.Id, String.valueOf(UserInfoManager.getInstance().getUserId()), String.valueOf(pos), fluec, url);
            }
        });
        mAdapter.setOnSearchWordListener(new TalkShowDetailAdapter.OnSearchWordListener() {
            @Override
            public void onSearchWord(String selectText) {
//                showSearchWordDialog(selectText);

                NewSearchActivity.start(getActivity(),selectText);
            }
        });
        mAdapter.setTimeStamp(mTimeStamp);
        mRecyclerView.setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        //如果Item够简单，高度是确定的，打开FixSize将提高性能。
        //mRecyclerView.setHasFixedSize(true);
        //设置Item默认动画，加也行，不加也行。
        //mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void initListener() {
        mPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //先检查是否音频、视频文件都存在，并且存在配音文件
                //下载的视频文件
                File videoFile = StorageUtil.getVideoFile(ConceptApplication.getContext(),mTalkLesson.voaId());
                //评测记录
                List<EvaluateScore> list = DubDBManager.getInstance().getEvaluate(mTalkLesson.Id, String.valueOf(UserInfoManager.getInstance().getUserId()));
                //评测的文件
                boolean isEvalFileExist = StorageUtil.hasRecordFile(getActivity().getApplicationContext(), mTalkLesson.voaId(), mTimeStamp);
                if (videoFile.exists()&&list!=null&&list.size()>0&&isEvalFileExist) {
                    pause();
                    startPreviewActivity();
                } else {
                    if (!videoFile.exists()){
                        showToast("未查找到音视频文件，请重新下载");
                    }else {
                        showToast("还没有配音哦，点击话筒试试吧");
                    }
                }

            }
        });
        mFlLoadError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.syncVoaTexts(mTalkLesson.voaId());
            }
        });
    }

    private void initClick(){
        close.setOnClickListener(v->{
            wordRoot.setVisibility(View.GONE);
        });
        add.setOnClickListener(v->{
            if (!UserInfoManager.getInstance().isLogin()) {
                LoginUtil.startToLogin(getActivity());
                return;
            }
            List<String> words = Arrays.asList(wordString);
            mPresenter.insertWords(UserInfoManager.getInstance().getUserId(), words);
        });
        audio.setOnClickListener(v->{
            try {
                wordPlayer.reset();
                wordPlayer.setDataSource(playUrl + wordString);
                wordPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        showBtn.setOnClickListener(v->{
            String showText = showBtn.getText().toString();
            if (showText.equals("登录账号")){
//                startActivity(new Intent(getActivity(), Login.class));
                LoginUtil.startToLogin(getActivity());
            }else if (showText.equals("申请权限")){
                //区分android15和以下的版本
                if (Build.VERSION.SDK_INT >= 35){
                    showPermissionArray = new String[]{Manifest.permission.RECORD_AUDIO};
                    showPermissionText = "录音权限(麦克风权限)";
                }else {
                    showPermissionArray = new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    showPermissionText = "录音权限(麦克风权限) 和 存储权限";
                }

                XXPermissions.with(getActivity())
                        .permission(showPermissionArray)
                        .request(new OnPermissionCallback() {
                            @Override
                            public void onGranted(List<String> permissions, boolean all) {
                                if (all){
                                    showMsgByStatus();
                                }else {
                                    showMsg.setText("当前功能需要开启 "+showPermissionText+" ,请全部授权后使用～");
                                }
                            }

                            @Override
                            public void onDenied(List<String> permissions, boolean never) {
                                if (never){
                                    showMsg.setText("当前功能需要开启 "+showPermissionText+" ,您已经拒绝授权，请手动授权后使用～");
                                    showBtn.setText("手动授权");
                                }
                            }
                        });
            }else if (showText.equals("手动授权")){
                XXPermissions.startPermissionActivity(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, new OnPermissionPageCallback() {
                    @Override
                    public void onGranted() {
                        showMsgByStatus();
                    }

                    @Override
                    public void onDenied() {

                    }
                });
            }else if (showText.equals("重新下载")){
                mPresenter.deleteAudioAndVideo();
                checkVideoAndMedia(false);
            }else if (showText.equals("下载文件")){
                showLoadingByProgress(true,true,"正在下载","");
                mPresenter.download();
            }else if (showText.equals("重新加载")){
                getData();
            }
        });
        rankView.setOnClickListener(v->{
            String voaId = getArguments().getString(key_voaId);
            TalkShowJumpActivity.start(getActivity(),TalkShowJumpActivity.JUMP_RANK,voaId);
        });
        albumView.setOnClickListener(v->{
            String voaId = getArguments().getString(key_voaId);
            TalkShowJumpActivity.start(getActivity(),TalkShowJumpActivity.JUMP_ALBUM,voaId);
        });
    }

    /************************其他方法*********************/
    //触摸回调
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

    /**
     * 检查音频和媒体文件是否下载
     */
    public void checkVideoAndMedia(boolean isFirst) {
        if (mPresenter.checkFileExist()) {
            showLoadingByProgress(false,false,"","");
            setVideoAndAudio();
            Timber.e("下载完成");
        } else {
            Timber.e("下载未完成");

            if (isFirst){
                //下载音频视频
                showLoadingByProgress(true,false,"首次使用需要下载音频和视频文件，时间较长，请确认是否下载所需文件~","下载文件");
            }else {
                //下载音频视频
                showLoadingByProgress(true,true,"正在下载","");
                mPresenter.download();
            }
        }
    }

    //删除音视频
    public void deleteAudioAndVideo(){
        isFileLoadFinish = false;
        if (mPresenter!=null){
            mPresenter.cancelDownload();
            mPresenter.deleteAudioAndVideo();
        }
    }

    //设置视频
    public void setVideoAndAudio() {
        try {
            isFileLoadFinish = true;
            Log.d("视频和音频", "下载完成");

            mVideoView.setVideoURI(Uri.fromFile(StorageUtil.getVideoFile(getActivity(), mTalkLesson.voaId())));
            mVideoView.pause();
            String backGroundAudio = StorageUtil.getAudioFile(getActivity(),
                    mTalkLesson.voaId()).getAbsolutePath();
            Timber.e("背景音地址" + backGroundAudio);
            mAccAudioPlayer.setDataSource(backGroundAudio);
            //mAccAudioPlayer.setDataSource(mTalkLesson.Sound);
            mAccAudioPlayer.prepare();
            mVideoView.setOnPreparedListener(new OnPreparedListener() {
                @Override
                public void onPrepared() {
                    mDuration = mVideoView.getDuration();
                }
            });
        } catch (Exception e) {
            isFileLoadFinish = false;
            Log.d("视频和音频", "出现错误");

            e.printStackTrace();
        }
    }

    //暂停线程
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    pause();
                    break;
            }
        }
    };

    //适配器回调
    TalkShowDetailAdapter.PlayVideoCallback mPlayVideoCallback = new TalkShowDetailAdapter.PlayVideoCallback() {
        @Override
        public void start(final VoaText voaText) {
            handler.removeCallbacksAndMessages(null);
            startPlayVideo(voaText);
        }

        @Override
        public void reStart() {
            if (!mVideoView.isPlaying()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mVideoView.start();
                    }
                });
            }
            try {
                if (!mAccAudioPlayer.isPlaying()) {
                    mAccAudioPlayer.start();
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
                ToastUtil.showToast(getActivity(), "播放器重开异常！");
            }
        }

        @Override
        public boolean isPlaying() {
            return isPlayVideo();
        }

        @Override
        public int getCurPosition() {
            return (int) mVideoView.getCurrentPosition();
        }

        @Override
        public void stop() {
            pause();
        }
    };

    TalkShowDetailAdapter.PlayRecordCallback mPlayRecordCallback = new TalkShowDetailAdapter.PlayRecordCallback() {
        @Override
        public void start(final VoaText voaText) {
            try {
                startPlayRecord(voaText);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void stop() {
            pause();
            handler.removeCallbacksAndMessages(null);
            mAdapter.mOperateHolder.iPlay.setVisibility(View.VISIBLE);
            mAdapter.mOperateHolder.iPause.setVisibility(View.INVISIBLE);
        }

        @Override
        public int getLength() {
            return (int) mRecordPlayer.getDuration();
        }
    };
    TalkShowDetailAdapter.RecordingCallback mRecordingCallback = new TalkShowDetailAdapter.RecordingCallback() {
        @Override
        public void init(String path) {
            //Timber.e("评测文件路径" + path);
            File file = new File(path);
            try {
                if (file.exists()) {
                    file.delete();
                }
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            initRecord(path);
        }

        @Override
        public void start(final VoaText voaText) {
            startRecording(voaText);
        }

        @Override
        public boolean isRecording() {
            return isOnRecording();
        }

        @Override
        public void stop() {
            stopRecording();
        }

        @Override
        public void convert(int paraId, List<VoaText> list) {
            File file = new File(mediaRecordHelper.getMP4FilePath());
        }

        @Override
        public void upload(final int paraId, List<VoaText> list, int progress, int secondaryProgress) {
            //String saveFile = mAudioEncoder.getmSavePath();
            String saveFile = mediaRecordHelper.getMP4FilePath();
            final File flakFile = new File(saveFile);
            boolean isExists = flakFile.exists();
            Timber.e("评测网络请求开始，录音结束" + saveFile + "存在" + isExists);
            if (flakFile.length() != 0 && (paraId - 1) >= 0 && (paraId - 1) < list.size()) {
                mPresenter.uploadSentence(list.get(paraId - 1).sentence, paraId, list.get(paraId - 1).getVoaId(),
                        paraId, "concept", String.valueOf(UserInfoManager.getInstance().getUserId()), flakFile, saveFile, progress, secondaryProgress);//type 原来是talkshow
            } else {
                //mAudioEncoder.stop();
                mediaRecordHelper.stopRecord();
                showToast("空的文件！，不能进行评测");
            }
        }
    };

    //组建数据
    private void buildMap(int index, WavListItem item) {
        map.put(index, item);
    }

    //显示单词数据
    private void showWordView(WordResponse bean) {
        pauseVideoView();
        wordString = bean.getKey();
        wordRoot.startAnimation(initAnimation());
        wordRoot.setVisibility(View.VISIBLE);
        wordRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        word.setText(bean.getKey());
        def.setText(bean.getDef());
        pron.setText(String.format("[%s]", bean.getPron()));
    }

    //动画样式
    public TranslateAnimation initAnimation() {
        TranslateAnimation animation = new TranslateAnimation(-300, 0, 0, 0);
        animation.setDuration(200);
        return animation;
    }

    //获取音频文件时长
    public long getAudioFileVoiceTime(String filePath) {
        long mediaPlayerDuration = 0L;
        if (filePath == null || filePath.isEmpty()) {
            return 0;
        }
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayerDuration = mediaPlayer.getDuration();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
        return mediaPlayerDuration;
    }

    /****************录音**************/
    //初始化录音
    public void initRecord(String path) {
        //Timber.e("评测 录音初始化");
        mediaRecordHelper.setFilePath(path);
        //mAudioEncoder.setSavePath(path);

    }
    //开启录音
    private void startRecording() {
        if (mAdapter.isRecording && !mediaRecordHelper.isRecording /*&& !mAudioEncoder.isRecording()*/) {
            try {
                Timber.d("record" + "startRecording: " + System.currentTimeMillis());
                mediaRecordHelper.recorder_Media();
                //                mAudioEncoder.prepare();
//                //Timber.e("评测 seekTo完成 录音开始");
//                mAudioEncoder.start();
            } catch (Exception e) {
                e.printStackTrace();
                //Timber.e("评测 配音开启失败");
            }
        }
    }
    //是否正在录音
    public boolean isOnRecording() {
        return mediaRecordHelper.isRecording;
    }
    //停止录音
    public void stopRecording() {
        if (mediaRecordHelper.isRecording /*mAudioEncoder.isRecording()*/) {
            Timber.d("record" + "stopRecording: 2" + System.currentTimeMillis());
            mediaRecordHelper.stopRecord();
            //mAudioEncoder.stop();
            //mVideoView.setOnSeekCompletionListener(null);
            mAccAudioPlayer.pause();
            pauseVideoView();
        }
    }

    /*****************评测*************/
    //开始评测
    public void startRecording(VoaText voaText) {
        //Timber.e("评测 录音准备");
        try {
            mVideoView.setVolume(0);
            int seekToVideo = TimeUtil.secToMilliSec(voaText.timing);
            //Timber.e("评测seekTo Time" + seekToVideo);
            mVideoView.seekTo(seekToVideo);//seekTo之后进行评测！！
            //mLoadingView.setVisibility(View.VISIBLE);
            mAccAudioPlayer.seekTo(TimeUtil.secToMilliSec(voaText.timing));
            mVideoView.start();
            if (seekToVideo == 0) {
                startRecording();
            }

            if (!mAccAudioPlayer.isPlaying()) {
                mAccAudioPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast("配音出现异常");
        }
    }
    //评测播放
    public void startPlayRecord(VoaText voaText) {
        mRecordPlayer.reset();
        //321001,1,1672365962093
        //路径不对哈，进行修改
//        File file=StorageUtil.getParaRecordMP4File(getActivity().getApplicationContext(), voaText.getVoaId(), voaText.paraId, mTimeStamp);
        File file=StorageUtil.getParaRecordMp3File(getActivity().getApplicationContext(), voaText.getVoaId(), voaText.paraId, mTimeStamp);
        String path=file.getAbsolutePath();
        ///storage/emulated/0/Android/data/com.iyuba.concept2/files/321001/evaluate/1.mp4
        mRecordPlayer.initialize(path);
        mRecordPlayer.prepareAndPlay();
        mRecordPlayer.setOnCompletionListener(new net.protyposis.android.mediaplayer.MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(net.protyposis.android.mediaplayer.MediaPlayer mediaPlayer) {
                mVideoView.pause();
                mPlayRecordCallback.stop();
            }
        });
        mVideoView.setVolume(0);
        //mRecordPlayer.setVolume(1.0f, 1.0f);
        seekTo(TimeUtil.secToMilliSec(voaText.timing));
        start();
    }

    /*****************视频*******************/
    //播放视频
    public void startPlayVideo(VoaText voaText) {
        pause();
        handler.removeCallbacksAndMessages(null);
        mVideoView.setVolume(1);
        if (voaText != null) {
            mVideoView.seekTo(TimeUtil.secToMilliSec(voaText.timing));
            isSeekToItem = true;
            delayTime = (int) ((voaText.endTiming - voaText.timing) * 1000);
            Timber.e("持续时间" + delayTime);
        }
        mVideoView.start();
    }
    //是否正在播放视频
    public boolean isPlayVideo() {
        return mVideoView.isPlaying();
    }
    //播放
    public void start() {
        if (!mVideoView.isPlaying()) {
            mVideoView.start();
        }
        if (!mAccAudioPlayer.isPlaying()) {
            mAccAudioPlayer.start();
        }
        if (!mRecordPlayer.isPlaying()) {
            mRecordPlayer.start();
        }
    }
    //跳转
    public void seekTo(int millSec) {
        mVideoView.seekTo(millSec);
        mAccAudioPlayer.seekTo(millSec);
    }
    //停止视频播放
    private void pauseVideoView() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mVideoView.pause();
            }
        });
    }

    /*********************单词查询****************/
    //单词查询的弹窗
//    private SearchWordDialog searchWordDialog;
    //显示查询弹窗
    /*private void showSearchWordDialog(String word){
        searchWordDialog = new SearchWordDialog(getActivity(),word);
        searchWordDialog.create();
        searchWordDialog.show();
    }*/

    //关闭查询弹窗
    /*private void closeSearchWordDialog(){
        if (searchWordDialog!=null&&searchWordDialog.isShowing()){
            searchWordDialog.dismiss();
        }
    }*/

    /*****************************接口中的方法*******************/
    @Override
    public void showVoaTexts(List<VoaText> voaTextList) {
        //获取 课程句子列表
        Timber.e("获取数据成功" + voaTextList.size());
        mFlLoadError.setVisibility(View.GONE);
        List<EvaluateScore> list = DubDBManager.getInstance().getEvaluate(mTalkLesson.Id, String.valueOf(UserInfoManager.getInstance().getUserId()));
        if (list != null && list.size() > 0) {
            for (EvaluateScore score : list) {
                for (VoaText voaText : voaTextList) {
                    if (score.paraId.equals(String.valueOf(voaText.paraId))) {
                        voaText.words = DubDBManager.getInstance().getEvWord(mTalkLesson.Id, String.valueOf(UserInfoManager.getInstance().getUserId()), score.paraId);

                        //这里按理说应该需要处理下，但是好像又不需要处理，后面再看看
                        //这里其实不用处理，因为adapter中已经处理了
                        voaText.isEvaluate = true;
                        voaText.isDataBase = true;
                        voaText.setIscore(true);
                        voaText.setIsshowbq(true);
                        voaText.setScore(Integer.parseInt(score.score));
                        voaText.progress = score.progress;
                        voaText.progress2 = score.progress2;
                    }
                }
                if (score.fluent != 0) {
                    //获取 流畅度
                    previewInfoBean.setSentenceScore(score.getParaId(), score.getScore());
                    previewInfoBean.setSentenceFluent(score.getParaId(), score.fluent);
                    previewInfoBean.setSentenceUrl(score.getParaId(), score.url);
                }
                if (score.endTime != 0) {
                    WavListItem item = new WavListItem();
                    item.setUrl(score.url);
                    item.setBeginTime(score.beginTime);
                    item.setEndTime(score.endTime);
                    item.setDuration(score.duration);
                    int paraId = Integer.parseInt(score.paraId);
                    item.setIndex(paraId);
                    buildMap(paraId, item);
                }
            }
        }

        for (VoaText voaText : voaTextList) {
            voaText.setVoaId(mTalkLesson.voaId());
        }
        mVoaTextList = voaTextList;
        //mVideoView.setOnPreparedListener(mOnPreparedListener);
        mAdapter.setList(voaTextList);
        mAdapter.mEvaluateNum = list.size();
        mAdapter.notifyDataSetChanged();
        //studyRecordUpdateUtil.getStudyRecord().setWordCount(voaTextList);
        previewInfoBean.setVoaTexts(voaTextList);
        //mPresenter.checkDraftExist(mTimeStamp);
    }

    @Override
    public void showEmptyTexts() {
        mAdapter.setList(Collections.<VoaText>emptyList());
        mAdapter.notifyDataSetChanged();
        mFlLoadError.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismissDubbingDialog() {

    }

    @Override
    public void showMergeDialog() {

    }

    @Override
    public void dismissMergeDialog() {

    }

    @Override
    public void startPreviewActivity() {
        previewInfoBean.initIndexList();
        //Record record = getDraftRecord();
        if (map == null || map.size() == 0) {
            showToast("textList 为空");
            return;
        }
        Intent intent = PreviewActivity.buildIntent(mVoaTextList, getActivity(), mTalkLesson, map,
                previewInfoBean, mTimeStamp, false);
        startActivity(intent);
    }

    @Override
    public void showToast(int resId) {
        ToastUtil.showToast(getActivity(), resId);
    }

    @Override
    public void showToast(String message) {
        ToastUtil.showToast(getActivity(), message);
    }

    @Override
    public void pause() {
        if (mVideoView.isPlaying()) {
            pauseVideoView();
        }
        try {

            if (mAccAudioPlayer.isPlaying()) {
                mAccAudioPlayer.pause();
            }
            if (mRecordPlayer.isPlaying()) {
                mRecordPlayer.pause();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
            ToastUtil.showToast(getActivity(), "播放器停止异常！");
        }
    }

    @Override
    public void showWord(WordResponse bean) {
        showWordView(bean);
    }

    @Override
    public void finish() {

    }

    @Override
    public void getEvaluateResponse(SendEvaluateResponse response, int paraId, File flakFile, int progress, int secondaryProgress) {
        WavListItem item = new WavListItem();
        item.setUrl(response.getURL());
        item.setBeginTime(mVoaTextList.get(paraId - 1).timing);
        if (paraId < mVoaTextList.size()) {
            item.setEndTime(mVoaTextList.get(paraId).timing);
        } else {
            item.setEndTime(mVoaTextList.get(paraId - 1).endTiming);
        }
        float duration = getAudioFileVoiceTime(flakFile.getAbsolutePath()) / 1000.0f;
        String temp = String.format("%.1f", duration);
        item.setDuration(Float.parseFloat(temp));//获取录音文件长度
        item.setIndex(paraId);
        buildMap(paraId, item);

        int score = (int) (Math.sqrt(Float.parseFloat(response.getTotal_score()) * 2000));
        DubDBManager dbManager = DubDBManager.getInstance();
        dbManager.setEvaluate(mTalkLesson.Id, String.valueOf(UserInfoManager.getInstance().getUserId()), String.valueOf(paraId), String.valueOf(score), progress, secondaryProgress);
        dbManager.setEvaluateTime(mTalkLesson.Id, String.valueOf(UserInfoManager.getInstance().getUserId()), String.valueOf(paraId), item.getBeginTime(), item.getEndTime(), item.getDuration());
        for (SendEvaluateResponse.WordsBean bean : response.getWords()) {
            dbManager.setEvWord(mTalkLesson.Id, String.valueOf(UserInfoManager.getInstance().getUserId()), String.valueOf(paraId), bean);
        }
        mNewScoreCallback.onResult(paraId, score, response);//开始计算

        int position = Math.max(paraId - 1, 0);
        if (!mVoaTextList.get(position).isEvaluate) {
            mAdapter.mEvaluateNum++;
            mVoaTextList.get(position).isEvaluate = true;
        }
        mVoaTextList.get(position).isDataBase = false;
    }

    @Override
    public void evaluateError(String message) {
        showToast(message);
        mAdapter.notifyDataSetChanged();
    }

    /******************回调接口******************/
    interface NewScoreCallback {
        void onResult(int pos, int score, SendEvaluateResponse beans);

        void onError(String errorMessage);
    }

    void setNewScoreCallback(NewScoreCallback mNewScoreCallback) {
        this.mNewScoreCallback = mNewScoreCallback;
    }

    /**************************事件************************/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadFinish(DownloadEvent downloadEvent) {
        switch (downloadEvent.status) {
            case DownloadEvent.Status.FINISH:
                DubDBManager.getInstance().setDownload(mTalkLesson.Id, String.valueOf(UserInfoManager.getInstance().getUserId()), mTalkLesson.Title,
                        mTalkLesson.DescCn, mTalkLesson.Pic, mTalkLesson.series);
                showLoadingByProgress(false,false,"","");
                setVideoAndAudio();
                break;
            case DownloadEvent.Status.DOWNLOADING:
                showLoadingByProgress(true,true,downloadEvent.msg,"");
                break;
            case DownloadEvent.Status.ERROR:
                showLoadingByProgress(true,false,"下载出错，文件内容缺失，请重新下载~","重新下载");
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(VipChangeEvent event){
        showMsgByStatus();
    }

    /*******************辅助功能***************************/
    //展示加载
//    private void showLoading(String msg){
//        if (loadingDialog==null){
//            loadingDialog = new LoadingDialog(getActivity());
//        }
//        loadingDialog.setMessage(msg);
//        if (!loadingDialog.isShowing()){
//            loadingDialog.show();
//        }
//    }
//
//    //关闭加载
//    private void closeLoading(){
//        if (loadingDialog!=null&&loadingDialog.isShowing()){
//            loadingDialog.dismiss();
//        }
//    }

    //判断数据显示状态
    private void showMsgByStatus(){
        if (!UserInfoManager.getInstance().isLogin()){
            showLayout.setVisibility(View.VISIBLE);
            showLoading.setVisibility(View.GONE);
            showMsg.setText("当前功能需要登录后使用，请先登录~");
            showBtn.setText("登录账号");
            return;
        }

        if (!XXPermissions.isGranted(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO)){
            showLayout.setVisibility(View.VISIBLE);
            showLoading.setVisibility(View.GONE);
            showMsg.setText("当前功能需要开启 存储权限 和 录音权限 ,请授权此权限后使用~");
            showBtn.setText("申请权限");
            return;
        }

        checkVideoAndMedia(true);
    }

    //加载数据
    private void showLoadingByProgress(boolean isShow,boolean isLoading,String msg,String btn){
        if (isShow){
            showLayout.setVisibility(View.VISIBLE);
            if (isLoading){
                showLoading.setVisibility(View.VISIBLE);
                showBtn.setVisibility(View.GONE);
                showMsg.setText(msg);
            }else {
                showLoading.setVisibility(View.GONE);
                showBtn.setVisibility(View.VISIBLE);
                showMsg.setText(msg);

                if (TextUtils.isEmpty(btn)){
                    showBtn.setVisibility(View.GONE);
                }else {
                    showBtn.setVisibility(View.VISIBLE);
                    showBtn.setText(btn);
                }
            }
        }else {
            showLayout.setVisibility(View.GONE);
            showLoading.setVisibility(View.GONE);
            showBtn.setVisibility(View.VISIBLE);
        }
    }


    //根据接口获取数据（先从数据库中获取，没有的话再从接口中获取存在数据库中）
    //加载口语秀的接口(该项目经过多人之手，逻辑太过于混乱，还不如重新写呢)
    //http://apps.iyuba.cn/iyuba/getTitleBySeries.jsp?type=title&seriesid=278&sign=22a9b2432482141d478e15f1a9caafd4
    private void getData(){
        mTalkLesson = TalkShowDBManager.getInstance().findTalkByVoaId(getArguments().getString(key_voaId));
        if (mTalkLesson==null){
            showLoadingByProgress(true,true,"正在加载配音内容",null);
            loadTalkShowData();
        }else {
            init();
        }
    }

    private void loadTalkShowData(){
        int bookId = ConceptBookChooseManager.getInstance().getBookId();
        DataManager.getInstance().getTalkLesson(String.valueOf(bookId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<TalkLesson>>() {
                    @Override
                    public void accept(List<TalkLesson> talkLessons) throws Exception {
                        if (talkLessons != null && talkLessons.size() > 0) {
                            TalkShowDBManager.getInstance().saveData(talkLessons);
                        }

                        mTalkLesson = TalkShowDBManager.getInstance().findTalkByVoaId(getArguments().getString(key_voaId));
                        if (mTalkLesson!=null){
                            init();
                        }else {
                            showLoadingByProgress(true,false,"加载配音内容失败","重新加载");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        showLoadingByProgress(true,false,"加载配音内容错误","重新加载");
                    }
                });
    }
}
