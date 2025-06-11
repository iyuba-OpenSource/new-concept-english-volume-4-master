package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study;

import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.ChapterDetailBean;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.mvp.BaseView;

import java.util.List;

/**
 * @title:
 * @date: 2023/5/22 16:03
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public interface StudyView extends BaseView {

    //展示数据
    void showData(List<ChapterDetailBean> list,boolean isRefreshData);

    //显示加载
    void showLoading(String msg);

    //收藏文章
    void showCollectArticle(boolean isSuccess,boolean isCollect);
}
