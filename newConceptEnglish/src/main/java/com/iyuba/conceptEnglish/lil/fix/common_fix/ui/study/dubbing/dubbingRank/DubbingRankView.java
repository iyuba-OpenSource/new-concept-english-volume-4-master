package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.dubbing.dubbingRank;

import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Dubbing_rank;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.mvp.BaseView;

import java.util.List;

/**
 * @title:
 * @date: 2023/6/13 15:15
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public interface DubbingRankView extends BaseView {

    //显示排行信息
    void showRank(List<Dubbing_rank> list);
}
