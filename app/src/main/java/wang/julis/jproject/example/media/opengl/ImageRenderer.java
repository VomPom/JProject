package wang.julis.jproject.example.media.opengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/*******************************************************
 *
 * Created by julis.wang on 2021/07/12 17:19
 *
 * Description :
 *              纹理绘制步骤
 *              1、生成纹理 glGenTextures(1, textures, 0)
 *              2、载入图片
 *                  Android 中采用 Bitmap 载入图片，然后使用 copyPixelsToBuffer() 复制图片数据到缓冲区
 *              3、激活纹理 glActivateTexture(GL_TEXTUREn)
 *                  OpenGL 允许绑定十六种纹理，同时 GL_TEXTURE0 是默认激活的，如果只使用一种纹理，那可以省略这个的调用
 *              4、绑定纹理 glBindTexture(target, texture)
 *                  target: GL_TEXTURE_1D GL_TEXTURE_2D GL_TEXTURE_3D
 *                  texture：上面生成的纹理的索引值
 *              5、设置纹理属性
 *                  包括环绕方式和过滤方式
 *                  环绕方式可以在几个轴分别设置
 *                  过滤方式可以在放大和缩小的时候分别设置
 *              6、导入图片数据 glTexImage2D(target, level, internalFormat, width, height, border, format, type, buffer)
 *                  target: GL_TEXTURE_1D GL_TEXTURE_2D GL_TEXTURE_3D
 *                  level: 多级渐远的等级
 *                  internalformat: 纹理的存储格式，这里采用 GL_RGBA
 *                  border: 总为 0
 *                  format：源图的存储格式，bitmap 使用 GL_RGBA
 *                  type：源图数据的数据类型，这里采用 GL_UNSIGNED_BYTE
 *                  buffer：图片数据的 Buffer
 *                  、导入完毕之后调用 bitmap.recycle() 回收资源
 *              7、设置 uniform sampler2D ourTexture
 *                  使用 glUnifrom1i() 对 ourTexture 进行设定，其值应该和你绑定的 GL_TEXTUREn 的 n 相同
 *                  由于默认值为 0，为我们默认激活的 GL_TEXTURE0，因此如果只使用一种纹理，那么可以跳过这一步
 *              8、调用 glDrawxxx() 方法进行绘制，这里采用了 EBO 的 glDrawElements() 进行绘制
 * History   :
 *
 *******************************************************/

public class ImageRenderer extends BaseGLSL implements GLSurfaceView.Renderer {

    //顶点着色器
    private static final String vertexMatrixShaderCode =
            "attribute vec4 vPosition;\n" +
                    "attribute vec2 vCoordinate;\n" +
                    "uniform mat4 vMatrix;\n" +
                    "varying vec2 aCoordinate;\n" +
                    "void main(){\n" +
                    "    gl_Position=vMatrix*vPosition;\n" +
                    "    aCoordinate=vCoordinate;\n" +
                    "}";

    //片元着色器
    private static final String fragmentShaderCode =
            "precision mediump float;\n" +
                    "uniform sampler2D vTexture;\n" +
                    "varying vec2 aCoordinate;\n" +
                    "void main(){\n" +
                    "    gl_FragColor=texture2D(vTexture,aCoordinate);\n" +
                    "}";
    //顶点坐标
    private final float[] sPos = {
            -1.0f, 1.0f,    //左上角 V1
            -1.0f, -1.0f,   //左下角 V2
            1.0f, 1.0f,     //右上角 V3
            1.0f, -1.0f     //右下角 V4
    };
    //纹理坐标
    private final float[] sCoord = {
            0.0f, 0.0f,  //左上角 V1
            0.0f, 1.0f,  //左下角 V2
            1.0f, 0.0f,   //右上角 V3
            1.0f, 1.0f  //右下角 V4
    };

    private Context mContext;
    private Bitmap mBitmap;
    private int mProgram;
    private int glHPosition;
    private int glHTexture;
    private int glHCoordinate;
    private int glHMatrix;

    private float[] mViewMatrix = new float[16];
    private float[] mProjectMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    private FloatBuffer bPos;
    private FloatBuffer bCoord;

    public ImageRenderer(Context context, Bitmap bitmap) {
        mContext = context;
        this.mBitmap = bitmap;
        ByteBuffer bb = ByteBuffer.allocateDirect(sPos.length * 4); //为存放形状的坐标，初始化顶点字节缓冲,float占4个字节
        bb.order(ByteOrder.nativeOrder()); // 使用设备的本点字节序
        bPos = bb.asFloatBuffer();// 从ByteBuffer创建一个浮点缓冲
        bPos.put(sPos);// 把顶点坐标加入FloatBuffer中
        bPos.position(0);// 设置buffer，从第一个坐标开始读

        ByteBuffer cc = ByteBuffer.allocateDirect(sCoord.length * 4);
        cc.order(ByteOrder.nativeOrder());
        bCoord = cc.asFloatBuffer();
        bCoord.put(sCoord);
        bCoord.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);// 申请底色空间
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);

        mProgram = createOpenGLProgram(vertexMatrixShaderCode, fragmentShaderCode);

        // 获取指向vertex shader(顶点着色器)的成员vPosition的handle
        //glGetAttribLocation方法：获取着色器程序中，指定为attribute类型变量的id。
        //glGetUniformLocation方法：获取着色器程序中，指定为uniform类型变量的id。
        glHPosition = GLES20.glGetAttribLocation(mProgram, "vPosition");
        glHCoordinate = GLES20.glGetAttribLocation(mProgram, "vCoordinate");
        glHTexture = GLES20.glGetUniformLocation(mProgram, "vTexture");
        glHMatrix = GLES20.glGetUniformLocation(mProgram, "vMatrix");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //参数X，Y指定了视见区域的左下角在窗口中的位置，一般情况下为（0，0）(屏幕左上角)，Width和Height指定了视见区域的宽度和高度。
        GLES20.glViewport(0, 0, width, height);
        int w = mBitmap.getWidth();
        int h = mBitmap.getHeight();

        float sWH = w / (float) h;
        float sWidthHeight = width / (float) height;
        if (width > height) {
            if (sWH > sWidthHeight) {
                Matrix.orthoM(mProjectMatrix, 0, -sWidthHeight * sWH, sWidthHeight * sWH, -1, 1, 3, 7);
            } else {
                Matrix.orthoM(mProjectMatrix, 0, -sWidthHeight / sWH, sWidthHeight / sWH, -1, 1, 3, 7);
            }
        } else {
            if (sWH > sWidthHeight) {
                Matrix.orthoM(mProjectMatrix, 0, -1, 1, -1 / sWidthHeight * sWH, 1 / sWidthHeight * sWH, 3, 7);
            } else {
                Matrix.orthoM(mProjectMatrix, 0, -1, 1, -sWH / sWidthHeight, sWH / sWidthHeight, 3, 7);
            }
        }
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 7.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);//表示要清除颜色缓冲以及深度缓冲
        GLES20.glUseProgram(mProgram);
        GLES20.glUniformMatrix4fv(glHMatrix, 1, false, mMVPMatrix, 0);
        GLES20.glEnableVertexAttribArray(glHPosition);
        GLES20.glEnableVertexAttribArray(glHCoordinate);
        GLES20.glUniform1i(glHTexture, 0);

        createTexture();

        //传入顶点坐标
        GLES20.glVertexAttribPointer(glHPosition, 2, GLES20.GL_FLOAT, false, 0, bPos);
        //传入纹理坐标
        GLES20.glVertexAttribPointer(glHCoordinate, 2, GLES20.GL_FLOAT, false, 0, bCoord);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }

    private int createTexture() {
        int[] texture = new int[1];
        if (mBitmap != null && !mBitmap.isRecycled()) {
            //生成纹理
            GLES20.glGenTextures(1, texture, 0);
            //生成纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            //根据以上指定的参数，生成一个2D纹理
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
            return texture[0];
        }
        return 0;
    }
}