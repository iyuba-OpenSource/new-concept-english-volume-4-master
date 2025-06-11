package com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.WordProgressBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.concept.WordEntity_conceptJunior;

import java.util.List;

/**
 * @title:
 * @date: 2023/5/11 16:54
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
@Dao
public interface WordConceptJuniorDao {

    //保存数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> saveData(List<WordEntity_conceptJunior> list);

    //保存单个数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long saveSingleData(WordEntity_conceptJunior concept);

    //查询本单元下的单词数据
    @Query("select * from WordEntity_conceptJunior where unit_id=:unitId order by position asc")
    List<WordEntity_conceptJunior> searchWordByUnitId(String unitId);

    //查询本章节下的单词数据
    @Query("select * from WordEntity_conceptJunior where voaId=:voaId order by position asc")
    List<WordEntity_conceptJunior> searchWordByVoaId(String voaId);

    //查询本书籍下的根据unitId分组的单词数据
    @Query("select book_id as bookId,unit_id as id,voaId,unit_id as lessonName,count(*) as size from WordEntity_conceptJunior where book_id=:bookId group by unit_id order by unit_id asc")
    List<WordProgressBean> searchWordByBookIdGroup(String bookId);
}
