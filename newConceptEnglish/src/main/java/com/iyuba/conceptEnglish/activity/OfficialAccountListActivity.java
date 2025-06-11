package com.iyuba.conceptEnglish.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.adapter.OfficialAccountListAdapter;
import com.iyuba.conceptEnglish.api.ApiRetrofit;
import com.iyuba.conceptEnglish.api.GetOfficialAccountListAPI;
import com.iyuba.conceptEnglish.entity.OfficialAccountListResponse;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.WaittingDialog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 公众号界面
 *
 * @author chentong
 * @version 1.2
 */
public class OfficialAccountListActivity extends Activity {
    private static final int PAGE_COUNT = 100;
    private static final String NEWS_FROM = "all";

    private Button backBtn;
    private RecyclerView mRecycler;
    private SwipeRefreshLayout mSwipe;

    private CustomDialog cd;

    private Context mContext;
    private boolean boolRequestSuccess = false;
    private int mPageNumberInt = 1;
    private List<OfficialAccountListResponse.AccountBean> mList = new ArrayList<>();
    private OfficialAccountListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_official_account_list);
        CrashApplication.getInstance().addActivity(this);
        mContext = this;
        cd = WaittingDialog.showDialog(mContext);
        //返回btn
        backBtn = (Button) findViewById(R.id.button_back);
        backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSwipe = findViewById(R.id.swipe);
        mRecycler = findViewById(R.id.recycler);

        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPageNumberInt = 1;
                refreshData(mPageNumberInt);
            }
        });

        mAdapter = new OfficialAccountListAdapter(OfficialAccountListActivity.this, mList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(OfficialAccountListActivity.this);
        mRecycler.setLayoutManager(layoutManager);
        mRecycler.setAdapter(mAdapter);

        refreshData(mPageNumberInt);

    }


    private void refreshData(int pageNumber) {
        GetOfficialAccountListAPI getOfficialAccountListAPI = ApiRetrofit.getInstance().getGetOfficialAccountListAPI();
        getOfficialAccountListAPI.getList(GetOfficialAccountListAPI.url, pageNumber, PAGE_COUNT, NEWS_FROM)
                .enqueue(new Callback<OfficialAccountListResponse>() {
                    @Override
                    public void onResponse(Call<OfficialAccountListResponse> call, Response<OfficialAccountListResponse> response) {
                        mSwipe.setRefreshing(false);
                        if (response.body() != null
                                && response.body().getResult() == 200) {
                            if (pageNumber == 1) {
                                mList.clear();
                            }
                            mList.addAll(response.body().getData());
                            mAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(mContext, "服务器获取失败", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<OfficialAccountListResponse> call, Throwable t) {
                        mSwipe.setRefreshing(false);
                        Toast.makeText(mContext, getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
