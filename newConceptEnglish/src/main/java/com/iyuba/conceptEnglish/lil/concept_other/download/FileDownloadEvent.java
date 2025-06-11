package com.iyuba.conceptEnglish.lil.concept_other.download;

/**
 * @title: 文件刷新来源标志
 * @date: 2023/11/8 14:28
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class FileDownloadEvent {

    public static final String home = "HomeFragment";//新概念-列表

    private String showType;

    //数据类型
    private String bookType;
    //voaId
    private int voaId;

    //当前的位置
    private int showDataPosition = -1;

    public FileDownloadEvent(String showType) {
        this.showType = showType;
    }

    public FileDownloadEvent(String showType, String bookType, int voaId,int dataPosition) {
        this.showType = showType;
        this.bookType = bookType;
        this.voaId = voaId;
        this.showDataPosition = dataPosition;
    }

    public String getShowType() {
        return showType;
    }

    public String getBookType() {
        return bookType;
    }

    public int getVoaId() {
        return voaId;
    }

    public int getShowDataPosition() {
        return showDataPosition;
    }
}
