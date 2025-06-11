package com.iyuba.conceptEnglish.protocol;

import com.iyuba.core.common.protocol.BaseJSONResponse;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdatePassResponse extends BaseJSONResponse {

	public String result;
	public String message;
	public String jifen;
	
	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		try {
			JSONObject jsonObjectRoot = new JSONObject(bodyElement);
			result= jsonObjectRoot.getString("result");
			message= jsonObjectRoot.getString("message");
			jifen = jsonObjectRoot.getString("jiFen");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		return true;
	}

}
