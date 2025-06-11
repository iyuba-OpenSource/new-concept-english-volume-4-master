package com.iyuba.core.common.data.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Location {

    /**
     * p : 北京
     * c : [{"n":"东城区"},{"n":"西城区"},{"n":"崇文区"},{"n":"宣武区"},{"n":"朝阳区"},{"n":"丰台区"},{"n":"石景山区"},{"n":"海淀区"},{"n":"门头沟区"},{"n":"房山区"},{"n":"通州区"},{"n":"顺义区"},{"n":"昌平区"},{"n":"大兴区"},{"n":"平谷区"},{"n":"怀柔区"},{"n":"密云县"},{"n":"延庆县"}]
     */

    private List<CitylistBean> citylist;

    public static Location objectFromData(String str) {

        return new Gson().fromJson(str, Location.class);
    }

    public List<CitylistBean> getCitylist() {
        return citylist;
    }

    public void setCitylist(List<CitylistBean> citylist) {
        this.citylist = citylist;
    }

    public static class CitylistBean {
        @SerializedName("p")
        private String province;
        /**
         * n : 东城区
         */
        @SerializedName("c")
        private List<CityBean> cityBean;

        public static CitylistBean objectFromData(String str) {

            return new Gson().fromJson(str, CitylistBean.class);
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public List<CityBean> getCityBean() {
            return cityBean;
        }

        public void setCityBean(List<CityBean> cityBean) {
            this.cityBean = cityBean;
        }

        public List<String> getCityStringList() {
            List<CityBean> rawList = getCityBean();
            if (rawList == null) {
                return null;
            }

            List<String> cityStringList = new ArrayList<>();
            for (CityBean rawData : rawList) {
                cityStringList.add(rawData.getCity());
            }
            return cityStringList;
        }

        public static class CityBean {
            @SerializedName("n")
            private String city;

            public static CityBean objectFromData(String str) {

                return new Gson().fromJson(str, CityBean.class);
            }

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }
        }
    }
}
