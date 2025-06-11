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
public class AppUpdateRequest extends BaseJSONRequest {

	private int version;

	//增加包名
	//concept2包名的升级信息(com.iyuba.concept2)
	//englishfm包名的升级信息(com.iyuba.englishfm)
	//newconcepttop包名的升级信息(com.iyuba.newconcepttop)
	private String packageId;

	public AppUpdateRequest(int version,String packageId) {
		this.version = version;
		this.packageId = packageId;

		String updateUrl = String.format(Constant.appUpdateUrl,this.version,this.packageId);
		setAbsoluteURI(updateUrl);
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
	}

	@Override
	public BaseHttpResponse createResponse() {
		return new AppUpdateResponse();
	}

}
