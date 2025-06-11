package com.iyuba.conceptEnglish.sqlite.op;

import com.iyuba.conceptEnglish.sqlite.mode.SentenceAudio;
import com.iyuba.core.common.data.model.VoaWord2;

import java.util.List;

public interface VoaWord2Inter {

    String TABLE_NAME = "voa_word_child";
    String VOA_ID = "voa_id";//原有

    String WORD = "word";//原有
    String DEF = "def";//原有
    String AUDIO = "audio";//原有
    String PRON = "pron";//原有
    String EXAMPLES = "examples"; //句子序号 //原有
    String ANSWER = "answer"; //单词对错  接口数据中本来没有的 //原有
    String POSITION = "position"; //单词序号 //原有

    String ID_INDEX = "id_index"; //句子ID
    String BOOK_ID = "book_id"; //书的id
    String UNIT_ID = "unit_id"; //关的ID

    String VIDEO_URL = "videoUrl"; //视频地址
    String SENTENCE_CN = "Sentence_cn"; //中文句子 可能没有
    String PIC_URL = "pic_url"; //图片 要拼接
    String SENTENCE = "Sentence"; //句子
    String SENTENCE_AUDIO = "Sentence_audio"; //句子音频

    String TIME = "updateTime"; //更新时间
    String VERSION = "version"; //应该无用

    void saveData(List<VoaWord2> voaWords);

    List<VoaWord2> findDataByVoaId(String bookId,String lessonId);

    List<VoaWord2> findDataByBookId(String bookId);

    void updateData(VoaWord2 voaWord, String answer);

    List<String> findVideoList(String bookId);

    List<SentenceAudio> findSentenceAudios(String bookId);

    //查询数据通过bookid和voaid
    List<VoaWord2> findDataByBookIdAndVoaId(String bookId,String voaId);

}
