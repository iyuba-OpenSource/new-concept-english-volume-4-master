package com.iyuba.conceptEnglish.entity;

public class YouthBookEntity {
    public int Id;
    public String DescCn;
    public int Category;
    public int SeriesCount;
    public String SeriesName;
    public String CreateTime;
    public String UpdateTime;
    public int isVideo;
    public int HotFlg;
    public String pic;
    public String KeyWords;
    public int version;//同时控制 单词和标题的更新，只要有变动就同时更新
}
