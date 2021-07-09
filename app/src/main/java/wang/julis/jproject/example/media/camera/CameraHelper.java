package wang.julis.jproject.example.media.camera;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LifecycleObserver;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/*******************************************************
 *
 * Created by julis.wang on 2021/07/09 15:48
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

public class CameraHelper implements LifecycleObserver {
    private static final int CAMERA_FACING = CameraCharacteristics.LENS_FACING_BACK;
    private static final int PREVIEW_WIDTH = 1080;
    private static final int PREVIEW_HEIGHT = 2340;
    private Size previewSize = new Size(PREVIEW_WIDTH, PREVIEW_HEIGHT);

    private final Context context;
    private TextureView textureView;
    private SurfaceView surfaceView;

    private String cameraId;
    private CameraDevice cameraDevice;
    private CameraManager cameraManager;
    private ImageReader imageReader;
    private HandlerThread handlerThread;
    private FileOutputStream outputStream;

    private Handler cameraHandler;
    private CameraCaptureSession cameraCaptureSession;
    private CameraCharacteristics cameraCharacteristics;

    public CameraHelper(Context context, SurfaceView surfaceView) {
        this.context = context;
        this.surfaceView = surfaceView;
        innerInit();
    }

    public CameraHelper(Context context, TextureView textureView) {
        this.context = context;
        this.textureView = textureView;
        innerInit();
    }

    private void innerInit() {
        cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        handlerThread = new HandlerThread("CameraThread");
        handlerThread.start();
        cameraHandler = new Handler(handlerThread.getLooper());

        if (textureView != null) {
            textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                    initCamera();
                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                    close();
                    return false;
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture surface) {

                }
            });
        } else if (surfaceView != null) {
            surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    initCamera();
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    close();
                }
            });
        }

    }

    private void initCamera() {
        String[] cameraIds;
        try {
            cameraIds = cameraManager.getCameraIdList();
            for (String cameraId : cameraIds) {
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                int cameraFacing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
                if (cameraFacing == CAMERA_FACING) {
                    this.cameraCharacteristics = cameraCharacteristics;
                    this.cameraId = cameraId;
                    break;
                }
            }

            StreamConfigurationMap streamConfigMap =
                    cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Size[] sizes = streamConfigMap.getHighSpeedVideoSizes();
            if (textureView != null) {
                textureView.getSurfaceTexture().setDefaultBufferSize(sizes[0].getWidth(), sizes[0].getHeight());
            }

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        imageReader = ImageReader.newInstance(
                previewSize.getWidth(),
                previewSize.getHeight(),
                ImageFormat.YUV_420_888,
                1
        );

        openCamera();
    }

    private void openCamera() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "没有照相机权限", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            cameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull @NotNull CameraDevice camera) {
                    cameraDevice = camera;
                    createCaptureSession(cameraDevice);
                }

                @Override
                public void onDisconnected(@NonNull @NotNull CameraDevice camera) {
                }

                @Override
                public void onError(@NonNull @NotNull CameraDevice camera, int error) {
                }
            }, cameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void createCaptureSession(CameraDevice camera) {
        if (camera == null) {
            return;
        }
        Surface surface;
        if (surfaceView != null) {
            surface = surfaceView.getHolder().getSurface();
        } else {
            surface = new Surface(textureView.getSurfaceTexture());
        }

        if (surface == null) {
            throw new IllegalStateException("Surface 错误");
        }
        try {
            CaptureRequest.Builder captureRequestBuilder =
                    cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

            captureRequestBuilder.addTarget(surface);
            captureRequestBuilder.set(
                    CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_ANTIBANDING_MODE_AUTO
            );
            captureRequestBuilder.set(
                    CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
            );
            cameraDevice.createCaptureSession(
                    Arrays.asList(surface, imageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull @NotNull CameraCaptureSession session) {
                            cameraCaptureSession = session;
                            try {
                                cameraCaptureSession.setRepeatingRequest(
                                        captureRequestBuilder.build(),
                                        captureCallback,
                                        cameraHandler
                                );
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull @NotNull CameraCaptureSession session) {
                            Toast.makeText(context, "开启预览会话失败", Toast.LENGTH_SHORT).show();
                        }
                    }, cameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    private final CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(@NonNull @NotNull CameraCaptureSession session, @NonNull @NotNull CaptureRequest request, @NonNull @NotNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
        }

        @Override
        public void onCaptureFailed(@NonNull @NotNull CameraCaptureSession session, @NonNull @NotNull CaptureRequest request, @NonNull @NotNull CaptureFailure failure) {
            super.onCaptureFailed(session, request, failure);
            Toast.makeText(context, "预览失败", Toast.LENGTH_SHORT).show();
        }
    };

    //TODO: large file
    public void recording(File file) {
        try {
            outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        HandlerThread writerHandlerThread = new HandlerThread("writerHandlerThread");
        writerHandlerThread.start();
        Handler writerHandler = new Handler(writerHandlerThread.getLooper());

        imageReader.setOnImageAvailableListener(reader -> {
            Image image = reader.acquireNextImage();
            byte[] bytes = new byte[imageReader.getWidth() * imageReader.getHeight() * 3 / 2];
            int count = 0;
            for (Image.Plane plane : image.getPlanes()) {
                ByteBuffer byteBuffer = plane.getBuffer();
                int pixelStride = plane.getPixelStride();
                for (int i = 0; i < byteBuffer.remaining(); i += pixelStride) {
                    bytes[count++] = byteBuffer.get(i);
                }
            }
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                image.close();
            }
        }, writerHandler);

        Surface surface;
        if (surfaceView != null) {
            surface = surfaceView.getHolder().getSurface();
        } else {
            surface = new Surface(textureView.getSurfaceTexture());
        }

        if (surface == null) {
            throw new IllegalStateException("Surface 错误");
        }
        try {
            CaptureRequest.Builder captureRequestBuilder =
                    cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            captureRequestBuilder.addTarget(imageReader.getSurface());
            captureRequestBuilder.addTarget(surface);
            captureRequestBuilder.set(
                    CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_ANTIBANDING_MODE_AUTO
            );
            captureRequestBuilder.set(
                    CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
            );
            cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureStarted(@NonNull @NotNull CameraCaptureSession session, @NonNull @NotNull CaptureRequest request, long timestamp, long frameNumber) {
                    super.onCaptureStarted(session, request, timestamp, frameNumber);
                }
            }, cameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void close() {
        cameraCaptureSession.close();
        cameraCaptureSession = null;
        cameraDevice.close();
//        outputStream.close();
        if (imageReader != null) {
            imageReader.close();
        }
    }


}








