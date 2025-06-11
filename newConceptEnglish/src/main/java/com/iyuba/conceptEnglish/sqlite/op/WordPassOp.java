package com.iyuba.conceptEnglish.sqlite.op;

import android.content.Context;
import android.database.Cursor;

import com.iyuba.conceptEnglish.entity.PassDetail;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.ConceptBookChooseManager;
import com.iyuba.conceptEnglish.sqlite.db.DatabaseService;
import com.iyuba.conceptEnglish.sqlite.db.WordChildDBManager;
import com.iyuba.configation.ConfigManager;
import com.iyuba.core.lil.user.UserInfoManager;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class WordPassOp extends DatabaseService {

    private static final String TABLE_NAME = "word_pass";


    private static final String UID = "uid";
    private static final String VOA_ID = "voa_id";
    private static final String BOOK_ID = "book_id";


    private Context mContext;

    public WordPassOp(Context context) {
        super(context);
        mContext = context;
    }

    /**
     * 初始化全四册的闯关等级，在用户状态改变的时候调用
     *
     */
    public void initWordPassLevel() {
        int pass1 = ConfigManager.Instance().getCurrPassFirst();
        int pass2 = ConfigManager.Instance().getCurrPassSecond();
        int pass3 = ConfigManager.Instance().getCurrPassThird();
        int pass4 = ConfigManager.Instance().getCurrPassFourth();
        importDatabase.openDatabase().execSQL("insert or replace into " + TABLE_NAME + " (" + VOA_ID + "," + UID + "," + BOOK_ID + " ) values( ?,?,? ) ", new Object[]{1000 + pass1, String.valueOf(UserInfoManager.getInstance().getUserId()), 1});
        importDatabase.openDatabase().execSQL("insert or replace into " + TABLE_NAME + " (" + VOA_ID + "," + UID + "," + BOOK_ID + " ) values( ?,?,? ) ", new Object[]{2000 + pass2, String.valueOf(UserInfoManager.getInstance().getUserId()), 2});
        importDatabase.openDatabase().execSQL("insert or replace into " + TABLE_NAME + " (" + VOA_ID + "," + UID + "," + BOOK_ID + " ) values( ?,?,? ) ", new Object[]{3000 + pass3, String.valueOf(UserInfoManager.getInstance().getUserId()), 3});
        importDatabase.openDatabase().execSQL("insert or replace into " + TABLE_NAME + " (" + VOA_ID + "," + UID + "," + BOOK_ID + " ) values( ?,?,? ) ", new Object[]{4000 + pass4, String.valueOf(UserInfoManager.getInstance().getUserId()), 4});
        closeDatabase(null);
    }

    /**
     * 更改闯关等级
     * 与 getCurrPassNum() 是一套的，一个更改一个获取
     *
     * @param voa_id 全四册是voaid 例如1001，青少版  的voaId 是 unitId，例如 1
     * @param book_id 全四册是 1 2 3 4 ，青少版 >= 278
     */
    public void updateVoaId(int voa_id, int book_id) { //
        importDatabase.openDatabase().execSQL("insert or replace into " + TABLE_NAME + " (" + VOA_ID + "," + UID + "," + BOOK_ID + " ) values( ?,?,? ) ",
                new Object[]{voa_id, String.valueOf(UserInfoManager.getInstance().getUserId()), book_id});
        closeDatabase(null);
    }

    /**
     * 获取闯关等级
     * 与 updateVoaId() 是一套的，一个更改，一个获取
     * @param currentBookForLocalPass 个位或者两位数的闯关数，例如 1 代表第一关。
     * @return
     */
    public int getCurrPassNum(int currentBookForLocalPass) {
        int voa_id;
        if (currentBookForLocalPass> 10){
            //青少版
            voa_id = 1;
        }else {
            //全四册
            voa_id = currentBookForLocalPass * 1000 + 1;
        }
        Cursor cursor = importDatabase.openDatabase().rawQuery("select " + VOA_ID + " from " +
                TABLE_NAME + " where " + BOOK_ID + " = " + currentBookForLocalPass + " and " +
                UID + " = ?" + " ORDER BY " + VOA_ID + " ASC", new String[]{String.valueOf(UserInfoManager.getInstance().getUserId())});
        if (cursor == null) {
            Timber.e("WordPassOp: Error: Nothing found in getCurrPassNum!");
            return 0;
        }
        if (cursor.moveToNext()) {
            voa_id = cursor.getInt(0);
        }
        cursor.close();

        //1006
        if (isLevelDone(voa_id)) {
            return voa_id > 1000 ? voa_id % 1000 + 1 : voa_id + 1; //如果是新的返回的应该是 unitID 应该是 1-15
        } else {
            return voa_id > 1000 ? voa_id % 1000 : voa_id;
        }
    }

    /**
     * 获取闯关的 每关详情
     *
     * @param baseVoaid 每个课本中最小的 voaid
     * @param currentPassLevel
     * @return
     */
    public List<PassDetail> getPassDetailList(int baseVoaid, int currentPassLevel) {
        List<PassDetail> list = new ArrayList<>();
        List<Integer> listVoidId = new ArrayList<>();
        for (int i = 0; i < currentPassLevel; i++) {
            listVoidId.add(baseVoaid + i);
        }
        //举例 青少版起始 321001 ，举例 全四册起始 1001
        for (int i = 0; i < listVoidId.size(); i++) {
            Integer voa_id = listVoidId.get(i);

            int wordCount = 0;
            String voaIdStr = String.valueOf(voa_id);
            Timber.d(voaIdStr);
            int checkUnit = Integer.parseInt(voaIdStr.substring(0, 3));

            if (checkUnit == 321) {
//                wordCount = new VoaWordOp(mContext).findDataByVoaId(voa_id).size();

                // TODO: 2023/4/11 青少版的单词数量获取
                wordCount = getChildWordList(i+1);
            } else {
                wordCount = new VoaWordOp(mContext).findDataByVoaId(voa_id).size();
            }
            int rightNum = new WordPassUserOp(mContext).getRightNum(voa_id);
            PassDetail passDetail = new PassDetail();
            passDetail.allCount = wordCount;
            passDetail.rightCount = rightNum;
            list.add(passDetail);
        }
        return list;
    }

    /**
     * 判断上一关是不是闯完了，创完了的话就要跳转到下一关，
     * 所以需要 islevelDone方法来判断一下
     * @param voa_id
     * @return
     */
    private boolean isLevelDone(int voa_id) {
        int wordCount = 0;
        if (voa_id < 1000) {
            return false;
        }

        String voaIdStr = String.valueOf(voa_id);
        Timber.d(voaIdStr);
        int checkUnit = Integer.parseInt(voaIdStr.substring(0, 3));

        if (checkUnit >= 278 && checkUnit <= 289) {
            try {
                wordCount = WordChildDBManager.getInstance().findDataByVoaId(String.valueOf(checkUnit), voaIdStr.substring(3)).size();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            wordCount = new VoaWordOp(mContext).findDataByVoaId(voa_id).size();
        }


        int rightNum = new WordPassUserOp(mContext).getRightNum(voa_id);

        if (wordCount == 0) {
            return false;
        } else {
            return (float) rightNum / wordCount >= 0.8;
        }
    }


    //青少版获取单词的数量
    private int getChildWordList(int position){
        //切换成当前的数据获取方式(青少版使用这种方式，其他的用别的)
        String bookId = String.valueOf(ConfigManager.Instance().getCurrBookforPass());
        int currBookForPass = ConfigManager.Instance().getCurrBookforPass();
        int flag=0;
        if (currBookForPass == 281 || currBookForPass == 283 || currBookForPass == 285) {
            flag=15;
            position = position + flag;
        } else if (currBookForPass == 287 || currBookForPass == 289) {
            flag=24;
            position = position + flag;
        }

        return WordChildDBManager.getInstance().findDataByVoaId(bookId, String.valueOf(position)).size();
    }

}
