package wang.julis.jproject.example.media.glrender.demo;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class DirectDrawer {
    private final String vertexShaderCode = "attribute vec4 vPosition;"
        + "attribute vec2 inputTextureCoordinate;"
        + "varying vec2 textureCoordinate;" + "void main()" + "{"
        + "gl_Position = vPosition;"
        + "textureCoordinate = inputTextureCoordinate;" + "}";

    private final String fragmentShaderCode = "#extension GL_OES_EGL_image_external : require\n"
        + "precision mediump float;"
        + "varying vec2 textureCoordinate;\n"
        + "uniform samplerExternalOES s_texture;\n"
        + "void main() {"
        + "  gl_FragColor = texture2D( s_texture, textureCoordinate );\n"
        + "}";

    private FloatBuffer vertexBuffer, textureVerticesBuffer;
    private ShortBuffer drawListBuffer;
    private final int mProgram;
    private int mPositionHandle;
    private int mTextureCoordHandle;

    private short drawOrder[] = {0, 1, 2, 0, 2, 3}; // order to draw vertices

    // number of coordinates per vertex in this array
    private static final int COORDS_PER_VERTEX = 2;

    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per
    // vertex

    static float squareCoords[] = {-1.0f, 0.0f, -1.0f, -2.2f, 1.0f, -2.2f,
        1.0f, 0.0f,};

    static float textureVertices[] = {0f, 0f, 0f, 1f, 1f, 1f, 1f, 0f,};

    private int texture;

    public DirectDrawer(int texture) {
        this.texture = texture;
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

    private void bindGLProgram() {
        GLES20.glUseProgram(mProgram);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the <insert shape here> coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
            GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);

        mTextureCoordHandle = GLES20.glGetAttribLocation(mProgram,
            "inputTextureCoordinate");
        GLES20.glEnableVertexAttribArray(mTextureCoordHandle);

        GLES20.glVertexAttribPointer(mTextureCoordHandle, COORDS_PER_VERTEX,
            GLES20.GL_FLOAT, false, vertexStride, textureVerticesBuffer);

    }

    private void render() {
        GLES20.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length,
            GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTextureCoordHandle);

    }

    public void draw() {
        drawFB();
    }

    private void drawFB() {
        // 绑定 帧缓冲，下面的渲染操作 都在帧缓冲中
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer);
        GLES20.glUseProgram(mProgram);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the <insert shape here> coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
            GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);

        mTextureCoordHandle = GLES20.glGetAttribLocation(mProgram,
            "inputTextureCoordinate");
        GLES20.glEnableVertexAttribArray(mTextureCoordHandle);

        GLES20.glVertexAttribPointer(mTextureCoordHandle, COORDS_PER_VERTEX,
            GLES20.GL_FLOAT, false, vertexStride, textureVerticesBuffer);

        GLES20.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
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

    // 帧缓存
    // frame buffer
    private int frameBuffer = 0;

    // 帧缓绑定的texture
    // the texture bind on the frame buffer
    private int frameBufferTexture = 0;

//
//    public void initFrameBuffer(int width, int height) {
//        // 创建frame buffer绑定的纹理
//        // Create texture which binds to frame buffer
//        int[] textures = new int[1];
//        GLES20.glGenTextures(textures.length, textures, 0);
//        frameBufferTexture = textures[0];
//
//        // 创建frame buffer
//        // Create frame buffer
//        int[] frameBuffers = new int[1];
//        GLES20.glGenFramebuffers(frameBuffers.length, frameBuffers, 0);
//        frameBuffer = frameBuffers[0];
//
//        // 将frame buffer与texture绑定
//        // Bind the texture to frame buffer
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, frameBufferTexture);
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
//        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
//        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer);
//        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, frameBufferTexture, 0);
//    }

    /**
     * 创建帧缓冲FBO
     *
     * @param width
     * @param height
     */
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