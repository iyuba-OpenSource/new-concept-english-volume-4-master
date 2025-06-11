package com.iyuba.conceptEnglish.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.iyuba.conceptEnglish.event.RefreshBookEvent;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.ConceptDataManager;
import com.iyuba.conceptEnglish.sqlite.mode.Book;
import com.iyuba.conceptEnglish.sqlite.mode.DownloadInfo;
import com.iyuba.conceptEnglish.sqlite.mode.Voa;
import com.iyuba.conceptEnglish.sqlite.op.BookOp;
import com.iyuba.conceptEnglish.sqlite.op.DownloadInfoOp;
import com.iyuba.conceptEnglish.sqlite.op.VoaOp;
import com.iyuba.conceptEnglish.sqlite.op.VoaWordOp;
import com.iyuba.conceptEnglish.util.ClearBuffer;
import com.iyuba.configation.*;
import com.iyuba.configation.ConfigManager;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.event.UpdateUnitTitleEvent;

import org.greenrobot.eventbus.EventBus;

public class DownloadStateManager {
    private static DownloadStateManager instance;

    private Context mContext;
    public DownloadInfoOp downloadInfoOp;
    public BookOp bookOp;
    public VoaWordOp voaWordOp;
    public VoaOp voaOp;

    public List<DownloadInfo> downloadList;
    public List<Book> bookList = new ArrayList<>();

    public DownloadStateManager() {
        mContext = RuntimeManager.getContext();
        downloadInfoOp = new DownloadInfoOp(mContext);
        bookOp = new BookOp(mContext);
        voaWordOp = new VoaWordOp(mContext);
        voaOp=new VoaOp(mContext);
        downloadList = downloadInfoOp.query();

        bookList = bookOp.findData();


    }

    public static synchronized DownloadStateManager instance() {
        if (instance == null) {
            instance = new DownloadStateManager();
        }

        return instance;
    }

    public void updateDownloadInfo(DownloadInfo info) {
        downloadInfoOp.update(info);
    }

    public void updateBook(int bookId) {
        bookOp.updateDownloadNum(bookId);
//        boolean isAmerican = ConfigManager.Instance().isAmercan();
//        if (isAmerican) {
//            bookList.get(bookId / 1000 - 1);
//        } else {
//            bookList.get(bookId / 10000 - 1 + 4);
//        }

    }

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    CustomToast.showToast(RuntimeManager.getContext(), "网络问题或服务器端错误", 2000);
                    break;
                case 1:
                    CustomToast.showToast(mContext, "正在下载", 2000);
                    break;
                case 2:
                    int voaId = msg.arg1;

                    try {
                        switch (ConfigManager.Instance().getBookType()){
                            case AMERICA:
                            default:
                                CustomToast.showToast(mContext, "第" + (voaId % 1000) + "课下载完成", 2000);
                                break;
                            case ENGLISH:
                                CustomToast.showToast(mContext, "第" + (voaId / 10 % 1000) + "课下载完成", 2000);
                                break;
                            case YOUTH:
                                CustomToast.showToast(mContext, "第" + voaWordOp.getUnitIdByVoaid(voaId+"")
                                        + "课下载完成", 2000);
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    EventBus.getDefault().post(new UpdateUnitTitleEvent());
                    break;
                default:
                    break;
            }
        }
    };

    public void delete(int voaid) {
        for (DownloadInfo info : downloadList) {
            if (info.voaId == voaid || (!ConfigManager.Instance().isAmercan() && info.voaId == voaid * 10)) {
                downloadInfoOp.delete(voaid);
                info.downloadedState = 0;
                info.downloadPer = 0;
                info.downloadedBytes = 0;
                new ClearBuffer("audio/"
                        + info.voaId
                        + Constant.append).Delete();

                if (ConfigManager.Instance().isAmercan()) {
                    File file = new File(Constant.videoAddr + voaid
                            + Constant.append);
                    if (file.isFile()) {
                        file.delete();
                    }
                }
                break;
            }
        }
        /* 目前英文根据文件寻找，未必出现在downloadInfo里面，直接删除即可。*/
        if (!ConfigManager.Instance().isAmercan()) {
            File file = new File(Constant.videoAddr + voaid + "_B"
                    + Constant.append);
            if (file.isFile()) {
                file.delete();
            }
        }

    }
}
