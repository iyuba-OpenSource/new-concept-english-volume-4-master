package com.iyuba.core.me.activity.vip;

import android.os.Bundle;
import android.view.LayoutInflater;

import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.lib.databinding.ActivityVipFixBinding;

/**
 * 会员界面-新
 */
public class NewVipCenterActivityFix extends BasisActivity {

    //布局样式
    private ActivityVipFixBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVipFixBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
