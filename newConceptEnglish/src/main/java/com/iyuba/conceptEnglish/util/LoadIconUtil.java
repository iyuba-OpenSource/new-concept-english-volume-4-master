package com.iyuba.conceptEnglish.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;

import com.iyuba.conceptEnglish.R;
import com.iyuba.configation.Constant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class LoadIconUtil {

    public static void loadIcon(Context context) {
        loadCommonIcon(context);
        loadWxMiniProgramIcon(context);
    }

    public static void loadCommonIcon(Context context) {
//        File icon = new File(Constant.iconAddr);
        File icon = new File(getAppLogoPath(context));
        Bitmap bitmap;
        if (icon.exists()) {
            icon.delete();
        }
        if (!icon.exists()) {
            try {
                if (!icon.getParentFile().exists()){
                    icon.getParentFile().mkdirs();
                }
                icon.createNewFile();

                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon);
                if (bitmap == null) {
                    return;
                }

                FileOutputStream out = new FileOutputStream(icon);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static void loadWxMiniProgramIcon(Context context) {
//        File icon = new File(Constant.iconWxMiniProgramAddr);
        File icon = new File(getAppLogoSmallPath(context));
        Bitmap bitmap;
        if (icon.exists()) {
            icon.delete();
        }
        if (!icon.exists()) {
            try {
                if (!icon.getParentFile().exists()){
                    icon.getParentFile().mkdirs();
                }
                icon.createNewFile();

                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_wx_mini_program);
                if (bitmap == null) {
                    return;
                }

                FileOutputStream out = new FileOutputStream(icon);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    //获取图标的保存路径
    public static String getAppLogoPath(Context context){
        String localPath = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            localPath = context.getExternalFilesDir(null).getPath()+"/iyuba/icon.png";
        }else {
            localPath = Environment.getExternalStorageDirectory().getPath()+"/iyuba/icon.png";
        }
        return localPath;
    }

    //获取小图标的保存路径
    public static String getAppLogoSmallPath(Context context){
        String localPath = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            localPath = context.getExternalFilesDir(null).getPath()+"/iyuba/iconWxMiniProgram.png";
        }else {
            localPath = Environment.getExternalStorageDirectory().getPath()+"/iyuba/iconWxMiniProgram.png";
        }
        return localPath;
    }
}
