package com.iyuba.conceptEnglish.sqlite.mode;

import com.google.gson.annotations.SerializedName;

import java.util.List;

//TODO 接口更换，需要升级
public class DownPassDataBean {

    /**
     * result : 1
     * mode : 2
     * totalRight : 24
     * msg : Success
     * uid : 6926336
     * dataWrong : [{"score":0,"userAnswer":"1","testMode":"W","testTime":"2020-02-17 00:00:00.0","id":10029},{"score":0,"userAnswer":"1","testMode":"W","testTime":"2020-02-17 00:00:00.0","id":100210}]
     * testMode : W
     * lesson : NewConcept1
     * dataRight : [{"score":1,"userAnswer":"1","testMode":"W","testTime":"2020-02-14 00:00:00.0","id":1002},{"score":1,"userAnswer":"1","testMode":"W","testTime":"2020-02-14 00:00:00.0","id":1003},{"score":1,"userAnswer":"1","testMode":"W","testTime":"2020-02-14 00:00:00.0","id":1004},{"score":1,"userAnswer":"1","testMode":"W","testTime":"2020-02-14 00:00:00.0","id":1005},{"score":1,"userAnswer":"1","testMode":"W","testTime":"2020-02-14 00:00:00.0","id":1006},{"score":1,"userAnswer":"1","testMode":"W","testTime":"2020-02-14 00:00:00.0","id":1007},{"score":1,"userAnswer":"1","testMode":"W","testTime":"2020-02-14 00:00:00.0","id":1008},{"score":1,"userAnswer":"1","testMode":"W","testTime":"2020-02-14 00:00:00.0","id":1009},{"score":1,"userAnswer":"1","testMode":"W","testTime":"2020-02-14 00:00:00.0","id":10011},{"score":1,"userAnswer":"1","testMode":"W","testTime":"2020-02-14 00:00:00.0","id":10012},{"score":1,"userAnswer":"1","testMode":"W","testTime":"2020-02-14 00:00:00.0","id":10013},{"score":1,"userAnswer":"1","testMode":"W","testTime":"2020-02-14 00:00:00.0","id":10014},{"score":1,"userAnswer":"1","testMode":"W","testTime":"2020-02-14 00:00:00.0","id":10015},{"score":1,"userAnswer":"1","testMode":"W","testTime":"2020-02-14 00:00:00.0","id":10016},{"score":1,"userAnswer":"1","testMode":"W","testTime":"2020-02-14 00:00:00.0","id":10017},{"score":1,"userAnswer":"1","testMode":"W","testTime":"2020-02-14 00:00:00.0","id":10018},{"score":1,"userAnswer":"1","testMode":"W","testTime":"2020-02-17 00:00:00.0","id":10021},{"score":1,"userAnswer":"1","testMode":"W","testTime":"2020-02-17 00:00:00.0","id":10022},{"score":1,"userAnswer":"1","testMode":"W","testTime":"2020-02-17 00:00:00.0","id":10023},{"score":1,"userAnswer":"1","testMode":"W","testTime":"2020-02-17 00:00:00.0","id":10024},{"score":1,"userAnswer":"1","testMode":"W","testTime":"2020-02-17 00:00:00.0","id":10025},{"score":1,"userAnswer":"1","testMode":"W","testTime":"2020-02-17 00:00:00.0","id":10026},{"score":1,"userAnswer":"1","testMode":"W","testTime":"2020-02-17 00:00:00.0","id":10027},{"score":1,"userAnswer":"1","testMode":"W","testTime":"2020-02-17 00:00:00.0","id":10028}]
     * totalWrong : 2
     */

    private int result;
    private String mode;
    private int totalRight;
    private String msg;
    private int uid;
    private String testMode;
    private String lesson;
    private int totalWrong;
    private List<DataWrongBean> dataWrong;
    private List<DataRightBean> dataRight;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public int getTotalRight() {
        return totalRight;
    }

    public void setTotalRight(int totalRight) {
        this.totalRight = totalRight;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getTestMode() {
        return testMode;
    }

    public void setTestMode(String testMode) {
        this.testMode = testMode;
    }

    public String getLesson() {
        return lesson;
    }

    public void setLesson(String lesson) {
        this.lesson = lesson;
    }

    public int getTotalWrong() {
        return totalWrong;
    }

    public void setTotalWrong(int totalWrong) {
        this.totalWrong = totalWrong;
    }

    public List<DataWrongBean> getDataWrong() {
        return dataWrong;
    }

    public void setDataWrong(List<DataWrongBean> dataWrong) {
        this.dataWrong = dataWrong;
    }

    public List<DataRightBean> getDataRight() {
        return dataRight;
    }

    public void setDataRight(List<DataRightBean> dataRight) {
        this.dataRight = dataRight;
    }

    public static class DataWrongBean {
        /**
         * TestId: "1",
         * score: 0,
         * userAnswer: "1",
         * testMode: "W",
         * LessonId: "1049",
         * testTime: "2020-02-21 00:00:00.0",
         * Lesson: "4"
         */

        /* 和position有关，可能是空的 */
        @SerializedName("TestId")
        private String testId;
        private int score;
        private String userAnswer;
        private String testMode;
        /* 可能是voaId，缺乏青少版的数据 */
        @SerializedName("LessonId")
        private String lessonId;
        private String testTime;
        @SerializedName("Lesson")
        private String lesson;


        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public String getUserAnswer() {
            return userAnswer;
        }

        public void setUserAnswer(String userAnswer) {
            this.userAnswer = userAnswer;
        }

        public String getTestMode() {
            return testMode;
        }

        public void setTestMode(String testMode) {
            this.testMode = testMode;
        }

        public String getTestTime() {
            return testTime;
        }

        public void setTestTime(String testTime) {
            this.testTime = testTime;
        }

        public String getLessonId() {
            return lessonId;
        }

        public void setLessonId(String lessonId) {
            this.lessonId = lessonId;
        }

        public String getTestId() {
            return testId;
        }

        public void setTestId(String testId) {
            this.testId = testId;
        }

        public String getLesson() {
            return lesson;
        }

        public void setLesson(String lesson) {
            this.lesson = lesson;
        }

        @Override
        public String toString() {
            return "DataWrongBean{" +
                    "testId='" + testId + '\'' +
                    ", score=" + score +
                    ", userAnswer='" + userAnswer + '\'' +
                    ", testMode='" + testMode + '\'' +
                    ", lessonId='" + lessonId + '\'' +
                    ", testTime='" + testTime + '\'' +
                    ", lesson='" + lesson + '\'' +
                    '}';
        }
    }

    public static class DataRightBean {
        /**
         * TestId: "1",
         * score: 1,
         * userAnswer: "1",
         * testMode: "W",
         * LessonId: "1049",
         * testTime: "2020-02-21 00:00:00.0",
         * Lesson: "4"
         */

        @SerializedName("TestId")
        private String testId;
        private int score;
        private String userAnswer;
        private String testMode;
        private String testTime;
        @SerializedName("LessonId")
        private String lessonId;
        @SerializedName("Lesson")
        private String lesson;

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public String getUserAnswer() {
            return userAnswer;
        }

        public void setUserAnswer(String userAnswer) {
            this.userAnswer = userAnswer;
        }

        public String getTestMode() {
            return testMode;
        }

        public void setTestMode(String testMode) {
            this.testMode = testMode;
        }

        public String getTestTime() {
            return testTime;
        }

        public void setTestTime(String testTime) {
            this.testTime = testTime;
        }

        public String getLesson() {
            return lesson;
        }

        public void setLesson(String lesson) {
            this.lesson = lesson;
        }

        public String getTestId() {
            return testId;
        }

        public void setTestId(String testId) {
            this.testId = testId;
        }

        public String getLessonId() {
            return lessonId;
        }

        public void setLessonId(String lessonId) {
            this.lessonId = lessonId;
        }

        @Override
        public String toString() {
            return "DataRightBean{" +
                    "testId='" + testId + '\'' +
                    ", score=" + score +
                    ", userAnswer='" + userAnswer + '\'' +
                    ", testMode='" + testMode + '\'' +
                    ", testTime='" + testTime + '\'' +
                    ", lessonId='" + lessonId + '\'' +
                    ", lesson='" + lesson + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "DownPassDataBean{" +
                "result=" + result +
                ", mode='" + mode + '\'' +
                ", totalRight=" + totalRight +
                ", msg='" + msg + '\'' +
                ", uid=" + uid +
                ", testMode='" + testMode + '\'' +
                ", lesson='" + lesson + '\'' +
                ", totalWrong=" + totalWrong +
                ", dataWrong=" + dataWrong +
                ", dataRight=" + dataRight +
                '}';
    }
}