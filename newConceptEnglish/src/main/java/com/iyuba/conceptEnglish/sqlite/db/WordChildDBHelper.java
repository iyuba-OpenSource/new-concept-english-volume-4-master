package com.iyuba.conceptEnglish.sqlite.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.iyuba.conceptEnglish.sqlite.op.VoaWord2Op;

public class WordChildDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "wordChild.db";
    private static final int DB_VERSION = 1;//1


    public WordChildDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    private static final String CREATE_CHILD_WORD = "create table if not exists " + VoaWord2Op.TABLE_NAME + " (" +
            VoaWord2Op.BOOK_ID + " char(10) not null," +
            VoaWord2Op.UNIT_ID + " char(10) not null," +
            VoaWord2Op.POSITION + " int(10) not null," +


            VoaWord2Op.VOA_ID + " char(10)," +//无用
            VoaWord2Op.ID_INDEX + " char(10) ," +//无用

            VoaWord2Op.WORD + " varchar(80)," +
            VoaWord2Op.DEF + " varchar(100) ," +
            VoaWord2Op.AUDIO + " varchar(100) ," +
            VoaWord2Op.PRON + " varchar(100) ," +
            VoaWord2Op.EXAMPLES + " int(10) ," +
            VoaWord2Op.ANSWER + " varchar(100) ," +
            VoaWord2Op.TIME + " varchar(100) ," +
            VoaWord2Op.VERSION + " varchar(10) ," +
            VoaWord2Op.VIDEO_URL + " varchar(100) ," +
            VoaWord2Op.SENTENCE_CN + " varchar(500) ," +
            VoaWord2Op.PIC_URL + " varchar(100) ," +
            VoaWord2Op.SENTENCE + " varchar(500) ," +
            VoaWord2Op.SENTENCE_AUDIO + " varchar(100) ," +
            "primary key(" + VoaWord2Op.BOOK_ID + "," +
            VoaWord2Op.POSITION + "," +
            VoaWord2Op.UNIT_ID + "))";  //18个参数



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CHILD_WORD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
