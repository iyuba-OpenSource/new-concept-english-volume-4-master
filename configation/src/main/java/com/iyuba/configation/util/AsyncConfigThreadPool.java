package com.iyuba.configation.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 异步加载方法类
 */
public class AsyncConfigThreadPool {
    private static ExecutorService singleService= Executors.newSingleThreadExecutor();

    public static void run(Runnable runnable){
        singleService.execute(runnable);
    }
}
