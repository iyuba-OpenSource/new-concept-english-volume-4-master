package com.iyuba.conceptEnglish.lil.fix.common_fix.util;

import android.content.Context;

import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.manager.VoaDataManager;
import com.iyuba.configation.Constant;

/**
 * @title: 音频辅助工具
 * @date: 2023/11/18 09:02
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class AudioHelpUtil {

    //获取新概念的评测音频链接
    public static String getConceptAudioEvalUrl(Context context,String lessonType, int voaId,String lineN){
        switch (lessonType){
            case TypeLibrary.BookType.conceptFourUS:
                return Constant.getsimRecordAddr(context) + "/" + voaId + lineN + ".mp3";
            case TypeLibrary.BookType.conceptFourUK:
                return Constant.getsimRecordAddr(context) + "/" + (voaId * 10) + lineN + ".mp3";
            case TypeLibrary.BookType.conceptJunior:
                return Constant.getsimRecordAddr(context) + "/" + voaId + lineN + ".mp3";
            default:
                return Constant.getsimRecordAddr(context) + "/" + voaId + lineN + ".mp3";
        }
    }

    //获取新概念的评测音频保存路径
}
