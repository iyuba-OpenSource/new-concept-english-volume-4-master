package com.iyuba.config;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;

/**
 * @title:
 * @date: 2023/5/19 13:50
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class AppConfig {

    /****************************中小学内容*********************/
    /****选书****/
    //类型
    public static final String junior_type = TypeLibrary.BookType.junior_primary;
    //教材大类型
    public static final String junior_big_type = "人教版";
    //教材小类型id
    public static final String junior_small_type_id = "313";
    //书籍id
    public static final String junior_book_id = "205";
    //书籍名称
    public static final String junior_book_name = "1年级上(新起点)";

    /*****************************小说***************************/
    /****选书****/
    //小说类型
    public static final String novel_type = TypeLibrary.BookType.bookworm;
    //小说等级
    public static final int novel_level = 0;
    //小说id
    public static final String novel_id = "1";
    //小说名称
    public static final String novel_name = "亚瑟王朝里的美国人";

    /****************************新概念内容*********************/
    /****选书****/
    //类型
    public static final String concept_type = TypeLibrary.BookType.conceptFourUS;
    //书籍id
    public static final int concept_book_id = 1;//其他包名为0
    //书籍名称
    public static final String concept_book_name = "新概念英语一（美音）";
}
