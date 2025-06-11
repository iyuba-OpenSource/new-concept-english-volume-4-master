package com.iyuba.conceptEnglish.lil.concept_other.download;

import android.os.Build;

import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ResUtil;
import com.iyuba.conceptEnglish.manager.VoaDataManager;
import com.iyuba.configation.Constant;

import java.io.File;

/**
 * @title:  文件路径工具类
 * @date: 2023/11/8 14:32
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class FilePathUtil {

    //音频文件的文件夹路径
    public static String getAudioDirectoryPath(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            return ResUtil.getInstance().getContext().getExternalFilesDir(null).getPath()+"/audio";
        }else {
            return Constant.videoAddr;
        }
    }

    private static String getAudioPath(String fileName){
        //获取保存的路径
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            return ResUtil.getInstance().getContext().getExternalFilesDir(null).getPath()+"/audio/"+fileName;
        }else {
            return Constant.videoAddr+fileName;
        }
    }

    //获取首页音频的路径
    public static String getHomeAudioPath(int voaId,String bookType){
        String filePath = "";

        //这里直接用原来的
        switch (bookType){
            case TypeLibrary.BookType.conceptFourUS:
            case TypeLibrary.BookType.conceptJunior:
                filePath = getAudioPath(voaId + Constant.append);
                break;
            case TypeLibrary.BookType.conceptFourUK:
                filePath = getAudioPath(voaId + "_B" + Constant.append);
                break;
        }
        return filePath;
    }

    //获取配音中的评测音频路径
    public static String getStudyEvalAudioPath(int voaId,int indexId){
        try {
            String evalDirPath = Constant.envir+voaId+"/eval/";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                evalDirPath = ResUtil.getInstance().getContext().getExternalFilesDir(null).getPath()+"/"+voaId+"/eval/";
            }

            String fileName = indexId+".mp3";

            File dirFile = new File(evalDirPath);
            if (!dirFile.exists()){
                dirFile.mkdirs();
            }

            File evalFile = new File(evalDirPath+fileName);
            if (evalFile.exists()){
                evalFile.delete();
            }
            evalFile.createNewFile();
            return evalFile.getPath();
        }catch (Exception e){
            return "";
        }
    }

    //获取文件路径
    public static String getDownloadDirPath(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            return ResUtil.getInstance().getContext().getExternalFilesDir(null).getPath()+"/iyuba/concept2/";
        }else {
            return Constant.envir;
        }
    }
}
