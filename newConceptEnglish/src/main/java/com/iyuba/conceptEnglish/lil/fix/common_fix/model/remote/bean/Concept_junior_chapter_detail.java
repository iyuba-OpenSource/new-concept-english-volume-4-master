package com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean;

import java.io.Serializable;

/**
 * @title: 新概念青少版-章节详情
 * @date: 2023/5/10 17:22
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class Concept_junior_chapter_detail implements Serializable {
    private static final long serialVersionUID = 4669759119850905146L;


    /**
     * ImgPath : /202111/321019_1.jpg
     * EndTiming : 9
     * ParaId : 1
     * IdIndex : 1
     * sentence_cn : 第四单元 是时候吃午餐了！第一课
     * ImgWords : (149,148),(592,243)
     * Start_x : 149
     * End_y : 243
     * Timing : 0
     * End_x : 592
     * Sentence : Unit 4 It's time for lunch! Lesson 1
     * Start_y : 148
     */

    private String ImgPath;
    private double EndTiming;
    private String ParaId;
    private String IdIndex;
    private String sentence_cn;
    private String ImgWords;
    private String Start_x;
    private String End_y;
    private double Timing;
    private String End_x;
    private String Sentence;
    private String Start_y;

    public String getImgPath() {
        return ImgPath;
    }

    public double getEndTiming() {
        return EndTiming;
    }

    public String getParaId() {
        return ParaId;
    }

    public String getIdIndex() {
        return IdIndex;
    }

    public String getSentence_cn() {
        return sentence_cn;
    }

    public String getImgWords() {
        return ImgWords;
    }

    public String getStart_x() {
        return Start_x;
    }

    public String getEnd_y() {
        return End_y;
    }

    public double getTiming() {
        return Timing;
    }

    public String getEnd_x() {
        return End_x;
    }

    public String getSentence() {
        return Sentence;
    }

    public String getStart_y() {
        return Start_y;
    }
}
