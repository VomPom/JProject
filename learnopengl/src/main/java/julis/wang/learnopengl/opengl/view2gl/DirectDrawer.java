package julis.wang.learnopengl.opengl.view2gl;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class DirectDrawer {
    private final static String vertexShaderCode = "attribute vec4 vPosition;"
        + "attribute vec2 inputTextureCoordinate;"
        + "varying vec2 textureCoordinate;" + "void main()" + "{"
        + "gl_Position = vPosition;"
        + "textureCoordinate = inputTextureCoordinate;" + "}";

    private final static String fragmentShaderCode = "#extension GL_OES_EGL_image_external : require\n"
        + "precision mediump float;"
        + "varying vec2 textureCoordinate;\n"
        + "uniform samplerExternalOES s_texture;\n"
        + "void main() {"
        + "  gl_FragColor = texture2D( s_texture, textureCoordinate );\n"
        + "}";
    private int frameBuffer = 0;
    private int frameBufferTexture = 0;

    private int mPositionHandle;
    private int mTextureCoordHandle;
    private final int surfaceTexture;
    private final int mProgram;

    private final FloatBuffer vertexBuffer;
    private final FloatBuffer textureVerticesBuffer;
    private final ShortBuffer drawListBuffer;

    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per
    private final short[] drawOrder = {0, 1, 2, 0, 2, 3}; // order to draw vertices
    private static final int COORDS_PER_VERTEX = 2;
    private static final float[] squareCoords = {-1.0f, 0.0f, -1.0f, -2.2f, 1.0f, -2.2f, 1.0f, 0.0f,};
    private static final float[] textureVertices = {0f, 0f, 0f, 1f, 1f, 1f, 1f, 0f,};


    public DirectDrawer(int texture) {
        this.surfaceTexture = texture;
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        ByteBuffer bb2 = ByteBuffer.allocateDirect(textureVertices.length * 4);
        bb2.order(ByteOrder.nativeOrder());
        textureVerticesBuffer = bb2.asFloatBuffer();
        textureVerticesBuffer.put(textureVertices);
        textureVerticesBuffer.position(0);

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,
            fragmentShaderCode);

        mProgram = GLES20.glCreateProgram(); // create empty OpenGL ES Program
        GLES20.glAttachShader(mProgram, vertexShader); // add the vertex shader
        // to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment
        // shader to program
        GLES20.glLinkProgram(mProgram); // creates OpenGL ES program executables
    }

    private void bindGLProgram(int texture) {
        // 应用GL程序
        // Use the GL program
        GLES20.glUseProgram(mProgram);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        // 获取字段a_position在shader中的位置
        // Get the location of a_position in the shader
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // 启动对应位置的参数
        // Enable the parameter of the location
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // 指定 vPosition 所使用的顶点数据
        // Specify the data of vPosition
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);

        // 获取字段 inputTextureCoordinate 在shader中的位置
        // Get the location of inputTextureCoordinate in the shader
        mTextureCoordHandle = GLES20.glGetAttribLocation(mProgram, "inputTextureCoordinate");
        GLES20.glEnableVertexAttribArray(mTextureCoordHandle);

        // 指定a_textureCoordinate所使用的顶点数据
        // Specify the data of a_textureCoordinate
        GLES20.glVertexAttribPointer(mTextureCoordHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, textureVerticesBuffer);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
    }

    private void render() {
        // 设置清屏颜色
        // Set the color which the screen will be cleared to
        GLES20.glClearColor(0.0f, 0.0f, 1.0f, 0f);

        // 清屏
        // Clear the screen
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length,
            GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTextureCoordHandle);

        // 解绑 扩展oes 纹理
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        // 解绑 帧缓冲
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    public int draw() {
        bindGLProgram(surfaceTexture);
        // 绑定 帧缓冲，下面的渲染操作 都在帧缓冲中
        bindFrameBuffer(frameBuffer);
        render();
        return frameBufferTexture;
    }

    private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    private void bindFrameBuffer(int frameBuffer) {
        // 绑定frame buffer
        // Bind the frame buffer
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer);
    }

    public void initFrameBuffer(int width, int height) {
        if (frameBuffer == 0) {
            int[] frameBuffers = new int[1];
            // 创建FBO
            GLES20.glGenFramebuffers(1, frameBuffers, 0);
            frameBuffer = frameBuffers[0];
            int[] textures = new int[1];

            // 创建 纹理
            GLES20.glGenTextures(1, textures, 0);
            frameBufferTexture = textures[0];

            // 绑定纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, frameBufferTexture);
            // 设置FBO分配内存大小，只是分配大小，此时纹理id 并没有图像数据
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
            // 设置纹理参数
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

            // 绑定FBO，可否提到前面
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer);

            // 把纹理附着到FBO 的颜色附着点，并没有图像数据
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER,
                GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D,
                frameBufferTexture,
                0);
            // 解绑纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

            // 解绑FBO
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        }
    }
}