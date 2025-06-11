package com.iyuba.conceptEnglish.sqlite.op;

import java.util.ArrayList;
import java.util.List;

import com.iyuba.conceptEnglish.sqlite.db.DatabaseService;
import com.iyuba.conceptEnglish.sqlite.mode.MultipleChoice;

import android.content.Context;
import android.database.Cursor;

import timber.log.Timber;

/**
 * 获取单词表数据库 
 */
public class MultipleChoiceOp extends DatabaseService {
	public static final String TABLE_NAME = "multiple_choice";
	public static final String VOA_ID = "voa_id";
	public static final String INDEX_ID = "index_id";
	public static final String QUESTION = "question";
	public static final String CHOICE_A = "choice_A";
	public static final String CHOICE_B = "choice_B";
	public static final String CHOICE_C = "choice_C";
	public static final String CHOICE_D = "choice_D";
	public static final String ANSWER = "answer";

	public MultipleChoiceOp(Context context) {
		super(context);
	}

	/**
	 * 批量插入数据
	 */
	public synchronized void saveData(List<MultipleChoice> multipleChoices) {
		if (multipleChoices != null && multipleChoices.size() != 0) {
			for (int i = 0; i < multipleChoices.size(); i++) {

				MultipleChoice tempMultipleChoice = multipleChoices.get(i);

				importLocalDatabase.openLocalDatabase().execSQL(
						"insert or replace into " + TABLE_NAME + " (" + VOA_ID + ","
								+ INDEX_ID + "," + QUESTION + "," + CHOICE_A + "," + CHOICE_B
								+ "," + CHOICE_C + "," + CHOICE_D + ","
								+ ANSWER + ") values(?,?,?,?,?,?,?,?)",
						new Object[] { tempMultipleChoice.voaId, tempMultipleChoice.indexId,
								tempMultipleChoice.question, tempMultipleChoice.choiceA,
								tempMultipleChoice.choiceB, tempMultipleChoice.choiceC,
								tempMultipleChoice.choiceD, tempMultipleChoice.answer });


				closeDatabase(null);
			}
		}
	}

	public synchronized void deleteData(List<MultipleChoice> multipleChoices) {
		if (multipleChoices != null && multipleChoices.size() != 0) {
			for (int i = 0; i < multipleChoices.size(); i++) {
				MultipleChoice tempMultipleChoice = multipleChoices.get(i);
				importLocalDatabase.openLocalDatabase().execSQL("delete from  " + TABLE_NAME + " where " + VOA_ID + "=?" +
						"and " + INDEX_ID + " =?",new Object[]{tempMultipleChoice.voaId, tempMultipleChoice.indexId});
				closeDatabase(null);
			}
		}
	}

	public synchronized void  upData(List<MultipleChoice> multipleChoices) {
		if (multipleChoices != null && multipleChoices.size() != 0) {
			for (int i = 0; i < multipleChoices.size(); i++) {
				MultipleChoice tempMultipleChoice = multipleChoices.get(i);
				importDatabase.openDatabase().execSQL("update " + TABLE_NAME +
						" set " + QUESTION + " =  '" + sqLiteEscape(tempMultipleChoice.question) +
						"', " + CHOICE_A + "='" + sqLiteEscape(tempMultipleChoice.choiceA)+
						"', " + CHOICE_B + "='" + sqLiteEscape(tempMultipleChoice.choiceB)+
						"', " + CHOICE_C + "='" + sqLiteEscape(tempMultipleChoice.choiceC)+
						"', " + CHOICE_D + "='" + sqLiteEscape(tempMultipleChoice.choiceD)+
						"', " + ANSWER + "='" + sqLiteEscape(tempMultipleChoice.answer)+
						"' where " + VOA_ID + "=" +tempMultipleChoice.voaId
						+ " and " + INDEX_ID + "="+tempMultipleChoice.indexId);
				//new Object[]{tempMultipleChoice.voaId, tempMultipleChoice.indexId}
				closeDatabase(null);
			}
		}
	}


	private  String sqLiteEscape(String keyWord) {
		keyWord = keyWord.replace("/", "//");
		keyWord = keyWord.replace("'", "''");
		keyWord = keyWord.replace("[", "/[");
		keyWord = keyWord.replace("]", "/]");
		keyWord = keyWord.replace("%", "/%");
		keyWord = keyWord.replace("&", "/&");
		keyWord = keyWord.replace("_", "/_");
		keyWord = keyWord.replace("(", "/(");
		keyWord = keyWord.replace(")", "/)");
		return keyWord;
	}

	/**
	 * @return
	 */
	public List<MultipleChoice> findData(int voaId) {
		//SQLiteDatabase db = importLocalDatabase.openLocalDatabase();
		//Cursor cursor = db.rawQuery("select * from multiple_choice where voa_id=?", new String[]{String.valueOf(voaId)});

		Cursor cursor = importLocalDatabase.openLocalDatabase().rawQuery(
				"select " + VOA_ID + "," + INDEX_ID + "," + QUESTION + "," + CHOICE_A + ","
						+ CHOICE_B + "," + CHOICE_C + "," + CHOICE_D + "," + ANSWER
						+ " from " + TABLE_NAME
						+ " where " + VOA_ID + "=?"
						+" ORDER BY "+INDEX_ID+" ASC",
				new String[] {String.valueOf(voaId)});

		List<MultipleChoice> multChoiceList = new ArrayList<>();
		MultipleChoice multChoice ;
		Timber.d("数据数量"+cursor.getCount());
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			multChoice = new MultipleChoice();
			multChoice.voaId = cursor.getInt(0);
			multChoice.indexId = cursor.getInt(1);
			multChoice.question = cursor.getString(2);
			multChoice.choiceA = cursor.getString(3);
			multChoice.choiceB = cursor.getString(4);
			multChoice.choiceC = cursor.getString(5);
			multChoice.choiceD = cursor.getString(6);
			multChoice.answer = tansformAnswer(cursor.getString(7));
			multChoiceList.add(multChoice);
		}


		cursor.close();
		closeDatabase(null);

		return multChoiceList;
	}

	public String tansformAnswer(String answer) {
		String result = "";

		switch(Integer.parseInt(answer)) {
			case 1:result = "a"; break;
			case 2:result = "b"; break;
			case 3:result = "c"; break;
			case 4:result = "d"; break;
		}

		return result;
	}

	//清空该表数据
	public synchronized void deleteAllData(int lowId,int highId){
		importLocalDatabase.openLocalDatabase().execSQL("delete from "+TABLE_NAME+" where "+VOA_ID+" >= "+lowId+" and "+VOA_ID+" < "+highId);
	}
}
