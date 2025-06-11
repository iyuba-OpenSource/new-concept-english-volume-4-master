package com.iyuba.conceptEnglish.sqlite.op;

import android.content.Context;
import android.database.Cursor;

import com.iyuba.conceptEnglish.sqlite.db.DatabaseService;
import com.iyuba.configation.ConfigManager;
import com.iyuba.core.lil.user.UserInfoManager;

public class MultipleRecordOp extends DatabaseService {
    public static final String TABLE_NAME = "multiple_choice_record";
    public static final String UID = "uid";
    public static final String VOA_ID = "voa_id";
    public static final String RIGHT_NUM = "right_num"; //做对题数


    public MultipleRecordOp(Context context) {
        super(context);
    }
    public synchronized void updateRightNum(int voa_id,int rightNum) {
        int uid = UserInfoManager.getInstance().getUserId();
        importDatabase.openDatabase().execSQL("insert or replace into " + TABLE_NAME + " (" + VOA_ID + "," + UID + "," + RIGHT_NUM + " ) values(?,?,?)", new Object[]{voa_id, uid, rightNum});
        closeDatabase(null);
    }
    public synchronized int getRightNum(int voa_id) {
        int rightNum = 0;
        int uid = UserInfoManager.getInstance().getUserId();

        Cursor cursor = importDatabase.openDatabase().rawQuery(
                "select " + RIGHT_NUM
                        + " from " + TABLE_NAME + " where " + UID + " = " + uid + " and " +
                        VOA_ID + " = " + voa_id, new String[]{});

        if (cursor.moveToNext())
            rightNum =  cursor.getInt(0);

        cursor.close();

        closeDatabase(null);

        return rightNum;
    }

}
