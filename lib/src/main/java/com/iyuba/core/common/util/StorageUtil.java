package com.iyuba.core.common.util;

import android.content.Context;
import androidx.collection.ArraySet;


import com.iyuba.configation.Constant;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;

public class StorageUtil {

    private static final String JPG_SUFFIX = ".jpg";

    public static File getMediaRankingDir(Context context, int voaId) {
        return new File(getMediaDir(context, voaId), "rank");
    }

    public static File getMediaDir(Context context, int voaId) {
        return BaseStorageUtil.getDir(context, String.valueOf(voaId));
    }

    public static String getVideoTmpFilename(int voaId) {
        return Constant.Voa.TMP_PREFIX + getVideoFilename(voaId);
    }

    public static String getPicTmpFilename(int voaId) {
        return Constant.Voa.TMP_PREFIX + getVideoFilename(voaId);
    }


    public static String getAudioTmpFilename(int voaId) {
        return Constant.Voa.TMP_PREFIX + getAudioFilename(voaId);
    }

//    public static String getPicTmpFilename(int voaId) {
//        return Constant.Voa.TMP_PREFIX + getPicFilename(voaId);
//    }
//
//    private static String getPicFilename(int voaId) {
//        return voaId + Constant.Voa.JPE;
//    }

    public static String getVideoFilename(int voaId) {
        return voaId + Constant.Voa.MP4_SUFFIX;
    }



    private static String getAudioFilename(int voaId) {
        return voaId + Constant.Voa.MP3_SUFFIX;
    }

    private static String getWordAudioFilename(int voaId) {
        return "word"+voaId + Constant.Voa.MP3_SUFFIX;
    }

    public static File getVideoFile(Context context, int voaId) {
        return new File(getMediaDir(context, voaId), getVideoFilename(voaId));
    }

    public static File getAudioFile(Context context, int voaId) {
        return new File(getMediaDir(context, voaId), getAudioFilename(voaId));
    }

    public static boolean checkFileExist(String dir, int voaId) {
        return isAudioExist(dir, voaId) && isVideoExist(dir, voaId);
    }

    public static boolean checkFileExistVideo(String dir, int voaId) {
        return isVideoExist(dir, voaId);
    }

    public static boolean isVideoExist(String dir, int voaId) {
        return new File(dir, getVideoFilename(voaId)).exists();
    }


    public static boolean isAudioExist(String dir, int voaId) {
        return new File(dir, getAudioFilename(voaId)).exists();
    }

    public static boolean deleteVideoFile(Context context, int voaId) {
        File dir = getMediaDir(context, voaId);
        File videoFile = new File(dir, getVideoFilename(voaId));
        return !videoFile.exists() || videoFile.delete();
    }

    public static boolean deleteAudioFile(Context context, int voaId) {
        File dir = getMediaDir(context, voaId);
        File audioFile = new File(dir, getAudioFilename(voaId));
        return !audioFile.exists() || audioFile.delete();
    }

    public static boolean renameVideoFile(String dir, int voaId) {
        String fromName = getVideoTmpFilename(voaId);
        String toName = getVideoFilename(voaId);
        return BaseStorageUtil.renameFile(dir, fromName, toName);
    }


    public static boolean renameAudioFile(String dir, int voaId) {
        String fromName = getAudioTmpFilename(voaId);
        String toName = getAudioFilename(voaId);
        return BaseStorageUtil.renameFile(dir, fromName, toName);
    }

    public static boolean renameWordAudioFile(String dir, int voaId) {
        String fromName = getWordAudioTmpFilename(voaId);
        String toName = getWordAudioFilename(voaId);
        return BaseStorageUtil.renameFile(dir, fromName, toName);
    }



    public static boolean renamePicFile(String dir, int id) {
        String fromName = getAudioTmpFilename(id);
        String toName = getAudioFilename(id);
        return BaseStorageUtil.renameFile(dir, fromName, toName);
    }


    public static boolean hasRecordFile(Context context, int voaId, long timestamp) {
        String dir = getShortRecordDir(voaId, timestamp);
        File dirFile = new File(BaseStorageUtil.getDirPath(context, dir));
        return dirFile.exists() && dirFile.list().length != 0;
    }

    public static File getWavRecordFile(Context context, int voaId, long timeStamp, int paraId) {
        String dir = getShortRecordDir(voaId, timeStamp);
        return BaseStorageUtil.getFile(context, dir, paraId + Constant.Voa.WAV_SUFFIX);
    }

    public static File getAccRecordFile(Context context, int voaId, long timeStamp, int paraId) {
        String dir = getShortRecordDir(voaId, timeStamp);
        return BaseStorageUtil.getFile(context, dir, paraId + Constant.Voa.AAC_SUFFIX);
    }

    public static File getMP4RecordFile(Context context, int voaId, long timeStamp, int paraId) {
        String dir = getShortRecordDir(voaId, timeStamp);
        return BaseStorageUtil.getFile(context, dir, paraId + Constant.Voa.MP4_SUFFIX);
    }

    public static int getRecordNum(Context context, int voaId, long timestamp) {
        String dirPath = getShortRecordDir(voaId, timestamp);
        File dirFile = BaseStorageUtil.getDir(context, dirPath);
        Set<Integer> set = new ArraySet<>();
        for (File file : dirFile.listFiles()) {
            String name = file.getName();
            String index = name.split("\\.")[0];
            if (NumberUtil.isNumeric(index)) {
                set.add(Integer.valueOf(index));
            }
        }
        return set.size();
    }

    public static void cleanRecordDir(Context context, int voaId, long timeStamp) {
        String dir = getShortRecordDir(voaId, timeStamp);
        File dirFile = BaseStorageUtil.getDir(context, dir);
        for (File file : dirFile.listFiles()) {
            file.delete();
        }
        dirFile.delete();
    }

    public static void cleanDraft(Context context, int voaId, long timeStamp) {
        String dir = getShortRecordDir(voaId, timeStamp);
        File dirFile = BaseStorageUtil.getDir(context, dir);
        for (File file : dirFile.listFiles()) {
            String name = file.getName();
            String index = name.split("\\.")[0];
            if (NumberUtil.isNumeric(index)) {
                file.delete();
            }
        }
        if (dirFile.list().length <= 0) {
            dirFile.delete();
        }
    }

    public static File getParaRecordWavFile(Context context, int voaId, long timestamp, int paraId) {
        String dir = getShortRecordDir(voaId, timestamp);
        return BaseStorageUtil.getFile(context, dir, paraId + Constant.Voa.WAV_SUFFIX);
    }

    public static File getParaRecordAacFile(Context context, int voaId, int paraId, long timestamp) {
        String dir = getShortRecordDir(voaId, timestamp);
        return BaseStorageUtil.getFile(context, dir, paraId + Constant.Voa.AAC_SUFFIX);
    }

    public static File getParaRecordMP4File(Context context, int voaId, int paraId, long timestamp) {
        String dir = getShortRecordDir(voaId, timestamp);
        return BaseStorageUtil.getFile(context, dir, paraId + Constant.Voa.MP4_SUFFIX);
    }

    public static File getParaRecordAmrFile(Context context, int voaId, int paraId, long timestamp) {
        String dir = getShortRecordDir(voaId, timestamp);
        return BaseStorageUtil.getFile(context, dir, paraId + Constant.Voa.AMR_SUFFIX);
    }

    public static File getAacMergeFile(Context context, int voaId, long timestamp) {
        String dir = getShortRecordDir(voaId, timestamp);
        return BaseStorageUtil.getFile(context, dir, Constant.Voa.MERGE_AAC_NAME);
    }

    public static File getM4AMergeFile(Context context, int voaId, long timestamp) {
        String dir = getShortRecordDir(voaId, timestamp);
        return BaseStorageUtil.getFile(context, dir, "merge.mp3");
    }

    public static File getWavParaFile(Context context, int voaId, int paraId, long timestamp) {
        String dir = getShortRecordDir(voaId, timestamp);
        String filename = paraId + Constant.Voa.WAV_SUFFIX;
        return BaseStorageUtil.getFile(context, dir, filename);
    }

    public static File getRecordDir(Context context, int voaId, long timestamp) {
        return BaseStorageUtil.getDir(context, getShortRecordDir(voaId, timestamp));
    }

    public static String getShortRecordDir(int voaId, long timestamp) {
        return voaId + File.separator + "evaluate";//timestamp
    }

    public static String getCommentVoicePath(Context context) throws IOException {
        File recordFile = File.createTempFile(Constant.Voa.COMMENT_VOICE_NAME,
                Constant.Voa.COMMENT_VOICE_SUFFIX, BaseStorageUtil.getInnerStorageDir(context));
        return recordFile.getAbsolutePath();
//        String path = getInnerStorageDir(context) + File.separator
//                + Constant.Voa.COMMENT_VOICE_NAME;
//        new File(path).createNewFile();
//        return path;
    }

    public static String getAppDir(Context context) {
        return BaseStorageUtil.getDirPath(context, "appUpdate");
    }

    public static File getAvatarFile(Context context, String filename) {
        return new File(BaseStorageUtil.getFilePath(context, filename));
    }


    public static String copyFile(Context context, int voaId, int paraId, long timestamp) {
        String filename = System.currentTimeMillis() + Constant.Voa.WAV_SUFFIX;
        String dir = voaId + File.separator + timestamp;
        File srcFile = BaseStorageUtil.getFile(context, dir, paraId + Constant.Voa.WAV_SUFFIX);
        File destFile = BaseStorageUtil.getFile(context, dir, filename);
        copyFile(srcFile, destFile);
        return filename;
    }

    // 复制文件
    public static void copyFile(File srcFile, File destFile) {
        int len; // 读取的字节数
        BufferedInputStream bin = null;
        BufferedOutputStream bout = null;

        try {
            bin = new BufferedInputStream(new FileInputStream(srcFile));
            bout = new BufferedOutputStream(new FileOutputStream(destFile));
            byte[] buffer = new byte[1024];

            while ((len = bin.read(buffer)) != -1) {
                bout.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            CloseUtil.close(bin);
            CloseUtil.close(bout);
        }
    }

    public static File getParaRecordMp3File(Context context, int voaId, int i, long timeStamp) {
        String dir = getShortRecordDir(voaId, timeStamp);
        return BaseStorageUtil.getFile(context, dir, i + ".mp3");
    }

    public static String getWordAudioTmpFilename(int position) {
        return Constant.Voa.TMP_PREFIX + getWordAudioFilename(position);
    }



    public static boolean renameWordImageFile(String dir, int voaId) {
        String fromName = getWordImageTmpFilename(voaId);
        String toName = getWordImageFilename(voaId);
        return BaseStorageUtil.renameFile(dir, fromName, toName);
    }

    private static String getWordImageFilename(int voaId) {
        return  getWordImageFileName(voaId);
    }

    private static String getWordImageFileName(int voaId) {
        return voaId+Constant.Voa.JPG_SUFFIX;
    }

    public  static String getWordImageTmpFilename(int voaId) {
        return Constant.Voa.TMP_PREFIX + getWordImageFileName(voaId);
    }

    public static boolean isImageExists(String mDir, int position) {
        return new File(mDir, getImageFilename(position)).exists();
    }

    public static String getImageFilename(int voaId) {
        return voaId + JPG_SUFFIX;
    }

    public static boolean isWordAudioExist(String dir, int voaId) {
        return new File(dir, getWordAudioFilename(voaId)).exists();
    }
}
