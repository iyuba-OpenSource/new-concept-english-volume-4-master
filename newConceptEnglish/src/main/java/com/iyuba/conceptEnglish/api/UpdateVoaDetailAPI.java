package com.iyuba.conceptEnglish.api;

import com.iyuba.configation.Constant;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface UpdateVoaDetailAPI {




    String url = "http://apps."+ Constant.IYUBA_CN+"concept/getConceptSentence.jsp";
    String type = "app";

    @GET
    Call<UpdateVoaDetailBean> getData(
            @Url String url,
            @Query("type") String type,
            @Query("voaid") String voaIds
    );

    class UpdateVoaDetailBean{

        /**
         * size : 8
         * data : [{"voaid":1001,"Paraid":"1","IdIndex":"1","Timing":"0.6","EndTiming":"4.2","Sentence":"lesson 1 Excuse me!","Sentence_cn":"对不起！"},{"voaid":1001,"Paraid":"1","IdIndex":"2","Timing":"4.9","EndTiming":"5.9","Sentence":"Excuse me!","Sentence_cn":"对不起"},{"voaid":1001,"Paraid":"1","IdIndex":"3","Timing":"6.4","EndTiming":"7.3","Sentence":"Yes?","Sentence_cn":"什么事？"},{"voaid":1001,"Paraid":"1","IdIndex":"4","Timing":"8.0","EndTiming":"10.2","Sentence":"Is this your handbag?","Sentence_cn":"这是您的手提包吗？"},{"voaid":1001,"Paraid":"1","IdIndex":"5","Timing":"11.0","EndTiming":"11.8","Sentence":"Pardon?","Sentence_cn":"对不起，请再说一遍。"},{"voaid":1001,"Paraid":"1","IdIndex":"6","Timing":"12.6","EndTiming":"15.3","Sentence":"Is this your handbag?","Sentence_cn":"这是您的手提包吗？"},{"voaid":1001,"Paraid":"1","IdIndex":"7","Timing":"16.0","EndTiming":"17.7","Sentence":"Yes, it is.","Sentence_cn":"是的，是我的。"},{"voaid":1001,"Paraid":"1","IdIndex":"8","Timing":"18.6","EndTiming":"20.4","Sentence":"Thank you very much.","Sentence_cn":"非常感谢！"}]
         */

        private int size;
        private List<DataBean> data;

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
             * voaid : 1001
             * Paraid : 1
             * IdIndex : 1
             * Timing : 0.6
             * EndTiming : 4.2
             * Sentence : lesson 1 Excuse me!
             * Sentence_cn : 对不起！
             */

            private int voaid;
            private String Paraid;
            private String IdIndex;
            private String Timing;
            private String EndTiming;
            private String Sentence;
            private String Sentence_cn;

            public int getVoaid() {
                return voaid;
            }

            public void setVoaid(int voaid) {
                this.voaid = voaid;
            }

            public String getParaid() {
                return Paraid;
            }

            public void setParaid(String Paraid) {
                this.Paraid = Paraid;
            }

            public String getIdIndex() {
                return IdIndex;
            }

            public void setIdIndex(String IdIndex) {
                this.IdIndex = IdIndex;
            }

            public String getTiming() {
                return Timing;
            }

            public void setTiming(String Timing) {
                this.Timing = Timing;
            }

            public String getEndTiming() {
                return EndTiming;
            }

            public void setEndTiming(String EndTiming) {
                this.EndTiming = EndTiming;
            }

            public String getSentence() {
                return Sentence;
            }

            public void setSentence(String Sentence) {
                this.Sentence = Sentence;
            }

            public String getSentence_cn() {
                return Sentence_cn;
            }

            public void setSentence_cn(String Sentence_cn) {
                this.Sentence_cn = Sentence_cn;
            }
        }
    }
}

