package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.dubbing;

import androidx.annotation.NonNull;

import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.BookChapterBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.ChapterDetailBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.CommonDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.JuniorDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.EvalEntity_chapter;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.junior.ChapterDetailEntity_junior;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.junior.ChapterEntity_junior;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.util.DBTransUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.base.BaseBean_data;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Eval_result;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Word_detail;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Word_insert;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.util.RemoteTransUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.mvp.BasePresenter;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.rxjava2.RxUtil;
import com.iyuba.core.lil.user.UserInfoManager;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @title:
 * @date: 2023/6/6 10:48
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class DubbingPresenter extends BasePresenter<DubbingView> {

    //提交单个评测
    private Disposable submitSingleEvalDis;
    //查询单个单词
    private Disposable searchWordDis;
    //插入/删除单个单词
    private Disposable insertOrDeleteWordDis;

    @Override
    public void detachView() {
        super.detachView();

        RxUtil.unDisposable(submitSingleEvalDis);
        RxUtil.unDisposable(searchWordDis);
        RxUtil.unDisposable(insertOrDeleteWordDis);
    }

    //加载章节数据
    public BookChapterBean getChapterData(String types, String voaId){
        if (types.equals(TypeLibrary.BookType.junior_primary)
                ||types.equals(TypeLibrary.BookType.junior_middle)){
            //中小学
            ChapterEntity_junior junior = JuniorDataManager.getSingleChapterFromDB(voaId);
            return DBTransUtil.transJuniorSingleChapterData(types,junior);
        }
        return null;
    }

    //获取章节详情数据
    public List<ChapterDetailBean> getChapterDetail(String types, String voaId){
        List<ChapterDetailBean> detailList = new ArrayList<>();

        if (types.equals(TypeLibrary.BookType.junior_primary)
                ||types.equals(TypeLibrary.BookType.junior_middle)){
            //中小学
            List<ChapterDetailEntity_junior> list = JuniorDataManager.getMultiChapterDetailFromDB(voaId);
            if (list!=null&&list.size()>0){
                detailList = DBTransUtil.transJuniorChapterDetailData(list);
            }
        }
        return detailList;
    }

    //判断是否可以使用
    public boolean isEvalNext(String types,String voaId,String paraId,String idIndex){
        boolean isVip = UserInfoManager.getInstance().isVip();
        boolean isThan3 = CommonDataManager.getEvalChapterSizeFromDB(types,voaId,String.valueOf(UserInfoManager.getInstance().getUserId()))>=3;
        boolean isHasEval = CommonDataManager.getEvalChapterDataFromDB(types, voaId, paraId, idIndex,String.valueOf(UserInfoManager.getInstance().getUserId()))!=null;

        if (isVip||isHasEval||!isThan3){
            return true;
        }
        return false;
    }

    //是否可以预览
    public boolean isPreview(String types,String voaId){
        boolean isThan3 = CommonDataManager.getEvalChapterSizeFromDB(types,voaId,String.valueOf(UserInfoManager.getInstance().getUserId()))>=1;
        return isThan3;
    }

    //提交单个评测
    public void submitSingleEval(String types,boolean isSentence,String voaId, String paraId, String indexId, String sentence, String filePath){
        checkViewAttach();
        RxUtil.unDisposable(submitSingleEvalDis);
        JuniorDataManager.submitLessonSingleEval(types,isSentence, voaId, paraId, indexId, sentence, filePath)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseBean_data<Eval_result>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        submitSingleEvalDis = d;
                    }

                    @Override
                    public void onNext(@NonNull BaseBean_data<Eval_result> bean) {
                        if (getMvpView()!=null){
                            if (bean.getResult().equals("1")){
                                //保存在数据库
                                CommonDataManager.saveEvalChapterDataToDB(RemoteTransUtil.transSingleEvalChapterData(types,voaId,paraId,indexId,filePath, bean.getData()));
                                //从数据库取出
                                EvalEntity_chapter chapter = CommonDataManager.getEvalChapterDataFromDB(types,voaId,paraId,indexId,String.valueOf(UserInfoManager.getInstance().getUserId()));
                                //展示数据
                                getMvpView().showSingleEval(DBTransUtil.transEvalSingleChapterData(chapter));
                            }else {
                                getMvpView().showSingleEval(null);
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        if (getMvpView()!=null){
                            getMvpView().showSingleEval(null);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //查询单个单词
    public void searchWord(String word){
        checkViewAttach();
        RxUtil.unDisposable(searchWordDis);
        CommonDataManager.searchWord(word)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Word_detail>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        searchWordDis = d;
                    }

                    @Override
                    public void onNext(Word_detail detail) {
                        if (getMvpView()!=null){
                            getMvpView().showSearchWord(detail);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView()!=null){
                            getMvpView().showSearchWord(null);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //插入或者删除单词
    public void insertOrDeleteWords(int userId, Word_detail detail,boolean isInsert) {
        checkViewAttach();
        RxUtil.unDisposable(insertOrDeleteWordDis);
        CommonDataManager.insertOrDeleteWord(detail.key,userId,isInsert)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Word_insert>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        insertOrDeleteWordDis = d;
                    }

                    @Override
                    public void onNext(Word_insert bean) {
                        if (getMvpView()!=null){
                            if (bean!=null&&bean.result==1){
                                getMvpView().showWordMsg(true,isInsert,detail);
                            }else {
                                getMvpView().showWordMsg(false,isInsert,null);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView()!=null){
                            getMvpView().showWordMsg(false,isInsert,null);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
