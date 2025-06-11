package com.iyuba.conceptEnglish.study;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.iyuba.configation.RuntimeManager.getSystemService;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.adapter.CommentListAdapterNew2;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.ConceptDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Concept_comment;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.rxjava2.RxUtil;
import com.iyuba.conceptEnglish.manager.RecordManager;
import com.iyuba.conceptEnglish.manager.VoaDataManager;
import com.iyuba.conceptEnglish.protocol.ExpressionRequest;
import com.iyuba.conceptEnglish.util.NetWorkState;
import com.iyuba.conceptEnglish.util.UtilPostFile;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.activity.login.LoginUtil;
import com.iyuba.core.common.network.ClientSession;
import com.iyuba.core.common.network.IResponseReceiver;
import com.iyuba.core.common.protocol.BaseHttpRequest;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.view.PermissionMsgDialog;
import com.iyuba.multithread.util.NetStatusUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class StudyCommentFragment extends Fragment implements CommentListAdapterNew2.OnItemClickListener {

    @BindView(R.id.btn_rank_refresh_comment)
    Button btnRankRefreshComment;
    @BindView(R.id.button_express)
    Button buttonExpress;

    @BindView(R.id.editText_express)
    EditText editTextExpress;
    @BindView(R.id.edittext)
    RelativeLayout edittext;
    private String expressWord;
    @BindView(R.id.fail_rela_comment)
    RelativeLayout failRelaComment;


    @BindView(R.id.list_comment)
    RecyclerView listComment;

    @BindView(R.id.mic_icon)
    ImageView micIcon;
    @BindView(R.id.mic_value)
    ImageView micValue;
    @BindView(R.id.plsay)
    TextView plsay;
    @BindView(R.id.press_speak)
    Button pressSpeak;
    @BindView(R.id.setmode)
    ImageButton setmode;
    @BindView(R.id.swiperefresh_comment)
    SmartRefreshLayout swiperefreshComment;
    @BindView(R.id.test_listen)
    Button testListen;
    @BindView(R.id.voice_view)
    RelativeLayout voiceView;
    @BindView(R.id.voicebutton)
    LinearLayout voicebutton;

    private Context mContext;
//    private ConceptViewModel conceptViewModel;
    private AlertDialog.Builder dialog;


    private MediaPlayer voiceMediaPlayer;

    //更换数据类型
//    private CommentListAdapterNew commentAdapter;
    private CommentListAdapterNew2 commentAdapter;

    private boolean commentAll = false;

    //private ArrayList<Comment> comments = new ArrayList();
    private int curCommentPage = 1;
    private int currMode = 1;
    private boolean isDownloadAll;
    private boolean isUploadVoice = false;
    private RecordManager rManager;
    private View rootView;
    Unbinder unbinder;

    //权限说明弹窗
    private PermissionMsgDialog msgDialog;

    public Handler commentHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if ((getActivity() != null && getActivity().isDestroyed()) || !isAdded()) {
                return;
            }
            switch (msg.what) {
                case 0:
                    //更换成新的数据
//                    new Thread(new commentThread()).start();
                    if (curCommentPage==1){
                        swiperefreshComment.autoRefresh();
                    }else {
                        swiperefreshComment.autoLoadMore();
                    }
                    break;
                case 1:// 发表评论
                    editTextExpress.setText("");
                    String userId = String.valueOf(UserInfoManager.getInstance().getUserId());
                    String username = UserInfoManager.getInstance().getUserName();
                    int voaId = VoaDataManager.Instace().voaTemp.voaId;
                    ClientSession.Instace().asynGetResponse(new ExpressionRequest(userId, String.valueOf(voaId), expressWord, username), new IResponseReceiver() {
                        @Override
                        public void onResponse(BaseHttpResponse response, BaseHttpRequest request, int rspCookie) {
                            waitingDialog.dismiss();
                            commentHandler.sendEmptyMessageDelayed(4, 1000);
                        }
                    });
                    break;
                case 3:
                    commentAll = false;
                    commentHandler.sendEmptyMessage(0);
                    break;
                case 4:
                    curCommentPage = 1;
                    commentHandler.sendEmptyMessage(3);
                    break;
                case 5:
                    failRelaComment.setVisibility(View.GONE);
                    commentAdapter.notifyDataSetChanged();
                    break;
                case 10:
                    String addscore = String.valueOf(msg.arg1);
                    if (addscore.equals("5")) {
                        String mg = "语音成功发送至评论区，恭喜您获得了" + addscore + "分";
                        CustomToast.showToast(mContext, mg, 3000);
                    } else {
                        String mg = "语音成功发送至评论区";
                        CustomToast.showToast(mContext, mg, 3000);
                    }
                    commentAdapter.notifyDataSetChanged();
                    break;
                case 11:
                    CustomToast.showToast(mContext, "请录音后，再发送。", 3000);
                    if (waitingDialog.isShowing()) waitingDialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    };

    // 以下评论
    private BroadcastReceiver rpl = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (currMode == 1) {
                switchToText();
                editTextExpress.findFocus();
                editTextExpress.requestFocus();
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(editTextExpress, 0);
                editTextExpress.setText(getResources().getString(R.string.reply) + intent.getExtras().getString("username") + ":");
                editTextExpress.setSelection(editTextExpress.length());
            } else if (currMode == 0) {
                switchToVoice();
                try {
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };


    Handler voice_handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    voiceView.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    voiceView.setVisibility(View.GONE);
                    break;
            }
        }
    };


    private OnClickListener voice_ocl = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.setmode:
                    if (currMode == 1) {

                        voicebutton.setVisibility(View.GONE);
                        edittext.setVisibility(View.VISIBLE);
                        currMode = 0;
                        setmode.setBackgroundResource(R.drawable.chatting_setmode_voice_btn);
                    } else {
                        voicebutton.setVisibility(View.VISIBLE);
                        edittext.setVisibility(View.GONE);
                        currMode = 1;
                        setmode.setBackgroundResource(R.drawable.chatting_setmode_keyboard_btn);
                    }
                    break;
                case R.id.test_listen:
                    if (voiceMediaPlayer.isPlaying()) {
                        voiceMediaPlayer.stop();
                    }
                    voiceMediaPlayer.reset();
                    try {
                        voiceMediaPlayer.setDataSource(Constant.voiceCommentAddr);
                        voiceMediaPlayer.prepareAsync();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private OnTouchListener voice_otl = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (v.getId() == R.id.press_speak) {

                //权限说明弹窗
                List<Pair<String, Pair<String,String>>> pairList = new ArrayList<>();
                pairList.add(new Pair<>(Manifest.permission.RECORD_AUDIO,new Pair<>("麦克风权限","录制评测时朗读的音频，用于评测打分使用")));
                // TODO: 2025/4/11 根据android版本处理
                if (Build.VERSION.SDK_INT<35){
                    pairList.add(new Pair<>(Manifest.permission.WRITE_EXTERNAL_STORAGE,new Pair<>("存储权限","保存评测的音频文件，用于评测打分使用")));
                }

                msgDialog = new PermissionMsgDialog(mContext);
                msgDialog.showDialog(null, pairList, true, new PermissionMsgDialog.OnPermissionApplyListener() {
                    @Override
                    public void onApplyResult(boolean isSuccess) {
                        if (isSuccess){
                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                voice_handler.sendEmptyMessageDelayed(0, 300);
                                // setTextPause(true);
                                // 录音
                                try {
                                    File file = new File(Constant.voiceCommentAddr);
                                    rManager = new RecordManager(file, micValue);
                                    rManager.startRecord();
                                } catch (Exception e) {
                                    Log.e("onTouch", e.getMessage());
                                }
                            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                                // comment.findViewById(R.id.voice_view).setVisibility(
                                // View.VISIBLE);
                                // comment.findViewById(R.id.voice_view)
                                // setTextPause(false);
                                voice_handler.sendEmptyMessageDelayed(1, 300);
                                rManager.stopRecord();
                                ToastUtil.showToast(mContext, "音频已生成，请点击发送");
                            }
                        }
                    }
                }).show();

                /*StudyNewActivityPermissionsDispatcher.initStorageWithPermissionCheck((StudyNewActivity) mContext);
                if (!permissions.dispatcher.PermissionUtils.hasSelfPermissions(mContext, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.RECORD_AUDIO})) {
                    return true;
                }*/
            } else {
                // comment.findViewById(R.id.voice_view)
                voiceView.setVisibility(View.VISIBLE);
                // comment.findViewById(R.id.voice_view)
                voiceView.setVisibility(View.GONE);
                testListen.setVisibility(View.VISIBLE);
                rManager.stopRecord();
            }

            return true;
        }
    };

    private CustomDialog waitingDialog;

    private boolean CheckNetWork() {
        return (NetWorkState.isConnectingToInternet()) && (NetWorkState.getAPNType() != 1);
    }

    private void initComment() {
        if (this.voiceMediaPlayer == null) {
            this.voiceMediaPlayer = new MediaPlayer();
        }
        this.voiceMediaPlayer.setAudioStreamType(3);
        this.voiceMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                voiceMediaPlayer.start();
            }
        });
        testListen.setOnClickListener(voice_ocl);
        setmode.setOnClickListener(voice_ocl);
        pressSpeak.setOnTouchListener(voice_otl);
        commentAdapter = new CommentListAdapterNew2(getActivity(), 0, StudyCommentFragment.this);
        listComment.setLayoutManager(new LinearLayoutManager(this.mContext));
        listComment.setAdapter(this.commentAdapter);
        swiperefreshComment.setOnRefreshListener(new OnRefreshListener() {
            public void onRefresh(RefreshLayout refreshLayout) {
                if (!NetStatusUtil.isConnected(mContext)) {
                    refreshLayout.finishRefresh();
                    return;
                }

//                commentHandler.sendEmptyMessage(4);
                curCommentPage = 1;
                getCommentData(false);
            }
        });
        swiperefreshComment.setOnLoadMoreListener(new OnLoadMoreListener() {
            public void onLoadMore(RefreshLayout refreshLayout) {
                if (!NetStatusUtil.isConnected(mContext)) {
                    refreshLayout.finishLoadMore();
                    return;
                }
                if (!isDownloadAll) {
                    //直接操作
//                    commentHandler.sendEmptyMessage(0);
                    getCommentData(true);
                } else {
                    ToastUtil.showToast(StudyCommentFragment.this.mContext, "已加载全部数据");
                }
            }
        });
        listComment.setAdapter(commentAdapter);

        //这里直接刷新操作
        commentHandler.sendEmptyMessage(4);

        btnRankRefreshComment.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //刷新操作
                swiperefreshComment.autoRefresh();
            }
        });
    }

    // 评论
    private void initExpression() {
        if (UserInfoManager.getInstance().isLogin()) {
            editTextExpress.setHint(getResources().getString(R.string.hint1));
            buttonExpress.setText(getResources().getString(R.string.send));
            editTextExpress.setFocusableInTouchMode(true);

            buttonExpress.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (CheckNetWork()) {
                        if (currMode == 0) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(editTextExpress.getWindowToken(), 0);
                            String expressionInput = editTextExpress.getText().toString();
                            if (expressionInput.toString().equals("")) {
                                CustomToast.showToast(mContext, R.string.study_input_comment, 1000);
                            } else {
                                waitingDialog.show();
                                expressWord = expressionInput;
                                commentHandler.sendEmptyMessage(1);
                            }
                        } else {
                            if (isUploadVoice) {
                                CustomToast.showToast(mContext, "评论发送中，请不要重复提交", 1000);
                            } else {
                                waitingDialog.show();
                                Thread thread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Map<String, String> textParams = new HashMap<String, String>();
                                        Map<String, File> fileParams = new HashMap<String, File>();
                                        File file = new File(Constant.voiceCommentAddr);
                                        fileParams.put("content.acc", file);
                                        if (file != null && file.exists()) {
                                            try {
                                                isUploadVoice = true;
                                                String response = UtilPostFile.post("http://daxue." + Constant.IYUBA_CN + "appApi/UnicomApi?" + "&platform=android&format=json&protocol=60003" + "&userid=" + UserInfoManager.getInstance().getUserId() + "&voaid=" + VoaDataManager.Instace().voaTemp.voaId + "&shuoshuotype=1" + "&appName=concept", textParams, fileParams);
                                                isUploadVoice = false;
                                                JSONObject jsonObjectRoot;
                                                jsonObjectRoot = new JSONObject(response);
                                                String result = jsonObjectRoot.getInt("ResultCode") + "";
                                                String addscore = jsonObjectRoot.getString("AddScore");
                                                // TODO
                                                if (result.equals("1")) {
                                                    waitingDialog.dismiss();
                                                    Looper.prepare();
                                                    if (addscore.equals("5"))
                                                        CustomToast.showToast(mContext, "评论成功，恭喜您获得了" + addscore + "分", 3000);

                                                    commentHandler.sendEmptyMessage(4);

                                                    file.delete();
                                                }
                                            } catch (IOException e) {
                                                isUploadVoice = false;
                                                e.printStackTrace();
                                            } catch (JSONException e) {
                                                isUploadVoice = false;
                                                e.printStackTrace();
                                            }
                                        } else {
                                            commentHandler.sendEmptyMessage(11);
                                        }
                                    }
                                });
                                thread.start();
                            }
                        }
                    } else {
                        Toast.makeText(mContext, "请在有网的状态下发送。", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        } else {
            editTextExpress.setHint(getResources().getString(R.string.hint3));
            editTextExpress.setFocusableInTouchMode(false);
            buttonExpress.setText(getResources().getString(R.string.login));
            buttonExpress.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    LoginUtil.startToLogin(mContext);
                }
            });
        }
    }

    private void switchToText() {
        this.voicebutton.setVisibility(View.GONE);
        this.edittext.setVisibility(View.VISIBLE);
        this.setmode.setBackgroundResource(R.drawable.chatting_setmode_voice_btn);
        this.currMode = 0;
    }

    private void switchToVoice() {
        this.voicebutton.setVisibility(View.VISIBLE);
        this.edittext.setVisibility(View.GONE);
        this.setmode.setBackgroundResource(R.drawable.chatting_setmode_keyboard_btn);
        this.currMode = 1;
    }


    public void onCreate(@Nullable Bundle paramBundle) {
        super.onCreate(paramBundle);
        this.mContext = getActivity();
    }

    @Nullable
    public View onCreateView(@NonNull LayoutInflater paramLayoutInflater, @Nullable ViewGroup paramViewGroup, @Nullable Bundle paramBundle) {
        if (rootView == null) {
            rootView = paramLayoutInflater.inflate(R.layout.comment, paramViewGroup, false);
        }
        waitingDialog = WaittingDialog.showDialog(this.mContext);
        this.unbinder = ButterKnife.bind(this, this.rootView);
        initExpression();
        initComment();
        mContext.registerReceiver(rpl, new IntentFilter("toreply"));
        voiceView.setVisibility(View.GONE);
        return this.rootView;
    }

    public void onDestroyView() {
        super.onDestroyView();
        this.unbinder.unbind();
        try {
            mContext.unregisterReceiver(rpl);
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (msgDialog!=null){
            msgDialog.dismiss();
        }
    }

    @Override
    public void onItemClick(int paramInt) {
        String uid = String.valueOf(UserInfoManager.getInstance().getUserId());
        Concept_comment.Row item = commentAdapter.getData().get(paramInt);
        if (!uid.equals(item.Userid)) {
            return;
        }
        if (dialog == null) {
            dialog = new AlertDialog.Builder(mContext);
        }

        // TODO: 2025/3/14  暂时未使用，关闭
        /*if (conceptViewModel == null) {
            conceptViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(ConceptViewModel.class);
        }*/

        dialog.setTitle("警告")
                .setPositiveButton("取消", null)
                .setMessage("此操作将删除你的评论且无法撤回，是否继续?")
                .setNegativeButton("确定", (d, i) -> {
                    StringBuilder hint = new StringBuilder().append("删除");

                    // TODO: 2025/3/14 暂时未使用，关闭
                    /*conceptViewModel.deleteComment(item.id, uid, response -> {
                        hint.append("成功");
                        ToastUtil.showToast(mContext, hint.toString());
                        commentAdapter.notifyItemRemoved(paramInt);
                        return null;
                    }, e -> {
                        hint.append("失败");
                        ToastUtil.showToast(mContext, hint.toString());
                        return null;
                    });*/
                })
                .show();
    }

    public void onResume() {
        super.onResume();
    }

    public void refreshData() {
        commentHandler.sendEmptyMessage(3);
    }

    public void stopCommentPlayer() {
        if ((voiceMediaPlayer != null) && (voiceMediaPlayer.isPlaying())) {
            voiceMediaPlayer.pause();
        }
    }

    // 获取评论
//    class commentThread implements Runnable {
//        @Override
//        public void run() {
//            int voaId = VoaDataManager.Instace().voaTemp.voaId;
//            ClientSession.Instace().asynGetResponse(new CommentRequest(String.valueOf(voaId), String.valueOf(curCommentPage)), new IResponseReceiver() {
//                @Override
//                public void onResponse(BaseHttpResponse response, BaseHttpRequest request, int rspCookie) {
//                    CommentResponse commentResponse = (CommentResponse) response;
//                    if (commentResponse.resultCode.equals("511")) {
//                        // comments.addAll();
//                        if (curCommentPage == 1) {
//                            commentAdapter.setData(commentResponse.Comments);
//                        } else {
//                            commentAdapter.addData(commentResponse.Comments);
//                        }
//                        commentHandler.sendEmptyMessage(5);
//                        if (commentResponse.Comments.size() != 0) {
//                            // curCommentPage<lastPage表示还有评论
//                            if (curCommentPage < Integer.parseInt(commentResponse.lastPage)) {
//                                isDownloadAll = false;
//                                curCommentPage += 1;
//                            } else {// curCommentPage ==lastPage 表示已经加载全部评论
//                                isDownloadAll = true;
//                            }
//                        }
//
//                    } else if (commentResponse.resultCode.equals("510")) {
//                        ToastUtil.showToast(mContext, commentResponse.message);
//                    }
//                }
//            }, null, null);
//        }
//    }

    @Override
    public void onPause() {
        super.onPause();
        voiceMediaPlayer.pause();
        pausePlay();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        voiceMediaPlayer.stop();
        voiceMediaPlayer.reset();
        voiceMediaPlayer.release();
        commentAdapter.stopPlay();
        RxUtil.unDisposable(getCommentDis);
    }

    public void pausePlay() {
        if (commentAdapter != null) {
            commentAdapter.pausePlay();
        }
    }

    //获取评论的数据
    private Disposable getCommentDis;

    private void getCommentData(boolean isLoadMore) {
        //固定数量-15
        int pageCount = 15;
        int voaId = VoaDataManager.Instace().voaTemp.voaId;

        ConceptDataManager.getConceptCommentData(String.valueOf(voaId), curCommentPage, pageCount)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Concept_comment>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        getCommentDis = d;
                    }

                    @Override
                    public void onNext(Concept_comment bean) {
                        if (bean != null) {
                            if (bean.ResultCode.equals("511")) {
                                //区分刷新或者加载
                                if (isLoadMore) {
                                    commentAdapter.addData(bean.row);
                                } else {
                                    commentAdapter.setData(bean.row);
                                }

                                if (bean.row.size() != 0) {
                                    // curCommentPage<lastPage表示还有评论
                                    if (curCommentPage < Integer.parseInt(bean.LastPage)) {
                                        isDownloadAll = false;
                                        curCommentPage += 1;
                                    } else {// curCommentPage ==lastPage 表示已经加载全部评论
                                        isDownloadAll = true;
                                    }
                                }
                            } else if (bean.ResultCode.equals("510")) {
                                ToastUtil.showToast(getActivity(), bean.Message);
                            } else {
                                ToastUtil.showToast(getActivity(), "暂无更多数据");
                            }
                        } else {
                            ToastUtil.showToast(getActivity(), "获取数据失败");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showToast(getActivity(), "获取数据失败");
                    }

                    @Override
                    public void onComplete() {
                        swiperefreshComment.finishRefresh();
                        swiperefreshComment.finishLoadMore();
                        RxUtil.unDisposable(getCommentDis);
                    }
                });
    }
}
