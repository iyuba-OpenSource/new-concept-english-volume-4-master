package com.iyuba.conceptEnglish.lil.concept_other.exercise_new;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.PopupWindow;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.databinding.LayoutContainerTabTitleBinding;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.StrLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingActivity;
import com.jn.yyz.practise.event.BookChooseEventsbus;
import com.jn.yyz.practise.fragment.HomeFragment;
import com.jn.yyz.practise.fragment.ListFragment;
import com.jn.yyz.practise.fragment.TestRankingFragment;
import com.jn.yyz.practise.fragment.WrongBookFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 新版练习题界面
 */
public class ExerciseNewShowActivity extends BaseViewBindingActivity<LayoutContainerTabTitleBinding> {

    private String showType;
    private String dataType;

    //选择的书籍数据
    private Pair<Integer,String> selectConceptPair = new Pair<>(1,"新概念第一册");

    public static void start(Context context,String showType,String dataType){
        Intent intent = new Intent();
        intent.setClass(context, ExerciseNewShowActivity.class);
        intent.putExtra(StrLibrary.showType,showType);
        intent.putExtra(StrLibrary.dataType,dataType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showType = getIntent().getStringExtra(StrLibrary.showType);
        dataType = getIntent().getStringExtra(StrLibrary.dataType);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        initToolbar();
        initFragment();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initToolbar(){
        binding.tabLayout.setVisibility(View.GONE);

        binding.toolbar.btnBack.setVisibility(View.VISIBLE);
        binding.toolbar.btnBack.setBackgroundResource(R.drawable.back_button_normal);
        binding.toolbar.btnBack.setOnClickListener(v->{
            finish();
        });

        if (TextUtils.isEmpty(showType)){
            return;
        }

        switch (showType){
            case TypeLibrary.ExerciseNewShowType.type_list:
            case TypeLibrary.ExerciseNewShowType.type_line:
                binding.toolbar.title.setText(selectConceptPair.second);

                binding.toolbar.btnRight.setVisibility(View.VISIBLE);
                binding.toolbar.btnRight.setBackgroundResource(R.drawable.textbook_category);
                binding.toolbar.btnRight.setOnClickListener(v->{
                    if (dataType.equals(TypeLibrary.ExerciseNewDataType.type_concept)){
                        showConceptBookDialog();
                    }else if (dataType.equals(TypeLibrary.ExerciseNewDataType.type_power)){

                    }
                });
                break;
            case TypeLibrary.ExerciseNewShowType.type_rank:
                binding.toolbar.title.setText("排行榜");
                break;
            case TypeLibrary.ExerciseNewShowType.type_note:
                binding.toolbar.title.setText("错题本");
                break;
        }
    }

    private void initFragment(){
        if (TextUtils.isEmpty(showType)){
            return;
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = null;

        switch (showType){
            case TypeLibrary.ExerciseNewShowType.type_list:
                fragment = ListFragment.newInstance(dataType,1);
                break;
            case TypeLibrary.ExerciseNewShowType.type_line:
                fragment = HomeFragment.newInstance(dataType,1);
                break;
            case TypeLibrary.ExerciseNewShowType.type_rank:
                fragment = TestRankingFragment.newInstance("D",1,12,false);
                break;
            case TypeLibrary.ExerciseNewShowType.type_note:
                fragment = WrongBookFragment.newInstance(dataType,false);
                break;
            default:
                fragment = EmptyFragment.getInstance("显示类型："+showType+"，数据类型："+dataType);
                break;
        }

        transaction.add(R.id.container,fragment).show(fragment).commitNowAllowingStateLoss();
    }

    //新概念的书籍弹窗
    private void showConceptBookDialog(){
        List<Pair<Integer,String>> pairList = new ArrayList<>();
        pairList.add(new Pair<>(1,"新概念第一册"));
        pairList.add(new Pair<>(2,"新概念第二册"));
        pairList.add(new Pair<>(3,"新概念第三册"));
        pairList.add(new Pair<>(4,"新概念第四册"));

        List<String> showList = new ArrayList<>();
        for (int i = 0; i < pairList.size(); i++) {
            showList.add(pairList.get(i).second);
        }

        //显示数据
        String[] showArray = new String[showList.size()];
        showList.toArray(showArray);

        new AlertDialog.Builder(this)
                .setTitle("选择书籍")
                .setItems(showArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Pair<Integer,String> curPair = pairList.get(which);
                        if (curPair.first == selectConceptPair.first){
                            return;
                        }

                        selectConceptPair = curPair;
                        EventBus.getDefault().post(new BookChooseEventsbus(dataType,selectConceptPair.first));
                        binding.toolbar.title.setText(selectConceptPair.second);
                        dialog.dismiss();
                    }
                }).create().show();
    }
}
