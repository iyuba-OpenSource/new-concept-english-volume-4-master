package com.iyuba.core.talkshow;

import com.iyuba.core.common.data.DataManager;
import com.iyuba.core.common.data.model.TalkClass;
import com.iyuba.core.common.data.model.TalkLesson;
import com.iyuba.core.talkshow.talkClass.TalkClassMvpView;
import com.iyuba.module.mvp.BasePresenter;
import com.iyuba.module.toolbox.RxUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import timber.log.Timber;

public class TalkShowPresenter extends BasePresenter<TalkShowMvpView> {

    private final DataManager mDataManager;

    private Disposable mDisposable;

    public TalkShowPresenter() {
        mDataManager = DataManager.getInstance();
    }

    @Override
    public void detachView() {
        RxUtil.dispose(mDisposable);
        super.detachView();
    }

    public void getLessonList(String classId){
        RxUtil.dispose(mDisposable);
        mDisposable = mDataManager.getTalkLesson(classId)
                .compose(RxUtil.<List<TalkLesson>>applySingleIoScheduler())
                .subscribe(new Consumer<List<TalkLesson>>() {
                    @Override
                    public void accept(List<TalkLesson> list) throws Exception {
                        if (isViewAttached()) {
                            if (list.size()>0) {
                                getMvpView().getTalkLesson(list);
                            }else {
                                getMvpView().getTalkLesson(list);
                                getMvpView().showMessage("数据为空!");
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e(throwable);
                        if (isViewAttached()) {
                            List<TalkLesson> lessons =new ArrayList<>();
                            getMvpView().getTalkLesson(lessons);
                            getMvpView().showMessage("获取数据失败，请稍后再试!");
                        }
                    }
                });
    }
}
