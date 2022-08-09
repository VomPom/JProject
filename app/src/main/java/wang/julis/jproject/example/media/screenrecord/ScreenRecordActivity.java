package wang.julis.jproject.example.media.screenrecord;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.julis.distance.R;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

import wang.julis.jwbase.Utils.ToastUtils;
import wang.julis.jwbase.basecompact.BaseActivity;

/*******************************************************
 *
 * Created by juliswang on 2021/07/28 15:08
 *
 * Description : 手机屏幕录制 (只支持Android 5.0以上，声音只能播放外界录取的声音，没法内部声音)
 *
 *              1）通过MediaProjectionManager取得向用户申请权限的intent，在onActivityResult()完成对用户动作的响应；
 *              2）用户允许后开始录制
 *              3）获取MediaProjection的实例，获取及配置MediaRecorder的实例，并MediaRecorder.prepare()；
 *              4）获取VirtualDisplay的实例，它是MediaProjection, MediaRecorder完成交互的地方，
 *                 录制的屏幕内容其实就是mediaRecorder.getSurface() 获得的 surface 上的内容。
 *
 *******************************************************/

public class ScreenRecordActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "ScreenRecord";
    private static final int REQUEST_CODE = 128;
    private static final int VIDEO_FRAME_RATE = 30;
    private static final String SAVE_PATH =
            Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + "DCIM" + File.separator + "Camera";

    private MediaRecorder mediaRecorder;
    private MediaProjection mediaProjection;
    private MediaProjectionManager projectionManager;
    private PermissionListener permissionListener;
    private VirtualDisplay virtualDisplay;
    private File saveFile;
    private String saveFileName;

    @Override
    protected void initView() {
        findViewById(R.id.btn_command).setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_screen_record;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        int viewID = view.getId();
        switch (viewID) {
            case R.id.btn_command:
                startRecord();
                break;
            case R.id.btn_stop:
                stopRecord();
                break;
        }
    }

    private void startRecord() {
        saveFileName = "record_screent_" + System.currentTimeMillis();
        requestPermissions(new String[]{
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, 100);

        permissionListener = granted -> {
            if (!granted) {
                ToastUtils.showToast("权限未正常打开");
                return;
            }
            Object projectionManagerObj = this.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            if (!(projectionManagerObj instanceof MediaProjectionManager)) {
                ToastUtils.showToast("本手机系统录屏不支持");
                return;
            }
            projectionManager = (MediaProjectionManager) projectionManagerObj;
            Intent screenCaptureIntent = projectionManager.createScreenCaptureIntent();
            ResolveInfo resolveInfo = this.getPackageManager()
                    .resolveActivity(screenCaptureIntent, PackageManager.MATCH_DEFAULT_ONLY);
            if (resolveInfo == null) {
                ToastUtils.showToast("本手机系统录屏不支持");
            } else {
                this.startActivityForResult(screenCaptureIntent, REQUEST_CODE);
            }
        };
    }

    private void stopRecord() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
            } catch (Exception e) {
                ToastUtils.showToast("Stop error:" + e.getMessage());
            } finally {
                mediaRecorder.reset();
                virtualDisplay.release();
                mediaProjection.stop();
            }
        }
        if (saveFile != null) {
            File newFile = new File(SAVE_PATH, saveFileName + ".mp4");
            saveFile.renameTo(newFile);
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(newFile));
            this.sendBroadcast(intent);
        }
    }

    private void release() {
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
        if (virtualDisplay != null) {
            virtualDisplay.release();
            virtualDisplay = null;
        }
        if (mediaProjection != null) {
            mediaProjection.stop();
            mediaProjection = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                mediaProjection = projectionManager.getMediaProjection(resultCode, data);
                realRecord();
            } else {
                ToastUtils.showToast("权限未正确打开");
            }
        }
    }

    private void realRecord() {
        ToastUtils.showToast("开始记录");
        initMediaRecord();
        mediaRecorder.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions,
                                           @NonNull @NotNull int[] grantResults) {
        if (requestCode == 100) {
            for (int grantResult : grantResults) {
                if (PackageManager.PERMISSION_GRANTED != grantResult) {
                    permissionListener.onRequestPermissionsResult(false);
                    return;
                }
            }
            permissionListener.onRequestPermissionsResult(true);
        }
    }

    private void initMediaRecord() {
        File filePath = new File(SAVE_PATH);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        saveFile = new File(SAVE_PATH, saveFileName + ".tmp");
        if (saveFile.exists()) {
            saveFile.delete();
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        int width = Math.min(displayMetrics.widthPixels, 1080);
        int height = Math.min(displayMetrics.heightPixels, 1920);
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(android.media.MediaRecorder.AudioSource.MIC);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaRecorder.setOutputFile(saveFile.getAbsolutePath());
        mediaRecorder.setVideoSize(width, height);
        mediaRecorder.setVideoEncodingBitRate(8 * 1024 * 1024);
        mediaRecorder.setVideoFrameRate(VIDEO_FRAME_RATE);
        try {
            mediaRecorder.prepare();
            virtualDisplay = mediaProjection.createVirtualDisplay(
                    "MainScreen",
                    width, height,
                    displayMetrics.densityDpi,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    mediaRecorder.getSurface(), null, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        release();
    }

    private interface PermissionListener {
        void onRequestPermissionsResult(boolean granted);
    }

}
