package wang.julis.jproject.example.media.glrender.demo;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.Display;
import android.view.Surface;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

class ViewRenderer implements GLSurfaceView.Renderer {
    int glSurfaceTex;
    DirectDrawer mDirectDrawer;
    ActivityManager activityManager;


    // Fixed values
    private int TEXTURE_WIDTH = 660;
    private int TEXTURE_HEIGHT = 660;

    Context context;

    private IRendedView rendedView;

    private SurfaceTexture surfaceTexture = null;

    private Surface surface;


    public ViewRenderer(Context context, IRendedView rendedView, Display mDisplay) {
        this.context = context;
        this.rendedView = rendedView;
        TEXTURE_WIDTH = mDisplay.getWidth();
        TEXTURE_HEIGHT = mDisplay.getHeight();
        activityManager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        synchronized (this) {
            surfaceTexture.updateTexImage();
        }
        float[] mtx = new float[16];
        surfaceTexture.getTransformMatrix(mtx);

        mDirectDrawer.draw();
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        surface = null;
        surfaceTexture = null;
        glSurfaceTex = Engine_CreateSurfaceTexture(TEXTURE_WIDTH, TEXTURE_HEIGHT);
        Log.d("GLES20Ext", "glSurfaceTex" + glSurfaceTex);
        if (glSurfaceTex > 0) {
            surfaceTexture = new SurfaceTexture(glSurfaceTex);
            surfaceTexture.setDefaultBufferSize(TEXTURE_WIDTH, TEXTURE_HEIGHT);
            surface = new Surface(surfaceTexture);
            rendedView.configSurface(surface);
            rendedView.configSurfaceTexture(surfaceTexture);
            //addedWidgetView.setSurfaceTexture(surfaceTexture);
            mDirectDrawer = new DirectDrawer(glSurfaceTex);
        }
    }


    int Engine_CreateSurfaceTexture(int width, int height) {
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);

        glSurfaceTex = textures[0];

        if (glSurfaceTex > 0) {
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, glSurfaceTex);

            // Notice the use of GL_TEXTURE_2D for texture creation
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB, width, height, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE, null);

            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        }
        return glSurfaceTex;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mDirectDrawer.initFrameBuffer(width, height);
    }


}