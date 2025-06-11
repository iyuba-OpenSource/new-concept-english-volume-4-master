package com.iyuba.conceptEnglish.lil.concept_other.download;

/**
 * @title:
 * @date: 2023/11/8 14:29
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class FileDownloadBean {

    private String fileUrl;//文件下载链接
    private String filePath;//文件保存路径

    //其他的一些数据
    private String bookType;
    private int voaId;
    private int position;

    public FileDownloadBean(String fileUrl, String filePath, String bookType, int voaId, int position) {
        this.fileUrl = fileUrl;
        this.filePath = filePath;
        this.bookType = bookType;
        this.voaId = voaId;
        this.position = position;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getBookType() {
        return bookType;
    }

    public int getVoaId() {
        return voaId;
    }

    public int getPosition() {
        return position;
    }
}
