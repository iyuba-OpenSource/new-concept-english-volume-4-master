package com.iyuba.conceptEnglish.protocol;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.conceptEnglish.util.MD5;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

public class NewInfoRequest extends BaseJSONRequest{
	public static final String protocolCode="62001";
	public NewInfoRequest(String id) {
		setAbsoluteURI("http://api."+ Constant.IYUBA_COM+"v2/api.iyuba?protocol="+ protocolCode
				+ "&uid=" + id
				+ "&sign="+MD5.getMD5ofStr(protocolCode+id+"iyubaV2"));
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {

	}

	@Override
	public BaseHttpResponse createResponse() {
		return new NewInfoResponse();
	}

}

