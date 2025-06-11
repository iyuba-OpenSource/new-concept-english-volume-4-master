package com.iyuba.core.talkshow.myTalk;

import com.iyuba.core.common.data.model.Ranking;
import com.iyuba.core.common.data.model.TalkLesson;
import com.iyuba.module.mvp.MvpView;

import java.util.List;

public interface PublishMvpView extends MvpView {
    void showLoadingLayout();

    void dismissLoadingLayout();

    void setEmptyData();

    void setReleasedData(List<Ranking> mData);

    void startWatchDubbingActivity(TalkLesson voa, Ranking ranking);

    void showToast(int resId);

    void showToast(String msg);

    void deleteSuccess();
}
