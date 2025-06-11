package com.iyuba.conceptEnglish.lil.fix.common_fix.manager;

import android.content.SharedPreferences;

import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ResUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.SPUtil;
import com.iyuba.config.AppConfig;

/**
 * @title: 中小学的书籍管理
 * @date: 2023/4/27 09:47
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class JuniorBookChooseManager {
    private static final String TAG = "JuniorBookChooseManager";

    private static JuniorBookChooseManager instance;

    public static JuniorBookChooseManager getInstance(){
        if (instance==null){
            synchronized (JuniorBookChooseManager.class){
                if (instance==null){
                    instance = new JuniorBookChooseManager();
                }
            }
        }
        return instance;
    }

    //存储的信息
    private static final String SP_NAME = TAG;
    private static final String SP_BOOK_TYPE = "type";
    private static final String SP_BOOK_BIG_TYPE = "bigType";
    private static final String SP_BOOK_SMALL_TYPE_ID = "smallTypeId";
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
        return SPUtil.loadString(getPreference(),SP_BOOK_TYPE, AppConfig.junior_type);
    }

    public void setBookType(String bookType){
        SPUtil.putString(getPreference(),SP_BOOK_TYPE,bookType);
    }

    public String getBookBigType(){
        return SPUtil.loadString(getPreference(),SP_BOOK_BIG_TYPE, AppConfig.junior_big_type);
    }

    public void setBookBigType(String bookBigType){
        SPUtil.putString(getPreference(),SP_BOOK_BIG_TYPE,bookBigType);
    }

    public String getBookSmallTypeId(){
        return SPUtil.loadString(getPreference(),SP_BOOK_SMALL_TYPE_ID, AppConfig.junior_small_type_id);
    }

    public void setBookSmallTypeId(String bookSmallType){
        SPUtil.putString(getPreference(),SP_BOOK_SMALL_TYPE_ID,bookSmallType);
    }

    public String getBookId(){
        return SPUtil.loadString(getPreference(),SP_BOOK_ID, AppConfig.junior_book_id);
    }

    public void setBookId(String bookId){
        SPUtil.putString(getPreference(),SP_BOOK_ID,bookId);
    }

    public String getBookName(){
        return SPUtil.loadString(getPreference(),SP_BOOK_NAME, AppConfig.junior_book_name);
    }

    public void setBookName(String bookName){
        SPUtil.putString(getPreference(),SP_BOOK_NAME,bookName);
    }
}
