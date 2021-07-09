package wang.julis.jproject.example.media.camera;

import android.os.Environment;
import android.view.TextureView;

import com.julis.distance.R;

import java.io.File;

import wang.julis.jwbase.basecompact.BaseActivity;

/*******************************************************
 *
 * Created by julis.wang on 2021/07/09 14:23
 *
 * Description :
 *
 * History   :
 *
 *******************************************************/

public class TextureViewCameraActivity extends BaseActivity {

    @Override
    protected void initView() {
        TextureView textureView = findViewById(R.id.ture_camera);
        CameraHelper helper = new CameraHelper(this, textureView);
        findViewById(R.id.btn_recording).setOnClickListener(v -> {
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_MOVIES), "test.mp4");
            helper.recording(file);
        });
        findViewById(R.id.btn_take_photo).setOnClickListener(v -> {
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_camera_texture_view;
    }


}
