package wang.julis.jproject.example.media.drawimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/*******************************************************
 *
 * Created by julis.wang on 2021/07/08 14:36
 *
 * Description :
 *
 * History   :
 *
 *******************************************************/

public class CustomImageView extends View {
    private Bitmap bitmap;
    private Paint paint;

    public CustomImageView(Context context) {
        this(context, null);
    }

    public CustomImageView(Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        paint = new Paint();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bitmap == null) {
            return;
        }
        canvas.drawBitmap(bitmap, 0, 0, paint);
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

}
