package com.iyuba.conceptEnglish.util;

import android.app.Application;

import androidx.core.app.NotificationManagerCompat;

/**
 * @desction:
 * @date: 2023/3/10 13:42
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class NotificationUtil {

    //是否开启了通知栏权限
    public static boolean isEnable(Application application){
        return NotificationManagerCompat.from(application).areNotificationsEnabled();
    }
}
