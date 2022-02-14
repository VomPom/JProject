package julis.wang.learnopengl.opengl;

import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static julis.wang.learnopengl.opengl.MyNativeRender.*;


public class MyGLRender implements GLSurfaceView.Renderer {
    private static final String TAG = "MyGLRender";
    private final MyNativeRender mNativeRender;
    private int mSampleType;

    MyGLRender() {
        mNativeRender = new MyNativeRender();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mNativeRender.nativeOnSurfaceCreated();
        Log.e(TAG, "onSurfaceCreated() called with: GL_VERSION = [" + gl.glGetString(GL10.GL_VERSION) + "]");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mNativeRender.nativeOnSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mNativeRender.nativeOnDrawFrame();
    }

    public void init() {
        mNativeRender.nativeInit();
    }

    public void unInit() {
        mNativeRender.nativeUnInit();
    }

    public void setParamsInt(int paramType, int value0, int value1) {
        if (paramType == SAMPLE_TYPE) {
            mSampleType = value0;
        }
        mNativeRender.nativeSetParamsInt(paramType, value0, value1);
    }

    public int getSampleType() {
        return mSampleType;
    }

    public void updateTransformMatrix(float rotateX, float rotateY, float scaleX, float scaleY) {
        mNativeRender.nativeUpdateTransformMatrix(rotateX, rotateY, scaleX, scaleY);
    }

    public void setTouchLoc(float x, float y) {
        mNativeRender.nativeSetParamsFloat(SAMPLE_TYPE_SET_TOUCH_LOC, x, y);
    }

    public void setGravityXY(float x, float y) {
        mNativeRender.nativeSetParamsFloat(SAMPLE_TYPE_SET_GRAVITY_XY, x, y);
    }

    public void setImageData(int format, int width, int height, byte[] bytes) {
        mNativeRender.nativeSetImageData(format, width, height, bytes);
    }

    public void setImageDataWithIndex(int index, int format, int width, int height, byte[] bytes) {
        mNativeRender.nativeSetImageDataWithIndex(index, format, width, height, bytes);
    }

    public void setAudioData(short[] audioData) {
        mNativeRender.nativeSetAudioData(audioData);
    }


}
