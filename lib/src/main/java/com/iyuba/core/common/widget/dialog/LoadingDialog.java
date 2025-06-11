package com.iyuba.core.common.widget.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.iyuba.core.lil.util.LibGlide3Util;
import com.iyuba.lib.R;

/**
 * 自定义加载进度对话框
 * Created by Administrator on 2016-10-28.
 */

public class LoadingDialog extends AlertDialog {

    private Context context;
    private TextView mTextTv;

    public LoadingDialog(Context context) {
        super(context, R.style.DialogTheme);
        /**设置对话框背景透明*/
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_loading);
        setCanceledOnTouchOutside(false);

        mTextTv = findViewById(R.id.loading_tv);
        ImageView loadingPic = findViewById(R.id.loadingPic);
//        Glide.with(context).load(R.drawable.ic_loading_new).asGif().into(loadingPic);
        LibGlide3Util.loadGif(context,R.drawable.ic_loading_new,R.drawable.ic_loading_new,loadingPic);
//        int dividerId = context.getResources().getIdentifier("android:id/titleDivider", null, null);
//        View divider = findViewById(dividerId);
//        divider.setBackgroundColor(getContext().getResources().getColor(R.color.transparent));

        mTextTv.setText("正在加载~");
    }

    /**
     * 为加载进度个对话框设置不同的提示消息
     *
     * @param message 给用户展示的提示信息
     * @return build模式设计，可以链式调用
     */
    public LoadingDialog setMessage(String message) {
        mTextTv.setText(message);
        return this;
    }


}