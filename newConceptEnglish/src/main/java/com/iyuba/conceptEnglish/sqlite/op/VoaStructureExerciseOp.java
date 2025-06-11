package com.iyuba.conceptEnglish.sqlite.op;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.iyuba.conceptEnglish.sqlite.db.DatabaseService;
import com.iyuba.conceptEnglish.sqlite.mode.VoaStructureExercise;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class VoaStructureExerciseOp extends DatabaseService {
	public static final String TABLE_NAME = "voa_structure_exercise";
	public static final String ID = "id";
	public static final String DESC_EN = "desc_EN";
	public static final String DESC_CH = "desc_CH";
	public static final String NUMBER = "number";
	public static final String COLUMN = "column";
	public static final String NOTE = "note";
	public static final String TYPE = "type";
	public static final String QUES_NUM = "ques_num";
	public static final String ANSWER = "answer";

	public VoaStructureExerciseOp(Context context) {
		super(context);
	}

	/**
	 * 查询第bookIndex册的全部数据
	 *
	 * @return
	 */
	public synchronized List<Map<Integer, VoaStructureExercise>> findData1(int id) {
		List<Map<Integer, VoaStructureExercise>> structureMapList= new ArrayList<Map<Integer, VoaStructureExercise>>();
		Map<Integer, VoaStructureExercise> structureMap = null;

		Cursor cursor = importLocalDatabase.openLocalDatabase()
				.rawQuery(
						"select " + DESC_EN + ", " + DESC_CH + ", "
								+ NUMBER + ", " + NOTE + ", " + TYPE + ", " + ANSWER
								+ " from "+ TABLE_NAME
								+ " WHERE " + ID + " = " + id
						,
						new String[] {});

		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			VoaStructureExercise structureExercise = new VoaStructureExercise();
			structureExercise.descEN = cursor.getString(0);
			structureExercise.descCN = cursor.getString(1);
			structureExercise.number = cursor.getInt(2);
			structureExercise.note = cursor.getString(3);
			structureExercise.type = cursor.getInt(4);
			structureExercise.answer = cursor.getString(5);

			if((structureExercise.descEN != null && !structureExercise.descEN.trim().equals(""))
					|| (structureExercise.descCN != null && !structureExercise.descCN.trim().equals(""))) {
				if(structureMap != null) {
					structureMapList.add(structureMap);
				}
				structureMap = new HashMap<Integer, VoaStructureExercise>();
			}
			structureMap.put(structureExercise.number, structureExercise);
		}

		if (cursor != null) {
			cursor.close();
		}

		closeDatabase(null);

		return structureMapList;
	}
	private void updateOther(String id,String number,String key,String value){
		ContentValues v = new ContentValues();
		v.put(key,value);
		importLocalDatabase.openLocalDatabase().update(TABLE_NAME,v,ID+"=? and "+NUMBER+"=?",new String[]{id,number});
	}
	private void updateOther(String id,String number,String note,String newNumber,String answer){
		ContentValues v = new ContentValues();
		v.put(NOTE,note);
		v.put(NUMBER,newNumber);
		v.put(ANSWER,answer);
		importLocalDatabase.openLocalDatabase().update(TABLE_NAME,v,ID+"=? and "+NUMBER+"=?",new String[]{id,number});
	}
	public void updateOther(){
		updateOther("1001","4",VoaStructureExerciseOp.ANSWER,"I beg your pardon.");
		updateOther("1025","4",VoaStructureExerciseOp.NOTE,"A refrigerator and an electric cooker are in（the Kitchen）.(就划线部分提问）");
		updateOther("1007","5","I'm (Italian). （就括号部分提问）","1","What nationality are you?");
		insertSome();
	}
	public void insertSome(){
		ContentValues v = new ContentValues();
		v.put(NUMBER,"1");
		v.put(NOTE,"It is （Toyota）.(就括号部分提问)");
		v.put(QUES_NUM,"0");
		v.put(ANSWER,"What make is it?");
		v.put(DESC_CH,"");
		v.put(COLUMN,"");
		v.put(ID,"1006");
		v.put(DESC_EN,"");
		v.put(TYPE,"0");
		importLocalDatabase.openLocalDatabase().insert(TABLE_NAME,null,v);
	}
	public synchronized Map<Integer, VoaStructureExercise> findData(int id) {
		Map<Integer, VoaStructureExercise> diffcultieMap = new HashMap<Integer, VoaStructureExercise>();

		Cursor cursor = importLocalDatabase.openLocalDatabase()
				.rawQuery(
						"select " + DESC_EN + ", " + DESC_CH + ", "+ NUMBER + ", " +
								NOTE + ", "+ QUES_NUM + ", " + TYPE + ", " + ANSWER
								+ " from "+ TABLE_NAME
								+ " WHERE " + ID + " = " + id
								+" ORDER BY "+NUMBER+" ASC",
						new String[] {});

		int index = 0;
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			VoaStructureExercise structureExercise = new VoaStructureExercise();
			structureExercise.descEN = cursor.getString(0);
			structureExercise.descCN = cursor.getString(1);
			structureExercise.number = cursor.getInt(2);
			structureExercise.note = cursor.getString(3);
			structureExercise.quesNum = cursor.getInt(4);
			structureExercise.type = cursor.getInt(5);
			structureExercise.answer = cursor.getString(6);

			diffcultieMap.put(index++, structureExercise);
		}
		if (cursor != null) {
			cursor.close();
		}

		closeDatabase(null);

		return diffcultieMap;
	}

	//查询区间数据
	public synchronized List<VoaStructureExercise> findDataBlock(int lowId,int highId){
		List<VoaStructureExercise> list = new ArrayList<>();

		Cursor cursor = importLocalDatabase.openLocalDatabase()
				.rawQuery(
						"select " + DESC_EN + ", " + DESC_CH + ", "+ NUMBER + ", " +
								NOTE + ", "+ QUES_NUM + ", " + TYPE + ", " + ANSWER
								+ " from "+ TABLE_NAME
								+ " WHERE " + ID + " >= " + lowId +" and "+ID+" < "+highId
								+ " ORDER BY "+NUMBER+" ASC",
						new String[] {});

		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			VoaStructureExercise structureExercise = new VoaStructureExercise();
			structureExercise.descEN = cursor.getString(0);
			structureExercise.descCN = cursor.getString(1);
			structureExercise.number = cursor.getInt(2);
			structureExercise.note = cursor.getString(3);
			structureExercise.quesNum = cursor.getInt(4);
			structureExercise.type = cursor.getInt(5);
			structureExercise.answer = cursor.getString(6);

			list.add(structureExercise);
		}

		return list;
	}

	/**
	 * 批量插入数据
	 */
	public synchronized void saveData(List<VoaStructureExercise> multipleChoices) {
		if (multipleChoices != null && multipleChoices.size() != 0) {
			for (int i = 0; i < multipleChoices.size(); i++) {

				VoaStructureExercise tempMultipleChoice = multipleChoices.get(i);

				importLocalDatabase.openLocalDatabase().execSQL(
						"insert or replace into " + TABLE_NAME + " (" + ID + ","
								+ DESC_EN + "," + DESC_CH + "," + NUMBER + "," + COLUMN
								+ "," + NOTE + "," + TYPE + "," + QUES_NUM + ","
								+ ANSWER + ") values(?,?,?,?,?,?,?,?,?)",
						new Object[] { tempMultipleChoice.id, tempMultipleChoice.descEN,
								tempMultipleChoice.descCN, tempMultipleChoice.number,
								tempMultipleChoice.column, tempMultipleChoice.note,
								tempMultipleChoice.type, tempMultipleChoice.quesNum,
								tempMultipleChoice.answer });

				closeDatabase(null);
			}
		}
	}

	public synchronized void deleteData(List<VoaStructureExercise> multipleChoices) {
		if (multipleChoices != null && multipleChoices.size() != 0) {
			for (int i = 0; i < multipleChoices.size(); i++) {
				VoaStructureExercise tempMultipleChoice = multipleChoices.get(i);
				importLocalDatabase.openLocalDatabase().execSQL("delete from  " + TABLE_NAME + " where " + ID + "=?" +
						"and " + NUMBER + " =?",new Object[]{tempMultipleChoice.id, tempMultipleChoice.number});
				closeDatabase(null);
			}
		}
	}

	//清空该表数据
	public synchronized void deleteAllData(int lowId,int highId){
		importLocalDatabase.openLocalDatabase().execSQL("delete from "+TABLE_NAME+" where "+ID+" >= "+lowId+" and "+ID+" < "+highId);
	}
}
