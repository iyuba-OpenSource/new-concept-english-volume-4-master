package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.word;

import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.WordProgressBean;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.mvp.BaseView;

import java.util.List;

/**
 * @title:
 * @date: 2023/5/10 18:54
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public interface WordView extends BaseView {

    //是否需要加载数据
    void showLoadWord(boolean isLoading);

    //显示分组的单词数据
    void showGroupWord(List<WordProgressBean> list);
}
