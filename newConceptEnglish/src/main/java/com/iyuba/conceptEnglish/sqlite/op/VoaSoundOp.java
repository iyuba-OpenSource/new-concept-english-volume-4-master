package com.iyuba.conceptEnglish.sqlite.op;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.ConceptBookChooseManager;
import com.iyuba.conceptEnglish.sqlite.db.DatabaseService;
import com.iyuba.conceptEnglish.sqlite.mode.VoaSound;
import com.iyuba.core.lil.user.UserInfoManager;

import java.util.ArrayList;


/**
 * 语音评测数据
 */
public class VoaSoundOp extends DatabaseService {
    public static final String TABLE_NAME = "voa_sound_new";
    public static final String TABLE_NAME_BRITISH = "voa_eval_british_new";
    public static final String TABLE_NAME_YOUNTH = "voa_eval_youth";

    //只用作查询数据 匹配旧版本 start
    public static final String TABLE_NAME_OLD = "voa_sound";
    public static final String TABLE_NAME_BRITISH_OLD = "voa_eval_british";
    //只用作查询数据 匹配旧版本 end

    public static final String VOA_ID = "voa_id";
    public static final String WORDSCORE = "wordscore";
    public static final String TOTALSCORE = "totalscore";
    public static final String FILEPATH = "filepath";
    public static final String TIME = "time";
    public static final String ITEMID = "itemid";

    public static final String SOUND_URL = "sound_url";
    public static final String UID = "uid";


    private Context mContext;
    private String currTableName;

    public VoaSoundOp(Context context) {
        super(context);
        mContext = context;
        switch (ConceptBookChooseManager.getInstance().getBookType()){
            case TypeLibrary.BookType.conceptFourUS:
                currTableName = TABLE_NAME;
                break;
            case TypeLibrary.BookType.conceptFourUK:
                currTableName = TABLE_NAME_BRITISH;
                break;
            case TypeLibrary.BookType.conceptJunior:
                currTableName = TABLE_NAME_YOUNTH;
                break;
        }
    }


    /**
     * 单一修改
     *
     * @param wordscore, totalscore,  voaId
     */
    public void updateWordScore(String wordscore, int totalscore, int voaId, String filepath, String time, int itemid, String sound_url) {
        int uid = UserInfoManager.getInstance().getUserId();
        importDatabase.openDatabase().execSQL("insert or replace into " + currTableName + " (" + VOA_ID + "," + WORDSCORE + "," +
                TOTALSCORE + "," + FILEPATH + "," + TIME + "," + ITEMID + "," + SOUND_URL +
                "," + UID + " ) values(?,?,?,?,?,?,?,?)", new Object[]{voaId, wordscore, totalscore, filepath, time, itemid, sound_url, uid});
        closeDatabase(null);
    }

    public void temporaryReplaceReal(){
        Cursor temporaryQuery = importDatabase.openDatabase().query(currTableName, null, UID+"=?", new String[]{"0"}, null, null, null);
        while (temporaryQuery.moveToNext()){
            String wordScore=temporaryQuery.getString(temporaryQuery.getColumnIndex(WORDSCORE));
            int totalScore=temporaryQuery.getInt(temporaryQuery.getColumnIndex(TOTALSCORE));
            int voaId=temporaryQuery.getInt(temporaryQuery.getColumnIndex(VOA_ID));
            String filepath=temporaryQuery.getString(temporaryQuery.getColumnIndex(FILEPATH));
            String time=temporaryQuery.getString(temporaryQuery.getColumnIndex(TIME));
            int itemId=temporaryQuery.getInt(temporaryQuery.getColumnIndex(ITEMID));
            String sound_url=temporaryQuery.getString(temporaryQuery.getColumnIndex(SOUND_URL));
            updateWordScore(wordScore,totalScore,voaId,filepath,time,itemId,sound_url);
        }
        temporaryQuery.close();
    }


    /**
     * 根据临时用户评测句子数量来判断
     * */
    public boolean temporaryUserFull(){
        int count=0;
        Cursor query = importDatabase.openDatabase().query(currTableName, null, UID+"=?", new String[]{"0"}, null, null, null);
        while (query.moveToNext()) {
            count++;
        }
        query.close();
        return count < 3;
    }

    /**
     * 单一修改-- 网络
     *
     * @param
     */
    public void updateWordScoreWeb(VoaSound voaSound,int webVoaId ) {
        if (webVoaId < 5000) {
            currTableName = TABLE_NAME;
        } else {
            if (webVoaId/1000 ==321){
                currTableName = TABLE_NAME_YOUNTH;
            }else {
                currTableName = TABLE_NAME_BRITISH;
            }
        }
        int uid = UserInfoManager.getInstance().getUserId();
        importDatabase.openDatabase().execSQL("insert or replace into " + currTableName + " (" + VOA_ID + "," + WORDSCORE + "," + TOTALSCORE + "," + FILEPATH + "," + TIME + "," + ITEMID + "," + SOUND_URL + "," + UID + " ) values(?,?,?,?,?,?,?,?)",
                new Object[]{voaSound.voa_id, voaSound.wordScore, voaSound.totalScore, voaSound.filepath, voaSound.time, voaSound.itemId, voaSound.sound_url, uid});
        closeDatabase(null);
    }

    /**
     * 根据itemid查询
     */
    public synchronized VoaSound findDataById(int itemid) {
        Cursor cursor = importDatabase.openDatabase().rawQuery("select " + VOA_ID + "," + WORDSCORE + ", " + TOTALSCORE + "," + FILEPATH + "," + TIME + "," + ITEMID + "," + SOUND_URL + " from " + currTableName + " where " + ITEMID + " =? and " + UID + " =?",
                new String[]{String.valueOf(itemid), String.valueOf(UserInfoManager.getInstance().getUserId())});
        if (cursor.moveToNext()) {
            VoaSound voaSound = fillIn(cursor);
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase(null);
            return voaSound;
        }
        if (cursor != null) {
            cursor.close();
        }
        closeDatabase(null);
        return null;
    }

    /**
     * 根据itemid查询 -- 网络
     */
    public synchronized VoaSound findDataByIdWeb(int itemid, int WebvoaId) {
        if (WebvoaId < 5000) {
            currTableName = TABLE_NAME;
        } else {
            if (WebvoaId/1000 ==321){
                currTableName = TABLE_NAME_YOUNTH;
            }else {
                currTableName = TABLE_NAME_BRITISH;
            }
        }

        Cursor cursor = importDatabase.openDatabase().rawQuery("select " + VOA_ID + "," + WORDSCORE + ", " + TOTALSCORE + "," + FILEPATH + "," + TIME + "," + ITEMID + "," + SOUND_URL + " from " + currTableName + " where " + ITEMID + " =? and " + UID + " =?", new String[]{String.valueOf(itemid), String.valueOf(UserInfoManager.getInstance().getUserId())});
        if (cursor.moveToNext()) {
            VoaSound voaSound = fillIn(cursor);
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase(null);
            return voaSound;
        }
        if (cursor != null) {
            cursor.close();
        }
        closeDatabase(null);
        return null;
    }




    /**
     * 根据voaid查询
     */
    public synchronized ArrayList<VoaSound> findDataByvoaId(int voaid) {

        ArrayList<VoaSound> voaSoundArrayList = new ArrayList<>();
        Cursor cursor = importDatabase.openDatabase().rawQuery("select " + VOA_ID + "," + WORDSCORE + ", " + TOTALSCORE + "," + FILEPATH + "," + TIME + "," + ITEMID + "," + SOUND_URL + " from " + currTableName + " where " + VOA_ID + " =? and " + UID + " =?" + " order by " + ITEMID, new String[]{String.valueOf(voaid), String.valueOf(UserInfoManager.getInstance().getUserId())});
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            VoaSound voaSound = new VoaSound();
            voaSound.voa_id = cursor.getInt(cursor.getColumnIndex(VOA_ID));
            voaSound.wordScore = cursor.getString(cursor.getColumnIndex(WORDSCORE));
            voaSound.totalScore = cursor.getInt(cursor.getColumnIndex(TOTALSCORE));
            voaSound.filepath = cursor.getString(cursor.getColumnIndex(FILEPATH));
            voaSound.time = cursor.getString(cursor.getColumnIndex(TIME));
            voaSound.itemId = cursor.getInt(cursor.getColumnIndex(ITEMID));
            voaSound.sound_url = cursor.getString(cursor.getColumnIndex(SOUND_URL));
            voaSoundArrayList.add(voaSound);
        }
        if (cursor != null) {
            cursor.close();
        }
        closeDatabase(null);
        return voaSoundArrayList;
    }

    private VoaSound fillIn(Cursor cursor) {
        VoaSound voaSound = new VoaSound();
        voaSound.voa_id = cursor.getInt(0);
        voaSound.wordScore = cursor.getString(1);
        voaSound.totalScore = cursor.getInt(2);
        voaSound.filepath = cursor.getString(3);
        voaSound.time = cursor.getString(4);
        voaSound.itemId = cursor.getInt(5);
        voaSound.sound_url = cursor.getString(6);
        return voaSound;
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
            if (tableIsExist(importDatabase.openDatabase(),TABLE_NAME)){
                cursor = importDatabase.openDatabase().rawQuery("SELECT * FROM " + TABLE_NAME + " LIMIT 0", null);
                result = cursor != null && cursor.getColumnIndex(SOUND_URL) != -1;
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
            if (tableIsExist(importDatabase.openDatabase(),TABLE_NAME)){
                importDatabase.openDatabase().execSQL("alter table " + TABLE_NAME + " add " + SOUND_URL + " text");
                closeDatabase(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 美音数据 -- 旧的
     */
    public synchronized ArrayList<VoaSound> findDataUS() {

        ArrayList<VoaSound> voaSoundArrayList = new ArrayList<>();
        Cursor cursor = importDatabase.openDatabase().rawQuery("select " + VOA_ID + "," + WORDSCORE + ", " + TOTALSCORE + "," + FILEPATH + "," + TIME + "," + ITEMID + "," + SOUND_URL + " from " + TABLE_NAME_OLD, new String[]{});
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            VoaSound voaSound = new VoaSound();
            voaSound.voa_id = cursor.getInt(0);
            voaSound.wordScore = cursor.getString(1);
            voaSound.totalScore = cursor.getInt(2);
            voaSound.filepath = cursor.getString(3);
            voaSound.time = cursor.getString(4);
            voaSound.itemId = cursor.getInt(5);
            voaSound.sound_url = cursor.getString(6);
            voaSoundArrayList.add(voaSound);
        }
        if (cursor != null) {
            cursor.close();
        }
        closeDatabase(null);
        return voaSoundArrayList;
    }


    /**
     * 英音数据 -- 旧的
     */
    public synchronized ArrayList<VoaSound> findDataUK() {

        ArrayList<VoaSound> voaSoundArrayList = new ArrayList<>();
        Cursor cursor = importDatabase.openDatabase().rawQuery("select " + VOA_ID + "," + WORDSCORE + ", " + TOTALSCORE + "," + FILEPATH + "," + TIME + "," + ITEMID + "," + SOUND_URL + " from " + TABLE_NAME_BRITISH_OLD, new String[]{});
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            VoaSound voaSound = new VoaSound();
            voaSound.voa_id = cursor.getInt(0);
            voaSound.wordScore = cursor.getString(1);
            voaSound.totalScore = cursor.getInt(2);
            voaSound.filepath = cursor.getString(3);
            voaSound.time = cursor.getString(4);
            voaSound.itemId = cursor.getInt(5);
            voaSound.sound_url = cursor.getString(6);
            voaSoundArrayList.add(voaSound);
        }
        if (cursor != null) {
            cursor.close();
        }
        closeDatabase(null);
        return voaSoundArrayList;
    }

    /**
     * 单一修改
     *
     * @param wordscore, totalscore,  voaId
     */
    public void updateUK(String wordscore, int totalscore, int voaId, String filepath, String time, int itemid, String sound_url) {
        int uid = UserInfoManager.getInstance().getUserId();
        importDatabase.openDatabase().execSQL("insert or replace into " + TABLE_NAME_BRITISH + " (" + VOA_ID + "," + WORDSCORE + "," + TOTALSCORE + "," + FILEPATH + "," + TIME + "," + ITEMID + "," + SOUND_URL + "," + UID + " ) values(?,?,?,?,?,?,?,?)", new Object[]{voaId, wordscore, totalscore, filepath, time, itemid, sound_url, uid});
        closeDatabase(null);
    }

    /**
     * 单一修改
     *
     * @param wordscore, totalscore,  voaId
     */
    public void updateUS(String wordscore, int totalscore, int voaId, String filepath, String time, int itemid, String sound_url) {
        int uid = UserInfoManager.getInstance().getUserId();
        importDatabase.openDatabase().execSQL("insert or replace into " + TABLE_NAME + " (" + VOA_ID + "," + WORDSCORE + "," + TOTALSCORE + "," + FILEPATH + "," + TIME + "," + ITEMID + "," + SOUND_URL + "," + UID + " ) values(?,?,?,?,?,?,?,?)", new Object[]{voaId, wordscore, totalscore, filepath, time, itemid, sound_url, uid});
        closeDatabase(null);
    }
    public static boolean tableIsExist(SQLiteDatabase base, String tableName){
        boolean result = false;
        if(tableName == null){
            return false;
        }
        Cursor cursor = null;
        try {
            String sql = "select count(*) as c from Sqlite_master  where type ='table' and name ='"+tableName.trim()+"' ";
            cursor = base.rawQuery(sql, null);
            if(cursor.moveToNext()){
                int count = cursor.getInt(0);
                if(count>0){
                    result = true;
                }
            }

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }finally {
            assert cursor != null;
            cursor.close();
        }
        return result;
    }

    /************************************新的操作*****************************/
    //根据类型判断使用哪个表
    private String gettableName(String lessonType){
        String tableName = TABLE_NAME;
        switch (lessonType){
            case TypeLibrary.BookType.conceptFourUS:
                tableName = TABLE_NAME;
                break;
            case TypeLibrary.BookType.conceptFourUK:
                tableName = TABLE_NAME_BRITISH;
                break;
            case TypeLibrary.BookType.conceptJunior:
                tableName = TABLE_NAME_YOUNTH;
                break;
        }
        return tableName;
    }

    //将数据保存在数据库中
    public void updateWordScoreFromType(String lessonType,String wordscore, int totalscore, int voaId, String filepath, String time, int itemid, String sound_url) {
        String tableName = gettableName(lessonType);

        int uid = UserInfoManager.getInstance().getUserId();
        importDatabase.openDatabase().execSQL("insert or replace into " + tableName + " (" + VOA_ID + "," + WORDSCORE + "," +
                TOTALSCORE + "," + FILEPATH + "," + TIME + "," + ITEMID + "," + SOUND_URL +
                "," + UID + " ) values(?,?,?,?,?,?,?,?)", new Object[]{voaId, wordscore, totalscore, filepath, time, itemid, sound_url, uid});
        closeDatabase(null);
    }

    //将数据从数据库中获取
    public synchronized VoaSound findDataByItemIdAndType(String lessonType,int itemId) {
        String tableName = gettableName(lessonType);

        Cursor cursor = importDatabase.openDatabase().rawQuery("select " + VOA_ID + "," + WORDSCORE + ", " + TOTALSCORE + "," + FILEPATH + "," + TIME + "," + ITEMID + "," + SOUND_URL + " from " + tableName + " where " + ITEMID + " =? and " + UID + " =?",
                new String[]{String.valueOf(itemId), String.valueOf(UserInfoManager.getInstance().getUserId())});
        if (cursor.moveToNext()) {
            VoaSound voaSound = fillIn(cursor);
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase(null);
            return voaSound;
        }
        if (cursor != null) {
            cursor.close();
        }
        closeDatabase(null);
        return null;
    }
}
