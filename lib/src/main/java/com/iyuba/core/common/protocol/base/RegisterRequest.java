package com.iyuba.core.common.protocol.base;

import android.util.Log;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.common.util.MD5;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 用户注册
 * 
 * @author chentong
 * 
 */
public class RegisterRequest extends BaseJSONRequest {

	private String userName, email;

	public RegisterRequest(String userName, String password, String email,String phoneNum) {
		this.userName = userName;
		this.email = email;
		
		Log.d("RegistRequest:","http://api."+Constant.IYUBA_COM+"v2/api.iyuba?protocol=10002&email="
				+ this.email
				+ "&username="
				+ this.userName
				+ "&password="
				+ MD5.getMD5ofStr(password)
				+ "&platform=android&app="
				+ Constant.AppName
				+ "&format=xml&sign="
				+ MD5.getMD5ofStr("10002" + userName
						+ MD5.getMD5ofStr(password) + email + "iyubaV2")
		        +"&appid="+Constant.APPID
		        +"&mobile="+phoneNum);
		
		setAbsoluteURI("http://api."+Constant.IYUBA_COM+"v2/api.iyuba?protocol=10002&email="
				+ this.email
				+ "&username="
				+ this.userName
				+ "&password="
				+ MD5.getMD5ofStr(password)
				+ "&platform=android&app="
				+ Constant.AppName
				+ "&format=xml&sign="
				+ MD5.getMD5ofStr("10002" + userName
				+ MD5.getMD5ofStr(password) + email + "iyubaV2")
				+"&appid="+Constant.APPID
				+"&mobile="+phoneNum);
	
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
	}

	@Override
	public BaseHttpResponse createResponse() {
		return new RegistResponse();
	}

}
