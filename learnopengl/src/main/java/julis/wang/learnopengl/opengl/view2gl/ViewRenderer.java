package julis.wang.learnopengl.opengl.view2gl;

import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.Display;
import android.view.Surface;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ViewRenderer implements GLSurfaceView.Renderer {
    private int glSurfaceTex;
    private DirectDrawer mDirectDrawer;
    private OnRenderListener renderListener;
    private final int TEXTURE_WIDTH;
    private final int TEXTURE_HEIGHT;
    private final IRenderedView rendedView;
    private SurfaceTexture surfaceTexture = null;

    public ViewRenderer(IRenderedView rendedView, Display mDisplay) {
        this.rendedView = rendedView;
        TEXTURE_WIDTH = mDisplay.getWidth();
        TEXTURE_HEIGHT = mDisplay.getHeight();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        synchronized (this) {
            surfaceTexture.updateTexImage();
        }

        int frameBufferTexture = mDirectDrawer.draw();
        if (renderListener != null) {
            renderListener.onDraw(frameBufferTexture);
        }
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        surfaceTexture = null;
        glSurfaceTex = createSurfaceTexture(TEXTURE_WIDTH, TEXTURE_HEIGHT);
        if (glSurfaceTex > 0) {
            surfaceTexture = new SurfaceTexture(glSurfaceTex);
            surfaceTexture.setDefaultBufferSize(TEXTURE_WIDTH, TEXTURE_HEIGHT);
            rendedView.configSurface(new Surface(surfaceTexture));
            rendedView.configSurfaceTexture(surfaceTexture);
            mDirectDrawer = new DirectDrawer(glSurfaceTex);
        }
    }


    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mDirectDrawer.initFrameBuffer(width, height);
    }

    int createSurfaceTexture(int width, int height) {
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

    public void setRenderListener(OnRenderListener renderListener) {
        this.renderListener = renderListener;
    }
}