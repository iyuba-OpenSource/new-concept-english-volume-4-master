package com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

/**
 * @title: 阅读界面语言显示表
 * @date: 2023/8/10 14:58
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
@Entity(primaryKeys = {"types","bookId","voaId","uid"})
public class Setting_ReadLanguageEntity {

    @NonNull
    public String types;
    @NonNull
    public String bookId;
    @NonNull
    public String voaId;
    @NonNull
    public String uid;

    public String languageType;//默认为英文

    public Setting_ReadLanguageEntity() {
    }

    @Ignore
    public Setting_ReadLanguageEntity(String types, String bookId, String voaId, String uid, String languageType) {
        this.types = types;
        this.bookId = bookId;
        this.voaId = voaId;
        this.uid = uid;
        this.languageType = languageType;
    }
}
