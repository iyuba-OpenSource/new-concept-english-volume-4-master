package com.iyuba.core.talkshow.lesson.rank;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.iyuba.core.common.data.model.Ranking;
import com.iyuba.core.common.data.model.TalkLesson;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.talkshow.lesson.watch.WatchDubbingActivity;
import com.iyuba.lib.R;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

/**
 * 配音评论 子页
 */
public class RankFragment extends Fragment implements RankingMvpView{
    private static final String VOA = "voa";
    private static final String TAG = RankFragment.class.getSimpleName();


    public static RankFragment newInstance(TalkLesson param1) {
        RankFragment fragment = new RankFragment();
        Bundle args = new Bundle();
        args.putParcelable(VOA, param1);
        fragment.setArguments(args);
        return fragment;
    }

    private static final int PAGE_NUM = 1;
    private static final int PAGE_SIZE = 20;

    private TalkLesson mVoa;

    SwipeRefreshLayout mRefreshLayout;
    RecyclerView mRecyclerView;
    View mLoadingLayout;
    View mEmptyView;
    TextView mEmptyTextTv;
    ImageView mEmptyImageIv;

    RankPresenter mPresenter;
    RankingAdapter mAdapter;

    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mVoa = getArguments().getParcelable(VOA);
        }
        mPresenter =new RankPresenter();
        mContext = getContext();
        mAdapter = new RankingAdapter(mContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rank_talk, container, false);
        initView(view);
        return view;
    }

    private void initView(View view){
        mRefreshLayout = view.findViewById(R.id.refresh_layout);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mLoadingLayout = view.findViewById(R.id.loading_layout);
        mEmptyView = view.findViewById(R.id.empty_view);
        mEmptyTextTv = view.findViewById(R.id.empty_text);
        mEmptyImageIv = view.findViewById(R.id.empty_image);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.attachView(this);

        mAdapter.setRankingCallback(callback);
        mRecyclerView.setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
       // LinearItemDivider divider = new LinearItemDivider(getActivity(), LinearItemDivider.VERTICAL_LIST);
        //divider.setDivider(getResources().getDrawable(R.drawable.voa_ranking_divider));
        //mRecyclerView.addItemDecoration(divider);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。
    }

    RankingAdapter.RankingCallback callback = new RankingAdapter.RankingCallback() {
        @Override
        public void onClickThumbs(int id) {
            mPresenter.doAgree(id);
        }

        @Override
        public void onClickLayout(Ranking ranking, int pos) {
            Intent intent = WatchDubbingActivity.buildIntent(getContext(), ranking, mVoa, ranking.userId);
            startActivity(intent);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (mVoa != null) {
            mPresenter.getRanking(mVoa.voaId(), PAGE_NUM, PAGE_SIZE);
            mRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        }
        MobclickAgent.onPageStart(TAG);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
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
                    mPresenter.getRanking(mVoa.voaId(), PAGE_NUM, PAGE_SIZE);
                }
            });
        }
    };

    @Override
    public void showRankings(List<Ranking> rankingList) {
        mEmptyView.setVisibility(View.GONE);
        mRefreshLayout.setVisibility(View.VISIBLE);
        mAdapter.setRankingList(rankingList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showEmptyRankings() {
        mEmptyView.setVisibility(View.VISIBLE);
        mRefreshLayout.setVisibility(View.GONE);
    }

    @Override
    public void showToast(int id) {
        ToastUtil.showToast(mContext,id);
    }

    @Override
    public void showToast(String msg) {
        ToastUtil.showToast(mContext,msg);
    }

    @Override
    public void showLoadingLayout() {
        mEmptyImageIv.setImageResource(R.drawable.empty_comment_data);
        mEmptyTextTv.setText("您还没有配音,\n赶紧去配音和 \n好友PK吧~");
        mLoadingLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismissLoadingLayout() {
        mLoadingLayout.setVisibility(View.GONE);
    }

    @Override
    public void dismissRefreshingView() {
        mRefreshLayout.setRefreshing(false);
    }
}
