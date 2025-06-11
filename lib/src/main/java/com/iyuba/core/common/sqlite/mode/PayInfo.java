package com.iyuba.core.common.sqlite.mode;

import androidx.annotation.Keep;

/**
 * 用户信息
 *
 * @author 陈彤
 */

@Keep
public class PayInfo {
	public String msg;
	public String code;


	public PayInfo() {
		msg = "0";
		code = "-1";
	}
}
