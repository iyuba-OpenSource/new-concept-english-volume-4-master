package com.iyuba.conceptEnglish.sqlite.op;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.iyuba.conceptEnglish.sqlite.db.DatabaseService;
import com.iyuba.conceptEnglish.sqlite.mode.VoaDiffcultyExercise;

import android.content.Context;
import android.database.Cursor;

public class VoaDiffcultyExerciseOp extends DatabaseService {
	public static final String TABLE_NAME = "voa_diffculty_exercise";
	public static final String ID = "id";
	public static final String DESC_EN = "desc_EN";
	public static final String DESC_CH = "desc_CH";
	public static final String NUMBER = "number";
	public static final String COLUMN = "column";
	public static final String NOTE = "note";
	public static final String TYPE = "type";
	public static final String QUES_NUM = "ques_num";
	public static final String ANSWER = "answer";

	public VoaDiffcultyExerciseOp(Context context) {
		super(context);
	}

	/**
	 * 查询第bookIndex册的全部数据
	 *
	 * @return
	 */
	public synchronized List<Map<Integer, VoaDiffcultyExercise>> findData1(int id) {
		List<Map<Integer, VoaDiffcultyExercise>> diffcultieMapList= new ArrayList<Map<Integer, VoaDiffcultyExercise>>();
		Map<Integer, VoaDiffcultyExercise> diffcultieMap = null;

		Cursor cursor = importLocalDatabase.openLocalDatabase()
				.rawQuery(
						"select " + DESC_EN + ", " + DESC_CH + ", "
								+ NUMBER + ", " + NOTE + ", " + TYPE + ", " + ANSWER
								+ " from "+ TABLE_NAME
								+ " WHERE " + ID + " = " + id
								+ " ORDER BY "+NUMBER+" ASC",
						new String[] {});

		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			VoaDiffcultyExercise diffcultyExercise = new VoaDiffcultyExercise();
			diffcultyExercise.descEN = cursor.getString(0);
			diffcultyExercise.descCN = cursor.getString(1);
			diffcultyExercise.number = cursor.getInt(2);
			diffcultyExercise.note = cursor.getString(3);
			diffcultyExercise.type = cursor.getInt(4);
			diffcultyExercise.answer = cursor.getString(5);

			if((diffcultyExercise.descEN != null && !diffcultyExercise.descEN.trim().equals(""))
					|| (diffcultyExercise.descCN != null && !diffcultyExercise.descCN.trim().equals(""))) {
				if(diffcultieMap != null) {
					diffcultieMapList.add(diffcultieMap);
				}

				diffcultieMap = new HashMap<Integer, VoaDiffcultyExercise>();
			}

			diffcultieMap.put(diffcultyExercise.number, diffcultyExercise);
		}

		if (cursor != null) {
			cursor.close();
		}

		closeDatabase(null);

		return diffcultieMapList;
	}

	public synchronized Map<Integer, VoaDiffcultyExercise> findData(int id) {
		Map<Integer, VoaDiffcultyExercise> diffcultieMap = new HashMap<Integer, VoaDiffcultyExercise>();

		Cursor cursor = importLocalDatabase.openLocalDatabase()
				.rawQuery(
						"select " + DESC_EN + ", " + DESC_CH + ", "+ NUMBER + ", "
								+ NOTE + ", " + QUES_NUM + ", " + TYPE + ", " + ANSWER
								+ " from "+ TABLE_NAME
								+ " WHERE " + ID + " = " + id
								+ " ORDER BY "+NUMBER+" ASC",
						new String[] {});

		int index = 0;
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			VoaDiffcultyExercise diffcultyExercise = new VoaDiffcultyExercise();
			diffcultyExercise.descEN = cursor.getString(0);
			diffcultyExercise.descCN = cursor.getString(1);
			diffcultyExercise.number = cursor.getInt(2);
			diffcultyExercise.note = cursor.getString(3);
			diffcultyExercise.quesNum = cursor.getInt(4);
			diffcultyExercise.type = cursor.getInt(5);
			diffcultyExercise.answer = cursor.getString(6);

			diffcultieMap.put(index++, diffcultyExercise);
		}

		if (cursor != null) {
			cursor.close();
		}

		closeDatabase(null);

		return diffcultieMap;
	}


	/**
	 * 插入重点难点的数据
	 */
	public synchronized void saveData(List<VoaDiffcultyExercise> multipleChoices){
		if (multipleChoices!=null&&multipleChoices.size()>0){
			String sql = "insert or replace into "+TABLE_NAME+" ("+ID+","+DESC_EN+","+DESC_CH+","+NUMBER+","+COLUMN+","+NOTE+","+TYPE+","+QUES_NUM+","+ANSWER+") values(?,?,?,?,?,?,?,?,?)";

			for (int i = 0; i < multipleChoices.size(); i++) {
				VoaDiffcultyExercise temp = multipleChoices.get(i);

				importLocalDatabase.openLocalDatabase().execSQL(sql,
						new Object[]{
								temp.id,
								temp.descEN,
								temp.descCN,
								temp.number,
								temp.column,
								temp.note,
								temp.type,
								temp.quesNum,
								temp.answer
						});
			}
		}
	}

	/**
	 * 删除重点难点数据
	 */
	public synchronized void deleteData(List<VoaDiffcultyExercise> multipleChoices){
		if (multipleChoices!=null&&multipleChoices.size()>0){
			String sql = "delete from "+TABLE_NAME+" where "+ID+"=? and "+NUMBER+"=?";

			for (int i = 0; i < multipleChoices.size(); i++) {
				VoaDiffcultyExercise temp = multipleChoices.get(i);

				importLocalDatabase.openLocalDatabase().execSQL(sql,new Object[]{temp.id,temp.number});
			}
		}
	}

	//清空该表数据
	public synchronized void deleteAllData(int lowId,int highId){
		importLocalDatabase.openLocalDatabase().execSQL("delete from "+TABLE_NAME +" where "+ID+" >= "+lowId+" and "+ID+" < "+highId);
	}
}
