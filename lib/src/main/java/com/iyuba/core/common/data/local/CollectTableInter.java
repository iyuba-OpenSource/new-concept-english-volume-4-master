package com.iyuba.core.common.data.local;

import com.iyuba.core.common.data.model.TalkLesson;

import java.util.List;

public interface CollectTableInter {
    String TABLE_NAME = "collect";

    String COLUMN_UID = "uId";
    String COLUMN_VOA_ID = "voaId";

    String TITLE = "title";
    String DESC = "desc";
    String IMAGE ="imagePath";//列名不能有空格
    String SERIES ="series";//难度？

    String IS_DOWNLOAD = "isDownLoad";
    String IS_COLLECT = "isCollect";


    boolean setCollect(String voaId, String uId,String title,String desc,String image,String series);

    void setDownload(String voaId, String uId,String title,String desc,String image,String series);

    void deleteCollect(String voaId, String uId);

    void deleteDown(String voaId);

    boolean getCollect(String voaId, String uId);

    List<TalkLesson> getCollectList(String uId);

    List<TalkLesson> getDownList();

}
