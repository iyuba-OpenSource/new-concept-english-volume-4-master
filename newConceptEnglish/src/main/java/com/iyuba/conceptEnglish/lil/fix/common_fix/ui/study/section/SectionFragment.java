package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.section;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.ad.AdInitManager;
import com.iyuba.conceptEnglish.databinding.FragmentFixSectionBinding;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.BookChapterBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.event.RefreshDataEvent;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.event.RefreshUserInfoEvent;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.StrLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.CommonDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.Setting_ReadLanguageEntity;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.ad.show.AdShowUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.ad.show.interstitial.AdInterstitialShowManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.ad.show.interstitial.AdInterstitialViewBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.ad.upload.AdUploadManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.ad.upload.AdUploadUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.search.NewSearchActivity;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.FixUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.view.dialog.LoadingDialog;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingFragment;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.DateUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ToastUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.view.NoScrollLinearLayoutManager;
import com.iyuba.configation.ConfigManager;
import com.iyuba.core.common.activity.login.LoginUtil;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.sdk.other.NetworkUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * @title: 分段阅读界面
 * @date: 2023/7/7 09:11
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description: 阅读速度在600/min之内算是正常
 */
public class SectionFragment extends BaseViewBindingFragment<FragmentFixSectionBinding> implements SectionView{

    //数据
    private String bookType;
    private String voaId;

    private SectionPresenter presenter;
    private SectionAdapter adapter;

    //进入的时间
    private long startTime;
    //当前的单词数量
    private long wordCount = 0;
    //阅读速度
    private static final int readSpeed = 600;
    //当前的章节数据
    private BookChapterBean chapterBean;
    //弹窗
    private LoadingDialog loadingDialog;

    //单词查询弹窗
//    private SearchWordDialog searchWordDialog;

    public static SectionFragment getInstance(String types, String voaId){
        SectionFragment fragment = new SectionFragment();
        Bundle bundle = new Bundle();
        bundle.putString(StrLibrary.types, types);
        bundle.putString(StrLibrary.voaId, voaId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        presenter = new SectionPresenter();
        presenter.attachView(this);

        bookType = getArguments().getString(StrLibrary.types);
        voaId = getArguments().getString(StrLibrary.voaId);

        chapterBean = presenter.getChapterData(bookType,voaId);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initList();
        refreshData();
    }

    @Override
    public void onResume() {
        super.onResume();
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onPause() {
        super.onPause();

//        closeSearchWordDialog();
        closeLoadingDialog();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /*****************************初始化***************************/
    private void initList(){
        binding.toolbar.getRoot().setVisibility(View.GONE);

        binding.refreshLayout.setEnableRefresh(false);
        binding.refreshLayout.setEnableLoadMore(false);

        adapter = new SectionAdapter(getActivity(),new ArrayList<>());
        NoScrollLinearLayoutManager manager = new NoScrollLinearLayoutManager(getActivity(),false);
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setAdapter(adapter);
        adapter.setOnWordSearchListener(new SectionAdapter.onWordSearchListener() {
            @Override
            public void onWordSearch(String selectText,int position) {
                if (!NetworkUtil.isConnected(getActivity())){
                    ToastUtil.showToast(getActivity(),"请链接网络后重试～");
                    return;
                }

                if (!TextUtils.isEmpty(selectText)) {
                    //先处理下数据
                    selectText = filterWord(selectText);

                    if (selectText.matches("^[a-zA-Z]*")){
//                        showSearchWordDialog(selectText);
                        NewSearchActivity.start(getActivity(),selectText);
                    }else {
                        CustomToast.showToast(getActivity(), R.string.play_please_take_the_word, 1000);
                    }
                } else {
                    CustomToast.showToast(getActivity(), R.string.play_please_take_the_word, 1000);
                }
            }
        });

        //这里查询下数据库中的数据，没有默认为英文
        Setting_ReadLanguageEntity entity = CommonDataManager.searchReadLanguageSettingFromDB(bookType,chapterBean.getBookId(),voaId);
        if (entity!=null){
            adapter.switchTextType(entity.languageType);
        }else {
            adapter.switchTextType(TypeLibrary.TextShowType.EN);
        }

        binding.submit.setOnClickListener(v->{
            if (!UserInfoManager.getInstance().isLogin()){
                LoginUtil.startToLogin(getActivity());
                return;
            }

            //获取当前读取的进度
            ////这里要求和学习报告中一致，则先去掉毫秒，然后转换
            long startNewTime = startTime/1000*1000;
            long endNewTime = System.currentTimeMillis()/1000*1000;

            long progressTime = endNewTime-startNewTime;
            float showProgressTime = progressTime*1.0f/(1000*60);
            int wordReadSpeed = (int) (wordCount/showProgressTime);

            if (wordReadSpeed>readSpeed){
                showReadWarnDialog();
            }else {
                showReadSubmitDialog(wordReadSpeed,startNewTime,endNewTime);
            }
        });

        //设置文本字体大小
        int level = ConfigManager.Instance().getfontSizeLevel();
        adapter.setTextSize(16+2*level);
    }

    /*****************************刷新数据显示***********************/
    private void refreshData(){
        List<Pair<String,String>> list = presenter.getMargeSameSectionDetail(bookType,voaId);
        if (list!=null&&list.size()>0){
            adapter.refreshData(list);

            //获取单词数量
            wordCount = presenter.getWordCount(bookType,voaId);
        }else {
            ToastUtil.showToast(getActivity(),"暂无该课程内容");
        }
    }

    //展示进度禁止弹窗
    private void showReadWarnDialog(){
        new AlertDialog.Builder(getActivity())
                .setTitle("阅读报告")
                .setMessage("你认真读完这篇文章了吗？请用正常速度阅读")
                .setCancelable(false)
                .setNegativeButton("确定", null)
                .show();
    }

    //展示进度提交弹窗
    private void showReadSubmitDialog(int readSpeedInt,long newStartTime,long newEndTime){
        long readTime = newEndTime-newStartTime;
        String readTimeStr = DateUtil.transPlayFormat(DateUtil.MINUTE,readTime);

        new AlertDialog.Builder(getActivity())
                .setTitle("阅读报告")
                .setMessage("当前阅读统计：\n文章单词数："+wordCount+"\n阅读时长："+readTimeStr+"\n阅读速度："+readSpeedInt+"单词/分钟\n是否提交阅读记录？")
                .setCancelable(false)
                .setPositiveButton("提交", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        binding.submit.setVisibility(View.GONE);

                        showLoadingDialog("正在提交阅读报告~");
                        presenter.submitReadReport(FixUtil.getTopic(bookType), voaId,chapterBean.getTitleCn(),wordCount,newStartTime,newEndTime);
                    }
                }).setNegativeButton("取消",null)
                .show();
    }

    //显示弹窗
    private void showLoadingDialog(String msg){
        if (loadingDialog==null){
            loadingDialog = new LoadingDialog(getActivity());
            loadingDialog.create();
        }
        loadingDialog.setMsg(msg);
        loadingDialog.show();
    }

    //关闭弹窗
    private void closeLoadingDialog(){
        if (loadingDialog!=null&&loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
    }

    @Override
    public void showReadReportResult(boolean isSubmit) {
        closeLoadingDialog();
        if (isSubmit){
            ToastUtil.showToast(getActivity(),"提交成功");
            // TODO: 2023/9/21 展姐要求提交完成之后按钮继续显示
            binding.submit.setVisibility(View.VISIBLE);

            //请求接口，显示插屏广告
            if (!AdShowUtil.Util.isPageExist(getActivity()) || !AdInitManager.isShowAd() || UserInfoManager.getInstance().isVip()){
                return;
            }

            showInterstitialAd();
        }else {
            ToastUtil.showToast(getActivity(),"提交失败，服务器链接超时，请稍后重试");
            binding.submit.setVisibility(View.VISIBLE);
        }
    }

    //切换显示文本类型
    public void switchTextType(String textType){
        CommonDataManager.saveReadLanguageSettingToDB(bookType,chapterBean.getBookId(),voaId,textType);
        adapter.switchTextType(textType);
    }

    /*********************单词查询****************/
    //显示查询弹窗
    /*private void showSearchWordDialog(String word){
        searchWordDialog = new SearchWordDialog(getActivity(),word);
        searchWordDialog.create();
        searchWordDialog.show();

        searchWordDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                adapter.notifyDataSetChanged();
            }
        });
    }*/

    //关闭查询弹窗
    /*private void closeSearchWordDialog(){
        if (searchWordDialog!=null&&searchWordDialog.isShowing()){
            searchWordDialog.dismiss();
        }
    }*/

    //处理单词数据
    public String filterWord(String selectText){
        selectText = selectText.replace(".","");
        selectText = selectText.replace(",","");
        selectText = selectText.replace("!","");
        selectText = selectText.replace("?","");
        selectText = selectText.replace("'","");

        return selectText;
    }

    //刷新文本显示
    public void setTextSize(int textSize){
        adapter.setTextSize(textSize);
    }

    /**********************************回调***************************************/
    //通用事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RefreshDataEvent event){
        //刷新课程详情的内容
        if (event.getType().equals(TypeLibrary.RefreshDataType.study_detailRefresh)){
            refreshData();
        }
    }

    /****************************开屏广告点击**************************/
    //点击开屏广告结果
    public void showClickAdResultData(boolean isSuccess, String showMsg) {
        //直接显示信息即可
        com.iyuba.core.common.util.ToastUtil.showToast(getActivity(), showMsg);

        if (isSuccess) {
            EventBus.getDefault().post(new RefreshUserInfoEvent());
        }
    }

    /*******************************插屏广告显示****************************/
    //是否已经获取了奖励
    private boolean isGetRewardByClickAd = false;
    //界面数据
    private AdInterstitialViewBean interstitialViewBean = null;

    //显示插屏广告
    private void showInterstitialAd() {
        //请求广告
        if (interstitialViewBean == null){
            interstitialViewBean = new AdInterstitialViewBean(new AdInterstitialShowManager.OnAdInterstitialShowListener() {
                @Override
                public void onLoadFinishAd() {

                }

                @Override
                public void onAdShow(String adType) {

                }

                @Override
                public void onAdClick(String adType, boolean isJumpByUserClick, String jumpUrl) {
                    if (isJumpByUserClick){
                        //跳转界面操作
                    }

                    //点击广告操作
                    if (!isGetRewardByClickAd){
                        isGetRewardByClickAd = true;

                        String fixShowType = AdUploadUtil.Param.AdShowPosition.show_interstitial;
                        String fixAdType = AdUploadUtil.Util.transShowAdTypeToNetAdType(adType);
                        AdUploadManager.getInstance().clickAdForReward(fixShowType, fixAdType, new AdUploadManager.OnAdClickCallBackListener() {
                            @Override
                            public void showClickAdResult(boolean isSuccess, String showMsg) {
                                showClickAdResultData(isSuccess, showMsg);
                            }
                        });
                    }
                }

                @Override
                public void onAdClose(String adType) {

                }

                @Override
                public void onAdError(String adType) {

                }
            });
            AdInterstitialShowManager.getInstance().setShowData(getActivity(),interstitialViewBean);
        }
        AdInterstitialShowManager.getInstance().showInterstitialAd();
        //重置数据
//        isGetRewardByClickAd = false;
    }
}
