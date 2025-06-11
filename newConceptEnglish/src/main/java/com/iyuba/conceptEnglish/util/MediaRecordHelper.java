package com.iyuba.conceptEnglish.util;

import android.media.MediaRecorder;
import android.util.Log;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author yq QQ:1032006226
 * @name TrainingCampProject
 * @class name：com.iyuba.trainingcamp.utils
 * @class describe
 * @time 2018/12/22 14:11
 * @change
 * @chang time
 * @class describe
 */
public class MediaRecordHelper {
    private MediaRecorder mediaRecorder = new MediaRecorder();
    private File audioFile;

    //判断是否正在录音
    public boolean isRecording = false;

    private static final int BASE = 1;
    private long startTime, endtime, recordTime;

    public interface State {
        int ERROR = -1;
        int INITIAL = 0;
        int RECORDING = 1;
        int COMPLETED = 2;
        int RELEASED = 3;
    }

    private final int mBaseAmplitude = 80; // magic number...just a joke


    public void setFilePath(String MP4FilePath) {
        this.MP4FilePath = MP4FilePath;
    }

    private String MP4FilePath;

    public void recorder_Media() {
        File path = new File(MP4FilePath);
        audioFile = path;
        if (audioFile.exists()) {
            audioFile.delete();
        }
        try {
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(path.getAbsolutePath());
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
            startTime = System.currentTimeMillis();
        } catch (Exception e) {
            e.printStackTrace();
            mediaRecorder = new MediaRecorder();
        }
    }


    public void stop_record() {
        try {
            //下面三个参数必须加，不加的话会奔溃，在mediarecorder.stop();
            //报错为：RuntimeException:stop failed

            if (isRecording) {
                mediaRecorder.setOnErrorListener(null);
                mediaRecorder.setOnInfoListener(null);
                mediaRecorder.setPreviewDisplay(null);
                isRecording = false;
                mediaRecorder.stop();
                mediaRecorder.reset();
                endtime = System.currentTimeMillis();
                recordTime = endtime - startTime;
            }

        } catch (IllegalStateException e) {
            // TODO: handle exception
            Log.i("Exception", Log.getStackTraceString(e));
        } catch (RuntimeException e) {
            // TODO: handle exception
            Log.i("Exception", Log.getStackTraceString(e));
        } catch (Exception e) {
            // TODO: handle exception
            Log.i("Exception", Log.getStackTraceString(e));
        }
    }

    public long getRecordTime() {
        return recordTime;
    }

    public void release() {
        mediaRecorder.release();
    }


    //mp4文件合成
    public static void appendMp4List(List<String> mp4PathList, String outPutPath) throws IOException {
        List<Movie> mp4MovieList = new ArrayList<>();// Movie对象集合[输入]
        for (String mp4Path : mp4PathList) {// 将每个文件路径都构建成一个Movie对象
            mp4MovieList.add(MovieCreator.build(mp4Path));
        }
        List<Track> audioTracks = new LinkedList<>();// 音频通道集合
        List<Track> videoTracks = new LinkedList<>();// 视频通道集合
        for (Movie mp4Movie : mp4MovieList) {// 对Movie对象集合进行循环
            for (Track inMovieTrack : mp4Movie.getTracks()) {
                if ("soun".equals(inMovieTrack.getHandler())) {// 从Movie对象中取出音频通道
                    audioTracks.add(inMovieTrack);
                }
                if ("vide".equals(inMovieTrack.getHandler())) {// 从Movie对象中取出视频通道
                    videoTracks.add(inMovieTrack);
                }
            }
        }
        Movie resultMovie = new Movie();// 结果Movie对象[输出]
        if (!audioTracks.isEmpty()) {// 将所有音频通道追加合并
            resultMovie.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
        }
        if (!videoTracks.isEmpty()) {// 将所有视频通道追加合并
            resultMovie.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
        }
        Container outContainer = new DefaultMp4Builder().build(resultMovie);// 将结果Movie对象封装进容器
        FileChannel fileChannel = new RandomAccessFile(String.format(outPutPath), "rw").getChannel();
        outContainer.writeContainer(fileChannel);// 将容器内容写入磁盘
        fileChannel.close();
    }


    /**
     * 获取录音的声音分贝值
     *
     * @return
     */
    public int getDB() {
        int result = (int) (20 * Math.log10(mediaRecorder.getMaxAmplitude() / mBaseAmplitude));
        return (result >= 0) ? result : 0;
    }

}
