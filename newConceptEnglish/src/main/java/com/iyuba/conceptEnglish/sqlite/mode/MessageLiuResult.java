package com.iyuba.conceptEnglish.sqlite.mode;

/**
 * Created by iyuba on 2018/2/1.
 */

public class MessageLiuResult {


    /**
     * result : 1
     * data : {"adId":"安卓 BBC 头条 TED AI 走遍美国信息流","firstLevel":"1","secondLevel":"2","thirdLevel":"2","title":""}
     */

    private String result;
    private Data data;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        /**
         * adId : 安卓 BBC 头条 TED AI 走遍美国信息流
         * firstLevel : 1
         * secondLevel : 2
         * thirdLevel : 2
         * title :
         */

        private String adId;
        private String firstLevel;
        private String secondLevel;
        private String thirdLevel;
        private String title;

        public String getAdId() {
            return adId;
        }

        public void setAdId(String adId) {
            this.adId = adId;
        }

        public String getFirstLevel() {
            return firstLevel;
        }

        public void setFirstLevel(String firstLevel) {
            this.firstLevel = firstLevel;
        }

        public String getSecondLevel() {
            return secondLevel;
        }

        public void setSecondLevel(String secondLevel) {
            this.secondLevel = secondLevel;
        }

        public String getThirdLevel() {
            return thirdLevel;
        }

        public void setThirdLevel(String thirdLevel) {
            this.thirdLevel = thirdLevel;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
