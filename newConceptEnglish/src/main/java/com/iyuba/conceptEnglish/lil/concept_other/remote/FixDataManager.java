package com.iyuba.conceptEnglish.lil.concept_other.remote;

import com.iyuba.conceptEnglish.api.UpdateTestAPI;
import com.iyuba.conceptEnglish.lil.concept_other.remote.bean.Concept_eval_marge;
import com.iyuba.conceptEnglish.lil.concept_other.remote.bean.Concept_eval_publish;
import com.iyuba.conceptEnglish.lil.concept_other.remote.bean.Concept_eval_publish_marge;
import com.iyuba.conceptEnglish.lil.concept_other.remote.bean.Concept_eval_result;
import com.iyuba.conceptEnglish.lil.concept_other.remote.service.EvalService;
import com.iyuba.conceptEnglish.lil.concept_other.remote.service.ExerciseService;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.StrLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.RemoteManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.base.BaseBean_data;
import com.iyuba.configation.Constant;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * @title:
 * @date: 2023/11/8 18:22
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class FixDataManager {

    /*************************************习题数据*****************************/
    //同步习题数据
    public static Observable<UpdateTestAPI.UpdateTestBean> syncExercise(String voaIds){
        ExerciseService exerciseService = RemoteManager.getInstance().createJson(ExerciseService.class);
        return exerciseService.syncExercise(voaIds);
    }

    /*************************************评测内容******************************/
    //提交评测
    public static Observable<BaseBean_data<Concept_eval_result>> submitEval(int userId, int voaId, String paraId, String lineN, String sentence, String evalPath){
        int appId = Constant.APP_ID;
        int wordId = 0;
        int flagId = 0;

        File evalFile = new File(evalPath);
        RequestBody body = MultipartBody.create(MediaType.parse("application/octet-stream"), evalFile);

        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(StrLibrary.type, Constant.AppName)
                .addFormDataPart(StrLibrary.userId, String.valueOf(userId))
                .addFormDataPart(StrLibrary.newsId, String.valueOf(voaId))
                .addFormDataPart(StrLibrary.paraId, paraId)
                .addFormDataPart(StrLibrary.IdIndex, lineN)
                .addFormDataPart(StrLibrary.sentence, sentence)
                .addFormDataPart(StrLibrary.wordId, String.valueOf(wordId))
                .addFormDataPart(StrLibrary.flg, String.valueOf(flagId))
                .addFormDataPart(StrLibrary.appId, String.valueOf(appId))
                .addFormDataPart(StrLibrary.file,evalFile.getName(),body)
                .build();

        EvalService evalService = RemoteManager.getInstance().createJson(EvalService.class);
        return evalService.submitEval(multipartBody);
    }

    //发布评测
    public static Observable<Concept_eval_publish> publishEval(int userId,String userName,int voaId,String paraId,String lineN,String score,String evalAudioUrl){
        String platform = "android";
        String format = "json";
        int protocol = 60002;
        String topic = "concept";
        int shuoType = 2;

        EvalService evalService = RemoteManager.getInstance().createJson(EvalService.class);
        return evalService.publishEval(platform,format,protocol,topic,userId,userName,voaId,lineN,paraId,score,shuoType,evalAudioUrl);
    }

    //合成音频
    public static Observable<Concept_eval_marge> margeAudio(List<String> evalAudioList,String score){
        //转换评测音频样式
        StringBuffer evalData = new StringBuffer();
        for (int i = 0; i < evalAudioList.size(); i++) {
            evalData.append(evalAudioList.get(i));

            if (i < evalAudioList.size()-1){
                evalData.append(",");
            }
        }

        EvalService evalService = RemoteManager.getInstance().createJson(EvalService.class);
        return evalService.margeAudio(evalData.toString(),score);
    }

    //发布合成的音频
    public static Observable<Concept_eval_publish_marge> publishMarge(int userId,String userName,int voaId,String score,String margeAudioUrl){
        String topic = "concept";
        String platform = "android";
        String format = "json";
        int protocol = 60003;
        int shuoType = 4;
        int appId = Constant.APP_ID;
        int rewardVersion = 1;

        EvalService evalService = RemoteManager.getInstance().createJson(EvalService.class);
        return evalService.publishMarge(topic,platform,format,protocol,userId,userName,voaId,score,shuoType,margeAudioUrl,appId,rewardVersion);
    }
}
