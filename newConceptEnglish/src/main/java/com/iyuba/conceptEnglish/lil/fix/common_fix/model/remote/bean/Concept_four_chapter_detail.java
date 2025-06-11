package com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean;

import java.io.Serializable;

/**
 * @title: 新概念全四册-章节详情
 * @date: 2023/5/8 18:12
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class Concept_four_chapter_detail implements Serializable {
    private static final long serialVersionUID = -8749143927062541050L;

    /**
     * voaid : 3001
     * EndTiming : 4.1
     * Paraid : 1
     * IdIndex : 1
     * Timing : 0.5
     * Sentence_cn : 逃遁的美洲狮
     * Sentence : --- lesson 1  A puma at large
     */

    private String voaid;
    private String Paraid;
    private String IdIndex;//行数

    private String EndTiming;
    private String Timing;
    private String Sentence_cn;
    private String Sentence;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getVoaid() {
        return voaid;
    }

    public String getParaid() {
        return Paraid;
    }

    public String getIdIndex() {
        return IdIndex;
    }

    public String getEndTiming() {
        return EndTiming;
    }

    public String getTiming() {
        return Timing;
    }

    public String getSentence_cn() {
        return Sentence_cn;
    }

    public String getSentence() {
        return Sentence;
    }
}
