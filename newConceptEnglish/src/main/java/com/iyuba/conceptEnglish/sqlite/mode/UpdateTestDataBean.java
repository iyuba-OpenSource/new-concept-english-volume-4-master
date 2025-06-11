package com.iyuba.conceptEnglish.sqlite.mode;

import java.util.List;

public class UpdateTestDataBean {

    /**
     * result : 1
     * data : [{"TestTime":"2020-02-21 11:14:54","testindex":"0","AppId":"222","UserAnswer":"d","LessonId":"1003","UpdateTime":"2020-02-21 11:15:10.0","BeginTime":"2020-02-21 11:14:54.0","TestNumber":"4","TestWords":"0","RightAnswer":"b","AppName":""},{"TestTime":"2020-02-21 11:14:58","testindex":"0","AppId":"222","UserAnswer":"d","LessonId":"1003","UpdateTime":"2020-02-21 11:15:10.0","BeginTime":"2020-02-21 11:14:54.0","TestNumber":"5","TestWords":"0","RightAnswer":"c","AppName":""},{"TestTime":"2020-02-21 11:14:53","testindex":"0","AppId":"222","UserAnswer":"","LessonId":"1003","UpdateTime":"2020-02-21 11:15:10.0","BeginTime":"2020-02-21 11:14:52.0","TestNumber":"2","TestWords":"0","RightAnswer":"c","AppName":""},{"TestTime":"2020-02-21 11:14:52","testindex":"0","AppId":"222","UserAnswer":"d","LessonId":"1003","UpdateTime":"2020-02-21 11:15:10.0","BeginTime":"2020-02-21 11:14:51.0","TestNumber":"1","TestWords":"0","RightAnswer":"c","AppName":""},{"TestTime":"2020-02-18","testindex":"1004","AppId":"222","UserAnswer":"1","LessonId":"21","UpdateTime":"2020-02-18 08:45:17.0","BeginTime":"2020-02-18 00:00:00.0","TestNumber":"10045","TestWords":"0","RightAnswer":"1","AppName":""},{"TestTime":"2020-02-17","testindex":"1001","AppId":"222","UserAnswer":"1","LessonId":"21","UpdateTime":"2020-02-17 17:39:09.0","BeginTime":"2020-02-17 00:00:00.0","TestNumber":"10018","TestWords":"0","RightAnswer":"1","AppName":""},{"TestTime":"2020-02-14","testindex":"1001","AppId":"222","UserAnswer":"1","LessonId":"21","UpdateTime":"2020-02-14 18:03:34.0","BeginTime":"2020-02-14 00:00:00.0","TestNumber":"10018","TestWords":"0","RightAnswer":"1","AppName":""},{"TestTime":"2020-01-09 15:24:23","testindex":"0","AppId":"222","UserAnswer":"c","LessonId":"1003","UpdateTime":"2020-01-09 15:24:27.0","BeginTime":"2020-01-09 15:24:23.0","TestNumber":"4","TestWords":"0","RightAnswer":"b","AppName":""},{"TestTime":"2020-01-09 15:24:24","testindex":"0","AppId":"222","UserAnswer":"c","LessonId":"1003","UpdateTime":"2020-01-09 15:24:27.0","BeginTime":"2020-01-09 15:24:23.0","TestNumber":"5","TestWords":"0","RightAnswer":"c","AppName":""},{"TestTime":"2020-01-09 15:24:22","testindex":"0","AppId":"222","UserAnswer":"c","LessonId":"1003","UpdateTime":"2020-01-09 15:24:27.0","BeginTime":"2020-01-09 15:24:22.0","TestNumber":"2","TestWords":"0","RightAnswer":"c","AppName":""},{"TestTime":"2020-01-09 15:24:10","testindex":"0","AppId":"222","UserAnswer":"c","LessonId":"1001","UpdateTime":"2020-01-09 15:24:11.0","BeginTime":"2020-01-09 15:24:09.0","TestNumber":"5","TestWords":"0","RightAnswer":"b","AppName":""},{"TestTime":"2020-01-09 15:24:09","testindex":"0","AppId":"222","UserAnswer":"c","LessonId":"1001","UpdateTime":"2020-01-09 15:24:11.0","BeginTime":"2020-01-09 15:24:08.0","TestNumber":"4","TestWords":"0","RightAnswer":"c","AppName":""},{"TestTime":"2020-01-09 15:24:07","testindex":"0","AppId":"222","UserAnswer":"c","LessonId":"1001","UpdateTime":"2020-01-09 15:24:11.0","BeginTime":"2020-01-09 15:24:07.0","TestNumber":"2","TestWords":"0","RightAnswer":"d","AppName":""},{"TestTime":"2020-01-09 15:24:08","testindex":"0","AppId":"222","UserAnswer":"c","LessonId":"1001","UpdateTime":"2020-01-09 15:24:11.0","BeginTime":"2020-01-09 15:24:07.0","TestNumber":"3","TestWords":"0","RightAnswer":"a","AppName":""}]
     * message : success
     */

    private String result;
    private String message;
    private List<DataBean> data;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * TestTime : 2020-02-21 11:14:54
         * testindex : 0
         * AppId : 222
         * UserAnswer : d
         * LessonId : 1003
         * UpdateTime : 2020-02-21 11:15:10.0
         * BeginTime : 2020-02-21 11:14:54.0
         * TestNumber : 4
         * TestWords : 0
         * RightAnswer : b
         * AppName :
         */

        private String TestTime;
        private String testindex;
        private String AppId;
        private String UserAnswer;
        private String LessonId;
        private String UpdateTime;
        private String BeginTime;
        private String TestNumber;
        private String TestWords;
        private String RightAnswer;
        private String AppName;

        public String getTestTime() {
            return TestTime;
        }

        public void setTestTime(String TestTime) {
            this.TestTime = TestTime;
        }

        public String getTestindex() {
            return testindex;
        }

        public void setTestindex(String testindex) {
            this.testindex = testindex;
        }

        public String getAppId() {
            return AppId;
        }

        public void setAppId(String AppId) {
            this.AppId = AppId;
        }

        public String getUserAnswer() {
            return UserAnswer;
        }

        public void setUserAnswer(String UserAnswer) {
            this.UserAnswer = UserAnswer;
        }

        public String getLessonId() {
            return LessonId;
        }

        public void setLessonId(String LessonId) {
            this.LessonId = LessonId;
        }

        public String getUpdateTime() {
            return UpdateTime;
        }

        public void setUpdateTime(String UpdateTime) {
            this.UpdateTime = UpdateTime;
        }

        public String getBeginTime() {
            return BeginTime;
        }

        public void setBeginTime(String BeginTime) {
            this.BeginTime = BeginTime;
        }

        public String getTestNumber() {
            return TestNumber;
        }

        public void setTestNumber(String TestNumber) {
            this.TestNumber = TestNumber;
        }

        public String getTestWords() {
            return TestWords;
        }

        public void setTestWords(String TestWords) {
            this.TestWords = TestWords;
        }

        public String getRightAnswer() {
            return RightAnswer;
        }

        public void setRightAnswer(String RightAnswer) {
            this.RightAnswer = RightAnswer;
        }

        public String getAppName() {
            return AppName;
        }

        public void setAppName(String AppName) {
            this.AppName = AppName;
        }
    }
}
