package com.iyuba.conceptEnglish.event;

/**
 * 本事件用于，选择课本页面，选择之后
 * 刷新最新的课本数据
 *
 */
public class PullLastLessonDataEvent {
    /**
     * 1234 分别代表 新概念1234 四本书
     */
    private int currentBook;
    /**
     * 是否是美音
     * true：美音
     */
    private boolean boolAmerican;


    public boolean isBoolAmerican() {
        return boolAmerican;
    }

    public void setBoolAmerican(boolean boolAmerican) {
        this.boolAmerican = boolAmerican;
    }

    public PullLastLessonDataEvent(int currentBook, boolean boolAmerican) {
        this.currentBook = currentBook;
        this.boolAmerican = boolAmerican;
    }

    public PullLastLessonDataEvent() {
    }
    public int getCurrentBook() {
        return currentBook;
    }

    public void setCurrentBook(int currentBook) {
        this.currentBook = currentBook;
    }
}
