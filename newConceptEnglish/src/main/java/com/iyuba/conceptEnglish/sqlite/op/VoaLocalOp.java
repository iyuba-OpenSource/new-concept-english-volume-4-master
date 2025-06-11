package com.iyuba.conceptEnglish.sqlite.op;

import android.content.Context;
import android.database.Cursor;

import com.iyuba.conceptEnglish.sqlite.db.DatabaseService;

public class VoaLocalOp extends DatabaseService {
    private Context mContext;
    public static final String TABLE_NAME = "voa";

    public VoaLocalOp(Context context) {
        super(context);
        mContext = context;
    }


    /**
     * 方法1：检查某表列是否存在
     *
     * @param
     * @return
     */
    public boolean checkSoundUrlExist1(String paraName) {
        boolean result = false;
        Cursor cursor = null;
        try {
            //查询一行
            cursor = importLocalDatabase.openLocalDatabase().rawQuery("SELECT * FROM " + TABLE_NAME + " LIMIT 0", null);
            result = cursor != null && cursor.getColumnIndex(paraName) != -1;
        } catch (Exception e) {

        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return result;
    }

    public void updateTable(String paraName) {
        try {
            importLocalDatabase.openLocalDatabase().execSQL("alter table " + TABLE_NAME + " add " + paraName + " integer");
            closeDatabase(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateTableText(String paraName) {
        try {
            importLocalDatabase.openLocalDatabase().execSQL("alter table " + TABLE_NAME + " add " + paraName + " text");
            closeDatabase(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
