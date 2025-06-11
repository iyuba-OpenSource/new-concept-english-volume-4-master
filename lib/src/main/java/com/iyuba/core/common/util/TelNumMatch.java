package com.iyuba.core.common.util;

import android.util.Log;

/**
 * judge if the number is a tel number
 * 
 * @author Administrator
 * 
 */
public class TelNumMatch {

	/*
         2020.5.25更新,运营商号段更新
        移动
        139 138 137 136 135 134
        147 148
        188 187 184 183 182
        178 172
        159 158 157 152 151 150
        165
        198  195
        170(5)

        联通
        185 186
        156 155
        175 176  171
        166
        130 131 132
        145 146

        电信
        189 181 180
        177
        153 133 173 199
        149 174 191

        特殊170：
        1700电信；1705移动；1709联通；其他号码，虚拟运营商
        */
	/*
	^(13[0-2])[0-9]{8}
	static String YD = "^[1]{1}(([3]{1}[4-9]{1})|([5]{1}[012789]{1})|([8]{1}[2378]{1})|([4]{1}[7]{1}))[0-9]{8}$";
	static String LT = "^[1]{1}(([3]{1}[0-2]{1})|([5]{1}[56]{1})|([8]{1}[56]{1}))[0-9]{8}$";
	static String DX = "^[1]{1}(([3]{1}[3]{1})|([5]{1}[3]{1})|([8]{1}[09]{1}))[0-9]{8}$";*/
	/* 170有多家运营商，而且也是虚拟运营商。目前已经全部在移动里面声明。由于不要求鉴别，目前170全部属于移动 */
	static String YD = "^((13[4-9])|(165)|(14[7-8])|(17[0,2,8])|(19[5,8])|(15[0-2,7-9])|(18[2-4,7-8]))[0-9]{8}$";
	static String LT = "^((13[0-2])|(14[5-6])|(15[5-6])|(17[1,5-6])|(166)|(18[5,6]))[0-9]{8}|(1709)[0-9]{7}$";
	static String DX = "^((19[1,9])|(149)|(133)|(153)|(17[3,4,7])|(18[0,1,9]))[0-9]{8}$";

	String mobPhnNum;

	public TelNumMatch(String mobPhnNum) {
		this.mobPhnNum = mobPhnNum;
		Log.d("tool", mobPhnNum);
	}

	public int matchNum() {
		/*
		 * flag = 1 YD 2 LT 3 DX 
		 */
		int flag;// 存储匹配结果
		// 判断手机号码是否??1??
		if (mobPhnNum.length() == 11) {
			// 判断手机号码是否符合中国移动的号码规??
			if (mobPhnNum.matches(YD)) {
				flag = 1;
			}
			// 判断手机号码是否符合中国联??的号码规??
			else if (mobPhnNum.matches(LT)) {
				flag = 2;
			}
			// 判断手机号码是否符合中国电信的号码规??
			else if (mobPhnNum.matches(DX)) {
				flag = 3;
			}
			// 都不合?? 未知
			else {
				flag = 4;
			}
		}
		// 不是11??
		else {
			flag = 5;
		}
		Log.d("TelNumMatch", "flag"+flag);
		//返回1,2,3 均认定为手机号，其他则认为不??
		return flag;
	}
}