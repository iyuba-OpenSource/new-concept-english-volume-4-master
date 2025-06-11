package com.iyuba.conceptEnglish.manager;

import android.content.Context;
import android.util.Log;

import com.iyuba.conceptEnglish.listener.AppUpdateCallBack;
import com.iyuba.conceptEnglish.protocol.AppUpdateRequest;
import com.iyuba.conceptEnglish.protocol.AppUpdateResponse;
import com.iyuba.conceptEnglish.util.APKVersionCodeUtils;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.network.ClientSession;
import com.iyuba.core.common.network.IResponseReceiver;
import com.iyuba.core.common.protocol.BaseHttpRequest;
import com.iyuba.core.common.protocol.BaseHttpResponse;





/**
 * 版本管理 这个是真正的 版本管理！！！
 *
 * @author chentong
 *
 */

public class VersionManager {
	private static VersionManager instance;
	public static final int version = APKVersionCodeUtils.getVersionCode(CrashApplication.getInstance().getApplicationContext());

//    public static final int version = 10;
	public static final String VERSION_CODE = APKVersionCodeUtils.getVerName(CrashApplication.getInstance().getApplicationContext());
	private VersionManager() {
	}

	public static synchronized VersionManager Instace(Context context) {
		if (instance == null) {
			instance = new VersionManager();
		}
		return instance;
	}

	/**
	 * 检查是否有新版本
	 *
	 * @param version
	 * AppUpdateCallBack aucb
	 */
	public void checkNewVersion(int version, String packageId,final AppUpdateCallBack aucb) {
		ClientSession.Instace().asynGetResponse(new AppUpdateRequest(version,packageId),
				new IResponseReceiver() {

					@Override
					public void onResponse(BaseHttpResponse response,
							BaseHttpRequest request, int rspCookie) {
						AppUpdateResponse aur = (AppUpdateResponse) response;
						if (aur.result.equals("NO")) {
							// 有新版本
							if (aucb != null) {
								String data = aur.data.replace("||", "★");
								String[] appUpdateInfos = data.split("★");
								Log.e("^^^^^^^^^^^^", data);
								if (appUpdateInfos.length == 2) {
									aucb.appUpdateSave(appUpdateInfos[0], appUpdateInfos[1]);
								} else {
									aucb.appUpdateFaild();
								}
							}
						} else {
							// 检查失败
							if (aucb != null) {
								aucb.appUpdateFaild();
							}
						}
					}
				}, null, null);
	}
}
