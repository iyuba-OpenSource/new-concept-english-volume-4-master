package com.iyuba.conceptEnglish.lil.concept_other.welcome;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.databinding.LayoutWelcomeNewBinding;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingActivity;
import com.iyuba.conceptEnglish.sqlite.DatabaseUtil;
import com.iyuba.configation.Constant;

/**
 * @title: 新的欢迎界面
 * @date: 2023/11/21 09:51
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class NewWelcomeActivity extends BaseViewBindingActivity<LayoutWelcomeNewBinding> {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preAppData();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //显示弹窗
    private void showDialog(){

    }

    //跳转界面

    //预置app数据
    private void preAppData(){
        Constant.APPName = getResources().getString(R.string.app_name);
        //设置数据库数据
        DatabaseUtil.getInstance().updateDatabase(this,null);
    }

    //预置书籍数据
}
