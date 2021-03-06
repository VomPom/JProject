package wang.julis.jproject.example.media.opengl;

import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;

import com.julis.distance.R;

import wang.julis.jproject.utils.ImageUtils;
import wang.julis.jwbase.basecompact.BaseActivity;

/*******************************************************
 *
 * Created by julis.wang on 2021/07/12 17:12
 *
 * Description :
 *              学习 Android 平台 OpenGL ES API，学习纹理绘制，能够使用 OpenGL 显示一张图片
 *
 * History   :
 *
 *******************************************************/

public class OpenGLImageActivity extends BaseActivity {

    @Override
    protected void initView() {
        GLSurfaceView glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.setEGLContextClientVersion(2);
        Bitmap bitmap = ImageUtils.getBitmapFromAssets(this, "julis.png");
        glSurfaceView.setRenderer(new ImageRenderer(this, bitmap));
        setContentView(glSurfaceView);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

}
