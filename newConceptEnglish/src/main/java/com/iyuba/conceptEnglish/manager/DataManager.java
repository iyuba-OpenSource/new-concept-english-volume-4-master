package com.iyuba.conceptEnglish.manager;

import com.iyuba.conceptEnglish.sqlite.mode.AbilityResult;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/10/20.
 */

public class DataManager {

    private static DataManager instance;

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    public ArrayList<AbilityResult> abilityResList = new ArrayList<>();//测试结果
}
