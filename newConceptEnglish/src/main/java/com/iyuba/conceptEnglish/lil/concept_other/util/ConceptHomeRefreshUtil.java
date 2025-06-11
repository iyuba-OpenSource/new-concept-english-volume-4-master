package com.iyuba.conceptEnglish.lil.concept_other.util;

public class ConceptHomeRefreshUtil {
    private static ConceptHomeRefreshUtil instance;

    public static ConceptHomeRefreshUtil getInstance(){
        if (instance==null){
            synchronized (ConceptHomeRefreshUtil.class){
                if (instance==null){
                    instance = new ConceptHomeRefreshUtil();
                }
            }
        }
        return instance;
    }

    //设置是否刷新
    private boolean isRefreshHome = false;

    public void setRefreshState(boolean refreshTag){
        this.isRefreshHome = refreshTag;
    }

    public boolean isRefresh(){
        return isRefreshHome;
    }
}
