package com.iyuba.conceptEnglish.lil.concept_other.remote.bean;

import java.util.List;

/**
 * 新概念-评测-结果数据
 */
public class Concept_eval_result {


    /**
     * sentence : lesson 1 Excuse me!
     * total_score : 2.0875000000000004
     * word_count : 4
     * URL : wav6/202405/concept/20240529/17169667879554090.mp3
     * words : [{"content":"lesson","index":1,"score":"1.95"},{"content":"1","index":2,"score":"1.95"},{"content":"Excuse","index":3,"score":"1.0"},{"content":"me!","index":4,"score":"3.45"}]
     */

    private String sentence;
    private String total_score;
    private String word_count;
    private String URL;
    private List<WordsDTO> words;

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public String getTotal_score() {
        return total_score;
    }

    public void setTotal_score(String total_score) {
        this.total_score = total_score;
    }

    public String getWord_count() {
        return word_count;
    }

    public void setWord_count(String word_count) {
        this.word_count = word_count;
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
         * content : lesson
         * index : 1
         * score : 1.95
         */

        private String content;
        private String index;
        private String score;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }
    }
}
