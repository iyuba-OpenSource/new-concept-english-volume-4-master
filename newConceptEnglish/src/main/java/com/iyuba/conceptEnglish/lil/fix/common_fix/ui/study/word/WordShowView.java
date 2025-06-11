package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.word;

import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.WordBean;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.mvp.BaseView;

import java.util.List;

/**
 * @title:
 * @date: 2023/8/15 11:49
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public interface WordShowView extends BaseView {

    //展示单词数据
    void showWord(List<WordBean> list);
}
