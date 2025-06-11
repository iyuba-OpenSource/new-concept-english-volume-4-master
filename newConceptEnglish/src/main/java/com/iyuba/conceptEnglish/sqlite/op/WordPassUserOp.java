package com.iyuba.conceptEnglish.sqlite.op;

import android.content.Context;
import android.database.Cursor;

import com.iyuba.conceptEnglish.sqlite.db.DatabaseService;
import com.iyuba.conceptEnglish.sqlite.mode.WordPassUser;
import com.iyuba.configation.ConfigManager;
import com.iyuba.core.lil.user.UserInfoManager;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class WordPassUserOp extends DatabaseService {

    private static final String TABLE_NAME = "word_pass_user";
    private static final String UID = "uid";
    //存储voaid
    private static final String SPECIAL_VOA_ID = "voa_id";
    private static final String POSITION = "position";
    private static final String WORD = "word";
    private static final String ANSWER = "answer";
    //1是上传失败0是成功
    private static final String IS_UPLOAD = "is_upload";
    //仅青少版的闯关时用到
    public static final String UNITID = "unitId";


    public WordPassUserOp(Context context) {
        super(context);
        if (!checkIsUpLoadExist(TABLE_NAME,IS_UPLOAD,false)){
            insertIsUpLoadColumn();
        }
    }
    public List<WordPassUser> getUploadError(){
        List<WordPassUser> list=new ArrayList<>();
        int uid = UserInfoManager.getInstance().getUserId();
        String sql="select * from "+TABLE_NAME+" where "+UID+"='"+uid+"' and "+IS_UPLOAD+"='1'";
        Cursor cursor=importDatabase.openDatabase().rawQuery(sql,null);
        while (cursor.moveToNext()){
            WordPassUser user = new WordPassUser();
            user.voa_id = cursor.getInt(cursor.getColumnIndex(SPECIAL_VOA_ID));
            user.position = cursor.getInt(cursor.getColumnIndex(POSITION));
            user.word = cursor.getString(cursor.getColumnIndex(WORD));
            user.uid = uid;
            user.answer = cursor.getInt(cursor.getColumnIndex(ANSWER));
            user.unitId = cursor.getInt(cursor.getColumnIndex(UNITID));
            user.is_upload = cursor.getInt(cursor.getColumnIndex(IS_UPLOAD));
            list.add(user);
        }
        cursor.close();
        return list;
    }

    public void updateUpLoadSuccess(int voaId){
        String sql="update "+TABLE_NAME+" set "+IS_UPLOAD+"='0' where "+SPECIAL_VOA_ID+" = "+voaId;
        Cursor cursor = importDatabase.openDatabase().rawQuery(sql, null);
        cursor.close();
    }

    public void updateWordOpLoad(String voaId){
        int uid = UserInfoManager.getInstance().getUserId();
        importDatabase.openDatabase().execSQL("update "+TABLE_NAME +" set "+IS_UPLOAD +"='0' where "+SPECIAL_VOA_ID+"='"+voaId+"' and "+UID+"='"+uid+"'");
    }

    /**
     * 添加错误单词
     *
     * @param isTrue 1正确  0错误
     * @param voa_id
     * @param word
     */
    public void updateWord(int voa_id, String word, int position, int isTrue, String unitId,String isUpLoad) {
        String url="insert or replace into "+TABLE_NAME+" ( "+SPECIAL_VOA_ID+"  ,  "+UID+"  ,  "+WORD+"  ,  "+POSITION+"  ,  "+ANSWER+"  ,  "+UNITID+" )" +
                " values("+voa_id+","+UserInfoManager.getInstance().getUserId()+","+word+","+position+","+isTrue+","+unitId+") ";
        Timber.tag("更新单词----------").d("updateWord: __________%s", url);
        importDatabase.openDatabase().execSQL("insert or replace into " + TABLE_NAME +
                " (" + SPECIAL_VOA_ID + "," + UID + "," + WORD + "," + POSITION + "," + ANSWER + "," + UNITID +","+IS_UPLOAD+
                " ) values(?,?,?,?,?,?,?) ", new Object[]{
                        voa_id,
                UserInfoManager.getInstance().getUserId(),
                word,
                position,
                isTrue,
                unitId,
                isUpLoad
        });
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
                        SPECIAL_VOA_ID + " = " + voa_id + " and " + UID + " = " + UserInfoManager.getInstance().getUserId() + " and " + WORD + " = \"" + word + "\""
                , new Object[]{});
        closeDatabase(null);
    }

    public int getRightNum(int voa_id) {
        int uid = UserInfoManager.getInstance().getUserId();
        Cursor cursor = importDatabase.openDatabase().rawQuery("select count(1) from " + TABLE_NAME + " where " + SPECIAL_VOA_ID + " = " + voa_id + " and " + UID + " =" + uid + " and " + ANSWER + " = 1", new String[]{});
        if (cursor.moveToNext())
            return cursor.getInt(0);
        else
            return 0;
    }

    public int getRightOrWrongCount(boolean flag,String voaId){
        String answer="1";
        String uid = String.valueOf(UserInfoManager.getInstance().getUserId());
        if (!flag){
            answer="0";
        }
        String sql="select * from "+TABLE_NAME+" where "+ANSWER+"='"+answer+"' and "+UID+"='"+uid+"' and "+SPECIAL_VOA_ID+"='"+voaId+"'";
        Cursor firstCursor=importDatabase.openDatabase().rawQuery(sql,null);
        int count=firstCursor.getCount();
        firstCursor.close();
        return count;
    }

    /**
     * @param voa_id
     * @param positon
     * @return 0：错误 1：正确 2：未答题
     */
    public int isError(int voa_id, int positon) {
        int returnCode = 2;
        int uid = UserInfoManager.getInstance().getUserId();
        Cursor cursor = importDatabase.openDatabase().rawQuery("select * from " + TABLE_NAME + " where " + SPECIAL_VOA_ID + " = " + voa_id + " and " + UID + " =" + uid + " and " + POSITION + " = " + positon, new String[]{});
        if (cursor.moveToNext()) {
            returnCode = cursor.getInt(5);
        } else {
            //returncode 不变
        }

        return returnCode;
    }

    public List<WordPassUser> findData(int voaId) {
        int uid = UserInfoManager.getInstance().getUserId();
        List<WordPassUser> errorList = new ArrayList<>();
        Cursor cursor = importDatabase.openDatabase().rawQuery("select * from " + TABLE_NAME +
                " where " + SPECIAL_VOA_ID + " = " + voaId + " and " + UID + " =" + uid, new String[]{});
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            WordPassUser wordError = new WordPassUser();
            wordError.voa_id = cursor.getInt(0);
            wordError.position = cursor.getInt(1);
            wordError.word = cursor.getString(2);
            wordError.uid = uid;
            wordError.answer = cursor.getInt(5);
            wordError.unitId = cursor.getInt(6);
            errorList.add(wordError);
        }
        cursor.close();
        return errorList;
    }


    public void updateTable(String paraName) {
        try {
            importDatabase.openDatabase().execSQL("alter table " + TABLE_NAME + " add " + paraName + " integer");
            closeDatabase(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void insertIsUpLoadColumn(){
        try {
            importDatabase.openDatabase().execSQL("ALTER TABLE "+TABLE_NAME+" ADD "+IS_UPLOAD+" varchar  default 1");
            closeDatabase(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
