package com.iyuba.conceptEnglish.sqlite.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.iyuba.conceptEnglish.sqlite.mode.SentenceAudio;
import com.iyuba.conceptEnglish.sqlite.op.VoaWord2Inter;
import com.iyuba.conceptEnglish.sqlite.op.VoaWord2Op;
import com.iyuba.core.common.data.model.VoaWord2;

import java.util.List;

import timber.log.Timber;

/**
 * 单词子类数据库 管理工具
 * 辅助类
 * 作用：在初始化中创建单词表，并且提供一系列对单词表的操作
 */
public class WordChildDBManager implements VoaWord2Inter {

    private static WordChildDBManager sInstance;
    private final VoaWord2Op voaWord2Op;


    public static void init(Context appContext) {
        if (sInstance == null) {
            sInstance = new WordChildDBManager(appContext);
            Timber.e("WordChildDBManager 创建");
        }
    }

    public static WordChildDBManager getInstance() {
        if (null == sInstance) {
            throw new NullPointerException("not init");
        }
        return sInstance;
    }

    private WordChildDBManager(Context context) {
        WordChildDBHelper dbHelper = new WordChildDBHelper(context);//onCreate
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        voaWord2Op = new VoaWord2Op(db);
    }

    @Override
    public void saveData(List<VoaWord2> voaWords) {
        voaWord2Op.saveData(voaWords);
    }

    @Override
    public List<VoaWord2> findDataByVoaId(String bookId, String lessonId) {
        return voaWord2Op.findDataByVoaId(bookId,lessonId);
    }

    @Override
    public List<VoaWord2> findDataByBookId(String bookId) {
        return voaWord2Op.findDataByBookId(bookId);
    }

    @Override
    public void updateData(VoaWord2 voaWord, String answer) {
        voaWord2Op.updateData(voaWord,answer);
    }

    @Override
    public List<String> findVideoList(String bookId) {
        return voaWord2Op.findVideoList(bookId);
    }

    @Override
    public List<SentenceAudio> findSentenceAudios(String bookId) {
        return voaWord2Op.findSentenceAudios(bookId);
    }

    @Override
    public List<VoaWord2> findDataByBookIdAndVoaId(String bookId, String voaId) {
        return voaWord2Op.findDataByBookIdAndVoaId(bookId, voaId);
    }
}
