package com.iyuba.conceptEnglish.protocol;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.common.util.MD5;

public class UpdateExerciseRecordRequest extends BaseJSONRequest {

	private String appName = "concept";
	private String macAddress = "";
	private String jsonStr = "";
	private String uid = ConfigManager.Instance().loadString("userId");
	SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
	private String sign;

	public UpdateExerciseRecordRequest(JSONObject json) {
		WifiManager wifiManager = (WifiManager) CrashApplication.getInstance().getApplicationContext()
				.getSystemService(Context.WIFI_SERVICE);
		if (wifiManager != null) {
			WifiInfo wInfo = wifiManager.getConnectionInfo();
			if (wInfo != null) {
				macAddress = wInfo.getMacAddress();
			}
		}

		try {
			jsonStr = URLEncoder.encode(json.toString(), "UTF-8");
			appName = URLEncoder.encode(URLEncoder.encode(appName, "UTF-8"),
					"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		this.sign = uid + "iyubaTest" + dft.format(System.currentTimeMillis());
		String url = "http://daxue." + Constant.IYUBA_CN + "ecollege/updateTestRecordNew.jsp?format=json"
				+ "&uid="
				+ uid
				+ "&appId="
				+ Constant.APPID
				+ "&DeviceId="
				+ macAddress
				+ "&appName="
				+ appName
				+ "&sign="
				+ MD5.getMD5ofStr(sign) + "&jsonStr=" + jsonStr;

		Log.e("url", url);

		setAbsoluteURI(url);
	}

	@Override
	public BaseHttpResponse createResponse() {
		return new UpdateExerciseRecordResponse();
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {

	}

}
