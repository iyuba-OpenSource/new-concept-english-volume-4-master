package com.iyuba.conceptEnglish.sqlite.op;

import java.util.ArrayList;
import java.util.List;

import com.iyuba.conceptEnglish.sqlite.db.DatabaseService;
import com.iyuba.conceptEnglish.sqlite.mode.Book;
import com.iyuba.configation.ConfigManager;

import android.content.Context;
import android.database.Cursor;

public class BookOp extends DatabaseService {
    public static final String TABLE_NAME = "book";
    public static final String BOOK_ID = "book_id";
    public static final String BOOK_NAME = "book_name";


    public static final String TOTAL_NUM = "total_num";
    public static final String DOWNLOAD_NUM = "download_num";
    public static final String DOWNLOAD_STATE = "download_state";


    public BookOp(Context context) {
        super(context);
    }

    public synchronized void updateDownloadNum(int bookId) {
            importLocalDatabase.openLocalDatabase().execSQL(
                    "update " + TABLE_NAME + " set " + DOWNLOAD_NUM + "="
                            + DOWNLOAD_NUM + " + 1" + " where " + BOOK_ID + "="
                            + bookId);



        closeDatabase(null);
    }

    //清空数据库
    public synchronized void updateDownloadNum(Book book) {

        importLocalDatabase.openLocalDatabase().execSQL(
                "update " + TABLE_NAME + " set " + DOWNLOAD_NUM + "="
                        + book.downloadNum +
                        " where " + BOOK_ID + "="
                        + book.bookId);

        importLocalDatabase.openLocalDatabase().execSQL(
                "update " + TABLE_NAME + " set " + DOWNLOAD_NUM + "="
                        + book.downloadNum +
                        " where " + BOOK_ID + "="
                        + book.bookId * 10);


        closeDatabase(null);
    }


    public void updateDownloadState(int bookId, int downloadState) {

            importLocalDatabase.openLocalDatabase().execSQL(
                    "update " + TABLE_NAME + " set " + DOWNLOAD_STATE + "="
                            + downloadState + " where " + BOOK_ID + "=" + bookId);


        closeDatabase(null);
    }

    public synchronized List<Book> findData() {

        List<Book> books = new ArrayList<Book>();

        Cursor cursor = importLocalDatabase.openLocalDatabase().rawQuery(
                "select " + BOOK_ID + ", " + BOOK_NAME + ", " + TOTAL_NUM
                        + ", " + DOWNLOAD_NUM + ", " + DOWNLOAD_STATE
                        + " from " + TABLE_NAME + " ORDER BY book_id", new String[]{});

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            books.add(fillIn(cursor));
        }

        if (cursor != null) {
            cursor.close();
        }

        closeDatabase(null);

        return books;
    }

    private Book fillIn(Cursor cursor) {
        Book book = new Book();
        book.bookId = cursor.getInt(0);
        book.bookName = cursor.getString(1);
        book.totalNum = cursor.getInt(2);
        book.downloadNum = cursor.getInt(3);
        book.downloadState = cursor.getInt(4);
        if (book.downloadState == 1 || book.downloadState == -2) {
            book.downloadState = -1;
        }
        return book;
    }
}
