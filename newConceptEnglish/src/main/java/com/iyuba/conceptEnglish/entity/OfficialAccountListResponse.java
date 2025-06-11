package com.iyuba.conceptEnglish.entity;

import java.util.List;

public class OfficialAccountListResponse {
    private int result;
    private List<AccountBean> data;

    public static class AccountBean{
        private String newsfrom;
        private String createTime;
        private String image_url;
        private String count;
        private String id;
        private String title;
        private String url;


        public String getNewsfrom() {
            return newsfrom;
        }

        public void setNewsfrom(String newsfrom) {
            this.newsfrom = newsfrom;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getImage_url() {
            return image_url;
        }

        public void setImage_url(String image_url) {
            this.image_url = image_url;
        }

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public List<AccountBean> getData() {
        return data;
    }

    public void setData(List<AccountBean> data) {
        this.data = data;
    }
}
