package com.iyuba.conceptEnglish.sqlite.op;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.iyuba.conceptEnglish.sqlite.mode.SentenceAudio;
import com.iyuba.core.common.data.model.VoaWord2;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取单词数据库
 */
public class VoaWord2Op implements VoaWord2Inter {

    private final SQLiteDatabase db;

    public VoaWord2Op(SQLiteDatabase db) {
        this.db = db;
    }

    /**
     * 批量插入数据
     */
    @Override
    public void saveData(List<VoaWord2> voaWords) {

        if (voaWords != null && voaWords.size() != 0) {
            for (int i = 0; i < voaWords.size(); i++) {
                VoaWord2 tempword = voaWords.get(i);//再加10个参数
                db.execSQL(
                        "insert or replace into " + TABLE_NAME + " (" + VOA_ID + ","
                                + WORD + "," + DEF + "," + AUDIO + "," + PRON + "," + EXAMPLES + ","
                                + POSITION + "," + ID_INDEX + "," + BOOK_ID + "," + UNIT_ID + "," + TIME + "," + VERSION
                                + "," + VIDEO_URL + "," + SENTENCE_CN + "," + PIC_URL + "," + SENTENCE + "," + SENTENCE_AUDIO
                                + ") values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                        new Object[]{tempword.voaId, tempword.word, tempword.def, tempword.audio,
                                tempword.pron, tempword.examples, tempword.position, tempword.idindex,
                                tempword.bookId, tempword.unitId, tempword.updateTime, tempword.version,
                                tempword.videoUrl, tempword.SentenceCn, tempword.picUrl, tempword.Sentence,
                                tempword.SentenceAudio});//17个 shite
            }
        }
    }

    /**
     * 查找 还能查的漏一点数据 也是无语
     *
     * @return  List<VoaWord2>
     */
    @Override
    public List<VoaWord2> findDataByVoaId(String bookId, String lessonId) {
        List<VoaWord2> voaWords = new ArrayList<VoaWord2>();
        Cursor cursor = db.rawQuery(
                "select " + VOA_ID + "," + WORD + "," + DEF + "," + AUDIO + "," + PRON + ","
                        + EXAMPLES + "," + ANSWER + "," + POSITION + "," + ID_INDEX + "," + BOOK_ID + ","
                        + UNIT_ID + "," + VIDEO_URL + "," + SENTENCE_CN + "," + PIC_URL + "," + SENTENCE + ","
                        + SENTENCE_AUDIO
                        + " from " + TABLE_NAME
                        + " where " + BOOK_ID + "= '" + bookId + "'and " + UNIT_ID + "=" + lessonId,
                new String[]{});

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            VoaWord2 tempWord = new VoaWord2();
            tempWord.voaId = cursor.getString(0);
            tempWord.word = cursor.getString(1);
            tempWord.def = cursor.getString(2);
            tempWord.audio = cursor.getString(3);
            tempWord.pron = cursor.getString(4);
            tempWord.examples = cursor.getInt(5);
            tempWord.answer = cursor.getString(6);
            tempWord.position = cursor.getInt(7);

            tempWord.idindex = cursor.getString(8);
            tempWord.bookId = cursor.getString(9);
            tempWord.unitId = cursor.getString(10);
            tempWord.videoUrl = cursor.getString(11);
            tempWord.SentenceCn = cursor.getString(12);
            tempWord.picUrl = cursor.getString(13);
            tempWord.Sentence = cursor.getString(14);
            tempWord.SentenceAudio = cursor.getString(15);
            voaWords.add(tempWord);
        }
        return voaWords;
    }

    /**
     * 查找
     *
     * @return List<VoaWord2>
     */
    @Override
    public List<VoaWord2> findDataByBookId(String bookId) {
        List<VoaWord2> voaWords = new ArrayList<VoaWord2>();
        Cursor cursor = db.rawQuery(
                "select " + VOA_ID + "," + WORD + "," + DEF + "," + AUDIO + "," + PRON + ","
                        + EXAMPLES + "," + ANSWER + "," + POSITION + "," + ID_INDEX + "," + BOOK_ID + ","
                        + UNIT_ID + "," + VIDEO_URL + "," + SENTENCE_CN + "," + PIC_URL + "," + SENTENCE + ","
                        + SENTENCE_AUDIO
                        + " from " + TABLE_NAME
                        + " where " + BOOK_ID + "=" + bookId,
                new String[]{});

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            VoaWord2 tempWord = new VoaWord2();
            tempWord.voaId = cursor.getString(0);
            tempWord.word = cursor.getString(1);
            tempWord.def = cursor.getString(2);
            tempWord.audio = cursor.getString(3);
            tempWord.pron = cursor.getString(4);
            tempWord.examples = cursor.getInt(5);
            tempWord.answer = cursor.getString(6);
            tempWord.position = cursor.getInt(7);

            tempWord.idindex = cursor.getString(8);
            tempWord.bookId = cursor.getString(9);
            tempWord.unitId = cursor.getString(10);
            tempWord.videoUrl = cursor.getString(11);
            tempWord.SentenceCn = cursor.getString(12);
            tempWord.picUrl = cursor.getString(13);
            tempWord.Sentence = cursor.getString(14);
            tempWord.SentenceAudio = cursor.getString(15);
            voaWords.add(tempWord);
        }
        cursor.close();
        return voaWords;
    }


    @Override
    public void updateData(VoaWord2 voaWord, String answer) {
        try {
            db.execSQL("update " + TABLE_NAME + " set " + ANSWER + " = '" + answer
                    + "'" + " where " + WORD + " = '" + voaWord.word +
                    "' and " + VOA_ID + " =" + Integer.parseInt(voaWord.voaId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> findVideoList(String bookId) {
        List<String> videos = new ArrayList<>();
        Cursor cursor = db.rawQuery(
                "select " + VIDEO_URL
                        + " from " + TABLE_NAME
                        + " where " + BOOK_ID + "=" + bookId,
                new String[]{});

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String video =cursor.getString(0);
            videos.add(video);
        }
        cursor.close();
        return videos;
    }

    @Override
    public List<SentenceAudio> findSentenceAudios(String bookId) {
        List<SentenceAudio> sentenceAudio = new ArrayList<>();
        Cursor cursor = db.rawQuery(
                "select " + SENTENCE_AUDIO + "," + POSITION + "," + UNIT_ID
                        + " from " + TABLE_NAME
                        + " where " + BOOK_ID + "=" + bookId,
                new String[]{});

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            SentenceAudio audio  =new SentenceAudio();
            audio.mSentenceAudio = cursor.getString(0);
            audio.position = cursor.getString(1);
            audio.unitId = cursor.getString(2);
            sentenceAudio.add(audio);
        }
        cursor.close();
        return sentenceAudio;
    }

    @Override
    public List<VoaWord2> findDataByBookIdAndVoaId(String bookId, String voaId) {
        List<VoaWord2> voaWords = new ArrayList<VoaWord2>();
        Cursor cursor = db.rawQuery(
                "select " + VOA_ID + "," + WORD + "," + DEF + "," + AUDIO + "," + PRON + ","
                        + EXAMPLES + "," + ANSWER + "," + POSITION + "," + ID_INDEX + "," + BOOK_ID + ","
                        + UNIT_ID + "," + VIDEO_URL + "," + SENTENCE_CN + "," + PIC_URL + "," + SENTENCE + ","
                        + SENTENCE_AUDIO
                        + " from " + TABLE_NAME
                        + " where " + BOOK_ID + "= '" + bookId + "'and " + VOA_ID + "=" + voaId,
                new String[]{});

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            VoaWord2 tempWord = new VoaWord2();
            tempWord.voaId = cursor.getString(0);
            tempWord.word = cursor.getString(1);
            tempWord.def = cursor.getString(2);
            tempWord.audio = cursor.getString(3);
            tempWord.pron = cursor.getString(4);
            tempWord.examples = cursor.getInt(5);
            tempWord.answer = cursor.getString(6);
            tempWord.position = cursor.getInt(7);

            tempWord.idindex = cursor.getString(8);
            tempWord.bookId = cursor.getString(9);
            tempWord.unitId = cursor.getString(10);
            tempWord.videoUrl = cursor.getString(11);
            tempWord.SentenceCn = cursor.getString(12);
            tempWord.picUrl = cursor.getString(13);
            tempWord.Sentence = cursor.getString(14);
            tempWord.SentenceAudio = cursor.getString(15);
            voaWords.add(tempWord);
        }
        return voaWords;
    }
}
