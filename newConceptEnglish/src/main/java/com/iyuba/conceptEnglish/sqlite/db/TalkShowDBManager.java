package com.iyuba.conceptEnglish.sqlite.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.iyuba.conceptEnglish.sqlite.op.TalkShowInter;
import com.iyuba.conceptEnglish.sqlite.op.TalkShowOp;
import com.iyuba.conceptEnglish.sqlite.op.VoaWord2Op;
import com.iyuba.core.common.data.model.TalkLesson;

import java.util.List;

import timber.log.Timber;

/**
 * @title: 口语秀的数据库管理
 * @date: 2023/5/19 15:30
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class TalkShowDBManager implements TalkShowInter {

    private static TalkShowDBManager sInstance;
    private final TalkShowOp talkShowOp;


    public static void init(Context appContext) {
        if (sInstance == null) {
            sInstance = new TalkShowDBManager(appContext);
            Timber.e("WordChildDBManager 创建");
        }
    }

    public static TalkShowDBManager getInstance() {
        if (null == sInstance) {
            throw new NullPointerException("not init");
        }
        return sInstance;
    }

    private TalkShowDBManager(Context context) {
        TalkShowDBHelper dbHelper = new TalkShowDBHelper(context);//onCreate
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        talkShowOp = new TalkShowOp(db);
    }

    @Override
    public void saveData(List<TalkLesson> list) {
        talkShowOp.saveData(list);
    }

    @Override
    public TalkLesson findTalkByVoaId(String voaId) {
        return talkShowOp.findTalkByVoaId(voaId);
    }

    @Override
    public List<TalkLesson> findTalkByBookId(String bookId) {
        return talkShowOp.findTalkByBookId(bookId);
    }
}
