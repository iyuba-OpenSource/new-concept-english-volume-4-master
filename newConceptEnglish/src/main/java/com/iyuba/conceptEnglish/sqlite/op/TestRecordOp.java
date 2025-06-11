package com.iyuba.conceptEnglish.sqlite.op;

import android.content.Context;
import android.database.Cursor;

import com.iyuba.conceptEnglish.sqlite.db.DatabaseService;
import com.iyuba.conceptEnglish.sqlite.mode.TestRecordBean;
import com.iyuba.configation.ConfigManager;
import com.iyuba.core.lil.user.UserInfoManager;

public class TestRecordOp extends DatabaseService {

    public static final String TABLE_NAME = "test_record";
    public static final String UID = "uid";
    public static final String LessonId = "LessonId";
    public static final String BeginTime = "BeginTime";
    public static final String TestNumber = "TestNumber";
    public static final String UserAnswer = "UserAnswer";
    public static final String RightAnswer = "RightAnswer";
    public static final String AnswerResult = "AnswerResult";
    public static final String TestTime = "TestTime";


    public TestRecordOp(Context context) {
        super(context);
    }

    public void updateData(TestRecordBean bean) {

        importDatabase.openDatabase().execSQL("insert or replace into " + TABLE_NAME +
                " (" + LessonId + "," + UID + "," + BeginTime + "," + TestNumber + "," + UserAnswer + "," + RightAnswer + "," + AnswerResult + "," + TestTime +
                " ) values(?,?,?,?,?,?,?,?) ", new Object[]{bean.LessonId, bean.uid, bean.BeginTime, bean.TestNumber, bean.UserAnswer, bean.RightAnswer, bean.AnswerResult, bean.TestTime});
        closeDatabase(null);
    }

    public int getRightNum(int voa_id) {
        int rightNum = 0;
        int uid = UserInfoManager.getInstance().getUserId();
        Cursor cursor = importDatabase.openDatabase().rawQuery("select count(1)" +
                " from " + TABLE_NAME
                + " where " + LessonId + " = " + voa_id + " and " + UID + " = " + uid
                + " and " + AnswerResult + " = 1", new String[]{}
        );
        if (cursor.moveToNext()) {
            rightNum = cursor.getInt(0);
        }
        cursor.close();
        return rightNum;
    }

    public boolean isExits(int voa_id, int testNumber) {
        boolean isExits = false;
        int uid = UserInfoManager.getInstance().getUserId();
        Cursor cursor = importDatabase.openDatabase().rawQuery("select * "   +
                " from " + TABLE_NAME
                + " where " + LessonId + " = " + voa_id + " and " + UID + " = " + uid
                + " and " + TestNumber + " = " + testNumber, new String[]{}
        );
        if (cursor.moveToNext()) {
            isExits = true;
        }
        cursor.close();
        return isExits;
    }

}
