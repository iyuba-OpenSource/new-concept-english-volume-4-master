package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.dubbing.dubbingRank;

import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.JuniorDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.base.BaseBean_dubbing_rank;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Dubbing_rank;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.mvp.BasePresenter;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.rxjava2.RxUtil;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @title:
 * @date: 2023/6/13 15:15
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class DubbingRankPresenter extends BasePresenter<DubbingRankView> {

    //查询口语秀排行信息
    private Disposable dubbingRankDis;

    @Override
    public void detachView() {
        super.detachView();

        RxUtil.unDisposable(dubbingRankDis);
    }

    //查询口语秀排行信息
    public void searchDubbingRank(String types,String voaId,int index,int count){
        checkViewAttach();
        RxUtil.unDisposable(dubbingRankDis);
        JuniorDataManager.getDubbingRank(types,voaId,index,count)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseBean_dubbing_rank<List<Dubbing_rank>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        dubbingRankDis = d;
                    }

                    @Override
                    public void onNext(BaseBean_dubbing_rank<List<Dubbing_rank>> bean) {
                        if (getMvpView()!=null){
                            if (bean!=null&&bean.getData()!=null){
                                getMvpView().showRank(bean.getData());
                            }else {
                                getMvpView().showRank(null);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView()!=null){
                            getMvpView().showRank(null);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
