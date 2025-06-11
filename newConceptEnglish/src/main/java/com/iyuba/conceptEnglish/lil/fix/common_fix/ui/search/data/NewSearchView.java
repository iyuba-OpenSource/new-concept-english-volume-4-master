package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.search.data;

import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Word_detail;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.mvp.BaseView;

/**
 * @title:
 * @date: 2023/11/16 17:21
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public interface NewSearchView extends BaseView {

    //查询单词内容
    void showWord(String failMsg,Word_detail detail);

    //收藏/取消收藏单词
    void showCollectResult(boolean isCollect,boolean isSuccess);

    //句子评测
    void showSentenceEvalResult(String errMsg,boolean isEvalSuccess);

    //句子发布
    void showSentencePublishResult(String errorMsg,int shuoshuoId);
}
