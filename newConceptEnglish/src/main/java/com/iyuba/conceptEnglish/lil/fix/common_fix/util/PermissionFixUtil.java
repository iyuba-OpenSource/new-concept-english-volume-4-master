package com.iyuba.conceptEnglish.lil.fix.common_fix.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * 在android15及以上的权限请求操作
 * <p>
 * 因为xxpermission在android15上存在存储权限的申请问题，因此直接使用android的权限申请使用
 */
public class PermissionFixUtil {
    //首页-下载功能的存储权限
    public static final int concept_home_downloadFile_code = 101;
    //新概念-评测的存储权限+录音权限
    public static final int concept_eval_recordAudio_code = 102;
    //中小学/小说-评测的存储权限+录音权限
    public static final int junior_eval_recordAudio_code = 103;
    //中小学-配音的存储权限+录音权限
    public static final int junior_talkShow_recordAudio_code = 104;
    //新概念-纠音的录音+存储权限
    public static final int concept_fix_recordAudio_code = 105;
    //中小学-纠音的录音权限+存储权限
    public static final int junior_fix_recordAudio_code = 106;


    //判断是否需要权限操作
    public static boolean isPermissionOk(Activity context, int code) {
        switch (code) {
            case concept_home_downloadFile_code:
                //新概念-首页-下载
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    new AlertDialog.Builder(context)
                            .setTitle("权限申请")
                            .setMessage("当前功能需要 存储权限(访问视频、音频权限)\n\n存储权限：用于将文件保存在本地后提供给当前功能使用")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, concept_home_downloadFile_code);
                                }
                            }).setNegativeButton("取消", null)
                            .setCancelable(false)
                            .create().show();
                    return false;
                }
                return true;
            case concept_eval_recordAudio_code://新概念-评测-录音
            case concept_fix_recordAudio_code://新概念-纠音-录音
            case junior_eval_recordAudio_code://中小学-评测-录音
            case junior_fix_recordAudio_code://中小学-纠音-录音
            case junior_talkShow_recordAudio_code://中小学-配音-录音
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    new AlertDialog.Builder(context)
                            .setTitle("权限申请")
                            .setMessage("当前功能需要 存储权限(访问视频、音频权限) 和 录音权限(麦克风权限)\n\n存储权限：用于将文件保存在本地后提供给当前功能使用\n录音权限：用于录音并通过服务器校验后提供给当前功能使用")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, code);
                                }
                            }).setNegativeButton("取消", null)
                            .setCancelable(false)
                            .create().show();
                    return false;
                }
                return true;
            default:
                return true;

        }
    }

    //是否使用存储权限操作
    public static boolean isCanUseExternalPermission(Context context){
        if (Build.VERSION.SDK_INT<35){
            return ContextCompat.checkSelfPermission(context,Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED;
        }else {
            return true;
        }
    }
}
