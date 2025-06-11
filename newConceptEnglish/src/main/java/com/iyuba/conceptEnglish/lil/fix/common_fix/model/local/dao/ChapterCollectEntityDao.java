package com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.ChapterCollectEntity;

import java.util.List;

/**
 * @title:
 * @date: 2023/5/24 09:30
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
@Dao
public interface ChapterCollectEntityDao {

    //保存数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long saveData(ChapterCollectEntity entity);

    //保存多个数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] saveMultiData(List<ChapterCollectEntity> entities);

    //删除数据
    @Query("delete from ChapterCollectEntity where types=:types and voaId=:voaId")
    void deleteData(String types,String voaId);

    //获取当前用户的所有收藏数据
    @Query("select * from ChapterCollectEntity where userId=:userId")
    List<ChapterCollectEntity> searchMultiDataByUserId(String userId);

    //获取当前用户的当前类型的所有收藏数据
    @Query("select * from ChapterCollectEntity where userId=:userId and types=:types order by voaId asc")
    List<ChapterCollectEntity> searchMultiDataByUserId(String userId,String types);

    //获取当前章节的收藏数据
    @Query("select * from ChapterCollectEntity where userId=:userId and types=:types and voaId=:voaId")
    ChapterCollectEntity searchSingleData(String userId,String types,String voaId);
}
