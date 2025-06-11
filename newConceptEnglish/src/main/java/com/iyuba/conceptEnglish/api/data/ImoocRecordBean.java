package com.iyuba.conceptEnglish.api.data;

import java.util.List;

public class ImoocRecordBean {
    public int result;
    public List<DataBean> data;


    public static class DataBean {
        public String EndTime;
        public int LessonId;
        public String BeginTime;
        public String Title;
        public int TestNumber;
        public int Lesson;
        public int EndFlg;
    }
}
