package devsearch;

import android.app.Activity;
import android.os.Bundle;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.dlna.manager.DLNADeviceManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jaeger.library.StatusBarUtil;

import org.cybergarage.android.R;
import org.cybergarage.android.R2;
import org.cybergarage.upnp.Device;

import java.util.ArrayList;
import java.util.List;

import static com.android.dlna.manager.DLNADeviceManager.getInstance;

public class DevSearchActivity extends AppCompatActivity implements DevSearchMvpView {

    RecyclerView recyclerView;
    //    @BindView(R2.id.refresh_layout)
//    SwipeRefreshLayout mRefreshLayout;
    ImageView gif;
    ImageView imgSearching;
    LinearLayout searchingView;
    ImageView back;
    Toolbar toolbar;
    AppBarLayout top;
    ImageView header;
    CollapsingToolbarLayout collapsingToolbarLayout;
    TextView title;
    TextView retry;

    private DevSearchAdapter mAdapter;
    private List<Device> mDeviceList = new ArrayList<>();

    private void startRotate() {
        RotateAnimation animation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(5000);
        animation.setRepeatMode(Animation.RESTART);
        animation.setRepeatCount(Animation.INFINITE);
        imgSearching.startAnimation(animation);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_search);

        initView();
        initClick();

        setToolBarReplaceActionBar();
        StatusBarUtil.setColor(this, ResourcesCompat.getColor(getResources(), R.color.transparent, getTheme()), 0);

        setAndroidNativeLightStatusBar(this, true);
        setTitleToCollapsingToolbarLayout();
//        setSupportActionBar(toolbar);
        startDiscovery(false);
        startRotate();
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(
                this, DividerItemDecoration.VERTICAL);
        ((DividerItemDecoration) itemDecoration).setDrawable(getResources().getDrawable(R.drawable.list_divider));
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new DevSearchAdapter();
        recyclerView.setAdapter(mAdapter);
//        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                startDiscovery(false);
//            }
//        });
        mAdapter.setOnItemClickListener(new DevSearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                getInstance().setCurrentDevice(mDeviceList.get(position));
                setResult(RESULT_OK);
                finish();
                Log.d("退出显示4", this.getClass().getName());
            }

            @Override
            public void onRetryClick() {
                startDiscovery(false);
            }
        });
        Glide.with(this).load(R.drawable.timg)
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(gif);

    }

    //初始化
    private void initView(){
        recyclerView = findViewById(R.id.recycler_view);
        gif = findViewById(R.id.gif);
        imgSearching = findViewById(R.id.img_searching);
        searchingView = findViewById(R.id.searching_view);
        back = findViewById(R.id.back);
        toolbar = findViewById(R.id.toolbar);
        top = findViewById(R.id.top);
        header = findViewById(R.id.headers);
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbarLayout);
        title = findViewById(R.id.title);
        retry = findViewById(R.id.retry);
    }

    //点击操作
    private void initClick(){
        back.setOnClickListener(v->{
            finish();
            Log.d("退出显示5", this.getClass().getName());
        });
        retry.setOnClickListener(v->{
            startDiscovery(false);
            retry.setVisibility(View.GONE);
        });
    }


    private void startDiscovery(boolean auto) {
        if (auto) {
//            mRefreshLayout.setRefreshing(true);
        }
        getInstance().startDiscovery(mListener);
        searchingView.setVisibility(View.VISIBLE);
    }

    private final DLNADeviceManager.MediaRenderDeviceChangeListener mListener = new DLNADeviceManager.MediaRenderDeviceChangeListener() {
        @Override
        public void onStarted() {
            updateEmptyView(false);
        }

        @Override
        public void onDeviceListChanged(List<Device> list) {
            mDeviceList = list;
//            mRefreshLayout.setRefreshing(false);
            updateEmptyView(mDeviceList.isEmpty());
            mAdapter.setmDeviceList(mDeviceList);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onFinished(List<Device> list) {
            searchingView.setVisibility(View.GONE);
            mDeviceList = list;
//            mRefreshLayout.setRefreshing(false);
            updateEmptyView(mDeviceList.isEmpty());
            if (mDeviceList.isEmpty()) {
                retry.setVisibility(View.VISIBLE);
            }else {
                retry.setVisibility(View.GONE);
            }
            mAdapter.setmDeviceList(mDeviceList);
            mAdapter.notifyDataSetChanged();
        }
    };

    private void updateEmptyView(boolean visible) {
//        if (visible && mEmptyView.getVisibility() != View.VISIBLE) {
//            mEmptyView.setVisibility(View.VISIBLE);
//        } else if (!visible && mEmptyView.getVisibility() == View.VISIBLE) {
//            mEmptyView.setVisibility(View.GONE);
//        }
    }

    private static void setAndroidNativeLightStatusBar(Activity activity, boolean dark) {
        View decor = activity.getWindow().getDecorView();
        if (dark) {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    private void setTitleToCollapsingToolbarLayout() {
        top.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset <= -header.getHeight() / 2) {
//                    collapsingToolbarLayout.setTitle("选择设备");
//                    //使用下面两个CollapsingToolbarLayout的方法设置展开透明->折叠时你想要的颜色
//                    collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
//                    collapsingToolbarLayout.setCollapsedTitleTextColor(Color.parseColor("#333333"));
//                    collapsingToolbarLayout.setLayoutMode(Color.parseColor("#333333"));
//                    collapsingToolbarLayout.setExpandedTitleGravity(Gravity.CENTER);
//                    collapsingToolbarLayout.setCollapsedTitleGravity(Gravity.CENTER);
                    title.setText("选择设备");
//                    collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.white));
                } else {
                    title.setText("");
                    collapsingToolbarLayout.setTitle("");
                }
            }
        });
    }

    private void setToolBarReplaceActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                finish();
                // onBackPressed();//结束程序
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getInstance().stopDiscovery();
    }
}