package com.iyuba.conceptEnglish.lil.concept_other.study_section.section;

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
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.event.RefreshUserInfoEvent;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.ConceptBookChooseManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.CommonDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.Setting_ReadLanguageEntity;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.ad.show.AdShowUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.ad.show.interstitial.AdInterstitialShowManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.ad.show.interstitial.AdInterstitialViewBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.ad.upload.AdUploadManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.ad.upload.AdUploadUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.search.NewSearchActivity;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.section.SectionAdapter;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.FixUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.view.dialog.LoadingDialog;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingFragment;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.DateUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ToastUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.view.NoScrollLinearLayoutManager;
import com.iyuba.conceptEnglish.manager.VoaDataManager;
import com.iyuba.conceptEnglish.sqlite.mode.Voa;
import com.iyuba.conceptEnglish.sqlite.mode.VoaDetail;
import com.iyuba.conceptEnglish.sqlite.op.VoaDetailOp;
import com.iyuba.configation.ConfigManager;
import com.iyuba.core.common.activity.login.LoginUtil;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.sdk.other.NetworkUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * @title: 学习界面-阅读功能
 * @date: 2023/8/17 09:12
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class StudySectionFragment extends BaseViewBindingFragment<FragmentFixSectionBinding> implements StudySectionView{

    //数据
    private String bookType;
    private String bookId;
    private String voaId;

    private SectionAdapter adapter;
    private StudySectionPresenter presenter;

    //章节数据
    private Voa voaTemp;
    //进入的时间
    private long startTime;
    //当前的单词数量
    private long wordCount = 0;
    //阅读速度
    private static final int readSpeed = 600;
    //弹窗
    private LoadingDialog loadingDialog;
    //单词查询弹窗
//    private SearchWordDialog searchWordDialog;

    public static StudySectionFragment getInstance(){
        StudySectionFragment fragment = new StudySectionFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bookType = ConceptBookChooseManager.getInstance().getBookType();
        bookId = String.valueOf(ConceptBookChooseManager.getInstance().getBookId());
        voaTemp = VoaDataManager.Instace().voaTemp;
        voaId = String.valueOf(voaTemp.voaId);

        presenter = new StudySectionPresenter();
        presenter.attachView(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initList();
        checkData();
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

        //关闭广告
        AdInterstitialShowManager.getInstance().stopInterstitialAd();

        presenter.detachView();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser){
            startTime = System.currentTimeMillis();
        }
    }

    /*******************初始化*******************/
    private void initList(){
        binding.toolbar.getRoot().setVisibility(View.GONE);
        binding.submit.setVisibility(View.GONE);

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
                        showSearchWordDialog(selectText);
                    }else {
                        CustomToast.showToast(getActivity(), R.string.play_please_take_the_word, 1000);
                    }
                } else {
                    CustomToast.showToast(getActivity(), R.string.play_please_take_the_word, 1000);
                }
            }
        });

        //这里查询下数据库中的数据，没有默认为英文
        Setting_ReadLanguageEntity entity = CommonDataManager.searchReadLanguageSettingFromDB(bookType,bookId,voaId);
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
            long progressTime = System.currentTimeMillis() - startTime;
            float showProgressTime = progressTime*1.0f/(1000*60);
            int wordReadSpeed = (int) (wordCount/showProgressTime);

            if (wordReadSpeed>readSpeed){
                showReadWarnDialog();
            }else {
                showReadSubmitDialog(wordReadSpeed);
            }
        });

        //设置文本字体大小
        int level = ConfigManager.Instance().getfontSizeLevel();
        adapter.setTextSize(16+2*level);
    }

    /*********************刷新数据*********************/
    private void checkData(){
        VoaDetailOp detailOp = new VoaDetailOp(getActivity());
        List<VoaDetail> detailList = detailOp.findConceptSectionData(Integer.parseInt(voaId));
        if (detailList!=null&&detailList.size()>0){
            //转换成段落显示(新概念和中小学为单句显示，小说为段落显示)
            List<Pair<String,String>> pairList = new ArrayList<>();
            for (int i = 0; i < detailList.size(); i++) {
                VoaDetail detail = detailList.get(i);
                pairList.add(new Pair<>(detail.sentence,detail.sentenceCn));
            }
            adapter.refreshData(pairList);
            binding.submit.setVisibility(View.VISIBLE);

            wordCount = getWordCount(pairList);
        }
    }

    /*******************************回调数据********************/
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

    /***************************辅助功能*******************/
    //处理单词数据
    public String filterWord(String selectText){
        selectText = selectText.replace(".","");
        selectText = selectText.replace(",","");
        selectText = selectText.replace("!","");
        selectText = selectText.replace("?","");
        selectText = selectText.replace("'","");

        return selectText;
    }

    //计算单词数据
    private int getWordCount(List<Pair<String,String>> list){
        int count =0;
        if (list!=null&&list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                String sentence = list.get(i).first;
                String[] array = sentence.split(" ");
                count+=array.length;
            }
        }
        return count;
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
    private void showReadSubmitDialog(int readSpeedInt){
        long endTime = System.currentTimeMillis();
        long readTime = endTime-startTime;
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
                        presenter.submitReadReport(FixUtil.getTopic(bookType), voaId,voaTemp.titleCn,wordCount,startTime,endTime);
                    }
                }).setNegativeButton("取消",null)
                .show();
    }

    //显示查询弹窗
    private void showSearchWordDialog(String word){
        /*searchWordDialog = new SearchWordDialog(getActivity(),word);
        searchWordDialog.create();
        searchWordDialog.show();

        searchWordDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                adapter.notifyDataSetChanged();
            }
        });*/

        NewSearchActivity.start(getActivity(),word);
    }

    //关闭查询弹窗
    /*private void closeSearchWordDialog(){
        if (searchWordDialog!=null&&searchWordDialog.isShowing()){
            searchWordDialog.dismiss();
        }
    }*/

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

    //切换显示文本类型
    public void switchTextType(String textType){
        CommonDataManager.saveReadLanguageSettingToDB(bookType,bookId,voaId,textType);
        adapter.switchTextType(textType);
    }

    //刷新数据显示
    public void refreshData(){
        voaTemp = VoaDataManager.getInstance().voaTemp;
        voaId = String.valueOf(voaTemp.voaId);
        startTime = System.currentTimeMillis();

        checkData();
    }

    //设置显示的字体大小
    public void setTextSize(int textSize){
        adapter.setTextSize(textSize);
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

                        String fixShowType = AdUploadUtil.Param.AdShowPosition.show_spread;
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
