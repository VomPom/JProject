package julis.wang.learnopengl.opengl.view2gl;

import android.graphics.SurfaceTexture;
import android.view.Surface;

public interface IRenderedView {

    void configSurface(Surface surface);

    void configSurfaceTexture(SurfaceTexture surfaceTexture);
}
