package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.word.wordList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.databinding.LayoutContainerBinding;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.StrLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingActivity;

/**
 * @title: 单词列表-容器界面
 * @date: 2023/5/11 18:16
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class WordListActivity extends BaseViewBindingActivity<LayoutContainerBinding> {

    public static void start(Context context,String types,String bookId,String tag,String voaId,String id,boolean isCanExercise){
        Intent intent = new Intent();
        intent.setClass(context,WordListActivity.class);
        intent.putExtra(StrLibrary.voaid,voaId);
        intent.putExtra(StrLibrary.id,id);

        intent.putExtra(StrLibrary.types,types);
        intent.putExtra(StrLibrary.bookId,bookId);
        intent.putExtra(StrLibrary.tag,tag);

        intent.putExtra(StrLibrary.canExercise,isCanExercise);
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
        String bookId = getIntent().getStringExtra(StrLibrary.bookId);
        String tag = getIntent().getStringExtra(StrLibrary.tag);
        String id = getIntent().getStringExtra(StrLibrary.id);
        String voaId = getIntent().getStringExtra(StrLibrary.voaid);
        boolean canExercise = getIntent().getBooleanExtra(StrLibrary.canExercise,false);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        WordListFragment listFragment = WordListFragment.getInstance(types,bookId,tag,voaId,id,canExercise);
        transaction.add(R.id.container,listFragment);
        transaction.show(listFragment);
        transaction.commitNow();
    }
}
