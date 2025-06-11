package com.iyuba.conceptEnglish.sqlite.mode;

import java.util.List;

public class UpdateEvalDataBean {


    /**
     * result : 1
     * size : 23
     * data : [{"sentence":"Nice to meet you.","paraid":1,"score":"5.0","newsid":10050,"idindex":20,"userid":5492787,"url":"wav6/202002/concept/20200203/15807181587392926.mp3","newstype":"concept"},{"sentence":"And this is Xiaohui.","paraid":1,"score":"4.58","newsid":10050,"idindex":21,"userid":5492787,"url":"wav6/202002/concept/20200203/15807181696511248.mp3","newstype":"concept"},{"sentence":"She's Chinese, too.","paraid":1,"score":"4.77","newsid":10050,"idindex":22,"userid":5492787,"url":"wav6/202002/concept/20200203/15807181814424770.mp3","newstype":"concept"},{"sentence":"Nice to meet you.","paraid":1,"score":"5.0","newsid":10050,"idindex":23,"userid":5492787,"url":"wav6/202002/concept/20200203/15807181875866086.mp3","newstype":"concept"},{"sentence":"What is Rober's job?","paraid":1,"score":"4.09","newsid":10070,"idindex":3,"userid":5492787,"url":"wav6/202002/concept/20200206/15809787136207018.mp3","newstype":"concept"},{"sentence":"My name's Robert.","paraid":1,"score":"4.62","newsid":10070,"idindex":5,"userid":5492787,"url":"wav6/202002/concept/20200206/15809787662717218.mp3","newstype":"concept"},{"sentence":"My name's Sophie.","paraid":1,"score":"4.77","newsid":10070,"idindex":7,"userid":5492787,"url":"wav6/202002/concept/20200206/15809787994408614.mp3","newstype":"concept"},{"sentence":"Are you French?","paraid":1,"score":"5.0","newsid":10070,"idindex":8,"userid":5492787,"url":"wav6/202002/concept/20200206/15809788093175430.mp3","newstype":"concept"},{"sentence":"Yes, I am.","paraid":1,"score":"5.0","newsid":10070,"idindex":9,"userid":5492787,"url":"wav6/202002/concept/20200206/15809788343033344.mp3","newstype":"concept"},{"sentence":"Are you French too?","paraid":1,"score":"4.56","newsid":10070,"idindex":10,"userid":5492787,"url":"wav6/202002/concept/20200206/15809788801978728.mp3","newstype":"concept"},{"sentence":"No, I am not.","paraid":1,"score":"4.79","newsid":10070,"idindex":11,"userid":5492787,"url":"wav6/202002/concept/20200206/15809788860168582.mp3","newstype":"concept"},{"sentence":"What nationality are you?","paraid":1,"score":"4.61","newsid":10070,"idindex":12,"userid":5492787,"url":"wav6/202002/concept/20200206/15809793957574068.mp3","newstype":"concept"},{"sentence":"I'm Italian.","paraid":1,"score":"4.58","newsid":10070,"idindex":13,"userid":5492787,"url":"wav6/202002/concept/20200206/15809794045688400.mp3","newstype":"concept"},{"sentence":"Are you a teacher?","paraid":1,"score":"4.54","newsid":10070,"idindex":14,"userid":5492787,"url":"wav6/202002/concept/20200206/15809794256817932.mp3","newstype":"concept"},{"sentence":"No, I'm not.","paraid":1,"score":"5.0","newsid":10070,"idindex":15,"userid":5492787,"url":"wav6/202002/concept/20200206/15809794706279284.mp3","newstype":"concept"},{"sentence":"What's your job?","paraid":1,"score":"5.0","newsid":10070,"idindex":16,"userid":5492787,"url":"wav6/202002/concept/20200206/15809794828038092.mp3","newstype":"concept"},{"sentence":"I'm a keyboard operator.","paraid":1,"score":"3.94","newsid":10070,"idindex":17,"userid":5492787,"url":"wav6/202002/concept/20200206/15809795484530898.mp3","newstype":"concept"},{"sentence":"What's your job?","paraid":1,"score":"4.83","newsid":10070,"idindex":18,"userid":5492787,"url":"wav6/202002/concept/20200206/15809795558966774.mp3","newstype":"concept"},{"sentence":"I'm an engineer.","paraid":1,"score":"4.57","newsid":10070,"idindex":19,"userid":5492787,"url":"wav6/202002/concept/20200206/15809795781365896.mp3","newstype":"concept"},{"sentence":"Is this your handbag?","paraid":1,"score":"1.14","newsid":1001,"idindex":6,"userid":5492787,"url":"wav6/202002/concept/20200213/15816046632346480.mp3","newstype":"concept"},{"sentence":"Excuse me!","paraid":1,"score":"2.62","newsid":1001,"idindex":2,"userid":5492787,"url":"wav6/202002/concept/20200213/15816052687955586.mp3","newstype":"concept"},{"sentence":"Is this your handbag?","paraid":1,"score":"4.46","newsid":1001,"idindex":4,"userid":5492787,"url":"wav6/202002/concept/20200213/15816052822931440.mp3","newstype":"concept"},{"sentence":"lesson 1 Excuse me!","paraid":1,"score":"5.0","newsid":1001,"idindex":1,"userid":5492787,"url":"wav6/202002/concept/20200220/15821681509926596.mp3","newstype":"concept"}]
     */

    private String result;
    private int size;
    private List<DataBean> data;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * sentence : Nice to meet you.
         * paraid : 1
         * score : 5.0
         * newsid : 10050
         * idindex : 20
         * userid : 5492787
         * url : wav6/202002/concept/20200203/15807181587392926.mp3
         * newstype : concept
         */

        private String sentence;
        private int paraid;
        private String score;
        private int newsid;
        private int idindex;
        private int userid;
        private String url;
        private String newstype;

        public String getSentence() {
            return sentence;
        }

        public void setSentence(String sentence) {
            this.sentence = sentence;
        }

        public int getParaid() {
            return paraid;
        }

        public void setParaid(int paraid) {
            this.paraid = paraid;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public int getNewsid() {
            return newsid;
        }

        public void setNewsid(int newsid) {
            this.newsid = newsid;
        }

        public int getIdindex() {
            return idindex;
        }

        public void setIdindex(int idindex) {
            this.idindex = idindex;
        }

        public int getUserid() {
            return userid;
        }

        public void setUserid(int userid) {
            this.userid = userid;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getNewstype() {
            return newstype;
        }

        public void setNewstype(String newstype) {
            this.newstype = newstype;
        }
    }
}