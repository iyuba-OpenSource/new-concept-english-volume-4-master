package com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.manager;

import com.iyuba.conceptEnglish.entity.CommonResponce;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.RemoteManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.newService.CommonService;
import com.iyuba.conceptEnglish.sqlite.mode.Voa;

import java.util.List;

import io.reactivex.Observable;

public class FixRemoteManager {

    //获取首页的微课进度数据
    public static Observable<CommonResponce<List<Voa>>> getHomeMocSync(int bookId,String language,int uid){
        CommonService commonService = RemoteManager.getInstance().createJson(CommonService.class);
        return commonService.getHomeMocSunc(bookId, language, uid);
    }
}
