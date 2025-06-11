package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.evalFix;

import java.util.List;

/**
 * 纠音的数据格式
 */
public class EvalFixBean {

    private int voaId;
    private int paraId;
    private int idIndex;

    private String sentence;
    private List<WordEvalFixBean> wordList;

    public EvalFixBean(int voaId, int paraId, int idIndex, String sentence, List<WordEvalFixBean> wordList) {
        this.voaId = voaId;
        this.paraId = paraId;
        this.idIndex = idIndex;
        this.sentence = sentence;
        this.wordList = wordList;
    }

    public int getVoaId() {
        return voaId;
    }

    public int getParaId() {
        return paraId;
    }

    public int getIdIndex() {
        return idIndex;
    }

    public String getSentence() {
        return sentence;
    }

    public List<WordEvalFixBean> getWordList() {
        return wordList;
    }

    public static class WordEvalFixBean{
        private String word;
        private String pron;
        private float score;
        private int sort;

        private String userPron;

        public WordEvalFixBean(String word, String pron, float score, int sort, String userPron) {
            this.word = word;
            this.pron = pron;
            this.score = score;
            this.sort = sort;
            this.userPron = userPron;
        }

        public String getWord() {
            return word;
        }

        public String getPron() {
            return pron;
        }

        public float getScore() {
            return score;
        }

        public int getSort() {
            return sort;
        }

        public String getUserPron() {
            return userPron;
        }

        public void setUserPron(String userPron) {
            this.userPron = userPron;
        }
    }
}
