package julis.wang.learnopengl.opengl.view2gl;

import android.app.Activity;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Display;
import android.widget.FrameLayout;
import android.widget.ImageView;

import julis.wang.learnopengl.R;
import julis.wang.learnopengl.opengl.utils.GLUtils;


public class ViewToGLActivity extends Activity {
    private FrameLayout root;
    private ImageView ivContrast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the activity to display the glSurfaceView
        setContentView(R.layout.activity_gl_render);
        root = findViewById(R.id.root);
        ivContrast = findViewById(R.id.iv_contrast);
        addView();
    }

    private void addView() {
        Display mDisplay = getWindowManager().getDefaultDisplay();
        GLProgressBar glProgressBar = new GLProgressBar(this);
        ViewRenderer renderer = new ViewRenderer(glProgressBar, mDisplay);
        GLSurfaceView glSurfaceView = new GLSurfaceView(getApplicationContext());

        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        glSurfaceView.setRenderer(renderer);
        glSurfaceView.setLayoutParams(
            new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        root.addView(glSurfaceView);
        root.addView(glProgressBar);

        renderer.setRenderListener(this::addContrast);
    }

    private void addContrast(int frameBufferTexture) {

        Bitmap b = GLUtils.saveBitmap(frameBufferTexture, 1080, 1920);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                ivContrast.setZ(1);
                ivContrast.setImageBitmap(b);
            }
        });
    }

}
