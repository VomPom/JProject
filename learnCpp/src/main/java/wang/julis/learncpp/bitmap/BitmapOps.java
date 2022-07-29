package wang.julis.learncpp.bitmap;

import android.graphics.Bitmap;

import wang.julis.learncpp.BaseOperation;

/**
 * Created by glumes on 24/07/2018
 */

public class BitmapOps extends BaseOperation {

    // 顺时针旋转 90° 的操作
    public native Bitmap rotateBitmap(Bitmap bitmap);

    public native Bitmap convertBitmap(Bitmap bitmap);

    public native Bitmap mirrorBitmap(Bitmap bitmap);

    @Override
    public void invoke() {
    }
}
