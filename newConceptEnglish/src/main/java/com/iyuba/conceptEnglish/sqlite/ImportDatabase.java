package com.iyuba.conceptEnglish.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.iyuba.ConstantNew;
import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.lil.concept_other.test.FourChapterTestData;
import com.iyuba.conceptEnglish.sqlite.db.DBOpenHelper;
import com.iyuba.conceptEnglish.sqlite.op.BookTableOp;
import com.iyuba.conceptEnglish.sqlite.op.VoaOp;
import com.iyuba.conceptEnglish.sqlite.op.VoaSoundOp;
import com.iyuba.conceptEnglish.sqlite.op.WordPassUserOp;
import com.iyuba.conceptEnglish.util.GsonUtils;
import com.iyuba.conceptEnglish.util.JSONFIleUtils;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.RuntimeManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 导入数据库
 *
 * @author chentong
 */
public class ImportDatabase {
    private final int BUFFER_SIZE = 400000;
    public static final String PACKNAME = ConstantNew.PACK_NAME;
    public static final String DB_NAME = "concept_database.sqlite";// 数据库名称

    public static String DB_PATH = "/data"
            + Environment.getDataDirectory().getAbsolutePath() + "/"
            + PACKNAME + "/" + "databases"; // 在手机里存放数据库的位置


    public static final DBOpenHelper mdbhelper = new DBOpenHelper(RuntimeManager.getContext());
    private static SQLiteDatabase database = null;
    private static Context mContext;


    public ImportDatabase(Context context) {
        mContext = context;
    }

    public synchronized SQLiteDatabase openDatabase() {
        database = mdbhelper.getWritableDatabase();
        return database;
    }


    /**
     * 修改后，此函数作为第一次运行时的创建数据库函数
     *
     * @param dbfile
     */
    public synchronized void loadDatabase(String dbfile) {

        File databaseFile = new File(dbfile);


        if (databaseFile.exists()) {
            database = mdbhelper.getWritableDatabase();

            //最小版本12开始更新数据库，不替换数据库
            int lastVersion = ConfigManager.Instance().loadInt("database_version", 17);

            switch (lastVersion) {
//                case 14:
//                    //添加评测英音句子表
//                    String sql = "create table if not exists voa_eval_british " +
//                            "(voa_id integer,wordscore text,totalscore integer,filepath text,time text,itemid integer primary key,sound_url text)";
//                    database.execSQL(sql);
//                    ConfigManager.Instance().putInt("database_version", 15);

//                case 15:
                //单词闯关记录表
//                    String sql = " create table if not exists word_error " +
//                            "(voa_id integer,word text,uid integer,primary key(voa_id,word,uid))";
//                    database.execSQL(sql);
//
//                    sql = "create table if not exists word_pass" +
//                            "(uid integer,voa_id integer,book_id integer,primary key(uid,book_id))";
//                    database.execSQL(sql);
//
//                    //评测英音句子表-新
//                    sql = "create table if not exists voa_eval_british_new " +
//                            "(voa_id integer,wordscore text,totalscore integer,filepath text,time text,itemid integer,sound_url text,uid integer,primary key(uid,itemid))";
//                    database.execSQL(sql);

                //评测美音句子表-新
//                    sql = "create table if not exists voa_sound_new " +
//                            "(voa_id integer,wordscore text,totalscore integer,filepath text,time text,itemid integer,sound_url text,uid integer,primary key(uid,itemid))";
//                    database.execSQL(sql);
//
//                    //单选题做题记录表
//                    sql = "create table if not exists multiple_choice_record " +
//                            "(voa_id integer,uid integer,right_num integer,all_num integer,primary key(uid,voa_id))";
//                    database.execSQL(sql);
//
//                    //听课文记录 type 区分英美音
//                    sql = "create table if not exists article_record " +
//                            "(voa_id integer,uid integer,curr_time integer,total_time integer,is_finish integer,type integer,primary key(uid,voa_id,type))";
//                    database.execSQL(sql);
//                    ConfigManager.Instance().putInt("database_version", 16);

//                case 16:
//                    //单词闯关记录表 -- 新表
//                    sql = " create table if not exists word_pass_user " +
//                            "(voa_id integer,position integer,word text,uid integer,is_upload integer,answer integer,primary key(voa_id,position,uid))";
//                    database.execSQL(sql);
////                    //课文记录表新加单词数字段
////                    sql = " alter table article_record add percent integer";
//                    // database.execSQL(sql);
//                    //测试题记录新表
//                    sql = "create table if not exists test_record (uid integer,LessonId integer,BeginTime text ,TestNumber integer," +
//                            "UserAnswer text,RightAnswer text,AnswerResult integer,TestTime text,IsUpload integer,primary key(uid,LessonId,TestNumber))";
//                    database.execSQL(sql);
//                    ConfigManager.Instance().putInt("database_version", 17);
                case 17:
                    String sql;
                    VoaOp voaOp = new VoaOp(mContext);
                    if (!voaOp.checkSoundUrlExist1(VoaOp.VERSION_UK)) {
                        voaOp.updateTable(VoaOp.VERSION_UK);
                    }
                    if (!voaOp.checkSoundUrlExist1(VoaOp.VERSION_US)) {
                        voaOp.updateTable(VoaOp.VERSION_US);
                    }
                    ConfigManager.Instance().putInt("database_version", 18);
                case 18:
                    /**
                     * 在voa表中，新增 CATEGORY_ID  TITLE_ID TOTAL_TIME 三个列
                     * 用于在课文title页，跳转微课，和记录微课的学习记录
                     */
                    VoaOp voaOp18 = new VoaOp(mContext);
                    if (!voaOp18.checkSoundUrlExist1(VoaOp.CATEGORY_ID)) {
                        //新增 列
                        voaOp18.updateTable(VoaOp.CATEGORY_ID);
                        voaOp18.updateTableText(VoaOp.TITLE_ID);
                        voaOp18.updateTable(VoaOp.TOTAL_TIME);
                        //新增预置数据

                    }
                    ConfigManager.Instance().putInt("database_version", 19);
                case 19:
                    VoaOp voaOp19 = new VoaOp(mContext);
                    if (!voaOp19.checkSoundUrlExist1(VoaOp.VERSION_WORD)) {
                        voaOp19.updateTable(VoaOp.VERSION_WORD);
                    }
                    //清除版本数据，因为 concept_local_database.sqlite 会被删除重置
                    //正常用户不受影响，不正常用户升级后 将会恢复正常
                    voaOp19.clearColumnDataToNull(VoaOp.VERSION_UK);
                    voaOp19.clearColumnDataToNull(VoaOp.VERSION_US);
                    voaOp19.clearColumnDataToNull(VoaOp.VERSION_WORD);

                    ConfigManager.Instance().putInt("database_version", 20);
                case 20:
                    sql = "CREATE table if not exists book_table (\n" +
                            "  Id INTEGER NOT NULL,\n" +
                            "  DescCn TEXT,\n" +
                            "  Category integer,\n" +
                            "  SeriesCount integer,\n" +
                            "  SeriesName TEXT,\n" +
                            "  CreateTime TEXT,\n" +
                            "  UpdateTime TEXT,\n" +
                            "  isVideo integer,\n" +
                            "  HotFlg integer,\n" +
                            "  pic TEXT,\n" +
                            "  KeyWords TEXT,\n" +
                            "  PRIMARY KEY (Id)\n" +
                            ");";
                    database.execSQL(sql);

                    //评测青少版句子表
                    sql = "create table if not exists "+ VoaSoundOp.TABLE_NAME_YOUNTH +" " +
                            "(voa_id integer,wordscore text,totalscore integer,filepath text,time text,itemid integer,sound_url text,uid integer,primary key(uid,itemid))";
                    database.execSQL(sql);

                    ConfigManager.Instance().putInt("database_version", 21);
                case 21:
                    BookTableOp op=new BookTableOp(mContext);
                    op.updateTable(BookTableOp.BOOK_Version);

                    WordPassUserOp wordPassUserOp=new WordPassUserOp(mContext);
                    wordPassUserOp.updateTable(WordPassUserOp.UNITID);

                    ConfigManager.Instance().putInt("database_version", 22);
                case 22:
                    //2021.12.16
                    VoaOp voaOp22 = new VoaOp(mContext);
                    voaOp22.updateTableText("clickRead");

                    ConfigManager.Instance().putInt("database_version", 23);
                    // TODO: 2023/5/15 将数据重新刷入
                case 23:
                    loadDataBase(dbfile);

                    ConfigManager.Instance().putInt("database_version", 24);
                // TODO: 2023/6/16 增加新概念第四册的图片
                case 24:
                    VoaOp voa24Op = new VoaOp(mContext);
                    String data = JSONFIleUtils.getOriginalFundData(mContext,"voa_chapter_concept_four_4_20230616.json");
                    FourChapterTestData testData = GsonUtils.toObject(data,FourChapterTestData.class);
                    if (testData!=null&&testData.getData()!=null&&testData.getData().size()>0){
                        for (int i = 0; i < testData.getData().size(); i++) {
                            FourChapterTestData.DataBean dataBean = testData.getData().get(i);
                            voa24Op.updatePic(dataBean.getVoa_id(),dataBean.getPic());
                        }
                    }

                    ConfigManager.Instance().putInt("database_version", 25);
            }
        } else if (!databaseFile.exists()) {// 判断数据库文件是否存在，若不存在则执行导入，否则直接打开数据库
            loadDataBase(dbfile);
        }
    }

    public void closeDatabase() {
        //database.close();
    }

    /**
     * 将数据库文件拷贝到需要的位置
     *
     * @param dbfile 文件
     */
    private void loadDataBase(String dbfile) {
        try {
            InputStream is = mContext.getResources().openRawResource(R.raw.concept_database); // 欲导入的数据库
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
