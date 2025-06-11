package com.iyuba.conceptEnglish.activity;

import com.iyuba.core.common.data.DataManager;
import com.iyuba.core.common.data.model.ClearUserResponse;
import com.iyuba.module.mvp.BasePresenter;
import com.iyuba.module.toolbox.RxUtil;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import timber.log.Timber;

public class SetPresenter extends BasePresenter<SetMvpView> {
    private final DataManager mDataManager;

    private Disposable mDisposable;

    public SetPresenter() {
        mDataManager = DataManager.getInstance();
    }

    @Override
    public void detachView() {
        RxUtil.dispose(mDisposable);
        super.detachView();
    }

    public void clearUser(String username, String password) {
        RxUtil.dispose(mDisposable);
        mDisposable = mDataManager.clearUser(username, password)
                .compose(RxUtil.<ClearUserResponse>applySingleIoScheduler())
                .subscribe(new Consumer<ClearUserResponse>() {
                    @Override
                    public void accept(ClearUserResponse list) throws Exception {
                        if (isViewAttached()) {
                            getMvpView().clearSuccess();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e(throwable);
                        if (isViewAttached()) {
                            getMvpView().showMessage("注销失败!请确认密码及网络正确。");
                        }
                    }
                });
    }

}
