package wang.julis.jproject.example.media.opengl;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.julis.distance.R;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import wang.julis.jwbase.basecompact.BaseActivity;

/*******************************************************
 *
 * Created by julis.wang on 2021/07/12 15:27
 *
 * Description :
 *              学习 Android 平台 OpenGL ES API，了解 OpenGL 开发的基本流程，使用 OpenGL 绘制一个三角形
 *
 * History   :
 *
 *******************************************************/

public class OpenGLTriangleActivity extends BaseActivity {
    private GLTriangle triangle;

    @Override
    protected void initView() {
        GLSurfaceView glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.setEGLContextClientVersion(2);

        glSurfaceView.setRenderer(new GLSurfaceView.Renderer() {
            public void onSurfaceCreated(GL10 unused, EGLConfig config) {
                triangle = new GLTriangle();
            }

            public void onDrawFrame(GL10 unused) {
                // Redraw background color
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
                triangle.draw();
            }

            public void onSurfaceChanged(GL10 unused, int width, int height) {
                GLES20.glViewport(0, 0, width, height);
            }
        });
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
