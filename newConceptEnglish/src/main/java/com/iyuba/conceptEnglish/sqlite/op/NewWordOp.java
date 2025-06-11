package com.iyuba.conceptEnglish.sqlite.op;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.iyuba.conceptEnglish.sqlite.db.DatabaseService;
import com.iyuba.conceptEnglish.sqlite.mode.NewWord;

import android.content.Context;
import android.database.Cursor;

/**
 * 获取单词表数据库 
 */
public class NewWordOp extends DatabaseService {
	public static final String TABLE_NAME_WORD = "new_word";
	public static final String ID = "id";
	public static final String WORD = "word";
	public static final String AUDIO_URL = "audio_url";
	public static final String PRON = "pron";
	public static final String DEF = "def";
	public static final String VIEW_COUNT = "view_count";
	public static final String CREATE_DATE = "create_date";
	public static final String IS_DELETE = "is_delete";
	
	public NewWordOp(Context context) {
		super(context);
	}

	/**
	 * 批量插入数据
	 */
	public synchronized void saveData(NewWord word) {
		String dateTime = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateTime = sdf.format(new Date());
		
		Cursor cursor = importDatabase.openDatabase().rawQuery(
				"select * from " + TABLE_NAME_WORD + " where word='" + word.word
						+ "' AND id='" + word.id + "'", new String[] {});
		
		int databaseHasNum = cursor.getCount();
		
		closeDatabase(null);
		
		if (databaseHasNum == 0) {
			importDatabase.openDatabase().execSQL(
					"insert into " + TABLE_NAME_WORD + " (" + ID + "," + WORD
							+ "," + AUDIO_URL + "," + PRON + "," + DEF + ","
							+ VIEW_COUNT + "," + CREATE_DATE + "," + IS_DELETE 
							+ ") values(?,?,?,?,?,?,?,?)",
							new Object[] { word.id, word.word, word.audio,
								word.pron, word.def, word.viewCount, dateTime, "0"});
			closeDatabase(null);
		}
	}
	
	/**
	 * 查找生词数量
	 */
	public synchronized int getNewWordNum() {
		Cursor cursor = importDatabase.openDatabase().rawQuery(
				"select count(*) from " + TABLE_NAME_WORD, new String[] {});
		
		cursor.moveToFirst();
		int num = cursor.getInt(0);
		
		closeDatabase(null);
		
		if (cursor!=null) {
			cursor.close();
		}
		
		return num;
	}

	/**
	 * @return
	 */
	public synchronized List<NewWord> findAllData(String id) {
		List<NewWord> words = new ArrayList<NewWord>();
		
		Cursor cursor = importDatabase.openDatabase().rawQuery(
				"select " + ID + "," + WORD + "," + AUDIO_URL + "," + PRON + ","
						+ DEF + "," + VIEW_COUNT + "," + CREATE_DATE 
						+ " from " + TABLE_NAME_WORD
						+ " where ID ='" + id 
						+ "' AND " + IS_DELETE + "='0'" 
						+ " ORDER BY " + ID + " DESC",
				new String[] {});
		
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			NewWord word = new NewWord();
			word.id = String.valueOf(cursor.getInt(0));
			word.word = cursor.getString(1);
			word.audio = cursor.getString(2);
			word.pron = cursor.getString(3);
			word.def = cursor.getString(4);
			word.viewCount = cursor.getString(5);
			word.createDate = cursor.getString(6);
			words.add(word);
		}
		if (cursor!=null) {
			cursor.close();
		}
		closeDatabase(null);
		if (words.size() != 0) {
			return words;
		}
		return null;
	}

	public synchronized List<NewWord> findDataByDelete(String userId) {
		List<NewWord> words = new ArrayList<NewWord>();
		Cursor cursor = importDatabase.openDatabase().rawQuery(
				"select " + ID + "," + WORD + "," + AUDIO_URL + ","
						+ PRON + "," + DEF + "," + VIEW_COUNT + "," + CREATE_DATE
						+ "," + IS_DELETE + " from " + TABLE_NAME_WORD
						+ " where ID='" + userId + "' AND " + IS_DELETE + "='1'", new String[] {});
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			NewWord word = new NewWord();
			word.id = String.valueOf(cursor.getInt(0));
			word.word = cursor.getString(1);
			word.audio = cursor.getString(2);
			word.pron = cursor.getString(3);
			word.def = cursor.getString(4);
			word.viewCount = cursor.getString(5);
			word.createDate = cursor.getString(6);
			
			String delete = cursor.getString(7);
			if(delete.equals("0")) {
				word.isDelete = false;
			} else {
				word.isDelete = true;
			}
			
			words.add(word);
		}
		if (cursor!=null) {
			cursor.close();
		}
		closeDatabase(null);
		if (words.size() != 0) {
			return words;
		}
		return null;
	}
	
	/**
	 * 删除
	 * 
	 * @param ids
	 *            ID集合，以“,”分割,每项加上""
	 * @return
	 */
	public synchronized boolean deleteWord(String userId) {
		try {
			importDatabase.openDatabase().execSQL(
					"delete from " + TABLE_NAME_WORD + " where " + ID + "='"
							+ userId + "' AND " + IS_DELETE + "='1'");
			closeDatabase(null);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public synchronized boolean tryToDeleteWord(String keys, String userId) {
		try {
			importDatabase.openDatabase().execSQL(
					"update " + TABLE_NAME_WORD + " set " + IS_DELETE
							+ "='1' where " + ID + "='" + userId + "' AND "
							+ WORD + " in (" + keys + ")");
			closeDatabase(null);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
