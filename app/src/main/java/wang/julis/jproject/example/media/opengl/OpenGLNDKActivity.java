package wang.julis.jproject.example.media.opengl;

import android.os.Bundle;

import com.julis.distance.R;

import wang.julis.jwbase.basecompact.BaseActivity;

import static android.opengl.GLSurfaceView.RENDERMODE_CONTINUOUSLY;

/*******************************************************
 *
 * Created by julis.wang on 2022/02/09 15:27
 *
 * Description : NDK 实现OpenGL ES相关效果
 *
 *
 * History   :
 *
 *******************************************************/

public class OpenGLNDKActivity extends BaseActivity {
    private final MyGLRender mGLRender = new MyGLRender();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGLRender.init();
    }


    @Override
    protected void initView() {
        MyGLSurfaceView mGLSurfaceView = new MyGLSurfaceView(this, mGLRender);
        mGLSurfaceView.setRenderMode(RENDERMODE_CONTINUOUSLY);
        setContentView(mGLSurfaceView);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGLRender.unInit();
    }
}
