package com.vompom.media;

import android.Manifest;

import androidx.core.app.ActivityCompat;

import com.vompom.media.audio.AudioMainActivity;
import com.vompom.media.camera.CameraMainActivity;
import com.vompom.media.codec.MediaCodecMainActivity;
import com.vompom.media.drawimage.DrawImageActivity;
import com.vompom.media.frames.ExtractMpegFramesActivity;
import com.vompom.media.mediaplayer.MediaPlayerActivity;
import com.vompom.media.screenrecord.MediaProjectionDemoActivity;
import com.vompom.media.screenrecord.ScreenRecordActivity;
import com.vompom.media.utils.ResUtils;

import wang.julis.jwbase.basecompact.baseList.BaseListActivity;

/*******************************************************
 *
 * Created by julis.wang on 2021/07/08 13:49
 *
 * Description : https://zhuanlan.zhihu.com/p/28518637
 *
 * History   :
 *
 *******************************************************/

public class MediaMainActivity extends BaseListActivity {

    @Override
    protected void initData() {
        addItem("MediaPlayer播放视频", MediaPlayerActivity.class);
        addItem("MediaCodec", MediaCodecMainActivity.class);
        addItem("多种方式绘制图片", DrawImageActivity.class);
        addItem("Audio", AudioMainActivity.class);
        addItem("摄像头捕获数据", CameraMainActivity.class);
        addItem("手机录屏", ScreenRecordActivity.class);
        addItem("提取帧", ExtractMpegFramesActivity.class);
        addItem("MediaProjectionDemo", MediaProjectionDemoActivity.class);
        check();
        ResUtils.INSTANCE.init(this);
    }

    private void check() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
    }
}
