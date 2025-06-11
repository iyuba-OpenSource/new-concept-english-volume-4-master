package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.download;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.iyuba.conceptEnglish.databinding.DialogDownloadFixBinding;
import com.iyuba.conceptEnglish.databinding.DialogLoadingBinding;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.listener.OnSimpleClickListener;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.ScreenUtil;

/**
 * @title: 加载弹窗
 * @date: 2023/4/26 18:29
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class DownloadFixDialog extends AlertDialog {

    private Context context;
    private DialogDownloadFixBinding binding;

    public DownloadFixDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        binding = DialogDownloadFixBinding.inflate(LayoutInflater.from(context));
        setContentView(binding.getRoot());
        binding.progressMsg.setText("正在下载文件中～");
        setCanceledOnTouchOutside(false);
        binding.cancel.setOnClickListener(v->{
            if (onSimpleClickListener!=null){
                onSimpleClickListener.onClick("取消下载");
            }
        });
    }

    public void setProgress(int progress){
        binding.progressMsg.setText("正在下载资源文件,进度"+progress+"%");
    }

    @Override
    public void show() {
        super.show();

        int size = (int) (ScreenUtil.getScreenW(context)*0.7);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = size;
        lp.height = size;
        getWindow().setAttributes(lp);
    }

    private OnSimpleClickListener<String> onSimpleClickListener;

    public void setOnSimpleClickListener(OnSimpleClickListener<String> onSimpleClickListener) {
        this.onSimpleClickListener = onSimpleClickListener;
    }
}
