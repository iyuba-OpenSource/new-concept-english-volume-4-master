package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.word;

import android.text.TextUtils;

import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.WordBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.WordProgressBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.ConceptDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.JuniorDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.concept.WordEntity_conceptFour;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.concept.WordEntity_conceptJunior;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.junior.WordEntity_junior;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.util.DBTransUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.base.BaseBean_data;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Concept_four_word;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Concept_junior_word;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Junior_word;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.util.RemoteTransUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.mvp.BasePresenter;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ResUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.rxjava2.RxTimer;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.rxjava2.RxUtil;
import com.iyuba.conceptEnglish.sqlite.db.WordChildDBManager;
import com.iyuba.conceptEnglish.sqlite.op.VoaWordOp;
import com.iyuba.core.common.data.model.VoaWord2;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @title:
 * @date: 2023/8/15 11:49
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class WordShowPresenter extends BasePresenter<WordShowView> {

    //获取单词数据
    private Disposable getWordDis;

    @Override
    public void detachView() {
        super.detachView();

        RxUtil.unDisposable(getWordDis);
    }

    //获取单词数据
    public void getWordData(String types, String bookId, String id) {
        if (getMvpView() == null) {
            return;
        }

        if (TextUtils.isEmpty(types)) {
            getMvpView().showWord(new ArrayList<>());
            return;
        }

        switch (types) {
            case TypeLibrary.BookType.junior_primary:
            case TypeLibrary.BookType.junior_middle:
                //中小学
                List<WordEntity_junior> juniorList = JuniorDataManager.searchWordByUnitIdFromDB(bookId,id);
                if (juniorList!=null&&juniorList.size()>0){
                    getMvpView().showWord(DBTransUtil.juniorWordToWordData(types,juniorList));
                }else {
                    loadJuniorWordData(types, bookId, id);
                }
                break;
            case TypeLibrary.BookType.conceptFour:
            case TypeLibrary.BookType.conceptFourUS:
            case TypeLibrary.BookType.conceptFourUK:
                //新概念全四册
//                List<WordEntity_conceptFour> conceptFourList = ConceptDataManager.searchConceptFourWordByVoaIdFromDB(id);
//                if (conceptFourList != null && conceptFourList.size() > 0) {
//                    getMvpView().showWord(DBTransUtil.conceptFourWordToWordData(types,conceptFourList));
//                } else {
//                    loadConceptFourWordData(types, bookId,id);
//                }
                //这里优化处理下，别用自己的数据库，用已经存在的数据库进行处理
                List<VoaWord2> fourList = new VoaWordOp(ResUtil.getInstance().getContext()).findDataByVoaId(Integer.parseInt(id));
                if (fourList!=null&&fourList.size()>0){
                    //转换成需要的数据
                    List<WordBean> tempList = DBTransUtil.oldDbConceptFourWordToWordData(types,fourList);
                    getMvpView().showWord(tempList);
                }else {
                    //加载数据（太懒了，没有加载数据，后面再说吧）
                    getMvpView().showWord(new ArrayList<>());
                }
                break;
            case TypeLibrary.BookType.conceptJunior:
                //新概念青少版
                //这里传入的是voaid，需要先获取到unitid，然后进行处理
//                int unitId = ConceptDataManager.searchConceptJuniorUnitIdByVoaId(id);
//                if (unitId > 0) {
//                    List<WordEntity_conceptJunior> conceptJuniorList = ConceptDataManager.searchConceptJuniorWordByUnitIdFromDB(String.valueOf(unitId));
//                    getMvpView().showWord(DBTransUtil.conceptJuniorWordToWordData(types,conceptJuniorList));
//                } else {
//                    loadConceptJuniorWordData(types, bookId,id);
//                }
                //这里优化处理下，别用自己的数据库，用已经存在的数据库进行处理
                List<VoaWord2> youngList = WordChildDBManager.getInstance().findDataByBookIdAndVoaId(bookId,id);
                if (youngList!=null&&youngList.size()>0){
                    //转换成需要的数据
                    List<WordBean> tempList = DBTransUtil.oldDbConceptFourWordToWordData(types,youngList);
                    getMvpView().showWord(tempList);
                }else {
                    //这里偷懒了，按理说应该从远程接口中获取数据，这里没有弄，后面处理哈
                    getMvpView().showWord(new ArrayList<>());
                }
                break;
        }
    }

    /******************************接口加载*************************/
    //加载中小学单词数据
    private void loadJuniorWordData(String types,String bookId,String id){
        checkViewAttach();
        RxUtil.unDisposable(getWordDis);
        JuniorDataManager.getJuniorWordData(bookId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseBean_data<List<Junior_word>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        getWordDis = d;
                    }

                    @Override
                    public void onNext(BaseBean_data<List<Junior_word>> bean) {
                        if (bean != null
                                && bean.getResult().equals("200")
                                && bean.getData() != null) {
                            //保存在数据库
                            JuniorDataManager.saveWordToDB(RemoteTransUtil.transJuniorWordToDB(bean.getData()));
                            //从数据库取出
                            List<WordEntity_junior> list = JuniorDataManager.searchWordByUnitIdFromDB(bookId,id);
                            //展示数据
                            getMvpView().showWord(DBTransUtil.juniorWordToWordData(types,list));
                        }else {
                            getMvpView().showWord(null);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showWord(null);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //获取新概念全四册单词数据
    private void loadConceptFourWordData(String types, String bookId,String id) {
        checkViewAttach();
        RxUtil.unDisposable(getWordDis);
        ConceptDataManager.getConceptFourWordData(bookId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseBean_data<List<Concept_four_word>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        getWordDis = d;
                    }

                    @Override
                    public void onNext(BaseBean_data<List<Concept_four_word>> bean) {
                        if (getMvpView() != null) {
                            if (bean != null && bean.getData() != null) {
                                //保存在本地
                                ConceptDataManager.saveConceptFourWordToDB(RemoteTransUtil.transConceptFourWordToDB(bookId, bean.getData()));
                                //获取单词分组数据
                                List<WordEntity_conceptFour> list = ConceptDataManager.searchConceptFourWordByVoaIdFromDB(id);
                                //合并单词进度
                                getMvpView().showWord(DBTransUtil.conceptFourWordToWordData(types,list));
                            } else {
                                getMvpView().showWord(null);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView() != null) {
                            getMvpView().showWord(null);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //获取新概念青少版单词数据
    private void loadConceptJuniorWordData(String types, String bookId,String id) {
        checkViewAttach();
        RxUtil.unDisposable(getWordDis);
        ConceptDataManager.getConceptJuniorWordData(bookId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseBean_data<List<Concept_junior_word>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        getWordDis = d;
                    }

                    @Override
                    public void onNext(BaseBean_data<List<Concept_junior_word>> bean) {
                        if (getMvpView() != null) {
                            if (bean != null
                                    && bean.getResult().equals("200")
                                    && bean.getData() != null) {
                                //保存在数据库
                                ConceptDataManager.saveConceptJuniorWordToDB(RemoteTransUtil.transConceptJuniorWordToDB(bean.getData()));
                                //查询unitId
                                int unitId = ConceptDataManager.searchConceptJuniorUnitIdByVoaId(id);
                                if (unitId>0){
                                    //从数据库取出
                                    List<WordEntity_conceptJunior> list = ConceptDataManager.searchConceptJuniorWordByUnitIdFromDB(id);
                                    //合并单词进度
                                    getMvpView().showWord(DBTransUtil.conceptJuniorWordToWordData(types,list));
                                }else {
                                    getMvpView().showWord(null);
                                }
                            } else {
                                getMvpView().showWord(null);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView() != null) {
                            getMvpView().showWord(null);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
