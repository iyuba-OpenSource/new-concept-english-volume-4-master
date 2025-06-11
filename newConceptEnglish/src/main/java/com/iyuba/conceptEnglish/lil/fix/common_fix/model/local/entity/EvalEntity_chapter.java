package com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

/**
 * @title: 评测表-章节
 * @date: 2023/5/4 18:17
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
@Entity(primaryKeys = {"types","voaId","paraId","indexId","uid"})
public class EvalEntity_chapter {

    public String sentence;
    @NonNull
    public double scores;
    @NonNull
    public double total_score;
    public String filepath;
    public String url;
    public String wordList;

    //本地数据
    @NonNull
    public String types;//类型
    @NonNull
    public String voaId;//章节的id
    @NonNull
    public String paraId;
    @NonNull
    public String indexId;
    @NonNull
    public String uid;//用户id

    public EvalEntity_chapter() {
    }

    @Ignore
    public EvalEntity_chapter(String sentence, double scores, double total_score, String filepath, String url, String wordList, @NonNull String types, @NonNull String voaId, @NonNull String paraId, @NonNull String indexId, @NonNull String uid) {
        this.sentence = sentence;
        this.scores = scores;
        this.total_score = total_score;
        this.filepath = filepath;
        this.url = url;
        this.wordList = wordList;
        this.types = types;
        this.voaId = voaId;
        this.paraId = paraId;
        this.indexId = indexId;
        this.uid = uid;
    }
}
