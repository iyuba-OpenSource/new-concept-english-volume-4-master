package com.iyuba.conceptEnglish.sqlite.op;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.ConceptBookChooseManager;
import com.iyuba.conceptEnglish.manager.VoaDataManager;
import com.iyuba.conceptEnglish.sqlite.compator.VoaCompator;
import com.iyuba.conceptEnglish.sqlite.db.DatabaseService;
import com.iyuba.conceptEnglish.sqlite.mode.Voa;
import com.iyuba.configation.ConfigManager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

/**
 * 获取文章列表数据
 */
public class VoaOp extends DatabaseService {
    public static final String TABLE_NAME = "voa";
    public static final String VOA_ID = "voa_id";
    public static final String TITLE = "title";
    public static final String TITLE_CN = "title_cn";
    public static final String CATEGORY = "category";
    public static final String SOUND = "sound";
    public static final String URL = "url";
    public static final String READ_COUNT = "read_count";
    public static final String IS_COLLECT = "is_collect";
    public static final String IS_READ = "is_read";
    public static final String IS_DOWNLOAD = "is_download";
    public static final String IS_SYNCHRO = "is_synchro";
    public static final String PIC = "pic";

    public static final String VERSION_UK = "version_uk";
    public static final String VERSION_US = "version_us";
    public static final String VERSION_WORD = "version_word";

    /**
     * 跳转微课 时需要用到的章节id
     */
    public static final String CATEGORY_ID = "categoryid";
    /**
     * 每个lesson 所包含的  小节id
     */
    public static final String TITLE_ID = "titleid";
    /**
     * 每个lesson 的总时间
     */
    public static final String TOTAL_TIME = "totalTime";

    public static final String CLICK_READ = "clickRead";

//	public static final String DOWNLOAD_TIME = "download";

    private Context mContext;

    public VoaOp(Context context) {
        super(context);
        mContext = context;
    }


    /**
     * 批量 插入或者更新数据
     */
    public synchronized void insertOrUpdate(List<Voa> voas) {
        importDatabase.openDatabase().beginTransaction();
        for (Voa voa : voas) {
            String sql = "INSERT OR REPLACE INTO voa(voa_id,title,title_cn,category,clickRead,pic)\n" +
                    " VALUES(" + voa.voaId + ",'" + voa.title + "','" + voa.titleCn + "'," + voa.category+ "," + voa.clickRead + ",'"+voa.pic+"')";
            //新版本插入数据
//             String sql = "insert or replace into voa(voa_id, title, desc_cn, desc_jp, title_jp, title_cn, category, sound, url, pic, creat_time, publish_time, read_count, hot_flg, is_read, is_download, is_collect, is_synchro, version_uk, version_us, version_word, categoryid, titleid, totalTime, clickRead)\n"
//                     +" values("+voa.voaId+","+voa.title+",,,,"+voa.titleCn+","+voa.category+","+voa.sound+","+voa.url+","+voa.pic+",,,"+voa.readCount+",,"+voa.isRead+","+voa.isDownload+","+voa.isCollect+","+voa.isSynchro+","+voa.version_uk+","+voa.version_us+","+voa.version_word+","+voa.categoryid+","+voa.titleid+","+voa.totalTime+","+voa.clickRead+")";

            importDatabase.openDatabase().execSQL(sql);
        }

        importDatabase.openDatabase().setTransactionSuccessful();
        importDatabase.openDatabase().endTransaction();
    }

    public synchronized int getMiniVoaidByCategory(int category) {
        String str = "SELECT * FROM " + TABLE_NAME + "\n" +
                "WHERE category = " + category + "\n" +
                "ORDER BY " + VOA_ID + " ASC";

        Cursor cursor = importDatabase.openDatabase().rawQuery(str, null);
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

    public synchronized int getReadVoaNum() {
        Cursor cursor = importDatabase.openDatabase().rawQuery(
                "select count(*) from " + TABLE_NAME + " where " + IS_READ
                        + " = 1", new String[]{});

        cursor.moveToFirst();
        int num = cursor.getInt(0);

        closeDatabase(null);

        if (cursor != null) {
            cursor.close();
        }

        return num;
    }

    public synchronized int getDownloadVoaNum() {
        Cursor cursor = importDatabase.openDatabase().rawQuery(
                "select count(*) from " + TABLE_NAME + " where " + IS_DOWNLOAD
                        + " = 1", new String[]{});

        cursor.moveToFirst();
        int num = cursor.getInt(0);

        closeDatabase(null);

        if (cursor != null) {
            cursor.close();
        }

        return num;
    }

    /**
     * 单一修改 -- 文本版本号
     *
     * @param tempVoa
     */
    public synchronized void updateDataVersion(Voa tempVoa, boolean isAmerican) {

        if (isAmerican) {
            importDatabase.openDatabase().execSQL(
                    "update " + TABLE_NAME + " set " +
                            VERSION_US + "=" + tempVoa.version_us
                            + " where " + VOA_ID + "=" + tempVoa.voaId);
        } else {
            importDatabase.openDatabase().execSQL(
                    "update " + TABLE_NAME + " set " +
                            VERSION_UK + "=" + tempVoa.version_uk
                            + " where " + VOA_ID + "=" + tempVoa.voaId);
        }
        closeDatabase(null);

    }

    //gn更新图片
    public synchronized void updatePic(String voaId,String picUrl) {

        importDatabase.openDatabase().execSQL(
                "update " + TABLE_NAME + " set " +
                        PIC + "= '" + picUrl
                        + "' where " + VOA_ID + "= '" + voaId+"'");
        closeDatabase(null);
    }

    /**
     * 单一修改 -- 单词版本号
     *
     * @param tempVoa
     */
    public synchronized void updateWordVersion(Voa tempVoa) {

        importDatabase.openDatabase().execSQL(
                "update " + TABLE_NAME + " set " +
                        VERSION_WORD + "=" + tempVoa.version_word
                        + " where " + VOA_ID + "=" + tempVoa.voaId);

        closeDatabase(null);

    }


    /**
     * 批量修改
     *
     * @param voas
     */
    public synchronized void updateData(List<Voa> voas) {
        if (voas != null && voas.size() != 0) {
            for (int i = 0; i < voas.size(); i++) {
                Voa tempVoa = voas.get(i);

                //单词处理下title
                if (tempVoa.title.contains("'")){
                    tempVoa.title = tempVoa.title.replace("'","‘");
                }

                importDatabase.openDatabase().execSQL(
                        "update " + TABLE_NAME + " set " + TITLE + "='"
                                + tempVoa.title + "', " + TITLE_CN + "='"
                                + tempVoa.titleCn + "', " + SOUND + "='"
                                + tempVoa.sound + "'," + URL + "='"
                                + tempVoa.url + "'," + IS_DOWNLOAD
                                + "='" + tempVoa.isDownload + "'," + READ_COUNT
                                + "='" + tempVoa.readCount + "' where "
                                + VOA_ID + "=" + tempVoa.voaId);

                closeDatabase(null);
            }
        }
    }


    /**
     * 单一修改
     * IS_READ 0-- 未学习  1-- 听但未完成  2-听完
     *
     * @param
     */
    public void updateIsRead(int voaId, String flag) {

        importDatabase.openDatabase().execSQL(
                "update " + TABLE_NAME + " set " + IS_READ + "=" + flag + " where "

                        + VOA_ID + "=" + voaId);
        closeDatabase(null);
    }

    public void updateReadCount(int voaId) {

        importDatabase.openDatabase().execSQL(
                "update " + TABLE_NAME + " set " + READ_COUNT + "="
                        + READ_COUNT + "+1 where " + VOA_ID + "=" + voaId);
        closeDatabase(null);
    }

    public void updateTitle(int voaId, String title) {

        importDatabase.openDatabase().execSQL(
                "update " + TABLE_NAME + " set " + TITLE + "='"
                        + sqliteEscape(title) + "'" + " where " + VOA_ID + "=" + voaId);
        closeDatabase(null);
    }

    public void updateTitleCn(int voaId, String titleCn) {

        importDatabase.openDatabase().execSQL(
                "update " + TABLE_NAME + " set " + TITLE_CN + "='"
                        + sqliteEscape(titleCn) + "'" + " where " + VOA_ID + "=" + voaId);
        closeDatabase(null);
    }

    /**
     * 更新微课 需要的数据
     * <p>
     * UPDATE voa
     * SET categoryid = 1126,
     * titleid ='12753,12754',
     * totalTime = 1479
     * WHERE voa_id=1001
     */
    public void updateMiacroLessonData(int voaId, int categoryId, String titleId, int totalTime) {

        importDatabase.openDatabase().execSQL("UPDATE " + TABLE_NAME + "\n" +
                "SET " + CATEGORY_ID + " = " + categoryId + ",\n" +
                TITLE_ID + " ='" + titleId + "',\n" +
                TOTAL_TIME + " = " + totalTime + "\n" +
                "WHERE " + VOA_ID + "=" + voaId + "");
        closeDatabase(null);
    }


    /**
     * 查询第bookIndex册的全部数据
     *
     * @return
     */
    public synchronized List<Voa> findDataByBook(int bookIndex) {
        int from = bookIndex * 1000;
        int to = from + 1000;

        List<Voa> voas = new ArrayList<Voa>();

        Cursor cursor = importDatabase.openDatabase().rawQuery(
                "select " + VOA_ID + ", " + TITLE + ", " + TITLE_CN + ", "
                        + SOUND + ", " + URL + ", " + READ_COUNT + ", "
                        + IS_COLLECT + "," + IS_READ + "," + IS_DOWNLOAD + "," + PIC+ "," + CATEGORY_ID
                        + "," + TITLE_ID + "," + TOTAL_TIME + "," + CATEGORY+"," + CLICK_READ
                        + " from " + TABLE_NAME
                        + " where " + VOA_ID + ">'" + from + "' and " + VOA_ID + "<'" + to + "'"
                        + " ORDER BY " + VOA_ID + " ASC", new String[]{});

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            voas.add(fillIn(cursor));
        }

        if (cursor != null) {
            cursor.close();
        }

        closeDatabase(null);

        return voas;
    }


    /**
     * 查询Category的全部数据
     *
     * @return
     */
    public synchronized List<Voa> findDataByCategory(int Category) {

        List<Voa> voas = new ArrayList<Voa>();

        Cursor cursor = importDatabase.openDatabase().rawQuery(
                "select " + VOA_ID + ", " + TITLE + ", " + TITLE_CN + ", "
                        + SOUND + ", " + URL + ", " + READ_COUNT + ", "
                        + IS_COLLECT + "," + IS_READ + "," + IS_DOWNLOAD + "," + PIC+ "," + CATEGORY_ID
                        + "," + TITLE_ID+ "," + TOTAL_TIME + "," + CATEGORY + "," + CLICK_READ
                        + " from " + TABLE_NAME
                        + " where " + CATEGORY + "='" + Category + "' "
                        + " ORDER BY " + VOA_ID + " ASC", new String[]{});

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            voas.add(fillIn(cursor));
        }

        if (cursor != null) {
            cursor.close();
        }

        closeDatabase(null);

        return voas;
    }

    /**
     * 查询数据分页
     *
     * @return
     */
    public synchronized List<Voa> findDataByPage(int curBook, int count, int offset) {

        List<Voa> voas = new ArrayList<Voa>();
        Cursor cursor = null;
        try {
            if (VoaSoundOp.tableIsExist(importDatabase.openDatabase(),TABLE_NAME)){
                cursor = importDatabase.openDatabase().rawQuery(
                        "select " + VOA_ID + "," + TITLE + ", " + TITLE_CN + ", "
                                + SOUND + ", " + CATEGORY + ", " + URL + "," + READ_COUNT + ","
                                + IS_COLLECT + "," + IS_READ + "," + IS_DOWNLOAD + "," + PIC
                                + "," + CATEGORY_ID + "," + TITLE_ID + "," + TOTAL_TIME+ "," + CLICK_READ
                                + " from " + TABLE_NAME
                                + " where " + CATEGORY + " = " + curBook + " Limit " + count + " Offset " + offset,
                        new String[]{});
            }


        } catch (SQLiteException e) {
            //加载数据库时出现的异常
            //设置让 数据库可以更新
            ConfigManager.Instance().putInt("version", 0);
            //退出
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
            return null;
        }

        if (cursor != null){
            if (cursor.getCount()!=0){
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    voas.add(fillInWithMicroData(cursor));
                }
            }
            cursor.close();
        }
            return voas;
//        if (cursor.getCount() == 0) {
//            if (cursor != null) {
//                cursor.close();
//            }
//
//            closeDatabase(null);
//            return voas;
//        } else {
//            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
//                    .moveToNext()) {
//                voas.add(fillInWithMicroData(cursor));
//            }
//
//            if (cursor != null) {
//                cursor.close();
//            }
//
//            closeDatabase(null);
//            return voas;
//        }
    }

    //测试-查询数据


    /**
     * 根据void主键查询
     */
    public synchronized Voa findDataById(int voaId) {

        Cursor cursor = importDatabase.openDatabase().rawQuery(
                "select " + VOA_ID + "," + TITLE + ", " + TITLE_CN + ", "
                        + SOUND + ", " + URL + "," + READ_COUNT + ","
                        + IS_COLLECT + "," + IS_READ + "," + IS_DOWNLOAD + "," + PIC+ "," + CATEGORY_ID
                        + "," + TITLE_ID+ "," + TOTAL_TIME + "," + CATEGORY + "," + CLICK_READ
                        + " from " + TABLE_NAME
                        + " where " + VOA_ID + " =?",
                new String[]{String.valueOf(voaId)});
        if (cursor.moveToNext()) {
            Voa voa = fillIn(cursor);
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase(null);
            return voa;
        }
        if (cursor != null) {
            cursor.close();
        }
        closeDatabase(null);
        return null;
    }

    /**
     * 查询收藏
     */
    public synchronized List<Voa> findDataFromCollection(int curBook) {
        int from = 0;
        int to = 0;

        if (curBook != 0) {
            from = curBook * 1000;
            to = from + 1000;
        } else {
            from = 0;
            to = 5000;
        }

        List<Voa> voas = new ArrayList<Voa>();
        Cursor cursor = importDatabase.openDatabase().rawQuery(
                "select " + VOA_ID + "," + TITLE + ", " + TITLE_CN + ", "
                        + SOUND + ", " + URL + "," + READ_COUNT + ","
                        + IS_COLLECT + "," + IS_READ + "," + IS_DOWNLOAD + "," + PIC+ "," + CATEGORY_ID
                        + "," + TITLE_ID+ "," + TOTAL_TIME + "," + CATEGORY + "," + CLICK_READ
                        + " from " + TABLE_NAME
                        + " where " + VOA_ID + ">'" + from + "' and " + VOA_ID
                        + " <'" + to + "' and " + IS_COLLECT + " ='1'",
                new String[]{});
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            voas.add(fillIn(cursor));
        }
        if (cursor != null) {
            cursor.close();
        }
        closeDatabase(null);
        if (voas.size() != 0) {
            return voas;
        }
        return null;
    }

    /**
     * 查询收藏
     */
    public synchronized List<Voa> findDataFromCollection() {
        List<Voa> voas = new ArrayList<Voa>();
        Cursor cursor = importDatabase.openDatabase().rawQuery(
                "select " + VOA_ID + "," + TITLE + ", " + TITLE_CN + ", "
                        + SOUND + ", " + URL + "," + READ_COUNT + ","
                        + IS_COLLECT + "," + IS_READ + "," + IS_DOWNLOAD + "," + PIC+ "," + CATEGORY_ID
                        + "," + TITLE_ID+ "," + TOTAL_TIME + "," + CATEGORY + "," + CLICK_READ
                        + " from " + TABLE_NAME
                        + " where " + IS_COLLECT + " ='1'", new String[]{});
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            voas.add(fillIn(cursor));
        }
        if (cursor != null) {
            cursor.close();
        }
        closeDatabase(null);

        return voas;
    }

    /**
     * 添加收藏
     */
    public synchronized void insertDataToCollection(int voaId) {

        importDatabase.openDatabase().execSQL(
                "update " + TABLE_NAME + " set " + IS_COLLECT + "='1' where "
                        + VOA_ID + "=" + voaId);
        closeDatabase(null);
    }

    /**
     * 删除收藏
     */
    public synchronized void deleteDataInCollection(int voaId) {

        importDatabase.openDatabase().execSQL(
                "update " + TABLE_NAME + " set " + IS_COLLECT + "='0' where "
                        + VOA_ID + "=" + voaId);
        closeDatabase(null);
    }

    /**
     * 删除收藏
     */
    public synchronized void deleteAllInCollection() {

        importDatabase.openDatabase().execSQL(
                "update " + TABLE_NAME + " set " + IS_COLLECT + "='0'  ");
        closeDatabase(null);
    }

    /**
     * 查询试听
     */
    public synchronized List<Voa> findDataFromRead(int curBook) {
        int from = 0;
        int to = 0;

        if (curBook != 0) {
            from = curBook * 1000;
            to = from + 1000;
        } else {
            from = 0;
            to = 5000;
        }

        List<Voa> voas = new ArrayList<Voa>();
        Cursor cursor = importDatabase.openDatabase().rawQuery(
                "select " + VOA_ID + "," + TITLE + ", " + TITLE_CN + ", "
                        + SOUND + ", " + URL + "," + READ_COUNT + ","
                        + IS_COLLECT + "," + IS_READ + "," + IS_DOWNLOAD + "," + PIC+ "," + CATEGORY_ID
                        + "," + TITLE_ID+ "," + TOTAL_TIME + "," + CATEGORY + "," + CLICK_READ
                        + " from " + TABLE_NAME
                        + " where " + VOA_ID + ">'" + from + "' and " + VOA_ID
                        + " <'" + to + "' and " + IS_READ + " ='1'",
                new String[]{});
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            voas.add(fillIn(cursor));
        }
        if (cursor != null) {
            cursor.close();
        }
        closeDatabase(null);
        if (voas.size() != 0) {
            return voas;
        }
        return null;
    }

    /**
     * 查询试听
     */
    public synchronized List<Voa> findDataFromRead() {
        List<Voa> voas = new ArrayList<Voa>();
        Cursor cursor = importDatabase.openDatabase().rawQuery(
                "select " + VOA_ID + "," + TITLE + ", " + TITLE_CN + ", "
                        + SOUND + ", " + URL + "," + READ_COUNT + ","
                        + IS_COLLECT + "," + IS_READ + "," + IS_DOWNLOAD + "," + PIC+ "," + CATEGORY_ID
                        + "," + TITLE_ID+ "," + TOTAL_TIME + "," + CATEGORY + "," + CLICK_READ
                        + " from " + TABLE_NAME
                        + " where " + IS_READ + " ='1'", new String[]{});
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            voas.add(fillIn(cursor));
        }
        if (cursor != null) {
            cursor.close();
        }
        closeDatabase(null);

        return voas;
    }

    /**
     * 添加试听
     */
    public synchronized void insertDataToRead(int voaId) {

        importDatabase.openDatabase().execSQL(
                "update " + TABLE_NAME + " set " + IS_READ + "='1' where "
                        + VOA_ID + "=" + voaId);
        closeDatabase(null);
    }

    /**
     * 删除试听
     */
    public synchronized void deleteDataInRead(int voaId) {

        importDatabase.openDatabase().execSQL(
                "update " + TABLE_NAME + " set " + IS_READ + "='0' where "
                        + VOA_ID + "=" + voaId);
        closeDatabase(null);
    }

    /**
     * 删除试听
     */
    public synchronized void deleteAllInRead() {

        importDatabase.openDatabase().execSQL(
                "update " + TABLE_NAME + " set " + IS_READ + "='0'  ");
        closeDatabase(null);
    }

    /**
     * 查询下载
     */
    public synchronized List<Voa> findDataFromDownload(int curBook) {
        int from = 0;
        int to = 0;

        if (curBook != 0) {
            from = curBook * 1000;
            to = from + 1000;
        } else {
            from = 0;
            to = 5000;
        }

        List<Voa> voas = new ArrayList<Voa>();
        Cursor cursor = importDatabase.openDatabase().rawQuery(
                "select " + VOA_ID + "," + TITLE + ", " + TITLE_CN + ", "
                        + SOUND + ", " + URL + "," + READ_COUNT + ","
                        + IS_COLLECT + "," + IS_READ + "," + IS_DOWNLOAD + "," + PIC+ "," + CATEGORY_ID
                        + "," + TITLE_ID+ "," + TOTAL_TIME + "," + CATEGORY + "," + CLICK_READ
                        + " from " + TABLE_NAME
                        + " where " + VOA_ID + ">'" + from + "' and " + VOA_ID
                        + " <'" + to + "' and " + IS_DOWNLOAD + " ='1'",
                new String[]{});
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            voas.add(fillIn(cursor));
        }
        if (cursor != null) {
            cursor.close();
        }
        closeDatabase(null);
        if (voas.size() != 0) {
            return voas;
        }
        return null;
    }

    /**
     * 查询下载
     */
    public synchronized List<Voa> findDataFromDownload() {
        List<Voa> voas = new ArrayList<Voa>();
        Cursor cursor = importDatabase.openDatabase().rawQuery(
                "select " + VOA_ID + "," + TITLE + ", " + TITLE_CN + ", "
                        + SOUND + ", " + URL + "," + READ_COUNT + ","
                        + IS_COLLECT + "," + IS_READ + "," + IS_DOWNLOAD + "," + PIC+ "," + CATEGORY_ID
                        + "," + TITLE_ID+ "," + TOTAL_TIME + "," + CATEGORY + "," + CLICK_READ
                        + " from " + TABLE_NAME
                        + " where " + IS_DOWNLOAD + " ='1'", new String[]{});
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            voas.add(fillIn(cursor));
        }
        if (cursor != null) {
            cursor.close();
        }
        closeDatabase(null);

        return voas;
    }

    /**
     * 添加下载
     */
    public synchronized void insertDataToDownload(int voaId) {

        importDatabase.openDatabase().execSQL(
                "update " + TABLE_NAME + " set " + IS_DOWNLOAD + "='1' where "
                        + VOA_ID + "=" + voaId);
        closeDatabase(null);
    }

    /**
     * 删除下载
     */
    public synchronized void deleteDataInDownload(int voaId) {

        importDatabase.openDatabase().execSQL(
                "update " + TABLE_NAME + " set " + IS_DOWNLOAD + "='0' where "
                        + VOA_ID + "=" + voaId);
        closeDatabase(null);
    }

    /**
     * 删除下载
     */
    public synchronized void deleteAllInDownload() {

        importDatabase.openDatabase().execSQL(
                "update " + TABLE_NAME + " set " + IS_DOWNLOAD + "='0'  ");
        closeDatabase(null);
    }

    /**
     * 更新synchro的状态
     */
    public synchronized void updateSynchro(int voaid, int state) {

        importDatabase.openDatabase().execSQL(
                "update " + TABLE_NAME + " set " + IS_SYNCHRO + "=" + state
                        + " where " + VOA_ID + "=" + voaid);
        closeDatabase(null);
    }


    /**
     * 查询未更新到服务器
     *
     * @param
     * @return
     */
    public synchronized List<Voa> findUnSynchroData() {
        List<Voa> voaList = new ArrayList<Voa>();
        Cursor cursor = importDatabase.openDatabase().rawQuery(
                "select " + VOA_ID + "," + TITLE + ", " + TITLE_CN + ", "
                        + SOUND + ", " + URL + "," + READ_COUNT + ","
                        + IS_COLLECT + "," + IS_READ + "," + IS_DOWNLOAD + "," + PIC+ "," + CATEGORY_ID
                        + "," + TITLE_ID+ "," + TOTAL_TIME + "," + CATEGORY + "," + CLICK_READ
                        + " from " + TABLE_NAME
                        + " where " + IS_SYNCHRO + " =0"
                , new String[]{});

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            voaList.add(fillIn(cursor));
        }
        if (cursor != null) {
            cursor.close();
        }
        closeDatabase(null);

        return voaList;
    }

    public synchronized Map<Integer, Voa> findAllData() {

        Map<Integer, Voa> voas = new HashMap<Integer, Voa>();
        Voa tempVoa;
        Cursor cursor = importDatabase.openDatabase().rawQuery(
                "select " + VOA_ID + "," + TITLE + ", " + TITLE_CN + ", "
                        + SOUND + ", " + URL + "," + READ_COUNT + ","
                        + IS_COLLECT + "," + IS_READ + "," + IS_DOWNLOAD + "," + PIC+ "," + CATEGORY_ID
                        + "," + TITLE_ID+ "," + TOTAL_TIME + "," + CATEGORY + "," + CLICK_READ
                        + " from " + TABLE_NAME,
                new String[]{});

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            tempVoa = fillIn(cursor);

            voas.put(tempVoa.voaId, tempVoa);
        }

        if (cursor != null) {
            cursor.close();
        }

        closeDatabase(null);
        if (voas.size() != 0) {
            return voas;
        }

        return null;
    }

    //根据key查询相关的数据
    public synchronized List<Voa> findDataByKey(String key) {

        List<Voa> voaList = new ArrayList<>();
        Cursor cursor = importDatabase.openDatabase().rawQuery(
                "select " + VOA_ID + "," + TITLE + ", " + TITLE_CN + ", "
                        + SOUND + ", " + URL + "," + READ_COUNT + ","
                        + IS_COLLECT + "," + IS_READ + "," + IS_DOWNLOAD + "," + PIC+ "," + CATEGORY_ID
                        + "," + TITLE_ID+ "," + TOTAL_TIME + "," + CATEGORY + "," + CLICK_READ
                        + " from " + TABLE_NAME +" where "+TITLE+" like \"%"+key+"%\""+" OR "+TITLE_CN+" like \"%"+key+"%\"",
                new String[]{});

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Voa tempVoa = fillIn(cursor);
            voaList.add(tempVoa);
        }

        if (cursor != null) {
            cursor.close();
        }

        closeDatabase(null);
        return voaList;
    }

    //根据key查询相关的数据
    public synchronized List<Voa> findDataByKeyLimit10(String key) {

        List<Voa> voaList = new ArrayList<>();
        Cursor cursor = importDatabase.openDatabase().rawQuery(
                "select " + VOA_ID + "," + TITLE + ", " + TITLE_CN + ", "
                        + SOUND + ", " + URL + "," + READ_COUNT + ","
                        + IS_COLLECT + "," + IS_READ + "," + IS_DOWNLOAD + "," + PIC+ "," + CATEGORY_ID
                        + "," + TITLE_ID+ "," + TOTAL_TIME + "," + CATEGORY + "," + CLICK_READ
                        + " from " + TABLE_NAME +" where "+TITLE+" like \"%"+key+"%\""+" OR "+TITLE_CN+" like \"%"+key+"%\" limit 10",
                new String[]{});

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Voa tempVoa = fillIn(cursor);
            voaList.add(tempVoa);
        }

        if (cursor != null) {
            cursor.close();
        }

        closeDatabase(null);
        return voaList;
    }

    /**
     * 查询包含str的课程
     */
    public synchronized Map<Integer, Voa> findData(Map<Integer, Voa> voaMap,
                                                   String str) {

        Map<Integer,Voa> tempMap = new HashMap<>();

        str = str.toLowerCase();
        Cursor cursor = importDatabase.openDatabase().rawQuery(
                "select " + VOA_ID + " from " + TABLE_NAME + " where lower("
                        + TITLE + ") like \"%" + str + "%\"" + " OR " + TITLE_CN
                        + " like \"%" + str + "%\"", new String[]{});

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int voaId = cursor.getInt(0);
            Voa tempVoa = voaMap.get(voaId);
            tempVoa.titleFind = 1;

            tempMap.put(voaId, tempVoa);
        }


        if (cursor != null) {
            cursor.close();
        }
        closeDatabase(null);

        return tempMap;
    }

    public List<Voa> getSearchResult(String str) {
        List<Voa> voaList = new ArrayList<Voa>();
        Map<Integer, Voa> voaMap = findAllData();
        voaMap = findData(voaMap, str);

        if (voaMap==null||voaMap.keySet().size()==0){
            return voaList;
        }

//        VoaDetailOp textDetailOp = new VoaDetailOp(mContext);
//        voaMap = textDetailOp.findData(voaMap, str);

        Collection<Voa> voaCollecton = voaMap.values();
        for (Voa voa : voaCollecton) {
            if (voa.titleFind != 0 || voa.textFind != 0) {
                if (ConceptBookChooseManager.getInstance().getBookType().equals(TypeLibrary.BookType.conceptFourUS)) {
                    voaList.add(voa);
                } else {
                    //if (!(voa.voaId < 2000 && voa.voaId % 2 == 0))
                    voaList.add(voa);
                }
            }
        }

        VoaCompator voaCompator = new VoaCompator();
        Collections.sort(voaList, voaCompator);

        return voaList;

    }

    private Voa fillIn(Cursor cursor) {
        Voa voa = new Voa();
        voa.voaId = cursor.getInt(cursor.getColumnIndex(VOA_ID));
        voa.title = cursor.getString(cursor.getColumnIndex(TITLE));
        voa.titleCn = cursor.getString(cursor.getColumnIndex(TITLE_CN));
        voa.category = cursor.getInt(cursor.getColumnIndex(CATEGORY));
        voa.sound = cursor.getString(cursor.getColumnIndex(SOUND));
        voa.url = cursor.getString(cursor.getColumnIndex(URL));
        voa.readCount = cursor.getString(cursor.getColumnIndex(READ_COUNT));
        voa.isCollect = cursor.getString(cursor.getColumnIndex(IS_COLLECT));
        voa.isRead = cursor.getString(cursor.getColumnIndex(IS_READ));
        voa.isDownload = cursor.getString(cursor.getColumnIndex(IS_DOWNLOAD));
        voa.pic = cursor.getString(cursor.getColumnIndex(PIC));

        voa.categoryid = cursor.getInt(cursor.getColumnIndex(CATEGORY_ID));
        voa.titleid = cursor.getString(cursor.getColumnIndex(TITLE_ID));
        voa.totalTime = cursor.getInt(cursor.getColumnIndex(TOTAL_TIME));
        voa.clickRead = cursor.getString(cursor.getColumnIndex(CLICK_READ));
        return voa;
    }

    private Voa fillInWithMicroData(Cursor cursor) {
        Voa voa = new Voa();
        voa.voaId = cursor.getInt(cursor.getColumnIndex(VOA_ID));
        voa.title = cursor.getString(cursor.getColumnIndex(TITLE));
        voa.titleCn = cursor.getString(cursor.getColumnIndex(TITLE_CN));
        voa.category = cursor.getInt(cursor.getColumnIndex(CATEGORY));
        voa.sound = cursor.getString(cursor.getColumnIndex(SOUND));
        voa.url = cursor.getString(cursor.getColumnIndex(URL));
        voa.readCount = cursor.getString(cursor.getColumnIndex(READ_COUNT));
        voa.isCollect = cursor.getString(cursor.getColumnIndex(IS_COLLECT));
        voa.isRead = cursor.getString(cursor.getColumnIndex(IS_READ));
        voa.isDownload = cursor.getString(cursor.getColumnIndex(IS_DOWNLOAD));
        voa.pic = cursor.getString(cursor.getColumnIndex(PIC));

        voa.categoryid = cursor.getInt(cursor.getColumnIndex(CATEGORY_ID));
        voa.titleid = cursor.getString(cursor.getColumnIndex(TITLE_ID));
        voa.totalTime = cursor.getInt(cursor.getColumnIndex(TOTAL_TIME));
        voa.clickRead = cursor.getString(cursor.getColumnIndex(CLICK_READ));
        return voa;
    }

    public int getCourseVersion(int voa_id, boolean isAmerican) {
        String versionType;
        int version = 0;
        if (isAmerican) {
            versionType = VERSION_US;
        } else {
            versionType = VERSION_UK;
        }
        try {
            Cursor cursor = importDatabase.openDatabase().rawQuery("select " + versionType + " from " + TABLE_NAME + " where " + VOA_ID + " = " + voa_id,
                    null);
            if (cursor.moveToNext()) {
                version = cursor.getInt(0);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return version;
    }

    public int getWordVersion(int voa_id) {
        int version = 0;
        try {
            Cursor cursor = importDatabase.openDatabase().rawQuery("select " + VERSION_WORD + " from " + TABLE_NAME + " where " + VOA_ID + " = " + voa_id,
                    null);
            if (cursor.moveToNext()) {
                version = cursor.getInt(0);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return version;
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
            cursor = importDatabase.openDatabase().rawQuery("SELECT * FROM " + TABLE_NAME + " LIMIT 0", null);
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
            importDatabase.openDatabase().execSQL("alter table " + TABLE_NAME + " add " + paraName + " integer");
            closeDatabase(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateTableText(String paraName) {
        try {
            importDatabase.openDatabase().execSQL("alter table " + TABLE_NAME + " add " + paraName + " text");
            closeDatabase(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void clearColumnDataToNull(String paraName) {
        try {
            importDatabase.openDatabase().execSQL("UPDATE " + TABLE_NAME + " SET " + paraName + " = NULL");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static String sqliteEscape(String keyWord) {
        //keyWord = keyWord.replace("/", "//");
//        keyWord = keyWord.replace("'", "''");
        //替换为下面这个
        keyWord = keyWord.replace("'", "‘");

        keyWord = keyWord.replace("[", "/[");
        keyWord = keyWord.replace("]", "/]");
        keyWord = keyWord.replace("%", "/%");
        keyWord = keyWord.replace("&", "/&");
        keyWord = keyWord.replace("_", "/_");
        keyWord = keyWord.replace("(", "/(");
        keyWord = keyWord.replace(")", "/)");
        return keyWord;
    }

    public boolean findLast(int voaId){
        boolean flag;
        String sql="select * from "+TABLE_NAME+" where "+VOA_ID+" = "+"'"+voaId+"'";
        Cursor cursor = importDatabase.openDatabase().rawQuery(sql, null);
        flag=cursor.getCount()<=0;
        cursor.close();
        return flag;
    }
}
