package com.iyuba.conceptEnglish.widget;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.iyuba.core.common.util.OnPlayStateChangedListener;

import java.io.IOException;

/**
 * 简化播放器
 *
 * @author 陈彤
 */
public class Player implements OnCompletionListener, OnPreparedListener, OnErrorListener {
    public MediaPlayer mediaPlayer;
    private OnPlayStateChangedListener opscl;
    private String audioUrl;

    public Player(Context context, OnPlayStateChangedListener opscl) {
        this.opscl = opscl;
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnErrorListener(this);
        } catch (Exception e) {


        }
    }

    public void initialize(String url) {
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(url);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void prepareAndPlay() {
        mediaPlayer.prepareAsync();
    }

    public void playUrl(final String videoUrl) {
        this.audioUrl = videoUrl;
        handler.sendEmptyMessage(1);
    }

    public void playUrl2(String url) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start(){
        mediaPlayer.start();
    }
    public void pause() {
        mediaPlayer.pause();
    }

    public void reset() {
        mediaPlayer.reset();
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    /**
     * 通过onPrepared播放
     */ public void onPrepared(MediaPlayer arg0) {
        arg0.start();
        if (opscl != null) {
            opscl.playSuccess();
        }
    }

    @Override
    public void onCompletion(MediaPlayer arg0) {
        if (opscl != null) {
            opscl.playCompletion();
        }
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    try {
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(audioUrl);
                        mediaPlayer.prepareAsync();

                    } catch (IllegalArgumentException e) {
                        // TODO Auto-generated catch block
                        if (opscl != null) {
                            opscl.playFaild();
                        }
                        e.printStackTrace();
                    } catch (IllegalStateException e) {
                        // TODO Auto-generated catch block
                        if (opscl != null) {
                            opscl.playFaild();
                        }
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        if (opscl != null) {
                            opscl.playFaild();
                        }
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    public String getTimes() {
        StringBuffer timeBuffer = new StringBuffer("");
        if (mediaPlayer != null) {
            int musicTime = mediaPlayer.getCurrentPosition() / 1000;

            String minu = String.valueOf(musicTime / 60);
            if (minu.length() == 1) {
                minu = "0" + minu;
            }
            String sec = String.valueOf(musicTime % 60);
            if (sec.length() == 1) {
                sec = "0" + sec;
            }

            timeBuffer.append(minu).append(":").append(sec);
        }
        return timeBuffer.toString();
    }

    public int getTime() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    /**
     * 获取音频总长
     *
     * @return
     */
    public String getDurations() {
        StringBuffer timeBuffer = new StringBuffer("");
        if (mediaPlayer != null) {
            int musicTime = mediaPlayer.getDuration() / 1000;

            String minu = String.valueOf(musicTime / 60);
            if (minu.length() == 1) {
                minu = "0" + minu;
            }
            String sec = String.valueOf(musicTime % 60);
            if (sec.length() == 1) {
                sec = "0" + sec;
            }

            timeBuffer.append(minu).append(":").append(sec);
        }
        return timeBuffer.toString();
    }

    public int getDuration() {
        if (mediaPlayer != null) {
            return mediaPlayer.getDuration();
        }
        return 0;
    }

    public int getCurrDuration() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        StringBuilder sb = new StringBuilder();
        sb.append("what : ").append(what).append("\nextra : ").append(extra);
        Log.e("Player", sb.toString());
        return false;
    }



    public void seekTo(int time){
        if (mediaPlayer != null) {
             mediaPlayer.seekTo(time);
        }
    }
}
