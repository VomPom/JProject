package wang.julis.jproject.example.media.drawimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/*******************************************************
 *
 * Created by julis.wang on 2021/07/08 14:52
 *
 * Description :
 *         SurfaceView 绘图步骤：
 *              1、获取 SurfaceHolder
 *              2、通过 SurfaceHolder.addCallback() 监听 Surface 状态，当 Surface 构建完毕时方可绘制图像
 *              3、通过 holder.lockCanvas() 获取绘图的 Canvas 对象
 *              4、通过 3 中获取到的 Canvas 进行绘图
 *              5、通过 holder.unlockCanvasAndPost() 将绘制的图像进行显示
 *
 * History   :
 *
 *******************************************************/

public class SurfaceImageView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private SurfaceHolder surfaceHolder;
    private boolean isRunning, isUpdate = true;
    private Bitmap bitmap;
    private Paint paint;
    private int width, height;
    private Thread mThread;

    public SurfaceImageView(Context context) {
        this(context, null);
    }

    public SurfaceImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        surfaceHolder = getHolder();
        paint = new Paint();
        surfaceHolder.addCallback(this);
        isRunning = true;
        setFocusable(true);
        setFocusableInTouchMode(true);
        setKeepScreenOn(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isRunning = true;
        // TODO: why need crate new thread when wake up?? use if(mThread == null) {}
        mThread = new Thread(this);
        mThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.width = width;
        this.height = height;
        isUpdate = true;

        Log.e("julis", "surfaceChanged width:" + width + " height:" + height);
        // 将图片放缩到 SurfaceView大小，则铺满屏幕
        // bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRunning = false;
    }

    @Override
    public void run() {
        while (isRunning) {
            if (isUpdate) {
                drawImage();
                isUpdate = false; // 让其直绘制一直
            }
        }
    }

    public void updateBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        isUpdate = true;
    }

    private void drawImage() {
        Canvas canvas = surfaceHolder.lockCanvas(new Rect(0, 0, this.width, this.height));
        try {
            if (canvas != null && bitmap != null) {
                canvas.drawBitmap(bitmap, 0, 0, paint);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (canvas != null) {
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }
}
