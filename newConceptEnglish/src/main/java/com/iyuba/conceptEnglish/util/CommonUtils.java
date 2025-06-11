package com.iyuba.conceptEnglish.util;

import android.content.Context;
import android.text.TextUtils;
import android.view.WindowManager;

import java.util.Locale;

/**
 * Created by Liuzhenli on 2016/9/19.
 */
public class CommonUtils {

    /**
     * 获取屏幕的宽
     *
     * @param context 上下文
     * @return 屏幕的高度
     */
    public static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return windowManager.getDefaultDisplay().getWidth();
    }

    /**
     * bookid 服务器和本地之间的对应
     *
     * @param bookLevel 服务器对应的bookid
     * @return 本地对应的bookid
     */
    public static int bookTranslateServiceToLocalForPass(String bookLevel) {
        switch (bookLevel) {
            case "278":
                return 5;
            case "279":
                return 6;
            case "280":
                return 7;
            case "281":
                return 8;
            case "282":
                return 9;
            case "283":
                return 10;
            case "284":
                return 11;
            case "285":
                return 12;
            case "286":
                return 13;
            case "287":
                return 14;
            case "288":
                return 15;
            case "289":
                return 16;
            default:
                return 100;
        }
    }


    /**
     * bookid 服务器和本地之间的对应
     *
     * @param bookLevel 本地对应的bookid
     * @return 服务器对应的bookid
     */
    public static int bookTranslateLocalForPassToService(int bookLevel) {
        switch (bookLevel) {
            case 5:
                return 278;
            case 6:
                return 279;
            case 7:
                return 280;
            case 8:
                return 281;
            case 9:
                return 282;
            case 10:
                return 283;
            case 11:
                return 284;
            case 12:
                return 285;
            case 13:
                return 286;
            case 14:
                return 287;
            case 15:
                return 288;
            case 16:
                return 289;
            default:
                return bookLevel;
        }
    }

    public static int getUnitFromTitle(String title) {
        int unit = 0;
        if (TextUtils.isEmpty(title)) {
            return unit;
        }
        title = title.trim().toLowerCase(Locale.ROOT);
        try {
            int start = title.indexOf("unit") + 4;
            //5 = 最大3位 unit + 1位空格 + 1位界限，有效值是三位
            int end = start + 5;
            String intStr = "";
            for (int i = start; i < end; i++) {
                if (title.charAt(i) >= 48 && title.charAt(i) <= 57) {
                    intStr += title.charAt(i);
                }
            }
            unit = Integer.parseInt(intStr);
        } catch (Exception e) {

        }
        return unit;

    }
}
