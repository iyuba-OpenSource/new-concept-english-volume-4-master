package com.iyuba.conceptEnglish.sqlite.op;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.iyuba.conceptEnglish.lil.concept_other.download.FilePathUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.ConceptBookChooseManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.search.bean.SearchSentenceBean;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ResUtil;
import com.iyuba.conceptEnglish.manager.VoaDataManager;
import com.iyuba.conceptEnglish.sqlite.db.DatabaseService;
import com.iyuba.conceptEnglish.sqlite.mode.Voa;
import com.iyuba.conceptEnglish.sqlite.mode.VoaDetail;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.data.model.VoaText;
import com.iyuba.core.lil.user.UserInfoManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 获取文章内容数据库
 */
public class VoaDetailOp extends DatabaseService {
    String TABLE_NAME = "voa_detail";
    public static final String VOA_ID = "voa_id";
    public static final String PARA_ID = "para_id";
    public static final String INDEX_N = "index_N";
    public static final String START_TIME = "start_time";
    public static final String END_TIME = "end_time";
    public static final String TIMING = "timing";
    public static final String SENTENCE = "sentence";
    public static final String SENTENCE_CN = "sentence_cn";
    public static final String IMG_PATH = "img_path";
    //青少版操作类
    VoaDetailYouthOp mVoaDetailYouthOp;


    public VoaDetailOp(Context context) {
        super(context);
        mVoaDetailYouthOp = new VoaDetailYouthOp(context);
    }


    /**
     * 仅用于 全四册 插入数据
     * <p>
     * 批量插入数据
     */
    public synchronized void saveData(List<VoaDetail> textDetails, boolean isAmerican) {

        if (isAmerican) {
            TABLE_NAME = "voa_detail_american";
        } else {
            TABLE_NAME = "voa_detail";
        }
        if (textDetails != null && textDetails.size() != 0) {
            SQLiteDatabase sqLiteDatabase = importLocalDatabase.openLocalDatabase();
            sqLiteDatabase.beginTransaction();
            for (int i = 0; i < textDetails.size(); i++) {
                VoaDetail textDetail = textDetails.get(i);
                sqLiteDatabase.execSQL(
                        "insert or replace into " + TABLE_NAME + " (" + VOA_ID + ","
                                + PARA_ID + "," + INDEX_N + "," + START_TIME
                                + "," + END_TIME + "," + TIMING + "," + SENTENCE
                                + "," + IMG_PATH + "," + SENTENCE_CN
                                + ") values(?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        new Object[]{textDetail.voaId,
                                Integer.parseInt(textDetail.paraId), Integer.parseInt(textDetail.lineN),
                                textDetail.startTime, textDetail.endTime,
                                textDetail.timing, textDetail.sentence,
                                textDetail.imgPath, textDetail.sentenceCn});

            }
            sqLiteDatabase.setTransactionSuccessful();
            sqLiteDatabase.endTransaction();
            closeDatabase(null);
        }
    }

    public int getLessonParas(int voaId) {
        List<VoaDetail> voaDetails = findDataByVoaId(voaId);
        if (voaDetails == null) {
            return 0;
        }

        return voaDetails.size();
    }


    /**
     * 用于全四册和青少版查询数据
     *
     * @param voaId 例如1002
     * @return
     */
    public List<VoaDetail> findDataByVoaId(int voaId) {
        //这里明显判断错误啊，因为查询的数据不一定是青少版的数据
//        if (MainFragmentActivity.isYouth){
//            //走青少版的逻辑
//            return mVoaDetailYouthOp.getVoaDetailByVoaid(voaId);
//        }
        if (voaId > 300000){
            //走青少版的逻辑
            return mVoaDetailYouthOp.getVoaDetailByVoaid(voaId);
        }
        SQLiteDatabase database = importLocalDatabase.openLocalDatabase();
        if (ConceptBookChooseManager.getInstance().getBookType().equals(TypeLibrary.BookType.conceptFourUS)) {
            TABLE_NAME = "voa_detail_american";
        } else {
            TABLE_NAME = "voa_detail";
        }
        /* 此处似乎存在问题 */
        if (voaId >= 10000) {
            TABLE_NAME = "voa_detail";
            voaId = voaId / 10;
        }
        List<VoaDetail> textDetails = new ArrayList<VoaDetail>();
        String sql="select " + VOA_ID + "," + PARA_ID + ", " + INDEX_N + ", "
                + START_TIME + "," + END_TIME + "," + TIMING + ", "
                + SENTENCE + ", " + IMG_PATH + "," + SENTENCE_CN
                + " from " + TABLE_NAME
                + " where " + VOA_ID + "=? ORDER BY " + START_TIME
                + " ASC";
        Cursor cursor = database.rawQuery(sql, new String[]{String.valueOf(voaId)});

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            VoaDetail textDetail = new VoaDetail();
            textDetail.voaId = cursor.getInt(0);
            textDetail.paraId = cursor.getString(1);
            textDetail.lineN = cursor.getString(2);
            textDetail.startTime = cursor.getDouble(3);
            textDetail.endTime = cursor.getDouble(4);
            textDetail.timing = cursor.getDouble(5);
            textDetail.sentence = cursor.getString(6).replaceFirst("--- ", "");
            textDetail.imgPath = cursor.getString(7);
            textDetail.sentenceCn = cursor.getString(8);

            textDetails.add(textDetail);
        }

        if (cursor != null) {
            cursor.close();
        }

        closeDatabase(null);

        if (textDetails.size() != 0) {
            return textDetails;
        }

        return null;


    }

    //查询全四册和青少版中阅读类型的数据(全四册按照idIndex排序，青少版按照paraId陪许)
    public List<VoaDetail> findConceptSectionData(int voaId){
        if (voaId > 300000){
            //走青少版的逻辑
            return mVoaDetailYouthOp.findConceptJuniorSectionData(voaId);
        }
        SQLiteDatabase database = importLocalDatabase.openLocalDatabase();
        if (ConceptBookChooseManager.getInstance().getBookType().equals(TypeLibrary.BookType.conceptFourUS)) {
            TABLE_NAME = "voa_detail_american";
        } else {
            TABLE_NAME = "voa_detail";
        }
        List<VoaDetail> textDetails = new ArrayList<VoaDetail>();
        String sql="select " + VOA_ID + "," + PARA_ID + ", " + INDEX_N + ", "
                + START_TIME + "," + END_TIME + "," + TIMING + ", "
                + SENTENCE + ", " + IMG_PATH + "," + SENTENCE_CN
                + " from " + TABLE_NAME
                + " where " + VOA_ID + "=? ORDER BY " + INDEX_N
                + " ASC";
        Cursor cursor = database.rawQuery(sql, new String[]{String.valueOf(voaId)});

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            VoaDetail textDetail = new VoaDetail();
            textDetail.voaId = cursor.getInt(0);
            textDetail.paraId = cursor.getString(1);
            textDetail.lineN = cursor.getString(2);
            textDetail.startTime = cursor.getDouble(3);
            textDetail.endTime = cursor.getDouble(4);
            textDetail.timing = cursor.getDouble(5);
            textDetail.sentence = cursor.getString(6).replaceFirst("--- ", "");
            textDetail.imgPath = cursor.getString(7);
            textDetail.sentenceCn = cursor.getString(8);

            textDetails.add(textDetail);
        }

        if (cursor != null) {
            cursor.close();
        }

        closeDatabase(null);

        if (textDetails.size() != 0) {
            return textDetails;
        }

        return null;
    }

    public synchronized Map<Integer, Voa> findData(Map<Integer, Voa> voaMap, String str) {

        Voa tempVoa;
        int voaId;


//        str = str.toLowerCase();

        Cursor cursor = importLocalDatabase.openLocalDatabase().rawQuery(
                "select " + VOA_ID
                        + " from " + TABLE_NAME
                        + " where " + SENTENCE + " like \"%" + str + "%\""
                        + " OR " + SENTENCE_CN + " like \"%" + str + "%\""
                , new String[]{});
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            voaId = cursor.getInt(0);
            tempVoa = voaMap.get(voaId);
            tempVoa.textFind = tempVoa.textFind + 1;
            voaMap.put(voaId, tempVoa);
        }

        if (cursor != null) {
            cursor.close();
        }

        closeDatabase(null);

        return voaMap;
    }


    /**
     * 根据单词查句子
     */
    public List<VoaDetail> findDataByKey(String keyword) {
        SQLiteDatabase database = importLocalDatabase.openLocalDatabase();
        if (ConceptBookChooseManager.getInstance().getBookType().equals(TypeLibrary.BookType.conceptFourUS)) {
            TABLE_NAME = "voa_detail_american";
        } else {
            TABLE_NAME = "voa_detail";
        }
        List<VoaDetail> textDetails = new ArrayList<VoaDetail>();

        Cursor cursor = database.rawQuery(
                "select " + VOA_ID + "," + PARA_ID + ", " + INDEX_N + ", "
                        + START_TIME + "," + END_TIME + "," + TIMING + ", "
                        + SENTENCE + ", " + IMG_PATH + "," + SENTENCE_CN
                        + " from " + TABLE_NAME
                        + " where " + SENTENCE + " like \"%" + keyword + "%\"" + " ORDER BY " + START_TIME
                        + " ASC", new String[]{});
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            VoaDetail textDetail = new VoaDetail();
            textDetail.voaId = cursor.getInt(0);
            textDetail.paraId = cursor.getString(1);
            textDetail.lineN = cursor.getString(2);
            textDetail.startTime = cursor.getDouble(3);
            textDetail.endTime = cursor.getDouble(4);
            textDetail.timing = cursor.getDouble(5);
            textDetail.sentence = cursor.getString(6).replaceFirst("--- ", "");
            textDetail.imgPath = cursor.getString(7);
            textDetail.sentenceCn = cursor.getString(8);

            textDetails.add(textDetail);
        }

        if (cursor != null) {
            cursor.close();
        }

        closeDatabase(null);

        if (textDetails.size() != 0) {
            return textDetails;
        }

        return null;
    }

    //根据单词查询显示的句子（美音、英音、青少版）[新版搜索界面使用]
    public List<SearchSentenceBean> findAllDataByKey(String keyword) {
        List<SearchSentenceBean> showList = new ArrayList<>();

        SQLiteDatabase database = importLocalDatabase.openLocalDatabase();
        //查询英音数据
        List<VoaDetail> ukList = new ArrayList<>();
        String tabUKName = "voa_detail";
        Cursor ukCursor = database.rawQuery(
                "select " + VOA_ID + "," + PARA_ID + ", " + INDEX_N + ", "
                        + START_TIME + "," + END_TIME + "," + TIMING + ", "
                        + SENTENCE + ", " + IMG_PATH + "," + SENTENCE_CN
                        + " from " + tabUKName
                        + " where " + SENTENCE + " like \"%" + keyword + "%\"" + " ORDER BY " + START_TIME
                        + " ASC", new String[]{});
        for (ukCursor.moveToFirst(); !ukCursor.isAfterLast(); ukCursor.moveToNext()) {
            VoaDetail textDetail = new VoaDetail();
            textDetail.voaId = ukCursor.getInt(0);
            textDetail.paraId = ukCursor.getString(1);
            textDetail.lineN = ukCursor.getString(2);
            textDetail.startTime = ukCursor.getDouble(3);
            textDetail.endTime = ukCursor.getDouble(4);
            textDetail.timing = ukCursor.getDouble(5);
            textDetail.sentence = ukCursor.getString(6).replaceFirst("--- ", "");
            textDetail.imgPath = ukCursor.getString(7);
            textDetail.sentenceCn = ukCursor.getString(8);

            ukList.add(textDetail);
        }

        if (ukCursor != null) {
            ukCursor.close();
        }

        //查询美音数据
        List<VoaDetail> usList = new ArrayList<>();
        String tableUSName = "voa_detail_american";
        Cursor usCursor = database.rawQuery(
                "select " + VOA_ID + "," + PARA_ID + ", " + INDEX_N + ", "
                        + START_TIME + "," + END_TIME + "," + TIMING + ", "
                        + SENTENCE + ", " + IMG_PATH + "," + SENTENCE_CN
                        + " from " + tableUSName
                        + " where " + SENTENCE + " like \"%" + keyword + "%\"" + " ORDER BY " + START_TIME
                        + " ASC", new String[]{});
        for (usCursor.moveToFirst(); !usCursor.isAfterLast(); usCursor.moveToNext()) {
            VoaDetail textDetail = new VoaDetail();
            textDetail.voaId = usCursor.getInt(0);
            textDetail.paraId = usCursor.getString(1);
            textDetail.lineN = usCursor.getString(2);
            textDetail.startTime = usCursor.getDouble(3);
            textDetail.endTime = usCursor.getDouble(4);
            textDetail.timing = usCursor.getDouble(5);
            textDetail.sentence = usCursor.getString(6).replaceFirst("--- ", "");
            textDetail.imgPath = usCursor.getString(7);
            textDetail.sentenceCn = usCursor.getString(8);
            usList.add(textDetail);
        }

        if (usCursor != null) {
            usCursor.close();
        }

        //查询青少版数据
        List<VoaDetail> youthList = new ArrayList<>();
        String tableYouthName = "voa_detail_youth";
        Cursor youthCursor = database.rawQuery(
                "SELECT * FROM "+tableYouthName+
                        " WHERE "+VoaDetailYouthOp.TABLE_COLUMN_SENTENCE+ " like \"%" + keyword + "%\"",
                new String[]{});
        if (youthCursor != null && youthCursor.moveToFirst()) {
            do {
                VoaText voaText = VoaDetailYouthOp.fillBean(youthCursor);
                //合并成detail
                VoaDetail detail = new VoaDetail();
                detail.voaId = voaText.getVoaId();
                detail.paraId = String.valueOf(voaText.paraId);
                detail.lineN = String.valueOf(voaText.idIndex);
                detail.startTime = voaText.timing;
                detail.endTime = voaText.endTiming;
                detail.timing = voaText.timing;
                detail.sentence = voaText.sentence;
                detail.sentenceCn = voaText.sentenceCn;
                detail.imgPath = voaText.imgPath;

                youthList.add(detail);
            } while (youthCursor.moveToNext());
        }

        if (youthCursor!=null){
            youthCursor.close();
        }


        //将三个数据合并到一起
        if (ukList!=null&&ukList.size()>0){
            for (int i = 0; i < ukList.size(); i++) {
                VoaDetail detail = ukList.get(i);
                showList.add(new SearchSentenceBean(
                        detail.voaId,
                        detail.paraId,
                        detail.lineN,
                        TypeLibrary.BookType.conceptFourUK,
                        detail.sentence,
                        detail.sentenceCn,
                        getAudioUrl(TypeLibrary.BookType.conceptFourUK,detail.voaId),
                        (long) detail.startTime*1000L,
                        (long) detail.endTime*1000L
                ));
            }
        }

        if (usList!=null&&usList.size()>0){
            for (int i = 0; i < usList.size(); i++) {
                VoaDetail detail = usList.get(i);
                showList.add(new SearchSentenceBean(
                        detail.voaId,
                        detail.paraId,
                        detail.lineN,
                        TypeLibrary.BookType.conceptFourUS,
                        detail.sentence,
                        detail.sentenceCn,
                        getAudioUrl(TypeLibrary.BookType.conceptFourUS,detail.voaId),
                        (long) detail.startTime*1000L,
                        (long) detail.endTime*1000L
                ));
            }
        }

        if (youthList!=null&&youthList.size()>0){
            for (int i = 0; i < youthList.size(); i++) {
                VoaDetail detail = youthList.get(i);
                showList.add(new SearchSentenceBean(
                        detail.voaId,
                        detail.paraId,
                        detail.lineN,
                        TypeLibrary.BookType.conceptJunior,
                        detail.sentence,
                        detail.sentenceCn,
                        getAudioUrl(TypeLibrary.BookType.conceptJunior,detail.voaId),
                        (long) detail.startTime*1000L,
                        (long) detail.endTime*1000L
                ));
            }
        }

        return showList;
    }

    //根据单词查询显示的句子（美音、英音、青少版）[新版搜索界面使用]
    public List<SearchSentenceBean> findAllDataByKeyLimit10(String keyword) {
        List<SearchSentenceBean> showList = new ArrayList<>();

        SQLiteDatabase database = importLocalDatabase.openLocalDatabase();
        //查询英音数据
        List<VoaDetail> ukList = new ArrayList<>();
        String tabUKName = "voa_detail";
        Cursor ukCursor = database.rawQuery(
                "select " + VOA_ID + "," + PARA_ID + ", " + INDEX_N + ", "
                        + START_TIME + "," + END_TIME + "," + TIMING + ", "
                        + SENTENCE + ", " + IMG_PATH + "," + SENTENCE_CN
                        + " from " + tabUKName
                        + " where " + SENTENCE + " like \"%" + keyword + "%\"" + " ORDER BY " + START_TIME
                        + " ASC limit 10", new String[]{});
        for (ukCursor.moveToFirst(); !ukCursor.isAfterLast(); ukCursor.moveToNext()) {
            VoaDetail textDetail = new VoaDetail();
            textDetail.voaId = ukCursor.getInt(0);
            textDetail.paraId = ukCursor.getString(1);
            textDetail.lineN = ukCursor.getString(2);
            textDetail.startTime = ukCursor.getDouble(3);
            textDetail.endTime = ukCursor.getDouble(4);
            textDetail.timing = ukCursor.getDouble(5);
            textDetail.sentence = ukCursor.getString(6).replaceFirst("--- ", "");
            textDetail.imgPath = ukCursor.getString(7);
            textDetail.sentenceCn = ukCursor.getString(8);

            ukList.add(textDetail);
        }

        if (ukCursor != null) {
            ukCursor.close();
        }

        //查询美音数据
        List<VoaDetail> usList = new ArrayList<>();
        String tableUSName = "voa_detail_american";
        Cursor usCursor = database.rawQuery(
                "select " + VOA_ID + "," + PARA_ID + ", " + INDEX_N + ", "
                        + START_TIME + "," + END_TIME + "," + TIMING + ", "
                        + SENTENCE + ", " + IMG_PATH + "," + SENTENCE_CN
                        + " from " + tableUSName
                        + " where " + SENTENCE + " like \"%" + keyword + "%\"" + " ORDER BY " + START_TIME
                        + " ASC limit 10", new String[]{});
        for (usCursor.moveToFirst(); !usCursor.isAfterLast(); usCursor.moveToNext()) {
            VoaDetail textDetail = new VoaDetail();
            textDetail.voaId = usCursor.getInt(0);
            textDetail.paraId = usCursor.getString(1);
            textDetail.lineN = usCursor.getString(2);
            textDetail.startTime = usCursor.getDouble(3);
            textDetail.endTime = usCursor.getDouble(4);
            textDetail.timing = usCursor.getDouble(5);
            textDetail.sentence = usCursor.getString(6).replaceFirst("--- ", "");
            textDetail.imgPath = usCursor.getString(7);
            textDetail.sentenceCn = usCursor.getString(8);
            usList.add(textDetail);
        }

        if (usCursor != null) {
            usCursor.close();
        }

        //查询青少版数据
        List<VoaDetail> youthList = new ArrayList<>();
        String tableYouthName = "voa_detail_youth";
        Cursor youthCursor = database.rawQuery(
                "SELECT * FROM "+tableYouthName+
                        " WHERE "+VoaDetailYouthOp.TABLE_COLUMN_SENTENCE+ " like \"%" + keyword + "%\" limit 10",
                new String[]{});
        if (youthCursor != null && youthCursor.moveToFirst()) {
            do {
                VoaText voaText = VoaDetailYouthOp.fillBean(youthCursor);
                //合并成detail
                VoaDetail detail = new VoaDetail();
                detail.voaId = voaText.getVoaId();
                detail.paraId = String.valueOf(voaText.paraId);
                detail.lineN = String.valueOf(voaText.idIndex);
                detail.startTime = voaText.timing;
                detail.endTime = voaText.endTiming;
                detail.timing = voaText.timing;
                detail.sentence = voaText.sentence;
                detail.sentenceCn = voaText.sentenceCn;
                detail.imgPath = voaText.imgPath;

                youthList.add(detail);
            } while (youthCursor.moveToNext());
        }

        if (youthCursor!=null){
            youthCursor.close();
        }

        //将三个数据合并到一起
        if (ukList!=null&&ukList.size()>0){
            for (int i = 0; i < ukList.size(); i++) {
                VoaDetail detail = ukList.get(i);
                showList.add(new SearchSentenceBean(
                        detail.voaId,
                        detail.paraId,
                        detail.lineN,
                        TypeLibrary.BookType.conceptFourUK,
                        detail.sentence,
                        detail.sentenceCn,
                        getAudioUrl(TypeLibrary.BookType.conceptFourUK,detail.voaId),
                        (long) detail.startTime*1000L,
                        (long) detail.endTime*1000L
                ));
            }
        }

        if (usList!=null&&usList.size()>0){
            for (int i = 0; i < usList.size(); i++) {
                VoaDetail detail = usList.get(i);
                showList.add(new SearchSentenceBean(
                        detail.voaId,
                        detail.paraId,
                        detail.lineN,
                        TypeLibrary.BookType.conceptFourUS,
                        detail.sentence,
                        detail.sentenceCn,
                        getAudioUrl(TypeLibrary.BookType.conceptFourUS,detail.voaId),
                        (long) detail.startTime*1000L,
                        (long) detail.endTime*1000L
                ));
            }
        }

        if (youthList!=null&&youthList.size()>0){
            for (int i = 0; i < youthList.size(); i++) {
                VoaDetail detail = youthList.get(i);
                showList.add(new SearchSentenceBean(
                        detail.voaId,
                        detail.paraId,
                        detail.lineN,
                        TypeLibrary.BookType.conceptJunior,
                        detail.sentence,
                        detail.sentenceCn,
                        getAudioUrl(TypeLibrary.BookType.conceptJunior,detail.voaId),
                        (long) detail.startTime*1000L,
                        (long) detail.endTime*1000L
                ));
            }
        }

        return showList;
    }

    //根据单词查询显示的句子（根据类型确定）[新版搜索界面使用]
    public List<SearchSentenceBean> findAllDataByKeyLimit10(String showType,String keyword) {
        List<SearchSentenceBean> showList = new ArrayList<>();

        SQLiteDatabase database = importLocalDatabase.openLocalDatabase();
        //查询英音数据
        List<VoaDetail> ukList = new ArrayList<>();
        String tabUKName = "voa_detail";
        Cursor ukCursor = database.rawQuery(
                "select " + VOA_ID + "," + PARA_ID + ", " + INDEX_N + ", "
                        + START_TIME + "," + END_TIME + "," + TIMING + ", "
                        + SENTENCE + ", " + IMG_PATH + "," + SENTENCE_CN
                        + " from " + tabUKName
                        + " where " + SENTENCE + " like \"%" + keyword + "%\" limit 10 " + " ORDER BY " + START_TIME
                        + " ASC", new String[]{});
        for (ukCursor.moveToFirst(); !ukCursor.isAfterLast(); ukCursor.moveToNext()) {
            VoaDetail textDetail = new VoaDetail();
            textDetail.voaId = ukCursor.getInt(0);
            textDetail.paraId = ukCursor.getString(1);
            textDetail.lineN = ukCursor.getString(2);
            textDetail.startTime = ukCursor.getDouble(3);
            textDetail.endTime = ukCursor.getDouble(4);
            textDetail.timing = ukCursor.getDouble(5);
            textDetail.sentence = ukCursor.getString(6).replaceFirst("--- ", "");
            textDetail.imgPath = ukCursor.getString(7);
            textDetail.sentenceCn = ukCursor.getString(8);

            ukList.add(textDetail);
        }

        if (ukCursor != null) {
            ukCursor.close();
        }

        //查询美音数据
        List<VoaDetail> usList = new ArrayList<>();
        String tableUSName = "voa_detail_american";
        Cursor usCursor = database.rawQuery(
                "select " + VOA_ID + "," + PARA_ID + ", " + INDEX_N + ", "
                        + START_TIME + "," + END_TIME + "," + TIMING + ", "
                        + SENTENCE + ", " + IMG_PATH + "," + SENTENCE_CN
                        + " from " + tableUSName
                        + " where " + SENTENCE + " like \"%" + keyword + "%\" limit 10 " + " ORDER BY " + START_TIME
                        + " ASC", new String[]{});
        for (usCursor.moveToFirst(); !usCursor.isAfterLast(); usCursor.moveToNext()) {
            VoaDetail textDetail = new VoaDetail();
            textDetail.voaId = usCursor.getInt(0);
            textDetail.paraId = usCursor.getString(1);
            textDetail.lineN = usCursor.getString(2);
            textDetail.startTime = usCursor.getDouble(3);
            textDetail.endTime = usCursor.getDouble(4);
            textDetail.timing = usCursor.getDouble(5);
            textDetail.sentence = usCursor.getString(6).replaceFirst("--- ", "");
            textDetail.imgPath = usCursor.getString(7);
            textDetail.sentenceCn = usCursor.getString(8);
            usList.add(textDetail);
        }

        if (usCursor != null) {
            usCursor.close();
        }

        //查询青少版数据
        List<VoaDetail> youthList = new ArrayList<>();
        String tableYouthName = "voa_detail_youth";
        Cursor youthCursor = database.rawQuery(
                "SELECT * FROM "+tableYouthName+
                        " WHERE "+VoaDetailYouthOp.TABLE_COLUMN_SENTENCE+ " like \"%" + keyword + "%\" limit 10 ",
                new String[]{});
        if (youthCursor != null && youthCursor.moveToFirst()) {
            do {
                VoaText voaText = VoaDetailYouthOp.fillBean(youthCursor);
                //合并成detail
                VoaDetail detail = new VoaDetail();
                detail.voaId = voaText.getVoaId();
                detail.paraId = String.valueOf(voaText.paraId);
                detail.lineN = String.valueOf(voaText.idIndex);
                detail.startTime = voaText.timing;
                detail.endTime = voaText.endTiming;
                detail.timing = voaText.timing;
                detail.sentence = voaText.sentence;
                detail.sentenceCn = voaText.sentenceCn;
                detail.imgPath = voaText.imgPath;

                youthList.add(detail);
            } while (youthCursor.moveToNext());
        }

        if (youthCursor!=null){
            youthCursor.close();
        }


        //将三个数据合并到一起
        if (ukList!=null&&ukList.size()>0){
            for (int i = 0; i < ukList.size(); i++) {
                VoaDetail detail = ukList.get(i);
                showList.add(new SearchSentenceBean(
                        detail.voaId,
                        detail.paraId,
                        detail.lineN,
                        TypeLibrary.BookType.conceptFourUK,
                        detail.sentence,
                        detail.sentenceCn,
                        getAudioUrl(TypeLibrary.BookType.conceptFourUK,detail.voaId),
                        (long) detail.startTime*1000L,
                        (long) detail.endTime*1000L
                ));
            }
        }

        if (usList!=null&&usList.size()>0){
            for (int i = 0; i < usList.size(); i++) {
                VoaDetail detail = usList.get(i);
                showList.add(new SearchSentenceBean(
                        detail.voaId,
                        detail.paraId,
                        detail.lineN,
                        TypeLibrary.BookType.conceptFourUS,
                        detail.sentence,
                        detail.sentenceCn,
                        getAudioUrl(TypeLibrary.BookType.conceptFourUS,detail.voaId),
                        (long) detail.startTime*1000L,
                        (long) detail.endTime*1000L
                ));
            }
        }

        if (youthList!=null&&youthList.size()>0){
            for (int i = 0; i < youthList.size(); i++) {
                VoaDetail detail = youthList.get(i);
                showList.add(new SearchSentenceBean(
                        detail.voaId,
                        detail.paraId,
                        detail.lineN,
                        TypeLibrary.BookType.conceptJunior,
                        detail.sentence,
                        detail.sentenceCn,
                        getAudioUrl(TypeLibrary.BookType.conceptJunior,detail.voaId),
                        (long) detail.startTime*1000L,
                        (long) detail.endTime*1000L
                ));
            }
        }

        return showList;
    }

    /**
     * 根据单词查句子 --- 单词闯关模块
     */
    public List<VoaDetail> findDataByKeyAndId(int index, String voaId) {
        SQLiteDatabase database = importLocalDatabase.openLocalDatabase();
        //单词例句 不区分英音美音， 以英音为准
        TABLE_NAME = "voa_detail";

        List<VoaDetail> textDetails = new ArrayList<VoaDetail>();

        Cursor cursor = database.rawQuery(
                "select " + VOA_ID + "," + PARA_ID + ", " + INDEX_N + ", "
                        + START_TIME + "," + END_TIME + "," + TIMING + ", "
                        + SENTENCE + ", " + IMG_PATH + "," + SENTENCE_CN
                        + " from " + TABLE_NAME
                        + " where " + VOA_ID + " = " + voaId + " and " + INDEX_N + " = " + index + " ORDER BY " + START_TIME
                        + " ASC", new String[]{});
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            VoaDetail textDetail = new VoaDetail();
            textDetail.voaId = cursor.getInt(0);
            textDetail.paraId = cursor.getString(1);
            textDetail.lineN = cursor.getString(2);
            textDetail.startTime = cursor.getDouble(3);
            textDetail.endTime = cursor.getDouble(4);
            textDetail.timing = cursor.getDouble(5);
            textDetail.sentence = cursor.getString(6).replaceFirst("--- ", "");
            textDetail.imgPath = cursor.getString(7);
            textDetail.sentenceCn = cursor.getString(8);
            textDetails.add(textDetail);
        }
        if (cursor != null) {
            cursor.close();
        }
        closeDatabase(null);
        if (textDetails.size() != 0) {
            return textDetails;
        }
        return null;
    }


    /**
     * 根据文章id和句子序号
     */
    public VoaDetail findDataByLineId(int voaid, int index) {

        SQLiteDatabase database = importLocalDatabase.openLocalDatabase();
        if (ConceptBookChooseManager.getInstance().getBookType().equals(TypeLibrary.BookType.conceptFourUS)) {
            TABLE_NAME = "voa_detail_american";
        } else {
            TABLE_NAME = "voa_detail";
        }
        VoaDetail textDetail = new VoaDetail();
        Cursor cursor = database.rawQuery(
                "select " + VOA_ID + "," + PARA_ID + ", " + INDEX_N + ", "
                        + START_TIME + "," + END_TIME + "," + TIMING + ", "
                        + SENTENCE + ", " + IMG_PATH + "," + SENTENCE_CN
                        + " from " + TABLE_NAME
                        + " where " + VOA_ID + "=" + voaid + " and " + INDEX_N + "=" + index, new String[]{});
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();

            textDetail.voaId = cursor.getInt(0);
            textDetail.paraId = cursor.getString(1);
            textDetail.lineN = cursor.getString(2);
            textDetail.startTime = cursor.getDouble(3);
            textDetail.endTime = cursor.getDouble(4);
            textDetail.timing = cursor.getDouble(5);
            textDetail.sentence = cursor.getString(6).replaceFirst("--- ", "");
            textDetail.imgPath = cursor.getString(7);
            textDetail.sentenceCn = cursor.getString(8);
        }

        if (cursor != null) {
            cursor.close();
        }

        closeDatabase(null);

        if (textDetail != null) {
            return textDetail;
        } else {
            return null;
        }


    }
    private void updateSomeOther(float start, float end, String sentence, String sentenceCn,String index){
        //用来更新本地显性sqlLite数据库的一些脏数据
        SQLiteDatabase database = importLocalDatabase.openLocalDatabase();
        ContentValues values = new ContentValues();
        values.put(START_TIME,start);
        values.put(END_TIME,end);
        values.put(SENTENCE,sentence);
        values.put(SENTENCE_CN,sentenceCn);
        String whereClause=INDEX_N+"=? and "+VOA_ID+"=?";
        database.update(TABLE_NAME, values, whereClause, new String[]{index, "1022"});
    }

    public void updateSomeOther(){
        updateSomeOtherChild(51F,57F,"No, not that full one. This empty one.　","不，不是满的那个。是这个空的。","8");
        updateSomeOtherChild(165F,168F,"Give me a box please.　","请给我一个盒子。","14");
        updateSomeOtherChild(44F,48F,"Give me a glass please.　","请给我一个玻璃杯。","6");
        updateSomeOtherChild(24F,27F,"Give me a cup please.","请给我一个茶杯。","2");
        updateSomeOtherChild(65F,67.5F,"Give me a bottle please.　","请给我一个瓶子。","10");

        updateSomeOtherChild(191F,194F,"Give me a tin please.　","请给我一个罐子。","18");
        updateSomeOtherChild(264F,267F,"Give me a fork please.　","请给我一把叉子。","30");
        updateSomeOtherChild(240F,243F,"Give me a spoon please.　","请给我一个匙。","26");
        updateSomeOtherChild(71F,75F,"No, not this large one. That small one.　","不，不是这个大的。是那个小的。","12");
        updateSomeOtherChild(168F,173F,"Which one? That little one?　","哪一个？那个小的吗？","15");

        updateSomeOtherChild(225F,229F,"No, not that blunt one. This sharp one.　","不，不是那把钝的。是这个尖利的。","24");
        updateSomeOtherChild(268F,271F,"Which one? That small one?　","哪一个？那把小的吗？","31");
        updateSomeOtherChild(216F,218F,"Give me a knife please.","请给我一把刀。","22");
        updateSomeOtherChild(173F,178F,"No, not that little one. This big one.　","不，不是那个小的。是这个大的。","16");
    }
    private void updateSomeOtherChild(float start, float end, String sentence, String sentenceCn,String index){
        //用来更新本地显性sqlLite数据库的一些脏数据
        String sql= "update "+TABLE_NAME+" set "+START_TIME+"='"+start+"' ,"+END_TIME+"='"+end+"' ,"+SENTENCE+"='"+sentence+"',"+SENTENCE_CN+"='"+sentenceCn
                +"'where "+INDEX_N+"='"+index+"' and "+VOA_ID+"='1022'";
    }


    /************************************这几个方法仅用于搜索相关的内容处理*********************/
    //这里根据当前voaId和类型处理下相关的音频
    private String getAudioUrl(String lessonType,int voaId){
        String localPath = getLocalSoundPath(lessonType, voaId);
        if (TextUtils.isEmpty(localPath)){
            return getRemoteSoundPath(lessonType, voaId);
        }
        return localPath;
    }

    //获取当前章节的音频本地路径
    private String getLocalSoundPath(String lessonType,int voaId) {
        String localPath = "";

        if (ContextCompat.checkSelfPermission(ResUtil.getInstance().getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            return localPath;
        }

        //更换路径获取方式
        String pathString = FilePathUtil.getHomeAudioPath(voaId,lessonType);
        File file = new File(pathString);
        if (file.exists()){
            localPath = pathString;
        }

        return localPath;
    }

    //获取当前章节的音频网络路径
    private String getRemoteSoundPath(String lessonType,int voaId){
        String soundUrl = null;
        //这里针对会员和非会员不要修改，测试也不要修改
        if (UserInfoManager.getInstance().isVip()){
            soundUrl="http://staticvip2." + Constant.IYUBA_CN + "newconcept/";
        }else {
            soundUrl=Constant.sound;
        }

        switch (lessonType) {
            case TypeLibrary.BookType.conceptFourUS:
            default:
                //美音
                soundUrl = soundUrl
                        + voaId / 1000
                        + "_"
                        + voaId % 1000
                        + Constant.append;
                break;
            case TypeLibrary.BookType.conceptFourUK: //英音
                soundUrl = soundUrl
                        + "british/"
                        + voaId / 1000
                        + "/"
                        + voaId / 1000
                        + "_"
                        + voaId % 1000
                        + Constant.append;
                break;
            case TypeLibrary.BookType.conceptJunior:
                soundUrl = "http://"+Constant.staticStr+Constant.IYUBA_CN+"sounds/voa/sentence/202005/"
                        + voaId
                        + "/"
                        + voaId
                        + Constant.append;
                break;
        }

        return soundUrl;
    }
}
