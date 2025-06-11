package com.iyuba.conceptEnglish.study;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.iyuba.conceptEnglish.PDF.PDFExport;
import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.databinding.StudyNewActivityBinding;
import com.iyuba.conceptEnglish.fragment.ClickReadFragment;
import com.iyuba.conceptEnglish.lil.concept_other.study_section.eval.StudyEvalFragment;
import com.iyuba.conceptEnglish.lil.concept_other.study_section.section.StudySectionFragment;
import com.iyuba.conceptEnglish.lil.concept_other.talkshow_detail.TalkShowDetailFragment;
import com.iyuba.conceptEnglish.lil.concept_other.util.HelpUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.event.RefreshDataEvent;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.StrLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.ConceptBookChooseManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.StudySettingManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.CommonDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.ConceptDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.Setting_ReadLanguageEntity;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Collect_chapter;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.rank.RankFragment;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.PermissionFixUtil;
import com.iyuba.conceptEnglish.lil.fix.concept.bgService.ConceptBgPlayEvent;
import com.iyuba.conceptEnglish.lil.fix.concept.bgService.ConceptBgPlaySession;
import com.iyuba.conceptEnglish.lil.fix.concept.study.ContentFragment;
import com.iyuba.conceptEnglish.manager.VoaDataManager;
import com.iyuba.conceptEnglish.sqlite.mode.Voa;
import com.iyuba.conceptEnglish.sqlite.op.VoaDetailOp;
import com.iyuba.conceptEnglish.sqlite.op.VoaOp;
import com.iyuba.conceptEnglish.util.ShareUtils;
import com.iyuba.conceptEnglish.widget.cdialog.CustomToast;
import com.iyuba.conceptEnglish.widget.dialog.SwitchDialog;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.InfoHelper;
import com.iyuba.core.common.activity.login.LoginUtil;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.lil.base.BaseStackActivity;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.widget.popmenu.ActionItem;
import com.iyuba.widget.popmenu.PopMenu;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


//课程详情界面
public class StudyNewActivity extends BaseStackActivity {

    //布局样式
    private StudyNewActivityBinding binding;

    private Context mContext;
    private StudyNewtAdapter adapter;
    private StudyTitleAdapter titleAdapter;
    private Voa voaTemp;
    private VoaOp voaOp;

    //原文
    ContentFragment contentFragment;
    //评测
    StudyEvalFragment evalFragment;
//    EvalFragment evalFragment;

    //评测排行
//    EvalRankFragment evalRankFragment;
    RankFragment evalRankFragment;
    //知识
    KnowledgeFragment knowledgeFragment;
    //练习
    ExerciseFragment exerciseFragment;
    //评论
    StudyCommentFragment studyCommentFragment;
    //点读
    ClickReadFragment clickReadFragment;
    //配音
    TalkShowDetailFragment detailFragment;
    //阅读界面
    StudySectionFragment sectionFragment;
    //单词界面
    VoaWordFragment voaWordFragment;
    //新版练习题界面
    ExerciseNewFragment exerciseNewFragment;

    //Current fragment's id index
    private int studyPage;//需要显示的界面
    private int curVoaId;//当前的课程id
    private boolean isFromPractise;//是否来自练习题界面

    //界面集合
    private List<Fragment> list;
    //标题数据
    private List<Pair<String,String>> titleList;
    //默认跳转的界面
    private String pageType;

    //跳转界面
    public static void start(Context context,String pageType,int voaId,boolean isFromPractise){
        Intent intent = new Intent();
        intent.setClass(context,StudyNewActivity.class);
        intent.putExtra(StrLibrary.pageType,pageType);
        intent.putExtra(StrLibrary.curVoaId,voaId);
        intent.putExtra(StrLibrary.from,isFromPractise);
        context.startActivity(intent);
    }

    private Handler adHanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    // TODO: 2023/6/21 修改成下面的接口 
                    /*String type = (voaTemp.isCollect.equals("1")) ? "insert" : "del";
                    ExeProtocol.exe(new FavorUpdateRequest(AccountManager.Instance(mContext).userId, voaTemp.voaId, type), new ProtocolResponse() {
                        @Override
                        public void finish(BaseHttpResponse bhr) {
                            FavorUpdateResponse reponse = (FavorUpdateResponse) bhr;
                            if (reponse.result == 1 || reponse.result == 2) {
                                voaOp.updateSynchro(voaTemp.voaId, 1);
                                adHanlder.sendEmptyMessage(2);
                            } else {
                                adHanlder.sendEmptyMessage(3);
                            }
                        }

                        @Override
                        public void error() {
                        }
                    });*/

                    boolean isCollect = voaTemp.isCollect.equals("1");
                    ConceptDataManager.collectArticle(TypeLibrary.BookType.conceptFour,String.valueOf(UserInfoManager.getInstance().getUserId()),String.valueOf(voaTemp.voaId),isCollect)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<Collect_chapter>() {
                                @Override
                                public void accept(Collect_chapter bean) throws Exception {
                                    if (bean!=null&&bean.msg.equals("Success")){
                                        voaOp.updateSynchro(voaTemp.voaId, 1);
                                        adHanlder.sendEmptyMessage(2);
                                    }else {
                                        adHanlder.sendEmptyMessage(3);
                                    }
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    adHanlder.sendEmptyMessage(3);
                                }
                            });
                    break;
                case 2:
                    String tip = (voaTemp.isCollect.equals("1")) ? "收藏成功" : "删除成功";
                    CustomToast.showToast(mContext, tip, 2000);
                    break;
                case 3:
                    CustomToast.showToast(mContext, "操作失败，请重试～", 1000);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        binding = StudyNewActivityBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        mContext = this;

        isFromPractise = getIntent().getBooleanExtra(StrLibrary.from,false);
        pageType = getIntent().getStringExtra(StrLibrary.pageType);
        if (TextUtils.isEmpty(pageType)){
            pageType = TypeLibrary.StudyPageType.read;
            //如果默认数据为阅读界面，则跳转到阅读界面
            if (StudySettingManager.getInstance().getStudyConceptHome().equals(TypeLibrary.StudyPageType.section)){
                pageType = TypeLibrary.StudyPageType.section;
            }
        }

        if (pageType.equals(TypeLibrary.StudyPageType.temp)){
            //如果默认数据为阅读界面，则跳转到阅读界面
            if (StudySettingManager.getInstance().getStudyConceptHome().equals(TypeLibrary.StudyPageType.section)){
                pageType = TypeLibrary.StudyPageType.section;
            }else if (StudySettingManager.getInstance().getStudyConceptHome().equals(TypeLibrary.StudyPageType.read)){
                pageType = TypeLibrary.StudyPageType.read;
            }
        }

        binding.studyText.setTextSize(16);
        voaOp = new VoaOp(this);
        voaTemp = VoaDataManager.Instace().voaTemp;
        if (voaTemp==null){
            return;
        }
        curVoaId = voaTemp.voaId;
        try {
            int lesson;
            String titleText;
            /**
             * curVoaId
             * 321001：青少版
             * 1001：美音,英音
             */
            if (curVoaId > 10000) {
//                titleText = String.format(Locale.CHINA, "Lesson %d %s", CommonUtils.getUnitFromTitle(voaTemp.title), voaTemp.title);
                titleText = voaTemp.title;
            } else {
                lesson = curVoaId % 1000;
                titleText = String.format(Locale.CHINA, "Lesson %d %s", lesson, voaTemp.title);
            }
            binding.studyText.setText(titleText);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //设置当前界面已经被阅读
        if (UserInfoManager.getInstance().isLogin()){
            ConceptDataManager.updateLocalMarkReadStatus(voaTemp.voaId, voaTemp.lessonType, UserInfoManager.getInstance().getUserId(), "1",voaTemp.position);
        }

        //设置点击事件
        initClick();

        //显示界面
        //初始化集合数据
        list = new ArrayList<>();
        titleList = new ArrayList<>();

        //当前选中的位置
        int positionInList = getIntent().getIntExtra(StrLibrary.position,0);
        contentFragment = ContentFragment.getInstance(positionInList,isFromPractise);//原文
        evalFragment = StudyEvalFragment.getInstance();//新的评测界面
        //设置下相关的数据
        String newBookType = ConceptBookChooseManager.getInstance().getBookType();
        int curVoaId = transVoaId(voaTemp.voaId);
        evalRankFragment = RankFragment.getInstance(newBookType,String.valueOf(curVoaId));//排行
        knowledgeFragment = new KnowledgeFragment();//知识
        exerciseFragment = new ExerciseFragment();//练习
        studyCommentFragment = new StudyCommentFragment();//评论
        sectionFragment = StudySectionFragment.getInstance();//阅读

        //阅读
        list.add(sectionFragment);
        titleList.add(new Pair<>(TypeLibrary.StudyPageType.section,"阅读"));
        //原文
        list.add(contentFragment);
        titleList.add(new Pair<>(TypeLibrary.StudyPageType.read,"听力"));
        //评测
        list.add(evalFragment);
        titleList.add(new Pair<>(TypeLibrary.StudyPageType.eval,"口语"));
        //排行
        list.add(evalRankFragment);
        titleList.add(new Pair<>(TypeLibrary.StudyPageType.rank,getResources().getString(R.string.voa_rank)));
        //单词
        int position = voaTemp.position;
        if (position!=-1){
            //原来使用中小学共通的界面
//            String types = TypeLibrary.BookType.conceptFour;
//            if (ConfigManager.Instance().isYouth()){
//                types = TypeLibrary.BookType.conceptJunior;
//            }
//            String bookId = String.valueOf(ConfigManager.Instance().loadInt("curBook"));
//            list.add(WordShowFragment.getInstance(types,bookId,String.valueOf(voaTemp.voaId),position));
            //之前从知识界面提取出来的界面
            voaWordFragment = VoaWordFragment.getInstance(position);
            list.add(voaWordFragment);
            titleList.add(new Pair<>(TypeLibrary.StudyPageType.word,"单词"));
        }

        //区分显示
        if (VoaDataManager.getInstance().voaTemp.lessonType.equals(TypeLibrary.BookType.conceptJunior)) {
            //配音
            detailFragment = TalkShowDetailFragment.getInstance(String.valueOf(voaTemp.voaId));
            list.add(detailFragment);
            titleList.add(new Pair<>(TypeLibrary.StudyPageType.talkShow,getResources().getString(R.string.voa_talk)));

            // TODO: 2023/10/25 之前李涛在新概念群组中说过，青少版没有习题功能
            //知识
//            list.add(knowledgeFragment);
//            titleList.add(new Pair<>(TypeLibrary.StudyPageType.knowledge,getResources().getString(R.string.voa_knowledge)));

            //点读
            if ("1".equals(voaTemp.clickRead)){
                clickReadFragment = ClickReadFragment.newInstance(String.valueOf(voaTemp.voaId));
                list.add(clickReadFragment);
                titleList.add(new Pair<>(TypeLibrary.StudyPageType.imageClick,getResources().getString(R.string.click_read_str)));
            }
        } else {
            //知识
            list.add(knowledgeFragment);
            titleList.add(new Pair<>(TypeLibrary.StudyPageType.knowledge,getResources().getString(R.string.voa_knowledge)));
            //习题
            list.add(exerciseFragment);
            titleList.add(new Pair<>(TypeLibrary.StudyPageType.exercise,"习题"));
            //评论(根据要求，暂时去掉)
            // TODO: 2023/10/25 因为可能涉及政治言论，暂时去掉
//            list.add(studyCommentFragment);
//            titleList.add(new Pair<>(TypeLibrary.StudyPageType.commit,getResources().getString(R.string.voa_remark)));
            // TODO: 2025/3/19 增加新版的练习功能(要求第一篇免费，其余的收费)
            if ((voaTemp.category==1 && positionInList%2==0) || voaTemp.category==2){
                exerciseNewFragment = ExerciseNewFragment.getInstance("concept",voaTemp.voaId,positionInList);
                list.add(exerciseNewFragment);
                titleList.add(new Pair<>(TypeLibrary.StudyPageType.exerciseNew,"测试"));
            }
        }

        //标题
        titleAdapter = new StudyTitleAdapter(this,titleList);
        GridLayoutManager manager = new GridLayoutManager(this,titleList.size());
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setAdapter(titleAdapter);
        titleAdapter.setListener(new StudyTitleAdapter.OnSimpleClickListener() {
            @Override
            public void onClick(int position) {
                Log.d("显示样式", "当前位置--"+position);

                //显示界面
                binding.viewPager.setCurrentItem(position);
                titleAdapter.refreshIndex(position);
            }
        });

        //界面
        adapter = new StudyNewtAdapter(getSupportFragmentManager(), list, this);
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.setOffscreenPageLimit(list.size());

        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                //当前的位置
                Log.d("显示样式2", "当前位置--"+binding.viewPager.getCurrentItem());

                int curShowPosition = binding.viewPager.getCurrentItem();
                titleAdapter.refreshIndex(curShowPosition);

                //设置选中的标题
                String titleTag = titleList.get(curShowPosition).first;

                //评测限制
                if ((!titleTag.equals(TypeLibrary.StudyPageType.eval)) && (evalFragment != null)) {
                    evalFragment.onPause();
                }

                //评论限制
                if ((!titleTag.equals(TypeLibrary.StudyPageType.commit)) && (studyCommentFragment != null)) {
                    studyCommentFragment.stopCommentPlayer();
                }

                //排行限制
//                if (!titleTag.equals(TypeLibrary.StudyPageType.rank) && evalRankFragment != null) {
//                    evalRankFragment.dismissDialog();
//                }

                //点读限制
                if (clickReadFragment!=null){
                    if (titleTag.equals(TypeLibrary.StudyPageType.imageClick)){
                        clickReadFragment.setPlayState(true);
                    }else {
                        clickReadFragment.setPlayState(false);
                    }
                }

                //原文限制
                if (contentFragment != null){
                    if (!titleTag.equals(TypeLibrary.StudyPageType.read)){
                        contentFragment.setPlayStatus(false);
                    }else {
                        contentFragment.setPlayStatus(true);
                    }
                }


                /*boolean instanceofStudy=list.get(curShowPosition) instanceof StudyCommentFragment;
                if (!instanceofStudy){
                    studyCommentFragment.pausePlay();
                }*/

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        //查询需要显示的样式
        for (int i = 0; i < titleList.size(); i++) {
            Pair<String,String> pair = titleList.get(i);
            if (pair.first.equals(pageType)){
                studyPage = i;
            }
        }

        titleAdapter.refreshIndex(studyPage);
        binding.viewPager.setCurrentItem(studyPage);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (voaTemp != null && "0".equals(voaTemp.isCollect)) {
            binding.favor.setBackgroundResource(R.drawable.nfavor);
        } else {
            binding.favor.setBackgroundResource(R.drawable.favor);
        }
    }

    private void initClick(){
        binding.buttonBack.setOnClickListener(v->{
            //返回
            finish();
        });
        binding.share.setOnClickListener(v->{
            //更多
            menu = buildMenu();
            menu.show(binding.share);
        });
        /*binding.favor.setOnClickListener(v->{
            //收藏
            favorArticle();
        });*/
        /*binding.tvPdf.setOnClickListener(v->{
            //下载pdf
            downPDF();
        });*/
    }

    public void refreshVoaData() {
        this.voaTemp = VoaDataManager.Instace().voaTemp;
        curVoaId = voaTemp.voaId;
        //顺便设置详情的数据
        //先设置详情数据
        VoaDataManager.getInstance().voaDetailsTemp = new VoaDetailOp(this).findDataByVoaId(curVoaId);
        VoaDataManager.getInstance().setSubtitleSum(voaTemp,VoaDataManager.getInstance().voaDetailsTemp);
        if (!isDestroyed()) {

            int lesson;
            String titleText;
            /**
             * curVoaId
             * 321001：青少版
             * 1001：美音,英音
             */
            if (curVoaId > 10000) {
                titleText = voaTemp.title;
            } else {
                lesson = curVoaId % 1000;
                titleText = String.format(Locale.CHINA, "Lesson %d %s", lesson, voaTemp.title);
            }
            binding.studyText.setText(HelpUtil.transTitleStyle(titleText));

//            if (this.textFragment.isAdded()) {
//                this.textFragment.initText();
//            }
            if (this.contentFragment.isAdded()) {
                this.contentFragment.refreshData();
            }
            if (this.evalFragment.isAdded()) {
                this.evalFragment.refreshData();
            }
            if (this.evalRankFragment.isAdded()) {
                this.evalRankFragment.refreshDate();
            }
            if (this.voaWordFragment.isAdded()){
                this.voaWordFragment.refreshData();
            }
            if (this.knowledgeFragment.isAdded()) {
                this.knowledgeFragment.refreshView();
            }
            if (this.exerciseFragment.isAdded()) {
                this.exerciseFragment.refreshView();
            }
            if (this.studyCommentFragment.isAdded()) {
                this.studyCommentFragment.refreshData();
            }
            if (this.sectionFragment.isAdded()){
                this.sectionFragment.refreshData();
            }
        }
    }

    void favorArticle() {
        if (UserInfoManager.getInstance().isLogin()) {
            if (this.voaTemp.isCollect.equals("0")) {
                binding.favor.setBackgroundResource(R.drawable.favor);
                voaTemp.isCollect = "1";
                //保存在其他数据库中
                ConceptDataManager.updateLocalMarkCollectStatus(voaTemp.voaId, voaTemp.lessonType, UserInfoManager.getInstance().getUserId(), voaTemp.isCollect, voaTemp.position);
                voaOp.insertDataToCollection(this.voaTemp.voaId);
                voaOp.updateSynchro(this.voaTemp.voaId, 0);
                adHanlder.sendEmptyMessage(1);
                return;
            }
            binding.favor.setBackgroundResource(R.drawable.nfavor);
            voaTemp.isCollect = "0";
            //保存在其他数据库中
            ConceptDataManager.updateLocalMarkCollectStatus(voaTemp.voaId, voaTemp.lessonType, UserInfoManager.getInstance().getUserId(), voaTemp.isCollect, voaTemp.position);
            voaOp.deleteDataInCollection(this.voaTemp.voaId);
            voaOp.updateSynchro(this.voaTemp.voaId, 0);
            adHanlder.sendEmptyMessage(1);
            return;
        }
        LoginUtil.startToLogin(mContext);
    }

    void downPDF() {
        PDFExport export = new PDFExport(mContext);
        if (ConceptBookChooseManager.getInstance().getBookType().equals(TypeLibrary.BookType.conceptFourUS))
            export.getPDFData(voaTemp.lessonType,voaTemp.title, voaTemp.voaId + "");
        else
            export.getPDFData(voaTemp.lessonType,voaTemp.title, voaTemp.voaId * 10 + "");

    }

    /*@NeedsPermission({Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void initStorage() {
    }

    @NeedsPermission({Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void initShare() {
        realShare();
    }

    @OnPermissionDenied({Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void locationDenied() {
        CustomToast.showToast(this.mContext, "请到设置中开启存储和录音权限,开启后即可正常使用此功能", 3000);
    }

    @SuppressLint({"NeedOnRequestPermissionsResult"})
    public void onRequestPermissionsResult(int paramInt, @NonNull String[] paramArrayOfString, @NonNull int[] paramArrayOfInt) {
        super.onRequestPermissionsResult(paramInt, paramArrayOfString, paramArrayOfInt);
        StudyNewActivityPermissionsDispatcher.onRequestPermissionsResult(this, paramInt, paramArrayOfInt);
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //评测的权限
        if (requestCode== PermissionFixUtil.concept_eval_recordAudio_code){
            if (grantResults.length<permissions.length){
                ToastUtil.showToast(this,"当前权限为功能所必需的权限，请全部授权后使用");
            }else {
                ToastUtil.showToast(this,"授权成功，请点击按钮后录音评测");
            }
        }

        //纠音的权限
        if (requestCode==PermissionFixUtil.concept_fix_recordAudio_code){
            if (grantResults.length<permissions.length){
                ToastUtil.showToast(this,"当前权限为功能所必需的权限，请全部授权后使用");
            }else {
                ToastUtil.showToast(this,"授权成功，请点击按钮后录音提交评测");
            }
        }
    }

    private PopMenu menu;

    private PopMenu buildMenu() {
        List<ActionItem> items = new ArrayList<>(4);
        items.add(new ActionItem(this, "PDF", R.drawable.ic_pdf_a));

        if (voaTemp != null && "0".equals(voaTemp.isCollect)) {
            items.add(new ActionItem(this, "收藏", R.drawable.ic_collect_not_a));
        } else {
            items.add(new ActionItem(this, "收藏", R.drawable.ic_collect_a));
        }

        if (InfoHelper.getInstance().openShare()){
            items.add(new ActionItem(this, "分享", R.drawable.ic_share_a));
        }
        items.add(new ActionItem(this, "字号", R.drawable.ic_font_a));
        items.add(new ActionItem(this, "设置", R.drawable.icon_study_new_set));
        //判断当前显示的功能
        String showTitle = titleAdapter.getSelectTitle();
        if (showTitle.equals("阅读")){
            String types = ConceptBookChooseManager.getInstance().getBookType();
            String bookId = String.valueOf(ConceptBookChooseManager.getInstance().getBookId());

            Setting_ReadLanguageEntity readSetting = CommonDataManager.searchReadLanguageSettingFromDB(types,bookId,String.valueOf(voaTemp.voaId));
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

        if (showTitle.equals("配音")){
            items.add(new ActionItem(this, "重载音视频", R.drawable.icon_study_download));
        }

        menu = new PopMenu(mContext, items);
        menu.setItemClickListener((item, position) -> {
            if (item.mTitle.equals("PDF")){
                downPDF();
            }else if (item.mTitle.equals("收藏")){
                favorArticle();
            }else if (item.mTitle.equals("分享")){
//                StudyNewActivityPermissionsDispatcher.initShareWithPermissionCheck(this);
                realShare();
            }else if (item.mTitle.equals("字号")){
                int level = ConfigManager.Instance().getfontSizeLevel();
                level = (level + 1) % 4;
                ConfigManager.Instance().setfontSizeLevel(level);
                try {
//                    this.textFragment.setTextSize(16 + (2 * level));
                    this.contentFragment.setTextSize(16 + (2 * level));
                    //同步设置阅读界面
                    this.sectionFragment.setTextSize(16 + (2 * level));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if (item.mTitle.equals("设置")){
                //名称
                List<String> switchName = new ArrayList<>();
                //配置
                List<Boolean> listSwitch = new ArrayList<>();
                //回调
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

                if (UserInfoManager.getInstance().isLogin()&&!ConceptBgPlaySession.getInstance().isTempData()){
                    switchName.add("展示听力报告");
                    listSwitch.add(ConfigManager.Instance().getSendListenReport());
                    callbackList.add(listenCallback);
                }

//                switchName.add("展示评测报告");
//                listSwitch.add(ConfigManager.Instance().getsendEvaReport());
//                callbackList.add(evaCallback);

                switchName.add("自动播放");
                listSwitch.add(ConfigManager.Instance().loadAutoPlay());
                callbackList.add(autoCallBack);

                SwitchDialog.getIntence()
                        .init(switchName, listSwitch, callbackList)
                        .inflateView(this)
                        .show();
            }else if (item.mTitle.equals("切换英文")){
                sectionFragment.switchTextType(TypeLibrary.TextShowType.EN);
            }else if (item.mTitle.equals("切换双语")){
                sectionFragment.switchTextType(TypeLibrary.TextShowType.ALL);
            }else if (item.mTitle.equals("重载音视频")){
                detailFragment.deleteAudioAndVideo();
                detailFragment.checkVideoAndMedia(false);
            }
        });
        return menu;
    }

    private void realShare() {
        String content = voaTemp.title;
        String siteUrl = getShareUrl();
        String title = "[我正在新概念中读:"
                + voaTemp.title + " " + voaTemp.titleCn + "这篇课文,非常有意思，大家快来读吧！] ";
        ShareUtils shareUtils = new ShareUtils();
        shareUtils.setVoaId(this.voaTemp.voaId);
        shareUtils.setMContext(this.mContext);

        int voaId = curVoaId;
//        switch (ConfigManager.Instance().getBookType()) {
        switch (VoaDataManager.getInstance().voaTemp.lessonType) {
            case TypeLibrary.BookType.conceptFourUS:
            case TypeLibrary.BookType.conceptJunior:
            default:
                //使用默认
                break;
            case TypeLibrary.BookType.conceptFourUK:
                voaId = voaId * 10;
                break;
        }
        String picUrl = voaTemp.pic;
        String keySuffix = "http://static2.";
        String replaceSuffix = "http://staticvip2.";
        if (picUrl.startsWith(keySuffix)){
            picUrl = picUrl.replace(keySuffix,replaceSuffix);
        }
        shareUtils.showShareMiniProgram(this.mContext, voaId + "",picUrl,siteUrl, title, content, shareUtils.platformActionListener);
    }

    private String getShareUrl() {

        int bookId = voaTemp.voaId / 1000;
//        switch (ConfigManager.Instance().getBookType()) {
        switch (VoaDataManager.getInstance().voaTemp.lessonType) {
            // TODO: 2023/10/24 李涛在新概念群里说需要更换成这个分享地址 https://www.aienglish.com/newconcept

            case TypeLibrary.BookType.conceptFourUS:
            default:
//        https://"+Constant.userSpeech+Constant.IYUBA_CN+"newconcept/course?language=US&unit=1&lesson=1001
//                return "http://"+Constant.userSpeech + "newconcept/course?language=US&unit=" + bookId + "&lesson=" + voaTemp.voaId;
                return "https://www.aienglish.com/newconcept/course?language=US&unit=" + bookId + "&lesson=" + voaTemp.voaId;
            case TypeLibrary.BookType.conceptFourUK:
//        https://"+Constant.userSpeech+Constant.IYUBA_CN+"newconcept/course?language=UK&unit=1&lesson=1001
//                return "http://"+Constant.userSpeech + "newconcept/course?language=UK&unit=" + bookId + "&lesson=" + voaTemp.voaId;
                return "https://www.aienglish.com/newconcept/course?language=UK&unit=" + bookId + "&lesson=" + voaTemp.voaId;
            case TypeLibrary.BookType.conceptJunior:
                //http://m."+Constant.IYUBA_CN+"voaS/playPY.jsp?apptype=newconcept&id=321001
                return "http://m."+Constant.IYUBA_CN+"voaS/playPY.jsp?apptype=newconcept&id=" + voaTemp.voaId;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

        //销毁其他操作
        Log.d("界面销毁", "onDestroy: ");
    }

    /********************回调数据********************/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RefreshDataEvent event){
        //奖励显示--toast
//        if (event.getType().equals(TypeLibrary.RefreshDataType.reward_refresh_toast)){
//            String showMsg = event.getMsg();
//            if (!TextUtils.isEmpty(showMsg)){
//                com.iyuba.core.common.util.ToastUtil.showToast(ConceptApplication.getInstance(),showMsg);
//
//                //刷新用户信息并填充
//                EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.userInfo));
//            }
//        }

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
    public void onPlayEvent(ConceptBgPlayEvent event){
        if (event.getShowType().equals(ConceptBgPlayEvent.event_data_refresh)){
            //刷新数据显示
            refreshVoaData();
        }
    }

    //将voaId数据转换为标准的数据
    private int transVoaId(int voaId){
        switch (VoaDataManager.getInstance().voaTemp.lessonType){
            case TypeLibrary.BookType.conceptFourUS:
                return voaId;
            case TypeLibrary.BookType.conceptFourUK:
                return voaId*10;
            case TypeLibrary.BookType.conceptJunior:
                return voaId;
            default:
                return voaId;
        }
    }
}
