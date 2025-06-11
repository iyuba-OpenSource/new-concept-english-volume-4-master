package com.iyuba.conceptEnglish.api;

import com.google.gson.annotations.SerializedName;
import com.iyuba.conceptEnglish.sqlite.mode.MultipleChoice;
import com.iyuba.configation.Constant;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by liuzhenli on 2017/5/23.
 */

public interface UpdateTestAPI {

    //    String url = "http://m." + Constant.IYUBA_CN + "jlpt1/getConceptExercise.jsp"; //淘汰
    //http://apps."+ Constant.IYUBA_CN+concept/getConceptExercise.jsp?bookNum=1001,1002,1003
    String url = "http://apps."+ Constant.IYUBA_CN+"concept/getConceptExercise.jsp";
    //    String urlCheck  ="http://m."+Constant.IYUBA_CN+"jlpt1/getVersionExercise.jsp"; //淘汰
    //http://apps."+ Constant.IYUBA_CN+"concept/getVersionExercise.jsp?book=1
    String urlCheck  ="http://apps."+ Constant.IYUBA_CN+"concept/getVersionExercise.jsp";
    //    String urlCheckKnow  ="http://m."+Constant.IYUBA_CN+"jlpt1/getVersionKnowledge.jsp"; //淘汰
    //http://apps."+ Constant.IYUBA_CN+"concept/getVersionKnowledge.jsp?book=1
    String urlCheckKnow  ="http://apps."+ Constant.IYUBA_CN+"concept/getVersionKnowledge.jsp";
    //    String urlKnow ="http://m."+Constant.IYUBA_CN+"jlpt1/getConceptKnowledge.jsp"; //淘汰
    //http://apps."+ Constant.IYUBA_CN+"concept/getConceptKnowledge.jsp?bookNum=1001,1002,1003
    String urlKnow ="http://apps."+ Constant.IYUBA_CN+"concept/getConceptKnowledge.jsp";

    @GET
    Call<UpdateTestBean> getData(
            @Url String url,
            @Query("bookNum") String voaId
    );

    @GET
    Call<TestBookUpData> getTestUpDataForBook(
            @Url String url,
            @Query("book") int bookId // 1,2,3,4
    );

    @GET
    Call<KnowLedgeVersion> getKnowUpDataForBook(
            @Url String url,
            @Query("book") int bookId // 1,2,3,4
    );

    @GET
    Call<knowLedgeData> getKnowData(
            @Url String url,
            @Query("bookNum") String voaId
    );

    class UpdateTestBean {

        /**
         * SizeMultipleChoice : 5
         * MultipleChoice : [{"voa_id":1003,"index_id":"1","question":"____? Yes, she is my daughter.","choice_A":"Is it your daughter","choice_B":"Is your daughter","choice_C":"Is this your daughter ","choice_D":"Is it this your daughter","answer":"3"},{"voa_id":1003,"index_id":"2","question":"Is this your coat?____.","choice_A":"Yes, it isn't","choice_B":"No, it is ","choice_C":"No, it isn't","choice_D":"NO, it's","answer":"3"},{"voa_id":1003,"index_id":"3","question":"____. Thank you,sir. ","choice_A":"Here's your ticket","choice_B":"Here's is your ticket","choice_C":"Your ticket is here","choice_D":"Your ticket here is","answer":"1"},{"voa_id":1003,"index_id":"4","question":"This is John speaking. who is that? ____.","choice_A":"I am Kate","choice_B":"This is Kate ","choice_C":"I'm your friend","choice_D":"This is Kate's speaking","answer":"2"},{"voa_id":1003,"index_id":"5","question":"Mum,I'm thirsty(口渴). would you please give me some ____ ?","choice_A":"pencils","choice_B":"cake","choice_C":"water","choice_D":"books","answer":"3"}]
         * SizeVoaStructureExercise : 5
         * VoaStructureExercise : [{"id":1003,"desc_EN":"Rewrite these sentences","desc_CH":"按要求改写下列句子","column":"","number":"1","note":"This is my shirt.(变为一般疑问句)","type":"0","ques_num":"0","answer":"Is this your shirt?"},{"id":1003,"desc_EN":"","desc_CH":"","column":"","number":"2","note":"This is not her teacher.(变为肯定句)","type":"0","ques_num":"0","answer":"This is her teacher."},{"id":1003,"desc_EN":"","desc_CH":"","column":"","number":"3","note":"This is my umbrella.(变为否定句)","type":"0","ques_num":"0","answer":"This is not my umbrella."},{"id":1003,"desc_EN":"","desc_CH":"","column":"","number":"4","note":"Whose skirt is this?(回答：这是我的短裙)","type":"0","ques_num":"0","answer":"This is my skirt."},{"id":1003,"desc_EN":"","desc_CH":"","column":"","number":"5","note":"Is this his pencil?(变为否定句)","type":"0","ques_num":"0","answer":"This isn't his pencil."}]
         * SizeVoaDiffcultyExercise : 0
         * VoaDiffcultyExercise : []
         */

        @SerializedName("SizeMultipleChoice")
        public int SizeMultipleChoice;
        @SerializedName("SizeVoaStructureExercise")
        public int SizeVoaStructureExercise;
        @SerializedName("SizeVoaDiffcultyExercise")
        public int SizeVoaDiffcultyExercise;
        @SerializedName("MultipleChoice")
        public List<MultipleChoice> multipleChoice;
        @SerializedName("VoaStructureExercise")
        public List<com.iyuba.conceptEnglish.sqlite.mode.VoaStructureExercise> VoaStructureExercise;

        //开启重点难点
        @SerializedName("VoaDiffcultyExercise")
        public List<com.iyuba.conceptEnglish.sqlite.mode.VoaDiffcultyExercise_inter> VoaDiffcultyExercise;
    }

    class TestBookUpData{
        /**
         * size : 6
         * data : [{"voa_id":1001,"version_exercise":"4"},{"voa_id":1003,"version_exercise":"1"},{"voa_id":1019,"version_exercise":"3"},{"voa_id":1037,"version_exercise":"6"},{"voa_id":1049,"version_exercise":"9"},{"voa_id":1053,"version_exercise":"8"}]
         */
        @SerializedName("size")
        public int size;
        @SerializedName("data")
        public List<DataBean> data;

        public static class DataBean {
            /**
             * voa_id : 1001
             * version_exercise : 4
             */
            @SerializedName("voa_id")
            public int voaId;
            @SerializedName("version_exercise")
            public int versionExercise;
        }
    }

    class KnowLedgeVersion{
        /**
         * size : 1
         * data : [{"voa_id":1001,"version_knowledge":"11"}]
         */

        @SerializedName("size")
        public int size;
        @SerializedName("data")
        public List<DataBean> data;

        public static class DataBean {
            /**
             * voa_id : 1001
             * version_knowledge : 11
             */

            @SerializedName("voa_id")
            public int voaId;
            @SerializedName("version_knowledge")
            public int versionKnowledge;
        }
    }

    class knowLedgeData{

        /**
         * SizeVoaAnnotation : 5
         * VoaAnnotation : [{"id":1001,"anno_N":"1","note":"Excuse me.这个短语常用于与陌生人搭话，打断别人的说话或从别人身边挤过。在课文中，男士为了吸引女士的注意力而用了这个比较客套的短语。"},{"id":1001,"anno_N":"2","note":"Pardon？全句为I beg your pardon.意思是请求对方把刚才讲过的话重复一遍。"},{"id":1003,"anno_N":"1","note":"Here's 是 Here is的缩略形式。类似的例子有He's(He is),It's(It is)等。缩略形式和非缩略形式在英语的书面用语和口语中均有，但非缩略形式常用于比较正式的场合。"},{"id":1003,"anno_N":"2","note":"Sorry = I'm sorry。这是口语中的缩略形式，用语社交场合，向他人表示歉意。"},{"id":1003,"anno_N":"3","note":"Is this it?本句中it是指your umbrella。由于前面提到了，后面就用代词it 来代替，以免重复。"}]
         * SizeVoaStructure : 2
         * VoaStructure : [{"id":1001,"desc_EN":"","desc_CH":"","number":"0","note":"Is this your pen? 这是你的钢笔吗？ Is this your watch?这是你的手表吗？ Is this your shirt?这是你的衬衫吗？ I beg your pardon? 你能重复一下吗？"},{"id":1003,"desc_EN":"","desc_CH":"","number":"0","note":"sorry，sir.对不起，先生。 My coat and my umbrella please? 请把我的大衣和伞给我。这是一个省略形式的祈使句。 Here is my ticket.此处是倒装句。"}]
         * SizeVoaDiffculty : 0
         * VoaDiffculty : []
         */

        @SerializedName("SizeVoaAnnotation")
        public int SizeVoaAnnotation;
        @SerializedName("SizeVoaStructure")
        public int SizeVoaStructure;
        @SerializedName("SizeVoaDiffculty")
        public int SizeVoaDiffculty;
        @SerializedName("VoaAnnotation")
        public List<com.iyuba.conceptEnglish.sqlite.mode.VoaAnnotation> VoaAnnotation;
        @SerializedName("VoaStructure")
        public List<com.iyuba.conceptEnglish.sqlite.mode.VoaStructure> VoaStructure;
        @SerializedName("VoaDiffculty")
        public List<?> VoaDiffculty;
    }




}

