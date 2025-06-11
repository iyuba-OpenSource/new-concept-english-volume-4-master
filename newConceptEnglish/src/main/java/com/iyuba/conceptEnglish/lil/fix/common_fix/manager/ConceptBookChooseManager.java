package com.iyuba.conceptEnglish.lil.fix.common_fix.manager;

import android.content.SharedPreferences;

import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ResUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.SPUtil;
import com.iyuba.config.AppConfig;

/**
 * @title: 新概念的书籍管理
 * @date: 2023/4/27 09:47
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class ConceptBookChooseManager {
    private static final String TAG = "ConceptBookChooseManager";

    private static ConceptBookChooseManager instance;

    public static ConceptBookChooseManager getInstance(){
        if (instance==null){
            synchronized (ConceptBookChooseManager.class){
                if (instance==null){
                    instance = new ConceptBookChooseManager();
                }
            }
        }
        return instance;
    }

    //存储的信息
    private static final String SP_NAME = TAG;
    private static final String SP_BOOK_TYPE = "type";
    private static final String SP_BOOK_ID = "bookId";
    private static final String SP_BOOK_NAME = "bookName";

    private SharedPreferences preferences;

    private SharedPreferences getPreference(){
        if (preferences==null){
            preferences = SPUtil.getPreferences(ResUtil.getInstance().getContext(), SP_NAME);
        }
        return preferences;
    }

    public String getBookType(){
        return SPUtil.loadString(getPreference(),SP_BOOK_TYPE, AppConfig.concept_type);
    }

    public void setBookType(String bookType){
        SPUtil.putString(getPreference(),SP_BOOK_TYPE,bookType);
    }

    public int getBookId(){
        return SPUtil.loadInt(getPreference(),SP_BOOK_ID, AppConfig.concept_book_id);
    }

    public void setBookId(int bookId){
        SPUtil.putInt(getPreference(),SP_BOOK_ID,bookId);
    }

    public String getBookName(){
        return SPUtil.loadString(getPreference(),SP_BOOK_NAME, AppConfig.concept_book_name);
    }

    public void setBookName(String bookName){
        SPUtil.putString(getPreference(),SP_BOOK_NAME,bookName);
    }
}
