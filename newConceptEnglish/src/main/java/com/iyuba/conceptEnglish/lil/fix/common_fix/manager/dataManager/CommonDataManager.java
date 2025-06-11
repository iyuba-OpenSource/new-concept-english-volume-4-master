package com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager;

import android.text.TextUtils;

import com.iyuba.conceptEnglish.lil.concept_other.verify.AppCheckResponse;
import com.iyuba.conceptEnglish.lil.concept_other.verify.HelpUtil;
import com.iyuba.conceptEnglish.lil.concept_other.verify.OtherUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.StrLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.RoomDB;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.AgreeEntity;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.ChapterCollectEntity;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.EvalEntity_chapter;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.EvalEntity_word;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.SettingEntity;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.Setting_ReadLanguageEntity;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.WordBreakEntity;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.WordBreakPassEntity;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.RemoteManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.base.BaseBean_data;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Eval_rank;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Eval_rank_agree;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Eval_rank_detail;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Integral_bean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Integral_deduct;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Report_read;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Reward_history;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Word_detail;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Word_eval;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Word_insert;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.newService.CommonService;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.newService.UserInfoService;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.util.RemoteTransUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.FixUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.SignUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.DateUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.EncodeUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ResUtil;
import com.iyuba.conceptEnglish.util.ConceptApplication;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.util.GetDeviceInfo;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.user.remote.User_info;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * @title: 数据操作-通用类型
 * @date: 2023/7/4 16:46
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class CommonDataManager {

    /*******************************用户信息*************************/
    //接口-获取用户信息
    public static Observable<User_info> getUserInfo(int uid){
        int protocol = 20001;
        int appId = Constant.APP_ID;
        String format = "json";
        String platform = "android";
        String sign = SignUtil.userInfoSign(protocol,uid);

        UserInfoService userInfoService = RemoteManager.getInstance().createJson(UserInfoService.class);
        return userInfoService.getUserInfo(protocol,appId,uid,uid,format,sign,platform);
    }

    /*******************************审核*********************************/
    //接口-获取审核信息
    public static Observable<AppCheckResponse> getVerifyData(int verifyId){
        String version = HelpUtil.getAppVersion(ResUtil.getInstance().getContext(), ResUtil.getInstance().getContext().getPackageName());
        if (OtherUtil.isBelongToOppoPhone()){
            version = "oppo_"+version;
        }

        CommonService verifyService = RemoteManager.getInstance().createJson(CommonService.class);
        return verifyService.verify(verifyId,version);
    }

    /***************************单词查询********************************/
    //接口-查询单词
    public static Observable<Word_detail> searchWord(String word){
        CommonService commonService = RemoteManager.getInstance().createXml(CommonService.class);
        return commonService.searchWord(word);
    }

    //接口-查询单词(搜索界面使用)
    public static Observable<Word_detail> searchWord(String word,int uid){
        int appId = Constant.APP_ID;
        int testMode = 2;

        CommonService commonService = RemoteManager.getInstance().createXml(CommonService.class);
        return commonService.searchWord(word,uid,testMode,appId);
    }

    //接口-插入/删除单词
    public static Observable<Word_insert> insertOrDeleteWord(String word, int userId, boolean isInsert){
        String mode = "delete";
        if (isInsert){
            mode = "insert";
        }
        String groupName = "Iyuba";

        CommonService commonService = RemoteManager.getInstance().createXml(CommonService.class);
        return commonService.insertWord(userId,mode,groupName,word);
    }

    //接口-单词评测
    public static Observable<Word_eval> evalWord(int voaId,int paraId,int idIndex,int userId,String wordId,String word,String filePath){
        word = EncodeUtil.encode(word).replaceAll("\\+","%20");

        File file = new File(filePath);
        RequestBody fileBody = MultipartBody.create(MediaType.parse("multipart/form-data"),file);
        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(StrLibrary.sentence, word)
                .addFormDataPart(StrLibrary.flg,"2")

                .addFormDataPart(StrLibrary.paraId,String.valueOf(paraId))
                .addFormDataPart(StrLibrary.newsId,String.valueOf(voaId))
                .addFormDataPart(StrLibrary.IdIndex,String.valueOf(idIndex))

                .addFormDataPart(StrLibrary.appId, String.valueOf(Constant.APP_ID))
                .addFormDataPart(StrLibrary.wordId,wordId)
                .addFormDataPart(StrLibrary.type,Constant.AppName)

                .addFormDataPart(StrLibrary.userId,String.valueOf(userId))
                .addFormDataPart(StrLibrary.file,file.getName(),fileBody)
                .build();

        CommonService commonService = RemoteManager.getInstance().createJson(CommonService.class);
        return commonService.evalWord(multipartBody);
    }

    /***************************单词闯关*******************************/
    //数据库-保存单词闯关数据
    public static void saveWordBreakDataToDB(List<WordBreakEntity> list){
        RoomDB.getInstance().getWordBreakEntityDao().saveData(list);
    }

    //数据库-获取本id下正确的单词闯关数据
    public static List<WordBreakEntity> searchWordBreakRightDataByIdFromDB(String types,String bookId,String id,long userId){
        return RoomDB.getInstance().getWordBreakEntityDao().searchRightDataById(types, bookId, id, userId);
    }

    //数据库-保存单词闯关数据
    public static void saveWordBreakPassDataToDB(WordBreakPassEntity entity){
        RoomDB.getInstance().getWordBreakPassDao().saveData(entity);
    }

    /*****************************单词进度数据*************************/
    //数据库-获取单词闯关进度的数据
    public static String searchWordBreakPassIdDataFromDB(String types,String bookId,long userId){
        return RoomDB.getInstance().getWordBreakPassDao().searchPassId(types, bookId,userId);
    }

    /*****************************************单词评测数据************************************/
    //数据库-保存单词评测数据
    public static void saveEvalWordToDB(EvalEntity_word entity){
        RoomDB.getInstance().getEvalWordDao().saveData(entity);
    }

    //数据库-获取单词的评测数据
    public static EvalEntity_word searchEvalWordFromDB(String types,String bookId,String voaId,String position,String sentence){
        sentence = RemoteTransUtil.transWordToEntityData(sentence);
        return RoomDB.getInstance().getEvalWordDao().searchSingleData(types, bookId, voaId, position, sentence);
    }

    /**********************************积分****************************/
    //接口-分享之后获取积分数据
    public static Observable<Integral_bean> getIntegralAfterShare(String integralId, int uid, String voaId){
        int appId = Constant.APP_ID;
        int mobile = 1;
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String flag = "1234567890"+df.format(new Date());

        CommonService commonService = RemoteManager.getInstance().createJson(CommonService.class);
        return commonService.getIntegralAfterShare(integralId,mobile,flag,uid,appId,voaId);
    }

    //接口-下载pdf扣除积分
    public static Observable<Integral_deduct> deductIntegralBeforePdf(int uid,String voaId){
        //http://api.iyuba.cn/credits/updateScore.jsp?srid=40&mobile=1&flag=MjAyMzA3MDQxNTUyMjE%3D%0A&uid=12071118&appid=222&idindex=1002
        int srid = 40;
        int mobile = 1;
        int appId = Constant.APP_ID;

        String date = DateUtil.toDateStr(System.currentTimeMillis(),"yyyyMMddHHmmss");
        date = EncodeUtil.encode64(EncodeUtil.encode(date).getBytes());

        CommonService commonService = RemoteManager.getInstance().createJson(CommonService.class);
        return commonService.deductIntegral(srid,mobile,date,String.valueOf(uid),appId,voaId);
    }

    /************************************收藏**************************/
    //数据库-保存收藏的章节数据
    public static void saveChapterCollectDataToDB(ChapterCollectEntity entity){
        RoomDB.getInstance().getChapterCollectEntityDao().saveData(entity);
    }

    //数据库-保存多个收藏的章节数据
    public static void saveChapterMultiCollectDataToDB(List<ChapterCollectEntity> entities){
//        RoomDB.getInstance().getChapterCollectEntityDao().saveMultiData(entities);
        for (int i = 0; i < entities.size(); i++) {
            ChapterCollectEntity entity = filterChapterCollect(entities.get(i));
            if (entity!=null){
                RoomDB.getInstance().getChapterCollectEntityDao().saveData(entity);
            }
        }
    }

    //数据库-删除收藏的章节数据
    public static void deleteChapterCollectDataToDB(String types,String voaId){
        RoomDB.getInstance().getChapterCollectEntityDao().deleteData(types, voaId);
    }

    //数据库-获取当前章节的收藏信息
    public static ChapterCollectEntity getChapterCollectDataFromDB(String types,String voaId,String userId){
        return RoomDB.getInstance().getChapterCollectEntityDao().searchSingleData(userId,types,voaId);
    }

    //数据库-获取当前用户的多个类型下的收藏数据
    public static List<ChapterCollectEntity> getChapterCollectMultiData(String[] types,String userId){
        List<ChapterCollectEntity> collectList = new ArrayList<>();
        for (int i = 0; i < types.length; i++) {
            List<ChapterCollectEntity> temp = getChapterCollectDataByTypes(types[i],userId);
            if (temp!=null&&temp.size()>0){
                collectList.addAll(temp);
            }
        }
        return collectList;
    }

    //数据库-获取当前用户的某个类型下的收藏数据
    public static List<ChapterCollectEntity> getChapterCollectDataByTypes(String types,String userId){
        return RoomDB.getInstance().getChapterCollectEntityDao().searchMultiDataByUserId(userId,types);
    }

    /*****************************课程评测数据************************************/
    //数据库-保存课程评测数据
    public static void saveEvalChapterDataToDB(EvalEntity_chapter chapter){
        RoomDB.getInstance().getEvalEntityChapterDao().saveData(chapter);
    }

    //数据库-获取课程的当前章节已经评测的数据数量
    public static long getEvalChapterSizeFromDB(String types,String voaId,String uid){
        return RoomDB.getInstance().getEvalEntityChapterDao().searchEvalCountFromVoaId(types, voaId,uid);
    }

    //数据库-获取课程的当前章节已经评测的数据
    public static List<EvalEntity_chapter> getEvalChapterByVoaIdFromDB(String types,String voaId,String uid){
        return RoomDB.getInstance().getEvalEntityChapterDao().searchEvalFromVoaId(types, voaId,uid);
    }

    //数据库-获取课程的当前位置的评测数据
    public static EvalEntity_chapter getEvalChapterDataFromDB(String types,String voaId,String paraId,String idIndex,String uid){
        return RoomDB.getInstance().getEvalEntityChapterDao().searchSingleEvalResult(types, voaId, paraId, idIndex,uid);
    }

    /************************************评测排行榜******************************/
    //接口-获取评测排行榜数据
    public static Observable<Eval_rank> getEvalRankData(String bookTypes, String voaId, int start, int total, String showType){
        int uid = UserInfoManager.getInstance().getUserId();
        String sign = SignUtil.getEvalRankSign(bookTypes,uid,voaId,start,total);
        String topic = FixUtil.getTopic(bookTypes);

        CommonService apiService = RemoteManager.getInstance().createJson(CommonService.class);
        return apiService.getEvalRankData(topic,voaId,uid,start,total,sign,showType);
    }

    //接口-获取课程评测排行榜的详情数据
    public static Observable<BaseBean_data<List<Eval_rank_detail>>>  getEvalRankDetailData(String types, String voaId, String showUserId){
        String shuoshuoType = "2,4";
        String sign = SignUtil.getEvalRankDetailSign(showUserId);
        String topic = FixUtil.getTopic(types);

        CommonService apiService = RemoteManager.getInstance().createJson(CommonService.class);
        return apiService.getEvalRankDetailData(showUserId,topic,shuoshuoType,sign,voaId);
    }

    //接口-点赞评测排行的详情内容数据
    public static Observable<Eval_rank_agree> agreeEvalRankDetailData(String userId, String evalSentenceId){
        int protocol = 61001;

        CommonService apiService = RemoteManager.getInstance().createJson(CommonService.class);
        return apiService.agreeEvalRankDetail(protocol,userId,evalSentenceId);
    }

    //数据库-保存评测或配音排行的详情的点赞数据
    public static void saveAgreeDataToDB(AgreeEntity eval){
        RoomDB.getInstance().getAgreeEntityEvalDao().saveData(eval);
    }

    //数据库-获取评测或配音排行的详情的点赞数据
    public static AgreeEntity getAgreeDataFromDB(String userId, String agreeUserId, String types, String voaId, String sentenceId){
        return RoomDB.getInstance().getAgreeEntityEvalDao().getData(userId, agreeUserId, types, voaId, sentenceId);
    }

    /***************************************学习报告******************************/
    //提交阅读学习报告
    public static Observable<Report_read> submitReportRead(String types,int uid, String lessonName, String voaId, long wordCount, long startTime, long endTime){
        String format = "xml";
        String platform = "android";
        int appId = Constant.APP_ID;
        GetDeviceInfo deviceInfo = new GetDeviceInfo(ConceptApplication.getContext());
        String device = deviceInfo.getLocalDeviceType();
        String deviceId = deviceInfo.getLocalMACAddress();
        String startDate = DateUtil.toDateStr(startTime,DateUtil.YMDHMS);
        String endDate = DateUtil.toDateStr(endTime,DateUtil.YMDHMS);
        int endFlag = 1;
        String categoryId = "0";

        lessonName = types;
        startDate = EncodeUtil.encode(startDate);
        endDate = EncodeUtil.encode(endDate);

        //增加奖励的参数
        int rewardVersion=1;

        CommonService commonService = RemoteManager.getInstance().createXml(CommonService.class);
        return commonService.submitReadReport(format,uid,startDate,endDate,types,lessonName,voaId,appId,device,deviceId,endFlag,wordCount,categoryId,platform,rewardVersion);
    }

    /****************************************奖励信息******************************/
    //获取奖励的历史记录
    public static Observable<BaseBean_data<List<Reward_history>>> getRewardHistory(int uid,int pages,int pageCount){
        String sign = SignUtil.getRewardHistorySign(uid);

        CommonService commonService = RemoteManager.getInstance().createJson(CommonService.class);
        return commonService.getRewardHistory(uid,pages,pageCount,sign);
    }

    /****************************************阅读界面语言切换****************************/
    /***************当前这个功能默认不和用户存在关联，如需关联后期进行处理即可*********/
    //数据库-保存阅读界面单个章节的语言切换操作
    public static void saveReadLanguageSettingToDB(String types,String bookId,String voaId,String languageType){
        Setting_ReadLanguageEntity entity = new Setting_ReadLanguageEntity(types,bookId,voaId,"0",languageType);
        RoomDB.getInstance().getReadLanguageDao().insertData(entity);
    }

    //数据库-获取阅读界面单个章节的语言切换操作
    public static Setting_ReadLanguageEntity searchReadLanguageSettingFromDB(String types, String bookId, String voaId){
        return RoomDB.getInstance().getReadLanguageDao().getSingleData(types, bookId, voaId, "0");
    }

    /***************************************其他操作*************************************/
    //数据库-保存设置
    public static void saveSettingDataToDB(SettingEntity entity){
        RoomDB.getInstance().getSettingEntityDao().saveData(entity);
    }

    //数据库-获取本用户的某个设置
    public static SettingEntity getSettingDataFromDB(String userId,String type){
        return RoomDB.getInstance().getSettingEntityDao().getDataByUser(userId, type);
    }

    /*************************************辅助方法操作********************************/
    //筛选同步的收藏数据，如果title和desc都是空，那直接不要这个数据了
    private static ChapterCollectEntity filterChapterCollect(ChapterCollectEntity entity){
        if (entity==null){
            return null;
        }

        if (TextUtils.isEmpty(entity.title)&&TextUtils.isEmpty(entity.desc)){
            return null;
        }

        return entity;
    }

    /********************************************下载文件******************************/
    public static Observable<ResponseBody> downloadFile(String fileUrl){
        CommonService commonService = RemoteManager.getInstance().create(CommonService.class);
        return commonService.downloadFile(fileUrl);
    }

    /**********************************************其他操作*******************************/
    //将文本转换成part类型
    private static RequestBody transTextToBody(String text){
        return RequestBody.create(MediaType.parse("text/plain"), text);
    }

    //将文件转换成part类型
    private static MultipartBody.Part transFileToBody(File file){
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        return MultipartBody.Part.createFormData("file", file.getName(), requestFile);
    }
}
