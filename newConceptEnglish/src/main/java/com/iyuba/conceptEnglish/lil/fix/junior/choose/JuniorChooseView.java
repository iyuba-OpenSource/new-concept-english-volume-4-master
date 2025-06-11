package com.iyuba.conceptEnglish.lil.fix.junior.choose;

import android.util.Pair;

import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.junior.BookEntity_junior;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.mvp.BaseView;

import java.util.List;

/**
 * @title:
 * @date: 2023/5/22 09:31
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public interface JuniorChooseView extends BaseView {

    //展示类型数据
    void showTypeData(List<Pair<String,List<Pair<String,String>>>> list);

    //展示书籍数据
    void showBookData(List<BookEntity_junior> list);

    //刷新书籍数据
    void refreshBookData();

    //展示人教版审核数据
    void showPepVerifyData(boolean isFirst);
}
