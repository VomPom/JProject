package com.vompom.media.codec.v1;

import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;

import com.vompom.media.utils.ResUtils;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

/*******************************************************
 *
 * Created by julis.wang on 2021/06/23 14:32
 *
 * Description : 提取并解析音视频
 *
 * History   :
 *
 *******************************************************/

public class MyExtractor {

    private int curSampleFlags;
    private int videoTrackId;
    private int audioTrackId;
    private long curSampleTime;

    private MediaFormat videoFormat;
    private MediaFormat audioFormat;
    private MediaExtractor mediaExtractor;


    public MyExtractor() {
        init();
    }

    private void init() {
        try {
            mediaExtractor = new MediaExtractor();
            String videoPath = ResUtils.INSTANCE.getTestVideo();
            Log.d("MyExtractor", "Trying to set data source: " + videoPath);

            // 检查文件是否存在
            File videoFile = new File(videoPath);
            if (!videoFile.exists()) {
                Log.e("MyExtractor", "Video file does not exist: " + videoPath);
                return;
            } else if (!videoFile.canRead()) {
                Log.e("MyExtractor", "Video file cannot be read: " + videoPath);
                return;
            } else {
                Log.d("MyExtractor", "Video file exists and is readable, size: " + videoFile.length() + " bytes");
            }

            mediaExtractor.setDataSource(videoPath);
            Log.d("MyExtractor", "MediaExtractor.setDataSource successful");
        } catch (IOException e) {
            Log.e("MyExtractor", "Error setting data source", e);
            e.printStackTrace();
            return;
        }

        int count = mediaExtractor.getTrackCount();
        Log.d("MyExtractor", "Found " + count + " tracks");

        for (int i = 0; i < count; i++) {
            MediaFormat format = mediaExtractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            Log.d("MyExtractor", "Track " + i + ": " + mime);

            if (mime.startsWith("video")) {
                videoTrackId = i;
                videoFormat = format;
                Log.d("MyExtractor", "Video track found at index " + i);
            } else if (mime.startsWith("audio")) {
                audioTrackId = i;
                audioFormat = format;
                Log.d("MyExtractor", "Audio track found at index " + i);
            }
        }

        if (videoFormat == null) {
            Log.e("MyExtractor", "No video track found in the file");
        }
        if (audioFormat == null) {
            Log.w("MyExtractor", "No audio track found in the file");
        }
    }

    /**
     * 读取一帧的数据
     */
    public int readBuffer(ByteBuffer buffer) {
        return readBuffer(buffer, true);
    }

    public int readBuffer(ByteBuffer buffer, boolean video) {
        buffer.clear();
        // 移除重复的selectTrack调用，因为在BaseCodec构造函数中已经调用过了
        // mediaExtractor.selectTrack(video ? videoTrackId : audioTrackId);
        int bufferCount = mediaExtractor.readSampleData(buffer, 0);
        if (bufferCount < 0) {
            return -1;
        }
        curSampleTime = mediaExtractor.getSampleTime();
        curSampleFlags = mediaExtractor.getSampleFlags();
        mediaExtractor.advance();
        return bufferCount;
    }

    public void selectTrack(int trackId) {
        mediaExtractor.selectTrack(trackId);
    }

    public int getVideoTrackId() {
        return videoTrackId;
    }

    public int getAudioTrackId() {
        return audioTrackId;
    }

    /**
     * 获取音频 MediaFormat
     *
     * @return
     */
    public MediaFormat getAudioFormat() {
        return audioFormat;
    }

    /**
     * 获取视频 MediaFormat
     *
     * @return
     */
    public MediaFormat getVideoFormat() {
        return videoFormat;
    }

    /**
     * 获取当前帧的标志位
     *
     * @return
     */
    public int getCurSampleFlags() {
        return curSampleFlags;
    }

    /**
     * 获取当前帧的时间戳
     *
     * @return
     */
    public long getCurSampleTime() {
        return curSampleTime;
    }

    /**
     * 释放资源
     */
    public void release() {
        mediaExtractor.release();
    }


}
