package com.iyuba.core.common.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.RuntimeManager;
import com.iyuba.core.common.sqlite.db.DBOpenHelper;
import com.iyuba.core.common.util.LogUtils;
import com.iyuba.lib.R;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 数据库管理
 *
 * @author chentong
 */
public class ImportLibDatabase {
    public static final String KEY_LIB_DATABASE_VERSION = "lib_database_version";
    private final int BUFFER_SIZE = 400000;
    private static final String DB_NAME = "lib_database.sqlite"; // 保存的数据库文件名
    private String PACKAGE_NAME;
    private String DB_PATH;
    public static DBOpenHelper mdbhelper = new DBOpenHelper(RuntimeManager.getContext());
    private static SQLiteDatabase database = null;
    private static Context mContext;
    private int lastVersion;

    public ImportLibDatabase(Context context) {
        mContext = context;
    }

    public String getDBPath() {
        return DB_PATH + "/" + DB_NAME;
    }

    /*
     * 传入报名 导入数据库
     */
    public void setPackageName(String packageName) {
        PACKAGE_NAME = packageName;
        DB_PATH = "/data" + Environment.getDataDirectory().getAbsolutePath()
                + "/" + PACKAGE_NAME + "/" + "databases";
    }


    public synchronized SQLiteDatabase openDatabase() {
        this.database = mdbhelper.getWritableDatabase();
        return this.database;
    }

    /**
     * 打开数据库 根据版本判断是否需要更新
     * 原生数据库 与预置数据库的结合：
     * 将预置数据库替换到原生数据库的文件位置！
     *
     * @param dbfile
     */
    public synchronized void openDatabase(String dbfile) {
        lastVersion = ConfigManager.Instance().loadInt(KEY_LIB_DATABASE_VERSION, 1);
        switch (lastVersion) {
            case 1:
                File database = new File(dbfile);
                if (database.exists()) {
                    if (selectTableCount("example") < 1
                            || selectTableCount("sayings") < 1
                            || selectTableCount("wordDB") < 1) {
                        //Anyone if it is not exit ,we should import our sqlite file
                        // If table is not exit,That mean it is a new user.
                        if (database.delete()) {
                            loadDataBase(dbfile);
                        }
                        LogUtils.d("wangwenyang libDatabase is new user");
                    } else {
                        LogUtils.d("wangwenyang libDatabase is old user");
                    }
                } else {
                    LogUtils.d("wangwenyang libDatabase sqlite file import");
                    loadDataBase(dbfile);
                }
                ConfigManager.Instance().putInt(KEY_LIB_DATABASE_VERSION, 2);
        }
    }

    /**
     * check if the table exit
     *
     * @param tableName
     * @return
     */
    private int selectTableCount(String tableName) {
        int count = 0;
        database = mdbhelper.getWritableDatabase();
        Cursor cursor = database.query("sqlite_master", new String[]{"count(*)"},
                "name = \"" + tableName + "\"",
                null, null, null, null);
        if (cursor.moveToFirst()) {
            count = cursor.getInt(cursor.getColumnIndexOrThrow("count(*)"));
        }
        if(cursor!=null){
            cursor.close();
        }
        LogUtils.d("wangwenyang libDatabase table :" + tableName + ",count :" + count);
        return count;
    }


    public void closeDatabase() {
    }

    /**
     * 更换！数据库
     *
     * @param dbfile
     */
    private void loadDataBase(String dbfile) {
        try {
            InputStream is = mContext.getResources().openRawResource(
                    R.raw.lib_database);
            BufferedInputStream bis = new BufferedInputStream(is);
            if (!(new File(DB_PATH).exists())) {
                new File(DB_PATH).mkdir();
            }
            FileOutputStream fos = new FileOutputStream(dbfile);
            BufferedOutputStream bfos = new BufferedOutputStream(fos);
            byte[] buffer = new byte[BUFFER_SIZE];
            int count = 0;
            while ((count = bis.read(buffer)) > 0) {
                bfos.write(buffer, 0, count);
            }
            fos.close();
            is.close();
            bis.close();
            bfos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
