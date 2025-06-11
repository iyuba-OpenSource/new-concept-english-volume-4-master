package com.iyuba.conceptEnglish.sqlite.db;

import com.iyuba.conceptEnglish.sqlite.ImportDatabase;
import com.iyuba.conceptEnglish.sqlite.ImportLocalDatabase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 数据库服务
 *
 * @author chentong
 */
public class DatabaseService {
    protected static ImportDatabase importDatabase;
    protected static ImportLocalDatabase importLocalDatabase;

    protected DatabaseService(Context context) {
        importDatabase = new ImportDatabase(context);
        importLocalDatabase = new ImportLocalDatabase(context);
    }

    /**
     * 删除表
     *
     * @param tableName
     */
    public void dropTable(String tableName) {
        //dbOpenHelper.getWritableDatabase().execSQL(
        //		"DROP TABLE IF EXISTS " + tableName);
        importDatabase.openDatabase().execSQL(
                "DROP TABLE IF EXISTS " + tableName);
    }

    /**
     * 关闭数据库
     *
     * @param DatabaseName
     */
    public void closeDatabase(String DatabaseName) {
        //dbOpenHelper.getWritableDatabase().close();
        //importDatabase.closeDatabase();
    }





    /**
     * 删除数据库表数据
     *
     * @param tableName
     * @param id
     */
    public void deleteItemData(String tableName, Integer id) {
        //dbOpenHelper.getWritableDatabase().execSQL(
        //		"delete from " + tableName + " where _id=?",
        //		new Object[] { id });
        //closeDatabase(null);
        importDatabase.openDatabase().execSQL(
                "delete from " + tableName + " where _id=?",
                new Object[]{id});

        importDatabase.closeDatabase();
    }

    /**
     * 删除数据库表数据
     *
     * @param tableName
     * @param ids       ids格式为"","","",""
     */
    public void deleteItemsData(String tableName, String ids) {
        //dbOpenHelper.getWritableDatabase().execSQL(
        //		"delete from " + tableName + " where voaid in (" + ids + ")",
        //		new Object[] {});
        //closeDatabase(null);
        importDatabase.openDatabase().execSQL(
                "delete from " + tableName + " where voaid in (" + ids + ")",
                new Object[]{});

        importDatabase.closeDatabase();
    }

    /**
     * 获取数据库表项数
     *
     * @param tableName
     * @return
     */
    public long getDataCount(String tableName) {
        // cursor = dbOpenHelper.getReadableDatabase().rawQuery(
        ///		"select count(*) from " + tableName, null);
        //cursor.moveToFirst();
        //closeDatabase(null);
        Cursor cursor = importDatabase.openDatabase().rawQuery(
                "select count(*) from " + tableName, null);
        cursor.moveToFirst();

        importDatabase.closeDatabase();

        return cursor.getLong(0);
    }

    /**
     * 关闭数据库服务
     */
    public void close() {
        //dbOpenHelper.close();
        importDatabase.closeDatabase();
    }

    /**
     * **表中是否存在**列
     * */
    protected boolean checkIsUpLoadExist(String table,String column,boolean isLocal) {
        boolean result = false;
        Cursor cursor = null;
        try {
            //查询一行
            SQLiteDatabase database;
            if (isLocal){
                database=importLocalDatabase.openLocalDatabase();
            }else {
                database = importDatabase.openDatabase();
            }
            cursor = database.rawQuery("SELECT * FROM " + table + " LIMIT 0", null);
            result = cursor != null && cursor.getColumnIndex(column) != -1;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return result;
    }

}
