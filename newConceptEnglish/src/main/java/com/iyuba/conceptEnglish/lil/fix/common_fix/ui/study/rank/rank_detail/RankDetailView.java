package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.rank.rank_detail;

import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.EvalRankDetailBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Eval_rank_detail;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.mvp.BaseView;

import java.util.List;

/**
 * @title:
 * @date: 2023/5/25 14:48
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public interface RankDetailView extends BaseView {

    //展示评测详情的数据
    void showRankEvalDetailData(List<EvalRankDetailBean> list);

    //刷新点赞数据
    void refreshAgreeData(boolean isSuccess);
}
