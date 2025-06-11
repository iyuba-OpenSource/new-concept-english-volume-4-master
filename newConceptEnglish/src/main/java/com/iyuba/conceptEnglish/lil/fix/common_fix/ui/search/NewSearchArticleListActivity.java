package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.iyuba.conceptEnglish.databinding.AtySearchNewListBinding;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.StrLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.ConceptBookChooseManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.search.data.NewSearchPresenter;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingActivity;
import com.iyuba.conceptEnglish.lil.fix.concept.bgService.ConceptBgPlaySession;
import com.iyuba.conceptEnglish.manager.VoaDataManager;
import com.iyuba.conceptEnglish.sqlite.mode.Voa;
import com.iyuba.conceptEnglish.sqlite.op.VoaDetailOp;
import com.iyuba.conceptEnglish.sqlite.op.VoaOp;
import com.iyuba.conceptEnglish.study.StudyNewActivity;
import com.iyuba.core.lil.base.StackUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @title: 新搜索-文章界面
 * @date: 2023/11/17 11:40
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class NewSearchArticleListActivity extends BaseViewBindingActivity<AtySearchNewListBinding> {

    private SearchArticleAdapter articleAdapter;

    public static void start(Context context,String wordStr){
        Intent intent = new Intent();
        intent.setClass(context,NewSearchArticleListActivity.class);
        intent.putExtra(StrLibrary.word,wordStr);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        initToolbar();
        initList();

        checkData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**************************初始化************************/
    private void initToolbar(){
        binding.title.setText("精彩文章");
        binding.back.setOnClickListener(v->{
            StackUtil.getInstance().finishCur();
        });
    }

    private void initList(){
        articleAdapter = new SearchArticleAdapter(this,new ArrayList<>());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(articleAdapter);
        articleAdapter.setOnSearchArticleItemListener(voa -> {
            //设置数据
            VoaDataManager.Instace().voaTemp = voa;
            VoaDataManager.Instace().voaDetailsTemp = new VoaDetailOp(this).findDataByVoaId(voa.voaId);

            //设置为临时数据
            ConceptBgPlaySession.getInstance().setTempData(true);

            //跳转界面
            Intent intent = new Intent();
            intent.setClass(this, StudyNewActivity.class);
            intent.putExtra("curVoaId",String.valueOf(voa.voaId));
            startActivity(intent);
        });
    }

    /****************************获取数据*********************/
    private void checkData(){
        String wordStr = getIntent().getStringExtra(StrLibrary.word);
        if (TextUtils.isEmpty(wordStr)){
            updateUi(false,"未获取相关的单词数据");
            return;
        }

        //查询相关内容
        List<Voa> articleList = new VoaOp(this).findDataByKey(wordStr);
        if (articleList==null||articleList.size()==0){
            updateUi(false,"暂无 "+wordStr+" 相关的内容");
            return;
        }

        //合并相关的内容
        for (int i = 0; i < articleList.size(); i++) {
            //判断类型
            Voa tempVoa = articleList.get(i);
            if (tempVoa.voaId > 300000){
                tempVoa.lessonType = TypeLibrary.BookType.conceptJunior;
            }else {
                //这里根据当前数据类型来判断显示哪个
                if (ConceptBookChooseManager.getInstance().getBookType().equals(TypeLibrary.BookType.conceptJunior)){
                    tempVoa.lessonType = TypeLibrary.BookType.conceptFourUS;
                }else {
                    tempVoa.lessonType = ConceptBookChooseManager.getInstance().getBookType();
                }
            }
            //临时数据，默认不显示单词操作
            tempVoa.position = -1;
            articleList.set(i,tempVoa);
        }

        updateUi(false,null);
        articleAdapter.refreshList(articleList);
    }

    /****************************其他功能*********************/
    //显示加载进度
    private void updateUi(boolean isLoading,String showMsg){
        if (isLoading){
            binding.loadingLayout.setVisibility(View.VISIBLE);
            binding.loadingProgress.setVisibility(View.VISIBLE);
            binding.loadingImg.setVisibility(View.GONE);
            binding.loadingMsg.setText("正在查询相关内容～");
            binding.loadingLayout.setOnClickListener(null);
        }else {
            if (!TextUtils.isEmpty(showMsg)){
                binding.loadingLayout.setVisibility(View.VISIBLE);
                binding.loadingImg.setVisibility(View.VISIBLE);
                binding.loadingProgress.setVisibility(View.GONE);
                binding.loadingMsg.setText(showMsg);
            }else {
                binding.loadingLayout.setVisibility(View.GONE);
            }
        }
    }
}
