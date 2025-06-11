package com.iyuba.core.talkshow.lesson.rank;


import com.iyuba.core.common.data.model.Ranking;
import com.iyuba.module.mvp.MvpView;

import java.util.List;

/**
 * Created by Administrator on 2016/11/28 0028.
 */

public interface RankingMvpView extends MvpView {
    void showRankings(List<Ranking> rankingList);

    void showEmptyRankings();

    void showToast(int id);

    void showToast(String msg);

    void showLoadingLayout();

    void dismissLoadingLayout();

    void dismissRefreshingView();
}
