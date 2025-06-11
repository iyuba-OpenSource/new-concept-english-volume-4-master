package com.iyuba.core.common.data.local;

import com.iyuba.core.common.data.model.SendEvaluateResponse.WordsBean;

import java.util.List;


public interface EvWordTableInter {
    String TABLE_NAME = "EvaluateWord";
    String COLUMN_UID = "uId";
    String COLUMN_PARA_ID = "paraId";


    String COLUMN_VOA_ID = "voaId";
    String COLUMN_CONTENT = "content";//单词内容
    String COLUMN_SCORE = "score";//单词得分
    String COLUMN_INDEX= "indexId";//单词序号


    void setEvWord(String voaId, String uId, String paraId,WordsBean wordsBean);

    List<WordsBean> getEvWord(String voaId, String uId,String paraId);

}
