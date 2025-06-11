package com.iyuba.conceptEnglish.sqlite.op;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.ConceptBookChooseManager;
import com.iyuba.conceptEnglish.manager.VoaDataManager;
import com.iyuba.conceptEnglish.sqlite.db.DatabaseService;
import com.iyuba.conceptEnglish.sqlite.mode.ArticleRecordBean;
import com.iyuba.core.lil.user.UserInfoManager;

/**
 * 文章本地记录操作，用到的类
 */
public class ArticleRecordOp extends DatabaseService {

    public static final String TABLE_NAME = "article_record";
    public static final String UID = "uid";
    public static final String VOA_ID = "voa_id";
    public static final String CURR_TIME = "curr_time";
    public static final String TOTAL_TIME = "total_time";
    public static final String IS_FINISH = "is_finish";
    // 0:美音   1：英音  2：青少版
    public static final String TYPE = "type";
    public static final String PERCENT = "percent";

    public ArticleRecordOp(Context context) {
        super(context);
    }

    public void updateData(ArticleRecordBean bean) {
        int uid = UserInfoManager.getInstance().getUserId();
        importDatabase.openDatabase().execSQL("insert or replace into " + TABLE_NAME +
                " (" + VOA_ID + "," + UID + "," + CURR_TIME + "," + TOTAL_TIME + "," + IS_FINISH + "," + TYPE + "," + PERCENT +
                " ) values(?,?,?,?,?,?,?) ", new Object[]{bean.voa_id, uid, bean.curr_time, bean.total_time, bean.is_finish, getType(), bean.percent});
        closeDatabase(null);
    }

    public void updateDataWeb(ArticleRecordBean bean) {
        int uid = UserInfoManager.getInstance().getUserId();
        importDatabase.openDatabase().execSQL("insert or replace into " + TABLE_NAME +
                " (" + VOA_ID + "," + UID + "," + CURR_TIME + "," + TOTAL_TIME + "," + IS_FINISH + "," + TYPE + "," + PERCENT +
                " ) values(?,?,?,?,?,?,?) ", new Object[]{bean.voa_id, uid, bean.curr_time, bean.total_time, bean.is_finish, bean.type, bean.percent});
        closeDatabase(null);
    }

    public ArticleRecordBean getData(int voa_id) {
        int uid = UserInfoManager.getInstance().getUserId();
        Cursor cursor = importDatabase.openDatabase().rawQuery("select " + VOA_ID + "," + UID + "," + CURR_TIME
                + "," + TOTAL_TIME + "," + IS_FINISH + "," + PERCENT +
                " from " + TABLE_NAME
                + " where " + VOA_ID + " = " + voa_id + " and " + UID + " = " + uid
                + " and " + TYPE + " = " + getType(), new String[]{}
        );
        if (cursor.moveToNext()) {
            ArticleRecordBean bean = new ArticleRecordBean();
            bean.voa_id = cursor.getInt(0);
            bean.uid = cursor.getInt(1);
            bean.curr_time = cursor.getInt(2);
            bean.total_time = cursor.getInt(3);
            bean.is_finish = cursor.getInt(4);
            bean.percent = cursor.getInt(5);
            return bean;
        } else {
            return null;
        }


    }

    public void getDatalin(int voa_id) {
        Cursor cursor = importDatabase.openDatabase().rawQuery("select count(1)" +
                " from " + TABLE_NAME
                + " where " + VOA_ID + " = " + voa_id, new String[]{}
        );
        if (cursor.moveToNext()) {
            Log.e("更新听力记录3", cursor.getInt(0) + "");
        } else {
            Log.e("更新听力记录4", 0 + "");
        }
    }


    public ArticleRecordBean getData(int voa_id, int type) {
        int uid = UserInfoManager.getInstance().getUserId();
        Cursor cursor = importDatabase.openDatabase().rawQuery("select " + VOA_ID + "," + UID + "," + CURR_TIME
                + "," + TOTAL_TIME + "," + IS_FINISH + "," + PERCENT +
                " from " + TABLE_NAME
                + " where " + VOA_ID + " = " + voa_id + " and " + UID + " = " + uid
                + " and " + TYPE + " = " + type, new String[]{}
        );
        if (cursor.moveToNext()) {
            ArticleRecordBean bean = new ArticleRecordBean();
            bean.voa_id = cursor.getInt(0);
            bean.uid = cursor.getInt(1);
            bean.curr_time = cursor.getInt(2);
            bean.total_time = cursor.getInt(3);
            bean.is_finish = cursor.getInt(4);
            bean.percent = cursor.getInt(5);
            cursor.close();
            return bean;
        } else {
            return null;
        }


    }


    public void deleteData() {
        int uid = UserInfoManager.getInstance().getUserId();
        importDatabase.openDatabase().execSQL("delete from " + TABLE_NAME + " where " + UID + " = " + uid);

    }


    private int getType() {
        //0--- 美音  1 --- 英音  2--青少版
//        switch (ConfigManager.Instance().getBookType()){
//            case AMERICA:
//                return 0;
//            case ENGLISH:
//                return 1;
//            case YOUTH:
//                return 2;
//            default:
//                return 0;
//        }
        switch (ConceptBookChooseManager.getInstance().getBookType()){
            case TypeLibrary.BookType.conceptFourUS:
                return 0;
            case TypeLibrary.BookType.conceptFourUK:
                return 1;
            case TypeLibrary.BookType.conceptJunior:
                return 2;
            default:
                return 0;
        }
    }


    /**
     * 方法1：检查某表列是否存在
     *
     * @param
     * @return
     */
    public boolean checkSoundUrlExist1() {
        boolean result = false;
        Cursor cursor = null;
        try {
            //查询一行
            if (VoaSoundOp.tableIsExist(importDatabase.openDatabase(),TABLE_NAME)){
                //为什么这么多样板代码？？？咬牙切齿
                cursor = importDatabase.openDatabase().rawQuery("SELECT * FROM " + TABLE_NAME + " LIMIT 0", null);
                result = cursor != null && cursor.getColumnIndex(PERCENT) != -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return result;
    }

    public void updateTable() {
        try {
            if (VoaSoundOp.tableIsExist(importDatabase.openDatabase(),TABLE_NAME)){
                importDatabase.openDatabase().execSQL("alter table " + TABLE_NAME + " add " + PERCENT + " text");
                closeDatabase(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
