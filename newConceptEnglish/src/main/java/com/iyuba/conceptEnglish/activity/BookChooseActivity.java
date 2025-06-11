//package com.iyuba.conceptEnglish.activity;
//
//import android.app.ActivityManager;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.View;
//import android.view.Window;
//import android.widget.ImageView;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.iyuba.ConstantNew;
//import com.iyuba.conceptEnglish.R;
//import com.iyuba.conceptEnglish.adapter.BookChooseAdapter;
//import com.iyuba.conceptEnglish.api.ApiRetrofit;
//import com.iyuba.conceptEnglish.api.UnitTitle;
//import com.iyuba.conceptEnglish.api.UpdateTitleAPI;
//import com.iyuba.conceptEnglish.api.UpdateUnitTitleAPI;
//import com.iyuba.conceptEnglish.api.UpdateVoaDetailAPI;
//import com.iyuba.conceptEnglish.api.UpdateWordDetailAPI;
//import com.iyuba.conceptEnglish.entity.YouthBookEntity;
//import com.iyuba.conceptEnglish.event.RefreshBookEvent;
//import com.iyuba.conceptEnglish.han.utils.AdvertisingKey;
//import com.iyuba.conceptEnglish.lil.fix.common_fix.data.event.RefreshDataEvent;
//import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
//import com.iyuba.conceptEnglish.lil.fix.concept.bgService.ConceptBgPlayEvent;
//import com.iyuba.conceptEnglish.lil.fix.concept.bgService.ConceptBgPlaySession;
//import com.iyuba.conceptEnglish.model.SaveWordDataModel;
//import com.iyuba.conceptEnglish.sqlite.mode.Voa;
//import com.iyuba.conceptEnglish.sqlite.mode.VoaDetail;
//import com.iyuba.conceptEnglish.sqlite.mode.VoaWord;
//import com.iyuba.conceptEnglish.sqlite.op.BookTableOp;
//import com.iyuba.conceptEnglish.sqlite.op.VoaDetailOp;
//import com.iyuba.conceptEnglish.sqlite.op.VoaOp;
//import com.iyuba.conceptEnglish.sqlite.op.VoaWordOp;
//import com.iyuba.configation.ConfigManager;
//import com.iyuba.configation.entity.enumconcept.BookType;
//import com.iyuba.core.common.base.BasisActivity;
//import com.iyuba.core.common.data.DataManager;
//import com.iyuba.core.common.data.model.TalkClass;
//import com.iyuba.core.common.data.model.TalkLesson;
//import com.iyuba.core.common.data.model.VoaWord2;
//import com.iyuba.core.common.util.TextAttr;
//import com.iyuba.core.common.util.ToastUtil;
//import com.iyuba.core.common.widget.dialog.CustomDialog;
//import com.iyuba.core.common.widget.dialog.WaittingDialog;
//import com.iyuba.module.toolbox.RxUtil;
//
//import org.greenrobot.eventbus.EventBus;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import io.reactivex.disposables.Disposable;
//import io.reactivex.functions.Consumer;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import timber.log.Timber;
//
//public class BookChooseActivity extends BasisActivity {
//
//    private Context mContext;
//    private int curBook;
//    private int clickPosition = 0;
//
//    private ImageView backButton, download;
//    private int isFirst = 0;
//
//    private View backView;
//    List<PhotoBean> fourVolumeList = new ArrayList<>();
//    List<PhotoBean> youthBookList = new ArrayList<>();
//    List<PhotoBean> oldYouthBookList = new ArrayList<>();
//
//    /**
//     * 是否是美音
//     * 是否是青少版
//     */
//    private boolean isAmerican, isYouth;
//    /**
//     * 课本类型
//     */
//    private BookType bookType;
//    private RadioGroup mSelectGroup;
//    private RadioButton mAmericaRB, mEnglishRB, mYoungRB;
//
//    private CustomDialog waittingDialog;
//
//    private RecyclerView recycler;
//    private BookChooseAdapter adapter;
//
//    private BookTableOp bookTableOp;
//
//    private DataManager mDataManager;
//    private Disposable mDisposable_BookDetail,
//            mDisposable_YouthUnitTitle,
//            mDisposable_GetWords;
//
//    //单元标题的更新结果
//    private boolean youthUtilTitleUpdate = false;
//    //课文详情的更新结果
//    private boolean youthVoaDetailUpdate = true;
//    //课本单词的更新结果
//    private boolean youthWordUpdate = false;
//
//    public static void start(Context context,int isFirstInfo){
//        Intent intent = new Intent();
//        intent.setClass(context,BookChooseActivity.class);
//        intent.putExtra("isFirstInfo",isFirstInfo);
//        context.startActivity(intent);
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.book_choose);
//
//
//        mContext = this;
//        waittingDialog = WaittingDialog.showDialog(mContext);
//        isFirst = getIntent().getIntExtra("isFirstInfo", 0);
//        bookType = ConfigManager.Instance().getBookType();
//        isAmerican = ConfigManager.Instance().isAmercan();
//        isYouth = ConfigManager.Instance().isYouth();
//        bookTableOp = new BookTableOp(mContext);
//        mDataManager = DataManager.getInstance();
//        bindAndClick();
//
//        initData();
//        if (!getPackageName().equals(AdvertisingKey.releasePackage)) {
//            download.setVisibility(View.GONE);
//        }
//    }
//
//    protected void onResume() {
//        super.onResume();
//        if (isFirst == 0) {
//            backButton.setVisibility(View.GONE);
//            download.setVisibility(View.GONE);
//        }
//        curBook = ConfigManager.Instance().loadInt("curBook", 1);
//    }
//
//    private void bindAndClick() {
//        backView = findViewById(R.id.backlayout);
//        backView.setBackgroundColor(Color.WHITE);
//        backButton = findViewById(R.id.button_back);
//        download = findViewById(R.id.download);
//        download.setVisibility(View.GONE);
//        mSelectGroup = findViewById(R.id.select_group);
//        mAmericaRB = findViewById(R.id.america_pronunciation);
//        mEnglishRB = findViewById(R.id.english_pronunciation);
//        mYoungRB = findViewById(R.id.youth);
//        recycler = findViewById(R.id.recycler);
//
//        mSelectGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                switch (checkedId) {
//                    case R.id.america_pronunciation:
//                    default:
//                        radioButtonTextColorChange(getResources().getColor(R.color.white), getResources().getColor(R.color.bookChooseUncheck),
//                                getResources().getColor(R.color.bookChooseUncheck));
//                        isAmerican = true;
//                        isYouth = false;
//                        if (adapter != null) {
//                            adapter.refreshData(fourVolumeList);
//                            adapter.notifyDataSetChanged();
//                        }
//                        break;
//                    case R.id.english_pronunciation:
//                        radioButtonTextColorChange(getResources().getColor(R.color.bookChooseUncheck), getResources().getColor(R.color.white),
//                                getResources().getColor(R.color.bookChooseUncheck));
//                        isAmerican = false;
//                        isYouth = false;
//                        if (adapter != null) {
//                            adapter.refreshData(fourVolumeList);
//                            adapter.notifyDataSetChanged();
//                        }
//                        break;
//                    case R.id.youth:
//                        radioButtonTextColorChange(getResources().getColor(R.color.bookChooseUncheck), getResources().getColor(R.color.bookChooseUncheck),
//                                getResources().getColor(R.color.white));
//                        isYouth = true;
//                        isAmerican = false;
//                        if (adapter != null) {
//                            adapter.refreshData(youthBookList);
//                            adapter.notifyDataSetChanged();
//                        }
//                        break;
//                }
//            }
//        });
//
//        backButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//
//        download.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setClass(mContext, BookDownloadActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        //对于top包名的应用宝渠道单独处理，将来可以删除
//        if (ConstantNew.isTencentStore){
//            mAmericaRB.setText("美音");
//            mEnglishRB.setText("英音");
//            mYoungRB.setText("青少版");
//        }
//    }
//
//    private void radioButtonTextColorChange(int americanColor, int englishColor, int youthColor) {
//        mAmericaRB.setTextColor(americanColor);
//        mEnglishRB.setTextColor(englishColor);
//        mYoungRB.setTextColor(youthColor);
//    }
//
//    Handler handler = new Handler() {
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//
//            //这里判断是否book改变了和类型，改变了就将后台播放停止
//            int preBookId = ConfigManager.Instance().loadInt("curBook",-1);
//            //判断类型
//            boolean isPreYouth = false;
//            boolean isPreAmerica = false;
//            switch (ConfigManager.Instance().getBookType()){
//                case AMERICA:
//                    isPreYouth = false;
//                    isPreAmerica = true;
//                    break;
//                case ENGLISH:
//                    isPreYouth = false;
//                    isPreAmerica = false;
//                    break;
//                case YOUTH:
//                    isPreYouth = true;
//                    isPreAmerica = false;
//                    break;
//            }
//
//            if (preBookId != curBook || (isPreAmerica != isAmerican) || (isPreYouth != isYouth)){
//                EventBus.getDefault().post(new ConceptBgPlayEvent(ConceptBgPlayEvent.event_control_hide));
//                //书本更换，重置所有的数据
//                ConceptBgPlaySession.getInstance().setPlayPosition(-1);
//                ConceptBgPlaySession.getInstance().setVoaList(null);
//            }
//
//            //这里将当前课本的id存储
//            ConfigManager.Instance().putInt("curBook", curBook);
//
//            switch (msg.what) {
//                case 0:
//                    //以下为新概念的逻辑
//                    if (!waittingDialog.isShowing()) {
//                        waittingDialog.show();
//                    }
//                    //更新 课文标题数据， 数据表voa_detail 和 voa_detail_american
//                    getUpdateContentVersion(curBook, isAmerican);
//                    getUpdateContentVersion(curBook, !isAmerican);
//                    //更新单词数据,数据表  voa_word
//                    getWordTitles(curBook);
//                    //用于更新 微课跳转所需要的的数据
//                    getUnitTitles(curBook, isAmerican);
//                    //拉取最新的课文数据
////                    EventBus.getDefault().post(new PullLastLessonDataEvent(curBook,isAmerican));
//                    break;
//                case 1:
//                    //如果这个活动在任务栈中就不重新启动，只是刷新数据，也没问题
//                    int isForegroundType = isForeground(mContext, MainFragmentActivity.class.getName());
//                    Timber.d("MainFragmentActivity 是否存在" + isForegroundType);
//                    ConfigManager.Instance().setAmerican(isAmerican);
//                    ConfigManager.Instance().setYouth(isYouth);
//                    setCurrentBookName();
//
//                    //针对个别包名，刷新单词显示
//                    if (getPackageName().equals("com.iyuba.learnNewEnglish")
//                            ||getPackageName().equals("com.iyuba.conceptStory")){
//                        //刷新单词的内容
//                        EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.concept_word));
//                    }
//
//                    switch (isForegroundType){
//                        case 0:
//                        default:
//                            //传入数据异常
//                            Intent intent = new Intent();
//                            intent.setClass(mContext, MainFragmentActivity.class);
//                            startActivity(intent);
//                            EventBus.getDefault().post(new RefreshBookEvent());
//                            break;
//                        case 1:
//                            //在后台
////                            Intent intent3 = new Intent();
////                            intent3.setClass(mContext, MainFragmentActivity.class);
////                            startActivity(intent3);
//                            EventBus.getDefault().post(new RefreshBookEvent());
//                            break;//不在后台
//                    }
//                    if (waittingDialog.isShowing()) {
//                        waittingDialog.dismiss();
//                    }
//                    finish();
//                    break;
//                case 10:
//                    //以下为青少版的逻辑
//                    clickPosition = msg.arg1;
//                    if (oldYouthBookList == null
//                            || oldYouthBookList.size() <= clickPosition
//                            || youthBookList.size() <= clickPosition
//                            || youthBookList.get(clickPosition).version > oldYouthBookList.get(clickPosition).version) {
//                        if (!waittingDialog.isShowing()) {
//                            waittingDialog.show();
//                        }
//                        //更新每一本书的 本文标题数据， concept_database数据库中的voa表
//                        insertOrUpdateYouthUnitTitles(youthBookList.get(clickPosition).bookId);
//                        //更新课文句子对话 跟全四册走不同的逻辑，不使用预加载
//
//                        //更新单词数据,数据表  voa_word
//                        getYouthWordData(youthBookList.get(clickPosition).bookId);
//                        return;
//                    }
//                    handler.sendEmptyMessage(1);
//
//                    break;
//                case 11:
//                    if (!waittingDialog.isShowing()) {
//                        waittingDialog.show();
//                    }
//                    //更新每一本书的 本文标题数据， concept_database数据库中的voa表
//                    insertOrUpdateYouthUnitTitles(youthBookList.get(clickPosition).bookId);
//                    break;
//                default:
//                    break;
//            }
//        }
//    };
//
//    /**
//     * 更新青少版的课文的标题详情
//     *
//     * @param bookId
//     */
//    private void insertOrUpdateYouthUnitTitles(int bookId) {
//        VoaOp voaOp = new VoaOp(mContext);
//        RxUtil.dispose(mDisposable_YouthUnitTitle);
//        mDisposable_YouthUnitTitle = mDataManager.getTalkLesson(bookId + "")
//                .compose(RxUtil.<List<TalkLesson>>applySingleIoScheduler())
//                .subscribe(new Consumer<List<TalkLesson>>() {
//                    @Override
//                    public void accept(List<TalkLesson> list) throws Exception {
//                        if (list != null && list.size() > 0) {
//                            List<Voa> listVoa = new ArrayList<>();
//                            for (TalkLesson talkLesson : list) {
//                                Voa voa = new Voa();
//                                voa.voaId = talkLesson.voaId();
//                                voa.title = talkLesson.Title;
//                                voa.titleCn = talkLesson.TitleCn;
//                                voa.category = Integer.parseInt(talkLesson.series);
//                                voa.clickRead = talkLesson.clickRead;
//
//                                voa.pic = talkLesson.Pic;
//                                listVoa.add(voa);
//                            }
//                            voaOp.insertOrUpdate(listVoa);
//                            handler.sendEmptyMessage(1);
////                            youthUtilTitleUpdate = true;
////                            endYouthUpdate();
//                        } else {
//                            ToastUtil.showToast(mContext, "服务器异常,更新课文标题失败");
//                        }
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        Timber.e(throwable);
//                        ToastUtil.showToast(mContext, "更新课文标题失败,请检查网络");
//                        if (waittingDialog.isShowing()) {
//                            waittingDialog.dismiss();
//                        }
//                        handler.sendEmptyMessage(1);
//                    }
//                });
//    }
//
//    /**
//     * 青少版单词数据的 更新插入
//     *
//     * @param bookId
//     */
//    private void getYouthWordData(int bookId) {
//        com.iyuba.core.common.util.RxUtil.unsubscribe(mDisposable_GetWords);
//        mDisposable_GetWords = mDataManager.getChildWords(bookId + "")
//                .compose(com.iyuba.module.toolbox.RxUtil.<List<VoaWord2>>applySingleIoScheduler())
//                .subscribe(new Consumer<List<VoaWord2>>() {
//                    @Override
//                    public void accept(List<VoaWord2> list) throws Exception {
//                        if (list != null && list.size() > 0) {
//                            SaveWordDataModel saveWordDataModel = new SaveWordDataModel(mContext);
//                            saveWordDataModel.saveWordData(list);
//                            youthWordUpdate = true;
//                            endYouthUpdate();
//                        } else {
//                            ToastUtil.showToast(mContext, "服务器异常,更新课文单词失败");
//                        }
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        Timber.e(throwable);
//                        ToastUtil.showToast(mContext, "更新课文单词失败,请检查网络");
//                    }
//                });
//    }
//
//    private void endYouthUpdate() {
//        if (youthUtilTitleUpdate && youthVoaDetailUpdate && youthWordUpdate) {
//            bookTableOp.updateVersion(youthBookList.get(clickPosition).bookId, youthBookList.get(clickPosition).version);
//            handler.sendEmptyMessage(1);
//        }
//    }
//
//    /**
//     * 用于更新 lesson页 跳转微课所需要的数据
//     *
//     * @param bookId
//     * @param isAmerican
//     */
//    private void getUnitTitles(int bookId, boolean isAmerican) {
//        VoaOp voaOp = new VoaOp(mContext);
//        String type;
//        if (isAmerican) {
//            type = UpdateUnitTitleAPI.TYPE_US;
//        } else {
//            type = UpdateUnitTitleAPI.TYPE_UK;
//        }
//
//
//        UpdateUnitTitleAPI updateUnitTitleAPI = ApiRetrofit.getInstance().getUnitTitleAPI();
//        updateUnitTitleAPI.getData(UpdateUnitTitleAPI.url, bookId, type, UpdateUnitTitleAPI.flgContainNew).enqueue(new Callback<UnitTitle>() {
//
//            @Override
//            public void onResponse(Call<UnitTitle> call, Response<UnitTitle> response) {
//                Log.e("ccad--获取更新课文标题字符", call.request().url().toString());
//                UnitTitle unitTitle = response.body();
//                if (unitTitle != null && unitTitle.getSize() > 0) {
//                    for (UnitTitle.DataBean dataBean : unitTitle.getData()) {
//                        int voaId = dataBean.getVoa_id();
//                        voaOp.updateTitle(voaId, dataBean.getTitle());
//                        voaOp.updateTitleCn(voaId, dataBean.getTitle_cn());
//                        //更新微课需要的数据
//                        voaOp.updateMiacroLessonData(voaId, dataBean.getCategoryid(), dataBean.getTitleid(),
//                                dataBean.getTotalTime());
//                    }
//                }
//                if (waittingDialog.isShowing()) {
//                    waittingDialog.dismiss();
//                }
//                handler.sendEmptyMessage(1);
//            }
//
//            @Override
//            public void onFailure(Call<UnitTitle> call, Throwable t) {
//                if (waittingDialog.isShowing()) {
//                    waittingDialog.dismiss();
//                }
//                handler.sendEmptyMessage(1);
//            }
//        });
//    }
//
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            finish();
//            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//
//
//    /**
//     * 判断某个界面是否在前台
//     *
//     * @param context   Context
//     * @param className 界面的类名
//     * @return 0: 传入信息异常
//     *         1：在后台
//     *         2：不在后台
//     */
//    public static int isForeground(Context context, String className) {
//        if (context == null || TextUtils.isEmpty(className))
//            return 0;
//        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
//        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
//        for (ActivityManager.RunningTaskInfo taskInfo : list) {
//            if (taskInfo.baseActivity.getClassName().contains(className)) { // 说明它已经启动了
//                return 1;
//            }
//        }
//        return 2;
//    }
//
//
//    private void initData() {
//        switch (bookType) {
//            case AMERICA:
//            default:
//                mSelectGroup.check(R.id.america_pronunciation);
//                radioButtonTextColorChange(getResources().getColor(R.color.white), getResources().getColor(R.color.bookChooseUncheck),
//                        getResources().getColor(R.color.bookChooseUncheck));
//                break;
//            case ENGLISH:
//                mSelectGroup.check(R.id.english_pronunciation);
//                radioButtonTextColorChange(getResources().getColor(R.color.bookChooseUncheck), getResources().getColor(R.color.white),
//                        getResources().getColor(R.color.bookChooseUncheck));
//                break;
//            case YOUTH:
//                mSelectGroup.check(R.id.youth);
//                radioButtonTextColorChange(getResources().getColor(R.color.bookChooseUncheck), getResources().getColor(R.color.bookChooseUncheck),
//                        getResources().getColor(R.color.white));
//                break;
//        }
//        //飞雷神小学英语
//        if ("com.iyuba.talkshow.childenglishnew".equals(getPackageName())){
//            fourVolumeList.add(new PhotoBean(1, R.drawable.icon_book_1, null, "新概念第一册", 0));
//        }else {
//            fourVolumeList.add(new PhotoBean(1, R.drawable.icon_book_1, null, "新概念第一册", 0));
//            fourVolumeList.add(new PhotoBean(1, R.drawable.icon_book_2, null, "新概念第二册", 0));
//            fourVolumeList.add(new PhotoBean(1, R.drawable.icon_book_3, null, "新概念第三册", 0));
//            fourVolumeList.add(new PhotoBean(1, R.drawable.icon_book_4, null, "新概念第四册", 0));
//        }
//
//
//        youthBookList.clear();
//        oldYouthBookList.clear();
//        List<YouthBookEntity> tempList = bookTableOp.selectAllData();
//        for (YouthBookEntity entity : tempList) {
//            youthBookList.add(new PhotoBean(entity.Id, -1, entity.pic, "新概念英语青少版" + entity.DescCn, entity.version));
//            oldYouthBookList.add(new PhotoBean(entity.Id, -1, entity.pic, "新概念英语青少版" + entity.DescCn, entity.version));
//        }
//
//        if (isYouth) {
//            initRecycler(youthBookList);
//        } else {
//            initRecycler(fourVolumeList);
//        }
//
//        //从服务器加载课本，更新数据
//        requestYouthBookDetail();
//    }
//
//    private void initRecycler(List<PhotoBean> list) {
//        adapter = new BookChooseAdapter(list, this);
//        GridLayoutManager layout = new GridLayoutManager(this, 2);
//        recycler.setLayoutManager(layout);
//        recycler.setAdapter(adapter);
//        adapter.setOnClickListener(new BookChooseAdapter.OnClickListener() {
//            @Override
//            public void onClick(View view, int position) {
//                if (isYouth) {
//                    //青少版逻辑
//                    curBook = youthBookList.get(position).bookId;
//                    Message message = new Message();
//                    clickPosition = position;
////                    message.what = 10;
////                    message.what = 1;
//                    message.what = 11;
//                    message.arg1 = position;
//                    handler.sendMessage(message);
//                } else {
//                    //全四册逻辑
//                    curBook = position + 1;
////                    handler.sendEmptyMessage(0);
//                    handler.sendEmptyMessage(1);
//                }
//            }
//        });
//
//    }
//
//    public static class PhotoBean {
//
//        private int bookId = -1;
//        private int bookImageId;
//        private String bookImageUrl;
//        private String title;
//        private int version;
//
//        public PhotoBean(int bookId, int bookImageId, String bookImageUrl, String title, int version) {
//            this.bookId = bookId;
//            this.bookImageId = bookImageId;
//            this.bookImageUrl = bookImageUrl;
//            this.title = title;
//            this.version = version;
//        }
//
//        public int getVersion() {
//            return version;
//        }
//
//        public void setVersion(int version) {
//            this.version = version;
//        }
//
//        public String getBookImageUrl() {
//            return bookImageUrl;
//        }
//
//        public void setBookImageUrl(String bookImageUrl) {
//            this.bookImageUrl = bookImageUrl;
//        }
//
//        public int getBookId() {
//            return bookId;
//        }
//
//        public void setBookId(int bookId) {
//            this.bookId = bookId;
//        }
//
//        public int getBookImageId() {
//            return bookImageId;
//        }
//
//        public void setBookImageId(int bookImageId) {
//            this.bookImageId = bookImageId;
//        }
//
//        public String getTitle() {
//            return title;
//        }
//
//        public void setTitle(String title) {
//            this.title = title;
//        }
//    }
//
//
//    String voaIdStr;
//
//    /**
//     * 这个方法的作用
//     * 1.获取版本号  根据书本和类型
//     * 2.筛选出需要更新的版本号
//     * 3.根据筛选出来的版本号调用 getVoaDetail(),去更新 local_database中的课文详情数据
//     * 4.经中经典中典的回调地狱
//     *
//     * @param bookId
//     * @param isAmerican
//     */
//    private void getUpdateContentVersion(int bookId, boolean isAmerican) {
//        voaIdStr = "";
//        VoaOp voaOp = new VoaOp(mContext);
//        String type;
//        if (isAmerican) {
//            type = UpdateTitleAPI.TYPE_US;
//        } else {
//            type = UpdateTitleAPI.TYPE_UK;
//        }
//        UpdateTitleAPI updateTitleAPI = ApiRetrofit.getInstance().getUpdateTitleAPI();
//        updateTitleAPI.getData(UpdateTitleAPI.url, bookId, type).enqueue(new Callback<UpdateTitleAPI.UpdateTitleBean>() {
//            @Override
//            public void onResponse(Call<UpdateTitleAPI.UpdateTitleBean> call, Response<UpdateTitleAPI.UpdateTitleBean> response) {
//
//                try {
//                    Log.e("ccad--获取更新课文标题", call.request().url().toString());
//                    UpdateTitleAPI.UpdateTitleBean bean = response.body();
//                    if (bean != null && bean.getSize() > 0) {
//                        for (UpdateTitleAPI.UpdateTitleBean.DataBean child : bean.getData()) {
//                            int version = Integer.parseInt(child.getVersion());
//                            Log.e("ccad-", "----");
//                            if (version > voaOp.getCourseVersion(child.getVoa_id(), isAmerican)
//                                    || ConfigManager.Instance().isForceLoading()) {
//                                //老用户的逻辑是走不进这里的，因为老用户的课文版本已经是最新了
//                                //forceload也是true，
//                                //但是每次升级local 数据库表都会被重置，所以导致无法获取课文详情
//                                Voa voa = voaOp.findDataById(child.getVoa_id());
//                                int id;
//                                if (isAmerican) {
//                                    id = child.getVoa_id();
//                                    voa.version_us = Integer.parseInt(child.getVersion());
//                                } else {
//                                    id = child.getVoa_id() * 10;
//                                    voa.version_uk = Integer.parseInt(child.getVersion());
//                                }
//                                voaOp.updateDataVersion(voa, isAmerican);
//                                if (TextUtils.isEmpty(voaIdStr)) {
//                                    voaIdStr += id;
//                                } else {
//                                    voaIdStr += "," + id;
//                                }
//
//                            }
//                        }
//
//                        if (ConfigManager.Instance().isForceLoading()) {
//                            ConfigManager.Instance().setForceLoading();
//                        }
//
//                        if (!TextUtils.isEmpty(voaIdStr)) {
//                            //请求修改的数据
//                            Log.e("ccad--", voaIdStr);
//                            getVoaDetail(TextAttr.decode(voaIdStr), isAmerican);
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<UpdateTitleAPI.UpdateTitleBean> call, Throwable t) {
//                Log.e("ccad--", "课文详情 获取 verson 失败");
//            }
//        });
//    }
//
//    /**
//     * 主要作用
//     * 更新local_database 数据库中的  课文详情表
//     *
//     * @param voaIdStr
//     */
//    private void getVoaDetail(String voaIdStr, boolean isAmerican) {
//        UpdateVoaDetailAPI updateVoaDetailAPI = ApiRetrofit.getInstance().getUpdateVoaDetailAPI();
//        updateVoaDetailAPI.getData(UpdateVoaDetailAPI.url, UpdateVoaDetailAPI.type, voaIdStr).enqueue(new Callback<UpdateVoaDetailAPI.UpdateVoaDetailBean>() {
//            @Override
//            public void onResponse(Call<UpdateVoaDetailAPI.UpdateVoaDetailBean> call, Response<UpdateVoaDetailAPI.UpdateVoaDetailBean> response) {
//
//                Log.e("ccad--更新课文详情", call.request().url().toString());
//                try {
//                    UpdateVoaDetailAPI.UpdateVoaDetailBean bean = response.body();
//                    if (bean != null && bean.getSize() > 0) {
//                        List<VoaDetail> details = new ArrayList<>();
//                        for (UpdateVoaDetailAPI.UpdateVoaDetailBean.DataBean child : bean.getData()) {
//                            VoaDetail detail = new VoaDetail();
//                            //为了让英音的id 还原，因为之前*10了 start
//                            if (child.getVoaid() > 5000) {
//                                detail.voaId = child.getVoaid() / 10;
//                            } else {
//                                detail.voaId = child.getVoaid();
//                            }
//                            //为了让英音的id 还原，因为之前*10了 end
//                            detail.paraId = child.getParaid();
//                            detail.lineN = child.getIdIndex();
//                            detail.startTime = Double.parseDouble(child.getTiming());
//                            detail.endTime = Double.parseDouble(child.getEndTiming());
//                            detail.sentence = child.getSentence();
//                            detail.sentenceCn = child.getSentence_cn();
//                            details.add(detail);
//                        }
//                        VoaDetailOp voaDetailOp = new VoaDetailOp(mContext);
//                        voaDetailOp.saveData(details, isAmerican);
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<UpdateVoaDetailAPI.UpdateVoaDetailBean> call, Throwable t) {
//
//            }
//        });
//
//    }
//
//
//    //单词更新
//    String voaIdStrWord;
//
//    /**
//     * 获取最新的单词版本号，并且筛选出需要更新的单词版本号
//     *
//     * @param bookId
//     */
//    private void getWordTitles(int bookId) {
////
////        //test start
////
////        switch (bookId){
////            case 1:
////                voaIdStrWord = "1001";
////                for(int i=1002;i<1145;i++){
////                    voaIdStrWord += "," + i;
////                }
////                break;
////            case 2:
////                voaIdStrWord = "2001";
////                for(int i=2001;i<2097;i++){
////                    voaIdStrWord += "," + i;
////                }
////
////                break;
////            case 3:
////                voaIdStrWord = "3001";
////                for(int i=3001;i<3061;i++){
////                    voaIdStrWord += "," + i;
////                }
////                break;
////            case 4:
////
////                voaIdStrWord = "4001";
////                for(int i=4001;i<4049;i++){
////                    voaIdStrWord += "," + i;
////                }
////                break;
////            default:
////                voaIdStrWord = "1001";
////                for(int i=1002;i<1145;i++){
////                    voaIdStrWord += "," + i;
////                }
////                break;
////        }
////
////
////
////        getWordDetail(TextAttr.decode(voaIdStrWord));
////        //test end
//
//        voaIdStrWord = "";
//        VoaOp voaOp = new VoaOp(mContext);
//        UpdateTitleAPI updateTitleAPI = ApiRetrofit.getInstance().getUpdateTitleAPI();
//        updateTitleAPI.getWordData(UpdateTitleAPI.word_url, bookId).enqueue(new Callback<UpdateTitleAPI.UpdateTitleBean>() {
//            @Override
//            public void onResponse(Call<UpdateTitleAPI.UpdateTitleBean> call, Response<UpdateTitleAPI.UpdateTitleBean> response) {
//
//                try {
//                    Log.e("ccad--获取更新课文标题,单词更新", call.request().url().toString());
//                    UpdateTitleAPI.UpdateTitleBean bean = response.body();
//                    if (bean != null && bean.getSize() > 0) {
//                        for (UpdateTitleAPI.UpdateTitleBean.DataBean child : bean.getData()) {
//                            int version = Integer.parseInt(child.getVersion());
//
//                            if (version > voaOp.getWordVersion(child.getVoa_id())) {
//                                Voa voa = voaOp.findDataById(child.getVoa_id());
//                                voa.version_word = Integer.parseInt(child.getVersion());
//                                voaOp.updateWordVersion(voa);
//                                if (TextUtils.isEmpty(voaIdStrWord)) {
//                                    voaIdStrWord += child.getVoa_id();
//                                } else {
//                                    voaIdStrWord += "," + child.getVoa_id();
//                                }
//
//                            }
//                        }
//                        if (!TextUtils.isEmpty(voaIdStrWord)) {
//                            //请求修改的数据
//                            getWordDetail(TextAttr.decode(voaIdStrWord));
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<UpdateTitleAPI.UpdateTitleBean> call, Throwable t) {
//
//            }
//        });
//    }
//
//    /**
//     * 主要作用
//     * 根据筛选出来的单词id
//     * <p>
//     * 更新local_database中的单词表
//     *
//     * @param voaIdStrWord
//     */
//    private void getWordDetail(String voaIdStrWord) {
//        UpdateWordDetailAPI updateWordDetailAPI = ApiRetrofit.getInstance().getUpdateWordDetailAPI();
//        updateWordDetailAPI.getData(UpdateWordDetailAPI.url, UpdateWordDetailAPI.type, voaIdStrWord).enqueue(new Callback<UpdateWordDetailAPI.WordBean>() {
//            @Override
//            public void onResponse(Call<UpdateWordDetailAPI.WordBean> call, Response<UpdateWordDetailAPI.WordBean> response) {
//
//                Log.e("ccad--更新课文详情——单词库", call.request().url().toString());
//                try {
//                    UpdateWordDetailAPI.WordBean bean = response.body();
//                    if (bean != null && bean.getSize() > 0) {
//                        List<VoaWord> wordList = new ArrayList<>();
//                        for (UpdateWordDetailAPI.WordBean.DataBean child : bean.getData()) {
//                            VoaWord word = new VoaWord();
//                            word.position = Integer.parseInt(child.getPosition());
//                            word.voaId = child.getVoa_id() + "";
//                            word.word = child.getWord();
//                            word.def = child.getDef();
//                            word.pron = child.getPron();
//                            word.audio = child.getAudio();
//                            word.examples = Integer.parseInt(child.getExamples());
//                            wordList.add(word);
//                        }
//                        VoaWordOp voaWordOp = new VoaWordOp(mContext);
//                        voaWordOp.saveData(wordList);
//                    }
//
//                } catch (Exception e) {
//                    ToastUtil.showToast(mContext, "单词更新异常");
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<UpdateWordDetailAPI.WordBean> call, Throwable t) {
//
//            }
//        });
//
//    }
//
//
//    /**
//     * 请求青少版 课本详情
//     */
//    private void requestYouthBookDetail() {
//        mDisposable_BookDetail = mDataManager.getTalkClassLesson("321")
//                .compose(RxUtil.<List<TalkClass>>applySingleIoScheduler())
//                .subscribe(new Consumer<List<TalkClass>>() {
//                    @Override
//                    public void accept(List<TalkClass> list) throws Exception {
//                        boolean needUpdate = youthBookList.size() == 0;
//                        youthBookList.clear();
//                        List<YouthBookEntity> youthList = new ArrayList<>();
//                        for (TalkClass talkClass : list) {
//                            YouthBookEntity entity = new YouthBookEntity();
//                            entity.Id = talkClass.getId();
//                            entity.DescCn = talkClass.DescCn;
//                            entity.Category = Integer.parseInt(talkClass.Category);
//                            entity.SeriesCount = talkClass.SeriesCount;
//                            entity.SeriesName = talkClass.SeriesName;
//                            entity.CreateTime = talkClass.CreateTime;
//                            entity.UpdateTime = talkClass.UpdateTime;
//                            entity.isVideo = talkClass.isVideo;
//                            entity.HotFlg = Integer.parseInt(talkClass.HotFlg);
//                            entity.pic = talkClass.pic;
//                            entity.KeyWords = talkClass.KeyWords;
//                            entity.version = talkClass.version;
//                            youthList.add(entity);
//                            //飞雷神小学英语
////                            if ("com.iyuba.talkshow.childenglishnew".equals(getPackageName())&&talkClass.DescCn.contains("Starter")){
////                                youthBookList.add(new PhotoBean(
////                                        talkClass.getId(),
////                                        -1,
////                                        talkClass.pic,
////                                        "新概念英语青少版" + talkClass.DescCn,
////                                        entity.version));
////                            }
//                                youthBookList.add(new PhotoBean(
//                                        talkClass.getId(),
//                                        -1,
//                                        talkClass.pic,
//                                        "新概念英语青少版" + talkClass.DescCn,
//                                        entity.version));
//
//
//                        }
//                        //更新UI
//                        if (needUpdate && isYouth) {
//                            adapter.refreshData(youthBookList);
//                            adapter.notifyDataSetChanged();
//                        }
////                        //清除数据
////                        bookTableOp.clearData();
//                        //存储数据
//                        try {
//                            if (needUpdate) {
//                                //插入更新
//                                bookTableOp.insertData(youthList);
//                            } else {
//                                //更新数据
//
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        Timber.e(throwable);
//                    }
//                });
//    }
//
//
//    private void setCurrentBookName() {
//        String titleText;
//        if (isYouth) {
//            titleText = youthBookList.get(clickPosition).title;
//        } else {
//            switch (curBook) {
//                case 1:
//                    titleText = getResources().getString(R.string.book_name_one);
//                    break;
//                case 2:
//                    titleText = getResources().getString(R.string.book_name_two);
//                    break;
//                case 3:
//                    titleText = getResources().getString(R.string.book_name_three);
//                    break;
//                case 4:
//                    titleText = getResources().getString(R.string.book_name_four);
//                    break;
//                default:
//                    titleText = "新概念英语";
//                    break;
//            }
//            if (isAmerican) {
//                titleText += "（美音）";
//            } else {
//                titleText += "（英音）";
//            }
//
//        }
//        ConfigManager.Instance().setCurrentBookName(titleText);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (waittingDialog.isShowing()) {
//            waittingDialog.dismiss();
//        }
//        RxUtil.dispose(mDisposable_BookDetail);
//        RxUtil.dispose(mDisposable_YouthUnitTitle);
//        RxUtil.dispose(mDisposable_GetWords);
//    }
//}
