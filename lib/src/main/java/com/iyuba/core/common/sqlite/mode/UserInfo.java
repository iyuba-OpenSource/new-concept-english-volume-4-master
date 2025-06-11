package com.iyuba.core.common.sqlite.mode;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

/**
 * 用户信息
 *
 * @author 陈彤
 */

@Keep
public class UserInfo {
	public String icoins;
	public String uid;
	public String username;// 用户名--->用户昵称
	public String realName;// 真实的用户名！！！
	public String doings;// 发布的心情数
	public String views;// 访客数
	public String gender;// 性别
	public String text;// 最近的心情签名
	public String follower;// 粉丝
	public String relation;// 与当前用户关系 百位我是否关注他十位特别关注 个位他是否关注我
	public String following;// 关注
	public String iyubi;//爱语币的数量
	public String vipStatus;
	public String distance;
	public String middle_url;//头像
	public String amount;//爱语币的数量
	public String money;//钱包, 单位是分
	//新增 start
	//新增 end
	public String notification;
	public int studytime;
	public String position;
	@SerializedName("expireTime")
	public String deadline;
	public String isteacher;

	public UserInfo() {
		icoins = "0";
		uid = "0";
		username = "";
		doings = "0";
		views = "0";
		gender = "0";
		follower = "0";// 粉丝
		relation = "0";// 与当前用户关系 百位我是否关注他十位特别关注 个位他是否关注我
		following = "0";// 关注
		iyubi = "0";
		vipStatus = "0";
		notification = "0";
		studytime = 0;
		position = "100000";
		deadline = "";
	}

	@Override
	public String toString() {
		return "UserInfo{" +
				"icoins='" + icoins + '\'' +
				", uid='" + uid + '\'' +
				", username='" + username + '\'' +
				", realName='" + realName + '\'' +
				", doings='" + doings + '\'' +
				", views='" + views + '\'' +
				", gender='" + gender + '\'' +
				", text='" + text + '\'' +
				", follower='" + follower + '\'' +
				", relation='" + relation + '\'' +
				", following='" + following + '\'' +
				", iyubi='" + iyubi + '\'' +
				", vipStatus='" + vipStatus + '\'' +
				", distance='" + distance + '\'' +
				", middle_url='" + middle_url + '\'' +
				", amount='" + amount + '\'' +
				", money='" + money + '\'' +
				", notification='" + notification + '\'' +
				", studytime=" + studytime +
				", position='" + position + '\'' +
				", deadline='" + deadline + '\'' +
				", isteacher='" + isteacher + '\'' +
				'}';
	}
}
