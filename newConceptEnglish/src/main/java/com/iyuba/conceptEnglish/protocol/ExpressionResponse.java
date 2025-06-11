package com.iyuba.conceptEnglish.protocol;

import org.json.JSONObject;

import com.iyuba.core.common.protocol.BaseJSONResponse;

public class ExpressionResponse extends BaseJSONResponse{

	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		return true;
	}

}
