package com.iyuba.conceptEnglish.sqlite.mode;

public class Book {
    public int bookId;
    public String bookName;
    public int totalNum;
    public int downloadNum;
    /**
     * 0：初始化状态
     * 1：正在下载状态
     * -1:暂停下载
     * 2：处于下载队列中，等候排队
     *
     *
     */
    public int downloadState;






}