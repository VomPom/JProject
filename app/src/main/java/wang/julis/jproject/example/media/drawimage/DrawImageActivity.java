package wang.julis.jproject.example.media.drawimage;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.julis.distance.R;

import java.util.Timer;
import java.util.TimerTask;

import wang.julis.jproject.utils.ImageUtils;
import wang.julis.jwbase.basecompact.BaseActivity;

/*******************************************************
 *
 * Created by julis.wang on 2021/07/08 14:13
 *
 * Description : 在 Android 平台绘制一张图片，使用至少 3 种不同的 API，ImageView，SurfaceView，自定义 View
 *
 * History   :
 *
 *******************************************************/

public class DrawImageActivity extends BaseActivity {
    private static final int PERIOD_TIME = 1000;
    boolean flag = false;
    private ImageView ivNormal;
    private CustomImageView customImageView;
    private SurfaceImageView imageSurfaceView;
    private Bitmap bitmap, bitmap2;

    @Override
    protected void initView() {
        ivNormal = findViewById(R.id.iv_normal);
        customImageView = findViewById(R.id.cus_iv);
        imageSurfaceView = findViewById(R.id.surface_iv);

        ivNormal.setImageBitmap(bitmap);
        customImageView.setBitmap(bitmap);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                imageSurfaceView.updateBitmap(flag ? bitmap : bitmap2);
                flag = !flag;
            }
        }, PERIOD_TIME, PERIOD_TIME);
    }

    @Override
    protected void initData() {
        bitmap = ImageUtils.getBitmapFromAssets(this, "julis.png");
        bitmap2 = ImageUtils.getBitmapFromAssets(this, "sign.png");
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_draw_image;
    }


}
