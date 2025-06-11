package com.iyuba.conceptEnglish.model;

public class PullYouthWordThread extends Thread {
    private Callback mCallback;

    public PullYouthWordThread(Callback callback) {
        mCallback = callback;
    }

    @Override
    public void run() {


        if (mCallback!=null){
            mCallback.callback();
        }
    }

    public interface Callback {
        void callback();
    }
}
