package com.iyuba.conceptEnglish.util;

import com.iyuba.conceptEnglish.sqlite.mode.Voa;

import java.util.Hashtable;

public class ConstUtil {
    public static boolean sJumpToMicro = false;

    /**
     * 用于存储 微课记录数据 ，暂时先用静态数据记录
     */
    public static Hashtable<Integer, Voa> sMicroList=new Hashtable<Integer, Voa>();
}
