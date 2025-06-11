package com.iyuba.conceptEnglish.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.iyuba.ConstantNew;
import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.api.UpdateTestAPI;
import com.iyuba.conceptEnglish.sqlite.db.DBLocalOpenHelper;
import com.iyuba.conceptEnglish.sqlite.mode.MultipleChoice;
import com.iyuba.conceptEnglish.sqlite.mode.VoaDiffcultyExercise;
import com.iyuba.conceptEnglish.sqlite.mode.VoaStructureExercise;
import com.iyuba.conceptEnglish.sqlite.op.MultipleChoiceOp;
import com.iyuba.conceptEnglish.sqlite.op.VoaDetailYouthOp;
import com.iyuba.conceptEnglish.sqlite.op.VoaDiffcultyExerciseOp;
import com.iyuba.conceptEnglish.sqlite.op.VoaLocalOp;
import com.iyuba.conceptEnglish.sqlite.op.VoaOp;
import com.iyuba.conceptEnglish.sqlite.op.VoaStructureExerciseOp;
import com.iyuba.conceptEnglish.sqlite.op.VoaWordOp;
import com.iyuba.conceptEnglish.util.GsonUtils;
import com.iyuba.conceptEnglish.util.JSONFIleUtils;
import com.iyuba.conceptEnglish.util.TransUtil;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.RuntimeManager;
import com.iyuba.core.common.util.LogUtils;
import com.iyuba.core.common.util.ToastUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * 导入数据库
 *
 * @author chentong
 */
public class ImportLocalDatabase {
    public static final String CONCEPT_LOCAL_DATABASE_VERSION = "concept_local_database_version";
    private final int BUFFER_SIZE = 400000;
    public static final String PACKNAME = ConstantNew.PACK_NAME;


    public static String DB_PATH = "/data"
            + Environment.getDataDirectory().getAbsolutePath() + "/"
            + PACKNAME + "/" + "databases"; // 在手机里存放数据库的位置


    private static Context mContext;
    //本地数据库(现在用这个数据库，那个数据库不用了)
    public static final String DB_NAME_LOCAL = "concept_local_database.sqlite";// 数据库名称
    public static final DBLocalOpenHelper mdbLocalhelper = new DBLocalOpenHelper(RuntimeManager.getContext());
    private static volatile SQLiteDatabase localDatabase = null;
    private int lastVersion;


    public ImportLocalDatabase(Context context) {
        mContext = context;
    }

    //本地数据库
    public synchronized SQLiteDatabase openLocalDatabase() {
        localDatabase = mdbLocalhelper.getWritableDatabase();
        return localDatabase;
    }


    /**
     * 修改后，此函数作为第一次运行时的创建数据库函数
     *
     * @param dbfile
     */
    public synchronized void openLocalDatabase(String dbfile) {
        File database = new File(dbfile);
//        <yourdb>.db，<yourdb>-wal，<yourdb>-shm。
        String file_wal = dbfile + "-wal";
        String file_shm = dbfile + "-shm";

        lastVersion = ConfigManager.Instance().loadInt(CONCEPT_LOCAL_DATABASE_VERSION, 1);
        switch (lastVersion) {
            case 1:
                if (database.exists()) {
                    // imported sqlite file that whether is table exit?
                    if (selectTableCount("voa") < 1
                            || selectTableCount("voa_detail") < 1
                            || selectTableCount("voa_detail_american") < 1) {
                        //Anyone if it is not exit ,we should import our sqlite file
                        // If table is not exit,That mean it is a new user.
                        File f_al = new File(file_wal);
                        File f_shm = new File(file_shm);
                        if (f_al.exists())
                            f_al.delete();
                        if (f_shm.exists())
                            f_shm.delete();
                        if (database.delete()) {
                            loadLocalDataBase(dbfile);
                        }
                        LogUtils.d("wangwenyang LocalDatabase is new user");
                    } else {
                        LogUtils.d("wangwenyang LocalDatabase is old user");
                        VoaLocalOp voaLocalOp = new VoaLocalOp(mContext);
                        if (!voaLocalOp.checkSoundUrlExist1(VoaOp.VERSION_UK)) {
                            voaLocalOp.updateTable(VoaOp.VERSION_UK);
                        }
                        if (!voaLocalOp.checkSoundUrlExist1(VoaOp.VERSION_US)) {
                            voaLocalOp.updateTable(VoaOp.VERSION_US);
                        }
                        if (!voaLocalOp.checkSoundUrlExist1(VoaOp.VERSION_WORD)) {
                            voaLocalOp.updateTable(VoaOp.VERSION_WORD);
                        }
                        if (!voaLocalOp.checkSoundUrlExist1(VoaOp.CATEGORY_ID)) {
                            //新增 列
                            voaLocalOp.updateTable(VoaOp.CATEGORY_ID);
                            voaLocalOp.updateTableText(VoaOp.TITLE_ID);
                            voaLocalOp.updateTable(VoaOp.TOTAL_TIME);
                            //新增预置数据
                        }
                    }
                } else if (!database.exists()) {// 判断数据库文件是否存在，若不存在则执行导入，否则直接打开数据库
                    LogUtils.d("wangwenyang LocalDatabase sqlite file import");
                    loadLocalDataBase(dbfile);
                }
                ConfigManager.Instance().putInt(CONCEPT_LOCAL_DATABASE_VERSION, 2);
            case 2:
                //新增 voa_detail_youth  表，用于记录青少版的课文详情
                VoaDetailYouthOp voaDetailYouthOp = new VoaDetailYouthOp(mContext);
                voaDetailYouthOp.createTable();
                VoaWordOp voaWordOp=new VoaWordOp(mContext);
                voaWordOp.updateTableNew(VoaWordOp.POSITION);
                voaWordOp.updateTableNew(VoaWordOp.UNITID);
                voaWordOp.updateTableNew(VoaWordOp.BOOK_ID);
                ConfigManager.Instance().putInt(CONCEPT_LOCAL_DATABASE_VERSION, 3);

            case 3:
                //预存习题数据
                //这里从本地json文件中获取数据，然后设置到表中
                //这里是第一册和第二册的数据，第三册和第四册数据暂时未上传，青少版的也没有处理
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //这里这样操作：如果2002有数据（之前没有），则不执行操作；如果没有数据，则执行操作
                        VoaStructureExerciseOp temp = new VoaStructureExerciseOp(mContext);
                        List<VoaStructureExercise> tempList = temp.findDataBlock(1002,1003);

                        if (tempList==null||tempList.size()==0){
                            //删除之前的混乱的本地数据，然后将数据放进去
                            MultipleChoiceOp op1 = new MultipleChoiceOp(mContext);
                            op1.deleteAllData(1000,3000);
                            VoaStructureExerciseOp op2 = new VoaStructureExerciseOp(mContext);
                            op2.deleteAllData(1000,3000);
                            VoaDiffcultyExerciseOp op3 = new VoaDiffcultyExerciseOp(mContext);
                            op3.deleteAllData(1000,3000);

                            updateExerciseByJsonFile("voa_exercise_update_usuk12_20230316.json");
                        }
                        ConfigManager.Instance().putInt(CONCEPT_LOCAL_DATABASE_VERSION, 4);
                    }
                }).start();
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
        localDatabase = mdbLocalhelper.getWritableDatabase();
        Cursor cursor = localDatabase.query("sqlite_master", new String[]{"count(*)"},
                "name = \"" + tableName + "\"",
                null, null, null, null);
        if (cursor.moveToFirst()) {
            count = cursor.getInt(cursor.getColumnIndexOrThrow("count(*)"));
        }
        if (cursor != null) {
            cursor.close();
        }
        LogUtils.d("wangwenyang LocalDatabase table :" + tableName + ",count :" + count);
        return count;
    }

    /**
     * 将数据库文件拷贝到需要的位置,替换原生的android 生成的数据库文件
     *
     * @param dbfile
     */
    private void loadLocalDataBase(String dbfile) {
        try {
            InputStream is = mContext.getResources().openRawResource(R.raw.concept_local_database); // 欲导入的数据库
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
            bis.close();
            is.close();
            bfos.close();
            fos.close();
        } catch (Exception e) {
            ToastUtil.showLongToast(mContext, e.toString() + "");
            e.printStackTrace();
        }

    }


    /**
     * 从assets中获取sql文件插入到数据库中
     */
    public static void executeAssetsSQL(Context context, SQLiteDatabase db, String dbfilepath) {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(context.getAssets().open(dbfilepath)));
            Log.e("db-error", "路径:" + dbfilepath);
            String line;
            String buffer = "";
            //开启事务
            db.beginTransaction();
            while ((line = in.readLine()) != null) {
                buffer += line;
                if (line.trim().endsWith(";")) {
                    db.execSQL(buffer.replace(";", ""));
                    buffer = "";
                }
            }
            //设置事务标志为成功，当结束事务时就会提交事务
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("db-error", e.toString());
        } finally {
            //事务结束
            db.endTransaction();
            try {
                if (in != null)
                    in.close();
            } catch (Exception e) {
                Log.e("db-error", e.toString());
            }
        }
    }

    /**
     * 从json文件中读取数据并且插入到习题数据库中
     */
    public static void updateExerciseByJsonFile(String jsonFileName){
        String data = JSONFIleUtils.getOriginalFundData(mContext, jsonFileName);
        UpdateTestAPI.UpdateTestBean updateTestBean = GsonUtils.toObject(data, UpdateTestAPI.UpdateTestBean.class);

        //插入三个数据
        List<MultipleChoice> list = updateTestBean.multipleChoice;//选择
        List<VoaStructureExercise> list2 = updateTestBean.VoaStructureExercise;//关键句型
        List<VoaDiffcultyExercise> list3 = TransUtil.transDiffExercise(updateTestBean.VoaDiffcultyExercise);//重点难点

        if (list != null && list.size() > 0) {
            MultipleChoiceOp multipleChoiceOp = new MultipleChoiceOp(mContext);
            multipleChoiceOp.deleteData(list);
            multipleChoiceOp.saveData(list);
        }

        if (list2 != null && list2.size() > 0) {
            VoaStructureExerciseOp voaStructureExerciseOp = new VoaStructureExerciseOp(mContext);
            voaStructureExerciseOp.deleteData(list2);
            voaStructureExerciseOp.saveData(list2);
        }

        if (list3!=null&&list3.size()>0){
            VoaDiffcultyExerciseOp voaDiffcultyExerciseOp = new VoaDiffcultyExerciseOp(mContext);
            voaDiffcultyExerciseOp.deleteData(list3);
            voaDiffcultyExerciseOp.saveData(list3);
        }
    }
}
