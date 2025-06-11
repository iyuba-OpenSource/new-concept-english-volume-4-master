package com.iyuba.conceptEnglish.activity;

import android.Manifest;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.adapter.BookDownloadAdapter;
import com.iyuba.conceptEnglish.sqlite.mode.Book;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.util.PermissionUtils;
import com.iyuba.core.common.util.ToastUtil;

public class BookDownloadActivity extends BasisActivity {
    private Context mContext;

    private GridView bookGridView;
    private BookDownloadAdapter bookAdapter;
    private ImageView backButton;

    private View backView;

    private Handler handlerDownload = new Handler();
    private Book mOperateBook;

    Runnable runnable = new Runnable() {
        public void run() {
            //每0.5s 更新一次
            if (bookAdapter != null)
                bookAdapter.notifyDataSetChanged();
            handlerDownload.postDelayed(this, 500);
        }
    };

    private BookDownloadAdapter.Callback mCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.easy_download);
        CrashApplication.getInstance().addActivity(this);
        mContext = this;

        backView = findViewById(R.id.backlayout);
        backView.setBackgroundColor(Color.WHITE);

        backButton = (ImageView) findViewById(R.id.button_back);
        bookGridView = (GridView) findViewById(R.id.book_list);

        backButton.setOnClickListener(arg0 -> finish());

        mCallback = new BookDownloadAdapter.Callback() {
            @Override
            public void requestPermission(Book book) {
                mOperateBook = book;
                realRequestPermission();
            }
        };
    }

    public void init() {
        bookAdapter = new BookDownloadAdapter(mContext, mCallback);
        bookGridView.setAdapter(bookAdapter);

        handlerDownload.postDelayed(runnable, 500);
    }

    protected void onResume() {
        super.onResume();
        init();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void realRequestPermission() {
        if (PermissionUtils.requestPermission(this, 1,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,})) {
            bookAdapter.realDownload(mOperateBook);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (PermissionUtils.checkResult(permissions,grantResults)){
                    bookAdapter.realDownload(mOperateBook);
                }else {
                    ToastUtil.showToast(this,"请同意功能相关权限");
                }
                break;
            default:
                break;
        }
    }
}
