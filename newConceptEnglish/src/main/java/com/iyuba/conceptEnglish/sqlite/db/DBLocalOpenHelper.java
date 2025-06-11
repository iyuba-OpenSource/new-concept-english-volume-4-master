package com.iyuba.conceptEnglish.sqlite.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 数据库更新表
 * 
 */
public class DBLocalOpenHelper extends SQLiteOpenHelper {


	public static final String DB_NAME = "concept_local_database.sqlite";// 数据库名称

	public static final int VERSION = 18;// 数据库版本，如果要更新数据库，在此版本号的基础上+1

	public DBLocalOpenHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	public DBLocalOpenHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.disableWriteAheadLogging();
		Log.e("DBLocalOpenHelper", "concept_local_database数据库已初始化...");
	}
	

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.e("DBLocalOpenHelper", "数据库已更新...");
	}
}
