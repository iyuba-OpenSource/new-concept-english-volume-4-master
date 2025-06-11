package com.iyuba.conceptEnglish.lil.fix.concept.bgService;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.google.android.exoplayer2.ExoPlayer;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.ConceptDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ResUtil;
import com.iyuba.core.common.util.ToastUtil;

/**
 * @title: 新概念-后台播放管理
 * @date: 2023/10/27 11:07
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class ConceptBgPlayManager {
    private static ConceptBgPlayManager instance;
    private ConceptBgPlayService playService;
    private ServiceConnection connection;

    public static ConceptBgPlayManager getInstance(){
        if (instance==null){
            synchronized (ConceptBgPlayManager.class){
                if (instance==null){
                    instance = new ConceptBgPlayManager();
                }
            }
        }
        return instance;
    }

    public ConceptBgPlayManager(){
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                ConceptBgPlayService.MyConceptPlayBinder binder = (ConceptBgPlayService.MyConceptPlayBinder) service;
                playService = binder.getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
    }

    //获取服务
    public ConceptBgPlayService getPlayService(){
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
