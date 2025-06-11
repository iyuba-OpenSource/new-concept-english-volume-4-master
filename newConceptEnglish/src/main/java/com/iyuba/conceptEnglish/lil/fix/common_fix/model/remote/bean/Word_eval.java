package com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 单词评测模型
 */
public class Word_eval implements Serializable {

    /**
     * result : 1
     * message : SCORE OK!
     * data : {"sentence":"big","words":[{"index":"0","content":"big","pron":"B IH G","pron2":"bɪg","user_pron":"G B IH","user_pron2":"gbɪ","score":"1.67","insert":"G","delete":"G","substitute_orgi":"","substitute_user":""}],"scores":33,"total_score":1.65,"filepath":"/data/projects/voa/mp34/202503/primaryenglish/20250311/17416578056563602.mp3","URL":"wav7/202503/primaryenglish/20250311/17416578056563602.mp3"}
     */

    private String result;
    private String message;
    private DataDTO data;

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

    public DataDTO getData() {
        return data;
    }

    public void setData(DataDTO data) {
        this.data = data;
    }

    public static class DataDTO {
        /**
         * sentence : big
         * words : [{"index":"0","content":"big","pron":"B IH G","pron2":"bɪg","user_pron":"G B IH","user_pron2":"gbɪ","score":"1.67","insert":"G","delete":"G","substitute_orgi":"","substitute_user":""}]
         * scores : 33
         * total_score : 1.65
         * filepath : /data/projects/voa/mp34/202503/primaryenglish/20250311/17416578056563602.mp3
         * URL : wav7/202503/primaryenglish/20250311/17416578056563602.mp3
         */

        private String sentence;
        private int scores;
        private double total_score;
        private String filepath;
        private String URL;
        private List<WordsDTO> words;

        public String getSentence() {
            return sentence;
        }

        public void setSentence(String sentence) {
            this.sentence = sentence;
        }

        public int getScores() {
            return scores;
        }

        public void setScores(int scores) {
            this.scores = scores;
        }

        public double getTotal_score() {
            return total_score;
        }

        public void setTotal_score(double total_score) {
            this.total_score = total_score;
        }

        public String getFilepath() {
            return filepath;
        }

        public void setFilepath(String filepath) {
            this.filepath = filepath;
        }

        public String getURL() {
            return URL;
        }

        public void setURL(String URL) {
            this.URL = URL;
        }

        public List<WordsDTO> getWords() {
            return words;
        }

        public void setWords(List<WordsDTO> words) {
            this.words = words;
        }

        public static class WordsDTO {
            /**
             * index : 0
             * content : big
             * pron : B IH G
             * pron2 : bɪg
             * user_pron : G B IH
             * user_pron2 : gbɪ
             * score : 1.67
             * insert : G
             * delete : G
             * substitute_orgi :
             * substitute_user :
             */

            private String index;
            private String content;
            private String pron;
            private String pron2;
            private String user_pron;
            private String user_pron2;
            private String score;
            private String insert;
            private String delete;
            private String substitute_orgi;
            private String substitute_user;

            public String getIndex() {
                return index;
            }

            public void setIndex(String index) {
                this.index = index;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getPron() {
                return pron;
            }

            public void setPron(String pron) {
                this.pron = pron;
            }

            public String getPron2() {
                return pron2;
            }

            public void setPron2(String pron2) {
                this.pron2 = pron2;
            }

            public String getUser_pron() {
                return user_pron;
            }

            public void setUser_pron(String user_pron) {
                this.user_pron = user_pron;
            }

            public String getUser_pron2() {
                return user_pron2;
            }

            public void setUser_pron2(String user_pron2) {
                this.user_pron2 = user_pron2;
            }

            public String getScore() {
                return score;
            }

            public void setScore(String score) {
                this.score = score;
            }

            public String getInsert() {
                return insert;
            }

            public void setInsert(String insert) {
                this.insert = insert;
            }

            public String getDelete() {
                return delete;
            }

            public void setDelete(String delete) {
                this.delete = delete;
            }

            public String getSubstitute_orgi() {
                return substitute_orgi;
            }

            public void setSubstitute_orgi(String substitute_orgi) {
                this.substitute_orgi = substitute_orgi;
            }

            public String getSubstitute_user() {
                return substitute_user;
            }

            public void setSubstitute_user(String substitute_user) {
                this.substitute_user = substitute_user;
            }
        }
    }
}
