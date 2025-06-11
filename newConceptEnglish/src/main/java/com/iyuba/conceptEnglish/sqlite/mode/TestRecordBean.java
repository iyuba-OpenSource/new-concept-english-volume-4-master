package com.iyuba.conceptEnglish.sqlite.mode;

public class TestRecordBean {
    public int uid;
    public int LessonId;//课程的Id
    public String BeginTime;//测试的开始时间
    public int TestNumber;     //题号
    public String UserAnswer = "";    //用户答案
    public String RightAnswer;    //正确答案
    public int AnswerResult;    //正确与否�0：错误；1：正确
    public String TestTime;    //测试时间
    public boolean IsUpload;
}
