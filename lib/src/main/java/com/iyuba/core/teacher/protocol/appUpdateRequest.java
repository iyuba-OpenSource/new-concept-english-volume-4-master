package com.iyuba.core.teacher.protocol;

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
public class appUpdateRequest extends BaseJSONRequest {

	private int version;

	//增加包名
	private String packageId;

	public appUpdateRequest(int version,String packageId) {
		this.version = version;
		this.packageId = packageId;

		String updateUrl = String.format(Constant.appUpdateUrl,this.version,this.packageId);
		setAbsoluteURI(updateUrl);
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new appUpdateResponse();
	}

}
