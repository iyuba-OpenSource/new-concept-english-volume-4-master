package com.iyuba.core.talkshow.lesson;

import com.iyuba.module.mvp.MvpView;

public interface LessonPlayMvpView extends MvpView {

    void showToast(int resId);

    void showToast(String message);

    void onDeductIntegralSuccess(int type);

    void showPdfFinishDialog(String url);
}
