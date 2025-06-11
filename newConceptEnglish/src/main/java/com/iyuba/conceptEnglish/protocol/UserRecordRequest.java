package com.iyuba.conceptEnglish.protocol;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

public class UserRecordRequest extends BaseJSONRequest {

	public UserRecordRequest(String uid) {
		// TODO Auto-generated constructor stub

		setAbsoluteURI("http://daxue." + Constant.IYUBA_CN + "ecollege/getStudyRecord.jsp?uid="
				+ uid);

	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub

	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new UserRecordResponse();
	}

}
