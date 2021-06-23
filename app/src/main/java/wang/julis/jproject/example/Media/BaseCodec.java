package wang.julis.jproject.example.Media;

import android.media.MediaCodec;
import android.media.MediaFormat;

import java.io.IOException;

public abstract class BaseCodec {
    private static final String TAG = "BaseCodec";
    protected final static int VIDEO = 1;
    protected final static int AUDIO = 2;
    protected MediaFormat mediaFormat;
    protected MediaCodec mediaCodec;
    protected MyExtractor extractor;

    public BaseCodec() {
        try {
            extractor = new MyExtractor();
            int type = decodeType();
            mediaFormat = (type == VIDEO ? extractor.getVideoFormat() : extractor.getAudioFormat());
            String mime = mediaFormat.getString(MediaFormat.KEY_MIME);
            extractor.selectTrack(type == VIDEO ? extractor.getVideoTrackId() : extractor.getAudioTrackId());
            mediaCodec = MediaCodec.createDecoderByType(mime);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    protected abstract int decodeType();
}
