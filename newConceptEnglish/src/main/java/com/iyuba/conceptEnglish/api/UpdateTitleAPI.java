package com.iyuba.conceptEnglish.api;

import com.iyuba.configation.Constant;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by liuzhenli on 2017/5/23.
 */

public interface UpdateTitleAPI {

    //淘汰 start
    //    http://m."+Constant.IYUBA_CN+"jlpt1/getVersion.jsp?book=1&type=UK
//    String url = "http://m." + Constant.IYUBA_CN + "jlpt1/getVersion.jsp";
    //淘汰 end

    //    http://apps."+ Constant.IYUBA_CN+"concept/getVersion.jsp?book=2&type=UK
    String url = "http://apps."+ Constant.IYUBA_CN+"concept/getVersion.jsp";

    String TYPE_UK = "UK";
    String TYPE_US = "US";

    @GET
    Call<UpdateTitleBean> getData(
            @Url String url,
            @Query("book") int bookId,
            @Query("type") String type
    );


//    //http://m."+Constant.IYUBA_CN+"jlpt1/getVersionWord.jsp?book=1
//    String word_url = "http://m." + Constant.IYUBA_CN + "jlpt1/getVersionWord.jsp"; //弃用

    //http://apps."+ Constant.IYUBA_CN+"concept/getVersionWord.jsp?book=1
    String word_url = "http://apps."+ Constant.IYUBA_CN+"concept/getVersionWord.jsp";



    @GET
    Call<UpdateTitleBean> getWordData(
            @Url String url,
            @Query("book") int bookId
    );


    class UpdateTitleBean {

        /**
         * size : 1
         * data : [{"voa_id":1001,"version":"1"}]
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
             * version_us : 1
             */

            private int voa_id;
            private String version;

            public int getVoa_id() {
                return voa_id;
            }

            public void setVoa_id(int voa_id) {
                this.voa_id = voa_id;
            }

            public String getVersion() {
                return version;
            }

            public void setVersion(String version) {
                this.version = version;
            }
        }
    }

}

