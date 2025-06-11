package com.iyuba.conceptEnglish.sqlite.op;

import java.util.ArrayList;
import java.util.List;

import com.iyuba.conceptEnglish.sqlite.db.DatabaseService;
import com.iyuba.conceptEnglish.sqlite.mode.VoaAnnotation;

import android.content.Context;
import android.database.Cursor;

/**
 * 获取文章列表数据
 * 
 * @author ct
 * @time 12.9.27
 * 
 */
public class AnnotationOp extends DatabaseService {
	public static final String TABLE_NAME = "voa_annotation";
	public static final String ID = "id";
	public static final String ANNO_N = "anno_N";
	public static final String NOTE = "note";
	public static final String ANNO_DETAIL = "anno_detail";

	public AnnotationOp(Context context) {
		super(context);
	}

	/**
	 * 批量插入数据
	 */
	public synchronized void saveData(List<VoaAnnotation> annos) {
		if (annos != null && annos.size() != 0) {
			for (int i = 0; i < annos.size(); i++) {
				VoaAnnotation anno = annos.get(i);
				importLocalDatabase.openLocalDatabase()
						.execSQL(
						"insert into " + TABLE_NAME + " (" + ID + ","
									+ ANNO_N + "," + NOTE + ") values(?,?,?)",
							new Object[] { anno.id, anno.annoN,
								anno.note});
					
				closeDatabase(null);
			}
		}

	}

	public synchronized void deleteData(List<VoaAnnotation> multipleChoices) {
		if (multipleChoices != null && multipleChoices.size() != 0) {
			for (int i = 0; i < multipleChoices.size(); i++) {
				VoaAnnotation tempMultipleChoice = multipleChoices.get(i);
				importLocalDatabase.openLocalDatabase().execSQL("delete from  " + TABLE_NAME + " where " + ID + "=?" +
						"and " + ANNO_N + " =?",new Object[]{tempMultipleChoice.id, tempMultipleChoice.annoN});
				closeDatabase(null);
			}
		}
	}


	/**
	 * 批量修改
	 * 
	 * @param
	 */
	public synchronized void updateData(List<VoaAnnotation> annos) {
		if (annos != null && annos.size() != 0) {
			for (int i = 0; i < annos.size(); i++) {
				VoaAnnotation anno = annos.get(i);
				
				importLocalDatabase.openLocalDatabase().execSQL(
						"update " + TABLE_NAME + " set " + ID + "=' "
								+ anno.id + "'," + ANNO_N + "='"
								+ anno.annoN + "', " + NOTE + "='"
								+ anno.note);
				
				closeDatabase(null);
			}
		}
	}

	/**
	 * 查询第bookIndex册的全部数据
	 * 
	 * @return
	 */
	public synchronized List<VoaAnnotation> findDataByVoaId(int id) {
		List<VoaAnnotation> annos = new ArrayList<VoaAnnotation>();
		
		Cursor cursor = importLocalDatabase.openLocalDatabase()
				.rawQuery(
				"select " + ID + ", " + ANNO_N + ", "
						+ NOTE + " from "
						+ TABLE_NAME + " WHERE " + ID + " = " + id
						,
				new String[] {});
		
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			VoaAnnotation tempAnno = new VoaAnnotation();
			tempAnno.id = cursor.getInt(0);
			tempAnno.annoN = cursor.getInt(1);
			tempAnno.note = cursor.getString(2);
			
			annos.add(tempAnno);
		}
		
		if (cursor != null) {
			cursor.close();
		}
		
		closeDatabase(null);
		
		return annos;
	}


}
