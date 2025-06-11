package com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.manager;

import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.RemoteManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Word_note;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.newService.WordService;

import io.reactivex.Observable;

/**
 * 单词的远程接口操作
 */
public class WordRemoteManager {

    //获取单词的生词本数据
    public static Observable<Word_note> getWordNoteData(int userId,int pageIndex,int showCount){
        WordService wordService = RemoteManager.getInstance().createXml(WordService.class);
        return wordService.getWordNoteData(userId,pageIndex,showCount);
    }
}
