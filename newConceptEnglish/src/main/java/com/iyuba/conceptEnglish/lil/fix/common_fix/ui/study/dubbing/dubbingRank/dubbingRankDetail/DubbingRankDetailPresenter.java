package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.dubbing.dubbingRank.dubbingRankDetail;

import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.JuniorDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Eval_rank_agree;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.mvp.BasePresenter;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.rxjava2.RxUtil;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @title:
 * @date: 2023/6/13 18:53
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class DubbingRankDetailPresenter extends BasePresenter<DubbingRankDetailView> {

    //配音排行详情点赞
    private Disposable rankDetailAgreeDis;

    @Override
    public void detachView() {
        super.detachView();

        RxUtil.unDisposable(rankDetailAgreeDis);
    }

    //配音排行详情点赞
    public void dubbingRankDetailAgree(String sentenceId){
        checkViewAttach();
        RxUtil.unDisposable(rankDetailAgreeDis);
        JuniorDataManager.agreeDubbingRankDetailData(sentenceId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Eval_rank_agree>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        rankDetailAgreeDis = d;
                    }

                    @Override
                    public void onNext(Eval_rank_agree bean) {
                        if (getMvpView()!=null){
                            if (bean!=null&&bean.getMessage().toLowerCase().equals("ok")){
                                getMvpView().showAgree(true);
                            }else {
                                getMvpView().showAgree(false);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView()!=null){
                            getMvpView().showAgree(false);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
