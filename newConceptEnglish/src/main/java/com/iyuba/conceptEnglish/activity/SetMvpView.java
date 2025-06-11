package com.iyuba.conceptEnglish.activity;

import com.iyuba.module.mvp.MvpView;

public interface SetMvpView extends MvpView {

    void clearSuccess();

    void showMessage(String msg);
}
