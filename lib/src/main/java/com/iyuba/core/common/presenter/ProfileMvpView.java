package com.iyuba.core.common.presenter;

import com.iyuba.core.common.data.model.UserDetailInfoResponse;
import com.iyuba.module.mvp.MvpView;

public interface ProfileMvpView extends MvpView {

    void getUserInfoSuccess(UserDetailInfoResponse response);

    void checkSuccess(String province, String city);

    void showMessage(String msg);

    void uploadSuccess();
}
