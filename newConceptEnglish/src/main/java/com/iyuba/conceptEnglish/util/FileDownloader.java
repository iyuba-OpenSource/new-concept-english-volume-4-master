package com.iyuba.conceptEnglish.util;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.iyuba.conceptEnglish.manager.DownloadStateManager;
import com.iyuba.conceptEnglish.sqlite.mode.Book;
import com.iyuba.conceptEnglish.sqlite.mode.DownloadInfo;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.lil.user.UserInfoManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class FileDownloader extends Thread {
    private static FileDownloader instance;
    private DownloadStateManager downloadStateManager;

    private int fileLen;

    private int downPercent;
    public DownloadInfo curInfo;
    public List<DownloadInfo> infoList;
    public List<Book> bookList;
    private String urlStr = null;
    private String dirPath = null;
    private String savePath = null;
    private File file = null;
    public int downloadNum = -1;
    public int downloadState = 0;

    public Handler handler;

    public static FileDownloader instance() {
        if (instance == null) {
            instance = new FileDownloader();
            instance.start();
        }

        return instance;
    }

    public int getDownloadState() {
        for (Book book : bookList) {
            Log.e("book.downloadState", book.downloadState + "=====" + book.bookId);
            if (book.downloadState == 1) {
                return (downloadState = 1);
            }
        }

        return (downloadState = 0);
    }


    public FileDownloader() {
        downloadStateManager = DownloadStateManager.instance();
        infoList = downloadStateManager.downloadList;
        bookList = downloadStateManager.bookList;
        handler = downloadStateManager.handler;

        makedir();
    }

    public void makedir() {
        dirPath = ConfigManager.Instance().loadString("media_saving_path");
        file = new File(dirPath);
        if (file != null) {
            file.mkdirs();
        }
    }

    public void run() {
        for (int i = 0; ; i++) {
            try {
                if (i == infoList.size()) {
                    if (downloadNum == 0 || infoList.size() == 0) {
                        synchronized (bookList) {
                            try {
                                setBookDownloadState(0);
                                bookList.wait();
                                downloadState = 0;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    i = 0;
                    downloadNum = 0;
                }

                curInfo = infoList.get(i);

                Log.e(curInfo.voaId + "", curInfo.downloadedState + "");

                switch (ConfigManager.Instance().getBookType()) {
                    case AMERICA:
                    case YOUTH:
                    default:
                        savePath = dirPath + "/" + curInfo.voaId;
                        break;
                    case ENGLISH:
                        Log.e("下载===" + curInfo.voaId + "", curInfo.downloadedState + "");
                        savePath = dirPath + "/" + curInfo.voaId / 10 + "_B";
                        break;
                }

                // 当当前文件的状态为等待时，设置下载状态为正在下载
                if (curInfo.downloadedState == -2 || curInfo.downloadedState == 1) {
                    curInfo.downloadedState = 1;
                    // 设置一册书的下载状态
                    switch (ConfigManager.Instance().getBookType()) {
                        case AMERICA:
                        default:
                            setBookDownloadState(curInfo.voaId / 1000);
                            break;
                        case YOUTH:
                            int bookid=DownloadStateManager.instance().voaOp.findDataById(curInfo.voaId).category;
                            setBookDownloadState(bookid);
                            break;
                        case ENGLISH:
                            setBookDownloadState(curInfo.voaId / 10000);
                            break;
                    }
                    downloadNum++;
                    downloadState = 1;
                    download();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setBookDownloadState() {
        for (Book tempBook : bookList) {
            if (downloadNum == 0) {
                if (tempBook.downloadState == 1 || tempBook.downloadState == -2) {
                    tempBook.downloadState = -1;
                }
            }
        }
    }


    public void setBookDownloadState(int bookId) {

        int num = 1000;
        switch (ConfigManager.Instance().getBookType()) {
            case AMERICA:
            default:
                num = 1000;
                break;
            case YOUTH:
                num = 1;
                break;
            case ENGLISH:
                num = 10000;
                break;
        }

        for (Book tempBook : bookList) {
            if (tempBook.bookId == bookId * num) {
                tempBook.downloadState = 1;
            } else if (tempBook.downloadState == 1) {
                tempBook.downloadState = -2;
            }
        }
    }

    public void download() {

        switch (ConfigManager.Instance().getBookType()) {
            case AMERICA:
            case ENGLISH:
            default:
                String headUrl;
                if (UserInfoManager.getInstance().isVip()){
                    headUrl="http://staticvip2." + Constant.IYUBA_CN + "newconcept/";
                }else {
                    headUrl=Constant.sound;
                }
                urlStr = headUrl + curInfo.url;
                break;
            case YOUTH:
                urlStr = "http://"+Constant.staticStr+Constant.IYUBA_CN+"sounds/voa/sentence/202005/"
                        + curInfo.voaId
                        + "/"
                        + curInfo.voaId
                        + Constant.append;
                break;
        }

        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", "Net");

            // 设置超时时间
            conn.setConnectTimeout(10000);
            if (conn.getResponseCode() == 200) {
                fileLen = conn.getContentLength();

                file = new File(savePath + Constant.append);
                // 当文件存在时，继续下载下一个文件
                if (file.exists()) {
                    if (curInfo.downloadedState != 2) {
                        curInfo.downloadedState = 2;
                        downloadStateManager.updateDownloadInfo(curInfo);

                        switch (ConfigManager.Instance().getBookType()) {
                            case AMERICA:
                            default:
                                downloadStateManager.updateBook(curInfo.voaId / 1000 * 1000);
                                break;
                            case YOUTH:
                                int bookid=DownloadStateManager.instance().voaOp.findDataById(curInfo.voaId).category;
                                downloadStateManager.updateBook(bookid);
                                break;
                            case ENGLISH:
                                downloadStateManager.updateBook(curInfo.voaId / 10000 * 10000);
                                break;
                        }
                    }
                    curInfo.downloadedBytes = fileLen;
                    curInfo.totalBytes = fileLen;
                    downPercent = 100;
                    curInfo.downloadPer = downPercent;
                    curInfo.downloadedState = 2;
                    downloadStateManager.updateDownloadInfo(curInfo);

                    int bookId;
                    Book tempBook;
                    switch (ConfigManager.Instance().getBookType()) {
                        case AMERICA:
                        default:
                            bookId = curInfo.voaId / 1000 * 1000;
                            tempBook = bookList.get(bookId / 1000 - 1 + 12);
                            break;

                        case YOUTH:
                            bookId = DownloadStateManager.instance().voaOp.findDataById(curInfo.voaId).category;
                            tempBook = bookList.get(bookId - 278);
                            break;
                        case ENGLISH:
                            bookId = curInfo.voaId / 10000 * 10000;
                            tempBook = bookList.get(bookId / 10000 - 1 + 16);
                            break;
                    }
                    downloadStateManager.updateBook(bookId);
                    tempBook.downloadNum++;
                    if (tempBook.downloadNum > tempBook.totalNum) {
                        tempBook.downloadNum = tempBook.totalNum;
                    }

                    if (getBookDownloadNum(tempBook.bookId) <= 0) {
                        tempBook.downloadState = 0;
                    }

                    Message msg = handler.obtainMessage();
                    msg.what = 2;
                    msg.arg1 = curInfo.voaId;
                    handler.sendMessage(msg);
                    return;
                }

                file = new File(savePath);
                //这里有个问题啊，你需要把文件创建出来后写入才行啊
                //哪个沙雕写的，api也不能自己创建啊
                //这里功能比较奇葩，先写入文件，然后更换名称
                //暂时不处理，之后再说吧
                String suffix = ".mp3";
                if (!TextUtils.isEmpty(urlStr)){
                    int index = urlStr.lastIndexOf(".");
                    if (index!=-1){
                        suffix = urlStr.substring(index);
                    }
                }

                String newPath = file.getPath()+suffix;
                file = new File(newPath);
                if (!file.getParentFile().exists()){
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();

                RandomAccessFile raf = new RandomAccessFile(file, "rws");
                raf.setLength(fileLen);
                raf.close();

                savePath = file.getPath();
                download(url, file, fileLen);
            } else {
                throw new IllegalArgumentException("404 path: " + urlStr);
            }
        } catch (IOException e) {
            curInfo.downloadedState = 0;
            Log.e("download", e.getMessage());
            Log.e("url", urlStr);

            handler.sendEmptyMessage(0);
        }
    }

    public void download(URL url, File file, int fileLen) {
        long start = curInfo.downloadedBytes; // 开始位置 += 已下载量
        long end = fileLen - 1;

        if (start > end) {
            file.delete();
            file = new File(savePath);
            curInfo.downloadedBytes = 0;

            start = 0;
        }

        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(3000);
            // 获取指定位置的数据，Range范围如果超出服务器上数据范围, 会以服务器数据末尾为准 + end
            conn.setRequestProperty("RANGE", "bytes=" + start + "-" + end);
            RandomAccessFile raf = new RandomAccessFile(file, "rws");
            raf.seek(start);
            // 开始读写数据
            InputStream in = conn.getInputStream();
            byte[] buf = new byte[1024 * 10];
            int len;
            while ((len = in.read(buf)) != -1) {
                raf.write(buf, 0, len);
                curInfo.downloadedBytes += len;
                curInfo.totalBytes = fileLen;
                downPercent = (int) (curInfo.downloadedBytes * 100 / fileLen);
                curInfo.downloadPer = downPercent;

                // 记录每个线程已下载的数据量
                downloadStateManager.updateDownloadInfo(curInfo);

                if (curInfo.downloadedState == -1) {
                    break;
                }
            }
            in.close();
            raf.close();

            if (curInfo.downloadedBytes == curInfo.totalBytes) {
                curInfo.downloadedState = 2;
                downloadStateManager.updateDownloadInfo(curInfo);

                int bookId;
                Book tempBook;

                switch (ConfigManager.Instance().getBookType()) {
                    case AMERICA:
                    default:
                        bookId = curInfo.voaId / 1000 * 1000;
                        tempBook = bookList.get(bookId / 1000 - 1 + 12);
                        break;
                    case YOUTH:
                        bookId = DownloadStateManager.instance().voaOp.findDataById(curInfo.voaId).category;
                        tempBook = bookList.get(bookId - 278);
                        break;
                    case ENGLISH:
                        bookId = curInfo.voaId / 10000 * 10000;
                        tempBook = bookList.get(bookId / 10000 - 1 + 16);
                        break;
                }

                downloadStateManager.updateBook(bookId);
                tempBook.downloadNum++;
                if (tempBook.downloadNum > tempBook.totalNum) {
                    tempBook.downloadNum = tempBook.totalNum;
                }

                if (getBookDownloadNum(tempBook.bookId) <= 0) {
                    tempBook.downloadState = 0;
                }

                //这里直接下载，不用重新命名
//                reNameFile(savePath, savePath + Constant.append);
//                file = new File(savePath);
//                file.delete();

                Message msg = handler.obtainMessage();
                msg.what = 2;
                msg.arg1 = curInfo.voaId;
                handler.sendMessage(msg);
            }
        } catch (IOException e) {
            curInfo.downloadedState = -2;
            Log.e("download 1", e.toString());
            handler.sendEmptyMessage(0);
        }
    }

    public int getBookDownloadNum(int bookId) {
        int downloadNum = 0;
        for (DownloadInfo info : infoList) {
            int bookIndex = bookId / 1000;
            switch (ConfigManager.Instance().getBookType()) {
                case AMERICA:
                default:
                    if (info.voaId < 5000 && info.voaId / 1000 == bookIndex) {
                        if (info.downloadedState == -1 || info.downloadedState == -2) {
                            downloadNum++;
                        }
                    }
                    break;
                case YOUTH:
                    if (DownloadStateManager.instance().voaOp.findDataById(info.voaId).category == bookId){
                        if (info.downloadedState == -1 || info.downloadedState == -2) {
                            downloadNum++;
                        }
                    }
                    break;
                case ENGLISH:
                    int bookIndexB = bookId / 10000;
                    if (info.voaId > 5000 && info.voaId / 10000 == bookIndexB) {
                        if (info.downloadedState == -1 || info.downloadedState == -2) {
                            downloadNum++;
                        }
                    }
                    break;
            }

        }

        return downloadNum;
    }

    public boolean reNameFile(String oldFilePath, String newFilePath) {
        File source = new File(oldFilePath);
        File dest = new File(newFilePath);
        return source.renameTo(dest);
    }

    public void updateInfoList(DownloadInfo downloadInfo) {
        infoList.add(downloadInfo);

        // 恢复所有线程
        synchronized (bookList) {
            bookList.notifyAll();
        }
    }

    public void updateInfoList(List<DownloadInfo> downloadInfoList) {
        for (DownloadInfo downloadInfo : downloadInfoList) {
            infoList.add(downloadInfo);
        }

        // 恢复所有线程
        synchronized (bookList) {
            bookList.notifyAll();
        }
    }

    public void updateInfoList() {
        // 恢复所有线程
        synchronized (bookList) {
            bookList.notifyAll();
        }
    }

}
