package com.iyuba.conceptEnglish.lil.concept_other.util;

/**
 * @title: 辅助工具
 * @date: 2023/11/2 16:19
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class HelpUtil {

    //这里统一处理下，将中文单引号处理成英文单引号
    public static String transTitleStyle(String titleStr){
        if (titleStr.contains("‘")){
            titleStr = titleStr.replace("‘","'");
        }

        if (titleStr.contains("’")){
            titleStr = titleStr.replace("’","'");
        }

        return titleStr;
    }
}
