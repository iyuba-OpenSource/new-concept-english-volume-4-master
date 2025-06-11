//package com.iyuba.conceptEnglish.activity;
//
//import android.content.Context;
//import android.os.Bundle;
//
//import androidx.fragment.app.FragmentActivity;
//import androidx.recyclerview.widget.DividerItemDecoration;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import android.util.Log;
//import android.widget.Button;
//import android.widget.TextView;
//
//import com.iyuba.conceptEnglish.R;
//import com.iyuba.conceptEnglish.adapter.SearchWordAdapter;
//import com.iyuba.conceptEnglish.sqlite.mode.RecycleViewItemData;
//import com.iyuba.conceptEnglish.sqlite.mode.Voa;
//import com.iyuba.conceptEnglish.sqlite.op.VoaOp;
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
//
///**
// * Created by ivotsm on 2017/2/22.
// */
//
//public class ArticleSearchActivity extends FragmentActivity {
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
//    private SearchWordAdapter searchWordAdapter;
//
//    private int pages = 1;
//    private boolean isLastPage = false;
//
//    private List<Voa> searchVoaList = new ArrayList<>();
//    private VoaOp voaOp;
//    private int pageNum = 10;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_search_article);
//        mContext = this;
//        ButterKnife.bind(this);
//
//        keyWord = getIntent().getStringExtra("keyword");
//        iv_title_back.setOnClickListener(v -> {
//            finish();
//        });
//        title_search.setText("\"" + keyWord + "\"" + "的相关搜索结果");
//        recycleview.setLayoutManager(new LinearLayoutManager(mContext));
//        recycleview.addItemDecoration(new DividerItemDecoration(mContext, 1));
//        searchWordAdapter = new SearchWordAdapter(mContext, pagelist);
//        recycleview.setAdapter(searchWordAdapter);
//        swipeRefreshLayout.setEnableRefresh(false);
//
//        searchAppointText();
//
//        setArticleData();
//        //加载更多
//        swipeRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
//            @Override
//            public void onLoadMore(RefreshLayout refreshlayout) {
//
//
//                if (isLastPage) {
//                    ToastUtil.showToast(mContext, "已加载全部数据");
//                } else {
//                    pages++;
//                    setArticleData();
//                }
//                refreshlayout.finishLoadMore();
//            }
//        });
//    }
//
//
//    /**
//     * 查询文章
//     */
//    private void searchAppointText() {
//        voaOp = new VoaOp(mContext);
//        List<Voa> searchResult = voaOp.getSearchResult(" " + keyWord + " ");
//        Log.e("searchVoaList", searchVoaList.size() + "***");
//        Log.e("keyWord", keyWord + "***");
//
//
//        for (int i = 0; i < searchResult.size(); i++) {
////            if (searchResult.get(i).voaId < 2000) {
//            searchVoaList.add(searchResult.get(i));
////            }
//        }
//
//        if (searchVoaList != null && searchVoaList.size() != 0) {
//            for (int i = 0; i < searchVoaList.size(); i++) {
//                RecycleViewItemData<Voa> itemData0 = new RecycleViewItemData<>();
//                itemData0.setT(searchVoaList.get(i));
//                itemData0.setDataType(SearchWordAdapter.ITEM_TYPE_ARTICLE);
//                list.add(itemData0);
//            }
//        }
//    }
//
//    private void setArticleData() {
//
//
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
