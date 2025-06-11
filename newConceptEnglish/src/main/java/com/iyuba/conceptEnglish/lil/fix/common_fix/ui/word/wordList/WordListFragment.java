package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.word.wordList;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.databinding.FragmentWordListBinding;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.BookChapterBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.WordBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.StrLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.listener.OnSimpleClickListener;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.word.wordList.wordBreak.WordBreakActivity;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.word.wordList.wordStudy.WordStudyActivity;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingFragment;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ToastUtil;
import com.iyuba.core.lil.base.StackUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @desction: 单词列表界面
 * @date: 2023/4/14 13:49
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class WordListFragment extends BaseViewBindingFragment<FragmentWordListBinding> implements WordListView{

    private String types;
    private String bookId;
    private String tag;
    private String voaId;
    private String id;
    private boolean canExercise;

    private WordListPresenter presenter;
    private WordListAdapter listAdapter;

    public static WordListFragment getInstance(String types,String bookId,String tag,String voaId,String id,boolean canExercise){
        WordListFragment fragment = new WordListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(StrLibrary.voaid,voaId);
        bundle.putString(StrLibrary.id,id);

        bundle.putString(StrLibrary.types,types);
        bundle.putString(StrLibrary.bookId,bookId);
        bundle.putString(StrLibrary.tag,tag);

        bundle.putBoolean(StrLibrary.canExercise,canExercise);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        types = getArguments().getString(StrLibrary.types);
        bookId = getArguments().getString(StrLibrary.bookId);
        tag = getArguments().getString(StrLibrary.tag);
        voaId = getArguments().getString(StrLibrary.voaId);
        id = getArguments().getString(StrLibrary.id);
        canExercise = getArguments().getBoolean(StrLibrary.canExercise);

        presenter = new WordListPresenter();
        presenter.attachView(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initToolbar();
        initList();
        initClick();

        refreshData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        presenter.detachView();
    }

    /*****************初始化********************/
    private void initToolbar(){
        binding.toolbar.getRoot().setVisibility(View.VISIBLE);
        binding.toolbar.btnBack.setVisibility(View.VISIBLE);
        binding.toolbar.btnBack.setBackgroundResource(R.drawable.back_button);
        binding.toolbar.btnBack.setOnClickListener(v->{
            StackUtil.getInstance().finishCur();
        });

        String titleName = presenter.getIdName(types,id);
        if (TextUtils.isEmpty(titleName)){
            titleName = "单词列表";
        }else {
            titleName += "\t单词";
        }
        binding.toolbar.title.setText(titleName);
    }

    private void initList(){
        binding.refreshLayout.setEnableRefresh(false);
        binding.refreshLayout.setEnableLoadMore(false);

        listAdapter = new WordListAdapter(getActivity(),new ArrayList<>());
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setAdapter(listAdapter);
        listAdapter.setListener(new OnSimpleClickListener<Integer>() {
            @Override
            public void onClick(Integer position) {
                WordStudyActivity.start(getActivity(),types,bookId,id,position);
            }
        });
    }

    private void initClick(){
        binding.study.setBackgroundResource(R.drawable.bg_next_word_normal);
        binding.pass.setBackgroundResource(canExercise?R.drawable.bg_next_word_normal:R.drawable.bg_next_word_pressed);

        binding.study.setOnClickListener(v->{
            WordStudyActivity.start(getActivity(),types,bookId,id,0);
        });
        binding.pass.setOnClickListener(v->{
            if (!canExercise){
                ToastUtil.showToast(getActivity(),"通关前面的单元后解锁此单元的闯关内容");
                return;
            }

            WordBreakActivity.start(getActivity(),types,bookId,id);
        });
    }

    /*******************刷新数据****************/
    private void refreshData(){
        BookChapterBean bean = presenter.getChapterData(tag,voaId);
        if (bean!=null){
            binding.toolbar.title.setText(bean.getTitleEn());
        }

        //刷新数据
        List<WordBean> list = presenter.getWordData(types,bookId,id);
        listAdapter.refreshData(list);
    }
}
