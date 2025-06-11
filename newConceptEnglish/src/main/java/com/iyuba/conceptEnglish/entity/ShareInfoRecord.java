package com.iyuba.conceptEnglish.entity;

public class ShareInfoRecord {
    public String uid;
    public String createtime;
    public String appid;
    public int scan;
    public String shareId;
    public ShareInfoRecord(){}
    public ShareInfoRecord(String uid, String createtime, String appid, int scan, String shareId) {
        this.uid = uid;
        this.createtime = createtime;
        this.appid = appid;
        this.scan = scan;
        this.shareId = shareId;
    }

    @Override
    public String toString() {
        return "ShareInfoRecord{" +
                "uid='" + uid + '\'' +
                ", createtime='" + createtime + '\'' +
                ", appid='" + appid + '\'' +
                ", scan=" + scan +
                ", shareId='" + shareId + '\'' +
                '}';
    }
    //哪个低能不会用toString？
}
