package com.iyuba.conceptEnglish.sqlite.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.iyuba.conceptEnglish.sqlite.op.TalkShowInter;

/**
 * @title: 口语秀的数据库
 * @date: 2023/5/19 15:03
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class TalkShowDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "talkShow.db";
    private static final int DB_VERSION = 1;//1

    public TalkShowDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    private static final String CREATE_TALK_SHOW = "create table if not exists " + TalkShowInter.TABLE_NAME + " (" +
            TalkShowInter.Category + " varchar(80)," +
            TalkShowInter.CreateTime + " varchar(80)," +
            TalkShowInter.Title + " varchar(80)," +
            TalkShowInter.Sound + " varchar(100)," +
            TalkShowInter.Pic + " varchar(100) ," +

            TalkShowInter.Flag + " varchar(80)," +
            TalkShowInter.Type + " varchar(100) ," +
            TalkShowInter.DescCn + " varchar(500) ," +
            TalkShowInter.TitleCn + " varchar(500) ," +
            TalkShowInter.series + " varchar(80) ," +
            TalkShowInter.CategoryName + " varchar(100) ," +
            TalkShowInter.Id + " varchar(100) ," +
            TalkShowInter.ReadCount + " varchar(80) ," +
            TalkShowInter.clickRead + " varchar(80) ," +
            TalkShowInter.video + " varchar(100) ," +
            "primary key(" + TalkShowInter.Id + "))";  //18个参数

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TALK_SHOW);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
