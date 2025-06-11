package com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.concept;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

/**
 * @title: 本地记录表-记录当前课程的类型
 * @date: 2023/10/31 10:20
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 * 前人栽树，后人乘凉；前人挖坑，后人骂娘
 * tnnd，不标明相关章节的类型，你从这里找个屁啊，怎么从本地篇目跳到界面上去的，测试吃干饭的啊
 */
@Entity(primaryKeys = {"voaId","lessonType"})
public class LocalMarkEntity_conceptDownload {

    @NonNull
    public int voaId;
    @NonNull
    public String lessonType;//英音、美音、青少版
    @NonNull
    public int userId;

    public String isDownload;//下载状态(-1：正在下载中，0:未下载，1:已经下载)

    public int position;//保存当前数据的位置

    public LocalMarkEntity_conceptDownload() {
    }

    @Ignore
    public LocalMarkEntity_conceptDownload(int voaId, @NonNull String lessonType, int userId, String isDownload, int position) {
        this.voaId = voaId;
        this.lessonType = lessonType;
        this.userId = userId;
        this.isDownload = isDownload;
        this.position = position;
    }

    @Override
    public String toString() {
        return "LocalMarkEntity_conceptDownload{" +
                "voaId=" + voaId +
                ", lessonType='" + lessonType + '\'' +
                ", userId=" + userId +
                ", isDownload='" + isDownload + '\'' +
                ", position=" + position +
                '}';
    }
}
