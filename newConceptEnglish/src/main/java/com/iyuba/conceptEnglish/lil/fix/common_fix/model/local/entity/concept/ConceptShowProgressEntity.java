package com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.concept;

import androidx.room.Entity;

/**
 * @title: 新概念进度展示数据表
 * @date: 2023/11/22 10:28
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class ConceptShowProgressEntity {

    public int voaId;
    public String lessonType;
    public int userId;

    public int lessonProgress;//音频进度（百分制）
    public int wordProgress;//单词进度（数量）
    public int evalProgress;//评测进度(数量)
    public int exerciseProgress;//练习进度(数量)
    public int mocProgress;//微课进度(数量)
}
