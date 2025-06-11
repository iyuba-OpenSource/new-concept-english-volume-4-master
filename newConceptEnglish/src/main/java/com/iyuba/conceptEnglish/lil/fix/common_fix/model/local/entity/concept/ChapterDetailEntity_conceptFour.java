package com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.concept;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

/**
 * @title: 章节详情表-新概念全四册
 * @date: 2023/5/8 18:14
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
@Entity(primaryKeys = {"types","voaid","Paraid","IdIndex"})
public class ChapterDetailEntity_conceptFour {

    @NonNull
    public String types;//类型
    @NonNull
    public long voaid;
    @NonNull
    public int Paraid;
    @NonNull
    public int IdIndex;//行数

    public String EndTiming;
    public String Timing;
    public String Sentence_cn;
    public String Sentence;

    public ChapterDetailEntity_conceptFour() {
    }

    @Ignore
    public ChapterDetailEntity_conceptFour(@NonNull String types, @NonNull long voaid, @NonNull int paraid, @NonNull int idIndex, String endTiming, String timing, String sentence_cn, String sentence) {
        this.types = types;
        this.voaid = voaid;
        Paraid = paraid;
        IdIndex = idIndex;
        EndTiming = endTiming;
        Timing = timing;
        Sentence_cn = sentence_cn;
        Sentence = sentence;
    }
}
