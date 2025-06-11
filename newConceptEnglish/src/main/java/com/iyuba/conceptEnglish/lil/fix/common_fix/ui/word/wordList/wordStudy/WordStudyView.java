package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.word.wordList.wordStudy;

import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.EvalShowBean;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.mvp.BaseView;

/**
 * @title:
 * @date: 2023/5/12 14:34
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public interface WordStudyView extends BaseView {

    //展示评测结果
    void showEvalData(EvalShowBean evalBean);
}
