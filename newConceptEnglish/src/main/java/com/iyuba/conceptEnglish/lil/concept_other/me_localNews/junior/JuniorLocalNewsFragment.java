package com.iyuba.conceptEnglish.lil.concept_other.me_localNews.junior;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.NetworkUtils;
import com.iyuba.conceptEnglish.databinding.FragmentLocalNewsJuniorBinding;
import com.iyuba.conceptEnglish.lil.concept_other.me_localNews.LocalNewsActivity;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.event.RefreshDataEvent;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.listener.OnSimpleClickListener;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.CommonDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.JuniorDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.ChapterCollectEntity;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.base.BaseBean_data;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Collect_chapter;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Junior_chapter_collect;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.util.RemoteTransUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.StudyActivity;
import com.iyuba.conceptEnglish.lil.fix.common_fix.view.dialog.LoadingDialog;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingFragment;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ToastUtil;
import com.iyuba.core.lil.user.UserInfoManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @title: 中小学本地内容列表
 * @date: 2023/6/20 16:33
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class JuniorLocalNewsFragment extends BaseViewBindingFragment<FragmentLocalNewsJuniorBinding> {

    private JuniorLocalNewsAdapter listAdapter;

    //是否需要删除
    private boolean isDeleteStart = false;
    //加载弹窗
    private LoadingDialog loadingDialog;
    //是否加载完成
    private int loadCollectDataFinish = 0;//如果

    public static JuniorLocalNewsFragment getInstance(){
        JuniorLocalNewsFragment fragment = new JuniorLocalNewsFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initList();
        refreshData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    /**********************初始化**********************/
    private void initList(){
        listAdapter = new JuniorLocalNewsAdapter(getActivity(),new ArrayList<>());
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setAdapter(listAdapter);
        listAdapter.setListener(new OnSimpleClickListener<ChapterCollectEntity>() {
            @Override
            public void onClick(ChapterCollectEntity bean) {
                StudyActivity.start(getActivity(), bean.types, bean.bookId, bean.voaId,0);
            }
        });
        listAdapter.setLongListener(new OnSimpleClickListener<ChapterCollectEntity>() {
            @Override
            public void onClick(ChapterCollectEntity entity) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("取消收藏")
                        .setMessage("是否取消收藏当前文章?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                collectArticle(entity.types, entity.voaId, false);
                            }
                        }).setNegativeButton("取消",null)
                        .setCancelable(false)
                        .show();
            }
        });
    }

    /**********************数据*********************/
    //刷新数据显示
    private void refreshData(){
        String[] types = new String[]{TypeLibrary.BookType.junior_primary,TypeLibrary.BookType.junior_middle};
        List<ChapterCollectEntity> collectEntities = CommonDataManager.getChapterCollectMultiData(types,String.valueOf(UserInfoManager.getInstance().getUserId()));
        listAdapter.refreshData(collectEntities);
    }

    /***********************辅助功能*****************/
    //设置编辑样式
    public void setEdit(boolean isDelete){
        if (!isDelete){
            listAdapter.refreshEditStatus(false);
            return;
        }

        if (isDeleteStart){
            //删除选中的数据，没选中则不删除
            List<ChapterCollectEntity> editList = listAdapter.getEditList();
            if (editList!=null&&editList.size()>0){
                for (int i = 0; i < editList.size(); i++) {
                    ChapterCollectEntity chapterBean = editList.get(i);
                    CommonDataManager.deleteChapterCollectDataToDB(chapterBean.types,chapterBean.voaId);
                }
                refreshData();
            }
        }
        isDeleteStart = !isDeleteStart;
        listAdapter.refreshEditStatus(isDeleteStart);
        ((LocalNewsActivity)getActivity()).setEditBtn(isDeleteStart);
    }

    //操作同步按钮
    public void setSync(){
        if (!NetworkUtils.isConnected()){
            ToastUtil.showToast(getActivity(),"请链接网络后重试～");
            return;
        }

        syncCollectData();
    }

    //弹窗显示加载
    private void startLoading(String msg){
        if (loadingDialog==null){
            loadingDialog = new LoadingDialog(getActivity());
            loadingDialog.create();
        }
        loadingDialog.setMsg(msg);
        loadingDialog.show();
    }

    //弹窗关闭加载
    private void closeLoading(){
        if (loadingDialog!=null&&loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
    }

    /*****************************接口*************************/
    //收藏/取消收藏
    private void collectArticle(String types,String voaId,boolean isCollect){
        if (!NetworkUtils.isConnected()){
            ToastUtil.showToast(getActivity(),"请链接网络后重试～");
            return;
        }

        JuniorDataManager.collectArticle(types,String.valueOf(UserInfoManager.getInstance().getUserId()),voaId,isCollect)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Collect_chapter>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Collect_chapter bean) {
                        if (bean!=null&&bean.msg.equals("Success")){
                            //删除或者保存本地数据，这里是删除
                            if (!isCollect){
                                CommonDataManager.deleteChapterCollectDataToDB(types,voaId);
                                ToastUtil.showToast(getActivity(),"删除数据完成～");
                            }
                            closeLoading();
                            //刷新数据
                            refreshData();
                        }else {
                            closeLoading();
                            ToastUtil.showToast(getActivity(),"操作失败，请重试～");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        closeLoading();
                        ToastUtil.showToast(getActivity(),"操作失败，请重试～");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //同步收藏数据
    private String syncTypes = null;

    private void syncCollectData(){
        if (loadCollectDataFinish==2){
            //完成，显示数据
            loadCollectDataFinish = 0;
            refreshData();
            closeLoading();
            return;
        }

        if (loadCollectDataFinish==0){
            syncTypes = TypeLibrary.BookType.junior_primary;
        }else if (loadCollectDataFinish==1){
            syncTypes = TypeLibrary.BookType.junior_middle;
        }

        startLoading("正在同步收藏数据");
        JuniorDataManager.getArticleCollect(syncTypes,UserInfoManager.getInstance().getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseBean_data<List<Junior_chapter_collect>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseBean_data<List<Junior_chapter_collect>> bean) {
                        if (bean!=null&&bean.getResult().equals("1")){
                            //这里做一个操作，将数据变成章节数据保存在本地
                            JuniorDataManager.saveChapterToDB(RemoteTransUtil.transJuniorCollectChapterData(syncTypes,bean.getData()));
                            //保存在本地
                            CommonDataManager.saveChapterMultiCollectDataToDB(RemoteTransUtil.transJuniorChapterCollectData(syncTypes,UserInfoManager.getInstance().getUserId(),bean.getData()));
                            //下一个
                            loadCollectDataFinish++;
                            syncCollectData();
                        }else {
                            closeLoading();
                            ToastUtil.showToast(getActivity(),"加载文章收藏数据失败～");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        closeLoading();
                        ToastUtil.showToast(getActivity(),"加载文章收藏数据失败～");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /*********************************回调**************************************/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RefreshDataEvent event){
        if (event.getType().equals(TypeLibrary.RefreshDataType.junior_lesson_collect)){
            refreshData();
        }
    }
}
