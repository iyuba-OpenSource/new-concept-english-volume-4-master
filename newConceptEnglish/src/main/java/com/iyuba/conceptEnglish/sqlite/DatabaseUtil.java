package com.iyuba.conceptEnglish.sqlite;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.iyuba.conceptEnglish.lil.concept_other.download.FilePathUtil;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.setting.SettingConfig;
import com.iyuba.core.common.sqlite.ImportLibDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DatabaseUtil {

    private volatile static DatabaseUtil databaseUtil;

    private DatabaseUtil() {
    }

    public static DatabaseUtil getInstance() {
        if (databaseUtil == null) {
            synchronized (DatabaseUtil.class) {
                if (databaseUtil == null) {
                    databaseUtil = new DatabaseUtil();
                }
            }
        }
        return databaseUtil;
    }


    public void updateDatabase(Context context,Callback callback) {
        int currentVersion;
        int lastVersion;
        try {
            //最新版本号和上次版本号
            currentVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
            lastVersion = ConfigManager.Instance().loadInt("version");
        } catch (Exception e) {
            Log.e("异常", e.toString());
            lastVersion = 0;
            currentVersion = 0;
            e.printStackTrace();
        }

        /**
         * it s  up to the versionCode
         * so every upgrade ,it will be true
         */
        if (currentVersion > lastVersion) { // 首次使用设置默认界面背景常亮
            ImportDatabase db = new ImportDatabase(context);
            db.loadDatabase(ImportDatabase.DB_PATH + "/" + ImportDatabase.DB_NAME);
            ImportLibDatabase db2 = new ImportLibDatabase(context);
            db2.setPackageName(context.getPackageName());
            Log.e("包名", context.getPackageName());
            ImportLocalDatabase db3 = new ImportLocalDatabase(context);
            Log.e("数据库", new Date().toString() + "");
            db3.openLocalDatabase(ImportLocalDatabase.DB_PATH + "/" + ImportLocalDatabase.DB_NAME_LOCAL);
            db2.openDatabase(db2.getDBPath());

            //这里设置之前已经处理好的数据
            ConfigManager.Instance().putInt("mode", 1);
            ConfigManager.Instance().putInt("isvip", 0);
            ConfigManager.Instance().putBoolean("saying", true);
//            SettingConfig.Instance().setHighSpeed(false);
            SettingConfig.Instance().setSyncho(true);
            SettingConfig.Instance().setLight(true);
            SettingConfig.Instance().setAutoPlay(false);
            SettingConfig.Instance().setAutoStop(true);
            ConfigManager.Instance().putInt("version", currentVersion);
            ConfigManager.Instance().putBoolean("showChinese", true);
            ConfigManager.Instance().putBoolean("firstuse", true);
            ConfigManager.Instance().putInt("mode", 1);
            ConfigManager.Instance().putBoolean("autoplay", false);
            ConfigManager.Instance().putBoolean("autostop", true);
            ConfigManager.Instance().putString("applanguage", "zh");
//            ConfigManager.Instance().putString("media_saving_path", Constant.envir + "audio/");
            ConfigManager.Instance().putString("media_saving_path", FilePathUtil.getDownloadDirPath() + "audio/");
            ConfigManager.Instance().putInt("curBook", ConfigManager.Instance().loadInt("curBook",0));
            ConfigManager.Instance().putInt("lately_one", 1001);
            ConfigManager.Instance().putInt("lately_two", 2001);
            ConfigManager.Instance().putInt("lately_three", 3001);
            ConfigManager.Instance().putInt("lately_four", 4001);
            ConfigManager.Instance().putBoolean("is_exercising", false);
            ConfigManager.Instance().putString("cur_tab", "text");
            ConfigManager.Instance().putString("updateAD", "1970-01-01");
//            ConfigManager.Instance().putString("media_saving_path", Environment.getExternalStorageDirectory() + "/iyuba/concept2/" + "/audio/");
            ConfigManager.Instance().putString("media_saving_path", FilePathUtil.getDownloadDirPath() + "/audio/");
            ConfigManager.Instance().putInt("quesAppType", 115);
            String startTime = "";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            startTime = sdf.format(new Date());
            ConfigManager.Instance().putString("study_start_time", startTime);
            callback.scheduleDo();
        }
    }

    public interface Callback{
        void scheduleDo();
    }
}
