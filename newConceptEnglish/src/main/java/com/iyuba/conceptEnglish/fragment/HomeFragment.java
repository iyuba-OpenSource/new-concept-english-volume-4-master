package com.iyuba.conceptEnglish.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.iyuba.ad.adblocker.AdBlocker;
import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.activity.MultipleChoiceActivity;
import com.iyuba.conceptEnglish.activity.Web;
import com.iyuba.conceptEnglish.ad.AdInitManager;
import com.iyuba.conceptEnglish.adapter.VoaAdapter;
import com.iyuba.conceptEnglish.adapter.VoaAdapterNew;
import com.iyuba.conceptEnglish.api.RefreshMicroReadPercentageAPI;
import com.iyuba.conceptEnglish.databinding.VoaListBinding;
import com.iyuba.conceptEnglish.entity.CommonResponce;
import com.iyuba.conceptEnglish.event.RefreshBookEvent;
import com.iyuba.conceptEnglish.lil.concept_other.book_choose.ConceptBookChooseActivity;
import com.iyuba.conceptEnglish.lil.concept_other.download.FileDownloadEvent;
import com.iyuba.conceptEnglish.lil.concept_other.download.FilePathUtil;
import com.iyuba.conceptEnglish.lil.concept_other.util.ConceptHomeRefreshUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.event.HomeMocProgressEvent;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.event.RefreshDataEvent;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.StrLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.listener.OnSimpleClickListener;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.ConceptBookChooseManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.ConceptDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.manager.FixRemoteManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.ad.show.template.AdTemplateShowManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.ad.show.template.AdTemplateViewBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.ad.show.template.OnAdTemplateShowListener;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.download.DownloadFixCallback;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.download.DownloadFixDialog;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.download.DownloadFixUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.search.NewSearchActivity;
import com.iyuba.conceptEnglish.lil.fix.concept.bgService.ConceptBgPlayEvent;
import com.iyuba.conceptEnglish.lil.fix.concept.bgService.ConceptBgPlayManager;
import com.iyuba.conceptEnglish.lil.fix.concept.bgService.ConceptBgPlaySession;
import com.iyuba.conceptEnglish.manager.VoaDataManager;
import com.iyuba.conceptEnglish.model.HomeFragmentModel;
import com.iyuba.conceptEnglish.model.SaveWordDataModel;
import com.iyuba.conceptEnglish.sqlite.db.TalkShowDBManager;
import com.iyuba.conceptEnglish.sqlite.db.WordChildDBManager;
import com.iyuba.conceptEnglish.sqlite.mode.ArticleRecordBean;
import com.iyuba.conceptEnglish.sqlite.mode.Book;
import com.iyuba.conceptEnglish.sqlite.mode.Voa;
import com.iyuba.conceptEnglish.sqlite.mode.VoaDetail;
import com.iyuba.conceptEnglish.sqlite.op.ArticleRecordOp;
import com.iyuba.conceptEnglish.sqlite.op.BookOp;
import com.iyuba.conceptEnglish.sqlite.op.VoaDetailOp;
import com.iyuba.conceptEnglish.sqlite.op.VoaDetailYouthOp;
import com.iyuba.conceptEnglish.sqlite.op.VoaOp;
import com.iyuba.conceptEnglish.study.StudyNewActivity;
import com.iyuba.conceptEnglish.util.ConstUtil;
import com.iyuba.conceptEnglish.util.PullHistoryDetailUtil;
import com.iyuba.conceptEnglish.util.ScreenUtils;
import com.iyuba.conceptEnglish.widget.ScrollSpeedLinearLayoutManger;
import com.iyuba.conceptEnglish.widget.dialog.DownloadDialog;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.activity.login.LoginUtil;
import com.iyuba.core.common.data.DataManager;
import com.iyuba.core.common.data.model.TalkLesson;
import com.iyuba.core.common.data.model.VoaText;
import com.iyuba.core.common.data.model.VoaWord2;
import com.iyuba.core.common.setting.SettingConfig;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.event.DownloadLessonEvent;
import com.iyuba.core.event.UpdateUnitTitleEvent;
import com.iyuba.core.event.VipChangeEvent;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.headlinelibrary.data.record.RecordUpdate;
import com.iyuba.module.headlinetalk.ui.widget.LoadingDialog;
import com.iyuba.module.toolbox.RxUtil;
import com.iyuba.sdk.data.iyu.IyuAdClickEvent;
import com.iyuba.sdk.other.NetworkUtil;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

//教材界面
public class HomeFragment extends Fragment implements OnScrollListener {
    private static final int WAIT_ADAPTER_INIT = 0;
    private static final int COMPELED_ADAPTER_INIT = 1;
    private Context mContext;

    // 课文列表adapter
    private VoaAdapterNew voaAdapter;
    // 搜索adapter
    private VoaAdapter searchVoaAdapter;
    private List<Voa> voaList = new ArrayList<Voa>();
    private List<Voa> searchVoaList;
    private Disposable mDispose_SyncVoaSub;

    private VoaOp voaOp;

    private int curVoaId;

    private String searchWord;

    private CustomDialog waittingDialog;

    private int searchCurrPages = 1;
    private Voa curVoa;
    VoaDetailOp voaDetailOp;
    private DataManager mDataManager;

    private int index = 0;
    private static int OFFSET = 144;
    private int pageNum = 10;


    //当前选中课本
    private int curBookId;
    //当前的书籍类型
    private String curBookType;


    /**
     * string: lessonid （代表的时章节 id 或者课程id 来着）
     * intger：最大的完成的时间， 99999时表示 已完成
     */
    private Hashtable<Integer, Voa> mMicroList = new Hashtable<Integer, Voa>();

    //加载弹窗
    private LoadingDialog loadingDialog;
    //是否显示在前台界面
    private boolean isShowUser = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WAIT_ADAPTER_INIT:
                    //将数据设置到adapter中，并且刷新页面
                    if (voaAdapter == null
                            || voaList == null
                            || voaList.size() == 0) {
                        mHandler.sendEmptyMessageDelayed(WAIT_ADAPTER_INIT, 200);
                    } else {
                        ConstUtil.sMicroList.clear();
                        ConstUtil.sMicroList.putAll(mMicroList);
                        voaAdapter.notifyDataSetChanged();
                        Log.d("刷新列表数据", "数据显示5");
                    }
                    break;
                default:
                    break;
            }
        }
    };

    //布局样式
    private VoaListBinding binding;

    //跳转的界面类型
    private String studyPage = null;

    public static HomeFragment getInstance(boolean isHideToolbar) {
        HomeFragment fragment = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(StrLibrary.hideToolbar, isHideToolbar);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //设置布局样式
        binding = VoaListBinding.inflate(inflater, container, false);

        //设置view高度为statusbar的高度，并填充statusbar
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) binding.fillStatusBarView.getLayoutParams();
        lp.width = LinearLayout.LayoutParams.MATCH_PARENT;
        lp.height = ScreenUtils.getStatusHeight(mContext);
        binding.fillStatusBarView.setLayoutParams(lp);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDataManager = DataManager.getInstance();
        init();
        initSearch();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

        //关闭广告
        AdTemplateShowManager.getInstance().stopTemplateAd(adTemplateKey);
        //关闭下载弹窗
        stopDownloadDialog();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isShowUser = isVisibleToUser;
    }

    /**
     * 初始化
     */
    public void init() {
        // waittingdialog 初始化
        waittingDialog = WaittingDialog.showDialog(mContext);
        //数据库 初始化
        voaOp = new VoaOp(mContext);
        //数据库 初始化
        voaDetailOp = new VoaDetailOp(mContext);
        //获取当前选中的课本
        curBookId = ConceptBookChooseManager.getInstance().getBookId();
        curBookType = ConceptBookChooseManager.getInstance().getBookType();

        //初始化列表数据
        initList();

        /**
         * 同步按钮的点击事件
         */
        binding.tvSync.setOnClickListener(v -> {
            if (!UserInfoManager.getInstance().isLogin()) {
                ToastUtil.showToast(mContext, "请登录后再执行此操作");
            } else {
                AlertDialog dialog = new AlertDialog.Builder(mContext)
                        .setTitle(R.string.alert_title)
                        .setMessage("此操作会将服务器各类统计数据同步到本地，耗时较长，请耐心等待")
                        .setPositiveButton("开始同步",
                                (dialog1, whichButton) -> updateArticleRecord())
                        .setNeutralButton("暂不同步",
                                (dialog12, which) -> {
                                }).create();
                dialog.show();
            }
        });

        /**
         * 初始化标题
         */
        String titleText = ConceptBookChooseManager.getInstance().getBookName();
        binding.title.setText(titleText);

        /**
         * 课本选择按钮的点击事件
         */
        binding.buttonShowCategory.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ConceptBookChooseActivity.start(mContext, 1);
            }
        });

        // 课文题目列表的绑定
//        binding.voaList.setLayoutManager(new ScrollSpeedLinearLayoutManger(mContext));

        //初始化 lesson 的title的数据
        getVoaList();
        //刷新列表(暂时屏蔽)
//        refreshVoaAdapter(1000);

        binding.voaList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();//获取LayoutManager
                if (manager instanceof LinearLayoutManager) {
                    //第一个可见的位置
                    int firstPosition = ((LinearLayoutManager) manager).findFirstVisibleItemPosition();
                    //如果 dx>0 则表示 右滑 ,dx<0 表示 左滑,dy <0 表示 上滑, dy>0 表示下滑
                    if (dy < 0) {
                        //上滑监听 firstPosition>1 ? View.VISIBLE :
                        binding.rlSpinner.setVisibility(View.GONE);
                    } else {
                        //下滑监听
                        binding.rlSpinner.setVisibility(firstPosition == 0 ? View.GONE : View.VISIBLE);
                    }
                }
            }
        });

        binding.imgPlay.setOnClickListener(v -> {
            //取消临时数据
            ConceptBgPlaySession.getInstance().setTempData(false);

            ExoPlayer exoPlayer = ConceptBgPlayManager.getInstance().getPlayService().getPlayer();
            if (exoPlayer != null) {
                if (exoPlayer.isPlaying()) {
                    EventBus.getDefault().post(new ConceptBgPlayEvent(ConceptBgPlayEvent.event_control_pause));
                } else {
                    EventBus.getDefault().post(new ConceptBgPlayEvent(ConceptBgPlayEvent.event_control_play));
                }
            }
        });
        binding.reBottom.setOnClickListener(v -> {
            //取消临时数据
            ConceptBgPlaySession.getInstance().setTempData(false);
            //重新设置详情和当前数据
            VoaDataManager.getInstance().voaTemp = ConceptBgPlaySession.getInstance().getCurData();
            if (VoaDataManager.getInstance().voaTemp.lessonType.equals(TypeLibrary.BookType.conceptJunior)) {
                VoaDataManager.getInstance().voaDetailsTemp = new VoaDetailYouthOp(getActivity()).getVoaDetailByVoaid(VoaDataManager.getInstance().voaTemp.voaId);
            } else {
                VoaDataManager.getInstance().voaDetailsTemp = new VoaDetailOp(getActivity()).findDataByVoaId(VoaDataManager.getInstance().voaTemp.voaId);
            }

            Intent intent = new Intent();
            intent.setClass(mContext, StudyNewActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            startActivity(intent);
        });


        initSpinner();

        //刷新口语秀数据
        refreshTalkShow();

        //隐藏或者展示toolbar
        if (getArguments() != null) {
            boolean isHideToolbar = getArguments().getBoolean(StrLibrary.hideToolbar, false);
            if (isHideToolbar) {
                binding.titleLayout.setVisibility(View.GONE);
            }
        }
    }

    private void initList(){
        voaAdapter = new VoaAdapterNew(mContext, new ArrayList<>(), onAdapterListener, this);
        binding.voaList.setLayoutManager(new ScrollSpeedLinearLayoutManger(mContext));
        binding.voaList.setAdapter(voaAdapter);
        voaAdapter.setOnDownloadProgressListener(downloadListener);
    }

    private void initSpinner() {
        HomeFragmentModel model = new HomeFragmentModel();
        List<String> spinnerData = model.getCurrentSpinnerData(curBookId);
        binding.niceSpinner.attachDataSource(spinnerData);
        binding.niceSpinner.setBackgroundResource(R.drawable.ic_list_scroll_select);
        binding.niceSpinner.setTextColor(getResources().getColor(R.color.white));
        binding.niceSpinner.setTextSize(13);

        binding.niceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (curBookType.equals(TypeLibrary.BookType.conceptJunior)) {
                    binding.voaList.smoothScrollToPosition(0);
                    return;
                }

                if (position == 0) {
                    binding.voaList.scrollToPosition(0);
                } else {
                    if (isLoadAD() && AdInitManager.isShowAd() && !UserInfoManager.getInstance().isVip()) {
                        //这里直接操作是存在bug的(这个操作实在是有点难受，看后续如何处理下吧)
                        //因为广告操作是停止后刷新的，滑动过程中无法刷新使用
                        //1.先把广告去掉
                        AdTemplateShowManager.getInstance().clearAd(adTemplateKey);
                        //2.刷新原来的数据
                        voaAdapter.refreshList(voaList);
                        //3.跳转到目标地址
                        int moveCount = position * 20;
                        ((LinearLayoutManager) binding.voaList.getLayoutManager()).scrollToPositionWithOffset(moveCount, 0);
                        //4.重置广告显示
                        refreshTemplateAd();
                    } else {
                        int scrollPosition = position * 20;
                        ((LinearLayoutManager) binding.voaList.getLayoutManager()).scrollToPositionWithOffset(scrollPosition, 0);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    public void getVoaDetail(int position) {
        // 从本地数据库中查找
        VoaDataManager.Instace().voaTemp = curVoa;

        if (curBookType.equals(TypeLibrary.BookType.conceptJunior)) {
            VoaDetailYouthOp voaDetailYouthOp = new VoaDetailYouthOp(mContext);
            int voaid = VoaDataManager.Instace().voaTemp.voaId;

            List<VoaDetail> list = voaDetailYouthOp.getVoaDetailByVoaid(voaid);
            if (list == null || list.size() == 0) {
                getYouthDataFromNet(voaid, voaDetailYouthOp, position);
                return;
            }
            //转换数据
            jumpTransfer(list, position);
        } else {
            //从本地获取
            VoaDataManager.Instace().voaDetailsTemp = voaDetailOp.findDataByVoaId(curVoa.voaId);
            turnUp(position);
        }
    }

    private void getYouthDataFromNet(int voaid, VoaDetailYouthOp voaDetailYouthOp, int position) {
        waittingDialog.show();
        //从服务器获取并保存到本地
        RxUtil.dispose(mDispose_SyncVoaSub);
        mDispose_SyncVoaSub = mDataManager.syncVoaTexts(voaid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<VoaText>>() {
                    @Override
                    public void accept(List<VoaText> voaTexts) throws Exception {
                        if (waittingDialog.isShowing()) {
                            waittingDialog.dismiss();
                        }
                        if (voaTexts.size() != 0) {
                            //转换数据
                            jumpTransfer(voaDetailYouthOp.voaTextTranslateToVoaDetail(voaid, voaTexts), position);
                            //存储数据
                            voaDetailYouthOp.insertOrReplaceData(voaid, voaTexts);
                        } else {
                            ToastUtil.showToast(mContext, "进入失败，无法获取课文详情");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (waittingDialog.isShowing()) {
                            waittingDialog.dismiss();
                        }
                        ToastUtil.showToast(mContext, "网络错误，无法获取课文详情");
                    }
                });
    }

    private void jumpTransfer(List<VoaDetail> timpList, int position) {
        //进入的逻辑
        if (VoaDataManager.Instace().voaDetailsTemp == null) {
            VoaDataManager.Instace().voaDetailsTemp = new ArrayList<>();
        }
        VoaDataManager.Instace().voaDetailsTemp.clear();
        VoaDataManager.Instace().voaDetailsTemp.addAll(timpList);
        turnUp(position);
    }

    /**
     * 跳转
     */
    private void turnUp(int position) {
        if (VoaDataManager.Instace().voaDetailsTemp != null && VoaDataManager.Instace().voaDetailsTemp.size() != 0) {
            VoaDataManager.Instace().setSubtitleSum(curVoa, VoaDataManager.Instace().voaDetailsTemp);
            VoaDataManager.Instace().setPlayLocalType(0);

            Intent intent = new Intent();
            intent.setClass(mContext, StudyNewActivity.class);
//            intent.setClass(mContext, StudyFixActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            intent.putExtra(StrLibrary.pageType, studyPage);
            intent.putExtra(StrLibrary.position, position);

            startActivity(intent);
            ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, android.R.anim.fade_out);
        }
    }

    /**
     * 设置课本标题列表的数据。
     */
    public void getVoaList() {
        curBookId = ConceptBookChooseManager.getInstance().getBookId();
        if (curBookType.equals(TypeLibrary.BookType.conceptJunior)) {
            getYouthList();
        } else {
            getFourVolume();
        }

        //刷新新的广告数据
        refreshTemplateAd();
    }

    private void getYouthList() {
        /**
         * 从数据库中查询出 lesson的 标题内容
         */

        List<Voa> tempList = voaOp.findDataByPage(curBookId, OFFSET, index);
        VoaDataManager.getInstance().voasTemp = VoaDataManager.getInstance().margeTypeToVoa(tempList, TypeLibrary.BookType.conceptJunior);
        voaList = VoaDataManager.getInstance().voasTemp;

        //刷新数据
        voaAdapter.refreshList(voaList);

        //存储在后台播放的会话中
        ConceptBgPlaySession.getInstance().setVoaList(voaList);
    }

    private void getFourVolume() {
        /**
         * 从数据库中查询出 lesson的 标题内容
         */
        List<Voa> tempList = new ArrayList<>();
        if (curBookId == 1 && curBookType.equals(TypeLibrary.BookType.conceptFourUK)) {
            VoaDataManager.Instace().voasTemp.clear();
            for (Voa voa : voaOp.findDataByPage(curBookId, OFFSET, index)) {
                //if (voa.voaId % 2 != 0)//第一册的奇数课 英音
                tempList.add(voa);
            }
        } else {
            tempList.addAll(voaOp.findDataByPage(curBookId, OFFSET, index));
        }


        VoaDataManager.getInstance().voasTemp = VoaDataManager.getInstance().margeTypeToVoa(tempList, curBookType);
        voaList = VoaDataManager.Instace().voasTemp;

        //刷新数据
        voaAdapter.refreshList(voaList);

        //存储在后台播放的会话中
        ConceptBgPlaySession.getInstance().setVoaList(voaList);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 200:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ToastUtil.showToast(mContext, "您已同意权限，请点击下载按钮进行下载");
                } else {
                    ToastUtil.showToast(mContext, "请同意相关权限");
                }
                break;
            default:
                break;
        }
    }

    /*public void updateIsRead() {
        List<Voa> tmpVoaList = voaOp.findDataByBook(curBook);
        for (int i = 0, j = 0; i < voaList.size() && j < tmpVoaList.size(); ) {
            Voa voa = voaList.get(i);
            Voa tmpVoa = tmpVoaList.get(j);
            if (voa.voaId == tmpVoa.voaId) {
                voa.isRead = tmpVoa.isRead;
                i++;
                j++;
            } else if (voa.voaId < tmpVoa.voaId) {
                i++;
            } else {
                j++;
            }
            //Log.e("run", "IsRead");
        }
    }*/

    /**
     * 初始化搜索
     */
    public void initSearch() {
        binding.searchIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.searchTitleLayout.setVisibility(View.VISIBLE);
                binding.textbookMainbody.setVisibility(View.GONE);
                searchAppointText();
            }
        });
        binding.buttonComplete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                binding.searchTitleLayout.setVisibility(View.VISIBLE);
                binding.textbookMainbody.setVisibility(View.GONE);
                searchAppointText();
            }
        });
        binding.editTextSearch.setImeOptions(EditorInfo.IME_ACTION_DONE);
        binding.editTextSearch.setOnEditorActionListener((v, actionId, event) -> {
            binding.searchTitleLayout.setVisibility(View.VISIBLE);
            binding.voaList.setVisibility(View.GONE);
            searchAppointText();
            InputMethodManager imm = (InputMethodManager) mContext
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(binding.editTextSearch.getWindowToken(), 0);
            return true;
        });
        binding.buttonShowSearchLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserInfoManager.getInstance().isLogin()) {
                    NewSearchActivity.start(mContext, null);
                } else {
                    ToastUtil.showToast(mContext, "未登录，请先登录");
                    LoginUtil.startToLogin(mContext);
                }
            }
        });

        binding.buttonBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.searchTitleLayout.setVisibility(View.GONE);
                binding.voaTitleLayout.setVisibility(View.VISIBLE);

                binding.searchResultLayout.setVisibility(View.GONE);
                binding.textbookMainbody.setVisibility(View.VISIBLE);

                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.getRoot().getWindowToken(), 0); //强制隐藏键盘
            }
        });

        searchVoaAdapter = new VoaAdapter(mContext);
        binding.searchList.setAdapter(searchVoaAdapter);

        binding.searchList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                curVoa = searchVoaAdapter.getItem(arg2);
                //这里没啥用，暂时定为-1，因为功能看起来已经被废弃了
                //要是开启的话，这里的位置是用户判断当前章节的位置，用于处理单词界面的四个训练功能(前三章节免费，后面的会员使用)
                getVoaDetail(-1);
                searchVoaAdapter.getView(arg2, arg1, arg0);
//                searchVoaAdapter.currViewHolder.voaN.setTextColor(Constant.readColor);
                searchVoaAdapter.currViewHolder.title.setTextColor(Constant.readColor);
                searchVoaAdapter.currViewHolder.titleCn.setTextColor(Constant.readColor);
            }
        });

        binding.searchList.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case OnScrollListener.SCROLL_STATE_IDLE: // 当不滚动时
                        // 判断滚动到底部
                        if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                            if (searchVoaList != null && searchVoaList.size() != 0) {
                                if (searchVoaList.size() > (searchCurrPages - 1) * pageNum) {
                                    List<Voa> tempVoaList = new ArrayList<Voa>();
                                    int from = (searchCurrPages - 1) * pageNum;
                                    int to = searchCurrPages * pageNum;

                                    for (int i = from; i < to; i++) {
                                        if (searchVoaList.size() - 1 >= i) {
                                            tempVoaList.add(searchVoaList.get(i));
                                        }
                                    }
                                    searchVoaAdapter.addList(tempVoaList);
                                }
                                searchCurrPages += 1;

                                searchVoaAdapter.notifyDataSetChanged();
                                Log.d("刷新列表数据", "数据显示17");
                            }
                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

            }
        });
    }

    private void searchAppointText() {
        String inputText = binding.editTextSearch.getText().toString();
        if (inputText.toString().equals("")) {
            CustomToast.showToast(mContext, R.string.pelease_write_search_content, 1000);
        } else {
            searchVoaAdapter.clearList();
            searchWord = inputText;

            searchCurrPages = 1;
            searchVoaList = voaOp.getSearchResult(searchWord);
            Log.e("searchVoaList", searchVoaList.size() + "***");
            Log.e("searchWord", searchWord + "***");

            if (searchVoaList != null && searchVoaList.size() != 0) {
                if (searchVoaList.size() > (searchCurrPages - 1)) {
                    List<Voa> tempVoaList = new ArrayList<Voa>();
                    int from = (searchCurrPages - 1) * pageNum;
                    int to = searchCurrPages * pageNum;

                    Voa tempVoa;
                    for (int i = from; i < to; i++) {
                        if (searchVoaList.size() - 1 >= i) {
                            tempVoa = searchVoaList.get(i);
                            // tempVoa.setTitleCn(tempVoa.getTitleCn());
                            // + "\n(匹配：标题"+ tempVoa.getTitleFind() + "次，"
                            // + "句子" + tempVoa.getTextFind() + "次)");
                            tempVoaList.add(tempVoa);
                        }
                    }
                    searchVoaAdapter.addList(tempVoaList);
                }
                searchCurrPages += 1;

                searchVoaAdapter.notifyDataSetChanged();
                Log.d("刷新列表数据", "数据显示18");
            } else {
                searchVoaAdapter.addList(new ArrayList<>());
                searchVoaAdapter.notifyDataSetChanged();
                Log.d("刷新列表数据", "数据显示19");
            }

            binding.searchInfo.setText(getResources().getString(R.string.category_search_info_1) + inputText + getResources().getString(R.string.category_search_info_2));

            binding.textbookMainbody.setVisibility(View.GONE);
            binding.searchResultLayout.setVisibility(View.VISIBLE);
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(binding.searchInfo.getWindowToken(), 0);
        }
    }

    //同步微课记录
    private void syncHomeMocProgress() {
        String type = TypeLibrary.BookType.conceptFourUS;
        if (ConceptBookChooseManager.getInstance().getBookType().equals(TypeLibrary.BookType.conceptFourUS)) {
            type = RefreshMicroReadPercentageAPI.TYPE_US;
        } else {
            type = RefreshMicroReadPercentageAPI.TYPE_UK;
        }

        FixRemoteManager.getHomeMocSync(ConceptBookChooseManager.getInstance().getBookId(), type, UserInfoManager.getInstance().getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CommonResponce<List<Voa>>>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {

                    }

                    @Override
                    public void onNext(CommonResponce<List<Voa>> bean) {
                        if (bean != null && bean.data != null) {
                            mMicroList.clear();
                            for (int i = 0; i < bean.data.size(); i++) {
                                Voa tempVoa = bean.data.get(i);
                                mMicroList.put(tempVoa.voaId, tempVoa);
                            }

                            //关闭刷新课程数据
//                                mHandler.sendEmptyMessageDelayed(WAIT_ADAPTER_INIT, 200);
                            voaAdapter.notifyDataSetChanged();
                            Log.d("刷新列表数据", "数据显示20");
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {

    }

    /**
     * 滑动监听，当滑动到最底部的时候，
     * 从数据库中拉取lesson的标题数据
     *
     * @param view
     * @param scrollState
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case OnScrollListener.SCROLL_STATE_IDLE: // 当不滚动时
                // 判断滚动到底部
                if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                    index = index + OFFSET;
                    List<Voa> voaListTemp = voaOp.findDataByPage(curBookId, OFFSET, index);

                    if (voaListTemp != null) {
                        voaList.addAll(voaListTemp);
                        voaAdapter.notifyDataSetChanged();
                        Log.d("刷新列表数据", "数据显示1");
                    }
                }
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("HomeFragment", "onPause");
        MobclickAgent.onPause(mContext);

        isToOtherPage = true;
        Log.d("后台音频播放", "显示跳转--" + isToOtherPage);
    }

    /**
     * resume的时候重新加载
     */
    @Override
    public void onResume() {
        //从本地更新数据
//        updateBook();
//        updateIsRead();
        super.onResume();
        MobclickAgent.onResume(mContext);

        //更新列表进度
        refreshAdapter();

        //设置界面标志
        isToOtherPage = false;
        Log.d("后台音频播放", "显示跳转--" + isToOtherPage);

        //暂时关闭
        /*if (ConfigManager.Instance().loadBoolean("unitTitleChange", false)) {
            //从数据库中查询出 lesson的 标题内容
            List<Voa> tempList = voaOp.findDataByPage(curBookId, OFFSET, index);
            VoaDataManager.Instace().voasTemp = VoaDataManager.getInstance().margeTypeToVoa(tempList, curBookType);
            voaList = VoaDataManager.Instace().voasTemp;
            voaAdapter.setmList(voaList);
            voaAdapter.notifyDataSetChanged();
            Log.d("刷新列表数据", "数据显示2");
            ConfigManager.Instance().putBoolean("unitTitleChange", false);
        }*/
    }

    private void setPlayInfo(Voa voa) {
        if (voa == null || voa.voaId == 0) {
            return;
        }

        int index = voa.voaId % 1000;
        if (curBookType.equals(TypeLibrary.BookType.conceptJunior)) {
            binding.tvTitle.setText(voa.title);
        } else {
            binding.tvTitle.setText("Lesson" + " " + index + "  " + voa.title);
        }
        binding.tvTitleCn.setText(voa.titleCn);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(RefreshBookEvent event) {
//        index = 0;
//        init();

        getVoaList();
    }

    /***********************************刷新格外的数据*******************/
    //口语秀内容是否加载完毕
    private boolean isTalkShowLoaded = false;
    //单词数据是否加载完毕
    private boolean isWordLoaded = false;

    //加载口语秀
    private void refreshTalkShow() {
        if (ConceptBookChooseManager.getInstance().getBookType().equals(TypeLibrary.BookType.conceptJunior)) {
            //这里增加接口，如果是青少版数据则获取口语秀数据，然后插入特定的数据库，需要的时候从数据库获取展示
            curBookId = ConceptBookChooseManager.getInstance().getBookId();
            List<TalkLesson> list = TalkShowDBManager.getInstance().findTalkByBookId(String.valueOf(curBookId));
            if (list != null && list.size() > 0) {
                isTalkShowLoaded = true;
            }

            List<VoaWord2> wordList = WordChildDBManager.getInstance().findDataByBookId(String.valueOf(curBookId));
            if (wordList != null && wordList.size() > 0) {
                isWordLoaded = true;
            }

            if (isTalkShowLoaded && isWordLoaded) {
                return;
            }

            showLoadingDialog("正在加载数据～");

            //口语秀的数据
            loadTalkShowData(String.valueOf(curBookId));
            //单词数据同步进行更新，因为刚进来没有单词数据
            loadYouthWordData(String.valueOf(curBookId));
        }
    }

    //加载口语秀的接口(该项目经过多人之手，逻辑太过于混乱，还不如重新写呢)
    //http://apps.iyuba.cn/iyuba/getTitleBySeries.jsp?type=title&seriesid=278&sign=22a9b2432482141d478e15f1a9caafd4
    private void loadTalkShowData(String bookId) {
        DataManager.getInstance().getTalkLesson(bookId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<TalkLesson>>() {
                    @Override
                    public void accept(List<TalkLesson> talkLessons) throws Exception {
                        isTalkShowLoaded = true;

                        closeLoadingDialog();

                        if (talkLessons != null && talkLessons.size() > 0) {
                            TalkShowDBManager.getInstance().saveData(talkLessons);
                        }

                        refreshLocalList();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        isTalkShowLoaded = true;

                        refreshLocalList();
                    }
                });
    }

    //加载单词的接口
    //http://apps.iyuba.cn/iyuba/getWordByUnit.jsp?bookid=278
    private void loadYouthWordData(String bookId) {
        DataManager.getInstance().getChildWords(bookId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<VoaWord2>>() {
                    @Override
                    public void accept(List<VoaWord2> list) throws Exception {
                        isWordLoaded = true;
                        if (list != null && list.size() > 0) {
                            SaveWordDataModel saveWordDataModel = new SaveWordDataModel(mContext);
                            saveWordDataModel.saveWordData(list);
                        }

                        refreshLocalList();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        isWordLoaded = true;
                        refreshLocalList();
                    }
                });
    }

    //刷新本地列表
    private void refreshLocalList() {
        if (!isTalkShowLoaded || !isWordLoaded) {
            return;
        }

        closeLoadingDialog();

        voaAdapter.notifyDataSetChanged();
        Log.d("刷新列表数据", "数据显示3");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(IyuAdClickEvent event) {
        startActivity(new Intent(mContext, Web.class).putExtra("url", event.info.linkUrl));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(VipChangeEvent event) {
        index = 0;
        init();

        Log.d("刷新列表数据", "登录后的数据刷新");

        //同步微课记录
        syncHomeMocProgress();
    }

    //列表的操作回调
    private VoaAdapterNew.MyOnclickListener onAdapterListener = new VoaAdapterNew.MyOnclickListener() {
        @Override
        public void onClick(int position, String pageType) {
            //设置跳转的数据
            ConceptBgPlaySession.getInstance().setVoaList(voaList);
            //取消临时数据
            ConceptBgPlaySession.getInstance().setTempData(false);

            //如果是原文界面，则没啥问题；如果是其他界面，则暂停播放
            if (!pageType.equals(TypeLibrary.StudyPageType.read)
                    && !pageType.equals(TypeLibrary.StudyPageType.temp)) {
                EventBus.getDefault().post(new ConceptBgPlayEvent(ConceptBgPlayEvent.event_control_pause));
                EventBus.getDefault().post(new ConceptBgPlayEvent(ConceptBgPlayEvent.event_audio_pause));
            }

            if (pageType.equals(TypeLibrary.StudyPageType.word)) {
                //单词闯关
//            ((MainFragmentActivity) mContext).gotoPassFragment();
                //这里设置通知操作，说明需要跳转到单词界面
                EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.word_pass));
            } else {
                if (VoaDataManager.Instace().voasTemp != null
                        && voaList != null
                        && VoaDataManager.Instace().voasTemp.size() < voaList.size()) {

                    //统一更换方法
                    /*if (curBookType.equals(TypeLibrary.BookType.conceptJunior)) {
                        getYouthList();
                    } else {
                        getFourVolume();
                    }*/
                    getVoaList();
                }

                //当前数据
                VoaDataManager.Instace().voaTemp = voaList.get(position);
                curVoa = voaList.get(position);

                //重新设置详情和当前数据
                if (VoaDataManager.getInstance().voaTemp.lessonType.equals(TypeLibrary.BookType.conceptJunior)) {
                    VoaDataManager.getInstance().voaDetailsTemp = new VoaDetailYouthOp(getActivity()).getVoaDetailByVoaid(curVoa.voaId);
                } else {
                    VoaDataManager.getInstance().voaDetailsTemp = new VoaDetailOp(getActivity()).findDataByVoaId(curVoa.voaId);
                }

                curVoaId = curVoa.voaId;
                Constant.category = curVoa.categoryid;
                //跳转界面的类型
                studyPage = pageType;
                getVoaDetail(position);// 准备数据
                curBookId = ConceptBookChooseManager.getInstance().getBookId();
                switch (curBookId) {
                    case 1:
                        ConfigManager.Instance().putInt("lately_one", curVoaId);
                        break;
                    case 2:
                        ConfigManager.Instance().putInt("lately_two", curVoaId);
                        break;
                    case 3:
                        ConfigManager.Instance().putInt("lately_three", curVoaId);
                        break;
                    case 4:
                        ConfigManager.Instance().putInt("lately_four", curVoaId);
                        break;
                }
                if (!"2".equals(curVoa.isRead)) {
                    voaOp.updateIsRead(curVoaId, "1");
                    curVoa.isRead = "1";
                }
                //暂时屏蔽
//            voaAdapter.notifyItemChanged(position);
                voaOp.updateReadCount(curVoaId);
                MultipleChoiceActivity.instance = null;
            }
        }
    };

    private boolean isLoadAD() {
        BookOp bookOp = new BookOp(mContext);
        List<Book> bookList = bookOp.findData();
        List<Integer> integers = new ArrayList<>();
        for (int i = 0; i < bookList.size(); i++) {
            integers.add(bookList.get(i).totalNum);
        }
        if (integers.contains(binding.voaList.getAdapter().getItemCount())) {
            return false;
        }

        return true;
    }

    private void updateArticleRecord() {
        //同步听力数据
        Log.e("开始时间", new Date() + "");
        CustomDialog customDialog = WaittingDialog.showDialog(mContext);
        customDialog.show();
        //拉取历史记录
        PullHistoryDetailUtil util = new PullHistoryDetailUtil(mContext, new PullHistoryDetailUtil.Callback() {
            @Override
            public void callback() {
                customDialog.dismiss();
                EventBus.getDefault().post(new VipChangeEvent());
            }
        });
        util.startPull();

        RecordUpdate.getInstance().update(new RecordUpdate.UpdateCallback() {
            @Override
            public void updateSuccess() {

            }

            @Override
            public void updateError() {

            }
        });
    }


    public int getCounts(List<VoaDetail> details) {
        int allWords = 0;
        if (details != null) {
            for (VoaDetail detail : details) {
                allWords += getWordCounts(detail.sentence);
                Log.e("Tag-content:", detail.sentence + "count:" + getWordCounts(detail.sentence));
            }
        }
        return allWords;
    }

    public int getWordCounts(String content) {
        return content.split(" ").length;
    }

    /**
     * 计算提交时间段的单词数
     */
    /*private int wordsCount(String endTime, MediaPlayer player, int words) {
        int wordNum = 0;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);// 设置日期格式

        try {

            if (TextUtils.isEmpty(BackgroundManager.Instace().bindService.startTime)) {
                return wordNum;
            }
            int timeAll = (int) ((df.parse(endTime).getTime() - df.parse(BackgroundManager.Instace().bindService.startTime).getTime()));
            wordNum = timeAll * words / player.getDuration();
            if (wordNum > words) {
                wordNum = words;
            }
            Log.e("提交学习记录", BackgroundManager.Instace().bindService.startTime + "==" + endTime + "==" + wordNum);

        } catch (Exception e) {
            e.printStackTrace();
            wordNum = 0;
        }
        return wordNum;
    }*/

    /*private void startSubmit(boolean isEnd){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINESE);// 设置日期格式
        String endTime = df.format(new Date());
        double second=BackgroundManager.Instace().bindService.getPlayer().getCurrentPosition() * 1.00 / 1000;
        String testNumber = String.valueOf(VoaDataManager.Instace().subtitleSum.getParagraph(second));
        new Thread(new UpdateStudyRecordNewThread(VoaDataManager.Instace().voaTemp.voaId,Integer.parseInt(testNumber),endTime,uploadWords,isEnd,true,false)).start();
    }*/

    /*public void event(){
        if (BackgroundManager.Instace().bindService.getPlayer()==null){
            return;
        }
        IJKPlayer player = BackgroundManager.Instace().bindService.getPlayer();
        player.setOnCompletionListener(i -> {
            int allWords = getCounts(voaDetailOp.findDataByVoaId(VoaDataManager.Instace().voaTemp.voaId));
            String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date());
            uploadWords = wordsCount(endTime, player, allWords);
            startSubmit(true);
        });
    }*/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(DownloadLessonEvent event) {
        //刷新数据
        voaAdapter.notifyDataSetChanged();
        Log.d("刷新列表数据", "下载操作刷新");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(UpdateUnitTitleEvent event) {
        Timber.d("UpdateUnitTitle download complete!");
//        voaAdapter.notifyDataSetChanged();
        //这里设置成初始化数据刷新
        init();
    }

    /*@Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RefreshDataEvent event){
        if (event.getType().equals(TypeLibrary.RefreshDataType.concept_play)){
            if (BackgroundManager.Instace().bindService != null) {
                MediaPlayer extendedPlayer = BackgroundManager.Instace().bindService.getPlayer();
                if (extendedPlayer != null) {
                    if (extendedPlayer.isPlaying()) {

                        BackgroundManager.Instace().bindService.notifyNotification(VoaDataManager.Instace().voaTemp,false);

                        extendedPlayer.pause();
                        imgPlay.setImageResource(R.drawable.image_play);
                        bean = articleRecordOp.getData(VoaDataManager.Instace().voaTemp.voaId);
                        if (bean == null) {
                            bean = new ArticleRecordBean();
                            bean.voa_id = VoaDataManager.Instace().voaTemp.voaId;
                            bean.curr_time = extendedPlayer.getCurrentPosition() / 1000;
                            bean.percent = VoaDataManager.Instace().subtitleSum.getParagraph(
                                    extendedPlayer
                                            .getCurrentPosition() * 1.00 / 1000);
                            bean.total_time = extendedPlayer.getDuration() / 1000;
                            bean.is_finish = 0;
                            articleRecordOp.updateData(bean);
                        } else if (bean.is_finish == 0 && bean.curr_time < extendedPlayer.getCurrentPosition() / 1000) {
                            bean.curr_time = extendedPlayer.getCurrentPosition() / 1000;
                            bean.percent = VoaDataManager.Instace().subtitleSum.getParagraph(
                                    extendedPlayer
                                            .getCurrentPosition() * 1.00 / 1000);
                            bean.total_time = extendedPlayer.getDuration() / 1000;
                            articleRecordOp.updateData(bean);
                        }

//                        new Thread(new UpdateStudyRecordunfinishThread()).start();

                    }
                    if (extendedPlayer.getDuration() != 0) {
                        listenPercent = 100 * extendedPlayer.getCurrentPosition() / extendedPlayer.getDuration();
                    }
                    refreshVoaAdapter(500);
                    if (VoaDataManager.Instace().voaTemp!=null){
                        int allWords = getCounts(voaDetailOp.findDataByVoaId(VoaDataManager.Instace().voaTemp.voaId));
                        String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date());
                        uploadWords = wordsCount(endTime, extendedPlayer, allWords);
                    }
                    EventBus.getDefault().post(new PlayControlEvent());
                }
            }
        }
    }*/

    /*********************加载弹窗*******************/
    //显示弹窗
    private void showLoadingDialog(String msg) {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(getActivity());
            loadingDialog.create();
        }
        loadingDialog.setMessage(msg);

        if (isShowUser) {
            loadingDialog.show();
        }
    }

    //关闭弹窗
    private void closeLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    /*****************************新的后台播放*****************************/
    //是否切换到其他界面
    private boolean isToOtherPage = false;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlayEvent(ConceptBgPlayEvent event) {
        //加载完成
        if (event.getShowType().equals(ConceptBgPlayEvent.event_audio_prepareFinish)) {
            if (!isToOtherPage) {
                Log.d("后台音频播放", "处理方式1");
                EventBus.getDefault().post(new ConceptBgPlayEvent(ConceptBgPlayEvent.event_control_play));
            }
        }

        //播放完成
        if (event.getShowType().equals(ConceptBgPlayEvent.event_audio_completeFinish)) {
            EventBus.getDefault().post(new ConceptBgPlayEvent(ConceptBgPlayEvent.event_control_pause));
            //当前的播放器和数据
            Voa curVoa = ConceptBgPlaySession.getInstance().getCurData();
            ExoPlayer exoPlayer = ConceptBgPlayManager.getInstance().getPlayService().getPlayer();
            //这里刷新当前的播放进度
            ArticleRecordBean bean = new ArticleRecordBean();
            bean.voa_id = curVoa.voaId;
            bean.is_finish = 1;
            if (exoPlayer != null && exoPlayer.getDuration() > 0) {
                bean.total_time = (int) (exoPlayer.getDuration() / 1000L);
                bean.curr_time = (int) (exoPlayer.getDuration() / 1000L);
            }
            new ArticleRecordOp(mContext).updateData(bean);
            //刷新显示
            refreshAdapter();

            //如果在当前界面没有切换，则进行处理
            if (!isToOtherPage) {
                EventBus.getDefault().post(new ConceptBgPlayEvent(ConceptBgPlayEvent.event_audio_switch));
            }
        }

        //播放
        if (event.getShowType().equals(ConceptBgPlayEvent.event_control_play)) {
            Voa voa = ConceptBgPlaySession.getInstance().getCurData();
            if (voa != null) {
                //进行播放
                ExoPlayer exoPlayer = ConceptBgPlayManager.getInstance().getPlayService().getPlayer();
                if (exoPlayer != null && !exoPlayer.isPlaying()) {
                    exoPlayer.play();
                }
                //显示图标
                binding.reBottom.setVisibility(View.VISIBLE);
                binding.imgPlay.setImageResource(R.drawable.image_pause);
                if (voa != null) {
                    binding.tvTitle.setText(voa.title);
                    binding.tvTitleCn.setText(voa.titleCn);
                }
                //通知栏处理
                Log.d("后台音频播放", "操作1");
                ConceptBgPlayManager.getInstance().getPlayService().showNotification(false, true);
            }
        }

        //暂停
        if (event.getShowType().equals(ConceptBgPlayEvent.event_control_pause)) {
            Voa voa = ConceptBgPlaySession.getInstance().getCurData();
            if (voa != null) {
                //暂停播放
                ExoPlayer exoPlayer = ConceptBgPlayManager.getInstance().getPlayService().getPlayer();
                if (exoPlayer != null && exoPlayer.isPlaying()) {
                    exoPlayer.pause();
                }
                //显示图标
                if (binding.reBottom.getVisibility() == View.VISIBLE) {
                    binding.reBottom.setVisibility(View.VISIBLE);
                }
                binding.imgPlay.setImageResource(R.drawable.image_play);
                if (voa != null) {
                    binding.tvTitle.setText(voa.title);
                    binding.tvTitleCn.setText(voa.titleCn);

                    //刷新播放进度
                    Voa curVoa = ConceptBgPlaySession.getInstance().getCurData();
                    //这里刷新当前的播放进度
                    ArticleRecordBean bean = new ArticleRecordBean();
                    bean.voa_id = curVoa.voaId;
                    bean.uid = UserInfoManager.getInstance().getUserId();
                    bean.is_finish = 0;
                    if (exoPlayer != null && exoPlayer.getDuration() > 0) {
                        bean.total_time = (int) (exoPlayer.getDuration() / 1000L);
                        bean.curr_time = (int) (exoPlayer.getCurrentPosition() / 1000L);
                    }
                    new ArticleRecordOp(mContext).updateData(bean);
                    //刷新显示
                    refreshAdapter();
                }
                //通知栏处理
                ConceptBgPlayManager.getInstance().getPlayService().showNotification(false, false);
            }
        }

        //切换音频
        if (event.getShowType().equals(ConceptBgPlayEvent.event_audio_switch)) {
            //根据当前类型选择下一个音频的位置
            ExoPlayer exoPlayer = ConceptBgPlayManager.getInstance().getPlayService().getPlayer();

            int playMode = ConfigManager.Instance().loadInt("mode", 1);
            if (playMode == 0) {
                //单曲循环
                exoPlayer.seekTo(0);
                //这里不需要重新加载
                ConceptBgPlayManager.getInstance().getPlayService().setPrepare(false);

                Log.d("后台音频播放", "处理方式2");
                EventBus.getDefault().post(new ConceptBgPlayEvent(ConceptBgPlayEvent.event_audio_play));
                EventBus.getDefault().post(new ConceptBgPlayEvent(ConceptBgPlayEvent.event_control_play));
            } else if (playMode == 1) {
                //顺序播放
                if (ConceptBgPlaySession.getInstance().getPlayPosition() < ConceptBgPlaySession.getInstance().getVoaList().size() - 1) {
                    //刷新数据显示
                    int nextPosition = ConceptBgPlaySession.getInstance().getPlayPosition() + 1;
                    Voa nextVoa = ConceptBgPlaySession.getInstance().getVoaList().get(nextPosition);
                    VoaDataManager.getInstance().voaTemp = nextVoa;

                    if (isToOtherPage) {
                        EventBus.getDefault().post(new ConceptBgPlayEvent(ConceptBgPlayEvent.event_data_refresh));
                    } else {
                        //获取音频并播放
                        ConceptBgPlaySession.getInstance().setPlayPosition(nextPosition);
                        initPlayerAndPlayAudio();
                    }
                } else {
                    //这里不需要重新加载
                    ConceptBgPlayManager.getInstance().getPlayService().setPrepare(false);
                }
            } else if (playMode == 2) {
                //随机播放
                //获取随机数
                int randomIndex = (int) (ConceptBgPlaySession.getInstance().getVoaList().size() * Math.random());
                if (randomIndex == ConceptBgPlaySession.getInstance().getPlayPosition()) {
                    if (randomIndex == ConceptBgPlaySession.getInstance().getVoaList().size() - 1) {
                        randomIndex--;
                    }
                    randomIndex++;
                }
                Voa nextVoa = ConceptBgPlaySession.getInstance().getVoaList().get(randomIndex);
                VoaDataManager.getInstance().voaTemp = nextVoa;

                if (isToOtherPage) {
                    EventBus.getDefault().post(new ConceptBgPlayEvent(ConceptBgPlayEvent.event_data_refresh));
                } else {
                    //获取音频并播放
                    ConceptBgPlaySession.getInstance().setPlayPosition(randomIndex);
                    initPlayerAndPlayAudio();
                }
            }
        }

        //隐藏控制栏
        if (event.getShowType().equals(ConceptBgPlayEvent.event_control_hide)) {
            EventBus.getDefault().post(new ConceptBgPlayEvent(ConceptBgPlayEvent.event_control_pause));
            //隐藏控制栏
            binding.reBottom.setVisibility(View.GONE);
        }
    }

    //初始化操作音频播放
    private void initPlayerAndPlayAudio() {
        if (TextUtils.isEmpty(getLocalSoundPath())) {
            //获取网络链接
            String soundUrl = getRemoteSoundPath();
            playAudio(soundUrl);
        } else {
            //本地播放
            playAudio(getLocalSoundPath());
        }
    }

    //获取当前章节的音频本地路径
    private String getLocalSoundPath() {
        String localPath = "";

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            return localPath;
        }

        //这里不获取当前的数据，而是获取数据中的类型
        /*switch (VoaDataManager.getInstance().voaTemp.lessonType) {
            case TypeLibrary.BookType.conceptFourUS:
            case TypeLibrary.BookType.conceptJunior:
            default:
                // 美音原文音频的存放路径
                String pathString = Constant.videoAddr + VoaDataManager.getInstance().voaTemp.voaId + Constant.append;
                File fileTemp = new File(pathString);
                if (fileTemp.exists()) {
                    localPath =  pathString;
                }
                break;
            case TypeLibrary.BookType.conceptFourUK:
                // 英音原文音频的存放路径
                String pathStringEng = Constant.videoAddr + VoaDataManager.getInstance().voaTemp.voaId + "_B" + Constant.append;
                File fileTempEng = new File(pathStringEng);
                if (fileTempEng.exists()) {
                    localPath = pathStringEng;
                }
                break;
        }*/

        //更换路径获取方式
        String pathString = FilePathUtil.getHomeAudioPath(VoaDataManager.getInstance().voaTemp.voaId, VoaDataManager.getInstance().voaTemp.lessonType);
        File file = new File(pathString);
        if (file.exists()) {
            localPath = pathString;
        }

        return localPath;
    }

    //获取当前章节的音频网络路径
    private String getRemoteSoundPath() {
        String soundUrl = null;
        //这里针对会员和非会员不要修改，测试也不要修改
        if (UserInfoManager.getInstance().isVip()) {
            soundUrl = "http://staticvip2." + Constant.IYUBA_CN + "newconcept/";
        } else {
            soundUrl = Constant.sound;
        }

//        switch (ConfigManager.Instance().getBookType()) {
        switch (VoaDataManager.getInstance().voaTemp.lessonType) {
            case TypeLibrary.BookType.conceptFourUS:
            default:
                //美音
                soundUrl = soundUrl
                        + VoaDataManager.getInstance().voaTemp.voaId / 1000
                        + "_"
                        + VoaDataManager.getInstance().voaTemp.voaId % 1000
                        + Constant.append;
                break;
            case TypeLibrary.BookType.conceptFourUK: //英音
                soundUrl = soundUrl
                        + "british/"
                        + VoaDataManager.getInstance().voaTemp.voaId / 1000
                        + "/"
                        + VoaDataManager.getInstance().voaTemp.voaId / 1000
                        + "_"
                        + VoaDataManager.getInstance().voaTemp.voaId % 1000
                        + Constant.append;
                break;
            case TypeLibrary.BookType.conceptJunior:
                soundUrl = "http://" + Constant.staticStr + Constant.IYUBA_CN + "sounds/voa/sentence/202005/"
                        + VoaDataManager.getInstance().voaTemp.voaId
                        + "/"
                        + VoaDataManager.getInstance().voaTemp.voaId
                        + Constant.append;
                break;
        }

        return soundUrl;
    }

    //播放音频
    private void playAudio(String urlOrPath) {
        MediaItem mediaItem = null;
        if (urlOrPath.startsWith("http")) {
            mediaItem = MediaItem.fromUri(urlOrPath);
        } else {
            //本地加载
            Uri uri = Uri.fromFile(new File(urlOrPath));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(getActivity(), getResources().getString(R.string.file_provider_name_personal), new File(urlOrPath));
            }
            mediaItem = MediaItem.fromUri(uri);
        }
        ConceptBgPlayManager.getInstance().getPlayService().getPlayer().setMediaItem(mediaItem);
        ConceptBgPlayManager.getInstance().getPlayService().getPlayer().prepare();
    }

    //刷新下载
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadEvent(FileDownloadEvent event) {
        if (event.getShowType().equals(FileDownloadEvent.home)) {
            Log.d("显示下载状态", "当前类型--" + ConceptBookChooseManager.getInstance().getBookType() + "--回调类型：" + event.getBookType());

            if (event.getBookType().equals(ConceptBookChooseManager.getInstance().getBookType())) {
                //设置数据显示
                int refreshPosition = event.getShowDataPosition();
                Log.d("显示下载状态", "界面刷新位置--" + refreshPosition);

                if (refreshPosition != -1) {
                    voaAdapter.notifyItemChanged(refreshPosition);
                } else {
                    voaAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    //刷新首页微课的进度
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HomeMocProgressEvent event) {
        //刷新微课接口
        if (UserInfoManager.getInstance().isLogin()) {
            syncHomeMocProgress();
            Log.d("刷新列表数据", "准备刷新数据1");
        }
    }

    //刷新列表显示(主要是临时刷新原文进度)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RefreshDataEvent event) {
        if (event.getType().equals(StrLibrary.list)) {
            voaAdapter.notifyDataSetChanged();
        }
    }

    /********************************其他方法*********************************/
    //是否跳转刷新
    private void refreshAdapter() {
        if (ConceptHomeRefreshUtil.getInstance().isRefresh()) {
            if (voaAdapter != null) {
                voaAdapter.notifyDataSetChanged();
                Log.d("刷新列表数据", "onresume的数据刷新");
            }
            ConceptHomeRefreshUtil.getInstance().setRefreshState(false);
        }
    }


    /*********************************新的广告处理********************************/
    //当前信息流广告的key
    private String adTemplateKey = HomeFragment.class.getName();
    //模版广告数据
    private AdTemplateViewBean templateViewBean = null;

    //显示广告
    private void showTemplateAd() {
        if (templateViewBean == null) {
            templateViewBean = new AdTemplateViewBean(R.layout.item_ad_mix, R.id.template_container, R.id.ad_whole_body, R.id.native_main_image, R.id.native_title, binding.voaList, voaAdapter, new OnAdTemplateShowListener() {
                @Override
                public void onLoadFinishAd() {

                }

                @Override
                public void onAdShow(String showAdMsg) {

                }

                @Override
                public void onAdClick() {

                }
            });
            AdTemplateShowManager.getInstance().setShowData(adTemplateKey, templateViewBean);
        }
        AdTemplateShowManager.getInstance().showTemplateAd(adTemplateKey, getActivity());
    }

    //刷新广告操作[根据类型判断刷新还是隐藏]
    private void refreshTemplateAd() {
//        if (NetworkUtil.isConnected(getActivity()) && AdInitManager.isShowAd() && !UserInfoManager.getInstance().isVip()) {
//            showTemplateAd();
//        } else {
//            AdTemplateShowManager.getInstance().stopTemplateAd(adTemplateKey);
//        }

        if (NetworkUtil.isConnected(getActivity()) && AdInitManager.isShowAd()){
            showTemplateAd();
        }
    }

    /**************************************新的下载逻辑***********************************/
    //下载弹窗
    private DownloadFixDialog downloadDialog;
    //初始化弹窗
    private void initDownloadDialog(){
        downloadDialog = new DownloadFixDialog(getActivity());
        downloadDialog.setOnSimpleClickListener(new OnSimpleClickListener<String>() {
            @Override
            public void onClick(String s) {
                stopDownloadDialog();
            }
        });
        downloadDialog.create();
        downloadDialog.show();
    }
    //关闭弹窗
    private void stopDownloadDialog(){
        //关闭弹窗
        if (downloadDialog.isShowing()){
            downloadDialog.dismiss();
        }

        //关闭操作
        DownloadFixUtil.getInstance().stopDownload();

        //删除文件
        if (!TextUtils.isEmpty(curDownloadFilePath)){
            File downloadFile = new File(curDownloadFilePath);
            if (downloadFile.exists()){
                downloadFile.delete();
            }
        }
    }

    //下载文件路径
    private String curDownloadFilePath = "";
    //android15级以上的下载操作
    private VoaAdapterNew.OnDownloadProgressListener downloadListener = new VoaAdapterNew.OnDownloadProgressListener() {
        @Override
        public void onDownload(Voa voa, int position) {
            //初始化弹窗
            initDownloadDialog();

            //获取下载文件的路径
            String fileUrl = getRemoteSoundPath(voa);
            curDownloadFilePath = FilePathUtil.getHomeAudioPath(voa.voaId,voa.lessonType);

            //准备下载文件
            DownloadFixUtil.getInstance().downloadFile(fileUrl, curDownloadFilePath, new DownloadFixCallback() {
                @Override
                public void onDownloadState(String downloadState, long progress, long total) {
                    Log.d("下载进度", "准备显示");

                    //判断进度
                    if (total<=0){
                        return;
                    }

                    //计算进度
                    int showProgress = (int) (progress*100/total);

                    //显示样式
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (downloadState){
                                case TypeLibrary.FileDownloadStateType.file_isDownloading:
                                    downloadDialog.setProgress(showProgress);
                                    Log.d("下载进度", progress+"--"+total+"--"+showProgress);
                                    break;
                                case TypeLibrary.FileDownloadStateType.file_downloaded:
                                    //重置路径
                                    curDownloadFilePath = null;
                                    //重置下载操作
                                    DownloadFixUtil.getInstance().stopDownload();
                                    //设置本地数据
                                    ConceptDataManager.updateLocalMarkDownloadStatus(voa.voaId,voa.lessonType,UserInfoManager.getInstance().getUserId(), TypeLibrary.FileDownloadStateType.file_downloaded,position);
                                    //刷新数据
                                    voaAdapter.notifyDataSetChanged();
                                    //关闭弹窗
                                    downloadDialog.dismiss();
                                    Log.d("下载进度", "file_downloaded");
                                    break;
                                case TypeLibrary.FileDownloadStateType.file_downloadFail:
                                    ToastUtil.showToast(getActivity(),"文件下载失败，请重试");
                                    curDownloadFilePath = null;
                                    //设置本地数据
                                    ConceptDataManager.updateLocalMarkDownloadStatus(voa.voaId,voa.lessonType,UserInfoManager.getInstance().getUserId(), TypeLibrary.FileDownloadStateType.file_no,position);
                                    //刷新数据
                                    voaAdapter.notifyDataSetChanged();
                                    //关闭弹窗
                                    downloadDialog.dismiss();
                                    Log.d("下载进度", "file_downloadFail");
                                    break;
                                case TypeLibrary.FileDownloadStateType.file_otherDownload:
                                    ToastUtil.showToast(getActivity(),"正在下载其他文件，请稍后");
                                    //设置本地数据
                                    ConceptDataManager.updateLocalMarkDownloadStatus(voa.voaId,voa.lessonType,UserInfoManager.getInstance().getUserId(), TypeLibrary.FileDownloadStateType.file_no,position);
                                    //刷新数据
                                    voaAdapter.notifyDataSetChanged();
                                    //关闭弹窗
                                    downloadDialog.dismiss();
                                    Log.d("下载进度", "file_otherDownload");
                                    break;
                            }
                        }
                    });
                }
            });
        }
    };

    private String getRemoteSoundPath(Voa tempVoa){
        String soundUrl = null;
        //这里针对会员和非会员不要修改，测试也不要修改
        if (UserInfoManager.getInstance().isVip()&& SettingConfig.Instance().isHighSpeed()){
            soundUrl="http://staticvip2." + Constant.IYUBA_CN + "newconcept/";
        }else {
            soundUrl=Constant.sound;
        }

//        switch (ConfigManager.Instance().getBookType()) {
        switch (tempVoa.lessonType) {
            case TypeLibrary.BookType.conceptFourUS:
            default:
                //美音
                soundUrl = soundUrl
                        + tempVoa.voaId / 1000
                        + "_"
                        + tempVoa.voaId % 1000
                        + Constant.append;
                break;
            case TypeLibrary.BookType.conceptFourUK: //英音
                soundUrl = soundUrl
                        + "british/"
                        + tempVoa.voaId / 1000
                        + "/"
                        + tempVoa.voaId / 1000
                        + "_"
                        + tempVoa.voaId % 1000
                        + Constant.append;
                break;
            case TypeLibrary.BookType.conceptJunior:
                soundUrl = "http://"+Constant.staticStr+Constant.IYUBA_CN+"sounds/voa/sentence/202005/"
                        + tempVoa.voaId
                        + "/"
                        + tempVoa.voaId
                        + Constant.append;
                break;
        }

        return soundUrl;
    }
}