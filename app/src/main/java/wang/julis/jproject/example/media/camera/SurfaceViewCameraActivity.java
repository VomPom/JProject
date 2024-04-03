package wang.julis.jproject.example.media.camera;

import android.view.SurfaceView;

import com.julis.wang.R;

import wang.julis.jwbase.basecompact.BaseActivity;

/*******************************************************
 *
 * Created by julis.wang on 2021/07/09 14:23
 *
 * Description :
 *               Camera2 API 使用的具体步骤如下:
 *                  1、准备好预览界面(SurfaceView/TextureView)
 *                  2、构建 HandlerThread 子线程
 *                  3、获取 CameraManager
 *                  4、通过CameraCharacteristic从CameraManager.cameraIdList 中获取对应的摄像头 ID
 *                  5、通过摄像头 ID 获取其CameraCharacteristic
 *                  6、从CameraCharacteristic中获取 StreamConfigMap
 *                  7、从 StreamConfigMap 中获取可用的输出尺寸，并依此确定录制视频的尺寸和预览的尺寸
 *                  8、从 CameraCharacteristic 中获取可用的 FPS 值和对应的摄像头传感器旋转值
 *                  9、根据 7 中获取到的录制视频的尺寸构建 ImageReader 实例
 *                  10、使用 manager.openCamera() 发送打开摄像头请求，并在回调中获取 CameraDevice 实例
 *                  11、使用 CameraDevice 实例构建 CaptureRequest
 *                  12、调用 CameraDevice.createCaptrueSession() 开启会话
 *                  13、在 CaptrueSession 中设置之前的 CaptrueRequest 即可实现拍照或者录像
 * History   :
 *
 *******************************************************/

public class SurfaceViewCameraActivity extends BaseActivity {
    private int cameraPosition;
    private SurfaceView surfaceView;

    @Override
    protected void initView() {
        findViewById(R.id.btn_change_camera).setOnClickListener(v -> {
        });
        surfaceView = findViewById(R.id.sv_camera);
    }

    @Override
    protected void initData() {
        CameraHelper cameraHelper = new CameraHelper(this, surfaceView);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_camera_surface_view;
    }


}
