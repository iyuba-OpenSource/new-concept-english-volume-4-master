package com.iyuba.conceptEnglish.lil.concept_other.study_section.eval;

import com.iyuba.conceptEnglish.han.bean.EvaluationSentenceData;
import com.iyuba.conceptEnglish.lil.concept_other.remote.bean.Concept_eval_result;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.mvp.BaseView;

public interface StudyEvalView extends BaseView {

    //评测回调
    void showEvalResult(Concept_eval_result sentenceData, String evalPath,String showMsg);

    //发布单句回调
    void showPublishEvalResult(boolean isSuccess,String showMsg,int shuoshuoId);

    //合成音频回调
    void showMargeResult(boolean isSuccess,String showMsg,String score,String margeAudioUrl);

    //发布合成回调
    void showPublishMargeResult(boolean isSuccess,String showMsg,int shuoshuoId,String rewardPrice);
}