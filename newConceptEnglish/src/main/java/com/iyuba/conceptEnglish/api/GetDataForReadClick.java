package com.iyuba.conceptEnglish.api;

import com.iyuba.configation.Constant;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface GetDataForReadClick {

//    http://apps."+ Constant.IYUBA_CN+"iyuba/textExamApi.jsp?format=json&voaid=313001
    String url = "http://apps."+ Constant.IYUBA_CN+"iyuba/textExamApi.jsp";

    String FORMAT = "json";

    @GET
    Call<ClickReadResponse> getData(
            @Url String url,
            @Query("format") String format,
            @Query("voaid") String voaid
    );

    class ClickReadResponse{
        public int total;
        public String Images;
        public List<VoatextDetail> voatext;
    }

    class VoatextDetail{
        public String ImgPath;
        public float EndTiming;
        public int ParaId;
        public int IdIndex;
        public String sentence_cn;
        public String ImgWords;
        public int Start_x;
        public int End_y;
        public float Timing;
        public int End_x;
        public String Sentence;
        public int Start_y;
    }
}
