package com.iyuba.conceptEnglish.fragment;

import com.iyuba.core.common.data.model.VoaWord2;
import com.iyuba.module.mvp.MvpView;

import java.util.List;

public interface PassMvpView extends MvpView {

    void showMessage(String msg);

    void getChildWordList(List<VoaWord2> list,String text);

    void upDataWordList(List<VoaWord2> list);

    void startDownload();
}
