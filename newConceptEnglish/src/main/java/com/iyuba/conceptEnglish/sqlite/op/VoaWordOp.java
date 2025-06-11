package com.iyuba.conceptEnglish.sqlite.op;

import android.content.Context;
import android.database.Cursor;

import com.iyuba.conceptEnglish.sqlite.db.DatabaseService;
import com.iyuba.conceptEnglish.sqlite.mode.VoaWord;
import com.iyuba.core.common.data.model.VoaWord2;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取单词数据库
 */
public class VoaWordOp extends DatabaseService {

    public static final String TABLE_NAME = "voa_word";
    public static final String VOA_ID = "voa_id";
    public static final String WORD = "word";
    public static final String DEF = "def";
    public static final String AUDIO = "audio";
    public static final String PRON = "pron";
    public static final String EXAMPLES = "examples"; //句子序号
    public static final String ANSWER = "answer"; //单词对错
    public static final String POSITION = "position"; //单词序号
    public static final String UNITID = "unitId"; //单元号
    public static final String BOOK_ID = "book_id"; //单元号


    public VoaWordOp(Context context) {
        super(context);
    }

    /**
     * 批量插入数据
     */
    public synchronized void saveData(List<VoaWord> voaWords) {
        if (voaWords != null && voaWords.size() != 0) {
            importLocalDatabase.openLocalDatabase().beginTransaction();
            for (int i = 0; i < voaWords.size(); i++) {
                VoaWord tempword = voaWords.get(i);
                importLocalDatabase.openLocalDatabase().execSQL(
                        "insert or replace into " + TABLE_NAME + " (" + VOA_ID + ","
                                + WORD + "," + DEF + "," + AUDIO
                                + "," + PRON + "," + EXAMPLES + "," + POSITION + "," + UNITID
                                + "," + BOOK_ID + ") values(?,?,?,?,?,?,?,?,?)",
                        new Object[]{tempword.voaId, tempword.word,
                                tempword.def, tempword.audio,
                                tempword.pron, tempword.examples, tempword.position
                                , tempword.unitId, tempword.book_id});
            }
            importLocalDatabase.openLocalDatabase().setTransactionSuccessful();
            importLocalDatabase.openLocalDatabase().endTransaction();
            closeDatabase(null);
        }
    }

    /**
     * 根据voaid 查询 bookid
     */
    public synchronized int getBookidByVoaid(String voaid) {
        int bookId=278;
        String str="SELECT * FROM voa_word\n" +
                "WHERE voa_id = "+voaid;

        Cursor cursor = importLocalDatabase.openLocalDatabase().rawQuery(
                str,
                null);
        if (cursor.moveToFirst()){
            bookId=cursor.getInt(cursor.getColumnIndex(BOOK_ID));
        }
        if (cursor != null) {
            cursor.close();
        }
        closeDatabase(null);
        return bookId;
    }

    /**
     * 根据voaid 查询 unitId
     */
    public synchronized int getUnitIdByVoaid(String voaid) {
        int bookId=278;
        String str="SELECT * FROM voa_word\n" +
                "WHERE voa_id = "+voaid;

        Cursor cursor = importLocalDatabase.openLocalDatabase().rawQuery(
                str,
                null);
        if (cursor.moveToFirst()){
            bookId=cursor.getInt(cursor.getColumnIndex(UNITID));
        }
        if (cursor != null) {
            cursor.close();
        }
        closeDatabase(null);
        return bookId;
    }

    /**
     * 根据 bookid和unitid 查询 voaid
     * 基本上只给青少版使用
     */
    public synchronized int getVoaidByBookIdAndUnit(int bookId,int unitId) {
        int voaid=321001;
        String str="SELECT * FROM voa_word\n" +
                "WHERE "+BOOK_ID+" = "+bookId + " AND "+UNITID+" = "+unitId;

        Cursor cursor = importLocalDatabase.openLocalDatabase().rawQuery(
                str,
                null);
        if (cursor.moveToFirst()){
            voaid=cursor.getInt(cursor.getColumnIndex(VOA_ID));
        }
        if (cursor != null) {
            cursor.close();
        }
        closeDatabase(null);
        return voaid;
    }

    public synchronized int getMiniVoaidByBookid(int bookid) {
        String str = "SELECT * FROM " + TABLE_NAME + "\n" +
                "WHERE "+BOOK_ID+" = " + bookid + "\n" +
                "ORDER BY " + VOA_ID + " ASC";

        Cursor cursor = importLocalDatabase.openLocalDatabase().rawQuery(str, null);
        int num = 321001;
        if (cursor.moveToFirst()) {
            num = cursor.getInt(cursor.getColumnIndex(VOA_ID));
        }

        closeDatabase(null);
        if (cursor != null) {
            cursor.close();
        }
        return num;
    }

    //根据书籍id查找当前的最大数据
    public synchronized int getMaxVoaIdByBookId(int bookId){
        String str = "SELECT * FROM " + TABLE_NAME + "\n" +
                "WHERE "+BOOK_ID+" = " + bookId + "\n" +
                "ORDER BY " + VOA_ID + " ASC";

        Cursor cursor = importLocalDatabase.openLocalDatabase().rawQuery(str, null);
        int num = 321001;
        if (cursor.moveToNext()) {
            num = cursor.getInt(cursor.getColumnIndex(VOA_ID));
        }

        closeDatabase(null);
        if (cursor != null) {
            cursor.close();
        }
        return num;
    }

    /**
     * 查找错误单词
     *
     * @return
     */
    public synchronized List<VoaWord> findDataforError() {
        List<VoaWord> voaWords = new ArrayList<VoaWord>();

        Cursor cursor = importLocalDatabase.openLocalDatabase().rawQuery(
                "select " + VOA_ID + "," + WORD + "," + DEF + ","
                        + AUDIO + "," + PRON + "," + EXAMPLES + "," + ANSWER
                        + " from " + TABLE_NAME
                        + " where " + ANSWER + " = '1'",
                new String[]{});
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            VoaWord tempWord = new VoaWord();
            tempWord.voaId = cursor.getString(0);
            tempWord.word = cursor.getString(1);
            tempWord.def = cursor.getString(2);
            tempWord.audio = cursor.getString(3);
            tempWord.pron = cursor.getString(4);
            tempWord.examples = cursor.getInt(5);
            tempWord.answer = cursor.getString(6);
            voaWords.add(tempWord);
        }

        if (cursor != null) {
            cursor.close();
        }
        closeDatabase(null);
        if (voaWords.size() != 0) {
            return voaWords;
        }
        return null;
    }


    /**
     * 查找
     *
     * @return
     */
    public synchronized List<VoaWord2> findDataByVoaId(int voaId) {
        List<VoaWord2> voaWords = new ArrayList<VoaWord2>();
        Cursor cursor = importLocalDatabase.openLocalDatabase().rawQuery(
                "select " + VOA_ID + "," + WORD + "," + DEF + ","
                        + AUDIO + "," + PRON + "," + EXAMPLES + "," + ANSWER + "," + POSITION
                        + " from " + TABLE_NAME
                        + " where " + VOA_ID + "= '" + voaId + "'",
                new String[]{});

        if (cursor.moveToFirst()){
            for (; !cursor.isAfterLast(); cursor.moveToNext()) {
                VoaWord2 tempWord = new VoaWord2();
                tempWord.voaId = cursor.getString(0);
                tempWord.word = cursor.getString(1);
                tempWord.def = cursor.getString(2);
                tempWord.audio = cursor.getString(3);
                tempWord.pron = cursor.getString(4);
                tempWord.examples = cursor.getInt(5);
                tempWord.answer = cursor.getString(6);
                tempWord.position = cursor.getInt(7);
                voaWords.add(tempWord);
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        closeDatabase(null);

        return voaWords;
    }

    /**
     * 查找
     *
     * @return
     */
    public synchronized List<VoaWord2> findDataByBookId(int startID, int endID) {
        List<VoaWord2> voaWords = new ArrayList<VoaWord2>();

        Cursor cursor = importLocalDatabase.openLocalDatabase().rawQuery(
                "select " + VOA_ID + "," + WORD + "," + DEF + ","
                        + AUDIO + "," + PRON + "," + EXAMPLES + "," + ANSWER + "," + POSITION
                        + " from " + TABLE_NAME
                        + " where " + VOA_ID + "<= '" + endID + "' and " + VOA_ID + ">= '" + startID + "'",
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
            voaWords.add(tempWord);
        }

        if (cursor != null) {
            cursor.close();
        }

        closeDatabase(null);

        if (voaWords.size() != 0) {
            return voaWords;
        }

        return null;
    }

    //获取随机的100个数据
    public synchronized List<VoaWord2> findDataByBookIdRandom100() {
        List<VoaWord2> voaWords = new ArrayList<VoaWord2>();

        Cursor cursor = importLocalDatabase.openLocalDatabase().rawQuery(
                "select " + VOA_ID + "," + WORD + "," + DEF + ","
                        + AUDIO + "," + PRON + "," + EXAMPLES + "," + ANSWER + "," + POSITION
                        + " from " + TABLE_NAME
                        +" order by random() limit 100",
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
            voaWords.add(tempWord);
        }

        if (cursor != null) {
            cursor.close();
        }

        closeDatabase(null);

        if (voaWords.size() != 0) {
            return voaWords;
        }

        return null;
    }


    public synchronized int findWordCount() {
        Cursor cursor = importLocalDatabase.openLocalDatabase().rawQuery(
                "select * " + " from " + TABLE_NAME
                ,
                null);
        cursor.moveToFirst();
        int num = cursor.getCount();
        if (cursor != null) {
            cursor.close();
        }

        closeDatabase(null);
        return num;
    }


    public synchronized List<VoaWord> findDataByVoaIds(List<Integer> wordIndex) {
        List<VoaWord> voaWords = new ArrayList<VoaWord>();
//        Iterator<Integer> iterator=integerHashSet.iterator();
        Cursor cursor = importLocalDatabase.openLocalDatabase().rawQuery(
                "select * " + " from " + TABLE_NAME
                ,
                null);
        cursor.moveToFirst();
        for (int i = 0; i < wordIndex.size(); i++) {
            cursor.moveToPosition(wordIndex.get(i));
            VoaWord tempWord = new VoaWord();
            tempWord.voaId = cursor.getString(0);
            tempWord.word = cursor.getString(1);
            tempWord.def = cursor.getString(2);
            tempWord.audio = cursor.getString(3);
            tempWord.pron = cursor.getString(4);
            tempWord.examples = cursor.getInt(5);
            voaWords.add(tempWord);
        }

        if (cursor != null) {
            cursor.close();
        }

        closeDatabase(null);

        if (voaWords.size() != 0) {
            return voaWords;
        }

        return null;
    }


    public void updateData(VoaWord voaWord, String anwser) {
        try {
            importLocalDatabase.openLocalDatabase().execSQL(
                    "update " + TABLE_NAME
                            + " set " + ANSWER + " = '" + anwser
                            + "'" + " where " + WORD + " = '" + voaWord.word +
                            "' and " + VOA_ID + " =" + Integer.parseInt(voaWord.voaId));
            closeDatabase(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateTable(String paraName) {
        try {
            importLocalDatabase.openLocalDatabase().execSQL("alter table " + TABLE_NAME + " add " + paraName + " integer");
            closeDatabase(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateTableNew(String column){
        if (!checkIsUpLoadExist(TABLE_NAME,column,true)){
            updateTable(column);
        }
    }
}
