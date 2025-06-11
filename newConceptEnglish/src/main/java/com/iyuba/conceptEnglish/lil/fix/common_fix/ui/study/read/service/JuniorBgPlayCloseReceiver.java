//package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.read.service;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//
//import org.greenrobot.eventbus.EventBus;
//
///**
// * @title: 新概念-后台播放关闭接收器
// * @date: 2023/10/27 13:18
// * @author: liang_mu
// * @email: liang.mu.cn@gmail.com
// * @description:
// */
//public class JuniorBgPlayCloseReceiver extends BroadcastReceiver {
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        //停止播放
//        EventBus.getDefault().post(new JuniorBgPlayEvent(JuniorBgPlayEvent.event_audio_pause));
//        EventBus.getDefault().post(new JuniorBgPlayEvent(JuniorBgPlayEvent.event_control_pause));
////        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
////        manager.cancelAll();
//        JuniorBgPlayManager.getInstance().getPlayService().stopForeground(true);
//        //开始计时
//    }
//}
