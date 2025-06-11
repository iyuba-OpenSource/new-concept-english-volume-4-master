package com.iyuba.core.talkshow.lesson.watch.comment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.iyuba.core.common.activity.login.LoginUtil;
import com.iyuba.core.common.data.model.Comment;
import com.iyuba.core.common.data.model.TalkLesson;
import com.iyuba.core.common.data.remote.IntegralService;
import com.iyuba.core.common.util.PermissionUtilConcept;
import com.iyuba.core.common.util.Recorder;
import com.iyuba.core.common.util.StorageUtil;
import com.iyuba.core.common.util.TimeUtil;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.event.StopEvent;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.talkshow.dub.DubbingActivity;
import com.iyuba.core.talkshow.dub.preview.Share;
import com.iyuba.lib.R;
import com.iyuba.module.headlinetalk.ui.widget.LoadingDialog;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import timber.log.Timber;

public class CommentFragment extends Fragment implements CommentMvpView {
    private static final String TAG = CommentFragment.class.getSimpleName();

    private static final int REQUEST_PERMISSION_CODE_AUDIO = 10;

    private static final String VOA = "voa";
    private static final String RANKING_ID = "ranking_id";
    private static final String RANKING_NAME = "ranking_name";
    private static final String RANKING_URL = "ranking_url";
    public static final int PAGE_NUM = 1;
    public static final int PAGE_SIZE = 20;

    private static final int REQUEST_RECORD_PERMISSION = 1;

    private TalkLesson mVoa;
    private int mRankingId;
    private String mRankingURL;
    private String mRankingName;
    private int pageNum = 1;

    MyRelativeLayout mRootLayout;
    TextView mCommentTitleTv;
    SwipeRefreshLayout mRefreshLayout;
    RecyclerView mRecyclerView;
    CommentInputView mCommentInput;
    View mDubbingView;
    MicVolumeView mVolumeView;
    TextView mShareTv;
    View mEmptyView;
    TextView mEmptyTextTv;
    View mLoadingLayout;
    TextView mTvComment;

    CommentPresenter mPresenter;

    CommentAdapter mAdapter;

    MediaPlayer mMediaPlayer;
    Recorder mRecorder;
    private String mRecorderFilePath;

    boolean mIsShowInputMethod;
    private LoadingDialog mLoadingDialog;

    private Context mContext;

    private Activity mActivity;

    private PermissionUtilConcept.Callback AudioCallback=new PermissionUtilConcept.Callback() {
        @Override
        public void agreeP() {
            agreedPermission();
        }

        @Override
        public void notAgreeP() {
            Timber.d("wangwenyang not agree per");
            Toast.makeText(mContext,"需要同意相关权限才能使用该功能",Toast.LENGTH_SHORT).show();
        }
    };

    public static CommentFragment newInstance(TalkLesson voa, int rankingId, String rankName, String rankUrl) {
        CommentFragment fragment = new CommentFragment();
        Bundle args = new Bundle();
        args.putParcelable(VOA, voa);
        args.putInt(RANKING_ID, rankingId);
        args.putString(RANKING_NAME, rankName);
        args.putString(RANKING_URL, rankUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //fragmentComponent().inject(this);
        mPresenter = new CommentPresenter();
        mContext = getContext();
        mActivity=getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment, container, false);
        initView(view);
        initClick(view);
        //mPresenter.attachView(this);

        init();
        initRecyclerView();
        initCommentInputView();
        if (getArguments() != null) {
            mVoa = getArguments().getParcelable(VOA);
            mRankingId = getArguments().getInt(RANKING_ID);
            mRankingName = getArguments().getString(RANKING_NAME);
            mRankingURL = getArguments().getString(RANKING_URL);
            showCommentLoadingLayout();
            //mPresenter.loadComment(mVoa.voaId(), mRankingId, PAGE_NUM, PAGE_SIZE);
        }
        return view;
    }

    private void initView(View view){
        mRootLayout = view.findViewById(R.id.root_layout);
        mCommentTitleTv = view.findViewById(R.id.comment_title_tv);
        mRefreshLayout = view.findViewById(R.id.refresh_layout);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mCommentInput = view.findViewById(R.id.comment_input_view);
        mDubbingView = view.findViewById(R.id.dubbing_view);
        mVolumeView = view.findViewById(R.id.mic_volume_view);
        mShareTv = view.findViewById(R.id.share_tv);
        mEmptyView = view.findViewById(R.id.empty_view);
        mEmptyTextTv = view.findViewById(R.id.empty_text);
        mLoadingLayout = view.findViewById(R.id.loading_layout);
        mTvComment = view.findViewById(R.id.comment_tv);
    }

    private void initClick(View view){
        view.findViewById(R.id.dubbing_tv).setOnClickListener(v->{
            String mDir = StorageUtil.getMediaDir(mContext, mVoa.voaId()).getAbsolutePath();
            if (mVoa.Title == null && mVoa.DescCn == null && !StorageUtil.checkFileExist(mDir, mVoa.voaId())) {
                ToastUtil.showToast(mContext, "请先下载课程！");
            } else {
                //请求权限，跳转到配音页面
                PermissionUtilConcept.getInstance().checkPermission(mActivity,
                        mContext,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        AudioCallback,
                        REQUEST_PERMISSION_CODE_AUDIO);
            }
        });
        view.findViewById(R.id.share_tv).setOnClickListener(v->{
            IntegralService integralService = IntegralService.Creator.newIntegralService();
            Share.prepareDubbingMessage(getContext(), mVoa, mRankingId, mRankingName, integralService,
                    UserInfoManager.getInstance().getUserId());
        });
        view.findViewById(R.id.comment_tv).setOnClickListener(v->{
            if (UserInfoManager.getInstance().isLogin()) {
                mDubbingView.setVisibility(View.GONE);
                mCommentInput.setVisibility(View.VISIBLE);
                mCommentInput.initState();
            } else {
                LoginUtil.startToLogin(getActivity());
            }
        });
        view.findViewById(R.id.comment_title_tv).setOnClickListener(v->{
            if (mIsShowInputMethod) {
                toggleInputMethod(false);
            } else {
                if (mDubbingView.getVisibility() == View.GONE) {
                    mDubbingView.setVisibility(View.VISIBLE);
                    mCommentInput.setVisibility(View.GONE);
                }
            }
        });
        view.findViewById(R.id.empty_layout).setOnClickListener(v->{
            if (mIsShowInputMethod) {
                toggleInputMethod(false);
            } else {
                if (mDubbingView.getVisibility() == View.GONE) {
                    mDubbingView.setVisibility(View.VISIBLE);
                    mCommentInput.setVisibility(View.GONE);
                }
            }
        });
    }

    private void init() {
        mRecorder = new Recorder();
        mMediaPlayer = new MediaPlayer();
        mLoadingDialog = new LoadingDialog(getContext());
        mLoadingDialog.create();
        mRootLayout.setOnResizeListener(new MyRelativeLayout.OnResizeListener() {
            @Override
            public void OnResize(int w, int h, int oldw, int oldh) {
                mIsShowInputMethod = h < oldh;
            }
        });
        int shareSize = (int) getResources().getDimension(R.dimen.comment_share_image_size);
        setTextDrawable(mShareTv, R.drawable.ic_share_yellow, shareSize, shareSize);
    }

    public void setTextDrawable(TextView textView, int resId, int width, int height) {
        Drawable drawable = ContextCompat.getDrawable(getActivity(), resId);
        drawable.setBounds(0, 0, width, height);
        textView.setCompoundDrawables(drawable, null, null, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
        mTvComment.setVisibility(View.GONE);
        mLoadingDialog.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
    }

    public void initCommentInputView() {
        mCommentInput.setOnCommentSendListener(mSendListener);
        mCommentInput.setInputMethodCallback(new CommentInputView.InputMethodCallback() {

            @Override
            public void show() {
                toggleInputMethod(true);
            }

            @Override
            public void hide() {
                toggleInputMethod(false);
            }
        });
        try {
            mRecorderFilePath = StorageUtil.getCommentVoicePath(getContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCommentInput.setRecordFilePath(mRecorderFilePath);
        mCommentInput.setRecorderListener(mRecorderListener);
        mCommentInput.setRequestPermissionCallback(mPermissionCB);
        mCommentInput.setMediaPlayer(mMediaPlayer);
        mCommentInput.setRecorder(mRecorder);
    }

    public void initRecyclerView() {
        mRefreshLayout.setOnRefreshListener(mOnRefreshListener);
//        mAdapter.setCallback(callback);
//        mAdapter.setMediaPlayer(mMediaPlayer);
//        mAdapter.setOnReplyListener(mOnReplyListener);
//        mAdapter.setOnItemClickListener(new CommentAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick() {
//
//            }
//        });
//           mRecyclerView.setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        mRecyclerView.addItemDecoration(new LinearItemDivider(getActivity(), LinearItemDivider.VERTICAL_LIST));
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。
        mRecyclerView.addOnScrollListener(mOnScrollListener);
    }

//    private CommentAdapter.OnReplyListener mOnReplyListener = new CommentAdapter.OnReplyListener() {
//        @Override
//        public void onReply(String targetUsername) {
//            mDubbingView.setVisibility(View.GONE);
//            mCommentInput.setVisibility(View.VISIBLE);
//            mCommentInput.replyToSomeone(targetUsername);
//        }
//    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        // mPresenter.detachView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //mPresenter.detachView();
        if (mMediaPlayer != null) mMediaPlayer.release();
        if (mRecorder != null) mRecorder.release();
//        mCommentInput.deleteRecordFile();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStopEvent(StopEvent stopEvent) {
        switch (stopEvent.source) {
            case StopEvent.SOURCE.VOICE:
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                }
                break;
            default:
                break;
        }
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onCommentEvent(CommentEvent event) {
//        switch (event.status) {
//            case CommentEvent.Status.GONE:
//                onClickOthers();
//                break;
//            default:
//                break;
//        }
//    }

    private void toggleInputMethod(boolean show) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (mIsShowInputMethod) {
            if (!show) inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        } else {
            if (show) inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 刷新监听
     */
    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {
//                    mPresenter.loadComment(mVoa.voaId(), mRankingId, PAGE_NUM, PAGE_SIZE);
                }
            });
        }
    };

    /**
     * 加载更多
     */
    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (!recyclerView.canScrollVertically(1)) {// 手指不能向上滑动了
                // TODO 这里有个注意的地方，如果你刚进来时没有数据，但是设置了适配器，
                // TODO 这个时候就会触发加载更多，需要开发者判断下是否有数据，如果有数据才去加载更多。
                pageNum++;
                // mPresenter.loadComment(mVoa.voaId(), mRankingId, pageNum, PAGE_SIZE);
            }
        }
    };

    private void agreedPermission() {
        EventBus.getDefault().post(new StopEvent(StopEvent.SOURCE.VIDEO));
        EventBus.getDefault().post(new StopEvent(StopEvent.SOURCE.VOICE));
        //mPresenter.getVoa(mVoa.voaId());
        long timestamp = TimeUtil.getTimeStamp();
        Intent intent = DubbingActivity.buildIntent(getActivity(), mVoa, timestamp);
        startActivity(intent);
    }

    @Override
    public void showComments(List<Comment> commentList) {
        mEmptyView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        //mAdapter.setList(commentList);
        //mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showEmptyComment() {
        mEmptyTextTv.setText("此配音还没有\n评论，快来抢\n占沙发吧~");
        mEmptyView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void showToast(int id) {
        Toast.makeText(getContext(), id, Toast.LENGTH_LONG).show();
    }

    @Override
    public void clearInputText() {
        mCommentInput.clearInputText();
    }

    @Override
    public void setCommentNum(int num) {
        mCommentTitleTv.setText(MessageFormat.format(getString(R.string.comment_num), num));
    }

    @Override
    public void startDubbingActivity(TalkLesson voa) {
        long timestamp = TimeUtil.getTimeStamp();
        Intent intent = DubbingActivity.buildIntent(getActivity(), voa, timestamp);
        startActivity(intent);
    }

    @Override
    public void showLoadingDialog() {
        //mLoadingDialog.show();
    }

    @Override
    public void dismissLoadingDialog() {
        mLoadingDialog.dismiss();
    }

    @Override
    public void showCommentLoadingLayout() {
        //mLoadingLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismissCommentLoadingLayout() {
        mLoadingLayout.setVisibility(View.GONE);
    }

    @Override
    public void dismissRefreshingView() {
        mRefreshLayout.setRefreshing(false);
    }

    private CommentInputView.RequestPermissionCallback mPermissionCB = new CommentInputView.RequestPermissionCallback() {
        @Override
        public void requestRecordPermission() {
//            MPermissions.requestPermissions(CommentFragment.this, REQUEST_RECORD_PERMISSION,
//                    Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    };

    //@PermissionGrant(REQUEST_RECORD_PERMISSION)
    void onRecordPermissionGranted() {
        mCommentInput.onRecordPermissionGranted();
    }

    //@PermissionDenied(REQUEST_RECORD_PERMISSION)
    void onRecordPermissionDenied() {
        Toast.makeText(getContext(), "Record Permission Denied! Can't make audio comment", Toast
                .LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CODE_AUDIO){
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    PermissionUtilConcept.getInstance().checkPermissionResult(grantResults,AudioCallback);
                }
            });
        }
    }

    private CommentInputView.RecorderListener mRecorderListener = new CommentInputView.RecorderListener() {
        @Override
        public void onBegin() {
            mVolumeView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onVolumeChanged(int db) {
            mVolumeView.setVolume(db);
        }

        @Override
        public void onEnd() {
            mVolumeView.setVisibility(View.GONE);
        }

        @Override
        public void onError() {
            mVolumeView.setVisibility(View.GONE);
        }
    };

    private CommentInputView.OnCommentSendListener mSendListener = new CommentInputView.OnCommentSendListener() {

        @Override
        public void onTextSend(String comment) {
            showLoadingDialog();
            //mPresenter.senTextComment(mVoa.voaId(), mRankingId, comment);
        }

        @Override
        public void onVoiceSend(File record) {
            showLoadingDialog();
            //mPresenter.sendVoiceComment(mVoa.voaId(), mRankingId, record);
        }
    };
}
