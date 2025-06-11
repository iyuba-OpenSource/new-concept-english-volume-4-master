package com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

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
public interface LocalMarkConceptDownloadDao {

    //保存单个数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveSingleData(LocalMarkEntity_conceptDownload entity);

    //获取单个数据
    @Query("select * from LocalMarkEntity_conceptDownload where voaId=:voaId and lessonType=:type and userId=:userId")
    LocalMarkEntity_conceptDownload getSingleData(int voaId,String type,int userId);

    //获取所有的下载的数据
    @Query("select * from LocalMarkEntity_conceptDownload where isDownload=:downloadStatus and userId=:userId")
    List<LocalMarkEntity_conceptDownload> getAllDownloadData(String downloadStatus,int userId);

    //更新下载状态
    @Query("update LocalMarkEntity_conceptDownload set isDownload=:downloadStatus where voaId=:voaId and lessonType=:type and userId=:userId")
    void updateDownloadStatus(int voaId,String type,int userId,String downloadStatus);
}
