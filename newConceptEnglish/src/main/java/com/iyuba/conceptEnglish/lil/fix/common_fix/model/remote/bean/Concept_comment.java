package com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.io.Serializable;
import java.util.List;

/**
 * @title: 新概念-评论内容
 * @date: 2023/10/25 14:31
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
@Root(name = "data",strict = false)
public class Concept_comment implements Serializable {

    @Element(required = false)
    public String ResultCode;
    @Element(required = false)
    public String Message;
    @Element(required = false)
    public String PageNumber;
    @Element(required = false)
    public String TotalPage;
    @Element(required = false)
    public String FirstPage;
    @Element(required = false)
    public String PrevPage;
    @Element(required = false)
    public String NextPage;
    @Element(required = false)
    public String LastPage;
    @Element(required = false)
    public String Counts;
    @Element(required = false)
    public String starCounts;
    @Element(required = false)
    public String ShuoShuoId;
    @Element(required = false)
    public String AddScore;
//    @Element(required = false)
//    public int FilePath;
    @ElementList(entry = "Row",inline = true,required = false)
    public List<Row> row;

    @Root(name = "Row",strict = false)
    public static class Row{
        @Element(required = false)
        public String id;
        @Element(required = false)
        public String Userid;
        @Element(required = false)
        public String UserName;
        @Element(required = false)
        public String ImgSrc;
        @Element(required = false)
        public int ShuoShuoType;
        @Element(required = false)
        public String ShuoShuo;
        @Element(required = false)
        public int agreeCount;
        @Element(required = false)
        public int againstCount;
        @Element(required = false)
        public String star;
        @Element(required = false)
        public String vip;
        @Element(required = false)
        public String CreateDate;
        @Element(required = false)
        public String TopicId;
//        @Element(required = false)
//        public String Title;
//        @Element(required = false)
//        public String Image;
    }
}
