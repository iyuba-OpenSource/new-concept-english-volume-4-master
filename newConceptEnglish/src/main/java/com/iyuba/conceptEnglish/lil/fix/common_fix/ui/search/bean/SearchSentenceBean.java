package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.search.bean;

/**
 * @title: 句子的数据
 * @date: 2023/11/17 11:35
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class SearchSentenceBean {

    private int voaId;
    private String paraId;
    private String idIndex;

    private String lessonType;
    private String title;
    private String titleCn;

    private String audioUrl;
    private long startTime;
    private long endTime;

    public SearchSentenceBean(int voaId, String paraId, String idIndex, String lessonType, String title, String titleCn, String audioUrl, long startTime, long endTime) {
        this.voaId = voaId;
        this.paraId = paraId;
        this.idIndex = idIndex;
        this.lessonType = lessonType;
        this.title = title;
        this.titleCn = titleCn;
        this.audioUrl = audioUrl;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getVoaId() {
        return voaId;
    }

    public String getParaId() {
        return paraId;
    }

    public String getIdIndex() {
        return idIndex;
    }

    public String getLessonType() {
        return lessonType;
    }

    public String getTitle() {
        return title;
    }

    public String getTitleCn() {
        return titleCn;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }
}
