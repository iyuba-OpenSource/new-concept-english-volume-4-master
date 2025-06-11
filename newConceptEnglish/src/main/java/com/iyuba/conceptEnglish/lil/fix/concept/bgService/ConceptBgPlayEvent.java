package com.iyuba.conceptEnglish.lil.fix.concept.bgService;

/**
 * @title: 新概念-后台播放事件
 * @date: 2023/10/27 13:35
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class ConceptBgPlayEvent {

    /******************类型******************/
    public static final String event_audio_prepareFinish = "concept_audio_prepareFinish";//音频-加载完成
    public static final String event_audio_completeFinish = "concept_audio_completeFinish";//音频-播放完成
    public static final String event_audio_play = "concept_audio_play";//音频-播放
    public static final String event_audio_pause = "concept_audio_pause";//音频-暂停
    public static final String event_audio_stop = "concept_audio_stop";//音频-停止
    public static final String event_audio_switch = "concept_switch";//音频-切换
    public static final String event_control_play = "concept_control_play";//控制栏-播放
    public static final String event_control_pause = "concept_control_pause";//控制栏-暂停
    public static final String event_control_hide = "concept_control_hide";//控制栏-隐藏
    public static final String event_data_refresh = "concept_data_refresh";//数据-刷新
    public static final String event_data_error = "concept_data_error";//数据-错误

    /*******************事件*****************/
    private String showType;
    private String showMsg;

    public ConceptBgPlayEvent(String showType) {
        this.showType = showType;
    }

    public ConceptBgPlayEvent(String showType, String showMsg) {
        this.showType = showType;
        this.showMsg = showMsg;
    }

    public String getShowType() {
        return showType;
    }

    public String getShowMsg() {
        return showMsg;
    }
}
