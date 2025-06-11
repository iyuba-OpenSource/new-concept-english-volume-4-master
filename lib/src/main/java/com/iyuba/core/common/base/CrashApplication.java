package com.iyuba.core.common.base;

/**
 * 程序崩溃后操作
 *
 * @version 1.0
 * @author 陈彤
 * 修改日期    2014.3.29
 */

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.multidex.MultiDexApplication;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.iyuba.configation.BuildConfig;
import com.iyuba.configation.Constant;
import com.iyuba.configation.RuntimeManager;
import com.iyuba.core.InfoHelper;
import com.iyuba.core.common.data.local.DubDBManager;
import com.iyuba.dlex.bizs.DLManager;
import com.iyuba.imooclib.IMooc;
import com.iyuba.module.dl.BasicDL;
import com.iyuba.module.favor.BasicFavor;
import com.iyuba.module.movies.IMovies;
import com.iyuba.module.movies.data.local.db.DBManager;
import com.iyuba.widget.unipicker.IUniversityPicker;

import java.util.LinkedList;
import java.util.List;

import personal.iyuba.personalhomelibrary.PersonalHome;
import timber.log.Timber;

public class CrashApplication extends MultiDexApplication {
    private static CrashApplication mInstance = null;
    private List<Activity> activityList = new LinkedList<Activity>();
    private RequestQueue queue;
    private static Context context;
    private static String AUTHORITY = "";

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        RuntimeManager.setApplicationContext(getApplicationContext());
        RuntimeManager.setApplication(this);
        queue = Volley.newRequestQueue(this);
        mInstance = this;
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        initImageLoader();
        InfoHelper.init(this);
        DubDBManager.init(this);
        DLManager.init(this, 5);//下载
        DBManager.init(this);
        IUniversityPicker.init(this);

        /**
         * 新版个人中心
         */
        PersonalHome.init(this);
        PersonalHome.setEnableShare(true);
        /**
         * 通用模块
         */
        IMovies.init(this, Constant.APPID, Constant.AppName); //美剧
        IMooc.init(this, Constant.APPID, Constant.AppName); //微课
        IMooc.setDebug(BuildConfig.DEBUG);
        IMooc.setEnableShare(true);

        BasicDL.init(this);//下载
        BasicFavor.init(this, Constant.APPID);//收藏
        AUTHORITY=getContext().getPackageName()+".file.download";

    }


    // 程序加入运行列表
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    // 程序退出
    public void exit() {
        for (Activity activity : activityList) {
            activity.finish();
            Log.d("当前退出的界面0002", getClass().getName());
        }

        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    public static CrashApplication getInstance() {
        return mInstance;
    }

    // 全局volley请求队列队列
    public RequestQueue getQueue() {
        return queue;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        MultiDex.install(this) ;
    }

    private void initImageLoader() {
        // 初始化ImageLoader
        /*@SuppressWarnings("deprecation")
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showStubImage(com.iyuba.lib.R.drawable.nearby_no_icon) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(com.iyuba.lib.R.drawable.nearby_no_icon) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(com.iyuba.lib.R.drawable.nearby_no_icon) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
                // .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
                .build(); // 创建配置过得DisplayImageOption对象

        File cacheDir = StorageUtils.getCacheDirectory(getApplicationContext());
        Log.e("cache", cacheDir.toString());
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(options)
                .memoryCacheExtraOptions(480, 800) // default = device screen dimensions
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .threadPoolSize(3)//线程池内加载的数量
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // You can pass your own memory cache implementation/你可以通过自己的内存缓存实现
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .memoryCache(new LruMemoryCache(5 * 1024 * 1024))
                .memoryCacheSize(5 * 1024 * 1024)
                .memoryCacheSizePercentage(13) // default
                .diskCache(new UnlimitedDiskCache(cacheDir)) // default
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .build();
        ImageLoader.getInstance().init(config);*/
    }


    public static String getAUTHORITY() {
        return AUTHORITY;
    }

    public static void setAUTHORITY(String AUTHORITY) {
        CrashApplication.AUTHORITY = AUTHORITY;
    }

    @Override
    public void onTerminate() {
        // 程序终止的时候执行

        super.onTerminate();
    }
}