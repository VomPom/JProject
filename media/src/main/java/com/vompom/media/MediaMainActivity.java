package com.vompom.media;

import android.Manifest;
import android.graphics.Bitmap;

import androidx.core.app.ActivityCompat;

import com.vompom.media.audio.AudioMainActivity;
import com.vompom.media.camera.CameraMainActivity;
import com.vompom.media.codec.DCT;
import com.vompom.media.codec.MediaCodecMainActivity;
import com.vompom.media.drawimage.DrawImageActivity;
import com.vompom.media.frames.ExtractMpegFramesActivity;
import com.vompom.media.mediaplayer.MediaPlayerActivity;
import com.vompom.media.screenrecord.MediaProjectionDemoActivity;
import com.vompom.media.screenrecord.ScreenRecordActivity;
import com.vompom.media.utils.ResUtils;

import wang.julis.jwbase.basecompact.baseList.BaseListActivity;
import wang.julis.jwbase.utils.ImageUtils;
import wang.julis.jwbase.utils.Logger;

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
        addItem("音频", AudioMainActivity.class);
        addItem("多种方式绘制图片", DrawImageActivity.class);
        addItem("摄像头捕获数据", CameraMainActivity.class);
        addItem("手机录屏", ScreenRecordActivity.class);
        addItem("提取帧", ExtractMpegFramesActivity.class);
        addItem("MediaProjectionDemo", MediaProjectionDemoActivity.class);
        addItem("DCT(离散余弦变换)", () -> {
            testDCT();
            return null;
        });
        check();
        ResUtils.INSTANCE.init(this);
    }

    private void testDCT() {
        Bitmap b = ImageUtils.getBitmapFromAssets(this, "julis.png");
        Bitmap applyDCT = DCT.Companion.applyDCT(b);
        ImageUtils.saveImageToGallery(this, applyDCT);
        Bitmap applyIDCT = DCT.Companion.applyIDCT(applyDCT);
        ImageUtils.saveImageToGallery(this, applyIDCT);
        Logger.INSTANCE.d("DCT", "applyDCT  applyIDCT finished.");
    }

    private void check() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
    }
}
