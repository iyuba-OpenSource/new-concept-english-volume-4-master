package com.iyuba.conceptEnglish.adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.databinding.ListitemVoaHomeBinding;
import com.iyuba.conceptEnglish.lil.concept_other.download.FileDownloadBean;
import com.iyuba.conceptEnglish.lil.concept_other.download.FileDownloadEvent;
import com.iyuba.conceptEnglish.lil.concept_other.download.FileDownloadManager;
import com.iyuba.conceptEnglish.lil.concept_other.download.FilePathUtil;
import com.iyuba.conceptEnglish.lil.concept_other.util.HelpUtil;
import com.iyuba.conceptEnglish.lil.concept_other.verify.AbilityControlManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.StrLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.ConceptBookChooseManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.ConceptDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.concept.LocalMarkEntity_conceptDownload;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.PermissionFixUtil;
import com.iyuba.conceptEnglish.lil.fix.concept.bgService.ConceptBgPlayEvent;
import com.iyuba.conceptEnglish.manager.DownloadStateManager;
import com.iyuba.conceptEnglish.sqlite.db.WordChildDBManager;
import com.iyuba.conceptEnglish.sqlite.mode.ArticleRecordBean;
import com.iyuba.conceptEnglish.sqlite.mode.DownloadInfo;
import com.iyuba.conceptEnglish.sqlite.mode.MultipleChoice;
import com.iyuba.conceptEnglish.sqlite.mode.Voa;
import com.iyuba.conceptEnglish.sqlite.mode.VoaDetail;
import com.iyuba.conceptEnglish.sqlite.mode.VoaSound;
import com.iyuba.conceptEnglish.sqlite.op.ArticleRecordOp;
import com.iyuba.conceptEnglish.sqlite.op.DownloadInfoOp;
import com.iyuba.conceptEnglish.sqlite.op.MultipleChoiceOp;
import com.iyuba.conceptEnglish.sqlite.op.MultipleRecordOp;
import com.iyuba.conceptEnglish.sqlite.op.TestRecordOp;
import com.iyuba.conceptEnglish.sqlite.op.VoaDetailOp;
import com.iyuba.conceptEnglish.sqlite.op.VoaOp;
import com.iyuba.conceptEnglish.sqlite.op.VoaSoundOp;
import com.iyuba.conceptEnglish.sqlite.op.VoaWordOp;
import com.iyuba.conceptEnglish.sqlite.op.WordErrorOP;
import com.iyuba.conceptEnglish.sqlite.op.WordPassOp;
import com.iyuba.conceptEnglish.sqlite.op.WordPassUserOp;
import com.iyuba.conceptEnglish.study.StudyNewActivity;
import com.iyuba.conceptEnglish.util.CommonUtils;
import com.iyuba.conceptEnglish.util.ConceptApplication;
import com.iyuba.conceptEnglish.util.ConstUtil;
import com.iyuba.conceptEnglish.widget.MyRing;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.activity.login.LoginUtil;
import com.iyuba.core.common.data.model.VoaWord2;
import com.iyuba.core.common.setting.SettingConfig;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.common.widget.RoundProgressBar;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.util.LibGlide3Util;
import com.iyuba.core.lil.view.PermissionMsgDialog;
import com.iyuba.core.me.activity.NewVipCenterActivity;
import com.iyuba.imooclib.data.local.IMoocDBManager;
import com.iyuba.imooclib.data.model.StudyProgress;
import com.iyuba.imooclib.ui.content.ContentActivity;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

/**
 * 文章列表适配器
 */
public class VoaAdapterNew extends RecyclerView.Adapter<VoaAdapterNew.MyViewHolder> {
    /*private FileDownloader fileDownloader;*/
    private List<DownloadInfo> infoList;
    private Voa voa;
    private Context mContext;
    private List<Voa> mList = new ArrayList<Voa>();
    private MyViewHolder viewHolder;
    public boolean modeDelete = false;
    private VoaOp voaOp;
    private DownloadStateManager manager;
    private DownloadInfoOp downloadInfoOp;
    private Handler handler;

    private MyOnclickListener listener;

    //单词信息
    private VoaWordOp voaWordOp;
    private List<VoaWord2> mWordList = new ArrayList<>();
    private WordPassUserOp wordPassUserOp;
    private WordPassOp wordPassOp;
    private WordErrorOP wordErrorOP;

    //评测信息
    private VoaDetailOp voaDetailOp;

    private VoaSoundOp voaSoundOp;

    //做题信息
    private MultipleRecordOp multipleRecordOp;
    private MultipleChoiceOp multipleChoiceOp;
    private List<MultipleChoice> choices;
    private TestRecordOp testRecordOp;

    //课文记录
    private ArticleRecordOp articleRecordOp;
    private Fragment mFragment;
    private static final int adFlag = -99;


    public VoaAdapterNew(Context context, List<Voa> list, MyOnclickListener listener, Fragment fragment) {
        /*fileDownloader = FileDownloader.instance();*/
        manager = DownloadStateManager.instance();
        downloadInfoOp = manager.downloadInfoOp;
        infoList = manager.downloadList;
        mContext = context;
        mFragment = fragment;
        mList = list;
        articleRecordOp = new ArticleRecordOp(mContext);
        voaWordOp = new VoaWordOp(mContext);
        voaDetailOp = new VoaDetailOp(mContext);
        voaSoundOp = new VoaSoundOp(mContext);
        wordPassUserOp = new WordPassUserOp(mContext);
        wordPassOp = new WordPassOp(mContext);
        wordErrorOP = new WordErrorOP(mContext);
        multipleRecordOp = new MultipleRecordOp(mContext);
        multipleChoiceOp = new MultipleChoiceOp(mContext);
        testRecordOp = new TestRecordOp(mContext);
        this.handler = manager.handler;
        this.listener = listener;
        init();
    }

    public VoaAdapterNew(Context context) {
        /*fileDownloader = FileDownloader.instance();*/
        mContext = context;
        init();
    }

    public void addList(List<Voa> voasTemps) {
        mList.addAll(voasTemps);
    }

    private void init() {
        voaOp = new VoaOp(mContext);
    }

    public void clearList() {
        mList.clear();
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ListitemVoaHomeBinding binding = ListitemVoaHomeBinding.inflate(LayoutInflater.from(mContext), viewGroup, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, @SuppressLint("RecyclerView") final int position) {
        final Voa curVoa = mList.get(position);
        final DownloadInfo info = getDownloadInfo(curVoa.voaId);
        final DownloadInfo infoBritish = getDownloadInfo(curVoa.voaId * 10);

        viewHolder = myViewHolder;
        //设置显示的数据
        initWordData(curVoa, myViewHolder.llWord, myViewHolder.ringWord, myViewHolder.tvWord, position);
        initEvalData(curVoa, myViewHolder.ringEval, myViewHolder.tvEval);
        initArticleData(curVoa, myViewHolder.ringArticle, myViewHolder.tvArticle);
        initTestData(curVoa, myViewHolder.testLayout, myViewHolder.ringTest, myViewHolder.tvTest);
        initMiccroLessonDataFromLocal(curVoa, myViewHolder.mocLayout, myViewHolder.microLecture, myViewHolder.tvMicroLecture);

        if (modeDelete) {
            viewHolder.deleteBox.setVisibility(View.VISIBLE);
        } else {
            viewHolder.deleteBox.setVisibility(View.GONE);
        }

        if (mList.get(position).isDelete) {
            viewHolder.deleteBox.setImageResource(R.drawable.check_box_checked);
        } else {
            viewHolder.deleteBox.setImageResource(R.drawable.check_box);
        }

        final int voaId = curVoa.voaId;
        int index = curVoa.voaId % 1000;

        //修改后的样式
        viewHolder.style2Layout.setVisibility(View.VISIBLE);
        viewHolder.style1Layout.setVisibility(View.GONE);

        if (TextUtils.isEmpty(curVoa.pic)) {
            viewHolder.voaIndex.setVisibility(View.VISIBLE);
            viewHolder.voaPic.setImageResource(R.drawable.shape_btn_bg);

            if (ConceptBookChooseManager.getInstance().getBookType().equals(TypeLibrary.BookType.conceptJunior)) {
                viewHolder.voaIndex.setText(CommonUtils.getUnitFromTitle(curVoa.title) + "");
            } else {
                viewHolder.voaIndex.setText(index + "");
            }
        } else {
            viewHolder.voaIndex.setVisibility(View.GONE);
            //加载图片
            LibGlide3Util.loadImg(mContext, curVoa.pic, 0, viewHolder.voaPic);
        }

        //增加lesson标识
        viewHolder.lesson.setVisibility(View.VISIBLE);
        if (curVoa.voaId > 10000) {
            //这里先找到unit的位置，然后将数据切分为单元号+标题

            //先设置标题(可能存在两种情况：unit xx和unitxx)
            String[] titleArray = curVoa.title.trim().split(" ");
            StringBuffer titleBuffer = new StringBuffer();
            unitTitle:
            for (int i = 0; i < titleArray.length; i++) {
                //第一种情况：Unit 10这种
                if (titleArray[i].trim().toLowerCase().equals("unit")) {
                    if (titleArray.length > i + 1) {
                        titleBuffer.append(titleArray[i]).append(" ").append(titleArray[i + 1]);
                    }
                    break unitTitle;
                }

                //第二种情况：Unit10这种
                if (titleArray[i].trim().toLowerCase().startsWith("unit")) {
                    titleBuffer.append(titleArray[i]);
                    break unitTitle;
                }
            }

            String showTitle = titleBuffer.toString();
            if (!TextUtils.isEmpty(showTitle)) {
                viewHolder.lesson.setVisibility(View.VISIBLE);
                //二次分拆
                String[] lessonArray = showTitle.split(" ");
                if (lessonArray.length > 1) {
                    viewHolder.lesson.setText(showTitle);
                } else {
                    String showLesson = showTitle.trim().toLowerCase().replace("unit", "");
                    viewHolder.lesson.setText("Unit " + showLesson);
                }

                String title = curVoa.title.replace(showTitle, "").trim();
                viewHolder.title.setText(HelpUtil.transTitleStyle(title));
            } else {
                viewHolder.lesson.setVisibility(View.GONE);
                viewHolder.title.setText(HelpUtil.transTitleStyle(curVoa.title));
            }

                /*if (titleArray.length>2){
                    String unitName = titleArray[0]+" "+titleArray[1];
                    viewHolder.lesson.setVisibility(View.VISIBLE);
                    viewHolder.lesson.setText(unitName);

                    String title = curVoa.title.replace(unitName,"").trim();
                    viewHolder.title.setText(HelpUtil.transTitleStyle(title));
                }else {
                    viewHolder.lesson.setVisibility(View.GONE);
                    viewHolder.title.setText(HelpUtil.transTitleStyle(curVoa.title));
                }*/

            //再设置中文(可能存在两种情况：unit xx和unitxx)
            String[] titleCnArray = curVoa.titleCn.trim().split(" ");
            StringBuffer titleCnBuffer = new StringBuffer();
            unitCnTitle:
            for (int i = 0; i < titleCnArray.length; i++) {
                if (titleCnArray[i].trim().toLowerCase().equals("unit")) {
                    if (titleCnArray.length > i + 1) {
                        titleCnBuffer.append(titleCnArray[i]).append(" ").append(titleCnArray[i + 1]);
                    }
                    break unitCnTitle;
                }

                if (titleCnArray[i].trim().toLowerCase().startsWith("unit")) {
                    titleCnBuffer.append(titleCnArray[i]);
                    break unitCnTitle;
                }
            }

            String showCnTitle = titleCnBuffer.toString();
            if (!TextUtils.isEmpty(showCnTitle)) {
                String titleCn = curVoa.titleCn.replace(showCnTitle, "").trim();
                viewHolder.titleCn.setText(titleCn);
            } else {
                viewHolder.titleCn.setText(curVoa.titleCn);
            }

                /*if (titleCnArray.length>2){
                    String unitName = titleCnArray[0]+" "+titleCnArray[1];
                    String titleCn = curVoa.titleCn.replace(unitName,"").trim();
                    viewHolder.titleCn.setText(titleCn);
                }else {
                    viewHolder.titleCn.setText(curVoa.titleCn);
                }*/
        } else {
            int lesson = curVoa.voaId % 1000;
            viewHolder.lesson.setVisibility(View.VISIBLE);
            viewHolder.lesson.setText(String.format("Lesson %d", lesson));

            viewHolder.title.setText(HelpUtil.transTitleStyle(curVoa.title));
            viewHolder.titleCn.setText(curVoa.titleCn);
        }

        //显示下载按钮的状态
        showDownloadStatus(position);

        // 是否阅读
        if (curVoa.isRead != null && curVoa.isRead.equals("0")) {
//            viewHolder.voaN.setTextColor(Constant.unreadCnColor);
            viewHolder.title.setTextColor(Constant.normalColor);
            viewHolder.titleCn.setTextColor(Constant.unreadCnColor);
        } else if (curVoa.isRead != null && curVoa.isRead.equals("1")) {
//            viewHolder.voaN.setTextColor(Constant.readColor);
            viewHolder.title.setTextColor(Constant.readColor);
            viewHolder.titleCn.setTextColor(Constant.readColor);
        }


        viewHolder.mCircleProgressBar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (!UserInfoManager.getInstance().isLogin()) {
                    LoginUtil.startToLogin(mContext);
                    return;
                }

                List<LocalMarkEntity_conceptDownload> downloadList = ConceptDataManager.getLocalMarkDownloadAndDownloadingData(UserInfoManager.getInstance().getUserId());
                if (downloadList.size() >= 10 && !UserInfoManager.getInstance().isVip()) {
                    new AlertDialog.Builder(mContext)
                            .setTitle("下载限制")
                            .setMessage("非会员仅能下载10篇课程，开通会员后下载无限制，是否开通会员?")
                            .setPositiveButton("开通会员", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    NewVipCenterActivity.start(mContext, NewVipCenterActivity.VIP_APP);
                                }
                            }).setNegativeButton("考虑一下", null)
                            .setCancelable(false)
                            .create().show();
                    return;
                }

                //检查下载的数量
                Voa tempVoa = mList.get(position);
                LocalMarkEntity_conceptDownload downloadData = ConceptDataManager.getLocalMarkDownloadSingleData(tempVoa.voaId, tempVoa.lessonType, UserInfoManager.getInstance().getUserId());
                int downloadStatus = 0;
                if (downloadData != null && !TextUtils.isEmpty(downloadData.isDownload)) {
                    downloadStatus = Integer.parseInt(downloadData.isDownload);
                }

                Log.d("显示下载状态", "下载后的状态--" + downloadStatus);

                if (downloadStatus == Integer.parseInt(TypeLibrary.FileDownloadStateType.file_downloaded)) {
                    if (listener != null) {
                        listener.onClick(position, TypeLibrary.StudyPageType.read);
                    }
                    return;
                }

                if (downloadStatus == Integer.parseInt(TypeLibrary.FileDownloadStateType.file_isDownloading)) {
                    ToastUtil.showToast(mContext, "正在下载中");
                    return;
                }

                showPermissionDialog(tempVoa, position);
            }
        });

        viewHolder.ringEval.setOnClickListener(v -> {
            if (listener != null)
                listener.onClick(position, TypeLibrary.StudyPageType.eval);

        });

        viewHolder.ringTest.setOnClickListener(v -> {
            if (!(voaId < 2000 && voaId % 2 == 0) || true) {
                if (listener != null)
                    listener.onClick(position, TypeLibrary.StudyPageType.exercise);
            }
        });

        viewHolder.microLecture.setOnClickListener(v -> {
            if (!"0".equalsIgnoreCase(mList.get(position).titleid)) {
                //停止音频播放
                EventBus.getDefault().post(new ConceptBgPlayEvent(ConceptBgPlayEvent.event_audio_pause));
                EventBus.getDefault().post(new ConceptBgPlayEvent(ConceptBgPlayEvent.event_control_pause));
                //跳转到微课
                ConstUtil.sJumpToMicro = true;
                mContext.startActivity(ContentActivity.buildIntent(mContext, mList.get(position).categoryid, "class.jichu"));
            }
        });

        viewHolder.ringWord.setOnClickListener(v -> {
            if (listener != null)
                listener.onClick(position, TypeLibrary.StudyPageType.word);
        });

        viewHolder.ringArticle.setOnClickListener(v -> {
            if (listener != null)
                listener.onClick(position, TypeLibrary.StudyPageType.read);
        });

        viewHolder.itemView.setOnClickListener(v -> {
            ConceptApplication.courseIndex = (position + 1);
            if (listener != null)
                listener.onClick(position, TypeLibrary.StudyPageType.temp);
        });
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    /**
     * 单词统计
     *
     * @param curVoa
     * @param
     * @param tvWord
     */
    private void initWordData(Voa curVoa, LinearLayout wordLayout, MyRing ringWord, TextView tvWord, int position) {
        int currWordPass = 0;

        // TODO: 2023/4/11
        if (curVoa.lessonType.equals(TypeLibrary.BookType.conceptJunior)) {
            //青少版
            mWordList = getChildWord(position + 1);
        } else {
            //其他数据
            mWordList = voaWordOp.findDataByVoaId(curVoa.voaId);
        }

        //这里判断没有单词就不要显示了，免得跳转过去之后还没有，太尴尬了
        if (mWordList != null && mWordList.size() > 0) {
            wordLayout.setVisibility(View.VISIBLE);
        } else {
            wordLayout.setVisibility(View.GONE);
            return;
        }

        int sum = 0;
        int right = 0;

        //应该不用处理，只是原来单词的部分
        if (ConceptBookChooseManager.getInstance().getBookType().equals(TypeLibrary.BookType.conceptJunior)) {
//            currWordPass = wordPassOp.getCurrPassNum(CommonUtils.bookTranslateServiceToLocalForPass(curVoa.category+""));
            currWordPass = wordPassOp.getCurrPassNum(curVoa.category);
        } else {
            currWordPass = wordPassOp.getCurrPassNum(curVoa.voaId / 1000);
        }
        Timber.d("Query word voaId: %d", curVoa.voaId);


        //这里统一使用这种逻辑操作
        //因为这里默认数据都是连续性的，不会出现断层操作，因此直接通过position进行判断，下边的判断方式进行注释
        int realPosition = position + 1;
        if (realPosition <= currWordPass) {
            sum = mWordList.size();
            right = wordPassUserOp.getRightNum(curVoa.voaId);
//            if (right == 0) {
//                right = sum - wordErrorOP.getWordNum(curVoa.voaId);
//            }

            if (right == 0) {
                ringWord.setCurrProgress(0, R.drawable.ic_home_word_0);
            } else {
                ringWord.setCurrProgress(360 * right / sum, R.drawable.ic_home_word_1);
            }
        } else {
            ringWord.setCurrProgress(0, R.drawable.ic_home_word_0);
            sum = mWordList.size();
        }

        /*if (curVoa.voaId % 1000 <= currWordPass) {
            if (mWordList.size() > 0) {
                sum = mWordList.size();
                right = wordPassUserOp.getRightNum(curVoa.voaId);
                if (right == 0) {
                    right = sum - wordErrorOP.getWordNum(curVoa.voaId);
                }
                ringWord.setCurrProgress(360 * right / sum, R.drawable.ic_home_word_1);
            }
        } else {
            ringWord.setCurrProgress(0, R.drawable.ic_home_word_0);
            sum = mWordList.size();
        }*/
        tvWord.setText(right + "/" + sum);
    }

    /**
     * 评测统计
     *
     * @param curVoa
     * @param
     * @param tvEval
     */
    private void initEvalData(Voa curVoa, MyRing ringEval, TextView tvEval) {
        //设置voaId
        int showVoaId = transVoaId(curVoa.lessonType, curVoa.voaId);
        //总数
        List<VoaDetail> mEvalList = new VoaDetailOp(mContext).findDataByVoaId(showVoaId);
        //已经进行的测试数
//        List<VoaSound> mSoundList = voaSoundOp.findDataByvoaId(showVoaId);
        //这里直接使用循环计算
        List<VoaSound> mSoundList = new ArrayList<>();
        for (int i = 0; i < mEvalList.size(); i++) {
            VoaDetail detail = mEvalList.get(i);
            int itemId = Integer.parseInt(showVoaId + "" + detail.paraId + "" + detail.lineN);
            VoaSound evalData = new VoaSoundOp(mContext).findDataById(itemId);
            if (evalData != null) {
                mSoundList.add(evalData);
            }
        }

        if (mSoundList.size() == 0) {
            ringEval.setCurrProgress(0, R.drawable.ic_home_eval_0);
            tvEval.setText("0/" + (mEvalList == null ? 0 : mEvalList.size()));
        } else {
            tvEval.setText(mSoundList.size() + "/" + (mEvalList == null ? 0 : mEvalList.size()));
            ringEval.setCurrProgress((mEvalList == null || mEvalList.size() == 0) ? 0 : (360 * mSoundList.size() / mEvalList.size()), R.drawable.ic_home_eval_1);
        }
    }

    private void initArticleData(Voa curVoa, MyRing ringArticle, TextView tvArticle) {

        ArticleRecordBean bean = articleRecordOp.getData(curVoa.voaId);

        if (bean == null) {
            tvArticle.setText("0%");
            ringArticle.setCurrProgress(0, R.drawable.ic_home_article_0);
        } else {
            if (bean.is_finish == 1) {
                tvArticle.setText("100%");
                ringArticle.setCurrProgress(360, R.drawable.ic_home_article_1);
            } else {
//                int paras = voaDetailOp.getLessonParas(curVoa.voaId);
//                int percent = 0;
//                if (paras != 0) {
//                    percent = bean.percent * 100 / paras;
//                }
//                int progress = Math.min(360 * percent / 100, 100);
//                tvArticle.setText(String.format(Locale.CHINA, "%d%%", percent));
//                ringArticle.setCurrProgress(progress, R.drawable.ic_home_article_1);
                int paras = bean.total_time;
                int percent = 0;
                if (paras != 0) {
                    percent = 100 * bean.curr_time / paras;
                }
                int progress = percent * 360 / 100;
                tvArticle.setText(String.format(Locale.CHINA, "%d%%", percent));
                ringArticle.setCurrProgress(progress, R.drawable.ic_home_article_1);
            }
        }
    }

    /**
     * 练习题相关的 初始化
     *
     * @param curVoa
     * @param ringTest
     * @param tvTest
     */
    private void initTestData(Voa curVoa, LinearLayout testLayout, MyRing ringTest, TextView tvTest) {
        if (curVoa.voaId / 1000 == 321) {
            //青少版
//            ringTest.setVisibility(View.GONE);
//            tvTest.setVisibility(View.GONE);
            testLayout.setVisibility(View.GONE);
            return;
        } else {
//            ringTest.setVisibility(View.VISIBLE);
//            tvTest.setVisibility(View.VISIBLE);
            testLayout.setVisibility(View.VISIBLE);
        }
        if (curVoa.voaId / 1000 == 1 && curVoa.voaId % 2 == 0 && false) {
            tvTest.setText(0 + "/" + 0);
            tvTest.setTextColor(Color.parseColor("#eae5df"));
            ringTest.setCurrProgress(0, R.drawable.ic_home_test_0);

        } else {
            choices = multipleChoiceOp.findData(curVoa.voaId);
            tvTest.setTextColor(mContext.getResources().getColor(R.color.black));
            int rightNum;
            int allNum = 0;
            if (choices != null && choices.size() > 0)
                allNum = choices.size();
            rightNum = multipleRecordOp.getRightNum(curVoa.voaId);

            int rightNumWeb = testRecordOp.getRightNum(curVoa.voaId);

            if (rightNum == 0) {
                rightNum = rightNumWeb;
            }

            if (0 == rightNum) {
                ringTest.setCurrProgress(0, R.drawable.ic_home_test_0);
                tvTest.setText(0 + "/" + allNum);
            } else {
                tvTest.setText(rightNum + "/" + allNum);
                ringTest.setCurrProgress(360 * rightNum / allNum, R.drawable.ic_home_test_1);
            }
        }
    }

    private void initMiccroLessonDataFromLocal(Voa curVoa, LinearLayout microLayout, MyRing microLessonRing, TextView tvMicro) {

        // 1.数据校验
        if ("0".equalsIgnoreCase(curVoa.titleid)
                || TextUtils.isEmpty(curVoa.titleid)) {
//            microLessonRing.setVisibility(View.GONE);
//            tvMicro.setVisibility(View.GONE);
            microLayout.setVisibility(View.GONE);
            return;
        } else {
//            microLessonRing.setVisibility(View.VISIBLE);
//            tvMicro.setVisibility(View.VISIBLE);
            microLayout.setVisibility(View.VISIBLE);
        }

        //这里根据接口判断，处理微课的显示和隐藏
        if (AbilityControlManager.getInstance().isLimitMoc()) {
            microLayout.setVisibility(View.GONE);
        } else {
            microLayout.setVisibility(View.VISIBLE);
        }

        String[] titleIdArray = curVoa.titleid.split(",");
        if (titleIdArray == null || titleIdArray.length == 0) {
            tvMicro.setText("0%");
            microLessonRing.setCurrProgress(0, R.drawable.ic_home_micro_leature_0);
            return;
        }
        //微课进度 百分制
        int progress = 0;
        if (titleIdArray.length == 1) {
            StudyProgress progress1 = IMoocDBManager.getInstance().findStudyProgress(UserInfoManager.getInstance().getUserId(),
                    Integer.parseInt(titleIdArray[0]));
            if (progress1 == null) {
                progress = 0;
            } else {
                if (progress1.endFlag == 1) {
                    progress = 100;
                } else {
                    try {
                        long progressTime = (com.iyuba.imooclib.Constant.SDF.parse(progress1.endTime).getTime()
                                -
                                com.iyuba.imooclib.Constant.SDF.parse(progress1.startTime).getTime())
                                / 1000;
                        progress = (int) (progressTime * 100 / curVoa.totalTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        progress = 0;
                    }
                }
            }
        } else {
            int studyTotalTime = 0;
            for (int i = 0; i < titleIdArray.length; i++) {
                StudyProgress progress1 = IMoocDBManager.getInstance().findStudyProgress(UserInfoManager.getInstance().getUserId(),
                        Integer.parseInt(titleIdArray[i]));
                if (progress1 == null) {
                    studyTotalTime = studyTotalTime + 0;
                } else {

                    try {
                        long progressTime = (com.iyuba.imooclib.Constant.SDF.parse(progress1.endTime).getTime()
                                -
                                com.iyuba.imooclib.Constant.SDF.parse(progress1.startTime).getTime())
                                / 1000;
                        studyTotalTime = (int) (studyTotalTime + progressTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        studyTotalTime = studyTotalTime + 0;
                    }
                }
            }
            progress = studyTotalTime * 100 / curVoa.totalTime;
        }

        //根据 progress变量 设置进度显示
        if (progress > 0) {
            tvMicro.setText(progress + "%");
            microLessonRing.setCurrProgress((int) (progress * 3.6), R.drawable.ic_home_micro_leature_1);
        } else {
            tvMicro.setText("0%");
            microLessonRing.setCurrProgress(0, R.drawable.ic_home_micro_leature_0);
        }
    }

//    private void initMiccroLessonDataFromServer(Voa curVoa, MyRing microLessonRing, TextView tvMicro) {
//
//        // 1.数据校验
//        if ("0".equalsIgnoreCase(curVoa.titleid)
//                || TextUtils.isEmpty(curVoa.titleid)) {
//            microLessonRing.setVisibility(View.INVISIBLE);
//            tvMicro.setVisibility(View.INVISIBLE);
//            return;
//        } else {
//            microLessonRing.setVisibility(View.VISIBLE);
//            tvMicro.setVisibility(View.VISIBLE);
//        }
//
//        if (ConstUtil.sMicroList.containsKey(curVoa.voaId)) {
//            int percentage = 0;
//            Voa timpVoa = ConstUtil.sMicroList.get(curVoa.voaId);
//            if (timpVoa != null) {
//                percentage = timpVoa.percentage;
//            }
//            String strPercentage = percentage + "%";
//            if (percentage > 0) {
//                tvMicro.setText(strPercentage);
//                microLessonRing.setCurrProgress((int) (percentage * 3.6), R.drawable.ic_home_micro_leature_1);
//            } else {
//                tvMicro.setText("0%");
//                microLessonRing.setCurrProgress(0, R.drawable.ic_home_micro_leature_0);
//            }
//        } else {
//            tvMicro.setText("0%");
//            microLessonRing.setCurrProgress(0, R.drawable.ic_home_micro_leature_0);
//        }
//    }

    /*public int getDownloadNum(int bookId) {
        int bookIndex = bookId / 1000;
        int downloadNum = 0;

        for (DownloadInfo info : infoList) {
            if (info.voaId / 1000 == bookIndex && info.downloadedState != 0) {
                downloadNum++;
            }
        }

        return downloadNum;
    }*/

    public DownloadInfo getDownloadInfo(int voaId) {
        if (infoList != null) {
            for (DownloadInfo tempInfo : infoList) {
                if (tempInfo.voaId == voaId) {
                    return tempInfo;
                }
            }
        }

        return null;
    }

    /*private boolean isOpenNetwork() {
        ConnectivityManager connManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connManager.getActiveNetworkInfo() != null) {
            return connManager.getActiveNetworkInfo().isAvailable();
        }

        return false;
    }

    public void downloadBritish() {
        handler.sendEmptyMessage(1);

        voa.isDownload = "1";
        //保存在其他数据库中
        ConceptDataManager.updateLocalMarkDownloadStatus(voa.voaId,voa.lessonType,voa.isDownload,voa.position);
        voaOp.insertDataToDownload(voa.voaId * 10);

        DownloadInfo downloadInfo = new DownloadInfo(voa.voaId * 10);
        if (fileDownloader.getDownloadState() == 0) {
            downloadInfo.downloadedState = 1;
        } else {
            downloadInfo.downloadedState = -2;
        }

        notifyDataSetChanged();

        downloadInfoOp.insert(downloadInfo);
        fileDownloader.updateInfoList(downloadInfo);
    }

    public void download() {
        handler.sendEmptyMessage(1);

        voa.isDownload = "1";//保存在其他数据库中
        ConceptDataManager.updateLocalMarkDownloadStatus(voa.voaId,voa.lessonType,voa.isDownload,voa.position);

        voaOp.insertDataToDownload(voa.voaId);

        DownloadInfo downloadInfo = new DownloadInfo(voa.voaId);
        if (fileDownloader.getDownloadState() == 0) {
            downloadInfo.downloadedState = 1;
        } else {
            downloadInfo.downloadedState = -2;
        }
        notifyDataSetChanged();

        downloadInfoOp.insert(downloadInfo);
        fileDownloader.updateInfoList(downloadInfo);
    }

    public void download(DownloadInfo downloadInfo) {
        voa.isDownload = "1";
        //保存在其他数据库中
        ConceptDataManager.updateLocalMarkDownloadStatus(voa.voaId,voa.lessonType,voa.isDownload,voa.position);
        voaOp.insertDataToDownload(voa.voaId);

        if (fileDownloader.getDownloadState() == 0) {
            downloadInfo.downloadedState = 1;
        } else {
            downloadInfo.downloadedState = -2;
        }
        notifyDataSetChanged();

        downloadInfoOp.insert(downloadInfo);
        fileDownloader.updateInfoList(downloadInfo);
    }

    public void downloadBritish(DownloadInfo downloadInfo) {
        voa.isDownload = "1";
        //保存在其他数据库中
        ConceptDataManager.updateLocalMarkDownloadStatus(voa.voaId,voa.lessonType,voa.isDownload,voa.position);
        voaOp.insertDataToDownload(voa.voaId * 10);

        if (fileDownloader.getDownloadState() == 0) {
            downloadInfo.downloadedState = 1;
        } else {
            downloadInfo.downloadedState = -2;
        }
        notifyDataSetChanged();

        downloadInfoOp.insert(downloadInfo);
        fileDownloader.updateInfoList(downloadInfo);
    }

    public DownloadInfo createDownloadInfo() {
        DownloadInfo downloadInfo = new DownloadInfo(voa.voaId);
        downloadInfoOp.insert(downloadInfo);
        return downloadInfo;
    }*/

    public List<Voa> getmList() {
        return mList;
    }

    public void setmList(List<Voa> mList) {
        this.mList.clear();
        this.mList.addAll(mList);
    }

    public void refreshList(List<Voa> mList) {
//        this.mList.clear();
//        this.mList.addAll(mList);

        this.mList = mList;
        notifyDataSetChanged();
    }

    //显示下载状态
    private void showDownloadStatus(int position) {
        if (!UserInfoManager.getInstance().isLogin()) {
            viewHolder.mCircleProgressBar.setBackgroundResource(R.drawable.pause_download);
            return;
        }

        //根据文件和数据库判断
        Voa tempVoa = mList.get(position);
        //获取路径
        File localFile = new File(FilePathUtil.getHomeAudioPath(tempVoa.voaId, tempVoa.lessonType));
        //获取当前数据在本地数据库存储的状态
        LocalMarkEntity_conceptDownload downloadData = ConceptDataManager.getLocalMarkDownloadSingleData(tempVoa.voaId, tempVoa.lessonType, UserInfoManager.getInstance().getUserId());
        //获取当前的状态
        int status = 0;
        if (downloadData != null && !TextUtils.isEmpty(downloadData.isDownload)) {
            status = Integer.parseInt(downloadData.isDownload);
        }

        Log.d("下载后刷新显示", "位置：" + position + "--状态：" + status);

        if (status == Integer.parseInt(TypeLibrary.FileDownloadStateType.file_downloaded)) {
            if (localFile.exists()) {
                viewHolder.mCircleProgressBar.setBackgroundResource(R.drawable.downloaded);
            } else {
                //重置数据，设置为没有
                ConceptDataManager.updateLocalMarkDownloadStatus(downloadData.voaId, downloadData.lessonType, UserInfoManager.getInstance().getUserId(), TypeLibrary.FileDownloadStateType.file_no, position);
                viewHolder.mCircleProgressBar.setBackgroundResource(R.drawable.pause_download);
            }
        } else if (status == Integer.parseInt(TypeLibrary.FileDownloadStateType.file_no)) {
            viewHolder.mCircleProgressBar.setBackgroundResource(R.drawable.pause_download);
        } else {
            viewHolder.mCircleProgressBar.setBackgroundResource(R.drawable.download);
        }

        Log.d("显示下载状态", "刷新位置--" + position + "--状态--" + status);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView deleteBox;

        //其他版本使用
        LinearLayout style1Layout;
        TextView voaN;

        //简版-新概念人工智能使用
        RelativeLayout style2Layout;
        ImageView voaPic;
        TextView voaIndex;

        TextView lesson;
        TextView title;
        TextView titleCn;

        //        RelativeLayout downloadLayout;
//        RoundProgressBar downloadedImage;
//        ImageView downloadedImage;
        RoundProgressBar mCircleProgressBar;


        LinearLayout llWord;
        TextView tvWord;
        MyRing ringWord;

        TextView tvEval;
        MyRing ringEval;

        TextView tvArticle;
        MyRing ringArticle;

        LinearLayout testLayout;
        TextView tvTest;
        MyRing ringTest;

        /**
         * 微课的进度以及点击跳转到微课
         */
        TextView tvMicroLecture;
        MyRing microLecture;
        LinearLayout mocLayout;

        public MyViewHolder(ListitemVoaHomeBinding binding) {
            super(binding.getRoot());
            deleteBox = binding.checkBoxIsDelete;

            //其他版本
            style1Layout = binding.style1Layout;
            voaN = binding.voaN;

            //新概念人工智能学外语和新概念英语微课使用
            style2Layout = binding.style2Layout;
            voaPic = binding.voaPic;
            voaIndex = binding.voaIndex;

            lesson = binding.lesson;
            title = binding.title;
            titleCn = binding.titleCn;

//            downloadLayout = binding.downloadLayout;
//            downloadedImage = binding.imageDownloaded;
            mCircleProgressBar = binding.roundBar1;

            llWord = binding.wordLayout;
            tvWord = binding.tvWord;
            ringWord = binding.ringWord;

            tvEval = binding.tvEval;
            ringEval = binding.ringEval;

            tvArticle = binding.tvArticle;
            ringArticle = binding.ringArticle;

            testLayout = binding.testLayout;
            tvTest = binding.tvTest;
            ringTest = binding.ringTest;

            tvMicroLecture = binding.tvMicroLecture;
            microLecture = binding.microLecture;
            mocLayout = binding.mocLayout;
        }
    }

    public interface MyOnclickListener {
        void onClick(int position, String pageType);
    }

    public void notifiAll() {
        notifyDataSetChanged();
    }

    //青少版获取单词的数量
    public List<VoaWord2> getChildWord(int position) {
        //切换成当前的数据获取方式(青少版使用这种方式，其他的用别的)
        int currBookForPass = ConceptBookChooseManager.getInstance().getBookId();
        String bookId = String.valueOf(currBookForPass);
        int flag = 0;
        if (currBookForPass == 281 || currBookForPass == 283 || currBookForPass == 285) {
            flag = 15;
            position = position + flag;
        } else if (currBookForPass == 287 || currBookForPass == 289) {
            flag = 24;
            position = position + flag;
        }

        return WordChildDBManager.getInstance().findDataByVoaId(bookId, String.valueOf(position));
    }

    //获取当前章节的音频网络路径
    private String getRemoteSoundPath(Voa tempVoa) {
        String soundUrl = null;
        //这里针对会员和非会员不要修改，测试也不要修改
        if (UserInfoManager.getInstance().isVip() && SettingConfig.Instance().isHighSpeed()) {
            soundUrl = "http://staticvip2." + Constant.IYUBA_CN + "newconcept/";
        } else {
            soundUrl = Constant.sound;
        }

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
                soundUrl = "http://" + Constant.staticStr + Constant.IYUBA_CN + "sounds/voa/sentence/202005/"
                        + tempVoa.voaId
                        + "/"
                        + tempVoa.voaId
                        + Constant.append;
                break;
        }

        return soundUrl;
    }

    //权限弹窗
    private PermissionMsgDialog msgDialog = null;

    private void showPermissionDialog(Voa tempVoa, int position) {
        //android15以上直接使用
        if (Build.VERSION.SDK_INT >= 35) {
            if (onDownloadProgressListener != null) {
                onDownloadProgressListener.onDownload(tempVoa, position);
            }
        } else {
            List<Pair<String, Pair<String, String>>> pairList = new ArrayList<>();
            pairList.add(new Pair<>(Manifest.permission.WRITE_EXTERNAL_STORAGE, new Pair<>("存储权限", "保存下载的文件到本地，用于弱网情况下的音频播放使用")));

            msgDialog = new PermissionMsgDialog(mContext);
            msgDialog.showDialog(null, pairList, true, new PermissionMsgDialog.OnPermissionApplyListener() {
                @Override
                public void onApplyResult(boolean isSuccess) {
                    if (isSuccess) {
                        downloadFile(tempVoa, position);
                    }
                }
            });
        }
    }

    //下载文件
    public void downloadFile(Voa tempVoa, int position) {
        //先把相应的数据设置为待下载，然后外面查询待下载和下载的数量，超出则显示不能下载
        ConceptDataManager.updateLocalMarkDownloadStatus(tempVoa.voaId, tempVoa.lessonType, UserInfoManager.getInstance().getUserId(), TypeLibrary.FileDownloadStateType.file_isDownloading, tempVoa.position);
        EventBus.getDefault().post(new FileDownloadEvent(FileDownloadEvent.home, ConceptBookChooseManager.getInstance().getBookType(), tempVoa.voaId, position));

        Log.d("显示下载状态", "下载位置--" + position + "--数据位置：" + tempVoa.position);

        //下载文件
        String fileUrl = getRemoteSoundPath(tempVoa);
        String filePath = FilePathUtil.getHomeAudioPath(tempVoa.voaId, tempVoa.lessonType);
        FileDownloadBean downloadBean = new FileDownloadBean(fileUrl, filePath, ConceptBookChooseManager.getInstance().getBookType(), tempVoa.voaId, position);
        FileDownloadManager.getInstance().downloadFile(downloadBean);
        //设置为正在下载
//        notifyDataSetChanged();
    }

    //将voaId数据转换为标准的数据
    private int transVoaId(String lessonType, int voaId) {
        switch (lessonType) {
            case TypeLibrary.BookType.conceptFourUS:
                return voaId;
            case TypeLibrary.BookType.conceptFourUK:
                return voaId * 10;
            case TypeLibrary.BookType.conceptJunior:
                return voaId;
            default:
                return voaId;
        }
    }

    /********************************新的操作*******************************/
    //下载回调接口
    private OnDownloadProgressListener onDownloadProgressListener;

    public interface OnDownloadProgressListener {
        void onDownload(Voa voa, int position);
    }

    public void setOnDownloadProgressListener(OnDownloadProgressListener onDownloadProgressListener) {
        this.onDownloadProgressListener = onDownloadProgressListener;
    }
}