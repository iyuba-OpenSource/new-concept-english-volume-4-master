package com.iyuba.core.common.presenter;

import com.iyuba.core.common.data.DataManager;
import com.iyuba.core.common.data.model.CheckIPResponse;
import com.iyuba.core.common.data.model.UploadUserInfoResponse;
import com.iyuba.core.common.data.model.UserDetailInfoResponse;
import com.iyuba.module.mvp.BasePresenter;
import com.iyuba.module.toolbox.RxUtil;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import timber.log.Timber;

public class ProfilePresenter extends BasePresenter<ProfileMvpView> {
    private final DataManager mDataManager;

    private Disposable mDisposable;
    private Disposable mGetInfoDisposable;
    private Disposable mUploadDisposable;

    public ProfilePresenter() {
        mDataManager = DataManager.getInstance();
    }

    @Override
    public void detachView() {
        RxUtil.dispose(mDisposable);
        RxUtil.dispose(mGetInfoDisposable);
        RxUtil.dispose(mUploadDisposable);
        super.detachView();
    }

    public void getUserInfo() {
        RxUtil.dispose(mGetInfoDisposable);
        mGetInfoDisposable = mDataManager.getUserInfo()
                .compose(RxUtil.<UserDetailInfoResponse>applySingleIoScheduler())
                .subscribe(new Consumer<UserDetailInfoResponse>() {
                    @Override
                    public void accept(UserDetailInfoResponse response) throws Exception {
                        if (isViewAttached()) {
                            getMvpView().getUserInfoSuccess(response);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e(throwable);
                        if (isViewAttached()) {
                            getMvpView().showMessage("网络请求失败!");
                        }
                    }
                });
    }

    public void checkIP(String uid, String appid) {
        RxUtil.dispose(mDisposable);
        mDisposable = mDataManager.checkIP(uid, appid)
                .compose(RxUtil.<CheckIPResponse>applySingleIoScheduler())
                .subscribe(new Consumer<CheckIPResponse>() {
                    @Override
                    public void accept(CheckIPResponse response) throws Exception {
                        if (isViewAttached()) {
                            getMvpView().checkSuccess(response.getProvince(), response.getCity());
                            //getMvpView().checkSuccess("山西省", "大同市");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e(throwable);
                        if (isViewAttached()) {
                            getMvpView().showMessage("网络请求失败!");
                        }
                    }
                });
    }

    public void uploadUserInfo(String gender, String province, String city, String age,
                               String occupation) {
        RxUtil.dispose(mUploadDisposable);
        mUploadDisposable = mDataManager.uploadUserInfo(gender, province, age, city, occupation)
                .compose(RxUtil.<UploadUserInfoResponse>applySingleIoScheduler())
                .subscribe(new Consumer<UploadUserInfoResponse>() {
                    @Override
                    public void accept(UploadUserInfoResponse response) {
                        if (isViewAttached()) {
                            //getMvpView().checkSuccess(response.getProvince(), response.getCity());
                            getMvpView().uploadSuccess();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e(throwable);
                        if (isViewAttached()) {
                            getMvpView().showMessage("网络请求失败!");
                        }
                    }
                });
    }
}
