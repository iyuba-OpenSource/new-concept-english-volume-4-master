//package com.iyuba.conceptEnglish.activity;
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.FragmentActivity;
//import androidx.recyclerview.widget.DividerItemDecoration;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//
//import com.iyuba.conceptEnglish.R;
//import com.iyuba.conceptEnglish.adapter.SearchWordAdapter;
//import com.iyuba.conceptEnglish.sqlite.mode.RecycleViewItemData;
//import com.iyuba.conceptEnglish.sqlite.mode.VoaDetail;
//import com.iyuba.conceptEnglish.sqlite.op.VoaDetailOp;
//import com.iyuba.conceptEnglish.widget.cdialog.CustomToast;
//import com.iyuba.core.common.util.ToastUtil;
//import com.scwang.smartrefresh.layout.SmartRefreshLayout;
//import com.scwang.smartrefresh.layout.api.RefreshLayout;
//import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import permissions.dispatcher.NeedsPermission;
//import permissions.dispatcher.OnPermissionDenied;
//import permissions.dispatcher.RuntimePermissions;
//
///**
// * Created by ivotsm on 2017/2/22.
// */
//@RuntimePermissions
//public class SentenceSearchActivity extends FragmentActivity {
//    private Context mContext;
//
//    @BindView(R.id.title_search)
//    TextView title_search;
//
//    @BindView(R.id.iv_title_back)
//    Button iv_title_back;
//
//    @BindView(R.id.swipe_refresh_widget)
//    SmartRefreshLayout swipeRefreshLayout;
//
//    @BindView(R.id.recyclerview)
//    RecyclerView recycleview;
//
//    private String keyWord = "mother";
//    private List<RecycleViewItemData> list = new ArrayList<>();
//    private List<RecycleViewItemData> pagelist = new ArrayList<>();
//
//    private SearchWordAdapter searchWordAdapter;
//
//
//    private int pages = 1;
//    private boolean isLastPage = false;
//    private int pageNum = 20;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_search_article);
//        mContext = this;
//        ButterKnife.bind(this);
//        keyWord = getIntent().getStringExtra("keyword");
//        title_search.setText("\"" + keyWord + "\"" + "的相关搜索结果");
//
//        iv_title_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//        searchSentence();
//        recycleview.setLayoutManager(new LinearLayoutManager(mContext));
//        recycleview.addItemDecoration(new DividerItemDecoration(mContext, 1));
//        searchWordAdapter = new SearchWordAdapter(mContext, pagelist);
//        recycleview.setAdapter(searchWordAdapter);
//
//        setSentenceData();
//        swipeRefreshLayout.setEnableRefresh(false);
//        //加载更多
//        swipeRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
//            @Override
//            public void onLoadMore(RefreshLayout refreshlayout) {
//
//                if (isLastPage) {
//                    ToastUtil.showToast(mContext, "已加载全部数据");
//                } else {
//                    pages++;
//                    setSentenceData();
//                }
//                refreshlayout.finishLoadMore();
//            }
//        });
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        searchWordAdapter.stopAllPlayer();
//
//    }
//
//    @SuppressLint("NeedOnRequestPermissionsResult")
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        SentenceSearchActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
//    }
//
//    public void requstPerssiion() {
//        //录音以及存储权限
//        SentenceSearchActivityPermissionsDispatcher.initLocationWithPermissionCheck(this);
//    }
//
//    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO})
//    public void initLocation() {
//    }
//
//    @OnPermissionDenied({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO})
//    public void locationDenied() {
//        CustomToast.showToast(SentenceSearchActivity.this, "录音或存储权限未开启，开启后可正常使用此功能", 1000);
//    }
//
//
//    /**
//     * 查询句子
//     */
//    private void searchSentence() {
//        VoaDetailOp voaDetailOp = new VoaDetailOp(mContext);
//        List<VoaDetail> voaDetailList = voaDetailOp.findDataByKey(" " + keyWord + " ");
//
//        if (voaDetailList != null && voaDetailList.size() > 0) {
//            for (int i = 0; i < voaDetailList.size(); i++) {
//                RecycleViewItemData<VoaDetail> itemData0 = new RecycleViewItemData<>();
//                VoaDetail voaDetail = voaDetailList.get(i);
//                voaDetail.setRealIndex(i + 1);
//                itemData0.setT(voaDetail);
//                itemData0.setDataType(SearchWordAdapter.ITEM_TYPE_SENTENCE);
//                list.add(itemData0);
//            }
//        }
//    }
//
//    private void setSentenceData() {
//        int count = pages * pageNum;
//        if (list.size() >= count) {
//            for (int i = count - pageNum; i < count; i++) {
//                pagelist.add(list.get(i));
//            }
//        } else {
//            for (int i = count - pageNum; i < list.size(); i++) {
//                pagelist.add(list.get(i));
//
//            }
//            isLastPage = true;
//        }
//        searchWordAdapter.notifyDataSetChanged();
//    }
//}
