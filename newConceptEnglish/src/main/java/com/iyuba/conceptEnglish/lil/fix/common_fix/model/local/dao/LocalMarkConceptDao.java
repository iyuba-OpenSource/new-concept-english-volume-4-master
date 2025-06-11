package com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.concept.LocalMarkEntity_concept;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.concept.LocalMarkEntity_conceptDownload;

import java.util.List;

/**
 * @title:
 * @date: 2023/10/31 10:35
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
@Dao
public interface LocalMarkConceptDao {

    //保存单个数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveSingleData(LocalMarkEntity_concept entity);

    //获取单个数据
    @Query("select * from LocalMarkEntity_concept where voaId=:voaId and lessonType=:type and userId=:userId")
    LocalMarkEntity_concept getSingleData(int voaId,String type,int userId);

    //获取所有的阅读数据
    @Query("select * from LocalMarkEntity_concept where userId=:userId and isRead=:readStatus")
    List<LocalMarkEntity_concept> getAllReadData(int userId,String readStatus);

    //获取所有的收藏数据
    @Query("select * from LocalMarkEntity_concept where userId=:userId and isCollect=:collectStatus")
    List<LocalMarkEntity_concept> getAllCollectData(int userId,String collectStatus);

    //更新阅读状态
    @Query("update LocalMarkEntity_concept set isRead=:readStatus where voaId=:voaId and lessonType=:type and userId=:userId")
    void updateReadStatus(int voaId,String type,int userId,String readStatus);

    //更新收藏状态
    @Query("update LocalMarkEntity_concept set isCollect=:collectStatus where voaId=:voaId and lessonType=:type and userId=:userId")
    void updateCollectStatus(int voaId,String type,int userId,String collectStatus);
}
