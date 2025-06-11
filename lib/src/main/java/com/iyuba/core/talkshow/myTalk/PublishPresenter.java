package com.iyuba.core.talkshow.myTalk;

import android.util.Log;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.data.DataManager;
import com.iyuba.core.common.data.model.GetMyDubbingResponse;
import com.iyuba.core.common.data.model.ThumbsResponse;
import com.iyuba.lib.R;
import com.iyuba.module.mvp.BasePresenter;


import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import timber.log.Timber;

public class PublishPresenter extends BasePresenter<PublishMvpView> {
    private final DataManager mDataManager;
    private Disposable mGetReleasedSub;


    public PublishPresenter(DataManager mDataManager) {
        this.mDataManager = mDataManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        com.iyuba.module.toolbox.RxUtil.dispose(mGetReleasedSub);
    }

    public void getLessonList(int uId) {
        com.iyuba.module.toolbox.RxUtil.dispose(mGetReleasedSub);
        Single<GetMyDubbingResponse> dubbing;
        switch (CrashApplication.getContext().getPackageName()){
            case "com.iyuba.concept2":
            case "com.iyuba.englishfm":
            case "com.iyuba.nce":
                dubbing= mDataManager.getMyDubbing(uId, Constant.AppName);
                break;
            default:
                dubbing= mDataManager.getMyDubbing(uId);
                break;
        }

        mGetReleasedSub = dubbing
                .compose(com.iyuba.module.toolbox.RxUtil.<GetMyDubbingResponse>applySingleIoScheduler())
                .subscribe(new Consumer<GetMyDubbingResponse>() {
                    @Override
                    public void accept(GetMyDubbingResponse dubbingResponse) throws Exception {
                        if (isViewAttached()) {
                            if (dubbingResponse.data.size() > 0) {
                                getMvpView().setReleasedData(dubbingResponse.data);
                            }else {
                                getMvpView().setEmptyData();
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e(throwable);
                        if (isViewAttached()) {
                            getMvpView().showToast(R.string.request_fail);
                        }
                    }
                });
    }

    public void deleteList(String idList,int uId) {
        com.iyuba.module.toolbox.RxUtil.dispose(mGetReleasedSub);
        mGetReleasedSub = mDataManager.deleteReleaseRecordList(idList,uId)
                .compose(com.iyuba.module.toolbox.RxUtil.<ThumbsResponse>applySingleIoScheduler())
                .subscribe(new Consumer<ThumbsResponse>() {
                    @Override
                    public void accept(ThumbsResponse dubbingResponse) throws Exception {
                        if (isViewAttached()) {
                            getMvpView().showToast("删除成功！");
                            getMvpView().deleteSuccess();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e(throwable);
                        if (isViewAttached()) {
                            getMvpView().showToast("删除失败！");
                        }
                    }
                });
    }
}
