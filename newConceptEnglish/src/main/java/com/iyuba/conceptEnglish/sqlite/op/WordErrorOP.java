package com.iyuba.conceptEnglish.sqlite.op;

import android.content.Context;
import android.database.Cursor;

import com.iyuba.conceptEnglish.sqlite.db.DatabaseService;
import com.iyuba.configation.ConfigManager;
import com.iyuba.core.lil.user.UserInfoManager;

public class WordErrorOP extends DatabaseService {

    private static final String TABLE_NAME = "word_error";
    private static final String UID = "uid";
    private static final String VOA_ID = "voa_id";
    private static final String WORD = "word";


    private Context mContext;
    private VoaWordOp voaWordOp;

    public WordErrorOP(Context context) {
        super(context);
        mContext = context;
    }

//    public void updateData() {
//        voaWordOp = new VoaWordOp(mContext);
//        List<VoaWord> words = voaWordOp.findDataforError();
//        if (words != null && words.size() > 0) {
//            for (VoaWord word : words) {
//                importDatabase.openDatabase().execSQL("insert or replace into " + TABLE_NAME +
//                        " (" + VOA_ID + "," + UID + "," + WORD +
//                        " ) values(?,?,?) ", new Object[]{Integer.parseInt(word.voaId), ConfigManager.Instance().getUserId(), word.word});
//            }
//        }
//        closeDatabase(null);
//
//    }

    /**
     * 添加错误单词
     *
     * @param voa_id
     * @param word
     */
    public void updateWord(int voa_id, String word) {
        importDatabase.openDatabase().execSQL("insert or replace into " + TABLE_NAME +
                " (" + VOA_ID + "," + UID + "," + WORD +
                " ) values(?,?,?) ", new Object[]{voa_id, String.valueOf(UserInfoManager.getInstance().getUserId()), word});
        closeDatabase(null);
    }

    /**
     * 删除错误单词
     *
     * @param voa_id
     * @param word
     */
    public void deleteWord(int voa_id, String word) {
        importDatabase.openDatabase().execSQL("delete from " + TABLE_NAME + " where " +
                        VOA_ID + " = " + voa_id + " and " + UID + " = " + UserInfoManager.getInstance().getUserId() + " and " + WORD + " = \"" + word + "\""
                , new Object[]{});
        closeDatabase(null);
    }

    public int getWordNum(int voa_id) {
        int uid = UserInfoManager.getInstance().getUserId();
        Cursor cursor = importDatabase.openDatabase().rawQuery("select count(1) from " + TABLE_NAME + " where " + VOA_ID + " = " + voa_id + " and " + UID + " =" + uid, new String[]{});
        if (cursor.moveToNext())
            return cursor.getInt(0);
        else
            return 0;
    }

    public boolean isError(int voa_id, String word) {
        int uid = UserInfoManager.getInstance().getUserId();
//        Cursor cursor = importDatabase.openDatabase().rawQuery("select * from " + TABLE_NAME +
//                " where " + VOA_ID + " = " + voa_id + " and " + UID + " =" + uid + " and "
//                + WORD + " = \"" + word + "\"", new String[]{});
        Cursor cursor = importDatabase.openDatabase().rawQuery("select * from " + TABLE_NAME +
                " where " + VOA_ID + " = " + voa_id + " and " + UID + " =? and "
                + WORD + " = ?", new String[]{String.valueOf(uid),word});
        if (cursor.moveToNext())
            return true;
        else
            return false;
    }
}
