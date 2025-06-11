package com.iyuba.conceptEnglish.lil.concept_other.study_section.section;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.event.RefreshDataEvent;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.CommonDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Report_read;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.BigDecimalUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.mvp.BasePresenter;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ResUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.rxjava2.RxUtil;
import com.iyuba.core.lil.user.UserInfoManager;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @title:
 * @date: 2023/8/17 10:11
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class StudySectionPresenter extends BasePresenter<StudySectionView> {

    //提交阅读的学习报告
    private Disposable readReportDis;

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unDisposable(readReportDis);
    }

    //提交阅读的学习报告数据
    public void submitReadReport(String types,String voaId,String lessonName,long wordCount,long startTime,long endTime){
        checkViewAttach();
        RxUtil.unDisposable(readReportDis);
        CommonDataManager.submitReportRead(types, UserInfoManager.getInstance().getUserId(),lessonName,voaId,wordCount,startTime,endTime)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Report_read>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        readReportDis = d;
                    }

                    @Override
                    public void onNext(Report_read bean) {
                        if (getMvpView()!=null){
                            if (bean!=null&&bean.result.equals("1")){
                                getMvpView().showReadReportResult(true);

                                //显示奖励信息s t
                                double price = Integer.parseInt(bean.reward)*0.01;
                                if (price>0){
                                    price = BigDecimalUtil.trans2Double(price);
                                    String showMsg = String.format(ResUtil.getInstance().getString(R.string.reward_show),price);
                                    EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.reward_refresh_dialog,showMsg));
                                }
                            }else {
                                getMvpView().showReadReportResult(false);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView()!=null){
                            getMvpView().showReadReportResult(false);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
