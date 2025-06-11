package com.iyuba.conceptEnglish.lil.fix.common_fix.manager;

import android.content.SharedPreferences;

import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ResUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.SPUtil;

/**
 * @title: 学习管理-通用类型数据
 * @date: 2023/5/23 10:37
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class StudySettingManager {
    private static final String TAG = "StudySettingManager";

    private static StudySettingManager instance;

    public static StudySettingManager getInstance(){
        if (instance==null){
            synchronized (StudySettingManager.class){
                if (instance==null){
                    instance = new StudySettingManager();
                }
            }
        }
        return instance;
    }

    //存储的信息
    private static final String SP_NAME = TAG;

    private SharedPreferences preferences;

    private SharedPreferences getPreference(){
        if (preferences==null){
            preferences = SPUtil.getPreferences(ResUtil.getInstance().getContext(), SP_NAME);
        }
        return preferences;
    }

    /**************通用**************/
    private static final String SP_STUDY_CONCEPT_HOME = "studyConceptHome";//新概念学习界面
    private static final String SP_STUDY_JUNIOR_HOME = "studyJuniorHome";//中小学学习界面
    private static final String SP_STUDY_NOVEL_HOME = "studyNovelHome";//小说学习界面

    private static final String SP_GUIDE_VERSION = "guideVersion";//引导界面版本

    /**************原文*************/
    private static final String SP_TEXT_TYPE = "textType";//文本显示类型
    private static final String SP_SYNC_MODE = "syncMode";//播放模式
    private static final String SP_ROLL_ENABLE = "rollEnable";//滚动是否开启


    public String getTextType(){
        return SPUtil.loadString(getPreference(),SP_TEXT_TYPE, TypeLibrary.TextShowType.ALL);
    }

    public void setTextType(String textType){
        SPUtil.putString(getPreference(),SP_TEXT_TYPE,textType);
    }

    public String getSyncMode(){
        return SPUtil.loadString(getPreference(),SP_SYNC_MODE,TypeLibrary.PlayModeType.ORDER_PLAY);
    }

    public void setSyncMode(String syncMode){
        SPUtil.putString(getPreference(),SP_SYNC_MODE,syncMode);
    }

    public boolean getRollOpen(){
        return SPUtil.loadBoolean(getPreference(),SP_ROLL_ENABLE,true);
    }

    public void setRollOpen(boolean isRoll){
        SPUtil.putBoolean(getPreference(),SP_ROLL_ENABLE,isRoll);
    }

    public String getStudyConceptHome() {
        return SPUtil.loadString(getPreference(),SP_STUDY_CONCEPT_HOME,TypeLibrary.StudyPageType.read);
    }

    public void setStudyConceptHome(String pageType){
        SPUtil.putString(getPreference(),SP_STUDY_CONCEPT_HOME,pageType);
    }

    public String getStudyJuniorHome() {
        return SPUtil.loadString(getPreference(),SP_STUDY_JUNIOR_HOME,TypeLibrary.StudyPageType.read);
    }

    public void setStudyJuniorHome(String pageType){
        SPUtil.putString(getPreference(),SP_STUDY_JUNIOR_HOME,pageType);
    }

    public String getStudyNovelHome() {
        return SPUtil.loadString(getPreference(),SP_STUDY_NOVEL_HOME,TypeLibrary.StudyPageType.section);
    }

    public void setStudyNovelHome(String pageType){
        SPUtil.putString(getPreference(),SP_STUDY_NOVEL_HOME,pageType);
    }

    public int getGuideVersion() {
        return SPUtil.loadInt(getPreference(),SP_GUIDE_VERSION,0);
    }

    public void setGuideVersion(int version){
        SPUtil.putInt(getPreference(),SP_GUIDE_VERSION,version);
    }
}
