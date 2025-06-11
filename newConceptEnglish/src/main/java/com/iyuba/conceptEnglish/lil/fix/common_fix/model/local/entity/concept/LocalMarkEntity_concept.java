package com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.concept;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

/**
 * @title: 本地记录表-记录当前课程的类型
 * @date: 2023/10/31 10:23
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description: 这里除了记录当前课程类型之外
 */
@Entity(primaryKeys = {"voaId","lessonType","userId"})
public class LocalMarkEntity_concept {

    @NonNull
    public int voaId;
    @NonNull
    public String lessonType;//英音、美音、青少版
    @NonNull
    public int userId;

    public String isRead;//阅读状态-1为已经阅读、0为没有阅读
    public String isCollect;//收藏状态-1为已经收藏、0为没有收藏

    public int position;//保存当前数据的位置

    public LocalMarkEntity_concept() {
    }

    @Ignore
    public LocalMarkEntity_concept(int voaId, @NonNull String lessonType, int userId, String isRead, String isCollect, int position) {
        this.voaId = voaId;
        this.lessonType = lessonType;
        this.userId = userId;
        this.isRead = isRead;
        this.isCollect = isCollect;
        this.position = position;
    }
}
