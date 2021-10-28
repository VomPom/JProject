package wang.julis.jproject.example.media.codec;

import android.content.Intent;
import android.os.Environment;
import android.view.Surface;
import android.view.SurfaceView;

import androidx.annotation.Nullable;

import com.julis.distance.R;

import wang.julis.jproject.example.media.codec.decode.sync.SyncVideoDecode;
import wang.julis.jwbase.basecompact.BaseActivity;

/*******************************************************
 *
 * Created by juliswang on 2021/07/30 10:21 
 *
 * Description : 
 *
 *
 *******************************************************/

public class MediaExtractorActivity extends BaseActivity {
    private static final String FILE_PATH = Environment.getExternalStorageDirectory().getPath() + "/test.mp4";
    private SurfaceView surfaceView;


    @Override
    protected void initView() {
        surfaceView = findViewById(R.id.sv_video);
    }

    @Override
    protected void initData() {
        Surface surface = surfaceView.getHolder().getSurface();
        SyncVideoDecode syncVideoDecode = new SyncVideoDecode(surface);

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
