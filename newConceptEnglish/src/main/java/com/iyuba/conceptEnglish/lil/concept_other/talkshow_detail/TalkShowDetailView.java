package com.iyuba.conceptEnglish.lil.concept_other.talkshow_detail;

import com.iyuba.core.common.data.model.SendEvaluateResponse;
import com.iyuba.core.common.data.model.VoaText;
import com.iyuba.core.common.data.remote.WordResponse;
import com.iyuba.module.mvp.MvpView;

import java.io.File;
import java.util.List;

/**
 * @title:
 * @date: 2023/5/17 10:02
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public interface TalkShowDetailView extends MvpView {

    void showVoaTexts(List<VoaText> voaTextList);

    void showEmptyTexts();

    void dismissDubbingDialog();

    void showMergeDialog();

    void dismissMergeDialog();

    void startPreviewActivity();

    void showToast(int resId);

    void showToast(String message);

    void pause();

    //void onDraftRecordExist(Record record);

    void showWord(WordResponse bean);

    void finish();

    void getEvaluateResponse(SendEvaluateResponse response, int paraId, File flakFile, int progress, int secondaryProgress);

    void evaluateError(String message);
}
