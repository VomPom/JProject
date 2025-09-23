package com.vompom.media.codec.v1.decode;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.util.Log;

import com.vompom.media.codec.v1.MyExtractor;

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

            if (mediaFormat == null) {
                Log.e(TAG, "MediaFormat is null for type: " + (type == VIDEO ? "VIDEO" : "AUDIO"));
                return;
            }

            String mime = mediaFormat.getString(MediaFormat.KEY_MIME);
            Log.d(TAG, "Creating codec for mime: " + mime + ", type: " + (type == VIDEO ? "VIDEO" : "AUDIO"));

            extractor.selectTrack(type == VIDEO ? extractor.getVideoTrackId() : extractor.getAudioTrackId());
            mediaCodec = MediaCodec.createDecoderByType(mime);

            Log.d(TAG, "MediaCodec created successfully: " + mediaCodec.getName());

        } catch (IOException e) {
            Log.e(TAG, "Error creating MediaCodec", e);
            e.printStackTrace();
        }
    }


    protected abstract int decodeType();
}
