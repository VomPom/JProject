package wang.julis.jproject.example.media.opengl.egl;

/*******************************************************
 *
 * Created by julis.wang on 2022/02/11 11:49
 *
 * Description :
 *
 * History   :
 *
 *******************************************************/

public class NativeEglRender {

    public native void native_EglRenderInit();

    public native void native_EglRenderSetImageData(byte[] data, int width, int height);

    public native void native_EglRenderSetIntParams(int paramType, int param);

    public native void native_EglRenderDraw();

    public native void native_EglRenderUnInit();
}
