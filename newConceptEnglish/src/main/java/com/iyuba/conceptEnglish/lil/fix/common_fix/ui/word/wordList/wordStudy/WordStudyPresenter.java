package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.word.wordList.wordStudy;

import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.EvalShowBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.WordBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.CommonDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.JuniorDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.EvalEntity_word;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.junior.WordEntity_junior;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.util.DBTransUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.base.BaseBean_data;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Junior_eval;
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
 * @date: 2023/5/12 14:34
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class WordStudyPresenter extends BasePresenter<WordStudyView> {

    //提交新概念评测
    private Disposable evalConceptDis;
    //提交中小学评测
    private Disposable evalJuniorDis;

    @Override
    public void detachView() {
        super.detachView();

        RxUtil.unDisposable(evalConceptDis);
        RxUtil.unDisposable(evalJuniorDis);
    }

    //获取当前章节的数据
    /*public BookChapterBean getChapterData(String types, String voaId){
        if (types.equals(TypeLibrary.BookType.bookworm)
                ||types.equals(TypeLibrary.BookType.newCamstory)
                ||types.equals(TypeLibrary.BookType.newCamstoryColor)){
            //小说
            ChapterEntity_novel novel = DataManager.getInstance().searchSingleNovelChapterFromDB(types, voaId);
            return DBTransUtil.novelToSingleChapterData(novel);
        }else if (types.equals(TypeLibrary.BookType.conceptFourUS)
                ||types.equals(TypeLibrary.BookType.conceptFourUK)
                ||types.equals(TypeLibrary.BookType.conceptJunior)){

            if (types.equals(TypeLibrary.BookType.conceptFourUS)
                    ||types.equals(TypeLibrary.BookType.conceptFourUK)){
                //全四册
                ChapterEntity_conceptFour conceptFour = DataManager.getInstance().searchSingleConceptFourChapterFromDB(types, voaId);
                return DBTransUtil.conceptFourToSingleChapterData(conceptFour);
            }else if (types.equals(TypeLibrary.BookType.conceptJunior)){
                //青少版
                ChapterEntity_conceptJunior conceptJunior = DataManager.getInstance().searchSingleConceptJuniorChapterFromDB(voaId);
                return DBTransUtil.conceptJuniorToSingleChapterData(conceptJunior);
            }
        }
        return null;
    }*/

    //获取单词数据
    public List<WordBean> getWordData(String types, String bookId,String id){
        List<WordBean> list = new ArrayList<>();

        /*if (types.equals(TypeLibrary.BookType.conceptFour)
                ||types.equals(TypeLibrary.BookType.conceptJunior)){

            if (types.equals(TypeLibrary.BookType.conceptFour)){
                //全四册
                List<WordEntity_conceptFour> fourList = DataManager.getInstance().searchConceptFourWordByVoaIdFromDB(id);
                if (fourList!=null&&fourList.size()>0){
                    return DBTransUtil.conceptFourWordToWordData(types,fourList);
                }
            }else if (types.equals(TypeLibrary.BookType.conceptJunior)){
                //青少版
                List<WordEntity_conceptJunior> juniorList = DataManager.getInstance().searchConceptJuniorWordByUnitIdFromDB(id);
                if (juniorList!=null&&juniorList.size()>0){
                    return DBTransUtil.conceptJuniorWordToWordData(types,juniorList);
                }
            }
        }else*/
            if (types.equals(TypeLibrary.BookType.junior_primary)
                ||types.equals(TypeLibrary.BookType.junior_middle)){
            //中小学
            List<WordEntity_junior> list1 = JuniorDataManager.searchWordByUnitIdFromDB(bookId,id);
            if (list1!=null&&list1.size()>0){
                return DBTransUtil.juniorWordToWordData(types,list1);
            }
        }

        return list;
    }

    //获取评测数据
    public EvalShowBean getEvalData(String types, String bookId, String voaId, String position, String sentence){
        switch (types){
            case TypeLibrary.BookType.conceptFour:
            case TypeLibrary.BookType.conceptJunior:
            case TypeLibrary.BookType.junior_primary:
            case TypeLibrary.BookType.junior_middle:
                EvalEntity_word word = CommonDataManager.searchEvalWordFromDB(types, bookId, voaId, position, sentence);
                return DBTransUtil.wordEvalToShowData(word);
        }

        return null;
    }

    //评测单词和句子
    public void evalWordOrSentence(boolean isSentence,String types,String bookId,String voaId,String id,String position,String sentence,String filePath){
        /*if (types.equals(TypeLibrary.BookType.conceptFour)
                ||types.equals(TypeLibrary.BookType.conceptJunior)){
            //新概念
            evalConceptWord(types, bookId, voaId,id, position, sentence, filePath);
        }else*/
            if (types.equals(TypeLibrary.BookType.junior_primary)
                ||types.equals(TypeLibrary.BookType.junior_middle)){
            //中小学
            evalJuniorWord(types,isSentence,sentence,position,bookId,filePath,voaId,id);
        }else {

        }
    }

    /*************新概念***************/
    /*private void evalConceptWord(String types,String bookId,String voaId,String id,String position,String sentence,String filePath){
        checkViewAttach();
        RxUtil.unDisposable(evalConceptDis);
        DataManager.getInstance().evalConceptWordData(sentence, UserInfoManager.getInstance().getUserId(), filePath)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseBean_data<Concept_eval>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        evalConceptDis = d;
                    }

                    @Override
                    public void onNext(BaseBean_data<Concept_eval> bean) {
                        if (getMvpView()!=null){
                            if (bean!=null&&bean.getResult().equals("1")){
                                //保存在数据库
                                DataManager.getInstance().saveEvalWordToDB(RemoteTransUtil.transConceptEvalWordToDB(types,bookId,voaId,id,position,filePath, bean.getData()));
                                //从数据库取出
                                EvalEntity_word entity = DataManager.getInstance().searchEvalWordFromDB(types, bookId, voaId, position, sentence);
                                //展示数据
                                getMvpView().showEvalData(DBTransUtil.wordEvalToShowData(entity));
                            }else {
                                getMvpView().showEvalData(null);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView()!=null){
                            getMvpView().showEvalData(null);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }*/

    /**************中小学****************/
    private void evalJuniorWord(String types,boolean isSentence,String sentence,String wordPosition,String bookId,String filepath,String voaId,String id){
        checkViewAttach();
        RxUtil.unDisposable(evalJuniorDis);
        JuniorDataManager.submitWordEval(types,isSentence,sentence,wordPosition,bookId, UserInfoManager.getInstance().getUserId(), filepath)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseBean_data<Junior_eval>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        evalJuniorDis = d;
                    }

                    @Override
                    public void onNext(BaseBean_data<Junior_eval> bean) {
                        if (getMvpView()!=null){
                            if (bean!=null&&bean.getResult().equals("1")){
                                //保存在数据库
                                CommonDataManager.saveEvalWordToDB(RemoteTransUtil.transJuniorEvalWordToDB(types,bookId,voaId,id,wordPosition,filepath, bean.getData()));
                                //从数据库取出
                                EvalEntity_word entity = CommonDataManager.searchEvalWordFromDB(types,bookId,voaId,wordPosition,sentence);
                                //展示数据
                                getMvpView().showEvalData(DBTransUtil.wordEvalToShowData(entity));
                            }else {
                                getMvpView().showEvalData(null);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView()!=null){
                            getMvpView().showEvalData(null);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
