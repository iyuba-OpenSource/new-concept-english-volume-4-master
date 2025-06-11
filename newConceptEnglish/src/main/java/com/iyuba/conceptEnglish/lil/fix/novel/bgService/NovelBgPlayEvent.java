package com.iyuba.conceptEnglish.lil.fix.novel.bgService;

/**
 * @title: 故事-后台播放事件
 * @date: 2023/10/27 13:35
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class NovelBgPlayEvent {

    /******************类型******************/
    public static final String event_audio_prepareFinish = "novel_audio_prepareFinish";//音频-加载完成
    public static final String event_audio_completeFinish = "novel_audio_completeFinish";//音频-播放完成
    public static final String event_audio_play = "novel_audio_play";//音频-播放
    public static final String event_audio_pause = "novel_audio_pause";//音频-暂停
    public static final String event_audio_stop = "novel_audio_stop";//音频-停止
    public static final String event_audio_switch = "novel_switch";//音频-切换
    public static final String event_control_play = "novel_control_play";//控制栏-播放
    public static final String event_control_pause = "novel_control_pause";//控制栏-暂停
    public static final String event_control_hide = "novel_control_hide";//控制栏-隐藏
    public static final String event_data_refresh = "novel_data_refresh";//数据-刷新

    /*******************事件*****************/
    private String showType;
    private int dataIndex;

    public NovelBgPlayEvent(String showType) {
        this.showType = showType;
    }

    public NovelBgPlayEvent(String showType, int dataIndex) {
        this.showType = showType;
        this.dataIndex = dataIndex;
    }

    public String getShowType() {
        return showType;
    }

    public int getDataIndex() {
        return dataIndex;
    }
}
