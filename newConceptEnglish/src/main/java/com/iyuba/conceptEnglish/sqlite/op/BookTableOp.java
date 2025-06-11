package com.iyuba.conceptEnglish.sqlite.op;

import android.content.Context;
import android.database.Cursor;

import com.iyuba.conceptEnglish.entity.YouthBookEntity;
import com.iyuba.conceptEnglish.sqlite.db.DatabaseService;

import java.util.ArrayList;
import java.util.List;

public class BookTableOp extends DatabaseService {
    public static final String TABLE_NAME = "book_table";

    public static final  String BOOK_Id="Id";
    public static final  String BOOK_DescCn="DescCn";
    public static final  String BOOK_Category="Category";
    public static final  String BOOK_SeriesCount="SeriesCount";
    public static final  String BOOK_SeriesName="SeriesName";
    public static final  String BOOK_CreateTime="CreateTime";
    public static final  String BOOK_UpdateTime="UpdateTime";
    public static final  String BOOK_isVideo="isVideo";
    public static final  String BOOK_HotFlg="HotFlg";
    public static final  String BOOK_pic="pic";
    public static final  String BOOK_KeyWords="KeyWords";
    public static final  String BOOK_Version="version";


    public BookTableOp(Context context) {
        super(context);
    }


    public synchronized void insertData(List<YouthBookEntity> list){
        importDatabase.openDatabase().beginTransaction();
        for (YouthBookEntity entity:list){
            String sql="INSERT OR REPLACE INTO "+TABLE_NAME+"(id,DescCn,Category,SeriesCount,SeriesName,CreateTime," +
                    "UpdateTime,isVideo,HotFlg,pic,KeyWords)\n" +
                    "VALUES("+entity.Id+",'"+entity.DescCn+"',"+entity.Category+","+entity.SeriesCount+
                    ",'"+entity.SeriesName+"','"+entity.CreateTime+"','"+entity.UpdateTime+"',"+entity.isVideo+
                    ","+entity.HotFlg+",'"+ entity.pic+"','"+entity.KeyWords +"')";
            importDatabase.openDatabase().execSQL(sql);
        }
        importDatabase.openDatabase().setTransactionSuccessful();
        importDatabase.openDatabase().endTransaction();
    }

    public synchronized void updateVersion(int bookId,int version){
        String sql="update "+TABLE_NAME+" set "+BOOK_Version+"="+version+" where id= "+bookId;
        importDatabase.openDatabase().execSQL(sql);
    }

    public synchronized void clearData(){
        String sql="delete from "+TABLE_NAME;
        importDatabase.openDatabase().execSQL(sql);
    }

    public synchronized List<YouthBookEntity> selectAllData() {

        List<YouthBookEntity> books = new ArrayList<YouthBookEntity>();

        try {
            String sql="select * from "+TABLE_NAME+" ORDER BY "+BOOK_Id;
            Cursor cursor = importDatabase.openDatabase().rawQuery(sql, new String[]{});

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                books.add(fillIn(cursor));
            }

            if (cursor != null) {
                cursor.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return books;
    }

    public void updateTable(String paraName) {
        try {
            importDatabase.openDatabase().execSQL("alter table " + TABLE_NAME + " add " + paraName + " integer");
            closeDatabase(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private YouthBookEntity fillIn(Cursor cursor) {
        YouthBookEntity book = new YouthBookEntity();
        book.Id = cursor.getInt(cursor.getColumnIndex(BOOK_Id));
        book.DescCn = cursor.getString(cursor.getColumnIndex(BOOK_DescCn));
        book.Category = cursor.getInt(cursor.getColumnIndex(BOOK_Category));
        book.SeriesCount = cursor.getInt(cursor.getColumnIndex(BOOK_SeriesCount));
        book.SeriesName = cursor.getString(cursor.getColumnIndex(BOOK_SeriesName));
        book.CreateTime = cursor.getString(cursor.getColumnIndex(BOOK_CreateTime));
        book.UpdateTime = cursor.getString(cursor.getColumnIndex(BOOK_UpdateTime));
        book.isVideo = cursor.getInt(cursor.getColumnIndex(BOOK_isVideo));
        book.HotFlg = cursor.getInt(cursor.getColumnIndex(BOOK_HotFlg));
        book.pic = cursor.getString(cursor.getColumnIndex(BOOK_pic));
        book.KeyWords = cursor.getString(cursor.getColumnIndex(BOOK_KeyWords));
        book.version = cursor.getInt(cursor.getColumnIndex(BOOK_Version));
        return book;
    }
}
