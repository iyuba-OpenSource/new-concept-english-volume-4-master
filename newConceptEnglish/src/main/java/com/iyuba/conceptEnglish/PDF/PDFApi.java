package com.iyuba.conceptEnglish.PDF;


import com.iyuba.configation.Constant;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * 获取群号
 */

public interface PDFApi {

//    新概念生成中英双文链接：
//    http://apps."+ Constant.IYUBA_CN+"iyuba/getConceptPdfFile.jsp?voaid=1003&type=concept
//    获取中英双文的pdf链接：
//    http://apps."+ Constant.IYUBA_CN+"iyuba/ceptpdf/1001.pdf
//
//    新概念生成全英链接
//    http://apps."+ Constant.IYUBA_CN+"iyuba/getConceptPdfFile_eg.jsp?voaid=1001&type=concept
//    获取全英链接
//    http://apps."+ Constant.IYUBA_CN+"iyuba/ceptpdf_eg/1001.pdf


    public String URL = " http://apps." + Constant.IYUBA_CN + "iyuba/getConceptPdfFile.jsp";
    public String URL_EG = " http://apps." + Constant.IYUBA_CN + "iyuba/getConceptPdfFile_eg.jsp";


    public String PDF_TYPE = "concept";

    @GET
    Call<PDFBean> getPDFInfo(
            @Url String url,
            @Query("type") String type,
            @Query("voaid") String voaid
    );
    class PDFBean {
        /**
         * {
         * "exists":"true",
         * "path":"/pdf/10010.pdf"
         * }
         */
        public String exists;
        public String path;
    }

    //中小学内容
    @GET
    Call<PDFBean> getJuniorPDFInfo(
            @Url String url,
            @Query("type") String type,
            @Query("voaid") String voaid,
            @Query("isenglish") int isEnglish
    );
}
