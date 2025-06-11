package com.iyuba.core.common.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

    private static Toast toast;

    public static void show(Context context, String text) {
        showToast(context,text);
    }
    public static void showToast(Context context, String text) {

        try {
            if (toast != null) {
                toast.cancel();
            }
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showToast(Context context, int text) {
        try { if (toast != null) {
                toast.cancel();
            }
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void showLongToast(Context context, String text) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        toast.show();
    }
}
