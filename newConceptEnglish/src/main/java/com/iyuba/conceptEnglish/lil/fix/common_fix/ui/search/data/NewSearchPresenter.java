package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.search.data;

import com.iyuba.conceptEnglish.han.bean.EvaluationSentenceData;
import com.iyuba.conceptEnglish.han.bean.EvaluationSentenceDataItem;
import com.iyuba.conceptEnglish.han.bean.EvaluationSentenceResponse;
import com.iyuba.conceptEnglish.han.utils.CorrectEvalHelper;
import com.iyuba.conceptEnglish.han.utils.ExpandKt;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.CommonDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.ConceptDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.base.BaseBean_data;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Eval_result_concept;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Publish_eval;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Word_detail;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Word_insert;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.BigDecimalUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.mvp.BasePresenter;
import com.iyuba.conceptEnglish.sqlite.mode.EvaluateBean;
import com.iyuba.conceptEnglish.sqlite.op.VoaSoundOp;
import com.iyuba.conceptEnglish.util.ConceptApplication;
import com.iyuba.core.lil.remote.util.LibRxUtil;
import com.iyuba.core.lil.user.UserInfoManager;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @title:
 * @date: 2023/11/16 17:20
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class NewSearchPresenter extends BasePresenter<NewSearchView> {

    //单词查询
    private Disposable searchWordDis;
    //收藏/取消收藏单词
    private Disposable collectWordDis;
    //提交句子评测
    private Disposable submitEvalDis;
    //发布到排行榜
    private Disposable publishRankDis;

    @Override
    public void detachView() {
        super.detachView();

        LibRxUtil.unDisposable(searchWordDis);
        LibRxUtil.unDisposable(collectWordDis);
    }

    //查询单词
    public void searchWord(String wordKey){
        LibRxUtil.unDisposable(searchWordDis);
        CommonDataManager.searchWord(wordKey, UserInfoManager.getInstance().getUserId())
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
                            if (detail!=null&&detail.result.equals("1")){
                                getMvpView().showWord(null,detail);
                            }else {
                                getMvpView().showWord("暂无当前单词数据("+detail.result+")",null);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView()!=null){
                            getMvpView().showWord("查询单词数据异常("+e.getMessage()+")",null);
                        }
                    }

                    @Override
                    public void onComplete() {
                        LibRxUtil.unDisposable(searchWordDis);
                    }
                });
    }

    //收藏/取消收藏单词
    public void collectWord(boolean isCollect,String word){
        LibRxUtil.unDisposable(collectWordDis);
        CommonDataManager.insertOrDeleteWord(word,UserInfoManager.getInstance().getUserId(), isCollect)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Word_insert>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        collectWordDis = d;
                    }

                    @Override
                    public void onNext(Word_insert bean) {
                        if (getMvpView()!=null){
                            if (bean!=null&&bean.result==1){
                                getMvpView().showCollectResult(isCollect,true);
                            }else {
                                getMvpView().showCollectResult(isCollect,false);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView()!=null){
                            getMvpView().showCollectResult(isCollect,false);
                        }
                    }

                    @Override
                    public void onComplete() {
                        LibRxUtil.unDisposable(collectWordDis);
                    }
                });
    }

    //提交句子评测
    public void submitSentenceEval(String lessonType,int voaId,String paraId,String idIndex,String sentence,String savePath){
        LibRxUtil.unDisposable(submitEvalDis);
        ConceptDataManager.submitEval(voaId,paraId,idIndex,sentence,savePath)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<EvaluationSentenceResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        submitEvalDis = d;
                    }

                    @Override
                    public void onNext(EvaluationSentenceResponse bean) {
                        if (getMvpView()!=null){
                            if (bean!=null&&bean.getResult()==1){
                                //保存在本地
                                loadLocalData(lessonType,bean,UserInfoManager.getInstance().getUserId(), voaId,idIndex,paraId,savePath);
                                //显示完成
                                getMvpView().showSentenceEvalResult(null,true);
                            }else {
                                //显示失败
                                getMvpView().showSentenceEvalResult("评测失败("+bean.getMessage()+")",false);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView()!=null){
                            getMvpView().showSentenceEvalResult("评测异常("+e.getMessage()+")",false);
                        }
                    }

                    @Override
                    public void onComplete() {
                        LibRxUtil.unDisposable(submitEvalDis);
                    }
                });
    }

    //发布评测后的句子
    public void publishEvalSentence(int voaId,String paraId,String idIndex,String score,String evalAudioUrl){
        LibRxUtil.unDisposable(publishRankDis);
        ConceptDataManager.publishEval(voaId,paraId,idIndex,score,evalAudioUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Publish_eval>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        publishRankDis = d;
                    }

                    @Override
                    public void onNext(Publish_eval bean) {
                        if (getMvpView()!=null){
                            if (bean!=null&&bean.getMessage().toLowerCase().equals("ok")){
                                getMvpView().showSentencePublishResult(null,bean.getShuoshuoId());
                            }else {
                                getMvpView().showSentencePublishResult("发布到排行榜失败("+bean.getMessage()+")",0);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView()!=null){
                            getMvpView().showSentencePublishResult("发布到排行榜异常("+e.getMessage()+")",0);
                        }
                    }

                    @Override
                    public void onComplete() {
                        LibRxUtil.unDisposable(publishRankDis);
                    }
                });
    }

    /***************************辅助功能**************************/
    //评测保存
    private void loadLocalData(String lessonType,EvaluationSentenceResponse bean,int userId,int voaId,String idIndex,String paraId,String savePath){
//        List<EvaluationSentenceDataItem> netResult = bean.getData().getWords();
//        CorrectEvalHelper helper = new CorrectEvalHelper(ConceptApplication.getInstance());
//        List<EvaluationSentenceDataItem> localData = helper.findByContent(String.valueOf(userId),String.valueOf(voaId),groupId,"",false);
//        if (localData.isEmpty()){
//            for (int i = 0; i < netResult.size(); i++) {
//                EvaluationSentenceDataItem item = netResult.get(i);
//                item.setGroupId(Integer.parseInt(groupId));
//                item.setUserId(userId);
//                item.setVoaId(voaId);
//                item.setContent(ExpandKt.removeSymbol(item.getContent()));
//                helper.insertItem(item);
//            }
//        }else if (netResult.size()==localData.size()){
//            for (int i = 0; i < netResult.size(); i++) {
//                EvaluationSentenceDataItem netItem = netResult.get(i);
//                EvaluationSentenceDataItem localItem = localData.get(i);
//                localItem.setPron(netItem.getPron());
//                localItem.setUser_pron(netItem.getUser_pron());
//                helper.updateItem(localItem);
//            }
//        }

        double totalScore = BigDecimalUtil.trans2Double(bean.getData().getTotal_score());
        int score = (int) (totalScore * 20.0D);
        String wordScore = "";
        for (int i = 0; i < bean.getData().getWords().size(); i++) {
            EvaluationSentenceDataItem item = bean.getData().getWords().get(i);
            wordScore = wordScore + item.getScore() + ",";
        }

        int itemId = Integer.parseInt(voaId+""+paraId+""+idIndex);
        new VoaSoundOp(ConceptApplication.getInstance()).updateWordScoreFromType(lessonType,
                wordScore, score, voaId,
                savePath,
                "", itemId, bean.getData().getURL());
    }

}
