package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.word.wordList;

import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.BookChapterBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.WordBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.JuniorDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.junior.WordEntity_junior;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.util.DBTransUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.mvp.BasePresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * @title:
 * @date: 2023/5/11 18:30
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class WordListPresenter extends BasePresenter<WordListView> {

    @Override
    public void detachView() {
        super.detachView();
    }

    //获取章节或者单元的名称
    public String getIdName(String types,String id){
        switch (types){
            case TypeLibrary.BookType.junior_primary:
            case TypeLibrary.BookType.junior_middle:
                return "Unit\t"+id;
        }
        return "";
    }

    //获取当前章节的数据
    public BookChapterBean getChapterData(String types, String voaId){
        /*if (types.equals(TypeLibrary.BookType.bookworm)
                ||types.equals(TypeLibrary.BookType.newCamstory)
                ||types.equals(TypeLibrary.BookType.newCamstoryColor)){
            //小说
            ChapterEntity_novel novel = DataManager.getInstance().searchSingleNovelChapterFromDB(types, voaId);
            return DBTransUtil.novelToSingleChapterData(novel);
        }else if (types.equals(TypeLibrary.BookType.conceptFourUS)
                ||types.equals(TypeLibrary.BookType.conceptFourUK)
                ||types.equals(TypeLibrary.BookType.conceptJunior)){
            //新概念
            if (types.equals(TypeLibrary.BookType.conceptFourUS)
                    ||types.equals(TypeLibrary.BookType.conceptFourUK)){
                //全四册
                ChapterEntity_conceptFour conceptFour = DataManager.getInstance().searchSingleConceptFourChapterFromDB(types, voaId);
                return DBTransUtil.conceptFourToSingleChapterData(conceptFour);
            }else if (types.equals(TypeLibrary.BookType.conceptJunior)){
                //青少版
                ChapterEntity_conceptJunior conceptJunior = DataManager.getInstance().searchSingleConceptJuniorChapterFromDB(voaId);
                return DBTransUtil.conceptJuniorToSingleChapterData(conceptJunior);
            }
        }else*/
            if (types.equals(TypeLibrary.BookType.junior_primary)
                ||types.equals(TypeLibrary.BookType.junior_middle)){
            //中小学

        }
        return null;
    }

    //获取单词数据
    public List<WordBean> getWordData(String types, String bookId, String id){
        List<WordBean> list = new ArrayList<>();

        /*if (types.equals(TypeLibrary.BookType.conceptFour)
                ||types.equals(TypeLibrary.BookType.conceptJunior)){
            //新概念
            if (types.equals(TypeLibrary.BookType.conceptFour)){
                //全四册
                List<WordEntity_conceptFour> fourList = DataManager.getInstance().searchConceptFourWordByVoaIdFromDB(id);
                if (fourList!=null&&fourList.size()>0){
                    return DBTransUtil.conceptFourWordToWordData(types,fourList);
                }
            }else if (types.equals(TypeLibrary.BookType.conceptJunior)){
                //青少版
                List<WordEntity_conceptJunior> juniorList = DataManager.getInstance().searchConceptJuniorWordByUnitIdFromDB(id);
                if (juniorList!=null&&juniorList.size()>0){
                    return DBTransUtil.conceptJuniorWordToWordData(types,juniorList);
                }
            }
        }else*/
            if (types.equals(TypeLibrary.BookType.junior_primary)
                ||types.equals(TypeLibrary.BookType.junior_middle)){
            //中小学
            List<WordEntity_junior> juniorList = JuniorDataManager.searchWordByUnitIdFromDB(bookId,id);
            if (juniorList!=null&&juniorList.size()>0){
                return DBTransUtil.juniorWordToWordData(types,juniorList);
            }
        }

        return list;
    }
}
