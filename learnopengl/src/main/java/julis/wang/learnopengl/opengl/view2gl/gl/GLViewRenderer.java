package julis.wang.learnopengl.opengl.view2gl.gl;


public interface GLViewRenderer {

    void onSurfaceCreated();

    void onSurfaceChanged(int width, int height);

    void onDrawFrame();
}
