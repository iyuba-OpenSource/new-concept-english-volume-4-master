package com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.WordProgressBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.concept.WordEntity_conceptFour;

import java.util.List;

/**
 * @title:
 * @date: 2023/5/11 16:54
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
@Dao
public interface WordConceptFourDao {

    //保存数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> saveData(List<WordEntity_conceptFour> list);

    //保存单个数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long saveSingleData(WordEntity_conceptFour concept);

    //查询本章节下的单词数据
    @Query("select * from WordEntity_conceptFour where voaId=:voaId order by position asc")
    List<WordEntity_conceptFour> searchWordByVoaId(String voaId);

    //以voaId分组的形式查询本书籍下单词的数据(这里的position没啥用)
    @Query("select bookId,voaId as id,voaId,position as lessonName,count(*) as size from WordEntity_conceptFour where bookId=:bookId group by voaId order by voaId asc")
    List<WordProgressBean> searchWordByBookIdGroup(String bookId);
}
