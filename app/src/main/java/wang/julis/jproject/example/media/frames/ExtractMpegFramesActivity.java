package wang.julis.jproject.example.media.frames;

import android.util.Log;

import com.julis.distance.R;

import wang.julis.jwbase.basecompact.BaseActivity;

/*******************************************************
 *
 * Created by juliswang on 2021/08/20 14:48 
 *
 * Description : 
 *
 *
 *******************************************************/

public class ExtractMpegFramesActivity extends BaseActivity {
    @Override
    protected void initView() {
        findViewById(R.id.btn_start).setOnClickListener(v -> extractMpegFrames());
    }

    private void extractMpegFrames() {
        ExtractMpegFramesCore test = new ExtractMpegFramesCore();

        new Thread(() -> {
            try {
                long startTime = System.currentTimeMillis();
                ExtractMpegFramesWrapper.runTest(test);
                Log.e("julis", "Cost:" + (System.currentTimeMillis() - startTime));
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                Log.e("julis", "throwable:" + throwable.getMessage());
            }

        }).start();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_screen_record;
    }
}
