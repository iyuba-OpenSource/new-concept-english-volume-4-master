package com.iyuba.core.talkshow.talkClass;

import com.iyuba.core.common.data.DataManager;
import com.iyuba.core.common.data.model.TalkClass;
import com.iyuba.module.mvp.BasePresenter;
import com.iyuba.module.toolbox.RxUtil;

import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import timber.log.Timber;


public class TalkClassPresenter  extends BasePresenter<TalkClassMvpView> {

    private final DataManager mDataManager;

    private Disposable mDisposable;

    public TalkClassPresenter() {
        mDataManager = DataManager.getInstance();
    }

    @Override
    public void detachView() {
        RxUtil.dispose(mDisposable);
        super.detachView();
    }

    public void getLessonList(String type) {
        RxUtil.dispose(mDisposable);
        mDisposable = mDataManager.getTalkClassLesson(type)
                .compose(RxUtil.<List<TalkClass>>applySingleIoScheduler())
                .subscribe(new Consumer<List<TalkClass>>() {
                    @Override
                    public void accept(List<TalkClass> list) throws Exception {
                        if (isViewAttached()) {
                            if (list.size()>0) {
                                getMvpView().getLesson(list);
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e(throwable);
                        if (isViewAttached()) {
                            getMvpView().showMessage("获取数据失败，请稍后再试!");
                        }
                    }
                });
    }

}
