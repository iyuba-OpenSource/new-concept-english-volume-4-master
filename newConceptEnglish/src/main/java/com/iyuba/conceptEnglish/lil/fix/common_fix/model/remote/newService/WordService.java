package com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.newService;

import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.StrLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.UrlLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.NetHostManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Word_note;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface WordService {

    //获取生词本数据
    //http://word.iyuba.cn/words/wordListService.jsp?u=12071118&pageNumber=1&pageCounts=10
    @Headers({StrLibrary.urlPrefix + ":" + UrlLibrary.HTTP_WORD, StrLibrary.urlHost + ":" + NetHostManager.domain_short})
    @GET("/words/wordListService.jsp")
    Observable<Word_note> getWordNoteData(@Query("u") int userid,
                                          @Query("pageNumber") int pageIndex,
                                          @Query("pageCounts") int pageCount);
}
