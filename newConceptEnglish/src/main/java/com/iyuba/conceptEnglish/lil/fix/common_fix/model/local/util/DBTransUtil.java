package com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.util;

import android.text.TextUtils;

import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.BookChapterBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.ChapterDetailBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.EvalChapterBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.EvalShowBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.WordBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.ChapterCollectEntity;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.concept.WordEntity_conceptFour;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.concept.WordEntity_conceptJunior;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.junior.ChapterDetailEntity_junior;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.novel.ChapterDetailEntity_novel;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.junior.ChapterEntity_junior;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.novel.ChapterEntity_novel;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.EvalEntity_chapter;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.EvalEntity_word;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.junior.WordEntity_junior;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Junior_eval;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.FixUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.gson.GsonUtil;
import com.iyuba.core.common.data.model.VoaWord2;

import java.util.ArrayList;
import java.util.List;

/**
 * @title: 数据库转换工具
 * @date: 2023/5/19 17:42
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class DBTransUtil {

    /******************转换为通用类****************/
    /********中小学********/
    //中小学-转换章节数据
    public static List<BookChapterBean> transJuniorChapterData(String types,List<ChapterEntity_junior> list){
        List<BookChapterBean> temp = new ArrayList<>();
        if (list!=null&&list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                ChapterEntity_junior junior = list.get(i);

                temp.add(transJuniorSingleChapterData(types,junior));
            }
        }

        return temp;
    }

    public static BookChapterBean transJuniorSingleChapterData(String types,ChapterEntity_junior junior){
        BookChapterBean chapterBean = null;
        if (junior!=null){
            boolean hasImage = !TextUtils.isEmpty(junior.clickRead) && Integer.parseInt(junior.clickRead) > 0;
            boolean hasExercise = !TextUtils.isEmpty(junior.havePractice) && junior.havePractice.equals("1");
            //单词这里需要查询数据库才行，这里暂存，等有单词数据库了再处理
            boolean hasVideo = !TextUtils.isEmpty(junior.video);

            chapterBean = new BookChapterBean(
                    types,
                    String.valueOf(junior.voaId),
                    String.valueOf(junior.series),
                    FixUtil.fixJuniorAudioUrl(String.valueOf(junior.voaId),junior.sound),
                    junior.pic,
                    TextUtils.isEmpty(junior.video)?"":FixUtil.fixJuniorVideoUrl(junior.video),
                    junior.sound,
                    junior.title_cn,
                    junior.title,
                    String.valueOf(junior.series),
                    String.valueOf(junior.voaId),
                    "",

                    false,//这个标志符号没有意义，需要查询数据库确定
                    hasImage,
                    hasExercise,
                    hasVideo
            );
        }
        return chapterBean;
    }

    //中小学-转换章节详情数据
    public static List<ChapterDetailBean> transJuniorChapterDetailData(List<ChapterDetailEntity_junior> list){
        List<ChapterDetailBean> temp = new ArrayList<>();

        if (list!=null&&list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                ChapterDetailEntity_junior junior = list.get(i);

                temp.add(new ChapterDetailBean(
                        junior.types,
                        junior.voaId,
                        String.valueOf(junior.paraId),
                        String.valueOf(junior.idIndex),
                        junior.sentence,
                        junior.sentence_cn,
                        junior.timing,
                        junior.endTiming,
                        TextUtils.isDigitsOnly(junior.imgPath)?"": FixUtil.fixJuniorImagePicUrl(junior.imgPath),
                        junior.start_x,
                        junior.start_y,
                        junior.end_x,
                        junior.end_y
                ));
            }
        }
        return temp;
    }

    //将单词数据-中小学转换为通用类
    public static List<WordBean> juniorWordToWordData(String types, List<WordEntity_junior> list){
        List<WordBean> temp = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            WordEntity_junior concept = list.get(i);

            temp.add(new WordBean(
                    types,
                    concept.book_id,
                    String.valueOf(concept.voaId),
                    String.valueOf(concept.unit_id),
                    transWordToShowData(concept.word),
                    concept.pron,
                    concept.def,
                    String.valueOf(concept.position),
                    transWordToShowData(concept.Sentence),
                    concept.Sentence_cn,
                    TextUtils.isEmpty(concept.pic_url)?"":FixUtil.fixWordPicUrl(concept.pic_url),
                    concept.videoUrl,
                    concept.audio,
                    concept.Sentence_audio
            ));
        }

        return temp;
    }

    /**小说**/
    //将章节数据-小说转换为通用类
    public static List<BookChapterBean> novelToChapterData(List<ChapterEntity_novel> list){
        List<BookChapterBean> temp = new ArrayList<>();
        if (list!=null&&list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                ChapterEntity_novel novel = list.get(i);

                temp.add(novelToSingleChapterData(novel));
            }
        }
        return temp;
    }

    public static BookChapterBean novelToSingleChapterData(ChapterEntity_novel novel){
        if (novel==null){
            return null;
        }

        return new BookChapterBean(
                novel.types,
                novel.voaid,
                novel.orderNumber,
                FixUtil.fixNovelAudioUrl(novel.sound),
                "",
                "",
                "",
                novel.cname_en,
                novel.cname_cn,
                novel.orderNumber,
                novel.chapterOrder,
                novel.level,
                false,
                false,
                false,
                false
        );
    }

    //将章节详情数据-小说转换为通用类
    public static List<ChapterDetailBean> novelToChapterDetailData(List<ChapterDetailEntity_novel> list){
        List<ChapterDetailBean> temp = new ArrayList<>();

        if (list!=null&&list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                ChapterDetailEntity_novel novel = list.get(i);

                // TODO: 2024/8/23 这里之前有个问题：部分内容的开始和结束时间是相反的，这里可以考虑将数据反过来使用
                //nb，反过来也没用，服务器上的数据错误了，需要服务器修改
                //如果开始时间比结束时间晚，则交换开始和结束时间
                double startTime = Double.parseDouble(novel.BeginTiming);
                double endTime = Double.parseDouble(novel.EndTiming);

                //交换时间
//                double fixTime = 0;
//                if (startTime > endTime){
//                    fixTime = startTime;
//                    startTime = endTime;
//                    endTime = fixTime;
//                }

                temp.add(new ChapterDetailBean(
                        novel.types,
                        String.valueOf(novel.voaid),
                        String.valueOf(novel.paraid),
                        String.valueOf(novel.indexId),
                        novel.textEN,
                        novel.textCH,
                        startTime,
                        endTime,
                        novel.image,
                        "",
                        "",
                        "",
                        ""
                ));
            }
        }

        return temp;
    }

    /****新概念****/
    //将单词数据-新概念全四册转换为通用类
    public static List<WordBean> conceptFourWordToWordData(String types, List<WordEntity_conceptFour> list){
        List<WordBean> temp = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            WordEntity_conceptFour concept = list.get(i);

            temp.add(new WordBean(
                    types,
                    concept.bookId,
                    String.valueOf(concept.voaId),
                    String.valueOf(concept.voaId),
                    transWordToShowData(concept.word),
                    concept.pron,
                    concept.def,
                    concept.position,
                    transWordToShowData(concept.sentence),
                    concept.sentence_cn,
                    "",
                    "",
                    concept.audio,
                    concept.sentence_single_audio
            ));
        }

        return temp;
    }

    //将单词数据-原来数据库的全四册转换为通用类
    public static List<WordBean> oldDbConceptFourWordToWordData(String type, List<VoaWord2> list){
        List<WordBean> wordBeanList = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            VoaWord2 voaWord2 = list.get(i);
            wordBeanList.add(oldDbConceptFourWordToSingleWordData(type,voaWord2,i));
        }

        return wordBeanList;
    }

    public static WordBean oldDbConceptFourWordToSingleWordData(String type, VoaWord2 voaWord2,int position){
        return new WordBean(
                type,
                voaWord2.bookId,
                voaWord2.voaId,
                voaWord2.unitId,
                voaWord2.word,
                voaWord2.pron,
                voaWord2.def,
                String.valueOf(position),
                voaWord2.Sentence,
                voaWord2.SentenceCn,
                voaWord2.picUrl,
                voaWord2.videoUrl,
                voaWord2.audio,
                voaWord2.SentenceAudio
        );
    }

    //将单词数据-新概念青少版转换为通用类
    public static List<WordBean> conceptJuniorWordToWordData(String types, List<WordEntity_conceptJunior> list){
        List<WordBean> temp = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            WordEntity_conceptJunior concept = list.get(i);

            temp.add(new WordBean(
                    types,
                    concept.book_id,
                    String.valueOf(concept.voaId),
                    String.valueOf(concept.unit_id),
                    transWordToShowData(concept.word),
                    concept.pron,
                    concept.def,
                    String.valueOf(concept.position),
                    transWordToShowData(concept.Sentence),
                    concept.Sentence_cn,
                    TextUtils.isEmpty(concept.pic_url)?"":FixUtil.fixWordPicUrl(concept.pic_url),
                    concept.videoUrl,
                    concept.audio,
                    concept.Sentence_audio
            ));
        }

        return temp;
    }

    /**********其他数据*********/
    //评测数据-转换章节评测数据
    public static EvalChapterBean transEvalSingleChapterData(EvalEntity_chapter chapter){
        if (chapter==null){
            return null;
        }

        //转换单词数据
        List<EvalChapterBean.WordBean> wordBeanList = new ArrayList<>();
        if (!TextUtils.isEmpty(chapter.wordList)){
            wordBeanList = GsonUtil.toList(chapter.wordList,EvalChapterBean.WordBean.class);
        }

        //转换评测的音频数据-根据类型单独处理
        String evalAudioUrl = FixUtil.fixJuniorEvalAudioUrl(chapter.url);
        if (chapter.types.equals(TypeLibrary.BookType.bookworm)
                ||chapter.types.equals(TypeLibrary.BookType.newCamstory)
                ||chapter.types.equals(TypeLibrary.BookType.newCamstoryColor)){
            evalAudioUrl = FixUtil.fixNovelEvalAudioUrl(chapter.url);
        }

        return new EvalChapterBean(
                chapter.sentence,
                chapter.scores,
                chapter.total_score,
                chapter.filepath,
                evalAudioUrl,
                chapter.types,
                chapter.voaId,
                chapter.paraId,
                chapter.indexId,
                wordBeanList
        );
    }

    //将单词评测数据转换为通用类
    public static EvalShowBean wordEvalToShowData(EvalEntity_word word){
        if (word==null){
            return null;
        }

        //根据类型转换单词数据
        List<EvalShowBean.WordResultBean> wordList = new ArrayList<>();
        //根据类型转换评测的音频数据
        String evalAudioUrl = "";

        switch (word.types){
            /*case TypeLibrary.BookType.conceptFour:
            case TypeLibrary.BookType.conceptFourUS:
            case TypeLibrary.BookType.conceptFourUK:
            case TypeLibrary.BookType.conceptJunior:
                //新概念
                wordList = toConceptWord(word.words);
                break;*/
            case TypeLibrary.BookType.junior_primary:
            case TypeLibrary.BookType.junior_middle:
                //中小学
                wordList = toJuniorWord(word.words);
                evalAudioUrl = FixUtil.fixJuniorEvalAudioUrl(word.url);
                break;
            case TypeLibrary.BookType.bookworm:
            case TypeLibrary.BookType.newCamstory:
            case TypeLibrary.BookType.newCamstoryColor:
                //小说
                wordList = toJuniorWord(word.words);
                evalAudioUrl = FixUtil.fixNovelEvalAudioUrl(word.url);
                break;
        }

        return new EvalShowBean(
                transWordToShowData(word.sentence),
                TextUtils.isEmpty(word.url)?"":evalAudioUrl,
                word.localPath,
                word.totalScore,
                wordList
        );
    }

    //将评测结果中的单词转换成中小学类型数据
    private static List<EvalShowBean.WordResultBean> toJuniorWord(String words){
        List<EvalShowBean.WordResultBean> wordList = new ArrayList<>();
        if (!TextUtils.isEmpty(words)){
            List<Junior_eval.WordsBean> tempList = GsonUtil.toList(words,Junior_eval.WordsBean.class);
            if (tempList!=null&&tempList.size()>0){
                for (int i = 0; i < tempList.size(); i++) {
                    Junior_eval.WordsBean temp = tempList.get(i);
                    wordList.add(new EvalShowBean.WordResultBean(
                            transWordToShowData(temp.getContent()),
                            temp.getScore(),
                            TextUtils.isEmpty(temp.getIndex())?0:Integer.parseInt(temp.getIndex()),
                            temp.getPron2(),
                            temp.getUser_pron2()
                    ));
                }
            }
        }
        return wordList;
    }

    //中小学-将收藏的章节数据转换为章节显示数据
    public static List<BookChapterBean> transJuniorCollectToBookChapterData(List<ChapterCollectEntity> list){
        List<BookChapterBean> temp = new ArrayList<>();
        if (list!=null&&list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                ChapterCollectEntity entity = list.get(i);
                BookChapterBean bean = transJuniorCollectSingleToBookChapterData(entity);
                if (bean!=null){
                    temp.add(bean);
                }
            }
        }
        return temp;
    }

    private static BookChapterBean transJuniorCollectSingleToBookChapterData(ChapterCollectEntity entity){
        if (entity!=null){
            return new BookChapterBean(
                    entity.types,
                    entity.voaId,
                    entity.bookId,
                    "",
                    entity.picUrl,
                    "",
                    "",
                    entity.title,
                    entity.desc,
                    "",
                    "",
                    "",
                    false,
                    false,
                    false,
                    false
            );
        }
        return null;
    }
    /**************************辅助方法******************/
    //将单词转为通用类型的数据
    public static String transWordToShowData(String word){
        if (TextUtils.isEmpty(word)){
            return word;
        }

        //因为数据库存储的特殊字符存在问题，因此需要转换
        if (word.contains("‘")){
            word = word.replace("‘","'");
        }

        return word;
    }
}
