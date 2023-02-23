package wang.julis.jproject.example.media.glrender.demo;

import android.graphics.SurfaceTexture;
import android.view.Surface;

public interface IRendedView {

    void configSurface(Surface surface);

    void configSurfaceTexture(SurfaceTexture surfaceTexture);
}
