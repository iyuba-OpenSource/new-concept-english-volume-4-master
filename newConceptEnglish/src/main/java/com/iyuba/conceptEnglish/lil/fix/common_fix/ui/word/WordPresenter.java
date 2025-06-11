package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.word;

import android.text.TextUtils;
import android.util.Log;

import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.WordProgressBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.CommonDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.JuniorDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.WordBreakEntity;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.base.BaseBean_data;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Junior_word;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.util.RemoteTransUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.mvp.BasePresenter;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.rxjava2.RxUtil;
import com.iyuba.core.lil.user.UserInfoManager;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @title:
 * @date: 2023/5/10 18:54
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class WordPresenter extends BasePresenter<WordView> {

    //查询中小学单词
    private Disposable searchJuniorWordDis;

    @Override
    public void detachView() {
        super.detachView();

        RxUtil.unDisposable(searchJuniorWordDis);
    }

    //查询本地单词数据
    public void getLocalWordData(String types, String bookId) {
        if (types.equals(TypeLibrary.BookType.junior_primary)
                ||types.equals(TypeLibrary.BookType.junior_middle)){
            //中小学
            List<WordProgressBean> list = JuniorDataManager.searchWordByBookIdGroup(bookId);
            if (list != null && list.size() > 0) {
                margeWordProgress(types, bookId, list);
            } else {
                getMvpView().showLoadWord(true);
            }
        }
    }

    //查询联网单词数据
    public void getNetWordData(String types, String bookId) {
        switch (types){
            case TypeLibrary.BookType.junior_primary://小学
            case TypeLibrary.BookType.junior_middle://初中
                //中小学
                loadJuniorWordData(types, bookId);
                break;
        }
    }

    //合并单词进度
    private void margeWordProgress(String types, String bookId, List<WordProgressBean> list) {
        checkViewAttach();

        if (types.equals(TypeLibrary.BookType.junior_primary)
                ||types.equals(TypeLibrary.BookType.junior_middle)){
            //中小学
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setLessonName("Unit\t" + list.get(i).lessonName);
                list.get(i).setTypes(types);
            }
        }

        //和数据库中的单词对错合并
        for (int i = 0; i < list.size(); i++) {
            WordProgressBean progressBean = list.get(i);
            List<WordBreakEntity> breakList = CommonDataManager.searchWordBreakRightDataByIdFromDB(types,bookId, progressBean.id, UserInfoManager.getInstance().getUserId());
            progressBean.setRight(breakList.size());
        }

        //和数据库中的进度数据合并
        String passId = CommonDataManager.searchWordBreakPassIdDataFromDB(types, bookId, UserInfoManager.getInstance().getUserId());
        if (!TextUtils.isEmpty(passId)) {
            long curPassId = Long.parseLong(passId);
            for (int i = 0; i < list.size(); i++) {
                WordProgressBean progressBean = list.get(i);
                long proId = Long.parseLong(progressBean.getId());

                if (curPassId >= proId) {
                    progressBean.setPass(true);
                } else {
                    int preIndex = i - 1;
                    if (preIndex >= 0) {
                        long preProId = Long.parseLong(list.get(preIndex).getId());
                        if (preProId == curPassId) {
                            progressBean.setPass(true);
                        }
                    }
                }
            }
        } else {
            //如果数据库中没有数据，则默认为第一个显示
            if (list != null && list.size() > 0) {
                WordProgressBean progressBean = list.get(0);
                if (!progressBean.isPass()) {
                    progressBean.setPass(true);
                }
            }
        }
        getMvpView().showGroupWord(list);
    }

    /***************中小学*************/
    //获取中小学的单词数据
    private void loadJuniorWordData(String types,String bookId){
        checkViewAttach();
        RxUtil.unDisposable(searchJuniorWordDis);
        JuniorDataManager.getJuniorWordData(bookId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseBean_data<List<Junior_word>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        searchJuniorWordDis = d;
                    }

                    @Override
                    public void onNext(BaseBean_data<List<Junior_word>> bean) {
                        if (getMvpView() != null) {
                            if (bean != null
                                    && bean.getResult().equals("200")
                                    && bean.getData() != null) {
                                //从数据库中删除数据
                                JuniorDataManager.deleteWordByBookId(bookId);
                                //保存在数据库
                                JuniorDataManager.saveWordToDB(RemoteTransUtil.transJuniorWordToDB(bean.getData()));
                                //从数据库取出
                                List<WordProgressBean> list = JuniorDataManager.searchWordByBookIdGroup(bookId);
                                //合并单词进度
                                margeWordProgress(types, bookId, list);
                            } else {
                                getMvpView().showGroupWord(null);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView() != null) {
                            getMvpView().showGroupWord(null);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
