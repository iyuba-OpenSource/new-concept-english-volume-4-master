package com.iyuba.conceptEnglish.lil.fix.common_fix.view;

import android.content.Context;

import androidx.recyclerview.widget.LinearSmoothScroller;

/**
 * @title: 顶部跳转辅助类
 * @date: 2023/10/24 14:23
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class TopSmoothScroller extends LinearSmoothScroller {

    public TopSmoothScroller(Context context) {
        super(context);
    }

    @Override
    protected int getHorizontalSnapPreference() {
        return SNAP_TO_START;
    }

    @Override
    protected int getVerticalSnapPreference() {
        return SNAP_TO_START;
    }
}
