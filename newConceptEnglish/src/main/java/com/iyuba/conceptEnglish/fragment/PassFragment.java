package com.iyuba.conceptEnglish.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.activity.pass.WordLevelListActivity;
import com.iyuba.conceptEnglish.activity.pass.WordPassEvent;
import com.iyuba.conceptEnglish.entity.PassDetail;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.event.RefreshDataEvent;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.StrLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.ConceptBookChooseManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.practise.line.PractiseLineEvent;
import com.iyuba.conceptEnglish.lil.fix.common_fix.view.dialog.LoadingDialog;
import com.iyuba.conceptEnglish.model.PullHistoryThread;
import com.iyuba.conceptEnglish.model.SaveWordDataModel;
import com.iyuba.conceptEnglish.sqlite.db.WordChildDBManager;
import com.iyuba.conceptEnglish.sqlite.op.VoaWordOp;
import com.iyuba.conceptEnglish.sqlite.op.WordPassOp;
import com.iyuba.conceptEnglish.widget.GameStageView;
import com.iyuba.conceptEnglish.widget.dialog.DownloadDialog;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.activity.login.LoginUtil;
import com.iyuba.core.common.data.model.VoaWord2;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.event.DownloadEvent;
import com.iyuba.core.event.VipChangeEvent;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.me.activity.NewVipCenterActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;


@SuppressLint("NonConstantResourceId")
public class PassFragment extends Fragment implements PassMvpView {

    private View rootView;

    @BindView(R.id.titlebar)
    RelativeLayout titleLayout;
    @BindView(R.id.game_state_view)
    GameStageView stageView;
    @BindView(R.id.btn_back)
    TextView btnBack;
    @BindView(R.id.btn_share)
    ImageView btnShare;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.refresh_word)
    SwipeRefreshLayout refreshWord;
    @BindView(R.id.tv_pass)
    TextView tvPass;
    @BindView(R.id.tv_sync)
    ImageView tvSync;
    @BindView(R.id.tv_refresh)
    TextView tvRefresh;

    private boolean isRefresh;

    private Context mContext;
    private int currPass = 0;
    //当前已经通关的最高关数
    private int curMaxPass = 0;

    private WordPassOp wordPassOp;

    private PassPresenter mPresenter;
    private DownloadDialog downloadDialog;
    private CustomDialog waittingDialog;
    private CustomDialog waittingDialog2;
    private int currBookForPass;
    private boolean isPackageYouth = false;

    //是否点击了按钮
    boolean isClickDataRefreshBtn = false;

    public static PassFragment getInstance(boolean isHideToolbar) {
        PassFragment fragment = new PassFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(StrLibrary.hideToolbar, isHideToolbar);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new PassPresenter(getActivity());
        mPresenter.attachView(this);

        isPackageYouth = getActivity().getPackageName().equals("com.iyuba.youth");
        if (isPackageYouth) {
            ConfigManager.Instance().setCurrBookforPass(278);
        }

        //针对个别包名设置默认数据
        if (getActivity().getPackageName().equals(Constant.package_learnNewEnglish)
                || getActivity().getPackageName().equals(Constant.package_conceptStory)) {
            currBookForPass = ConceptBookChooseManager.getInstance().getBookId();
            ConfigManager.Instance().setCurrBookforPass(currBookForPass);
        }else {
            currBookForPass = ConfigManager.Instance().getCurrBookforPass();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_pass, container, false);
        }
        mContext = getActivity();
        waittingDialog = WaittingDialog.showDialog(mContext);
        waittingDialog2 = WaittingDialog.showDialog(mContext);
        ButterKnife.bind(this, rootView);
        wordPassOp = new WordPassOp(mContext);
        initTopBar();
        initStageView();
        refreshWord.setColorSchemeResources(R.color.app_color, R.color.yellow, R.color.red);
        refreshWord.setOnRefreshListener(() -> {
            if (!UserInfoManager.getInstance().isLogin()) {
                refreshWord.setRefreshing(false);
                ToastUtil.showToast(getActivity(), "请登录后更新历史进度");
                return;
            }

            updatePASSData(true);
            setHeadImage();
        });
        setHeadImage();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();

        isClickDataRefreshBtn = false;
    }

    private void initStageView() {
        stageView.setOnGameItemClickListener(new GameStageView.OnGameItemClickListener() {
            @Override
            public void onGameItemClick(int position, int max) {
                if (!UserInfoManager.getInstance().isLogin()) {
                    LoginUtil.startToLogin(getActivity());
                    return;
                }

                if (position <= 1) {
                    WordLevelListActivity.start(getActivity(), position, true);
                    return;
                }

                int currBookForPass = ConfigManager.Instance().getCurrBookforPass();
                int flag = 0;
                if (currBookForPass == 281 || currBookForPass == 283 || currBookForPass == 285) {
                    flag = 15;
                    position = position + flag;
                } else if (currBookForPass == 287 || currBookForPass == 289) {
                    flag = 24;
                    position = position + flag;
                }
                max += flag;

                //判断会员和非会员
                if (UserInfoManager.getInstance().isVip()) {
                    if (position > max) {
                        // TODO: 2023/11/22 这里根据展姐在新概念群组中的要求，会员可以点击未解锁的单元，但是不能闯关，只能学习
                        WordLevelListActivity.start(getActivity(), position, false);
                    } else {
                        WordLevelListActivity.start(getActivity(), position, true);
                    }
                } else {
                    showDialog();
                }
            }
        });
        currBookForPass = ConfigManager.Instance().getCurrBookforPass();//?
        currPass = wordPassOp.getCurrPassNum(currBookForPass);//获取当前关数

        //这里更换为显示最大关数，不是动态显示当前的通关数
        //这里同步下数据：如果存在数据，则直接使用数据库数据处理；如果没有没有数据，则需要先同步
        /*WordPassHelpEntity entity = CommonDataManager.getConceptWordPassHelpData(currBookForPass);
        if (entity==null){
            curMaxPass = wordPassOp.getCurrPassNum(currBookForPass);//获取当前关数
            WordPassHelpEntity helpEntity = new WordPassHelpEntity(currBookForPass,curMaxPass);
            CommonDataManager.saveConceptWordPassHelpData(helpEntity);
        }else {
            curMaxPass = entity.showPassPosition;
        }*/

        List<PassDetail> list = wordPassOp.getPassDetailList(getMiniVoaidAboutBook(currBookForPass), currPass);
        stageView.setPassDetailList(list);
        //stageView.setNowPosition(currPass);unitID 处理 太恶心了，
        if (currBookForPass > 4) {
//            stageView.setTitle("Unit ");
            if (currBookForPass == 281 || currBookForPass == 283 || currBookForPass == 285) {
                stageView.setAddLessonNum(15);
                stageView.setNowPosition(currPass > 15 ? currPass - 15 : currPass);//若currPass>15，nowPosition = currPass -15；否则nowPosition = currPass
            } else if (currBookForPass == 287 || currBookForPass == 289) {
                stageView.setAddLessonNum(24);
                stageView.setNowPosition(currPass > 24 ? currPass - 24 : currPass);
            } else {
                stageView.setAddLessonNum(0);
                stageView.setNowPosition(currPass);
            }
            tvRefresh.setVisibility(View.VISIBLE);
        } else {
//            stageView.setTitle("Lesson ");
            stageView.setAddLessonNum(0);
            stageView.setNowPosition(currPass);
            tvRefresh.setVisibility(View.GONE);
        }
        getData(currBookForPass);
        stageView.setLineColumns(3);
        stageView.requestLayout();
    }

    /**
     * 根据课本id（currBookForPass），获取这个课本最小的voaid
     *
     * @param currBookForPass
     * @return
     */
    private int getMiniVoaidAboutBook(int currBookForPass) {
        int miniVoaid = 1001;
        if (currBookForPass > 277) {
            VoaWordOp voaWordOp = new VoaWordOp(mContext);
            miniVoaid = voaWordOp.getMiniVoaidByBookid(currBookForPass);
//            VoaOp voaOp = new VoaOp(mContext);
//            miniVoaid = voaOp.getMiniVoaidByCategory(currBookForPass);
        } else {
            switch (currBookForPass) {
                case 1:
                    miniVoaid = 1001;
                    break;
                case 2:
                    miniVoaid = 2001;
                    break;
                case 3:
                    miniVoaid = 3001;
                    break;
                case 4:
                    miniVoaid = 4001;
                    break;
            }
        }
        return miniVoaid;
    }

    /**
     * 拉取单词数据
     *
     * @param currBookForPass
     */
    private void getData(int currBookForPass) {
        switch (currBookForPass) {
            case 1:
                tvPass.setText("新概念英语第一册共1062个单词");
                stageView.setTotalColumns(144);
                break;
            case 2:
                tvPass.setText("新概念英语第二册共858个单词");
                stageView.setTotalColumns(96);
                break;
            case 3:
                tvPass.setText("新概念英语第三册共1038个单词");
                stageView.setTotalColumns(60);
                break;
            case 4:
                tvPass.setText("新概念英语第四册共788个单词");
                stageView.setTotalColumns(48);
                break;
            case 278:
                setData("278", "新概念青少版StarterA");
                break;
            case 279:
                setData("279", "新概念青少版StarterB");
                break;
            case 280:
                setData("280", "新概念青少版1A");
                break;
            case 281:
                setData("281", "新概念青少版1B");
                break;
            case 282:
                setData("282", "新概念青少版2A");
                break;
            case 283:
                setData("283", "新概念青少版2B");
                break;
            case 284:
                setData("284", "新概念青少版3A");
                break;
            case 285:
                setData("285", "新概念青少版3B");
                break;
            case 286:
                setData("286", "新概念青少版4A");
                break;
            case 287:
                setData("287", "新概念青少版4B");
                break;
            case 288:
                setData("288", "新概念青少版5A");
                break;
            case 289:
                setData("289", "新概念青少版5B");
                break;
            default:
                tvPass.setText("新概念青少版");
                stageView.setTotalColumns(0);
        }

    }

    private void initTopBar() {
        btnBack.setVisibility(View.INVISIBLE);
        tvTitle.setText("单词闯关");
        btnShare.setOnClickListener(v -> {
            showTopData();
        });
        tvSync.setOnClickListener(v -> {
            if (!UserInfoManager.getInstance().isLogin()) {
                ToastUtil.showToast(getActivity(), "请登录后更新历史进度");
                return;
            }

            updatePASSData(false);
        });
        tvRefresh.setOnClickListener(v -> {
            //将点击按钮开启
            isClickDataRefreshBtn = true;

            ConfigManager config = ConfigManager.Instance();
            setData(config.getCurrBookId(), config.getCurrBookTitle(), true);
            isRefresh = true;

            startLoading("正在加载数据～");
        });

        //隐藏或者展示toolbar
        if (getArguments() != null) {
            boolean isHideToolbar = getArguments().getBoolean(StrLibrary.hideToolbar, false);
            if (isHideToolbar) {
                titleLayout.setVisibility(View.GONE);
            }
        }
    }

    //头部日常数据展示
    private void showTopData() {
        String[] strings = new String[]{"第一册（1062个单词）", "第二册（858个单词）",
                "第三册（1038个单词）", "第四册（788个单词）", "StarterA（94个单词）",
                "StarterB（130个单词）", "青少版1A（368个单词）", "青少版1B（346个单词）",
                "青少版2A（308个单词）", "青少版2B（276个单词）", "青少版3A（320个单词）",
                "青少版3B（197个单词）", "青少版4A（493个单词）", "青少版4B（433个单词）",
                "青少版5A（378个单词）", "青少版5B（315个单词）"};
        String[] strings2 = new String[]{"StarterA（94个单词）",
                "StarterB（130个单词）", "青少版1A（368个单词）", "青少版1B（346个单词）",
                "青少版2A（308个单词）", "青少版2B（276个单词）", "青少版3A（320个单词）",
                "青少版3B（197个单词）", "青少版4A（493个单词）", "青少版4B（433个单词）",
                "青少版5A（378个单词）", "青少版5B（315个单词）"};
        ArrayList<Integer> bookId = new ArrayList<>();
        if (!isPackageYouth) {
            bookId.add(1);
            bookId.add(2);
            bookId.add(3);
            bookId.add(4);
        }
        bookId.add(278);//278
        bookId.add(279);//279
        bookId.add(280);//280
        bookId.add(281);//281
        bookId.add(282);//282
        bookId.add(283);//283
        bookId.add(284);//284
        bookId.add(285);//285
        bookId.add(286);//286
        bookId.add(287);//287
        bookId.add(288);//288
        bookId.add(289);//289

        AlertDialog itemDialog = new AlertDialog.Builder(mContext)
                .setSingleChoiceItems(isPackageYouth ? strings2 : strings,
                        findSelectItemIndex(bookId, currBookForPass), (dialog, which) -> {
                            //设置当前的bookId
                            currBookForPass = bookId.get(which);
                            //保存数据
                            ConfigManager.Instance().setCurrBookforPass(currBookForPass);
                            dialog.dismiss();
                            initStageView();
                        })
                .setPositiveButton("取消", null)
                .create();
        itemDialog.show();
        Window window = itemDialog.getWindow();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        float scale = 0.8f;
        window.setLayout(width, (int) (height * scale));
    }

    //头部限制数据展示
    private void showTopLimitData() {
        String[] strings = new String[]{"第一册（1062个单词）", "第二册（858个单词）",
                "第三册（1038个单词）", "第四册（788个单词）"};
        ArrayList<Integer> bookId = new ArrayList<>();
        bookId.add(1);
        bookId.add(2);
        bookId.add(3);
        bookId.add(4);

        AlertDialog itemDialog = new AlertDialog.Builder(mContext)
                .setSingleChoiceItems(strings,
                        findSelectItemIndex(bookId, currBookForPass), (dialog, which) -> {
                            ConfigManager.Instance().setCurrBookforPass(bookId.get(which));
                            dialog.dismiss();
                            initStageView();
                        })
                .setPositiveButton("取消", null)
                .create();
        itemDialog.show();
    }

    private void setData(String bookId, String title) {
        setData(bookId, title, false);
    }

    private void setData(String bookId, String title, boolean isRefresh) {
        List<VoaWord2> word2List = WordChildDBManager.getInstance().findDataByBookId(bookId);
        if (isRefresh || word2List == null || word2List.isEmpty()) {
            if (waittingDialog == null) {
                waittingDialog = WaittingDialog.showDialog(mContext);
            }
            if (!waittingDialog.isShowing()) {
                waittingDialog.show();
            }

            mPresenter.getChildWords(bookId, title);
        } else {
            tvPass.setText(MessageFormat.format("{0}共{1}个单词", title, word2List.size()));
        }
        if (bookId.equals("286") || bookId.equals("287") || bookId.equals("288") || bookId.equals("289")) {
            stageView.setTotalColumns(24);
        } else if (bookId.equals("279")) {
            stageView.setTotalColumns(13);
        } else {
            stageView.setTotalColumns(15);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mPresenter.getObObject() != null) {
            mPresenter.stopOb();
        }
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        mPresenter.detachView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(WordPassEvent event) {
        initStageView();//闯关完成的
        int currBookforPass = ConfigManager.Instance().getCurrBookforPass();
        currPass = wordPassOp.getCurrPassNum(currBookforPass);//获取当前关数
        Timber.d("闯关完成" + currPass);

        // TODO: 2025/4/2 刷新练习题的单词数据显示
        EventBus.getDefault().post(new PractiseLineEvent(PractiseLineEvent.event_word));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(VipChangeEvent event) {
        initStageView();
        setHeadImage();
    }

    private void setHeadImage() {
        if (UserInfoManager.getInstance().isLogin()) {
            String imageUrl = "http://api." + Constant.IYUBA_COM + "v2/api.iyuba?protocol=10005&uid=" + UserInfoManager.getInstance().getUserId() + "&size=middle";
            /*Glide.with(mContext)
                    .asBitmap()
                    .load(imageUrl)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.ic_w_head)
                    .error(R.drawable.ic_w_head)
                    .dontAnimate()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            stageView.setHeadBitmap(resource);
                            stageView.invalidate();
                        }
                    });*/
            Glide.with(mContext)
                    .asBitmap()
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.ic_w_head)
                    .error(R.drawable.ic_w_head)
                    .dontAnimate()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            stageView.setHeadBitmap(resource);
                            stageView.invalidate();
                        }
                    });
        } else {
            stageView.setHeadBitmap(null);
            stageView.invalidate();
        }
    }

    @Override
    public void showMessage(String msg) {
        stopLoading();
        if (waittingDialog != null && waittingDialog.isShowing()) {
            waittingDialog.dismiss();
        }
        ToastUtil.showToast(mContext, msg);
    }

    @Override
    public void getChildWordList(List<VoaWord2> list, String title) {
        stopLoading();

        int currBookForPass = ConfigManager.Instance().getCurrBookforPass();//?

        if (list.isEmpty()) {
            showMessage("数据为空");
            tvPass.setText("数据为空");//右上角 title
            return;
        }
        tvPass.setText(title + "共" + list.size() + "个单词");

        //存储单词数据
        SaveWordDataModel saveWordDataModel = new SaveWordDataModel(mContext);
        saveWordDataModel.saveWordData(list);

        List<PassDetail> stageViewList = wordPassOp.getPassDetailList(getMiniVoaidAboutBook(currBookForPass), currPass);
        stageView.setPassDetailList(stageViewList);

        if (waittingDialog != null && waittingDialog.isShowing()) {
            waittingDialog.dismiss();
        }

        //本地数据存储之后下载(这里点击按钮后下载，切换不显示下载)
        if (isClickDataRefreshBtn) {
            showDownloadDialog(ConfigManager.Instance().getCurrBookId());
        }

        //如果下载了才更新下载，没下载更新个锤子
        if (ConfigManager.Instance().loadBoolean(ConfigManager.Instance().getCurrBookId())) {
            if (isRefresh) {
                //走接口更新数据
                String bookId = ConfigManager.Instance().getCurrBookId();
                int version = ConfigManager.Instance().getWordUpDataVersion(bookId);//更新版本号写在网络请求中了
                mPresenter.upDataDownload(bookId, version);
                isRefresh = false;
            }
        }
        stageView.invalidate();
    }

    private List<String> createEmptyErrorList(List<VoaWord2> list) {
        List<String> emptyErrorList = new ArrayList<>();
        final String video = ": videoUrl is empty!";
        final String video404 = ": videoUrl is error!";
        final String audio = ": audio is empty!";
        final String audio404 = ": audio is error!";
        final String sentence = ": Sentence_audio is empty!";
        final String sentence404 = ": Sentence_audio is error!";
        final String pic = ": pic_url is empty";
        final String pic404 = ": pic_url is error";
        final String pron = ": pron is empty";

        if (list == null || list.size() == 0) {
            return null;
        }

        for (VoaWord2 word : list) {
            if (word.videoUrl == null || "".equals(word.videoUrl)) {
                if (word.videoUrl == null) {
                    Timber.d("errorTest: videoUrl is null!");
                } else {
                    Timber.d("errorTest: videoUrl is just empty!");
                }
                emptyErrorList.add(errorTextFormat(word, video));
            } else {
                //mUrlList.add(word.videoUrl);
                exist(word.videoUrl, word, video404);
            }

            if (word.audio == null || "".equals(word.audio)) {
                emptyErrorList.add(errorTextFormat(word, audio));
            } else {
                //mUrlList.add(word.audio);
                exist(word.audio, word, audio404);
            }

            if (word.SentenceAudio == null || "".equals(word.SentenceAudio)) {
                emptyErrorList.add(errorTextFormat(word, sentence));
            } else {
                //mUrlList.add(word.SentenceAudio);
                exist(word.SentenceAudio, word, sentence404);
            }

            if (word.picUrl == null || "".equals(word.picUrl)) {
                emptyErrorList.add(errorTextFormat(word, pic));
            } else {
                //mUrlList.add("http://static2."+Constant.IYUBA_CN+"images/words/" + word.picUrl);
                exist("http://static2." + Constant.IYUBA_CN + "images/words/" + word.picUrl, word, pic404);
            }

            if (word.pron == null || "".equals(word.pron)) {
                emptyErrorList.add(errorTextFormat(word, pron));
            }

        }

        return emptyErrorList;
    }

    /* 功能不稳定，线程数过多 */
    /* 乱开线程，稳定才怪 */
    private void exist(String url, VoaWord2 word, String flag) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL u = new URL(url);
                    HttpURLConnection huc = (HttpURLConnection) u.openConnection();
                    huc.setRequestMethod("HEAD");
                    huc.setConnectTimeout(5000); //视情况设置超时时间
                    huc.connect();

                    if (huc.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                        Timber.d("url404detected" + errorTextFormat(word, flag + " " + url));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Timber.d("url404detected" + errorTextFormat(word, flag + " " + url));
                }
            }
        });
        thread.start();
    }

    private String errorTextFormat(VoaWord2 voaWord, String flag) {
        String bookId = voaWord.bookId;
        String unitId = voaWord.unitId;
        String word = voaWord.word;

        if (bookId == null) {
            bookId = "";
        }
        if (unitId == null) {
            unitId = "";
        }
        if (word == null) {
            word = "";
        }

        return "bookId:" + bookId + "; unitId:" + unitId + "; word:" + word + flag;
    }

    @Override
    public void upDataWordList(List<VoaWord2> list) {
        //要开启下载啊
        String bookId = ConfigManager.Instance().getCurrBookId();
        mPresenter.startDownloadUpData(bookId, list);
    }

    private void showDownloadDialog(String bookId) {
        //ConfigManager.Instance().putBoolean(bookId,false);
        //只是判断记录，并没有判断文件 是否存在，里面是判断了的
        if (!ConfigManager.Instance().loadBoolean(bookId)) {//这个 应该用数据库的 懒得弄了 ||isRefresh
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
            builder.setMessage("是否需要下载视频资源？该过程可能需要耗费大量时间。");
            builder.setNegativeButton("不需要", null);
            builder.setPositiveButton("下载", (dialog, which) -> mPresenter.startDownload(bookId));
            builder.show();
        }
    }

    @Override
    public void startDownload() {
        downloadDialog = new DownloadDialog(mContext);
        downloadDialog.setCallback(new DownloadDialog.CallBack() {
            @Override
            public void onCancel() {
                mPresenter.cancelDownload();
                downloadDialog.dismiss();
                showMessage("下载任务已取消");
            }
        });
        downloadDialog.show();
    }

    /**
     * @param downloadEvent 接受下载事件并处理
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadFinish(DownloadEvent downloadEvent) {
        switch (downloadEvent.status) {
            case DownloadEvent.Status.FINISH:
                /* 全部下载完成 */
                if (downloadEvent.downloadId == 0) {
                    updateBookDb();
                    showMessage("下载完成！");
                    downloadDialog.dismiss();
                } else {
                    //下载完成本地数据库记录？？？？
                    //mPresenter.addDownload(downloadEvent.downloadId);
                }
                break;
            case DownloadEvent.Status.DOWNLOADING:
                if (downloadEvent.downloadId == 1000) {
//                    notification.setProress(configManager.getCourseTitle(), downloadEvent.msg);
//                    Log.d("111111111111111", "onDownloadFinish: ________________"+downloadEvent.msg);
                    downloadDialog.setProgress(Integer.parseInt(downloadEvent.msg));
                }
                break;
            case DownloadEvent.Status.ERROR:
                showMessage("下载出现错误！");
                //mPresenter.startDownload("0000");
                //mLoadingDialog.dismiss();
                break;
            default:
                break;
        }
    }

    private void updateBookDb() {
        String bookId = ConfigManager.Instance().getCurrBookId();
        ConfigManager.Instance().putBoolean(bookId, true);
    }

    /**
     * 从服务器拉取更新单词数据
     */
    private void updatePASSData(boolean isDownRefresh) {
        if (!isDownRefresh) {
            if (!waittingDialog2.isShowing()) {
                waittingDialog2.show();
            }
        }

        PullHistoryThread thread = new PullHistoryThread(mContext, new String[]{String.valueOf(currBookForPass)}, new PullHistoryThread.CallBack() {
            @Override
            public void callback(boolean isSuccess) {
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshWord.setRefreshing(false);
                        waittingDialog2.dismiss();
                        EventBus.getDefault().post(new VipChangeEvent());

                        if (!isSuccess) {
                            ToastUtil.showToast(requireActivity(), "暂无当前内容的单词进度");
                        }
                    }
                });
            }
        });
        //乱开线程？？？
        thread.start();

    }

    private int findSelectItemIndex(ArrayList<Integer> array, int currentItem) {
        int index = -1;
        for (int i = 0; i < array.size(); i++) {
            if (currentItem == array.get(i)) {
                index = i;
                break;
            }
        }
        return index;
    }


    /****************************回调数据**************************/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RefreshDataEvent event) {
        if (event.getType().equals(TypeLibrary.RefreshDataType.concept_word)) {
            //这里刷新单词数据
            currBookForPass = ConceptBookChooseManager.getInstance().getBookId();
            ConfigManager.Instance().setCurrBookforPass(currBookForPass);
            getData(currBookForPass);
            initStageView();
        }
    }

    /*****************************辅助功能************************/
    private void showDialog() {
        new AlertDialog.Builder(getActivity())
                .setCancelable(false)
                .setTitle("提示")
                .setMessage("会员可以无限制单词学习和单词闯关，是否购买会员继续使用？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NewVipCenterActivity.start(getActivity(), NewVipCenterActivity.VIP_APP);
                    }
                }).setNegativeButton("取消", null)
                .show();
    }

    //加载弹窗
    private LoadingDialog loadingDialog;

    private void startLoading(String showMsg){
        if (loadingDialog==null){
            loadingDialog = new LoadingDialog(getActivity());
            loadingDialog.create();
            loadingDialog.setCancelable(false);
        }
        loadingDialog.setMsg(showMsg);
        loadingDialog.show();
    }

    private void stopLoading(){
        if (loadingDialog!=null&&loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
    }
}
