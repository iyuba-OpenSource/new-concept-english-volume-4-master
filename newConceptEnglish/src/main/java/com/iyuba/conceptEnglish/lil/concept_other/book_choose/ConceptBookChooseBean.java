package com.iyuba.conceptEnglish.lil.concept_other.book_choose;

/**
 * @title:
 * @date: 2023/11/8 11:08
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class ConceptBookChooseBean {

    private int bookId;
    private int bookImageId;
    private String bookImageUrl;
    private String title;
    private int version;

    public ConceptBookChooseBean(int bookId, int bookImageId, String bookImageUrl, String title, int version) {
        this.bookId = bookId;
        this.bookImageId = bookImageId;
        this.bookImageUrl = bookImageUrl;
        this.title = title;
        this.version = version;
    }

    public int getBookId() {
        return bookId;
    }

    public int getBookImageId() {
        return bookImageId;
    }

    public String getBookImageUrl() {
        return bookImageUrl;
    }

    public String getTitle() {
        return title;
    }

    public int getVersion() {
        return version;
    }
}
