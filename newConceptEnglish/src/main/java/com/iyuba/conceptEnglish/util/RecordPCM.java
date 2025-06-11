//package com.iyuba.conceptEnglish.util;
//
//import android.media.AudioFormat;
//import android.media.AudioRecord;
//import android.media.MediaRecorder;
//import android.util.Log;
//
//import com.iyuba.conceptEnglish.adapter.ValReadAdapter;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//
///**
// * Created by iyuba on 2018/12/19.
// */
//
//public class RecordPCM {
//    /**
//     * R
//     * 采样率，现在能够保证在所有设备上使用的采样率是44100Hz, 但是其他的采样率（22050, 16000, 11025）在一些设备上也可以使用。
//     */
//    public static final int SAMPLE_RATE_INHZ = 16000;
//
//    /**
//     * 声道数。CHANNEL_IN_MONO and CHANNEL_IN_STEREO. 其中CHANNEL_IN_MONO是可以保证在所有设备能够使用的。
//     */
//    public static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_STEREO;
//    /**
//     * 返回的音频数据的格式。 ENCODING_PCM_8BIT, ENCODING_PCM_16BIT, and ENCODING_PCM_FLOAT.
//     */
//    public static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
//
//    private static final String TAG = "RecordPCM";
//    private AudioRecord audioRecord = null;  // 声明 AudioRecord 对象
//    private int recordBufSize = 0; // 声明recoordBufffer的大小字段
//    private String pcmFilePath;
//    private boolean isRecording;
//
//
//    private long startTime, endTime, recordTime;
//    private ValReadAdapter valReadAdapter;
//    private int readVioce;
//
//
//    public RecordPCM(String pcmFilePath, long recordTime, ValReadAdapter valReadAdapter) {
//        this.pcmFilePath = pcmFilePath;
//        this.recordTime = recordTime;
//        this.valReadAdapter = valReadAdapter;
//    }
//
//
//    public void startRecord() {
//
//        final int minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT);
//        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT, minBufferSize);
//
//        final byte data[] = new byte[minBufferSize];
//        final File file = new File(pcmFilePath);
//        if (file.exists()) {
//            file.delete();
//        }
//
//        audioRecord.startRecording();
//        /* 获取开始时间* */
//        startTime = System.currentTimeMillis();
//        isRecording = true;
//
//
//        // TODO: 2018/3/10 pcm数据无法直接播放，保存为WAV格式。
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//
//                FileOutputStream os = null;
//                try {
//
//                    os = new FileOutputStream(file);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                if (null != os) {
//                    while (isRecording) {
//                        int read = audioRecord.read(data, 0, minBufferSize);
//                        readVioce = (int) countDb(data);
//                        // 如果读取音频数据没有出现错误，就将数据写入到文件
//                        if (AudioRecord.ERROR_INVALID_OPERATION != read) {
//                            try {
//                                os.write(data);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                    try {
//                        Log.i(TAG, "run: close file output stream !");
//                        os.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//
//            }
//        }).start();
//
//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                try {
//                    //睡眠本句子时长1.5时长后，结束录音
//                    sleep(recordTime);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                valReadAdapter.dismissDia();
//
//            }
//        }.start();
//    }
//
//    public int getReadVioce() {
//
//        return readVioce;
//    }
//
//    public long stopRecord() {
//        isRecording = false;
//        // 释放资源
//        if (audioRecord == null) return 0L;
//        if (startTime == 0 || (startTime < endTime)) {
//            return 0L;
//        } else {
//            try {
//                audioRecord.stop();
//            } catch (IllegalStateException e) {
//                // TODO 如果当前java状态和jni里面的状态不一致，
//                //e.printStackTrace();
//                audioRecord = null;
//                final int minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT);
//                audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT, minBufferSize);
//
//            }
//            audioRecord.release();
//            audioRecord = null;
//        }
//        endTime = System.currentTimeMillis();
//        return endTime - startTime;
//    }
//
//    /**
//     * 计算录音分贝值
//     *
//     * @param read
//     * @param data
//     * @return
//     */
//    private double getVoice(int read, byte data[]) {
//        long v = 0;
//        // 将 buffer 内容取出，进行平方和运算
//        for (int i = 0; i < data.length; i++) {
//            v += data[i] * data[i];
//        }
//        // 平方和除以数据总长度，得到音量大小。
//        double mean = v / (double) read;
//        final double volume = 10 * Math.log10(mean);
//        Log.e("volume", volume + "");
//        return volume;
//    }
//
//    public double countDb(byte[] data) {
//        float BASE = 32768f;
//        float maxAmplitude = 0;
//
//        for (int i = 0; i < data.length; i++) {
//            maxAmplitude += data[i] * data[i];
//        }
//        maxAmplitude = (float) Math.sqrt(maxAmplitude / data.length);
//        float ratio = maxAmplitude / BASE;
//        float db = 0;
//        if (ratio > 0) {
//            db = (float) (20 * Math.log10(ratio)) + 100;
//        }
//
//        return db;
//    }
//
//
//}
