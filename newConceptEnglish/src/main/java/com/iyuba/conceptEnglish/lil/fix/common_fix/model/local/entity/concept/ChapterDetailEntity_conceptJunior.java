package com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.concept;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

/**
 * @title: 章节详情表-新概念青少版
 * @date: 2023/5/10 17:21
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
@Entity(primaryKeys = {"voaId","paraId","idIndex"})
public class ChapterDetailEntity_conceptJunior {

    @NonNull
    public long voaId;
    @NonNull
    public int paraId;
    @NonNull
    public int idIndex;

    public String imgPath;
    @NonNull
    public double endTiming;
    public String sentence_cn;
    public String imgWords;
    public String start_x;
    public String end_y;
    @NonNull
    public double timing;
    public String end_x;
    public String sentence;
    public String start_y;

    public ChapterDetailEntity_conceptJunior() {
    }

    @Ignore
    public ChapterDetailEntity_conceptJunior(long voaId, int paraId, int idIndex, String imgPath, double endTiming, String sentence_cn, String imgWords, String start_x, String end_y, double timing, String end_x, String sentence, String start_y) {
        this.voaId = voaId;
        this.paraId = paraId;
        this.idIndex = idIndex;
        this.imgPath = imgPath;
        this.endTiming = endTiming;
        this.sentence_cn = sentence_cn;
        this.imgWords = imgWords;
        this.start_x = start_x;
        this.end_y = end_y;
        this.timing = timing;
        this.end_x = end_x;
        this.sentence = sentence;
        this.start_y = start_y;
    }
}
