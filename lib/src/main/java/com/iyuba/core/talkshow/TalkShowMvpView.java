package com.iyuba.core.talkshow;

import com.iyuba.core.common.data.model.TalkLesson;
import com.iyuba.module.mvp.MvpView;

import java.util.List;

public interface TalkShowMvpView extends MvpView {
    void showMessage(String message);

    void getTalkLesson(List<TalkLesson> list);
}
