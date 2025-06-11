package com.iyuba.conceptEnglish.protocol;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

/**
 * 用户登录
 * 
 * @author chentong
 */
public class AdRequest extends BaseJSONRequest {

	public AdRequest() {
		//TODO
		String url="http://app." + Constant.IYUBA_CN + "dev/getAdEntryAll.jsp?appId=222&flag=1";
		setAbsoluteURI(url);
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new AdResponse();
	}

}
