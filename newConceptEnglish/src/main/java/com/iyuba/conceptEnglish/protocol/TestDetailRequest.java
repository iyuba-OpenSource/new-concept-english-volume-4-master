package com.iyuba.conceptEnglish.protocol;

import java.text.SimpleDateFormat;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.common.util.MD5;

public class TestDetailRequest extends BaseJSONRequest {
	SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
	private String sign;

	public TestDetailRequest(String uid, String testMode,String page, String numPerPage) {
		this.sign = uid + dft.format(System.currentTimeMillis());
		setAbsoluteURI("http://daxue." + Constant.IYUBA_CN + "ecollege/getTestRecordDetail.jsp?format=json&uid="
				+ uid
				+ "&TestMode="
				+ testMode
				+ "&sign="
				+ MD5.getMD5ofStr(sign)+"&Pageth="
						+ page
						+ "&NumPerPage="
						+ numPerPage);
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
	}

	@Override
	public BaseHttpResponse createResponse() {
		return new TestDetailResponse();
	}

}
