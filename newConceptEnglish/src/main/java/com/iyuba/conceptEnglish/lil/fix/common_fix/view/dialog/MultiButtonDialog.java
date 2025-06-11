package com.iyuba.conceptEnglish.lil.fix.common_fix.view.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.iyuba.conceptEnglish.databinding.DialogMultiButtonBinding;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.ScreenUtil;

/**
 * @title: 多按钮弹窗
 * @date: 2023/5/14 19:39
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class MultiButtonDialog extends AlertDialog {

    private Context context;
    private DialogMultiButtonBinding binding;

    public MultiButtonDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        binding = DialogMultiButtonBinding.inflate(LayoutInflater.from(context));
        setContentView(binding.getRoot());
        setCancelable(false);
    }

    @Override
    public void show() {
        super.show();

        int width = ScreenUtil.getScreenW(context);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = (int) (width*0.8);
        getWindow().setAttributes(lp);
    }

    //设置标题
    public void setTitle(String title){
        binding.title.setText(title);
    }

    //设置按钮
    public void setButton(String disagree,String agree,OnMultiClickListener listener){
        binding.agree.setText(agree);
        binding.agree.setOnClickListener(v->{
            dismiss();

            if (listener!=null){
                listener.onAgree();
            }
        });
        binding.disagree.setText(disagree);
        binding.disagree.setOnClickListener(v->{
            dismiss();

            if (listener!=null){
                listener.onDisagree();
            }
        });
    }

    //设置信息
    public void setMsg(String content){
        binding.content.setText(content);
    }

    //设置回调
    public interface OnMultiClickListener{
        //同意
        void onAgree();
        //不同意
        void onDisagree();
    }
}
