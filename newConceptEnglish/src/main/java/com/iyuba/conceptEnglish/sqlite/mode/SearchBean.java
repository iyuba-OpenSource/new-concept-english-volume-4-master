package com.iyuba.conceptEnglish.sqlite.mode;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by iyuba on 2018/12/3.
 */

public class SearchBean implements Serializable {

    private static final SimpleDateFormat ownSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * WordId : 290650
     * Word : mother
     * def : ["母亲，妈妈","女修道院院长","大娘"]n.
     * ph_am : %CB%88m%CA%8C%C3%B0%C9%9A
     * ph_am_mp3 : http://res.iciba.com/resource/amp3/1/0/6e/e6/6ee6a213cb02554a63b1867143572e70.mp3
     * titleData : [{"Category":"128","Title_Cn":"据说妈妈的恋爱史会影响后代的约会轨迹","CreateTime":"2018-11-16 13:53:48.0","Title":"Your Mother\u2019s Romantic Past Affects Your Own Dating Adventures","Pic":"64219.jpg","ReadCount":"235","NewsId":"64219"},{"Category":"128","Title_Cn":"男子遛狗不牵绳，还当街暴打孩母亲","CreateTime":"2018-11-09 11:37:23.0","Title":"Dog owner detained for attack on mother of two young children","Pic":"64123.jpg","ReadCount":"171","NewsId":"64123"}]
     * ph_en : %CB%88m%CA%8C%C3%B0%C9%99%28r%29
     * titleToal : 2
     * ph_en_mp3 : http://res.iciba.com/resource/amp3/oxford/0/51/2a/512adc515175966fd898530687e19480.mp3
     * textData : [{"EndTiming":"0.0","ParaId":"8","IdIndex":"1","Sentence_cn":"周三，孩子们的母亲泰瑞.科普兰在Facebook上向这名邮递员表达了敬意，并附上了两封信的照片说：\u201c事实上我不知道怎么形容杰斯知道他爸爸收到了信件后的兴奋劲儿。皇家邮局，你刚刚恢复了我对人性的信心。\u201d","Timing":"0.0","VoaId":"64486","Sentence":"On Wednesday, the children\u2019s mother, Teri Copland, posted a tribute to the postman on Facebook along with photos of both letters, saying, \u201cI actually cannot state how emotional [Jase] is knowing his dad got his card. Royal Mail you\u2019ve just restored my faith in humanity.\u201d"},{"EndTiming":"0.0","ParaId":"4","IdIndex":"1","Sentence_cn":"研究人员考察了一名业余化石收藏者1977年在美国俄勒冈州发现的一头古代鲸遗骸。这些遗骸数十年来一直保存在马里兰州的一个仓库内。迄今为止，人们对这头鲸的了解是它生活在大约3300万年前。科学家称之为\u201c母鲸\u201d（Maiabalaena）。","Timing":"0.0","VoaId":"64482","Sentence":"The researchers examined the remains of an ancient whale discovered in Oregon in 1977 by an amateur fossil collector, which for decades were stored away in a warehouse in Maryland. To date, all we\u2019ve known about this whale was that it lived about 33 million years ago; scientists called it Maiabalaena, or \u201cmother whale.\u201d"}]
     * English : true
     * WordCn : 母亲，妈妈；女修道院院长；大娘；
     * <p>
     * textToal : 2
     */

    private String WordId;
    private String Word;
    private String def;
    private String ph_am;
    private String ph_am_mp3;
    private String ph_en;
    private int titleToal;
    private String ph_en_mp3;
    private boolean English;
    private String WordCn;
    private int textToal;
    private List<TitleDataBean> titleData;
    private List<TextDataBean> textData;

    public String getWordId() {
        return WordId;
    }

    public void setWordId(String WordId) {
        this.WordId = WordId;
    }

    public String getWord() {
        return Word;
    }

    public void setWord(String Word) {
        this.Word = Word;
    }

    public String getDef() {
        return def;
    }

    public void setDef(String def) {
        this.def = def;
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

    public String getPh_en() {
        return ph_en;
    }

    public void setPh_en(String ph_en) {
        this.ph_en = ph_en;
    }

    public int getTitleToal() {
        return titleToal;
    }

    public void setTitleToal(int titleToal) {
        this.titleToal = titleToal;
    }

    public String getPh_en_mp3() {
        return ph_en_mp3;
    }

    public void setPh_en_mp3(String ph_en_mp3) {
        this.ph_en_mp3 = ph_en_mp3;
    }

    public boolean isEnglish() {
        return English;
    }

    public void setEnglish(boolean English) {
        this.English = English;
    }

    public String getWordCn() {
        return WordCn;
    }

    public void setWordCn(String WordCn) {
        this.WordCn = WordCn;
    }

    public int getTextToal() {
        return textToal;
    }

    public void setTextToal(int textToal) {
        this.textToal = textToal;
    }

    public List<TitleDataBean> getTitleData() {
        return titleData;
    }

    public void setTitleData(List<TitleDataBean> titleData) {
        this.titleData = titleData;
    }

    public List<TextDataBean> getTextData() {
        return textData;
    }

    public void setTextData(List<TextDataBean> textData) {
        this.textData = textData;
    }

    public static class TitleDataBean implements Serializable {
        /**
         * "Auid":"928",
         * Category : 128
         * Title_Cn : 据说妈妈的恋爱史会影响后代的约会轨迹
         * CreateTime : 2018-11-16 13:53:48.0
         * Title : Your Mother’s Romantic Past Affects Your Own Dating Adventures
         * Pic : 64219.jpg
         * ReadCount : 235
         * NewsId : 64219
         */

        private String Category;
        private String Title_Cn;
        private String CreateTime;
        private String Title;
        private String Pic;
        private String ReadCount;
        private String NewsId;
        private String Auid;

        public String getAuid() {
            return Auid;
        }

        public void setAuid(String auid) {
            Auid = auid;
        }

        public String getCategory() {
            return Category;
        }

        public void setCategory(String Category) {
            this.Category = Category;
        }

        public String getTitle_Cn() {
            return Title_Cn;
        }

        public void setTitle_Cn(String Title_Cn) {
            this.Title_Cn = Title_Cn;
        }

        public String getCreateTime() {
            return CreateTime;
        }

        public void setCreateTime(String CreateTime) {
            this.CreateTime = CreateTime;
        }

        public String getTitle() {
            return Title;
        }

        public void setTitle(String Title) {
            this.Title = Title;
        }

        public String getPic() {
            return Pic;
        }

        public void setPic(String Pic) {
            this.Pic = Pic;
        }

        public String getReadCount() {
            return ReadCount;
        }

        public void setReadCount(String ReadCount) {
            this.ReadCount = ReadCount;
        }

        public String getNewsId() {
            return NewsId;
        }

        public void setNewsId(String NewsId) {
            this.NewsId = NewsId;
        }


        public String getCreatTimeInFormat(SimpleDateFormat today, SimpleDateFormat before) {
            String result = "";
            try {
                Date date = ownSDF.parse(CreateTime);
                result = isToday(date) ? today.format(date) : before.format(date);
            } catch (ParseException e) {
                result = CreateTime;
            }
            return result;
        }

        private boolean isToday(Date date) {
            Date now = new Date();
            return now.getDate() == date.getDate() && (now.getTime() - date.getTime()) < 86400000;
        }
    }

    public class TextDataBean implements Serializable {


        /**
         * EndTiming : 0.0
         * ParaId : 8
         * IdIndex : 1
         * Sentence_cn : 周三，孩子们的母亲泰瑞.科普兰在Facebook上向这名邮递员表达了敬意，并附上了两封信的照片说：“事实上我不知道怎么形容杰斯知道他爸爸收到了信件后的兴奋劲儿。皇家邮局，你刚刚恢复了我对人性的信心。”
         * Timing : 0.0
         * VoaId : 64486
         * SoundText：
         * Sentence : On Wednesday, the children’s mother, Teri Copland, posted a tribute to the postman on Facebook along with photos of both letters, saying, “I actually cannot state how emotional [Jase] is knowing his dad got his card. Royal Mail you’ve just restored my faith in humanity.”
         */

        private String EndTiming;
        private String ParaId;
        private String SoundText;
        private String IdIndex;
        private String Sentence_cn;
        private String Timing;
        private String VoaId;
        private String Sentence;
        private boolean isRead; //是否测评过
        private int score; //测评分数
//        private SpannableStringBuilder readText; //测评结果
        private int shuoshuoId; //评论返回
        private String filePath;//评论返回
        private int realPostion; // 搜索首页句子真正下标
        private EvaluateBean evaluateBean; //评测接口返回结果

        public EvaluateBean getEvaluateBean() {
            return evaluateBean;
        }

        public void setEvaluateBean(EvaluateBean evaluateBean) {
            this.evaluateBean = evaluateBean;
        }

        public String getSoundText() {
            return SoundText;
        }

        public void setSoundText(String soundText) {
            SoundText = soundText;
        }

        public int getRealPostion() {
            return realPostion;
        }

        public void setRealPostion(int realPostion) {
            this.realPostion = realPostion;
        }

        public int getShuoshuoId() {
            return shuoshuoId;
        }

        public void setShuoshuoId(int shuoshuoId) {
            this.shuoshuoId = shuoshuoId;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public boolean isRead() {
            return isRead;
        }

        public void setRead(boolean read) {
            isRead = read;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public String getEndTiming() {
            return EndTiming;
        }

        public void setEndTiming(String EndTiming) {
            this.EndTiming = EndTiming;
        }

        public String getParaId() {
            return ParaId;
        }

        public void setParaId(String ParaId) {
            this.ParaId = ParaId;
        }

        public String getIdIndex() {
            return IdIndex;
        }

        public void setIdIndex(String IdIndex) {
            this.IdIndex = IdIndex;
        }

        public String getSentence_cn() {
            return Sentence_cn;
        }

        public void setSentence_cn(String Sentence_cn) {
            this.Sentence_cn = Sentence_cn;
        }

        public String getTiming() {
            return Timing;
        }

        public void setTiming(String Timing) {
            this.Timing = Timing;
        }

        public String getVoaId() {
            return VoaId;
        }

        public void setVoaId(String VoaId) {
            this.VoaId = VoaId;
        }

        public String getSentence() {
            return Sentence;
        }

        public void setSentence(String Sentence) {
            this.Sentence = Sentence;
        }

        @Override
        public String toString() {
            String s = "Sentence_cn: " + Sentence_cn + "Sentence: " + Sentence + "isRead: " + isRead + "score" + score;
            return s;
        }
    }
}
