package com.iyuba.conceptEnglish.sqlite.mode;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 每一个voa的基本信息
 */
@Keep
public class Voa implements Serializable {
    @SerializedName("voa_id")
    public int voaId; // 课 ID
    public String title = ""; // 课名
    @SerializedName("title_cn")
    public String titleCn = ""; // 课名
    public int category; // 全四册是 1234 ，青少版 服务器是321，本地是 服务器课本id 例如 278 288
    public String sound = ""; // 声音
    public String url = "";
    public String pic = ""; // 图片
    //    public String create_time = "";
//    public String update_time = "";
    @SerializedName("read_count")
    public String readCount = ""; // 阅读量


    public String clickRead;
    public String isSynchro;
    public int titleFind;
    public int textFind;

    public int version_uk;
    public int version_us;
    public int version_word;

    public int categoryid;
    public String titleid;
    public int totalTime;
    public int compeleTime;
    //微课学习记录
    public int percentage;

    public boolean isDelete;

    public int downLoadPercentage; // 下载百分比

    public int doMultiple;
    public int doBlank;
    public int doThinking;

    //这三个已经被替换，因为阅读和收藏和账号是关联的，下载需要根据英音、美音处理，这里有很大的问题
    public String isCollect = "0";
    public String isRead = "0";
    public String isDownload = "0";

    //这里增加相关的非课程相关数据（一些数据，如阅读和收藏数据是和userId绑定的，因此写在这里就不合适了）
    //使用这里的类型：TypeLibrary.BookType（英音、美音、青少版）
    public String lessonType = "";//当前课程的类型，每次刷新会被替换【这里作为临时数据，每次处理会进行更换，但是是可以使用的】
    //临时数据-当前章节的位置
    public int position = -1;//这里填充当前的数据位置

    /**
     * string: lessonid （代表的时章节 id 或者课程id 来着）
     * intger：最大的完成的时间， 99999时表示 已完成
     */
//    public Hashtable<String, ListenWordDetail> mMicroList = new Hashtable<String, ListenWordDetail>();


    @Override
    public String toString() {
        return "Voa{" +
                "voaId=" + voaId +
                ", title='" + title + '\'' +
                ", titleCn='" + titleCn + '\'' +
                ", category=" + category +
                ", sound='" + sound + '\'' +
                ", url='" + url + '\'' +
                ", pic='" + pic + '\'' +
                ", readCount='" + readCount + '\'' +
                ", isCollect='" + isCollect + '\'' +
                ", isRead='" + isRead + '\'' +
                ", clickRead='" + clickRead + '\'' +
                ", isDownload='" + isDownload + '\'' +
                ", isSynchro='" + isSynchro + '\'' +
                ", titleFind=" + titleFind +
                ", textFind=" + textFind +
                ", version_uk=" + version_uk +
                ", version_us=" + version_us +
                ", version_word=" + version_word +
                ", categoryid=" + categoryid +
                ", titleid='" + titleid + '\'' +
                ", totalTime=" + totalTime +
                ", compeleTime=" + compeleTime +
                ", percentage=" + percentage +
                ", isDelete=" + isDelete +
                ", downLoadPercentage=" + downLoadPercentage +
                ", doMultiple=" + doMultiple +
                ", doBlank=" + doBlank +
                ", doThinking=" + doThinking +
                '}';
    }
}
