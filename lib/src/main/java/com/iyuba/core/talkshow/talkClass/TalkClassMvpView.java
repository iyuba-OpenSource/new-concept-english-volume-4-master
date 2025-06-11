package com.iyuba.core.talkshow.talkClass;

import com.iyuba.core.common.data.model.TalkClass;
import com.iyuba.module.mvp.MvpView;

import java.util.List;

public interface TalkClassMvpView extends MvpView {

    void showMessage(String str);

    void getLesson(List<TalkClass>list);
}
