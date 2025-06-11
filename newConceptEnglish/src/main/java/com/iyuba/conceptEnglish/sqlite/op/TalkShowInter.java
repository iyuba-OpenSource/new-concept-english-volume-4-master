package com.iyuba.conceptEnglish.sqlite.op;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.iyuba.core.common.data.model.TalkLesson;

import java.util.List;

/**
 * @title: 口语秀的参数
 * @date: 2023/5/19 15:08
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public interface TalkShowInter {

    String TABLE_NAME = "voa_talk_show";

    String Category = "category";
    String CreateTime = "createTime";
    String Title = "title";
    String Sound = "sound";
    String Pic = "pic";
    String Flag = "flag";
    String Type = "type";
    String DescCn = "descCn";
    String TitleCn = "titleCn";
    String series = "series";
    String CategoryName = "categoryName";
    String Id = "id";
    String ReadCount = "readCount";
    String clickRead = "clickRead";
    String video = "video";

    //插入数据
    void saveData(List<TalkLesson> list);

    //查询该章节下的数据
    TalkLesson findTalkByVoaId(String voaId);

    //查询该书籍下的数据
    List<TalkLesson> findTalkByBookId(String bookId);
}
