package com.iyuba.conceptEnglish.sqlite.op;

import com.iyuba.conceptEnglish.sqlite.db.DatabaseService;
import com.iyuba.conceptEnglish.sqlite.mode.VoaStructure;

import android.content.Context;
import android.database.Cursor;

import java.util.List;

public class VoaStructureOp extends DatabaseService {
	public static final String TABLE_NAME = "voa_structure";
	public static final String ID = "id";
	public static final String DESC_EN = "desc_EN";
	public static final String DESC_CN = "desc_CH";
	public static final String NUMBER = "number";
	public static final String NOTE = "note";

	public VoaStructureOp(Context context) {
		super(context);
	}

	/**
	 * 查询第bookIndex册的全部数据
	 * 
	 * @return
	 */
	public synchronized VoaStructure findData(int id) {
		VoaStructure structure = null;
		
		Cursor cursor = importLocalDatabase.openLocalDatabase()
				.rawQuery(
				"select " + DESC_EN + ", " + DESC_CN + ", "
						+ NUMBER + ", " + NOTE
						+ " from "+ TABLE_NAME 
						+ " WHERE " + ID + " = " + id
						, null);
		
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			structure = new VoaStructure();
			structure.descEN = cursor.getString(0);
			structure.descCH = cursor.getString(1);
			structure.number = cursor.getString(2);
			structure.note = cursor.getString(3);
		}
		
		if (cursor != null) {
			cursor.close();
		}
		
		closeDatabase(null);
		
		return structure;
	}

	public synchronized void deleteData(List<VoaStructure> multipleChoices) {
		if (multipleChoices != null && multipleChoices.size() != 0) {
			for (int i = 0; i < multipleChoices.size(); i++) {
				VoaStructure tempMultipleChoice = multipleChoices.get(i);
				importLocalDatabase.openLocalDatabase().execSQL("delete from  " + TABLE_NAME + " where " + ID + "=?"
						,new Object[]{tempMultipleChoice.id});
				closeDatabase(null);
			}
		}
	}

	/**
	 * 批量插入数据
	 */
	public synchronized void saveData(List<VoaStructure> multipleChoices) {
		if (multipleChoices != null && multipleChoices.size() != 0) {
			for (int i = 0; i < multipleChoices.size(); i++) {

				VoaStructure tempMultipleChoice = multipleChoices.get(i);

				importLocalDatabase.openLocalDatabase().execSQL(
						"insert or replace into " + TABLE_NAME + " (" + ID + ","
								+ DESC_EN + "," + DESC_CN + "," + NUMBER + "," + NOTE
								+ ") values(?,?,?,?,?)",
						new Object[] { tempMultipleChoice.id, tempMultipleChoice.descEN,
								tempMultipleChoice.descCH, tempMultipleChoice.number,
								tempMultipleChoice.note});
				closeDatabase(null);

			}
		}
	}



}
