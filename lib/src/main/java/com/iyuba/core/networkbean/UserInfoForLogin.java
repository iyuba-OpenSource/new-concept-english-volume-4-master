package com.iyuba.core.networkbean;

/**
 * 网络请求中用到的 实体类
 */
public class UserInfoForLogin {
    /**
     * albums = 0;//专辑数
     *     allThumbUp = 552;//总的点赞数
     *     amount = 24;//爱语币数量
     *     bio = "是啊真的好想好好学学吧”;//一句话简介
     *     blogs = 0;//日志数
     *     contribute = 0;
     *     credits = 10304;//积分数
     *     distance = "";
     *     doings = 47;//说说数目
     *     email = "chenjinrong@iyuba.cn”;//注册邮箱
     *     expireTime = 1689400894;//会员到期时间
     *     follower = 222;//粉丝数
     *     following = 50;//关注数
     *     friends = 0;
     *     gender = 1;//性别
     *     icoins = 10304;//积分
     *     message = "";
     *     "middle_url" = "head/2021/2/5/19/3/52/59f71a19-77b9-4093-ae47-9781fc1bb014-m.jpg”;//头像
     *     mobile = 00000000000;//手机号
     *     money = 315;//钱包, 单位是分
     *     nickname = "每个月”;//昵称
     *     posts = 0;
     *     relation = 0;
     *     result = 201;
     *     sharings = 0;
     *     shengwang = 0;//声望数
     *     text = "good morning”;//最近的一个说说文字内容
     *     username = Jinrong110;//用户名
     *     views = 27571;//多少人看我的主页
     *     vipStatus = 1;//会员状态
     *     isteacher 是否是老师
     */
    public String albums = "0";
    public String gender = "0";
    public String distance = "0";
    public String blogs = "0";
    public String middle_url = "0";
    public String contribute = "0";
    public String shengwang = "0";
    public String bio = "0";
    public String posts = "0";
    public String relation = "0";
    public String result = "0";
    public String credits = "0";
    public String nickname = "0";
    public String email = "0";
    public String views = "0";
    public String amount = "0";
    public String follower = "0";
    public String mobile = "0";
    public String allThumbUp = "0";
    public String icoins = "0";
    public String message = "0";
    public String friends = "0";
    public String doings = "0";
    public String expireTime = "";
    public String money = "0";
    public String following = "0";
    public String sharings = "0";
    public String vipStatus = "0";
    public String username = "";
    public String isteacher = "0";

    @Override
    public String toString() {
        return "UserInfoForLogin{" +
                "albums='" + albums + '\'' +
                ", gender='" + gender + '\'' +
                ", distance='" + distance + '\'' +
                ", blogs='" + blogs + '\'' +
                ", middle_url='" + middle_url + '\'' +
                ", contribute='" + contribute + '\'' +
                ", shengwang='" + shengwang + '\'' +
                ", bio='" + bio + '\'' +
                ", posts='" + posts + '\'' +
                ", relation='" + relation + '\'' +
                ", result='" + result + '\'' +
                ", credits='" + credits + '\'' +
                ", nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                ", views='" + views + '\'' +
                ", amount='" + amount + '\'' +
                ", follower='" + follower + '\'' +
                ", mobile='" + mobile + '\'' +
                ", allThumbUp='" + allThumbUp + '\'' +
                ", icoins='" + icoins + '\'' +
                ", message='" + message + '\'' +
                ", friends='" + friends + '\'' +
                ", doings='" + doings + '\'' +
                ", expireTime='" + expireTime + '\'' +
                ", money='" + money + '\'' +
                ", following='" + following + '\'' +
                ", sharings='" + sharings + '\'' +
                ", vipStatus='" + vipStatus + '\'' +
                ", username='" + username + '\'' +
                ", isteacher='" + isteacher + '\'' +
                '}';
    }
}
