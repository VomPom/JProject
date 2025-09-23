package com.vompom.media.codec.v1.player;

import android.content.Intent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.vompom.media.R;
import com.vompom.media.codec.v1.decode.sync.SyncAudioDecode;
import com.vompom.media.codec.v1.decode.sync.SyncVideoDecode;

import wang.julis.jwbase.basecompact.BaseActivity;

/*******************************************************
 *
 * Created by juliswang on 2021/07/30 10:21 
 *
 * Description :  使用两个不同的线程分别去播放 .mp4 文件的音频轨道和视频轨道
 *                视频帧数据渲染到 SurfaceView 上，音频使用 AudioTrack进行播放
 *
 *
 *******************************************************/

public class PlayMediaActivity extends BaseActivity {
    private SurfaceView surfaceView;

    @Override
    protected void initView() {
        surfaceView = findViewById(R.id.sv_video);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                Surface surface = holder.getSurface();
                SyncVideoDecode syncVideoDecode = new SyncVideoDecode(surface);
                SyncAudioDecode syncAudioDecode = new SyncAudioDecode();
                new Thread(syncVideoDecode).start();
                new Thread(syncAudioDecode).start();
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

            }
        });

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_media;
    }
}
