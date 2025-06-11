package com.iyuba.conceptEnglish.lil.fix.common_fix.manager;

import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;
import android.util.Pair;

import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ResUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.SPUtil;
import com.iyuba.core.common.activity.login.TempDataManager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @title: 学习的相关数据
 * @date: 2023/11/20 10:48
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class StudyDataManager {
    private static final String TAG = "StudyDataManager";

    private static StudyDataManager instance;

    public static StudyDataManager getInstance(){
        if (instance==null){
            synchronized (StudyDataManager.class){
                if (instance==null){
                    instance = new StudyDataManager();
                }
            }
        }
        return instance;
    }

    //缓存数据
    private SharedPreferences getPreference(){
        return SPUtil.getPreferences(ResUtil.getInstance().getContext(), TAG);
    }

    //搜索的历史记录(和账号不关联)
    private static final String search_history = "search_history";

    private void setHistoryData(String historyData){
        getPreference().edit().putString(search_history,historyData).apply();
    }

    public void addHistoryData(String wordStr){
        String[] oldData = getHistoryData();
        if (oldData.length>0){

            boolean hasData = false;
            for (int i = 0; i < oldData.length; i++) {
                if (oldData[i].equals(wordStr)){
                    hasData = true;
                }
            }

            if (!hasData){
                String oldHistoryStr = getPreference().getString(search_history,null);
                oldHistoryStr=oldHistoryStr+","+wordStr;
                setHistoryData(oldHistoryStr);
            }
        }else {
            setHistoryData(wordStr);
        }
    }

    public void deleteHistoryData(String wordStr){
        String[] oldData = getHistoryData();
        if (oldData.length>0){
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < oldData.length; i++) {
                if (!oldData[i].equals(wordStr)){
                    buffer.append(oldData[i]+",");
                }
            }

            //除掉后面的,
            String newData = buffer.toString();
            if (!TextUtils.isEmpty(newData)&&newData.endsWith(",")){
                String timestamp = String.valueOf(System.currentTimeMillis());
                newData+=timestamp;
                newData = newData.replace(","+timestamp,"");
            }
            setHistoryData(newData);
        }
    }

    public String[] getHistoryData(){
        String[] historyData = new String[]{};
        String oldData = getPreference().getString(search_history,null);
        if (oldData!=null&&oldData.length()>0){
            historyData = oldData.split(",");
        }
        return historyData;
    }
}
