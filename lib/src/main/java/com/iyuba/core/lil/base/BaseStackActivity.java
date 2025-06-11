package com.iyuba.core.lil.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @title:
 * @date: 2023/11/16 15:09
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class BaseStackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StackUtil.getInstance().add(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StackUtil.getInstance().remove(this);
    }
}
