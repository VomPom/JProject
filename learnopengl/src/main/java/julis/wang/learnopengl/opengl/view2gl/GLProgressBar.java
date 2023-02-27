package julis.wang.learnopengl.opengl.view2gl;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.SurfaceTexture;
import android.view.Surface;
import android.view.Surface.OutOfResourcesException;
import android.widget.ProgressBar;

public class GLProgressBar extends ProgressBar implements IRenderedView {
    private Surface mSurface;
    private SurfaceTexture mSurfaceTexture;

    @Override
    public void configSurface(Surface surface) {
        this.mSurface = surface;
    }

    @Override
    public void configSurfaceTexture(SurfaceTexture surfaceTexture) {
        this.mSurfaceTexture = surfaceTexture;
    }

    public GLProgressBar(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mSurface != null) {
            try {
                final Canvas surfaceCanvas = mSurface.lockCanvas(null);
                super.onDraw(surfaceCanvas);
                mSurface.unlockCanvasAndPost(surfaceCanvas);
            } catch (OutOfResourcesException e) {
                e.printStackTrace();
            }
        }

        if (mSurface != null) {
            mSurface.release();
            mSurface = null;
            mSurface = new Surface(mSurfaceTexture);
        }
//		super.onDraw( canvas ); // <- Uncomment this if you want to show the original view
    }
}
