package wang.julis.jproject.example.media.codec;

import android.media.MediaExtractor;
import android.media.MediaFormat;

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
            mediaExtractor.setDataSource(MuxerActivity.SOURCE_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int count = mediaExtractor.getTrackCount();
        for (int i = 0; i < count; i++) {
            MediaFormat format = mediaExtractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("video")) {
                videoTrackId = i;
                videoFormat = format;
            } else if (mime.startsWith("audio")) {
                audioTrackId = i;
                audioFormat = format;
            }
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
        mediaExtractor.selectTrack(video ? videoTrackId : audioTrackId);
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
