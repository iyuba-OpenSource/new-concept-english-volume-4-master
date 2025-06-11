package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.read;

import android.text.TextUtils;

import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.BookChapterBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.ChapterDetailBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.JuniorDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.NovelDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.junior.ChapterDetailEntity_junior;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.junior.ChapterEntity_junior;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.novel.ChapterDetailEntity_novel;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.novel.ChapterEntity_novel;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.util.DBTransUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.mvp.BasePresenter;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.rxjava2.RxUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * @title:
 * @date: 2023/5/22 19:06
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class ReadPresenter extends BasePresenter<ReadView> {

    //广告
    private Disposable adDis;

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unDisposable(adDis);
    }

    //加载章节数据
    public BookChapterBean getChapterData(String types, String voaId){
        if (TextUtils.isEmpty(types)){
            return null;
        }

        switch (types){
            case TypeLibrary.BookType.junior_primary://小学
            case TypeLibrary.BookType.junior_middle://初中
                //中小学
                ChapterEntity_junior junior = JuniorDataManager.getSingleChapterFromDB(voaId);
                return DBTransUtil.transJuniorSingleChapterData(types,junior);
            case TypeLibrary.BookType.bookworm://书虫
            case TypeLibrary.BookType.newCamstory://剑桥小说馆
            case TypeLibrary.BookType.newCamstoryColor://剑桥小说馆彩绘
                ChapterEntity_novel novel = NovelDataManager.searchSingleChapterFromDB(types, voaId);
                return DBTransUtil.novelToSingleChapterData(novel);
        }
        return null;
    }

    //加载章节详情数据
    public List<ChapterDetailBean> getChapterDetail(String types, String voaId){
        List<ChapterDetailBean> detailList = new ArrayList<>();
        if (TextUtils.isEmpty(types)){
            return detailList;
        }

        switch (types){
            case TypeLibrary.BookType.junior_primary://小学
            case TypeLibrary.BookType.junior_middle://初中
                //中小学
                List<ChapterDetailEntity_junior> juniorList = JuniorDataManager.getMultiChapterDetailFromDB(voaId);
                if (juniorList!=null&&juniorList.size()>0){
                    detailList = DBTransUtil.transJuniorChapterDetailData(juniorList);
                }
            case TypeLibrary.BookType.bookworm://书虫
            case TypeLibrary.BookType.newCamstory://剑桥小说馆
            case TypeLibrary.BookType.newCamstoryColor://剑桥小说馆彩绘
                //小说
                List<ChapterDetailEntity_novel> novelList = NovelDataManager.searchMultiChapterDetailFromDB(types, voaId);
                if (novelList!=null&&novelList.size()>0){
                    detailList = DBTransUtil.novelToChapterDetailData(novelList);
                }
        }
        return detailList;
    }

    //获取广告数据
    /*public void getBannerAd(){
        checkViewAttach();
        RxUtil.unDisposable(adDis);
        AdRemoteManager.getAd(UserInfoManager.getInstance().getUserId(), 4, Constant.APP_ID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Ad_result>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        adDis = d;
                    }

                    @Override
                    public void onNext(List<Ad_result> list) {
                        if (getMvpView()!=null){
                            if (list==null||list.size()==0){
                                getMvpView().showWebSplashAD(null,null);
                                return;
                            }

                            Ad_result result = list.get(0);
                            if (!result.getResult().equals("1")){
                                getMvpView().showWebSplashAD(null,null);
                                return;
                            }

                            Ad_result.DataBean data = result.getData();
                            if (data==null){
                                getMvpView().showWebSplashAD(null,null);
                                return;
                            }

                            Log.d("广告显示", "类型--"+data.getType());

                            // TODO: 2023/9/14 展姐在[中小学英语书虫讨论组]中明确说明ads2使用共通广告模块显示
                            switch (data.getType()){
                                case AdDataUtil.AdType.AD_Youdao:
                                    getMvpView().showYoudaoSplashAD(data.getStartuppic(),data.getStartuppic_Url());
                                    break;
                                case AdDataUtil.AdType.AD_Web:
                                    getMvpView().showWebSplashAD(data.getStartuppic(),data.getStartuppic_Url());
                                    break;
                                case AdDataUtil.AdType.AD_Ads1:
                                case AdDataUtil.AdType.AD_Ads2:
                                case AdDataUtil.AdType.AD_Ads3:
                                case AdDataUtil.AdType.AD_Ads4:
                                case AdDataUtil.AdType.AD_Ads5:
                                    getMvpView().showIyubaSdkAD(data.getType(),data.getStartuppic(),data.getStartuppic_Url());
                                    break;
                                default:
                                    getMvpView().showWebSplashAD(data.getStartuppic(),data.getStartuppic_Url());
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView()!=null){
                            getMvpView().showWebSplashAD(null,null);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }*/
}
