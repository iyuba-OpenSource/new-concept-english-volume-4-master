package com.iyuba.conceptEnglish.lil.concept_other.exercise_new;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.iyuba.conceptEnglish.databinding.FragmentEmptyBinding;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.StrLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingFragment;

/**
 * 空界面
 */
public class EmptyFragment extends BaseViewBindingFragment<FragmentEmptyBinding> {

    public static EmptyFragment getInstance(String showText){
        EmptyFragment fragment = new EmptyFragment();
        Bundle bundle = new Bundle();
        bundle.putString(StrLibrary.data,showText);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initView(){
        String showText = getArguments().getString(StrLibrary.data);
        if (TextUtils.isEmpty(showText)){
            binding.showText.setText("暂无显示数据");
            return;
        }

        binding.showText.setText(showText);
    }
}
