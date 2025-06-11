package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.choose;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.databinding.LayoutContainerBinding;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.StrLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingActivity;
import com.iyuba.conceptEnglish.lil.fix.junior.choose.JuniorChooseFragment;
import com.iyuba.conceptEnglish.lil.fix.novel.choose.NovelChooseFragment;

/**
 * @title: 选书界面
 * @date: 2023/5/19 11:37
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class ChooseActivity extends BaseViewBindingActivity<LayoutContainerBinding> {

    public static void start(Context context,String type){
        Intent intent = new Intent();
        intent.setClass(context, ChooseActivity.class);
        intent.putExtra(StrLibrary.types,type);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        initFragment();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initFragment(){
        String types = getIntent().getStringExtra(StrLibrary.types);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (types.equals(TypeLibrary.BookType.junior_primary)
                ||types.equals(TypeLibrary.BookType.junior_middle)){
            //中小学
            JuniorChooseFragment fragment = JuniorChooseFragment.getInstance();
            transaction.add(R.id.container,fragment).show(fragment);
        }else if (types.equals(TypeLibrary.BookType.newCamstory)
                ||types.equals(TypeLibrary.BookType.newCamstoryColor)
                ||types.equals(TypeLibrary.BookType.bookworm)){
            //小说
            NovelChooseFragment fragment = NovelChooseFragment.getInstance();
            transaction.add(R.id.container,fragment).show(fragment);
        }
        transaction.commitAllowingStateLoss();
    }
}
