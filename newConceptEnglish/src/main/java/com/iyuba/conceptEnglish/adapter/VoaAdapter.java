package com.iyuba.conceptEnglish.adapter;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;
import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.lil.concept_other.download.FileDownloadBean;
import com.iyuba.conceptEnglish.lil.concept_other.download.FileDownloadEvent;
import com.iyuba.conceptEnglish.lil.concept_other.download.FileDownloadManager;
import com.iyuba.conceptEnglish.lil.concept_other.download.FilePathUtil;
import com.iyuba.conceptEnglish.lil.concept_other.util.HelpUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.StrLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.ConceptBookChooseManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.ConceptDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.concept.LocalMarkEntity_conceptDownload;
import com.iyuba.conceptEnglish.manager.DownloadStateManager;
import com.iyuba.conceptEnglish.sqlite.mode.DownloadInfo;
import com.iyuba.conceptEnglish.sqlite.mode.Voa;
import com.iyuba.conceptEnglish.sqlite.op.DownloadInfoOp;
import com.iyuba.conceptEnglish.sqlite.op.VoaOp;
import com.iyuba.conceptEnglish.study.StudyNewActivity;
import com.iyuba.conceptEnglish.util.CommonUtils;
import com.iyuba.conceptEnglish.util.FileDownloader;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.activity.login.LoginUtil;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.common.widget.RoundProgressBar;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.util.LibGlide3Util;
import com.iyuba.core.me.activity.NewVipCenterActivity;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 文章列表适配器
 */
public class VoaAdapter extends BaseAdapter {
    private FileDownloader fileDownloader;
    private List<DownloadInfo> infoList;
    private Voa voa;
    private Context mContext;
    private List<Voa> mList = new ArrayList<Voa>();
    public ViewHolder currViewHolder;
    public boolean modeDelete = false;
    private ViewHolder viewHolder;
    private VoaOp voaOp;
    private DownloadStateManager manager;
    private DownloadInfoOp downloadInfoOp;
    private Handler handler;

    public VoaAdapter(Context context, List<Voa> list) {
        fileDownloader = FileDownloader.instance();
        manager = DownloadStateManager.instance();
        downloadInfoOp = manager.downloadInfoOp;
        infoList = manager.downloadList;
        mContext = context;
        mList = list;
        this.handler = manager.handler;
        init();
    }

    public VoaAdapter(Context context) {
        fileDownloader = FileDownloader.instance();
        mContext = context;
        init();
    }

    //刷新数据
    public void refreshList(List<Voa> refreshList){
        this.mList.clear();
        this.mList.addAll(refreshList);
        notifyDataSetChanged();
    }

    public void addList(List<Voa> voasTemps) {
        mList.addAll(voasTemps);
    }

    private void init() {
        voaOp = new VoaOp(mContext);
    }

    public void clearList() {
        mList.clear();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Voa getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Voa curVoa = mList.get(position);
        final DownloadInfo info = getDownloadInfo(curVoa.voaId);

        final DownloadInfo infoBritish = getDownloadInfo(curVoa.voaId * 10);

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.listitem_voa, null);
            viewHolder = new ViewHolder();
            viewHolder.deleteBox = (ImageView) convertView
                    .findViewById(R.id.checkBox_isDelete);
//            viewHolder.voa = (TextView) convertView
//                    .findViewById(R.id.voa);
//            viewHolder.voaN = (TextView) convertView
//                    .findViewById(R.id.voaN);
            viewHolder.voaPic = convertView.findViewById(R.id.voa_pic);
            viewHolder.voaIndex = convertView.findViewById(R.id.voa_index);

            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.titleCn = (TextView) convertView
                    .findViewById(R.id.titleCn);
            viewHolder.lessonType = convertView.findViewById(R.id.lessonType);

            viewHolder.downloadShowView = convertView.findViewById(R.id.download);
            viewHolder.downloadShowView.setVisibility(View.GONE);
            viewHolder.downloadLayout = convertView
                    .findViewById(R.id.download_layout);
            viewHolder.downloadedImage = convertView
                    .findViewById(R.id.image_downloaded);

            // 下载的滚动条，实际是不动的
            viewHolder.mCircleProgressBar = (RoundProgressBar) convertView.findViewById(R.id.roundBar1);

            //根据设置处理类型显示(奇怪了，这里的标志显示是错误的，但是数据是正确的)
//            if (showLessonTypeTag){
//                viewHolder.lessonType.setVisibility(View.VISIBLE);
//                viewHolder.lessonType.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
//
//                switch (curVoa.lessonType){
//                    case TypeLibrary.BookType.conceptFourUS:
//                        viewHolder.lessonType.setText("美音");
//                        break;
//                    case TypeLibrary.BookType.conceptFourUK:
//                        viewHolder.lessonType.setText("英音");
//                        break;
//                    case TypeLibrary.BookType.conceptJunior:
//                        viewHolder.lessonType.setText("青少版");
//                        break;
//                    default:
//                        viewHolder.lessonType.setText("美音");
//                        break;
//                }
//            }else {
//                viewHolder.lessonType.setVisibility(View.GONE);
//            }

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (modeDelete) {
            viewHolder.deleteBox.setVisibility(View.VISIBLE);
        } else {
            viewHolder.deleteBox.setVisibility(View.GONE);
        }

        if (mList.get(position).isDelete) {
            viewHolder.deleteBox.setImageResource(R.drawable.check_box_checked);
        } else {
            viewHolder.deleteBox.setImageResource(R.drawable.check_box);
        }

        final int voaId = curVoa.voaId;
        int index = curVoa.voaId % 1000;
        //原版数据
//        viewHolder.voa.setText("Lesson");
//        viewHolder.voaN.setText(index + "");
        //新版数据
        if (TextUtils.isEmpty(curVoa.pic)){
            viewHolder.voaPic.setVisibility(View.GONE);
            viewHolder.voaIndex.setVisibility(View.VISIBLE);

            if (curVoa.category>10) {
                viewHolder.voaIndex.setText(CommonUtils.getUnitFromTitle(curVoa.title) + "");
            } else {
                viewHolder.voaIndex.setText(index + "");
            }
        }else {
            viewHolder.voaPic.setVisibility(View.VISIBLE);
            viewHolder.voaIndex.setVisibility(View.GONE);

            LibGlide3Util.loadImg(mContext,curVoa.pic,R.drawable.shape_btn_bg, viewHolder.voaPic);
        }


        viewHolder.title.setText(HelpUtil.transTitleStyle(curVoa.title));
        viewHolder.titleCn.setText(curVoa.titleCn);

        //根据文件和数据库判断
        Voa tempVoa = mList.get(position);
        //获取路径
        File localFile = new File(FilePathUtil.getHomeAudioPath(tempVoa.voaId,tempVoa.lessonType));
        //获取当前数据在本地数据库存储的状态
        LocalMarkEntity_conceptDownload downloadData = ConceptDataManager.getLocalMarkDownloadSingleData(tempVoa.voaId,tempVoa.lessonType,UserInfoManager.getInstance().getUserId());
        //获取当前的状态
        int status = 0;
        if (downloadData!=null&&!TextUtils.isEmpty(downloadData.isDownload)){
            status = Integer.parseInt(downloadData.isDownload);
        }
        if (status==1){
            if (localFile.exists()){
                viewHolder.downloadLayout.setVisibility(View.GONE);
                viewHolder.downloadedImage.setVisibility(View.VISIBLE);
            }else {
                viewHolder.downloadLayout.setVisibility(View.VISIBLE);
                viewHolder.downloadedImage.setVisibility(View.GONE);
            }
        }else if (status==0){
            viewHolder.downloadLayout.setVisibility(View.VISIBLE);
            viewHolder.downloadedImage.setVisibility(View.GONE);
        }else {
            viewHolder.downloadLayout.setVisibility(View.VISIBLE);
            viewHolder.downloadedImage.setVisibility(View.GONE);
        }

        //下载设置
        switch (status) {
            case -2:
                viewHolder.mCircleProgressBar.setBackgroundResource(R.drawable.wait_download);
                break;
            case 0:
                viewHolder.mCircleProgressBar.setBackgroundResource(R.drawable.pause_download);
                break;
            case -1:
                viewHolder.mCircleProgressBar.setBackgroundResource(R.drawable.download);
                break;
        }

        // 是否阅读
        /*if (curVoa.isRead != null && curVoa.isRead.equals("0")) {
//            viewHolder.voa.setTextColor(Constant.normalColor);
//            viewHolder.voaN.setTextColor(Constant.unreadCnColor);
            viewHolder.title.setTextColor(Constant.normalColor);
            viewHolder.titleCn.setTextColor(Constant.unreadCnColor);
        } else if (curVoa.isRead != null && curVoa.isRead.equals("1")) {
//            viewHolder.voa.setTextColor(Constant.readColor);
//            viewHolder.voaN.setTextColor(Constant.readColor);
            viewHolder.title.setTextColor(Constant.readColor);
            viewHolder.titleCn.setTextColor(Constant.readColor);
        }*/

        viewHolder.downloadLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                //使用前必须开启-存储权限--
                /*if (Build.VERSION.SDK_INT >= 23) { //判断是否为android6.0系统版本，如果是，需要动态添加权限
                    if (!permissions.dispatcher.PermissionUtils.hasSelfPermissions(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        CustomToast.showToast(mContext, "存储权限开通才可以正常下载，请到系统设置中开启", 3000);
                        return;
                    }
                }
                voa = curVoa;

                if (!isOpenNetwork()) {
                    // 请检查网络
                    CustomToast.showToast(mContext, R.string.category_check_network, 1000);
                } else {
                    int downloadNum = getDownloadNum(voa.voaId / 1000 * 1000) + getDownloadNum(voa.voaId * 10 / 10000 * 10000);

                    if (UserInfoManager.getInstance().isVip() || downloadNum < 10) {

                        boolean isAmerican = curVoa.lessonType.equals(TypeLibrary.BookType.conceptFourUS);

                        if (isAmerican) {

                            File file = new File(Constant.videoAddr + voa.voaId + "" + Constant.append);
                            if (file.exists()) {
                                file.delete();
                            }

                            //美音
                            if (info == null) {
                                download();

                            } else if (info.downloadedState == 0) {

                                download(info);


                            } else if (info.downloadedState == -1) {

                                download(info);


                            } else if (info.downloadedState == 1 || info.downloadedState == -2) {
                                info.downloadedState = -1;
                                notifyDataSetChanged();
                            }
                        } else {
                            //英音

                            if (*//*(/*voaId < 2000 && voaId % 2 == 0*//* false) {
                                ToastUtil.showToast(mContext, "第一册偶数课暂时只有美音模式，请切换美音进行下载");
                                return;
                            }

                            File file = new File(Constant.videoAddr + voa.voaId + "_B" + Constant.append);
                            if (file.exists()) {
                                file.delete();
                            }
                            if (infoBritish == null) {
                                downloadBritish();
                            } else if (infoBritish.downloadedState == 0) {

                                downloadBritish(infoBritish);

                            } else if (infoBritish.downloadedState == -1) {
                                downloadBritish(infoBritish);

                            } else if (infoBritish.downloadedState == 1 || infoBritish.downloadedState == -2) {
                                infoBritish.downloadedState = -1;
                                notifyDataSetChanged();
                            }
                        }


                    } else {
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
                                        if (UserInfoManager.getInstance().isLogin()) {
                                            NewVipCenterActivity.start(mContext,NewVipCenterActivity.VIP_APP);
                                        } else {
                                            LoginUtil.startToLogin(mContext);
                                        }
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
                                    }
                                });
                        alert.show();
                    }
                }*/


                if (!UserInfoManager.getInstance().isLogin()){
                    LoginUtil.startToLogin(mContext);
                    return;
                }

                List<LocalMarkEntity_conceptDownload> downloadList = ConceptDataManager.getLocalMarkDownloadAndDownloadingData(UserInfoManager.getInstance().getUserId());
                Log.d("下载和待下载的数量", downloadList.size()+"");
                if (downloadList.size()>=10&&!UserInfoManager.getInstance().isVip()){
                    new AlertDialog.Builder(mContext)
                            .setTitle("下载限制")
                            .setMessage("非会员仅能下载10篇课程，开通会员后下载无限制，是否开通会员?")
                            .setPositiveButton("开通会员", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    NewVipCenterActivity.start(mContext,NewVipCenterActivity.VIP_APP);
                                }
                            }).setNegativeButton("考虑一下",null)
                            .setCancelable(false)
                            .create().show();
                    return;
                }

                //检查下载的数量
                Voa tempVoa = mList.get(position);
                LocalMarkEntity_conceptDownload downloadData = ConceptDataManager.getLocalMarkDownloadSingleData(tempVoa.voaId,tempVoa.lessonType,UserInfoManager.getInstance().getUserId());
                int downloadStatus = 0;
                if (downloadData!=null&&!TextUtils.isEmpty(downloadData.isDownload)){
                    downloadStatus = Integer.parseInt(downloadData.isDownload);
                }

                if (downloadStatus==1){
                    Intent intent = new Intent();
                    intent.setClass(mContext, StudyNewActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    intent.putExtra(StrLibrary.pageType, TypeLibrary.StudyPageType.read);
                    intent.putExtra(StrLibrary.position,tempVoa.position);
                    return;
                }

                if (downloadStatus==-1){
                    ToastUtil.showToast(mContext,"正在下载中");
                    return;
                }

                XXPermissions.with(mContext)
                        .permission(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .request(new OnPermissionCallback() {
                            @Override
                            public void onGranted(List<String> permissions, boolean all) {
                                if (all){
                                    //先把相应的数据设置为待下载，然后外面查询待下载和下载的数量，超出则显示不能下载
                                    ConceptDataManager.updateLocalMarkDownloadStatus(tempVoa.voaId,tempVoa.lessonType,UserInfoManager.getInstance().getUserId(), TypeLibrary.FileDownloadStateType.file_isDownloading,tempVoa.position);

                                    //下载文件
                                    String fileUrl = getRemoteSoundPath(tempVoa);
                                    String filePath = FilePathUtil.getHomeAudioPath(tempVoa.voaId,tempVoa.lessonType);
                                    FileDownloadBean downloadBean = new FileDownloadBean(fileUrl,filePath, ConceptBookChooseManager.getInstance().getBookType(), tempVoa.voaId,position);
                                    FileDownloadManager.getInstance().downloadFile(downloadBean);
                                    //设置当前数据为下载状态
                                    EventBus.getDefault().post(new FileDownloadEvent(FileDownloadEvent.home,ConceptBookChooseManager.getInstance().getBookType(),tempVoa.voaId,position));
                                }else {
                                    ToastUtil.showToast(mContext,"请授权存储权限后使用");
                                }
                            }
                        });
            }
        });

        currViewHolder = viewHolder;
        return convertView;
    }

    private void setDownloadState(int position) {


        /*File localFile;

        if (isAmerican) {
            localFile = new File(Constant.videoAddr + curVoa.voaId + Constant.append);
        } else {
            localFile = new File(Constant.videoAddr + curVoa.voaId + "_B" + Constant.append);
        }
        // 如果本地存在
        if (downloadInfo != null && localFile.exists()) {
            viewHolder.downloadLayout.setVisibility(View.GONE);
            viewHolder.downloadedImage.setVisibility(View.VISIBLE);
        } else {
            viewHolder.downloadedImage.setVisibility(View.GONE);
            viewHolder.downloadLayout.setVisibility(View.VISIBLE);
        }*/

        /*******************新的处理*******************/
        //根据文件和数据库判断
        Voa tempVoa = mList.get(position);
        //获取路径
        File localFile = new File(FilePathUtil.getHomeAudioPath(tempVoa.voaId,tempVoa.lessonType));
        //获取当前数据在本地数据库存储的状态
        LocalMarkEntity_conceptDownload downloadData = ConceptDataManager.getLocalMarkDownloadSingleData(tempVoa.voaId,tempVoa.lessonType,UserInfoManager.getInstance().getUserId());
        //获取当前的状态
        int status = 0;
        if (downloadData!=null&&!TextUtils.isEmpty(downloadData.isDownload)){
            status = Integer.parseInt(downloadData.isDownload);
        }
        if (status==1){
            if (localFile.exists()){
                viewHolder.downloadLayout.setVisibility(View.GONE);
                viewHolder.downloadedImage.setVisibility(View.VISIBLE);
            }else {
                viewHolder.downloadLayout.setVisibility(View.VISIBLE);
                viewHolder.downloadedImage.setVisibility(View.GONE);
            }
        }else if (status==0){
            viewHolder.downloadLayout.setVisibility(View.VISIBLE);
            viewHolder.downloadedImage.setVisibility(View.GONE);
        }else {
            viewHolder.downloadLayout.setVisibility(View.VISIBLE);
            viewHolder.downloadedImage.setVisibility(View.GONE);
        }

        switch (status) {
            case -2:
                viewHolder.mCircleProgressBar.setBackgroundResource(R.drawable.wait_download);
                break;
            case 0:
                viewHolder.mCircleProgressBar.setBackgroundResource(R.drawable.pause_download);
                break;
            case -1:
                viewHolder.mCircleProgressBar.setBackgroundResource(R.drawable.download);
                break;
        }
    }

    public int getDownloadNum(int bookId) {
        int bookIndex = bookId / 1000;
        int downloadNum = 0;

        for (DownloadInfo info : infoList) {
            if (info.voaId / 1000 == bookIndex && info.downloadedState != 0) {
                downloadNum++;
            }
        }

        return downloadNum;
    }

    public DownloadInfo getDownloadInfo(int voaId) {
        if (infoList != null) {
            for (DownloadInfo tempInfo : infoList) {
                if (tempInfo.voaId == voaId) {
                    return tempInfo;
                }
            }
        }

        return null;
    }

    private boolean isOpenNetwork() {
        ConnectivityManager connManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connManager.getActiveNetworkInfo() != null) {
            return connManager.getActiveNetworkInfo().isAvailable();
        }

        return false;
    }

    public void downloadBritish() {
        handler.sendEmptyMessage(1);

        voa.isDownload = "1";
        //保存在其他数据库中
        ConceptDataManager.updateLocalMarkDownloadStatus(voa.voaId,voa.lessonType,UserInfoManager.getInstance().getUserId(), voa.isDownload,voa.position);
        voaOp.insertDataToDownload(voa.voaId * 10);

        DownloadInfo downloadInfo = new DownloadInfo(voa.voaId * 10);
        if (fileDownloader.getDownloadState() == 0) {
            downloadInfo.downloadedState = 1;
        } else {
            downloadInfo.downloadedState = -2;
        }

        notifyDataSetChanged();

        downloadInfoOp.insert(downloadInfo);
        fileDownloader.updateInfoList(downloadInfo);
    }

    public void download() {
        handler.sendEmptyMessage(1);

        voa.isDownload = "1";
        //保存在其他数据库中
        ConceptDataManager.updateLocalMarkDownloadStatus(voa.voaId,voa.lessonType,UserInfoManager.getInstance().getUserId(), voa.isDownload,voa.position);
        voaOp.insertDataToDownload(voa.voaId);

        DownloadInfo downloadInfo = new DownloadInfo(voa.voaId);
        if (fileDownloader.getDownloadState() == 0) {
            downloadInfo.downloadedState = 1;
        } else {
            downloadInfo.downloadedState = -2;
        }
        notifyDataSetChanged();

        downloadInfoOp.insert(downloadInfo);
        fileDownloader.updateInfoList(downloadInfo);
    }

    public void download(DownloadInfo downloadInfo) {
        voa.isDownload = "1";
        //保存在其他数据库中
        ConceptDataManager.updateLocalMarkDownloadStatus(voa.voaId,voa.lessonType,UserInfoManager.getInstance().getUserId(), voa.isDownload,voa.position);
        voaOp.insertDataToDownload(voa.voaId);

        if (fileDownloader.getDownloadState() == 0) {
            downloadInfo.downloadedState = 1;
        } else {
            downloadInfo.downloadedState = -2;
        }
        notifyDataSetChanged();

        downloadInfoOp.insert(downloadInfo);
        fileDownloader.updateInfoList(downloadInfo);
    }

    public void downloadBritish(DownloadInfo downloadInfo) {
        voa.isDownload = "1";
        //保存在其他数据库中
        ConceptDataManager.updateLocalMarkDownloadStatus(voa.voaId,voa.lessonType,UserInfoManager.getInstance().getUserId(), voa.isDownload,voa.position);
        voaOp.insertDataToDownload(voa.voaId * 10);

        if (fileDownloader.getDownloadState() == 0) {
            downloadInfo.downloadedState = 1;
        } else {
            downloadInfo.downloadedState = -2;
        }
        notifyDataSetChanged();

        downloadInfoOp.insert(downloadInfo);
        fileDownloader.updateInfoList(downloadInfo);
    }

    public DownloadInfo createDownloadInfo() {
        DownloadInfo downloadInfo = new DownloadInfo(voa.voaId);
        downloadInfoOp.insert(downloadInfo);

        return downloadInfo;
    }

    public class ViewHolder {
        View downloadShowView;
        ImageView deleteBox;
        View downloadLayout;

        //旧版样式
//        public TextView voa;
//        public TextView voaN;
        //新版样式
        public ImageView voaPic;
        public TextView voaIndex;

        public TextView title;
        public TextView titleCn;
        public TextView lessonType;

        RoundProgressBar mCircleProgressBar;// 进度环
        RoundProgressBar downloadedImage;// 下载完成的标示
    }

    public List<Voa> getmList() {
        return mList;
    }

    public void setmList(List<Voa> mList) {
        this.mList = mList;
    }


    /***************************************增加的相关功能*********************************/
    //获取当前章节的音频本地路径
    private String getLocalSoundPath(Voa curVoa) {
        String localPath = "";

        if (!XXPermissions.isGranted(mContext,Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            return localPath;
        }

        //这里不获取当前的数据，而是获取数据中的类型
        /*switch (curVoa.lessonType) {
            case TypeLibrary.BookType.conceptFourUS:
            case TypeLibrary.BookType.conceptJunior:
            default:
                // 美音原文音频的存放路径
                String pathString = Constant.videoAddr + curVoa.voaId + Constant.append;
                File fileTemp = new File(pathString);
                if (fileTemp.exists()) {
                    localPath =  pathString;
                }
                break;
            case TypeLibrary.BookType.conceptFourUK:
                // 英音原文音频的存放路径
                String pathStringEng = Constant.videoAddr + curVoa.voaId + "_B" + Constant.append;
                File fileTempEng = new File(pathStringEng);
                if (fileTempEng.exists()) {
                    localPath = pathStringEng;
                }
                break;
        }*/
        //更换地址
        String pathString = FilePathUtil.getHomeAudioPath(curVoa.voaId,curVoa.lessonType);
        File file = new File(pathString);
        if (file.exists()){
            localPath = pathString;
        }

        return localPath;
    }

    //获取当前章节的音频网络路径
    private String getRemoteSoundPath(Voa tempVoa){
        String soundUrl = null;
        //这里针对会员和非会员不要修改，测试也不要修改
        if (UserInfoManager.getInstance().isVip()){
            soundUrl="http://staticvip2." + Constant.IYUBA_CN + "newconcept/";
        }else {
            soundUrl=Constant.sound;
        }

//        switch (ConfigManager.Instance().getBookType()) {
        switch (tempVoa.lessonType) {
            case TypeLibrary.BookType.conceptFourUS:
            default:
                //美音
                soundUrl = soundUrl
                        + tempVoa.voaId / 1000
                        + "_"
                        + tempVoa.voaId % 1000
                        + Constant.append;
                break;
            case TypeLibrary.BookType.conceptFourUK: //英音
                soundUrl = soundUrl
                        + "british/"
                        + tempVoa.voaId / 1000
                        + "/"
                        + tempVoa.voaId / 1000
                        + "_"
                        + tempVoa.voaId % 1000
                        + Constant.append;
                break;
            case TypeLibrary.BookType.conceptJunior:
                soundUrl = "http://"+Constant.staticStr+Constant.IYUBA_CN+"sounds/voa/sentence/202005/"
                        + tempVoa.voaId
                        + "/"
                        + tempVoa.voaId
                        + Constant.append;
                break;
        }

        return soundUrl;
    }
}