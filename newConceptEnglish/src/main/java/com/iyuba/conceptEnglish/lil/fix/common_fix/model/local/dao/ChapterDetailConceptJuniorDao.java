package com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.concept.ChapterDetailEntity_conceptJunior;

import java.util.List;

/**
 * @title:
 * @date: 2023/5/10 17:52
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
@Dao
public interface ChapterDetailConceptJuniorDao {

    //保存数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> saveData(List<ChapterDetailEntity_conceptJunior> list);

    //保存单个数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long saveSingleData(ChapterDetailEntity_conceptJunior junior);

    //查询本课程下的详情数据
    @Query("select * from ChapterDetailEntity_conceptJunior where voaId=:voaId order by paraId,idIndex asc")
    List<ChapterDetailEntity_conceptJunior> searchMultiDataByVoaId(String voaId);

    //查询单条详情数据
    @Query("select * from ChapterDetailEntity_conceptJunior where voaId=:voaId and paraId=:paraId and idIndex=:idIndex")
    ChapterDetailEntity_conceptJunior searchSingleDataByVoaId(String voaId,String paraId,String idIndex);
}
