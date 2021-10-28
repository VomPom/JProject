package wang.julis.jproject.example.media.codec;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.julis.distance.R;

import java.io.File;

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

public class MuxerActivity extends BaseActivity implements View.OnClickListener {
    public static final String SOURCE_PATH = Environment.getExternalStorageDirectory() + "/test.mp4";
    private static final String SAVE_PATH = String.valueOf(Environment.getExternalStorageDirectory());
    private static final String name = "mixVideo.mp4";
    private TextView tvTips;

    @Override
    protected void initView() {
        findViewById(R.id.btn_muxer).setOnClickListener(this);
        tvTips = findViewById(R.id.tv_tips);
    }

    /**
     * 音视频分离与合成
     */
    private void testMuxer() {
        new MyMuxer(SAVE_PATH + File.separator + name, new MyMuxer.MuxerListener() {
            @Override
            public void onStart() {
                Log.e("----julis", "Muxer start");
            }

            @Override
            public void onSuccess(String path) {
                tvTips.setText("Muxer onSuccess,save in:" + path);
            }

            @Override
            public void onFail(String message) {
                tvTips.setText("Muxer onFail:" + message);
            }
        }).start();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_media_muxer;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.btn_muxer:
                testMuxer();
                break;
        }
    }

}
