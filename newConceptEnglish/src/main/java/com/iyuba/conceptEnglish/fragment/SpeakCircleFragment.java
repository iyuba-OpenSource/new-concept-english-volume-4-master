package com.iyuba.conceptEnglish.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.adapter.SpeakCircleAdapter;
import com.iyuba.conceptEnglish.api.ApiRetrofit;
import com.iyuba.conceptEnglish.api.SpeakCircleApi;
import com.iyuba.conceptEnglish.sqlite.mode.SpeakCircleBean;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.lil.user.UserInfoManager;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpeakCircleFragment extends Fragment implements Consumer<Integer> {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.smart_refresh_layout)
    SmartRefreshLayout smartRefreshLayout;
    Unbinder unbinder;

    private Context mContext;
    private View rootView;
    private List<SpeakCircleBean.DataBean> listData = new ArrayList<>();
    private SpeakCircleAdapter adapter;

    private int pageNumber = 1, pageCounts = 10;
    private int selflg = 0;
    private boolean isEnd = false;

    private Dialog mWaittingDialog;

    private boolean isFirst = true;
    private boolean isPrepared;
//    private ConceptViewModel conceptViewModel;
    private AlertDialog.Builder dialog;

    public static SpeakCircleFragment instence(int selflg) {
        SpeakCircleFragment speakCircleFragment = new SpeakCircleFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("selflg", selflg);
        speakCircleFragment.setArguments(bundle);
        return speakCircleFragment;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            lazyLoad();
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            selflg = getArguments().getInt("selflg", 0);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isPrepared = true;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_speak_circle, container, false);
        }
        mContext = getActivity();
        mWaittingDialog = WaittingDialog.showDialog(mContext);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh();
                listData.clear();
                pageNumber = 1;
                initData();
            }
        });

        smartRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                smartRefreshLayout.finishLoadMore();
                if (isEnd) {
                    ToastUtil.showToast(mContext, "已加载全部数据");
                    return;
                }
                pageNumber++;
                initData();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new SpeakCircleAdapter(mContext, listData);
        adapter.setOnItemLongClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    private void initData() {
        mWaittingDialog.show();
        SpeakCircleApi speakCircleApi = ApiRetrofit.getInstance().getSpeakCircleApi();
        speakCircleApi.getSpeakList(SpeakCircleApi.url, SpeakCircleApi.protocol, Constant.EVAL_TYPE,
                String.valueOf(selflg), pageNumber + "", pageCounts + "", Constant.APPID,
                String.valueOf(UserInfoManager.getInstance().getUserId())).enqueue(new Callback<SpeakCircleBean>() {
            @Override
            public void onResponse(Call<SpeakCircleBean> call, Response<SpeakCircleBean> response) {

                mWaittingDialog.dismiss();
                try {
                    SpeakCircleBean bean = response.body();
                    Log.e("请求成功======", bean.toString() + bean.getLastPage() + "==" + pageNumber);
                    if (bean.getData() != null && bean.getData().size() > 0) {
                        listData.addAll(bean.getData());
                        adapter.notifyDataSetChanged();
                    }
                    if (bean.getLastPage() <= pageNumber) {
                        isEnd = true;
                    } else {
                        isEnd = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<SpeakCircleBean> call, Throwable t) {
                mWaittingDialog.dismiss();
                Log.e("请求失败======", t.getMessage() + t.toString() + t.getCause());
            }
        });

    }


    private void lazyLoad() {
        if (!isFirst || !isPrepared) {
            return;
        }
        isFirst = false;
        initData();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getUserVisibleHint()) {
            setUserVisibleHint(true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (adapter != null) {
            adapter.stopPLayer();
        }
    }

    public void pauseAdapter() {
        if (adapter != null) {
            adapter.stopPLayer();
        }
    }

    @Override
    public void accept(Integer position) {
        SpeakCircleBean.DataBean item = listData.get(position);
        String uid = String.valueOf(UserInfoManager.getInstance().getUserId());
        if (!uid.equals(item.getUserid())){
            return;
        }
        if (dialog == null) {
            dialog = new AlertDialog.Builder(mContext);
        }

        // TODO: 2025/3/14 暂时没有使用，关闭 
        /*if (conceptViewModel == null) {
            conceptViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(ConceptViewModel.class);
        }
        dialog.setTitle("警告")
                .setPositiveButton("取消", null)
                .setMessage("此操作将删除你的配音且无法撤回，是否继续?")
                .setNegativeButton("确定", (d, i) -> {
                    StringBuilder hint = new StringBuilder().append("删除");
                    conceptViewModel.deleteEval(item.getId(), response -> {
                        hint.append("成功");
                        ToastUtil.showToast(mContext, hint.toString());
                        adapter.notifyItemRemoved(position);
                        return null;
                    }, e -> {
                        hint.append("失败");
                        ToastUtil.showToast(mContext, hint.toString());
                        return null;
                    });
                })
                .show();*/
    }
}
