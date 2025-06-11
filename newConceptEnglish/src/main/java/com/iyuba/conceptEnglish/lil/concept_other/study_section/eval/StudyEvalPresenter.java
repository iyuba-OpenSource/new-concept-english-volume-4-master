package com.iyuba.conceptEnglish.lil.concept_other.study_section.eval;

import android.content.Context;

import com.iyuba.conceptEnglish.han.bean.EvaluationSentenceData;
import com.iyuba.conceptEnglish.han.bean.EvaluationSentenceDataItem;
import com.iyuba.conceptEnglish.han.bean.EvaluationSentenceResponse;
import com.iyuba.conceptEnglish.han.utils.CorrectEvalHelper;
import com.iyuba.conceptEnglish.han.utils.ExpandKt;
import com.iyuba.conceptEnglish.lil.concept_other.remote.FixDataManager;
import com.iyuba.conceptEnglish.lil.concept_other.remote.bean.Concept_eval_marge;
import com.iyuba.conceptEnglish.lil.concept_other.remote.bean.Concept_eval_publish;
import com.iyuba.conceptEnglish.lil.concept_other.remote.bean.Concept_eval_publish_marge;
import com.iyuba.conceptEnglish.lil.concept_other.remote.bean.Concept_eval_result;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.base.BaseBean_data;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.mvp.BasePresenter;
import com.iyuba.core.lil.remote.util.LibRxUtil;
import com.iyuba.core.lil.user.UserInfoManager;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class StudyEvalPresenter extends BasePresenter<StudyEvalView> {

    //提交评测
    private Disposable submitEvalDis;
    //发布评测
    private Disposable publishEvalDis;
    //合成音频
    private Disposable margeAudioDis;
    //合成音频发布
    private Disposable publishMargeAudioDis;

    @Override
    public void detachView() {
        super.detachView();

        LibRxUtil.unDisposable(submitEvalDis);
        LibRxUtil.unDisposable(publishEvalDis);
        LibRxUtil.unDisposable(margeAudioDis);
        LibRxUtil.unDisposable(publishMargeAudioDis);
    }

    //提交评测
    public void submitEval(int voaId,String paraId,String idIndex,String sentence,String evalPath){
        checkViewAttach();
        LibRxUtil.unDisposable(submitEvalDis);

        FixDataManager.submitEval(UserInfoManager.getInstance().getUserId(),voaId,paraId,idIndex,sentence,evalPath)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseBean_data<Concept_eval_result>>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        submitEvalDis = disposable;
                    }

                    @Override
                    public void onNext(BaseBean_data<Concept_eval_result> bean) {
                        if (bean!=null &&bean.getResult().equals("1")){
                            getMvpView().showEvalResult(bean.getData(),evalPath,"数据加载成功，但是没有数据显示");
                        }else {
                            getMvpView().showEvalResult(null,evalPath,"提交评测数据失败～");
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        getMvpView().showEvalResult(null,evalPath,"提交评测数据异常～");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //发布评测
    public void publishEval(int voaId,String paraId,String lineN,String score,String evalAudioUrl){
        checkViewAttach();
        LibRxUtil.unDisposable(publishEvalDis);

        FixDataManager.publishEval(UserInfoManager.getInstance().getUserId(), UserInfoManager.getInstance().getUserName(), voaId,paraId,lineN,score,evalAudioUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Concept_eval_publish>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        publishEvalDis = disposable;
                    }

                    @Override
                    public void onNext(Concept_eval_publish bean) {
                        if (bean!=null&&bean.getResultCode().equals("501")){
                            getMvpView().showPublishEvalResult(true,null,bean.getShuoshuoId());
                        }else {
                            getMvpView().showPublishEvalResult(false,"发布评测失败，请重试～",0);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        getMvpView().showPublishEvalResult(false,"发布评测异常，请重试～",0);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //合成音频
    public void margeAudio(List<String> audioList,String score){
        checkViewAttach();
        LibRxUtil.unDisposable(margeAudioDis);

        FixDataManager.margeAudio(audioList, score)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Concept_eval_marge>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        margeAudioDis = disposable;
                    }

                    @Override
                    public void onNext(Concept_eval_marge bean) {
                        if (bean!=null&&bean.getResult().equals("1")){
                            getMvpView().showMargeResult(true,null,score,bean.getURL());
                        }else {
                            getMvpView().showMargeResult(false,"合成音频错误，请重试～",null,null);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        getMvpView().showMargeResult(false,"合成音频异常，请重试～",null,null);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //发布合成音频
    public void publishMarge(int voaId,String score,String margeAudioUrl){
        checkViewAttach();
        LibRxUtil.unDisposable(publishMargeAudioDis);

        FixDataManager.publishMarge(UserInfoManager.getInstance().getUserId(), UserInfoManager.getInstance().getUserName(), voaId,score,margeAudioUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Concept_eval_publish_marge>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        publishMargeAudioDis = disposable;
                    }

                    @Override
                    public void onNext(Concept_eval_publish_marge bean) {
                        if (bean!=null&&bean.getResultCode().equals("501")){
                            getMvpView().showPublishMargeResult(true,null,bean.getShuoshuoId(),bean.getReward());
                        }else {
                            getMvpView().showPublishMargeResult(false,"发布合成音频失败，请重试～",0,null);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        getMvpView().showPublishMargeResult(false,"发布合成音频异常，请重试～",0,null);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
