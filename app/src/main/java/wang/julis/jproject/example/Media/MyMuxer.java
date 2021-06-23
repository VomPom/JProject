package wang.julis.jproject.example.Media;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

/*******************************************************
 *
 * Created by julis.wang on 2021/06/23 14:32
 *
 * Description :
 *
 * History   :
 *
 *******************************************************/

public class MyMuxer {
    private static final String SAVE_PATH = String.valueOf(Environment.getExternalStorageDirectory());
    private static final String name = "mixVideo.mp4";

    private int audioId, videoId;
    private MediaMuxer mediaMuxer;
    private MediaFormat audioFormat, videoFormat;
    private final MuxerListener muxerListener;
    private final MyExtractor audioExtractor = new MyExtractor();
    private final MyExtractor videoExtractor = new MyExtractor();

    public MyMuxer(MuxerListener listener) {
        this.muxerListener = listener;
        init();
    }

    private void init() {
        File dir = new File(SAVE_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(SAVE_PATH, name);
        if (file.exists()) {
            file.delete();
        }

        try {
            audioFormat = audioExtractor.getAudioFormat();
            videoFormat = videoExtractor.getVideoFormat();
            mediaMuxer = new MediaMuxer(file.getAbsolutePath(), MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    muxerListener.onStart();
                    audioId = mediaMuxer.addTrack(audioFormat);
                    videoId = mediaMuxer.addTrack(videoFormat);
                    mediaMuxer.start();

                    ByteBuffer buffer = ByteBuffer.allocate(500 * 1024);
                    MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();

                    //混合视频
                    int videoSize;
                    //读取视频帧的数据，直到结束
                    while ((videoSize = videoExtractor.readBuffer(buffer, true)) > 0) {
                        info.offset = 0;
                        info.size = videoSize;
                        info.presentationTimeUs = videoExtractor.getCurSampleTime();
                        info.flags = videoExtractor.getCurSampleFlags();
                        mediaMuxer.writeSampleData(videoId, buffer, info);
                    }
                    //写完视频，再把音频混合进去
                    int audioSize;
                    //读取音频帧的数据，直到结束
                    while ((audioSize = audioExtractor.readBuffer(buffer, false)) > 0) {
                        info.offset = 0;
                        info.size = audioSize;
                        info.presentationTimeUs = audioExtractor.getCurSampleTime();
                        info.flags = audioExtractor.getCurSampleFlags();
                        mediaMuxer.writeSampleData(audioId, buffer, info);
                    }
                    //释放资源
                    audioExtractor.release();
                    videoExtractor.release();
                    mediaMuxer.stop();
                    mediaMuxer.release();
                    muxerListener.onSuccess(SAVE_PATH + File.separator + name);
                } catch (Exception e) {
                    e.printStackTrace();
                    muxerListener.onFail(e.getMessage());
                }
            }
        }).start();
    }

    interface MuxerListener {
        void onStart();

        void onSuccess(String path);

        void onFail(String message);
    }

}

