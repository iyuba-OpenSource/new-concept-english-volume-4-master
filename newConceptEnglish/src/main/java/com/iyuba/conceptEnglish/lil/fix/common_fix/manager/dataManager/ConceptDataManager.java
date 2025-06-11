package com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager;

import com.iyuba.conceptEnglish.han.bean.EvaluationSentenceData;
import com.iyuba.conceptEnglish.han.bean.EvaluationSentenceResponse;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.WordBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.WordProgressBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.StrLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.RoomDB;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.concept.ChapterDetailEntity_conceptFour;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.concept.ChapterDetailEntity_conceptJunior;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.concept.LocalMarkEntity_concept;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.concept.LocalMarkEntity_conceptDownload;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.concept.WordEntity_conceptFour;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.concept.WordEntity_conceptJunior;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.RemoteManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.base.BaseBean_data;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.base.BaseBean_voatext;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Collect_chapter;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Concept_comment;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Concept_four_chapter_detail;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Concept_four_word;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Concept_junior_chapter_detail;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Concept_junior_word;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Eval_result;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Eval_result_concept;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Junior_chapter_collect;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Publish_eval;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Study_report;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.newService.ConceptService;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.FixUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.SignUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.DateUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.EncodeUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ResUtil;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.util.GetDeviceInfo;
import com.iyuba.core.lil.user.UserInfoManager;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * @title: 数据操作-新概念
 * @date: 2023/7/4 16:22
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class ConceptDataManager {

    /****************书籍*********************/

    /****************章节*********************/

    /******************章节详情****************/

    /*******************pdf******************/

    /*****************排行*******************/

    /*******************评测*****************/
    //句子评测
    public static Observable<EvaluationSentenceResponse> submitEval(int voaId, String paraId, String idIndex, String sentence, String filePath){
        //sentence			What%20does%20a%20pen%20have%20to%20do%20to%20record%20on%20paper%20the%20vibrations%20generated%20by%20an%20earthquake%3F
        //paraId			1
        //newsId			4042
        //IdIndex			2
        //type			concept
        //userId			14977580
        //file	application/octet-stream	/storage/emulated/0/Android/data/com.iyuba.learnNewEnglish/files/audio/sound14977580/40422.mp4	5.37 KB (5,496 bytes)

        int userId = UserInfoManager.getInstance().getUserId();
        String type = "concept";

        File file = new File(filePath);
        RequestBody fileBody = MultipartBody.create(MediaType.parse("application/octet-stream"),file);
        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(StrLibrary.type, type)
                .addFormDataPart(StrLibrary.userId,String.valueOf(userId))

                .addFormDataPart(StrLibrary.newsId,String.valueOf(voaId))
                .addFormDataPart(StrLibrary.paraId,paraId)
                .addFormDataPart(StrLibrary.IdIndex,idIndex)

                .addFormDataPart(StrLibrary.sentence,sentence)
                .addFormDataPart(StrLibrary.file,file.getName(),fileBody)
                .build();

        ConceptService conceptService = RemoteManager.getInstance().createJson(ConceptService.class);
        return conceptService.submitEval(multipartBody);
    }

    /*********************发布****************/
    //句子评测发布
    public static Observable<Publish_eval> publishEval(int voaId,String paraId,String idIndex,String score,String evalAudioUrl){
        //http://voa.iyuba.cn/voa/UnicomApi?platform=android&format=json&protocol=60002&topic=concept&userid=14977580&username=newtest101&voaid=1002&idIndex=3&paraid=1&score=41&shuoshuotype=2&content=wav6/202311/concept/20231118/17002727290802218.mp3
        int userId = UserInfoManager.getInstance().getUserId();
        String userName = UserInfoManager.getInstance().getUserName();

        String platform = "android";
        String json = "json";
        int protocol = 60002;
        String topic = "concept";

        int shuoshuoType = 2;

        ConceptService conceptService = RemoteManager.getInstance().createJson(ConceptService.class);
        return conceptService.publishEval(platform,json,protocol,topic,userId,userName,voaId,idIndex,paraId,score,shuoshuoType,evalAudioUrl);
    }

    /***********************************学习报告**********************/
    //接口-听力学习报告
    //http://daxue.iyuba.cn/ecollege/updateStudyRecordNew.jsp?format=json&platform=android&appName=concept&Lesson=concept&appId=222&BeginTime=2023-11-10+14%3A32%3A07&EndTime=2023-11-10+14%3A32%3A13&EndFlg=0&LessonId=30010&TestNumber=1&TestWords=0&TestMode=1&UserAnswer=&Score=0&DeviceId=&uid=14977580&sign=2efebc3d06007c3e38023af0779f9eb3&rewardVersion=1
    public static Observable<Study_report> submitConceptListenReport(long startTime,long endTime,int voaId,int userId,boolean isEnd,int curSentenceIndex,int wordCount){
        int appId = Constant.APP_ID;
        String type = "concept";
        String format = "json";
        String platform = "android";
        int testMode = 1;
        String userAnswer = "";
        String score = "0";
        GetDeviceInfo deviceInfo = new GetDeviceInfo(ResUtil.getInstance().getContext());
        String deviceId = deviceInfo.getLocalMACAddress();
        int rewardVersion = 1;
        int flag = 0;
        if (isEnd){
            flag = 1;
        }

        String appName = type;
        String lesson = type;
        String startDate = DateUtil.toDateStr(startTime,"yyyy-MM-dd+HH:mm:ss");
        startDate = EncodeUtil.encode(startDate);
        String endDate = DateUtil.toDateStr(endTime,"yyyy-MM-dd+HH:mm:ss");
        endDate = EncodeUtil.encode(endDate);

        String sign = userId+startTime+DateUtil.toDateStr(System.currentTimeMillis(),DateUtil.YMD);
        sign = EncodeUtil.md5(sign);

        ConceptService conceptService = RemoteManager.getInstance().createJson(ConceptService.class);
        return conceptService.submitListenReport(
                format,
                platform,
                appName,
                lesson,
                appId,
                startDate,
                endDate,
                flag,
                voaId,
                curSentenceIndex,
                testMode,
                String.valueOf(wordCount),
                userAnswer,
                score,
                deviceId,
                userId,
                sign,
                rewardVersion);
    }

    /************************************收藏**************************/
    //接口-收藏/取消收藏文章
    public static Observable<Collect_chapter> collectArticle(String types, String userId, String voaId, boolean isCollect){
        String groupName = "Iyuba";
        String sentenceFlag = "0";

        int appId = FixUtil.getAppId(types);
        String topic = FixUtil.getTopic(types);
        String sentenceId = "0";
        String type = "del";
        if (isCollect){
            type = "insert";
        }

        ConceptService commonService = RemoteManager.getInstance().createXml(ConceptService.class);
        return commonService.collectArticle(groupName,sentenceFlag,appId,userId,topic,voaId,sentenceId,type);
    }

    //接口-获取收藏的文章数据
    public static Observable<BaseBean_data<List<Junior_chapter_collect>>> getArticleCollect(String types, int userId){
        int appId = FixUtil.getAppId(types);
        String topic = FixUtil.getTopic(types);
        int flag = 0;
        String sign = SignUtil.getJuniorArticleCollectSign(topic,userId,appId);

        ConceptService commonService = RemoteManager.getInstance().createJson(ConceptService.class);
        return commonService.getArticleCollect(userId,sign,topic,appId,flag);
    }

    /*********************************章节详情*******************/
    //数据-获取新概念全四册的章节详情数据
    public Observable<BaseBean_data<List<Concept_four_chapter_detail>>> getConceptFourChapterDetailData(String voaId){
        ConceptService conceptService = RemoteManager.getInstance().createJson(ConceptService.class);
        return conceptService.getConceptFourChapterDetailData(voaId);
    }

    //数据-获取新概念青少版的章节详情数据
    public Observable<BaseBean_voatext<List<Concept_junior_chapter_detail>>> getConceptJuniorChapterDetailData(String voaId){
        ConceptService conceptService = RemoteManager.getInstance().createJson(ConceptService.class);
        return conceptService.getConceptJuniorChapterDetailData(voaId);
    }

    /****全四册****/
    //章节详情数据-查询新概念全四册的本课程的章节详情数据
    public static List<ChapterDetailEntity_conceptFour> searchConceptFourChapterDetailFromDB(String types, String voaId){
        return RoomDB.getInstance().getChapterDetailConceptFourDao().searchMultiDataByVoaId(types, voaId);
    }

    //章节详情数据-保存新概念全四册的本课程的章节详情数据
    public static void saveConceptFourChapterDetailToDB(List<ChapterDetailEntity_conceptFour> list){
        RoomDB.getInstance().getChapterDetailConceptFourDao().saveData(list);
    }

    /****青少版****/
    //章节详情数据-查询新概念青少版的本课程的章节详情数据
    public static List<ChapterDetailEntity_conceptJunior> searchConceptJuniorChapterDetailFromDB(String voaId){
        return RoomDB.getInstance().getChapterDetailConceptJuniorDao().searchMultiDataByVoaId(voaId);
    }

    //章节详情数据-保存新概念青少版的本课程的章节详情数据
    public static void saveConceptJuniorChapterDetailToDB(List<ChapterDetailEntity_conceptJunior> list){
        RoomDB.getInstance().getChapterDetailConceptJuniorDao().saveData(list);
    }

    /***************************单词********************/
    //数据-获取新概念全四册的单词数据
    public static Observable<BaseBean_data<List<Concept_four_word>>> getConceptFourWordData(String bookId){
        ConceptService conceptService = RemoteManager.getInstance().createJson(ConceptService.class);
        return conceptService.getConceptFourWordData(bookId);
    }

    //数据-获取新概念青少版的单词数据
    public static Observable<BaseBean_data<List<Concept_junior_word>>> getConceptJuniorWordData(String bookId){
        ConceptService conceptService = RemoteManager.getInstance().createJson(ConceptService.class);
        return conceptService.getConceptJuniorWordData(bookId);
    }

    /****全四册****/
    //单词数据-以分组的形式获取单词分组数据
    public static List<WordProgressBean> searchConceptFourGroupWordByBookIdFromDB(String bookId){
        return RoomDB.getInstance().getWordConceptFourDao().searchWordByBookIdGroup(bookId);
    }

    //单词数据-获取新概念全四册的本章节的单词数据
    public static List<WordEntity_conceptFour> searchConceptFourWordByVoaIdFromDB(String voaId){
        return RoomDB.getInstance().getWordConceptFourDao().searchWordByVoaId(voaId);
    }

    //单词数据-保存新概念全四册单词数据
    public static void saveConceptFourWordToDB(List<WordEntity_conceptFour> list){
        RoomDB.getInstance().getWordConceptFourDao().saveData(list);
    }

    //单词数据-保存新概念全四册单词数据
    public static void saveOldConceptFourWordToDB(List<WordBean> list){
        //转换成相应的数据
        List<WordEntity_conceptFour> tempList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            WordBean wordBean = list.get(i);

            WordEntity_conceptFour temp = new WordEntity_conceptFour();
            temp.voaId = Integer.parseInt(wordBean.getVoaId());
            temp.bookId = wordBean.getBookId();
            temp.position = wordBean.getPosition();
            temp.word = wordBean.getWord();
            temp.pron = wordBean.getPron();
            temp.def = wordBean.getDef();
            temp.audio = wordBean.getWordAudioUrl();
            temp.sentence_audio = wordBean.getSentenceAudioUrl();
            temp.sentence = wordBean.getSentence();
            temp.sentence_cn = wordBean.getSentenceCn();

            tempList.add(temp);
        }
        RoomDB.getInstance().getWordConceptFourDao().saveData(tempList);
    }

    /***青少版****/
    //单词数据-查询新概念青少版本书籍下分组的单词数据
    public static List<WordProgressBean> searchConceptJuniorWordByBookIdGroup(String bookId){
        return RoomDB.getInstance().getWordConceptJuniorDao().searchWordByBookIdGroup(bookId);
    }

    //单词数据-查询新概念青少版本书籍下本单元的单词数据
    public static List<WordEntity_conceptJunior> searchConceptJuniorWordByUnitIdFromDB(String unitId){
        return RoomDB.getInstance().getWordConceptJuniorDao().searchWordByUnitId(unitId);
    }

    //单词数据-查询新概念青少版本书籍下本章节的单词数据
    public static List<WordEntity_conceptJunior> searchConceptJuniorWordByVoaIdFromDB(String voaId){
        return RoomDB.getInstance().getWordConceptJuniorDao().searchWordByVoaId(voaId);
    }

    //单词数据-查询新概念青少版本书籍下本章节的单元id
    public static int searchConceptJuniorUnitIdByVoaId(String voaId){
        List<WordEntity_conceptJunior> list = searchConceptJuniorWordByVoaIdFromDB(voaId);
        if (list!=null&&list.size()>0){
            return list.get(0).unit_id;
        }
        return 0;
    }

    //单词数据-保存新概念青少版的单词数据
    public static void saveConceptJuniorWordToDB(List<WordEntity_conceptJunior> list){
        RoomDB.getInstance().getWordConceptJuniorDao().saveData(list);
    }

    /*********************************************评论内容**********************************/
    public static Observable<Concept_comment> getConceptCommentData(String voaId,int pageNum,int pageCount){
        //http://daxue.iyuba.cn/appApi/UnicomApi?protocol=60001&platform=android&format=xml&voaid=2001&pageNumber=1&pageCounts=15&appName=concept
        int protocol = 60001;
        String platform = "android";
        String format = "xml";
        String appName = "concept";

        ConceptService conceptService = RemoteManager.getInstance().createXml(ConceptService.class);
        return conceptService.getConceptCommentData(protocol,platform,format,voaId,pageNum,pageCount,appName);
    }

    /**********************************************本地的篇目数据****************************/
    //获取所有已经下载和需要下载的数据
    public static List<LocalMarkEntity_conceptDownload> getLocalMarkDownloadAndDownloadingData(int userId){
        List<LocalMarkEntity_conceptDownload> downloadedList = RoomDB.getInstance().getLocalMarkConceptDownloadDao().getAllDownloadData(TypeLibrary.FileDownloadStateType.file_downloaded,userId);
        List<LocalMarkEntity_conceptDownload> downloadingList = RoomDB.getInstance().getLocalMarkConceptDownloadDao().getAllDownloadData(TypeLibrary.FileDownloadStateType.file_isDownloading,userId);

        List<LocalMarkEntity_conceptDownload> allShowList = new ArrayList<>();

        if (downloadedList!=null&&downloadedList.size()>0){
            allShowList.addAll(downloadedList);
        }

        if (downloadingList!=null&&downloadingList.size()>0){
            allShowList.addAll(downloadingList);
        }
        return allShowList;
    }

    //获取所有的下载数据
    public static List<LocalMarkEntity_conceptDownload> getLocalMarkDownloadData(int userId){
        return RoomDB.getInstance().getLocalMarkConceptDownloadDao().getAllDownloadData(TypeLibrary.FileDownloadStateType.file_downloaded,userId);
    }

    //获取某个状态的下载数据
    public static List<LocalMarkEntity_conceptDownload> getLocalMarkDownloadStatusData(int userId,String downloadStatus){
        return RoomDB.getInstance().getLocalMarkConceptDownloadDao().getAllDownloadData(downloadStatus, userId);
    }

    //获取单个下载数据
    public static LocalMarkEntity_conceptDownload getLocalMarkDownloadSingleData(int voaId,String lessonType,int userId){
        return RoomDB.getInstance().getLocalMarkConceptDownloadDao().getSingleData(voaId,lessonType,userId);
    }

    //获取当前类型和id的下载数据
    public static LocalMarkEntity_conceptDownload getLocalMarkDownloadByVoaId(int voaId,String lessonType,int userId){
        return RoomDB.getInstance().getLocalMarkConceptDownloadDao().getSingleData(voaId, lessonType,userId);
    }

    //更新下载状态数据
    public static void updateLocalMarkDownloadStatus(int voaId,String type,int userId,String downloadStatus,int position){
        //先看下当前是否存在数据
        LocalMarkEntity_conceptDownload entity = RoomDB.getInstance().getLocalMarkConceptDownloadDao().getSingleData(voaId, type,userId);
        if (entity==null){
            LocalMarkEntity_conceptDownload newData = new LocalMarkEntity_conceptDownload(voaId,type,userId,downloadStatus,position);
            RoomDB.getInstance().getLocalMarkConceptDownloadDao().saveSingleData(newData);
        }else {
            RoomDB.getInstance().getLocalMarkConceptDownloadDao().updateDownloadStatus(voaId, type, userId,downloadStatus);
        }
    }

    //获取当前账号所有的阅读数据
    public static List<LocalMarkEntity_concept> getLocalMarkReadData(int userId){
        return RoomDB.getInstance().getLocalMarkConceptDao().getAllReadData(userId,"1");
    }

    //更新阅读状态数据
    public static void updateLocalMarkReadStatus(int voaId,String type,int userId,String readStatus,int position){
        //先判断当前是否存在再处理
        LocalMarkEntity_concept entity = RoomDB.getInstance().getLocalMarkConceptDao().getSingleData(voaId, type, userId);
        if (entity==null){
            LocalMarkEntity_concept newData = new LocalMarkEntity_concept(voaId,type,userId,readStatus,null,position);
            RoomDB.getInstance().getLocalMarkConceptDao().saveSingleData(newData);
        }else {
            RoomDB.getInstance().getLocalMarkConceptDao().updateReadStatus(voaId, type, userId, readStatus);
        }
    }

    //获取当前账号所有的收藏数据
    public static List<LocalMarkEntity_concept> getLocalMarkCollectData(int userId){
        return RoomDB.getInstance().getLocalMarkConceptDao().getAllCollectData(userId,"1");
    }

    //更新收藏状态数据
    public static void updateLocalMarkCollectStatus(int voaId,String type,int userId,String collectStatus,int position){
        //先判断当前是否存在再处理
        LocalMarkEntity_concept entity = RoomDB.getInstance().getLocalMarkConceptDao().getSingleData(voaId, type, userId);
        if (entity==null){
            LocalMarkEntity_concept newData = new LocalMarkEntity_concept(voaId,type,userId,null,collectStatus,position);
            RoomDB.getInstance().getLocalMarkConceptDao().saveSingleData(newData);
        }else {
            RoomDB.getInstance().getLocalMarkConceptDao().updateCollectStatus(voaId, type, userId, collectStatus);
        }
    }

    //获取当前类型下的单个下载数据
    public static LocalMarkEntity_conceptDownload getLocalMarkSingleDownload(int voaId,String type,int userId){
        return RoomDB.getInstance().getLocalMarkConceptDownloadDao().getSingleData(voaId, type,userId);
    }

    //获取当前类型下的单个收藏和阅读数据
    public static LocalMarkEntity_concept getLocalMarkSingle(int voaId,String type,int userId){
        return RoomDB.getInstance().getLocalMarkConceptDao().getSingleData(voaId, type, userId);
    }

    /************************************评测数据*******************************/

}
