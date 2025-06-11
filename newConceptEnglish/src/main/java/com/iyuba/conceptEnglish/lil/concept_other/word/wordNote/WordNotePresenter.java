package com.iyuba.conceptEnglish.lil.concept_other.word.wordNote;

import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.CommonDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Word_insert;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Word_note;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.manager.WordRemoteManager;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.mvp.BasePresenter;
import com.iyuba.core.lil.remote.util.LibRxUtil;
import com.iyuba.core.lil.user.UserInfoManager;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class WordNotePresenter extends BasePresenter<WordNoteView> {

    //获取生词本数据
    private Disposable wordNoteDis;
    //删除数据
    private Disposable deleteWordDis;
    //下载pdf
    private Disposable downloadPdfDis;

    @Override
    public void detachView() {
        super.detachView();

        LibRxUtil.unDisposable(wordNoteDis);
    }

    //获取生词本数据
    public void getWordNoteData(int showIndex,int showCount){
        checkViewAttach();
        LibRxUtil.unDisposable(wordNoteDis);

        WordRemoteManager.getWordNoteData(UserInfoManager.getInstance().getUserId(), showIndex,showCount)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Word_note>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        wordNoteDis = disposable;
                    }

                    @Override
                    public void onNext(Word_note bean) {
                        if (bean!=null && bean.tempWords!=null && showIndex<=bean.totalPage){
                            getMvpView().onWordShow(bean.tempWords,null);
                        }else {
                            getMvpView().onWordShow(new ArrayList<>(),"暂无更多生词数据");
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        getMvpView().onWordShow(null,"获取生词数据异常，请重试～");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //取消收藏生词数据
    public void deleteWordData(String word,boolean isCollect){
        LibRxUtil.unDisposable(deleteWordDis);
        CommonDataManager.insertOrDeleteWord(word,UserInfoManager.getInstance().getUserId(), isCollect)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Word_insert>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        deleteWordDis = disposable;
                    }

                    @Override
                    public void onNext(Word_insert bean) {
                        if (bean!=null&&bean.result==1){
                            getMvpView().onCollectWord(true,null);
                        }else {
                            getMvpView().onCollectWord(false,"取消收藏单词失败，请重试～");
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        getMvpView().onCollectWord(false,"取消收藏单词异常，请重试～");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
