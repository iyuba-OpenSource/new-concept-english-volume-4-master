package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.databinding.AtyStudyBinding;
import com.iyuba.conceptEnglish.lil.concept_other.util.HelpUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.BookChapterBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.ChapterDetailBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.event.RefreshDataEvent;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.StrLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.StudySettingManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.CommonDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.ChapterCollectEntity;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.Setting_ReadLanguageEntity;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.dubbing.DubbingFragment;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.eval.EvalFragment;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.rank.RankFragment;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.read.ReadFragment;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.section.SectionFragment;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.word.WordShowFragment;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.PdfUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.PermissionFixUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.ShareUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.view.dialog.LoadingDialog;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingActivity;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ToastUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.rxjava2.RxTimer;
import com.iyuba.conceptEnglish.lil.fix.junior.bgService.JuniorBgPlayEvent;
import com.iyuba.conceptEnglish.lil.fix.junior.bgService.JuniorBgPlaySession;
import com.iyuba.conceptEnglish.lil.fix.novel.bgService.NovelBgPlayEvent;
import com.iyuba.conceptEnglish.lil.fix.novel.bgService.NovelBgPlaySession;
import com.iyuba.conceptEnglish.study.StudyTitleAdapter;
import com.iyuba.conceptEnglish.util.ConceptApplication;
import com.iyuba.conceptEnglish.widget.dialog.ListenStudyReportDialog;
import com.iyuba.conceptEnglish.widget.dialog.SwitchDialog;
import com.iyuba.configation.ConfigManager;
import com.iyuba.core.InfoHelper;
import com.iyuba.core.common.activity.login.LoginUtil;
import com.iyuba.core.lil.base.StackUtil;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.widget.popmenu.ActionItem;
import com.iyuba.widget.popmenu.PopMenu;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * @title: 学习界面
 * @date: 2023/5/22 15:50
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class StudyActivity extends BaseViewBindingActivity<AtyStudyBinding> implements StudyView{

    //类型
    private String types;
    //书籍的id
    private String bookId;
    //voaId
    private String voaId;
    //当前章节的位置
    private int position = 0;
    //本章节的数据
    private BookChapterBean chapterBean;

    private StudyAdapter studyAdapter;
    private StudyTitleAdapter titleAdapter;

    private StudyPresenter presenter;
    //加载弹窗
    private LoadingDialog loadingDialog;

    //原文界面
    private ReadFragment readFragment;
    //阅读界面
    private SectionFragment sectionFragment;

    //听力记录弹窗
    private ListenStudyReportDialog listenDialog;
    //听力记录弹窗标识位
    private String listenDialogTag = "listenDialogTag";

    public static void start(Context context,String types,String bookId,String voaId,int position){
        Intent intent = new Intent();
        intent.setClass(context,StudyActivity.class);
        intent.putExtra(StrLibrary.types,types);
        intent.putExtra(StrLibrary.bookId,bookId);
        intent.putExtra(StrLibrary.voaid,voaId);
        intent.putExtra(StrLibrary.position,position);
        context.startActivity(intent);
    }

    public static Intent buildIntent(Context context,String types,String bookId,String voaId){
        Intent intent = new Intent();
        intent.setClass(context,StudyActivity.class);
        intent.putExtra(StrLibrary.types,types);
        intent.putExtra(StrLibrary.bookId,bookId);
        intent.putExtra(StrLibrary.voaid,voaId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        types = getIntent().getStringExtra(StrLibrary.types);
        bookId = getIntent().getStringExtra(StrLibrary.bookId);
        voaId = getIntent().getStringExtra(StrLibrary.voaid);
        position = getIntent().getIntExtra(StrLibrary.position,0);

        presenter = new StudyPresenter();
        presenter.attachView(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        initToolbar();
        initList();

        presenter.getChapterDetail(types,bookId,voaId);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        presenter.detachView();

        //销毁
        if (readFragment!=null){
            readFragment.destroyReadFragment();
        }

        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (listenDialog!=null){
            listenDialog.closeSelf();
        }
        RxTimer.getInstance().cancelTimer(listenDialogTag);
    }

    /**************初始化数据*************/
    private void initToolbar(){
        binding.toolbar.title.setText("选择课程");
        binding.toolbar.btnBack.setVisibility(View.VISIBLE);
        binding.toolbar.btnBack.setBackgroundResource(R.drawable.back_button);
        binding.toolbar.btnBack.setOnClickListener(v->{
            StackUtil.getInstance().finishCur();
        });
        binding.toolbar.btnRight.setVisibility(View.VISIBLE);
        binding.toolbar.btnRight.setBackgroundResource(R.drawable.textbook_category);
        binding.toolbar.btnRight.setOnClickListener(v->{
            showMenuDialog(binding.toolbar.btnRight);
        });
    }

    private void initList(){
        //进入这里的时候，停止新概念的音频播放
        EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.concept_play));

        titleAdapter = new StudyTitleAdapter(this,new ArrayList<>());
        GridLayoutManager manager = new GridLayoutManager(this,2);
        binding.titleView.setLayoutManager(manager);
        binding.titleView.setAdapter(titleAdapter);
        titleAdapter.setListener(new StudyTitleAdapter.OnSimpleClickListener() {
            @Override
            public void onClick(int position) {
                binding.viewPager2.setCurrentItem(position);

                String showTitle = titleAdapter.getSelectTitle();
                if (readFragment!=null){
                    if (showTitle.equals("听力")||showTitle.equals("原文")){
                        readFragment.setPlayStatus(true);
                    }else {
                        readFragment.setPlayStatus(false);
                    }
                }
            }
        });

        studyAdapter = new StudyAdapter(this,new ArrayList<>());
        binding.viewPager2.setAdapter(studyAdapter);
        binding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                titleAdapter.refreshIndex(position);

                String showTitle = titleAdapter.getSelectTitle();
                if (readFragment!=null){
                    if (showTitle.equals("听力")||showTitle.equals("原文")){
                        readFragment.setPlayStatus(true);
                    }else {
                        readFragment.setPlayStatus(false);
                    }
                }
            }
        });
    }

    /**************刷新数据***************/
    private void refreshData(){
        //获取数据进行展示
        chapterBean = presenter.margeChapterData(types,voaId);
        if (chapterBean==null){
            ToastUtil.showToast(this,"获取数据失败，请重试～");
            return;
        }

        //标题
        binding.toolbar.title.setText(HelpUtil.transTitleStyle(chapterBean.getTitleEn()));
        //显示数据
        List<Pair<String,String>> titleList = new ArrayList<>();
        List<Fragment> fragmentList = new ArrayList<>();

        String unitId = presenter.getWordUnitId(types,bookId,voaId);

        titleList.add(new Pair<>(TypeLibrary.StudyPageType.read,"听力"));
        readFragment = ReadFragment.getInstance(types,voaId,position,bookId,unitId);
        readFragment.setContext(this);
        fragmentList.add(readFragment);

        titleList.add(new Pair<>(TypeLibrary.StudyPageType.eval,"口语"));
        fragmentList.add(EvalFragment.getInstance(types,voaId));

        titleList.add(new Pair<>(TypeLibrary.StudyPageType.rank,"排行"));
        fragmentList.add(RankFragment.getInstance(types,voaId));

        //这里需要根据数据库来操作，判断当前是否存在单词
        //可以直接查询当前章节是否存在单词，然后在单词章节通过unitId反查当前的单词显示
        boolean hasWord = presenter.isExistWord(types,bookId,voaId);
        if (hasWord&&!TextUtils.isEmpty(unitId)){
            titleList.add(new Pair<>(TypeLibrary.StudyPageType.word,"单词"));
            fragmentList.add(WordShowFragment.getInstance(types,chapterBean.getBookId(),unitId,position));
        }

//        if (chapterBean.isShowImage()){
//            titleList.add("点读");
//        }

        if (chapterBean.isShowVideo()){
            titleList.add(new Pair<>(TypeLibrary.StudyPageType.talkShow,"配音"));
            fragmentList.add(DubbingFragment.getInstance(types,voaId));
        }

//        if (chapterBean.isShowExercise()){
//            titleList.add("练习");
//        }

        //针对中小学和小说内容不同处理
        sectionFragment = SectionFragment.getInstance(types,voaId);
        String showPageType = null;
        if (types.equals(TypeLibrary.BookType.junior_primary)
                ||types.equals(TypeLibrary.BookType.junior_middle)){
            showPageType = StudySettingManager.getInstance().getStudyJuniorHome();
        }else if (types.equals(TypeLibrary.BookType.bookworm)
                ||types.equals(TypeLibrary.BookType.newCamstory)
                ||types.equals(TypeLibrary.BookType.newCamstoryColor)){
            showPageType = StudySettingManager.getInstance().getStudyNovelHome();
        }
        if (showPageType.equals(TypeLibrary.StudyPageType.section)){
            titleList.add(0,new Pair<>(TypeLibrary.StudyPageType.section,"阅读"));
            fragmentList.add(0,sectionFragment);
        }else {
            titleList.add(new Pair<>(TypeLibrary.StudyPageType.section,"阅读"));
            fragmentList.add(sectionFragment);
        }

        GridLayoutManager manager = new GridLayoutManager(this,titleList.size());
        binding.titleView.setLayoutManager(manager);
        titleAdapter.refreshData(titleList);

        binding.viewPager2.setOffscreenPageLimit(fragmentList.size());
        studyAdapter.refreshData(fragmentList);

        //如果标题显示一个，则不用显示
        if (titleList.size()<=1){
            binding.titleView.setVisibility(View.GONE);
        }else {
            binding.titleView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showData(List<ChapterDetailBean> list,boolean isRefresh) {
        stopLoading();

        if (list!=null){
            if (list.size()>0){
                if (isRefresh){
                    //发送广播
                    EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.study_detailRefresh));
                }else {
                    //显示界面
                    refreshData();
                }
            }else {
                ToastUtil.showToast(this,"暂无此章节的详情数据");
            }
        }else {
            ToastUtil.showToast(this,"获取详情数据失败~");
        }
    }


    /*******************加载样式*****************/
    //显示加载
    @Override
    public void showLoading(String msg) {
        if (loadingDialog==null){
            loadingDialog = new LoadingDialog(this);
            loadingDialog.create();
        }
        loadingDialog.setMessage(msg);
        loadingDialog.show();
    }

    @Override
    public void showCollectArticle(boolean isSuccess,boolean isCollect) {
        if (isSuccess){
            if (isCollect){
                ChapterCollectEntity entity = new ChapterCollectEntity(
                        types,
                        voaId,
                        String.valueOf(UserInfoManager.getInstance().getUserId()),
                        bookId,
                        chapterBean.getPicUrl(),
                        chapterBean.getTitleEn(),
                        chapterBean.getTitleCn()
                );
                CommonDataManager.saveChapterCollectDataToDB(entity);
                ToastUtil.showToast(getApplicationContext(),"收藏文章成功～");
            }else {
                CommonDataManager.deleteChapterCollectDataToDB(types,voaId);
                ToastUtil.showToast(getApplicationContext(),"取消收藏文章成功～");
            }
        }else {
            ToastUtil.showToast(this,"请检查网络后重试～");
        }
    }

    //停止加载
    private void stopLoading(){
        if (loadingDialog!=null&&loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
    }

    /*********************更多弹窗******************/
    //显示更多弹窗
    private void showMenuDialog(View view){
        List<ActionItem> items = new ArrayList<>();
        items.add(new ActionItem(this, "PDF", R.drawable.ic_pdf_a));

        ChapterCollectEntity entity = CommonDataManager.getChapterCollectDataFromDB(types,voaId, String.valueOf(UserInfoManager.getInstance().getUserId()));
        if (entity==null) {
            items.add(new ActionItem(this, "收藏", R.drawable.ic_collect_not_a));
        } else {
            items.add(new ActionItem(this, "收藏", R.drawable.ic_collect_a));
        }
        if (InfoHelper.getInstance().openShare()){
            items.add(new ActionItem(this, "分享", R.drawable.ic_share_a));
        }

        String showTitle = titleAdapter.getSelectTitle();
        if (showTitle.equals("原文")
                ||showTitle.equals("听力")
                ||showTitle.equals("阅读")){
            items.add(new ActionItem(this, "字号", R.drawable.ic_font_a));

            if (!showTitle.equals("阅读")){
                items.add(new ActionItem(this, "设置", R.drawable.icon_study_new_set));
            }
        }

        // TODO: 2024/8/23 因为部分详情内容的时间参数是错误的，这里增加一个同步服务器文本内容的操作(展姐在新概念群里的展示的问题)
        items.add(new ActionItem(this,"内容修复",R.drawable.ic_study_fix));


        //根据当前显示的类型处理
        if (showTitle.equals("阅读")){
            Setting_ReadLanguageEntity readSetting = CommonDataManager.searchReadLanguageSettingFromDB(types,bookId,voaId);
            if (readSetting!=null){
                if (readSetting.languageType.equals(TypeLibrary.TextShowType.ALL)){
                    items.add(new ActionItem(this,"切换英文",R.drawable.ic_study_texttype_en));
                }else if (readSetting.languageType.equals(TypeLibrary.TextShowType.EN)){
                    items.add(new ActionItem(this,"切换双语",R.drawable.ic_study_texttype_cn));
                }
            }else {
                items.add(new ActionItem(this,"切换双语",R.drawable.ic_study_texttype_cn));
            }
        }

        PopMenu popMenu = new PopMenu(this,items);
        popMenu.setItemClickListener(new PopMenu.PopMenuOnItemClickListener() {
            @Override
            public void setItemOnclick(ActionItem actionItem, int i) {
                if (actionItem.mTitle.equals("PDF")){
                    if (UserInfoManager.getInstance().isLogin()){
                        downloadPdf();
                    }else {
//                        startActivity(new Intent(StudyActivity.this, Login.class));
                        LoginUtil.startToLogin(StudyActivity.this);
                    }
                }else if (actionItem.mTitle.equals("收藏")){
                    if (UserInfoManager.getInstance().isLogin()){
                        collectArticle();
                    }else {
//                        startActivity(new Intent(StudyActivity.this, Login.class));
                        LoginUtil.startToLogin(StudyActivity.this);
                    }
                }else if (actionItem.mTitle.equals("分享")){
                    shareAbility();
                }else if (actionItem.mTitle.equals("字号")){
                    int level = ConfigManager.Instance().getfontSizeLevel();
                    level = (level + 1) % 4;
                    ConfigManager.Instance().setfontSizeLevel(level);
                    try {
                        readFragment.refreshTextSize(16 + 2 * level);
                        //处理下阅读的显示
                        sectionFragment.setTextSize(16 + 2 * level);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else if (actionItem.mTitle.equals("设置")){
                    settingAbility();
                }else if (actionItem.mTitle.equals("切换英文")){
                    sectionFragment.switchTextType(TypeLibrary.TextShowType.EN);
                }else if (actionItem.mTitle.equals("切换双语")){
                    sectionFragment.switchTextType(TypeLibrary.TextShowType.ALL);
                }else if (actionItem.mTitle.equals("内容修复")){
                    //直接刷新数据，然后发送广播
                    presenter.getChapterDetailFromRemote(types,bookId,voaId);
                }
            }
        });
        popMenu.show(view);
    }

    //分享功能
    private void shareAbility(){
        String title = chapterBean.getTitleCn();
        String text = chapterBean.getTitleCn()+"\t"+chapterBean.getTitleEn();
        String imageUrl = chapterBean.getPicUrl();
        String shareUrl = ShareUtil.getInstance().getCourseShareUrl(types,chapterBean.getLevel(),voaId);
        if (types.equals(TypeLibrary.BookType.bookworm)
                ||types.equals(TypeLibrary.BookType.newCamstory)
                ||types.equals(TypeLibrary.BookType.newCamstoryColor)){
            //小说
            shareUrl = ShareUtil.getInstance().getCourseShareUrl(types,chapterBean.getLevel(),chapterBean.getOrderNumber());
        }

        ShareUtil.getInstance().shareArticle(this,chapterBean.getTypes(),chapterBean.getBookId(),chapterBean.getVoaId(),UserInfoManager.getInstance().getUserId(),title,text,imageUrl,shareUrl);
    }

    //设置功能
    private void settingAbility(){
        List<String> switchName = new ArrayList<>();
        List<Boolean> listSwitch = new ArrayList<>();
        List<SwitchDialog.SwitchDialogCallback> callbackList = new ArrayList<>();

        SwitchDialog.SwitchDialogCallback listenCallback = isTurn -> {
            ConfigManager.Instance().setSendListenReport(isTurn);
        };
        SwitchDialog.SwitchDialogCallback evaCallback = isTurn -> {
            ConfigManager.Instance().setsendEvaReport(isTurn);
        };
        SwitchDialog.SwitchDialogCallback autoCallBack= isTurn -> {
            ConfigManager.Instance().putAutoPlay(isTurn);
        };

        if (UserInfoManager.getInstance().isLogin()){
            switchName.add("展示听力报告");
            listSwitch.add(ConfigManager.Instance().getSendListenReport());
            callbackList.add(listenCallback);
        }

//        switchName.add("展示评测报告");
//        listSwitch.add(ConfigManager.Instance().getsendEvaReport());
//        callbackList.add(evaCallback);

        switchName.add("自动播放");
        listSwitch.add(ConfigManager.Instance().loadAutoPlay());
        callbackList.add(autoCallBack);

        SwitchDialog.getIntence()
                .init(switchName, listSwitch, callbackList)
                .inflateView(StudyActivity.this)
                .show();
    }

    //pdf下载
    private void downloadPdf(){
        PdfUtil.getInstance().checkNext(this,types,chapterBean.getTitleEn(),chapterBean.getPicUrl(),voaId);
    }

    //文章收藏
    private void collectArticle(){
        ChapterCollectEntity entity = CommonDataManager.getChapterCollectDataFromDB(types,voaId, String.valueOf(UserInfoManager.getInstance().getUserId()));
        if (entity==null){
            //收藏文章
            presenter.collectArticle(types,voaId,String.valueOf(UserInfoManager.getInstance().getUserId()), true);
        }else {
            //取消收藏文章
            presenter.collectArticle(types,voaId,String.valueOf(UserInfoManager.getInstance().getUserId()), false);
        }
    }

    /*****************回调数据*******************/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RefreshDataEvent event){

        //上一曲
        /*if (event.getType().equals(TypeLibrary.RefreshDataType.study_pre)){
            //获取上下章节数据
            switchChapterPair = presenter.getCurChapterIndex(chapterBean.getTypes(),chapterBean.getLevel(),chapterBean.getBookId(),chapterBean.getVoaId());
            if ((switchChapterPair.first == -1)
                    ||(switchChapterPair.first == 0)){
                ToastUtil.showToast(this,"当前已经是第一个");
                return;
            }

            BookChapterBean preChapterBean = switchChapterPair.second.first;
            if (preChapterBean==null){
                ToastUtil.showToast(this,"未找到上一章节数据");
                return;
            }

            voaId = preChapterBean.getVoaId();
            presenter.getChapterDetail(types,bookId,voaId);
        }*/

        //下一曲
        /*if (event.getType().equals(TypeLibrary.RefreshDataType.study_next)){
            //获取上下章节数据
            switchChapterPair = presenter.getCurChapterIndex(chapterBean.getTypes(),chapterBean.getLevel(),chapterBean.getBookId(),chapterBean.getVoaId());
            if ((switchChapterPair.first == -1)
                    ||(switchChapterPair.first == -2)){
                ToastUtil.showToast(this,"当前已经是最后一个");
                return;
            }

            BookChapterBean nextChapterBean = switchChapterPair.second.second;
            if (nextChapterBean==null){
                ToastUtil.showToast(this,"未找到下一章节数据");
                return;
            }

            voaId = nextChapterBean.getVoaId();
            presenter.getChapterDetail(types,bookId,voaId);
        }*/

        //随机播放
        /*if (event.getType().equals(TypeLibrary.RefreshDataType.study_random)){
            BookChapterBean randomChapterData = presenter.getRandomChapterData(chapterBean.getTypes(),chapterBean.getLevel(),chapterBean.getBookId());
            if (randomChapterData==null){
                ToastUtil.showToast(this,"未找到下一章节数据");
                return;
            }

            voaId = randomChapterData.getVoaId();
            presenter.getChapterDetail(types,bookId,voaId);
        }*/

        //奖励显示--toast
        if (event.getType().equals(TypeLibrary.RefreshDataType.reward_refresh_toast)){
            String showMsg = event.getMsg();
            if (!TextUtils.isEmpty(showMsg)){
                com.iyuba.core.common.util.ToastUtil.showToast(ConceptApplication.getInstance(),showMsg);

                //刷新用户信息并填充
                EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.userInfo));
            }else {

            }
        }

        //奖励显示--弹窗
        if (event.getType().equals(TypeLibrary.RefreshDataType.reward_refresh_dialog)){
            String showMsg = event.getMsg();
            if (!TextUtils.isEmpty(showMsg)){
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("奖励信息")
                        .setMessage(showMsg)
                        .setPositiveButton("确定",null)
                        .show();

                //刷新用户信息并填充
                EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.userInfo));
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onJuniorPlayEvent(JuniorBgPlayEvent event){
        if (event.getShowType().equals(JuniorBgPlayEvent.event_data_refresh)){
            //注销原文界面
            if (readFragment!=null){
                readFragment.destroyReadFragment();
            }

            //数据刷新
            position = event.getDataIndex();
            chapterBean = JuniorBgPlaySession.getInstance().getVoaList().get(position);
            voaId = chapterBean.getVoaId();
            presenter.getChapterDetail(types,bookId,voaId);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNovelPlayEvent(NovelBgPlayEvent event){
        if (event.getShowType().equals(NovelBgPlayEvent.event_data_refresh)){
            //注销原文界面
            if (readFragment!=null){
                readFragment.destroyReadFragment();
            }

            //数据刷新
            position = event.getDataIndex();
            chapterBean = NovelBgPlaySession.getInstance().getVoaList().get(position);
            voaId = chapterBean.getVoaId();
            presenter.getChapterDetail(types,bookId,voaId);
        }
    }

    /******************************统一权限处理*********************************/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //评测-录音评测
        if (requestCode== PermissionFixUtil.junior_eval_recordAudio_code){
            if (grantResults.length<permissions.length){
                ToastUtil.showToast(this,"当前权限为功能所必需的权限，请全部授权后使用");
            }else {
                ToastUtil.showToast(this,"授权成功，请点击按钮后录音评测");
            }
        }

        //纠音-录音评测
        if (requestCode==PermissionFixUtil.junior_fix_recordAudio_code){
            if (grantResults.length<permissions.length){
                ToastUtil.showToast(this,"当前权限为功能所必需的权限，请全部授权后使用");
            }else {
                ToastUtil.showToast(this,"授权成功，请点击按钮后录音评测");
            }
        }

        //配音-录音评测
        if (requestCode==PermissionFixUtil.junior_talkShow_recordAudio_code){
            if (grantResults.length<permissions.length){
                ToastUtil.showToast(this,"当前权限为功能所必需的权限，请全部授权后使用");
            }else {
                ToastUtil.showToast(this,"授权成功，请点击按钮后录音评测");
            }
        }
    }
}
