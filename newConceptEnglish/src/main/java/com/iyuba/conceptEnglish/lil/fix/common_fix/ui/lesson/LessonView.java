package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.lesson;

import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.BookChapterBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Junior_type;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.mvp.BaseView;

import java.util.List;

/**
 * @title:
 * @date: 2023/5/19 13:55
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public interface LessonView extends BaseView {

    //展示列表数据
    void showData(List<BookChapterBean> list);

    //联网加载数据
    void loadNetData();
}
