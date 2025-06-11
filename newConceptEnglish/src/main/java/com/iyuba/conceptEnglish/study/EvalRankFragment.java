package com.iyuba.conceptEnglish.study;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.activity.CommentActivity;
import com.iyuba.conceptEnglish.adapter.RankListAdapterNew;
import com.iyuba.conceptEnglish.databinding.FragmentEvalRankBinding;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.event.RefreshDataEvent;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.ConceptBookChooseManager;
import com.iyuba.conceptEnglish.manager.VoaDataManager;
import com.iyuba.conceptEnglish.protocol.GetRankInfoRequest;
import com.iyuba.conceptEnglish.protocol.GetRankInfoResponse;
import com.iyuba.conceptEnglish.sqlite.mode.RankUser;
import com.iyuba.conceptEnglish.widget.CircleImageView;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.activity.login.LoginUtil;
import com.iyuba.core.common.network.ClientSession;
import com.iyuba.core.common.network.IErrorReceiver;
import com.iyuba.core.common.network.INetStateReceiver;
import com.iyuba.core.common.network.IResponseReceiver;
import com.iyuba.core.common.protocol.BaseHttpRequest;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.ErrorResponse;
import com.iyuba.core.common.thread.GitHubImageLoader;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.event.VipChangeEvent;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.multithread.util.NetStatusUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 排行榜界面
 *
 * 这里注意一个严重的问题
 * 如果voaId错误，会导致整个学习界面的点击和界面刷新会错误
 * 目前不清楚为啥这样，可以考虑重写数据(未解决)
 */
public class EvalRankFragment extends Fragment {
    private static final String TAG = "EvalRankFragment";

    private RankListAdapterNew adapter;

    private RankUser champion = new RankUser();

    private boolean isFirst = true;
    private boolean isLastPage = false;
    private boolean isPrepared;
    protected boolean isVisible;
    private Matcher m;
    private INetStateReceiver mNetStateReceiver = new INetStateReceiver() {
        public void onCancel(BaseHttpRequest paramAnonymousBaseHttpRequest, int paramAnonymousInt) {
        }

        public void onConnected(BaseHttpRequest paramAnonymousBaseHttpRequest, int paramAnonymousInt) {
        }

        public void onNetError(BaseHttpRequest paramAnonymousBaseHttpRequest, int paramAnonymousInt, ErrorResponse paramAnonymousErrorResponse) {
            EvalRankFragment.this.rankHandler.sendEmptyMessage(2);
        }

        public void onRecv(BaseHttpRequest paramAnonymousBaseHttpRequest, int paramAnonymousInt1, int paramAnonymousInt2) {
        }

        public void onRecvFinish(BaseHttpRequest paramAnonymousBaseHttpRequest, int paramAnonymousInt) {
        }

        public void onSend(BaseHttpRequest paramAnonymousBaseHttpRequest, int paramAnonymousInt1, int paramAnonymousInt2) {
        }

        public void onSendFinish(BaseHttpRequest paramAnonymousBaseHttpRequest, int paramAnonymousInt) {
            Log.e("onSendFinish", "---");
        }

        public void onStartConnect(BaseHttpRequest paramAnonymousBaseHttpRequest, int paramAnonymousInt) {
            Log.e("onStartConncet", "---");
        }

        public void onStartRecv(BaseHttpRequest paramAnonymousBaseHttpRequest, int paramAnonymousInt1, int paramAnonymousInt2) {
            Log.e("onStartRecv", "---");
        }

        public void onStartSend(BaseHttpRequest paramAnonymousBaseHttpRequest, int paramAnonymousInt1, int paramAnonymousInt2) {
            Log.e("onStartSend", "---");
        }
    };
    private String myCount;
    private String myImgUrl;
    private String myName;
    private String myId;
    private String myRank;
    private String myScore;
    private Pattern p;
    private int pageNum = 20;

    //布局样式
    private FragmentEvalRankBinding binding;
    //是否是第一次数据
    private boolean isFirstLoadData = false;

    private List<RankUser> rankList = new ArrayList();

    private int startRank = 0;

    private int voaId;
    private CustomDialog waitingDialog;

    public static EvalRankFragment getInstance(){
        EvalRankFragment rankFragment = new EvalRankFragment();
        return rankFragment;
    }

    Handler rankHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (getActivity() ==null || getActivity().isDestroyed() || !isAdded()) {
                return;
            }

            switch (msg.what) {
                case 0:
                    try {
                        getCurrentArticleInfo();
                        String uid = String.valueOf(UserInfoManager.getInstance().getUserId());

                        final String currVoaId;
                        if (ConceptBookChooseManager.getInstance().getBookType().equals(TypeLibrary.BookType.conceptFourUS)
                                ||ConceptBookChooseManager.getInstance().getBookType().equals(TypeLibrary.BookType.conceptJunior)) {
                            currVoaId = String.valueOf(voaId);
                        } else {
                            currVoaId = String.valueOf(voaId * 10);
                        }

                        ClientSession.Instace().asynGetResponse(new GetRankInfoRequest(uid, Constant.EVAL_TYPE, currVoaId, startRank + "", pageNum + ""), new IResponseReceiver() {
                            @Override
                            public void onResponse(BaseHttpResponse response, BaseHttpRequest request, int rspCookie) {
                                GetRankInfoResponse tr = (GetRankInfoResponse) response;
                                adapter.setCurVoaId(currVoaId + "");
                                if (!TextUtils.isEmpty(tr.myName) && !"null".equals(tr.myName) && !"none".equals(tr.myName))
                                    myName = tr.myName;
                                else {
                                    myName = tr.uid;
                                }
                                myId = tr.uid;
                                myImgUrl = tr.myImgSrc;
                                myScore = tr.myScores;
                                myCount = tr.myCount;
                                myRank = tr.myRanking;
                                rankList.addAll(tr.rankUsers);

                                if (rankList.size() > 0) {
                                    champion = rankList.get(0);
                                }
                                if (rankList.size() < 20) {
                                    isLastPage = true;
                                }

                                //判断显示还是隐藏
                                if (rankList.size()>0){
                                    updateUi(false,null);
                                    rankHandler.sendEmptyMessage(1);
                                }else {
                                    if (isFirstLoadData){
                                        isFirstLoadData = false;
                                        updateUi(false,"未查询到当前课程的排行数据");
                                    }else {
                                        ToastUtil.showToast(getActivity(),"未查询到更多数据");
                                    }
                                }

                                Log.d(TAG, "加载数据显示--"+rankList.size());
                            }
                        }, new IErrorReceiver() {
                            @Override
                            public void onError(ErrorResponse errorResponse, BaseHttpRequest request, int rspCookie) {
                            }
                        }, mNetStateReceiver);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                case 1:
                    if (waitingDialog != null) {
                        waitingDialog.dismiss();
                    }

                    try {
                        binding.rankList.setVisibility(View.VISIBLE);
                        if (adapter != null) {
                            adapter.refreshData(rankList);
                        }
                        startRank = rankList.size();

                        //个人信息
                        if ("0".equals(myId)) {
                            binding.username.setText("未登录");
                        } else {
                            binding.username.setText(myName);
                            Glide.with(getActivity()).load(myImgUrl).placeholder(R.drawable.defaultavatar).error(R.drawable.defaultavatar).into(binding.myImage);
                        }
                        binding.rankInfo.setText(String.format("句子:%s,得分:%s,排名:%s", new Object[]{myCount, myScore, myRank}));

                        //第一名信息
                        if (champion.getRanking().equals("1")) {
                            int score;
                            try {
                                score = Integer.parseInt(EvalRankFragment.this.champion.getScores());
                            } catch (Exception paramAnonymousMessage) {
                                paramAnonymousMessage.printStackTrace();
                                score = 0;
                            }
                            setFirstImag(champion.getImgSrc(), champion.getName(), score, champion.getUid());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    private String getFirstChar(String name) {
        String subString;
        for (int i = 0; i < name.length(); i++) {
            subString = name.substring(i, i + 1);

            p = Pattern.compile("[0-9]*");
            m = p.matcher(subString);
            if (m.matches()) {
//                Toast.makeText(Main.this,"输入的是数字", Toast.LENGTH_SHORT).show();
                return subString;
            }

            p = Pattern.compile("[a-zA-Z]");
            m = p.matcher(subString);
            if (m.matches()) {
//                Toast.makeText(Main.this,"输入的是字母", Toast.LENGTH_SHORT).show();
                return subString;
            }

            p = Pattern.compile("[\u4e00-\u9fa5]");
            m = p.matcher(subString);
            if (m.matches()) {
//                Toast.makeText(Main.this,"输入的是汉字", Toast.LENGTH_SHORT).show();
                return subString;
            }
        }

        return "A";
    }

    private void initRecyclerView() {
        binding.rankList.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rankList.setFocusable(false);
        binding.rankList.addItemDecoration(new DividerItemDecoration(getActivity(), 1));
        adapter = new RankListAdapterNew(getActivity(), rankList);
        binding.rankList.setAdapter(adapter);

        binding.swipeRefreshWidget.setRefreshHeader(new ClassicsHeader(getActivity()));
        binding.swipeRefreshWidget.setRefreshFooter(new ClassicsFooter(getActivity()));
        binding.swipeRefreshWidget.setOnRefreshListener(new OnRefreshListener() {
            public void onRefresh(RefreshLayout refreshLayout) {
                if (!NetStatusUtil.isConnected(getActivity())) {
                    refreshLayout.finishRefresh();
                    return;
                }

                startRank = 0;
                isLastPage = false;
                rankList.clear();
                rankHandler.sendEmptyMessage(0);
                refreshLayout.finishRefresh();
            }
        });
        binding.swipeRefreshWidget.setOnLoadMoreListener(new OnLoadMoreListener() {
            public void onLoadMore(RefreshLayout refreshLayout) {
                if (!NetStatusUtil.isConnected(getActivity())) {
                    refreshLayout.finishLoadMore();
                    return;
                }

                updateUi(false,null);

                if (isLastPage) {
                    ToastUtil.showToast(getActivity(), "已加载全部数据");
                } else {
                    rankHandler.sendEmptyMessage(0);
                }
                refreshLayout.finishLoadMore();
            }
        });
    }

    private void setFirstImag(String imagUrl, String name, int score, String uid) {

        String str = "";
        if (TextUtils.isEmpty(name)) {
            str = uid;

        } else {
            str = name;

        }
        binding.rankUserName.setText(str);
        binding.championReadWords.setText(String.valueOf(score));
        if (("http://static1." + Constant.IYUBA_CN + "uc_server/images/noavatar_middle.jpg")
                .equals(imagUrl)) {
            binding.rankUserImage.setVisibility(View.INVISIBLE);
            binding.rankUserImageText.setVisibility(View.VISIBLE);
            p = Pattern.compile("[a-zA-Z]");
            m = p.matcher(getFirstChar(str));
            if (m.matches()) {
                binding.rankUserImageText.setBackgroundResource(R.drawable.rank_blue);
                binding.rankUserImageText.setText(getFirstChar(str));
            } else {
                binding.rankUserImageText.setBackgroundResource(R.drawable.rank_green);
                binding.rankUserImageText.setText(getFirstChar(str));
            }

        } else {
            binding.rankUserImage.setVisibility(View.VISIBLE);
            binding.rankUserImageText.setVisibility(View.INVISIBLE);
            GitHubImageLoader.getInstance().setRawPic(imagUrl, binding.rankUserImage, R.drawable.defaultavatar);

        }

    }

    private void initClick(){
        binding.reChampion.setOnClickListener(v->{
            String currVoaId;
            if (VoaDataManager.getInstance().voaTemp.lessonType.equals(TypeLibrary.BookType.conceptFourUS)
                    ||VoaDataManager.getInstance().voaTemp.lessonType.equals(TypeLibrary.BookType.conceptJunior)) {
                currVoaId = String.valueOf(voaId);
            } else {
                currVoaId = String.valueOf(voaId * 10);
            }

            Intent intent = new Intent();
            intent.putExtra("uid", champion.getUid());
            intent.putExtra("voaId", currVoaId);
            intent.putExtra("userName", champion.getName());
            intent.putExtra("userPic", champion.getImgSrc());
            intent.setClass(getActivity(), CommentActivity.class);
            startActivity(intent);
        });

        binding.reMyInfo.setOnClickListener(v->{
            if (UserInfoManager.getInstance().isLogin()) {
                String currVoaId;
                if (ConceptBookChooseManager.getInstance().getBookType().equals(TypeLibrary.BookType.conceptFourUS)
                        ||ConceptBookChooseManager.getInstance().getBookType().equals(TypeLibrary.BookType.conceptJunior)) {
                    currVoaId = String.valueOf(voaId);
                } else {
                    currVoaId = String.valueOf(voaId * 10);
                }

                Intent intent = new Intent();
                intent.putExtra("uid", String.valueOf(UserInfoManager.getInstance().getUserId()));
                intent.putExtra("voaId", currVoaId);
                intent.putExtra("userName", UserInfoManager.getInstance().getUserName());
                intent.putExtra("userPic", myImgUrl);
                intent.setClass(getActivity(), CommentActivity.class);
                startActivity(intent);
            } else {
//            Intent intent = new Intent(mContext, Login.class);
//            mContext.startActivity(intent);
                LoginUtil.startToLogin(getActivity());
            }
        });
    }

    public void getCurrentArticleInfo() {
        this.voaId = VoaDataManager.Instace().voaTemp.voaId;
    }

    protected void lazyLoad() {
        if ((isPrepared) && (isVisible)) {
            if (!isFirst) {
                return;
            }
            if (waitingDialog != null) {
                waitingDialog.show();
            }
            rankHandler.sendEmptyMessage(0);
            isFirst = false;
        }
    }

    public void onCreate(@Nullable Bundle paramBundle) {
        super.onCreate(paramBundle);
        EventBus.getDefault().register(this);

        this.isPrepared = true;
//        lazyLoad();
    }

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup viewGroup, @Nullable Bundle paramBundle) {
        binding = FragmentEvalRankBinding.inflate(inflater,viewGroup,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initRecyclerView();
        waitingDialog = WaittingDialog.showDialog(getActivity());
        initClick();

        isFirstLoadData = true;
        rankHandler.sendEmptyMessage(0);
    }

    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    //可以转为onUnitChange
    public void refreshDate() {
        rankList.clear();
        startRank = 0;
        getCurrentArticleInfo();
        rankHandler.sendEmptyMessage(0);
    }

    public void dismissDialog() {
        if (waitingDialog != null) {
            waitingDialog.dismiss();
        }
    }

    //评测中刷新排行榜数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RefreshDataEvent event){
        if (event.getType().equals(TypeLibrary.RefreshDataType.eval_rank)){
            //评测的排行榜
            refreshDate();
        }
    }

    //登录完成后刷新排行榜数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(VipChangeEvent event){
        refreshDate();
    }

    //刷新ui界面显示
    private void updateUi(boolean isLoading,String showMsg){
        if (isLoading){
            binding.failLayout.setVisibility(View.VISIBLE);
            binding.failMsg.setText("正在刷新数据");
        }else {
            if (TextUtils.isEmpty(showMsg)){
                binding.failLayout.setVisibility(View.GONE);
            }else {
                binding.failLayout.setVisibility(View.VISIBLE);
                binding.failMsg.setText(showMsg);
            }
        }
    }
}
