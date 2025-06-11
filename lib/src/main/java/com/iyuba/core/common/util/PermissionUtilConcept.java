package com.iyuba.core.common.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class PermissionUtilConcept {
    private static volatile PermissionUtilConcept permissionUtil;

    private PermissionUtilConcept() {
    }

    public static PermissionUtilConcept getInstance() {
        if (permissionUtil == null) {
            synchronized (PermissionUtilConcept.class) {
            }
            if (permissionUtil == null) {
                permissionUtil = new PermissionUtilConcept();
            }
        }
        return permissionUtil;
    }

    public void checkPermission(Activity activity,Context context, String[] permissions, Callback callback,int requestCode) {
        boolean agree = true;
        for (String strPermission : permissions) {
            if (ContextCompat.checkSelfPermission(context, strPermission) != PackageManager.PERMISSION_GRANTED) {
                agree = false;
            }
        }
        if (!agree){
            //请求权限
            ActivityCompat.requestPermissions(activity,permissions,requestCode);
        }else {
            //已经同意过了，不要重新申请
            callback.agreeP();
        }
    }

    public void checkPermissionAboutFragment(Fragment fragment,Context context, String[] permissions, Callback callback, int requestCode) {
        boolean agree = true;
        for (String strPermission : permissions) {
            if (ContextCompat.checkSelfPermission(context, strPermission) != PackageManager.PERMISSION_GRANTED) {
                agree = false;
            }
        }
        if (!agree){
            //请求权限
            fragment.requestPermissions(permissions,requestCode);
        }else {
            //已经同意过了，不要重新申请
            callback.agreeP();
        }
    }

    public void checkPermissionResult(int[] grantResults,Callback callback){
        if (grantResults ==null || grantResults.length == 0){
            callback.notAgreeP();
            return;
        }
        boolean agree = true;
        for (int i=0;i<grantResults.length;i++){
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                agree = false;
            }
        }
        if (agree){
            callback.agreeP();
        }else {
            callback.notAgreeP();
        }
    }

    public interface Callback {
        void agreeP();
        void notAgreeP();
    }
}
