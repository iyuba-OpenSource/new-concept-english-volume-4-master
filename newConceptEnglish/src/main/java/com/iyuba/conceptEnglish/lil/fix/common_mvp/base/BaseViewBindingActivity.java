package com.iyuba.conceptEnglish.lil.fix.common_mvp.base;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import com.iyuba.core.lil.base.BaseStackActivity;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.mvp.BaseView;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @desction: 基础activity(ViewBinding类型)
 * @date: 2023/3/15 17:55
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class BaseViewBindingActivity<VB extends ViewBinding> extends BaseStackActivity implements BaseView {

    protected VB binding;
    protected AppCompatActivity context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.context = this;

        try {
            Type type = this.getClass().getGenericSuperclass();
            if (type instanceof ParameterizedType){
                Class clz = (Class<VB>) ((ParameterizedType)type).getActualTypeArguments()[0];
                Method method = clz.getMethod("inflate", LayoutInflater.class);
                binding = (VB) method.invoke(null,this.getLayoutInflater());
                setContentView(binding.getRoot());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d("堆栈数据", "数据集合--3333");
        binding = null;
    }

    @Override
    public void finish() {
        super.finish();
        Log.d("当前退出的界面0018", getClass().getName());
    }
}
