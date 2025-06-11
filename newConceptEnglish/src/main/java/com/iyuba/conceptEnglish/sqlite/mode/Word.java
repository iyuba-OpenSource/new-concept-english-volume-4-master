package com.iyuba.conceptEnglish.sqlite.mode;

public class Word {


    private String word;
    private String wordCn;
    private String  ph_am;
    private String ph_am_mp3;

    private String ph_en;
    private String ph_en_mp3;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getPh_en() {
        return ph_en;
    }

    public void setPh_en(String ph_en) {
        this.ph_en = ph_en;
    }

    public String getWordCn() {
        return wordCn;
    }

    public void setWordCn(String wordCn) {
        this.wordCn = wordCn;
    }

    public String getPh_am() {
        return ph_am;
    }

    public void setPh_am(String ph_am) {
        this.ph_am = ph_am;
    }

    public String getPh_am_mp3() {
        return ph_am_mp3;
    }

    public void setPh_am_mp3(String ph_am_mp3) {
        this.ph_am_mp3 = ph_am_mp3;
    }

    public String getPh_en_mp3() {
        return ph_en_mp3;
    }

    public void setPh_en_mp3(String ph_en_mp3) {
        this.ph_en_mp3 = ph_en_mp3;
    }
}
