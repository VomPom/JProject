package wang.julis.jproject.example.media;

import android.Manifest;

import androidx.core.app.ActivityCompat;

import wang.julis.jproject.example.media.audio.AudioMainActivity;
import wang.julis.jproject.example.media.camera.CameraMainActivity;
import wang.julis.jproject.example.media.codec.MediaCodecMainActivity;
import wang.julis.jproject.example.media.drawimage.DrawImageActivity;
import wang.julis.jproject.example.media.frames.ExtractMpegFramesActivity;
import wang.julis.jproject.example.media.screenrecord.MediaProjectionDemoActivity;
import wang.julis.jproject.example.media.screenrecord.ScreenRecordActivity;
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
        addActivity("MediaPlayer播放视频", MediaPlayerActivity.class);
        addActivity("MediaCodec", MediaCodecMainActivity.class);
        addActivity("多种方式绘制图片", DrawImageActivity.class);
        addActivity("Audio", AudioMainActivity.class);
        addActivity("摄像头捕获数据", CameraMainActivity.class);
        addActivity("手机录屏", ScreenRecordActivity.class);
        addActivity("提取帧", ExtractMpegFramesActivity.class);
        addActivity("MediaProjectionDemo", MediaProjectionDemoActivity.class);
        check();
    }

    private void check() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
    }
}
