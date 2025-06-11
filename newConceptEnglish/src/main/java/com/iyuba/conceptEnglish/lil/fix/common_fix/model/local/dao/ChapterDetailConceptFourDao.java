package com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.concept.ChapterDetailEntity_conceptFour;

import java.util.List;

/**
 * @title:
 * @date: 2023/5/8 18:16
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
@Dao
public interface ChapterDetailConceptFourDao {

    //保存数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> saveData(List<ChapterDetailEntity_conceptFour> list);

    //保存单个数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long saveSingleData(ChapterDetailEntity_conceptFour conceptFour);

    //查询本课程下的详情数据
    @Query("select * from ChapterDetailEntity_conceptFour where types=:types and voaid=:voaId order by Paraid,IdIndex asc")
    List<ChapterDetailEntity_conceptFour> searchMultiDataByVoaId(String types,String voaId);

    //查询单个详情数据
    @Query("select * from ChapterDetailEntity_conceptFour where types=:types and voaid=:voaId and Paraid=:paraId and IdIndex=:indexId")
    ChapterDetailEntity_conceptFour searchSingleDataByVoaId(String types,String voaId,String paraId,String indexId);
}
