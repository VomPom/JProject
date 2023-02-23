package wang.julis.jproject.example.media.glrender;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import com.julis.distance.R;

import wang.julis.jproject.example.media.glrender.cuberenerer.CubeGLRenderer;


public class GlRenderActivity extends Activity {

    private GLSurfaceView mGLSurfaceView;
    private GLRenderable mGLLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }

    private void initViews() {
        setContentView(R.layout.activity_gl_render);

        ViewToGLRenderer viewToGlRenderer = new CubeGLRenderer(this);

        mGLSurfaceView = (GLSurfaceView) findViewById(R.id.gl_surface_view);
        mGLLinearLayout = (GLRenderable) findViewById(R.id.gl_layout);

        mGLSurfaceView.setEGLContextClientVersion(2);
        mGLSurfaceView.setRenderer(viewToGlRenderer);

        mGLLinearLayout.setViewToGLRenderer(viewToGlRenderer);

    }


}
