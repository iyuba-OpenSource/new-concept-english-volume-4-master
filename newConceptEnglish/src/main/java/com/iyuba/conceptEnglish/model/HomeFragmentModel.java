package com.iyuba.conceptEnglish.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class HomeFragmentModel {
    public List<String> getCurrentSpinnerData(int curBook) {
        List<String> list = new ArrayList<>();

        switch (curBook) {
            case 1:
//                boolean isAmerican = ConfigManager.Instance().isAmercan();
//                if (isAmerican) {
                    list = new LinkedList<>(Arrays.asList("1~20", "21~40", "41~60", "61~80", "81~100", "101~120", "121~144"));
//                } else {
//                    list = new LinkedList<>(Arrays.asList("1~19", "21~39", "41~59", "61~79", "81~99", "101~119", "121~143"));
//                }
                break;
            case 2:
                list = new LinkedList<>(Arrays.asList("1~20", "21~40", "41~60",
                        "61~80", "81~96"));
                break;
            case 3:
                list = new LinkedList<>(Arrays.asList("1~20", "21~40", "41~60"));
                break;
            case 4:
                list = new LinkedList<>(Arrays.asList("1~20", "21~40", "41~48"));
                break;
            case 278:
                list = new LinkedList<>();
                list.add("1~15");
                break;
            case 279:
                list = new LinkedList<>();
                list.add("1~15");
                break;
            case 280:
                list = new LinkedList<>();
                list.add("1~15");
                break;
            case 281:
                list = new LinkedList<>();
                list.add("16~30");
                break;
            case 282:
                list = new LinkedList<>();
                list.add("1~15");
                break;
            case 283:
                list = new LinkedList<>();
                list.add("16~30");
                break;
            case 284:
                //3a
                list = new LinkedList<>();
                list.add("1~15");
                break;
            case 285:
                list = new LinkedList<>();
                list.add("16~30");
                break;
            case 286:
                //4a
                list = new LinkedList<>();
                list.add("1~24");
                break;
            case 287:
                list = new LinkedList<>();
                list.add("25~48");
                break;
            case 288:
                //5a
                list = new LinkedList<>();
                list.add("1~24");
                break;
            case 289:
                list = new LinkedList<>();
                list.add("25~48");
                break;
            default:
                list = new LinkedList<>(Arrays.asList("1~20", "21~40", "41~60",
                        "61~80", "81~100", "101~120", "121~144"));
                break;
        }

        return list;
    }
}
