package com.iyuba.core.common.protocol.message;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

public class RequestGetPhoneAndRand extends BaseJSONRequest {

	public RequestGetPhoneAndRand() {
		setAbsoluteURI("http://api."+Constant.IYUBA_COM+"getPhoneAndRand.jsp?format=json");
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO 自动生成的方法存根

	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO 自动生成的方法存根
		return null;
	}

}
