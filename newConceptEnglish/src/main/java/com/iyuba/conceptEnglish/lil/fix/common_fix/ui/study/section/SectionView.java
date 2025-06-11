package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.section;

import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.ChapterDetailBean;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.mvp.BaseView;

import java.util.List;

/**
 * @title:
 * @date: 2023/7/7 09:11
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public interface SectionView extends BaseView {

    //展示提交结果
    void showReadReportResult(boolean isSubmit);
}
