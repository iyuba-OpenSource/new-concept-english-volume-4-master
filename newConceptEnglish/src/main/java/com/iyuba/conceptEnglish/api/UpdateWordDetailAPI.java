package com.iyuba.conceptEnglish.api;

import com.iyuba.configation.Constant;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface UpdateWordDetailAPI {


//    // http://m."+Constant.IYUBA_CN+"jlpt1/getConceptWord.jsp?bookNum=1001&type=app
//    String url = "http://m." + Constant.IYUBA_CN + "jlpt1/getConceptWord.jsp";  //弃用

    // http://apps."+ Constant.IYUBA_CN+"concept/getConceptWord2.jsp?bookNum=1001&type=app
    String url = "http://apps."+ Constant.IYUBA_CN+"concept/getConceptWord2.jsp";
    String type = "app";

    @GET
    Call<WordBean> getData(
            @Url String url,
            @Query("type") String type,
            @Query("bookNum") String voaIds
    );

   class WordBean{

       /**
        * size : 1
        * data : [{"voa_id":1001,"word":"is.","def":"v. be 动词现在时第三人称单数","pron":"iz","examples":"3","audio":"http://res.iciba.com/resource/amp3/oxford/0/b7/64/b76466de7742e3fe06548d1037406061.mp3","position":"1"}]
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
            * voa_id : 1001
            * word : is.
            * def : v. be 动词现在时第三人称单数
            * pron : iz
            * examples : 3
            * audio : http://res.iciba.com/resource/amp3/oxford/0/b7/64/b76466de7742e3fe06548d1037406061.mp3
            * position : 1
            */

           private int voa_id;
           private String word;
           private String def;
           private String pron;
           private String examples;
           private String audio;
           private String position;

           public int getVoa_id() {
               return voa_id;
           }

           public void setVoa_id(int voa_id) {
               this.voa_id = voa_id;
           }

           public String getWord() {
               return word;
           }

           public void setWord(String word) {
               this.word = word;
           }

           public String getDef() {
               return def;
           }

           public void setDef(String def) {
               this.def = def;
           }

           public String getPron() {
               return pron;
           }

           public void setPron(String pron) {
               this.pron = pron;
           }

           public String getExamples() {
               return examples;
           }

           public void setExamples(String examples) {
               this.examples = examples;
           }

           public String getAudio() {
               return audio;
           }

           public void setAudio(String audio) {
               this.audio = audio;
           }

           public String getPosition() {
               return position;
           }

           public void setPosition(String position) {
               this.position = position;
           }
       }
   }
}

