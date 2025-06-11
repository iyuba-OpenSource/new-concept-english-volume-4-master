package com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.Setting_ReadLanguageEntity;

/**
 * @title:
 * @date: 2023/8/10 15:02
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
@Dao
public interface ReadLanguageDao {

    //保存单个数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertData(Setting_ReadLanguageEntity entity);

    //获取单个当前章节的数据
    @Query("select * from Setting_ReadLanguageEntity where types=:types and bookId=:bookId and voaId=:voaId and uid=:uid")
    Setting_ReadLanguageEntity getSingleData(String types, String bookId, String voaId, String uid);
}
