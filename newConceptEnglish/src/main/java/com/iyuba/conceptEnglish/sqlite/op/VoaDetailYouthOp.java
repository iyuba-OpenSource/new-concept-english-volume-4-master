package com.iyuba.conceptEnglish.sqlite.op;

import android.content.Context;
import android.database.Cursor;

import com.iyuba.conceptEnglish.sqlite.db.DatabaseService;
import com.iyuba.conceptEnglish.sqlite.mode.VoaDetail;
import com.iyuba.core.common.data.model.VoaText;
import com.iyuba.core.common.data.model.VoaTextYouthByBook;

import java.util.ArrayList;
import java.util.List;

public class VoaDetailYouthOp extends DatabaseService {
    public static final String TABLE_NAME = "voa_detail_youth";

    public static final String TABLE_COLUMN_VOAID = "voaId";
    public static final String TABLE_COLUMN_PARAID = "ParaId";
    public static final String TABLE_COLUMN_IMGPATH = "ImgPath";
    public static final String TABLE_COLUMN_ENDTIMIG = "EndTiming";
    public static final String TABLE_COLUMN_IDINDEX = "IdIndex";
    public static final String TABLE_COLUMN_SENTENCE_CN = "sentence_cn";
    public static final String TABLE_COLUMN_IMGWORDS = "ImgWords";
    public static final String TABLE_COLUMN_TIMING = "Timing";
    public static final String TABLE_COLUMN_SENTENCE = "Sentence";


    public VoaDetailYouthOp(Context context) {
        super(context);
    }


    /**
     * 如果不存在的话 创建表
     */
    public synchronized void createTable() {
        String str = "CREATE TABLE  if not exists " + TABLE_NAME + " (\n" +
                "  " + TABLE_COLUMN_VOAID + " INTEGER NOT NULL,\n" +
                "  " + TABLE_COLUMN_PARAID + " INTEGER NOT NULL,\n" +
                "  " + TABLE_COLUMN_IMGPATH + " TEXT,\n" +
                "  " + TABLE_COLUMN_ENDTIMIG + " text,\n" +
                "  " + TABLE_COLUMN_IDINDEX + " integer,\n" +
                "  " + TABLE_COLUMN_SENTENCE_CN + " TEXT,\n" +
                "  " + TABLE_COLUMN_IMGWORDS + " TEXT,\n" +
                "  " + TABLE_COLUMN_TIMING + " TEXT,\n" +
                "  " + TABLE_COLUMN_SENTENCE + " TEXT,\n" +
                "  PRIMARY KEY (" + TABLE_COLUMN_VOAID + ", " + TABLE_COLUMN_PARAID + ")\n" +
                ");";
        importLocalDatabase.openLocalDatabase().execSQL(str);
    }

    /**
     * INSERT or replace into voa_detail_youth (voaId,ParaId,ImgPath,EndTiming,IdIndex,sentence_cn,ImgWords,Timing,sentence)
     * VALUES(2,2,'1','1',1,'1','1','1','1')
     */
    public synchronized void insertOrReplaceData(int voaid, List<VoaText> voaTexts) {
        if (voaTexts == null) {
            return;
        }
        importLocalDatabase.openLocalDatabase().beginTransaction();
        for (VoaText text : voaTexts) {
            text.sentenceCn = text.sentenceCn.replaceAll("'", "’");
            text.sentence = text.sentence.replaceAll("'", "’");
            String str = "INSERT or replace into " + TABLE_NAME + " (" + TABLE_COLUMN_VOAID + "," + TABLE_COLUMN_PARAID
                    + "," + TABLE_COLUMN_IMGPATH + "," + TABLE_COLUMN_ENDTIMIG + "," + TABLE_COLUMN_IDINDEX
                    + "," + TABLE_COLUMN_SENTENCE_CN + "," + TABLE_COLUMN_IMGWORDS + "," + TABLE_COLUMN_TIMING + "," + TABLE_COLUMN_SENTENCE + ")\n" +
                    "VALUES(" + voaid + "," + text.paraId + ",'" + text.imgPath + "','" + text.endTiming + "'," + text.idIndex
                    + ",'" + text.sentenceCn + "','" + text.imgWords + "','" + text.timing + "','" + text.sentence + "')";
            importLocalDatabase.openLocalDatabase().execSQL(str);
        }
        importLocalDatabase.openLocalDatabase().setTransactionSuccessful();
        importLocalDatabase.openLocalDatabase().endTransaction();
    }


    /**
     * INSERT or replace into voa_detail_youth (voaId,ParaId,ImgPath,EndTiming,IdIndex,sentence_cn,ImgWords,Timing,sentence)
     * VALUES(2,2,'1','1',1,'1','1','1','1')
     */
    public synchronized void insertOrReplaceDataBySeries( List<VoaTextYouthByBook> voaTexts) {
        if (voaTexts == null) {
            return;
        }
        importLocalDatabase.openLocalDatabase().beginTransaction();
        try{
            for (VoaTextYouthByBook text : voaTexts) {
                text.sentenceCn = text.sentenceCn.replaceAll("'", "’");
                text.sentence = text.sentence.replaceAll("'", "’");
                String str = "INSERT or replace into " + TABLE_NAME + " (" + TABLE_COLUMN_VOAID + "," + TABLE_COLUMN_PARAID
                        + "," + TABLE_COLUMN_IMGPATH + "," + TABLE_COLUMN_ENDTIMIG + "," + TABLE_COLUMN_IDINDEX
                        + "," + TABLE_COLUMN_SENTENCE_CN + "," + TABLE_COLUMN_IMGWORDS + "," + TABLE_COLUMN_TIMING + "," + TABLE_COLUMN_SENTENCE + ")\n" +
                        "VALUES(" + text.getVoaId() + "," + text.paraId + ",'" + text.imgPath + "','" + text.endTiming + "'," + text.idIndex
                        + ",'" + text.sentenceCn + "','" + text.imgWords + "','" + text.timing + "','" + text.sentence + "')";
                importLocalDatabase.openLocalDatabase().execSQL(str);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        importLocalDatabase.openLocalDatabase().setTransactionSuccessful();
        importLocalDatabase.openLocalDatabase().endTransaction();
    }

    /**
     * 获取voadetail 类型的 voa课本数据， voadetail是青少版 用于跟全四册匹配
     *
     * @param voaId
     * @return
     */
    public synchronized List<VoaDetail> getVoaDetailByVoaid(int voaId) {
        return voaTextTranslateToVoaDetail(voaId,getDataByVoaid(voaId));
    }

    public List<VoaDetail> voaTextTranslateToVoaDetail(int voaId,List<VoaText> voaTexts) {
        List<VoaDetail> timpList = new ArrayList<>();
        for (int i = 0; i < voaTexts.size(); i++) {
            VoaText text = voaTexts.get(i);

            VoaDetail timp = new VoaDetail();
            timp.voaId = voaId;
            timp.paraId = text.paraId + "";
            //这里全四册的 linen 跟青少版的 paraid 可以对应
//            timp.lineN = text.paraId + "";
            // TODO: 2023/11/17 这里修改为idindex,如果不对，重新修改回来
            timp.lineN = text.idIndex + "";
            if (i == 0) {
                //第一句
                timp.startTime = 0;
            } else {
                timp.startTime = voaTexts.get(i - 1).endTiming;
            }
            timp.endTime = text.endTiming;
            timp.timing = text.timing;
            timp.sentence = text.sentence;
            timp.imgPath = text.imgPath;
            timp.sentenceCn = text.sentenceCn;
            timpList.add(timp);
        }
        return timpList;
    }

    public synchronized List<VoaText> getDataByVoaid(int voaId) {
        List<VoaText> list = new ArrayList<>();
        String str = "SELECT * FROM voa_detail_youth\n" +
                "WHERE voaId=" + voaId;
        Cursor cursor = importLocalDatabase.openLocalDatabase().rawQuery(str, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(fillBean(cursor));
            } while (cursor.moveToNext());
        }

        return list;
    }

    /***********************阅读界面专用*****************/
    public synchronized List<VoaDetail> findConceptJuniorSectionData(int voaId){
        return voaTextTranslateToVoaDetail(voaId,getSectionDataByVoaId(voaId));
    }

    //青少版根据paraId进行排序显示
    public synchronized List<VoaText> getSectionDataByVoaId(int voaId){
        List<VoaText> list = new ArrayList<>();
        String str = "SELECT * FROM voa_detail_youth\n" +
                "WHERE voaId=" + voaId +" ORDER BY "+TABLE_COLUMN_PARAID+" ASC";
        Cursor cursor = importLocalDatabase.openLocalDatabase().rawQuery(str, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(fillBean(cursor));
            } while (cursor.moveToNext());
        }

        return list;
    }


    public static VoaText fillBean(Cursor cursor) {
        VoaText text = new VoaText();
        text.paraId = cursor.getInt(cursor.getColumnIndex(TABLE_COLUMN_PARAID));
        text.imgPath = cursor.getString(cursor.getColumnIndex(TABLE_COLUMN_IMGPATH));
        text.endTiming = cursor.getFloat(cursor.getColumnIndex(TABLE_COLUMN_ENDTIMIG));
        text.idIndex = cursor.getInt(cursor.getColumnIndex(TABLE_COLUMN_IDINDEX));
        text.sentenceCn = cursor.getString(cursor.getColumnIndex(TABLE_COLUMN_SENTENCE_CN));
        text.imgWords = cursor.getString(cursor.getColumnIndex(TABLE_COLUMN_IMGWORDS));
        text.timing = cursor.getFloat(cursor.getColumnIndex(TABLE_COLUMN_TIMING));
        text.sentence = cursor.getString(cursor.getColumnIndex(TABLE_COLUMN_SENTENCE));
        return text;
    }
}
