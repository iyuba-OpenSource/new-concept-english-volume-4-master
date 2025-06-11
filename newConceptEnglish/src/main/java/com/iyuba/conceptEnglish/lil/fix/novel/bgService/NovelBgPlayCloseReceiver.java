package com.iyuba.conceptEnglish.lil.fix.novel.bgService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.iyuba.conceptEnglish.lil.fix.common_fix.data.event.RefreshDataEvent;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.concept.bgService.ConceptBgPlayEvent;
import com.iyuba.conceptEnglish.util.ConceptApplication;

import org.greenrobot.eventbus.EventBus;

/**
 * @title: 故事-后台播放关闭接收器
 * @date: 2023/10/27 13:18
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class NovelBgPlayCloseReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //根据状态判断
//        String playStatus = intent.getStringExtra(StrLibrary.playStatus);
//        if (playStatus.equals(ConceptBgPlayEvent.event_audio_play)){
//            //开始播放
//            EventBus.getDefault().post(new ConceptBgPlayEvent(ConceptBgPlayEvent.event_audio_play));
//            EventBus.getDefault().post(new ConceptBgPlayEvent(ConceptBgPlayEvent.event_control_play));
//        }else if (playStatus.equals(ConceptBgPlayEvent.event_audio_pause)){
//            //暂停播放
//            EventBus.getDefault().post(new ConceptBgPlayEvent(ConceptBgPlayEvent.event_audio_pause));
//            EventBus.getDefault().post(new ConceptBgPlayEvent(ConceptBgPlayEvent.event_control_pause));
//        }

        //暂停播放
        EventBus.getDefault().post(new NovelBgPlayEvent(NovelBgPlayEvent.event_audio_pause));
        EventBus.getDefault().post(new NovelBgPlayEvent(NovelBgPlayEvent.event_control_pause));
        //退出app
        EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.existApp));
    }
}
