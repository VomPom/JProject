package wang.julis.jproject.example.media.frames;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

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

    private TextView tvCost;

    @Override
    protected void initView() {
        tvCost = findViewById(R.id.tv_cost);
        findViewById(R.id.btn_start).setOnClickListener(v -> extractMpegFrames());
    }


    private void extractMpegFrames() {

        new Thread(() -> {
            try {
                ExtractMpegFramesCore test = new ExtractMpegFramesCore();

                long startTime = System.currentTimeMillis();
                ExtractMpegFramesWrapper.runTest(test);
                String msg = "Cost:" + (System.currentTimeMillis() - startTime) / 1000.0 + "s";
                Log.e("julis", msg);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        tvCost.setText(msg);
                    }
                });

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
