package com.iyuba.conceptEnglish.sqlite.op;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.iyuba.core.common.data.model.TalkLesson;
import com.iyuba.core.common.data.model.VoaWord2;

import java.util.ArrayList;
import java.util.List;

/**
 * @title:
 * @date: 2023/5/19 15:31
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class TalkShowOp implements TalkShowInter{

    private final SQLiteDatabase db;

    public TalkShowOp(SQLiteDatabase db) {
        this.db = db;
    }

    //插入数据（15个参数）
    @Override
    public void saveData(List<TalkLesson> list) {
        if (list != null && list.size() != 0) {
            for (int i = 0; i < list.size(); i++) {
                TalkLesson tempword = list.get(i);
                db.execSQL(
                        "insert or replace into " + TABLE_NAME + " (" + Category + ","
                                + CreateTime + "," + Title + "," + Sound + "," + Pic + "," + Flag + ","
                                + Type + "," + DescCn + "," + TitleCn + "," + series + "," + CategoryName + "," + Id
                                + "," + ReadCount + "," + clickRead + "," + video
                                + ") values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                        new Object[]{tempword.Category,tempword.CreateTime,tempword.Title,tempword.Sound,tempword.Pic,
                        tempword.Flag,tempword.Type,tempword.DescCn,tempword.TitleCn,tempword.series,tempword.CategoryName,tempword.Id,
                        tempword.ReadCount,tempword.clickRead,tempword.video});
            }
        }
    }

    //获取当前章节下的数据
    @Override
    public TalkLesson findTalkByVoaId(String voaId) {
        String sql = "select " + Category + "," + CreateTime + "," + Title + "," + Sound + "," + Pic + ","
                + Flag + "," + Type + "," + DescCn + "," + TitleCn + "," + series + ","
                + CategoryName + "," + Id + "," + ReadCount + "," + clickRead + "," + video
                +" from "+TABLE_NAME+" where "+Id+" = ?";
        Cursor cursor = db.rawQuery(sql,new String[]{voaId});

        if (cursor.moveToNext()){
            TalkLesson lesson = new TalkLesson();
            lesson.Category = cursor.getString(0);
            lesson.CreateTime = cursor.getString(1);
            lesson.Title = cursor.getString(2);
            lesson.Sound = cursor.getString(3);
            lesson.Pic = cursor.getString(4);
            lesson.Flag = cursor.getString(5);
            lesson.Type = cursor.getString(6);
            lesson.DescCn = cursor.getString(7);
            lesson.TitleCn = cursor.getString(8);
            lesson.series = cursor.getString(9);
            lesson.CategoryName = cursor.getString(10);
            lesson.Id = cursor.getString(11);
            lesson.ReadCount = cursor.getString(12);
            lesson.clickRead = cursor.getString(13);
            lesson.video = cursor.getString(14);
            return lesson;
        }

        return null;
    }

    @Override
    public List<TalkLesson> findTalkByBookId(String bookId) {
        List<TalkLesson> list = new ArrayList<>();
        String sql = "select " + Category + "," + CreateTime + "," + Title + "," + Sound + "," + Pic + ","
                + Flag + "," + Type + "," + DescCn + "," + TitleCn + "," + series + ","
                + CategoryName + "," + Id + "," + ReadCount + "," + clickRead + "," + video
                +" from "+TABLE_NAME+" where "+series+" = ? order by "+Id;
        Cursor cursor = db.rawQuery(sql,new String[]{bookId});


        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            TalkLesson lesson = new TalkLesson();
            lesson.Category = cursor.getString(0);
            lesson.CreateTime = cursor.getString(1);
            lesson.Title = cursor.getString(2);
            lesson.Sound = cursor.getString(3);
            lesson.Pic = cursor.getString(4);
            lesson.Flag = cursor.getString(5);
            lesson.Type = cursor.getString(6);
            lesson.DescCn = cursor.getString(7);
            lesson.TitleCn = cursor.getString(8);
            lesson.series = cursor.getString(9);
            lesson.CategoryName = cursor.getString(10);
            lesson.Id = cursor.getString(11);
            lesson.ReadCount = cursor.getString(12);
            lesson.clickRead = cursor.getString(13);
            lesson.video = cursor.getString(14);

            list.add(lesson);
        }

        return list;
    }
}
