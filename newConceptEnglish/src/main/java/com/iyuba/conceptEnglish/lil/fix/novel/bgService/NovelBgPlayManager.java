package com.iyuba.conceptEnglish.lil.fix.novel.bgService;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ResUtil;
import com.iyuba.core.common.util.ToastUtil;

/**
 * @title: 故事-后台播放管理
 * @date: 2023/10/27 11:07
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class NovelBgPlayManager {
    private static NovelBgPlayManager instance;
    private NovelBgPlayService playService;
    private ServiceConnection connection;

    public static NovelBgPlayManager getInstance(){
        if (instance==null){
            synchronized (NovelBgPlayManager.class){
                if (instance==null){
                    instance = new NovelBgPlayManager();
                }
            }
        }
        return instance;
    }

    public NovelBgPlayManager(){
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                NovelBgPlayService.MyNovelPlayBinder binder = (NovelBgPlayService.MyNovelPlayBinder) service;
                playService = binder.getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
    }

    //获取服务
    public NovelBgPlayService getPlayService(){
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
