package com.iyuba.core.common.protocol.message;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.common.util.MD5;

public class RequestNewInfo extends BaseJSONRequest {
	public static final String protocolCode = "62001";

	public RequestNewInfo(String id) {
		setAbsoluteURI("http://api."+ Constant.IYUBA_COM+"v2/api.iyuba?protocol="
				+ protocolCode + "&uid=" + id + "&sign="
				+ MD5.getMD5ofStr(protocolCode + id + "iyubaV2"));
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
		// return null;
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new ResponseNewInfo();
	}

}
