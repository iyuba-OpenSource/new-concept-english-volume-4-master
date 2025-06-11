package com.iyuba.conceptEnglish.lil.fix.common_fix.model.local;

import android.database.Cursor;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.iyuba.conceptEnglish.lil.fix.common_fix.data.event.RefreshDataEvent;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.dao.AgreeEntityDao;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.dao.BookEntityJuniorDao;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.dao.ChapterCollectEntityDao;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.dao.ChapterDetailConceptFourDao;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.dao.ChapterDetailConceptJuniorDao;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.dao.ChapterDetailEntityJuniorDao;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.dao.ChapterDetailNovelDao;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.dao.ChapterEntityJuniorDao;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.dao.ChapterNovelDao;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.dao.EvalEntityChapterDao;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.dao.EvalWordDao;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.dao.LocalMarkConceptDao;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.dao.LocalMarkConceptDownloadDao;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.dao.ReadLanguageDao;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.dao.SettingEntityDao;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.dao.WordBreakEntityDao;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.dao.WordBreakPassDao;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.dao.WordConceptFourDao;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.dao.WordConceptJuniorDao;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.dao.WordJuniorDao;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.AgreeEntity;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.ChapterCollectEntity;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.EvalEntity_chapter;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.EvalEntity_word;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.SettingEntity;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.Setting_ReadLanguageEntity;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.WordBreakEntity;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.WordBreakPassEntity;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.concept.ChapterDetailEntity_conceptFour;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.concept.ChapterDetailEntity_conceptJunior;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.concept.LocalMarkEntity_concept;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.concept.LocalMarkEntity_conceptDownload;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.concept.WordEntity_conceptFour;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.concept.WordEntity_conceptJunior;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.junior.BookEntity_junior;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.junior.ChapterDetailEntity_junior;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.junior.ChapterEntity_junior;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.junior.WordEntity_junior;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.novel.ChapterDetailEntity_novel;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.novel.ChapterEntity_novel;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.DateUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ResUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.rxjava2.RxTimer;
import com.iyuba.conceptEnglish.util.ConceptApplication;
import com.iyuba.configation.Constant;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @title: 数据库操作
 * @date: 2023/5/19 11:25
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
@Database(entities = {BookEntity_junior.class,ChapterEntity_junior.class, ChapterEntity_novel.class,ChapterDetailEntity_junior.class, ChapterDetailEntity_conceptFour.class, ChapterDetailEntity_conceptJunior.class,ChapterDetailEntity_novel.class,EvalEntity_chapter.class,SettingEntity.class, Setting_ReadLanguageEntity.class,ChapterCollectEntity.class, AgreeEntity.class, EvalEntity_word.class, WordBreakEntity.class, WordBreakPassEntity.class, WordEntity_junior.class, WordEntity_conceptFour.class, WordEntity_conceptJunior.class, LocalMarkEntity_concept.class, LocalMarkEntity_conceptDownload.class},exportSchema = false,version = 12)
public abstract class RoomDB extends RoomDatabase {
    private static final String TAG = "RoomDB";

    private static RoomDB instance;
    //数据加载标志位
    private static final String dbDataLoadTag = "dbDataLoadTag";

    /**
     * 数据库初始化
     * 包名：com.iyuba.newconcepttop 并且channel为：vivo
     * 包名：com.iyuba.learnNewEnglish(已经预填充中小学数据)
     * 包名：com.iyuba.conceptStory(已经预填充中小学和小说的数据)
     * 其他包名：进行限制，不能使用这个数据库；如有需要，请自行放开
     */
    public static RoomDB getInstance(){
        //根据包名进行数据库的限制
        if ( ConceptApplication.getInstance().getPackageName().equals(Constant.package_newconcepttop)
                ||ConceptApplication.getInstance().getPackageName().equals(Constant.package_learnNewEnglish)
                ||ConceptApplication.getInstance().getPackageName().equals(Constant.package_conceptStory)
                ||ConceptApplication.getInstance().getPackageName().equals(Constant.package_concept2)
                ||ConceptApplication.getInstance().getPackageName().equals(Constant.package_englishfm)
                ||ConceptApplication.getInstance().getPackageName().equals(Constant.package_nce)){
            if (instance==null){
                synchronized (RoomDB.class){
                    if (instance==null){
                        instance = Room.databaseBuilder(ResUtil.getInstance().getApplication(),RoomDB.class,getDBName())
//                                .createFromAsset("database/juniorAndNovelFix_5_20230710.db")//直接使用db数据库的形式，room会将数据库复制到上边的名称中
                                .allowMainThreadQueries()
                                .fallbackToDestructiveMigration()
                                .addCallback(callback)
                                .addMigrations(migration_3_4,migration_9_10)
                                .build();
                    }
                }
            }
        }
        return instance;
    }

    //数据库名称
    private static String getDBName(){
        //这里设置为包名最后一个+db
        String packageName = ResUtil.getInstance().getApplication().getPackageName();
        int index = packageName.lastIndexOf(".");
        String dbName = packageName.substring(index+1);
        return dbName+".db";
    }

    //回调信息
    private static Callback callback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            //执行数据预置操作
            preData(db);
        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

            //执行数据插入操作
//            preData(db);
        }

        @Override
        public void onDestructiveMigration(@NonNull SupportSQLiteDatabase db) {
            super.onDestructiveMigration(db);
        }
    };

    /****************dao操作类***************/
    /*******中小学******/
    //书籍表-中小学书籍操作
    public abstract BookEntityJuniorDao getBookEntityJuniorDao();
    //章节表-中小学章节操作
    public abstract ChapterEntityJuniorDao getChapterEntityJuniorDao();
    //章节详情表-中小学章节详情操作
    public abstract ChapterDetailEntityJuniorDao getChapterDetailEntityJuniorDao();
    //单词表-中小学单词操作
    public abstract WordJuniorDao getWordJuniorDao();

    /***********新概念*************/
    //单词表-新概念全四册单词操作
    public abstract WordConceptFourDao getWordConceptFourDao();
    //单词表-新概念青少版单词操作
    public abstract WordConceptJuniorDao getWordConceptJuniorDao();
    //新概念-全四册章节详情数据
    public abstract ChapterDetailConceptFourDao getChapterDetailConceptFourDao();
    //新概念-青少版的章节详情数据
    public abstract ChapterDetailConceptJuniorDao getChapterDetailConceptJuniorDao();
    //新概念-篇目数据的相关处理(本地篇目、下载篇目、喜爱篇目)
    public abstract LocalMarkConceptDao getLocalMarkConceptDao();
    public abstract LocalMarkConceptDownloadDao getLocalMarkConceptDownloadDao();

    /****小说****/
    //书籍表-小说书籍操作
//    public abstract BookEntityNovelDao getBookEntityNovelDao();
    //小说-章节数据
    public abstract ChapterNovelDao getChapterNovelDao();
    //小说-章节详情数据
    public abstract ChapterDetailNovelDao getChapterDetailNovelDao();

    //评测表-章节
    public abstract EvalEntityChapterDao getEvalEntityChapterDao();

    /***********其他***************/
    //设置数据表
    public abstract SettingEntityDao getSettingEntityDao();
    //阅读界面语言切换表
    public abstract ReadLanguageDao getReadLanguageDao();
    //章节收藏表
    public abstract ChapterCollectEntityDao getChapterCollectEntityDao();
    //评测结果点赞表
    public abstract AgreeEntityDao getAgreeEntityEvalDao();

    //单词评测
    public abstract EvalWordDao getEvalWordDao();
    //单词闯关详情
    public abstract WordBreakEntityDao getWordBreakEntityDao();
    //单词闯关进度
    public abstract WordBreakPassDao getWordBreakPassDao();

    /**************************迁移数据*************************/
    private static Migration migration_3_4 = new Migration(3,4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //文章收藏增加一个列，bookid
            if (!isColumnExist(database, ChapterCollectEntity.class.getSimpleName(),"bookId")){
                String sql = "alter table "+ChapterCollectEntity.class.getSimpleName()+" add column bookId TEXT";
                database.execSQL(sql);
            }
        }
    };

    private static Migration migration_9_10 = new Migration(9,10) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //这里主要是想要删除399、400、401和402的单词数据，重新替换下
            //但是原来的单词表有问题，直接创建临时表，将数据导入后进行处理

            //创建临时表
            String createTableSql = "CREATE TABLE IF NOT EXISTS `WordEntity_junior_temp` (`def` TEXT, `updateTime` TEXT, `book_id` TEXT NOT NULL, `version` TEXT, `examples` TEXT, `videoUrl` TEXT, `pron` TEXT, `voaId` INTEGER NOT NULL, `idindex` INTEGER NOT NULL, `audio` TEXT, `position` INTEGER NOT NULL, `Sentence_cn` TEXT, `pic_url` TEXT, `unit_id` INTEGER NOT NULL, `word` TEXT, `Sentence` TEXT, `Sentence_audio` TEXT, PRIMARY KEY(`book_id`, `voaId`, `idindex`, `position`))";
            database.execSQL(createTableSql);
            //将原来的数据导入
            String importDataSql = "INSERT INTO WordEntity_junior_temp(def,updateTime,book_id,version,examples,videoUrl,pron,voaId,idindex,audio,position,Sentence_cn,pic_url,unit_id,word,Sentence,Sentence_audio) select def,updateTime,book_id,version,examples,videoUrl,pron,voaId,idindex,audio,position,Sentence_cn,pic_url,unit_id,word,Sentence,Sentence_audio from WordEntity_junior";
            database.execSQL(importDataSql);
            //删除原来的表
            String deleteTabSql = "drop table WordEntity_junior";
            database.execSQL(deleteTabSql);
            //更换为新的表
            String renameTableSql = "ALTER TABLE WordEntity_junior_temp RENAME TO WordEntity_junior";
            database.execSQL(renameTableSql);

            //删除之前的数据
            String deleteWordSql = "delete from WordEntity_junior where book_id=";
            database.execSQL(deleteWordSql+399);
            database.execSQL(deleteWordSql+400);
            database.execSQL(deleteWordSql+401);
            database.execSQL(deleteWordSql+402);
            //将数据导入
            String wordDataPath = "database/migrate/update_junior_word.sql";
            insertDataByAssetsSql(database,wordDataPath);
        }
    };

    private static Migration migration_6_7 = new Migration(6,7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //这里增加一个评测表中的用户id
            if (!isColumnExist(database,EvalEntity_chapter.class.getSimpleName(),"uid")){
                String sql = "alter table "+EvalEntity_chapter.class.getSimpleName()+" add column uid TEXT";
                database.execSQL(sql);
            }
        }
    };

    /**********************辅助功能***************************/
    //判断表是否存在
    private static boolean isTableExist(SupportSQLiteDatabase db,String tabName){
        boolean isTableExist = false;
        Cursor cursor = null;
        try {
            String sql = "select name from sqlite_master where type='table' and name='"+tabName+"'";
            cursor = db.query(sql,null);
            if (cursor!=null&&cursor.getCount()>0){
                isTableExist = true;
            }
        }catch (Exception e){

        }finally {
            if (cursor!=null){
                cursor.close();
            }
        }
        return isTableExist;
    }

    //判断列是否存在
    private static boolean isColumnExist(SupportSQLiteDatabase db,String tableName,String columnName){
        boolean isColumnExist = false;
        Cursor cursor = null;
        try {
            String sql = "select "+columnName+" from "+tableName;
            cursor = db.query(sql,null);
            if (cursor!=null&&cursor.getCount()>0){
                isColumnExist = true;
            }
        }catch (Exception e){
            isColumnExist = false;
        }finally {
            if (cursor!=null){
                cursor.close();
            }
        }
        return isColumnExist;
    }

    //判断数据是否存在
    private static boolean isDataExist(SupportSQLiteDatabase db,String tableName,String columnName,String searchData){
        boolean isColumnExist = false;
        Cursor cursor = null;
        try {
            String sql = "select * from "+tableName +" where "+columnName+" = "+searchData;
            cursor = db.query(sql,null);
            if (cursor!=null&&cursor.getCount()>0){
                isColumnExist = true;
            }
        }catch (Exception e){
            isColumnExist = false;
        }finally {
            if (cursor!=null){
                cursor.close();
            }
        }
        return isColumnExist;
    }

    //判断单词数据是否存在
    private static boolean isWordDataExist(SupportSQLiteDatabase db,String tableName,String columnName1,String searchData1,String columnName2,String searchData2){
        boolean isColumnExist = false;
        Cursor cursor = null;
        try {
            String sql = "select * from "+tableName +" where "+columnName1+" like '"+searchData1+"' and "+columnName2+" like '"+searchData2+"'";
            cursor = db.query(sql,null);
            if (cursor!=null&&cursor.getCount()>0){
                isColumnExist = true;
            }
        }catch (Exception e){
            isColumnExist = false;
        }finally {
            if (cursor!=null){
                cursor.close();
            }
        }
        return isColumnExist;
    }

    //从assets中读取sql数据并且插入到数据库中(目前看来是可以的，如果存在问题请及时解决)
    private static void insertDataByAssetsSql(SupportSQLiteDatabase db,String sqlPath){
        String startTime = DateUtil.toDateStr(System.currentTimeMillis(),DateUtil.YMDHMSS);

        try {
            Log.d(TAG, "insertDataByAssetsSql: --start--"+startTime+"---"+sqlPath);
            //获取文件数据流
            InputStream is = ResUtil.getInstance().getApplication().getAssets().open(sqlPath);
            //读取并且插入
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine())!=null){
                if (line.startsWith("##")){
                    //这个是自定义的标识符，不参与数据插入
                    continue;
                }

//                if (sqlPath.equals("database/junior/preData_junior_word.sql")){
//                    Log.d(TAG, "执行操作--"+line);
//                }
                db.execSQL(line);
            }

//            RxTimer.cancelTimer(dbDataLoadTag);
            String endTime = DateUtil.toDateStr(System.currentTimeMillis(),DateUtil.YMDHMSS);
            Log.d(TAG, "insertDataByAssetsSql: --finish--"+endTime+"---"+sqlPath);
            //刷新数据显示
            EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.junior));
            EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.novel));
        }catch (Exception e){
            Log.d(TAG, "Inserting data failed, using network data！！！"+sqlPath);
        }
    }


    //预存数据
    private static void preData(SupportSQLiteDatabase db){
        //这里有六个数据表需要存储，先判断表是否存在，然后判断表中是否存在数据
        //中小学-书籍表
        if (isTableExist(db,BookEntity_junior.class.getSimpleName())){
            String delayTag = "juniorBookTag";
            String filePath = "database/junior/preData_junior_book.sql";
            if (!isDataExist(db,ChapterEntity_junior.class.getSimpleName(),"bookId","420")){
                RxTimer.getInstance().timerInIO(delayTag, 0, new RxTimer.RxActionListener() {
                    @Override
                    public void onAction(long number) {
                        RxTimer.getInstance().cancelTimer(delayTag);
                        insertDataByAssetsSql(db,filePath);
                    }
                });
            }
        }
        //中小学-章节表
        if (isTableExist(db,ChapterEntity_junior.class.getSimpleName())){
            String delayTag = "juniorChapterTag";
            String filePath = "database/junior/preData_junior_chapter.sql";
            if (!isDataExist(db,ChapterEntity_junior.class.getSimpleName(),"voaId","3374048")){
                RxTimer.getInstance().timerInIO(delayTag, 0, new RxTimer.RxActionListener() {
                    @Override
                    public void onAction(long number) {
                        RxTimer.getInstance().cancelTimer(delayTag);
                        insertDataByAssetsSql(db,filePath);
                    }
                });
            }
        }
        //小说-章节表
        if (isTableExist(db,ChapterEntity_novel.class.getSimpleName())){
            String delayTag = "novelChapterTag";
            String filePath = "database/novel/preData_novel_chapter.sql";
            if (!isDataExist(db,ChapterEntity_novel.class.getSimpleName(),"voaid","60116")){
                RxTimer.getInstance().timerInIO(delayTag, 0, new RxTimer.RxActionListener() {
                    @Override
                    public void onAction(long number) {
                        RxTimer.getInstance().cancelTimer(delayTag);
                        insertDataByAssetsSql(db,filePath);
                    }
                });
            }
        }
        //中小学-单词表
        if (isTableExist(db,WordEntity_junior.class.getSimpleName())){
            String delayTag = "juniorWordDetailTag";
            String filePath = "database/junior/preData_junior_word.sql";
            if (!isWordDataExist(db,WordEntity_junior.class.getSimpleName(),"pron","wɪŋ","word","wing")){
                RxTimer.getInstance().timerInIO(delayTag, 0, new RxTimer.RxActionListener() {
                    @Override
                    public void onAction(long number) {
                        RxTimer.getInstance().cancelTimer(delayTag);
                        insertDataByAssetsSql(db,filePath);
                    }
                });
            }
        }
        //中小学-章节详情表
        if (isTableExist(db,ChapterDetailEntity_junior.class.getSimpleName())){
            String delayTag = "juniorChapterDetailTag";
            String filePath = "database/junior/preData_junior_chapterDetail.sql";
            if (!isDataExist(db,ChapterDetailEntity_junior.class.getSimpleName(),"endTiming","344.8")){
                RxTimer.getInstance().timerInIO(delayTag, 0, new RxTimer.RxActionListener() {
                    @Override
                    public void onAction(long number) {
                        RxTimer.getInstance().cancelTimer(delayTag);
                        insertDataByAssetsSql(db,filePath);
                    }
                });
            }
        }
        //小说-章节详情表
        if (isTableExist(db,ChapterDetailEntity_novel.class.getSimpleName())){
            String delayTag = "novelChapterDetailTag";
            String filePath = "database/novel/preData_novel_chapterDetail.sql";
            if (!isDataExist(db,ChapterDetailEntity_novel.class.getSimpleName(),"EndTiming","456.48")){
                RxTimer.getInstance().timerInIO(delayTag, 0, new RxTimer.RxActionListener() {
                    @Override
                    public void onAction(long number) {
                        RxTimer.getInstance().cancelTimer(delayTag);
                        insertDataByAssetsSql(db,filePath);
                    }
                });
            }
        }
    }
}
