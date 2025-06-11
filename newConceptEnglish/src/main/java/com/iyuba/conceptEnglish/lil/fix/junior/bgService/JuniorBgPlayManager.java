package com.iyuba.conceptEnglish.lil.fix.junior.bgService;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ResUtil;
import com.iyuba.core.common.util.ToastUtil;

/**
 * @title: 中小学-后台播放管理
 * @date: 2023/10/27 11:07
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class JuniorBgPlayManager {
    private static JuniorBgPlayManager instance;
    private JuniorBgPlayService playService;
    private ServiceConnection connection;

    public static JuniorBgPlayManager getInstance(){
        if (instance==null){
            synchronized (JuniorBgPlayManager.class){
                if (instance==null){
                    instance = new JuniorBgPlayManager();
                }
            }
        }
        return instance;
    }

    public JuniorBgPlayManager(){
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                JuniorBgPlayService.MyJuniorPlayBinder binder = (JuniorBgPlayService.MyJuniorPlayBinder) service;
                playService = binder.getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
    }

    //获取服务
    public JuniorBgPlayService getPlayService(){
        if (playService==null){
            ToastUtil.showToast(ResUtil.getInstance().getContext(), "服务未进行初始化");
            return null;
        }

        return playService;
    }

    //获取链接
    public ServiceConnection getConnection(){
        return connection;
    }
}
