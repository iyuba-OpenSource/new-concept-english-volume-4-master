package com.iyuba.core.discover.adapter;

import android.view.View;

/**
 * Created by liuzhenli on 2017/4/12.
 */

public interface OnRecyclerViewItemClickListener {
    void onItemClick(View view, int position);
    void onItemLongClick(View view, int position);
}
