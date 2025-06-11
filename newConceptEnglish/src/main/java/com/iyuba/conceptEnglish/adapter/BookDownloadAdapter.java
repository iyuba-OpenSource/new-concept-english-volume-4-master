package com.iyuba.conceptEnglish.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.manager.DownloadStateManager;
import com.iyuba.conceptEnglish.sqlite.mode.Book;
import com.iyuba.conceptEnglish.sqlite.mode.DownloadInfo;
import com.iyuba.conceptEnglish.sqlite.mode.Voa;
import com.iyuba.conceptEnglish.sqlite.op.BookOp;
import com.iyuba.conceptEnglish.sqlite.op.DownloadInfoOp;
import com.iyuba.conceptEnglish.sqlite.op.VoaOp;
import com.iyuba.conceptEnglish.util.FileDownloader;
import com.iyuba.configation.ConfigManager;
import com.iyuba.core.common.widget.RoundProgressBar;
import com.iyuba.core.me.activity.NewVipCenterActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 文章列表适配器
 */
public class BookDownloadAdapter extends BaseAdapter {
    private Context mContext;

    public FileDownloader fileDownloader;
    public DownloadStateManager downloadStateManager;
    public List<DownloadInfo> infoList;
    public List<Book> bookList = new ArrayList<>();

    public ViewHolder currViewHolder;
    private ViewHolder viewHolder;
    private VoaOp voaOp;
    private BookOp bookOp;
    private DownloadInfoOp downloadInfoOp;
    private Callback mCallback;

    public BookDownloadAdapter() {

    }

    public BookDownloadAdapter(Context context, Callback callback) {
        mContext = context;
        mCallback = callback;
        downloadStateManager = DownloadStateManager.instance();
        fileDownloader = FileDownloader.instance();

        bookOp = downloadStateManager.bookOp;
        downloadInfoOp = downloadStateManager.downloadInfoOp;
        infoList = downloadStateManager.downloadList;
        voaOp = new VoaOp(mContext);

        freshDownloadData();
    }

    public void addList(List<Book> booksTemps) {
        bookList.addAll(booksTemps);
    }

    @Override
    public int getCount() {
        return bookList.size();
    }

    @Override
    public Book getItem(int position) {
        return bookList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Book curBook = bookList.get(position);

        Log.e("更新课文=====", curBook.downloadNum + "");
        //生成view的集成 对象 viewholder start
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.griditem_download, null);

            viewHolder = new ViewHolder();
            viewHolder.downloadLayout = convertView
                    .findViewById(R.id.download_layout);
            viewHolder.downloadedImage = (ImageView) convertView
                    .findViewById(R.id.image_downloaded);
            viewHolder.mCircleProgressBar = (RoundProgressBar) convertView
                    .findViewById(R.id.roundBar1);
            viewHolder.downloadNum = (TextView) convertView
                    .findViewById(R.id.download_num);
            viewHolder.bookName = (TextView) convertView
                    .findViewById(R.id.book_name);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //生成view的集成 对象 viewholder end


        viewHolder.downloadNum.setText(String.format("%d/%d", curBook.downloadNum, curBook.totalNum));
        viewHolder.bookName.setText(curBook.bookName);

        if (curBook.downloadNum >= curBook.totalNum) { // 下载结束
            curBook.downloadState = 2;
            bookOp.updateDownloadState(curBook.bookId, 2);
        }

        if (curBook.downloadState == 2) {
            viewHolder.downloadLayout.setVisibility(View.GONE);
            viewHolder.downloadedImage.setVisibility(View.VISIBLE);
        } else {
            viewHolder.downloadedImage.setVisibility(View.GONE);
            viewHolder.downloadLayout.setVisibility(View.VISIBLE);

            // 如果下载列表中存在
            if (curBook.downloadState == 1) {
                DownloadInfo curInfo = fileDownloader.curInfo;
                if (((curInfo != null) && (curBook.bookId / getDivisor()) == (curInfo.voaId / getDivisor()))
                        || curBook.bookId < 1000) {
                    viewHolder.mCircleProgressBar.setCricleProgressColor(0xff00AEFF);
                    viewHolder.mCircleProgressBar.setMax(100);
                    viewHolder.mCircleProgressBar.setProgress(curInfo.downloadPer);
                }
            } else {
                viewHolder.mCircleProgressBar.setCricleProgressColor(0xff00AEFF);
                viewHolder.mCircleProgressBar.setMax(1);
                viewHolder.mCircleProgressBar.setProgress(0);
            }

            switch (curBook.downloadState) {
                case -2:
                    viewHolder.mCircleProgressBar.setBackgroundResource(R.drawable.wait_download);
                    break;
                case -1:
                case 0:
                    viewHolder.mCircleProgressBar.setBackgroundResource(R.drawable.pause_download);
                    break;
                case 1:
                    viewHolder.mCircleProgressBar.setBackgroundResource(R.drawable.download);
                    break;
            }
        }

        viewHolder.downloadLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //检测权限
                mCallback.requestPermission(curBook);

            }
        });

        currViewHolder = viewHolder;
        return convertView;
    }

    /**
     * 实际的下载方法。
     *
     * @param curBook
     */
    public void realDownload(Book curBook) {
        final int bookId;
        bookId = curBook.bookId;

        if (curBook.downloadState == 0) {
            bookOp.updateDownloadState(curBook.bookId, -2);
            if ((ConfigManager.Instance().loadInt("isvip") >= 1)) {
                if (fileDownloader.getDownloadState() == 0) {
                    curBook.downloadState = 1;
                } else {
                    curBook.downloadState = -2;
                }
                notifyDataSetChanged();

                Message msg = handler.obtainMessage();
                msg.what = 1;
                msg.arg1 = bookId;
                handler.sendMessage(msg);
            } else {
                final int downloadNum = getDownloadNum(bookId);
                Log.e("bookId", bookId + "");
                Log.e("downloadNum", downloadNum + "");

                AlertDialog alert = new AlertDialog.Builder(
                        mContext).create();
                alert.setTitle(mContext.getResources().getString(
                        R.string.alert));
                alert.setMessage(mContext.getResources().getString(
                        R.string.nladapter_notvip));
                alert.setIcon(android.R.drawable.ic_dialog_alert);
                alert.setButton(
                        AlertDialog.BUTTON_POSITIVE,
                        mContext.getResources().getString(
                                R.string.alert_btn_buy),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(
                                    DialogInterface dialog,
                                    int which) {
                                NewVipCenterActivity.start(mContext,NewVipCenterActivity.VIP_APP);
                            }
                        });
                alert.setButton(
                        AlertDialog.BUTTON_NEGATIVE,
                        mContext.getResources().getString(
                                R.string.alert_btn_cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dialog,
                                    int which) {
                                if (downloadNum < 10) {
                                    Log.e("downloadNum", downloadNum + "");

                                    if (fileDownloader.getDownloadState() == 0) {
                                        curBook.downloadState = 1;
                                    } else {
                                        curBook.downloadState = -2;
                                    }
                                    notifyDataSetChanged();

                                    Message msg = handler.obtainMessage();
                                    msg.what = 2;
                                    msg.arg1 = bookId;
                                    handler.sendMessage(msg);
                                }
                            }
                        });
                alert.show();
            }
        } else if (curBook.downloadState == -1) {

            if (fileDownloader.getDownloadState() == 0) {
                curBook.downloadState = 1;
            } else {
                curBook.downloadState = -2;
            }
            Log.e("downloadState", curBook.downloadState + "");
            notifyDataSetChanged();

            Message msg = handler.obtainMessage();
            msg.what = 3;
            msg.arg1 = bookId;
            handler.sendMessage(msg);

        } else if (curBook.downloadState == 1
                || curBook.downloadState == -2) {
            curBook.downloadState = -1;
            notifyDataSetChanged();

            updateStateToStop(bookId);
            bookOp.updateDownloadState(curBook.bookId, -1);
        }
    }

    public void updateStateToWait(int bookId) {
        int bookIndex = bookId / getDivisor();
        for (DownloadInfo downloadInfo : infoList) {

            if ((downloadInfo.voaId / getDivisor() == bookIndex)
            || bookId<1000) {
                switch (ConfigManager.Instance().getBookType()) {
                    case AMERICA:
                    default:
                        if (downloadInfo.voaId > 999
                                && downloadInfo.voaId < 5000) {
                            if (downloadInfo.downloadedState == 0 || downloadInfo.downloadedState == -1) {
                                downloadInfo.downloadedState = -2;
                            }
                        }
                        break;
                    case ENGLISH:
                        if (downloadInfo.voaId > 5000) {
                            if (downloadInfo.downloadedState == 0 || downloadInfo.downloadedState == -1) {
                                downloadInfo.downloadedState = -2;
                            }
                        }
                        break;
                    case YOUTH:
                        if (voaOp.findDataById(downloadInfo.voaId).category == bookId) {
                            if (downloadInfo.downloadedState == 0 || downloadInfo.downloadedState == -1) {
                                downloadInfo.downloadedState = -2;
                            }
                        }
                        break;
                }

            }
        }
    }

    public void updateStateToStop(int bookId) {
        int bookIndex = bookId / getDivisor();
        for (DownloadInfo downloadInfo : infoList) {
            if ((downloadInfo.voaId / getDivisor() == bookIndex)
            || bookId < 1000) {

                switch (ConfigManager.Instance().getBookType()) {
                    case AMERICA:
                    default:
                        if (downloadInfo.voaId > 999
                                && downloadInfo.voaId < 5000) {
                            if (downloadInfo.downloadedState == 1
                                    || downloadInfo.downloadedState == -2) {
                                downloadInfo.downloadedState = -1;
                            }
                        }
                        break;
                    case ENGLISH:
                        if (downloadInfo.voaId > 5000) {
                            if (downloadInfo.downloadedState == 1
                                    || downloadInfo.downloadedState == -2) {
                                downloadInfo.downloadedState = -1;
                            }
                        }
                        break;
                    case YOUTH:
                        if (voaOp.findDataById(downloadInfo.voaId).category == bookId) {
                            if (downloadInfo.downloadedState == 1
                                    || downloadInfo.downloadedState == -2) {
                                downloadInfo.downloadedState = -1;
                            }
                        }
                        break;
                }

            }
        }
    }

    public DownloadInfo getDownloadInfo(int voaId) {
        for (DownloadInfo tempInfo : infoList) {
            if (tempInfo.voaId == voaId) {
                return tempInfo;
            }
        }

        return null;
    }

    private void downloadForVip(int bookId) {
        List<Voa> voaList = null;
        switch (ConfigManager.Instance().getBookType()) {
            case AMERICA:
            default:
                voaList = voaOp.findDataByBook(bookId / 1000);
                break;
            case ENGLISH:
                voaList = voaOp.findDataByBook(bookId / 10000);
                break;
            case YOUTH:
                voaList = voaOp.findDataByCategory(bookId);
                break;
        }

        List<DownloadInfo> infoList = new ArrayList<DownloadInfo>();
        DownloadInfo info = null;

        for (Voa voa : voaList) {
            voa.isDownload = "1";
            int currVoaId = 0;


            switch (ConfigManager.Instance().getBookType()) {
                case AMERICA:
                case YOUTH:
                default:
                    currVoaId = voa.voaId;
                    break;
                case ENGLISH:
                    currVoaId = voa.voaId * 10;
                    break;
            }

            voaOp.insertDataToDownload(currVoaId);
            info = getDownloadInfo(currVoaId);
            if (info == null) {
                info = new DownloadInfo(currVoaId);
                /*if (!isAmeican() && voa.voaId / 1000 == 1 && voa.voaId % 2 == 0) { //英音 第一册 偶数课
                } else {
                    infoList.add(info);
                }*/
                infoList.add(info);
            }
        }
        downloadInfoOp.insert(infoList);
        fileDownloader.updateInfoList(infoList);
    }

    private void download(int bookId, int downloadNum) {
        List<Voa> voaList = new ArrayList<>();
        switch (ConfigManager.Instance().getBookType()) {
            case AMERICA:
            default:
                voaList = voaOp.findDataByBook(bookId / 1000);
                break;
            case ENGLISH:
                voaList = voaOp.findDataByBook(bookId / 10000);
                break;
            case YOUTH:
                voaList = voaOp.findDataByCategory(bookId);
                break;
        }


        List<DownloadInfo> infoList = new ArrayList<DownloadInfo>();
        DownloadInfo info = null;

        switch (ConfigManager.Instance().getBookType()) {
            case AMERICA:
            case YOUTH:
            default:
                for (Voa voa : voaList) {
                    info = getDownloadInfo(voa.voaId);

                    if (info == null) {
                        voa.isDownload = "1";
                        voaOp.insertDataToCollection(voa.voaId);
                        info = new DownloadInfo(voa.voaId);
                        infoList.add(info);

                        downloadNum++;
                    } else if (info.downloadedState == 0) {
                        voa.isDownload = "1";
                        voaOp.insertDataToCollection(voa.voaId);
                        info.downloadedState = -2;

                        downloadNum++;
                    }

                    if (downloadNum >= 10) {
                        break;
                    }
                }
                break;
            case ENGLISH:
                for (Voa voa : voaList) {

                    info = getDownloadInfo(voa.voaId * 10);

                    if (info == null) {
                        voa.isDownload = "1";
                        voaOp.insertDataToCollection(voa.voaId * 10);
                        info = new DownloadInfo(voa.voaId * 10);
                        infoList.add(info);

                        downloadNum++;
                    } else if (info.downloadedState == 0) {
                        voa.isDownload = "1";
                        voaOp.insertDataToCollection(voa.voaId * 10);
                        info.downloadedState = -2;

                        downloadNum++;
                    }

                    if (downloadNum >= 10) {
                        break;
                    }
                }
                break;
        }
        downloadInfoOp.insert(infoList);
        fileDownloader.updateInfoList(infoList);
    }

    public void restartDownload(int bookId) {
        int num;
        switch (ConfigManager.Instance().getBookType()) {
            case AMERICA:
            default:
                num = 1000;
                allFourVolumes(bookId, num);
                break;
            case ENGLISH:
                num = 10000;
                allFourVolumes(bookId, num);
                break;
            case YOUTH:
                youthEdition(bookId);
                break;
        }
    }

    private void allFourVolumes(int bookId, int num) {
        int bookIndex = bookId / num;
        for (DownloadInfo info : infoList) {
            Log.e("下载的状态", info.voaId + "==" + bookIndex + "==" + fileDownloader.curInfo.voaId);
            if (info.voaId / num == bookIndex) {
                if (info == fileDownloader.curInfo) {
                    info.downloadedState = 1;
                } else if (info.downloadedState == -1) {
                    info.downloadedState = -2;
                }
            }
        }

        fileDownloader.updateInfoList();
    }

    private void youthEdition(int bookId) {
        List<Voa> list = voaOp.findDataByCategory(bookId);
        if (list == null || list.size() < 1) {
            return;
        }
        int startVoaid = list.get(0).voaId;
        int endVoaid = list.get(list.size() - 1).voaId;
        for (DownloadInfo info : infoList) {
            if (info.voaId >= startVoaid && info.voaId <= endVoaid) {
                if (info == fileDownloader.curInfo) {
                    info.downloadedState = 1;
                } else if (info.downloadedState == -1) {
                    info.downloadedState = -2;
                }
            }
        }

        fileDownloader.updateInfoList();
    }

    public int getDownloadNum(int bookId) {


        int bookIndex = bookId / getDivisor();
        int downloadNum = 0;
        for (DownloadInfo info : infoList) {
            if ((info.voaId / getDivisor() == bookIndex && info.downloadedState != 0)
                    || info.voaId / 1000 == 321) {

                switch (ConfigManager.Instance().getBookType()) {
                    case AMERICA:
                    default:
                        if (info.voaId > 999
                                && info.voaId < 5000)
                            downloadNum++;
                        break;
                    case ENGLISH:
                        if (voaOp.findDataById(info.voaId).category == bookId) {
                            downloadNum++;
                        }
                        break;
                    case YOUTH:
                        if (info.voaId < 1000)
                            downloadNum++;
                        break;
                }
            }
        }

        return downloadNum;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            int bookId = 0;
            int downloadNum = 0;

            switch (msg.what) {
                case 0:
                    notifyDataSetChanged();
                    break;
                case 1:
                    //VIP下载
                    bookId = msg.arg1;
                    downloadForVip(bookId);
                    updateStateToWait(bookId);
                    break;
                case 2:
                    //非VIP下载
                    bookId = msg.arg1;
                    downloadNum = msg.arg2;
                    download(bookId, downloadNum);
                    updateStateToWait(bookId);
                    break;
                case 3:
                    bookId = msg.arg1;
                    restartDownload(bookId);
                    break;
                default:
                    break;
            }
        }
    };

    public class ViewHolder {
        View downloadLayout;
        RoundProgressBar mCircleProgressBar;// 进度环
        ImageView downloadedImage;// 下载完成的标示
        TextView downloadNum;
        TextView bookName;
    }

    /**
     * 刷新下载数据的状态
     */
    private void freshDownloadData() {
        bookList.clear();
        switch (ConfigManager.Instance().getBookType()) {
            case AMERICA:
            default:
                //美音音频下载
                for (int i = 0; i < downloadStateManager.bookList.size(); i++) {
                    if (downloadStateManager.bookList.get(i).bookId > 999
                            && downloadStateManager.bookList.get(i).bookId < 5000) {
                        bookList.add(downloadStateManager.bookList.get(i));

                    }
                }
                break;
            case ENGLISH:
                //英音音频下载
                for (int i = 0; i < downloadStateManager.bookList.size(); i++) {
                    if (downloadStateManager.bookList.get(i).bookId > 5000) {
                        bookList.add(downloadStateManager.bookList.get(i));
                    }
                }
                break;
            case YOUTH:
                //添加青少版
                for (int i = 0; i < downloadStateManager.bookList.size(); i++) {
                    if (downloadStateManager.bookList.get(i).bookId < 1000) {
                        bookList.add(downloadStateManager.bookList.get(i));
                    }
                }
                break;

        }
    }

    private int getDivisor() {
        switch (ConfigManager.Instance().getBookType()) {
            case AMERICA:
            default:
                return 1000;
            case YOUTH:
                return 1;
            case ENGLISH:
                return 10000;
        }
    }

    public interface Callback {
        void requestPermission(Book book);
    }
}
