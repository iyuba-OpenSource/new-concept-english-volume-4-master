package com.iyuba.core.common.widget.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import com.iyuba.lib.R;

public class DownloadDialog extends BaseDialog {

    private OnDownloadListener mOnDownloadListener;

    public DownloadDialog(Context context) {
        super(context, R.style.DialogTheme);
    }

    public DownloadDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public void setmOnDownloadListener(OnDownloadListener listener) {
        this.mOnDownloadListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_download);
        initClick();
        //dialogComponent().inject(this);

        WindowManager m = getOwnerActivity().getWindowManager();
        Display defaultDisplay = m.getDefaultDisplay();
        Window window = getWindow();
        window.setBackgroundDrawableResource(R.drawable.dialog_bkg);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = (int) (defaultDisplay.getWidth() * 0.78);
        window.setAttributes(layoutParams);
    }

    private void initClick(){
        findViewById(R.id.continue_loading_tv).setOnClickListener(v->{
            if(mOnDownloadListener != null) {
                mOnDownloadListener.onContinue();
            }
        });
        findViewById(R.id.cancel_loading_tv).setOnClickListener(v->{
            if (mOnDownloadListener != null) {
                mOnDownloadListener.onCancel();
            }
        });
    }

    public interface OnDownloadListener {
        void onContinue();

        void onCancel();
    }
}
