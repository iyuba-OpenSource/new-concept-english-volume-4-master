package com.iyuba.conceptEnglish.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iyuba.conceptEnglish.R;



public class DownloadDialog extends Dialog {

    CallBack callBack;


    public DownloadDialog(@NonNull Context context) {
        super(context);
    }

    public DownloadDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId );
    }

    protected DownloadDialog(@NonNull Context context, boolean cancelable, @Nullable DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    private ProgressBar mProgress;
    private TextView progressTv;
    private TextView cancel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        setContentView(R.layout.dialog_downloading);

        //设置宽高
        setDialogStyle();

        mProgress = findViewById(R.id.progress);
        progressTv = findViewById(R.id.progresstv);
        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(v -> callBack.onCancel());
    }

    public void setProgress(int progress){
        mProgress.setProgress(progress);
        progressTv.setText("正在下载资源文件,进度"+progress+"%");
    }

    private void setDialogStyle() {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = (int)getContext().getResources().getDisplayMetrics().widthPixels-100; // 宽度
//        params.height = (int)getContext().getResources().getDisplayMetrics().heightPixels/2; // 高度
        //lp.width = 650;
        getWindow().setAttributes(params);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public void setCallback(CallBack callback) {
        this.callBack = callback;
    }

    @Override
    public void show() {
        super.show();
        setProgress(0);
    }

    public interface  CallBack{
        void onCancel();
    }
}
