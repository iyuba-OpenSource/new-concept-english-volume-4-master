package com.iyuba.conceptEnglish.protocol;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.core.common.protocol.BaseJSONResponse;

public class DataCollectResponse extends BaseJSONResponse{

	public String result;
	public String message;
	public String score;
	public String reward;
	public String rewardMessage;

	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		// TODO Auto-generated method stub
		try {
			JSONObject jsonObjectRoot = new JSONObject(bodyElement);
			result= jsonObjectRoot.getString("result");
			message= jsonObjectRoot.getString("message");
			score = jsonObjectRoot.getString("jifen");
			reward = jsonObjectRoot.getString("reward");
			rewardMessage = jsonObjectRoot.getString("rewardMessage");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		return true;
	}


	@Override
	public String toString() {
		return "DataCollectResponse{" +
				"result='" + result + '\'' +
				", message='" + message + '\'' +
				", score='" + score + '\'' +
				", reward='" + reward + '\'' +
				", rewardMessage='" + rewardMessage + '\'' +
				'}';
	}
}
