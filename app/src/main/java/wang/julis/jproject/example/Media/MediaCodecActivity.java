package wang.julis.jproject.example.Media;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.julis.distance.R;

import wang.julis.jwbase.basecompact.BaseActivity;

/*******************************************************
 *
 * Created by julis.wang on 2021/06/22 19:42
 *
 * Description :
 *
 * History   :
 *
 *******************************************************/

public class MediaCodecActivity extends BaseActivity implements View.OnClickListener {
    private SurfaceHolder surfaceHolder;
    public static final String PATH = Environment.getExternalStorageDirectory() + "/test.mp4";

    private static final String url = "https://media.w3.org/2010/05/sintel/trailer.mp4";

    @Override
    protected void initView() {
        SurfaceView surfaceView = findViewById(R.id.sv_video);
        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_end).setOnClickListener(this);
        findViewById(R.id.btn_pause).setOnClickListener(this);

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });


    }

    /**
     * 音视频分离与合成
     */
    private void testMediaCode() {
        new MyMuxer(new MyMuxer.MuxerListener() {
            @Override
            public void onStart() {
                Log.e("julis", "Muxer start");
            }

            @Override
            public void onSuccess(String path) {
                Log.e("julis", "Muxer onSuccess:" + path);
            }

            @Override
            public void onFail(String message) {
                Log.e("julis", "Muxer onFail:" + message);
            }
        }).start();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_media;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            default:
                testMediaCode();
                break;
        }
    }

}
