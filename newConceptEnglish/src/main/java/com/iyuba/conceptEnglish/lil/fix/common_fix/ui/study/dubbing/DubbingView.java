package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.dubbing;

import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.EvalChapterBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Publish_eval;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Word_detail;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.mvp.BaseView;

/**
 * @title:
 * @date: 2023/6/6 10:48
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public interface DubbingView extends BaseView {

    //展示单个评测数据
    void showSingleEval(EvalChapterBean bean);

    //展示查询出的单词
    void showSearchWord(Word_detail detail);

    //显示信息
    void showWordMsg(boolean isSuccess,boolean isInsert,Word_detail detail);
}
